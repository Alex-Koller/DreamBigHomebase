package com.dreambig.homebase.common;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dreambig.homebase.entity.PasswordClaim;
import com.dreambig.homebase.entity.PasswordClaimStatus;
import com.dreambig.homebase.helper.RandomString;
import com.dreambig.homebase.helper.TwilioHelper;
import com.dreambig.homebase.repository.DataAccess;
import com.dreambig.homebase.repository.DataAccess.PasswordClaimNotFoundException;
import com.twilio.sdk.TwilioRestException;


public class PasswordClaimTextWorkerServlet extends HttpServlet {

	  private static final long serialVersionUID = 1L;
	  private static final Logger log = Logger.getLogger(PasswordClaimTextWorkerServlet.class.getCanonicalName());
	  private DataAccess dataAccess = new DataAccess();
	  private TwilioHelper twilioHelper = new TwilioHelper();
	  private RandomString randomString = new RandomString(5);

	  @Override
	  public void doPost(HttpServletRequest req, HttpServletResponse res) {
		  log.log(Level.INFO,"Retrieved request to send out a text");
		  
		  String phoneNumber = req.getParameter("phoneNumber");
		  Long claimId = Long.valueOf(req.getParameter("claimId"));
		  boolean isThisTestAccount = isThisATestAccountNumber(phoneNumber);
		  
		  String code;
		  if(isThisTestAccount) {
			  code = "ABCDE";
		  } else {
			  code = generateCode();
		  }
		  
		  
		  try {
			  if(addCodeToThePasswordClaim(claimId, code)) {
				  log.log(Level.INFO,"Code added, sending out the text message");
				  if(!isThisTestAccount) {
					  twilioHelper.sendConfirmationMessage(phoneNumber, code);
					  try {
						  dataAccess.setStatusToPasswordClaim(claimId, PasswordClaimStatus.SMS_SENT);
					  } catch(PasswordClaimNotFoundException e) {
						  //don't think this could quite happen
						  log.log(Level.SEVERE,"Claim not found to update status on to SMS_SENT: " + claimId,e);
					  }					  
				  } else {
					  log.log(Level.INFO,"No text to be sent, it's test account");
				  }
			  } else {
				  //not sure how this could happen
				  log.log(Level.SEVERE,"So there isn't any code to send out");
				  if(!isThisTestAccount) {
					  twilioHelper.sendSomethingWrongMessage(phoneNumber);
				  }
			  }
		  } catch (TwilioRestException e) {
			  log.log(Level.SEVERE,"Failed sending text to " + phoneNumber,e);
			  try {
				  dataAccess.setStatusToPasswordClaim(claimId, PasswordClaimStatus.SMS_FAILED_SENDING);
			  } catch (PasswordClaimNotFoundException e2) {
				  log.log(Level.SEVERE,"And claim not found now: " + claimId,e);
			  }
		  }
	  }
	  
	  private boolean addCodeToThePasswordClaim(Long claimId, String code) {
		  try {
			  dataAccess.addCodeToPasswordClaim(claimId, code);
			  return true;
		  } catch (PasswordClaimNotFoundException e) {
			  log.log(Level.SEVERE,e.getMessage(),e);
			  return false;
		  }		  
	  }
	  
	  //random and maths
	  private String generateCode() {
	    return randomString.nextString();  
	  }
	  
	  private boolean isThisATestAccountNumber(String phoneNumber) {
		  return "0044123456789".equals(phoneNumber);
	  }
	  

	
}
