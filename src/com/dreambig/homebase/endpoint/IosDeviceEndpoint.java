package com.dreambig.homebase.endpoint;

import com.dreambig.homebase.entity.DeviceType;
import com.dreambig.homebase.entity.PasswordClaim;
import com.dreambig.homebase.entity.PasswordClaimParcel;
import com.dreambig.homebase.entity.PasswordClaimParcelStatus;
import com.dreambig.homebase.entity.UserTicket;
import com.dreambig.homebase.repository.DataAccess;
import com.dreambig.homebase.repository.PasswordClaimManager;
import com.dreambig.homebase.repository.UserManager;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.Named;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Alexandr on 11/12/14.
 */

@Api(name = "iosdevice")
public class IosDeviceEndpoint {

    private static final Logger log = Logger.getLogger(IosDeviceEndpoint.class.getName());
    private UserManager userManager = new UserManager();
    private PasswordClaimManager passwordClaimManager = new PasswordClaimManager();
    

    @ApiMethod(name = "registerDevice")
    public void registerDevice(@Named("userId") String userId, @Named("token") String token) {
        log.log(Level.INFO,"iOS request to register user id: " + userId + ", token: " + token);
        
        userManager.handleDeviceRegistrationRequest(userId, token, DeviceType.iOS);   
    }
    
    @ApiMethod(name = "sendConfirmationTextIOS")
    public PasswordClaimParcel sendConfirmationTextIOS(@Named("phoneNumber") String phoneNumber) {
    	log.log(Level.INFO,"iOS request for a text received: " + phoneNumber);
    	
    	PasswordClaim passwordClaim = passwordClaimManager.createPasswordClaim(phoneNumber);
    	passwordClaimManager.sendOutTextMessage(passwordClaim); //this places it on a worker queue to be done asynchronously
    	return new PasswordClaimParcel(passwordClaim.getClaimId(),PasswordClaimParcelStatus.RECEIVED.toString(),null);
    }
    
    @ApiMethod(name = "getUserPasswordIOS")
    public PasswordClaimParcel getUserPasswordIOS(@Named("claimId") Long claimId, @Named("claimCode") String claimCode) {
    	log.log(Level.INFO,"iOS request for password claim: " + claimId + ", code: " + claimCode);
    	
    	return passwordClaimManager.processRequestForPassword(claimId,  claimCode);
    }     
    
}
