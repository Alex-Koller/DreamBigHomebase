package com.dreambig.homebase.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class PasswordClaim {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long claimId;
	private String phoneNumber;
	private PasswordClaimStatus passwordClaimStatus;
	private String associatedCode; //this is the little code sent via the sms and expected back
	private Date lastUpdate;
	
	public PasswordClaim() {
		super();
	}

	public Long getClaimId() {
		return claimId;
	}

	public void setClaimId(Long claimId) {
		this.claimId = claimId;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public PasswordClaimStatus getPasswordClaimStatus() {
		return passwordClaimStatus;
	}

	public void setPasswordClaimStatus(PasswordClaimStatus passwordClaimStatus) {
		this.passwordClaimStatus = passwordClaimStatus;
	}

	public String getAssociatedCode() {
		return associatedCode;
	}

	public void setAssociatedCode(String associatedCode) {
		this.associatedCode = associatedCode;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	
	
	
	
}
