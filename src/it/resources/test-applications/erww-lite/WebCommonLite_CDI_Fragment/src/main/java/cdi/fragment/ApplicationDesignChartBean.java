package cdi.fragment;

import jakarta.annotation.ManagedBean;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import irwwbase.IRWWBase;
import irwwbase.POJOQualifier;

@ManagedBean
@RequestScoped
@Named("applicationDesignChartBean")
public class ApplicationDesignChartBean {
	
	@Inject @POJOQualifier IRWWBase irwwbase;
	
	String classname = "ApplicationDesignChartBean";
	private String imageId = null;

	public String getImageId() {
		irwwbase.debugOut("applicationDesignChartBean.getImageId() = " + imageId);
		return imageId;
	}
	public void setImageId(String imageId) {
		irwwbase.debugOut("applicationDesignChartBean.setImageId() = " + imageId);
		this.imageId = imageId;
	}

	public String actionNewOrderCDI1(){
	    this.setImageId("NewOrder_CDI_1"); 
        return "success_ApplicationDesignChart";
	}	
	
	public String actionNewOrderCDI2(){
	    this.setImageId("NewOrder_CDI_2"); 
        return "success_ApplicationDesignChart";
	}
	
	public String actionNewOrderCDI3(){
	    this.setImageId("NewOrder_CDI_3"); 
        return "success_ApplicationDesignChart";
	}
}
