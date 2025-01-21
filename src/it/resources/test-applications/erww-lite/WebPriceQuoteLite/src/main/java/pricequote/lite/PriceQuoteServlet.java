package pricequote.lite;

import irwwbase.IRWWBase;
import itemjpa.ItemJPA;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet implementation class PriceQuoteServlet
 */
@WebServlet("/PriceQuoteServlet")
public class PriceQuoteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	int itemListLength=15;
	IRWWBase ib = new IRWWBase();
	String className = "PriceQuoteServlet";
	
	@PersistenceUnit(unitName = "PriceQuoteLite")
	EntityManagerFactory emf;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PriceQuoteServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//Comment out unused variable
		//byte database = ib.dbSize();
		int actualLength=0;
		String status="SUCCESSFUL";
		String stack="NO STACK";
		HttpSession session = null;	
		PriceQuoteSingleItemInInfo[] arraySingleInputItems = null;
		PriceQuoteInputInfo input=null;
		//Comment out unused variable
		//int maxNumberOfItemIds = 15;  //Need to itemId1 through itemId15 from the session
		
		String command = request.getParameter("command");
		
		try {
			session = request.getSession();
		} catch (Throwable t) {
			ib.debugOut("<<< Error:  request.getSession(false) failed");
			t.printStackTrace();
			throw new ServletException("Error:  request.getSession(false) failed", t);
		}
		input = new PriceQuoteInputInfo(); 
		
		if (command.trim().equalsIgnoreCase("Autogen")) {
			ib.debugOut("In "+ className + ": Autogen path");
			
			//GenerateAutoGenInputTag was invoked in the index.jsp to store the autogen input in the session
    		arraySingleInputItems = new PriceQuoteSingleItemInInfo[itemListLength];
        	
    		try {	 			
    			arraySingleInputItems = (PriceQuoteSingleItemInInfo[])session.getAttribute("arraySingleInputItems");   
    		} catch (Exception e) {
				System.out.println("<<< "	+ className	+ " mainline() - priceQuoteClient.web method - Exception: " + e);
    			e.printStackTrace();
    			throw new ServletException("<<< Error: In " + className + ": Error getting attribute from the session or invoking priceQuoteClient.webAutoGen: exception: "+e);
    		}    
    		
			input.setListLength((short)arraySingleInputItems.length);
			input.setInItemList(arraySingleInputItems);
		}
		else { 	
			ib.debugOut("In "+ className + ": Manual path");

			PriceQuoteSingleItemInInfo[] itemElement = new PriceQuoteSingleItemInInfo[itemListLength];
			boolean skip = false;
			if (Integer.valueOf(request.getParameter("itemId1")).intValue() != 0) { 
				itemElement[0] = new PriceQuoteSingleItemInInfo();
				itemElement[0].setInItemId(Integer.valueOf(request.getParameter("itemId1")));	
				itemElement[0].setInItemQuantity(Integer.valueOf(request.getParameter("quantity1")));
				skip = false;
			} else
				skip = true;

			if (skip == false) {
				if (Integer.valueOf(request.getParameter("itemId2")).intValue() != 0) {
					itemElement[1] = new PriceQuoteSingleItemInInfo();
					itemElement[1].setInItemId(Integer.valueOf(request.getParameter("itemId2")));
					itemElement[1].setInItemQuantity(Integer.valueOf(request.getParameter("quantity2")));
				} else {
					actualLength = 1;
					skip = true;
				}
			}

			if (skip == false) {
				if (Integer.valueOf(request.getParameter("itemId3")).intValue() != 0) {
					itemElement[2] = new PriceQuoteSingleItemInInfo();
					itemElement[2].setInItemId(Integer.valueOf(request.getParameter("itemId3")));
					itemElement[2].setInItemQuantity(Integer.valueOf(request.getParameter("quantity3")));
				} else {
					actualLength = 2;
					skip = true;
				}
			}

			if (skip == false) {
				if (Integer.valueOf(request.getParameter("itemId4")).intValue() != 0) {
					itemElement[3] = new PriceQuoteSingleItemInInfo();
					itemElement[3].setInItemId(Integer.valueOf(request.getParameter("itemId4")));
					itemElement[3].setInItemQuantity(Integer.valueOf(request.getParameter("quantity4")));
					} else {
					actualLength = 3;
					skip = true;
				}
			}

			if (skip == false) {
				if (Integer.valueOf(request.getParameter("itemId5")).intValue() != 0) {
					itemElement[4] = new PriceQuoteSingleItemInInfo();
					itemElement[4].setInItemId(Integer.valueOf(request.getParameter("itemId5")));
					itemElement[4].setInItemQuantity(Integer.valueOf(request.getParameter("quantity5")));
					} else {
					actualLength = 4;
					skip = true;
				}
			}

			if (skip == false) {
				if (Integer.valueOf(request.getParameter("itemId6")).intValue() != 0) {
					itemElement[5] = new PriceQuoteSingleItemInInfo();
					itemElement[5].setInItemId(Integer.valueOf(request.getParameter("itemId6")));
					itemElement[5].setInItemQuantity(Integer.valueOf(request.getParameter("quantity6")));
					} else {
					actualLength = 5;
					skip = true;
				}
			}

			if (skip == false) {
				if (Integer.valueOf(request.getParameter("itemId7")).intValue() != 0) {
					itemElement[6] = new PriceQuoteSingleItemInInfo();
					itemElement[6].setInItemId(Integer.valueOf(request.getParameter("itemId7")));
					itemElement[6].setInItemQuantity(Integer.valueOf(request.getParameter("quantity7")));
					} else {
					actualLength = 6;
					skip = true;
				}
			}

			if (skip == false) {
				if (Integer.valueOf(request.getParameter("itemId8")).intValue() != 0) {
					itemElement[7] = new PriceQuoteSingleItemInInfo();
					itemElement[7].setInItemId(Integer.valueOf(request.getParameter("itemId8")));
					itemElement[7].setInItemQuantity(Integer.valueOf(request.getParameter("quantity8")));
					} else {
					actualLength = 7;
					skip = true;
				}
			}

			if (skip == false) {
				if (Integer.valueOf(request.getParameter("itemId9")).intValue() != 0) {
					itemElement[8] = new PriceQuoteSingleItemInInfo();
					itemElement[8].setInItemId(Integer.valueOf(request.getParameter("itemId9")));
					itemElement[8].setInItemQuantity(Integer.valueOf(request.getParameter("quantity9")));
					} else {
					actualLength = 8;
					skip = true;
				}
			}

			if (skip == false) {
				if (Integer.valueOf(request.getParameter("itemId10")).intValue() != 0) {
					itemElement[9] = new PriceQuoteSingleItemInInfo();
					itemElement[9].setInItemId(Integer.valueOf(request.getParameter("itemId10")));
					itemElement[9].setInItemQuantity(Integer.valueOf(request.getParameter("quantity10")));
					} else {
					actualLength = 9;
					skip = true;
				}
			}

			if (skip == false) {
				if (Integer.valueOf(request.getParameter("itemId11")).intValue() != 0) {
					itemElement[10] = new PriceQuoteSingleItemInInfo();
					itemElement[10].setInItemId(Integer.valueOf(request.getParameter("itemId11")));
					itemElement[10].setInItemQuantity(Integer.valueOf(request.getParameter("quantity11")));
					} else {
					actualLength = 10;
					skip = true;
				}
			}

			if (skip == false) {
				if (Integer.valueOf(request.getParameter("itemId12")).intValue() != 0) {
					itemElement[11] = new PriceQuoteSingleItemInInfo();
					itemElement[11].setInItemId(Integer.valueOf(request.getParameter("itemId12")));
					itemElement[11].setInItemQuantity(Integer.valueOf(request.getParameter("quantity12")));
					} else {
					actualLength = 11;
					skip = true;
				}
			}

			if (skip == false) {
				if (Integer.valueOf(request.getParameter("itemId13")).intValue() != 0) {
					itemElement[12] = new PriceQuoteSingleItemInInfo();
					itemElement[12].setInItemId(Integer.valueOf(request.getParameter("itemId13")));
					itemElement[12].setInItemQuantity(Integer.valueOf(request.getParameter("quantity13")));
				} else {
					actualLength = 12;
					skip = true;
				}
			}

			if (skip == false) {
				if (Integer.valueOf(request.getParameter("itemId14")).intValue() != 0) {
					itemElement[13] = new PriceQuoteSingleItemInInfo();
					itemElement[13].setInItemId(Integer.valueOf(request.getParameter("itemId14")));
					itemElement[13].setInItemQuantity(Integer.valueOf(request.getParameter("quantity14")));
				} else {
					actualLength = 13;
					skip = true;
				}
			}

			if (skip == false) {
				if (Integer.valueOf(request.getParameter("itemId15")).intValue() != 0) {
					itemElement[14] = new PriceQuoteSingleItemInInfo();
					itemElement[14].setInItemId(Integer.valueOf(request.getParameter("itemId15")));
					itemElement[14].setInItemQuantity(Integer.valueOf(request.getParameter("quantity15")));
					actualLength = 15;
				} else {
					actualLength = 14;
				}
			}

			input.setListLength((short) actualLength);
			input.setInItemList(itemElement);
		}
		
		PriceQuoteOutput output=new PriceQuoteOutput(initializePriceQuoteOutput());
		try {
			output=priceQuoteServlet(input, output);
		}
		catch (Exception e) {
			e.printStackTrace();
			stack=ib.getStackTrace(e);
			status="FAILED";			
		}
		
		output.setStatus(status);
		output.setStack(stack);
		
		this.displayPriceQuoteOutput(output, request, response);

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}
	
	protected PriceQuoteOutput priceQuoteServlet(PriceQuoteInputInfo input,
			PriceQuoteOutput output) throws ServletException {
		short usSequenceLen; // length of input sequence
		short usIndex; // loop index;
		double dTotalPrices = Double.valueOf(output.getValue().getOutTotalPrice())
				.doubleValue();
		IRWWBase ib = new IRWWBase();
		// Set class name of class
		
		ib.startOfTran("PriceQuoteSessionServlet");

		usSequenceLen = input.getItemListLength();
		ib.debugOut(">>>ItemListLength in PriceQuoteSession "
				+ input.getItemListLength());
		
		EntityManager em = emf.createEntityManager();
		ItemJPA itemJPA=null;

		for (usIndex = 0; usIndex < usSequenceLen; usIndex++) {
			
			itemJPA=getItemInstance(input.getInItemList()[usIndex].getInItemId(),em);			

			output.getValue().getOutItemList()[usIndex].setOutItemId(input
					.getInItemList()[usIndex].getInItemId());
			output.getValue().getOutItemList()[usIndex]
					.setOutItemQuantity(input.getInItemList()[usIndex]
							.getInItemQuantity());

			output.getValue().getOutItemList()[usIndex].setOutItemName(itemJPA
					.getItemName());
			output.getValue().getOutItemList()[usIndex].setOutItemPrice(itemJPA
					.getItemPrice().doubleValue());
			ib.debugOut(">>> ERWW JPA - item name = "
					+ itemJPA.getItemName() + " and price = "
					+ itemJPA.getItemPrice().doubleValue());

			double dItemPrice = output.getValue().getOutItemList()[usIndex]
					.getOutItemPrice();
			int dItemQuantity = output.getValue().getOutItemList()[usIndex]
					.getOutItemQuantity();

			double dItemTotalPrice = dItemQuantity * dItemPrice;

			// below ensures dItemTotalPrice is rounded to two decimal places
			dItemTotalPrice = Math.floor(dItemTotalPrice * 100.0 + 0.5) / 100.0;

			output.getValue().getOutItemList()[usIndex]
					.setOutItemTotalPrice(dItemTotalPrice);

			dTotalPrices += dItemTotalPrice;

		} // end for
		
		// below ensures dItemTotalPrice is rounded to two decimal places
		dTotalPrices = Math.floor(dTotalPrices * 100.0 + 0.5) / 100.0;
		
		output.getValue().setOutTotalPrice(dTotalPrices);
		ib.debugOut(">>> ERWW - Grand Total =  " + dTotalPrices);

		ib.endOfTran("PriceQuoteSessionServlet");
		output.setStcnum(irwwbase.Stcnum.getInstance().getStcnum());
		
		try {
			if (em!=null && em.isOpen()){
				em.close();
			}

		 }catch(Exception e){
				System.out.println("<<< PriceQuoteServlet - em close() - Exception: " + e);
				e.printStackTrace();				
		 }

		return output;
	}
	
	protected PriceQuoteOutputInfo initializePriceQuoteOutput() {

		PriceQuoteSingleItemOutInfo itemElement[] = new PriceQuoteSingleItemOutInfo[itemListLength];
		for (int i = 0; i < itemListLength; i++) {
			itemElement[i] = new PriceQuoteSingleItemOutInfo();
			itemElement[i].setOutItemId(0);
			itemElement[i].setOutItemName("NotInitialized");
			itemElement[i].setOutItemQuantity(0);
			itemElement[i].setOutItemPrice(0);
			itemElement[i].setOutItemTotalPrice(0);
		}
		PriceQuoteOutputInfo output = new PriceQuoteOutputInfo();
		output.setOutItemList(itemElement);
		output.setOutTotalPrice(0);
		return output;
	}

	protected void displayPriceQuoteOutput(PriceQuoteOutput output, HttpServletRequest request, HttpServletResponse response) {
		
		PrintWriter out = null;	
        response.setContentType("text/html");  
        String imageNameWithPath = "/images/ERWW_Logo.jpg";
        
	    try{
	    	 out = response.getWriter();
	    }
		catch (Throwable e) {
			e.printStackTrace();	
		}   
        
		out.println("<html>");
		out.println("<head><title>Price Quote Results</title>");
 		out.println("<body bgcolor=\"#f8f7cd\">");
		out.println("<p><img src=\"DisplayImageServlet?imageNameWithPath=" + imageNameWithPath + "&out=" + out +"\" width=\"200\" height=\"50\" /></p>");
		out.println("<h2 center>Price Quote Results</h2> ");
		out.println("</head> ");
 		out.println("<h5>Time: " + new java.util.Date().toString()+ ", milliseconds: " + String.valueOf(System.currentTimeMillis())+ "</h5>");
 		out.println("<TABLE border=\"8\" bgcolor=\"#cccccc\" bordercolor=\"#FFCC99\" >");  	
		out.println("<tr><th>ItemID</th>");
		out.println("<th>ItemName</th>");
		out.println("<th>ItemPrice</th>");
		out.println("<th>ItemQuantity</th>");
		out.println("<th>ItemTotalPrice</th></tr>");
			
		PriceQuoteSingleItemOutInfo[] outItems = output.getValue().getOutItemList();
		
		int length = outItems.length;
		
		ib.debugOut("In displayPriceQuoteOutput where length = " + length);
		
		for (int i = 0; i < length; i++) {
			try {				
				if (outItems[i].getOutItemId() != 0){
					out.println("<tr><th>" + outItems[i].getOutItemId() + "</th>");
					out.println("<td>" + outItems[i].getOutItemName()+ "</td>");
					out.println("<th>" + outItems[i].getOutItemPrice()+ "</th>");
					out.println("<th>" + outItems[i].getOutItemQuantity()+ "</th>");
					out.println("<th>" + outItems[i].getOutItemTotalPrice()+ "</th></tr>");
				}
			} 
			catch (Throwable e) {
				e.printStackTrace();
			}
			
		}	
		out.println("<tr><th>    </th>");
		out.println("<tr><th>Grand Total</th>");
		out.println("<td>: " + output.getValue().getOutTotalPrice()+ "</td>");
		out.println("<tr><th>Status</th>");
		out.println("<td>" + output.getStatus()+ "</td>");
		out.println("<tr><th>Stack</th>");
		out.println("<td>" + output.getStack()+ "</td></tr>");		
		out.println( "</table>");       
		out.println( "</body>");
		out.println( "</html>"); 
		out.flush();	
    }
	

	protected ItemJPA getItemInstance(int item,EntityManager em) throws ServletException  {
		ItemJPA itemJPA = null;
		try {

			itemJPA = em.find(ItemJPA.class, item);
			if (itemJPA==null)	throw new ServletException("itemJPA not found, item="+item+" "+ib.getSTC() +"no stack");


		} catch (Exception t) {
			t.printStackTrace();
			if (ib.is913(t))
				throw new ServletException("<&!@"+ib.checkFor913(t)+", finding Item JPA, itemId="+item+", "+ t.getCause()+ib.getSTC()+ib.getStackTrace(t));
			else throw new ServletException("<&!@"+"failure in getItemInstance: "
					+" exception message: "+t.getMessage()+", exception cause: "+ t.getCause()+ib.getSTC()+ib.getStackTrace(t));
		} // end try
		return itemJPA;

	}
	
}
