<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Order Archive Input Form: JDBC 4.1 (RowSets), Java 8, EL 3.0 Version</title>

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
	<H1>Order Archive Input Form</H1>
	<H1>JDBC 4.1 (RowSets), Java 8, EL 3.0 Version</H1>
	<H4>Please select "AutoGeneration" to remove all delivered orders from a random warehouse/district combination.</H4>
	<H4>Please select "Manual" to remove all delivered orders from the warehouse/district combination(s) that you select below.</H4>
	<BR>

	<!--Form-->
    <FORM name="myForm" method="POST" action="OrderArchiveServlet_JDBC_EL">
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
		<h4>Considerations:</h4>
		<ul style="list-style-type:disc">
		<li>This is an Update transaction that uses Disconnected RowSets which are only connected to the database when they want to read or write; otherwise, the RowSets are disconnected.</li>
			<li>If the Transaction Timeout value is exceeded, one Retry will occur which only selects the minimum Warehouse ID and the minimum District ID.</li>
			<li>The Transaction Timeout value can be changed in server.xml.</li>
			<li>Run with more data initially in order to exceed the Transaction Timeout, then Retry with less data.</li>
		</ul>
	</FORM>
</body>
</html>