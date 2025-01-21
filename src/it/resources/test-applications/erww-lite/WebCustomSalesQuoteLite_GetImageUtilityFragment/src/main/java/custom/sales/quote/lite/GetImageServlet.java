package custom.sales.quote.lite;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.annotation.security.DeclareRoles;
import jakarta.annotation.security.RolesAllowed;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import imagejpa.ImageJPA;
import irwwbase.IRWWBase;

/**
 * Servlet implementation class GetImageServlet
 */
@DeclareRoles("AdministratorRole")
@RolesAllowed("AdministratorRole")
@WebServlet("/GetImageServlet")
public class GetImageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	String classname = "GetImageServlet";

	// Directly injecting the Entity Manager rather than using an Entity Manager Factory
	// here.  That is ok because this servlet is only going to read data. 
	@PersistenceContext(unitName = "GetImage")
	EntityManager em;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetImageServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// Get parameters passed into this servlet on the request object
		String imageId = (String)request.getAttribute("ImageId");
		PrintWriter out = (PrintWriter)request.getAttribute("OutputWriter");

		IRWWBase irwwbase = new IRWWBase();

		ImageJPA image = new ImageJPA();

		try	{			
			image = em.find(ImageJPA.class, imageId);

			// If JPA image was located, set it into the request object so that we
			// return it to the caller (ie. code that dispatched this servlet).
			request.setAttribute("JPAImage", image);

		} catch (Exception e) { 
			System.out.println("<<< Error: In " + classname + ": Error getting image: exception: "+e);
			out.println("<tr><td>Status: Failed: " + e.getMessage() + "</td></tr>");
			e.printStackTrace();
			out.println("<tr><td>Stack " + irwwbase.getStackTrace(e) + "</td></tr>");
			throw new ServletException("<<< Error: In " + classname + ": Error getting image: exception: "+e);
		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
