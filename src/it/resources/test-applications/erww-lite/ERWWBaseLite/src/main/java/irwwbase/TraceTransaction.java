package irwwbase;
/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.transaction.TransactionSynchronizationRegistry;
import jakarta.transaction.UserTransaction;

import com.ibm.wsspi.uow.UOWManager;

/**
 * Helper for transaction context. The implementation should only use WebSphere
 * public APIs.
 */
public class TraceTransaction
{
    public static UOWManager getUOWManager()
    {
        try
        {
            return (UOWManager) new InitialContext().lookup("java:comp/websphere/UOWManager");
        } catch (NamingException ex)
        {
            throw new IllegalStateException(ex);
        }
    }

    public static TransactionSynchronizationRegistry getTransactionSynchronizationRegistry()
    {
        try
        {
            return (TransactionSynchronizationRegistry) new InitialContext().lookup("java:comp/TransactionSynchronizationRegistry");
        } catch (NamingException ex)
        {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Get the unit of work ID from the current thread. This value can only be
     * usefully compared with other values from the same JVM.
     * 
     * @throws IllegalStateException if the thread has no unit of work context
     */
    public static String getTransactionId()
    {
    	String transactionId = "None";
    	
    	try {
    		transactionId = Long.valueOf(getUOWManager().getLocalUOWId()).toString();
    	}catch (IllegalStateException e){
    		transactionId = "None";		
    	}  
        return transactionId;
    }

    /**
     * Return true if the current thread has an unspecified transaction context.
     * This is the same as {@link #isTransactionLocal} but better reflects the
     * intent of the test.
     */
    public static boolean isUnspecifiedTransactionContext()
    {
        return isTransactionLocal();
    }

    /**
     * Return true if the current thread has a local transaction context.
     */
    public static boolean isTransactionLocal()
    {
        return getTransactionKey() == null;
    }

    /**
     * Return true if the current thread has a global transaction context.
     */
    public static boolean isTransactionGlobal()
    {
        return getTransactionKey() != null;
    }

    /**
     * Returns the global transaction key, or null if no global transaction is
     * active. This value can only be usefully compared with other values from
     * the same JVM.
     */
    public static Object getTransactionKey()
    {
        return getTransactionSynchronizationRegistry().getTransactionKey();
    }

    public static UserTransaction lookupUserTransaction() throws NamingException
    {
        UserTransaction userTran = null;
        try {
            Context context = new InitialContext();
            userTran = (UserTransaction) context.lookup("java:comp/UserTransaction");
        } catch (NamingException ex) {
            throw new IllegalStateException(ex);
        }
        return userTran;
    }

}
