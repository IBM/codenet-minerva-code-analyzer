package catalog.update.lite;

import imagejpa.ImageJPA;
import irwwbase.ExtendedRandom;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Queue;

import javax.imageio.ImageIO;
import javax.naming.InitialContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.transaction.UserTransaction;

public class WriteListenerImpl implements WriteListener {
	private String classname = "WriteListenerImpl";
	private ServletOutputStream servletOutputStream = null;
	private int printProgressFlag =  0;
	private Queue<byte[]> output1Queue = null;
	private Queue<byte[]> output2Queue = null;
	private AsyncContext asyncContext = null;
	private byte [] image1ByteArray = null;
	private byte [] image2ByteArray = null;
	private EntityManagerFactory entityManagerFactory = null;	

	public WriteListenerImpl(ServletOutputStream output, int printProgress, Queue<byte[]> queue1, Queue<byte[]> queue2 ,AsyncContext context) {
		servletOutputStream = output;
		printProgressFlag = printProgress;
        output1Queue = queue1;
        output2Queue = queue2;
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
	    
	    int productImage1FileSizeBytes = 0;
	    int productImage2FileSizeBytes = 0;
	    
		EntityManager entityManager = null;
		InitialContext initialContext = null;
		UserTransaction userTran = null;
		String invocationMode = null;
		String imageId1 = null;
		String imageId2 = null;
		
    	this.debugOut("In " + classname + ": The WriteListener may go into async mode during any of the servletOutputStream.println statements");
    	this.printNonBlockingIO(printFlag, "<html>");
    	this.printNonBlockingIO(printFlag++, "<head><title>ERWW-Lite Servlet 3.1: Non-Blocking I/O: Results</title></head> ");
    	this.printNonBlockingIO(printFlag++, "<body>");
    	this.printNonBlockingIO(printFlag++, "<body bgcolor=\"#f8f7cd\">");
    	this.printNonBlockingIO(printFlag++, "<h2>ERWW-Lite Results</h2>");
    	this.printNonBlockingIO(printFlag++, "<h3>Servlet 3.1: Non-Blocking I/O</h3>");
    	this.printNonBlockingIO(printFlag++, "<table border=\"8\" bgcolor=\"#cccccc\" bordercolor=\"#FFCC99\" >"); 
		
    	try{
    		invocationMode = asyncContext.getRequest().getAttribute("invocationMode").toString();
    		this.debugOut("In " + classname + ": invocationMode = " + invocationMode);
        }catch (Throwable t){
    		System.out.println("Exception in (String)request.getAttribute(invocationMode): " + t.getMessage());
    		t.printStackTrace();
    		return;
    	}
    	
		try{
			imageId1 = asyncContext.getRequest().getAttribute("imageId1").toString();
			imageId2 = asyncContext.getRequest().getAttribute("imageId2").toString();
			this.debugOut("In " + classname + ": imageId1 = " + imageId1);
			this.debugOut("In " + classname + ": imageId2 = " + imageId2);
		}catch (Throwable t){
			System.out.println("Exception in (String)request.getAttribute(imageId): " + t.getMessage());
			t.printStackTrace();
			return;
		}
    			
    	if (invocationMode.contentEquals("businessLogic_Servlet") || invocationMode.contentEquals("businessLogic_None")){
    		this.debugOut("In " + classname + ": "+ invocationMode + " path");
    		this.printNonBlockingIO(printFlag++, "<tr><td>Business Logic in "+ invocationMode + " Path<tr><td>");
    		
     		this.debugOut("In " + classname + ": onWritePossible(): Before while loop: output1Queue.size() = " + output1Queue.size());
     		this.debugOut("In " + classname + ": onWritePossible(): Before while loop: output2Queue.size() = " + output2Queue.size());
			
     		int imageId1_printFlag = printFlag++;
     		int imageId2_printFlag = printFlag++;
     		
     		try{			
     			while (((output1Queue.peek() != null) || (output2Queue.peek() != null)) && servletOutputStream.isReady()) {
     				this.debugOut("In " + classname + ": onWritePossible(): This while loop can get invoked multiple times in async mode depending upon network traffic etc.");
     				
     				image1ByteArray = output1Queue.poll();
     				image2ByteArray = output2Queue.poll();
     				this.debugOut("In " + classname + ": onWritePossible(): in while loop: imageByteArray.length = " + image1ByteArray.length);
     				this.debugOut("In " + classname + ": onWritePossible(): in while loop: imageByteArray.length = " + image2ByteArray.length);
     				
     				this.getImageOutputFile(image1ByteArray, imageId1, imageId1_printFlag);     				
     				this.getImageOutputFile(image2ByteArray, imageId2, imageId2_printFlag);
     			}
     		} catch (Throwable t) { 
     			System.out.println("<<< Error: In " + classname + ": Error in while loop: exception: "+t);
     			t.printStackTrace();
     			this.printNonBlockingIO(printFlag++, "<h2>WebCatalogUpdateLite Status: Failed: " + t.getMessage() + "</h2>");
     			return;
     		} 
     	}

    	if (invocationMode.contentEquals("businessLogic_Listeners")){     		
    		this.debugOut("In " + classname + ": businessLogic_Listeners path");
    		this.printNonBlockingIO(printFlag++, "<tr><td>Business Logic in ReadListener/WriteListener Path<tr><td>");
	    
    		try{
    			initialContext = new InitialContext();
    		}catch(Throwable t){
    			System.out.println("<<< Error: In " + classname + ": Error newing up InitialContect(): " + t.getMessage());
    			t.printStackTrace();
    			return;
    		}
     	
    		try	{
    			entityManagerFactory = (EntityManagerFactory)initialContext.lookup("java:comp/env/persistence/WebCatalogUpdateLite"); 
    			entityManager = entityManagerFactory.createEntityManager();
    		}catch (Throwable t){ 
    			System.out.println("<<< Error: In " + classname + ": Error getting the entityManager: "+ t.getMessage());
    			t.printStackTrace();
    			return;
    		}
		
    		try{
    			userTran = (UserTransaction) initialContext.lookup("java:comp/UserTransaction"); //thread sensitive
    			userTran.begin();
    			entityManager.joinTransaction();
    		}catch(Throwable t){
    			System.out.println("<<< Error: In " + classname + ": Error beginning the transaction: " + t.getMessage());
    			t.printStackTrace();
    			return;
    		}
    		
     		int imageId1_printFlag = printFlag++;
     		int imageId2_printFlag = printFlag++;
    		
    		image1ByteArray = this.getImage(entityManager, imageId1, imageId1_printFlag);    		
    		image2ByteArray = this.getImage(entityManager, imageId2, imageId2_printFlag);
    		
     		try {			
     			userTran.commit();
     			this.debugOut("In " + classname + ": After userTran.commit() ");
     		}catch(Throwable t){	
     			System.out.println("<<< Error: In " + classname + ": Commit Transaction: " + t.getMessage());
     			t.printStackTrace();
     			return;
     		}
		
     		try {
     			if (entityManager!=null && entityManager.isOpen()){
     				entityManager.close();
     			}
     		}catch(Throwable t){
     			System.out.println("<<< Error: In " + classname + ": Error entityManager.close(): " + t.getMessage());
     			t.printStackTrace();
     			return;
     		}
 				
     		this.getImageOutputFile(image1ByteArray, imageId1, imageId1_printFlag);	
     		this.getImageOutputFile(image2ByteArray, imageId2, imageId2_printFlag); 
		} 
 
    	try{
    		productImage1FileSizeBytes = Integer.parseInt(asyncContext.getRequest().getAttribute("productImage1FileSizeBytes").toString()); 
    		this.debugOut("In " + classname + ": onWritePossible(): productImage1FileSizeBytes = " + productImage1FileSizeBytes);
    		
    		productImage2FileSizeBytes = Integer.parseInt(asyncContext.getRequest().getAttribute("productImage2FileSizeBytes").toString()); 
    		this.debugOut("In " + classname + ": onWritePossible(): productImage2FileSizeBytes = " + productImage2FileSizeBytes);
     	} catch (Throwable t) { 
    		System.out.println("<<< Error: In " + classname + ": Error involving productImage1FileSizeBytes: exception: "+t);
     		t.printStackTrace();
     		this.printNonBlockingIO(printFlag++, "</table >"); 
     		this.printNonBlockingIO(printFlag++, "<h2>WebCatalogUpdateLite Status: Failed: " + t.getMessage() + "</h2>");    
    		return;
    	}    	
    		    		
    	if((productImage1FileSizeBytes == image1ByteArray.length) && (productImage2FileSizeBytes == image2ByteArray.length) ){
    		this.printNonBlockingIO(printFlag++, "</table >"); 
    		this.printNonBlockingIO(printFlag++, "<h3>WebCatalogUpdateLite Status: Successful </h3>");
    		this.printNonBlockingIO(printFlag++, "<h4>(productImageFileSizeBytes equals imageByteArray.length)</h4>");
    	} else {
    		this.printNonBlockingIO(printFlag++, "</table >"); 
    		this.printNonBlockingIO(printFlag++, "<h3>WebCatalogUpdateLite Status: Failed</h3>");
    		this.printNonBlockingIO(printFlag++, "<h4>(productImageFileSizeBytes does NOT equal imageByteArray.length)</h4>");
    	}    		
    	this.printNonBlockingIO(printFlag++, "</body >"); 
    	this.printNonBlockingIO(printFlag++, "</html >"); 
    		
        asyncContext.complete();         
	}
	
	public byte[] getImage(EntityManager entityManager, String imageId, int printErrorFlag) throws IOException {
		ImageJPA imageJPA = null;
		byte[] imageByteArray = null;
		
		try	{			
			imageJPA = entityManager.find(ImageJPA.class, imageId);  
			this.debugOut("In " + classname + ": After entityManager.find(ImageJPA.class, imageId) where image = " + imageJPA);
		} catch (Throwable t){ 
			System.out.println("<<< Error: In " + classname + ": Error getting image: exception: "+t.getMessage());
			t.printStackTrace();
			this.printNonBlockingIO(printErrorFlag++, "</table >"); 
			this.printNonBlockingIO(printErrorFlag++, "<h2>WebCatalogUpdateLite Status: Failed: " + t.getMessage() + "</h2>");
		}		
	
		if (imageJPA != null){
			try	{
				this.debugOut("In " + classname + ": Before image.getImageData()");
				imageByteArray = imageJPA.getImageData();
			} catch (Throwable t) {
				System.out.println("<<< Error: In " + classname + ": Error setting/persisting the imageJPA instance: exception: " + t.getMessage());
				t.printStackTrace();
				this.printNonBlockingIO(printErrorFlag++, "</table >"); 
				this.printNonBlockingIO(printErrorFlag++, "<h2>WebCatalogUpdateLite Status: Failed: " + t.getMessage() + "</h2>");
			}
		} 
		return imageByteArray;
	}
	
	public void getImageOutputFile(byte[] imageByteArray, String imageId, int printFlag) throws IOException {
		String imageTempFile = null;
		BufferedImage bufferedImage = null;
		File tempFile = null;
		String path = null;
		File outputImage = null;
		
        if (imageByteArray != null) {  
        	this.debugOut("In " + classname + ": onWritePossible(): imageByteArray != null");
        	this.debugOut("In " + classname + ": onWritePossible(): imageByteArray.length  = " + imageByteArray.length);
        	
        	this.printNonBlockingIO(printFlag++, "<table border=\"8\" bgcolor=\"#cccccc\" bordercolor=\"#FFCC99\" >"); 
        	
    		ExtendedRandom rand = new ExtendedRandom();
    		rand.nextInt(0, 10);		
    		imageTempFile = imageId + rand.nextInt(100000000, 999999999);     
        	
    		try{
    			if (imageByteArray != null){
    				this.debugOut("In " + classname + ": onWritePossible(): imageByteArray.length = " + imageByteArray.length);
     				bufferedImage = ImageIO.read(new ByteArrayInputStream(imageByteArray));
    				this.debugOut("In " + classname + ": onWritePossible(): bufferedImage = " + bufferedImage);
    			}
    		} catch (Throwable t) { 
    			System.out.println("<<< Error: In " + classname + ": Error getting bufferedImage: exception: "+t);
    			t.printStackTrace();
    			this.printNonBlockingIO(printFlag++, "</table >"); 
    			this.printNonBlockingIO(printFlag++, "<h2>WebCatalogUpdateLite Status: Failed: " + t.getMessage() + "</h2>");
    		}        	
        	
     	 	try{
    			tempFile = (File)asyncContext.getRequest().getServletContext().getAttribute(ServletContext.TEMPDIR);
    			path = tempFile.getPath(); 
    			this.debugOut("In " + classname + ": onWritePossible(): path = " + path);
                  String finalPath = null;
        		if (path.contains("\\")){
    				finalPath = path + "\\";
    			} else {
    				finalPath = path + "/";
    			}                		
    			outputImage = new File(finalPath + imageTempFile + ".jpg");
    		} catch (Throwable t) { 
    			System.out.println(" Error creating outputImage: exception: "+t);
    			t.printStackTrace(); 
    			this.printNonBlockingIO(printFlag++, "</table >"); 
    			this.printNonBlockingIO(printFlag++, "<h2>WebCatalogUpdateLite Status: Failed: " + t.getMessage() + "</h2>");
      		}

    		try{
      			this.debugOut("In " + classname + ": onWritePossible(): ImageIO.write(bufferedImage, jpg, outputImage) writes bufferedImage to file: " + outputImage.getAbsolutePath());
    			ImageIO.write(bufferedImage, "jpg", outputImage);				
    		} catch (Throwable t) { 
    			System.out.println("<<< Error: In " + classname + ": Error writing image using ImageIO: exception: "+t);
    			t.printStackTrace();
    			this.printNonBlockingIO(printFlag++, "</table >"); 
    			this.printNonBlockingIO(printFlag++, "<h2>WebCatalogUpdateLite Status: Failed: " + t.getMessage() + "</h2>");
    		}
    		
    		try {
    			if (bufferedImage!=null){
    				bufferedImage.flush();
    			}
    		} catch(Throwable t) {
    			System.out.println("<<< Error: In " + classname + ": Error bufferedImage.flush() - Exception: " + t);
      			t.printStackTrace(); 
      			this.printNonBlockingIO(printFlag++, "</table >"); 
      			this.printNonBlockingIO(printFlag++, "<h2>WebCatalogUpdateLite Status: Failed: " + t.getMessage() + "</h2>");
     		}   
    		
        	if ((imageTempFile != null)){
        		try {
        			this.printNonBlockingIO(printFlag++, "<p><img src=\"ImageServingServlet?imageFileName=" + imageTempFile + ".jpg" +  "&tempdir=" + path + "\" width=\"400\" height=\"300\" alt=\"WebCatalogUpdateLite Status: Failed\" /></p>");
             		//Test failed case
          			//servletOutputStream.println("<p><img src=\"ImageServingServlet?imageFileName=missing.jpg" +  "&tempdir=" + path + "\" width=\"400\" height=\"300\" alt=\"Status: Failed\" /></p>");
              	} catch (Throwable t) {
              		this.printNonBlockingIO(printFlag++, "</table >"); 
              		this.printNonBlockingIO(printFlag++, "<h2>WebCatalogUpdateLite Status: Failed: Error displaying image: " + t.getMessage() + "</h2>");    
            	}
        	} 
        	/* Comment out dead code
        	else {
        		this.printNonBlockingIO(printFlag++, "<tr><td>Error: " + outputImage + " is null <tr><td>");
        		this.printNonBlockingIO(printFlag++, "<tr><td>WebCatalogUpdateLite Status: Failed: " + outputImage + " is null <tr><td>");
        	} 
        	*/
        }
    	return;
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
	
	protected void debugOut(String message) {
		if (System.getProperty("DEBUG_OUT") != null) {
			if (System.getProperty("DEBUG_OUT").equals("ON") || System.getProperty("DEBUG_OUT").equals("SERVLET_NIO_ON")) {
				long tid = Thread.currentThread().getId();
				System.out.println("WebCatalog_thread_" + tid + ": " + message);
			}
		}
	}
}
