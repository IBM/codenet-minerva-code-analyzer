<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page 
contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" 
errorPage="error.jsp"
%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<HTML>
<HEAD>
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<META name="GENERATOR" content="IBM Software Development Platform">

<TITLE>PriceChange Lite Results</TITLE>

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
<jsp:useBean id="oBean" scope="session" class="pricechangesession.PriceChangeOutput" type="pricechangesession.PriceChangeOutput" />
<!--Banner-->
<H1>Price Change Results</H1>

<BR><BR>
<c:if test="${oBean.item!=null}" >
<!-- Result Table -->
	<TABLE border="8" bgcolor="#cccccc" bordercolor="#FFCC99">
			<TBODY> 
		
					<TR>
						<TH>Item ID</TH>
						<TD><font color="#000000" id="itemId"><%= oBean.getItem().getItemId() %></font></TD>
					</TR>
					<TR>
						<TH>Item Price</TH>
						<TD><font color="#000000" id="price"><%= oBean.getItem().getItemPrice() %></font></TD>
					</TR>
					<TR> 
						<TH>Item Name</TH>
						<TD><font color="#000000" id="name"><%= oBean.getItem().getItemName() %></font></TD>
					</TR>
					<TR>
						<TH>Item Data</TH>
						<TD><font color="#000000" id="data"><%= oBean.getItem().getItemData() %></font></TD>
					</TR>	
					<TR> 
						<TH>Item Status</TH>
						<TD><font color="#000000" id="data"><%= oBean.getStatus() %></font></TD>
					</TR>	
					<TR>
						<TH>Item Stack</TH>
						<TD><font color="#000000" id="data"><%= oBean.getStack() %></font></TD>
					</TR>	
					<TR>
						<TH>Message</TH>
						<TD><font color="#000000" id="data"><%= oBean.getMessage() %></font></TD>
					</TR>					
			</TBODY>
	</TABLE >
</c:if>

<c:if test="${oBean.item==null}" >
<!-- Result Table -->
	<TABLE border="8" bgcolor="#cccccc" bordercolor="#FFCC99">
			<TBODY> 
					<TR> 
						<TH>Item Status</TH>
						<TD><font color="#000000" id="data"><%= oBean.getStatus() %></font></TD>
					</TR>	
					<TR>
						<TH>Item Message</TH>
						<TD><font color="#000000" id="data"><%= oBean.getMessage() %></font></TD>
					</TR>	
					<TR>
						<TH>Item Stack</TH>
						<TD><font color="#000000" id="data"><%= oBean.getStack() %></font></TD>
					</TR>	
			</TBODY>
	</TABLE >
</c:if>

</body>
</html>
