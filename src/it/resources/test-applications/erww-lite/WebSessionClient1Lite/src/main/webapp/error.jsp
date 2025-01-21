<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"><%@page
	language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page isErrorPage="true"%>
<%@ page import="irwwbase.UserException" %>
<%@ page import="java.io.PrintWriter" %>
<html>
<head>
<title>error</title>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
</head>
<body>
An error was encountered while processing your request. 

<%
	if (exception !=null) {
     if (exception instanceof irwwbase.UserException)  
       {
		out.println("********** Exception that session bean caught **************");
		UserException ue = (UserException)exception;
		out.println(ue.getEx());
	    ue.printStackTrace(); 
		ue.printStackTrace(new PrintWriter(out)); 
	   }
     else 
      { 
       exception.printStackTrace();
	   exception.printStackTrace(new PrintWriter(out)); 
	  } 
	 
   }
%>
</body>
</html>