<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page 
contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" 
session="false"
errorPage="error.jsp"
%>
<HTML>
<HEAD>
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ page import="neworder.lite.ws.NewOrderOutputInfo" %>
<%@ page import="java.math.BigDecimal" %>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<META name="GENERATOR" content="IBM Software Development Platform">

<TITLE>Web Services: JAX-WS/JAXB with WS-Security: NewOrder Lite Results</TITLE>

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

<!--Banner-->
<h1>Web Services: JAX-WS/JAXB with WS-Security: NewOrder Lite Results</h1>

<table border="8" bgcolor="#cccccc" bordercolor="#FFCC99">
<%
int length = 0;	
NewOrderOutputInfo output = (NewOrderOutputInfo)request.getAttribute("output");
if (output != null) {
	length = output.getOutItemList().size();
%>
      <tr><td><%= "Warehouse ID: " + output.getOutWarehouseId() %></td></tr>
      <tr><td><%= "District ID:  " + output.getOutDistrictId()%> </td></tr>      
      <tr><td><%= "Customer ID: " + output.getOutCustomerId() %></td></tr>     
      <tr><td><%= "Customer Last Name:  " + output.getOutCustomerLastName()%> </td></tr>           
      <tr><td><%= "Customer Credit : " + output.getOutCustomerCredit() %></td></tr>      
      <tr><td><%= "Customer Discount:  " +  new BigDecimal(output.getOutCustomerDiscount()).setScale(2,BigDecimal.ROUND_HALF_UP) + "%" %></td></tr>    
      <tr><td><%= "Order ID: " + output.getOutOrderId() %></td></tr>                  
      <tr><td><%= "NumItems:  " + length %></td></tr>       
      <tr><td><%= "Warehouse Tax: " + output.getOutWarehouseTax() %></td></tr> 
      <tr><td><%= "District Tax:  " + output.getOutDistrictTax() %></td></tr>   
      <tr><td><%= "Date: " + java.util.Calendar.getInstance().getTime() %></td></tr>  
      <tr><td><%= "_____________________________"%></td></tr>
      <tr><td><%= "_______OrderDetails__________"%></td></tr>
      <tr><td><%= "_____________________________"%></td></tr>
<%
}else{
%>
	<tr><td><%= "Error:  Output instance is null "%></td></tr>
<%
}

if (output.getOutItemList() != null) {
    for (int i = 0; i < length; i++) {
%>
        <tr><td><%= "Supply Warehouse ID: " + output.getOutItemList().get(i).getOutItemSupplyWarehouseId()%></td></tr>
        <tr><td><%= "Item ID: " + output.getOutItemList().get(i).getOutItemId()%></td></tr>
        <tr><td><%= "Item Name: " + output.getOutItemList().get(i).getOutItemName()%></td></tr>
        <tr><td><%= "Item Quantity: " + output.getOutItemList().get(i).getOutItemQuantity()%></td></tr>
        <tr><td><%= "Stock Quantity: " + output.getOutItemList().get(i).getOutStockQuantity()%></td></tr>        
        <tr><td><%= "Item Total Price: " + new BigDecimal(output.getOutItemList().get(i).getOutItemTotal()).setScale(2,BigDecimal.ROUND_HALF_UP)%></td></tr>
        <tr><td><%= "_____________________________"%></td></tr>
<%       
    }
}
%>	
        <tr><td><%= "Grand Total: " + new BigDecimal(output.getOutTotal()).setScale(2,BigDecimal.ROUND_HALF_UP)%></td></tr>
        <tr><td><%= "Status: " + output.getOutMsg()%></td></tr>
</table>
</BODY>
</HTML>
