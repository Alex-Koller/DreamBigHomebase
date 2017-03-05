package com.dreambig.homebase.endpoint;

import com.dreambig.homebase.entity.DeviceType;
import com.dreambig.homebase.entity.PasswordClaim;
import com.dreambig.homebase.entity.PasswordClaimParcel;
import com.dreambig.homebase.entity.PasswordClaimParcelStatus;
import com.dreambig.homebase.entity.PasswordClaimStatus;
import com.dreambig.homebase.entity.UserTicket;
import com.dreambig.homebase.repository.DataAccess;
import com.dreambig.homebase.repository.PasswordClaimManager;
import com.dreambig.homebase.repository.UserManager;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.Named;

import java.math.BigDecimal;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Alexandr on 11/12/14.
 */

@Api(name = "androiddevice")
public class AndroidDeviceEndpoint {

    private static final Logger log = Logger.getLogger(AndroidDeviceEndpoint.class.getName());
    private UserManager userManager = new UserManager();
    private PasswordClaimManager passwordClaimManager = new PasswordClaimManager();

    @ApiMethod(name = "registerAndroidDevice")
    public void registerAndroidDevice(@Named("userId") String userId, @Named("token") String token) {
        log.log(Level.INFO,"Android request to register user id: " + userId + ", token: " + token);
        
        userManager.handleDeviceRegistrationRequest(userId, token, DeviceType.Android);   
    }
    
    @ApiMethod(name = "sendConfirmationTextAndroid")
    public PasswordClaimParcel sendConfirmationTextAndroid(@Named("phoneNumber") String phoneNumber) {
    	log.log(Level.INFO,"Android request for a text received: " + phoneNumber);
    	
    	PasswordClaim passwordClaim = passwordClaimManager.createPasswordClaim(phoneNumber);
    	passwordClaimManager.sendOutTextMessage(passwordClaim); //this places it on a worker queue to be done asynchronously
    	return new PasswordClaimParcel(passwordClaim.getClaimId(),PasswordClaimParcelStatus.RECEIVED.toString(),null);
    }
    
    @ApiMethod(name = "getUserPasswordAndroid")
    public PasswordClaimParcel getUserPasswordAndroid(@Named("claimId") Long claimId, @Named("claimCode") String claimCode) {
    	log.log(Level.INFO,"Android request for password claim: " + claimId + ", code: " + claimCode);
    	
    	return passwordClaimManager.processRequestForPassword(claimId,  claimCode);
    }    
  
   
}
