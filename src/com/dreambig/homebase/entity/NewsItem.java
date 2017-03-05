package com.dreambig.homebase.entity;

import java.util.ArrayList;
import java.util.List;

public class NewsItem {

	public enum NewsType {
		INVITED_TO_ACTIVITY,
		ACTIVITY_CANCELLED,
		NEW_COMMENT,
		NEW_WHEN_SUGGESTION,
		NEW_WHERE_SUGGESTION,
		ACTIVITY_TITLE_EDITED,
		ACTIVITY_DESCRIPTION_EDITED,
		ACTIVITY_TIME_EDITED,
		ACTIVITY_PLACE_EDITED,
		ACTIVITY_TYPE_EDITED
	}	
	
	private String newsId;
	private String createdBy;
	private String createdForUserId;
	private String subjectId;
	private String subjectTitle;
	private NewsType newsType;
	private String status;
	private String additionalValue;
	private Long createdOn;
	
	
	
	public NewsItem() {
		super();
	}

	public NewsItem(String newsId) {
		super();
		this.newsId = newsId;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public void setCreatedForUserId(String createdForUserId) {
		this.createdForUserId = createdForUserId;
	}

	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}

	public void setSubjectTitle(String subjectTitle) {
		this.subjectTitle = subjectTitle;
	}

	public void setNewsType(NewsType newsType) {
		this.newsType = newsType;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setAdditionalValue(String additionalValue) {
		this.additionalValue = additionalValue;
	}

	public void setCreatedOn(Long createdOn) {
		this.createdOn = createdOn;
	}

	public String getNewsId() {
		return newsId;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public String getCreatedForUserId() {
		return createdForUserId;
	}

	public String getSubjectId() {
		return subjectId;
	}

	public String getSubjectTitle() {
		return subjectTitle;
	}

	public NewsType getNewsType() {
		return newsType;
	}

	public String getStatus() {
		return status;
	}

	public String getAdditionalValue() {
		return additionalValue;
	}

	public Long getCreatedOn() {
		return createdOn;
	}
	
}
