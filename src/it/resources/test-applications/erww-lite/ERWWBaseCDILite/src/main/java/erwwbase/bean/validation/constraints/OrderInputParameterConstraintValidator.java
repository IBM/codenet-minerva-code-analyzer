package erwwbase.bean.validation.constraints;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import irwwbase.IRWWBase;

// Commenting out as a suggestion to remove HV000116 error
//@SupportedValidationTarget(value = { ValidationTarget.PARAMETERS })
public class OrderInputParameterConstraintValidator implements ConstraintValidator<OrderInputParameterConstraint, HttpServletRequest> {
	private String className = ( OrderInputParameterConstraintValidator.class).getName();
	
	@Override
	public void initialize(OrderInputParameterConstraint annotation) {		
	}

	@Override
	public boolean isValid(HttpServletRequest request, ConstraintValidatorContext context) {
		try{			
			Short.parseShort(request.getParameter("warehouseId"));
			Short.parseShort(request.getParameter("districtId"));
			Short.parseShort(request.getParameter("customerId"));			
		}
		catch (Exception e){
			IRWWBase irwwbase = new IRWWBase();
		    irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": either warehouseId, districtId and/or customerId is not a number.");
		    irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": warehouseId = " + request.getParameter("warehouseId"));
		    irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": districtId = " + request.getParameter("districtId"));
		    irwwbase.debugOut("<<< ThreadId: " + Thread.currentThread().getId() + ": In " + className + ": customerId = " + request.getParameter("customerId"));
			return false;
		}		
		return true;
	}

}
