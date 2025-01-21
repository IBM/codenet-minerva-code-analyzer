<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"><%@page
	language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<html>
<head>
<title>Delivery JMS Lite Form Login</title>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
</head>
<STYLE type="text/css">
BODY
{
background-color: #f8f7cd;
}
H2 {
	text-align: center !IMPORTANT;
}

TH {
	text-align: left !IMPORTANT;
	vertical-align: top !IMPORTANT;
}

TD {
	text-align: left !IMPORTANT;
	vertical-align: top !IMPORTANT;
}

TH.result {
	background-color: #999999 !IMPORTANT;
}

TD.result {
	background-color: #cccccc;
	vertical-align: top !IMPORTANT;
}
</STYLE>
<body>
	<h2>Delivery JMS (1.1) Lite Form Login</h2>
	<form METHOD="POST" ACTION="j_security_check">
		<p>
			<font size="2"> <strong>Enter user ID and password: </strong>
			</font> <BR> <strong> User ID</strong> <input type="text" size="20"
				name="j_username"> <strong> Password </strong> <input
				type="password" size="20" name="j_password"> <BR> <BR>
			<font size="2"> <strong> And then click this button:
			</strong>
			</font> <input type="submit" name="login" value="Login">
		</p>
	</form>
</body>
</html>