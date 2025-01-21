package pc.transaction.event;

//import static jakarta.enterprise.event.TransactionPhase.AFTER_COMPLETION;
import static jakarta.enterprise.event.TransactionPhase.AFTER_FAILURE;
import static jakarta.enterprise.event.TransactionPhase.AFTER_SUCCESS;
//import static jakarta.enterprise.event.TransactionPhase.BEFORE_COMPLETION;

import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;

public class TransactionalObserver {
	
	private String getPhaseMsg(TransactionPhase phase) {
		return (new StringBuffer()).append("PriceChange had a phase of: ").append(phase).append(" ").toString();
	}
	
	private void transactionAfterFailure(@Observes(during=AFTER_FAILURE) TransactionEvent event ) {
		event.addMessage(getPhaseMsg(AFTER_FAILURE));
	}
	
	private void transactionAfterSuccess(@Observes(during=AFTER_SUCCESS) TransactionEvent event ) {
		event.addMessage(getPhaseMsg(AFTER_SUCCESS));
	}

}
