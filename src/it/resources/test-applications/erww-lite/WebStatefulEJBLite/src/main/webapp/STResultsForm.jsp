<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page 
contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" 
errorPage="error.jsp"
%>
<HTML>
<HEAD>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<META name="GENERATOR" content="IBM WebSphere Studio">
<META http-equiv="Content-Style-Type" content="text/css">
<%@ taglib prefix="callStatefulTag" uri="http://WebStatefulEJBLite/CallStatefulTag.tld"%>
<!-- <LINK href="theme/Master.css" rel="stylesheet" type="text/css"> -->

<TITLE>Session Results</TITLE>

<!--Styles-->
<STYLE type="text/css">
<!--
H1 {
	text-align: center !IMPORTANT;
	color: "#000000";
}

TH {
	text-align: left !IMPORTANT;
	vertical-align: top !IMPORTANT;
	color: "#000000";
}

TD {
	text-align: left !IMPORTANT;
	vertical-align: top !IMPORTANT;
	color: "#000000";
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

</HEAD>
<body>

<jsp:useBean id="sTBean" scope="request" class="webstateful.Client" type="webstateful.Client" />
<jsp:setProperty name="sTBean" property="loopnum" value="${param.loopnum}" />
<jsp:setProperty name="sTBean" property="duration" value="${param.duration}" />

<!--Banner-->
<H1>Session Results</H1>

<BR><BR>

<!-- Result Table -->
<TABLE border="0">
	<TBODY>
		<callStatefulTag:web object="${sTBean}" >
 				<jsp:attribute name="jspFragment1"> 
					<TR>
						<TH>Results: </TH>
        				<td >${results}</td>
    				</tr>
					<TR>
						<TH>Status: </TH>
						<TD>${status}</TD>
					</TR>
 				</jsp:attribute> 
		</callStatefulTag:web>  
	</TBODY>
</TABLE>

<br><br>
<font color="#000000">Stack: </font><br>${stack}
</body>
</HTML>
