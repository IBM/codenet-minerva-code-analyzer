<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page 
contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" 
errorPage="error.jsp"
%>
<HTML>
<HEAD>
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<META name="GENERATOR" content="IBM Software Development Platform">



<TITLE>NewOrder Lite Results</TITLE>

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

<jsp:useBean id="nOBean" scope="session" class="neworder.lite.Client" type="neworder.lite.Client" /> 


<!--Banner-->
<H1>New Order Results</H1>

<BR><BR>

<!-- Result Table -->
<TABLE border="8" bgcolor="#cccccc" bordercolor="#FFCC99">
	<TBODY>
		<%
int length = 0;	
String[] output = nOBean.getOput();
if (output==null) System.out.println("output array is null");
length = output.length;
    for (int i = 0; i < length; i++) {
    %>
        	<tr><td><%= output[i] %></td></tr>
    <%} %>
		
	</TBODY>
</TABLE>
<br><br>
<font color="#000000">Stack:</font><br><% nOBean.getStack(); %>
</BODY>
</HTML>
