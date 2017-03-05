package com.dreambig.homebase.repository;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.dreambig.homebase.endpoint.IosDeviceEndpoint;
import com.dreambig.homebase.entity.DeviceType;
import com.dreambig.homebase.entity.UserTicket;

public class UserManager {
	
	private static final Logger log = Logger.getLogger(UserManager.class.getName());
	private DataAccess dataAccess = new DataAccess();
	
	public void handleDeviceRegistrationRequest(String userId, String deviceToken, DeviceType deviceType) {        
		
	  UserTicket userTicket = dataAccess.getUserTicket(userId);
    
	  if(isUserRegistered(userTicket)) {
    	log.log(Level.INFO,"User id " + userId + " already registered");
    	
    	if(isItAnUpdate(userTicket, deviceToken, deviceType)) {
    		log.log(Level.INFO,"It is an update");
    		executeUpdate(userTicket,deviceToken,deviceType);
    	} else {
    		log.log(Level.FINE,"The user attributes haven't changed. Nothing to do.");
    	}    	
     } else {
    	log.log(Level.INFO,"User id not registered yet");
    	handleBrandNewUserId(userId,deviceToken,deviceType);
     }
		
	}
	
    private boolean isUserRegistered(UserTicket userTicket) {
    	return userTicket != null;
    }
    
    private boolean isItAnUpdate(UserTicket userTicket, String newToken, DeviceType deviceType) {
    	return !userTicket.getToken().equals(newToken) || !userTicket.getDeviceType().equals(deviceType);
    }
    
    private void handleBrandNewUserId(String userId, String token, DeviceType deviceType) {
    	dataAccess.addUserTicket(new UserTicket(userId,token,deviceType));
    }
    
    private void executeUpdate(UserTicket existingRegistration, String newToken, DeviceType deviceType) {
    	existingRegistration.setToken(newToken);
    	existingRegistration.setDeviceType(deviceType);
    	dataAccess.updateUserTicket(existingRegistration);
    }

}
