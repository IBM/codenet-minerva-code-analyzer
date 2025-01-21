package cdi.fragment;

import jakarta.decorator.Decorator;
import jakarta.decorator.Delegate;
import jakarta.inject.Inject;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.*;

import irwwbase.IRWWBase;
import irwwbase.MaxValues;
import irwwbase.POJOQualifier;

@Decorator
public class CustomerValidatorDecorator implements ValidatorBeanInterface {
	
	@Inject @Delegate @CustomerQualifier ValidatorBeanInterface validator;
	
	@Inject @POJOQualifier IRWWBase irwwbase;
			
	@Override
	public void validate(FacesContext context, UIComponent c, Object val) throws ValidatorException {
		
		validator.validate(context, c, val);

		String classname = "CustomerValidatorDecorator";
		String value = (String) val;
		String id = c.getId();
		int maxCustomerId = MaxValues.customerId(irwwbase.dbSize());

		irwwbase.debugOut("<<< " + classname + ": getId = " + id); 	
		
		//Ensure that inCustomerId is not null
		if (id.contains("inCustomerId") ){
			try { 
		        Integer.parseInt(value); 
		    } catch(NumberFormatException e) { 
		    	throw new ValidatorException(new FacesMessage("Invalid Customer Id (needs to be an integer from 1 - " + maxCustomerId + "):  Please re-enter."));
		    }		
		}
			
		if (id.contains("inCustomerId") && (value.trim().length() > 0)){
			Integer customerId = Integer.valueOf(value);
			
			if (!(customerId >= 1 && customerId <= maxCustomerId)){
				throw new ValidatorException(new FacesMessage("Invalid Customer Id (needs to be an integer from 1 - " + maxCustomerId + "):  Please re-enter."));
			}			
		}			
	}
}
