<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<HTML>
<HEAD>
<META http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<META name="GENERATOR" content="IBM Software Development Platform">
<TITLE>ERWW LogOff Input Form</TITLE>

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
<H1>ERWW LogOff Input Form</H1>

<!--Form-->

<FORM METHOD=POST ACTION="ibm_security_logout" NAME="logout">
<input type="submit" name="logout" value="Logout">
<input type="HIDDEN" name="logoutExitPage" value="LogOffResultsForm.jsp">
<H3> Select Logout to end the secure HTTP Session and remove the LPTA token </H3>
</Form>



<!--Hidden Fields-->

</BODY>
</HTML>
