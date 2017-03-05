package com.dreambig.homebase.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class UserTicket {
	
	@Id
	private String userId;
    private String token;
    private Date lastModified;
    private Long lastNewsTimestamp;
    private DeviceType deviceType;
    
	public UserTicket() {
		super();
	}    
	
	public void updateFrom(UserTicket in) {
		this.token = in.token;
		this.lastModified = new Date();
		this.deviceType = in.deviceType;
	}
	  
	public UserTicket(String userId, String token, DeviceType deviceType) {
		super();
		this.userId = userId;
		this.token = token;
		this.deviceType = deviceType;
		this.lastModified = new Date();
	}

	public String getUserId() {
		return userId;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public String getToken() {
		return token;
	}
	
	public void setToken(String token) {
		this.token = token;
	}

	public DeviceType getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date createdOn) {
		this.lastModified = createdOn;
	}

	public Long getLastNewsTimestamp() {
		return lastNewsTimestamp;
	}

	public void setLastNewsTimestamp(Long lastNewsTimestamp) {
		this.lastNewsTimestamp = lastNewsTimestamp;
	}
	  
}
