package order.tracking.session.ejb;

import static jakarta.enterprise.event.TransactionPhase.BEFORE_COMPLETION;
import irwwbase.IRWWBase;
import irwwbase.POJOQualifier;
import irwwbase.UserException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.EventMetadata;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.inject.Inject;

import erwwbase.cdi.events.OrderTrackingEvent;
import erwwbase.cdi.events.RequestOrderTrackingEventTransactionalImpl;
import erwwbase.cdi.events.RequestOrderTrackingPayload;
import erwwbase.cdi.events.RequestOrderTrackingEvent;
import erwwbase.cdi.input.output.pojos.OrderInput;
import erwwbase.cdi.input.output.pojos.OrderOutput;
import erwwbase.cdi.interceptors.ApplicationTraceInterceptor;
import orderjpa.OrderJPA;
import customerjpa.CustomerJPA;
import customerjpa.CustomerKey;
import erwwbase.cdi.qualifiers.OrderTrackingTransactionalQualifier;
import erwwbase.cdi.qualifiers.OrderTrackingQualifier;

@Stateless
@LocalBean
@ApplicationTraceInterceptor
public class OrderTrackingSessionBean {
	
	private String className = (OrderTrackingSessionBean.class).getName();
	
	private CustomerJPA customer = null;
	private OrderJPA order = null;
	
	@Inject @POJOQualifier IRWWBase irwwbase;
	
	@Inject @OrderTrackingTransactionalQualifier Event<RequestOrderTrackingEvent> requestOrderTrackingEvent;
	
	@Inject @OrderTrackingQualifier RequestOrderTrackingPayload requestOrderTrackingPayload;
	
	@PersistenceContext(unitName = "OrderTrackingSessionEJBLite")
	EntityManager em;
	
    public OrderTrackingSessionBean() {
    }
    
    @ApplicationTraceInterceptor
	public void getCustomerByLastName(String lastName, short districtId,
			short warehouseId) throws UserException {

		int noCustomers = 0;
		int noCustDiv2 = 0;
		int remainder = 0;

		java.util.Vector<CustomerJPA> v = new java.util.Vector<CustomerJPA>();

		CustomerJPA customer;

		irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": EJB Path: getCustomerByLastName: Searching Customer with LastName " + lastName);

		try {
			Query q = em.createNamedQuery("findCustomerByLastName");
			q.setParameter("lastName", lastName);
			q.setParameter("districtId", districtId);
			q.setParameter("warehouseId", warehouseId);
			Collection<?> coll = q.getResultList();
			Iterator<?> it = coll.iterator();

			while (it.hasNext()) {
				noCustomers++;
				customer = (CustomerJPA) it.next();
				v.addElement(customer);
			}
		} catch (Throwable e) {
			e.printStackTrace();
			throw new UserException(
					"<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": EJB Path: Error on query in findCustomerByLastName(....), lastName = "
							+ lastName + ", district = " + districtId
							+ " and warehouse = " + warehouseId  
							+ "; exception message: " + e.getMessage()
							+ "; exception cause: " + e.getCause() + "; " , irwwbase.getStackTrace(e));
		}
		if (noCustomers == 1) {
			noCustDiv2 = 1;
		} else {
			noCustDiv2 = noCustomers / 2;
			remainder = noCustomers % 2;

			if (remainder > 0) {
				noCustDiv2 = noCustDiv2 + 1;
			}
		}

		try {
			if (noCustDiv2 > 0) {
				customer = v.elementAt(noCustDiv2 - 1);
				getCustomerInstance(customer.getCustomerId(), customer.getCustomerDId(),customer.getCustomerWId());
			}
		} catch (Throwable e) {
			System.out.println("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": EJB Path: ERROR: getting customerInstance failed in getCustomerByLastName method:  " + e.getMessage());
			e.printStackTrace();
			throw new UserException("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": EJB Path: ERROR: getting customerInstance failed in getCustomerByLastName method  "
					+ "; exception message: " + e.getMessage()
					+ "; exception cause: " + e.getCause() + "; " , irwwbase.getStackTrace(e));
		} // end try
	}

    @ApplicationTraceInterceptor
	protected void getCustomerInstance(short customerId, short districtId, short warehouseId) throws UserException {

		irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": EJB Path: Entered getCustomerInstance()");
		
		try {
			CustomerKey key = new CustomerKey();
			key.setCustomerDId(districtId);
			key.setCustomerId(customerId);
			key.setCustomerWId(warehouseId);

			customer = em.find(CustomerJPA.class, key);
			if (customer == null)
				throw new UserException("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": EJB Path: customer not found: district="
						+ districtId + ", customer=" + customerId
						+ " and warehouse=" + warehouseId );
		} catch (Throwable e) {
			e.printStackTrace();
			throw new UserException("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": EJB Path: failure in getCustomerInstance: "
					+ " exception message: " + e.getMessage()
					+ "; exception cause: " + e.getCause() + "; ", irwwbase.getStackTrace(e));
		} // end try
	}

    @ApplicationTraceInterceptor
	private void getOrderInstance(short inCustomerId, short inCustomerWarehouseId, short inCustomerDistrictId) throws UserException {

		irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": EJB Path: Entered getOrder()");
		
		int orderId = 0;

		try {

			Query q = em.createNamedQuery("findMaxOrderId");
			q.setParameter("customerId", inCustomerId);
			q.setParameter("districtId", inCustomerDistrictId);
			q.setParameter("warehouseId", inCustomerWarehouseId);

			order = (OrderJPA) q.getSingleResult();
			if (order == null)
				throw new RuntimeException("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": EJB Path: order not found ");

			if (order != null) {
				orderId = order.getOrderId();
				irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": EJB Path: OrderId: " + order.getOrderId());
			}
		} catch (Throwable e) {
			e.printStackTrace();
			System.out.println("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": EJB Path: orderid: " + orderId + "customerid: "
					+ inCustomerId + "districtid: " + inCustomerDistrictId
					+ "warehouseid: " + inCustomerWarehouseId + ": " + e.getMessage());
			throw new UserException("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": EJB Path: failure in getOrder: orderid: " + orderId
					+ "customerid: " + inCustomerId + "districtid: "
					+ inCustomerDistrictId + "warehouseid: "
					+ inCustomerWarehouseId + ", " + " exception message: "
					+ e.getMessage() + ", exception cause: " + e.getCause()
					+ ": ", irwwbase.getStackTrace(e));
		}
	}

    @ApplicationTraceInterceptor
	public OrderOutput requestOrderTrackingSession(OrderInput input, Boolean ejbInvalidDataFlag) throws Exception, ConstraintViolationException, UserException {
	
		irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": EJB Path: Entered requestOrderTrackingSession(input)");
		
		OrderOutput output = new OrderOutput();
		
		output.setOutCustomerWarehouseId(input.getInCustomerWarehouseId());
		output.setOutCustomerDistrictId(input.getInCustomerDistrictId());

		if (input.isInByLastName()) {
			getCustomerByLastName(input.getInCustomerLastName(), input
					.getInCustomerDistrictId(), input
					.getInCustomerWarehouseId());
		} else {
			getCustomerInstance(input.getInCustomerId(), input
					.getInCustomerDistrictId(), input
					.getInCustomerWarehouseId());
		}

		if (customer != null) {
			output.setOutCustomerId(customer.getCustomerId());
			output.setOutCustomerFirstName(customer.getCustomerFirst());
			output.setOutCustomerMiddleName(customer.getCustomerMiddle());
			output.setOutCustomerLastName(customer.getCustomerLast());
			output.setOutCustomerBalance((customer.getCustomerBalance()).doubleValue());

			this.getOrderInstance(output.getOutCustomerId(), output.getOutCustomerWarehouseId(), output.getOutCustomerDistrictId());
			output.setOutOrderId(order.getOrderId());

			irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": EJB Path: OrderId: " + output.getOutOrderId());
			irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": EJB Path: CustomerId: " + output.getOutCustomerId());
			
			try {
				requestOrderTrackingPayload.setWarehouseId(output.getOutCustomerWarehouseId());				
				requestOrderTrackingPayload.setDistrictId(output.getOutCustomerDistrictId());
				requestOrderTrackingPayload.setCustomerId(output.getOutCustomerId());
				requestOrderTrackingPayload.setOrderId(output.getOutOrderId());
				requestOrderTrackingPayload.setEntityManager(em);	
				if (ejbInvalidDataFlag == true){
					requestOrderTrackingPayload.setEntityManager(null); // Tests Bean Validation:  Programmatic approach:  EJB Path
				}
			} catch (ConstraintViolationException e){
				for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
					irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": EJB Path: ConstraintViolationException: violationMessage(s): Property Path: " + violation.getPropertyPath().toString() + " Invalid value = " + violation.getInvalidValue() + " Violation Message = " + violation.getMessage());
			    }
				throw new ConstraintViolationException("<<< ThreadId: " + Thread.currentThread().getId() + ": "  + className + ": EJB Path: ERROR: " + e.getConstraintViolations() , e.getConstraintViolations());	
			} catch (Throwable e) {
				System.out.println("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": EJB Path: ERROR when executing requestOrderTrackingSession() setter methods: " + e.getMessage());
				e.printStackTrace();
				throw new UserException("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": EJB Path: ERROR when executing requestOrderTrackingSession() setter methods: ", e.getMessage());
			}
			
			try{
				irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": EJB Path: CDI Event 1: Before (new RequestOrderTrackingEventTransactionalImpl(requestOrderTrackingPayload))): object identity = " + this);
				requestOrderTrackingEvent.fire(new RequestOrderTrackingEventTransactionalImpl(requestOrderTrackingPayload));
			}catch (Throwable e){			
				System.out.println("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": EJB Path: Error in requestOrderTrackingEvent.fire: " + e.getMessage());
				e.printStackTrace();
				throw new UserException("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": EJB Path: Error in requestOrderTrackingEvent.fire: ", e.getMessage());
			}		
		} else {
			output.setOutErrorMsg("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": EJB Path: Customer " + input.getInCustomerLastName() + " not Found");
		}
		irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": EJB Path: Exit requestOrderTrackingSession(input)");

		return output;
	}
	
    @ApplicationTraceInterceptor
	public void getOrderTrackingSession(@Observes(during=BEFORE_COMPLETION) @OrderTrackingTransactionalQualifier OrderTrackingEvent orderTrackingEvent, EventMetadata eventMetadata) throws Exception {
	    
		irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": EJB Path: CDI Event 4: In " + className + ": getOrderTracking(@Observes(during=BEFORE_COMPLETION) @OrderTrackingTransactionalQualifier OrderTrackingEvent, EventMetadata eventMetadata): object identity = " + this);	 
		
		OrderOutput output = new OrderOutput();
		String orderTrackingNumber = null;
		
		Type eventType = eventMetadata.getType();
		Set<Annotation> qualifiers = eventMetadata.getQualifiers();
		
		irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": EJB Path: eventType = " + eventType);
		irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": EJB Path: event qualifiers = " +  qualifiers);

	    if (output!= null && orderTrackingEvent!= null){
	    	 try{
	    		 orderTrackingNumber = orderTrackingEvent.getOrderTrackingPayload().getOrderTrackingInstances()[0].getOrderTrackingNumber();
	    		 irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": EJB Path: getOrderTracking observer method where orderTrackingNumber = " + orderTrackingNumber);		    		 
	    	 } catch (Throwable e) {
	    		System.out.println("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": EJB Path: Error in getOrderTracking(@Observes(during=BEFORE_COMPLETION) @OrderTrackingTransactionalQualifier OrderTrackingEvent, EventMetadata eventMetadata): " + e.getMessage());
	 			e.printStackTrace();
				throw new UserException("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": EJB Path: Error in " +className +  ": getOrderTracking: Exception calling orderTrackingEvent.getOrderTrackingPayload().getOrderTrackingNumber(): "	+ 
						" Error Message: " + e.getMessage()	+ "; Exception Cause: " + e.getCause());
			} 
	    }else{
	    	 System.out.println("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": EJB Path: Error in getOrderTracking(@Observes(during=BEFORE_COMPLETION) @OrderTrackingTransactionalQualifier OrderTrackingEvent, EventMetadata eventMetadata)" );
	    	 throw new Exception("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": EJB Path: Error in " + className +  ": getOrderTracking(): either output or orderTrackingEvent is null ");
	    }
	} 

}
