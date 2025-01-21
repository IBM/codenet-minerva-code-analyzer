package produce.ejb;

import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ejb.Stateless;

@Stateless
public class PCEntityManagerProducer {
	
	@PersistenceContext(unitName="PriceChangeEJB") private EntityManager em;
	
	@Produces @PCEntityManagerQualifier
	public EntityManager getEntityManager() {
		return em;
	}
}