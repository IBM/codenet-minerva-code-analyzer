//***********************************************************
// Functionality Tested:
//		EJB 3.x - Stateful Session bean holding a 
//                Singleton reference.  Ensuring that the
//                singleton reference survives passivation/activation.
//
//***********************************************************
package stocklevelsession;

import irwwbase.IRWWBase;
import irwwbase.StcnumBean;
import irwwbase.UserException;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import jakarta.annotation.Resource;
import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.PostActivate;
import jakarta.ejb.PrePassivate;
import jakarta.ejb.Stateful;
import jakarta.ejb.StatefulTimeout;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import javax.sql.DataSource;

import stockjpa.StockJPA;
import districtjpa.DistrictJPA;
import districtjpa.DistrictKey;

/**
 * This is a Stateful Session Bean Class
 */
@Stateful
@StatefulTimeout(value=2, unit=TimeUnit.HOURS)
@Local(StockLevelSessionFacade.class)
@Resource(name="jdbc/ERWWDataSourceWithJaasAlias", type = DataSource.class, authenticationType = Resource.AuthenticationType.CONTAINER, shareable = true)
public class StockLevelSessionFacadeBean extends IRWWBase implements
StockLevelSessionFacade {
	
	String _className = (StockLevelSessionFacadeBean.class).getName();
	
	
	@PersistenceContext(unitName = "StockLevelSessionEJB")
	EntityManager em;
	
	private DistrictJPA district = null;

	

	final static long serialVersionUID = 3206093459760846163L;

	
	@EJB
	StcnumBean ivStcnum;
	
	
	protected void getDistrictInstance(short districtId, short warehouseId) throws UserException {

		try {
			DistrictKey key = new DistrictKey();
			key.setDistrictId(districtId);
			key.setDistrictWareId(warehouseId);

			//district = em.find(DistrictJPA.class, key,LockModeType.OPTIMISTIC);
			district = em.find(DistrictJPA.class, key);
			if (district==null)	throw new UserException("district not found, districtId="+districtId+" and warehouseId="+warehouseId+" "+ ivStcnum.getStcnum(),"no stack");


		} catch (Exception t) {
			t.printStackTrace();
			if (is913(t))
				throw new UserException("<&!@"+checkFor913(t)+", finding District JPA, "+ t.getCause()+ ivStcnum.getStcnum(), getStackTrace(t));
			else throw new UserException("<&!@"+"failure in getDistrictInstance: "
					+" exception message: "+t.getMessage()+", exception cause: "+ t.getCause()+ ivStcnum.getStcnum(), getStackTrace(t));
		} // end try

	}

		
	/**
	 * Insert the method's description here. Creation date: (21/08/00 12:45:47)
	 * 
	 * @return stockLevelSessionPackage.StockLevelOutput
	 * @param input
	 *            stockLevelSessionPackage.StockLevelInput
	 * @exception java.rmi.RemoteException
	 *                The exception description.
	 * @exception jakarta.ejb.FinderException
	 *                The exception description.
	 */
	public StockLevelOutput stockLevelSession(StockLevelInput input) throws UserException {
		short districtId = input.getDistrictId();
		short warehouseId = input.getWarehouseId();
		long threshold = input.getThreshold();
		int nextOrderId = 0;

		startOfTran("StockLevelSessionEJB");
		
	
		StockLevelOutput output = new StockLevelOutput();

		getDistrictInstance(districtId, warehouseId);
		nextOrderId = district.getDistrictNextOrderId();
			
		int foid = nextOrderId - 20;
		int noid = nextOrderId;
		// next order number (20) - since we use the between operator in the
		// query

		// alternative implementation that uses a finderHelper method
		// this implementation is more than three times slower than the
		// implementation that uses the sql query
		// within the EJB session bean

		output.setLowStock(findStockThreshold(threshold, warehouseId,
				warehouseId, districtId, foid, noid));
		output.setStcnum(ivStcnum.getStcnum());

		endOfTran("StockLevelSessionEJB");

		// debugOut("IRWW - output.lowstock = " + output.lowStock );

		return output;
	}

	public int findStockThreshold(long threshold, short stockWarehouseId,
			short olWarehouseId, short districtId, int firstOrderId,
			int nextOrderId) throws UserException {

		
		int lowStock = 0;
		
		//Comment out unused variable
		//java.util.Vector<StockJPA> v = new java.util.Vector<StockJPA>();
		
		debugOut("%%%%% In findStockThreshold - threshold = "
				+ threshold);
		debugOut("%%%%% In findStockThreshold - stockWarehouseId = "
				+ stockWarehouseId);
		debugOut("%%%%% In findStockThreshold olWarehouseId = "
				+ olWarehouseId);
		debugOut("%%%%% In findStockThreshold - CMP path - districtId = "
				+ districtId);
		debugOut("%%%%% In findStockThreshold - CMP path - firstOrderId = "
				+ firstOrderId);
		debugOut("%%%%% In findStockThreshold - CMP path - nextOrderId = "
				+ nextOrderId);

	

	
			try {				
					Query q = em.createNamedQuery("findStockThreshold");
					q.setParameter("warehouseId", olWarehouseId);
					q.setParameter("districtId", districtId);
					q.setParameter("lowOrderId", firstOrderId);
					q.setParameter("highOrderId", nextOrderId);
					q.setParameter("stockWareHouseId", stockWarehouseId);
					q.setParameter("stockQuantity", java.math.BigDecimal.valueOf(threshold));
					Collection<?> coll=null;
					//q.setLockMode(LockModeType.OPTIMISTIC);
					coll = q.getResultList();
					
					
					//changes by surya
					Iterator<?> it = coll.iterator();
					StockJPA sja;
					while (it.hasNext()) {
						
						sja = (StockJPA) it.next();
						debugOut("StockItemID: "+ sja.getS_I_ID()+" StockWareID: "+sja.getS_W_ID()+" StockQuantity: "+sja.getS_QUANTITY());
					}
					//end changes
					
					if (coll!=null) lowStock=coll.size();
					//Comment out dead code
					//else;				
				} catch (Exception e) {
					e.printStackTrace();
					if (is913(e))
						throw new UserException("<&!@"+checkFor913(e)+", finding low stock, "+ e.getCause()+ ivStcnum.getStcnum(), getStackTrace(e));					
					else throw new UserException("<&!@"+"Error doing complex query to determine lowStock in StockLevelSessionFacadeBean, olWid="+olWarehouseId+", did="+districtId+", foid="+firstOrderId+", nextOid="+nextOrderId+", stkWid="+stockWarehouseId+" and threshold="+threshold+", "+" exception message: "+e.getMessage()+", exception cause: " + e.getCause() + ivStcnum.getStcnum(),getStackTrace(e));
				}
				return lowStock;
			
	}

	@PrePassivate
	public void PrePassivate() {
		debugOut("%%%%% Passivating StockLevel:  Stcnum = "
				+ ivStcnum.getStcnum());
	}
	@PostActivate
	public void PostActivate() {
		debugOut("%%%%% Activating StockLevel:  Stcnum = "
				+ ivStcnum.getStcnum());
	}

} /* @lineinfo:generated-code */

