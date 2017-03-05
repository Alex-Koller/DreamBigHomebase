package com.dreambig.homebase.iosapn;

import javapns.Push;
import javapns.communication.exceptions.CommunicationException;
import javapns.communication.exceptions.KeystoreException;
import javapns.devices.Device;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * HttpServlet for querying APNS Feedback service and retrieving the list of inactive devices and
 * initiating removal of the corresponding DeviceRegistration records.
 *
 * It is intended to be called by Push Task Queue or Cron
 *
 */
public class PushFeedbackProcessingServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;
  private static final Logger log =
      Logger.getLogger(PushFeedbackProcessingServlet.class.getCanonicalName());

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
    try {
      // Retrieving the list of inactive devices from the Feedback service
      List<Device> inactiveDevices = Push.feedback(
          Configuration.getCertificateBytes(), Configuration.CERTIFICATE_PASSWORD,
          Configuration.USE_PRODUCTION_APNS_SERVICE);

      if (inactiveDevices.size() > 0) {
        log.info("Number of inactive devices: " + inactiveDevices.size());
        // get the list of inactive device tokens
        List<String> inactiveDeviceTokens = new ArrayList<String>();
        for (Device device : inactiveDevices) {
          inactiveDeviceTokens.add(device.getToken());
        }
        PushNotificationUtility.enqueueRemovingDeviceTokens(inactiveDeviceTokens);
      }
    } catch (CommunicationException e) {
      log.log(Level.WARNING,
          "Retrieving the list of inactive devices failed with CommunicationException:"
          + e.toString(), e);
    } catch (KeystoreException e) {
      log.log(Level.WARNING,
          "Retrieving the list of inactive devices failed with KeystoreException:" + e.toString(),
          e);
    }
  }
}

