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
public class DistrictValidatorDecorator implements ValidatorBeanInterface {
	
	@Inject @Delegate @DistrictQualifier ValidatorBeanInterface validator;
	
	@Inject @POJOQualifier IRWWBase irwwbase;
		
	@Override
	public void validate(FacesContext context, UIComponent c, Object val) throws ValidatorException {
		
		validator.validate(context, c, val);
		
		String classname = "DistrictValidatorDecorator";
		String value = (String) val;
		String id = c.getId();
		int maxDistrictId = MaxValues.districtId(irwwbase.dbSize());
			
		irwwbase.debugOut("<<< " + classname + ": getId = " + id); 
				
		//Ensure that inDistrictId is not null
		if (id.contains("inDistrictId") ){
			try { 
		        Integer.parseInt(value); 
		    } catch(NumberFormatException e) { 
		    	throw new ValidatorException(new FacesMessage("Invalid District Id (needs to be an integer from 1 - " +maxDistrictId+ "):  Please re-enter."));
		    }		
		}
	
		if (id.contains("inDistrictId") && (value.trim().length() > 0)){
			Integer districtId = Integer.valueOf(value);
				
			if (!(districtId >= 1 && districtId <= maxDistrictId)){
				throw new ValidatorException(new FacesMessage("Invalid District Id (needs to be an integer from 1 - " +maxDistrictId+ "):  Please re-enter."));
			}			
		}		
	}
}
