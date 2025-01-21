package orderstatus.concurrent.ejb.lite;

import irwwbase.IRWWBase;


public class ConcurrencyStatusOutput extends IRWWBase{
	
	private String stack = "";
	private String status = "";
	private String buttonPressed = null;
	private String message = "";
	private String exception = "";
	private String time = "0 days - 0 hours - 0 minutes - 0 seconds";
	private int numberOfClients = 0;
	private int attemptedTransactions = 0;
	private int passedTransactions = 0;
	private int expectedFailedTransactions = 0;
	private int unexpectedFailedTransactions = 0;
	private int cancelledTransactions = 0;
	private int hours = 0;
	private int minutes = 0;
	private int seconds = 0;
	private int days = 0;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void setNumberOfClients (int num){
		numberOfClients = num;
	}
	
	public void setAttemptedTrans(int num){
		attemptedTransactions = num;
	}
	
	public void setPassedTrans(int num){
		passedTransactions = num;
	}
	
	public void setExpectedFailedTrans(int num){
		expectedFailedTransactions = num;
	}
	
	public void setUnexpectedFailedTrans(int num){
		unexpectedFailedTransactions = num;
	}
	
	public void setCancelledTrans(int num){
		cancelledTransactions = num;
	}
	
	public void setStatusMsg(String msg){
		status = msg;
	}
	
	public void setButtonPressed(String btn){
		buttonPressed = btn;
	}

	public void setException(String excp){
		exception = excp;
	}

	public void setMessage(String msg){
		message = msg;
	}
	
	public void setStack(String msg){
		stack = msg;
	}

	public void setTime(int timeSecs) {
		if(timeSecs/60 < 1){
			days = 0;
			hours = 0;
			minutes = 0;
			seconds = timeSecs;
		}
		else if( timeSecs/3600 < 1){
			days = 0;
			hours = 0;
			minutes = timeSecs/60;
			seconds = timeSecs - minutes*60;
		}
		else if(timeSecs/86400 < 1){
			days = 0;
			hours = timeSecs/3600;
			minutes = (timeSecs - hours*3600)/60;
			seconds = timeSecs - minutes*60 - hours*3600;
		}
		else {
			days = timeSecs/86400;
			hours = (timeSecs - days*86400)/3600;
			minutes = (timeSecs - days*86400 - hours*3600)/60;
			seconds = timeSecs - days*86400 - minutes*60 - hours*3600;
		}
		
		time = days + " days - " + hours + " hours - " 
		+ minutes + " minutes - " + seconds + " seconds";		
	}
	
	public int getNumberOfClients() {
		return numberOfClients;
	}
	
	public int getAttemptedTrans(){ 
		return attemptedTransactions;
	}
	
	public int getPassedTrans(){
		return passedTransactions;
	}
	
	public int getExpectedFailedTrans(){
		return expectedFailedTransactions;
	}
	
	public int getUnexpectedFailedTrans(){
		return unexpectedFailedTransactions;
	}
	
	public int getCancelledTrans(){
		return cancelledTransactions;
	}
	
	public String getStatusMsg(){
		return status;
	}
	
	public String getButtonPressed(){
		return buttonPressed;
	}
	
	public String getException(){
		return exception;
	}
	
	public String getMessage(){
		return message;
	}
	
	public String getStack(){
		return stack;
	}

	public String getTime(){
		return time;
	}
}
