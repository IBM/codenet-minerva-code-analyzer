package decorators;

import irwwbase.UserException;

import jakarta.decorator.Decorator;
import jakarta.decorator.Delegate;
import jakarta.inject.Inject;

import deliverysession.DeliveryInput;
import deliverysession.DeliveryOutput;
import deliverysession.DeliverySessionFacade;



import java.sql.Timestamp;

@Decorator
public class DeliveryDecorator implements DeliverySessionFacade {
protected String nonProxyName = "DeliveryDecorator";
@Inject @Delegate DeliverySessionFacade dei;



private String getTimeStamp() {

	// Get Time Stamp in the form of yyyy-mm-dd-hh.mm.ss.nnnnnn
	// The toString methods returns the following format: dow mon dd
	// hh:mm:ss zzz yyyy

	Timestamp timestamp = new Timestamp(java.lang.System
			.currentTimeMillis());
	String timeStamp = timestamp.toString();

	return timeStamp; // method getTimeStamp

}

@Override
public DeliveryOutput deliverySession(DeliveryInput input) throws UserException {
	String timeStamp = new String("");
	timeStamp = getTimeStamp();

	try {
		java.io.FileWriter resultFile = new java.io.FileWriter("deliveryEntryStatus.out");
		resultFile.write(timeStamp + " - Start of delivery operation (logged via Decorator) ");
		resultFile.close();
	} catch (Exception ex) {
		
		ex.printStackTrace();
		throw new UserException(
				"%%%%% ERROR: resultFile.write failed in log method - "
						, ex.getMessage());
	}
	return dei.deliverySession(input);
}





}
