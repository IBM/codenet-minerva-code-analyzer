<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"><%@page
	language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.lang.String" %>
<html>
<head>
<title>Delivery Lite Error</title>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
</head>
<style type="text/css">
<!--
BODY
{
background-color: #f8f7cd;
}
-->
</style>
<body>
<% String isUserInRoleResults = (String)request.getAttribute("isUserInRoleResults"); %>
<h1>Delivery Lite Error</h1>
<h2>Authorization failure occurred</h2>
<table border="8" bgcolor="#cccccc" bordercolor="#FFCC99">
   <tr><td><%= "Error: " + isUserInRoleResults %></td></tr>
</table>
</body>
</html>

