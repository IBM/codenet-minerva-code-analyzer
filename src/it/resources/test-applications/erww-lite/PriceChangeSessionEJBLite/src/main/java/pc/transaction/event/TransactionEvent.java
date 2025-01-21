package pc.transaction.event;

public class TransactionEvent {
	
	private StringBuffer transactionSB;
	
	public TransactionEvent() {
		transactionSB = new StringBuffer();
	}
	
	public void addMessage(String msg) {
		transactionSB.append(msg);
	}
	
	public String getMessages() {
		return transactionSB.toString();
	}
}
