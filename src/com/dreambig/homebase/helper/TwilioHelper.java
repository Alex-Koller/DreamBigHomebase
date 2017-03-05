package com.dreambig.homebase.helper;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.MessageFactory;
import com.twilio.sdk.resource.instance.Account;
import com.twilio.sdk.resource.instance.Message;


public class TwilioHelper {
	
	
	private static final String ACCOUNT_SID = "AC0248be775b39af10a96258973dfe8e49";
    private static final String AUTH_TOKEN = "04f7238c8d5be9aacf7404788d0d6944";
    //private static final String TWILIO_OUR_NUMBER = "+441172001538"; uk. domestic texts only
    private static final String TWILIO_OUR_NUMBER = "+15016510529";
	
	
	public void sendConfirmationMessage(String phoneNumber, String code) throws TwilioRestException {
		sendMessage(phoneNumber, createConfirmationMessageBody(code));
	}
	
	public void sendSomethingWrongMessage(String phoneNumber) throws TwilioRestException {
		sendMessage(phoneNumber,createSomethingWrongBody());
	}	

	private void sendMessage(String phoneNumber, String body) throws TwilioRestException {
        TwilioRestClient client = new TwilioRestClient(ACCOUNT_SID, AUTH_TOKEN);
        
        Account account = client.getAccount();
 
        MessageFactory messageFactory = account.getMessageFactory();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        
        params.add(new BasicNameValuePair("To", makeUsFriendly(phoneNumber))); 
        params.add(new BasicNameValuePair("From", TWILIO_OUR_NUMBER)); 
        params.add(new BasicNameValuePair("Body", body));
        Message sms = messageFactory.create(params);		
	}
	
	private String createConfirmationMessageBody(String code) {
		return "Great to hear you are about to start using BeApp4It. This is your confirmation code: " + code; 
	}
	
	private String createSomethingWrongBody() {
		return "Something went wrong on the servers and your confirmation code couldn't be generated :-(. Please re-try.";
	}
	
	private String makeUsFriendly(String phoneNumber) {
		//for some reason 00 is not accepted but + is for the US number
		if(phoneNumber.startsWith("00")) {
			phoneNumber = phoneNumber.replaceFirst("00", "+");
		}
		
		return phoneNumber;
	}
}
