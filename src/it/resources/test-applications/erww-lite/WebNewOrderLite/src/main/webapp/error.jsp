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

			An error was encountered while processing your request.
			<BR>

		<BODY text="black">

			<%
				if (exception !=null) {

				exception.printStackTrace();
				exception.printStackTrace(new PrintWriter(out)); 

				}
			%>

</BODY>
</HTML>