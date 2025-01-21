package orderstatus.concurrent.ejb.lite;


public class OrderStatusConcurrentInput implements java.io.Serializable {
	
	private final static long serialVersionUID = -702265289437737156L;
	
	private Integer numberOfClients = 0;
	private Integer clientStartupInterval = 0;
	private Integer minimumClientTransactionWaitTime = 0;
	private Integer maximumClientTransactionWaitTime = 0;
	private Integer testRunDuration = 0;
	
	
	/**
	 * constructor
	 */
	public OrderStatusConcurrentInput() {
		super();
	}


	public Integer getNumberOfClients() {
		return numberOfClients;
	}

	public void setNumberOfClients(Integer inNumberOfClients) {
		numberOfClients = inNumberOfClients;
	}

	public Integer getClientStartupInterval() {
		return clientStartupInterval;
	}

	public void setClientStartupInterval(Integer inClientStartupInterval) {
		clientStartupInterval = inClientStartupInterval;
	}

	public Integer getMinimumClientTransactionWaitTime() {
		return minimumClientTransactionWaitTime;
	}

	public void setMinimumClientTransactionWaitTime(Integer inMinimumClientTransactionWaitTime) {
		minimumClientTransactionWaitTime = inMinimumClientTransactionWaitTime;
	}
	
	public Integer getMaximumClientTransactionWaitTime() {
		return maximumClientTransactionWaitTime;
	}

	public void setMaximumClientTransactionWaitTime(Integer inMaximumClientTransactionWaitTime) {
		maximumClientTransactionWaitTime = inMaximumClientTransactionWaitTime;
	}
	
	public Integer getTestRunDuration() {
		return testRunDuration;
	}

	public void setTestRunDuration(Integer inTestRunDuration) {
		testRunDuration = inTestRunDuration;
	}

}
