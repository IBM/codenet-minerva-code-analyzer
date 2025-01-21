package irwwbase;

/**
 * Insert the type's description here.
 * Creation date: (9/25/00 2:25:18 PM)
 * @author: Administrator
 */

/*
Description of the exception handling concept:

Whenever an exception will be thrown, it will be catched and a special handleXYZException method will
be called. There are several handleXYZException methods in the IRWWBase class. A handleXYZException method
prints the stack trace and other helpful information to the console and then creates a new instance
of the UserException class and throws it back to the caller.
So you will get information about the problem both on the server side and on the client side.


Examples:

method handleSQLException:

protected void handleSQLException(java.sql.SQLException s,String methodName, String className) throws UserException
{
	System.out.println("Exception detected in "+methodName+" method of class " +className );
	System.out.println("Error Code: " +s.getErrorCode());
	System.out.println("SQL State: " +s.getSQLState());
	System.out.println("********** S T A C K  T R A C E **************");
	s.printStackTrace();
	throw new UserException("Method " +methodName + " of class " +className +" failed with SqlState: " +s.getSQLState() +" and SqlRc: " +s.getErrorCode());
}

method handleException:

protected void handleException(Exception t,String methodName, String className) throws UserException
{
	System.out.println("Exception detected in "+methodName+" method of "+this.getClass() );
	t.printStackTrace();
	throw new UserException("Method " +methodName +" failed!!!");
}



How to use the concept:

try
{
	 doAnything();
}
catch( XYZException r )
{
	  handleXYZException(r, "method name", _className);
}


Important aspect you have to notice:

1. Each method using the try/catch mechanism must handle the irwwbase.UserException,
	i.e. method xyz (....) throws irwwbase.UserException)

2. Due to the fact that AccessBeans cannot work together with self defined exceptions, a special
	 handleCreateException method exists, which must be used in the ejbCreate methods. The handleCreateMethod
	 does not rethrow an UserException object.

3. For main methods there is a handleMainException, which does not rethrow an UserException object either.
	This is because main() is the starting method and should catch everything.

4. When you add the irwwbase.UserException to the throws clause within your session bean, you have to add
	the same to the corresponding interface, e.g.

- implemented  class:

public PriceQuoteOutput priceQuoteSession(PriceQuoteInputInfo input, PriceQuoteOutput output) 
	throws java.rmi.RemoteException, jakarta.ejb.FinderException, UserException

- corresponding remote interface class:

priceQuoteSessionPackage.PriceQuoteOutput priceQuoteSession(priceQuoteSessionPackage.PriceQuoteInputInfo input, priceQuoteSessionPackage.PriceQuoteOutput output) throws java.rmi.RemoteException, jakarta.ejb.FinderException, irwwbase.UserException;

5. Each class should have a private member variable
	private java.lang.String _className;

   This variable should be initialized by calling
   
	   _className = (CLASSNAME.class).getName();

	where CLASSNAME has to be replaced by the correct existing class name.
   		
*/

public class UserException extends java.lang.Exception {

	/**
	 * RAD generated UUID
	 */
	private static final long serialVersionUID = 3550595133196084173L;
	private java.lang.String _infostring = null;
	private java.lang.String ex;
/**
 * UserException constructor comment.
 */
public UserException() {
	super();
}
/**
 * UserException constructor comment.
 */
public UserException (String infostring)
{
	_infostring = infostring;
} // ctor
/**
 * Insert the method's description here.
 * Creation date: (11/29/2001 9:25:50 AM)
 * @param msg java.lang.String
 * @param ex java.lang.Exception
 */
public UserException(String msg,String t) {
	super(msg);
	this.setEx(t);
	
	}

public UserException(String msg, Throwable cause, String infoString) {
	super(msg, cause);
	_infostring = infoString;
	setEx(cause.toString());
}
	/**
	 * @return
	 */
	public java.lang.String get_infostring() {
		return _infostring;
	}

	/**
	 * @return
	 */
	public java.lang.String getEx() {
		return ex;
	}

	/**
	 * @param string
	 */
	public void set_infostring(java.lang.String string) {
		_infostring = string;
	}

	/**
	 * @param Exception
	 */
	public void setEx(java.lang.String exception) {
		ex = exception;
	}
	
// MLS - we can't figure out why UserException would want this special printStackTrace method
//	     so we are taking it out for now to see if that causes any issues.
//	
//	public void printStackTrace(java.io.PrintWriter s) {  
//	  System.out.println("the exception is :"+ex);	
//	  }

}