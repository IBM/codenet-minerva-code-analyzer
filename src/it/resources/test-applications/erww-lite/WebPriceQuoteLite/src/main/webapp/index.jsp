<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<HTML>
<HEAD>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<META name="GENERATOR" content="IBM Software Development Platform">

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri='http://java.sun.com/jstl/fmt' prefix='fmt' %>
<%@ taglib prefix="generateAutoGenInputTag" uri="http://WebPriceQuoteLite/GenerateAutoGenInputTag.tld"%>

<TITLE>Price Quote Input Form</TITLE>

<!--Styles-->
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

<!--Always generate the input data in case Autogen is submitted-->
<generateAutoGenInputTag:webAutoGen/>

<!--Java Script-->
<SCRIPT language="JavaScript" type="text/javascript">
<!--
	function submitForm(){
		document.myForm.submit()
	}
//-->
</SCRIPT>

<!--Banner-->
<H1>Price Quote Input Form</H1>

<!--Form-->
<FORM name="myForm" method="POST" action="PriceQuoteServlet">
<BR>
<INPUT TYPE=SUBMIT NAME="command" VALUE="Autogen"> 
<INPUT TYPE=SUBMIT NAME="command" VALUE="Manual">
<H4>PriceQuote uses Dynacache so please enable Dynacache in your configuration.  
Invalidation of the PriceQuote objects in cache is done by PriceChange.</H4>

<H4>The Dynamic Cache Monitor application can be used to verify that Dynacache is operating as expected.</H4>

<H3>* Indicates Required Fields</H3>


<!--Input Fields-->
<TABLE border="8" bgcolor="#cccccc" bordercolor="#FFCC99">
	<TBODY>
		<TR>
			<TH>itemId1 (e.g. 1)*</TH>
			<TD><INPUT name='itemId1' type='text' value=0></TD>
		</TR>
		<TR>
			<TH>quantity1 (e.g. 3)*</TH>
			<TD><INPUT name='quantity1' type='text' value=0></TD>
		</TR>
		<TR>
			<TH>itemId2</TH>
			<TD><INPUT name='itemId2' type='text' value=0></TD>
		</TR>
		<TR>
			<TH>quantity2</TH>
			<TD><INPUT name='quantity2' type='text' value=0></TD>
		</TR>
		<TR>
			<TH>itemId3</TH>
			<TD><INPUT name='itemId3' type='text' value=0></TD>
		</TR>
		<TR>
			<TH>quantity3</TH>
			<TD><INPUT name='quantity3' type='text' value=0></TD>
		</TR>
		<TR>
			<TH>itemId4</TH>
			<TD><INPUT name='itemId4' type='text' value=0></TD>
		</TR>
		<TR>
			<TH>quantity4</TH>
			<TD><INPUT name='quantity4' type='text' value=0></TD>
		</TR>
		<TR>
			<TH>itemId5</TH>
			<TD><INPUT name='itemId5' type='text' value=0></TD>
		</TR>
		<TR>
			<TH>quantity5</TH>
			<TD><INPUT name='quantity5' type='text' value=0></TD>
		</TR>
		<TR>
			<TH>itemId6</TH>
			<TD><INPUT name='itemId6' type='text' value=0></TD>
		</TR>
		<TR>
			<TH>quantity6</TH>
			<TD><INPUT name='quantity6' type='text' value=0></TD>
		</TR>
		<TR>
			<TH>itemId7</TH>
			<TD><INPUT name='itemId7' type='text' value=0></TD>
		</TR>
		<TR>
			<TH>quantity7</TH>
			<TD><INPUT name='quantity7' type='text' value=0></TD>
		</TR>
		<TR>
			<TH>itemId8</TH>
			<TD><INPUT name='itemId8' type='text' value=0></TD>
		</TR>
		<TR>
			<TH>quantity8</TH>
			<TD><INPUT name='quantity8' type='text' value=0></TD>
		</TR>
		<TR>
			<TH>itemId9</TH>
			<TD><INPUT name='itemId9' type='text' value=0></TD>
		</TR>
		<TR>
			<TH>quantity9</TH>
			<TD><INPUT name='quantity9' type='text' value=0></TD>
		</TR>
		<TR>
			<TH>itemId10</TH>
			<TD><INPUT name='itemId10' type='text' value=0></TD>
		</TR>
		<TR>
			<TH>quantity10</TH>
			<TD><INPUT name='quantity10' type='text' value=0></TD>
		</TR>
		<TR>
			<TH>itemId11</TH>
			<TD><INPUT name='itemId11' type='text' value=0></TD>
		</TR>
		<TR>
			<TH>quantity11</TH>
			<TD><INPUT name='quantity11' type='text' value=0></TD>
		</TR>
		<TR>
			<TH>itemId12</TH>
			<TD><INPUT name='itemId12' type='text' value=0></TD>
		</TR>
		<TR>
			<TH>quantity12</TH>
			<TD><INPUT name='quantity12' type='text' value=0></TD>
		</TR>
		<TR>
			<TH>itemId13</TH>
			<TD><INPUT name='itemId13' type='text' value=0></TD>
		</TR>
		<TR>
			<TH>quantity13</TH>
			<TD><INPUT name='quantity13' type='text' value=0></TD>
		</TR>
		<TR>
			<TH>itemId14</TH>
			<TD><INPUT name='itemId14' type='text' value=0></TD>
		</TR>
		<TR>
			<TH>quantity14</TH>
			<TD><INPUT name='quantity14' type='text' value=0></TD>
		</TR>
		<TR>
			<TH>itemId15</TH>
			<TD><INPUT name='itemId15' type='text' value=0></TD>
		</TR>
		<TR>
			<TH>quantity15</TH>
			<TD><INPUT name='quantity15' type='text' value=0></TD>
		</TR>
	</TBODY>
</TABLE>
</FORM>
</BODY>
</HTML>

