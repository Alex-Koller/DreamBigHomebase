package com.dreambig.homebase.entity;

public class PasswordClaimParcel {

	private Long   claimId;
	private String claimStatus;
	private String password;
	
	public PasswordClaimParcel() {
		super();
	}

	public PasswordClaimParcel(Long claimId, String claimStatus, String password) {
		super();
		this.claimId = claimId;
		this.claimStatus = claimStatus;
		this.password = password;
	}

	public Long getClaimId() {
		return claimId;
	}

	public void setClaimId(Long claimId) {
		this.claimId = claimId;
	}

	public String getClaimStatus() {
		return claimStatus;
	}

	public void setClaimStatus(String claimStatus) {
		this.claimStatus = claimStatus;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}


	
}
