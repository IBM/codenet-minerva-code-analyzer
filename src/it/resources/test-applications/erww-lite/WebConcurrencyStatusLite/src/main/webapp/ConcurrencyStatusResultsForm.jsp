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



<TITLE>Concurrency Status Lite Results</TITLE>

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

<jsp:useBean id="outputBean" scope="session" class="orderstatus.concurrent.ejb.lite.ConcurrencyStatusOutput" type="orderstatus.concurrent.ejb.lite.ConcurrencyStatusOutput" />

<!--Banner-->
<H1>WebConcurrencyStatusLite Results</H1>

<BR><BR>


<!-- Result Table -->
	<TABLE border="8" bgcolor="#cccccc" bordercolor="#FFCC99" align="center">
			<TBODY> 
		
					<TR>
						<TH>Status:</TH>
						<TD><font color="#000000" id="Status"><%= outputBean.getStatusMsg() %></font></TD>
					</TR>
					<TR> 
						<TH>Message:</TH>
						<TD><font color="#000000" id="Message"><%= outputBean.getMessage() %></font></TD>
					</TR>
					<TR> 
						<TH>Running Time:</TH>
						<TD><font color="#000000" id="Time"><%= outputBean.getTime() %></font></TD>
					</TR>
					<TR><TH>********************************</TH>
					    <TD>***********************************</TD>
					</TR>	
					<TR>
						<TH>Number of concurrent clients:</TH>
						<TD><font color="#000000" id="NumberOfClients"><%= outputBean.getNumberOfClients() %></font></TD>
					</TR>
					<TR>
						<TH>Attempted Transactions:</TH>
						<TD><font color="#000000" id="AttemptedTrans"><%= outputBean.getAttemptedTrans() %></font></TD>
					</TR>
					<TR>
						<TH>Passed Transactions:</TH>
						<TD><font color="#000000" id="PassingTrans"><%= outputBean.getPassedTrans() %></font></TD>
					</TR>
					<TR>
						<TH>Cancelled Transactions:</TH>
						<TD><font color="#000000" id="CancelledTrans"><%= outputBean.getCancelledTrans() %></font></TD>
					</TR>
					<TR>
						<TH>Failed Transactions (unexpected):</TH>
						<TD><font color="#000000" id="UnexpectedFailedTrans"><%= outputBean.getUnexpectedFailedTrans() %></font></TD>
					</TR>
					<TR>
						<TH>Failed Transactions (expected):</TH>
						<TD><font color="#000000" id="ExpectedFailedTrans"><%= outputBean.getExpectedFailedTrans() %></font></TD>
					</TR>
					<TR> 
						<TH>Exception:</TH>
						<TD><font color="#000000" id="Exception"><%= outputBean.getException() %></font></TD>
					</TR>
					<TR>
						<TH>Stack:</TH>
						<TD><font color="#000000" id="Stack"><%= outputBean.getStack() %></font></TD>
					</TR>				
			</TBODY>
	</TABLE >


<br><br>
</BODY>
</HTML>
