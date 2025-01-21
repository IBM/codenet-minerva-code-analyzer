package com.acme.modres;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.websphere.servlet.response.ResponseUtils;

@WebServlet("/resorts/upper")
public class UpperServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		
		String originalStr = request.getParameter("input");
		if (originalStr == null) {
			originalStr = "";
		}
		
        String newStr = originalStr.toUpperCase();
        newStr = ResponseUtils.encodeDataString(newStr);
        
	    PrintWriter out = response.getWriter();  
	    out.print("<br/><b>capitalized input " + newStr + "</b>");  
	}
}
