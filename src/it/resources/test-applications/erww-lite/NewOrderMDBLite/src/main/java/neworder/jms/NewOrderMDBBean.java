package neworder.jms;

import irwwbase.UserException;

import java.util.concurrent.atomic.AtomicInteger;

import jakarta.annotation.Resource;
import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.EJB;
import jakarta.ejb.MessageDriven;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSContext;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.ObjectMessage;
import jakarta.jms.Queue;
import jakarta.jms.TextMessage;
import jakarta.jms.Topic;

import newordersession.NewOrderInputInfo;
import newordersession.NewOrderOutputInfo;
import newordersession.NewOrderSessionBean;
import newordersession.NewOrderSingleItemOutInfo;

/**
 * Bean implementation class for Enterprise Bean: NewOrderMDBBean
 */
@MessageDriven(activationConfig =
{
		@ActivationConfigProperty(propertyName="destinationType",propertyValue="jakarta.jms.Queue"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode",propertyValue = "Auto-acknowledge"),
		@ActivationConfigProperty(propertyName="destination",propertyValue="jms/NewOrderRequestQ")
},name="NewOrderMDBBean")
public class NewOrderMDBBean implements MessageListener {

	// Reference to EJB with JPA logic for the New Order transaction
	@EJB
    NewOrderSessionBean newOrderSessionBean;
	
	// Class level counter of number of active instances currently running.  This must be atomic since it is shared
	// by all instances (ie. multiple threads).
	static AtomicInteger NewOrderMDBInFlightCounter = new AtomicInteger(0);

	// Check if the current message failed previously and is
	// now being retried (ie. re-delivered) by the messaging engine.
	// Normally the JMSXDeliveryCount is 1.  Anything above that 
	// indicates a retry is in progress.
	private int msgDeliveryCount = 0;

	@Resource(name="jms/NewOrderCF")
	private ConnectionFactory connectionFactory;

	@Resource(name="jms/NewOrderLogTopic")
	private Topic newOrderLogTopic = null;


	/**
	 * ejbCreate
	 */
	public void ejbCreate() {
		debugOut("NewOrderMDBBean instance created in Server: " + this);
	}


	/**
	 * onMessage - This is the main logic of the MDB.  It is called each time the message engine
	 *             sees a message arrive on the NewOrderRequestQ.
	 */
	public void onMessage(jakarta.jms.Message inMessage) {
		

		// Time when the onMessage is invoked
		long startTime = 0;

		// The input and output structures
		NewOrderInputInfo newOrderInput = null;
		NewOrderOutputInfo newOrderOutput = null;

		// The queues and topic are shared by many client instances (threads) and many MDB instances (threads).
		// We use a UUID to correlate all the work for a single client request as messages flow to the server
		// and back to the client.  Since the queues and topics are shared we build selectors based on this UUID
		// to make sure only the messages intended for the specific client instance are pulled off the queues and
		// topic.
		String correlationUUID = null;

		// The reply queue name will be obtained from the request message that starts the MDB activity.
		Queue replyQ = null;

		// JMSContext is used throughout onMessage, and closed in finally block.  In JMS 2.0
		// the context object replaces the connection and session objects from JMS 1.1.
		JMSContext jmsContext = null;

		try{

			// Notes about exception handing strategy.  This MDB may run on a server that is different
			// from the server where the original web app request arrived.   Whenever possible, we try
			// to return error information to the client web app using a reply message.   However, that 
			// requires JMS resources like the JMSContext and JMSProducer to be working.  If we can't
			// connect to the message engine, all we will be able to do is log information about the failure
			// in the server's log.  Sending info back to the client web app will not be an option at times.
			// In these cases the client web app will get a timeout waiting on a reply message that never
			// arrives.

			
			// See if we can connect to the message engine.  If exceptions happen here we won't be
			// able to send a message back to the client.  All we can do is log what happened and 
			// let the client timeout waiting for a reply.
			try {
				debugOut("< Enter NewOrderMDBBean.onMessage() for MDB: " + this + " >");

				// Record that an MDB instance is active (ie. in flight)
				startTime = System.currentTimeMillis();
				JMS_MDB_debugOut("  >> JMSNewOrderMDBInFlightCounter = " + NewOrderMDBInFlightCounter.incrementAndGet() + " MDBptr = " + this + " TimeInMilliSec = " + startTime);

				// For JMS 2.0 the jmsContext object replaces the connections and sessions used in JMS 1.1.
				jmsContext = connectionFactory.createContext();

				// First thing in onMessage, pull the correlation ID from the message we received because we need to
				// use it as the correlation Id for any reply message we send back to the web client
				correlationUUID = inMessage.getJMSCorrelationID();
				debugOut("< NewOrderMDBBean.onMessage(), for MDB: " + this + ", UUID to use for reply correlation is: " + correlationUUID + " >");

				// Also, grab the replyQ from the incoming message so we know where to send our final results
				replyQ = (Queue)inMessage.getJMSReplyTo();

				// Use builder pattern to send Log message to let client know that we successfully connected to JMS
				long elapsedTime = System.currentTimeMillis() - startTime;
				TextMessage textMsg = jmsContext.createTextMessage();
				textMsg.setText("MDB successfully connected to message engine (elapsed time = " + elapsedTime + " ms.)");
				jmsContext.createProducer().setDeliveryMode(jakarta.jms.DeliveryMode.NON_PERSISTENT).setJMSCorrelationID(correlationUUID).setProperty("RC", true).send(newOrderLogTopic, textMsg);
				debugOut("< NewOrderMDBBean dumping first log message sent back to client: " + textMsg );

			} catch (Throwable t) {

				// All exceptions are unexpected errors and we cannot continue.  Log what we can and then rethrow the exception so 
				// that we skip the remainder of processing in onMessage().
				System.out.println("Unexpected exception in NewOrderMDBBean.onMessage() while trying to connect to message engine: " + t);
				throw t;
			}

			// Retrieve details about the incoming message and setup the JMS reply queue to send results
			// back to the client web app. 
			try {
				newOrderInput = processReceivedMsg(inMessage);

				// See if this message had previously failed and was redelivered (ie. delivery count > 1).
				msgDeliveryCount = inMessage.getIntProperty("JMSXDeliveryCount");			
				if (msgDeliveryCount > 1){
					JMS_MDB_debugOut("  >> NewOrderJMSMDBBean is working on a Redelivered Msg!  JMSXDeliveryCount = " + msgDeliveryCount + " MDBptr = " + this);
				}

			} catch (UserException ue) {

				// Normal issues (expected errors) will be thrown out of processReceivedMessage(msg) method as s
				// Since we are connected to the message engine now, we can hopefully return a failure message to the client.
				// We will also rethrow the exception so that we skip the remainder of processing in onMessage().
				System.out.println("NewOrderMDBBean caught UserException while attempting to process input message: " + ue);

				// Use builder pattern to send Log message to let client know that we failed (ie. RC=false)
				TextMessage textMsg = jmsContext.createTextMessage();
				textMsg.setText("MDB failed with exception: " + ue.getMessage());
				jmsContext.createProducer().setDeliveryMode(jakarta.jms.DeliveryMode.NON_PERSISTENT).setJMSCorrelationID(correlationUUID).setProperty("RC", false).send(newOrderLogTopic, textMsg);
				debugOut("< NewOrderMDBBean dumping failure message sent back to client: " + textMsg );	
				throw ue;

			} catch (Throwable t) {

				// All other exceptions are unexpected errors.  Since we are connected to the message engine now,
				// we can hopefully return a failure message to the client.  We will also rethrow the exception so
				// that we skip the remainder of processing in onMessage().
				System.out.println("NewOrderMDBBean caught unexpected exception while attempting to process input message: " + t);

				// Use builder pattern to send Log message to let client know that we failed (ie. RC=false)
				TextMessage textMsg = jmsContext.createTextMessage();
				textMsg.setText("MDB failed with exception: " + t.getMessage());
				jmsContext.createProducer().setDeliveryMode(jakarta.jms.DeliveryMode.NON_PERSISTENT).setJMSCorrelationID(correlationUUID).setProperty("RC", false).send(newOrderLogTopic, textMsg);
				debugOut("< NewOrderMDBBean dumping failure message sent back to client: " + textMsg );	
				throw t;
			}					


			// Send Log message to let client know that we successfully (ie. RC = true) processed the incoming work request msg that kicked off this MDB
			try {
				long elapsedTime = System.currentTimeMillis() - startTime;
				TextMessage textMsg = jmsContext.createTextMessage();
				textMsg.setText("MDB successfully processed New Order work request messsage (elapsed time = " + elapsedTime + " ms.)");
				jmsContext.createProducer().setDeliveryMode(jakarta.jms.DeliveryMode.NON_PERSISTENT).setJMSCorrelationID(correlationUUID).setProperty("RC", true).send(newOrderLogTopic, textMsg);
				debugOut("< NewOrderMDBBean dumping second log message sent back to client: " + textMsg );


			} catch (Throwable t) {

				// We really don't expect any exceptions now just sending a simple test message back to the client.
				// However, since we did get an error, trying to send an error message probably does not make sense.
				// We will log what we can here and also rethrow the exception so that we skip the remainder of 
				// processing in onMessage().			
				System.out.println("NewOrderMDBBean caught unexpected exception while attempting to send message to logTopic: " + t);				
				throw t;                
			}


			// We are connected to the message engine and have processed the input message so
			// do the real work now.  Process the New Order and if we run into issues now, try sending
			// an error response to the client.  To make sure that we don't rollback the current
			// transaction all DB work will be done under a new transaction.
			try {

				long beforeDBTime = System.currentTimeMillis(); 
				JMS_MDB_debugOut("    >> JMSNewOrderMDB Execution Before DB activity, MDBptr = " + this + ", TimeInMilliSec = " + beforeDBTime );

				// Update the DB under a separate (ie. new) global transaction
				newOrderOutput = newOrderSessionBean.processNewOrder(newOrderInput);

				long afterDBTime = System.currentTimeMillis();
				long totalDBTime = afterDBTime - beforeDBTime;
				JMS_MDB_debugOut("    << JMSNewOrderMDB Execution After DB activity, MDBptr = " + this + ", TimeInMilliSec = " + afterDBTime + ", Total time in DB (msec) = " + totalDBTime );			

			} catch (Throwable t) {
				
				// Issues may occur while trying to update the database.  If so, send error response message to
				// client web app and log the details.  Rethrow the exception to skip the remainder of onMessage()
				// processing.  There is no use trying to send final results back to the client, but we can send
				// an error message instead.
				System.out.println("NewOrderMDBBean caught exception while attempting to update the database: " + t);

				// Use builder pattern to send Log message to let client know that we failed (ie. RC=false)
				TextMessage textMsg = jmsContext.createTextMessage();
				textMsg.setText("MDB failed with exception: " + t.getMessage());
				jmsContext.createProducer().setDeliveryMode(jakarta.jms.DeliveryMode.NON_PERSISTENT).setJMSCorrelationID(correlationUUID).setProperty("RC", false).send(newOrderLogTopic, textMsg);
				debugOut("< NewOrderMDBBean dumping failure message sent back to client: " + textMsg );	
				throw t;
			}


			// Send Log message to let client know that we successfully (ie. RC = true) updated the DB for this New Order. 
			try {
				long elapsedTime = System.currentTimeMillis() - startTime;
				TextMessage textMsg = jmsContext.createTextMessage();
				textMsg.setText("MDB sucessfully completed DB updates for New Order work request (elapsed time = " + elapsedTime + " ms.)");
				jmsContext.createProducer().setDeliveryMode(jakarta.jms.DeliveryMode.NON_PERSISTENT).setJMSCorrelationID(correlationUUID).setProperty("RC", true).send(newOrderLogTopic, textMsg);
				debugOut("< NewOrderMDBBean dumping third log message sent back to client: " + textMsg );

			} catch (Throwable t) {

				// We really don't expect any exceptions now just sending a simple test message back to the client.
				// However, since we did get an error, trying to send an error message probably does not make sense.
				// We will log what we can here and also rethrow the exception so that we skip the remainder of 
				// processing in onMessage().
				System.out.println("NewOrderMDBBean caught unexpected exception while attempting to send message to logTopic: " + t);				
				throw t;                          
			}


			// So far so good, now send the final results of the DB update back to web client
			try {

				ObjectMessage responseMsg = jmsContext.createObjectMessage(newOrderOutput);
				jmsContext.createProducer().setDeliveryMode(jakarta.jms.DeliveryMode.NON_PERSISTENT).setJMSCorrelationID(correlationUUID).setProperty("RC", true).send(replyQ, responseMsg);
				debugOut("< NewOrderMDBBean dumping final result message sent back to client: " + responseMsg );

			} catch (Throwable t) {
				// If we get an error sending the reply message, there is not much we can do.   Trying to
				// send a error response message is probably useless too since it would also fail.  Log
				// the information we have and give up.
				System.out.println("Exception trying to send final reply message in NewOrderMDBBean.onMessage(): " + t);
				throw t;
			}


		} catch (Throwable t) {

			// If we get here it is very likely that an exception occurred in the MDB.  If so, we should 
			// have already sent an error response message to the web client queue if possible.   Log the error
			// here, just in case, but eat the exception.  It is very important not to let an exception be
			// returned out of the onMessage() method because this will cause the EJB Container to not
			// complete the current transaction.  If that occurs, JMS will not send our reply message
			// since it is doing transactional sends (ie. send happens on transaction commit, but not
			// on rollback).

			System.out.println("Purposely eating exception in NewOrderMDBBean.onMessage(): " + t);
			t.printStackTrace();

		} finally {

			// Close down resources used in MDB.  This is done here so it will happen
			// regardless of whether an exception occurred or not.
			debugOut("< NewOrderMDBBean before jmsContext.close() >");
			if (jmsContext != null) {
			   jmsContext.close();
			}

			// Record that an MDB instance in no longer active (ie. in flight)
			long exitTime = System.currentTimeMillis();
			long totalTime = exitTime - startTime;
			JMS_MDB_debugOut("  << JMSNewOrderMDBInFlightCounter = " + NewOrderMDBInFlightCounter.decrementAndGet() + " MDBptr = " + this + " TimeInMilliSec = " + exitTime + ", Total time in MDB (msec) = " + totalTime);

			debugOut("< Exit NewOrderMDBBean.onMessage() for MDB: " + this + " >");
		}

	} //End onMessage


	/**
	 * ejbRemove
	 */
	public void ejbRemove() {
	}



	/**
	 * Method to process input data from the input message this MDB received
	 */
	private NewOrderInputInfo processReceivedMsg(Message inMessage) throws UserException {


		// The New Order Input that this method will return back to caller.
		NewOrderInputInfo noInput = null;

		try {
			debugOut("<< Enter NewOrderMDBBean.processReceivedMsg >>");

			if (inMessage instanceof ObjectMessage) {


				// Attempt to retrieve input data for New Order from incoming message
				// that was sent by NewOrderJMSClient
				try {

					debugOut("<< Dump of Msg received in NewOrder MDB: " + inMessage.toString());

					// This message should be of type ObjectMessage which contains a
					// serialized New Order imput object.
					noInput = (NewOrderInputInfo) ((ObjectMessage)inMessage).getObject();			

				} catch (JMSException j) {
					System.out.println("JMSException caught attempting to retrieve web client data from incoming message in NewOrderMDB.processReceivedMessage(msg): " + j);
					throw new UserException("JMSException caught attempting to retrieve web client data from incoming message in NewOrderMDB.processReceivedMessage(msg): " + j);
				}

			} else {

				System.out.println("Received message was not expected type 'ObjectMessage' in processReceivedMessage() method of NewOrderMDB");
				throw new UserException("Received message was not expected type 'ObjectMessage' in processReceivedMessage() method of NewOrderMDB");
			}

			// If we make it here we should have good input to return
			return (noInput);

		} finally {
			debugOut("<< Exit NewOrderMDBBean.processReceivedMsg >>");
		}
	}



	//
	// Method to initialize the output structure of NewOrderTransaction
	//

	NewOrderOutputInfo generateNewOrderOutput(byte db, int numberOfItems) {

		NewOrderOutputInfo output = new NewOrderOutputInfo();
		output.setOutWarehouseId((short) 0);
		output.setOutDistrictId((short) 0);
		output.setOutCustomerId((short) 0);
		output.setOutOrderId((short) 0);
		// double outOrderItemCount
		output.setOutCustomerLastName(" ");
		output.setOutCustomerCredit(" ");
		// double outCustomerDiscount
		// double outWarehouseTax
		// double outDistrictTax
		// output.setOutOrderEntryDate(java.util.Calendar.getInstance());
		// double outTotal
		output.setOutMsg(" ");

		debugOut("In generateNewOrderOutput, numberOfItems = " + numberOfItems);

		NewOrderSingleItemOutInfo item[] = new NewOrderSingleItemOutInfo[numberOfItems];

		for (int i = 0; i < numberOfItems; i++) {

			item[i] = new NewOrderSingleItemOutInfo();
			item[i].setOutItemId((short) 0);
			item[i].setOutItemSupplyWarehouseId((short) 0);
			item[i].setOutItemName(" ");
			// double outItemQuantity
			// double outStockQuantity
			// char outBrandGeneric
			// double outItemPrice
			// double outItemTotal
		}

		output.setOutItemList(item);

		return output;
	}


	public void debugOut(String message) {
		if (System.getProperty("DEBUG_OUT") != null)
			if (System.getProperty("DEBUG_OUT").equals("ON"))
				System.out.println(message);
	}

	private void JMS_MDB_debugOut(String message) {
		if (System.getProperty("JMS_MDB_DEBUG_OUT") != null)
			if (System.getProperty("JMS_MDB_DEBUG_OUT").equals("ON"))
				System.out.println(message);
	}



}
