package product.catalog.lite;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.ReadListener;
import jakarta.servlet.http.WebConnection;


public class ReadListenerImpl implements ReadListener {	      	
	private String classname = "ReadListenerImpl";
	private ServletInputStream servletInputStream = null;
	private ServletOutputStream servletOutputStream = null;
	private WebConnection webConnection = null;
	private Queue<byte[]> productDataQueue = new LinkedBlockingQueue<byte[]>();
	private ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	private String productName = null;
	private String productFileName = null;
	private InputStream entireInputStream = null;
	private ObjectInputStream objectInputStream = null;

	public ReadListenerImpl(ServletInputStream input, ServletOutputStream output, WebConnection wc) {
		servletInputStream = input;
		servletOutputStream = output;
        webConnection = wc;
     } 

	@Override
	public void onAllDataRead() throws IOException {  
        this.debugOut("In " + classname + ": onAllDataRead()");
	}

	@Override
	public void onDataAvailable() throws IOException { //Get all data and process the data
		this.debugOut("In " + classname + ": onDataAvailable()");		

		Object productFile = null;
		byte[] byteArray = new byte[1024]; 
		int productByteArrayLength = 0;
		int length = 0; 
		
	    try {	
			while (servletInputStream.isReady() && (length = servletInputStream.read(byteArray)) != -1){  //length = 1024
				this.debugOut("In " + classname + ": onDataAvailable(): Writes length bytes from the specified byte array starting at offset 0 to this byteArrayOutputStream");
				byteArrayOutputStream.write(byteArray, 0, length);				
			}			
		} catch (IOException ex) {
            System.out.println("<<< Exception in " + classname + ": onDataAvailable: byteArrayOutputStream.write(byteArray, 0, length): IOException: " + ex.getMessage());
            ex.printStackTrace();
            byteArrayOutputStream.reset();
            return;
		} catch (Throwable t) {
			System.out.println("<<< Exception in " + classname + ": onDataAvailable: byteArrayOutputStream.write(byteArray, 0, length): Throwable: " + t.getMessage());
		    t.printStackTrace();
		    byteArrayOutputStream.reset();
		    return;
		}
	    
	    int lengthByteArray = byteArrayOutputStream.toByteArray().length;
	    this.debugOut("In " + classname + ": byteArrayOutputStream.toByteArray().length: " + byteArrayOutputStream.toByteArray().length);	
	    
	    if (byteArrayOutputStream.toByteArray()[lengthByteArray-1] == -1){
				
	        try {
	        	entireInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
	        	this.debugOut("In " + classname + ": entireInputStream: " + entireInputStream);	
	          } catch (Throwable t){
				System.out.println("Exception in getting entireInputStream: " + t.getMessage());
				t.printStackTrace();
				return;
			}	      
	
	        try {
	        	objectInputStream = new ObjectInputStream(entireInputStream);
	          	this.debugOut("In " + classname + ": objectInputStream: " + objectInputStream);	
	        } catch (Throwable t){
				System.out.println("Exception in getting objectInputStream: " + t.getMessage());
				t.printStackTrace();
				return;
			}        
	       
	        try {
	        	productName = (String)objectInputStream.readObject();
	          	this.debugOut("In " + classname + ": productName: " + productName);	
	        } catch (Throwable t){
				System.out.println("Exception in productName = objectInputStream.readObject(): " + t.getMessage());
				t.printStackTrace();
				return;
			}        
	        
	        try {
	        	productFileName = (String)objectInputStream.readObject();
	          	this.debugOut("In " + classname + ": productFileName: " + productFileName);	
	        } catch (Throwable t){
				System.out.println("Exception in productFileName = objectInputStream.readObject(): " + t.getMessage());
				t.printStackTrace();
				return;
			}
	        
	        try {
	        	productByteArrayLength = (int)objectInputStream.readObject();
	          	this.debugOut("In " + classname + ": productByteArrayLength: " + productByteArrayLength);	
	        } catch (Throwable t){
				System.out.println("Exception in productByteArrayLength: " + t.getMessage());
				t.printStackTrace();
				return;
			} 
	
	        try {
	        	productFile = objectInputStream.readObject();
	          	this.debugOut("In " + classname + ": productFile: " + productFile);	
	        } catch (Throwable t){
				System.out.println("Exception in productFile: " + t.getMessage());
				t.printStackTrace();
				return;
			}     
	  		
			try {
				this.debugOut("In " + classname + ": Before productDataQueue.add((byte [])productFile)" );		
	            productDataQueue.add((byte [])productFile);
	            this.debugOut("In " + classname + ": productDataQueue size = " + productDataQueue.size());
	
	            this.debugOut("In " + classname + ": Before NIOReadListenerHttpUpgradeHandler.setWriteListener(webConnection, productName, productFileName, productDataQueue, productByteArrayLength) where productDataQueue = " + productDataQueue);
	            NIOReadListenerHttpUpgradeHandler.setWriteListener(webConnection, productName, productFileName, productDataQueue, productByteArrayLength, servletOutputStream);            
			}catch(Throwable t){
				System.out.println("<<< Error: In " + classname + ": Error setting the WriteListener: " + t.getMessage());
				t.printStackTrace();
				return;
			}   
	    }
        
	}

	@Override
	public void onError(Throwable t) {
		System.out.println("<<< Exception in " + classname + ": onError()" + t.getMessage());
        t.printStackTrace();
        try {
        	byteArrayOutputStream.reset();
        	webConnection.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
	} 	
	
	public void debugOut(String message) {
		if (System.getProperty("DEBUG_OUT") != null) {
			if (System.getProperty("DEBUG_OUT").equals("ON") || System.getProperty("DEBUG_OUT").equals("SERVLET_UpgradeRequest_ON")) {
				long tid = Thread.currentThread().getId();
				System.out.println("WebProductCatalog_thread_" + tid + ": " + message);
			}
		}
	}

}
