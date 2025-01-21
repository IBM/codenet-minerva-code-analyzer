package orderstatus.concurrent.ejb.lite;

/**
 * Insert the type's description here.
 * Creation date: (09/10/00 14:30:25)
 * @author: Administrator
 */
public class OrderStatusConcurrentOutput implements java.io.Serializable {
	
	private final static long serialVersionUID = 3717603783007674663L;
	
	private String status = "";
	private String message = "";
	private String exception = "";
	private String stack = "";
	
/**
 * OrderStatusOutput constructor .
 */
public OrderStatusConcurrentOutput() {
	super();
}


public String getStatus() {
	return status;
}

public void setStatus(String inStatus) {
	status = inStatus;
}

public String getMessage() {
	return message;
}

public void setMessage(String inMessage) {
	message = inMessage;
}

public String getException() {
	return exception;
}

public void setException(String inException) {
	exception = inException;
}

public String getStack() {
	return stack;
}

public void setStack(String inStack) {
	stack = inStack;
}

}
