package erwwbase.cdi.managed.beans;

import irwwbase.ExtendedRandom;
import irwwbase.IRWWBase;
import irwwbase.MaxValues;
import irwwbase.POJOQualifier;
import irwwbase.UserException;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import erwwbase.bean.validation.constraints.OrderInputParameterConstraint;
import erwwbase.cdi.input.output.pojos.OrderInput;
import erwwbase.cdi.interceptors.ApplicationTraceInterceptor;
import erwwbase.cdi.qualifiers.OrderTrackingQualifier;

/**
 * Insert the type's description here.
 * Creation date: (09/10/00 14:29:54)
 * @author: Administrator
 */
@RequestScoped
@OrderTrackingQualifier 
public class HandleOrderInputBean implements java.io.Serializable {
	private final static long serialVersionUID = -702265289437737154L;
	private final static short idPercentage = 60;
	private String className = (HandleOrderInputBean.class).getName();
	
	@Inject @POJOQualifier IRWWBase irwwbase;

/**
 * OrderInput constructor comment.
 */
public HandleOrderInputBean() {
	super();
}

@ApplicationTraceInterceptor
public OrderInput obtainManualInput(@OrderInputParameterConstraint HttpServletRequest request) throws ConstraintViolationException, UserException {
	irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": getManualInput(HttpServletRequest request)");

	Short warehouseId = 0;
	Short districtId = 0;
	String customerLastName = null;
	Short customerId = 0;
	
	OrderInput input = new OrderInput();
		
	try{
		warehouseId = Short.parseShort(request.getParameter("warehouseId"));
		districtId = Short.parseShort(request.getParameter("districtId"));
		customerLastName = request.getParameter("customerLastName");
		customerId = Short.parseShort(request.getParameter("customerId"));
	} catch (Throwable e) {
		System.out.println("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": ERROR when getting parameters for manual input: " + e.getMessage());
		e.printStackTrace();
		throw new UserException("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": ERROR when getting parameters for manual input: ", e.getMessage());
	}
		
	try{
		input.setInCustomerWarehouseId(warehouseId);	
		input.setInCustomerDistrictId(districtId);
		input.setInCustomerId(customerId);
		input.setInCustomerLastName(customerLastName);
   		
		if (customerLastName==null || customerLastName.equals(" ") || customerLastName.length()==0) input.setInByLastName(false);
		else input.setInByLastName(true);
   	
	} catch (Throwable e) {
		System.out.println("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": ERROR when setting input for manual path: " + e.getMessage());
		e.printStackTrace();
		throw new UserException("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": ERROR when setting input for manual path: ", e.getMessage());
	}

	return input;	
}

@ApplicationTraceInterceptor
public OrderInput generateAutoGenInput(java.lang.Byte db) throws UserException {

	irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": generateAutoGenInput(byte db)");
	
	ExtendedRandom rand = new ExtendedRandom();
	OrderInput input = new OrderInput();

	try{
		input.setInCustomerWarehouseId((short)(rand.nextInt(MaxValues.warehouseId(db))+1));
		input.setInCustomerDistrictId((short)(rand.nextInt(MaxValues.districtId(db))+1));
   
		if (rand.nextBoolProb(idPercentage, 100)) {
			input.setInByLastName(false);
			input.setInCustomerId((short)(rand.nextInt(MaxValues.customerId(db))+1));
			input.setInCustomerLastName(" ");
		}
		else {
			input.setInByLastName(true);
			input.setInCustomerLastName(rand.nextName(db));
			input.setInCustomerId((short)0);
		}
    
		} catch (Throwable e) {
			System.out.println("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": ERROR when setting input for autogen path: " + e.getMessage());
			e.printStackTrace();
			throw new UserException("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": ERROR when setting input for autogen path: ", e.getMessage());
		}
    
    return input;
}

}
