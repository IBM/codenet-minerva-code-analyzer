<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<HTML>
<HEAD>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<META name="GENERATOR" content="IBM Software Development Platform">


<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<TITLE>Delivery Input Form</TITLE>

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

<BODY>
<!--Java Script-->
<SCRIPT language="JavaScript" type="text/javascript">
<!--
	function submitForm(){
		document.myForm.submit()
	}
//-->
</SCRIPT>

<!--Banner-->
<H1>Delivery (Bad Application) Input Form</H1>
<BR>
This test skips retrieval of asynchronous delivery method results to purposely orphan server-side Future objects
<BR>

<!--Form-->
<FORM name="myForm" method="POST" action="DEController">
<INPUT TYPE="SUBMIT" NAME="command" VALUE="AutoGeneration">
<INPUT TYPE="SUBMIT" NAME="command" VALUE="Manual">

<BR><BR><BR>


<BR><BR><BR>
<H3>Select Distributed(Remote) or Local EJB calls:</H3>

<SELECT name="distributed">
    <OPTION value="false">Local</OPTION>
    <OPTION value="true" selected>Distributed</OPTION>
</SELECT> 

<!-- shupert - no longer used.  Asynch methods Future.get timeout is now an ERWW system property -->
<INPUT type="hidden" name="asyncTimeout" type='text' value=5 size=5>


<!-- shupert - Invocation mode is hidden for this Bad Application test.  We always want the 
               asynchronous delivery to be used
-->
<INPUT type="hidden" name="useAsync" value="true">

<!-- shupert - New hidden attribute for this Bad Application test.  We will skip the call 
               to Future.get() to retrieve the asynchronous delivery results. This will cause
               the server-side Future objects to be leaked (on purpose).
-->
<INPUT type="hidden" name="leakDeliveryResults" value="true">


<BR>
<H3> <fmt:message key="NOInput.RequiredFields"/> </H3>
<!--Input Fields-->
<TABLE border="8" bgcolor="#cccccc" bordercolor="#FFCC99">
	<TBODY>
		<TR>
			<TH>inWarehouseId (e.g. 1)*</TH>
			<TD><INPUT name='warehouseId' type='text' value=0></TD>
		</TR>
		<TR>
			<TH>carrierId (e.g.1)*></TH>
			<TD><INPUT name='carrierId' type='text' value=0></TD>
		</TR>
	</TBODY>
</TABLE>

<!--Hidden Fields--></FORM>
</BODY>
</HTML>
