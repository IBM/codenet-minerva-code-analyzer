<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page
	language="java" contentType="text/html; charset=ISO-8859-1"
	errorPage="error.jsp"
	pageEncoding="ISO-8859-1"%>
<html>
<head>
<title>WebClient3ResultsForm</title>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<%@ taglib prefix="callSessionTag" uri="http://WebSessionClient3Lite/CallSessionTag.tld"%>
<TITLE>Client3 - Session Test Results page</TITLE>

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

H2 {
	color: 993333 !important;
	text-align: left !important;
}

H3 {
	color: 003399 !important;
	text-align: left !important;
}

H4 {
	color: 993333 !important;
	text-align: left !important;
	size: 3;
}

TH {
	text-align: left !important;
	color: #000000 !important;
	vertical-align: top !important;
}

TD {
	text-align: left !important;
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
<!--Style Sheet-->
</head>
<body>
<!--Banner-->
		<callSessionTag:session>
 				<jsp:attribute name="jspFragment1"> 
					<H1>Client3 - Session Test Results page</H1><BR>
					<H2>${message}</H2>
					<H3>===================================================================</H3>
					<H3>Information Generated from Previous Browser Invocation:</H3>
					<H3>-----------------------------------------------------------------------------</H3>
					<H3>Values Retrieved from JSessionID:  ${JSESSIONIDvalue}</H3>  
					<H4>ItemId: ${previousItemId}</H4>
					<H4>ItemPrice: ${previousItemPrice}</H4> 
					<H3>Value Retrieved from the ItemCookie:</H3> 
					<H4>ItemId: ${previousItemCookie}</H4> 
					<H4>Note: The above values will be null when there was not a previously
						existing Session</H4> 
					<H3> ===================================================================</H3>
					<H3>Information Generated from Current Browser Invocation:</H3>
					<H3>-----------------------------------------------------------------------------</H3>
					<H3>Value Stored in the Session and the ItemCookie:</H3>
					<H4>ItemId: ${itemId}</H4> 
					<H3>Value Stored in the Session:</H3>
					<H4>ItemPrice: ${itemPrice}</H4>
                    <H3> ===================================================================</H3>
				</jsp:attribute> 
		</callSessionTag:session>  
</body>
</html>
