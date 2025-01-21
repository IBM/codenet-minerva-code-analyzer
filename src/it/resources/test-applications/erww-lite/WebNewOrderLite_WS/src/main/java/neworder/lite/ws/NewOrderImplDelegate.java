package neworder.lite.ws;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import jakarta.transaction.SystemException;
import jakarta.jws.WebService;
import jakarta.jws.WebParam;


@WebService (targetNamespace="http://ws.lite.neworder/", serviceName="NewOrderImplService", portName="NewOrderImplPort", wsdlLocation="WEB-INF/wsdl/NewOrderImplService.wsdl")
public class NewOrderImplDelegate{

	@PersistenceUnit
	EntityManagerFactory emf;
	
    neworder.lite.ws.NewOrderImpl _newOrderImpl = null;

    public NewOrderOutputInfo createNewOrder (@WebParam(name="input") NewOrderInputInfo input, @WebParam(name="output") NewOrderOutputInfo output) throws Exception, IllegalStateException, SecurityException, SystemException {
        return _newOrderImpl.createNewOrder(input,output, emf);
    }

    public NewOrderImplDelegate() {
        _newOrderImpl = new neworder.lite.ws.NewOrderImpl(); }

}