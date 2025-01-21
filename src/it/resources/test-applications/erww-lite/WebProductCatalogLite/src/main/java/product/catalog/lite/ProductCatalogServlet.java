package product.catalog.lite;

import java.io.IOException;
import java.util.regex.Pattern;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


/**
 * Servlet implementation class ProductCatalogServlet
 */
@WebServlet(name="/ProductCatalogServlet", value = "/uploadProduct/*")
public class ProductCatalogServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private String className = (ProductCatalogServlet.class).getName();
	private Pattern uploadProductPattern = Pattern.compile("/WebProductCatalogLite/uploadProduct");
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ProductCatalogServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.debugOut("Entered " + className + " doPost method");
		
		String uri = request.getRequestURI();
		this.debugOut("<<< " + className + ": uri =  " + uri);
		if(uploadProductPattern.matcher(uri).matches()){	
			this.debugOut("<<< " + className + ": uploadProductPattern path ");
				
			response.setStatus(101);
			response.setHeader("Upgrade", "NIOReadListener");
			response.setHeader("Connection","Upgrade");
			this.debugOut("Upgraded to use NIOReadListenerHttpUpgradeHandler");
			//Comment out unused variable
			//NIOReadListenerHttpUpgradeHandler upgradeHandler = request.upgrade(NIOReadListenerHttpUpgradeHandler.class);
		}
	}
	
	public void debugOut(String message) {
		if (System.getProperty("DEBUG_OUT") != null) {
			if (System.getProperty("DEBUG_OUT").equals("ON")) {
				long tid = Thread.currentThread().getId();
				System.out.println("WebProductCatalog_thread_" + tid + ": " + message);
			}
		}
	}

}
