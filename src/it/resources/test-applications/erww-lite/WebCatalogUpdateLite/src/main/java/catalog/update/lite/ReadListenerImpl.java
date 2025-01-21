package catalog.update.lite;

import imagejpa.ImageJPA;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.naming.InitialContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.UserTransaction;

import org.apache.commons.fileupload.MultipartStream;

public class ReadListenerImpl implements ReadListener {
	      	
	private String classname = "ReadListenerImpl";
	private ServletInputStream servletInputStream = null;
	private HttpServletRequest httpServletRequest = null;
	private HttpServletResponse httpServletResponse = null;
	private AsyncContext asyncContext = null;
	private Queue<byte[]> image1DataQueue = new LinkedBlockingQueue<byte[]>();
	private Queue<byte[]> image2DataQueue = new LinkedBlockingQueue<byte[]>();
	private InputStream entireInputStream = null;
	private ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	private EntityManagerFactory entityManagerFactory = null;

	public ReadListenerImpl(ServletInputStream input, HttpServletRequest request, HttpServletResponse response, AsyncContext context) {
        servletInputStream = input;
        httpServletRequest = request;
        httpServletResponse = response;
        asyncContext = context;
     } 

	@Override
	public void onAllDataRead() throws IOException {
        this.debugOut("In " + classname + " - onAllDataRead()");
        
        String contentType = null;
    	String boundary = null;   
    	String image1Header = null;
       	String image2Header = null;
        byte[] byteArrayBoundary = null;
        int index = 0;
        MultipartStream multipartStream = null;
        
		EntityManager entityManager = null;
		InitialContext initialContext = null;
		UserTransaction userTran = null;
		byte [] image1Data = null;
		byte [] image2Data = null;
		boolean imageJPA_was_set = false;
        
        try {
        	entireInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
		
        	contentType = httpServletRequest.getContentType();
        	this.debugOut("In " + classname + ": httpServletRequest.getContentType(): " + httpServletRequest.getContentType());		
        	index = contentType.indexOf("boundary=");
        	boundary = contentType.substring(index + 9); 
        	this.debugOut("In " + classname + ": boundary: " + boundary);		
        	byteArrayBoundary = boundary.getBytes();
        } catch (Throwable t){
			System.out.println("Exception in getting entireInputStream or getting the byteArrayBoundary : " + t.getMessage());
			t.printStackTrace();
			return;
        }

        try {
        	multipartStream = new MultipartStream(entireInputStream, byteArrayBoundary); 	
        } catch (Throwable t){
			System.out.println("Exception in getting multipartStream : " + t.getMessage());
			t.printStackTrace();
			return;
        }
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String invocationMode = null;
        String imageId1 = null;
        String productImage1FileSizeBytes = null;
        String imageId2 = null;
        String productImage2FileSizeBytes = null;
        int count = 0;             
        
        try {	
        	boolean nextPart = multipartStream.skipPreamble();
        	this.debugOut("In " + classname + ": nextPart:" + nextPart);
        	
        	while (nextPart) {
        		count++;
        		this.debugOut("--------------- PART " + count + " ---------------");
        		String header = multipartStream.readHeaders();
        		this.debugOut("In " + classname + ": Headers: multipartStream.readHeaders():" + header);
                         
        		multipartStream.readBodyData(baos);
        		this.debugOut("In " + classname + ": Body: After multipartStream.readBodyData(baos)");
        		
        		if (header.contains("name=\"invocationMode\"")){
        			invocationMode = baos.toString();
        			this.debugOut("In " + classname + ": invocationMode:" + invocationMode);
        		}
            
        		if (header.contains("name=\"imageId1\"")){
        			imageId1 = baos.toString();
        			this.debugOut("In " + classname + ": imageId1:" + imageId1);
        		}
            
        		if (header.contains("name=\"productImage1\"")){
        			image1Header = header;
        			multipartStream.readBodyData(baos);                
        			image1DataQueue.add(baos.toByteArray());
        		}     
        		        		
        		if (header.contains("name=\"productImage1FileSizeBytes\"")){
        			productImage1FileSizeBytes = baos.toString();
        			this.debugOut("In " + classname + ": productImage1FileSizeBytes:" + productImage1FileSizeBytes);
        		}  
        	
        		if (header.contains("name=\"imageId2\"")){
        			imageId2 = baos.toString();
        			this.debugOut("In " + classname + ": imageId2:" + imageId2);
        		}
            
        		if (header.contains("name=\"productImage2\"")){
        			image2Header = header;
        			multipartStream.readBodyData(baos);                
        			image2DataQueue.add(baos.toByteArray());
        		}     
        		        		
        		if (header.contains("name=\"productImage2FileSizeBytes\"")){
        			productImage2FileSizeBytes = baos.toString();
        			this.debugOut("In " + classname + ": productImage2FileSizeBytes:" + productImage2FileSizeBytes);
        		}  
        		
        		baos.reset();                        
        		nextPart = multipartStream.readBoundary(); 
        	}
    	}
		catch (Throwable t){
			System.out.println("Exception in while (nextPart): " + t.getMessage());
			t.printStackTrace();
			return;
        }
        this.debugOut("----------------------------------------");   
        
      		       
		if ((imageId1.trim().length() == 0) || (image1Header.contains("filename=\"\"")) || (imageId2.trim().length() == 0) || (image2Header.contains("filename=\"\""))){
    		try {	
	    		this.debugOut("In " + classname + ": Before asyncContext.dispatch(/ReenterInputDataNullForm.html: either imageId or imageDataQueue is null.");
	    		this.debugOut("In " + classname + ": imageId1.trim().length() = " + imageId1.trim().length());
	    		this.debugOut("In " + classname + ": image1DataQueue.size() = " + image1DataQueue.size());
	    		this.debugOut("In " + classname + ": imageId2.trim().length() = " + imageId2.trim().length());
	    		this.debugOut("In " + classname + ": image2DataQueue.size() = " + image2DataQueue.size());
	    		asyncContext.dispatch("/ReenterInputDataNullForm.html");
	    		asyncContext.complete();
	    		return;
	    	} catch (Throwable t){
    				System.out.println("Exception in asyncContext.dispatch(/ReenterInputForm.html): " + t.getMessage());
    				t.printStackTrace();
    				return;
    		}
     	}

     	try {
     		this.debugOut("In " + classname + ": Before asyncContext.getRequest().setAttribute(): imageId1:" +imageId1);
     		asyncContext.getRequest().setAttribute("invocationMode", invocationMode);
     		asyncContext.getRequest().setAttribute("imageId1", imageId1);
        	asyncContext.getRequest().setAttribute("productImage1FileSizeBytes", productImage1FileSizeBytes);
     		asyncContext.getRequest().setAttribute("imageId2", imageId2);
        	asyncContext.getRequest().setAttribute("productImage2FileSizeBytes", productImage2FileSizeBytes);
      	} catch (Throwable t){
    		System.out.println("Exception in httpServletRequest.setAttribute() of imageId, imageDataQueue and/or productImageFileSizeBytes " + t.getMessage());
    		t.printStackTrace();
    		return;
    	}    	
     	
     	if (invocationMode.contentEquals("businessLogic_Servlet")){
     	this.debugOut("In " + classname + ": invocationMode.contentEquals(businessLogic_Servlet)");
     		try {
     			this.debugOut("In " + classname + ": Before asyncContext.getRequest().setAttribute(): imageDataQueue");
     			asyncContext.getRequest().setAttribute("image1DataQueue", image1DataQueue);
     			asyncContext.getRequest().setAttribute("image2DataQueue", image2DataQueue);
     		} catch (Throwable t){
     			System.out.println("Exception in httpServletRequest.setAttribute() of imageId, imageDataQueue and/or productImageFileSizeBytes " + t.getMessage());
     			t.printStackTrace();
     			return;
     		} 
     	}  
     	     	
     	if (invocationMode.contentEquals("businessLogic_Listeners")){ 
     	this.debugOut("In " + classname + ": invocationMode.contentEquals(businessLogic_Listeners)");
      		try {
     			image1Data = this.getImageData(image1DataQueue);      		
     		}catch (Throwable t){
     			System.out.println("Exception involving the imageData1Queue: " + t.getMessage());
     			t.printStackTrace();
     			return;
     		}
     		
     		try {
     			image2Data = this.getImageData(image2DataQueue);      		
     		}catch (Throwable t){
     			System.out.println("Exception involving the imageData2Queue: " + t.getMessage());
     			t.printStackTrace();
     			return;
     		}
     	
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
     		
     		try{
    		     imageJPA_was_set = this.setImageJPA(asyncContext, entityManager, imageId1, image1Data);
    		     if (imageJPA_was_set == false){
    		    	userTran.commit();
     				this.debugOut("In " + classname + ": Before asyncContext.dispatch(/ReenterInputImageIdAlreadyExistsForm.html: ImageJPA already exists.");
     				asyncContext.dispatch("/ReenterInputImageIdAlreadyExistsForm.html");
     				return;
    		     } 
    		}catch(Throwable t){
    			System.out.println("<<< Error: In " + classname + ": Error in this.setImageJPA(asyncContext, entityManager, imageId1, image1Data): " + t.getMessage());
    			t.printStackTrace();
    			return;
    		}
    		
    		try{
    			this.setImageJPA(asyncContext, entityManager, imageId2, image2Data);
   		    if (imageJPA_was_set == false){
   		    		userTran.commit();
    				this.debugOut("In " + classname + ": Before asyncContext.dispatch(/ReenterInputImageIdAlreadyExistsForm.html: ImageJPA already exists.");
    				asyncContext.dispatch("/ReenterInputImageIdAlreadyExistsForm.html");
    				return;
   		    } 
    		}catch(Throwable t){
    			System.out.println("<<< Error: In " + classname + ": Error in this.setImageJPA(asyncContext, entityManager, imageId2, image2Data): " + t.getMessage());
    			t.printStackTrace();
    			return;
    		}
 
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
 		}   	   	

     	if (invocationMode.contentEquals("businessLogic_Listeners") || invocationMode.contentEquals("businessLogic_Servlet")){ 
     		try {
     			asyncContext.dispatch("/CatalogControllerServlet");    		
     		} catch (Throwable t){
     			System.out.println("Exception in dispatch.forward(servletRequest, servletResponse): " + t.getMessage());
     			t.printStackTrace();
     			return;
     		}
     	}
    	
     	if (invocationMode.contentEquals("businessLogic_None")){    		
     		     		
        	try {
        		this.debugOut("In " + classname + ": Before response.getOutputStream()");
        		ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();
                
        		this.debugOut("In " + classname + ": Before new WriteListenerImpl(output, image1DataQueue, image2DataQueue, asyncContext) where " +
        				" image1DataQueue.size() = "+ image1DataQueue.size() + " and image2DataQueue.size() = " + image2DataQueue.size());
        		WriteListener writeListener = new WriteListenerImpl(servletOutputStream, 0, image1DataQueue, image2DataQueue, asyncContext);
                
        		this.debugOut("In " + classname + ": Before output.setWriteListener(writeListener)");
        		servletOutputStream.setWriteListener(writeListener);
        	}catch(Throwable t){
        		System.out.println("<<< Error: In " + classname + ": Error setting the WriteListener: " + t.getMessage());
        		t.printStackTrace();
        		return;
        	}
     		
     	}
    	
	}

	@Override
	public void onDataAvailable() throws IOException {
		this.debugOut("In " + classname + ": onDataAvailable()");
		
	    try {	
			byte[] byteArray = new byte[1024]; 
			int count = 0; 
			while (servletInputStream.isReady() && ((count = servletInputStream.read(byteArray)) >= 0)) { 
			this.debugOut("In " + classname + ": onDataAvailable(): This while loop can get invoked multiple times in async mode depending upon network traffic etc.");
				byteArrayOutputStream.write(byteArray, 0, count);	
				this.debugOut("In " + classname + ": byteArrayOutputStream.size() = " + byteArrayOutputStream.size());
			} 
		} catch (IOException ex) {
            System.out.println("<<< Exception in " + classname + ": onDataAvailable" + ex.getMessage());
            ex.printStackTrace();
		} catch (Throwable t) {
			System.out.println("<<< Exception in " + classname + ": onDataAvailable" + t.getMessage());
		    t.printStackTrace();
		}
	}

	@Override
	public void onError(Throwable t) {
		System.out.println("<<< Exception in " + classname + ": onError()" + t.getMessage());
        t.printStackTrace();
		asyncContext.complete();
	}
	
	public byte[] getImageData(Queue<byte[]> imageDataQueue) {
		byte[] imageData = null;
		
  		this.debugOut("In " + classname + ": imageDataQueue.size() = " + imageDataQueue.size());
 		while (imageDataQueue.peek() != null) {
 			this.debugOut("In " + classname + ": Within (imageDataQueue.peek() != null)");
 			imageData = imageDataQueue.poll();
 		}  
 		this.debugOut("In " + classname + ": After image1DataQueue.poll(): imageDataQueue.size() = " + imageDataQueue.size());         		

 		return imageData;
	}
	
	
	public boolean setImageJPA(AsyncContext asyncContext, EntityManager entityManager, String imageId, byte[] imageData) {
		ImageJPA imageJPA = null;
		boolean imageJPA_Set=false;
		
 		try	{			
 			imageJPA = entityManager.find(ImageJPA.class, imageId);  
 			this.debugOut("In " + classname + ": After entityManager.find(ImageJPA.class, imageId) where image = " + imageJPA);
 		} catch (Throwable t){ 
 			System.out.println("<<< Error: In " + classname + ": Error getting image: exception: "+t.getMessage());
 			t.printStackTrace();
 		}		

 		if (imageJPA == null){
 			try	{
 				imageJPA = new ImageJPA();
 				imageJPA.setImageId(imageId);	
 				this.debugOut("In " + classname + ": Before image.setImageData(imageData) where imageData.length = " + imageData.length);
 				imageJPA.setImageData(imageData);
 				entityManager.persist(imageJPA); 
 				imageJPA_Set=true;
 			} catch (Throwable t) {
 				System.out.println("<<< Error: In " + classname + ": Error setting/persisting the imageJPA instance: exception: " + t.getMessage());
 				t.printStackTrace();
 			}
 		} 
 		return imageJPA_Set;
	}
	
	public void debugOut(String message) {
		if (System.getProperty("DEBUG_OUT") != null) {
			if (System.getProperty("DEBUG_OUT").equals("ON") || System.getProperty("DEBUG_OUT").equals("SERVLET_NIO_ON")) {
				long tid = Thread.currentThread().getId();
				System.out.println("WebCatalog_thread_" + tid + ": " + message);
			}
		}
	}
}
