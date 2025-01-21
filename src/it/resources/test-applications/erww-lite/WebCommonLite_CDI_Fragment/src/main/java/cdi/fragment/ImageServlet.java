package cdi.fragment;

import imagejpa.ImageJPA;

import java.io.BufferedOutputStream;
import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;

import irwwbase.IRWWBase;


/**
 * Servlet implementation class ApplicationDesignChartServlet
 */
@WebServlet("/ImageServlet")
public class ImageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@PersistenceUnit(unitName = "GetImage")
	EntityManagerFactory emf;
	
	String classname = "ImageServlet";

    private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ImageServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
    	IRWWBase irwwbase = new IRWWBase();
    	
    	irwwbase.debugOut("<<< " + classname + " Entered"); 
    	
		EntityManager em = null;
		em = emf.createEntityManager();
    	
        BufferedOutputStream output = null;
    		
		String imageId = "Small_Image2";
		ImageJPA image = new ImageJPA();
		
		imageId = request.getParameter("id");
    	
    	irwwbase.debugOut("<<< imageId = " + imageId);  

		try	{
			image = em.find(ImageJPA.class, imageId);
		} catch(Exception e) {
			System.out.println("<<< Error: In " + classname + ": Exception: " + e);
			e.printStackTrace();
			throw new ServletException("<<< Error: In " + classname + 
					": Error Message: " + e.getMessage()	+ ": Exception Cause: " + e.getCause());
		}

        // Initialize servlet response.
        response.reset();
        response.setBufferSize(DEFAULT_BUFFER_SIZE);

        try {
            // Open streams.
            output = new BufferedOutputStream(response.getOutputStream(), DEFAULT_BUFFER_SIZE);

            // Write file contents to response.
            output.write(image.getImageData());
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("<<< Error: In " + classname + 
            		": Error Message: " + e.getMessage()	+ ": Exception Cause: " + e.getCause());

        }   
            
        finally {
        	
        	try {
                if (em!=null && em.isOpen()){
                	em.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new ServletException("<<< Error: In " + classname + 
                		": Error Message: " + e.getMessage()	+ ": Exception Cause: " + e.getCause());
            }
        }
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);
	}

}
