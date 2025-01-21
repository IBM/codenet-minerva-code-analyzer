package orders.query.lite.jdbc.el;

import irwwbase.IRWWBase;
import irwwbase.MaxValues;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Random;
import java.util.concurrent.Executor;

import jakarta.annotation.Resource;
import jakarta.el.ELManager;
import jakarta.el.ELProcessor;
import javax.naming.InitialContext;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import jakarta.transaction.UserTransaction;

/**
 * Servlet implementation class OrdersQueryServlet
 */
@WebServlet(name="OrdersQueryServlet", urlPatterns={"/OrdersQueryServlet"})
public class OrdersQueryServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String classname = this.getClass().toString();
	private IRWWBase irwwbase = new IRWWBase();
	
    @Resource(name = "jdbc/ERWWDataSourceWithPwd")
	private DataSource ERWWDataSourceWithPwd;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public OrdersQueryServlet() {
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

		String status = "Failed";
		boolean throwable = false;
		String message = "";
		Exception passedException = null;
		Throwable passedThrowable = null;
		String manualErrorPage = "webManualError.html";
		
		int initialNetworkTimeout = 0;
		int retryNetworkTimeout = 0;
		int networkTimeout = 0;
		boolean firstTimeFlag = true;
		boolean retryFlag = false;
		int numberOfRetries = 0;
		
		InitialContext initialContext = null;
		UserTransaction userTran = null;
		
		OrdersQueryInput input = new OrdersQueryInput();   
	    
		String theButton = request.getParameter("command");

		irwwbase.debugOut("<<< In " + classname + ": doPost() method. Before AutoGeneration/Manual decision.");

		if (theButton.trim().equalsIgnoreCase("AutoGeneration")) {
			try {
				input = this.webAutoGenInput(request,response);	
			}
			catch (Throwable e) {
				throwable = true;
				passedThrowable = e;
				message = "<<< In " + classname + ": throwable exception occurred in doPost() method invoking webAutoGenInput.";
				this.webException(message,passedException,passedThrowable,throwable,request,response);
				System.out.println(message);
				e.printStackTrace();			
				return;
			}
		}
		else if (theButton.trim().equalsIgnoreCase("Manual")) {
			try {
				input = this.webManualInput(request,response);
			}
			catch (Throwable e) {
				throwable = true;
				message = "<<< In " + classname + ": throwable exception occurred in doPost() method invoking webManualInput().";
				passedThrowable = e;
				this.webException(message,passedException,passedThrowable,throwable,request,response);
				System.out.println(message);
				e.printStackTrace();			
				return;
			}
		}		

	    int numberWarehouseIds = (input.getMaxWarehouseId() - input.getMinWarehouseId() + 1);
	    int numberDistrictIds = (input.getMaxDistrictId() - input.getMinDistrictId() + 1);
	    int numberArrayRows = numberWarehouseIds * numberDistrictIds;
	    int numberArrayColumns = 3;	   
	    int[][] ordersArray = new int[numberArrayRows][numberArrayColumns];  
	    
		try {
			initialNetworkTimeout = (Integer.valueOf(request.getParameter("initialNetworkTimeout")));
			irwwbase.debugOut("<<< In " + classname + ": Initial Network Timeout = " + request.getParameter("initialNetworkTimeout"));
			
			retryNetworkTimeout = (Integer.valueOf(request.getParameter("retryNetworkTimeout")));
			irwwbase.debugOut("<<< In " + classname + ": Retry Network Timeout = " + request.getParameter("retryNetworkTimeout"));
		}
		catch(NumberFormatException e) {
			message = "<<< In " + classname + ": A user entered null as a value for the initialNetworkTimeout or retryNetworkTimeout in doPost() for " + classname;
			passedException = e;
			this.webManualError(request,response,manualErrorPage);
			System.out.println(message);
			e.printStackTrace();
		}
		
	    networkTimeout = initialNetworkTimeout;
	    irwwbase.debugOut(classname + ": networkTimeout = " + networkTimeout);
		
		try {  		    
		    irwwbase.debugOut("<<< In " + classname + ": Before Lamda Expression TwoDimentionalArray logic defined");
		    TwoDimentionalArray populatedArray = (array, numberOfInput1, numberOfInput2, minInput1, minInput2) -> {	    	
			    for (int i=0; i < numberOfInput1;){	    	   	
		    		for (int j=0; j < numberOfInput2; ){
			    		array[(numberOfInput2 * i) + j][0]=minInput1+i;
			    		array[(numberOfInput2 * i) + j][1]=minInput2+j;
			    		j++;
			    	}  
		    		i++;
		    	}
			    return array;
		    };
		    
		    irwwbase.debugOut("<<< In " + classname + ": Before Lamda Expression TwoDimentionalArray invoked (populatedArray is the object instance)");
		    ordersArray = populatedArray.populateArrayTwoColumns(ordersArray, numberWarehouseIds, numberDistrictIds, input.getMinWarehouseId(), input.getMinDistrictId());			    
	    } catch (Throwable e) {
			throwable = true;
			passedThrowable = e;
			message = "<<< In " + classname + ": throwable exception involving the Lamda Expression TwoDimentionalArray.";
			this.webException(message,passedException,passedThrowable,throwable,request,response);
			System.out.println(message);
			e.printStackTrace();			
			return;
	    }
		
		while ((firstTimeFlag || retryFlag) && (numberOfRetries == 0)) {
			irwwbase.debugOut("<<< In " + classname + ": Enter while loop where firstTimeFlag =  " + firstTimeFlag);
			irwwbase.debugOut("<<< In " + classname + ": Enter while loop where retryFlag =  " + retryFlag);
			
			firstTimeFlag = false;	
			
			if (retryFlag){
				networkTimeout = retryNetworkTimeout;
				numberOfRetries++;
			}	
			
			try {
				irwwbase.debugOut("<<< In " + classname + ": Before beginning a transaction in the doPost method");
				initialContext = new InitialContext();
				userTran = (UserTransaction) initialContext.lookup("java:comp/UserTransaction");
				userTran.begin();
			} catch (Exception e) {
				throwable = true;
				passedThrowable = e;
				message = "<<< In " + classname + ": throwable exception occurred in doPost() method invoking beginTran.";
				this.webException(message,passedException,passedThrowable,throwable,request,response);
				System.out.println(message);
				e.printStackTrace();			
				return;
			}
			
			DataSource dataSource = null;
			try {
				dataSource = this.getDataSource();
			} catch (Throwable e) {
				throwable = true;
				passedThrowable = e;
				message = "<<< In " + classname + ": this.getDataSource()";
				this.webException(message,passedException,passedThrowable,throwable,request,response);
				System.out.println(message);
				e.printStackTrace();			
				return;
			}
		
			irwwbase.debugOut("<<< In " + classname + ": Before Lamda Expression Cumulate logic defined");
			CumulateInt ordersCount = (x, y) -> x + y;
		
			String sqlString = "SELECT * FROM ORDERS where (O_W_ID between " + input.getMinWarehouseId() + " and " + input.getMaxWarehouseId() + ")" +
	        		" and (O_D_ID between " + input.getMinDistrictId() + " and " + input.getMaxDistrictId() + ")" +
	        		" and O_CARRIER_ID > 0 ORDER BY O_W_ID, O_D_ID ASC";
	    
			irwwbase.debugOut("<<< In " + classname + ": Before try-with-resource when invoking getConnection(), prepareStatement(sqlString), resultSet"); 		
			boolean firstTimeIteratingResultSet = true;
			int cumulativeOrdersCount = 0;
			short previousWarehouseId = 0;
			short previousDistrictId = 0;
			try (Connection connection = dataSource.getConnection();){
				connection.setNetworkTimeout(this.getExec(), networkTimeout);
				try (PreparedStatement preparedStatement = connection.prepareStatement(sqlString);
					ResultSet resultSet = preparedStatement.executeQuery()) {				
					irwwbase.debugOut("<<< In " + classname + ": DB2 Driver Version = " + connection.getMetaData().getDriverVersion());
					while (resultSet.next()) {  
					   	if (firstTimeIteratingResultSet){
					    	previousWarehouseId = resultSet.getShort("O_W_ID");
					    	previousDistrictId = resultSet.getShort("O_D_ID");
					    	firstTimeIteratingResultSet = false;
					    }
					    for (int i=0; i < numberWarehouseIds; i++){	    	   	
					       	for (int j=0; j < numberDistrictIds; ){
					        	if ((resultSet.getShort("O_W_ID") != previousWarehouseId) || (resultSet.getShort("O_D_ID") != previousDistrictId)){
					        		cumulativeOrdersCount = 0;
					        		previousWarehouseId = resultSet.getShort("O_W_ID");
					        		previousDistrictId = resultSet.getShort("O_D_ID");
					        	}
					        	if ((ordersArray[(numberDistrictIds * i) + j][0] == resultSet.getShort("O_W_ID")) && (ordersArray[i+j][1] == resultSet.getShort("O_D_ID"))) {
					        		// Lamda Expression CumulateInt invoked (ordersCount is the object instance)
					        		cumulativeOrdersCount = ordersCount.add(cumulativeOrdersCount, 1);
					        		ordersArray[(numberDistrictIds * i) + j][2] = cumulativeOrdersCount;
					        	}	
					    	   	j++;
					       	}  	
					    }
					}
				}		
			} catch (SQLException e) {
				throwable = false;
				passedException = e;
				message = "<<< In " + classname + ": dataSource.getConnection(), connection.prepareStatement(sqlString), preparedStatement.executeQuery(), iterate over resultSet. ";
				e.printStackTrace();
				System.out.println(message);			
				if (e.toString().contains("timed out")){
					retryFlag = true;
					irwwbase.debugOut("<<< In " + classname + ": Timeout occurred; retryFlag = " + retryFlag);
				}else{
					this.webException(message,passedException,passedThrowable,throwable,request,response);
					return;			
				}
			} catch (Throwable e) {
				throwable = true;
				passedThrowable = e;
				message = "<<< In " + classname + ": dataSource.getConnection(), connection.prepareStatement(sqlString), preparedStatement.executeQuery(), iterate over resultSet. ";
				e.printStackTrace();
				System.out.println(message);
				if (e.toString().contains("timed out")){
					retryFlag = true;
					irwwbase.debugOut(classname + ": Timeout occurred; retryFlag = " + retryFlag);
				}else{
					this.webException(message,passedException,passedThrowable,throwable,request,response);
					return;			
				}
			} 
			
		    for (int i=0; i < numberWarehouseIds;){	    	   	
	    		for (int j=0; j < numberDistrictIds; ){
	    			irwwbase.debugOut("<<< In " + classname + ": ordersArray: warehouseId = " + ordersArray[(numberDistrictIds * i) + j][0]);
	    			irwwbase.debugOut("<<< In " + classname + ": ordersArray: districtId = " + ordersArray[(numberDistrictIds * i) + j][1]);
	    			irwwbase.debugOut("<<< In " + classname + ": ordersArray: Number of Orders = " + ordersArray[(numberDistrictIds * i) + j][2]);
	    			irwwbase.debugOut("  ");
	      			j++; 
	    		}  	
	  			i++;
		    }
			
			try {
				irwwbase.debugOut("<<< In " + classname + ": Before commit of a transaction in doPost method");
				userTran.commit();
				status = "Successful";			
				this.webResults(ordersArray, numberWarehouseIds, numberDistrictIds, status, request, response);
			} catch (Throwable e) {
				throwable = true;
				passedThrowable = e;
				message = "<<< In " + classname + ": userTran.commit(). ";
				e.printStackTrace();
				retryFlag = true;
				if (numberOfRetries > 0){
					this.webException(message,passedException,passedThrowable,throwable,request,response);
				}
			} 
		}
	}
	
	public OrdersQueryInput webAutoGenInput(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {

		boolean throwable = false;
		String message = "";
		Exception passedException = null;
		Throwable passedThrowable = null;
		OrdersQueryInput input = null;

		// outer try block to cover entire AutoGenInput() code 
		// allows for rollback() if anything goes wrong in transaction 
		try {
			short randomWarehouseId = 0;
			short randomDistrictId = 0;
			short numberOrderLines = 0;
			
			// - use (byte)0 for small tables
			// - use (byte)1 for medium tables
			// - use (byte)2 for large tables
			byte database = irwwbase.dbSize();
			
			input = new OrdersQueryInput();
			
			// According to Java API, nextInt(param) goes from 0 ---> param - 1 therefore need to increment by 1
			// or else random number generator will start 0
			Random rand = new Random();
			randomWarehouseId = (short) rand.nextInt(MaxValues.warehouseId(database));
			randomWarehouseId++;
			randomDistrictId = (short) rand.nextInt(MaxValues.districtId(database));
			randomDistrictId++;
			irwwbase.debugOut("<<< In " + classname + ": the randomly-generated Warehouse ID = " + randomWarehouseId);
			irwwbase.debugOut("<<< In " + classname + ": the randomly-generated District ID = " + randomDistrictId);
			
			input.setMinWarehouseId(randomWarehouseId);
			input.setMaxWarehouseId(randomWarehouseId);
			input.setMinDistrictId(randomDistrictId);
			input.setMaxDistrictId(randomDistrictId);
			input.setNumOrders(numberOrderLines);
			
		} catch (Exception e) {
			message = "<<< In " + classname + ": exception occurred in webAutoGenInput().";
			passedThrowable = e;
			this.webException(message,passedException,passedThrowable,throwable,request,response);
			System.out.println(message);
			e.printStackTrace();
		}
		return input;
	}
	
	private OrdersQueryInput webManualInput(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {

		boolean throwable = false;
		String message = "";
		Exception passedException = null;
		Throwable passedThrowable = null;
		OrdersQueryInput userInput = null;

		// outer try block to cover entire webManualInput() code 
		// allows for rollback() if anything goes wrong in transaction 
		try {

			// - use (byte)0 for small tables
			// - use (byte)1 for medium tables
			// - use (byte)2 for large tables
			byte database = irwwbase.dbSize();

			irwwbase.debugOut("<<< In " + classname + ": Enter webManualInput()");

			String manualErrorPage = "webManualError.html";

			userInput = new OrdersQueryInput();

			try {
				userInput.setMinWarehouseId(Short.valueOf(request.getParameter("minWarehouseId")));
				irwwbase.debugOut("<<< In " + classname + ": Minimum WarehouseID Input = " + Short.valueOf(request.getParameter("minWarehouseId")));
			}
			catch(NumberFormatException e) {
				message = "<<< In " + classname + ": A user entered null as a value for the minimum warehouse id to delete in webManualInput() for " + classname;
				passedException = e;
				this.webManualError(request,response,manualErrorPage);
				System.out.println(message);
				e.printStackTrace();
			}
			
			try {
				userInput.setMaxWarehouseId(Short.valueOf(request.getParameter("maxWarehouseId")));
				irwwbase.debugOut("<<< In " + classname + ": Maximum WarehouseID Input = " + Short.valueOf(request.getParameter("maxWarehouseId")));
			}
			catch(NumberFormatException e) {
				message = "<<< In " + classname + ": A user entered null as a value for the maxnimum warehouse id to delete in webManualInput() for " + classname;
				passedException = e;
				this.webManualError(request,response,manualErrorPage);
				System.out.println(message);
				e.printStackTrace();
			}

			try {
				userInput.setMinDistrictId(Short.valueOf(request.getParameter("minDistrictId")));
				irwwbase.debugOut("<<< In " + classname + ": Minimum DistrictID Input = " + Short.valueOf(request.getParameter("minDistrictId")));
			}
			catch(NumberFormatException e) {
				message = "<<< In " + classname + ": A user entered null as a value for the maximum district id to delete in webManualInput() for " + classname;
				passedException = e;
				this.webManualError(request,response,manualErrorPage);
				System.out.println(message);
				e.printStackTrace();
			}
			
			try {
				userInput.setMaxDistrictId(Short.valueOf(request.getParameter("maxDistrictId")));
				irwwbase.debugOut("<<< In " + classname + ": Maximum DistrictID Input = " + Short.valueOf(request.getParameter("maxDistrictId")));
			}
			catch(NumberFormatException e) {
				message = "<<< In " + classname + ": A user entered null as a value for the maximum district id to delete in webManualInput() for " + classname;
				this.webManualError(request,response,manualErrorPage);
				System.out.println(message);
				e.printStackTrace();
			}

			// set user input values according to the manual values the of the user input
			short minWarehouseId = userInput.getMinWarehouseId();
			short maxWarehouseId = userInput.getMaxWarehouseId();
			short minDistrictId = userInput.getMinDistrictId();
			short maxDistrictId = userInput.getMaxDistrictId();
			//int numOrders = userInput.getNumOrders();

			// set max values according to the database to check the user values are valid
			// "numOrders" is not a value set by database, that max value is set by us 
			// rowToStart, MaxNumOrders will be used to query database for first 10 rows
			// "rowToStart" = 0 is row 1 as per the API

			short maximumWarehouseId = (short)MaxValues.warehouseId(database);
			short maximumDistrictId = (short)MaxValues.districtId(database);

			// Validate the user input is all valid                 
			if (((minWarehouseId  > 0) && (minWarehouseId <= maximumWarehouseId)  &&  
					((maxWarehouseId  > 0) && (maxWarehouseId <= maximumWarehouseId))  && 
					((minDistrictId > 0) && (minDistrictId <= maximumDistrictId))  &&  
					((maxDistrictId > 0) && (maxDistrictId <= maximumDistrictId))))   
			{
				irwwbase.debugOut("<<< In " + classname + ": All inputs are valid");
			}
			else {
				irwwbase.debugOut("<<< In " + classname + ": The user inputted invalid values for the manual path.");
				this.webManualError(request,response,manualErrorPage);
				throw new ServletException("<<< Error: In " + classname + ": Error Message: The user inputted invalid values for the manual path.");
			}
		} catch (Exception e) {
			message = "<<< In " + classname + ": exception occurred in webManualInput().";
			passedThrowable = e;
			this.webException(message,passedException,passedThrowable,throwable,request,response);
			e.printStackTrace();
			System.out.println(message);
			throw new ServletException("<<< Error: In " + classname + ": Error Message: " + e.getMessage() + ": Exception Cause: " + e.getCause());
		}
		return userInput;
	}
	
	private synchronized DataSource getDataSource()	throws Exception {
		
		InitialContext initialContext = null;
		DataSource dataSource = null;
		String message = null;
		
		if (dataSource == null) {
			String dataSourceName = "java:comp/env/jdbc/ERWWDataSourceWithPwd";
			try {
				initialContext = new InitialContext();
				dataSource = (javax.sql.DataSource) initialContext.lookup(dataSourceName);
			} catch (Exception e) {
				message = "<<< In " + classname + ": Error in initialContext.lookup(dataSourceName)";
				System.out.println(message);
				e.printStackTrace();
			}
		}
		return dataSource;
	}
	
    private Executor getExec() {
        return new Executor() {
            @Override
            public void execute(Runnable command) {
                command.run();
            }
        };
    }
	
	public void webResults(int[][] ordersArray, int numberWarehouseIds, int numberDistrictIds, String status, HttpServletRequest request, HttpServletResponse response) {

		boolean throwable = false;
		String message = "";
		Exception passedException = null;
		Throwable passedThrowable = null;
		Object dbSizeMessage = null;
		
		PrintWriter out = null;	
		response.setContentType("text/html");  

		try {
			out = response.getWriter();
		}
		catch (IOException e) {
			message = "<<< In " + classname + ": IO exception occurred trying to getWriter() in webResults().";
			passedException = e;
			this.webException(message,passedException,passedThrowable,throwable,request,response);
			e.printStackTrace();
			System.out.println(message);
			return;
		}   
		
		irwwbase.debugOut("<<< In " + classname + ": Expression Language 3.0");		
		try {
			ELProcessor elProcessor = new ELProcessor();
				
			ELManager elManager = elProcessor.getELManager();
			elManager.importClass("irwwbase.IRWWBase");
			
			elProcessor.defineBean("irwwBaseBean", new IRWWBase());

			elProcessor.setVariable("dbSizeVariable", "irwwBaseBean.dbSize()");
			byte dbSizeVariableResult = (byte)elProcessor.eval("dbSizeVariable");
			irwwbase.debugOut("<<< In " + classname + ": Expression Language 3.0: dbSizeVariableResult = " + dbSizeVariableResult);
			
			dbSizeMessage = elProcessor.eval(" size = ['Small','Medium','Large'].get(dbSizeVariable); 'Database Size = ' += size ");
			
		} catch (Exception e) {
			message = "<<< In " + classname + ": exception occurred with EL 3.0 statements.";
			passedThrowable = e;
			this.webException(message,passedException,passedThrowable,throwable,request,response);
			e.printStackTrace();
			System.out.println(message);
			return;
		} 	

		out.println("<html>");
		out.println("<center><H1>Delivered Orders Query Lite Results Form</H1></center>");
		out.println("<center><H2>JDBC 4.1 (try-with-resouces), EL 3.0, Java 8</H2></center>");
		out.println("<center><H2>" + dbSizeMessage.toString()+ "</H2></center>");
		out.println("<body bgcolor=\"#f8f7cd\">");
		out.println("<h3 center>Status: " + status + "</h3> ");
		out.println("<h2 center>The following lists the number(s) of delivered order(s) by warehouse/district.</h2> ");

		out.println("</head> ");
		out.println("<table border=\"8\" bgcolor=\"#cccccc\" bordercolor=\"#FFCC99\" >"); 
		out.println("<tr>");
		out.println("<td>WarehouseId</td>");
		out.println("<td>DistrictId</td>");
		out.println("<td>Number of Orders</td>");

		NumberFormat integerFormat = NumberFormat.getIntegerInstance();
		
	    for (int i=0; i < numberWarehouseIds;){	    	   	
    		for (int j=0; j < numberDistrictIds; ){	
    		    out.println("<tr>");
		    	out.println("<td>" + ordersArray[(numberDistrictIds * i) + j][0]+ "</td>");
		    	out.println("<td>" + ordersArray[(numberDistrictIds * i) + j][1]+ "</td>");
		    	out.println("<td>" + integerFormat.format(ordersArray[(numberDistrictIds * i) + j][2]) + "</td>");
		    	out.println("<td>" + "  "+ "</td>");
		    	out.println("</tr>");
		      	j++; 
		    }  	
    		i++;
		}
	
		out.println( "</table>"); 
		out.println( "</body>");
		out.println( "</html>"); 
		out.flush();	
		out.close();
	}


	public void webManualError(HttpServletRequest request,HttpServletResponse response, String manualErrorPage) {
		
		boolean throwable = false;
		String message = "";
		Exception passedException = null;
		Throwable passedThrowable = null;

		RequestDispatcher dispatch = request.getRequestDispatcher(manualErrorPage);

		try {
			dispatch.forward(request, response);
		}
		catch (Exception e) {
			message = "<<< In " + classname + ": general exception occurred trying to dispatch manual invalid input web page.";
			passedThrowable = e;
			this.webException(message,passedException,passedThrowable,throwable,request,response);
			e.printStackTrace();
			System.out.println(message);
			return;	
		}
	}
	
	/* A method that will dispatch the HTML page describing errors in the web application for both 
	 * webAutoGenInput() and webManualInput()
	 * 
	 * webException() * Deploys (Web Page) that Handles Exceptions
	 */
	public void webException(String theMessage, Exception theException, Throwable theThrowable, boolean caughtThrowable,
			HttpServletRequest request, HttpServletResponse response) {
		
		IRWWBase irwwbase = new IRWWBase();

		if (caughtThrowable == false) {
			PrintWriter out = null;	
			response.setContentType("text/html");  

			try {
				out = response.getWriter();
			}
			catch (IOException e) {
			}

			out.println("<html>");
			out.println("<center><H1>Delivered Order Query Lite Results Form</H1></center>");
			out.println("<center><H2>JDBC 4.1 (try-with-resouces), EL 3.0, Java 8</H2></center>");
			out.println("<body bgcolor=\"#f8f7cd\">");
			out.println("<h3 center>Status: FAILED</h3> ");
			out.println("<h3 center>Message:</h3>");
			out.println("<td>" + theMessage + "</td>");
			out.println("<h3 center>Exception:</h3>");
			out.println("<td>" + theException + "</td>");
			out.print("<h3 center>Stack:</h3>");
			out.println("<td>" + irwwbase.getStackTrace(theException) + "</td>");
			out.println( "</body>");
			out.println( "</html>"); 
			out.flush();
			out.close();
		}

		else  {
			PrintWriter out = null;	
			response.setContentType("text/html");  

			try {
				out = response.getWriter();
			}
			catch (IOException e) {

			}

			out.println("<html>");
			out.println("<center><H1>Delivered Order Archive Lite Results Form</H1></center>");
			out.println("<body bgcolor=\"#f8f7cd\">");
			out.println("<h3 center>Status: FAILED</h3> ");
			out.println("<h3 center>Message:</h3>");
			out.println("<td>" + theMessage + "</td>");
			out.println("<h3 center>Exception:</h3>");
			out.println("<td>" + theThrowable + "</td>");
			out.print("<h3 center>Stack:</h3>");
			out.println("<td>" + irwwbase.getStackTrace(theThrowable) + "</td>");
			out.println( "</body>");
			out.println( "</html>"); 
			out.flush();
			out.close();
		}
	}
}
