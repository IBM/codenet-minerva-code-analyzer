<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Orders Query Input Form: JDBC 4.1, Java 8, EL 3.0 Version</title>

<!--Styles-->
<STYLE type="text/css">
<!--
BODY {
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
</STYLE>
</head>
<body>
	<!--Java Script-->
	<SCRIPT language="JavaScript" type="text/javascript">
	<!--
		function submitForm() {
			document.myForm.submit()
		}
	//-->
	</SCRIPT>

	<!--Banner-->
	<H1>Delivered Orders Query Input Form</H1>
	<H1>JDBC 4.1 (try-with-resources), Java 8, EL 3.0 Version</H1>
	<H4>Please select "AutoGeneration" to query delivered orders from one random warehouse/district combination.</H4>
	<H4>Please select "Manual" to query one or more delivered orders from a particular warehouse/district combination.</H4>
	<BR>

	<!--Form-->
	<FORM name="myForm" method="POST" action="OrdersQueryServlet">
		<INPUT TYPE="SUBMIT" NAME="command" VALUE="AutoGeneration"> 
		<INPUT TYPE="SUBMIT" NAME="command" VALUE="Manual"> 

		<h3>If you choose "Manual," the form below must be completed in its entirety. 
		    Every field value must contain an integer within the specified ranges.</h3>
		<!--Input Fields-->
		<TABLE border="8" bgcolor="#cccccc" bordercolor="#FFCC99">
			<TBODY>
				<TR>
					<TH>Minimum Warehouse ID*</TH>
					<TD><INPUT name='minWarehouseId' type='text' value=0></TD>
					<TH>Minimum District ID (1-10)</TH>
					<TD><INPUT name='minDistrictId' type='text' value=0></TD>
				</TR>
				<TR>
					<TH>Maximum Warehouse ID*</TH>
					<TD><INPUT name='maxWarehouseId' type='text' value=0></TD>
					<TH>Maximum District ID (1-10)</TH>
					<TD><INPUT name='maxDistrictId' type='text' value=0></TD>
				</TR>
		   </TBODY>
		</TABLE>		
		<ul style="list-style-type:disc">
			<li>Valid Warehouse ID(s): small tables = 1, medium tables = 1-25, large tables = 1-75 (inclusive)</li>
		</ul>
		<TABLE border="8" bgcolor="#cccccc" bordercolor="#FFCC99">
			<TBODY>
				<TR>
					<TH>Initial Database Network Timeout value (ms)</TH>
					<TD><INPUT name='initialNetworkTimeout' type='text' value=0></TD>
				</TR>
				<TR>
					<TH>Retry Database Network Timeout value (ms)</TH>
					<TD><INPUT name='retryNetworkTimeout' type='text' value=0></TD>
				</TR>
		   </TBODY>
		</TABLE>
		<ul style="list-style-type:disc">
			<li>The default Database Network Timeout value is 0 which means there is no Network Timeout.</li>
			<li>If the Database Network Timeout value is exceeded, one Retry will occur using the Retry Database Network Timeout value.</li>
		</ul>
	</FORM>
</body>
</html>