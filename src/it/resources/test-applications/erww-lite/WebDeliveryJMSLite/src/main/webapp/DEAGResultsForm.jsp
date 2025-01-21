<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page 
contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" 
%>
<%@ page isErrorPage="true"%>
<%@ page import="java.io.PrintWriter" %>
<%@ page import="deliverysession.DeliveryOutput" %>
<HTML>
<HEAD>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"%>
<META http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<TITLE>Delivery JMS Results - Auto Generation Version</TITLE>

<%-- Styles --%>
<STYLE type="text/css">
BODY
{
background-color: #f8f7cd;
}
H1 {
	text-align: center !IMPORTANT;
}

TH {
	text-align: left !IMPORTANT;
	vertical-align: top !IMPORTANT;
}

TD {
	text-align: left !IMPORTANT;
	vertical-align: top !IMPORTANT;
}

TH.result {
	background-color: #999999 !IMPORTANT;
}

TD.result {
	background-color: #cccccc;
	vertical-align: top !IMPORTANT;
}
</STYLE>

</HEAD>

<BODY>

<jsp:useBean id="dEBean" scope="request"
	class="delivery.jms.lite.DeliveryJMSClient" type="delivery.jms.lite.DeliveryJMSClient" />
<jsp:setProperty name="dEBean" property="distributed"
	value='<%=Boolean.valueOf(request.getParameter("distributed")).booleanValue()%>' />	
<jsp:setProperty name="dEBean" property="jsFlow"
	value="false" />	

<%
Throwable tt=null;

// Assume failure, until proven otherwise
String status="FAILED";
String status2="none";

//Execute client bean method for auto generation of input
try {
  dEBean.webAutoGenInput();
  
  // We have success, unless we got an exception
  status="SUCCESSFUL";
  status2 = "none";
}
catch (Throwable t) {
	tt=t;
	// We failed, report exception info in status2
	status="FAILED";
	if (t.getCause()!=null) {
	  status2=t.getCause().getMessage();
	} else {
	  status2=t.getMessage();
	}
}
%>

<%-- Banner --%>
<H1>Delivery JMS (1.1) Results</H1>
<H3 ALIGN="CENTER">(Auto Generation Version)</H3>

<BR>
<BR>

<%-- Result Table --%>
<TABLE border="8" bgcolor="#cccccc" bordercolor="#FFCC99">
	<TBODY>
		<TR>
			<TH>Output:</TH>
			<TD><%=dEBean.getOutput()%></TD>
		</TR>
		<TR>
			<TH>Status:</TH>
			<TD><%=status%></TD>
		</TR>
		<TR>
			<TH>Exception:</TH>
			<TD><%=status2%></TD>
		</TR>
	</TBODY>
</TABLE>

<BR>
<BR>

<H2>Delivery details:</H2>

<table border="8" bgcolor="#cccccc" bordercolor="#FFCC99">
<%
int length = 0;	
DeliveryOutput output = (DeliveryOutput)dEBean.getDeliveryOutput();
if (output != null) {
  length = output.getSingleDeliveries().length;
  if (length != 0) {
    for (int i = 0; i < length; i++) {
       if (output.getSingleDeliveries()[i].getCustomerId() != 0){
%>
        	<tr><td><%= "_________________________________"%></td></tr>
        	<tr><td><%= "Delivered the following to Customer ID: " + output.getSingleDeliveries()[i].getCustomerId()%></td></tr>
        	<tr><td><%= "Warehouse ID: " + output.getSingleDeliveries()[i].getWarehouseId()%></td></tr>
        	<tr><td><%= "District ID: " + output.getSingleDeliveries()[i].getDistrictId()%></td></tr>
        	<tr><td><%= "Order ID: " + output.getSingleDeliveries()[i].getOrderId()%></td></tr>
        	<tr><td><%= "Amount: " + output.getSingleDeliveries()[i].getAmount()%></td></tr>
        	<tr><td><%= "_________________________________"%></td></tr>
<%       
        }else{
%>
        	<tr><td><%= "No deliveries for Warehouse ID: "  + output.getSingleDeliveries()[i].getWarehouseId() + " and District ID: " + output.getSingleDeliveries()[i].getDistrictId() %></td></tr>
<% 
       }
    }
  }    
}
%>	
</table>

<br>
<br>
<%
if (tt!=null) {
%> 
   <H3>*** Exception ***</H3>
<%  
   tt.printStackTrace(new PrintWriter(out));   
   if (tt.getCause()!=null) {
%>       
      <H3>*** Nested Exception ***</H3>
<%      
      tt.getCause().printStackTrace(new PrintWriter(out));
   }
}
%>
</BODY>
</HTML>
