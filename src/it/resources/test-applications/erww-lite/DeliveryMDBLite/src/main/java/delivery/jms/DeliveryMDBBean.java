package delivery.jms;

import java.sql.Timestamp;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.annotation.Resource;
import jakarta.annotation.security.DeclareRoles;
import jakarta.annotation.security.RunAs;
import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.EJBException;
import jakarta.ejb.MessageDriven;
import jakarta.jms.JMSException;
import jakarta.jms.MapMessage;
import jakarta.jms.ObjectMessage;
import jakarta.jms.Queue;
import jakarta.jms.QueueConnection;
import jakarta.jms.QueueConnectionFactory;
import jakarta.jms.QueueSender;
import jakarta.jms.QueueSession;
import jakarta.jms.TextMessage;
import jakarta.jms.Topic;
import jakarta.jms.TopicConnection;
import jakarta.jms.TopicConnectionFactory;
import jakarta.jms.TopicPublisher;
import jakarta.jms.TopicSession;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import deliverysession.DeliveryInput;
import deliverysession.DeliveryOutput;
import deliverysession.DeliverySessionFacade;
import deliverysession.SingleDelivery;

/**
 * Bean implementation class for Enterprise Bean: DeliveryMDB
 */

@MessageDriven(activationConfig =
{
@ActivationConfigProperty(propertyName="destinationType",propertyValue="jakarta.jms.Queue"),
@ActivationConfigProperty(propertyName = "acknowledgeMode",propertyValue = "Auto-acknowledge"),
@ActivationConfigProperty(propertyName="destination",propertyValue="jms/DeliveryRequestQ")
},name="DeliveryMDBBean")

@DeclareRoles("RunAsCompanyRole")
@RunAs("RunAsCompanyRole")
public class DeliveryMDBBean  implements jakarta.jms.MessageListener {

	private jakarta.ejb.MessageDrivenContext fMessageDrivenCtx = null;

	private Queue replyQ = null;
	private QueueSender queueSender = null;	
	private QueueSession queueSession = null;
	private QueueConnection queueConnection = null;
	private TopicPublisher topicPublisher = null;
	private TopicSession topicSession = null;	
	private TopicConnection topicConnection = null;	

	short warehouseId = 0;
	short carrierId = 0; 
	int maxdistrictId = 0; 
	short districtId = 0;
	int msgDeliveryCount = 0;

	String selectorUUID = null;

	DeliveryInput input = null;

	private boolean jsFlow = false;
	
	private QueueSender paySender = null;

	private String deliveryTs = null;
	
	// For JMS MDB debug
	static AtomicInteger DeliveryMDBInFlightCounter = new AtomicInteger(0);
	
	
	@Resource(name="jms/DeliveryQueueCF")
	private jakarta.jms.QueueConnectionFactory queueCF=null;
	@Resource(name="jms/DeliveryTopicCF")
	private jakarta.jms.TopicConnectionFactory topicCF=null;

	@Resource(name="jms/DeliveryLogTopic")
	private Topic deliveryLogTopic = null;
	
	// MLS - take out injection since payment flow is not used right now, but
	//       leave paymentQueue variable in since unused code needs it to compile.
	//@Resource(name="jms/JSFlowQueue")
	private Queue paymentQueue = null;
	
	/**
	 * getMessageDrivenContext
	 */
	public jakarta.ejb.MessageDrivenContext getMessageDrivenContext() {
		return fMessageDrivenCtx;
	}

	
	/**
	 * setMessageDrivenContext
	 */
	public void setMessageDrivenContext(jakarta.ejb.MessageDrivenContext ctx) {
		fMessageDrivenCtx = ctx;
	}

	private void jmsReceive(jakarta.jms.Message inMessage) throws EJBException {

		try {
			debugOut("<< Entering jmsReceive() in Delivery MDB >>");
			if (inMessage instanceof MapMessage) {
				replyQ = (Queue) inMessage.getJMSReplyTo();
				debugOut("<<< Dump of received Msg in Delivery MDB: " + inMessage.toString());				
				MapMessage inMsg = (MapMessage) inMessage;
				this.setWarehouseId(inMsg.getShort("warehouseId"));
				this.setCarrierId(inMsg.getShort("carrierId"));
				this.setMaxdistrictId(inMsg.getInt("maxDistrictId"));
				msgDeliveryCount = inMsg.getIntProperty("JMSXDeliveryCount");

				try {
					this.setJsFlow(inMsg.getBoolean("jsFlow"));
				} catch (NullPointerException npe) {
					this.setJsFlow(false);
				}

				try {
					deliveryTs = inMsg.getString("deliveryTs");
				} catch (NullPointerException npe) {
					deliveryTs = String.valueOf(System.currentTimeMillis());
				}
				

			} else {
				System.out.println("<<< Message was not a MapMessage in jmsReceive()!");
				throw new EJBException("Message was not a MapMessage in jmsReceive()!");
			}
		} catch (JMSException j) {
			System.out.println("<<< Caught JMSException in jmsReceive(): " + j.getMessage());
			j.printStackTrace();
			jmsSendError(j.getMessage());
			Exception e = j.getLinkedException();
			if (e != null) {
				throw new EJBException("Caught JMSException in jmsReceive()", e);
			} else {
			    throw new EJBException("Caught JMSException in jmsReceive(msg)", j);
			}
			
		} catch (Exception e) {
			System.out.println("<<< Caught Exception in jmsReceive(): " + e.getMessage());
			e.printStackTrace();
			jmsSendError(e.getMessage());
			throw new EJBException("Caught Exception in jmsReceive()", e);
			
		} finally {
			debugOut("<< Exiting jmsReceive() in Delivery MDB >>");
		}
	}


	public void jmsSetup() throws NamingException, JMSException, EJBException {
		try {
			debugOut("<< Entering jmsSetup() in Delivery MDB >>");
			
			InitialContext ic = new InitialContext();
	
			queueCF = (QueueConnectionFactory) ic.lookup("java:comp/env/jms/DeliveryQueueCF");
			topicCF = (TopicConnectionFactory) ic.lookup("java:comp/env/jms/DeliveryTopicCF");
			
			if (deliveryLogTopic == null) {
				deliveryLogTopic = (Topic) ic.lookup("java:comp/env/jms/DeliveryLogTopic");
				
				if (deliveryLogTopic == null) {
					debugOut("<<< deliveryLogTopic is null in jmsSetup()!");
					throw new EJBException("deliveryLogTopic is null in jmsSetup()!");
				}		
			}	
			
			if (jsFlow) {
				paymentQueue = (Queue) ic.lookup("java:comp/env/jms/JSFlowQueue");

				if (paymentQueue == null) {
					debugOut("<<< paymentQueue is null in jmsSetup()!");
					throw new EJBException("paymentQueue is null in jmsSetup()!");
				}	

				debugOut("<<< paymentQueue: " + paymentQueue.toString());
			}

			queueConnection = queueCF.createQueueConnection();
			if (queueConnection != null) {
				debugOut("<<< queueConnection: " + queueConnection.toString());
			} else {
				System.out.println("<<< queueConnection is null in jmsSetup()!");
				throw new EJBException("queueConnection is null in jmsSetup()!");
			}
			
			topicConnection = topicCF.createTopicConnection();
			if (topicConnection != null) {
				debugOut("<<< topicConnection: " + topicConnection.toString());
			} else {
				System.out.println("<<< topicConnection is null in jmsSetup()!");
				throw new EJBException("topicConnection is null in jmsSetup()!");
			}
			
			queueConnection.start();
			queueSession = queueConnection.createQueueSession(true, TopicSession.AUTO_ACKNOWLEDGE);
			if (queueSession != null) {
				debugOut("<<< queueSession: " + queueSession.toString());
			} else {
				System.out.println("<<< queueSession is null in jmsSetup()!");
				throw new EJBException("queueSession is null in jmsSetup()!");
			}
			
			topicConnection.start();				
			topicSession = topicConnection.createTopicSession(true, TopicSession.AUTO_ACKNOWLEDGE);
			if (topicSession != null) {
				debugOut("<<< topicSession: " + topicSession.toString());
			} else {
				System.out.println("<<< topicSession is null in jmsSetup()!");
				throw new EJBException("topicSession is null in jmsSetup()!");
			}
			
			topicPublisher = topicSession.createPublisher(deliveryLogTopic);
			if (topicPublisher != null) {
				debugOut("<<< topicPublisher: " + topicPublisher.toString());
			} else {
				System.out.println("<<< topicPublisher is null in jmsSetup()!");
				throw new EJBException("topicPublisher is null in jmsSetup()!");
			}
			
			
		} catch (JMSException j) {
			System.out.println("<<< Caught JMSException in jmsSetup(): " + j.getMessage());
			j.printStackTrace();
			Exception e = j.getLinkedException();
			if (e != null) {
				throw new EJBException("Caught JMSException in jmsSetup()", e);
			} else {
			    throw new EJBException("Caught JMSException in jmsSetup()", j);
			}
			
		} catch (Exception e) {
			System.out.println("<<< Caught Exception in jmsSetup(): " + e.getMessage());
			e.printStackTrace();
			throw new EJBException("Caught Exception in jmsSetup()", e);

		} finally {
			debugOut("<< Exiting jmsSetup() in Delivery MDB >>");
		}
		
	}

	public void jmsSendToQueue(String msgText, boolean rc) throws EJBException {

		try {
			debugOut("<< Entering jmsSendToQueue() in Delivery MDB >>");
			
			debugOut("<<< Before createTextMessage() in jmsSendtoQueue()");
			TextMessage tm = queueSession.createTextMessage(getTimeStamp() + msgText + " ");
			
			debugOut("<<< Before setJMSDeliveryMode() in jmsSendtoQueue()");
			tm.setJMSDeliveryMode(jakarta.jms.DeliveryMode.NON_PERSISTENT);
			
			tm.setBooleanProperty("RC", rc);
			
			debugOut("<<< Using sender: " + queueSender.toString());
			debugOut("<<< DeliveryMDB is sending message now...");
			
			queueSender.send(tm);
			debugOut("<<< Dump of Msg sent by Delivery MDB: " + tm.toString());
			
		} catch (JMSException j) {
			System.out.println("<<< Caught JMSException in jmsSendToQueue(): " + j.getMessage());
			j.printStackTrace();
			Exception e = j.getLinkedException();
			if (e != null) {
				throw new EJBException("Caught JMSException in jmsSendToQueue()", e);
			} else {
			    throw new EJBException("Caught JMSException in jmsSendToQueue()", j);
			}
			
		} catch (Exception e) {
			System.out.println("Caught Exception in jmsSendToQueue(): " + e.getMessage());
			e.printStackTrace();		
			throw new EJBException("Caught Exception in jmsSendToQueue()", e);
			
		} finally {
			debugOut("<< Exiting jmsSendtoQueue() in Delivery MDB >>");
		}
	}

	public void jmsSendError(String msgText) {

		try {
			debugOut("<< Entering jmsSendError() in Delivery MDB >>");

			if (msgText != null) {
				System.out.println("<<< Delivery MDB detected an error, jmsSendError() is attempting to send an error msg and bad return code back to the Client: " + msgText);

				try {

					jmsSendToQueue(msgText, false);

				} catch (Throwable t) {

					// If the error msg send fails, there is not much we can do.  The original error should already be logged.
					System.out.println("<<< jmsSendError() was unable to send error message back to Client");    
				}

			} else {
				// This really should not happen unless we have an internal bug.
				System.out.println("<<< jmsSendError() was given a null msg text string!");	
			}

		} finally {
			debugOut("<< Exiting jmsSendError() in Delivery MDB >>");
		}		

	}

	public void releaseResources() throws EJBException {

		try {
			debugOut("<< Entering releaseResources() in Delivery MDB >>");
			
			// Don't assume we got far enough to create all the resources
			// some of them may be null if we had an error early
			if (queueSender != null) {
			   queueSender.close();
			}
			if (queueSession != null) {
				   queueSession.close();
			}				
			if (queueConnection != null) {
				   queueConnection.close();
			}			
			
			if (topicPublisher != null) {
			   topicPublisher.close();
			}			
			if (topicSession != null) {
			   topicSession.close();
			}			
			if (topicConnection != null) {
			   topicConnection.close();
			}
			
		} catch (Exception e) {
			System.out.println("<<< Caught Exception in releaseResources(): " + e.getMessage());
			e.printStackTrace();
			throw new EJBException(" Caught Exception in releaseResources()", e);
			
		} finally {
			
			debugOut("<< Exiting releaseResources() in Delivery MDB >>");
		}
	}

	/**
	 * onMessage
	 */
	public void onMessage(jakarta.jms.Message msg) {
		
		// Notes about exception handing strategy.  This MDB may run on a server that is different
		// from the server where the original web app request arrived.   Whenever possible, we try
		// to return error information to the client web app using a reply message.   However, that 
		// requires JMS resources like the queue connection factory and the reply queue to be working.
		// If enough stuff is not alive, all we will be able to do is log information about the failure
		// in the server's log.  Sending info back to the client web app will not be an option at times.
		// In these cases the client web app will get a timeout waiting on a reply message that never
		// arrives
		
		long enterTime = 0;  

		try {
			enterTime = System.currentTimeMillis();
			JMS_MDB_debugOut("  >> JMSDeliveryMDBInFlightCounter = " + DeliveryMDBInFlightCounter.incrementAndGet() + ", MDBptr = " + this + ", TimeInMilliSec = " + enterTime );
			
			debugOut("< Entering onMessage() in Delivery MDB >");
			
			jmsReceive(msg);

			try {
				if (msg == null) {
					System.out.println("<< The received message is null >>");
					throw new EJBException("Received msg is null in DeliveryMDBBean.onMessage()");
				} else {
					if (msg instanceof MapMessage) {
						replyQ = (Queue) msg.getJMSReplyTo();
						debugOut("<< replyQname: " + replyQ.getQueueName());
						
						// Pull the Correlation ID from the message we received because we need to
						// use it as the correlation Id for the topic message we publish back
						// to the web client later
						selectorUUID = msg.getStringProperty("ERWWCorrelationID");
						
						debugOut("<< selectorUUID of received msg: " + selectorUUID);
						
					} else {
						System.out.println("<< Received message is not a map message!");
						throw new EJBException("Received message is not a map message!");
					}
				}
			} catch (JMSException j) {
				System.out.println("<< Caught JMSException in onMessage(): " + j.getMessage());
				j.printStackTrace();
				Exception e = j.getLinkedException();
				if (e != null) {
					throw new EJBException("Caught JMSException in onMessage()", e);
				}
				throw new EJBException("Caught JMSException in onMessage()", j);
				
			} catch (Exception e) {
				System.out.println("<< Caught Exception in onMessage(): " + e.getMessage());
				e.printStackTrace();
				throw new EJBException("Caught Exception in onMessage()", e);
			}

			try {
				debugOut("<< Before jmsSetup() in onMessage() in Delivery MDB >>");

				jmsSetup();
				
				queueSender = queueSession.createSender(replyQ);

				if (queueSender == null) {
					System.out.println("<< queueSender is null in onMessage()!");
					throw new EJBException("queueSender is null in onMessage()!");
				}
				if (jsFlow) {
					debugOut("<< before paySender=sess.createSender(), paymentQueue.toString(): " + paymentQueue.toString());
					paySender = queueSession.createSender(paymentQueue);
					if (paySender == null) {
						debugOut("<< paySender is null in onMessage()!");
						throw new EJBException("paySender is null in onMessage()!");
					}
				}
				
				queueSender.setDeliveryMode(jakarta.jms.DeliveryMode.PERSISTENT);
				
			} catch (NamingException ne) {
				System.out.println("<< Caught NamingException in onMessage(): " + ne.getMessage());
				ne.printStackTrace();
				throw new EJBException("Caught NamingException in onMessage()", ne);

			} catch (JMSException j) {
				System.out.println("<< Caught JMSException in onMessage(): " + j.getMessage());
				j.printStackTrace();	
				Exception e = j.getLinkedException();
				if (e != null) {
					throw new EJBException("Caught JMSException in onMessage()", e);
				} else {
					throw new EJBException("Caught JMSException in onMessage()", j);
				}

			} catch (Exception e) {
				System.out.println("<< Caught Exception in onMessage(): " + e.getMessage());
				e.printStackTrace();
				throw new EJBException("Caught Exception in onMessage()", e);
			}

			String outMessage = "*** JMS Delivery (MDB): Execution Status: Delivery has been queued! *** Servant: "
					+ irwwbase.Stcnum.getInstance().getStcnum();
			
			debugOut("<< Delivery has been queued message to be sent: " + outMessage);
			jmsSendToQueue(outMessage, true);

			DeliveryOutput output = null;		
			DeliveryInput input = new DeliveryInput();
			input.setWarehouseId(getWarehouseId());
			input.setCarrierId(getCarrierId());
			input.setMaxdistrictId(this.maxdistrictId);
			
			// Put out extra debug line if this message was redelivered by the messaging engine
			if (msgDeliveryCount > 1){
			   JMS_MDB_debugOut("  >> Working on Redelivered Msg! JMSXDeliveryCount = " + msgDeliveryCount + " MDBptr = " + this + " WarehouseId = " + input.getWarehouseId() + 
					   " CarrierId = " + input.getCarrierId() + " MaxDistrictId = " + input.getMaxdistrictId());
			}

			try {			
				InitialContext ic = new InitialContext();
				DeliverySessionFacade desession = (DeliverySessionFacade) ic.lookup("java:app/DeliverySessionEJBLite/DeliverySessionFacadeBean!deliverysession.DeliverySessionFacade");
				
				long beforeDBTime = System.currentTimeMillis(); 
				JMS_MDB_debugOut("    >> JMSDeliveryMDB Execution Before DB activity, MDBptr = " + this + ", TimeInMilliSec = " + beforeDBTime );
				
				// Execute DB work for the Delivery transaction
				debugOut("<< Calling deliverSession EJB to do all the DB work now...");
				output = desession.deliverySession(input);
				debugOut("<< Back from deliverySession EJB call, continuing in normal path...");
				
				long afterDBTime = System.currentTimeMillis();
				long totalDBTime = afterDBTime - beforeDBTime;
				JMS_MDB_debugOut("    << JMSDeliveryMDB Execution After DB activity, MDBptr = " + this + ", TimeInMilliSec = " + afterDBTime + ", Total time in DB (msec) = " + totalDBTime );
				
			} catch (Exception e) {
				System.out.println("<< Caught Exception from call to deliverySession EJB: " + e.getMessage());
				e.printStackTrace();
				jmsSendError("Caught Exception on call to deliverySession EJB: " + e.getMessage());
				throw new EJBException("Caught Exception on call to deliverySession EJB", e);
			}	

			// normal (good) path continues
			
			try {

				// Send Delivery response back to client, including deliveryOutput object
				// Delivery output is serialized into JMS message here and deserialized
				// in the client so output can be displayed.
				ObjectMessage message = queueSession.createObjectMessage(output);
				message.setBooleanProperty("RC", true);
				debugOut("<< Sending delivery output message in Delivery MDB...");
				queueSender.send(message);
				debugOut("<< Dump of Delivery output Msg sent by Delivery MDB: " + message.toString());

			} catch (Exception e) {
				
				System.out.println("<< Caught Exception sending Delivery output message to client in Delivery MDB: " + e.getMessage());
				e.printStackTrace();
				jmsSendError("Caught Exception sending Delivery output message to client in Delivery MDB: " + e.getMessage());
				throw new EJBException("Caught Exception sending Delivery output message to client in Delivery MDB", e);
			}
			if (isJsFlow()) {
				debugOut("<< DeliveryMDB, in loop for making payments");
				SingleDelivery[] singleDeliveries = null;
				singleDeliveries = output.getSingleDeliveries();
				try {
					for (int i = 0; i < singleDeliveries.length; i++) {

						if (singleDeliveries[i].getAmount() != null) {
							MapMessage mm = queueSession.createMapMessage();

							mm.setShort("customerId", singleDeliveries[i]
									.getCustomerId());
							mm.setDouble("amount", singleDeliveries[i].getAmount()
									.doubleValue());
							mm.setShort("districtId", singleDeliveries[i]
									.getDistrictId());
							mm.setInt("orderId", singleDeliveries[i].getOrderId());
							mm.setShort("warehouseId", singleDeliveries[i]
									.getWarehouseId());
							mm.setStringProperty("JSFlowMsgType", "Payment");
							mm.setString("callPayMDBfromDEMDB", String
									.valueOf(System.currentTimeMillis()));
							mm.setString("deliveryTs", deliveryTs);

							mm.setJMSReplyTo(replyQ);

							debugOut("<< paySender.toString(): "
									+ paySender.toString());
							debugOut("<<toString of sent message before send:"
									+ mm.toString());
							debugOut("<<< before paySender.send(paymentQueue,mm), paymentQueue.toString()="
									+ paymentQueue.toString());
							paySender.send(mm);
						}
						int j = i + 1;
						debugOut("<<< DeliveryMDB kicked off " + j + " Payments");
					}
				} catch (JMSException j) {
					System.out.println("JMS Exception in DeliveryMDBBean.onMessage()");
					Exception e = j.getLinkedException();
					if (e != null) {
						throw new EJBException("JMS Exception in DeliveryMDBBean.onMessage()", e);
					}
					throw new EJBException("JMS Exception in DeliveryMDBBean.onMessage()", j);
				} catch (Exception e) {
					System.out.println("Exception in DeliveryMDBBean.onMessage()"
							+ e.getMessage());
					e.printStackTrace();
					throw new EJBException("JMS Exception in DeliveryMDBBean.onMessage()", e);
				}
			} // end if JSFlow
			
			// Continue normal (good) path, send success message to Delivery log topic
			
			debugOut("<< publishing Msg to deliveryLogTopic...");
	
			String logMessage = "*** JMS Delivery (MDB): Execution Status: Delivery has returned from processing! *** Servant: "
					+ irwwbase.Stcnum.getInstance().getStcnum();
			publishMsgToTopic(logMessage);	
					
		} catch (Throwable t) {

			// If we get here it is very likely that an exception occurred in the MDB.  If so, we should 
			// have already sent an error response message to the web client queue if possible.   Log the error
			// here, just in case, but eat the exception.  It is very important not to let an exception be
			// returned out of the onMessage() method because this will cause the EJB Container to not
			// complete the current transaction.  If that occurs, JMS will not send our reply message
			// since it is doing transactional based sends (ie. send happens on commit).

			System.out.println("<< Purposely eating exception in DeliveryMDBBean.onMessage(): " + t);

		} finally {

			releaseResources();
			long exitTime = System.currentTimeMillis();
			long totalTime = exitTime - enterTime;
			JMS_MDB_debugOut("  << JMSDeliveryMDBInFlightCounter = " + DeliveryMDBInFlightCounter.decrementAndGet() + ", MDBptr = " + this + ", TimeInMilliSec = " + exitTime + ", Total time in MDB (msec) = " + totalTime);
			debugOut("< Exiting onMessage(msg) in Delivery MDB >");
			
		}
	}

	
	/**
	 * Insert the method's description here. Creation date: (9/20/00 4:23:25 PM)
	 * 
	 * @return java.lang.String
	 * @exception java.lang.Exception
	 *                The exception description.
	 */
	public String getTimeStamp() {

		//Get Time Stamp in the form of yyyy-mm-dd-hh.mm.ss.nnnnnn
		//The toString methods returns the following format: dow mon dd
		// hh:mm:ss zzz yyyy

		Timestamp timestamp = new Timestamp(java.lang.System
				.currentTimeMillis());
		String timeStamp = timestamp.toString();

		return timeStamp; // method getTimeStamp

	}

	
	/**
	 * Returns the warehouseId.
	 * 
	 * @return short
	 */
	public short getWarehouseId() {
		return warehouseId;
	}

	/**
	 * Sets the warehouseId.
	 * 
	 * @param warehouseId
	 *            The warehouseId to set
	 */
	public void setWarehouseId(short warehouseId) {
		this.warehouseId = warehouseId;
	}

	/**
	 * Returns the carrierId.
	 * 
	 * @return short
	 */
	public short getCarrierId() {
		return carrierId;
	}

	/**
	 * Sets the carrierId.
	 * 
	 * @param carrierId
	 *            The carrierId to set
	 */
	public void setCarrierId(short carrierId) {
		this.carrierId = carrierId;
	}

	public void debugOut(String message) {
		if (System.getProperty("DEBUG_OUT") != null)
			if (System.getProperty("DEBUG_OUT").equals("ON"))
				System.out.println(message);
	}

	/**
	 * Returns the maxdistrictId.
	 * 
	 * @return int
	 */
	public int getMaxdistrictId() {
		return maxdistrictId;
	}

	/**
	 * Sets the maxdistrictId.
	 * 
	 * @param maxdistrictId
	 *            The maxdistrictId to set
	 */
	public void setMaxdistrictId(int maxdistrictId) {
		this.maxdistrictId = maxdistrictId;
	}

	public void publishMsgToTopic(String logLine) throws EJBException {
		
		debugOut("<< Entering publishMsgToTopic() in Delivery MDB >");
		String timeStamp = new String("");
		timeStamp = getTimeStamp();
		try {
			TextMessage textMessage = topicSession.createTextMessage();
			textMessage.setText(timeStamp + logLine);
			// MLS temporary fix for MQ issue with using JMSCorrelationID as the selector field
			//textMessage.setJMSCorrelationID(messageId);
			textMessage.setStringProperty("ERWWCorrelationID", selectorUUID);
			topicPublisher.send(textMessage);
			debugOut("<<< Dump of topic message after publish: " + textMessage.toString());
			
		} catch (JMSException j) {
			System.out.println("<<< Caught JMSException in publishMsgToTopic(): " + j.getMessage());
			j.printStackTrace();
			Exception e = j.getLinkedException();
			if (e != null) {
				throw new EJBException("Caught JMSException in publishMsgToTopic()", e);
			}
			throw new EJBException("Caught JMSException in publishMsgToTopic()", j);
			
		} catch (Exception e) {
			System.out.println("<<< Caught Exception in publishMsgToTopic(): " + e.getMessage());
			e.printStackTrace();
			throw new EJBException("Caught Exception in publishMsgToTopic()", e);
			
		} finally {
			
			debugOut("<< Exiting publishMsgToTopic() in Delivery MDB >");
		}
	}

	
	/*Comment out unused private method
	private String getSTC() {
		return irwwbase.Stcnum.getInstance().getStcnum();		
	}
	*/
	
	/**
	 * @return Returns the jsFlow.
	 */
	public boolean isJsFlow() {
		return jsFlow;
	}

	/**
	 * @param jsFlow
	 *            The jsFlow to set.
	 */
	public void setJsFlow(boolean jsFlow) {
		this.jsFlow = jsFlow;
	}
	
	private void JMS_MDB_debugOut(String message) {
		if (System.getProperty("JMS_MDB_DEBUG_OUT") != null)
			if (System.getProperty("JMS_MDB_DEBUG_OUT").equals("ON"))
				System.out.println(message);
	}
}