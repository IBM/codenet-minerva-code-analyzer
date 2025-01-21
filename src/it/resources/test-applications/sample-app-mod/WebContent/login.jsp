<%--
  Created by IntelliJ IDEA.
  User: kieran
  Date: 13/04/2023
  Time: 16:42
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" session="false"%>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MOD RESORTS Login</title>
    <link href="https://fonts.googleapis.com/css?family=Poppins:300,400,500,600" rel="stylesheet">
</head>
<body style="background-color: black;">

    <div style="text-align: center;">

        <img  style="margin-top: 100px;" src="images/m-logo.svg" alt="">
        
        <div style="color: white; padding: 64px;">
            LOG IN TO MODRESORTS
        </div>

        <!-- When app security is turned on, use j_security_check as th form action -->
        <!-- <form action="j_security_check"> -->
        <form action="/resorts">    
            <table style="margin: auto;">

                <tr>
                    <td><span style="color: white; padding: 64px;"> Username: </span></td>
                    <td><input style="width: 250px" type="text" name="j_username"></td>
                </tr>

                <tr>
                    <td><span style="color: white; padding: 64px;"> Password: </span></td>
                    <td><input style="width: 250px" type="password" name="j_password" autocomplete="off"></td>
                </tr>

                <tr>
                    <td colspan="2" style="padding: 50px; text-align: center"><input type="submit" value="Log In"/></td>
                </tr>
            </table>

        </form>

    </div>

</body>
</html>
