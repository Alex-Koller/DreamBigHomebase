package com.dreambig.homebase.gcm;

public class Configuration {
	
	private static final String GCM_URL = "https://android.googleapis.com/gcm/send"; //generic GCM url for HTTP GCM server
	private static final String API_KEY = "AIzaSyBlh6S1-em9Ry3Pmbg5fD7FuMpaQ_0fTjU"; //this is they key generated when white-listing an IP in Google Project/Api Access
	
	public static String getGcmUrl() {
		return GCM_URL;
	}
	
	public static String getApiKey() {
		return API_KEY;
	}

}
