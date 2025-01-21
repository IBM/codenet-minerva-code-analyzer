package priceChange.jms;

import jakarta.annotation.Resource;

import java.math.BigDecimal;
import java.math.RoundingMode;

import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.EJBException;
import jakarta.ejb.MessageDriven;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.jms.JMSException;
import jakarta.jms.MapMessage;
import jakarta.jms.MessageListener;
import jakarta.jms.Queue;
import jakarta.jms.QueueConnection;
import jakarta.jms.QueueConnectionFactory;
import jakarta.jms.QueueSender;
import jakarta.jms.QueueSession;
import jakarta.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

//Liberty does not have an I18N service, commenting out for now
//import com.ibm.websphere.i18n.context.Internationalization;
//import i18nPackage.I18nService;
//import java.util.Locale;
//import java.util.PropertyResourceBundle;
//import java.util.ResourceBundle;




import itemjpa.ItemJPA;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Bean implementation class for Enterprise Bean: PriceChangeMDB
 * public class PriceChangeMDBBean extends IRWWBase implements  jakarta.jms.MessageListener {
 */
@MessageDriven(activationConfig =
{
@ActivationConfigProperty(propertyName="destinationType",propertyValue="jakarta.jms.Queue"),
@ActivationConfigProperty(propertyName = "acknowledgeMode",propertyValue = "Auto-acknowledge"),
@ActivationConfigProperty(propertyName="destination",propertyValue="jms/PriceChangeRequestQ")
},name="PriceChangeMDBBean")
public class PriceChangeMDBBean implements MessageListener {
	/**
	 * 
	 */
	static AtomicInteger PriceChangeMDBInFlightCounter = new AtomicInteger(0);
	
	private jakarta.ejb.MessageDrivenContext messageDrivenCtx;

	private Queue replyQ = null;

	private QueueSender qSender = null;
	private QueueSession qSession = null;
	private QueueConnection qConnection = null;

	private Context env = null;
	private Context ctx = null;
	
	private int itemId = 0;
	private double Price = 0;
	private java.lang.String msgSize, largeMsgSize = null;
	
	// Check if the current message failed previously and is
	// now being retried by the messaging engine.  Normally
	// the JMSXDeliveryCount is 1.  Anything above that 
	// indicates a retry.
	private int msgDeliveryCount = 0;

	ItemJPA itemJPA = null;
	
	private java.lang.String messageId = null;
	
	@Resource(name="jms/PriceChangeQCF")
	private QueueConnectionFactory qConnectionFactory;
	
    //I18nService declarations
	//Liberty does not have an I18N service, commenting out for now.
	//protected I18nService i18n; 
	//protected Internationalization callerI18n;
	//protected Locale locale;
	
	@PersistenceContext(unitName="PriceChangeMDBLite")
	EntityManager em;
	
	/**
	 * getMessageDrivenContext
	 */
	public jakarta.ejb.MessageDrivenContext getMessageDrivenContext() {
		return messageDrivenCtx;
	}
	/**
	 * setMessageDrivenContext
	 */
	public void setMessageDrivenContext(jakarta.ejb.MessageDrivenContext msgCtx) {
		messageDrivenCtx = msgCtx;
	}
	/**
	 * ejbCreate
	 */
	public void ejbCreate() {
	 debugOut("PriceChangeMDBBean instance created in Server: " + this);
	}
	/**
	 * onMessage
	 */
	public void onMessage(jakarta.jms.Message msg) {
		
		long enterTime = 0;

		try{

			// Notes about exception handing strategy.  This MDB may run on a server that is different
			// from the server where the original web app request arrived.   Whenever possible, we try
			// to return error information to the client web app using a reply message.   However, that 
			// requires JMS resources like the queue connection factory and the reply queue to be working.
			// If enough stuff is not alive, all we will be able to do is log information about the failure
			// in the server's log.  Sending info back to the client web app will not be an option at times.
			// In these cases the client web app will get a timeout waiting on a reply message that never
			// arrives
					
			
            // First thing in onMessage, pull the msg ID from the message we received because we need to
			// use it as the correlation Id for any reply message we send back to the web client
			messageId = msg.getJMSMessageID();
			debugOut("< Entering PriceChangeMDBBean.onMessage(), for MDB: " + this + ", messageID to use for reply correlation ID is: " + messageId + " >");
						
			// Record that an MDB instance is active (ie. in flight)
			enterTime = System.currentTimeMillis();
			JMS_MDB_debugOut("  >> JMSPriceChangeMDBInFlightCounter = " + PriceChangeMDBInFlightCounter.incrementAndGet() + " MDBptr = " + this + " TimeInMilliSec = " + System.currentTimeMillis() );
			
			// Setup most JMS resources needed by the MDB. If we run into issues, all we can do is log
			// the error in the server.  We will not be able to attempt sending an error reply message
			// back to the web app client.
			try{

				setupJMS();

			} catch (EJBException e) {
				// Normal issues (expected errors) will be thrown out of setup() method as EJBExceptions
				// We won't be able to try to send an error reply message to the client web app so just log
				// the details.  Rethrow the exception to skip an subsequent processing in onMessage().
				System.out.println("EJBException in PriceChangeMDBBean.setup(): " + e);
				e.printStackTrace();
				throw e;

			} catch (Throwable t) {
				// All other exceptions are unexpected errors. Rethrow the exception to skip an subsequent
				// processing in onMessage().
				System.out.println("Unexpected exception in PriceChangeMDBBean.setup(): " + t);
				t.printStackTrace();
				throw t;
			}
			
			// Retrieve details about the incoming message and setup the JMS reply queue to send results
			// back to the client web app.  If we run into issues, all we can do is log the error
			// in the server.  We will not be able to attempt sending an error reply message back
			// to the web app client.
			try {

				debugOut("<< Before processReceivedMsg(msg)");
				processReceivedMsg(msg);
				
				//Put out extra debug line if message was redelivered by the messaging engine
				if (msgDeliveryCount > 1){
				   JMS_MDB_debugOut("  >> Working on Redelivered Msg! JMSXDeliveryCount = " + msgDeliveryCount + " MDBptr = " + this + " ItemId = " + itemId);
				}

			} catch (EJBException e) {
				
				// Normal issues (expected errors) will be thrown out of processReceivedMessage(msg) method as EJBExceptions
				// We won't be able to try to send an error reply message to the client web app so just log
				// the details.  Rethrow the exception to skip an subsequent processing in onMessage().
				System.out.println("EJBException in PriceChangeMDBBean.processReceivedMessage(msg): " + e);
				e.printStackTrace();
				throw e;

			} catch (Throwable t) {
				// All other exceptions are unexpected errors.  Rethrow the exception to skip an subsequent
				// processing in onMessage().
				System.out.println("Unexpected exception in PriceChangeMDBBean.processReceivedMessage(msg): " + t);
				t.printStackTrace();
				throw t;
			}			

			// Update the Database with the item's new price.  If we run into issues now, we can try sending
			// an error response to the web app client.  To make sure that we don't rollback the current
			// transaction.  The DB work will be done under a new transaction.
			try {
				BigDecimal itemPrice = (new BigDecimal(getPrice())).setScale(2, RoundingMode.HALF_UP);
			    
			    long beforeDBTime = System.currentTimeMillis(); 
				JMS_MDB_debugOut("    >> JMSPriceChangeMDB Execution Before DB activity, MDBptr = " + this + ", TimeInMilliSec = " + beforeDBTime );
				
				updateNewPriceforItemInDB(getItemId(), itemPrice);
				
				long afterDBTime = System.currentTimeMillis();
				long totalDBTime = afterDBTime - beforeDBTime;
				JMS_MDB_debugOut("    << JMSPriceChangeMDB Execution After DB activity, MDBptr = " + this + ", TimeInMilliSec = " + afterDBTime + ", Total time in DB (msec) = " + totalDBTime );
				
			} catch (Exception e) {
				// Issues may occur while trying to update the database.  If so, send error response message to
				// client web app and log the details.  Rethrow the exception to skip any subsequent processing
				// in onMessage().
				System.out.println("Exception updating the database in PriceChangeMDBBean.onMessage(): " + e);
				e.printStackTrace();
				sendErrorMsg(e.getMessage());
				throw e;

			} catch (Throwable t) {
				// All other exceptions are unexpected errors. If so, send error response message to the
				// client web app and log the details.  Rethrow the exception to skip an subsequent processing
				// in onMessage().				
				System.out.println("Unexpected exception updating the database in PriceChangeMDBBean.onMessage(): " + t);
				t.printStackTrace();
				sendErrorMsg(t.getMessage());
				throw t;
			}			

			// So far so good, now send results of the DB update back to web client
			try {
				sendResponseMsg();
			
			} catch (Throwable t) {
				// If we get an error sending the reply message, there is not much we can do.   Trying to
				// send a error response message is probably useless too since it would also fail.  Log
				// the information we have and give up.
				System.out.println("Exception trying to send reply message in PriceChangeMDBBean.mqPutMsg(): " + t);
				t.printStackTrace();
			}

		
		} catch (Throwable t) {

			// If we get here it is very likely that an exception occurred in the MDB.  If so, we should 
			// have already sent an error response message to the web client queue if possible.   Log the error
			// here, just in case, but eat the exception.  It is very important not to let an exception be
			// returned out of the onMessage() method because this will cause the EJB Container to not
			// complete the current transaction.  If that occurs, JMS will not send our reply message
			// since it is doing transactional based sends (ie. send happens on commit).

			System.out.println("Purposely eating exception in PriceChangeMDBBean.onMessage(): " + t);

		} finally {
			
			// Close down resources used in MDB.  This is done here so it will happen
			// regardless of whether an exception occurred or not.   Note that mqBreakDown is coded
			// so that it will not throw additional exceptions that we would need to handle here.
			debugOut("<< before closeJMS() >>");
			closeJMS();
			
			// Record that an MDB instance in no longer active (ie. in flight)
			long exitTime = System.currentTimeMillis();
			long totalTime = exitTime - enterTime;
			JMS_MDB_debugOut("  << JMSPriceChangeMDBInFlightCounter = " + PriceChangeMDBInFlightCounter.decrementAndGet() + " MDBptr = " + this + " TimeInMilliSec = " + exitTime + ", Total time in MDB (msec) = " + totalTime);
			
			debugOut("< Exiting PriceChangeMDBBean.onMessage() for MDB: " + this);
		}

	} //End onMessage
	
	/**
	 * ejbRemove
	 */
	public void ejbRemove() {
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (11/28/2001 10:07:24 AM)
	 */

	/**
	 * Insert the method's description here.
	 * Creation date: (9/17/2001 4:10:31 PM)
	 * @return java.lang.String
	 */
	public int getItemId() {
		return itemId;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (9/17/2001 4:10:45 PM)
	 * @return java.lang.String
	 */
	public double getPrice() {
		return Price;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (6/27/2001 2:30:57 PM)
	 */
	public void closeJMS() {

		try {
			if (qSender != null) {
				qSender.close();
			}
			if (qSession != null) {
				qSession.close();
			}
			if (qConnection != null) {
				qConnection.close();
			}
		} 
		
		// If any exceptions occur trying to close down resources just log them, but
		// eat the exception.  It is very important not to let an exception be returned
		// out of the onMessage() method because this will cause the EJB Container to not complete 
		// the current transaction.  If that occurs, JMS will not send our reply message since it
		// is doing transaction based sends (ie. send happens on commit).
		
		catch (Throwable t) {
			System.out.println("Purposely eating exception in PriceChangeMDB.closeJMS(): " + t);
			t.printStackTrace();
		}
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (6/27/2001 1:40:10 PM)
	 */
	private void processReceivedMsg(jakarta.jms.Message inMessage) throws EJBException {
		
			if (inMessage instanceof MapMessage) {

				// Log incoming message from web client, and try to retrieve reply queue info.
				try {
					debugOut("<< Dump of Msg received in PriceChange MDB: " + inMessage.toString());

					debugOut("<< Before (Queue)inMessage.getJMSReplyTo() >>");
					replyQ = (Queue)inMessage.getJMSReplyTo();

				} 	catch (JMSException j) {
					System.out.println("JMS exception retrieving reply queue info in PriceChangeMDB.processReceivedMessage(msg): " + j);
					throw new EJBException("JMS exception retrieving reply queue info in PriceChangeMDB.processReceivedMessage(msg): " + j);
				}

				// Try to create producer (ie. sender) for sending responses back to client web app on the
				// replyQ
				if (qSession != null) {

					try {

						debugOut("<< Before session.createSender(replyQ)");
						
						qSender = qSession.createSender(replyQ);

						if (qSender != null) {
							
							debugOut("<< qSender is: " + qSender.toString());
						
						} else {
							
							System.out.println("qSender is null in processReceivedMessage() method of PriceChangeMDB");
							throw new EJBException("qSender is null in processReceivedMessage() method of PriceChangeMDB)");
						}
						
					} catch (JMSException j) {
						System.out.println("JMS exception creating sender in PriceChangeMDB.processReceivedMessage(msg): " + j);
						throw new EJBException("JMS exception creating sender in PriceChangeMDB.processReceivedMessage(msg): " + j);
					}

				} else {
					System.out.println("JMS session is null in processReceivedMessage() method of PriceChangeMDB");
					throw new EJBException("JMS session is null in processReceivedMessage() method of PriceChangeMDB");
				}

				// Attempt to retrieve input data about item, new price, etc. from the incoming message
				// sent by web app client
				try {
					MapMessage inMsg = (MapMessage) inMessage;
					this.setItemId((Integer.valueOf(inMsg.getString("itemId"))).intValue());
					this.setPrice(Double.valueOf(inMsg.getString("price")).doubleValue());
					this.setMsgSize(inMsg.getString("messageSize"));
					this.setLargeMsgSize(inMsg.getString("largeMessageSize"));
					msgDeliveryCount = inMsg.getIntProperty("JMSXDeliveryCount");
					debugOut("<< itemId: " + getItemId());
					debugOut("<< price: " + getPrice());
					debugOut("<< largeMsgSize: " + getLargeMsgSize());
					debugOut("<< JMSXDeliveryCount: " + msgDeliveryCount);

				} catch (JMSException j) {
					System.out.println("JMS exception retrieving web client data from incoming message in PriceChangeMDB.processReceivedMessage(msg): " + j);
					throw new EJBException("JMS exception retrieving web client data from incoming message in PriceChangeMDB.processReceivedMessage(msg): " + j);
				}
				
			} else {
				
				System.out.println("Received message was not expected type 'mapMessage' in processReceivedMessage() method of PriceChangeMDB");
				throw new EJBException("Received message was not expected type 'mapMessage' in processReceivedMessage() method of PriceChangeMDB");
			}			

	}
	
	public void jmsSend(String msgText, boolean rc) throws EJBException {

		try {
			debugOut("<<< Enter jmsSend() method of PriceChangeMDBBean >>");
			TextMessage textMsg = qSession.createTextMessage(msgText);
			
			textMsg.setJMSDeliveryMode(jakarta.jms.DeliveryMode.NON_PERSISTENT);
			textMsg.setJMSCorrelationID(messageId);
			textMsg.setBooleanProperty("RC", rc);
			
			debugOut("<<<< Before sender.send(textMsg) in jmsSend() method of PriceChangeMDBBean");
			qSender.send(textMsg);
			
			debugOut("<<<< Dump of response msg sent by PriceChange MDB: " + textMsg.toString());
		
		// If we catch any errors trying to send the reply message to the client,
		// we are pretty much out of luck.  Log what we can and throw back an
	    // exception to onMessage().
			
		} catch (JMSException j) {
			System.out.println("JMS exception attempting to send reply message back to client in PriceChangeMDBBean.jmsSend(): " + j);
			throw new EJBException ("JMS exception attempting to send reply message back to client in PriceChangeMDBBean.jmsSend(): " + j);
			
		} finally {
			debugOut("<<< Exit jmsSend() method of PriceChangeMDBBean >>");
		}

	}


	public void sendErrorMsg(String msgText) throws EJBException {
		
		// Log the error and send it out with RC = false to tell client
		// that we were not successful in the MDB
        debugOut("<< sendErrorMsg invoked in PriceChangeMDBBean, error message to send web client is: " + msgText);
		jmsSend(msgText, false);
	}

	public void sendResponseMsg() throws EJBException {

		debugOut("<< Enter sendResponseMsg()- PriceChange MDB sending response msg to client");

		try {
			//Liberty does not have an I18N service, commenting out for now
			//I18nService i18n = new I18nService();
			//i18n.setSessionContext();
			//i18n.setResourceBundle();
			//locale=i18n.getLocale();
			//java.text.DateFormat df = java.text.DateFormat.getDateInstance (java.text.DateFormat.DEFAULT, locale);
			//java.text.DateFormat tf = java.text.DateFormat.getTimeInstance (java.text.DateFormat.DEFAULT, locale);
			//java.text.NumberFormat nf = java.text.NumberFormat.getCurrencyInstance(locale);
			String msgText = null;

			if (this.getMsgSize().equals("L")) {
	
				int lmSize = (Double.valueOf(getLargeMsgSize())).intValue();
				int len = 1000000*lmSize;  // Len is the final length
				StringBuffer buf = new StringBuffer(len);
				String ab = "ABCDEFGHIJKLMNOPQRSTUVWZY";
				int i = 0;
				for (; i < len;) {
					buf.insert(i, ab);  // don't use append, just insert into predefined StringBuffer
					i = i + 25;
				}
				//Liberty does not have 18N service yet, so hardcoding message to english for now
				msgText =
						//i18n.getMsg("PriceChangeJMS.ItemID")
						"The itemID is: "
						+ this.getItemId()
						+ " "
						//+ i18n.getMsg("PriceChangeJMS.PriceID")
						+ "The price is: "
						+ itemJPA.getItemPrice().setScale(2,RoundingMode.HALF_UP)
						+ " "
						//+ i18n.getMsg("PriceChangeJMS.MessageSizeLarge")
						+ "The message size is large, sampling: "
						+ buf.toString();

			} else {
				msgText =
						//i18n.getMsg("PriceChangeJMS.ItemID")
						"The itemID is: "
						+ this.getItemId()
						+ " "
						//+ i18n.getMsg("PriceChangeJMS.PriceID")
						+ "The price is: "
						+ itemJPA.getItemPrice().setScale(2,RoundingMode.DOWN)
						+ " "
						//+ i18n.getMsg("PriceChangeJMS.MessageSizeSmall");
						+ "The message size is small";
			}

			debugOut("<<< Response msg text is: " + msgText);

			// send the "success" response message to the client (ie. rc = true)
			jmsSend(msgText, true);

		} catch (Exception e) {
			System.out.println("Exception caught in sendResponseMsg() method of PriceChangeMDBBean: " + e);
			throw new EJBException("Exception caught in sendResponseMsg() method of PriceChangeMDBBean: " + e);
			
		} finally {
			debugOut("<< Exit sendResponseMsg()");
		}

	} // End sendResponseMsg

	

	public void setupJMS() throws EJBException {


		debugOut("<< Enter setupJMS()");

		// outer try block for unexpected exceptions
		try {

			// Lookups for JMS resources 
			try {
				
				ctx = new InitialContext();
				env = (Context) ctx.lookup("java:comp/env");

			} catch (NamingException n) {
				System.out.println("Naming exception looking up initial context in priceChangeMDB.setupJMS(): " + n);
				throw new EJBException("Naming exception looking up initial context in priceChangeMDB.setupJMS(): " + n);
			}

			try {
				debugOut("<<< before lookup of queue connection factory");
				qConnectionFactory = (QueueConnectionFactory) env.lookup("jms/PriceChangeQCF");

			} catch (NamingException n) {
				System.out.println("Naming exception looking up PriceChangeQCF in priceChangeMDB.setupJMS(): " + n);				
				throw new EJBException("Naming exception looking up PriceChangeQCF in priceChangeMDB.setupJMS(): " + n);
			}

			if (qConnectionFactory == null) {
				System.out.println("Queue connection factory is null in priceChangeMDB.setupJMS()");
				throw new EJBException("Queue connection factory is null in priceChangeMDB.setupJMS()");
			}	

			try {
				debugOut("<<< before create of queue connection");
				qConnection = qConnectionFactory.createQueueConnection();
			} catch (JMSException j) {
				System.out.println("JMS exception creating connection in priceChangeMDB.setupJMS(): " + j);
				throw new EJBException("JMS exception creating connection in priceChangeMDB.setupJMS(): " + j);
			}

			if (qConnection == null) {
				System.out.println("JMS connection is null in priceChangeMDB.setupJMS()");	
				throw new EJBException("JMS connection is null in priceChangeMDB.setupJMS()");
			}	

			try {
				debugOut("<<< before qConnection.start()");
				qConnection.start();

			} catch (JMSException j) {
				System.out.println("JMS Exception starting connection in PriceChangeMDB.setupJMS(): " + j);
				throw new EJBException("JMS Exception starting connection in PriceChangeMDB.setupJMS(): " + j);
			}

			try {

				debugOut("<<< before create of queue session");
				
				// the queue session will be transacted
				qSession = qConnection.createQueueSession(true, QueueSession.SESSION_TRANSACTED);

			} catch (JMSException j) {
				System.out.println("JMS Exception creating session in PriceChangeMDB.setupJMS(): " + j);
				throw new EJBException("JMS Exception creating session in PriceChangeMDB.setupJMS(): " + j);
			}

		} catch (Throwable t) {

			System.out.println("Unexpected exception in PriceChangeMDB.setupJMS(): " + t);
			throw new EJBException("Unexpected exception in PriceChangeMDB.setupJMS(): " + t);

		} finally {

			debugOut("<< Exit setupJMS()");

		}   

	} // End setupJMS()

	public void setItemId(int newItemId) {
		itemId = newItemId;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (9/17/2001 4:10:45 PM)
	 * @param newPrice java.lang.String
	 */
	public void setPrice(double newPrice) {
		Price = newPrice;
	}

	/**
	 * Returns the msgSize.
	 * @return java.lang.String
	 */
	public java.lang.String getMsgSize() {
		return msgSize;
	}

	/**
	 * Sets the msgSize.
	 * @param msgSize The msgSize to set
	 */
	public void setMsgSize(java.lang.String msgSize) {
		this.msgSize = msgSize;
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
	
	public java.lang.String getLargeMsgSize() {
		return largeMsgSize;
	}
	public void setLargeMsgSize(java.lang.String largeMsgSize) {
		this.largeMsgSize = largeMsgSize;
	}
	
	// Updating the DB will be done under a new global transaction.  This
	// is done in case any DB related errors occur, causing the new 
	// transaction to be marked for rollback by JPA.  We do not want the 
	// original onMessage transaction to be affected because we may use
	// that transaction to send an error message back to the client.
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void updateNewPriceforItemInDB(int itemId, BigDecimal newPrice) 
			throws Exception {

		try {
			debugOut("<< Enter updateNewPriceForItemInDB()");

			debugOut("<<< Before finding ItemJPA");
			itemJPA = em.find(ItemJPA.class, itemId);

			if (itemJPA != null) {

				debugOut("<<< Before updating Item's price in DB");
				itemJPA.setItemPrice(newPrice);

			} else {

				System.out.println("itemJPA retrieved from DB for itemId = " + itemId + " was null");
				throw new EJBException("itemJPA retrieved from DB for itemId = " + itemId + " was null");			
			}

		} finally {

			debugOut("<< Exit updateNewPriceForItemInDB()");
		}
	}

}
