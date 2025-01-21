package deliverysession;

/**
 * Insert the type's description here. Creation date: (9/15/00 3:38:19 PM)
 * 
 * @author: Administrator
 */
public class DeliveryOutput implements java.io.Serializable {
	private final static long serialVersionUID = 5314763151140402894L;

	private java.lang.String stcnum;

	private int retc;
	private String status = new String("failed");
	
	private boolean deliveryResultsLeaked = false;   // For Bad App testing scenario
	
	SingleDelivery[] singleDeliveries = null;
	
	private DistrictDeliveryStatistics dds = new DistrictDeliveryStatistics();

	/**
	 * DeliveryOutput constructor comment.
	 */
	public DeliveryOutput() {
		super();
	}

	/**
	 * DeliveryOutput constructor comment.
	 */
	public DeliveryOutput(int rc, String s) {
		super();
		retc = rc;
		stcnum = s;
	}

	/**
	 * Insert the method's description here. Creation date: (4/29/2002 4:49:59
	 * PM)
	 * 
	 * @return int
	 */
	public int getRetc() {
		return retc;
	}

	public void setUpSingleDeliveries(int maxDistricts) {
		singleDeliveries = new SingleDelivery[maxDistricts];		
	}
	
	/**
	 * Insert the method's description here. Creation date: (4/29/2002 4:49:47
	 * PM)
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getStcnum() {
		return stcnum;
	}

	/**
	 * Insert the method's description here. Creation date: (4/29/2002 4:49:59
	 * PM)
	 * 
	 * @param newRetc
	 *            int
	 */
	public void setRetc(int newRetc) {
		retc = newRetc;
	}

	/**
	 * Insert the method's description here. Creation date: (4/29/2002 4:49:47
	 * PM)
	 * 
	 * @param newStcnum
	 *            java.lang.String
	 */
	public void setStcnum(java.lang.String newStcnum) {
		stcnum = newStcnum;
	}

	/**
	 * @return Returns the singleDeliveries.
	 */
	public SingleDelivery[] getSingleDeliveries() {
		return singleDeliveries;
	}

	/**
	 * @param singleDeliveries
	 *            The singleDeliveries to set.
	 */
	public void setSingleDeliveries(SingleDelivery[] singleDeliveries) {
		this.singleDeliveries = singleDeliveries;
	}
	
	/**
	 * @return  Flag which indicates if we purposely skipped getting the async delivery
	 *          results (ie. Bad App testing scenario)
	 */
	public boolean getDeliveryResultsLeaked() {
		return deliveryResultsLeaked;
	}

	/**
	 * @param resultsLeaked
	 *            Set flag to indicate if we purposely skipped getting the async delivery
	 *            results (ie. Bad App testing scenario)
	 *            	 */
	public void setDeliveryResultsLeaked(boolean resultsLeaked) {
		this.deliveryResultsLeaked = resultsLeaked;
	}

	
	public DistrictDeliveryStatistics getDistrictDeliveryStatistics() {
		return dds;
	}

	
	public void setDistrictDeliveryStatistics(DistrictDeliveryStatistics newDDS) {
		dds = newDDS;
	}
	
	public String getStatus() {
		return status;
	}

	
	public void setStatus(String newStatus) {
		status = newStatus;
	}
	
}