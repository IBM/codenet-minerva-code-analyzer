<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>ERWW Table Loader Web App</title>

<!--Styles-->
<STYLE type="text/css">
<!--
BODY
{
background-color: #f8f7cd;
}
H1 {
	text-align: center !IMPORTANT;
}
H2 {
	color: #993333 !important;
	text-align: left !important;
}
TH {
	text-align: center !IMPORTANT;
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
</STYLE>

</head>
<body>

<!--Banner-->
<h1>WebTableLoaderLite</h1>
 
<br><br>

<form name="myForm" method="POST" action="TableLoaderServlet">
	<table style="background-color:#CCCCCC; border-color:#FFCC99;"> 
	  	<tbody>  
	  		<tr>
				<td>Number of Warehouses to load</td>
				<td><input name='warehouseCount' type='text' value='0' /></td>
			</tr>
			<tr>
				<td>Schema Name</td>
				<td><input name='schemaName' type='text' value='CBIVP'/></td>
			</tr>
		</tbody>
	</table>
	<br>
	<input TYPE="SUBMIT" NAME="command" VALUE="LoadTables">
</form>

</body>
</html>