package cdi.fragment;

import jakarta.inject.Inject;
import jakarta.inject.Named;

import irwwbase.IRWWBase;
import irwwbase.POJOQualifier;

@MockAlternativeStereotype
@Named("applicationDesignChartBean")
public class MockApplicationDesignChartBean {
	
	@Inject @POJOQualifier IRWWBase irwwbase;
	
	String classname = "MockApplicationDesignChartBean";
	
	public String actionNewOrderCDI1(){
	  	irwwbase.debugOut("<<< " + classname + " actionNewOrderCDI1 method entered");
        return "success_MockApplicationDesignChart";
	} 
	
	public String actionNewOrderCDI2(){
	  	irwwbase.debugOut("<<< " + classname + " actionNewOrderCDI2 method entered");
        return "success_MockApplicationDesignChart";
	}
	
	public String actionNewOrderCDI3(){
	  	irwwbase.debugOut("<<< " + classname + " actionNewOrderCDI3 method entered");
        return "success_MockApplicationDesignChart";
	}

}
