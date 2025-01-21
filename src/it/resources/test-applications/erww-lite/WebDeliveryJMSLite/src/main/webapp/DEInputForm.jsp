<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<HTML>
<HEAD>
<%@page language="java" contentType="text/html; charset=ISO-8859-1"	pageEncoding="ISO-8859-1"%>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Delivery JMS Lite Input Form</title>
</HEAD>

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

<body>

<%-- Java Script --%>
<script language="JavaScript" type="text/javascript">
</script>

<%-- Banner --%>
<H1>Delivery JMS (1.1) Lite Input Form</H1>

<%-- Form --%>
<form name="myForm" method="POST" action="DEJSController">
<input TYPE="SUBMIT" NAME="command" VALUE="AutoGeneration">
<input TYPE="SUBMIT" NAME="command" VALUE="Manual">
<br>
<br>
  
<%-- Input Fields --%>  
<H3>Required Input Fields (for manual input only)</H3>
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

