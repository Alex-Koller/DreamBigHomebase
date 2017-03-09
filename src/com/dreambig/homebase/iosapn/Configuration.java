package com.dreambig.homebase.iosapn;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Configuration {

	  /**
	   * The name of p12 certificate file. The certificate file needs to be deployed with other
	   * binaries. Eclipse does it automatically if the file is placed under the src folder
	   */

	  private static final String CERTTIFICATE_FILE_NAME = "apns-prod-cert.p12"; 
	  static final boolean USE_PRODUCTION_APNS_SERVICE = true;
	  
	  static final String CERTIFICATE_PASSWORD = "Ch00selife";
	  private static byte[] certificateBytes = null;

	  
	  /**
	   * Unauthenticated calls are disabled by default to prevent random clients from calling endpoints
	   * exposed by this sample and triggering push notification. Developers may want to temporarily
	   * allow unauthenticated calls during controlled tests if they don't have CLIENT_ID configured yet
	   *
	   */
	  //public static final boolean ALLOW_UNAUTHENTICATED_CALLS = false;
	  //public static final String CLIENT_ID = "<!!! INSERT YOUR CLIENT ID FOR IOS HERE !!!>";

	  private static final String TASKQUEUE_NAME_HEADER = "X-AppEngine-QueueName";

	  /**
	   * All push task queues used in this sample are configured to be available to admin only. This
	   * prevents random users from interfering with task queue request handlers. For additional
	   * protection, e.g, if a developer accidently changes the security configuration, all task queue
	   * request handlers in this sample are additionally checking for a presence of a header that is
	   * guaranteed to indicate that the request came from a Task Queue.
	   *
	   * @throws IOException
	   */
	  static boolean isRequestFromTaskQueue(HttpServletRequest request, HttpServletResponse response)
	      throws IOException {
	    String queueName = request.getHeader(TASKQUEUE_NAME_HEADER);
	    if (queueName == null || queueName.isEmpty()) {
	      response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
	      return false;
	    }
	    return true;
	  }

	  private static InputStream getCertificateStream() {
	    return Configuration.class.getClassLoader().getResourceAsStream(CERTTIFICATE_FILE_NAME);
	  }

	  protected static byte[] getCertificateBytes() {
	    if (certificateBytes == null) {
	      InputStream certifacteStream = getCertificateStream();
	      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

	      byte[] buffer = new byte[4096];

	      try {
	        int bytesRead = 0;
	        while ((bytesRead = certifacteStream.read(buffer, 0, buffer.length)) != -1) {
	          outputStream.write(buffer, 0, bytesRead);
	        }
	      } catch (IOException e) {
	        Logger.getLogger(Configuration.class.getCanonicalName())
	            .log(Level.SEVERE, "Error reading the certificate", e);
	      }

	      certificateBytes = outputStream.toByteArray();
	    }
	    return certificateBytes;
	  }

	public static boolean isUseProductionApnsService() {
		return USE_PRODUCTION_APNS_SERVICE;
	}	
	  
	  
	
}
