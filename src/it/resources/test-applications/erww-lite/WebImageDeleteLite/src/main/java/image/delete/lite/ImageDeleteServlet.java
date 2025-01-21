package image.delete.lite;

import java.io.IOException;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


/**
 * Servlet implementation class ImageDeleteServlet
 */
@WebServlet(name="/ImageDeleteServlet", urlPatterns = {"/ImageDeleteServlet"}, asyncSupported=true)
public class ImageDeleteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String classname = "ImageDeleteServlet";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ImageDeleteServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		AsyncContext asyncContext = null;
		ServletInputStream servletInputStream = null;
		
		try{
			this.debugOut("In " + classname  + ": Before request.startAsync(request, response): Puts the request into asynchronous mode and ensures that the response is not committed after exiting the doPost method");
			asyncContext = request.startAsync(request, response);
		}catch(Throwable t){
			System.out.println("Exception in request.startAsync(request, response): " + t.getMessage());
			t.printStackTrace();
			return;
		}
		
		try {
       		this.debugOut("In " + classname + ": Before request.getInputStream(): Open the InputStream");
       		servletInputStream = request.getInputStream();
		}catch (Throwable t){
			System.out.println("Exception in request.getInputStream(): " + t.getMessage());
			t.printStackTrace();
			return;
		}
		
		if (servletInputStream.isFinished() == false){
        	this.debugOut("In " + classname + ": servletInputStream.isFinished()== FALSE: we do NOT have all the input data");
              
        	try{
        		this.debugOut("In " + classname + ": Before new ReadListenerImpl(servletInputStream, request, asyncContext)");
        		ReadListener readListener = new ReadListenerImpl(servletInputStream, request, response, asyncContext);
                       
        		this.debugOut("In " + classname + ": Before setReadListener(readListener): Associates the InputStream with a ReadListener object that contains callback methods to read data asynchronously");
        		servletInputStream.setReadListener(readListener);            	
            }catch (Throwable t){
        		System.out.println("Exception in setting the ReadListener: " + t.getMessage());
        		t.printStackTrace();
        		return;
        	}              	        
        } else if (servletInputStream.isFinished() == true) { 	
        	this.debugOut("In " + classname + ": servletInputStream.isFinished()== TRUE: we have all the input data");
        }
	}
	public void debugOut(String message){
		if (System.getProperty("DEBUG_OUT") != null){
			if (System.getProperty("DEBUG_OUT").equals("ON") || System.getProperty("DEBUG_OUT").equals("SERVLET_NIO_ON")) {
				long tid = Thread.currentThread().getId();
				System.out.println("WebImageDelete_thread_" + tid + ": " + message);
			}
		}
	}
}
