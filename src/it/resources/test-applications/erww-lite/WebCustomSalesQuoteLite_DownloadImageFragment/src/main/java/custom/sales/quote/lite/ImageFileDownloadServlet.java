package custom.sales.quote.lite;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.annotation.security.DeclareRoles;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import jakarta.transaction.UserTransaction;
import javax.imageio.ImageIO;
import javax.naming.InitialContext;

import java.awt.image.BufferedImage;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import imagejpa.ImageJPA;
import irwwbase.ExtendedRandom;
import irwwbase.IRWWBase;

/**
 * Servlet implementation class ImageFileUploadServlet
 */
//Declare Roles is necessary until the spec addresses the 'user map to role' for dynamic servlet
@DeclareRoles("RunAsAdministratorRole")
@MultipartConfig(fileSizeThreshold=1000000, maxFileSize=15000000, maxRequestSize=30000000)
public class ImageFileDownloadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	String classname = "ImageFileDownloadServlet";

	EntityManagerFactory emf = null;

	IRWWBase irwwbase = new IRWWBase();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ImageFileDownloadServlet() {
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
		byte [] imageByteArray = null;
		BufferedImage bufferedImage = null;

		File outputImage = null;
		String path = null;
		ServletContext servletContext = null;

		String imageTempFile = null;

		ImageJPA image = new ImageJPA();

		InitialContext initialContext = null;
		UserTransaction userTran = null;

		PrintWriter out = response.getWriter();
		response.setContentType("text/html");

		servletContext = this.getServletContext();

		out.println( "<html>");
		out.println( "<head><title>Download Image from the IMAGE Table</title></head> ");
		out.println( "<body>");
		out.println( "<body bgcolor=\"#f8f7cd\">");
		out.println( "<h1>Download Image from the IMAGE Table</h1>");
		out.println( "<TABLE border=\"8\" bgcolor=\"#cccccc\" bordercolor=\"#FFCC99\" >"); 

		try{

			// obtain the initial JNDI context and EM factory
			initialContext = new InitialContext();
			EntityManagerFactory emf = (EntityManagerFactory)
			initialContext.lookup("java:comp/env/persistence/ImageFileDownload");

			// now create the EM from the EM factory
			em = emf.createEntityManager();

			// lookup user transaction, begin a global tx, and then tell EM to join it
			initialContext = new InitialContext();
			userTran = (UserTransaction) initialContext.lookup("java:comp/UserTransaction");
			userTran.begin();
			em.joinTransaction();

		} catch(Exception e) {
			System.out.println("<<< Error: In " + classname + ": Exception: " + e);
			out.println("<tr><td>Status: Failed" + " " + e.getMessage() + "</td></tr>");
			e.printStackTrace();
			out.println("<tr><td>Stack: " + irwwbase.getStackTrace(e) + "</td></tr>");
			throw new ServletException("<<< Error: In " + classname + ": Exception: "+e);
		}

		try	{

			imageIdPart = request.getPart("imageId");

		} catch (Exception e) { 
			System.out.println("<<< Error: In " + classname + ": Error getting imageIdPart: exception: "+e);
			out.println("<tr><td>Status: Failed: " + e.getMessage() + "</td></tr>");
			e.printStackTrace();
			out.println("<tr><td>Stack:  " + irwwbase.getStackTrace(e) + "</td></tr>");
			throw new ServletException("<<< Error: In " + classname + ": Error getting imageIdPart: exception: "+e);
		}

		if (imageIdPart.getSize()!=0){
			imageId = request.getParameter(imageIdPart.getName()).trim();
		} else {
			System.out.println("<<< Error: In " + classname + ": Image Id is empty");
			out.println("<tr><td>Status: Failed: Image Id is empty</td></tr>");
			throw new ServletException("<<< Error: In " + classname + ": Image Id is empty");
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
			imageByteArray = image.getImageData();
		} else {
			out.println("<tr><td> Error: imageId: " + imageId + " was not found <tr><td>");
			out.println("<tr><td>Status: Failed: Error: imageId: " + imageId + " was not found <tr><td>");
			throw new ServletException("<<< Error: In " + classname + " Error: imageId: " + imageId + " was not found <tr><td>");
		}

		try{
			if (imageByteArray != null){
				bufferedImage = ImageIO.read(new ByteArrayInputStream(imageByteArray));
			} else {
				out.println("<tr><td> Error: " + imageByteArray + " is null <tr><td>");
				out.println("<tr><td>Status: Failed: " +  imageByteArray + " is null <tr><td>");
				throw new ServletException("<<< Error: In " + classname + " Error: " + imageByteArray + " is null <tr><td>");
			}
		} catch (Exception e) { 
			System.out.println("<<< Error: In " + classname + ": Error getting bufferedImage: exception: "+e);
			out.println("<tr><td>Status: Failed: " + e.getMessage() + "</td></tr>");
			e.printStackTrace();
			out.println("<tr><td>Stack: " + irwwbase.getStackTrace(e) + "</td></tr>");
			throw new ServletException("<<< Error: In " + classname + ": Error getting bufferedImage: exception: "+e);
		}

		ExtendedRandom rand = new ExtendedRandom();
		rand.nextInt(0, 10);		
		imageTempFile = imageId + rand.nextInt(100000000, 999999999);
		
		try{
			File tempFile = (File)servletContext.getAttribute(ServletContext.TEMPDIR);
            path = tempFile.getPath();
            String finalPath = null;
    		if (path.contains("\\")){
				finalPath = path + "\\";
			} else {
				finalPath = path + "/";
			}
            		
			outputImage = new File(finalPath + imageTempFile + ".jpg");
			out.println("<tr><td>To save the image to your local file system:<tr><td>");
			out.println("<tr><td>Right click on the image and select \"Save Image As\".<tr><td>");
			

		} catch (Exception e) { 
			System.out.println(" Error creating outputImage: exception: "+e);
			out.println("<tr><td>Status: Failed: " + e.getMessage() + "</td></tr>");
			e.printStackTrace();
			out.println("<tr><td>Stack: " + irwwbase.getStackTrace(e) + "</td></tr>");
			throw new ServletException("<<< Error: In " + classname + ": Error creating outputImage: exception: "+e);
		}

		try{
			ImageIO.write(bufferedImage, "jpg", outputImage);				
		} catch (Exception e) { 
			System.out.println("<<< Error: In " + classname + ": Error writing image using ImageIO: exception: "+e);
			out.println("<tr><td>Status: Failed: " + e.getMessage() + "</td></tr>");
			e.printStackTrace();
			out.println("<tr><td>Stack: " + irwwbase.getStackTrace(e) + "</td></tr>");
			throw new ServletException("<<< Error: In " + classname + ": Error writing image using ImageIO: exception: "+e);
		}

		if (outputImage != null){
			out.println("Rename the Image Name to " + imageId + ".jpg");
			out.println("<p><img src=\"ImageServingServlet?imageFileName=" + imageTempFile + ".jpg" +  "&tempdir=" + path + "\" width=\"400\" height=\"300\" /></p>");
		}
		/*Comment out dead code
		else {
			out.println("<tr><td> Error: " + outputImage + " is null <tr><td>");
			out.println("<tr><td>Status: Failed: " + outputImage + " is null <tr><td>");
			throw new ServletException("<<< Error: In " + classname + ": Error " + outputImage + " is null");
		}
		*/
		try {
			userTran.commit();
			out.println("<tr><td>Status: Successful</td></tr>");
		} catch(Exception e) {	
			System.out.println("<<< Error: In " + classname + ": Error Commit Transaction - Exception: " + e);
			out.println("<tr><td>Status: Failed: " + e.getMessage() + "</td></tr>");
			e.printStackTrace();
			out.println("<tr><td>Stack: " + irwwbase.getStackTrace(e) + "</td></tr>");	
			throw new ServletException("<<< Error: In " + classname + ": Error Commit Transaction - Exception: " + e);
		} 

		try {
			if (bufferedImage!=null){
				bufferedImage.flush();
			}
		} catch(Exception e) {
			System.out.println("<<< Error: In " + classname + ": Error bufferedImage.flush() - Exception: " + e);
			out.println("<tr><td>Status: Failed: " + e.getMessage() + "</td></tr>");
			e.printStackTrace();
			out.println("<tr><td>Stack: " + irwwbase.getStackTrace(e) + "</td></tr>");
			throw new ServletException("<<< Error: In " + classname + ": Error bufferedImage.flush() - Exception: " + e);
		} 

		try {
			if (em!=null && em.isOpen()){
				em.close();
			}
		} catch(Exception e) {
			System.out.println("<<< Error: In " + classname + ": Error em.close() - Exception: " + e);
			out.println("<tr><td>Status: Failed: " + e.getMessage() + "</td></tr>");
			e.printStackTrace();
			out.println("<tr><td>Stack: " + irwwbase.getStackTrace(e) + "</td></tr>");
			throw new ServletException("<<< Error: In " + classname + ": Error em.close() - Exception: " + e);
		}

		out.println( "</TABLE>");        
		out.println( "</body>");
		out.println( "</html>"); 

	} // end doPost

}
