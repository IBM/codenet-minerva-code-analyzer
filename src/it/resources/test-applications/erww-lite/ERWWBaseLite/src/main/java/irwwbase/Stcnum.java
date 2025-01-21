package irwwbase;

//import com.ibm.websphere.runtime.ServerName;

/**
 * Insert the type's description here.
 * Creation date: (4/23/2002 11:12:48 AM)
 * @author: Administrator
 */
public class Stcnum {
	private static Stcnum instance = null;
	private static java.lang.String stcnum = null;
	/**
	 * Stcnum constructor comment.
	 */
	private Stcnum() {
		super();
		//if (System.getProperty("os.name").equals("OS/390")
		//	|| System.getProperty("os.name").equals("z/OS"))
		//	System.loadLibrary("Stcnum");
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (4/23/2002 11:31:21 AM)
	 */
	public static Stcnum getInstance() {
		if (instance == null)
			instance = new Stcnum();
		return instance; 
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (4/23/2002 11:56:43 AM)
	 * @return java.lang.String
	 */
	public java.lang.String getStcnum() {

		if (stcnum != null)
			return stcnum;

		if (System.getProperty("os.name").equals("OS/390")
			|| System.getProperty("os.name").equals("z/OS")) {
			updateStcnum();
		}
		//    else stcnum=" cannot print job id, non z/OS system"; 
		//    on non z/OS return hashcode of  jvm runtime. 
		else
			stcnum = Integer.valueOf(Runtime.getRuntime().hashCode()).toString();

		return stcnum;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (4/23/2002 11:31:21 AM)
	 */
	public native static String getSTCNUM(); // @P5A
	/**
	 * Insert the method's description here.
	 * Creation date: (4/23/2002 11:56:43 AM)
	 * @param newStcnum java.lang.String
	 */
	private synchronized static void setStcnum(java.lang.String newStcnum) {
		stcnum = newStcnum;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (4/23/2002 11:31:21 AM)
	 */
	private synchronized static void updateStcnum() {

		// in v5 STCNUM is returning EBCDIC string like in v4 however WAS now expects ascii 
		// so we need to do a conversion here specifying EBCDIC as incoming format so we can convert. 

		try {
		// MLS - when this code was ported to Liberty we found that the severName class
		// "com.ibm.websphere.runtime.ServerName" was not supported on Liberty yet.
		// So as a temporary workaround we are commenting out the following two lines of
		// code and hard-coding the server name instead. This needs to be undone once the
		// tWAS class for serverName, or an equivalent is supported on Liberty

		//setStcnum(new String(aByteArray, "IBM-1047"));
		//setStcnum(ServerName.getjsabjbnm().trim());

		setStcnum("TempServerNameUntilFixed");

		} catch (Exception ex) {
		ex.printStackTrace();

		}
	} 
}
