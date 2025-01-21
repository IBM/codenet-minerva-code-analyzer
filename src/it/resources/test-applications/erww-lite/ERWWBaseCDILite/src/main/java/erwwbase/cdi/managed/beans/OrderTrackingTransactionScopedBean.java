package erwwbase.cdi.managed.beans;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Random;

import jakarta.annotation.Priority;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.transaction.TransactionScoped;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import erwwbase.cdi.data.values.DataValues;
import erwwbase.cdi.events.OrderTrackingEvent;
import erwwbase.cdi.events.OrderTrackingEventTransactionScopedImpl;
import erwwbase.cdi.events.OrderTrackingPayload;
import erwwbase.cdi.events.RequestOrderTrackingEvent;
import erwwbase.cdi.events.RequestOrderTrackingPayload;
import erwwbase.cdi.input.output.pojos.OrderTrackingSingleInstance;
import erwwbase.cdi.qualifiers.OrderTrackingQualifier;
import erwwbase.cdi.qualifiers.OrderTrackingTransactionScopedQualifier;
import orderjpa.OrderJPA;
import orderjpa.OrderKey;
import ordertrackingjpa.OrderTrackingJPA;
import ordertrackingjpa.OrderTrackingKey;
import irwwbase.IRWWBase;
import irwwbase.POJOQualifier;
import irwwbase.UserException;

@TransactionScoped
@Priority(1)
public class OrderTrackingTransactionScopedBean implements Serializable  {
	
	private final static long serialVersionUID = 7529256653789513316L;
	
	@Inject @OrderTrackingTransactionScopedQualifier Event<OrderTrackingEvent> orderTrackingEvent;
	
	@Inject @OrderTrackingQualifier RequestOrderTrackingPayload requestOrderTrackingPayload;
	
	@Inject @OrderTrackingQualifier OrderTrackingPayload orderTrackingPayload;
	
	@Inject @POJOQualifier IRWWBase irwwbase;
	
	private String className = (OrderTrackingTransactionScopedBean.class).getName();
	
	public OrderTrackingTransactionScopedBean(){
	}
		
	public void generateOrderTracking(@Observes @OrderTrackingTransactionScopedQualifier RequestOrderTrackingEvent requestOrderTrackingEvent) throws UserException {
		irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": CDI Event 2: In OrderTrackingBean: Entered generateOrderTracking(@Observes @OrderTrackingTransactionScopedQualifier RequestOrderTrackingEvent requestOrderTrackingEvent: object identity = " + this);
				
		Random rand = new Random();
		int index = rand.nextInt(49) + 1;
		
		short warehouseId = 0;
		short districtId = 0;
		short customerId = 0;
		int orderId = 0;
		EntityManager em = null;
		
		String orderTrackingNumber = null;
		int currentOrderTrackingHopsCount = 0;
		int maxOrderTrackingHop = 0;
		int currentOrderTrackingHop = 0;
		
		OrderJPA order = null;
		OrderKey orderKey = null;
		
		OrderTrackingJPA orderTracking = null;
		OrderTrackingKey orderTrackingKey = null;
		
		OrderTrackingSingleInstance orderTrackingSingleInstance = new OrderTrackingSingleInstance();
		OrderTrackingSingleInstance [] orderTrackingInstances = null;
		
		short activityCode = 0;
		
		irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Get the requestOrderTrackingPayload from the requestOrderTrackingEvent" );
		try{
			requestOrderTrackingPayload = requestOrderTrackingEvent.getRequestOrderTrackingPayload();
		} catch (ConstraintViolationException e){
			for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
				irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Servlet Path: ConstraintViolationException: violationMessage(s): Property Path: " + violation.getPropertyPath().toString() + " Invalid value = " + violation.getInvalidValue() + " Violation Message = " + violation.getMessage());
		    }
			throw new ConstraintViolationException("<<< ThreadId: " + Thread.currentThread().getId() + ": "  + className + ": Servlet Path: ERROR: " + e.getConstraintViolations() , e.getConstraintViolations());
		} catch (Throwable e) {
			System.out.println("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": ERROR when executing requestOrderTrackingEvent.getRequestOrderTrackingPayload(): " + e.getMessage());
			e.printStackTrace();
			throw new UserException("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": ERROR when executing requestOrderTrackingEvent.getRequestOrderTrackingPayload(): ", e.getMessage());
		}		
	
		irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Get the contents of the requestOrderTrackingPayload " );
		try{
			warehouseId = requestOrderTrackingPayload.getWarehouseId();
			districtId = requestOrderTrackingPayload.getDistrictId();
			customerId = requestOrderTrackingPayload.getCustomerId();
			orderId = requestOrderTrackingPayload.getOrderId();
			em = requestOrderTrackingPayload.getEntityManager();		
			orderTrackingNumber = (String.valueOf(warehouseId) + "-" + String.valueOf(districtId) +  "-" +  String.valueOf(customerId) +  "-" + String.valueOf(orderId));
			irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": orderTrackingString = " + orderTrackingNumber);
			irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": em = " + em);
		} catch (Throwable e) {
			System.out.println("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": ERROR with requestOrderTrackingPayload getter methods: " + e.getMessage());
			e.printStackTrace();
			throw new UserException("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": ERROR with requestOrderTrackingPayload getter methods: ", e.getMessage());
		}						
		
		irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Before getting the order instance " );
		try {			
			orderKey = new OrderKey(districtId, orderId, warehouseId);
			order = em.find(OrderJPA.class, orderKey, LockModeType.PESSIMISTIC_WRITE);	
			currentOrderTrackingHopsCount = order.getOrderTrackingHopsCount();
		} catch (Throwable e) {
			System.out.println("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": ERROR getting the order: " + e.getMessage());
			e.printStackTrace();
			throw new UserException("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": ERROR getting the order: ", e.getMessage());
		}		
		
		irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Before getting the maximum OrderTrackingHop from the OrderTracking table " );
		try{
		    // Removed because of known issue with max value returning null https://github.com/OpenLiberty/open-liberty/issues/15408
		    //maxOrderTrackingHop = (int)em.createNamedQuery("findMaxOrderTrackingHopforOrderTrackingNumber").setParameter("orderTrackingNumber", orderTrackingNumber).getSingleResult();
		    jakarta.persistence.Query query = em.createNamedQuery("findMaxOrderTrackingHopforOrderTrackingNumber");
            irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": query = " + query + " orderTrackingNumber = " + orderTrackingNumber);
		    jakarta.persistence.Query query2 = query.setParameter("orderTrackingNumber", orderTrackingNumber);
            irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": query2 = " + query2);
		    if (query2.getSingleResult() != null) {
			    irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": There is a value set for OrderTrackingHop in the db");
			    maxOrderTrackingHop = (int)query2.getSingleResult();
		    }
		    else {
			    irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": There is NOT a value set for OrderTrackingHop in the db so setting the value to 0");
			    maxOrderTrackingHop = 0;
		    }
		    irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": maxOrderTrackingHop = " +  maxOrderTrackingHop);     
		} catch (Exception e){			
			System.out.println("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Error in getting the maxOrderTrackingHop: " + e.getMessage());
			e.printStackTrace();
			throw new UserException("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": ERROR getting the maxOrderTrackingHop: ", e.getMessage());
		}
	
		irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Get the orderTracking instance with the maximum OrderTrackingHop " );
		try {
			orderTrackingKey = new OrderTrackingKey(orderTrackingNumber, maxOrderTrackingHop);
			orderTracking = em.find(OrderTrackingJPA.class, orderTrackingKey, LockModeType.PESSIMISTIC_WRITE);
		} catch (Exception e){			
			System.out.println("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Error in getting the orderTracking instance: " + e.getMessage());
			e.printStackTrace();
			throw new UserException("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": ERROR getting the orderTracking instance: ", e.getMessage());
		} 	
		
		irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Check to see if the Order has already been delivered " );
		if ((orderTracking != null) && (orderTracking.getOrderTrackingActivity() == 3)){	
			irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": order has been delivered previously so get the delivered row(s)");
			
			irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": New up an array with the number of elements equal to the maximum OrderTrackingHop " );
			orderTrackingInstances = new OrderTrackingSingleInstance[maxOrderTrackingHop];
			
			irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Get all the orderTracking instances associated with this order " );
			try{				
				for (short i = 0; i < maxOrderTrackingHop;) {
					short hop = (short)(i+1);
					irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Before orderTrackingInstances[" + i + "] where orderTrackingNumber = " + orderTrackingNumber + " and hop = " + hop );
					orderTrackingInstances[i] = this.getOrderTrackingInstance(orderTrackingNumber, hop, em);	
					i++;
				}					
			} catch (Throwable e) {
				System.out.println("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": ERROR: this.getOrderTrackingInstance(orderTrackingNumber, i, em): " + e.getMessage());
				e.printStackTrace();
				throw new UserException("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": ERROR: this.getOrderTrackingInstance(orderTrackingNumber, i, em): ", e.getMessage());
			}
			
			currentOrderTrackingHop = maxOrderTrackingHop;
			
		irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Order has NOT been delivered yet so create another OrderTracking Hop " );
		} else {			
			if (order != null){
				irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": order.getWarehouseId() = " + order.getWarehouseId());
				irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": order.getDistrictId() = " + order.getDistrictId());
				irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": order.getCustomerId() = " + order.getCustomerId());
				irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": order.getOrderId() = " + order.getOrderId());
				
				irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Increment the orderTrackingHopsCount by 1 in the Orders table " );
				try{
					order.setOrderTrackingNumber(orderTrackingNumber);
					order.setOrderTrackingHopsCount(currentOrderTrackingHopsCount+1);
					em.persist(order);
					irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": order setters and em.persist(order) " );
				} catch (Exception e){			
					System.out.println("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Error in setting the order: " + e.getMessage());
					e.printStackTrace();
					throw new UserException("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Error in setting the order: ", e.getMessage());
				}  
			} 
			
			irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Create a new row in the OrderTracking table " );
			try {
				orderTracking = new OrderTrackingJPA();
				orderTrackingKey = new OrderTrackingKey();
				currentOrderTrackingHop = maxOrderTrackingHop+1;
				
				orderTrackingInstances = new OrderTrackingSingleInstance[currentOrderTrackingHop];
					
				orderTracking.setOrderTrackingNumber(orderTrackingNumber);
				orderTracking.setOrderTrackingHop(currentOrderTrackingHop);
				orderTracking.setOrderTrackingDate(Timestamp.valueOf(LocalDateTime.now()));
				orderTracking.setOrderTrackingCity(DataValues.CITIES[index]);
				orderTracking.setOrderTrackingState(DataValues.STATES[index]);
				orderTracking.setOrderTrackingCountry("USA");
				
				irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Determine whether the order has been delivered or arrived or left a delivery location " );
				if (order.getCarrierId() > 0) {
					irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": order has been delivered");
					orderTracking.setOrderTrackingActivity((short)3);
					activityCode = 3;
				} else if (!((currentOrderTrackingHop%2)==0)){
					irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": orderTrackingHop is an odd number, "
							+ "therefore, the package arrived at the delivery location");
					orderTracking.setOrderTrackingActivity((short)1);
					activityCode = 1;
				} else {
					irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": orderTrackingHop is an even number, "
							+ "therefore, the package left the delivery location");
					orderTracking.setOrderTrackingActivity((short)2);
					activityCode = 2;
				}	
				em.persist(orderTracking);
	
			}catch (Exception e){			
				System.out.println("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Error in orderTracking setters: " + e.getMessage());
				e.printStackTrace();
				throw new UserException("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Error in orderTracking setters: ", e.getMessage());
			} 	
				
			irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Get all the hops that exist in the OrderTracking table and set them in the orderTrackingInstances array ");
			try{				
				for (int i = 0; i < currentOrderTrackingHop; ) {
					short hop = (short)(i+1);
					orderTrackingInstances[i] = this.getOrderTrackingInstance(orderTrackingNumber, hop, em);
					i++;
				}					
			} catch (Throwable e) {
				System.out.println("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": ERROR: this.getOrderTrackingInstance(orderTrackingNumber, i, em): " + e.getMessage());
				e.printStackTrace();
				throw new UserException("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": ERROR: this.getOrderTrackingInstance(orderTrackingNumber, i, em): ", e.getMessage());
			}
			
			irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Set the new hop in the orderTrackingInstances array ");
			try{					
				irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Before orderTrackingInstances setters for 1st one where: orderTrackingNumber = " + orderTrackingNumber);
				irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Before orderTrackingInstances setters for 1st one where: currentOrderTrackingHop = " + currentOrderTrackingHop);
				
				orderTrackingSingleInstance.setOrderTrackingNumber(orderTrackingNumber);
				orderTrackingSingleInstance.setOrderTrackingHop(currentOrderTrackingHop);
				orderTrackingSingleInstance.setOrderTrackingDate(new java.sql.Timestamp(System.currentTimeMillis()));
				orderTrackingSingleInstance.setOrderTrackingCity(DataValues.CITIES[index]);
				orderTrackingSingleInstance.setOrderTrackingState(DataValues.STATES[index]);
				orderTrackingSingleInstance.setOrderTrackingCountry("USA");
				orderTrackingSingleInstance.setOrderTrackingActivity(activityCode);
				
				orderTrackingInstances[currentOrderTrackingHop-1] = orderTrackingSingleInstance;
		
			} catch (Throwable e) {
				System.out.println("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": ERROR: orderTrackingInstances[0] setters: " + e.getMessage());
				e.printStackTrace();
				throw new UserException("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": ERROR: orderTrackingInstances[0] setters: ", e.getMessage());
			}
			
			try{
				orderTrackingPayload.setRequestOrderTrackingPayload(requestOrderTrackingPayload);
			} catch (ConstraintViolationException e){
				for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
					irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Servlet Path: ConstraintViolationException: violationMessage(s): Property Path: " + violation.getPropertyPath().toString() + " Invalid value = " + violation.getInvalidValue() + " Violation Message = " + violation.getMessage());
			    }
				throw new ConstraintViolationException("<<< ThreadId: " + Thread.currentThread().getId() + ": "  + className + ": Servlet Path: ERROR: " + e.getConstraintViolations() , e.getConstraintViolations());
			} catch (Throwable e) {
				System.out.println("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": ERROR when executing orderTrackingPayload setter methods: " + e.getMessage()); 
				e.printStackTrace();
				throw new UserException("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": ERROR when executing orderTrackingPayload setter methods: ", e.getMessage());
			}
						
		}		
			
		irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Set the orderTrackingPayload ");
		try{	
			orderTrackingPayload.setOrderTrackingHopsCount((short)currentOrderTrackingHop);
			orderTrackingPayload.setOrderTrackingInstances(orderTrackingInstances);			
		} catch (ConstraintViolationException e){
			for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
				irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Servlet Path: ConstraintViolationException: violationMessage(s): Property Path: " + violation.getPropertyPath().toString() + " Invalid value = " + violation.getInvalidValue() + " Violation Message = " + violation.getMessage());
		    }
			throw new ConstraintViolationException("<<< ThreadId: " + Thread.currentThread().getId() + ": "  + className + ": Servlet Path: ERROR: " + e.getConstraintViolations() , e.getConstraintViolations());
		} catch (Throwable e) {
			System.out.println("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": ERROR when executing orderTrackingPayload.setOrderTrackingInstances(orderTrackingInstances): " + e.getMessage());
			e.printStackTrace();
			throw new UserException("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": ERROR when executing orderTrackingPayload.setOrderTrackingInstances(orderTrackingInstances): ", e.getMessage());
		}
		
		try{
			orderTrackingEvent.fire(new OrderTrackingEventTransactionScopedImpl(orderTrackingPayload));
			irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": CDI Event 3: orderTrackingEvent.fire(new OrderTrackingEventTransactionScopedImpl(orderTrackingPayload)): object identity = " + this);
		}catch (Exception e){			
			System.out.println("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Error in orderTrackingEvent.fire(new OrderTrackingEventTransactionScopedImpl(orderTrackingPayload)): " + e.getMessage());
			e.printStackTrace();
			throw new UserException("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Error in orderTrackingEvent.fire(new OrderTrackingEventTransactionScopedImpl(orderTrackingPayload)): ", e.getMessage());
		} 
	}
		
	protected OrderTrackingSingleInstance getOrderTrackingInstance(String orderTrackingNumber, short orderTrackingHop, EntityManager em) throws UserException {

		OrderTrackingJPA orderTracking = null;
		OrderTrackingSingleInstance orderTrackingSingleInstance = new OrderTrackingSingleInstance();
			
		try {
			OrderTrackingKey orderTrackingKey = new OrderTrackingKey();
			orderTrackingKey.setOrderTrackingNumber(orderTrackingNumber);
			orderTrackingKey.setOrderTrackingHop(orderTrackingHop);
			orderTracking = em.find(OrderTrackingJPA.class, orderTrackingKey);
			irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": getOrderTrackingInstance: orderTrackingNumber = " + orderTrackingNumber);
			irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": getOrderTrackingInstance: orderTrackingHop = " + orderTrackingHop);
		} catch (Exception e){			
			System.out.println("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Error in finding the orderTracking instance: " + e.getMessage());;
			e.printStackTrace();
			throw new UserException("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Error in finding the orderTracking instance: ", e.getMessage());
		} 
		
		if (orderTracking == null){
			irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": getOrderTrackingInstance: No more OrderTracking rows in the table");
		} else {		
			try {
				irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": getOrderTrackingInstance: orderTrackingNumber = " + orderTrackingNumber + " orderTrackingHop = " + orderTrackingHop);
				orderTrackingSingleInstance.setOrderTrackingNumber(orderTracking.getOrderTrackingNumber());
				orderTrackingSingleInstance.setOrderTrackingHop(orderTracking.getOrderTrackingHop());
				orderTrackingSingleInstance.setOrderTrackingDate(orderTracking.getOrderTrackingDate());
				orderTrackingSingleInstance.setOrderTrackingCity(orderTracking.getOrderTrackingCity());
				orderTrackingSingleInstance.setOrderTrackingState(orderTracking.getOrderTrackingState()); 
				orderTrackingSingleInstance.setOrderTrackingCountry(orderTracking.getOrderTrackingCountry()); 
				orderTrackingSingleInstance.setOrderTrackingActivity(orderTracking.getOrderTrackingActivity());		
			} catch (Exception e){			
				System.out.println("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Error in getOrderTrackingInstance setters: " + e.getMessage());
				e.printStackTrace();
				throw new UserException("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Error in getOrderTrackingInstance setters: ", e.getMessage());
			} 
		}
		
		return orderTrackingSingleInstance;
	} 		
}
 
