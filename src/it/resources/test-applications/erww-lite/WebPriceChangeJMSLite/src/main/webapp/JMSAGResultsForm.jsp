<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="error.jsp"%>
<HTML>
<HEAD>
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<META name="GENERATOR" content="IBM Software Development Platform">
<META http-equiv="Content-Style-Type" content="text/css">
<%@ taglib prefix="callAutoGenTag" uri="http://WebPriceChangeJMSLite/CallAutoGenTag.tld"%>
<%@ taglib prefix="priceChangeOutputTag" uri="http://WebPriceChangeJMSLite/PriceChangeOutputTag.tld"%>

<TITLE>Price Change JMS Lite - Auto Generation Version</title>
  
<%-- Styles --%>

<STYLE type="text/css">
BODY
{
background-color: #f8f7cd;
}
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
</STYLE>

</HEAD>

<BODY>

<%-- set properties from input data --%>
<jsp:useBean id="JSBean" scope="request" class="pricechange.jms.lite.PriceChangeJMSClient" type="pricechange.jms.lite.PriceChangeJMSClient" />
<jsp:setProperty name="JSBean" property="msgSize" value="${param.msgSize}" />
<jsp:setProperty name="JSBean" property="largeMsgSize" value="${param.largeMsgSize}" />

<%-- call the auto generation tag class to process input --%>
<callAutoGenTag:webAutoGenInput object="${JSBean}"/>

<%-- Banner --%>
<H1>Price Change JMS (1.1) Results</H1>
<H3 ALIGN="CENTER">(Auto Generation Version)</H3>

<BR><BR>

<%-- Results Table --%>
<priceChangeOutputTag:getResults object="${JSBean}">
	<jsp:attribute name="jspFragment1"> 
		<TABLE border="8" bgcolor="#cccccc" bordercolor="#FFCC99">
			<TBODY> 
				<tr>
				    <TH>Output:</TH>
       					<td>${priceChangeResult}</td>
				</tr>			
				<tr>
				    <TH>Status:</TH>
   					<td>${status}</td>
				</tr>
				<tr>
				    <TH>Exception:</TH>
       				<td>${exception}</td>
    			</tr>     			  					
			</TBODY> 
		</TABLE>
 	</jsp:attribute> 
</priceChangeOutputTag:getResults> 

</BODY>
</HTML>
