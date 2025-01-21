package stateful;

import stateful.StatefulOutput;

public interface AdditupLocal {
	
	public void setClientIdentifier(String string);
	public String addIt(int i);
	public StatefulOutput total();
	public String getStcNum();
	public void remove();

}
