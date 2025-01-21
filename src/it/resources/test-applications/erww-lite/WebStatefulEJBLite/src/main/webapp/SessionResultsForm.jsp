<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="error.jsp"%>
<HTML>
<HEAD>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<META name="GENERATOR" content="IBM WebSphere Studio">
<%@ taglib prefix="callSessionTag" uri="http://WebStatefulEJBLite/CallSessionTag.tld"%>

<TITLE>session test results</TITLE>

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

<!--Banner-->
<H1>session test results</H1>

<BR><BR>

<!-- Result Table -->
<TABLE border="0">
	<TBODY>
		<callSessionTag:session>
 				<jsp:attribute name="jspFragment1"> 
					<TR>
						<TH>Results:</TH>
        				<TD >${results}</TD>
    				</TR>
					<TR>
						<TH>Status:</TH>
						<TD>${status}</TD>
					</TR>
 				</jsp:attribute> 
		</callSessionTag:session>  
	</TBODY>
</TABLE>
<br><br>
<font color="#000000">Stack:</font>
<br>${stack}
</body>
</HTML>
