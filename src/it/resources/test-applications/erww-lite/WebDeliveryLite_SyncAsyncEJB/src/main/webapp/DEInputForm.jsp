<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<HTML>
<HEAD>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<META name="GENERATOR" content="IBM Software Development Platform">

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

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
<H1>Delivery Asynchronous/Synchronous EJB Input Form</H1>

<!--Form-->
<FORM name="myForm" method="POST" action="DEController">
<INPUT TYPE="SUBMIT" NAME="command" VALUE="AutoGeneration">
<INPUT TYPE="SUBMIT" NAME="command" VALUE="Manual">

<BR><BR>


<BR><BR>
<H3>Select Local or Remote EJB calls:</H3>

<SELECT name="distributed">
    <OPTION value="false">Local</OPTION>
    <OPTION value="true" selected>Remote</OPTION>
</SELECT> 

<!-- shupert - no longer used.  Asynch methods Future.get timeout is now an ERWW system property -->
<INPUT type="hidden" name="asyncTimeout" type='text' value=5 size=5>


<BR>
<H3>Select EJB Method Invocation Mode</H3>
<SELECT name="useAsync">
    <OPTION value="true" selected>Asynchronous invocation</OPTION>
    <OPTION value="false">Synchronous invocation</OPTION>
</SELECT> 
<BR> 
<BR>
<BR>

<H3>* Indicates Required Fields (for manual input only)</H3>
<!--Input Fields-->
<TABLE border="8" bgcolor="#cccccc" bordercolor="#FFCC99">
	<TBODY>
		<TR>
			<TH>inWarehouseId (e.g. 1)*</TH>
			<TD><INPUT name='warehouseId' type='text' value=0></TD>
		</TR>
		<TR>
			<TH>carrierId (e.g.1)*</TH>
			<TD><INPUT name='carrierId' type='text' value=0></TD>
		</TR>
	</TBODY>
</TABLE>

<!--Hidden Fields--></FORM>
</BODY>
</HTML>
