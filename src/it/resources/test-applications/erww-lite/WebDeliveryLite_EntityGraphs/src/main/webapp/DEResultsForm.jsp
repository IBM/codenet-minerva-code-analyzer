<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"><%@page
	language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="deliverysession.DeliveryOutput, java.lang.String"%>
<html>
<head>
<title>Delivery Lite Results</title>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<!--styles-->
<style>
<!--
BODY {
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
-->
</style>
</head>
<body>
	<h1>Delivery Lite Results</h1>
	<table border="8" bgcolor="#cccccc" bordercolor="#FFCC99">
		<%
int length = 0;	
DeliveryOutput output = (DeliveryOutput)request.getAttribute("output");
Long fetchGraphTime = (Long)request.getAttribute("fetchGraphTime");
Long loadGraphTime = (Long)request.getAttribute("loadGraphTime");
length = output.getSingleDeliveries().length;
if (length != 0) { %>
		<tr>
			<td><%= "Average Time using Fetch Graph: " + fetchGraphTime + " milliseconds"%></td>
		</tr>
		<tr>
			<td><%= "Average Time using Load Graph: " + loadGraphTime + " milliseconds"%></td>
		</tr>
		<%
    for (int i = 0; i < length; i++) {
        if (output.getSingleDeliveries()[i].getCustomerId() != 0){
%>
		<tr>
			<td><%= "_________________________________"%></td>
		</tr>
		<tr>
			<td><%= "Delivered the following to Customer ID: " + output.getSingleDeliveries()[i].getCustomerId()%></td>
		</tr>
		<tr>
			<td><%= "Warehouse ID: " + output.getSingleDeliveries()[i].getWarehouseId()%></td>
		</tr>
		<tr>
			<td><%= "District ID: " + output.getSingleDeliveries()[i].getDistrictId()%></td>
		</tr>
		<tr>
			<td><%= "Order ID: " + output.getSingleDeliveries()[i].getOrderId()%></td>
		</tr>
		<tr>
			<td><%= "Amount: " + output.getSingleDeliveries()[i].getAmount()%></td>
		</tr>
		<tr>
			<td><%= "_________________________________"%></td>
		</tr>
		<%       
        }else{
%>
		<tr>
			<td><%= "No deliveries for Warehouse ID: "  + output.getSingleDeliveries()[i].getWarehouseId() + " and District ID: " + output.getSingleDeliveries()[i].getDistrictId() %></td>
		</tr>
		<% 
       }
    }
%>
		<tr>
			<td><%= "_________________________________"%></td>
		</tr>
		<%
%>
		<tr>
			<td><%= "Status: " + output.getStatus()%></td>
		</tr>
		<%
}
%>
	</table>
</body>
</html>