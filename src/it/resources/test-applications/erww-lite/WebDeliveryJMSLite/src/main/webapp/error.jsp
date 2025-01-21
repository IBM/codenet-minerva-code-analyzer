<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page isErrorPage="true"%>
<%@ page import="irwwbase.UserException" %>
<%@ page import="java.io.PrintWriter" %>

<HTML>
<HEAD>
<META name="GENERATOR" content="IBM WebSphere Studio">
<TITLE>error.jsp</TITLE>
</HEAD>
<BODY text="red">

An error was encountered while processing your request.  Sorry about that but it happens sometime! 
<BR>
<BODY text="black">

<%if (exception !=null) 
   {
     if (exception instanceof irwwbase.UserException)  
       {
		out.println("********** Exception that session bean caught **************");
		UserException ue = (UserException)exception;
		out.println(ue.getEx());
		//ex.printStackTrace();
	    //ex.printStackTrace(new PrintWriter(out)); 
	    ue.printStackTrace(); 
		ue.printStackTrace(new PrintWriter(out)); 
	   }
     else 
      { 
       exception.printStackTrace();
	   exception.printStackTrace(new PrintWriter(out)); 
	  } 
	 
   }%>

</BODY>
</HTML>