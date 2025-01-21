package image.serving.servlet.lite;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jakarta.annotation.security.DeclareRoles;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


/**
 * Servlet implementation class ImageServingServlet
 */
@DeclareRoles("AdministratorRole")
@RolesAllowed("AdministratorRole")
@WebServlet("/ImageServingServlet")
public class ImageServingServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	String classname = "ImageServingServlet";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ImageServingServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		this.debugOut("<<< In " + classname + ": Entering doGet method: request = " + request);
		this.debugOut("<<< In " + classname + ": Entering doGet method: response = " + response);
		this.debugOut("<<< In " + classname + ": Entering doGet method: request.getRequestURL() = " + request.getRequestURL());
		
		response.setContentType("image/jpeg");

		String tempdir = null;
		InputStream imageInputStream = null;
		OutputStream imageOutputStream = null;		
		String imageName = null;
		String imageNameWithPath = null;
		
		tempdir = request.getParameter("tempdir");

		if (tempdir != null){
			if (tempdir.contains("\\")){
				tempdir = tempdir + "\\";
			}else{
				tempdir = tempdir + "/";
			} 
		}

		this.debugOut("<<< In " + classname + ": Before getting request.getParameter(imageFileName)");
		try	{
			imageName = request.getParameter("imageFileName");
			this.debugOut("<<< In " + classname + ": After request.getParameter(imageFileName) = " +imageName);
			imageNameWithPath = tempdir + imageName;
		}catch (Exception e)	{ 
			System.out.println("<<< Error: In " + classname + ": Error getting request.getParameter(imageFileName): exception: "+e);
			e.printStackTrace();
			throw new ServletException("<<< Error: In " + classname + ": Error getting request.getParameter(imageFileName): exception: "+e);

		}			
		this.debugOut("<<< In " + classname + ": Image name: " + imageName);

		this.debugOut("<<< In " + classname + ": Before new BufferedInputStream( new FileInputStream(imageNameWithPath))");
		try	{
			imageInputStream = new BufferedInputStream( new FileInputStream(imageNameWithPath)); 				
		}catch (Exception e)	{ 
			System.out.println("<<< Error: In " + classname + ": Error getting imageInputStream: exception: "+e);
			e.printStackTrace();
			throw new ServletException("<<< Error: In " + classname + ": Error getting imageInputStream: exception: "+e);

		}

		this.debugOut("<<< In " + classname + ": Before response.getOutputStream()");
		try	{
			imageOutputStream = response.getOutputStream(); 
			this.debugOut("<<< In " + classname + ": imageOutputStream  = " + imageOutputStream);
		}catch (Exception e)	{ 
			System.out.println("<<< Error: In " + classname + ": Error response.getOutputStream(): exception: "+e);
			e.printStackTrace();
			if (imageInputStream != null) {
				imageInputStream.close();
			}
			throw new ServletException("<<< Error: In " + classname + ": Error response.getOutputStream(): exception: "+e);
		}

		this.debugOut("<<< In " + classname + " : Before copy the contents of the inputStream to the outputStream");
		try	{
			byte[] buf = new byte[1024]; 
			int count = 0; 
			while ((count = imageInputStream.read(buf)) >= 0) { 
				imageOutputStream.write(buf, 0, count);				
			}
		}catch (Exception e)	{ 
			System.out.println("<<< Error: In " + classname + ": Error copying contents of inputStream to outputStream : exception: "+e);
			e.printStackTrace();
			if (imageInputStream != null) {
				imageInputStream.close();
			}
			throw new ServletException("<<< Error: In " + classname + ": Error copying contents of inputStream to outputStream : exception: "+e);
		}
			
		if (imageInputStream != null) {
			imageInputStream.close();
		}

		if (imageOutputStream != null) {
			imageOutputStream.flush();
		}

		if (imageNameWithPath != null){
			try	{
				this.debugOut("<<< In " + classname + ": imageNameWithPath = " + imageNameWithPath);
				(new File(imageNameWithPath)).delete();
			}catch (Exception e)	{ 
				System.out.println("<<< Error: In " + classname + ": Error deleting temporary file: exception: "+e);
				e.printStackTrace();
				throw new ServletException("<<< Error: In " + classname + ": Error deleting temporary file: exception: "+e);
			}
		} 
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	public void debugOut(String message) {
		if (System.getProperty("DEBUG_OUT") != null) {
			if (System.getProperty("DEBUG_OUT").equals("ON")) {
				long tid = Thread.currentThread().getId();
				System.out.println(classname + "_thread_" + tid + ": " + message);
			}
		}
	}	

}
