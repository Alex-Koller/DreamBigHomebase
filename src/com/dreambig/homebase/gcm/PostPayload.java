package com.dreambig.homebase.gcm;

import java.util.List;

public class PostPayload {
	
	private List<String> registration_ids;
	private NewsPackage data;
	
	public PostPayload() {

	}

	public PostPayload(List<String> registration_ids, NewsPackage data) {
		this.registration_ids = registration_ids;
		this.data = data;
	}

	public List<String> getRegistration_ids() {
		return registration_ids;
	}

	public void setRegistration_ids(List<String> registration_ids) {
		this.registration_ids = registration_ids;
	}

	public NewsPackage getData() {
		return data;
	}

	public void setData(NewsPackage data) {
		this.data = data;
	}
	

}
