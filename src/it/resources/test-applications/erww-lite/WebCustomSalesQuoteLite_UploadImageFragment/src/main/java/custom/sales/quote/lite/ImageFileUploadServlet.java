package custom.sales.quote.lite;


import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.HttpConstraint;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.ServletSecurity;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import jakarta.transaction.UserTransaction;

import javax.naming.InitialContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;

import imagejpa.ImageJPA;
import irwwbase.IRWWBase;

/**
 * Servlet implementation class ImageFileUploadServlet
 */
@ServletSecurity(value=@HttpConstraint(
		rolesAllowed = {"CustomerRole"},transportGuarantee=ServletSecurity.TransportGuarantee.CONFIDENTIAL)
)
@MultipartConfig(fileSizeThreshold=1000000, maxFileSize=15000000, maxRequestSize=30000000)
public class ImageFileUploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	String classname = "ImageFileUploadServlet";

	@PersistenceUnit(unitName = "ImageFileUpload")
	EntityManagerFactory emf = null;

	IRWWBase irwwbase = new IRWWBase();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ImageFileUploadServlet() {
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

		EntityManager em = null;

		Part imageIdPart = null;
		String imageId = null;
		ImageJPA image = new ImageJPA();

		Part imagePart = null;
		InputStream imagePartInputStream = null;
		byte [] imagePartByteArray = null;

		InitialContext initialContext = null;
		UserTransaction userTran = null;

		PrintWriter out = response.getWriter();
		response.setContentType("text/html");

		out.println( "<html>");
		out.println( "<head><title>Upload Image to the IMAGE Table</title></head> ");
		out.println( "<body bgcolor=\"#f8f7cd\">");
		out.println( "<h1>Upload Image to the IMAGE Table</h1>");
		out.println( "<TABLE border=\"8\" bgcolor=\"#cccccc\" bordercolor=\"#FFCC99\" >"); 

		try{
			em = emf.createEntityManager();
			initialContext = new InitialContext();
			userTran = (UserTransaction) initialContext.lookup("java:comp/UserTransaction");
			userTran.begin();
			em.joinTransaction();
		}catch(Exception e){
			System.out.println("<<< Error: In " + classname + ": Error Begin transaction - Exception: " + e);
			out.println("<tr><td>Status: Failed: " + e.getMessage() + "</td></tr>");
			e.printStackTrace();
			out.println("<tr><td>Stack: " + irwwbase.getStackTrace(e) + "</td></tr>");
			throw new ServletException("<<< Error: In " + classname + ": Error Begin transaction - Exception: " + e);
		}

		try	{
			imageIdPart = request.getPart("imageId");			
		}catch (Exception e)	{ 
			System.out.println("<<< Error: In " + classname + ": Error getting imageIdPart: exception: "+e);
			out.println("<tr><td>Status: Failed: " + e.getMessage() + "</td></tr>");
			e.printStackTrace();
			out.println("<tr><td>Stack:  " + irwwbase.getStackTrace(e) + "</td></tr>");
			throw new ServletException("<<< Error: In " + classname + ": Error getting imageIdPart: exception: "+e);
		}

		if (imageIdPart.getSize()!=0){
			imageId = request.getParameter(imageIdPart.getName()).trim();
		}else{
			System.out.println("<<< Error: In " + classname + ": Image Id is empty");
			out.println("<tr><td>Status: Failed: Image Id is empty</td></tr>");
			throw new ServletException("<<< Error: In " + classname + ": Image Id is empty");
		}

		try	{
			imagePart = request.getPart("image");			
		}catch (Exception e)	{ 
			System.out.println("<<< Error: In " + classname + ": Error getting imagePart: exception: "+e);
			out.println("<tr><td>Status: Failed: " + e.getMessage() + "</td></tr>");
			e.printStackTrace();
			out.println("<tr><td>Stack: " + irwwbase.getStackTrace(e) + "</td></tr>");
			throw new ServletException("<<< Error: In " + classname + ": Error getting imagePart: exception: "+e);
		}

		try	{
			if (imagePart.getSize()!= 0){			
				imagePartInputStream = imagePart.getInputStream();
				imagePartByteArray = new byte[((int)imagePart.getSize())];  
				imagePartInputStream.read(imagePartByteArray);	
				imagePartInputStream.close(); 	
			}else{
				System.out.println("<<< Error: In " + classname + ": Image is empty");
				out.println("<tr><td>Status: Failed: Image is empty</td></tr>");
				throw new ServletException("<<< Error: In " + classname + ": Image is empty");
			}
		}catch (Exception e)	{ 
			System.out.println("<<< Error: In " + classname + ": Error converting imagePart to a byte array: exception: "+e);
			out.println("<tr><td>Status: Failed: " + e.getMessage() + "</td></tr>");
			e.printStackTrace();
			out.println("<tr><td>Stack: " + irwwbase.getStackTrace(e) + "</td></tr>");
			throw new ServletException("<<< Error: In " + classname + ": Error converting imagePart to a byte array: exception: "+e);
		}		

		// Set input parameters for calling GetImageServlet		
		request.setAttribute("ImageId", imageId);
		request.setAttribute("OutputWriter", out);

		// Invoke the GetImageServlet now, but use "include" dispatch so we continue back here afterwards.
		RequestDispatcher dispatch = request.getRequestDispatcher("/GetImageServlet");
		dispatch.include(request,response);

		// Get the JPA image object which is returned from the servlet dispatch as an attribute in the request object
		image = (ImageJPA)request.getAttribute("JPAImage");

		if (image != null){
			out.println("<tr><td>The image was NOT uploaded to the IMAGE table!<tr><td>");	
			out.println("<tr><td>Image ID: " + imageId + " already exists in the IMAGE table!  Please choose another Image Id!<tr><td>");	
			out.println("<td><tr>Status: Failed:</td></tr>");
		}

		if ((imageId != null) && (image == null)){	
			this.createImageInstance(em, imageId, imagePartByteArray, out);
			out.println("<tr><td>Image ID: " + imageId + " was uploaded and inserted into the IMAGE table<tr><td>");	
		}

		try {
			userTran.commit();
			out.println("<tr><td>Status: Successful</td></tr>");
		}catch(Exception e){	
			System.out.println("<<< Error: In " + classname + ": Commit Transaction - Exception: " + e);
			out.println("<tr><td>Status: Failed: " + e.getMessage() + "</td></tr>");
			e.printStackTrace();
			out.println("<tr><td>Stack: " + irwwbase.getStackTrace(e) + "</td></tr>");
			throw new ServletException("<<< Error: In " + classname + ": Error Commit Transaction: exception: "+e);
		} 

		try {
			if (em!=null && em.isOpen()){
				em.close();
			}
		}catch(Exception e){
			System.out.println("<<< Error: In " + classname + ": Error em.close() - Exception: " + e);
			out.println("<tr><td>Status: Failed: " + e.getMessage() + "</td></tr>");
			e.printStackTrace();
			out.println("<tr><td>Stack: " + irwwbase.getStackTrace(e) + "</td></tr>");
			throw new ServletException("<<< Error: In " + classname + " Error em.close(): exception: "+e);
		}

		out.println( "</TABLE>");        
		out.println( "</body>");
		out.println( "</html>"); 
	}


	protected void createImageInstance(EntityManager em, String imageId, byte[] imagePartByteArray, PrintWriter out)throws ServletException, IOException {

		ImageJPA image = new ImageJPA();

		try	{
			image.setImageId(imageId);
			image.setImageData(imagePartByteArray);

			em.persist(image);
		}catch (Exception e)	{ 
			System.out.println("<<< Error: In " + classname + ": Error setting/persisting image: exception: "+e);
			out.println("<tr><td>Status: Failed: " + e.getMessage() + "</td></tr>");
			e.printStackTrace();
			out.println("<tr><td>Stack: " + irwwbase.getStackTrace(e) + "</td></tr>");
			throw new ServletException("<<< Error: In " + classname + ": Error setting/persisting image: exception: "+e);
		}		
	}
}
