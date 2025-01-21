package catalog.update.lite;


import imagejpa.ImageJPA;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import jakarta.annotation.security.DeclareRoles;
import javax.naming.InitialContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.transaction.UserTransaction;

//Comment out unused import
//import org.jboss.weld.context.beanstore.http.RequestBeanStore;


/**
 * Servlet implementation class CatalogControllerServlet
 */
@DeclareRoles("RunAsAdministratorRole")
@WebServlet(name="/CatalogControllerServlet",urlPatterns = {"/CatalogControllerServlet"}, asyncSupported=true)
public class CatalogControllerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String classname = "CatalogControllerServlet";
	
	@PersistenceUnit(unitName = "WebCatalogUpdateLite")
	EntityManagerFactory entityManagerFactory = null;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CatalogControllerServlet() {
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
		Queue<byte[]> image1DataQueue = null;
		Queue<byte[]> image2DataQueue = null;
		Queue<byte[]> output1Queue = null;
		Queue<byte[]> output2Queue = null;
		byte [] image1Data = null;
		byte [] image2Data = null;
		EntityManager entityManager = null;
		InitialContext initialContext = null;
		UserTransaction userTran = null;
		String invocationMode = null;
		String imageId1 = null;
		String imageId2 = null;	
		boolean imageJPA_was_set = false;


       	try{
       		this.debugOut("In " + classname + ": Before request.startAsync(request, response): Puts the request into asynchronous mode and ensures that the response is not committed after exiting the doPost method");
       		asyncContext = request.startAsync(request, response);
       		this.debugOut("In " + classname + ": Before asyncContext.setTimeout(300000): 5 minutes");
       		asyncContext.setTimeout(300000); 
        }catch (Throwable t){
    		System.out.println("Exception in request.startAsync(request, response): " + t.getMessage());
    		t.printStackTrace();
    		return;
    	} 
		
       	try{
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
        	
        	try{
        		invocationMode = (String)request.getAttribute("invocationMode");
        		this.debugOut("In " + classname + ": invocationMode = " + invocationMode);
            }catch (Throwable t){
        		System.out.println("Exception in (String)request.getAttribute(invocationMode): " + t.getMessage());
        		t.printStackTrace();
        		return;
        	}
        	
         	if (invocationMode.contentEquals("businessLogic_Servlet")){        	
         		try{
         			imageId1 = (String)request.getAttribute("imageId1");
         			this.debugOut("In " + classname + ": imageId1 = " + imageId1);
         			
         			imageId2 = (String)request.getAttribute("imageId2");
         			this.debugOut("In " + classname + ": imageId2 = " + imageId2);
         			
         		}catch (Throwable t){
         			System.out.println("Exception in (String)request.getAttribute(imageId): " + t.getMessage());
         			t.printStackTrace();
         			return;
         		}
        	        	
         		try {
         			image1DataQueue = ((Queue<byte[]>)request.getAttribute("image1DataQueue"));
         			this.debugOut("In " + classname + ": imageData1Queue.size() = " + image1DataQueue.size());
         			image1Data = this.getImageData(image1DataQueue);       		
         		}catch (Throwable t){
         			System.out.println("Exception involving the image1DataQueue: " + t.getMessage());
         			t.printStackTrace();
         			return;
         		}
         		
         		try {
         			image2DataQueue = ((Queue<byte[]>)request.getAttribute("image2DataQueue"));
         			this.debugOut("In " + classname + ": image2DataQueue.size() = " + image2DataQueue.size());
         			image2Data = this.getImageData(image2DataQueue);        		
         		}catch (Throwable t){
         			System.out.println("Exception involving the image2DataQueue: " + t.getMessage());
         			t.printStackTrace();
         			return;
         		}
  	                	
         		try	{
         			entityManager = entityManagerFactory.createEntityManager();
         		}catch (Throwable t){ 
         			System.out.println("<<< Error: In " + classname + ": Error getting the entityManager: "+ t.getMessage());
         			t.printStackTrace();
         			return;
         		}
    		
         		try{
         			initialContext = new InitialContext();
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
            
         		try {
         			output1Queue = new LinkedBlockingQueue<byte[]>();
         			output1Queue.add(image1Data);
         		}catch(Throwable t){	
         			System.out.println("<<< Error: In " + classname + ": outputQueue.add(imageData): " + t.getMessage());
         			t.printStackTrace();
         			return;
         		}
         		
         		try {
         			output2Queue = new LinkedBlockingQueue<byte[]>();
         			output2Queue.add(image2Data);
         		}catch(Throwable t){	
         			System.out.println("<<< Error: In " + classname + ": outputQueue.add(imageData): " + t.getMessage());
         			t.printStackTrace();
         			return;
         		}
         		
         	}
        		
        	try {
        		ServletOutputStream servletOutputStream = null;
        		WriteListener writeListener = null;
        		
        		this.debugOut("In " + classname + ": Before response.getOutputStream()");
        		servletOutputStream = response.getOutputStream();
                
        		this.debugOut("In " + classname + ": Before new WriteListenerImpl(output, output1Queue, output2Queue, asyncContext)");
       			writeListener = new WriteListenerImpl(servletOutputStream, 0, output1Queue, output2Queue, asyncContext);
                
        		this.debugOut("In " + classname + ": Before output.setWriteListener(writeListener)");
        		servletOutputStream.setWriteListener(writeListener);
        	}catch(Throwable t){
        		System.out.println("<<< Error: In " + classname + ": Error setting the WriteListener: " + t.getMessage());
        		t.printStackTrace();
        		return;
        	}
         }                   
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
