package custom.sales.quote.lite;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;

import jakarta.annotation.security.DeclareRoles;
import javax.naming.InitialContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import jakarta.transaction.UserTransaction;

import itemjpa.ItemJPA;
import irwwbase.ExtendedRandom;
import irwwbase.IRWWBase;
import irwwbase.MaxValues;

/**
 * Servlet implementation class CustomSalesQuoteServlet
 */
//Declare Roles is necessary until the spec addresses the 'user map to role' for dynamic servlet
@DeclareRoles("RunAsAdministratorRole")
@MultipartConfig(fileSizeThreshold=1000000, maxFileSize=15000000, maxRequestSize=30000000)
public class CustomSalesQuoteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	String classname = "CustomSalesQuoteServlet";

	@PersistenceUnit(unitName = "CustomSalesQuote")
	EntityManagerFactory emf = null;

	IRWWBase irwwbase = new IRWWBase();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CustomSalesQuoteServlet() {
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

		EntityManager em = null;

		Part productDescriptionPart = null;
		String productDescription = null;

		ItemJPA item = new ItemJPA();
		BigDecimal price = new BigDecimal(0);

		InitialContext initialContext = null;
		UserTransaction userTran = null;

		java.text.NumberFormat numberFormat = java.text.NumberFormat.getCurrencyInstance(java.util.Locale.US);

		PrintWriter out = response.getWriter();
		response.setContentType("text/html");

		out.println( "<html>");
		out.println( "<head><title>Custom Sales Quote</title></head> ");
		out.println( "<body>");
		out.println( "<body bgcolor=\"#f8f7cd\">");
		out.println( "<h1>Custom Sales Quote</h1>");
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
			throw new ServletException("<<< Error: In " + classname + ": Error Begin transaction: exception: "+e);
		}

		try	{
			productDescriptionPart = request.getPart("productDescription");			
		}catch (Exception e)	{ 
			System.out.println("<<< Error: In " + classname + ": Error getting productDescription part: exception: "+e);
			out.println("<tr><td>Status: Failed: " + e.getMessage() + "</td></tr>");
			e.printStackTrace();
			out.println("<tr><td>Stack: " + irwwbase.getStackTrace(e) + "</td></tr>");
			throw new ServletException("<<< Error: In " + classname + ": Error getting productDescription part: exception: "+e);
		}

		if (productDescriptionPart.getSize()!= 0)	{
			productDescription = request.getParameter(productDescriptionPart.getName().trim());
			out.println("<tr><td>  Product Description: " + productDescription + "<tr><td>");	
			out.println("<tr><td>  <tr><td>");	
		}else{
			out.println("<tr><td>Product Description cannot be blank</td></tr>");
			out.println("<tr><td>Status: Failed: Product Description is blank</td></tr>");
			throw new ServletException("<<< Error: In " + classname + ": Product Description is blank");
		}

		irwwbase.debugOut("<<< In " + classname + ", Thread id: " + Thread.currentThread().getId() + " Before productGraphic part (image 1)");
		out.println("<tr><td> Diagram of the Custom Product: <tr><td>");
		this.displayImage(request, response, "productGraphic", "graphicsPartFile1", out);	
		out.println("<tr><td>  <tr><td>");	
		irwwbase.debugOut("<<< In " + classname + ", Thread id: " + Thread.currentThread().getId() + " After productGraphic part (image 1)");

		irwwbase.debugOut("<<< In " + classname + ", Thread id: " + Thread.currentThread().getId() + " Before productPartsGraphic part (image 2)");
		out.println("<tr><td> Picture of the Parts: <tr><td>");
		this.displayImage(request, response, "productPartsGraphic", "graphicsPartFile2", out);
		out.println("<tr><td>  <tr><td>");
		irwwbase.debugOut("<<< In " + classname + ", Thread id: " + Thread.currentThread().getId() + " After productPartsGraphic part (image 2)");

		item = this.getItemInstance(em, out);
		price = (item.getItemPrice().multiply(new BigDecimal(100)));
		out.println("<tr><td>Custom Sales Price: " + " " + numberFormat.format(price)+ "<tr><td>");
		irwwbase.debugOut("<<< In " + classname + ", Thread id: " + Thread.currentThread().getId() + " After getItem information");
			
		try {
			userTran.commit();
			out.println("<tr><td>Status: Successful</td></tr>");
		}catch(Exception e){	
			System.out.println("<<< Error: In " + classname + ": Error Commit Transaction - Exception: " + e);
			out.println("<tr><td>Status: Failed: " + e.getMessage() + "</td></tr>");
			e.printStackTrace();
			out.println("<tr><td>Stack: " + irwwbase.getStackTrace(e) + "</td></tr>");
			throw new ServletException("<<< Error: In " + classname + ": Error Commit Transaction - Exception: " + e);
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
			throw new ServletException("<<< Error: In " + classname + ": Error em.close() - Exception: " + e);
		}

		irwwbase.debugOut("<<< In " + classname + ", Thread id: " + Thread.currentThread().getId() + " Exiting doPost method ");
		out.println( "</TABLE>");        
		out.println( "</body>");
		out.println( "</html>"); 

	}

	protected String displayImage(HttpServletRequest request, HttpServletResponse response, String partName, String imageName, PrintWriter out)throws ServletException, IOException {

		Part graphicPart = null;		
		String imageTempFile = null;
		String tempdir = null;
		long graphicPartSize = 0;
		ServletContext servletContext = this.getServletContext();

		ExtendedRandom rand = new ExtendedRandom();
		rand.nextInt(0, 10);

		imageTempFile = imageName + rand.nextInt(100000000, 999999999) + ".jpg";

		try	{
			graphicPart = request.getPart(partName);			
		}catch (Exception e)	{ 
			System.out.println("<<< Error: In " + classname + ": Error getting " + graphicPart + ": exception: "+e);
			out.println("<tr><td>Status: Failed: " + e.getMessage() + "</td></tr>");
			e.printStackTrace();
			out.println("<tr><td>Stack: " + irwwbase.getStackTrace(e) + "</td></tr>");
			throw new ServletException("<<< Error: In " + classname + ": Error getting " + graphicPart + ": exception: "+e);
		}	
		
		graphicPartSize = graphicPart.getSize();
		
		try	{
			if (graphicPartSize!=0){
				graphicPart.write(imageTempFile);
			}else{
				System.out.println("<<< Error: In " + classname + ": graphicPart is empty");
				out.println("<tr><td>Status: Failed: graphicPart is empty</td></tr>");
				throw new ServletException("<<< Error: In " + classname + ": Error graphicPart is empty");
			}
		}catch (Exception e)	{ 
			System.out.println("<<< Error: In " + classname + ": Error writing " + graphicPart + ": exception: "+e);
			out.println("<tr><td>Status: Failed: " + e.getMessage() + "</td></tr>");
			e.printStackTrace();
			out.println("<tr><td>Stack: " + irwwbase.getStackTrace(e) + "</td></tr>");
			throw new ServletException("<<< Error: In " + classname + ": Error writing " + graphicPart + ": exception: "+e);
		}		

		tempdir = servletContext.getAttribute("jakarta.servlet.context.tempdir").toString();

		try	{
			irwwbase.debugOut("<<< In " + classname + ", Thread id: " + Thread.currentThread().getId() + " Before <img src=\"ImageServingServlet.../> where imageFileName = " + 
					imageTempFile + " and size = " + graphicPartSize + " bytes ");
			out.println("<p><img src=\"ImageServingServlet?imageFileName=" + imageTempFile +  "&tempdir=" + tempdir + "&out=" + out +"\" width=\"400\" height=\"300\" /></p>");
			irwwbase.debugOut("<<< In " + classname + ", Thread id: " + Thread.currentThread().getId() + " After <img src=\"ImageServingServlet.../> where imageFileName = " + imageTempFile);
		}catch (Exception e)	{ 
			System.out.println("<<< Error: In " + classname + ": Error in the ImageServingServlet: exception: "+e);
			out.println("<tr><td>Status: Failed: " + e.getMessage() + "</td></tr>");
			e.printStackTrace();
			out.println("<tr><td>Stack: " + irwwbase.getStackTrace(e) + "</td></tr>");
			throw new ServletException("<<< Error: In " + classname + ": Error in the ImageServingServlet: exception: "+e);
		}		

		if (tempdir != null){
			if (tempdir.contains("\\")){
				tempdir = tempdir + "\\";
			}else{
				tempdir = tempdir + "/";
			} 
		}

		return tempdir + imageTempFile;
	}

	protected ItemJPA getItemInstance(EntityManager em, PrintWriter out)throws ServletException, IOException {

		ItemJPA item = new ItemJPA();

		byte db = 1;
		ExtendedRandom rand = new ExtendedRandom();		
		Integer itemId = (rand.nextInt(1, MaxValues.itemId(db)));

		try	{			
			item = em.find(ItemJPA.class, itemId);
		}catch (Exception e)	{ 
			System.out.println("<<< Error: In " + classname + ": Error getting item: exception: "+e);
			out.println("<tr><td>Status: Failed: " + e.getMessage() + "</td></tr>");
			e.printStackTrace();
			out.println("<tr><td>Stack: " + irwwbase.getStackTrace(e) + "</td></tr>");
			throw new ServletException("<<< Error: In " + classname + ": Error getting item: exception: "+e);
		}
		return item;
	}
}
