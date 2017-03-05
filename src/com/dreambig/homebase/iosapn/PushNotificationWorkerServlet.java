package com.dreambig.homebase.iosapn;

import com.google.appengine.api.LifecycleManager;
import com.google.appengine.api.ThreadManager;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.apphosting.api.ApiProxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javapns.communication.exceptions.CommunicationException;
import javapns.communication.exceptions.KeystoreException;
import javapns.notification.PushedNotification;
import javapns.notification.PushedNotifications;
import javapns.notification.ResponsePacket;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * HttpServlet for sending pending notifications.
 *
 * It can be hosted on a backend
 *
 */
public class PushNotificationWorkerServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;
  private static final Logger log = Logger.getLogger(PushNotificationWorkerServlet.class.getName());
  
  private PushNotificationSender notificationSender = new PushNotificationSender(
	      Configuration.getCertificateBytes(), Configuration.CERTIFICATE_PASSWORD,
	      Configuration.USE_PRODUCTION_APNS_SERVICE);  

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse res) {
	  log.log(Level.INFO,"Retrieved notficiations to process");
	  
	  String alert = req.getParameter("alert");
	  String token = req.getParameter("token");
	  
	  log.log(Level.INFO,"alert: " + alert + ", token: " + token);
	  
	  try {
		  PushedNotifications pushedNotifications = notificationSender.sendAlert(alert, new String[]{token},createCustomProperties(req));
		  processPushedNotifications(pushedNotifications);
	  } catch (CommunicationException e) {
	        log.log(Level.WARNING, "Sending push alert failed with CommunicationException:" + e.toString(), e);
	  } catch (KeystoreException e) {
	        log.log(Level.WARNING, "Sending push alert failed with KeystoreException:" + e.toString(), e);
	  }
  }
  
  private Map<String,String> createCustomProperties(HttpServletRequest request) {
	  Map<String,String> ret = new HashMap<String,String>();
	  
	  String action = request.getParameter("action");
	  String subjectId = request.getParameter("subjectId");
	  String subjectTitle = request.getParameter("subjectTitle");
	  
	  if(action != null) {
		  ret.put("action", action);
	  }
	  
	  if(subjectId != null) {
		  ret.put("subjectId", subjectId);
	  }
	  
	  if(subjectTitle != null) {
		  ret.put("subjectTitle", subjectTitle);
	  }
	  
	  return ret;
  }
  
  private void processPushedNotifications(PushedNotifications pushedNotifications) {
	    notificationSender.processedPendingNotificationResponses();

	    List<String> invalidTokens = new ArrayList<String>();

	    for (PushedNotification notification : pushedNotifications) {

	      if (!notification.isSuccessful()) {
	        log.log(Level.WARNING,
	            "Notification to device " + notification.getDevice().getToken() +
	            " wasn't successful.",
	            notification.getException());

	        // Check if APNS returned an error-response packet.
	        ResponsePacket errorResponse = notification.getResponse();
	        if (errorResponse != null) {
	          if (errorResponse.getStatus() == 8) {
	            String invalidToken = notification.getDevice().getToken();
	            invalidTokens.add(invalidToken);
	          }
	          log.warning("Error response packet: " + errorResponse.getMessage());
	        }
	      }

	      log.log(Level.INFO,"This many invalid tokens: " + invalidTokens.size());
	      if (invalidTokens.size() > 0) {
	        PushNotificationUtility.enqueueRemovingDeviceTokens(invalidTokens);
	      }
	    }
	    
  }  

 
}
