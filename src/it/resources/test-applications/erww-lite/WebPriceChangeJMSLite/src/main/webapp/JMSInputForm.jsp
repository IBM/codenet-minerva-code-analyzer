<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<HTML>
<HEAD>
<%@page language="java" contentType="text/html; charset=ISO-8859-1"	pageEncoding="ISO-8859-1"%>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Price Change JMS Lite Input Form</title>
</HEAD> 

<%-- Styles --%>
<STYLE TYPE="text/css">
BODY
{
background-color: #f8f7cd;
}
H1
{
text-align: center;
}
TH
{
text-align:left;
color: #993333;
}
</STYLE>

<BODY>
  
<%-- Java Script --%>
<SCRIPT LANGUAGE="JavaScript" TYPE="text/javascript">
</SCRIPT>
   
<%-- Banner --%>
<H1>Price Change JMS (1.1) Input Form</H1>
   
<%-- Form --%>
<FORM name="myForm" method="POST" action="/WebPriceChangeJMSLite/JMSController">
   
<BR><BR>
<P>Please complete the form and select either the "AutoGeneration" or "Manual" button below to execute the request.</P>


<H4>Select Message Size:</H4>
<P>      
     (Small: approx 15 bytes, Large: X number of MegaBytes based on what you specify):
<BR> 
	<SELECT NAME="msgSize" ID="msgSize">
    <OPTION value="S" selected>Small
		</OPTION>
    <OPTION value="L">Large
 	</OPTION>
</SELECT>
</P>

<%--Input Fields --%>
<TABLE border="8" bgcolor="#cccccc" bordercolor="#FFCC99">
	<TBODY>
		<TR>
			<TH>Size of Large Message in MegaBytes</TH>
			<TD><INPUT name='largeMsgSize' type='text' value=7></TD>
		</TR>		
	</TBODY>
</TABLE>

<BR><BR>
<H4>Supply Manual Input Data:</H4>
<H5>(Ignored for auto generation)</H5>

<%-- Input Fields (Manual Input) --%>
<TABLE border="8" bgcolor="#cccccc" bordercolor="#FFCC99">
	<TBODY>
		<TR>
			<TH>itemId (e.g. 1)</TH>
			<TD><INPUT name='itemId' type='text' value=0></TD>
		</TR>
		<TR>
			<TH>price (e.g. 22.25)</TH>
			<TD><INPUT name='price' type='text' value=0></TD>
		</TR>		
	</TBODY>
</TABLE> 
  
<BR><BR>
<INPUT TYPE="SUBMIT" NAME="command" VALUE="AutoGeneration">
<INPUT TYPE="SUBMIT" NAME="command" VALUE="Manual">
<INPUT TYPE="reset" NAME="Reset" ID="Reset" VALUE="ResetForm">

</FORM>
</BODY>
</HTML>
