package com.dreambig.homebase.endpoint;

import com.dreambig.homebase.entity.CallResult;
import com.dreambig.homebase.entity.DeviceType;
import com.dreambig.homebase.entity.UserTicket;
import com.dreambig.homebase.entity.NewsAlert;
import com.dreambig.homebase.entity.NewsItem;
import com.dreambig.homebase.gcm.Configuration;
import com.dreambig.homebase.gcm.PostPayload;
import com.dreambig.homebase.helper.IOUtils;
import com.dreambig.homebase.repository.DataAccess;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.Named;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Alexandr on 11/12/14.
 */

@Api(name = "news")
public class NewsEndpoint {
	
	private static enum CallResultCode {
		DONE,
		USER_NOT_REGISTERED,
		UNSUPPORTED_DEVICE_TYPE
	}

    private static final Logger log = Logger.getLogger(NewsEndpoint.class.getName());
    private DataAccess dataAccess = new DataAccess();

    @ApiMethod(name = "postNews")
    public CallResult postNews(NewsItem newsItem) {
        log.log(Level.INFO,"Received a news item for user id: " + newsItem.getCreatedForUserId() + ", type: " + newsItem.getNewsType());
        CallResultCode callResultCode;
        
        UserTicket userTicket = dataAccess.getUserTicket(newsItem.getCreatedForUserId());
        if(userTicket == null) {
        	callResultCode = CallResultCode.USER_NOT_REGISTERED;
        } else if (userTicket.getDeviceType().equals(DeviceType.iOS)) {
        	callResultCode = doIOS(newsItem, userTicket);
        } else if (userTicket.getDeviceType().equals(DeviceType.Android)) {
        	callResultCode = doAndroid(newsItem, userTicket);
        } else {
        	callResultCode = CallResultCode.UNSUPPORTED_DEVICE_TYPE;
        }
        
        if(callResultCode == CallResultCode.DONE) {
        	dataAccess.upadateLatestNewsTimestamp(newsItem.getCreatedForUserId(), newsItem.getCreatedOn());
        }
        
        return new CallResult(callResultCode.toString());
    }
    
    private CallResultCode doAndroid(NewsItem newsItem, UserTicket registration) {
    	String recepientUserId = newsItem.getCreatedForUserId();
    	log.log(Level.INFO,"Dispatching for Android user: " + recepientUserId + ", token: " + registration.getToken());
    	delegateTheDeliveryToAWorker(newsItem,registration.getToken(),"notification-pass-to-android-worker","/admin/gcm/notifications/deliver");
    	return CallResultCode.DONE;
    }
    
    private CallResultCode doIOS(NewsItem newsItem, UserTicket registration) {
    	String recepientUserId = newsItem.getCreatedForUserId();
    	log.log(Level.INFO,"Dispatching for iOS user: " + recepientUserId + ", token: " + registration.getToken());
    	delegateTheDeliveryToAWorker(newsItem, registration.getToken(),"notification-pass-to-ios-worker","/admin/push/notifications/deliver");
    	return CallResultCode.DONE;
    }
    
	  private void delegateTheDeliveryToAWorker(NewsItem newsItem, String token, String queueName, String workerUrl) {
		  log.log(Level.INFO,"Delegating to a task, via queue " + queueName);
		  Queue queue = QueueFactory.getQueue(queueName);
		  
		  NewsAlert newsAlert = newsItemToNewsAlert(newsItem);
		  
		  TaskOptions taskOptions = TaskOptions.Builder.withMethod(TaskOptions.Method.POST).url(workerUrl).param("alert", newsAlert.getMessage()).param("token", token);
		  if(newsAlert.getAction() != null) taskOptions = taskOptions.param("action", newsAlert.getAction());
		  if(newsAlert.getSubjectId() != null) taskOptions = taskOptions.param("subjectId", newsAlert.getSubjectId());
		  if(newsAlert.getSubjectTitle() != null) taskOptions = taskOptions.param("subjectTitle", newsAlert.getSubjectTitle());
		  
		  queue.add(taskOptions);
	  }
	  
	  private NewsAlert newsItemToNewsAlert(NewsItem newsItem) {
		  switch(newsItem.getNewsType()) {
			case INVITED_TO_ACTIVITY:
				return new NewsAlert("You have been invited to " + newsItem.getSubjectTitle(),newsItem.getCreatedOn());
			
			case ACTIVITY_CANCELLED:
				return new NewsAlert(newsItem.getSubjectTitle() + " has been cancelled", newsItem.getCreatedOn());
				
			case NEW_COMMENT:
				NewsAlert ret = new NewsAlert("New comment on " + newsItem.getSubjectTitle(), newsItem.getCreatedOn());
				ret.setAction("showComments");
				ret.setSubjectId(newsItem.getSubjectId());
				ret.setSubjectTitle(newsItem.getSubjectTitle());
				return ret;
				
			case NEW_WHEN_SUGGESTION:
				return new NewsAlert("New time proposed for " + newsItem.getSubjectTitle(), newsItem.getCreatedOn());
				
			case NEW_WHERE_SUGGESTION:
				return new NewsAlert("New place proposed for " + newsItem.getSubjectTitle(), newsItem.getCreatedOn());
				
			case ACTIVITY_TITLE_EDITED:
				return new NewsAlert(newsItem.getSubjectTitle() + " changed name to " + newsItem.getAdditionalValue(), newsItem.getCreatedOn());
				
			case ACTIVITY_DESCRIPTION_EDITED:
				return new NewsAlert("Description changed on " + newsItem.getSubjectTitle(), newsItem.getCreatedOn());
				
			case ACTIVITY_TIME_EDITED:
				return new NewsAlert("Time changed on " + newsItem.getSubjectTitle(), newsItem.getCreatedOn());	
				
			case ACTIVITY_PLACE_EDITED:
				return new NewsAlert("Place changed on " + newsItem.getSubjectTitle(), newsItem.getCreatedOn());
				
			case ACTIVITY_TYPE_EDITED:
				return new NewsAlert("Event type changed on " + newsItem.getSubjectTitle(), newsItem.getCreatedOn());					
				
			default:
				throw new UnsupportedNewsTypeException(newsItem.getNewsType().toString());
		}
	  }
	  
		public static class UnsupportedNewsTypeException extends RuntimeException {
			public UnsupportedNewsTypeException(String message) {
				super(message);
			}
		}	  

}
