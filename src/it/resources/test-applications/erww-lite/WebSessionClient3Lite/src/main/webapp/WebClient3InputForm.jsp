<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"><%@page
	language="java" contentType="text/html; charset=ISO-8859-1"
	session="false"
	pageEncoding="ISO-8859-1"%>
<html>
<head>
<title>WebClient3InputForm</title>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

<title>Web Client 3 - Session Sharing/URL Rewriting Input Form</title>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta name="GENERATOR"
	content="Rational® Application Developer for WebSphere® Software">
	<!--Styles-->
<STYLE TYPE="text/css">
<!--
BODY {
	background-color: #f8f7cd !important;
}
H1 {
	color: #000000 !important;
	text-align: center !important;
}
TH {
	text-align:left !important;
	color: #000000 !important;
	vertical-align: top !important;
}
TD {
	text-align:left !important;
	vertical-align: top !important;
}
TH.result {
	background-color: #999999 !important;
}
TD.result {
	background-color: #cccccc;
	vertical-align: top !important;
}
-->
</STYLE>
</head>
<body>
<!--Java Script-->
<SCRIPT LANGUAGE="JavaScript" TYPE="text/javascript">
<!--
	function submitForm(){
		document.myForm.submit();
	}
//-->
</SCRIPT>

<!--Banner-->

<H1>Web Client 3 - Cookie/URL Rewriting with Session Sharing Input Form </H1>

<!--Form-->
<FORM name="myForm" method="POST" action="WebClient3Servlet">
	<INPUT TYPE="hidden" NAME="command" VALUE="WebClient3ResultsForm">
    <INPUT NAME="Submit" TYPE=Submit VALUE="Submit" onClick="href='javascript:submitForm()';">
	<BR><BR><BR>
<font face="arial" color="#003399" SIZE="4">If you are testing the Cookie path, ensure the following JVM property is specified: </font>
<BR><BR> 
<font face="arial" COLOR="#993333" SIZE="3">SESSION_TRACKING_MODE=COOKIE </font> <BR>
<BR>
<font face="arial" color="#003399" SIZE="4">If you are testing the URL Rewriting path, ensure the following JVM property is specified: </font>
<BR><BR> 
<font face="arial" COLOR="#993333" SIZE="3">SESSION_TRACKING_MODE=URL </font> <BR>
<BR><BR> 
<font face="arial" COLOR="#003399" SIZE="4">Note:</font> 
<font face="arial" COLOR="#003399" SIZE="3">AutoGen will be used to generate user input data </font> <BR>
</FORM>

</body>
</html>
