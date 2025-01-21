package monitor;

import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;

/**
 * Message-Driven Bean implementation class for: MonitorMessageListenerMDB
 */
@MessageDriven(name = "MonitorMessageListenerMDB1.1",
		activationConfig = { @ActivationConfigProperty(
				propertyName = "destinationType", propertyValue = "jakarta.jms.Queue")
		})
public class MonitorMessageListenerMDB implements MessageListener {

    /**
     * Default constructor. 
     */
    public MonitorMessageListenerMDB() {
        // TODO Auto-generated constructor stub
    }
	
	/**
     * @see MessageListener#onMessage(Message)
     */
    @Override
    public void onMessage(Message message) {
    	
    	long enterTime = 0;
    	
    	try {
    		
    		enterTime = System.currentTimeMillis();
    		JMS_WEBSOCKET_MDB_debugOut("  >> JMS MonitorMessageListenerMDB MDBptr = " + this + ", TimeInMilliSec = " + enterTime );
			
    		if (message instanceof jakarta.jms.MapMessage) {
		    	String type = ((jakarta.jms.MapMessage) message).getString("type");
		    	int id = ((jakarta.jms.MapMessage) message).getInt("id");
		    	double value = ((jakarta.jms.MapMessage) message).getDouble("value");
		    	
		    	if ("payment".equals(type)) {
		    		SimpleMonitorEndpoint.sendPaymentTransaction(id, value);
		    	} else if ("neworder".equals(type)) {
		    		SimpleMonitorEndpoint.sendNewOrderTransaction(id, value);
		    	}
    		} else {
    			// not a supported message type
    		}
    	} catch (JMSException ex) {
    		//TODO: exception information using system.out
    		ex.printStackTrace();
    	} catch (Exception e) {
    		e.printStackTrace();
    	} finally {
    		long exitTime = System.currentTimeMillis();
			long totalTime = exitTime - enterTime;
			JMS_WEBSOCKET_MDB_debugOut("  << JMS MonitorMessageListenerMDB MDBptr = " + this + ", TimeInMilliSec = " + exitTime + ", Total time in MDB (msec) = " + totalTime);
    		
    	}
        
    }
    
    private void JMS_WEBSOCKET_MDB_debugOut(String message) {
		if (System.getProperty("JMS_WEBSOCKET_MDB_DEBUG_OUT") != null)
			if (System.getProperty("JMS_WEBSOCKET_MDB_DEBUG_OUT").equals("ON"))
				System.out.println(message);
	}

}
