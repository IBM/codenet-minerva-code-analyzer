<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page 
contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" 
%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<HTML>
<HEAD>
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<META name="GENERATOR" content="IBM Software Development Platform">

<TITLE>Order Status (Concurrent) Lite Results</TITLE>

<!--Styles-->
<STYLE type="text/css">
<!--
BODY
{
background-color: #f8f7cd;
}
H1 {
	text-align: center !IMPORTANT;
	color: "#000000";
}

TH {
	text-align: left !IMPORTANT;
	vertical-align: top !IMPORTANT;
	color: "#000000";
}

TD {
	text-align: left !IMPORTANT;
	vertical-align: top !IMPORTANT;
	color: "#000000";
}

TH.result {
	background-color: #999999 !IMPORTANT;
}

TD.result {
	background-color: #cccccc;
	vertical-align: top !IMPORTANT;
}
-->
</STYLE>
</HEAD>
<BODY>
<jsp:useBean id="outputBean" scope="session" class="orderstatus.concurrent.ejb.lite.OrderStatusConcurrentOutput" type="orderstatus.concurrent.ejb.lite.OrderStatusConcurrentOutput" />
<!--Banner-->
<H1>Order Status (Concurrent) Results</H1>

<BR><BR>

<!-- Result Table -->
	<TABLE border="8" bgcolor="#cccccc" bordercolor="#FFCC99">
			<TBODY> 
		
					<TR>
						<TH>Status:</TH>
						<TD><font color="#000000" id="Status"><%= outputBean.getStatus() %></font></TD>
					</TR>
					<TR>
						<TH>Message:</TH>
						<TD><font color="#000000" id="Message"><%= outputBean.getMessage() %></font></TD>
					</TR>
					<TR> 
						<TH>Exception:</TH>
						<TD><font color="#000000" id="Exception"><%= outputBean.getException() %></font></TD>
					</TR>
					<TR>
						<TH>Stack:</TH>
						<TD><font color="#000000" id="Stack"><%= outputBean.getStack() %></font></TD>
					</TR>				
			</TBODY>
	</TABLE >


</body>
</html>
