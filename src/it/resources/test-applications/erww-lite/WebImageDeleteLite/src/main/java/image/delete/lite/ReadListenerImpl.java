package image.delete.lite;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import imagejpa.ImageJPA;

import javax.naming.InitialContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
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
        byte[] byteArrayBoundary = null;
        int index = 0;
        MultipartStream multipartStream = null;
        
		EntityManager entityManager = null;
		InitialContext initialContext = null;
		UserTransaction userTran = null;
		Query imageQuery = null;
		int imagesDeletedFlag = 0;
		
		Collection<?> collection = null;
		ImageJPA imageJPA = null;
		ArrayList<String> arrayListImageIdsDeleted = new ArrayList<String>();

        try {
        	entireInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());        	
        	contentType = httpServletRequest.getContentType();
        	this.debugOut("In " + classname + ": httpServletRequest.getContentType(): " + httpServletRequest.getContentType());		
        	index = contentType.indexOf("boundary=");
        	boundary = contentType.substring(index + 9); 
        	this.debugOut("In " + classname + ": boundary: " + boundary);	
        	byteArrayBoundary = boundary.getBytes();        	
        }catch (Throwable t){
			System.out.println("Exception in getting entireInputStream or getting the byteArrayBoundary : " + t.getMessage());
			t.printStackTrace();
			return;
        }
        
        try {
        	multipartStream = new MultipartStream(entireInputStream, byteArrayBoundary);
        }catch(Throwable t){
			System.out.println("Exception in getting multipartStream : " + t.getMessage());
			t.printStackTrace();
			return;
        }
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String imageIdsContainValue1 = null;
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
        		
        		if (header.contains("name=\"imageIdsContainValue1\"")){
        			imageIdsContainValue1 = baos.toString();
        			this.debugOut("In " + classname + ": imageIdsContainValue1:" + imageIdsContainValue1);
        		}
        		
        		baos.reset();                        
        		nextPart = multipartStream.readBoundary(); 
        	}
        	
        }catch (Throwable t){
    		System.out.println("Exception in while (nextPart): " + t.getMessage());
    		t.printStackTrace();
    		return;
        }
        this.debugOut("----------------------------------------");  
        
		if ((imageIdsContainValue1.trim().length() == 0)){
    		try {	
	    		this.debugOut("In " + classname + ": Before asyncContext.dispatch(/ReenterInputDataNullForm.html: imageIdsContainValue1 is null.");
	    		this.debugOut("In " + classname + ": imageIdsContainValue1.trim().length() = " + imageIdsContainValue1.trim().length());
	    		asyncContext.dispatch("/ReenterInputDataNullForm.html");
	    		asyncContext.complete();
	    		return;
	    	} catch (Throwable t){
    				System.out.println("Exception in asyncContext.dispatch(/ReenterInputForm.html): " + t.getMessage());
    				t.printStackTrace();
    				return;
    		}
     	}
 		try{
 			initialContext = new InitialContext();
 		}catch(Throwable t){
 			System.out.println("<<< Error: In " + classname + ": Error newing up InitialContect(): " + t.getMessage());
 			t.printStackTrace();
 			return;
 		}
 	
 		try	{
 			entityManagerFactory = (EntityManagerFactory)initialContext.lookup("java:comp/env/persistence/WebImageDeleteLite"); 
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
 			imageQuery = entityManager.createNamedQuery("selectImageIdsContainValue");  
 			imageQuery.setParameter("imageIdsContainValue1", "%"+ imageIdsContainValue1.trim()+"%");

			int cnt = 0;
			collection = imageQuery.getResultList();
			if (collection != null) {
				Iterator<?> it = collection.iterator();

				while (it.hasNext()) {
					cnt++;
					imageJPA = (ImageJPA) it.next();
					debugOut("In " + classname + ": selectImageIdsContainValue:" + cnt + ": imageId =  " + imageJPA.getImageId());					
					arrayListImageIdsDeleted.add(imageJPA.getImageId());
				}			
			}
 		}catch(Throwable t){	
 			System.out.println("<<< Error: In " + classname + ": Query to delete Images: " + t.getMessage());
 			t.printStackTrace();
 			return;
 		}
 		
 		try{
 			imageQuery = entityManager.createNamedQuery("deleteImageIdsContainValue");  
 			imageQuery.setParameter("imageIdsContainValue1", "%"+ imageIdsContainValue1.trim()+"%");
 			imagesDeletedFlag = imageQuery.executeUpdate();
 			this.debugOut("In " + classname + ": imagesDeletedFlag " + imagesDeletedFlag);  			
 		}catch(Throwable t){	
 			System.out.println("<<< Error: In " + classname + ": Query to delete Images: " + t.getMessage());
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
 		
 		try {
    		this.debugOut("In " + classname + ": Before response.getOutputStream()");
    		ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();

    		this.debugOut("In " + classname + ": Before new WriteListenerImpl(output, imagesDeletedFlag, asyncContext) where " +
    				" imagesDeletedFlag, = "+ imagesDeletedFlag);
    		
    		WriteListener writeListener = new WriteListenerImpl(servletOutputStream, 0, imagesDeletedFlag, arrayListImageIdsDeleted, asyncContext);
   
    		this.debugOut("In " + classname + ": Before output.setWriteListener(writeListener)");
    		servletOutputStream.setWriteListener(writeListener);
    		
    	}catch(Throwable t){
    		System.out.println("<<< Error: In " + classname + ": Error setting the WriteListener: " + t.getMessage());
    		t.printStackTrace();
    		return;
    	}
	}

	@Override
	public void onDataAvailable() throws IOException {
		this.debugOut("In " + classname + ": onDataAvailable()");
		
		try {
			byte[] byteArray = new byte[1024];
			int count = 0;
			while (servletInputStream.isReady() && ((count = servletInputStream.read(byteArray)) >=0 )){
				this.debugOut("In " + classname + ": onDataAvailable(): This while loop can get invoked multiple times in async mode depending upon network traffic etc.");
					byteArrayOutputStream.write(byteArray, 0, count);
					this.debugOut("In " + classname + ": byteArrayOutputStream.size() = " + byteArrayOutputStream.size());
			}
		}catch (IOException ex) {
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
	
	public void debugOut(String message) {
		if (System.getProperty("DEBUG_OUT") != null) {
			if (System.getProperty("DEBUG_OUT").equals("ON") || System.getProperty("DEBUG_OUT").equals("SERVLET_NIO_ON")) {
				long tid = Thread.currentThread().getId();
				System.out.println("WebImageDelete_thread_" + tid + ": " + message);
			}
		}
	}

}
