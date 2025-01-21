<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page 
contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" 
errorPage="error.jsp"
%>
<HTML>
<HEAD>
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<META name="GENERATOR" content="IBM Software Development Platform">

<%@ taglib prefix="callManualTag" uri="http://WebDeliveryLite_SyncAsyncEJB/CallManualTag.tld"%>
<%@ taglib prefix="outputDetailTag" uri="http://WebDeliveryLite_SyncAsyncEJB/DeliveryOutputDetailTag.tld"%>
<%@ taglib uri='http://java.sun.com/jstl/fmt' prefix='fmt' %>

<TITLE>Delivery Results - Manual Version</TITLE>

<!--Styles-->
<STYLE type="text/css">
<!--
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
-->
</STYLE>


</HEAD>
<BODY>

<jsp:useBean id="dEBean" scope="request" class="deliverysession.ejb3.Client" type="deliverysession.ejb3.Client" />

<jsp:setProperty name="dEBean" property="distributed" value="${param.distributed}" />

<jsp:setProperty name="dEBean" property="asyncTimeout" value="${param.asyncTimeout}" />

<jsp:setProperty name="dEBean" property="useAsync" value="${param.useAsync}" />

<jsp:setProperty name="dEBean" property="leakDeliveryResults" value="${param.leakDeliveryResults}" />


<jsp:setProperty name="dEBean" property="warehouseId" value="${param.warehouseId}" />
<jsp:setProperty name="dEBean" property="carrierId" value="${param.carrierId}" />

<callManualTag:webManual object="${dEBean}"/>

<!--Banner-->
<H1>Delivery Results - Manual Version</H1>

<BR>
<BR>

<!-- Result Table -->
<TABLE border="8" bgcolor="#cccccc" bordercolor="#FFCC99">
	<TBODY>
		<outputDetailTag:detail object="${dEBean}">
 				<jsp:attribute name="jspFragment1"> 
      				<tr bgcolor="#cccccc" align="right"> 
        				<td ><font color="#000000">${deliveryRows}</font></td>
    				</tr>    				
 				</jsp:attribute> 
 				<jsp:attribute name="jspFragment2"> 
 				<tr bgcolor="#cccccc" align="right"> 
         				<td ><font color="#000000">Status: ${status}</font></td>
    				</tr>
  				</jsp:attribute>
		</outputDetailTag:detail> 
	</TBODY>
</TABLE>
<br><br>
<font color="#000000">Exception Stack:</font><br>${stack}
</BODY>
</HTML>
