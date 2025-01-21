<!DOCTYPE HTML>
<%@page language="java"	contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@page import="erwwbase.cdi.input.output.pojos.OrderOutput" %>
<%@page import="erwwbase.cdi.input.output.pojos.OrderTrackingSingleInstance" %>
<%@page import="erwwbase.cdi.data.values.DataValues" %>
<%@page import="java.text.SimpleDateFormat" %>

<html>
<head>
<link rel="stylesheet" href="theme/Master.css" type="text/css">
<title>OrderTrackingResultsForm</title>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<style>
	body {background-color: #f8f7cd}
	
	H1 {
		color: black;
		font-family: 'times new roman';
		text-transform: capitalize
	}

	H2 {
		color: black;
		font-family: 'times new roman';
		text-transform: capitalize
	}

	H3 {
		color: black;
		font-family: 'times new roman';
		text-transform: none
	}
</style>
</head>
<body>

<body bgcolor="#F8F7CD">
<h1 align="center">ERWW Order Tracking Results</h1>
<h2 align="center">CDI 1.2, Bean Validation 1.1, JTA 1.2</h2>
<h2 align="center"><%= request.getParameter("inputPath") %></h2>
<%
	short zero = 0;
	String status1 = "Failed";
	String status2 = "Failed";
         
    short warehouseId = 0;
	short districtId = 0;
	short customerId = 0;
	int orderId = 0;
	String orderTrackingNumber = null;
	String orderTrackingNumberCompare = null;
         
	OrderOutput output = new OrderOutput();
	OrderTrackingSingleInstance [] orderTrackingInstances = null;
         
    output = (OrderOutput)request.getSession().getAttribute("output");
    if (output == null){
       	output.getOrderTrackingInstances()[0].setOrderTrackingNumber("No OrderTrackingNumber");
       	output.setOutCustomerWarehouseId(zero);
       	output.setOutCustomerDistrictId(zero);
       	output.setOutCustomerId(zero);
       	output.setOutOrderId(zero);
	} else if (output.getOrderTrackingInstances() == null){
       	output.getOrderTrackingInstances()[0].setOrderTrackingNumber("No OrderTrackingNumber");
       	warehouseId = output.getOutCustomerWarehouseId();
		districtId = output.getOutCustomerDistrictId();
		customerId = output.getOutCustomerId();
		orderId = output.getOutOrderId();
		status1 = output.getStatus();
	} else if (output.getOrderTrackingInstances()[0].getOrderTrackingNumber() == null){
       	output.getOrderTrackingInstances()[0].setOrderTrackingNumber("No OrderTrackingNumber");
       	warehouseId = output.getOutCustomerWarehouseId();
		districtId = output.getOutCustomerDistrictId();
		customerId = output.getOutCustomerId();
		orderId = output.getOutOrderId();
		status1 = output.getStatus();
	} else {
		warehouseId = output.getOutCustomerWarehouseId();
		districtId = output.getOutCustomerDistrictId();
		customerId = output.getOutCustomerId();
		orderId = output.getOutOrderId();
		orderTrackingNumber = output.getOrderTrackingInstances()[0].getOrderTrackingNumber();	
		status1 = output.getStatus();
			
		orderTrackingNumberCompare = (String.valueOf(warehouseId) + "-" + String.valueOf(districtId) +  "-" +  String.valueOf(customerId) +  "-" + String.valueOf(orderId));
			
		if (orderTrackingNumber.contentEquals(orderTrackingNumberCompare) && status1.contains("SUCCESSFUL")){
			status2 = "Successful";
		}
	}
%>
<hr	style="border-color: gray; border-style: outset; border-width: thick">

<h3 align="left">Order Tracking Number: <%= output.getOrderTrackingInstances()[0].getOrderTrackingNumber() %></h3>

<table border="1">
	<tbody>
		<tr bgcolor="#E6E6E6" align="left"> 
			<td><b>OrderId: </b></td>
			<td><b><%= output.getOutOrderId() %></b></td>
		</tr>
 		<tr bgcolor="#E6E6E6" align="left"> 
 			<td>WarehouseId: </td>
			<td><%= output.getOutCustomerWarehouseId() %></td>
		</tr>
		<tr bgcolor="#E6E6E6" align="left"> 
		 	<td>DistrictId: </td>
			<td><%= output.getOutCustomerDistrictId() %></td>
		</tr>
		<tr bgcolor="#E6E6E6" align="left"> 
		 	<td>CustomerId: </td>
			<td><%= output.getOutCustomerId() %> </td>
		</tr>
	</tbody>
</table>
<br/>
<h3 align="left">Order Tracking Details</h3>
<table border="1">
	<tbody>
<%
SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy  HH:mm");

if (output.getOrderTrackingInstances() != null) {
    for (int i = 0; i < output.getOutOrderTrackingHopsCount(); i++) {
%>
	<tr bgcolor="#E6E6E6" align="left">
    	<td><%= "Order Tracking Hop: " + output.getOrderTrackingInstances()[i].getOrderTrackingHop() + " " %></td>
     	<td><%=  output.getOrderTrackingInstances()[i].getOrderTrackingCity() + ", "  
       		 	 	+ output.getOrderTrackingInstances()[i].getOrderTrackingState() + ", "  
        		 	+ output.getOrderTrackingInstances()[i].getOrderTrackingCountry()  + " " %></td> 
 		
       	<td><%= simpleDateFormat.format(output.getOrderTrackingInstances()[i].getOrderTrackingDate())%></td> 
        <td><%= DataValues.ORDER_TRACKING_ACTIVITIES[output.getOrderTrackingInstances()[i].getOrderTrackingActivity()]  %></td>
	</tr>
<%       
    }
}
%>	
	</tbody>
</table>
<br/>

<table border="1">
	<tbody>
		<tr bgcolor="#E6E6E6" align="left"> 
			<td >Status: </td>
			<td ><%= status2 %></td>
		</tr>
	</tbody>
</table>
<br/>
<br/>
<table border="1">
	<TBODY>
		<TR>
			<TH class="result" align="center">
			<FORM METHOD=POST ACTION="../../WebLogOffLite/LogOffInputForm.jsp">
			   <INPUT type="submit" name="Modify Properties"
				value="Logoff" STYLE="color: black; background-color: #E6E6E6;"/>
			</FORM>
			</TH>
		</TR>	
	</TBODY>
</TABLE>
</body>
</html>