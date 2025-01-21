package deliverysession.ejb3;

import irwwbase.ExtendedRandom;
import irwwbase.IRWWBase;
import irwwbase.MaxValues;
import irwwbase.UserException;

import java.io.FileWriter;
import java.io.Serializable;
import java.sql.Timestamp;

import javax.naming.InitialContext;

import deliverysession.DeliveryInput;
import deliverysession.DeliveryInputInfo;
import deliverysession.DeliveryOutput;
import deliverysession.DeliverySessionFacade;
import deliverysession.DeliverySessionFacadeRemote;

import javax.rmi.PortableRemoteObject;


public class Client extends IRWWBase implements Serializable {

    private final static long serialVersionUID = 5432763151230402468L;

	public int runtimeMode = 1;
	public java.lang.String output;
	private short warehouseId = 0;
	private short carrierId = 0;
	
	private DeliveryInput input = null;
	
	private boolean distributed;
	
	private boolean useAsync;
	
	private boolean leakDeliveryResults = false;  // default = don't skip getting async delivery results
	
	private int asyncTimeout = 0;
	
	private java.lang.String[] oput = null;
	private String status="FAILED";
	private String stack="none";
	
	/**
	 * DeliveryClient constructor comment.
	 */
	public Client() {
		super();
	}
	
//	private boolean checkAsyncBean() {
//		String aBooleanString = System.getProperty("ERWW_EnableAsynchDelivery");
//		boolean useAsyncBean = false;
//		if (aBooleanString != null)
//			useAsyncBean = new Boolean(aBooleanString).booleanValue();
//		return useAsyncBean;
//	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (10/11/00 6:24:35 PM)
	 * @return java.lang.String
	 */
	public String className() {
		return null;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10/11/00 7:30:42 PM)
	 * @return deliverySessionPackage.DeliveryInputInfo
	 * @param db byte
	 */
	public DeliveryInput generateDeliveryInput(byte db) {

		ExtendedRandom rand = new ExtendedRandom();

		DeliveryInput input = new DeliveryInput();
		input.setCarrierId((short) (rand.nextInt(MaxValues.carrierId(db)+1)));
		carrierId = input.getCarrierId();
		input.setWarehouseId((short) rand.nextInt(1, MaxValues.warehouseId(db)));
		warehouseId = input.getWarehouseId();
		input.setMaxdistrictId(MaxValues.districtId(db));

		return input;

	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/2/2001 3:28:44 PM)
	 * @return java.lang.String
	 */
	public java.lang.String getOutput() {
		return output;
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (10/11/00 7:23:29 PM)
	 * @param input deliverySessionPackage.DeliveryInputInfo
	 */
	public void initialDisplay(DeliveryInput input) {

		debugOut("****************************************************************************");
		debugOut("*                 EJB IRWW  Delivery (initial display)                     *");
		debugOut("*                                                                          *");
		debugOut("*    Warehouse ID:     " + input.getWarehouseId());
		debugOut("*    Carrier ID:       " + input.getCarrierId());
		debugOut("*                                                                          *");
		debugOut("****************************************************************************");

	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/11/00 7:23:29 PM)
	 * @param input deliverySessionPackage.DeliveryInputInfo
	 */
	public void initialDisplay(DeliveryInputInfo input) {

		debugOut("****************************************************************************");
		debugOut("*                 EJB IRWW  Delivery (initial display)                     *");
		debugOut("*                                                                          *");
		debugOut("*    Warehouse ID:     " + input.inWarehouseId);
		debugOut("*    Carrier ID:       " + input.inCarrierId);
		debugOut("*                                                                          *");
		debugOut("****************************************************************************");

	}
		/**
	 * Insert the method's description here.
	 * Creation date: (10/11/00 7:25:57 PM)
	 */
	public void secondDisplay() {

		debugOut("****************************************************************************");
		debugOut("*                 EJB IRWW  Delivery (second display)                      *");
		debugOut("*                                                                          *");
		debugOut("* Execution Status: Delivery has been queued!");
		debugOut("*                                                                          *");
		debugOut("****************************************************************************");

	}
	
	
	private String[] genOput(DeliveryOutput output, String result) {

		int z = 0;
		int length = output.getSingleDeliveries().length;

		java.lang.String[] out = null;
		if (!output.getDeliveryResultsLeaked()) {
			out = new String[(length * 8) + 5];
		} else {
			out = new String[(length * 4) + 5];	
		}


		out[0] = "****************************************************************************";
		out[1] = "*                                                                           *";
		out[2] = result;
		out[3] = "*                                                                           *";
		out[4] = "****************************************************************************";
		int y = 4;

		//System.out.println("In genOput, length = " + length);
		if (length != 0) {
			for (int i=0; i<length; i++) {

				if (!output.getDeliveryResultsLeaked()){

					// Normal path - don't leak the async delivery results on purpose								
					if (output.getSingleDeliveries()[i] != null){
						if (output.getSingleDeliveries()[i].getCustomerId()!=0){
							out[y+1] = "* Delivered the following to Customer ID: " + output.getSingleDeliveries()[i].getCustomerId();
							out[y+2] = "*                                                                          *";
							out[y+3] = "* Warehouse ID: " + output.getSingleDeliveries()[i].getWarehouseId();			
							out[y+4] = "* District ID: " + output.getSingleDeliveries()[i].getDistrictId();
							out[y+5] = "* Order ID: " + output.getSingleDeliveries()[i].getOrderId();
							out[y+6] = "* Amount: " + output.getSingleDeliveries()[i].getAmount();
							out[y+7] = "*                                                                          *";
							out[y+8] = "****************************************************************************";
							y = y+8;
							z = z+1;
						}
					}
					
				} else {
					
					// Bad app testing path - async delivery results were leaked on purpose
					out[y+1] = "*                                                                          *";
					out[y+2] = "* Delivery results leaked (Bad application testing)";
					out[y+3] = "*                                                                          *";
					out[y+4] = "****************************************************************************";
					y = y+4;
					z = z+1;
				}
			}
		}
		if (z == 0){
			out[y+1] = "*                                                                          *";
			out[y+2] = "* All Districts were skipped since no deliveries needed to be made.        *";
			out[y+3] = "*                                                                          *";
			out[y+4] = "****************************************************************************";
		}

		return out;
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (10/2/2001 3:28:44 PM)
	 * @param newOutput java.lang.String
	 */
	public void setOutput(java.lang.String newOutput) {
		output = newOutput;
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (10/2/2001 3:24:38 PM)
	 */
	public void web() throws UserException {


		FileWriter resultFile = null;
		
		//Outer try block to make sure resources get closed
		try {
			status="FAILED";

			displayOut("Running Web Delivery - AutoGeneration of Input");
			displayOut(
					"Starting Time is: "
							+ new java.util.Date().toString()
							+ ", milliseconds: "
							+ String.valueOf(System.currentTimeMillis()));

			int rc = 0;

			byte database = (byte) 1;
			database = dbSize();
			String result = null;

			input = this.generateDeliveryInput(database);
			DeliveryOutput output = new DeliveryOutput();

			// shupert - no longer used.  Asynch methods Future.get timeout is now an ERWW system property
			//input.setAsyncTimeout(getAsyncTimeout());

			input.setDistributedEJBs( isDistributed() );
			input.setUse31Async( isUseAsync() );
			input.setLeakDeliveryResults( isLeakDeliveryResults() );   
			
			debugOut("ERWW - Initialize the result file");
			try {
				resultFile = new java.io.FileWriter("results.out");
			} catch (Exception ex) {
				System.out.println("ERROR: resultFile = new java.io.FileWriter(results.out) failed in web()");
				ex.printStackTrace();
				throw new UserException("ERROR: resultFile = new java.io.FileWriter(results.out) failed in web()");
			}

			logToResultFile("WebDeliveryLite_SyncAsyncEJB - Beginning delivery for Warehouse " + warehouseId + ", Carrier " + carrierId + '\n', resultFile);
	
			// temporarily close the log file so sub-method calls can append to it
			try {
				resultFile.close();
			} catch (Exception ex) {
				System.out.println("ERROR: resultFile.close() failed in web()");
				ex.printStackTrace();
				throw new UserException("ERROR: resultFile.close() failed in web()");
			}
						
			
			if (isDistributed()) {
				try {
					InitialContext ic = new InitialContext();
					DeliverySessionFacadeRemote desession = (DeliverySessionFacadeRemote) PortableRemoteObject.narrow(ic
							.lookup("java:comp/env/ejb/DeliverySessionRemote"), DeliverySessionFacadeRemote.class);
					output = desession.deliverySession(input);
					setStatus("SUCCESSFUL");

				} catch (UserException ux) {
					debugOut("UserException caught when executing desession.deliverySession(input) ");
					//loopThruExes(ex);
					//ex.printStackTrace();
					setStatus("FAILED: "+ux.getMessage());
					setStack(getStackTrace(ux));
					rc=8;
				} 

				catch (Exception ex) {
					debugOut("Exception caught when executing desession.deliverySession(input) ");
					//loopThruExes(ex);
					//ex.printStackTrace();
					setStatus("FAILED: "+ex.getMessage());	
					setStack(getStackTrace(ex));
					rc=8;
				}
			} else {
				//this is the local path
				debugOut("Before output = desession.deliverySession(input);");
				try {
					InitialContext ic = new InitialContext();
					DeliverySessionFacade slsession = (DeliverySessionFacade) ic
							.lookup("java:comp/env/ejb/DeliverySessionLocal");
					output = slsession.deliverySession(input);
					setStatus("SUCCESSFUL");

				} catch (UserException ux) {
					debugOut("UserException caught when executing desession.deliverySession(input) ");
					//loopThruExes(ex);
					//ex.printStackTrace();
					setStatus("FAILED: "+ux.getMessage());
					setStack(getStackTrace(ux));
					rc=8;
				}


				catch (Exception ex) {
					debugOut("Exception caught when executing desession.deliverySession(input) ");
					//loopThruExes(ex);
					//ex.printStackTrace();
					setStatus("FAILED: "+ex.getMessage());	
					setStack(getStackTrace(ex));
					rc=8;
				}
			}

			if (getStatus().indexOf("FAILED:")==-1) {
				oput = this.genOput(output, result);
			}


		} finally {
			
			// reopen log file writer to "append" to existing log file
			try {
				resultFile = new java.io.FileWriter("results.out", true);
			} catch (Exception ex) {
				System.out.println("ERROR: resultFile = new java.io.FileWriter(results.out, true) failed in web()");
				ex.printStackTrace();
				throw new UserException("ERROR: resultFile = new java.io.FileWriter(results.out, true) failed in web()");
			}		

			if (resultFile != null) {
				logToResultFile("WebDeliveryLite_SyncAsyncEJB - Finishing delivery for Warehouse " + warehouseId + ", Carrier " + carrierId + '\n', resultFile);

				try {
					resultFile.close();
				} catch (Exception ex) {
					debugOut("Exception caught when closing resultFile: " + ex.getMessage());					
				}

			}

		}

	}

	
	/**
	 * Insert the method's description here.
	 * Creation date: (10/2/2001 3:24:38 PM)
	 */
	public void webWithoutGen() throws UserException {

		FileWriter resultFile = null;
		
		//Outer try block to make sure resources get closed
		try {

			String result = null;
			status="FAILED";

			displayOut("Running Web Delivery - Manual Input");
			displayOut(
					"Starting Time is: "
							+ new java.util.Date().toString()
							+ ", milliseconds: "
							+ String.valueOf(System.currentTimeMillis()));

			int rc = 0;
			byte database = (byte) 1;
			database = dbSize();

			//Client client = new Client(); 
			input = new DeliveryInput();
			DeliveryOutput output = new DeliveryOutput();

			// shupert - no longer used.  Asynch methods Future.get timeout is now an ERWW system property
			// input.setAsyncTimeout(getAsyncTimeout());

			input.setDistributedEJBs( isDistributed() );
			input.setUse31Async( isUseAsync() );
			input.setLeakDeliveryResults( isLeakDeliveryResults() );


			if (database == 0) {
				input.setMaxdistrictId(3);
			} else {
				input.setMaxdistrictId(10);
			}

			if (getWarehouseId() != 0) {
				input.setWarehouseId(getWarehouseId());
			}

			if (getCarrierId() != 0) {
				input.setCarrierId(getCarrierId());
			}

			debugOut("ERWW - Initialize the result file");
			try {
				resultFile = new java.io.FileWriter("results.out");
			} catch (Exception ex) {
				System.out.println("ERROR: resultFile = new java.io.FileWriter(results.out) failed in webWithoutGen()");
				ex.printStackTrace();
				throw new UserException("ERROR: resultFile = new java.io.FileWriter(results.out) failed in webWithoutGent()");
			}


			logToResultFile("WebDeliveryLite_SyncAsyncEJB - Beginning delivery for Warehouse " + warehouseId + ", Carrier " + carrierId + '\n', resultFile);

			
			// temporarily close the log file so sub-method calls can append to it
			try {
				resultFile.close();
			} catch (Exception ex) {
				System.out.println("ERROR: resultFile.close() failed in webWithoutGen()");
				ex.printStackTrace();
				throw new UserException("ERROR: resultFile.close() failed in webWithoutGen()");
			}
			
			
			
			if (isDistributed()) {
				try {
					InitialContext ic = new InitialContext();

					DeliverySessionFacadeRemote desession = (DeliverySessionFacadeRemote) PortableRemoteObject.narrow(ic
							.lookup("java:comp/env/ejb/DeliverySessionRemote"), DeliverySessionFacadeRemote.class);
					output = desession.deliverySession(input);
					setStatus("SUCCESSFUL");

				} catch (UserException ex) {
					debugOut("UserException caught when executing desession.deliverySession(input) ");
					//loopThruExes(ex);
					//ex.printStackTrace();
					setStatus("FAILED: "+ex.getMessage());	
					setStack(getStackTrace(ex));
					rc=8;
				}

				catch (Exception ex) {
					debugOut("Exception caught when executing desession.deliverySession(input) ");
					//loopThruExes(ex);
					//ex.printStackTrace();
					setStatus("FAILED: "+ex.getMessage());	
					setStack(getStackTrace(ex));
					rc=8;
				}

			} else {
				// this is the local path
				debugOut("Before output = desession.deliverySession(input);");
				try {
					InitialContext ic = new InitialContext();
					DeliverySessionFacade slsession = (DeliverySessionFacade) ic
							.lookup("java:comp/env/ejb/DeliverySessionLocal");
					output = slsession.deliverySession(input);
					setStatus("SUCCESSFUL");

				} catch (UserException ex) {
					debugOut("UserException caught when executing desession.deliverySession(input) ");
					//loopThruExes(ex);
					//ex.printStackTrace();
					setStatus("FAILED: "+ex.getMessage());
					setStack(getStackTrace(ex));
					rc=8;
				}

				catch (Exception ex) {
					debugOut("Exception caught when executing desession.deliverySession(input) ");
					//loopThruExes(ex);
					//ex.printStackTrace();
					setStatus("FAILED: "+ex.getMessage());
					setStack(getStackTrace(ex));
					rc=8;
				}
			}

			if (getStatus().indexOf("FAILED:")==-1) {
				oput = this.genOput(output, result);
			}

		} finally {
			
			// reopen log file writer to "append" to existing log file
			try {
				resultFile = new java.io.FileWriter("results.out", true);
			} catch (Exception ex) {
				System.out.println("ERROR: resultFile = new java.io.FileWriter(results.out, true) failed in webWithoutGen()");
				ex.printStackTrace();
				throw new UserException("ERROR: resultFile = new java.io.FileWriter(results.out, true) failed in webWithoutGen()");
			}		
			
			if (resultFile != null) {
				logToResultFile("WebDeliveryLite_SyncAsyncEJB - Finishing delivery for Warehouse " + warehouseId + ", Carrier " + carrierId + '\n', resultFile);
				
				try {
				resultFile.close();
				} catch (Exception ex) {
					debugOut("Exception caught when closing resultFile: " + ex.getMessage());					
				}
				
			}

		}
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
	 * Gets the carrierId
	 * @return Returns a short
	 */
	public short getCarrierId() {
		return carrierId;
	}
	/**
	 * Sets the carrierId
	 * @param carrierId The carrierId to set
	 */
	public void setCarrierId(short carrierId) {
		this.carrierId = carrierId;
	}

	
	public void setRuntimeMode(int runtimeMode) {
		this.runtimeMode = runtimeMode;
	}

	public int getRuntimeMode() {
		return runtimeMode;
	}

	
	/**
	 * @return Returns the oput.
	 */
	public java.lang.String[] getOput() {
		return oput;
	}
	/**
	 * @param oput The oput to set.
	 */
	public void setOput(java.lang.String[] oput) {
		this.oput = oput;
	}

	public boolean isDistributed() {
		return distributed;
	}

	public void setDistributed(boolean distributed) {
		this.distributed = distributed;
	}

	public void setAsyncTimeout(int asyncTimeout) {
		this.asyncTimeout = asyncTimeout;
	}

	public int getAsyncTimeout() {
		return this.asyncTimeout;
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

	public boolean isUseAsync() {
		return useAsync;
	}

	public void setUseAsync(boolean useAsync) {
		this.useAsync = useAsync;
	}

	public boolean isLeakDeliveryResults() {
		return leakDeliveryResults;
	}

	public void setLeakDeliveryResults (boolean leakDeliveryResults) {
		this.leakDeliveryResults = leakDeliveryResults;
	}
	
	private void logToResultFile(String logline, java.io.FileWriter resultFile) throws UserException {

		String timeStamp = new String("");
		Timestamp timestamp = new Timestamp(java.lang.System.currentTimeMillis());
		timeStamp = timestamp.toString();	

		try {
			resultFile.write(timeStamp + " - " + logline);
		} catch (Exception ex) {
			System.out.println("ERROR: resultFile.write failed in Delivery Client logToResultFile method");
			ex.printStackTrace();
			throw new UserException("ERROR: resultFile.write failed Delivery Client logToResultFile method");
		}
		return; // method log

	}
	
}
