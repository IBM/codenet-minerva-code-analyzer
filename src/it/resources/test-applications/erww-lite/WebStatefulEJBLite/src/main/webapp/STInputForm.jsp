<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<HTML>
<HEAD>

<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<META name="GENERATOR" content="IBM Software Development Platform">
<META http-equiv="Content-Style-Type" content="text/css">
<!-- <LINK href="theme/Master.css" rel="stylesheet" type="text/css"> -->

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>


<TITLE>Stateful Session Bean Test Input Form</TITLE>

<!--Styles-->
<!--Styles-->
<STYLE type="text/css">
<!--
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
</STYLE>


</HEAD>

<body>
<!--Java Script-->
<SCRIPT LANGUAGE="JavaScript" TYPE="text/javascript">
<!--
	function submitForm(){
		document.myForm.submit()
	}
//-->
</SCRIPT>

<!--Banner-->
<H1>Stateful Session Bean Test</H1>

<!--Form-->

<FORM name="myForm" method="POST" action="STController">
<INPUT TYPE="SUBMIT" NAME="command" VALUE="Submit">
<INPUT TYPE="SUBMIT" NAME="command" VALUE="SessionTest">
<A href="javascript:submitForm()"></A>
 
<BR>
<BR>

<!--Input Fields-->
<TABLE border="0">
	<TBODY>
		<TR>
			<TH>Number of times to call</TH>
			<TD><INPUT NAME='loopnum' TYPE='text' VALUE='1'></TD>
		</TR>
		<TR>
			<TH>Sleep time between calls</TH>
			<TD><INPUT NAME='duration' TYPE='text' VALUE='10'></TD>
		</TR>
	</TBODY>
</TABLE>
</FORM>
</body>
</HTML>
