package com.dreambig.homebase.iosapn;

import javapns.communication.exceptions.CommunicationException;
import javapns.communication.exceptions.KeystoreException;
import javapns.devices.Device;
import javapns.devices.Devices;
import javapns.devices.exceptions.InvalidDeviceTokenFormatException;
import javapns.devices.implementations.basic.BasicDevice;
import javapns.notification.AppleNotificationServer;
import javapns.notification.AppleNotificationServerBasicImpl;
import javapns.notification.Payload;
import javapns.notification.PushNotificationManager;
import javapns.notification.PushNotificationPayload;
import javapns.notification.PushedNotification;
import javapns.notification.PushedNotifications;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;

/**
 * PushNotificationSender encapsulates sending push notifications.
 */
class PushNotificationSender {
  private static final Logger log = Logger.getLogger(PushNotificationSender.class.getName());
  private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  private PushNotificationManager pushManager = new PushNotificationManager();
  private Object keystore;
  private String password;
  private boolean production;
  private boolean isConnected = false;
  Method processedFailedNotificationsMethod = null;

  /**
   * @constructor
   *
   * @param keystore a keystore containing a private key and the APNS certificate
   * @param password the keystore's password.
   * @param production true to use Apple's production servers, false to use the sandbox servers.
   */
  public PushNotificationSender(byte[] keystore, String password, boolean production) {
    this.keystore = keystore;
    this.password = password;
    this.production = production;

    try {
      processedFailedNotificationsMethod =
          pushManager.getClass().getDeclaredMethod("processedFailedNotifications");
      processedFailedNotificationsMethod.setAccessible(true);
    } catch (NoSuchMethodException e) {
      log.log(Level.SEVERE, "Incompatible JavaPNS library.", e);
    } catch (SecurityException e) {
      log.log(Level.SEVERE, "This code requires reflection permission.", e);
    }
  }

  private void initializeConnection() throws KeystoreException, CommunicationException {
    AppleNotificationServer server =
            new AppleNotificationServerBasicImpl(keystore, password, production);

    pushManager.initializeConnection(server);
    isConnected = true;
  }

  /**
   * Stop connection and closes the socket
   *
   * @throws CommunicationException thrown if an error occurred while communicating with the target
   *         server even after a few retries.
   * @throws KeystoreException thrown if an error occurs with using the certificate.
   */
  public void stopConnection() throws CommunicationException, KeystoreException {
    pushManager.stopConnection();
    isConnected = false;
  }

  /**
   * Sends an alert notification to a list of devices.
   *
   * @param alertMessage alert to be sent as a push notification
   * @param deviceTokens the list of tokens for devices to which the notifications should be sent
   * @return a list of pushed notifications that contain details on transmission results.
   * @throws CommunicationException thrown if an error occurred while communicating with the target
   *         server even after a few retries.
   * @throws KeystoreException thrown if an error occurs with using the certificate.
   */
  public PushedNotifications sendAlert(String alertMessage, String[] deviceTokens,Map<String,String> customProperties)
      throws CommunicationException, KeystoreException {

    log.info("Sending alert='" + alertMessage + "' to " + deviceTokens.length
        + " devices started at " + dateFormat.format(new Date()) + ".");
    PushNotificationPayload payload = PushNotificationPayload.alert(alertMessage);
    try {
    	payload.addSound("default");
    	enrichWithCustomDictionary(payload, customProperties);
    } catch (JSONException e) {
    	log.log(Level.WARNING,"Failed adding sound to notification",e);
    }
    PushedNotifications notifications =
        sendPayload(payload, deviceTokens);

    log.info("Sending alert='" + alertMessage + "' to " + deviceTokens.length
        + " devices completed at " + dateFormat.format(new Date()) + ".");

    return notifications;
  }

  /**
   * Sends a payload to a list of devices.
   *
   * @param payload preformatted payload to be sent as a push notification
   * @param deviceTokens the list of tokens for devices to which the notifications should be sent
   * @return a list of pushed notifications that contain details on transmission results.
   * @throws CommunicationException thrown if an error occurred while communicating with the target
   *         server even after a few retries.
   * @throws KeystoreException thrown if an error occurs with using the certificate.
   */
  public PushedNotifications sendPayload(Payload payload, String[] deviceTokens)
      throws CommunicationException, KeystoreException {
    PushedNotifications notifications = new PushedNotifications();

    if (payload == null) {
      return notifications;
    }

    try {
      if (!isConnected) {
        initializeConnection();
      }

      List<Device> deviceList = Devices.asDevices(deviceTokens);
      notifications.setMaxRetained(deviceList.size());
      for (Device device : deviceList) {
        try {
          BasicDevice.validateTokenFormat(device.getToken());
          PushedNotification notification = pushManager.sendNotification(device, payload, false);
          notifications.add(notification);
        } catch (InvalidDeviceTokenFormatException e) {
          notifications.add(new PushedNotification(device, payload, e));
        }
      }
    } catch (CommunicationException e) {
      stopConnection();
      throw e;
    }
    return notifications;
  }

  /**
   * Read and process any pending error-responses.
   */
  public void processedPendingNotificationResponses() {
    log.log(Level.INFO, "Processing sent notifications.");

    if (processedFailedNotificationsMethod == null) {
      return;
    }

    try {
      processedFailedNotificationsMethod.invoke(pushManager);
    } catch (Exception e) {
      // Catching all exception as the method requires handling 3+ reflection related exceptions
      // and 2+ JavaPNS exceptions. And there is nothing much that can be done when any of them
      // happens other than logging the exception.
      log.log(Level.WARNING, "Processing failed notifications failed", e);
    }
  }
  
  private void enrichWithCustomDictionary(Payload payload, Map<String,String> properties) throws JSONException {
	 
	  if(properties != null) {
		  for(Entry<String,String> entry : properties.entrySet()) {
			  payload.addCustomDictionary(entry.getKey(),entry.getValue());
		  }
	  }
	  
  }
}
