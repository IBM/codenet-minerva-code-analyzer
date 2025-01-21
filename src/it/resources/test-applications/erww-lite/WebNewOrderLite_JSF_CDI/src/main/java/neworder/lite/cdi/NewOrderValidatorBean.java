package neworder.lite.cdi;

import jakarta.inject.Named;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.*;

import cdi.fragment.CustomerQualifier;
import cdi.fragment.DistrictQualifier;
import cdi.fragment.ItemQualifier;
import cdi.fragment.ItemSupplyWarehouseQualifier;
import cdi.fragment.QuantityQualifier;
import cdi.fragment.ValidatorBeanInterface;
import cdi.fragment.WarehouseQualifier;
import irwwbase.IRWWBase;

@Named("newOrderValidatorBean")
@RequestScoped
@CustomerQualifier 
@DistrictQualifier 
@WarehouseQualifier
@ItemQualifier 
@QuantityQualifier 
@ItemSupplyWarehouseQualifier
public class NewOrderValidatorBean implements ValidatorBeanInterface {
		
	public void validate(FacesContext context, UIComponent c, Object val) throws ValidatorException {
		String value = (String) val;
		String id = c.getId();
		
		String classname = "NewOrderValidatorBean";
		
		IRWWBase irwwbase = new IRWWBase();
	
		irwwbase.debugOut("<<< " + classname + ": getId = " + id + " value = " + value); 		

		if (((value != null) && (value.trim().length() > 0))){
			//Check if the value is an integer		
		    try { 
		        Integer.parseInt(value); 
		    } catch(NumberFormatException e) { 
		    	throw new ValidatorException(new FacesMessage("Invalid " + id + " (needs to be an integer):  Please re-enter."));
		    }
		    
			if (!(Integer.parseInt(value) >= 1)){
				throw new ValidatorException(new FacesMessage("Invalid " + id + " (needs to be an integer greater than 0):  Please re-enter."));
			}
		}
	}
}
