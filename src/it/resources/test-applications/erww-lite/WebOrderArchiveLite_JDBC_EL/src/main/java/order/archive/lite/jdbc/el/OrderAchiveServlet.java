package order.archive.lite.jdbc.el;

import irwwbase.IRWWBase;
import irwwbase.MaxValues;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Random;

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
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.JoinRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import jakarta.transaction.RollbackException;
import jakarta.transaction.UserTransaction;

/**
 * Servlet implementation class OrderAchiveServlet
 */
@WebServlet(name="OrderArchiveServlet_JDBC_EL", urlPatterns={"/OrderArchiveServlet_JDBC_EL"})
public class OrderAchiveServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String classname = this.getClass().toString();
	private IRWWBase irwwbase = new IRWWBase();
	/*
    @Resource(name = "jdbc/ERWWDataSourceWithPwd")
	private DataSource ERWWDataSourceWithPwd; */
    
    @Resource(name = "jdbc/ERWWDataSourceWithJaasAlias")
	private DataSource ERWWDataSourceWithJaasAlias; 
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public OrderAchiveServlet() {
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
		
		boolean firstTimeFlag = true;
		boolean retryFlag = false;
		int numberOfRetries = 0;
		
		InitialContext initialContext = null;
		UserTransaction userTran = null;
		
		OrderArchiveInput input = new OrderArchiveInput();
		
	    int numberWarehouseIds = 0;
	    int numberDistrictIds = 0;
	    int numberArrayRows = 0;
	    int numberArrayColumns = 4;	
		
	    RowSetFactory rowSetFactory = null;
	    CachedRowSet cachedOrderRowSet = null;
	    CachedRowSet cachedOrderlineRowSet = null;  
	    
		String theButton = request.getParameter("command");

		irwwbase.debugOut("<<< In " + classname + ": In doPost() method. Before AutoGeneration/Manual/RemoveAllDelivered decision.");

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
	    	    
	    int[][] ordersArray = null;  
	    		
	    while ((firstTimeFlag || retryFlag) && (numberOfRetries == 0)) {
				
			try {
				irwwbase.debugOut("<<< In " + classname + ": Enter while loop where firstTimeFlag =  " + firstTimeFlag);
				irwwbase.debugOut("<<< In " + classname + ": Enter while loop where retryFlag =  " + retryFlag);
				
				try {
					irwwbase.debugOut("<<< In " + classname + ": Before beginning a transaction in performTask method");
					initialContext = new InitialContext();
					userTran = (UserTransaction) initialContext.lookup("java:comp/UserTransaction");
					userTran.begin();
				} catch (Throwable e) {
					message = "<<< In " + classname + ": exception occurred beginning the transaction.";
					passedThrowable = e;
					this.webException(message,passedException,passedThrowable,throwable,request,response);
					e.printStackTrace();
					System.out.println(message);
					return;
				} 
						
				if (firstTimeFlag){
					irwwbase.debugOut("<<< In " + classname + ": First Time through the while loop");
					numberWarehouseIds = (input.getMaxWarehouseId() - input.getMinWarehouseId() + 1);
					numberDistrictIds = (input.getMaxDistrictId() - input.getMinDistrictId() + 1);
					irwwbase.debugOut("<<< In " + classname + ": First Time through the while loop: numberWarehouseIds = " + numberWarehouseIds);
					irwwbase.debugOut("<<< In " + classname + ": First Time through the while loop: numberDistrictIds = " + numberDistrictIds);
					numberArrayRows = numberWarehouseIds * numberDistrictIds;	
					ordersArray = new int[numberArrayRows][numberArrayColumns]; 
				} else if (retryFlag){
					irwwbase.debugOut("<<< In " + classname + ": Retry in the while loop:  only select the minimum WarehouseId and minimum DistrictId during a retry.");
					input.setMaxWarehouseId(input.getMinWarehouseId());
					input.setMaxDistrictId(input.getMinDistrictId());
					numberWarehouseIds = 1;
					numberDistrictIds = 1;
					irwwbase.debugOut("<<< In " + classname + ": Retry in the while loop:  numberWarehouseIds = " + numberWarehouseIds);
					irwwbase.debugOut("<<< In " + classname + ": Retry in the while loop:  numberDistrictIds = " + numberDistrictIds);
					numberArrayRows = numberWarehouseIds * numberDistrictIds;
					ordersArray = new int[numberArrayRows][numberArrayColumns]; 
					numberOfRetries++;
				} 
				firstTimeFlag = false;
		
				rowSetFactory = RowSetProvider.newFactory();
				cachedOrderRowSet = rowSetFactory.createCachedRowSet();	
				//cachedOrderRowSet.setDataSourceName("jdbc/ERWWDataSourceWithPwd");
				cachedOrderRowSet.setDataSourceName("java:comp/env/jdbc/ERWWDataSourceWithJaasAlias");
				cachedOrderRowSet.setCommand("SELECT * FROM ORDERS where (O_W_ID between " + input.getMinWarehouseId() + " and " + input.getMaxWarehouseId() + ")" +
	        		" and (O_D_ID between " + input.getMinDistrictId() + " and " + input.getMaxDistrictId() + ")" +
	        		" and O_CARRIER_ID > 0 ORDER BY O_W_ID, O_D_ID, O_ID ASC");
	        		//" and O_CARRIER_ID > 0 ORDER BY O_W_ID, O_D_ID, O_ID ASC");
	                // Temporary for debugging
	        		// " and O_ID = 85 and O_CARRIER_ID > 0 ORDER BY O_W_ID, O_D_ID, O_ID ASC");
				cachedOrderRowSet.execute();
	    				
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
		    
				irwwbase.debugOut("<<< In " + classname + ": Before Lamda Expression CumulateInt logic defined");
				CumulateInt ordersCount = (x, y) -> x + y;
	    
				boolean firstTimeIteratingRowSet = true;
				int cumulativeOrdersCount = 0;
				short previousWarehouseId = 0;
				short previousDistrictId = 0;

				while (cachedOrderRowSet.next()) { 
					if (firstTimeIteratingRowSet){
						previousWarehouseId = cachedOrderRowSet.getShort("O_W_ID");
						previousDistrictId = cachedOrderRowSet.getShort("O_D_ID");
						irwwbase.debugOut("<<< In " + classname + ": First Row: cachedOrderRowSet.getShort(O_W_ID) = " + cachedOrderRowSet.getShort("O_W_ID"));
						irwwbase.debugOut("<<< In " + classname + ": First Row: cachedOrderRowSet.getShort(O_D_ID) = " + cachedOrderRowSet.getShort("O_D_ID"));
						firstTimeIteratingRowSet = false;
					}
					for (int i=0; i < numberWarehouseIds; i++){	    	   	
						for (int j=0; j < numberDistrictIds; ){
							if ((cachedOrderRowSet.getShort("O_W_ID") != previousWarehouseId) || (cachedOrderRowSet.getShort("O_D_ID") != previousDistrictId)){
								cumulativeOrdersCount = 0;
								previousWarehouseId = cachedOrderRowSet.getShort("O_W_ID");
								previousDistrictId = cachedOrderRowSet.getShort("O_D_ID");
							}
							if ((ordersArray[(numberDistrictIds * i) + j][0] == cachedOrderRowSet.getShort("O_W_ID")) && (ordersArray[i+j][1] == cachedOrderRowSet.getShort("O_D_ID"))) {
								// Lamda Expression CumulateInt invoked (ordersCount is the object instance)
								cumulativeOrdersCount = ordersCount.add(cumulativeOrdersCount, 1);
								ordersArray[(numberDistrictIds * i) + j][2] = cumulativeOrdersCount;
							}	
							j++;
						}  	
					}
				}	
	      	    
				rowSetFactory = RowSetProvider.newFactory();
				cachedOrderlineRowSet = rowSetFactory.createCachedRowSet();	        
				//cachedOrderlineRowSet.setDataSourceName("jdbc/ERWWDataSourceWithPwd");
				cachedOrderlineRowSet.setDataSourceName("java:comp/env/jdbc/ERWWDataSourceWithJaasAlias");
				// Do not select I_ORDERLINE_INFO (XML type column) due to defect 160909
				cachedOrderlineRowSet.setCommand("SELECT OL_O_ID, OL_D_ID, OL_W_ID, OL_NUMBER, OL_AMOUNT " +
	                " FROM ORDERLINE where (OL_W_ID between " + input.getMinWarehouseId() + " and " + input.getMaxWarehouseId() + ")" +
	        		" and (OL_D_ID between " + input.getMinDistrictId() + " and " + input.getMaxDistrictId() + ")" +
	        		" ORDER BY OL_W_ID, OL_D_ID, OL_O_ID ASC");
	        		//" ORDER BY OL_W_ID, OL_D_ID, OL_O_ID ASC");
	        	    // Temporary for debugging
	        		//" and OL_O_ID = 85 ORDER BY OL_W_ID, OL_D_ID, OL_O_ID ASC");
				cachedOrderlineRowSet.execute();
	 	    
				JoinRowSet joinRowSet = null;
				String[] OrderColumns = {"O_W_ID","O_D_ID","O_ID"};
				String[] OrderlineColumns = {"OL_W_ID","OL_D_ID","OL_O_ID"};
	
				irwwbase.debugOut("<<< In " + classname + ": Before RowSetProvider.createJoinRowSet()");
				rowSetFactory = RowSetProvider.newFactory();
				joinRowSet = rowSetFactory.createJoinRowSet();
				irwwbase.debugOut("<<< In " + classname + ": After RowSetProvider.createJoinRowSet()");
	    	
				irwwbase.debugOut("<<< In " + classname + ": Before addRowSet(cachedOrderRowSet)");
				cachedOrderRowSet.first();
				cachedOrderRowSet.setMatchColumn(OrderColumns);
				joinRowSet.addRowSet(cachedOrderRowSet);
				irwwbase.debugOut("<<< In " + classname + ": After addRowSet(cachedOrderRowSet)");
	    	
				irwwbase.debugOut("<<< In " + classname + ": Before addRowSet(cachedOrderlineRowSet)");
				cachedOrderlineRowSet.first();
				cachedOrderlineRowSet.setMatchColumn(OrderlineColumns);	
				joinRowSet.addRowSet(cachedOrderlineRowSet);   
				irwwbase.debugOut("<<< In " + classname + ": After addRowSet(cachedOrderlineRowSet)");
	    
				irwwbase.debugOut("<<< In " + classname + ": Before Lamda Expression CumulateDecimal logic defined");
				CumulateBigDecimal orderlineAmount = (x, y) -> x = x.add(y);  	    	

				firstTimeIteratingRowSet = true;
				BigDecimal cumulativeAmount = new BigDecimal(0);
				previousWarehouseId = 0;
				previousDistrictId = 0;

				while (joinRowSet.next()) { 
					if (firstTimeIteratingRowSet){
						previousWarehouseId = joinRowSet.getShort("O_W_ID");
						previousDistrictId = joinRowSet.getShort("O_D_ID");
						irwwbase.debugOut("<<< In " + classname + ": joinRowSet.getShort(O_W_ID) = " + joinRowSet.getShort("O_W_ID"));
						irwwbase.debugOut("<<< In " + classname + ": joinRowSet.getShort(O_D_ID) = " + joinRowSet.getShort("O_D_ID"));
						irwwbase.debugOut("<<< In " + classname + ": joinRowSet.getBigDecimal(OL_AMOUNT) = " + joinRowSet.getBigDecimal("OL_AMOUNT"));
						firstTimeIteratingRowSet = false;
					} 
					for (int i=0; i < numberWarehouseIds; i++){	    	   	
						for (int j=0; j < numberDistrictIds; ){
							if ((joinRowSet.getShort("O_W_ID") != previousWarehouseId) || (joinRowSet.getShort("O_D_ID") != previousDistrictId)){
								cumulativeAmount = new BigDecimal(0);
								previousWarehouseId = joinRowSet.getShort("O_W_ID");
								previousDistrictId = joinRowSet.getShort("O_D_ID");
							}
							if ((ordersArray[(numberDistrictIds * i) + j][0] == joinRowSet.getShort("O_W_ID")) && (ordersArray[i+j][1] == joinRowSet.getShort("O_D_ID"))) {
								// Before Lamda Expression CumulateBigDecimal (orderlineAmount is the object instance)
								cumulativeAmount = orderlineAmount.add(cumulativeAmount, joinRowSet.getBigDecimal("OL_AMOUNT"));
								ordersArray[(numberDistrictIds * i) + j][3] = cumulativeAmount.intValue();	 
								joinRowSet.deleteRow();
							}	
							j++;
						}  	
					}
				}
				
				irwwbase.debugOut("<<< In " + classname + ": Before iterating over ordersArray: numberWarehouseIds = " + numberWarehouseIds);
				irwwbase.debugOut("<<< In " + classname + ": Before iterating over ordersArray: numberDistrictIds = " + numberDistrictIds);
				for (int i=0; i < numberWarehouseIds;){	    	   	
					for (int j=0; j < numberDistrictIds; ){
						irwwbase.debugOut("<<< In " + classname + ": ordersArray: warehouseId = " + ordersArray[(numberDistrictIds * i) + j][0]);
						irwwbase.debugOut("<<< In " + classname + ": ordersArray: districtId = " + ordersArray[(numberDistrictIds * i) + j][1]);
						irwwbase.debugOut("<<< In " + classname + ": ordersArray: Number of Orders = " + ordersArray[(numberDistrictIds * i) + j][2]);
						irwwbase.debugOut("<<< In " + classname + ": ordersArray: Total Amount = " + ordersArray[(numberDistrictIds * i) + j][3]);
						irwwbase.debugOut("  ");
						j++; 
					}  	
					i++;
				}	    
		    
				irwwbase.debugOut("<<< In " + classname + ": Before commit of a transaction in doPost method");
				userTran.commit();
				status = "Successful";
				this.webResults(ordersArray, numberWarehouseIds, numberDistrictIds, status, request, response); 
			} catch (RollbackException e) {
				throwable = false;
				passedException = e;
				irwwbase.debugOut("<<< In " + classname + " where e.toString() = " + e.toString());
				message = "<<< In " + classname + ": Rollback exception."; 
				retryFlag = true;
				irwwbase.debugOut("<<< In " + classname + ": Rollback exception occurred; retryFlag = " + retryFlag);
			} catch (Throwable e) {
				throwable = true;
				passedThrowable = e;
				irwwbase.debugOut("<<< In " + classname + " where e.toString() = " + e.toString());
				message = "<<< In " + classname + ": exception occurred";
				this.webException(message,passedException,passedThrowable,throwable,request,response);
				return;	
			}
		}		
	}
	
	public OrderArchiveInput webAutoGenInput(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {

		boolean throwable = false;
		String message = "";
		Exception passedException = null;
		Throwable passedThrowable = null;
		OrderArchiveInput input = null;		

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
			
			input = new OrderArchiveInput();
			
			// According to Java API, nextInt(param) goes from 0 ---> param - 1 therefore need to increment by 1
			// or else random number generator will start 0
			Random rand = new Random();
			randomWarehouseId = (short) rand.nextInt(MaxValues.warehouseId(database));
			randomWarehouseId++;
			randomDistrictId = (short) rand.nextInt(MaxValues.districtId(database));
			randomDistrictId++;
			irwwbase.debugOut("<<< In " + classname + ": The randomly-generated Warehouse ID = " + randomWarehouseId);
			
			input.setMinWarehouseId(randomWarehouseId);
			input.setMaxWarehouseId(randomWarehouseId);
			input.setMinDistrictId(randomDistrictId);
			input.setMaxDistrictId(randomDistrictId);
			input.setNumOrders(numberOrderLines);
			
		} catch (Throwable e) {
			message = "<<< In " + classname + ": exception occurred in webAutoGenInput().";
			passedThrowable = e;
			this.webException(message,passedException,passedThrowable,throwable,request,response);
			System.out.println(message);
			e.printStackTrace();			
		}
		return input;
	}
	
	private OrderArchiveInput webManualInput(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {

		boolean throwable = false;
		String message = "";
		Exception passedException = null;
		Throwable passedThrowable = null;
		OrderArchiveInput userInput = null;

		// outer try block to cover entire webManualInput() code 
		// allows for rollback() if anything goes wrong in transaction 
		try {

			// - use (byte)0 for small tables
			// - use (byte)1 for medium tables
			// - use (byte)2 for large tables
			byte database = irwwbase.dbSize();

			irwwbase.debugOut("<<< In " + classname + ": Entered webManualInput()");

			String manualErrorPage = "webManualError.html";

			userInput = new OrderArchiveInput();

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
				message = "<<< In " + classname + ": A user entered null as a value for the minimum district id to delete in webManualInput() for " + classname;
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
					//&& ((numOrders > 0) && (numOrders <= maxNumOrders)))) 
			{
				irwwbase.debugOut("<<< In " + classname + ": All inputs are valid");
			}
			else {
				irwwbase.debugOut("<<< In " + classname + ": The user inputted invalid values for the manual path.");
				this.webManualError(request,response,manualErrorPage);
				throw new ServletException("<<< Error: In " + classname + ": Error Message: The user inputted invalid values for the manual path.");
			}
		} catch (Throwable e) {
			message = "<<< In " + classname + ": exception occurred in webManualInput().";
			passedThrowable = e;
			this.webException(message,passedException,passedThrowable,throwable,request,response);
			e.printStackTrace();
			System.out.println(message);
			throw new ServletException("<<< Error: In " + classname + ": Error Message: " + e.getMessage() + ": Exception Cause: " + e.getCause());
		}
		return userInput;
	}

	
	public void webResults(int[][] ordersArray, int numberWarehouseIds, int numberDistrictIds, String status, HttpServletRequest request, HttpServletResponse response) throws ServletException {

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
			
		} catch (Throwable e) {
			message = "<<< In " + classname + ": exception occurred with EL 3.0 statements.";
			passedThrowable = e;
			this.webException(message,passedException,passedThrowable,throwable,request,response);
			e.printStackTrace();
			System.out.println(message);
			return;
		} 		

		out.println("<html>");
		out.println("<center><H1>Order Archive Lite Results Form</H1></center>");
		out.println("<center><H2>JDBC 4.1 (RowSets), EL 3.0, Java 8</H2></center>");
		out.println("<center><H2>" + dbSizeMessage.toString()+ "</H2></center>");
		out.println("<body bgcolor=\"#f8f7cd\">");
		out.println("<h3 center>Status: " + status + "</h3> ");
		out.println("<h2 center>The following number of order(s) have been removed.</h2> ");

		out.println("</head> ");
		out.println("<table border=\"8\" bgcolor=\"#cccccc\" bordercolor=\"#FFCC99\" >"); 
		out.println("<tr>");
		out.println("<td>WarehouseId</td>");
		out.println("<td>DistrictId</td>");
		out.println("<td>Number of Orders Archived</td>");
		out.println("<td>Total Sales Amount of Orders Archived</td>");
		
		NumberFormat integerFormat = NumberFormat.getIntegerInstance();
		NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

	    for (int i=0; i < numberWarehouseIds;){	    	   	
    		for (int j=0; j < numberDistrictIds; ){	
    		    out.println("<tr>");
		    	out.println("<td>" + ordersArray[(numberDistrictIds * i) + j][0]+ "</td>");
		    	out.println("<td>" + ordersArray[(numberDistrictIds * i) + j][1]+ "</td>");
		    	out.println("<td>" + integerFormat.format(ordersArray[(numberDistrictIds * i) + j][2])+ "</td>");
		    	out.println("<td>" + currencyFormat.format(ordersArray[(numberDistrictIds * i) + j][3])+ "</td>");
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
		catch (Throwable e) {
			message = classname + ": general exception occurred trying to dispatch manual invalid input web page.";
			passedThrowable = e;
			this.webException(message,passedException,passedThrowable,throwable,request,response);
			e.printStackTrace();
			System.out.println(message);
			return;	
		}
	}
	
	public void webException(String theMessage, Exception theException, Throwable theThrowable, boolean caughtThrowable, HttpServletRequest request, HttpServletResponse response) {
		
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
			out.println("<center><H1>Order Archive Lite Results Form</H1></center>");
			out.println("<center><H1>JDBC 4.1 (RowSets), EL 3.0, Java 8 Version</H1></center>");
			out.println("<body bgcolor=\"#f8f7cd\">");
			out.println("<h3 center>Status: FAILED</h3> ");
			out.println("<h3 center>Message:</h3>");
			out.println("<td>" + theMessage + "</td>");
			out.println("<h3 center>Exception:</h3>");
			out.println("<td>" + theException + "</td>");
			out.print("<h3 center>Stack:</h3>");
			out.println("<td>" + irwwbase.getStackTrace(theThrowable) + "</td>");
			out.println("</body>");
			out.println("</html>"); 
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
			out.println("<center><H1>Order Archive Lite Results Form</H1></center>");
			out.println("<center><H1>JDBC 4.1, EL 3.0, Java 8 Version</H1></center>");
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
