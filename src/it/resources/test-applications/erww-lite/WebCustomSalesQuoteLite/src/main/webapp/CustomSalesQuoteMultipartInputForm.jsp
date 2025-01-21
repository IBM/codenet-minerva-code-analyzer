<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"><%@page
	language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<html>
<head>
<TITLE>Custom Sales Quote Input Form</TITLE>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta name="GENERATOR" content="Rational® Application Developer for WebSphere® Software">
<STYLE type="text/css">
<!--
BODY
{
background-color: #f8f7cd;
}
-->
</STYLE>
</head>
<body>
<img border="0" src="images/ERWW_Logo.jpg" width="200" height="50">
<h1>Custom Sales Quote Input Form</h1>

<h2>Using multipart/form-data annotation in the servlet</h2>
<form action="/WebCustomSalesQuoteLite/CustomSalesQuoteServlet" enctype="multipart/form-data" method="POST" >
<div align="left"></div>

<p align="left">Please enter a description of the custom product that you would like the ERWW Company to manufacture:</p>
<div align="left"><textarea NAME="productDescription" ROWS="10"
	COLS="78"></textarea></div>
<p align="left">Please attach a diagram of the custom product</p>  
<p align="left"><input TYPE="file" size="55" NAME="productGraphic" ><br></p>

<p align="left">Please attach a picture of the parts: </p>
<p align="left"><input TYPE="file" size="55" NAME="productPartsGraphic" ><br></p>
<div align="left"><input TYPE="SUBMIT" name="SubmitButton"
	value="Submit"></div>
<hr align="left">
<h4 align="left"><span
	style="font-style: normal; text-transform: none; text-decoration: none; font-variant: normal; font-weight: normal"><span
	style="letter-spacing: normal; line-height: normal"><span><span><font
	face="Times New Roman" color="black"><b>Instructions: </b>Please enter
a description and attach one of the following sets of images (small,
medium or large):</font></span></span></span></span></h4>
<div align="left">
<blockquote>
<h4><span
	style="font-style: normal; text-transform: none; text-decoration: none; font-variant: normal; font-weight: normal"><span
	style="letter-spacing: normal; line-height: normal"><span><span><font
	face="Times New Roman"><b>Description for Small Images: </b>Solar Panels</font></span></span></span></span></h4>
</blockquote>
<blockquote>
<blockquote>
<p><span
	style="font-style: normal; text-transform: none; text-decoration: none; font-variant: normal; font-weight: normal"><span
	style="letter-spacing: normal; line-height: normal"><span><span><font
	face="Times New Roman">Small_Image1.jpg</font></span></span></span></span></p>
</blockquote>
</blockquote>
<blockquote>
<blockquote>
<p><span
	style="font-style: normal; text-transform: none; text-decoration: none; font-variant: normal; font-weight: normal"><span
	style="letter-spacing: normal; line-height: normal"><span><span><font
	face="Times New Roman">Small_Image2.jpg</font></span></span></span></span></p>
</blockquote>
</blockquote>
<blockquote>
<h4><span
	style="font-style: normal; text-transform: none; text-decoration: none; font-variant: normal; font-weight: normal"><span
	style="letter-spacing: normal; line-height: normal"><span><span><font
	face="Times New Roman"><b>Description for Medium Images:</b> Credenza</font></span></span></span></span></h4>
</blockquote>
<blockquote>
<blockquote>
<p><span
	style="font-style: normal; text-transform: none; text-decoration: none; font-variant: normal; font-weight: normal"><span
	style="letter-spacing: normal; line-height: normal"><span><span><font
	face="Times New Roman">Medium_Image1.jpg</font></span></span></span></span></p>
</blockquote>
</blockquote>
<blockquote>
<blockquote>
<p><span
	style="font-style: normal; text-transform: none; text-decoration: none; font-variant: normal; font-weight: normal"><span
	style="letter-spacing: normal; line-height: normal"><span><span><font
	face="Times New Roman">Medium_Image2.jpg</font></span></span></span></span></p>
</blockquote>
</blockquote>
<blockquote>
<h4><span
	style="font-style: normal; text-transform: none; text-decoration: none; font-variant: normal; font-weight: normal"><span
	style="letter-spacing: normal; line-height: normal"><span><span><font
	face="Times New Roman"><b>Description for Large Images</b>: Bicycle</font></span></span></span></span></h4>
</blockquote>
<blockquote>
<blockquote>
<p><span
	style="font-style: normal; text-transform: none; text-decoration: none; font-variant: normal; font-weight: normal"><span
	style="letter-spacing: normal; line-height: normal"><span><span><font
	face="Times New Roman">Large_Image1.jpg</font></span></span></span></span></p>
</blockquote>
</blockquote>
<blockquote>
<blockquote>
<p><font face="Times New Roman"><span style="text-align: left"><span
	style="margin: auto"><span
	style="letter-spacing: normal; line-height: normal"><span
	style="font-style: normal; text-transform: none; text-decoration: none; font-variant: normal; font-weight: normal">Large_Image2.jpg</span></span></span></span></font></p>
</blockquote>
</blockquote>
</div>
<hr align="left">
</form>
</body>
</html>