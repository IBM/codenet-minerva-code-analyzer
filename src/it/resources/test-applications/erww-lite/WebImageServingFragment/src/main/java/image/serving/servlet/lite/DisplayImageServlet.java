package image.serving.servlet.lite;

import irwwbase.IRWWBase;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


/**
 * Servlet implementation class DisplayImageServlet
 */
@WebServlet("/DisplayImageServlet")
public class DisplayImageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	String classname = "DisplayImageServlet";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DisplayImageServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		IRWWBase irwwbase = new IRWWBase();

		irwwbase.debugOut("<<< In " + classname + ": Entering the doGet method");
		
		response.setContentType("image/jpeg");
		
		String imageNameWithPath = request.getParameter("imageNameWithPath");
		
		try{ 	 		   	
	    	 this.getServletContext().getRequestDispatcher(imageNameWithPath).include(request, response);
	    }
		catch (Throwable e) {
			System.out.println("<<< Error: In " + classname + ": Error: this.getServletContext().getRequestDispatcher(imageNameWithPath).include(request, response): exception: "+e);
			e.printStackTrace();
			throw new ServletException("<<< Error: In " + classname + ": Error: this.getServletContext().getRequestDispatcher(imageNameWithPath).include(request, response): exception:: "+e);		
		} 
		
		irwwbase.debugOut("<<< In " + classname + ": Exiting the doGet method");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
