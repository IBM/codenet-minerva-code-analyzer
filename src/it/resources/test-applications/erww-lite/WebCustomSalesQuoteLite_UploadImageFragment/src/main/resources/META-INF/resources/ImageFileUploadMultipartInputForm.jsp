<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"><%@page
	language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<html>
<head>
<TITLE>Upload Image to the IMAGE Table</TITLE>
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
<h1>Upload Image to the IMAGE Table</h1>
<h2>Using multipart/form-data annotation in the servlet</h2>

<form action="/WebCustomSalesQuoteLite/ImageFileUploadServlet" enctype="multipart/form-data" method="POST" >

<p>Please enter the Image Id:</p>
<TEXTAREA NAME="imageId" ROWS=1 COLS=15></TEXTAREA>
<p></p>
<p>Please attach an image file.</p>
<p><input TYPE="file" size="55" NAME="image" ><br></p>

<input TYPE="SUBMIT" name="SubmitButton" value="Submit">

</form>
</body>
</html>