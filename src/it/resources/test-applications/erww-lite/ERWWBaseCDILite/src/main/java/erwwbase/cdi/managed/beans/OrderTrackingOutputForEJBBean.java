package erwwbase.cdi.managed.beans;

import java.sql.SQLException;
import java.sql.SQLWarning;

import irwwbase.IRWWBase;
import irwwbase.POJOQualifier;
import irwwbase.UserException;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.servlet.ServletException;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;

import orderjpa.OrderJPA;
import ordertrackingjpa.OrderTrackingJPA;
import ordertrackingjpa.OrderTrackingKey;
import erwwbase.cdi.input.output.pojos.OrderOutput;
import erwwbase.cdi.input.output.pojos.OrderTrackingSingleInstance;
import erwwbase.cdi.interceptors.ApplicationTraceInterceptor;
import erwwbase.cdi.qualifiers.OrderTrackingTransactionalQualifier;

@Priority(1)

@Transactional (value = Transactional.TxType.REQUIRES_NEW,
rollbackOn = {SQLException.class},
dontRollbackOn = {SQLWarning.class}) 

@OrderTrackingTransactionalQualifier
@Dependent
public class OrderTrackingOutputForEJBBean implements java.io.Serializable {

	private static final long serialVersionUID = 7537800259172794151L;

	private String className = (OrderTrackingOutputForEJBBean.class).getName();
	
	@Inject @POJOQualifier IRWWBase irwwbase;
	
	@ApplicationTraceInterceptor
	public OrderOutput obtainOrderTrackingOutput(OrderOutput output, EntityManager em) throws UserException {
		
		OrderJPA order = null;
		
		irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": EJB Path: Before getOrder");
		try {
			order = this.getOrder(output.getOutCustomerId(),output.getOutCustomerWarehouseId(),output.getOutCustomerDistrictId(), em);
		} catch (Throwable e) {
			System.out.println("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": EJB Path: ERROR when getting the order: " + e.getMessage());
			e.printStackTrace();
			throw new UserException("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": EJB Path: ERROR when getting the order", e.getMessage());
		}
		
		//why doesn't this reflect the updated Orders table information?
		irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": EJB Path: Initialize the orderTrackingInstances array with using the Order Tracking Hops Count: " + order.getOrderTrackingHopsCount());
		OrderTrackingSingleInstance [] orderTrackingInstances = new OrderTrackingSingleInstance[order.getOrderTrackingHopsCount()];			
		
		irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": EJB Path: Get all the hops that exist in the OrderTracking table and set them in the orderTrackingInstances array ");
		try{				
			for (int i = 0; i < order.getOrderTrackingHopsCount(); ) {
				short hop = (short)(i+1);
				orderTrackingInstances[i] = this.getOrderTrackingInstance(order.getOrderTrackingNumber(), hop, em);
				i++;
			}					
		} catch (ConstraintViolationException e){
			System.out.println("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": EJB Path: this.getOrderTrackingInstance(orderTrackingNumber, i, em): ConstraintViolationException: " + e.getMessage()); 
			e.printStackTrace();
			throw new UserException("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": EJB Path: this.getOrderTrackingInstance(orderTrackingNumber, i, em): ConstraintViolationException", e.getMessage());
		} catch (Throwable e) {
			System.out.println("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": ERROR: EJB Path: this.getOrderTrackingInstance(orderTrackingNumber, i, em) : " + e.getMessage());
			e.printStackTrace();
			throw new UserException("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": ERROR: EJB Path: this.getOrderTrackingInstance(orderTrackingNumber, i, em) ", e.getMessage());
		}	

		irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": EJB Path: Before setting the orderTrackingNumber in the output object");
		try {
			irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": EJB Path: warehouseId = " + order.getWarehouseId());
			irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": EJB Path: districtId = " + order.getDistrictId());
			irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": EJB Path: customerId = " + order.getCustomerId());
			irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": EJB Path: orderId = " + order.getOrderId());
			irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": EJB Path: orderTrackingNumber (from order) = " + order.getOrderTrackingNumber());
			output.setOrderTrackingInstances(orderTrackingInstances);
			output.setOutOrderTrackingHopsCount((short)order.getOrderTrackingHopsCount());
			irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": EJB Path: orderTrackingNumber (from output) = " + output.getOrderTrackingInstances()[0].getOrderTrackingNumber());
		} catch (Throwable e) {
			System.out.println("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": EJB Path: ERROR when getting then setting the orderTrackingNumber: " + e.getMessage());
			e.printStackTrace();
			throw new UserException("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": EJB Path: ERROR when getting then setting the orderTrackingNumber ", e.getMessage());
		}	
		
		return output;
	}
	
	@ApplicationTraceInterceptor
	OrderJPA getOrder(short inCustomerId, short inCustomerWarehouseId, short inCustomerDistrictId, EntityManager em) throws UserException, ServletException {

		irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": EJB Path: Entered getOrder()");
		OrderJPA order = null;
		
		int orderId = 0;

		try {
			Query q = em.createNamedQuery("findMaxOrderId");
			q.setParameter("customerId", inCustomerId);
			q.setParameter("districtId", inCustomerDistrictId);
			q.setParameter("warehouseId", inCustomerWarehouseId);

			order = (OrderJPA) q.getSingleResult();
			if (order == null)
				throw new RuntimeException("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": EJB Path: Order not found ");

			if (order != null) {
				orderId = order.getOrderId();
				irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": EJB Path: OrderId: " + order.getOrderId());
			}
		} catch (Throwable e) {
			e.printStackTrace();
			System.out.println("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": EJB Path: ERROR: orderid: " + orderId + "customerid: "
					+ inCustomerId + "districtid: " + inCustomerDistrictId
					+ "warehouseid: " + inCustomerWarehouseId + ": " + e.getMessage());
			throw new ServletException("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": EJB Path: ERROR: failure in getOrder: orderid: " + orderId
					+ "; customerid: " + inCustomerId + "; districtid: " + inCustomerDistrictId + ""
					+ "; warehouseid: " + inCustomerWarehouseId + "; " + " exception message: "
					+ e.getMessage() + "; exception cause: " + e.getCause(), e);
		}
		return order;
	}
	
	@ApplicationTraceInterceptor
	OrderTrackingSingleInstance getOrderTrackingInstance(String orderTrackingNumber, short orderTrackingHop, EntityManager em) throws UserException, ServletException {

		OrderTrackingJPA orderTracking = null;
		OrderTrackingSingleInstance orderTrackingSingleInstance = new OrderTrackingSingleInstance();
			
		try {
			OrderTrackingKey orderTrackingKey = new OrderTrackingKey();
			orderTrackingKey.setOrderTrackingNumber(orderTrackingNumber);
			orderTrackingKey.setOrderTrackingHop(orderTrackingHop);
			orderTracking = em.find(OrderTrackingJPA.class, orderTrackingKey);
			irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": EJB Path: getOrderTrackingInstance: orderTrackingNumber = " + orderTrackingNumber);
			irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": EJB Path: getOrderTrackingInstance: orderTrackingHop = " + orderTrackingHop);
		} catch (Exception e){			
			System.out.println("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": EJB Path: Error in getOrderTrackingInstance method: " + e.getMessage());
			e.printStackTrace();
			throw new ServletException("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": EJB Path: Error in getOrderTrackingInstance method ", e);
		} 
		
		if (orderTracking == null){
			irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": EJB Path: getOrderTrackingInstance: No more OrderTracking rows in the table");
		} else {		
			try {
				irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": EJB Path: getOrderTrackingInstance: orderTrackingNumber = " + orderTrackingNumber + " orderTrackingHop = " + orderTrackingHop);
				orderTrackingSingleInstance.setOrderTrackingNumber(orderTracking.getOrderTrackingNumber());
				orderTrackingSingleInstance.setOrderTrackingHop(orderTracking.getOrderTrackingHop());
				orderTrackingSingleInstance.setOrderTrackingDate(orderTracking.getOrderTrackingDate());
				orderTrackingSingleInstance.setOrderTrackingCity(orderTracking.getOrderTrackingCity());
				orderTrackingSingleInstance.setOrderTrackingState(orderTracking.getOrderTrackingState()); 
				orderTrackingSingleInstance.setOrderTrackingCountry(orderTracking.getOrderTrackingCountry()); 
				orderTrackingSingleInstance.setOrderTrackingActivity(orderTracking.getOrderTrackingActivity());		
			} catch (Exception e){			
				System.out.println("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": EJB Path: Error in getOrderTrackingInstance method with the setters: " + e.getMessage());
				e.printStackTrace();
				throw new ServletException("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": EJB Path: Error in getOrderTrackingInstance method with the setters ", e);
			} 
		}
		
		return orderTrackingSingleInstance;
	} 

}
