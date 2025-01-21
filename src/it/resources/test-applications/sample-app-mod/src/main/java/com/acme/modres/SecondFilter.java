package com.acme.modres;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.stream.Collectors;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//Assisted by WCA for GP
//Latest GenAI contribution: granite-20B-code-instruct-v2 model
//Note: This new filter needs to be added to the web.xml config
public class SecondFilter implements Filter { 

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
     BufferedReader rd = req.getReader();
     String rcontents = rd.lines().collect(Collectors.joining());
     PrintWriter out = response.getWriter();  
     out.print(rcontents + " to our site! ");  
     chain.doFilter(request, response);
 }

 @Override
 public void destroy() {
     // Clean up resources used by the filter
 }
}

