package com.dreambig.homebase.repository;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dreambig.homebase.entity.NewsAlert;
import com.dreambig.homebase.entity.NewsItem;
import com.dreambig.homebase.entity.PasswordClaim;
import com.dreambig.homebase.entity.PasswordClaimParcel;
import com.dreambig.homebase.entity.PasswordClaimParcelStatus;
import com.dreambig.homebase.entity.PasswordClaimStatus;
import com.dreambig.homebase.helper.PasswordCreater;
import com.dreambig.homebase.helper.PasswordCreater.FailedCreatingPasswordException;
import com.dreambig.homebase.repository.DataAccess.PasswordClaimNotFoundException;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

public class PasswordClaimManager {

	private static final int MAX_PASSWORD_LENGTH = 12;
	private static final Logger log = Logger.getLogger(PasswordClaimManager.class.getName());
	private DataAccess dataAccess = new DataAccess();	
	private PasswordCreater passwordCreater = new PasswordCreater();
	
	public PasswordClaim createPasswordClaim(String phoneNumber) {
		PasswordClaim passwordClaim = new PasswordClaim();
		passwordClaim.setLastUpdate(new Date());
		passwordClaim.setPasswordClaimStatus(PasswordClaimStatus.RECEIVED);
		passwordClaim.setPhoneNumber(phoneNumber);
		
		dataAccess.storePasswordClaim(passwordClaim);
		return passwordClaim;
	}
	
	public PasswordClaim getPasswordClaim(Long claimId) {
		return dataAccess.getPasswordClaim(claimId);
	}
	
	public void sendOutTextMessage(PasswordClaim passwordClaim) {
		String phoneNumber = passwordClaim.getPhoneNumber();
		Long claimId = passwordClaim.getClaimId();
		
		log.log(Level.INFO,"Placing request for message on worker queue. For " + phoneNumber);
		Queue queue = QueueFactory.getQueue("password-claim-text-out-worker");
			  
		TaskOptions taskOptions = TaskOptions.Builder.withMethod(TaskOptions.Method.POST).url("/admin/passwordclaim/textdeliver").param("phoneNumber", phoneNumber).param("claimId", String.valueOf(claimId));
			  
		queue.add(taskOptions);	  		
	}
	
	public PasswordClaimParcel processRequestForPassword(Long claimId, String code) {
		PasswordClaim passwordClaim = dataAccess.getPasswordClaim(claimId);
		if(passwordClaim == null) {
			return new PasswordClaimParcel(claimId, PasswordClaimParcelStatus.CLAIM_NOT_FOUND.toString(), null);
		} else {
			if(code.equals(passwordClaim.getAssociatedCode())) {
				//it's fine, send back the password
				String password = phoneNumberToPassword(passwordClaim.getPhoneNumber());
				if(password == null) {
					changeStatusOnPasswordClaim(claimId, PasswordClaimStatus.PASSWORD_FAILED_GENERATING);
					return new PasswordClaimParcel(claimId, PasswordClaimParcelStatus.PASSWORD_FAILED_CREATING.toString(), null);
				} else {
					log.log(Level.INFO,"Provided code does match, sending password back");
					changeStatusOnPasswordClaim(claimId, PasswordClaimStatus.PASSWORD_PROVIDED);
					return new PasswordClaimParcel(claimId, PasswordClaimParcelStatus.CODE_MATCHES.toString(), password);					
				}
			} else {
				//it's not fine
				log.log(Level.INFO,"Provided code does not match, not sending password back");
				changeStatusOnPasswordClaim(claimId, PasswordClaimStatus.PASSWORD_DENIED);
				return new PasswordClaimParcel(claimId, PasswordClaimParcelStatus.CODE_DOESNT_MATCH.toString(),null);
			}
		}
	}
	
	private String phoneNumberToPassword(String phoneNumber) {
		try {
			String password = passwordCreater.createPassword(phoneNumber);
			String shortened = shortenThePassword(password);
			log.log(Level.INFO,"Generated password: " + shortened);
			return shortened;
		} catch(FailedCreatingPasswordException e) {
			log.log(Level.SEVERE,"Failed creating password: " + e.getMessage(),e);
			return null;
		}
		
	}
	
	private void changeStatusOnPasswordClaim(Long claimId, PasswordClaimStatus newStatus) {
		try {
			dataAccess.setStatusToPasswordClaim(claimId, newStatus);
		} catch (PasswordClaimNotFoundException e) {
			//not sure how this could ever happen
			log.log(Level.SEVERE,"Failed updating status on password claim " + e.getMessage(),e);
		}
	}
	
	private String shortenThePassword(String in) {
		if(in.length() <= MAX_PASSWORD_LENGTH) {
			return in;
		} else {
			return in.substring(0,MAX_PASSWORD_LENGTH);
		}
	}
	
}
