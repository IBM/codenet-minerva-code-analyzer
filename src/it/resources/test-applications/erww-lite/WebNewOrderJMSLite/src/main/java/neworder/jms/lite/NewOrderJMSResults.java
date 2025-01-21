package neworder.jms.lite;

/**
 *  A simple results object for passing results from the Servlet to the output JSP page 
 */
public class NewOrderJMSResults implements java.io.Serializable {
	
	private final static long serialVersionUID = 5556611544618328126L;
	
	private String message1 = null;
	private String message2 = null;
	private String message3 = null;
	private String status = null;
	private String exception = null;
	private String[] outputDetails = null;
/**
 * NewOrderSingleItemOutInfo constructor comment.
 */
public NewOrderJMSResults() {
	super();
}

public String getMessage1() {
	return message1;
}

public void setMessage1(String message1) {
	this.message1 = message1;
}

public String getMessage2() {
	return message2;
}

public void setMessage2(String message2) {
	this.message2 = message2;
}

public String getMessage3() {
	return message3;
}

public void setMessage3(String message3) {
	this.message3 = message3;
}

public String getStatus() {
	return status;
}

public void setStatus(String status) {
	this.status = status;
}


public String getException() {
	return exception;
}

public void setException(String exception) {
	this.exception = exception;
}

public String[] getOutputDetails() {
	return outputDetails;
}

public void setOutputDetails(String[] outputDetails) {
	this.outputDetails = outputDetails;
}

}