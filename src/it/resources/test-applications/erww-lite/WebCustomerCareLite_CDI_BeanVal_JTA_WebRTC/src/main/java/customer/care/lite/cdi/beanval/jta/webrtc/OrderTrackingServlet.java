package customer.care.lite.cdi.beanval.jta.webrtc;

import static jakarta.enterprise.event.TransactionPhase.BEFORE_COMPLETION;
import irwwbase.IRWWBase;
import irwwbase.POJOQualifier;
import irwwbase.UserException;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import jakarta.ejb.EJB;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.EventMetadata;
import jakarta.inject.Inject;
import javax.naming.InitialContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.RollbackException;
import jakarta.transaction.UserTransaction;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import customerjpa.CustomerJPA;
import customerjpa.CustomerKey;
import erwwbase.cdi.events.OrderTrackingEvent;
import erwwbase.cdi.events.RequestOrderTrackingEvent;
import erwwbase.cdi.events.RequestOrderTrackingEventTransactionScopedImpl;
import erwwbase.cdi.events.RequestOrderTrackingPayload;
import erwwbase.cdi.input.output.pojos.OrderInput;
import erwwbase.cdi.input.output.pojos.OrderOutput;
import erwwbase.cdi.input.output.pojos.OrderTrackingSingleInstance;
import erwwbase.cdi.interceptors.ApplicationTraceInterceptor;
import erwwbase.cdi.managed.beans.HandleOrderInputBean;
import erwwbase.cdi.managed.beans.OrderTrackingOutputForEJBBean;
import erwwbase.cdi.qualifiers.OrderTrackingQualifier;
import erwwbase.cdi.qualifiers.OrderTrackingTransactionScopedQualifier;
import erwwbase.cdi.qualifiers.OrderTrackingTransactionalQualifier;
import order.tracking.session.ejb.OrderTrackingSessionBean;
import orderjpa.OrderJPA;

@WebServlet("/OrderTrackingServlet")
@ApplicationTraceInterceptor
@Dependent
public class OrderTrackingServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private String className = (OrderTrackingServlet.class).getName();
	
	@PersistenceContext(unitName = "WebCustomerCareLite_CDI_BeanVal_JTA_WebRTC")
	EntityManager em;
	
	@Inject @OrderTrackingQualifier HandleOrderInputBean handleOrderInputBean;  //Contains business logic for obtainManualInput and generateAutogen Input
		
	@Inject @OrderTrackingTransactionScopedQualifier Event<RequestOrderTrackingEvent> requestOrderTrackingEvent;
	
	@EJB 
    OrderTrackingSessionBean orderTrackingSessionBean;
	
	@Inject @OrderTrackingTransactionalQualifier OrderTrackingOutputForEJBBean orderTrackingOutputForEJBBean;
	
	@Inject @POJOQualifier IRWWBase irwwbase;	
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public OrderTrackingServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@ApplicationTraceInterceptor
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, ConstraintViolationException, IOException {
		
		String status="FAILED";
		InitialContext initialContext = null;
		UserTransaction userTran = null;
		HttpSession aHttpSession = null;
		
		OrderInput input = new OrderInput();
		
		OrderOutput output = new OrderOutput();	
		output = this.generateOrderOutput(irwwbase.dbSize());
		
		String inputPath = request.getParameter("inputPath");		
		String inputType = request.getParameter("inputType");
		
		Boolean ejbInvalidDataFlag = false;

		List<String> violationMessages = new ArrayList<String>();
		
		if (aHttpSession == null) {
			irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": The http session session does not exist; Before create a new session");
			try {
				aHttpSession = request.getSession(true);			
			} catch (Throwable e) {
				System.out.println("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ":  ERROR when creating a new session: " + e.getMessage());
				e.printStackTrace();
				throw new ServletException("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": ERROR when creating a new session", e);
			}
		}
		
		if (inputType.trim().equalsIgnoreCase("Manual")){
				
			try{	
				input = handleOrderInputBean.obtainManualInput(request);
			} catch (ConstraintViolationException e){					
				for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
			    	violationMessages.add(String.format("\n" + violation.getMessage()));
			    	irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Manual Input: ConstraintViolationException: violationMessage(s): Property Path: " + violation.getPropertyPath().toString() + " Invalid value = " + violation.getInvalidValue() + " Violation Message = " + violation.getMessage());
			    }
				aHttpSession.setAttribute("violationMessages", violationMessages); 
				this.dispatch(request, response, "OrderTrackingInputErrorForm.jsp");
				return;
			} catch (Throwable e) {
				System.out.println("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": ERROR when executing Manual Input: " + e.getMessage());
				e.printStackTrace();
				throw new ServletException("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": ERROR when executing Manual Input: ", e );
			}
		} else if (inputType.trim().equalsIgnoreCase("AutoGen")){
			try{
				input = handleOrderInputBean.generateAutoGenInput(irwwbase.dbSize());
			} catch (ConstraintViolationException e){
			    for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
			    	violationMessages.add(String.format("\n" + violation.getMessage()));
			    	irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": AutoGen Input: ConstraintViolationException: violationMessage(s): Property Path: " + violation.getPropertyPath().toString() + " Invalid value = " + violation.getInvalidValue() + " Violation Message = " + violation.getMessage());
			    }
				aHttpSession.setAttribute("violationMessages", violationMessages); 
				this.dispatch(request, response, "OrderTrackingInputErrorForm.jsp");
				return;
			}  catch (Throwable e) {
				System.out.println("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": ERROR when executing Autogen Input: " + e.getMessage());
				e.printStackTrace();
				throw new ServletException("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": ERROR when executing Autogen Input: ", e);
			}
		}
		
		if (inputPath.contains("Servlet")){
			try {
				irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Servlet Path: Before beginning a transaction in doPost method");
				initialContext = new InitialContext();
				userTran = (UserTransaction) initialContext.lookup("java:comp/UserTransaction");
				userTran.begin();
			} catch (Throwable e) {
				System.out.println("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Servlet Path: ERROR when executing userTran.begin(): " + e.getMessage());
				e.printStackTrace();
				throw new ServletException("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Servlet Path: ERROR when executing userTran.begin()", e);
			}			
			
			try {
				output = this.requestOrderTrackingServlet(input, request);
			} catch (Throwable e) {
				System.out.println("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Servlet Path: ERROR when executing this.requestOrderTrackingServlet(input): " + e.getMessage());
				e.printStackTrace();
				throw new ServletException("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Servlet Path: ERROR when executing this.requestOrderTrackingServlet(input)", e);
			}
			
			irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Servlet Path: Before setting the output object in the session");
			try {
				aHttpSession.setAttribute("output", output);			
			} catch (Throwable e) {
				System.out.println("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Servlet Path: ERROR when setting the output object in the session: " + e.getMessage());
				e.printStackTrace();
				throw new ServletException("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Servlet Path: ERROR when setting the output object in the session", e);
			}
						
			try {	
				irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Servlet Path: Before commit of a transaction in doPost method");
				userTran.commit();
				status="SUCCESSFUL";
				output.setStatus(status);
			} catch (RollbackException e) {
				System.out.println("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Servlet Path: ERROR when commit of a transaction in doPost method: " + e.getMessage());
				irwwbase.loopThruExes(e);
				e.printStackTrace();
				throw new ServletException("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Servlet Path: ERROR when commit of a transaction in doPost method", e);
			} catch (Throwable e) {
				System.out.println("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Servlet Path: ERROR when commit of a transaction in doPost method: " + e.getMessage());
				e.printStackTrace();
				throw new ServletException("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Servlet Path: ERROR when commit of a transaction in doPost method", e);
			}
			
			irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Servlet Path: Before setting the output object in the session");
			try {
				aHttpSession.setAttribute("output", output);			
			} catch (Throwable e) {
				System.out.println("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Servlet Path: ERROR when setting the output object in the session: " + e.getMessage());
				e.printStackTrace();
				throw new ServletException("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Servlet Path: ERROR when setting the output object in the session", e);
			}
		} else if (inputPath.contains("EJB")){	
			
			if (inputPath.contains("Invalid Data")){
				ejbInvalidDataFlag = true;				
			}
						
			irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": EJB Path: Before orderTrackingSession.requestOrderTrackingSession(input)");
			try {
				output = orderTrackingSessionBean.requestOrderTrackingSession(input, ejbInvalidDataFlag); //Global Transaction				
			} catch (ConstraintViolationException e){					
				for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
			    	violationMessages.add(String.format("\n" + violation.getMessage()));
			    	irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": EJB Path: ConstraintViolationException: violationMessage(s): Property Path: " + violation.getPropertyPath().toString() + " Invalid value = " + violation.getInvalidValue() + " Violation Message = " + violation.getMessage());
			    }
				aHttpSession.setAttribute("violationMessages", violationMessages); 
				this.dispatch(request, response, "OrderTrackingError.jsp");
				return;				
			} catch (Throwable e) {
				System.out.println("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": ERROR when executing orderTrackingSession.requestOrderTrackingSession(input): " + e.getMessage());
				e.printStackTrace();
				throw new ServletException("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": ERROR executing orderTrackingSession.requestOrderTrackingSession(input)", e);
			}
			
			try {
				output = orderTrackingOutputForEJBBean.obtainOrderTrackingOutput(output, em);
			} catch (Throwable e) {
				System.out.println("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": ERROR when executing orderTrackingOutputForEJBBean.obtainOrderTrackingOutput(output, em): " + e.getMessage());
				e.printStackTrace();
				throw new ServletException("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": ERROR executing orderTrackingOutputForEJBBean.obtainOrderTrackingOutput(output, em)", e);
			}
			
			irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Before setting the output object in the session");
			try {
				status="SUCCESSFUL";
				output.setStatus(status);
				aHttpSession.setAttribute("output", output);			
			} catch (Throwable e) {
				System.out.println("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": ERROR when setting the output object in the session: " + e.getMessage());
				e.printStackTrace();
				throw new ServletException("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": ERROR when setting the output object in the session: ", e);
			}				
		} 	
		
		this.dispatch(request, response, "OrderTrackingResultsForm.jsp");	
	}	
	
	@ApplicationTraceInterceptor
	private OrderOutput generateOrderOutput(byte db) 
	{
	   OrderOutput output = new OrderOutput();
	   output.outCustomerWarehouseId = 0;
	   output.outCustomerDistrictId = 0;
	   output.outCustomerId = 0;
	   output.outCustomerFirstName = " ";
	   output.outCustomerMiddleName = " ";
	   output.outCustomerLastName = " ";
	   output.outCustomerBalance = (double)0;
	   output.outOrderId = 0;
	   output.outOrderEntryDate = " ";
	   output.outOrderCarrierId = 0;
	   output.outOrderLineCount = 0;
	   output.outErrorMsg = " ";
	   return output;
	}

	@ApplicationTraceInterceptor
	private OrderOutput requestOrderTrackingServlet(OrderInput input, HttpServletRequest request) throws ConstraintViolationException, ServletException {
		irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Servlet Path: Entered requestOrderTrackingServlet(input, request)");
		
		CustomerJPA customer = null;
		OrderJPA order = null;
		OrderOutput output = new OrderOutput();
		
		output = this.generateOrderOutput(irwwbase.dbSize());

		RequestOrderTrackingPayload requestOrderTrackingPayload = new RequestOrderTrackingPayload();
			
		output.setOutCustomerWarehouseId(input.getInCustomerWarehouseId());
		output.setOutCustomerDistrictId(input.getInCustomerDistrictId());

		try {
			if (input.isInByLastName()) {
				customer = getCustomerByLastName(input.getInCustomerLastName(), input.getInCustomerDistrictId(), input.getInCustomerWarehouseId());
			} else {
				customer = getCustomerInstance(input.getInCustomerId(), input.getInCustomerDistrictId(), input.getInCustomerWarehouseId());
			}
		}catch (Throwable e){			
			System.out.println("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Servlet Path: Error in getCustomerByLastName() or getCustomerInstance(): " + e.getMessage());
			e.printStackTrace();
			throw new ServletException("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Servlet Path: Error in getCustomerByLastName() or getCustomerInstance() ", e);
		}
		irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Servlet Path: CustomerId: " + customer.getCustomerId());

		if (customer != null) {
			output.setOutCustomerId(customer.getCustomerId());
			output.setOutCustomerFirstName(customer.getCustomerFirst());
			output.setOutCustomerMiddleName(customer.getCustomerMiddle());
			output.setOutCustomerLastName(customer.getCustomerLast());
			output.setOutCustomerBalance((customer.getCustomerBalance()).doubleValue());

			try {
				order = this.getOrderInstance(output.getOutCustomerId(), output.getOutCustomerWarehouseId(), output.getOutCustomerDistrictId());
				output.setOutOrderId(order.getOrderId());
			}catch (Throwable e){			
				System.out.println("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Servlet Path: Error in getOrder(): " + e.getMessage());
				e.printStackTrace();
				throw new ServletException("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Servlet Path: Error in getOrder() ", e);
			}			
			
			try {
				requestOrderTrackingPayload.setWarehouseId(output.getOutCustomerWarehouseId());
				requestOrderTrackingPayload.setDistrictId(output.getOutCustomerDistrictId());
				requestOrderTrackingPayload.setCustomerId(output.getOutCustomerId());
				requestOrderTrackingPayload.setOrderId(output.getOutOrderId());
				requestOrderTrackingPayload.setEntityManager(em);
			} catch (ConstraintViolationException e){
				for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
					irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Servlet Path: ConstraintViolationException: violationMessage(s): Property Path: " + violation.getPropertyPath().toString() + " Invalid value = " + violation.getInvalidValue() + " Violation Message = " + violation.getMessage());
			    }
				throw new ConstraintViolationException("<<< ThreadId: " + Thread.currentThread().getId() + ": "  + className + ": Servlet Path: ERROR: " + e.getConstraintViolations() , e.getConstraintViolations());
			} catch (Throwable e) {
				System.out.println("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Servlet Path: ERROR when executing requestOrderTrackingServlet() setter methods: " + e.getMessage());
				e.printStackTrace();
				throw new ServletException("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Servlet Path: ERROR when executing requestOrderTrackingServlet() setter methods ", e);
			}
			
			try{
				irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Servlet Path: CDI Event 1: Before (new RequestOrderTrackingEventTransactionScopedImpl(requestOrderTrackingPayload))): object identity = " + this);
				requestOrderTrackingEvent.fire(new RequestOrderTrackingEventTransactionScopedImpl(requestOrderTrackingPayload));
			}catch (Throwable e){			
				System.out.println("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Servlet Path: Error in requestOrderTrackingEvent.fire: " + e.getMessage());
				e.printStackTrace();
				throw new ServletException("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Servlet Path: Error in requestOrderTrackingEvent.fire ", e);
			}		
		} 
		/* Comment out dead code
		else {
			output.setOutErrorMsg("Customer " + input.getInCustomerLastName() + " not found");
		}
		*/
		irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Servlet Path: Exit requestOrderTrackingServlet(input, request)");

		return output;
	}
	
	@ApplicationTraceInterceptor
	public CustomerJPA getCustomerByLastName(String lastName, short districtId,
			short warehouseId) throws ServletException {

		int noCustomers = 0;
		int noCustDiv2 = 0;
		int remainder = 0;

		java.util.Vector<CustomerJPA> v = new java.util.Vector<CustomerJPA>();

		CustomerJPA customer = null;

		irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": getCustomerByLastName: Searching Customer with LastName " + lastName);

		try {
			Query q = em.createNamedQuery("findCustomerByLastName");
			q.setParameter("lastName", lastName);
			q.setParameter("districtId", districtId);
			q.setParameter("warehouseId", warehouseId);
			Collection<?> coll = null;
			coll = q.getResultList();
			Iterator<?> it = coll.iterator();

			while (it.hasNext()) {
				noCustomers++;
				customer = (CustomerJPA) it.next();
				v.addElement(customer);
			}
		} catch (Throwable e) {
			e.printStackTrace();
			throw new ServletException("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Error on query in getCustomerByLastName(....), lastName = "
							+ lastName + ", district = " + districtId
							+ " and warehouse = " + warehouseId   
							+ "; exception message: " + e.getMessage()
							+ "; exception cause: " + e.getCause());
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
				getCustomerInstance(customer.getCustomerId(), customer.getCustomerDId(),
						customer.getCustomerWId());
			}
		} catch (Throwable e) {
			System.out.println("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": ERROR: getting customerInstance in PaymentSession failed in getCustomerByLastName method: " + e.getMessage());
			e.printStackTrace();
			throw new ServletException("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": ERROR: getting customerInstance in PaymentSession failed in getCustomerByLastName method  "
						+ "; exception message: " + e.getMessage()
						+ "; exception cause: " + e.getCause());
		} // end try
		return customer;
	}

	@ApplicationTraceInterceptor
	protected CustomerJPA getCustomerInstance(short customerId, short districtId,
			short warehouseId) throws UserException, ServletException {

		CustomerJPA customer = null;
		
		try {
			CustomerKey key = new CustomerKey();
			key.setCustomerDId(districtId);
			key.setCustomerId(customerId);
			key.setCustomerWId(warehouseId);

			customer = em.find(CustomerJPA.class, key);
			if (customer == null)
				throw new ServletException("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": customer not found: district = "
						+ districtId + ", customer = " + customerId
						+ " and warehouse = " + warehouseId);
		} catch (Throwable e) {
			e.printStackTrace();
			throw new ServletException("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": ERROR: failure in getCustomerInstance "
					+ "; exception message: " + e.getMessage()
					+ "; exception cause: " + e.getCause(), e);
		} // end try
		return customer;
	}

	@ApplicationTraceInterceptor
	private OrderJPA getOrderInstance(short inCustomerId, short inCustomerWarehouseId, short inCustomerDistrictId) throws UserException, ServletException {

		irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Servlet Path: Entered getOrderInstance()");
		OrderJPA order = null;
		
		int orderId = 0;

		try {
			Query q = em.createNamedQuery("findMaxOrderId");
			q.setParameter("customerId", inCustomerId);
			q.setParameter("districtId", inCustomerDistrictId);
			q.setParameter("warehouseId", inCustomerWarehouseId);

			order = (OrderJPA) q.getSingleResult();
			if (order == null)
				throw new RuntimeException("order not found " + irwwbase.getSTC());

			if (order != null) {
				orderId = order.getOrderId();
				irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Servlet Path: OrderId: " + order.getOrderId());
			}
		} catch (Throwable e) {
			e.printStackTrace();
			System.out.println("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Servlet Path: ERROR: orderid: " + orderId + "customerid: "
					+ inCustomerId + "districtid: " + inCustomerDistrictId
					+ "warehouseid: " + inCustomerWarehouseId + ": " + e.getMessage());
			throw new ServletException("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Servlet Path: ERROR: failure in getOrder: orderid: " + orderId
					+ "; customerid: " + inCustomerId + "; districtid: " + inCustomerDistrictId + ""
					+ "; warehouseid: " + inCustomerWarehouseId + "; " + " exception message: "
					+ e.getMessage() + "; exception cause: " + e.getCause(), e);
		}
		return order;
	}

	@ApplicationTraceInterceptor
	public void getOrderTrackingServlet(@Observes(during=BEFORE_COMPLETION) @OrderTrackingTransactionScopedQualifier OrderTrackingEvent orderTrackingEvent, HttpServletRequest request, EventMetadata eventMetadata) throws ServletException {

		irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Servlet Path: CDI Event 4: In " + className + ": getOrderTracking(@Observes(during=BEFORE_COMPLETION) @OrderTrackingTransactionScopedQualifier OrderTrackingEvent, HttpServletRequest request, EventMetadata eventMetadata): object identity = " + this);	 
		OrderTrackingSingleInstance [] orderTrackingInstances = null;
		OrderOutput output = new OrderOutput();
		
		Type eventType = eventMetadata.getType();
		Set<Annotation> qualifiers = eventMetadata.getQualifiers();
		
		irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Servlet Path: eventType = " + eventType);
		irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Servlet Path: event qualifiers = " +  qualifiers);

	    if ((orderTrackingEvent!= null) && (request.getParameter("inputPath").contains("Servlet"))){
	    	 try{	
	 			 output = (OrderOutput)request.getSession().getAttribute("output");	
	 			 output.setOutOrderTrackingHopsCount(orderTrackingEvent.getOrderTrackingPayload().getOrderTrackingHopsCount());
	    		 orderTrackingInstances = orderTrackingEvent.getOrderTrackingPayload().getOrderTrackingInstances();	
	 			 output.setOrderTrackingInstances(orderTrackingInstances);	 			 
	 			 request.getSession().setAttribute("output",output);

	    		 irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Servlet Path: getOrderTracking observer method where orderTrackingNumber = " + orderTrackingInstances[0].getOrderTrackingNumber());
		    		 
	    	 } catch (Throwable e) {
	    		System.out.println("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Servlet Path: Error in getOrderTrackingServlet(@Observes(during=BEFORE_COMPLETION) @OrderTrackingTransactionScopedQualifier OrderTrackingEvent, HttpServletRequest request, EventMetadata eventMetadata): " + e.getMessage());
	 			e.printStackTrace();
				throw new ServletException("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Servlet Path: Error in " +className +  ": getOrderTracking: Exception calling orderTrackingEvent.getOrderTrackingPayload().getOrderTrackingNumber(): " + 
						" Error Message: " + e.getMessage()	+ ": Exception Cause: " + e.getCause(), e);
			} 
	    }else if (orderTrackingEvent == null) {
	    	 System.out.println("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": Servlet Path: Error in getOrderTrackingServlet(@Observes(during=BEFORE_COMPLETION) @OrderTrackingTransactionScopedQualifier OrderTrackingEvent, HttpServletRequest request, EventMetadata eventMetadata) ");
	    	 throw new ServletException("<<< Error in " + className +  ": Servlet Path: getOrderTrackingServlet(): orderTrackingEvent is null");
	    }
	}
	
	@ApplicationTraceInterceptor
	public void dispatch(HttpServletRequest request, HttpServletResponse response, String nextPage) throws ServletException, IOException {
			RequestDispatcher dispatch = request.getRequestDispatcher(nextPage);
			dispatch.forward(request, response);
		}
}
