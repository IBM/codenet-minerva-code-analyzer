package websession.client1.lite;


import java.util.EnumSet;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.ServletRegistration.Dynamic;
import jakarta.servlet.SessionTrackingMode;
import jakarta.servlet.annotation.WebListener;

import irwwbase.IRWWBase;
/**
 * Application Lifecycle Listener implementation class WebClient1ServletContextListener
 *
 */
@WebListener
public class WebClient1ServletContextListener implements ServletContextListener {
	private String classname = "WebClient1ServletContextListener";
	private IRWWBase irwwBase = new IRWWBase();
	
    /**
     * Default constructor. 
     */
    public WebClient1ServletContextListener() {
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
        
		try	{         
			if (System.getProperty("SESSION_TRACKING_MODE") != null) {
			irwwBase.debugOut("<<< " + classname + ": SESSION_TRACKING_MODE = " + System.getProperty("SESSION_TRACKING_MODE"));
			irwwBase.debugOut("<<< " + classname + "(System.getProperty(SESSION_TRACKING_MODE).startsWith(COOKIE) = " + (System.getProperty("SESSION_TRACKING_MODE").startsWith("COOKIE")));
			irwwBase.debugOut("<<< " + classname + "(System.getProperty(SESSION_TRACKING_MODE).startsWith(URL) = " + (System.getProperty("SESSION_TRACKING_MODE").startsWith("URL")));
				if (System.getProperty("SESSION_TRACKING_MODE").startsWith("COOKIE")){
					irwwBase.debugOut("<<< " + classname + ": setSessionTrackingModes to COOKIE");
					servletContext.setSessionTrackingModes(EnumSet.of(SessionTrackingMode.COOKIE));
				} else if (System.getProperty("SESSION_TRACKING_MODE").startsWith("URL")){
					irwwBase.debugOut("<<< " + classname + ": setSessionTrackingModes to URL");
					servletContext.setSessionTrackingModes(EnumSet.of(SessionTrackingMode.URL));
				} else {
					System.out.println("<<< Error: In " + classname + ": SESSION_TRACKING_MODE JVM Property is not set. ");
					throw new RuntimeException("<<< Error: In " + classname + ": SESSION_TRACKING_MODE JVM Property is not set. ");	
				}				
			}else{
				System.out.println("<<< Error: In " + classname + ": SESSION_TRACKING_MODE JVM Property is not set. ");
				throw new RuntimeException("<<< Error: In " + classname + ": SESSION_TRACKING_MODE JVM Property is not set. ");
			}
			irwwBase.debugOut("<<< " + classname + ": getEffectiveSessionTrackingModes() = " + servletContext.getEffectiveSessionTrackingModes());
			
		}catch (Exception e){ 
			System.out.println("<<< Error: In " + classname + ": Error: setSessionTrackingModes:  exception: " + e);
			e.printStackTrace();
			throw new RuntimeException("<<< Error: In " + classname + ": Error: setSessionTrackingModes: exception: ", e);
		}
        
		try	{
	        Dynamic dynamicServletRegistration1 = servletContext.addServlet("WebClient1Controller","websessionclient1.WebClient1Controller");
	        dynamicServletRegistration1.addMapping("/WebClient1Controller");
	        dynamicServletRegistration1.setInitParameter("WebClient1Results", "/WebClient1ResultsForm.jsp");
		}catch (Exception e){ 
			System.out.println("<<< Error: In " + classname + ": Error adding a dynamically added servlet:  exception: " + e);
			e.printStackTrace();
			throw new RuntimeException("<<< Error: In " + classname + ": Error adding a dynamically added servlet:  exception: ", e);
		}
    }
	
}
