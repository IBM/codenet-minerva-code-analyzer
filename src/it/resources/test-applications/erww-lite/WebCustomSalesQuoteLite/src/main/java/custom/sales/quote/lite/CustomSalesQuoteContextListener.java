package custom.sales.quote.lite;

import java.util.List;
import java.util.ArrayList;

import jakarta.servlet.HttpConstraintElement;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.ServletRegistration.Dynamic;
import jakarta.servlet.ServletSecurityElement;
import jakarta.servlet.annotation.ServletSecurity.EmptyRoleSemantic;
import jakarta.servlet.annotation.ServletSecurity.TransportGuarantee;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.ServletContext;
import jakarta.servlet.HttpMethodConstraintElement;


/**
 * Application Lifecycle Listener implementation class CustomerSalesQuoteContextListener
 *
 */
@WebListener
public class CustomSalesQuoteContextListener implements ServletContextListener {

    /**
     * Default constructor. 
     */
    public CustomSalesQuoteContextListener() {
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent sce) {
    	
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext servletContext  = sce.getServletContext();
        HttpConstraintElement constraint1 = null;
        List<HttpMethodConstraintElement> methodConstraints1 = new ArrayList<HttpMethodConstraintElement>();
        ServletSecurityElement servletSecurity1 = null;
        HttpConstraintElement constraint3 = null;
        ServletSecurityElement servletSecurity3 = null;
        
		try	{
	        Dynamic dynamicServletRegistration1 = servletContext.addServlet("CustomSalesQuoteServlet","custom.sales.quote.lite.CustomSalesQuoteServlet");
	        dynamicServletRegistration1.addMapping("/CustomSalesQuoteServlet");
	        dynamicServletRegistration1.setRunAsRole("RunAsAdministratorRole");
	        constraint1 = new HttpConstraintElement(EmptyRoleSemantic.DENY);
	        methodConstraints1.add(new HttpMethodConstraintElement("GET", new HttpConstraintElement(TransportGuarantee.NONE, new String[]{"CustomerRole"})));
	        methodConstraints1.add(new HttpMethodConstraintElement("PUT", new HttpConstraintElement(TransportGuarantee.NONE, new String[]{"CustomerRole"})));
	        methodConstraints1.add(new HttpMethodConstraintElement("POST", new HttpConstraintElement(TransportGuarantee.NONE, new String[]{"CustomerRole"})));
	        servletSecurity1 = new ServletSecurityElement(constraint1, methodConstraints1);
	        dynamicServletRegistration1.setServletSecurity(servletSecurity1);
	        
	        Dynamic dynamicServletRegistration2 = servletContext.addServlet("ImageFileUploadServlet","custom.sales.quote.lite.ImageFileUploadServlet");
	        dynamicServletRegistration2.addMapping("/ImageFileUploadServlet");  
	        
	        Dynamic dynamicServletRegistration3 = servletContext.addServlet("ImageFileDownloadServlet","custom.sales.quote.lite.ImageFileDownloadServlet");
	        dynamicServletRegistration3.addMapping("/ImageFileDownloadServlet"); 
	        dynamicServletRegistration3.setRunAsRole("RunAsAdministratorRole");
	        constraint3 = new HttpConstraintElement(TransportGuarantee.CONFIDENTIAL, new String[]{"CustomerRole"});
	        servletSecurity3 = new ServletSecurityElement(constraint3);
	        dynamicServletRegistration3.setServletSecurity(servletSecurity3);

		}catch (Exception e){ 
			System.out.println(">>> Error: In CustomSalesQuoteContextListener: Error adding a dynamically added servlet:  exception: "+e);
			e.printStackTrace();
			throw new RuntimeException(">>> Error: In CustomSalesQuoteContextListener: Error adding a dynamically added servlet:  exception: "+e);
		}
    }
}
