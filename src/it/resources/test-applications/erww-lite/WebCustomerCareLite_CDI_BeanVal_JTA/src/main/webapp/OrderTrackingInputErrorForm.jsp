<!DOCTYPE HTML>
<%@page import="java.util.ArrayList" %>
<%@page import="java.util.List" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>OrderTrackingInputErrorForm</title>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1"/>
<link rel="stylesheet" href="theme/Master.css"/>  
<style>
	body {background-color: #f8f7cd}
		
	H1 {
		color: black;
		font-family: 'times new roman';
		text-transform: capitalize
	}
	H2 {
		color: black;
		font-family: 'times new roman';
		text-transform: capitalize
	}
	H3 {
		color: red;
		font-family: 'times new roman';
		text-transform: none
	}
</style>
</head>
<body>
	<h2></h2>
	<br/>
    <nav style="background-color: #FFFFEB; border-style:solid; border-color: lightgrey;">
    	<a href="#top">
	    	<img src="images/ERWW_Logo_Border_Yellow.jpg" width="200" height="79" align="left" border="none"/>
		</a>
		<h1 style="font-size:180%; font-family:'times new roman'; align:center" >Order Tracking Error Input Form</h1>
		<a href="#top">
	    	<img src="images/Background_Yellow.jpg" width="260" height="30" align="left" border="none"/>
		</a>
		<h1 style="font-size:120%; font-family:'times new roman'; align:center" >CDI 1.2, Bean Validation 1.1, JTA 1.2</h1>
		<h3>Please re-enter the correct values for the input variables.</h3>	
	</nav>
	<%
	//Added this to remove the Type safety: Unchecked cast from Object to List<String> warning
	Object var = request.getSession().getAttribute("violationMessages");
	List<String> violationMessages = new ArrayList<String>();	
	if (var instanceof List){
		for (int i=0; i<((List<?>)var).size();i++) {
			Object item = ((List<?>)var).get(i);
			if (item instanceof String) {
				violationMessages.add((String)item);
			}
		}
	}
	//Commenting this out to remove the Type safety: Unchecked cast from Object to List<String> warning
	//List<String> violationMessages = (List<String>)request.getSession().getAttribute("violationMessages");
    %>
		<form id="orderTracking" name="orderTrackingForm" method="post" action="OrderTrackingServlet">
			<input type="submit" name="inputType" value="AutoGen"/> 
			<input type="submit" name="inputType" value="Manual"/> 
			<br/>
			<h2>Invocation Path</h2>
     		<input type="radio" name="inputPath" value="Servlet Path" checked="checked"/><label>Servlet Path</label>
     		<input type="radio" name="inputPath" value="EJB Path"/><label>EJB Path</label>
     		<br/>
     		<br/>
			<table style="background-color:#CCCCCC; border-color:#FFCC99;"> 
  				<tbody>  
  					<tr>
						<td>warehouseId (e.g. 1)</td>
						<td><input name='warehouseId' type='text' value='0' /></td>
					</tr>
					<tr>
						<td>districtId (e.g. 1)</td>
						<td><input name='districtId' type='text' value='0' /></td>
					</tr>
					<tr>
						<td>customerId (e.g. 1)(When customerLastName is supplied, customerID must be 0)</td>
						<td><input name='customerId' type='text' value='0' /></td>
					</tr>
					<tr>
						<td>customerLastName (e.g. BARBARBAR)</td>
						<td><input name='customerLastName' type='text' value=''/></td>
					</tr>
				</tbody>
			</table>
			<%
				for (int i = 0; i < violationMessages.size(); i++){
			%>
    			<h3><%= violationMessages.get(i) %></h3>	
    		<%			
             	} 
            %>            
			<ul>
				<li>CustomerId or Customer Last Name required.</li> 
			</ul>
		</form>
</body>
</html>