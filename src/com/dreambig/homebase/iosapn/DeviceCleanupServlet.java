package com.dreambig.homebase.iosapn;

import com.dreambig.homebase.entity.DeviceType;
import com.dreambig.homebase.entity.UserTicket;
import com.dreambig.homebase.repository.DataAccess;
import com.dreambig.homebase.repository.EMF;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * HttpServlet for removing inactive or invalid device registration information.
 *
 * It is intended to be called by Push Task Queue, so the request is retried if it fails.
 *
 */
public class DeviceCleanupServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;
  private static final Logger log = Logger.getLogger(DeviceCleanupServlet.class.getName());


  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    if (!Configuration.isRequestFromTaskQueue(req, resp)) {
      return;
    }

    String devicesJSon = req.getParameter("devices");
    if (devicesJSon == null) {
      log.warning("Missing 'devices' argument on task queue request. This indicates a bug");
      return;
    }
    String[] devices = null;
    try {
      devices = new Gson().fromJson(devicesJSon, String[].class);
    } catch (JsonSyntaxException e) {
      log.warning(
          "Invalid format of 'devices' argument on task queue request. This indicates a bug");
      return;
    }
    removeDevices(devices);
  }

  private void removeDevices(String[] devices) {
    DataAccess dataAccess = new DataAccess();

    for (String deviceToken : devices) {
    	List<UserTicket> registrations = dataAccess.getUserTicketByTokenAndType(deviceToken, DeviceType.iOS);

        // Remove the registration if it hasn't been already removed or recently (re-)registered.
    	for(UserTicket registration : registrations) {

	          log.log(Level.INFO,"Removing token for user id: " + registration.getUserId());
	          dataAccess.removeUserTicketInTransaction(registration);
	        
    	}
    }
    
  }

}
