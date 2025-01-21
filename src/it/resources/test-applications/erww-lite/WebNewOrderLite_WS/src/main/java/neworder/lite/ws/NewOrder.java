package neworder.lite.ws;

import jakarta.persistence.EntityManagerFactory;
import jakarta.transaction.SystemException;

public interface NewOrder {
	
	public NewOrderOutputInfo createNewOrder(NewOrderInputInfo input, NewOrderOutputInfo output, EntityManagerFactory emf) throws Exception,
	IllegalStateException, SecurityException, SystemException;

}
