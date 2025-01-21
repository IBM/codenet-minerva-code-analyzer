<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    session="false"
    pageEncoding="ISO-8859-1"%>
<%@ page import="orderstatus.lite.ws.OrderStatusOutput" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:f="http://java.sun.com/jsf/core" xmlns:h="http://java.sun.com/jsf/html">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>Web Services: JAX-WS/JAXB: Order Status Lite Results</title>
<!--styles-->
<style>
<!--
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
-->
</style>
</head>
<body>
<h1>Web Services: JAX-WS/JAXB: Order Status Lite Results</h1>
<f:view>
<table border="8" style="background-color:#CCCCCC; border-color:#FFCC99;">
<%
int length = 0;	
OrderStatusOutput output = (OrderStatusOutput)request.getAttribute("output");
if (output != null) {
	length = output.getOutOrderLineCount();
%>
      <tr><td><%= "Warehouse ID: " + output.getOutCustomerWarehouseId() %></td></tr>
      <tr><td><%= "District ID:  " + output.getOutCustomerDistrictId()%> </td></tr>      
      <tr><td><%= "Customer ID: " + output.getOutCustomerId() %></td></tr>
      <tr><td><%= "Customer First Name:  " + output.getOutCustomerFirstName() %></td></tr>      
      <tr><td><%= "Customer Middle Name: " + output.getOutCustomerMiddleName() %></td></tr>      
      <tr><td><%= "Customer Last Name:  " + output.getOutCustomerLastName()%> </td></tr>           
      <tr><td><%= "Customer Customer Balance : " + output.getOutCustomerBalance() %></td></tr>      
      <tr><td><%= "Order ID:  " + output.getOutOrderId() %></td></tr>     
      <tr><td><%= "Order Entry Date: " + output.getOutOrderEntryDate() %></td></tr>             
      <tr><td><%= "Order Carrier ID:  " + output.getOutOrderCarrierId() %></td></tr> 
      <tr><td><%= "Orderline Count: " + output.getOutOrderLineCount() %></td></tr>      
      <tr><td><%= "Output stcNum: " + output.getStcnum() %></td></tr>
<%
}else{
%>
	<tr><td><%= "Error:  Output instance is null "%></td></tr>
<%
}
if (output.getOutOrderLines() != null) {
    for (int i = 0; i < length; i++) {
%>
    	<tr><td><%= "_____________________________"%></td></tr>
        <tr><td><%= "Supply Warehouse ID: " + output.getOutOrderLines().get(i).getOutOlSupplyWarehouseId()%></td></tr>
        <tr><td><%= "Item ID: " + output.getOutOrderLines().get(i).getOutOlItemId()%></td></tr>
        <tr><td><%= "Item Quantity: " + output.getOutOrderLines().get(i).getOutOlQuantity()%></td></tr>
        <tr><td><%= "Item Amount: " + output.getOutOrderLines().get(i).getOutOlAmount()%></td></tr>
        <tr><td><%= "Item Delivery Date: " + output.getOutOrderLines().get(i).getOutOlDeliveryDate()%></td></tr>
        <tr><td><%= "_____________________________"%></td></tr>
<%       
    }
}

%>	
        <tr><td><%= "Status: " + output.getRetc()%></td></tr>
</table>
</f:view>
</body>
</html>