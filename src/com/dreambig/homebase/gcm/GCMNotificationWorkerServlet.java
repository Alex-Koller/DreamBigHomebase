package com.dreambig.homebase.gcm;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dreambig.homebase.entity.NewsItem;
import com.dreambig.homebase.entity.UserTicket;
import com.dreambig.homebase.helper.IOUtils;
import com.google.gson.Gson;


public class GCMNotificationWorkerServlet extends HttpServlet {
	  private static final long serialVersionUID = 1L;
	  private static final Logger log = Logger.getLogger(GCMNotificationWorkerServlet.class.getName());
	  

	  @Override
	  public void doPost(HttpServletRequest req, HttpServletResponse res) {
		  log.log(Level.INFO,"Retrieved notficiations to process");
		  
		  String alert = req.getParameter("alert");
		  String token = req.getParameter("token");
		  
		  log.log(Level.INFO,"alert: " + alert + ", token: " + token);
		  PostPayload postPayload = preparePostPayload(req);
		  postToGCM(postPayload);
	  }
	  
	  private PostPayload preparePostPayload(HttpServletRequest req) {
		  String alert = req.getParameter("alert");
		  String token = req.getParameter("token");	
		  
		  //these three are optional
		  String action = req.getParameter("action");
		  String subjectId = req.getParameter("subjectId");
		  String subjectTitle = req.getParameter("subjectTitle");
		  
		  NewsPackage newsPackage = new NewsPackage(alert,action,subjectId,subjectTitle);
		  return new PostPayload(Arrays.asList(token),newsPackage);
	  }

	  private void postToGCM(PostPayload postPayload) {
			 String jsonString = new Gson().toJson(postPayload);
			 
		     try {
		    	 URL url = new URL(Configuration.getGcmUrl());
		         HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		         connection.setDoOutput(true);
		         connection.setRequestMethod("POST");
		         connection.setRequestProperty("Authorization", "key=" + Configuration.getApiKey());
		         connection.setRequestProperty("Content-Type", "application/json");

		         OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
		         writer.write(jsonString);
		         writer.close();
		    
		         int responseCode = connection.getResponseCode();
		         String responseMessage = IOUtils.getStringFromInputStream(connection.getInputStream());
		
		         if (responseCode == HttpURLConnection.HTTP_OK) {
		        	 log.log(Level.INFO,"Post to GCM was OK");
		        	 examineResponseMessage(responseMessage); 	 
		         } else {
		        	 log.log(Level.SEVERE,"Error code returned: " + responseCode + ", message content " + responseMessage);
		         }
		     } catch (Exception e) {
		    	 log.log(Level.SEVERE,"Exception: " + e.getMessage(), e);
		     }	 
		} 
	  
	  private void examineResponseMessage(String responseMessage) {
		  Map<String,Object> responseMessageMap = new Gson().fromJson(responseMessage, Map.class);
		  int failure = ((Double)responseMessageMap.get("failure")).intValue();
		  int canonicalIds = ((Double)responseMessageMap.get("canonical_ids")).intValue();
		  List<Map<String,Object>> results = (List<Map<String,Object>>)responseMessageMap.get("results");
		  
		  if(failure != 0 || canonicalIds != 0) {
			  log.log(Level.SEVERE, "The response failures: " + failure + ", canonicalIds: " + canonicalIds);
		  }
		  
		  for(Map<String,Object> result : results) {
			  String registrationId = (String)result.get("registration_id");
			  String error = (String)result.get("error");
			  if(registrationId != null) {
				  log.log(Level.SEVERE, "Registration id is not empty");
			  }
			  if(error != null) {
				  log.log(Level.SEVERE, "There's an error: " + error);
			  }
			  
		  }
	  }
	  
	  
}
