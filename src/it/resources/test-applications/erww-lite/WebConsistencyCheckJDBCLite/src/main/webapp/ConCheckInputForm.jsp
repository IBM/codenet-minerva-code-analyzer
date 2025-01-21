<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page
	language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"
	import="consistency.check.jdbc.lite.ConCheckServlet"%>
<html>
<head>
<title>ConCheckInputForm</title>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
</head>
<body bgcolor="#F8F7CD">
<form name="myForm" method="POST" action="ConCheckServlet">
<h2 align="center"><font color="navy">ERWW Consistency Checks using JDBC</font></h2>

<p><input type="submit" name="Submit" value="Submit" style="font-style: normal; border-color: #80ffff; font-weight: bold; border-width: thin; border-style: outset"><br></p>
<hr
	style="border-color: gray; border-style: outset; border-width: thick">



<hr style="border-color: gray; border-style: outset; border-width: thick">
<h4><font face="Arial" color="navy">Minimum Warehouse Id:</font>
	<input type="text" name="minWarehouseId" size="5" value="1"> 
	<font face="Arial" color="#ffffaa"><i>-_____</i></font>
	<font face="Arial" color="navy">Minimum District Id:</font>
	<input type="text" name="minDistrictId" size="5" value="1"></h4>
<h4><font face="Arial" color="navy">Maximum Warehouse Id:</font>
	<input type="text" name="maxWarehouseId" size="5" value="25">
	<font face="Arial" color="#ffffaa"><i>_____</i></font>
	<font face="Arial" color="navy">Maximum District Id:</font>
	<input type="text" name="maxDistrictId" size="5" value="10"></h4>
<hr style="border-color: gray; border-style: outset; border-width: thick">
<p align="center"><label for="all">
	<input type="radio" name="consistencyCheckGroup" id="all" value="0" checked>
	<font face="Arial" color="#804000" size="4">All Consistency Checks</font></label></p>
<p align="center">
    <label for="c1"><input type="radio" name="consistencyCheckGroup" id="c1" value="1">
	<font face="Arial" color="#804000" size="4">Consistency Check 1</font></label>
	<font face="Arial" color="#804000"> 
	<label for="c2"><input type="radio" name="consistencyCheckGroup" id="c2" value="2">
	<font face="Arial" size="4">Consistency Check 2</font></label> 
	<label for="c3"><input type="radio" name="consistencyCheckGroup" id="c3" value="3">
	<font face="Arial" size="4">Consistency Check 3</font></label></font></p>
<p align="center"><font color="#804000">
    <label for="c4"><input type="radio" name="consistencyCheckGroup" id="c4" value="4">
	<font face="Arial" size="4">Consistency Check 4</font></label>
	<label for="c8"><input type="radio" name="consistencyCheckGroup" id="c8" value="8">
	<font face="Arial" size="4">Consistency Check 8</font></label>
	<label for="c9"><input type="radio" name="consistencyCheckGroup" id="c9" value="9">
	<font face="Arial" size="4">Consistency Check 9</font></label></font></p>
<p align="center"><font color="#804000">
	<label for="c13"><input type="radio" name="consistencyCheckGroup" id="c13" value="13">
	<font face="Arial" size="4">Consistency Check 13</font></label></font></p> 
<hr	style="border-color: gray; border-style: outset; border-width: thick">
</form>
</body>
</html>