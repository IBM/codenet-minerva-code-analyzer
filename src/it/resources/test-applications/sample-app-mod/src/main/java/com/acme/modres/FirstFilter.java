package com.acme.modres;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// Assisted by WCA for GP
// Latest GenAI contribution: granite-20B-code-instruct-v2 model
//Note: This new filter needs to be added to the web.xml config
public class FirstFilter implements Filter { 

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialize the filter
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        res.setContentType("text/plain"); 
        String user = req.getParameter("user");
		if (user == null) {
			user = "defaultUser";
		}
        res.getWriter().print("Welcome " + user);
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Clean up resources used by the filter
    }
}
