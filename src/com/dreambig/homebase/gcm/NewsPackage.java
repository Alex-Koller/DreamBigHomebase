package com.dreambig.homebase.gcm;

public class NewsPackage {
	  private String message;
	  private String action;
	  private String subjectId;
	  private String subjectTitle;
	  
	  public NewsPackage() {
	  }

	  public NewsPackage(String message, String action, String subjectId, String subjectTitle) {
		super();
		this.message = message;
		this.action = action;
		this.subjectId = subjectId;
		this.subjectTitle = subjectTitle;
	  }

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}

	public String getSubjectTitle() {
		return subjectTitle;
	}

	public void setSubjectTitle(String subjectTitle) {
		this.subjectTitle = subjectTitle;
	}
	  
}
