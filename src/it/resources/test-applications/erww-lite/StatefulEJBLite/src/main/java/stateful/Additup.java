package stateful;

import irwwbase.*;

import jakarta.ejb.Local;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Remove;
import jakarta.ejb.Stateful;

/**
 * Session Bean implementation class Additup
 */
@Stateful
@Local(AdditupLocal.class)
@LocalBean
public class Additup implements AdditupLocal {
	
	private String clientIdentifier=null;
	private int sum=0;
	private StatefulOutput output=null;
	private IRWWBase ib = new IRWWBase();

    /**
     * Default constructor. 
     */
    public Additup() {
    }
    
    public String addIt(int i){
		sum=i+sum;
		ib.debugOut("<Bean> Received value is: "+i+" current sum is: "+sum);		
		return clientIdentifier;		
	}
    
	public StatefulOutput total(){
		output = new StatefulOutput();
		output.setTotal(sum);
		ib.debugOut("<Bean> Final sum is: "+sum);
		output.setClientIdentifier(clientIdentifier);
		return output;
	}
	
	/**
	 * @return
	 */
	public String getClientIdentifier() {
		return clientIdentifier;
	}

	/**
	 * @param string
	 */
	public void setClientIdentifier(String string) {
		clientIdentifier = string;
	}

	/**
	 * @return Returns the stcNum.
	 */
	public String getStcNum() {
		return ib.getSTC();
	}
	
	@Remove
	public void remove() {
		
	}

}







	
	
	

	
	


