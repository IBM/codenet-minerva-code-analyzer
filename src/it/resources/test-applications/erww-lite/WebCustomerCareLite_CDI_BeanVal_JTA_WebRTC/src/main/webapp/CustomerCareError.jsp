<!DOCTYPE HTML>
<%@ page isErrorPage="true"%>
<%@ page import="irwwbase.UserException" %>
<%@ page import="java.io.PrintWriter" %>

<html>
<head>
<link rel="stylesheet" href="theme/Master.css" type="text/css">
<title>ERWW-Lite CustomerCareErrorPage</title>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<style>
	body {background-color: #f8f7cd}
	
	H1 {
		color: black;
		font-family: 'times new roman';
		text-transform: capitalize
	}

	H2 {
		color: red;
		font-family: 'times new roman';
		text-transform: capitalize
	}

	H3 {
		color: black;
		font-family: 'times new roman';
		text-transform: none
	}
</style>
</head>
<body bgcolor="#F8F7CD">
<h1 align="center">ERWW-Lite Customer Care Error Page</h1>

<h2 align="left">Customer Care Status: Failed</h2>

<table border="1">
	<tbody>
		<tr bgcolor="#E6E6E6" align="left"> 
			<td><b>Exception Message </b></td>
		</tr>
		<tr bgcolor="#E6E6E6" align="left"> 
			<td> 
			<% if (exception !=null){
				out.println(exception.getMessage());
   			}%>	
   			</td>
   		</tr>
	
		<tr bgcolor="#E6E6E6" align="left"> 
			<td><b>Exception Cause </b></td>
		</tr>
		<tr bgcolor="#E6E6E6" align="left"> 
			<td> 
			<% if (exception !=null){
				out.println(exception.getCause());
   			}%>	
   			</td>
   		</tr>
		<tr bgcolor="#E6E6E6" align="left"> 
			<td><b>Stack Trace </b></td>
		</tr>
		<tr bgcolor="#E6E6E6" align="left"> 
			<td> 
			<% if (exception !=null){
     			 if (exception instanceof irwwbase.UserException) {
					UserException userException = (UserException)exception;
	    			userException.printStackTrace(); 
					userException.printStackTrace(new PrintWriter(out)); 
	   			}
     			else { 
       				exception.printStackTrace();
	   				exception.printStackTrace(new PrintWriter(out)); 
	  			} 	 
   			 }%>
   		 	</td>
   		</tr>
   	</tbody>
</table>

</body>
</html>