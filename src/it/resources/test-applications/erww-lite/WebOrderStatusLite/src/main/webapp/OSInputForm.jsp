<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:f="http://java.sun.com/jsf/core" xmlns:h="http://java.sun.com/jsf/html">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>Order Status Lite Input Form</title>
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
<!--Java Script-->
<script language="JavaScript" type="text/javascript">
<!--
	function submitForm(){
		document.myForm.submit();
	}
//-->
</script>

<!--Banner-->
<h1>Order Status Lite Input Form</h1>

<!--Form-->
<form name="myForm" method="post" action="OSController">

<input type="submit" name="inputType" value="AutoGen"/> 
<input type="submit" name="inputType" value="Manual"/> 

<h3>* Indicates Required Fields</h3>
<h3>** CustomerId or Customer Last Name required</h3>

<table border="8" style="background-color:#CCCCCC; border-color:#FFCC99;"> 
  <tbody>  
  		<tr>
			<th>warehouseId (e.g. 2)*</th>
			<td><input name='warehouseId' type='text' value='0' /></td>
		</tr>
		<tr>
			<th>districtId (e.g. 1)</th>
			<td><input name='districtId' type='text' value='0' /></td>
		</tr>
		<tr>
			<th>customerId (e.g. 13)
			(When customerLastName is supplied, customerID must be 0)</th>
			<td><input name='customerId' type='text' value='0' /></td>
		</tr>
		<tr>
			<th>customerLastName**
			(e.g. BARBARBAR)</th>
			<td><input name='customerLastName' type='text' value=''/></td>
		</tr>
	</tbody>
</table>
</form>
</body>
</html>
