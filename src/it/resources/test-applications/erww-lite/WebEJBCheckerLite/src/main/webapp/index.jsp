<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>EJB checker and related tools</title>

<!--Styles-->
<STYLE type="text/css">
<!--
BODY
{
background-color: #f8f7cd;
}
H1 {
	text-align: center !IMPORTANT;
}
H2 {
	color: #993333 !important;
	text-align: left !important;
}
TH {
	text-align: center !IMPORTANT;
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
-->
</STYLE>



</head>
<body>

<!--Banner-->
<H1>EJB Checker / Tools</H1>

  <TABLE border="8" bgcolor="#CCCCCC" bordercolor="#FFCC99">
    <TBODY>
 	   <TR>
		 <TH class="result" align="center">
			<FORM METHOD=POST ACTION="Validator">
			  <INPUT TYPE="SUBMIT" NAME="command" VALUE="Asynchronous Methods Validation"/>
			</FORM>
		  </TH>
       </TR>            
       <TR>
          <TH class="result" align="center">
          	<FORM METHOD=POST ACTION="Validator">
	 	     <input TYPE="SUBMIT" NAME="command" VALUE="Non-persistent Timers Validation"/>    
		    </form>    
		  </TH>
	   </TR>    	 		      
	   <TR>
         <TH class="result" align="center">
          	<FORM METHOD=POST ACTION="Validator">
		      <INPUT TYPE="SUBMIT" NAME="command" VALUE="Persistent Timers Validation"/>
		     </FORM>
		   </TH>   
        </TR>
        <TR>
          <TH class="result" align="center">
          	<FORM METHOD=POST ACTION="Validator">
		      <INPUT TYPE="SUBMIT" NAME="command" VALUE="Cancel All Timers"/>
		    </FORM>
		   </TH>   
        </TR>
     </TBODY>
  </TABLE>  

</body>
</html>