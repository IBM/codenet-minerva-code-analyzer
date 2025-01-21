package websession.client2.lite;

import java.util.EnumSet;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.ServletRegistration.Dynamic;
import jakarta.servlet.SessionTrackingMode;
import jakarta.servlet.annotation.WebListener;

import irwwbase.IRWWBase;
/**
 * Application Lifecycle Listener implementation class WebClient2ServletContextListener
 *
 */
@WebListener
public class WebClient2ServletContextListener implements ServletContextListener {
	private String classname = "WebClient2ServletContextListener";
	private IRWWBase irwwBase = new IRWWBase();
	
    /**
     * Default constructor. 
     */
    public WebClient2ServletContextListener() {

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
	        Dynamic dynamicServletRegistration1 = servletContext.addServlet("WebClient2Controller","websessionclient2.WebClient2Controller");
	        dynamicServletRegistration1.addMapping("/WebClient2Controller");
	        dynamicServletRegistration1.setInitParameter("WebClient2Results", "/WebClient2ResultsForm.jsp");
		}catch (Exception e){ 
			System.out.println("<<< Error: In " + classname + ": Error adding a dynamically added servlet:  exception: " + e);
			e.printStackTrace();
			throw new RuntimeException("<<< Error: In " + classname + ": Error adding a dynamically added servlet:  exception: ", e);
		}
    }
	
}
