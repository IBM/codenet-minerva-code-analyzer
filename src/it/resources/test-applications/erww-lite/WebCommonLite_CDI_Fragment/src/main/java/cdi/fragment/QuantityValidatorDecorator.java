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
public class QuantityValidatorDecorator implements ValidatorBeanInterface {
	
	@Inject @Delegate @ItemQualifier ValidatorBeanInterface validator;
	
	@Inject @POJOQualifier IRWWBase irwwbase;
	
	String classname = "QuantityValidatorDecorator";
		
	@Override
	public void validate(FacesContext context, UIComponent c, Object val) throws ValidatorException {
		
		validator.validate(context, c, val);

		String value = (String) val;
		String id = c.getId();
		int maxItemQuantity = MaxValues.itemQuantity(irwwbase.dbSize());

		irwwbase.debugOut("<<< " + classname + ": getId = " + id); 
		
		//Ensure that the first quantity is not null
		if (id.equals("quantity1")){
			try { 
		        Integer.parseInt(value); 
		    } catch(NumberFormatException e) { 
		    	throw new ValidatorException(new FacesMessage("Invalid Quantity (needs to be an integer from 1 - " + maxItemQuantity + "):  Please re-enter."));
		    }		
		}
			
		if (id.contains("quantity") && (value.trim().length() > 0)){
			Integer quantity = Integer.valueOf(value);
			
			if (!(quantity >= 1 && quantity <= maxItemQuantity)){
				throw new ValidatorException(new FacesMessage("Invalid Quantity (needs to be an integer from 1 - " + maxItemQuantity + "):  Please re-enter."));
			}			
		}
	}
}
