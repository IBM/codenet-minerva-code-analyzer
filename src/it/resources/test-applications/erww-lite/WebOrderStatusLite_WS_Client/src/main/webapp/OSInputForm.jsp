<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Web Services: JAX-WS/JAXB: Order Status Lite Input Form</title>
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
<h1>Web Services: JAX-WS/JAXB: Order Status Lite Input Form</h1>

<!--Form-->
<form name="myForm" method="post" action="OrderStatusServlet">

<input type=submit name="inputType" value="AutoGen"/> 
<input type=submit name="inputType" value="Manual"/> 

<h3>* Indicates Required Fields even when AutoGen is submitted</h3>

<table border="8" bgcolor="#cccccc" bordercolor="#FFCC99">
  <tbody>  
  		<tr>
			<th>Host where Web Service is running</th>
			<td><input name='ws_host' type='text' value='localhost' /></td>
		</tr>
		<tr>
			<th>Port where Web Service is running</th>
			<td><input name='ws_port' type='text' value='9080' /></td>
		</tr>
	</tbody>
</table>

<h3>* Indicates Required Fields</h3>
<h3>** CustomerId or Customer Last Name required</h3>

<table border="8" bgcolor="#cccccc" bordercolor="#FFCC99">
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
			<th>customerId (e.g. 13)**
			<br/>(when customerLastName is supplied, customerId must be 0)
			</th>
			<td><input name='customerId' type='text' value='0' /></td>
		</tr>
		<tr>
			<th>customerLastName (e.g. BARBARBAR)**
			</th>
			<td><input name='customerLastName' type='text' value=''/></td>
		</tr>
	</tbody>
</table>
<h4>Note: On tWAS, the com.ibm.websphere.webservices.WSDL_Generation_Extra_ClassPath JVM custom property needs to be specified pointing to the db2jcc4.jar location (i.e. /usr/lpp/db2/db2910/db2910_jdbc/classes/db2jcc4.jar).
On zOS, this JVM custom property needs to be specified for the Servant and Control Process definitions.</h4>

</form>
</body>
</html>
