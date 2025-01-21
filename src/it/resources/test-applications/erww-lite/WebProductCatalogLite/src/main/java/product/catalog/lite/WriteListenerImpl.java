package product.catalog.lite;

import java.io.IOException;
import java.util.Queue;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.WebConnection;

public class WriteListenerImpl implements WriteListener {
	private String classname = "WriteListenerImpl";
	private WebConnection webConnection = null;
	private String productName = null;
	private String productFileName = null;
	private Queue<byte[]> productOutputQueue = null;
	private int productByteArrayLength = 0;
	private ServletOutputStream servletOutputStream = null;
	private byte [] productByteArray = null;

	public WriteListenerImpl(WebConnection wc, String pName, String pfName,  Queue<byte[]> pQueue, int pByteArrayLength, ServletOutputStream outputStream) {
		webConnection = wc;
		productName = pName;
		productFileName = pfName;
        productOutputQueue = pQueue;
        productByteArrayLength = pByteArrayLength;
        servletOutputStream = outputStream;
	}

	@Override
	public void onWritePossible() throws IOException {
		this.debugOut("In " + classname + ": onWritePossible(): Before while loop: productName = " + productName);
		this.debugOut("In " + classname + ": onWritePossible(): Before while loop: productFileName = " + productFileName);
		this.debugOut("In " + classname + ": onWritePossible(): Before while loop: productByteArrayLength = " + productByteArrayLength);
     	this.debugOut("In " + classname + ": onWritePossible(): Before while loop: productOutputQueue.size() = " + productOutputQueue.size());       	
     	try{ 
     		while ((productOutputQueue.peek() != null) && (servletOutputStream.isReady())) {
     			this.debugOut("In " + classname + ": onWritePossible(): This while loop can get invoked multiple times in async mode");     				
     			productByteArray = productOutputQueue.poll();      			    		
       			servletOutputStream.write(productByteArray, 0, productByteArray.length);
       		} 
     	} catch (Throwable t) { 
     		System.out.println("<<< Error: In " + classname + ": Error in while loop: exception: "+t);
     		t.printStackTrace();
      		return;
     	} 
    	
     	if ((productOutputQueue.peek() == null) && (servletOutputStream.isReady())) {
	    	try{
	    		this.debugOut("In " + classname + ": Before servletOutputStream.flush(): productByteArray.length = " + productByteArray.length); 
	    		servletOutputStream.flush();
	    		servletOutputStream.close();
	    		webConnection.close();
	    		this.debugOut("In " + classname + ": After servletOutputStream.close(): productByteArray.length = " + productByteArray.length);
	      	} catch (Throwable t) { 
	    		System.out.println("<<< Error: In " + classname + ": Error involving flush/close of servletOutputStream and objectOutputStream: exception: "+t);
	     		t.printStackTrace();
	    		return;
	    	} 
     	}	  
	}
	
	@Override
	public void onError(Throwable t) {
		System.out.println("In " + classname + ":Error in WriteListnerImpl - onError()");
        t.printStackTrace();
        try { 
    		servletOutputStream.close();
            webConnection.close(); 
        } catch (Exception e) {
            System.out.println("In " + classname + ": Failed to close web connection" + e);
        }  
	}
	
	protected void debugOut(String message) {
		if (System.getProperty("DEBUG_OUT") != null) {
			if (System.getProperty("DEBUG_OUT").equals("ON") || System.getProperty("DEBUG_OUT").equals("SERVLET_UpgradeRequest_ON")) {
				long tid = Thread.currentThread().getId();
				System.out.println("WebProductCatalog_thread_" + tid + ": " + message);
			}
		}
	}
}
