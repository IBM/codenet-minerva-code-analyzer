<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"><%@page
	language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<html>
<head>
<TITLE>Download Image from the IMAGE Table</TITLE>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta name="GENERATOR" content="RationalÆ Application Developer for WebSphereÆ Software">
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
<h1>Download Image from the IMAGE Table</h1>
<h2>Using multipart/form-data annotation in the servlet</h2>

<form action="/WebCustomSalesQuoteLite/ImageFileDownloadServlet" enctype="multipart/form-data" method="POST" >

<p>Please enter the Image Id of the Image file that you want to download:</p>
<TEXTAREA NAME="imageId" ROWS=1 COLS=15></TEXTAREA>
<p></p>

<input TYPE="SUBMIT" name="SubmitButton" value="Submit">
<hr>
<h4><font face="Times New Roman" color="black">Instructions: <b><span
	style="font-style: normal; text-transform: none; font-variant: normal; font-weight: normal">Please
enter one of the following Image Ids:</span></b></font></h4>

<blockquote>
<p><font face="Times New Roman" color="black">Small_Image1</font></p>
<p><font face="Times New Roman" color="black">Small_Image2</font></p>
<p><font face="Times New Roman" color="black">Medium_Image1</font></p>
<p><font face="Times New Roman" color="black">Medium_Image2</font></p>
<p><font face="Times New Roman" color="black">Large_Image1</font></p>
<p><font face="Times New Roman" color="black">Large_Image2</font></p>
</blockquote>
<hr>
<h4><font face="Times New Roman" color="black">Note: <b><span
	style="font-style: normal; text-transform: none; font-variant: normal; font-weight: normal">The java heap size may need to be increased in order to download the medium and large images.</span></b></font></h4>
</form>
</body>
</html>