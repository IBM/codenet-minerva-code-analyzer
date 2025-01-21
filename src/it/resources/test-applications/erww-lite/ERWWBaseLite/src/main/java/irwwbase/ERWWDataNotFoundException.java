package irwwbase;

import javax.xml.namespace.QName;

public class ERWWDataNotFoundException extends Exception {
	
	private static final long serialVersionUID = 5896991122037287452L;
	private String fault;
	
	public ERWWDataNotFoundException(String message, Throwable t){
		super(message+ " not found",t);
	}
	
	public ERWWDataNotFoundException(String message, String fault){
		super(message+ " not found");
        this.fault = fault;
	}
	
	public ERWWDataNotFoundException(String message, String fault, Throwable t){
		super(message+ " not found",t);
        this.fault = fault;
	}
	
	public static QName FAULT_ELEMENT = 
		new QName("http://jdbc.sca.orderstatus","ERWWDataNotFoundException");
	
    public String getFaultInfo() {
    	return this.fault;
    }
}
