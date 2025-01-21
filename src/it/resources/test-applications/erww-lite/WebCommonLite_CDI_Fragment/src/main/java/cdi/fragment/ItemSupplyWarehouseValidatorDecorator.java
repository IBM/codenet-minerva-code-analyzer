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
public class ItemSupplyWarehouseValidatorDecorator implements ValidatorBeanInterface {
	
	@Inject @Delegate @ItemSupplyWarehouseQualifier ValidatorBeanInterface validator;
	
	@Inject @POJOQualifier IRWWBase irwwbase;

	@Override
	public void validate(FacesContext context, UIComponent c, Object val) throws ValidatorException {
		
		validator.validate(context, c, val);

		String classname = "ItemSupplyWarehouseValidatorDecorator";
		String value = (String) val;
		String id = c.getId();
		int maxWarehouseId = MaxValues.warehouseId(irwwbase.dbSize());
	
		irwwbase.debugOut("<<< " + classname + ": getId = " + id); 

		//Ensure that the 1st warehouseId and the 1st itemSupplyWarehouseID are not null
		if (id.equals("itemSupplyWarehouseId1")){
			try { 
		        Integer.parseInt(value); 
		    } catch(NumberFormatException e) { 
		    	throw new ValidatorException(new FacesMessage("Invalid Item Supply Warehouse Id (needs to be an integer from 1 - " + maxWarehouseId + "):  Please re-enter."));
		    }		
		}
					
		if (id.contains("itemSupplyWarehouseId") && (value.trim().length() > 0)){
			Integer warehouseId = Integer.valueOf(value);
			
			if (!(warehouseId >= 1 && warehouseId <= maxWarehouseId)){
				throw new ValidatorException(new FacesMessage("Invalid Item Supply Warehouse Id (needs to be an integer from 1 - " + maxWarehouseId + "):  Please re-enter."));
			}			
		}	
	}	
}
