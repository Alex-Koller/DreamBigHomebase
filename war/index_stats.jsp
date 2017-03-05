<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<!-- The HTML 4.01 Transitional DOCTYPE declaration-->
<!-- above set at the top of the file will set     -->
<!-- the browser's rendering engine into           -->
<!-- "Quirks Mode". Replacing this declaration     -->
<!-- with a "Standards Mode" doctype is supported, -->
<!-- but may lead to some differences in layout.   -->

<%@ page import="com.dreambig.homebase.repository.DataAccess" %>
<%@ page import="java.util.List" %>
<%@ page import="com.dreambig.homebase.entity.UserTicket" %>
<%@ page import="com.dreambig.homebase.entity.PasswordClaim" %>
<%@ page import="com.dreambig.homebase.helper.DateTimeHelper" %>
<%@ page import="com.dreambig.homebase.iosapn.Configuration" %>

<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <title>DreamBig HomeBase</title>
  </head>

  <body>
    <h1>DreamBig HomeBase</h1>
    
    <%
        	DataAccess dataAccess = new DataAccess();
                List<UserTicket> userTickets = dataAccess.getUserTickets();
                List<PasswordClaim> passwordClaims = dataAccess.getPasswordClaims();
        		out.println("Number of registered users: " + userTickets.size() + ", iosAPN service: " + (Configuration.isUseProductionApnsService() ? "PROD" : "DEV"));

        		out.println("<table border='1'>");
        		out.println("<tr><th>User Id</th><th>Type</th><th>Token</th><th>Last Modified</th><th>Last News Time</th></tr>");
        		for(int i = 0;i < userTickets.size();i++) {
        			UserTicket userTicket = userTickets.get(i);
        	out.println("<tr>");
        		out.println("<td>" + userTicket.getUserId() + "</td>");
        		out.println("<td>" + userTicket.getDeviceType() + "</td>");
                        out.println("<td>" + userTicket.getToken() + "</td>");
        		out.println("<td>" + userTicket.getLastModified() + "</td>");
				out.println("<td>" + DateTimeHelper.timestampToString(userTicket.getLastNewsTimestamp()) + "</td>");
        	out.println("</tr>");
        		}
        		out.println("</table>");
        		
        		out.println("<br>Password claims</br>");
        		out.println("<table border='1'>");
        		out.println("<tr><th>Id</th><th>Phone Number</th><th>Status</th><th>Last Modified</th></tr>");
        		for(int i = 0;i < passwordClaims.size();i++) {
        			PasswordClaim passwordClaim = passwordClaims.get(i);
        	out.println("<tr>");
        		out.println("<td>" + passwordClaim.getClaimId() + "</td>");
        		out.println("<td>" + passwordClaim.getPhoneNumber() + "</td>");
                out.println("<td>" + passwordClaim.getPasswordClaimStatus() + "</td>");
				out.println("<td>" + DateTimeHelper.timestampToString(passwordClaim.getLastUpdate().getTime()) + "</td>");
        	out.println("</tr>");
        		}
        		out.println("</table>");        		
        %>
	
  </body>
</html>
