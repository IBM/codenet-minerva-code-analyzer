package image.delete.lite;

import java.io.IOException;
import java.util.ArrayList;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;

public class WriteListenerImpl implements WriteListener {
	private String classname = "WriteListenerImpl";
	private ServletOutputStream servletOutputStream = null;
	private int printProgressFlag =  0;
	private int imagesDeletedFlag = 0;
	private ArrayList<String> arrayListImageIdsDeleted = new ArrayList<String>();
	private AsyncContext asyncContext = null;
	
	public WriteListenerImpl(ServletOutputStream output, int printProgress, int theImagesDeletedFlag ,ArrayList<String> theArrayListImageIdsDeleted, AsyncContext context) {
		servletOutputStream = output;
		printProgressFlag = printProgress;
        imagesDeletedFlag = theImagesDeletedFlag;
        arrayListImageIdsDeleted = theArrayListImageIdsDeleted;
        asyncContext = context;
	}

	@Override
	public void onError(Throwable t) {
		System.out.println("Error in WriteListnerImpl - onError()");
        t.printStackTrace();
        asyncContext.complete();
	}

	@Override
	public void onWritePossible() throws IOException {
		this.debugOut("In " + classname + ": onWritePossible()");
		
	    int printFlag=0;
		
    	try{
    		this.debugOut("In " + classname + ": The WriteListener may go into async mode during any of the servletOutputStream.println statements"); 
    		this.printNonBlockingIO(printFlag++, "<html>");
    		this.printNonBlockingIO(printFlag++, "<head><title>Delete Images Servlet 3.1: Non-Blocking I/O: Results</title></head> ");
    		this.printNonBlockingIO(printFlag++, "<body>");
    		this.printNonBlockingIO(printFlag++, "<body bgcolor=\"#f8f7cd\">");
    		this.printNonBlockingIO(printFlag++, "<h2>ERWW-Lite Delete Images Results</h2>");
    		this.printNonBlockingIO(printFlag++, "<h3>Servlet 3.1: Non-Blocking I/O (light weight version)</h3>");
    		this.printNonBlockingIO(printFlag++, "<hr>");
    		this.printNonBlockingIO(printFlag++, "<table border=\"8\" bgcolor=\"#cccccc\" bordercolor=\"#FFCC99\" >"); 
    		this.printNonBlockingIO(printFlag++, "<tr><td>Number of Images Deleted: " + imagesDeletedFlag + "</tr></td>" );
    		this.printNonBlockingIO(printFlag++, "</table >"); 
    		
    		this.printNonBlockingIO(printFlag++, "<table border=\"8\" bgcolor=\"#cccccc\" bordercolor=\"#FFCC99\" >"); 
    		this.printNonBlockingIO(printFlag++, "<tr><td><b>Image Ids</b></tr></td>");
    		int x = 0;
    		while (arrayListImageIdsDeleted.size() > x) { 
    			this.printNonBlockingIO(printFlag++, "<tr><td> " + arrayListImageIdsDeleted.get(x)+ "</tr></td>" );
    			x++;
    		}
    		this.printNonBlockingIO(printFlag++, "</table >");    		
    		this.printNonBlockingIO(printFlag++, "<hr>");
    		this.printNonBlockingIO(printFlag++, "<h3>WebImageDeleteLite Status: Successful </h3>");
    		this.printNonBlockingIO(printFlag++, "</html>");
    		this.printNonBlockingIO(printFlag++, "<hr>");
		} catch(Throwable t) {
			System.out.println("<<< Error: In " + classname + ": servletOutputStream.println() - Exception: " + t);
  			t.printStackTrace(); 
  			this.printNonBlockingIO(printFlag++, "<html>");
  			this.printNonBlockingIO(printFlag++, "<h3>WebImageDeleteLite Status: Failed: " + t.getMessage() + "</h3>");
  			this.printNonBlockingIO(printFlag++, "</html>");
 		}   
		
    	try{
    		this.debugOut("In " + classname + ": Before asyncContext.complete()"); 
    		asyncContext.complete();
		} catch(Throwable t) {
			System.out.println("<<< Error: In " + classname + ": Error asyncContext.complete() - Exception: " + t);
  			t.printStackTrace(); 
  			this.printNonBlockingIO(printFlag++, "<html>");
  			this.printNonBlockingIO(printFlag++, "<h2>WebImageDeleteLite Status: Failed: " + t.getMessage() + "</h2>");
  			this.printNonBlockingIO(printFlag++, "</html>");
 		} 
	}
	
	protected void printNonBlockingIO(int printFlagId, String message){
		this.debugOut("In " + classname + ": printNonBlockingIO(): Before while where message: " + message);
		this.debugOut("In " + classname + ": printNonBlockingIO(): Before while where printFlagId: " + printFlagId);
		this.debugOut("In " + classname + ": printNonBlockingIO(): Before while where printProgressFlag: " + printProgressFlag);
		while (servletOutputStream.isReady() && (printProgressFlag==printFlagId)){	
			try{
				this.debugOut("In " + classname + ": printNonBlockingIO(): Inside while where message: " + message);
				servletOutputStream.println(message);
				printProgressFlag = printFlagId+1;
			} catch(Throwable t) {
				System.out.println("<<< Error: In " + classname + ": Error printing servletOutputStream - Exception: " + t);
				t.printStackTrace(); 
			} 
		}
	}
	
	public void debugOut(String message) {
		if (System.getProperty("DEBUG_OUT") != null) {
			if (System.getProperty("DEBUG_OUT").equals("ON") || System.getProperty("DEBUG_OUT").equals("SERVLET_NIO_ON")) {
				long tid = Thread.currentThread().getId();
				System.out.println("WebImageDelete_thread_" + tid + ": " + message);
			}
		}
	}

}
