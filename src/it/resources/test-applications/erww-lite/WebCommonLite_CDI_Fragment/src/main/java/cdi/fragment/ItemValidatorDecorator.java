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
public class ItemValidatorDecorator implements ValidatorBeanInterface {
	
	@Inject @Delegate @ItemQualifier ValidatorBeanInterface validator;
	
	@Inject @POJOQualifier IRWWBase irwwbase;
	
	String classname = "ItemValidatorDecorator";
		
	@Override
	public void validate(FacesContext context, UIComponent c, Object val) throws ValidatorException {
		
		validator.validate(context, c, val);

		String value = (String) val;
		String id = c.getId();		
		int maxItemId = MaxValues.itemId(irwwbase.dbSize());

		irwwbase.debugOut("<<< " + classname + ": getId = " + id); 
		
		//Ensure that the 1st itemId is not null
		if (id.equals("itemId1")){
			try { 
		        Integer.parseInt(value); 
		    } catch(NumberFormatException e) { 
		    	throw new ValidatorException(new FacesMessage("Invalid Item Id (needs to be an integer from 1 - " + maxItemId+ "):  Please re-enter."));
		    }		
		}
			
		if (id.contains("itemId") && (value.trim().length() > 0)){
			Integer itemId = Integer.valueOf(value);
			
			if (!(itemId >= 1 && itemId <= maxItemId)){
				throw new ValidatorException(new FacesMessage("Invalid Item Id (needs to be an integer from 1 - " + maxItemId+ "):  Please re-enter."));
			}			
		}
	}
}
