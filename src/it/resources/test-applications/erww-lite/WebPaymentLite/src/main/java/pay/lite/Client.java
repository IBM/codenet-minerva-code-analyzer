package pay.lite;

/**
 * Insert the type's description here.
 * Creation date: (9/1/00 11:07:47 AM)
 * @author: Administrator
 */


import irwwbase.ExtendedRandom;
import irwwbase.IRWWBase;
//import irwwbase.IRWWProperties;
import irwwbase.MaxValues;


import java.util.Locale;

import javax.naming.InitialContext;
//comment out unused imports
//import pay.lite.PayInput;
//import pay.lite.PayOutput;





public class Client extends IRWWBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4507521046463939299L;
	private static java.lang.String _className;
	
	
	private InitialContext initialContext = null;
	
	private String portNumber = null;
	private String hostName = null;
	private static final String DEFAULT_PROTOCOL = "IIOP";
	private static final String DEFAULT_HOST = "localhost";

	public static int waitTime;

	private static final short remotePercentage = 85;
	private static final short idPercentage = 40;
	private static final int maxPay = 5000;

	private java.lang.String[] oput = new String[49];
	private java.lang.String[] oputForWebDeliveryAsync = new String[7];

	private short warehouseId;
	private short districtId;
	private short customerId;
	private short customerDistrictId;
	private short customerWarehouseId;
	private java.lang.String customerLastName;
	private java.lang.String historyAmount;
	//need to convert this to java.math.BigDecimal
	private java.lang.String historyDate;
	private boolean byLastName;
	private java.lang.String clientIdentifier;
	private boolean requiredRollback; // MD11544 Rollback
	private short entityType;
	private boolean useCmp;
	private boolean distributed;	
	private int db2LockSharing;
	private String status="FAILED";
	private String stack="none";
	

	protected Locale locale;
	

	/**
	 * PaymentClient constructor comment.
	 */
	public Client() {

		super();
		_className = (Client.class).getName();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (9/1/00 4:37:17 PM)
	 * @return paySessionPackage.PayInput
	 * @param db byte
	 */

	private PayInput generatePayInput(byte db) {
		ExtendedRandom rand = new ExtendedRandom();

		PayInput input = new PayInput();

		input.setWarehouseId((short) (rand.nextInt(1, MaxValues.warehouseId(db))));
		input.setDistrictId((short) (rand.nextInt(1, MaxValues.districtId(db))));

		if (rand.nextBoolProb(remotePercentage, 100)) {
			input.setCustomerDistrictId((short) (rand.nextInt(1, MaxValues.districtId(db))));

			if (MaxValues.warehouseId(db) == 1) {
				input.setCustomerWarehouseId((short) 1);
			} else {
				do {
					input.setCustomerWarehouseId((short) (rand.nextInt(1, MaxValues.warehouseId(db))));
				} while (input.getCustomerWarehouseId() == input.getWarehouseId());
			}
		} // if remote payment
		else {
			input.setCustomerDistrictId(input.getDistrictId());
			input.setCustomerWarehouseId(input.getWarehouseId());
		}

		if (rand.nextBoolProb(idPercentage, 100)) {
			input.setByLastName(false);
			input.setCustomerId((short) (rand.nextInt(1, MaxValues.customerId(db))));
			input.setCustomerLastName(" ");
		} else {
			input.setByLastName(true);
			input.setCustomerLastName(rand.nextName(db));
			input.setCustomerId((short) 0);
		}

		// neccessary for cast from double to BigDecimal !!! ???
		String tmpRandom = String.valueOf(rand.nextDouble(maxPay));
		input.setHistoryAmount(new java.math.BigDecimal(tmpRandom));

		/*String dateString = new String(new Date().toString());
		// actual format is  : Thu Dec 09 05:50:07 EST 1999
		// required format is: 1999-12-09-11.03.22.000000

		String year = dateString.substring(24);
		String _month = dateString.substring(4, 7);
		String day = dateString.substring(8, 10);
		String time = dateString.substring(11, 19).replace(':', '.');
		String month = "00";
		if (_month.equals("Dec"))
			month = "12";
		if (_month.equals("Jan"))
			month = "01";
		if (_month.equals("Feb"))
			month = "02";
		if (_month.equals("Mar"))
			month = "03";
		if (_month.equals("Apr"))
			month = "04";
		if (_month.equals("May"))
			month = "05";
		if (_month.equals("Jun"))
			month = "06";
		if (_month.equals("Jul"))
			month = "07";
		if (_month.equals("Aug"))
			month = "08";
		if (_month.equals("Sep"))
			month = "09";
		if (_month.equals("Oct"))
			month = "10";
		if (_month.equals("Nov"))
			month = "11";
			
		input.setHistoryDate(year + "-" + month + "-" + day + "-" + time + ".000000"); */
			
		java.sql.Timestamp historyDate = new java.sql.Timestamp(System.currentTimeMillis());

		input.setHistoryDate(historyDate.toString());

		// input.useQueryServices = 1;	  
		input.setClientIdentifier(" ");

		return input;

	}
	//**
	/* Insert the method's description here.
	 * Creation date: (9/1/00 4:38:11 PM)
	 * @return paySessionPackage.PayOutput
	 */
	private PayOutput generatePayOutput() {
		PayOutput output = new PayOutput();

		output.setCustomerId((short) 0);
		output.setWarehouseStreet1(" ");
		output.setWarehouseStreet2(" ");
		output.setWarehouseCity(" ");
		output.setWarehouseState(" ");
		output.setWarehouseZip(" ");
		output.setDistrictStreet1(" ");
		output.setDistrictStreet2(" ");
		output.setDistrictCity(" ");
		output.setDistrictState(" ");
		output.setDistrictZip(" ");
		output.setCustomerFirstName(" ");
		output.setCustomerMiddleName(" ");
		output.setCustomerLastName(" ");
		output.setCustomerStreet1(" ");
		output.setCustomerStreet2(" ");
		output.setCustomerCity(" ");
		output.setCustomerState(" ");
		output.setCustomerZip(" ");
		output.setCustomerPhone(" ");
		//	output.customerSince = " ";
		output.setCustomerCredit(" ");
		// float outCustomerCreditLimit;
		// float outCustomerDiscount;
		// float outCustomerBalance;
		output.setCustomerData(" ");
		output.setHistoryDate(" ");
		output.setMsg(" ");
		output.setWarehouseId((short) 0);
		output.setDistrictId((short) 0);
		output.setCustomerDistrictId((short) 0);
		output.setCustomerWarehouseId((short) 0);

		return output;

	}
	/**
	 * Insert the method's description here.
	 * Creation date: (9/1/00 4:39:28 PM)
	 * @param status2 
	 */
	public void genOput(PayOutput output, PayInput input, String status2) {

		this.oput[0] = "****************************************************************************";
		this.oput[1] = "*                       " + "PaySession Lite" +  "                      *";
		this.oput[2] = "*                                                                          *";
		this.oput[3] = "* HistoryDate = " + input.getHistoryDate();
		this.oput[4] = "* ";
		this.oput[5] = "* WarehouseID = " + input.getWarehouseId();
		this.oput[6] = "* WareHouseStreet1 = " + output.getWarehouseStreet1();
		this.oput[7] = "* WareHouseStreet2 = " + output.getWarehouseStreet2();
		this.oput[8] = "* WareHouseCity = "    + output.getWarehouseCity();
		this.oput[9] = "* WareHouseState = "   + output.getWarehouseState();
		this.oput[10] = "* WareHouseZip = "    + output.getWarehouseZip();
		this.oput[11] = "* ";
		this.oput[12] = "*  DistrictID= " + input.getDistrictId();
		this.oput[13] = "*  CustomerID= " + output.getCustomerId();
		this.oput[14] = "*  CustomerDistrictID= " + output.getCustomerDistrictId();
		this.oput[15] = "*  CustomerWareHouseID= " + output.getCustomerWarehouseId();
		this.oput[16] = "*  DistrictStreet1= " + output.getDistrictStreet1();
		this.oput[17] = "*  DistrictStreet2= " + output.getDistrictStreet2();
		this.oput[18] = "*  DistrictCity= " + output.getDistrictCity();
		this.oput[19] = "*  DistrictState= " + output.getDistrictState();
		this.oput[20] = "*  DistrictZip= " + output.getDistrictZip();
		this.oput[21] = "* ";
		this.oput[22] = "*  CustomerID= " + output.getCustomerId();
		this.oput[23] = "*  CustomerDistrictID= " + output.getCustomerDistrictId();
		this.oput[24] = "*  CustomerWareHouseID= " + output.getCustomerWarehouseId();
		this.oput[25] = "*  CustomerFirstName= " + output.getCustomerFirstName();
		this.oput[26] = "*  CustomerMiddleName= " + output.getCustomerMiddleName();
		this.oput[27] = "*  CustomerLastName= " + output.getCustomerLastName();
		this.oput[28] = "*  CustomerStreet1= " + output.getCustomerStreet1();
		this.oput[29] = "*  CustomerStreet2= " + output.getCustomerStreet2();
		this.oput[30] = "*  CustomerCity= " + output.getCustomerCity();
		this.oput[31] = "*  CustomerState= " + output.getCustomerState();
		this.oput[32] = "*  CustomerZip= " + output.getCustomerZip();
		this.oput[33] = "*  CustomerPhone= " + output.getCustomerPhone();
		this.oput[34] = "* ";
		this.oput[35] = "*  CustomerSince= " + output.getCustomerSince();
		this.oput[36] = "*  CustomerCredit= " + output.getCustomerCredit();
		this.oput[37] = "*  CustomerCreditLimit= " + output.getCustomerCreditLimit();
		this.oput[38] = "*  CustomerDiscount= " + output.getCustomerDiscount();
		this.oput[39] = "*  HistoryAmount= " + input.getHistoryAmount();
		this.oput[40] = "*  CustomerBalance= " + output.getCustomerBalance();
		if (output.getCustomerData()!=null){
			if (output.getCustomerData().length() > 10){
				this.oput[41] = "*  CustomerData= " + output.getCustomerData().substring(0, 9);
			} else {
				this.oput[41] = "*  CustomerData= " + output.getCustomerData();
			}
		} else {
			this.oput[41] = "*  CustomerData= null";
		}
		this.oput[42] = "*                                                                          *";
		this.oput[43] = "****************************************************************************";		    
		this.oput[44] = "";
		this.oput[45] = "* Status: "+status2;
		this.oput[46] = "";	
		this.oput[47] = "";
		this.oput[48] = "";
		
	}
	
	

	
	/**
	 * Insert the method's description here.
	 * Creation date: (10/3/2001 2:41:46 PM)
	 * @return java.lang.String[]
	 */
	public java.lang.String[] getOput() {
		return oput;
	}
	
	public java.lang.String[] getOputForWebDeliveryAsync() {
		return oputForWebDeliveryAsync;
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (9/1/00 4:39:00 PM)
	 */
	/* Commenting out private method that's never used
	private void initialDisplay(PayInput input) {
		System.out.println("****************************************************************************");
		System.out.println("*                      IRWW Payment (initial display)                      *");
		System.out.println("*                                                                          *");
		System.out.println("* Warehouse ID:          " + input.getWarehouseId());
		System.out.println("* District ID:           " + input.getDistrictId());
		if (input.isByLastName() == false) {
			System.out.println("* Customer ID:           " + input.getCustomerId());
		} else {
			System.out.println("* Customer ID:           n/a");
		}
		System.out.println("* Customer District ID:  " + input.getCustomerDistrictId());
		System.out.println("* Customer Warehouse ID: " + input.getCustomerWarehouseId());
		if (input.isByLastName() == true) {
			System.out.println("* Customer Last Name:    " + input.getCustomerLastName());
		} else {
			System.out.println("* Customer Last Name:    n/a");
		}
		System.out.println("* History Amount:        " + input.getHistoryAmount());
		System.out.println("* History Date:          " + input.getHistoryDate());
		System.out.println("*                                                                          *");
		System.out.println("****************************************************************************");

	}
	*/
	/**
	 * Insert the method's description here.
	 * Creation date: (9/1/00 11:22:20 AM)
	 * @return paymentSessionPackage.PaymentOutput
	 * @param input paymentSessionPackage.PaymentInput
	 * @exception jakarta.ejb.CreateException The exception description.
	 * @exception java.rmi.RemoteException The exception description.
	 * @exception javax.naming.NamingException The exception description.
	 * @exception jakarta.ejb.FinderException The exception description.
	 */
	/**
	 * Insert the method's description here.
	 * Creation date: (9/1/00 4:39:28 PM)
	 */
	/* Commenting out unused private method
	private void secondDisplay(PayOutput output, PayInput input) {
		System.out.println("****************************************************************************");
		System.out.println("*                       IRWW Pay (second display)                      *");
		System.out.println("*                                                                          *");
		System.out.println("* History Date:          " + input.getHistoryDate());
		System.out.println("* ");
		System.out.println("* Warehouse ID:          " + input.getWarehouseId());
		System.out.println("* Warehouse Street 1:    " + output.getWarehouseStreet1());
		System.out.println("* Warehouse Street 2:    " + output.getWarehouseStreet2());
		System.out.println("* Warehouse City:        " + output.getWarehouseCity());
		System.out.println("* Warehouse State:       " + output.getWarehouseState());
		System.out.println("* Warehouse Zip:         " + output.getWarehouseZip());
		System.out.println("* ");
		System.out.println("* District ID:           " + input.getDistrictId());
		System.out.println("* Customer ID:           " + output.getCustomerId());
		System.out.println("* Customer District ID:  " + output.getCustomerDistrictId());
		System.out.println("* Customer Warehouse ID: " + output.getCustomerWarehouseId());
		System.out.println("* District Street 1:     " + output.getDistrictStreet1());
		System.out.println("* District Street 2:     " + output.getDistrictStreet2());
		System.out.println("* District City:         " + output.getDistrictCity());
		System.out.println("* District State:        " + output.getDistrictState());
		System.out.println("* District Zip:          " + output.getDistrictZip());
		System.out.println("* ");
		System.out.println("* Customer ID:           " + output.getCustomerId());
		System.out.println("* Customer District ID:  " + output.getCustomerDistrictId());
		System.out.println("* Customer Warehouse ID: " + output.getCustomerWarehouseId());
		System.out.println("* Customer First Name:   " + output.getCustomerFirstName());
		System.out.println("* Customer Middle Name:  " + output.getCustomerMiddleName());
		System.out.println("* Customer Last Name:    " + output.getCustomerLastName());
		System.out.println("* Customer Street 1:     " + output.getCustomerStreet1());
		System.out.println("* Customer Street 2:     " + output.getCustomerStreet2());
		System.out.println("* Customer City:         " + output.getCustomerCity());
		System.out.println("* Customer State:        " + output.getCustomerState());
		System.out.println("* Customer Zip:          " + output.getCustomerZip());
		System.out.println("* Customer Phone:        " + output.getCustomerPhone());
		System.out.println("* ");
		System.out.println("* Customer Since:        " + output.getCustomerSince());
		System.out.println("* Customer Credit:       " + output.getCustomerCredit());
		System.out.println("* Customer Credit Limit: " + output.getCustomerCreditLimit());
		System.out.println("* Customer Discount:     " + output.getCustomerDiscount());
		System.out.println("* History Amount:        " + input.getHistoryAmount());
		System.out.println("* Customer Balance:      " + output.getCustomerBalance());
		System.out.println("* Customer Data:         " + output.getCustomerData());		
		System.out.println("* ");
		
		System.out.println("*                                                                          *");
		System.out.println("****************************************************************************");

	}
	*/

	/**
	 * Insert the method's description here.
	 * Creation date: (8/21/00 11:04:23 AM)
	 * @param newCachePriceQuoteSessionRef jakarta.ejb.EJBObject
	 */
	
	/**
	 * Insert the method's description here.
	 * Creation date: (10/3/2001 2:41:46 PM)
	 * @param newOput java.lang.String[]
	 */
	public void setOput(java.lang.String[] newOput) {
		oput = newOput;
	}

	
	/**
	 * Insert the method's description here.
	 * Creation date: (10/3/2001 2:42:14 PM)
	 */
	public PayInput web()  {

		
		//displayOut("Running Web Payment");
		/*displayOut(
			"Starting Time is: "
				+ new java.util.Date().toString()
				+ ", milliseconds: "
				+ String.valueOf(System.currentTimeMillis())); */
		
		status="FAILED";

		PayInput payInput = new PayInput();
		PayOutput payOutput = null;
		payOutput = this.generatePayOutput();
		byte database = 1;
		database = dbSize();
		//UserTransaction userTran = null;
		
		

		payInput = this.generatePayInput(database);
		
		debugOut("entityType is " + this.getEntityType() + " in Client class.");
		payInput.setEntityType(this.getEntityType());

		payInput.setRequiredRollback(getRequiredRollback());
		payInput.setDistributed(isDistributed());
		payInput.setDb2LockSharing(getDb2LockSharing());	
		return payInput;
		
		
		}		
		

		

	

	public void webWithoutGen()  {

	
		//displayOut("Running Web Payment");
		/*displayOut(
			"Starting Time is: "
				+ new java.util.Date().toString()
				+ ", milliseconds: "
				+ String.valueOf(System.currentTimeMillis())); */
		
		status="FAILED";

		PayInput payInput = new PayInput();
		//Client payClient = new Client();
		PayOutput payOutput = null;
		payOutput = this.generatePayOutput();
		

		debugOut("entityType is " + this.getEntityType() + " in Client class.");
		payInput.setEntityType(this.getEntityType());
		
		payInput.setDistributed(isDistributed());
		payInput.setDb2LockSharing(getDb2LockSharing());			

		//payInput.setByLastName(getByLastName());
		payInput.setRequiredRollback(getRequiredRollback());
		
		if (customerLastName == null || customerLastName.equals(" ") || customerLastName.length() == 0) {
			payInput.setByLastName(false);
		} else {
			payInput.setByLastName(true);
		}

		if (getWarehouseId() != 0) {
			payInput.setWarehouseId(getWarehouseId());
		}

		if (getDistrictId() != 0) {
			payInput.setDistrictId(getDistrictId());
		}

		if (getCustomerId() != 0) {
			payInput.setCustomerId(getCustomerId());
		}

		if (getCustomerDistrictId() != 0) {
			payInput.setCustomerDistrictId(getCustomerDistrictId());
		}

		if (getCustomerWarehouseId() != 0) {
			payInput.setCustomerWarehouseId(getCustomerWarehouseId());
		}

		if (getCustomerLastName() != null) {
			payInput.setCustomerLastName(getCustomerLastName());
		}

		if (getHistoryDate() != null) {
			payInput.setHistoryDate(getHistoryDate());
		}
		if (getClientIdentifier() != null) {
			payInput.setClientIdentifier(getClientIdentifier());
		}

		if (getHistoryAmount() != null) {
			payInput.setHistoryAmount(new java.math.BigDecimal(getHistoryAmount()));
		}
		
		
		
		

	}
	/**
	 * Gets the useCmp
	 * @return Returns a boolean
	 */
	public boolean getUseCmp() {
		return useCmp;
	}
	/**
	 * Sets the useCmp
	 * @param useCmp The useCmp to set
	 */
	public void setUseCmp(boolean useCmp) {
		this.useCmp = useCmp;
	}

	/**
	 * Gets the warehouseId
	 * @return Returns a short
	 */
	public short getWarehouseId() {
		return warehouseId;
	}
	/**
	 * Sets the warehouseId
	 * @param warehouseId The warehouseId to set
	 */
	public void setWarehouseId(short warehouseId) {
		this.warehouseId = warehouseId;
	}

	/**
	 * Gets the districtId
	 * @return Returns a short
	 */
	public short getDistrictId() {
		return districtId;
	}
	/**
	 * Sets the districtId
	 * @param districtId The districtId to set
	 */
	public void setDistrictId(short districtId) {
		this.districtId = districtId;
	}

	/**
	 * Gets the customerId
	 * @return Returns a short
	 */
	public short getCustomerId() {
		return customerId;
	}
	/**
	 * Sets the customerId
	 * @param customerId The customerId to set
	 */
	public void setCustomerId(short customerId) {
		this.customerId = customerId;
	}

	/**
	 * Gets the customerDistrictId
	 * @return Returns a short
	 */
	public short getCustomerDistrictId() {
		return customerDistrictId;
	}
	/**
	 * Sets the customerDistrictId
	 * @param customerDistrictId The customerDistrictId to set
	 */
	public void setCustomerDistrictId(short customerDistrictId) {
		this.customerDistrictId = customerDistrictId;
	}

	/**
	 * Gets the customerWarehouseId
	 * @return Returns a short
	 */
	public short getCustomerWarehouseId() {
		return customerWarehouseId;
	}
	/**
	 * Sets the customerWarehouseId
	 * @param customerWarehouseId The customerWarehouseId to set
	 */
	public void setCustomerWarehouseId(short customerWarehouseId) {
		this.customerWarehouseId = customerWarehouseId;
	}

	/**
	 * Gets the customerLastName
	 * @return Returns a java.lang.String
	 */
	public java.lang.String getCustomerLastName() {
		return customerLastName;
	}
	/**
	 * Sets the customerLastName
	 * @param customerLastName The customerLastName to set
	 */
	public void setCustomerLastName(java.lang.String customerLastName) {
		this.customerLastName = customerLastName;
	}

	/**
	 * Gets the historyDate
	 * @return Returns a java.lang.String
	 */
	public java.lang.String getHistoryDate() {
		return historyDate;
	}
	/**
	 * Sets the historyDate
	 * @param historyDate The historyDate to set
	 */
	public void setHistoryDate(java.lang.String historyDate) {
		this.historyDate = historyDate;
	}

	/**
	 * Gets the byLastName
	 * @return Returns a boolean
	 */
	public boolean getByLastName() {
		return byLastName;
	}
	/**
	 * Sets the byLastName
	 * @param byLastName The byLastName to set
	 */
	public void setByLastName(boolean byLastName) {
		this.byLastName = byLastName;
	}

	/**
	 * Gets the clientIdentifier
	 * @return Returns a java.lang.String
	 */
	public java.lang.String getClientIdentifier() {
		return clientIdentifier;
	}
	/**
	 * Sets the clientIdentifier
	 * @param clientIdentifier The clientIdentifier to set
	 */
	public void setClientIdentifier(java.lang.String clientIdentifier) {
		this.clientIdentifier = clientIdentifier;
	}

	/**
	 * Gets the requiredRollback
	 * @return Returns a boolean
	 */
	public boolean getRequiredRollback() {
		return requiredRollback;
	}
	/**
	 * Sets the requiredRollback
	 * @param requiredRollback The requiredRollback to set
	 */
	public void setRequiredRollback(boolean requiredRollback) {
		this.requiredRollback = requiredRollback;
	}

	/**
	 * Gets the historyAmount
	 * @return Returns a java.lang.String
	 */
	public java.lang.String getHistoryAmount() {
		double historyAmtDub = Double.valueOf(historyAmount.trim()).doubleValue();
		historyAmtDub = Math.floor(historyAmtDub * 100.0 + 0.5) / 100.0;
		historyAmount = Double.toString(historyAmtDub);
		return historyAmount;
	}
	/**
	 * Sets the historyAmount
	 * @param historyAmount The historyAmount to set
	 */
	public void setHistoryAmount(java.lang.String historyAmount) {
		this.historyAmount = historyAmount;
	}

	/**
	 * @return
	 */
	public String getPortNumber() {
		return portNumber;
	}

	/**
	 * @param string
	 */
	public void setPortNumber(String string) {
		portNumber = string;
	}

	/**
	 * @return
	 */
	public String getHostName() {
		return hostName;
	}

	/**
	 * @param string
	 */
	public void setHostName(String string) {
		hostName = string;
	}

	/**
	 * @return
	 */
	public static java.lang.String get_className() {
		return _className;
	}

	/**
	 * @return
	 */
	public static String getDEFAULT_HOST() {
		return DEFAULT_HOST;
	}

	/**
	 * @return
	 */
	public static String getDEFAULT_PROTOCOL() {
		return DEFAULT_PROTOCOL;
	}

	/**
	 * @return
	 */
	public static short getIdPercentage() {
		return idPercentage;
	}

	/**
	 * @return
	 */
	public static int getMaxPay() {
		return maxPay;
	}

	/**
	 * @return
	 */
	public static short getRemotePercentage() {
		return remotePercentage;
	}

	/**
	 * @return
	 */
	public static int getWaitTime() {
		return waitTime;
	}

	/**
	 * @return
	 */
	public InitialContext getInitialContext() {
		return initialContext;
	}

	/**
	 * @param string
	 */
	public static void set_className(java.lang.String string) {
		_className = string;
	}

	/**
	 * @param i
	 */
	public static void setWaitTime(int i) {
		waitTime = i;
	}

	/**
	 * @param context
	 */
	public void setInitialContext(InitialContext context) {
		initialContext = context;
	}
	
	/**
	 * @return
	 */
	public short getEntityType() {
		return entityType;
	}

	/**
	 * @param s
	 */
	public void setEntityType(short s) {
		entityType = s;
	}

	/**
	 * @return
	 */
	public boolean isDistributed() {
		return distributed;
	}

	/**
	 * @param b
	 */
	public void setDistributed(boolean b) {
		distributed = b;
	}

	/**
	 * @return the db2LockSharing
	 */
	public int getDb2LockSharing() {
		return db2LockSharing;
	}
	/**
	 * @param db2LockSharing the db2LockSharing to set
	 */
	public void setDb2LockSharing(int db2LockSharing) {
		this.db2LockSharing = db2LockSharing;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getStack() {
		return stack;
	}
	public void setStack(String stack) {
		this.stack = stack;
	}

}
