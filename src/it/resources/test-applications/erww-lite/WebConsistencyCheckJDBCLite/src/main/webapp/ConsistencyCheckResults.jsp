<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page
	language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"
	import="java.util.Vector"
	import="java.util.Iterator"
	%>
<html>
<head>
<title>ConsistencyCheckResults</title>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
</head>
<body bgcolor="#F8F7CD">
<h2 align="center"><font color="navy">ERWW Consistency Checks using JDBC Results</font></h2>
<h3 align="center"><font color="navy"><i>${param.lockMode}</i></font></h3>
<hr	style="border-color: gray; border-style: outset; border-width: thick">
<TABLE border="0">
	<TBODY>
<%
//Modified this to remove the warning "Vector is a raw type.  References to generic type Vector<E> should be parameterized"
Vector<String> results = new Vector<String>();
String result = null;
//Added this to remove the warning "Vector is a raw type.  References to generic type Vector<E> should be parameterized"
Object var = request.getAttribute("results");
if (var instanceof Vector<?>){
	for (int i=0;i<((Vector<?>)var).size(); i++) {
		Object item = ((Vector<?>)var).get(i);
		if (item instanceof String) {
			results.add((String)item);
		}
	}
}
//Removed this to remove the warning "Vector is a raw type.  References to generic type Vector<E> should be parameterized"
//This is now being done in the if/for/if loop above.
//results = (Vector<String>)request.getAttribute("results");
Iterator<?> iterator = results.iterator();

try {
	while (iterator.hasNext()) {
		result = (String)iterator.next();
%>
 		<tr bgcolor="#cccccc" align="left"> 
			<td ><font face="Arial" color="#804000" size="4"><%=result%></font></td>
		</tr>
<%
	}
} catch (Exception e) {
		e.printStackTrace();
}
%>
</TBODY>
</TABLE>
<hr style="border-color: gray; border-style: outset; border-width: thick">
</body>
</html>