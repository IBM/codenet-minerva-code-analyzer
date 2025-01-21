package customer.rest.apis;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.regex.Pattern;

import jakarta.inject.Inject;
import javax.naming.InitialContext;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.PersistenceUnit;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.SystemException;
import jakarta.transaction.UserTransaction;

import com.ibm.json.java.JSONObject;

import customerjpa.CustomerJPA;
import customerjpa.CustomerKey;

import irwwbase.IRWWBase;
import irwwbase.POJOQualifier;

/**
 * Servlet implementation class CustomerCreditServlet
 */
@WebServlet(name="CustomerCreditServlet", value = "/customer/*")
public class CustomerCreditServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String className = (CustomerCreditServlet.class).getName();
	
	@PersistenceUnit(unitName = "CustomerJPA")
	EntityManagerFactory entityManagerFactory;
	
    private Pattern oneCustomerPattern = Pattern.compile("/WebCustomerLite_REST_APIs/customer/.*");
	
	@Inject @POJOQualifier IRWWBase irwwbase;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CustomerCreditServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String uri = request.getRequestURI();
		
		irwwbase.debugOut("<<< " + className + ": doGet: uri = " + uri);
		irwwbase.debugOut("<<< " + className + ": doGet: oneCustomerPattern.matcher(uri) = " + oneCustomerPattern.matcher(uri));
		
		short warehouseId = 0;
		short districtId = 0;
		short customerId = 0;
		String[] parameters = null;	
       	
		EntityManager entityManager = null;
		CustomerJPA customer = null;
		
		if(oneCustomerPattern.matcher(uri).matches()){
		
			try {
				parameters = request.getQueryString().split("&");	
				irwwbase.debugOut("<<< " + className + ": request.getQueryString().split(&)[0] = " + request.getQueryString().split("&")[0]);	
				for (String string : parameters) {
					String[] parameterPair =string.split("=");         
					if (parameterPair[0].equals("warehouseId")){
						warehouseId = Short.parseShort(parameterPair[1]);
						irwwbase.debugOut("<<< warehouseId = " + warehouseId);
					}
					if (parameterPair[0].equals("districtId")){
						districtId = Short.parseShort(parameterPair[1]);
						irwwbase.debugOut("<<< districtId = " + districtId);
					}
					if (parameterPair[0].equals("customerId")){
						customerId = Short.parseShort(parameterPair[1]);
						irwwbase.debugOut("<<< customerId = " + customerId);
					}            
				}			
    
			} catch (Exception t) {
				t.printStackTrace();
				throw new ServletException("Error getting Customer Credit Input", t);
			} // end try
		
			try {	
				entityManager = entityManagerFactory.createEntityManager();
			
				InitialContext ctx = new InitialContext();
				UserTransaction userTran = (UserTransaction) ctx.lookup("java:comp/UserTransaction");

				CriteriaBuilder cb = entityManager.getCriteriaBuilder();
				CriteriaQuery<CustomerJPA> cq = cb.createQuery(CustomerJPA.class);
				Root<CustomerJPA> root = cq.from(CustomerJPA.class);

				Predicate[] predicates = new Predicate[4];
				predicates[0] = cb.like(root.get("customerId"), "1");
				predicates[1] = cb.like(root.get("customerDId"), "1");
				predicates[2] = cb.like(root.get("customerWId"), "1");
				predicates[3] = cb.like(root.get("customerTime"), "06:00");
				cq.select(root).where(predicates);

				//TypedQuery<CustomerJPA> q = entityManager.createQuery(cq);
				//List<CustomerJPA> resultlist = q.getResultList();

				//JPQL with LocalTime
				TypedQuery<CustomerJPA> query = entityManager.createQuery("select m from CustomerJPA m where m.customerId=1 and m.customerDId=1 and m.customerWId=1 and m.customerTime='06:00'", CustomerJPA.class);
				List<CustomerJPA> list = query.getResultList();
				irwwbase.debugOut("<<< " + className + ":TypedQuery output = " + list);

				userTran.begin();
				entityManager.joinTransaction();

				customer = this.getCustomerInstance(customerId, districtId, warehouseId, entityManager, userTran);
				irwwbase.debugOut("<<< " + className + ": Get Customer Time = " + customer.getCustomerTime());
				userTran.commit();
				entityManager.close();


			} catch (PersistenceException t) {
				t.printStackTrace();
				System.out.println(t.getCause().getMessage());
				String causeMsg = t.getCause().getMessage();
				if (causeMsg.contains("-204"))
					throw new ServletException("table does not exist please run populate DB function from web page: " + t.getCause().getMessage(), t.getCause());
				else
					throw new ServletException("persistence exception obtaining customer acct: " + t.getCause().getMessage(), t.getCause());
			} // end try

			catch (Exception t) {
				t.printStackTrace();
				throw new ServletException("error locating customer account", t);
			} // end try
			this.setCustomerResponse(customer, response);
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String uri = request.getRequestURI();
		irwwbase.debugOut("<<< " + className + ": doPost: uri = " + uri);
		irwwbase.debugOut("<<< " + className + ": doPost: oneCustomerPattern.matcher(uri) = " + oneCustomerPattern.matcher(uri));
		
		short warehouseId = 0;
		short districtId = 0;
		short customerId = 0;
		String customerCredit = null;
		BigDecimal customerCreditLimit = new BigDecimal(0);
		String[] parameters = null;
				
		JSONObject customerCreditInputJSONObject = new JSONObject();
       	
		EntityManager entityManager = null;
		CustomerJPA customer = null;
		
		if(oneCustomerPattern.matcher(uri).matches()){	
			try {
				parameters = request.getQueryString().split("&");	
				irwwbase.debugOut("<<< " + className + ": request.getQueryString().split(&)[0] = " + request.getQueryString().split("&")[0]);	
				for (String string : parameters) {
					String[] parameterPair =string.split("=");         
					if (parameterPair[0].equals("warehouseId")){
						warehouseId = Short.parseShort(parameterPair[1]);
						irwwbase.debugOut("<<< warehouseId = " + warehouseId);
					}
					if (parameterPair[0].equals("districtId")){
						districtId = Short.parseShort(parameterPair[1]);
						irwwbase.debugOut("<<< districtId = " + districtId);
					}
					if (parameterPair[0].equals("customerId")){
						customerId = Short.parseShort(parameterPair[1]);
						irwwbase.debugOut("<<< customerId = " + customerId);
					}            
				}			
				
				customerCreditInputJSONObject = JSONObject.parse(request.getReader());
				irwwbase.debugOut("<<< jsonCustomerCreditJSONObject = " + customerCreditInputJSONObject);
				customerCredit = customerCreditInputJSONObject.get("customerCredit").toString();
				customerCreditLimit = new BigDecimal(customerCreditInputJSONObject.get("customerCreditLimit").toString().replaceAll(",", ""));			
        
			} catch (Exception t) {
				t.printStackTrace();
				throw new ServletException("Error getting Customer Credit Input", t);
			} // end try	
		
			try {	
				entityManager = entityManagerFactory.createEntityManager();
				
				InitialContext ctx = new InitialContext();
				UserTransaction userTran = (UserTransaction) ctx.lookup("java:comp/UserTransaction");

				userTran.begin();
				entityManager.joinTransaction();

				customer = this.getCustomerInstance(customerId, districtId, warehouseId, entityManager, userTran);
				customer.setCustomerCredit(customerCredit);
				customer.setCustomerCreditLim(customerCreditLimit);
				userTran.commit();
				entityManager.close();

			} catch (PersistenceException t) {
				t.printStackTrace();
				System.out.println(t.getCause().getMessage());
				String causeMsg = t.getCause().getMessage();
				if (causeMsg.contains("-204"))
					throw new ServletException("table does not exist please run populate DB function from web page: " + t.getCause().getMessage(), t.getCause());
				else
					throw new ServletException("persistence exception obtaining customer acct: " + t.getCause().getMessage(), t.getCause());
			} // end try

			catch (Exception t) {
				t.printStackTrace();
				throw new ServletException("error locating customer account", t);
			} // end try
			
			this.setCustomerResponse(customer, response);
		}
	}
	
	private CustomerJPA getCustomerInstance(short customerId, short districtId,
			short warehouseId, EntityManager em, UserTransaction userTran)
			throws ServletException {
		CustomerJPA customer = null;

		try {

			CustomerKey key = new CustomerKey();
			key.setCustomerDId(districtId);
			key.setCustomerId(customerId);
			key.setCustomerWId(warehouseId);

			customer = em.find(CustomerJPA.class, key,
					LockModeType.PESSIMISTIC_WRITE);

			if (customer == null)
				throw new ServletException("customer not found: district="
						+ districtId + ", customer=" + customerId
						+ " and warehouse=" + warehouseId + ", " + irwwbase.getSTC()
						+ "no stack");

		} catch (Exception t) {
			t.printStackTrace();
			try {
				userTran.rollback();
			} catch (IllegalStateException e) {
				e.printStackTrace();
				throw new ServletException(
						"exception rolling back user tran finding customer row",
						e);
			} catch (SecurityException e) {
				e.printStackTrace();
				throw new ServletException(
						"exception rolling back user tran finding customer row",
						e);
			} catch (SystemException e) {
				e.printStackTrace();
				throw new ServletException(
						"exception rolling back user tran finding customer row",
						e);
			}

			if (irwwbase.is913(t))
				throw new ServletException("<&!@" + irwwbase.checkFor913(t)
						+ ", finding Customer JPA, " + t.getCause()
						+ irwwbase.getSTC() + irwwbase.getStackTrace(t));
			else
				throw new ServletException("<&!@"
						+ "failure in getCustomerInstance: "
						+ " exception message: " + t.getMessage()
						+ ", exception cause: " + t.getCause() + irwwbase.getSTC()
						+ irwwbase.getStackTrace(t));
		} // end try
		return customer;
	}
	private void setCustomerResponse(CustomerJPA customer,HttpServletResponse response) throws ServletException {
		
		JSONObject customerCreditOutputJsonObject = new JSONObject();
		try {
			if (customer != null) {
				response.setStatus(200);
				response.setContentType("application/json");
				customerCreditOutputJsonObject.put("customerId", String.valueOf(customer.getCustomerId()));
				customerCreditOutputJsonObject.put("customerDId", String.valueOf(customer.getCustomerDId()));
				customerCreditOutputJsonObject.put("customerWId", String.valueOf(customer.getCustomerWId()));
				customerCreditOutputJsonObject.put("customerFirst", String.valueOf(customer.getCustomerFirst()));
				customerCreditOutputJsonObject.put("customerMiddle", String.valueOf(customer.getCustomerMiddle()));
				customerCreditOutputJsonObject.put("customerLast", String.valueOf(customer.getCustomerLast()));
				customerCreditOutputJsonObject.put("customerCredit", String.valueOf(customer.getCustomerCredit()));
				customerCreditOutputJsonObject.put("customerCreditLim", String.valueOf(customer.getCustomerCreditLim()));
				customerCreditOutputJsonObject.put("customerTime", String.valueOf(customer.getCustomerTime()));
				if (customerCreditOutputJsonObject != null) {
					try {						
						String json = customerCreditOutputJsonObject.serialize();
						response.setContentLength(json.length());
						response.getWriter().write(json);							
					} catch (Throwable e) {
						System.out.println("<<< " + className + ": Exception in doPost method where exception = " + e);
						response.setStatus(404);
						response.setContentLength(0);
						response.setContentType("text/plain");
					}
				}
			} 
			
		} catch (Exception t) {
			t.printStackTrace();
			throw new ServletException("Error creating output object", t);
		}
		
	}

}
