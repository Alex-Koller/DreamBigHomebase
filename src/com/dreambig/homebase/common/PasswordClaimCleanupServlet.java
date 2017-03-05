package com.dreambig.homebase.common;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dreambig.homebase.entity.PasswordClaimStatus;
import com.dreambig.homebase.repository.DataAccess;


public class PasswordClaimCleanupServlet extends HttpServlet {

	  private static final long serialVersionUID = 1L;
	  private static final Logger log = Logger.getLogger(PasswordClaimCleanupServlet.class.getCanonicalName());
	  private DataAccess dataAccess = new DataAccess();

	  @Override
	  public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		  log.log(Level.INFO,"Cleaning up password claim objects");
		  //delete all older than 24 hours that's in password provided state (should be majority)
		  int nUpdated = dataAccess.deleteAllPasswordClaims(getHorizonDate(), PasswordClaimStatus.PASSWORD_PROVIDED);
		  log.log(Level.INFO,"This many PasswordClaim objects deleted: " + nUpdated);
	  }	
	  
	  private Date getHorizonDate() {
		  Calendar c = Calendar.getInstance();
		  c.add(Calendar.HOUR_OF_DAY, -24);
		  return c.getTime();
	  }
	
}
