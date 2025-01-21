package deliverysession;


import irwwbase.ExtendedRandom;
import irwwbase.IRWWBase;
import irwwbase.MaxValues;
import irwwbase.UserException;

import jakarta.ejb.Singleton;
import jakarta.ejb.EJB;
/* commenting out unused imports
import deliverysession.DeliveryInput;
import deliverysession.DeliveryOutput;
import deliverysession.DeliverySessionFacade;
import deliverysession.DeliverySessionFacadeRemote;
*/

@Singleton
public class DeliverySessionClient extends IRWWBase
{
	    private final static long serialVersionUID = -7543365099931739321L;
		
		private boolean distributed;   // local or remote EJB interface call?
		private boolean useAsync;      // synchronous or asynchronous EJB call?
		
		@EJB
		DeliverySessionFacade deliverySession;
		
		@EJB
		DeliverySessionFacadeRemote deliverySessionRemote;
		

		public DeliverySessionClient() 
		{
			super();
		}
		

		private DeliveryInput generateDeliveryInput(byte db) 
		{

			ExtendedRandom rand = new ExtendedRandom();

			DeliveryInput input = new DeliveryInput();
			
			input.setCarrierId((short) (rand.nextInt(MaxValues.carrierId(db)+1)));
			input.setWarehouseId((short) rand.nextInt(1, MaxValues.warehouseId(db)));
			input.setMaxdistrictId(MaxValues.districtId(db));

			return input;

		}

		// shupert - removed timeout because async methods Future.get timeout is now a system property
		public void setInput(boolean distributed, boolean useAsync)
		{
			this.distributed = distributed;
			this.useAsync = useAsync;
		}
		
		//this is the moral equivalent to the .web() method on the Client servicing the web projects
		public void doWork() throws UserException 
		{
			byte database = dbSize();

			DeliveryInput input = generateDeliveryInput(database);
			//Comment out unused variable
			DeliveryOutput output = new DeliveryOutput();
			
			// shupert - removed timeout because async methods Future.get timeout is now a system property		
	        //input.setAsyncTimeout(asyncTimeout);
			
	        input.setDistributedEJBs(distributed);
			input.setUse31Async(useAsync);

			if (distributed) 
			{
				debugOut("ERWW - Invoking DeliverySessionFacade.deliverySession() on remote bean exposure.");
			
				// Let exceptions flow back to the timer bean that called doWork().  They will be
				// recorded there for display on the EJB Checker output.
				output = deliverySessionRemote.deliverySession(input);

			} 
			else 
			{
				debugOut("ERWW - Invoking DeliverySessionFacade.deliverySession() on local bean exposure.");

				// Let exceptions flow back to the timer bean that called doWork().  They will be
				// recorded there for display on the EJB Checker output.
				output = deliverySession.deliverySession(input);
			
			}
		}
}
