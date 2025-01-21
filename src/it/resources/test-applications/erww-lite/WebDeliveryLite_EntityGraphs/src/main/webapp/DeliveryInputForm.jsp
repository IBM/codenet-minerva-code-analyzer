<!DOCTYPE HTML><%@page language="java"
	contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<html>
<head>
<title>Delivery Lite InputForm</title>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

<!--Styles-->
<style type="text/css">
<!--
BODY {
	background-color: #f8f7cd;
}

H1 {
	text-align: center !IMPORTANT;
}

TABLE {
	background-color: #cccccc;
	border-color: "#FFCC99";
	border:solid 8px;
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
	<script lang="JavaScript" type="text/javascript">
<!--
	function submitForm(){
		document.myForm.submit();
	}
//-->
</script>

	<!--Banner-->
	<H1>Delivery Lite EntityGraphs Input Form</H1>

	<!--Form-->
	<form name="myForm" method="POST" action="DeliveryServlet">
		<input TYPE="SUBMIT" NAME="inputType" VALUE="AutoGeneration">
		<input TYPE="SUBMIT" NAME="inputType" VALUE="Manual"> <br>

		<H3>Required Input Fields (for manual only)</H3>
		<!--Input Fields-->
		<table>
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