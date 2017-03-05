package com.dreambig.homebase.entity;

public class NewsAlert {
	
	private String message;
	private String action;
	private String subjectId;
	private String subjectTitle;
	private Long newsTimestamp;
	
	public NewsAlert(String message, Long newsTimestamp) {
		super();
		this.message = message;
		this.newsTimestamp = newsTimestamp;
	}

	public String getMessage() {
		return message;
	}

	public Long getNewsTimestamp() {
		return newsTimestamp;
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
