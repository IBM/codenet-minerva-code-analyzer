package irwwbase;

import javax.xml.namespace.QName;

public class ERWWSecurityCheckException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5896991122037287452L;
	private String fault;
	
	public ERWWSecurityCheckException(String message, String fault){
		super(message+ " not found");
        this.fault = fault;
	}

	public ERWWSecurityCheckException(String message, String fault, Throwable t){
		super(message+ " not found",t);
	}
	
	public static QName FAULT_ELEMENT = 
		new QName("http://jdbc.sca.orderstatus","ERWWSecurityCheckException");
	
    public String getFaultInfo() {
    	return this.fault;
    }
}
