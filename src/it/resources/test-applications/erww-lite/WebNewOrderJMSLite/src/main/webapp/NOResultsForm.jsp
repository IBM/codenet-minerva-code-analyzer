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



<TITLE>New Order Lite JMS 2.0 Results</TITLE>

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

<jsp:useBean id="NewOrderJMSResults" scope="session" class="neworder.jms.lite.NewOrderJMSResults" type="neworder.jms.lite.NewOrderJMSResults" /> 


<!--Banner-->
<H1>New Order Lite JMS 2.0 Results</H1>

<BR><BR>
<%-- Results Overview Table --%>
<TABLE border="8" bgcolor="#cccccc" bordercolor="#FFCC99">
	<TBODY>
		<TR>
			<TH>Messages:</TH>
			<TD><%=NewOrderJMSResults.getMessage1()%></TD>			
		</TR>
		<TR>	
		    <TH></TH>
			<TD><%=NewOrderJMSResults.getMessage2()%></TD>
		</TR>	
		<TR>	
		    <TH></TH>
			<TD><%=NewOrderJMSResults.getMessage3()%></TD>
		</TR>
		<TR>
			<TH>Status:</TH>
			<TD><%=NewOrderJMSResults.getStatus()%></TD>
		</TR>
		<TR>
			<TH>Exception:</TH>
			<TD><%=NewOrderJMSResults.getException()%></TD>
		</TR>
	</TBODY>
</TABLE>

<H5>Note:  Elapsed time is the cumulative time, starting from when the "onMessage" method is invoked on the NewOrder MDB, and continuing until the New Order transaction completes.</H5>

<BR>
<BR>
<H3>New Order JMS Details:</H3>

<!-- New Order Details Table -->
<TABLE border="8" bgcolor="#cccccc" bordercolor="#FFCC99">
	<TBODY>
		<%
int length = 0;	
String[] output = NewOrderJMSResults.getOutputDetails();
if (output==null) System.out.println("output array is null");
length = output.length;
    for (int i = 0; i < length; i++) {
    %>
        	<tr><td><%= output[i] %></td></tr>
    <%} %>
		
	</TBODY>
</TABLE>
</BODY>
</HTML>
