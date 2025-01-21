package neworder.jms.lite;

import irwwbase.ExtendedRandom;
import irwwbase.IRWWBase;
import irwwbase.MaxValues;
import irwwbase.UserException;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSConsumer;
import jakarta.jms.JMSContext;
import jakarta.jms.JMSSecurityRuntimeException;
import jakarta.jms.Message;
import jakarta.jms.ObjectMessage;
import jakarta.jms.Queue;
import jakarta.jms.TextMessage;
import jakarta.jms.Topic;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import newordersession.NewOrderInputInfo;
import newordersession.NewOrderOutputInfo;
import newordersession.NewOrderSingleItemInInfo;

/**
 *  This is the client bean that contains the logic for each JMS New Order transaction.
 *  There is a new instance of this bean created for each client session (thread) that is
 *  currently active.  Therefore, instance variables here do not need to be thread-safe
 *  since each instance of this bean is only used by one client thread at a time.
 */

public class NewOrderJMSClient extends IRWWBase implements Serializable {

	private final static long serialVersionUID = 1234511544618328126L;

	private int numberOfItems = 0;

	private short inWarehouseId;

	private short inDistrictId;

	private short inCustomerId;

	private int itemId1 = 0;
	private int itemId2 = 0;
	private int itemId3 = 0;
	private int itemId4 = 0;
	private int itemId5 = 0;
	private int itemId6 = 0;
	private int itemId7 = 0;
	private int itemId8 = 0;
	private int itemId9 = 0;
	private int itemId10 = 0;
	private int itemId11 = 0;
	private int itemId12 = 0;
	private int itemId13 = 0;
	private int itemId14 = 0;
	private int itemId15 = 0;

	private int quantity1 = 0;
	private int quantity2 = 0;
	private int quantity3 = 0;
	private int quantity4 = 0;
	private int quantity5 = 0;
	private int quantity6 = 0;
	private int quantity7 = 0;
	private int quantity8 = 0;
	private int quantity9 = 0;
	private int quantity10 = 0;
	private int quantity11 = 0;
	private int quantity12 = 0;
	private int quantity13 = 0;
	private int quantity14 = 0;
	private int quantity15 = 0;

	private short itemSupplyWarehouseId1 = 0;
	private short itemSupplyWarehouseId2 = 0;
	private short itemSupplyWarehouseId3 = 0;
	private short itemSupplyWarehouseId4 = 0;
	private short itemSupplyWarehouseId5 = 0;
	private short itemSupplyWarehouseId6 = 0;
	private short itemSupplyWarehouseId7 = 0;
	private short itemSupplyWarehouseId8 = 0;
	private short itemSupplyWarehouseId9 = 0;
	private short itemSupplyWarehouseId10 = 0;
	private short itemSupplyWarehouseId11 = 0;
	private short itemSupplyWarehouseId12 = 0;
	private short itemSupplyWarehouseId13 = 0;
	private short itemSupplyWarehouseId14 = 0;
	private short itemSupplyWarehouseId15 = 0;

	// The input and output data structures
	private NewOrderInputInfo input = null;	
	private NewOrderOutputInfo output = null;

	// JMS shared resources
	private ConnectionFactory connectionFactory = null;
	JMSContext jmsContext = null;
	private Queue requestQ = null;
	private Queue responseQ = null;
	private Topic logTopic = null;
	private String selector = null;
	private String correlationUUID = null;
	JMSConsumer topicConsumer = null;

	// Initialize the result messages to assume no messages are successfully
	// received from the NewOrderMDB that will run on the server.  Then as
	// each successful response from the MDB is receive, these will be written
	// over with good responses instead.  This lets us see how far the MDB actually
	// got before any error occurs in the server.
	private String resultMessage1 = "1) ERROR: message1 not received from MDB!";
	private String resultMessage2 = "2) ERROR: message2 not received from MDB!";
	private String resultMessage3 = "3) ERROR: message3 not received from MDB!";		


	/**
	 * NewOrderJMSClient constructor
	 */
	public NewOrderJMSClient() {
		super();
	}

	
	/**
	 * Initialize the various JMS resources required by this client bean to perform its business logic
	 */
	public void setupJMS() throws UserException {

		try {	
			
			// Lookup resources in naming
			try {

				debugOut("<<< Enter JMSNewOrderClient.setupJMS() >>>");

				InitialContext context = new InitialContext();

				// For JMS 2.0 lookup a generic connection factory that will be used for all connections 
				connectionFactory = (ConnectionFactory)context.lookup("java:comp/env/jms/NewOrderCF");

				// Lookup the request queue, response queue, and the log topic (where messages about the progress of 
				// the MDB's execution on the server will be logged).
				requestQ = (Queue) context.lookup("java:comp/env/jms/NewOrderRequestQueue");
				responseQ = (Queue) context.lookup("java:comp/env/jms/NewOrderReplyQueue");				
				logTopic = (Topic) context.lookup("java:comp/env/jms/NewOrderLogTopic");

				// Generate a unique UUID.  Since the queues and topics used in this transaction
				// are shared by all client instances and all server side MDBs running concurrently,
				// we use this unique identifier to separate out which messages are specifically for
				// this client instance.   All messages are tracked this way.  The message readers setup
				// "selectors" which will only read messages with the correct ID.
				correlationUUID = (UUID.randomUUID()).toString();
				debugOut("<<< Correlation UUID for New Order client bean is: " + correlationUUID + " >>>");
				selector = "JMSCorrelationID = '" + correlationUUID + "'";

			} catch (NamingException ne) {

				System.out.println("<<< NewOrderJMSClient caught naming exception in setupJMS(): " + ne.getMessage());
				if (ne.getCause() != null) {
					Throwable nestedExc = ne.getCause();
					throw new UserException("NewOrderJMSClient caught naming exception in setupJMS(): " + nestedExc.getMessage(), nestedExc.getMessage());
				} else {
					throw new UserException("NewOrderJMSClient caught naming exception in setupJMS(): " + ne.getMessage(), ne.getMessage());
				}

			} catch (Exception e) {
				System.out.println("<<< NewOrderJMSClient caught unexpected exception in setupJMS(): " + e.getMessage());
				if (e.getCause() != null) {
					Throwable nestedExc = e.getCause();
					throw new UserException("NewOrderJMSClient caught unexpected exception in setupJMS(): " + nestedExc.getMessage(), nestedExc.getMessage());
				} else {
					throw new UserException("NewOrderJMSClient caught unexpected exception in setupJMS(): " + e.getMessage(), e.getMessage());
				}
			}	

			
			// Attempt to connect to the message engine
			try {
				
				// For JMS 2.0, the JMSContext replaces the JMS 1.1 connection and session objects
				jmsContext = connectionFactory.createContext();

			} catch (JMSSecurityRuntimeException se) {
				System.out.println("<< NewOrderJMSClient caught JMSSecurityRuntimeException attempting to connect to the messageing engine in setupJMS(): " + se.getMessage());
				if (se.getCause() != null) {
					Throwable nestedExc = se.getCause();				nestedExc.printStackTrace();
					throw new UserException("NewOrderJMSClient caught JMSSecurityRuntimeException attempting to connect to the messageing engine in setupJMS(): " + nestedExc.getMessage(), nestedExc.getMessage());
				} else {
					throw new UserException("NewOrderJMSClient caught JMSSecurityRuntimeException attempting to connect to the messageing engine in setupJMS(): " + se.getMessage(), se.getMessage());
				}

			} catch (Exception e) {
				System.out.println("<< NewOrderJMSClient caught unexpected exception attempting to connect to the messageing engine in setupJMS(): " + e.getMessage());
				if (e.getCause() != null) {
					Throwable nestedExc = e.getCause();
					throw new UserException("NewOrderJMSClient caught unexpected exception attempting to connect to the messageing engine in setupJMS(): " + nestedExc.getMessage(), nestedExc.getMessage());
				} else {
					throw new UserException("NewOrderJMSClient caught unexpected exception attempting to connect to the messageing engine in setupJMS(): " + e.getMessage(), e.getMessage());
				}	
			} 
			
			
			// We need to subscribe to the log topic here before the MDB starts to run on
			// the server.  The topic Consumer must be created before the MDB publishes to the topic
			// or we risk missing messages (ie. publishing a message to a topic when there
			// are no active subscribers causes the message to be discarded by JMS).
			try {
				
				topicConsumer = jmsContext.createConsumer(logTopic, selector);	
				
			} catch (Exception e) {
				System.out.println("<< NewOrderJMSClient caught unexpected exception attempting to create Topic Consumer in setupJMS(): " + e.getMessage());
				if (e.getCause() != null) {
					Throwable nestedExc = e.getCause();
					throw new UserException("NewOrderJMSClient caught unexpected exception attempting to create Topic Consumer in setupJMS(): " + nestedExc.getMessage(), nestedExc.getMessage());
				} else {
					throw new UserException("NewOrderJMSClient caught unexpected exception attempting to create Topic Consumer in setupJMS(): " + e.getMessage(), e.getMessage());
				}	
			} 
	 					
		} finally {
			debugOut("<<< Exit JMSNewOrderClient.setupJMS() >>>");
		}

	}


	/**
	 * Cleanup the various JMS resources used by this client bean to perform its business logic
	 */
	private void cleanupJMS () throws UserException {

		try {
			debugOut("<<< Enter JMSNewOrderClient.cleanupJMS() >>>");
			
			if (jmsContext != null) {
				jmsContext.close();
			}

			if (topicConsumer != null) {
				topicConsumer.close();

			}

		} catch (Throwable t) {

			System.out.println("<< NewOrderJMSClient caught unexpected exception attempting to close resources in cleanupJMS(): " + t.getMessage());
			if (t.getCause() != null) {
				Throwable nestedExc = t.getCause();
				throw new UserException("NewOrderJMSClient caught unexpected exception attempting to close resources in cleanupJMS(): " + nestedExc.getMessage(), nestedExc.getMessage());
			} else {
				throw new UserException("NewOrderJMSClient caught unexpected exception attempting to close resources in cleanupJMS(): " + t.getMessage(), t.getMessage());
			}		       

		} finally {
			debugOut("<<< Exit JMSNewOrderClient.cleanupJMS() >>>");
		}
	}

	
	
	/**
	 * Generate random input data for a NewOrderTransaction.  Used when the client
	 * picks the auto-generation option on the New Order input panel.
	 */
	private NewOrderInputInfo generateNewOrderInput(byte db) {

		try {
			debugOut("<< Enter NewOrderJMSClient.generateNewOrderInput(), numberOfItems = " + numberOfItems + " >>");

			ExtendedRandom rand = new ExtendedRandom();

			numberOfItems = rand.nextInt(MaxValues.minOrderlinesPerOrder(db),
					MaxValues.orderlinesPerOrder(db));

			NewOrderInputInfo input = new NewOrderInputInfo();

			input.setInWareHouseId((short) rand.nextInt(1, MaxValues
					.warehouseId(db)));

			input.setInDistrictId((short) rand.nextInt(1, MaxValues
					.districtId(db)));

			input.setInCustomerId((short) rand.nextInt(1, MaxValues
					.customerId(db)));

			input.setNumberOfItems(numberOfItems);

			NewOrderSingleItemInInfo item[] = new NewOrderSingleItemInInfo[numberOfItems];

			for (int i = 0; i < numberOfItems; i++) {

				item[i] = new NewOrderSingleItemInInfo();
				item[i].setInItemId(rand.nextInt(1, MaxValues.itemId(db)));
				// item[ i ].setInItemId(item[i].getInItemId()+1); do not increment
				// any longer
				if (rand.nextBoolProb(1, 100) && MaxValues.warehouseId(db) != 1) { 
					// remote
					// warehouse
					// 1%
					// of
					// time
					do {
						item[i].setInItemSupplyWarehouseId((short) rand.nextInt(1,
								MaxValues.warehouseId(db)));
						// item[i].setInItemSupplyWarehouseId((short)(item[1].getInItemSupplyWarehouseId()+1));
					} while (item[i].getInItemSupplyWarehouseId() == input
							.getInWareHouseId());
				} else { // supplying warehouse = home warehouse
					item[i].setInItemSupplyWarehouseId(input.getInWareHouseId());
				}
				item[i].setInItemQuantity(rand.nextInt(1, 10));
				// item[i].setInItemQuantity((int)(item[i].getInItemQuantity()+1));
			} // for i
			input.setInItemList(item);
			input.setInOrderEntryDate(java.util.Calendar.getInstance());

			return input;

		} finally {
			debugOut("<< Exit NewOrderJMSClient.generateNewOrderInput() >>");
		}
	}


	/**
	 * Format the New Order output object into an array of strings for display on the results web page
	 */	
	public String[] formatOutput(NewOrderOutputInfo noOutput) {

		try {
			int x = numberOfItems * 8 + 22;
			String[] oput = new String[x];
			debugOut("< Enter NewOrderJMSClient.formatOuput(), length of array = " + oput.length + " >");

			oput[0] = "****************************************************************************";
			oput[1] = "*                 NewOrderJMSLite   ";
			oput[2] = "*                                                                          *";
			oput[3] = "*  WarehouseID=" + noOutput.getOutWarehouseId();
			oput[4] = "*  DistrictID=" + noOutput.getOutDistrictId();
			oput[5] = "*  " + "Date=" + java.util.Calendar.getInstance().getTime();
			oput[6] = "*  " + "CustomerID=" + noOutput.getOutCustomerId();
			oput[7] = "*  " + "CustomerName=" + noOutput.getOutCustomerLastName();
			oput[8] = "*  " + "CustomerCredit=" + noOutput.getOutCustomerCredit();
			oput[9] = "*  "	+ "CustomerDiscount=" + new BigDecimal(noOutput.getOutCustomerDiscount()).setScale(2,
					RoundingMode.HALF_UP) + "%";
			oput[10] = "*                                                                          *";
			oput[11] = "*  " + "OrderID=" + noOutput.getOutOrderId();
			oput[12] = "*  " + "NumItems=" + numberOfItems;
			oput[13] = "*  " + "WareHouseTax=" + noOutput.getOutWarehouseTax();
			oput[14] = "*  " + "DistrictTax=" + noOutput.getOutDistrictTax();
			oput[15] = "*                                                                          *";
			oput[16] = "*  " + "OrderDetails                                                     *";
			oput[17] = "*   ---------------------------------------------------------------------------"
					+ "----------------------------";
			
			int i = 0;
			int j = 0;
			for (; i < numberOfItems; i++) {
				j = i * 8;
				oput[j + 18] = ("*  "
						+ "SupplyWarehouseID=" + noOutput.getOutItemList()[i].getOutItemSupplyWarehouseId());
				oput[j + 19] = ("*  " + "ItemID=" + noOutput.getOutItemList()[i].getOutItemId());
				oput[j + 20] = ("*  " + "ItemName=" + noOutput.getOutItemList()[i].getOutItemName());
				oput[j + 21] = ("*  " + "ItemQty=" + noOutput.getOutItemList()[i].getOutItemQuantity());
				oput[j + 22] = ("*  " + "StockQty=" + noOutput.getOutItemList()[i].getOutStockQuantity());
				oput[j + 23] = ("*  " + "ItemPrice=" + (new BigDecimal(
						noOutput.getOutItemList()[i].getOutItemPrice()).setScale(2,
								RoundingMode.HALF_UP)));
				oput[j + 24] = ("*  "
						+ "ItemTotalPrice=" + (new BigDecimal(
								noOutput.getOutItemList()[i].getOutItemTotal()).setScale(2,
								RoundingMode.HALF_UP)));
				oput[j + 25] = "*   ---------------------------------------------------------------------------"
						+ "----------------------------";
			}
			
			oput[j + 26] = "*                                                                          *";
			oput[j + 27] = "*    " + "GrandTotal="
					+ new BigDecimal(noOutput.getOutTotal()).setScale(2,
					RoundingMode.HALF_UP);
			oput[j + 28] = "*                                                                          *";
			oput[j + 29] = "****************************************************************************";

			return oput;

		} finally {
			debugOut("< Exit NewOrderJMSClient.formatOuput() >");

		}
	}


	/**
	 * Generate random input data (ie. auto-generation) and then process the business logic for a 
	 * New Order transaction.
	 * 
	 * @throws UserException
	 */
	public void webAutoGenInput() throws UserException {

		try {

			debugOut("< Enter JMSNewOrderClient.webAutoGenInput() >");

			byte database = dbSize();

			// Generate random input data since client picked the autogen option
			NewOrderInputInfo generatedInput = generateNewOrderInput(database);
			setInput(generatedInput);

			//Now call client business logic
			webBusinessLogic();

		} finally {
			debugOut("< Exit JMSNewOrderClient.webAutoGenInput() >");
		}
	}


	/**
	 * Process the business logic for a New Order transaction based on the input data
	 * that was passed in from the client input.
	 * 
	 * @throws UserException
	 */
	public void webManualInput(NewOrderInputInfo input) throws UserException {


		try {

			debugOut("< Enter JMSNewOrderClient.webManualInput() >");

			// Save input gathered from client on servlet's input page
			setInput(input);
			numberOfItems = input.getNumberOfItems();

			// Now call client business logic
			webBusinessLogic();

		} finally {
			debugOut("< Exit JMSNewOrderClient.webManualInput() >");
		}
	}

	/**
	 * This is the main logic of the NewOrderJMSClient.  It processes the business logic for a New Order transaction.
	 *	  
	 * @throws UserException
	 */
	public void webBusinessLogic() throws UserException {

		long startTime = 0;

		// Outer try block to guarantee resources are closed
		try {
			
			debugOut("<< Enter JMSNewOrderClient.webBusinessLogic() >>");
			
			startTime = System.currentTimeMillis();

			// Initialize JMS resources.  This may throw UserException in which case we want to not
			// execute any more of the function in this method.  So don't catch the exception.
			setupJMS();


			// Build and send the New Order client request message that will trigger the MDB to start 
			// working.  The New Order input is serialized into a JMS message here which is sent off and
			// then later deserialized into an MDB that will process the client request data over on the server.
			try {

				debugOut("<< Queue name where NewOrderJMSClient request message will be sent: " + requestQ.getQueueName());

				Message requestMsg = jmsContext.createObjectMessage(input);

				debugOut("<< Sending New Order client request message now...");

				jmsContext.createProducer().setDeliveryMode(jakarta.jms.DeliveryMode.NON_PERSISTENT).setJMSReplyTo(responseQ).setJMSCorrelationID(correlationUUID).send(requestQ, requestMsg);

				debugOut("<< Dump of request message that NewOrderJMSClient sent: " + requestMsg.toString());


			} catch (Exception e) {
				System.out.println("<< NewOrderJMSClient caught exception attempting to send request messsage in webBusinessLogic() method: " + e.getMessage());
				if (e.getCause() != null) {
					Throwable nestedExc = e.getCause();
					throw new UserException("NewOrderJMSClient caught exception attempting to send request messsage in webBusinessLogic() method: " + nestedExc.getMessage(), nestedExc.getMessage());
				} else {
					throw new UserException("NewOrderJMSClient caught exception attempting to send request messsage in webBusinessLogic() method: " + e.getMessage(), e.getMessage());
				}			
			}


			debugOut("<< NewOrderJMSClient waiting up to 3 minutes to receive MDB message that it is connected to message engine, current time = "
					+ new java.util.Date().toString());

			// wait up to 3 minutes (180,000 milliseconds) to receive first logTopic message from MDB activity 
			long wait = 180000;

			try {
				Message inMessage = topicConsumer.receive(wait);
				
				if (inMessage != null) {

					// Check if the MDB was successful so far, or failed
					if ((inMessage.getBooleanProperty("RC")) == true) {

						// Successful so far, save the first logTopic message returned by NewOrderMDBBean
						setResultMessage1(((TextMessage) inMessage).getText());
						debugOut("<< NewOrderJMSClient received first logTopic message: " + ((TextMessage)inMessage).getText());
						debugOut("<< NewOrderJMSClient dumping first logTopic message received: " + inMessage.toString());

					} else {

						// MDB failed, throw exception so we stop any further processing.
						setResultMessage1(((TextMessage) inMessage).getText());
						debugOut("<< NewOrderJMSClient received failure message from MDB: " + ((TextMessage)inMessage).getText());
						debugOut("<< NewOrderJMSClient dumping MDB failure message received: " + inMessage.toString());
						throw new UserException("NewOrderJMSClient received failure message from MDB");


					}

				} else {
					// wait timed out an no message was received
					System.out.println("<< NewOrderJMSClient timed out waiting to receive first logTopic message, correlationUUID was: " + correlationUUID);
					System.out.println("<< Timeout might be a runtime issue, or first logTopic messsage may have taken too long if messaging engine is very slow");
					throw new UserException("NewOrderJMSClient timed out waiting to receive first logTopic message", "NewOrderJMSClient timed out waiting to receive first logTopic message");
				}

			} catch (UserException ue) {
				
				// Just rethrow UserExceptions.  Failure was already handled.
				throw ue;	
				
			} catch (Exception e) {	

				System.out.println("<< NewOrderJMSClient caught exception waiting on first logTopic message receive: " + e.getMessage());
				if (e.getCause() != null) {
					Throwable nestedExc = e.getCause();
					throw new UserException("NewOrderJMSClient caught exception waiting on first logTopic message receive:: " + nestedExc.getMessage(), nestedExc.getMessage());
				} else {
					throw new UserException("NewOrderJMSClient caught exception waiting on first logTopic message receive:: " + e.getMessage(), e.getMessage());
				}
			}


			debugOut("<< NewOrderJMSClient waiting up to 3 minutes to receive MDB message that incoming New Order work message was processed, current time = "
					+ new java.util.Date().toString());

			// wait up to 3 minutes (180,000 milliseconds) to receive second logTopic message from MDB activity 
			wait = 180000;

			try {
				Message inMessage = topicConsumer.receive(wait);
				
				if (inMessage != null) {

					// Check if the MDB was successful so far, or failed
					if ((inMessage.getBooleanProperty("RC")) == true) {

						// Successful so far, so save the second logTopic message returned by the NewOrderMDBBean
						setResultMessage2(((TextMessage) inMessage).getText());
						debugOut("<< NewOrderJMSClient received second logTopic message: " + resultMessage2);
						debugOut("<< NewOrderJMSClient dumping the second logTopic message received: " + inMessage.toString());

					} else {

						// MDB failed, throw exception so we stop any further processing.
						setResultMessage2(((TextMessage) inMessage).getText());
						debugOut("<< NewOrderJMSClient received failure message from MDB: " + ((TextMessage)inMessage).getText());
						debugOut("<< NewOrderJMSClient dumping MDB failure message received: " + inMessage.toString());
						throw new UserException("NewOrderJMSClient received failure message from MDB", "NewOrderJMSClient received failure message from MDB");
					}

				} else {

					// wait timed out and no message was received
					System.out.println("<< NewOrderJMSClient timed out waiting on second logTopic message, correlationUUID was: " + correlationUUID);
					System.out.println("<< NewOrderJMSClient timeout might be a runtime issue, or second logTopic messsage may have taken too long if messaging engine is very slow");
					throw new UserException("NewOrderJMSClient timed out waiting on second LogTopic message", "NewOrderJMSClient timed out waiting on second LogTopic message");
				}

				
			} catch (UserException ue) {
				
				// Just rethrow UserExceptions.  Failure was already handled.
				throw ue;	
				
			} catch (Exception e) {		
				System.out.println("<< NewOrderJMSClient caught exception waiting on second logTopic message receive: " + e.getMessage());
				if (e.getCause() != null) {
					Throwable nestedExc = e.getCause();
					throw new UserException("NewOrderJMSClient caught exception waiting on second logTopic message receive: " + nestedExc.getMessage(), nestedExc.getMessage());
				} else {
					throw new UserException("NewOrderJMSClient caught exception waiting on second logTopic message receive: " + e.getMessage(), e.getMessage());
				}
			}

			
			
			debugOut("<< NewOrderJMSClient waiting up to 3 minutes to receive MDB message that DB activity was completed, current time = "
					+ new java.util.Date().toString());

			// wait up to 3 minutes (180,000 milliseconds) to receive third logTopic message from MDB activity 
			wait = 180000;

			try {
				Message inMessage = topicConsumer.receive(wait);
				
				if (inMessage != null) {

					// Check if the MDB was successful so far, or failed
					if ((inMessage.getBooleanProperty("RC")) == true) {

						// Successful so far, save the third logTopic message returned by the NewOrderMDBBean.
						setResultMessage3(((TextMessage) inMessage).getText());
						debugOut("<< NewOrderJMSClient recieved third logTopic message: " + resultMessage3);
						debugOut("<< NewOrderJMSClient dumping third logTopic message received: " + inMessage.toString());

					} else {

						// MDB failed, throw exception so we stop any further processing.
						setResultMessage3(((TextMessage) inMessage).getText());
						debugOut("<< NewOrderJMSClient received failure message from MDB: " + ((TextMessage)inMessage).getText());
						debugOut("<< NewOrderJMSClient dumping MDB failure message received: " + inMessage.toString());
						throw new UserException("NewOrderJMSClient received failure message from MDB", ((TextMessage)inMessage).getText());															
					}

				} else {
					// wait timed out and no message was received
					System.out.println("<< NewOrderJMSClient timed out waiting on third logTopic message, correlationUUID was: " + correlationUUID);
					System.out.println("<< NewOrderJMSClient timeout might be a runtime issue, or third logTopic messsage may have taken too long if messaging engine is very slow");
					throw new UserException("NewOrderJMSClient timed out waiting on third logTopic message", "NewOrderJMSClient timed out waiting on third logTopic message");
				}

				
			} catch (UserException ue) {
				
				// Just rethrow UserExceptions.  Failure was already handled.
				throw ue;
				
			} catch (Exception e) {		
				System.out.println("<< NewOrderJMSClient caught exception waiting on third logTopic message receive: " + e.getMessage());
				if (e.getCause() != null) {
					Throwable nestedExc = e.getCause();
					throw new UserException("NewOrderJMSClient caught exception waiting on third logTopic message receive: " + nestedExc.getMessage(), nestedExc.getMessage());
				} else {
					throw new UserException("NewOrderJMSClient caught exception waiting on third logTopic message receive: " + e.getMessage(), e.getMessage());
				}
			}

			debugOut("<< NewOrderJMSClient waiting up to 3 minutes to receive final NewOrder output from transaction, current time = "
					+ new java.util.Date().toString());

			wait = 180000;   // 3 minutes = 180,000 milliseconds
			try {

				// Use the same selector for reading the final response queue					
				Message inMessage = jmsContext.createConsumer(responseQ, selector).receive(wait);

				if (inMessage != null) {

					if ((inMessage.getBooleanProperty("RC")) == true) {	

						// If RTC is true the MDB executed fine so keep going
						debugOut("<< NewOrderJMSClient return code in final result msg from NewOrderMDBBean's execution said successful");

						if (inMessage instanceof ObjectMessage) {

							// This message should be of type ObjectMessage which contains a
							// serialized NewOrderOuputInfo object coming back from the NewOrderMDBBean

							setOutput((NewOrderOutputInfo) ((ObjectMessage)inMessage).getObject() );						
							debugOut("<< NewOrderJMSClient dumping final result msg received from NewOrderMDBBean: " + inMessage.toString());

						} else {

							// throw exception for unexpected message type
							debugOut("<< NewOrderJMSClient received wrong message type for final result message from NewOrderMDBBean");
							throw new UserException("NewOrderJMSClient received wrong message type for final result message from NewOrderMDBBean",
									"NewOrderJMSClient received wrong message type for final result message from NewOrderMDBBean");
						}

					} else {

						// RC is false, the MDB had an exception and the exception
						// text was returned in response message we just receive.  Propagate
						// the exception text received to the client here.
						debugOut("<< NewOrderJMSClient return code in final result msg from NewOrderMDBBean's execution said failure occurred");

						if (inMessage instanceof TextMessage) {

							debugOut("<< NewOrderJMSClient dumping failure msg received from NewOrderMDBBean: " + inMessage.toString());
							debugOut("<< NewOrderJMSClient throwing exception with error text returned in failure msg from NewOrderMDBBean");
							throw new UserException ("Error text received from NewOrderMDBBean: " +  ((TextMessage)inMessage).getText(), ((TextMessage)inMessage).getText());

						} else {

							// throw exception for unexpected message type
							debugOut("<< NewOrderJMSClient received wrong message type for final result message from NewOrderMDBBean");
							throw new UserException("NewOrderJMSClient received wrong message type for final result message from NewOrderMDBBean",
									"NewOrderJMSClient received wrong message type for final result message from NewOrderMDBBean");
						}
					}			


				} else {
					// wait timed out and no message was received
					System.out.println("<< NewOrderJMSClient timed out waiting on final response message, correlationUUID was: " + correlationUUID);
					System.out.println("<< NewOrderJMSClient timeout might be a runtime issue, or final response messsage may have taken too long if messaging engine is very slow");
					throw new UserException("NewOrderJMSClient timed out waiting on final response message", "NewOrderJMSClient timed out waiting on final response message");
				}

			} catch (UserException ue) {
				
				// Just rethrow UserExceptions.  Failure already handled.
				throw ue;	
				
			} catch (Exception e) {
				System.out.println("<< NewOrderJMSClient caught exception waiting on final response msg: " + e.getMessage());
				if (e.getCause() != null) {
					Throwable nestedExc = e.getCause();
					throw new UserException("NewOrderJMSClient caught exception waiting on final response msg: " + nestedExc.getMessage(), nestedExc.getMessage());
				} else {
					throw new UserException("NewOrderJMSClient caught exception waiting on final response msg: " + e.getMessage(), e.getMessage());
				}				
			}

			
		} finally {

			// Make sure resources are closed out
			cleanupJMS();

			long elapsedTime = System.currentTimeMillis() - startTime; 
			// Make sure the debug exit msg is printed even if we had errors
			debugOut("<< Exit JMSNewOrderClient.webBusinessLogic(), elpased time = " + elapsedTime + " ms. >>");

		}

	} // end webBusinessLogic




	/**
	 * Gets the inCustomerId
	 * 
	 * @return Returns a short
	 */
	public short getInCustomerId() {
		return inCustomerId;
	}

	/**
	 * Sets the inCustomerId
	 * 
	 * @param inCustomerId
	 *            The inCustomerId to set
	 */
	public void setInCustomerId(short inCustomerId) {
		this.inCustomerId = inCustomerId;
	}

	/**
	 * Gets the inDistrictId
	 * 
	 * @return Returns a short
	 */
	public short getInDistrictId() {
		return inDistrictId;
	}

	/**
	 * Sets the inDistrictId
	 * 
	 * @param inDistrictId
	 *            The inDistrictId to set
	 */
	public void setInDistrictId(short inDistrictId) {
		this.inDistrictId = inDistrictId;
	}

	/**
	 * Gets the inWarehouseId
	 * 
	 * @return Returns a short
	 */
	public short getInWarehouseId() {
		return inWarehouseId;
	}

	/**
	 * Sets the inWarehouseId
	 * 
	 * @param inWarehouseId
	 *            The inWarehouseId to set
	 */
	public void setInWarehouseId(short inWarehouseId) {
		this.inWarehouseId = inWarehouseId;
	}

	/**
	 * Gets the itemId10
	 * 
	 * @return Returns a int
	 */
	public int getItemId10() {
		return itemId10;
	}

	/**
	 * Sets the itemId10
	 * 
	 * @param itemId10
	 *            The itemId10 to set
	 */
	public void setItemId10(int itemId10) {
		this.itemId10 = itemId10;
	}

	/**
	 * Gets the itemId11
	 * 
	 * @return Returns a int
	 */
	public int getItemId11() {
		return itemId11;
	}

	/**
	 * Sets the itemId11
	 * 
	 * @param itemId11
	 *            The itemId11 to set
	 */
	public void setItemId11(int itemId11) {
		this.itemId11 = itemId11;
	}

	/**
	 * Gets the itemId12
	 * 
	 * @return Returns a int
	 */
	public int getItemId12() {
		return itemId12;
	}

	/**
	 * Sets the itemId12
	 * 
	 * @param itemId12
	 *            The itemId12 to set
	 */
	public void setItemId12(int itemId12) {
		this.itemId12 = itemId12;
	}

	/**
	 * Gets the itemId13
	 * 
	 * @return Returns a int
	 */
	public int getItemId13() {
		return itemId13;
	}

	/**
	 * Sets the itemId13
	 * 
	 * @param itemId13
	 *            The itemId13 to set
	 */
	public void setItemId13(int itemId13) {
		this.itemId13 = itemId13;
	}

	/**
	 * Gets the itemId14
	 * 
	 * @return Returns a int
	 */
	public int getItemId14() {
		return itemId14;
	}

	/**
	 * Sets the itemId14
	 * 
	 * @param itemId14
	 *            The itemId14 to set
	 */
	public void setItemId14(int itemId14) {
		this.itemId14 = itemId14;
	}

	/**
	 * Gets the itemId15
	 * 
	 * @return Returns a int
	 */
	public int getItemId15() {
		return itemId15;
	}

	/**
	 * Sets the itemId15
	 * 
	 * @param itemId15
	 *            The itemId15 to set
	 */
	public void setItemId15(int itemId15) {
		this.itemId15 = itemId15;
	}

	/**
	 * Gets the itemId1
	 * 
	 * @return Returns a int
	 */
	public int getItemId1() {
		return itemId1;
	}

	/**
	 * Sets the itemId1
	 * 
	 * @param itemId1
	 *            The itemId1 to set
	 */
	public void setItemId1(int itemId1) {
		this.itemId1 = itemId1;
	}

	/**
	 * Gets the itemId2
	 * 
	 * @return Returns a int
	 */
	public int getItemId2() {
		return itemId2;
	}

	/**
	 * Sets the itemId2
	 * 
	 * @param itemId2
	 *            The itemId2 to set
	 */
	public void setItemId2(int itemId2) {
		this.itemId2 = itemId2;
	}

	/**
	 * Gets the itemId3
	 * 
	 * @return Returns a int
	 */
	public int getItemId3() {
		return itemId3;
	}

	/**
	 * Sets the itemId3
	 * 
	 * @param itemId3
	 *            The itemId3 to set
	 */
	public void setItemId3(int itemId3) {
		this.itemId3 = itemId3;
	}

	/**
	 * Gets the itemId4
	 * 
	 * @return Returns a int
	 */
	public int getItemId4() {
		return itemId4;
	}

	/**
	 * Sets the itemId4
	 * 
	 * @param itemId4
	 *            The itemId4 to set
	 */
	public void setItemId4(int itemId4) {
		this.itemId4 = itemId4;
	}

	/**
	 * Gets the itemId5
	 * 
	 * @return Returns a int
	 */
	public int getItemId5() {
		return itemId5;
	}

	/**
	 * Sets the itemId5
	 * 
	 * @param itemId5
	 *            The itemId5 to set
	 */
	public void setItemId5(int itemId5) {
		this.itemId5 = itemId5;
	}

	/**
	 * Gets the itemId6
	 * 
	 * @return Returns a int
	 */
	public int getItemId6() {
		return itemId6;
	}

	/**
	 * Sets the itemId6
	 * 
	 * @param itemId6
	 *            The itemId6 to set
	 */
	public void setItemId6(int itemId6) {
		this.itemId6 = itemId6;
	}

	/**
	 * Gets the itemId7
	 * 
	 * @return Returns a int
	 */
	public int getItemId7() {
		return itemId7;
	}

	/**
	 * Sets the itemId7
	 * 
	 * @param itemId7
	 *            The itemId7 to set
	 */
	public void setItemId7(int itemId7) {
		this.itemId7 = itemId7;
	}

	/**
	 * Gets the itemId8
	 * 
	 * @return Returns a int
	 */
	public int getItemId8() {
		return itemId8;
	}

	/**
	 * Sets the itemId8
	 * 
	 * @param itemId8
	 *            The itemId8 to set
	 */
	public void setItemId8(int itemId8) {
		this.itemId8 = itemId8;
	}

	/**
	 * Gets the itemId9
	 * 
	 * @return Returns a int
	 */
	public int getItemId9() {
		return itemId9;
	}

	/**
	 * Sets the itemId9
	 * 
	 * @param itemId9
	 *            The itemId9 to set
	 */
	public void setItemId9(int itemId9) {
		this.itemId9 = itemId9;
	}

	/**
	 * Gets the quantity10
	 * 
	 * @return Returns a int
	 */
	public int getQuantity10() {
		return quantity10;
	}

	/**
	 * Sets the quantity10
	 * 
	 * @param quantity10
	 *            The quantity10 to set
	 */
	public void setQuantity10(int quantity10) {
		this.quantity10 = quantity10;
	}

	/**
	 * Gets the quantity11
	 * 
	 * @return Returns a int
	 */
	public int getQuantity11() {
		return quantity11;
	}

	/**
	 * Sets the quantity11
	 * 
	 * @param quantity11
	 *            The quantity11 to set
	 */
	public void setQuantity11(int quantity11) {
		this.quantity11 = quantity11;
	}

	/**
	 * Gets the quantity12
	 * 
	 * @return Returns a int
	 */
	public int getQuantity12() {
		return quantity12;
	}

	/**
	 * Sets the quantity12
	 * 
	 * @param quantity12
	 *            The quantity12 to set
	 */
	public void setQuantity12(int quantity12) {
		this.quantity12 = quantity12;
	}

	/**
	 * Gets the quantity13
	 * 
	 * @return Returns a int
	 */
	public int getQuantity13() {
		return quantity13;
	}

	/**
	 * Sets the quantity13
	 * 
	 * @param quantity13
	 *            The quantity13 to set
	 */
	public void setQuantity13(int quantity13) {
		this.quantity13 = quantity13;
	}

	/**
	 * Gets the quantity14
	 * 
	 * @return Returns a int
	 */
	public int getQuantity14() {
		return quantity14;
	}

	/**
	 * Sets the quantity14
	 * 
	 * @param quantity14
	 *            The quantity14 to set
	 */
	public void setQuantity14(int quantity14) {
		this.quantity14 = quantity14;
	}

	/**
	 * Gets the quantity15
	 * 
	 * @return Returns a int
	 */
	public int getQuantity15() {
		return quantity15;
	}

	/**
	 * Sets the quantity15
	 * 
	 * @param quantity15
	 *            The quantity15 to set
	 */
	public void setQuantity15(int quantity15) {
		this.quantity15 = quantity15;
	}

	/**
	 * Gets the quantity1
	 * 
	 * @return Returns a int
	 */
	public int getQuantity1() {
		return quantity1;
	}

	/**
	 * Sets the quantity1
	 * 
	 * @param quantity1
	 *            The quantity1 to set
	 */
	public void setQuantity1(int quantity1) {
		this.quantity1 = quantity1;
	}

	/**
	 * Gets the quantity2
	 * 
	 * @return Returns a int
	 */
	public int getQuantity2() {
		return quantity2;
	}

	/**
	 * Sets the quantity2
	 * 
	 * @param quantity2
	 *            The quantity2 to set
	 */
	public void setQuantity2(int quantity2) {
		this.quantity2 = quantity2;
	}

	/**
	 * Gets the quantity3
	 * 
	 * @return Returns a int
	 */
	public int getQuantity3() {
		return quantity3;
	}

	/**
	 * Sets the quantity3
	 * 
	 * @param quantity3
	 *            The quantity3 to set
	 */
	public void setQuantity3(int quantity3) {
		this.quantity3 = quantity3;
	}

	/**
	 * Gets the quantity4
	 * 
	 * @return Returns a int
	 */
	public int getQuantity4() {
		return quantity4;
	}

	/**
	 * Sets the quantity4
	 * 
	 * @param quantity4
	 *            The quantity4 to set
	 */
	public void setQuantity4(int quantity4) {
		this.quantity4 = quantity4;
	}

	/**
	 * Gets the quantity5
	 * 
	 * @return Returns a int
	 */
	public int getQuantity5() {
		return quantity5;
	}

	/**
	 * Sets the quantity5
	 * 
	 * @param quantity5
	 *            The quantity5 to set
	 */
	public void setQuantity5(int quantity5) {
		this.quantity5 = quantity5;
	}

	/**
	 * Gets the quantity6
	 * 
	 * @return Returns a int
	 */
	public int getQuantity6() {
		return quantity6;
	}

	/**
	 * Sets the quantity6
	 * 
	 * @param quantity6
	 *            The quantity6 to set
	 */
	public void setQuantity6(int quantity6) {
		this.quantity6 = quantity6;
	}

	/**
	 * Gets the quantity7
	 * 
	 * @return Returns a int
	 */
	public int getQuantity7() {
		return quantity7;
	}

	/**
	 * Sets the quantity7
	 * 
	 * @param quantity7
	 *            The quantity7 to set
	 */
	public void setQuantity7(int quantity7) {
		this.quantity7 = quantity7;
	}

	/**
	 * Gets the quantity8
	 * 
	 * @return Returns a int
	 */
	public int getQuantity8() {
		return quantity8;
	}

	/**
	 * Sets the quantity8
	 * 
	 * @param quantity8
	 *            The quantity8 to set
	 */
	public void setQuantity8(int quantity8) {
		this.quantity8 = quantity8;
	}

	/**
	 * Gets the quantity9
	 * 
	 * @return Returns a int
	 */
	public int getQuantity9() {
		return quantity9;
	}

	/**
	 * Sets the quantity9
	 * 
	 * @param quantity9
	 *            The quantity9 to set
	 */
	public void setQuantity9(int quantity9) {
		this.quantity9 = quantity9;
	}


	/**
	 * Gets the itemSupplyWarehouseId10
	 * 
	 * @return Returns a short
	 */
	public short getItemSupplyWarehouseId10() {
		return itemSupplyWarehouseId10;
	}

	/**
	 * Sets the itemSupplyWarehouseId10
	 * 
	 * @param itemSupplyWarehouseId10
	 *            The itemSupplyWarehouseId10 to set
	 */
	public void setItemSupplyWarehouseId10(short itemSupplyWarehouseId10) {
		this.itemSupplyWarehouseId10 = itemSupplyWarehouseId10;
	}

	/**
	 * Gets the itemSupplyWarehouseId11
	 * 
	 * @return Returns a short
	 */
	public short getItemSupplyWarehouseId11() {
		return itemSupplyWarehouseId11;
	}

	/**
	 * Sets the itemSupplyWarehouseId11
	 * 
	 * @param itemSupplyWarehouseId11
	 *            The itemSupplyWarehouseId11 to set
	 */
	public void setItemSupplyWarehouseId11(short itemSupplyWarehouseId11) {
		this.itemSupplyWarehouseId11 = itemSupplyWarehouseId11;
	}

	/**
	 * Gets the itemSupplyWarehouseId12
	 * 
	 * @return Returns a short
	 */
	public short getItemSupplyWarehouseId12() {
		return itemSupplyWarehouseId12;
	}

	/**
	 * Sets the itemSupplyWarehouseId12
	 * 
	 * @param itemSupplyWarehouseId12
	 *            The itemSupplyWarehouseId12 to set
	 */
	public void setItemSupplyWarehouseId12(short itemSupplyWarehouseId12) {
		this.itemSupplyWarehouseId12 = itemSupplyWarehouseId12;
	}

	/**
	 * Gets the itemSupplyWarehouseId13
	 * 
	 * @return Returns a short
	 */
	public short getItemSupplyWarehouseId13() {
		return itemSupplyWarehouseId13;
	}

	/**
	 * Sets the itemSupplyWarehouseId13
	 * 
	 * @param itemSupplyWarehouseId13
	 *            The itemSupplyWarehouseId13 to set
	 */
	public void setItemSupplyWarehouseId13(short itemSupplyWarehouseId13) {
		this.itemSupplyWarehouseId13 = itemSupplyWarehouseId13;
	}

	/**
	 * Gets the itemSupplyWarehouseId14
	 * 
	 * @return Returns a short
	 */
	public short getItemSupplyWarehouseId14() {
		return itemSupplyWarehouseId14;
	}

	/**
	 * Sets the itemSupplyWarehouseId14
	 * 
	 * @param itemSupplyWarehouseId14
	 *            The itemSupplyWarehouseId14 to set
	 */
	public void setItemSupplyWarehouseId14(short itemSupplyWarehouseId14) {
		this.itemSupplyWarehouseId14 = itemSupplyWarehouseId14;
	}

	/**
	 * Gets the itemSupplyWarehouseId15
	 * 
	 * @return Returns a short
	 */
	public short getItemSupplyWarehouseId15() {
		return itemSupplyWarehouseId15;
	}

	/**
	 * Sets the itemSupplyWarehouseId15
	 * 
	 * @param itemSupplyWarehouseId15
	 *            The itemSupplyWarehouseId15 to set
	 */
	public void setItemSupplyWarehouseId15(short itemSupplyWarehouseId15) {
		this.itemSupplyWarehouseId15 = itemSupplyWarehouseId15;
	}

	/**
	 * Gets the itemSupplyWarehouseId1
	 * 
	 * @return Returns a short
	 */
	public short getItemSupplyWarehouseId1() {
		return itemSupplyWarehouseId1;
	}

	/**
	 * Sets the itemSupplyWarehouseId1
	 * 
	 * @param itemSupplyWarehouseId1
	 *            The itemSupplyWarehouseId1 to set
	 */
	public void setItemSupplyWarehouseId1(short itemSupplyWarehouseId1) {
		this.itemSupplyWarehouseId1 = itemSupplyWarehouseId1;
	}

	/**
	 * Gets the itemSupplyWarehouseId2
	 * 
	 * @return Returns a short
	 */
	public short getItemSupplyWarehouseId2() {
		return itemSupplyWarehouseId2;
	}

	/**
	 * Sets the itemSupplyWarehouseId2
	 * 
	 * @param itemSupplyWarehouseId2
	 *            The itemSupplyWarehouseId2 to set
	 */
	public void setItemSupplyWarehouseId2(short itemSupplyWarehouseId2) {
		this.itemSupplyWarehouseId2 = itemSupplyWarehouseId2;
	}

	/**
	 * Gets the itemSupplyWarehouseId3
	 * 
	 * @return Returns a short
	 */
	public short getItemSupplyWarehouseId3() {
		return itemSupplyWarehouseId3;
	}

	/**
	 * Sets the itemSupplyWarehouseId3
	 * 
	 * @param itemSupplyWarehouseId3
	 *            The itemSupplyWarehouseId3 to set
	 */
	public void setItemSupplyWarehouseId3(short itemSupplyWarehouseId3) {
		this.itemSupplyWarehouseId3 = itemSupplyWarehouseId3;
	}

	/**
	 * Gets the itemSupplyWarehouseId4
	 * 
	 * @return Returns a short
	 */
	public short getItemSupplyWarehouseId4() {
		return itemSupplyWarehouseId4;
	}

	/**
	 * Sets the itemSupplyWarehouseId4
	 * 
	 * @param itemSupplyWarehouseId4
	 *            The itemSupplyWarehouseId4 to set
	 */
	public void setItemSupplyWarehouseId4(short itemSupplyWarehouseId4) {
		this.itemSupplyWarehouseId4 = itemSupplyWarehouseId4;
	}

	/**
	 * Gets the itemSupplyWarehouseId5
	 * 
	 * @return Returns a short
	 */
	public short getItemSupplyWarehouseId5() {
		return itemSupplyWarehouseId5;
	}

	/**
	 * Sets the itemSupplyWarehouseId5
	 * 
	 * @param itemSupplyWarehouseId5
	 *            The itemSupplyWarehouseId5 to set
	 */
	public void setItemSupplyWarehouseId5(short itemSupplyWarehouseId5) {
		this.itemSupplyWarehouseId5 = itemSupplyWarehouseId5;
	}

	/**
	 * Gets the itemSupplyWarehouseId6
	 * 
	 * @return Returns a short
	 */
	public short getItemSupplyWarehouseId6() {
		return itemSupplyWarehouseId6;
	}

	/**
	 * Sets the itemSupplyWarehouseId6
	 * 
	 * @param itemSupplyWarehouseId6
	 *            The itemSupplyWarehouseId6 to set
	 */
	public void setItemSupplyWarehouseId6(short itemSupplyWarehouseId6) {
		this.itemSupplyWarehouseId6 = itemSupplyWarehouseId6;
	}

	/**
	 * Gets the itemSupplyWarehouseId7
	 * 
	 * @return Returns a short
	 */
	public short getItemSupplyWarehouseId7() {
		return itemSupplyWarehouseId7;
	}

	/**
	 * Sets the itemSupplyWarehouseId7
	 * 
	 * @param itemSupplyWarehouseId7
	 *            The itemSupplyWarehouseId7 to set
	 */
	public void setItemSupplyWarehouseId7(short itemSupplyWarehouseId7) {
		this.itemSupplyWarehouseId7 = itemSupplyWarehouseId7;
	}

	/**
	 * Gets the itemSupplyWarehouseId8
	 * 
	 * @return Returns a short
	 */
	public short getItemSupplyWarehouseId8() {
		return itemSupplyWarehouseId8;
	}

	/**
	 * Sets the itemSupplyWarehouseId8
	 * 
	 * @param itemSupplyWarehouseId8
	 *            The itemSupplyWarehouseId8 to set
	 */
	public void setItemSupplyWarehouseId8(short itemSupplyWarehouseId8) {
		this.itemSupplyWarehouseId8 = itemSupplyWarehouseId8;
	}

	/**
	 * Gets the itemSupplyWarehouseId9
	 * 
	 * @return Returns a short
	 */
	public short getItemSupplyWarehouseId9() {
		return itemSupplyWarehouseId9;
	}

	/**
	 * Sets the itemSupplyWarehouseId9
	 * 
	 * @param itemSupplyWarehouseId9
	 *            The itemSupplyWarehouseId9 to set
	 */
	public void setItemSupplyWarehouseId9(short itemSupplyWarehouseId9) {
		this.itemSupplyWarehouseId9 = itemSupplyWarehouseId9;
	}

	public NewOrderInputInfo getInput() {
		return input;
	}

	public void setInput(NewOrderInputInfo input) {
		this.input = input;
	}

	public NewOrderOutputInfo getOutput() {
		return output;
	}

	public void setOutput(NewOrderOutputInfo output) {
		this.output = output;
	}

	public String getResultMessage1() {
		return resultMessage1;
	}

	public void setResultMessage1(String msg1) {
		resultMessage1 = "1) " + msg1;
	}

	public String getResultMessage2() {
		return resultMessage2;
	}

	public void setResultMessage2(String msg2) {
		resultMessage2 = "2) " + msg2;
	}

	public String getResultMessage3() {
		return resultMessage3;
	}

	public void setResultMessage3(String msg3) {
		resultMessage3 = "3) " + msg3;
	}

}

