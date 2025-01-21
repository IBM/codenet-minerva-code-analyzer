<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"><%@page
	language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<html>
<head>
<title>Delivery Lite InputForm</title>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

<!--Styles-->
<style type="text/css">
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
<H1>Delivery Lite Input Form (SSL Version)</H1>

<!--Form-->
<form name="myForm" method="POST" action="DeliveryServlet">
<input TYPE="SUBMIT" NAME="inputType" VALUE="AutoGeneration">
<input TYPE="SUBMIT" NAME="inputType" VALUE="Manual">
<br>
<H2>Invocation Mode</H2>
     <INPUT TYPE="radio" NAME="invocationModeRadios" VALUE="radio1" CHECKED>
     Synchronous Servlet, business logic in servlet
     <BR>
     <INPUT TYPE="radio" NAME="invocationModeRadios" VALUE="radio2">
     Synchronous Servlet, calls synchronous EJB, business logic in EJB
     <BR>
<br>
<H2>Application Security: Check if User is in Role</H2>
<select name="checkUserInRole">
    <option value="true">Yes, check if the user is in the CompanyRole</option>
    <option value="false" selected>No, do not check if the user is in the CompanyRole</option>
</select> 
<br>
<H3>Required Input Fields</H3>
<!--Input Fields-->
<table border="8" bgcolor="#cccccc" bordercolor="#FFCC99">
	<tbody>
		<tr>
			<th>Warehouse ID</th> 
			<td><input name='warehouseId' type='text' value=0></td>
		</tr>
		<tr>
			<th>Carrier ID</th>
			<td><input name='carrierId' type='text' value=0></td>
		</tr>
	</tbody>
</table>
</form>
</body>
</html>