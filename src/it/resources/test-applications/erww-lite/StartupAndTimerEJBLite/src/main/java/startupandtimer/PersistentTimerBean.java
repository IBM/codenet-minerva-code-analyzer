package startupandtimer;

import deliverysession.DeliverySessionClient;
import irwwbase.EJBPersistentTimerStatsBean;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import jakarta.annotation.Resource;
import jakarta.annotation.security.DeclareRoles;
import jakarta.annotation.security.RunAs;
import jakarta.ejb.Local;
import jakarta.ejb.Schedule;
import jakarta.ejb.ScheduleExpression;
import jakarta.ejb.SessionContext;
import jakarta.ejb.Stateless;
import jakarta.ejb.Timer;
import jakarta.ejb.Timeout;
import jakarta.ejb.TimerConfig;
import jakarta.ejb.TimerService;
import jakarta.ejb.EJB;

@Stateless(name="PersistentTimerBean")
@Local(PersistentTimerBeanLocal.class)
@DeclareRoles("RunAsCompanyRole")
@RunAs("RunAsCompanyRole")
public class PersistentTimerBean 
{
	@Resource private SessionContext ctx;
	
	@EJB
	DeliverySessionClient deliveryClient;
	
	@EJB
	EJBPersistentTimerStatsBean theEJBPersistentTimerStatsBean;
	

	// shupert - updated schedule so that this persistent timer does not conflict with
	//           either the other two persistent timers in this bean, or the two EJB
	//           non-persistent timers that are also running in ERWW.
	//
	@Schedule(minute="5, 25, 45", hour="*", persistent=true, info="AutomaticDeliverySessionTimerAnnotation")
	public void delivery1(Timer timer)
	{
		
		debugOut(" ERWW - Executing .delivery1() for EJB persistent timer (fires at 5, 25, 45 min, every hour)");
		
		try
		{	
		
			// Set the input, and do a delivery transaction.		
			deliveryClient.setInput(true, true);
			deliveryClient.doWork();
			
			// Now, tell the statistics bean that this timer just fired.  If we get here the delivery transaction
			// ran ok.   Otherwise, if an exception occurred, we will skip over this line of code, which is good because
			// we don't want to count the timer method as running yet.  The EJB container will be soon retrying the timer
			// method if an exception occurred.
			theEJBPersistentTimerStatsBean.timer1Fired();
		}
		catch(Throwable t)
		{
			System.out.println(" ERWW - Something went wrong in the .delivery1() persistent timer method, exception is: " + t);
			
			// If this is the first exception that we caught (ie. FFDC), save it to print later if client runs the WebEJBChecker
			if ( !theEJBPersistentTimerStatsBean.getTimer1FirstExceptionCaught() ){
			   theEJBPersistentTimerStatsBean.setTimer1FirstExceptionCaught(true);
			   theEJBPersistentTimerStatsBean.setTimer1ExceptionData(t.getMessage());
			   theEJBPersistentTimerStatsBean.setTimer1InfoMessage("Unexpected exception occurred while Timer1 was executing the Delivery transaction");
			}  
						
			t.printStackTrace();
		}
	}
	
	// shupert - updated schedule so that this persistent timer does not conflict with
	//           either the other two persistent timers in this bean, or the two EJB
	//           non-persistent timers that are also running in ERWW.
	//
	// This timer is configured via xml.  It is defined as being an automatic persistent
	// timer method that runs at hour = "*" minute = "10, 30, 50".
	public void delivery2(Timer timer)
	{
		
		debugOut(" ERWW - Executing .delivery2() for EJB persistent timer (fires at 10, 30, 50 min, every hour)");
	
		try
		{
	
	        // Setup input and do a Delivery tranaction.		
			deliveryClient.setInput(false, true);
			deliveryClient.doWork();
			
			// Now, tell the statistics bean that this timer just fired.  If we get here the delivery transaction
			// ran ok.   Otherwise, if an exception occurred, we will skip over this line of code, which is good because
			// we don't want to count the timer method as running yet.  The EJB container will be soon retrying the timer
			// method if an exception occurred.
			theEJBPersistentTimerStatsBean.timer2Fired();
			
		}
		catch(Throwable t)
		{
			System.out.println(" ERWW - Something went wrong in the .delivery2() persistent timer method, exception is: " + t);
			
			// If this is the first exception that we caught (ie. FFDC), save it to print later if client runs the WebEJBChecker
			if ( !theEJBPersistentTimerStatsBean.getTimer2FirstExceptionCaught() ){
			   theEJBPersistentTimerStatsBean.setTimer2FirstExceptionCaught(true);
			   theEJBPersistentTimerStatsBean.setTimer2ExceptionData(t.getMessage());
			   theEJBPersistentTimerStatsBean.setTimer2InfoMessage("Unexpected exception occurred while Timer2 was executing the Delivery transaction");
			}  
			
			t.printStackTrace();
		}
	}	

	// shupert - updated schedule so that this persistent timer does not conflict with
	//           either the other two persistent timers in this bean, or the two EJB 3.x
	//           non-persistent timers that are also running in ERWW.	
	//
	// This is the timeout method for a programmatic persistent timer (created in initTimer method).
	// It runs at hour = "*", and minute = "15, 35, 55".
	@Timeout
	public void delivery3(Timer timer)
	{
	
		debugOut(" ERWW - Executing .delivery3() for EJB persistent timer (fires at 15, 35, 55 min, every hour)" );
		
		try
		{
			
			// Setup input and do a Delivery transaction.
			deliveryClient.setInput(true, false);
			deliveryClient.doWork();
			
			// Now, tell the statistics bean that this timer just fired.  If we get here the delivery transaction
			// ran ok.   Otherwise, if an exception occurred, we will skip over this line of code, which is good because
			// we don't want to count the timer method as running yet.  The EJB container will be soon retrying the timer
			// method if an exception occurred.
			theEJBPersistentTimerStatsBean.timer3Fired();
			
		}
		catch(Throwable t)
		{
			System.out.println(" ERWW - Something went wrong in the .delivery3() persistent timer method, exception is: " + t);
			
			// If this is the first exception that we caught (ie. FFDC), save it to print later if client runs the WebEJBChecker
			if ( !theEJBPersistentTimerStatsBean.getTimer3FirstExceptionCaught() ){
			   theEJBPersistentTimerStatsBean.setTimer3FirstExceptionCaught(true);
			   theEJBPersistentTimerStatsBean.setTimer3ExceptionData(t.getMessage());
			   theEJBPersistentTimerStatsBean.setTimer3InfoMessage("Unexpected exception occurred while Timer3 was executing the Delivery transaction");
			}  
			
			t.printStackTrace();
		}
	}	
	
	
	public void initTimer() 
	{
		TimerService ts = ctx.getTimerService();
		
		debugOut("Executing PersistentTimerBean.initTimer()...");
				
		// shupert - first make sure that this programmatic, persistent timer has
		//           not already been created in the timer DB.
		Collection<?> preExistingTimers = ts.getTimers();
		boolean existingTimerMatchFound = false;
		if (preExistingTimers.size() > 0) {
			Iterator<?> existingTimerIterator = preExistingTimers.iterator();
			Timer existingTimer = (Timer)existingTimerIterator.next();
			while (existingTimer != null  && !existingTimerMatchFound) {
				ScheduleExpression existingTimerSchedule = existingTimer.getSchedule();
				String hour = existingTimerSchedule.getHour();
				String minute = existingTimerSchedule.getMinute();
				if (minute.equals("15, 35, 55") && hour.equals("*")){
					
					debugOut("initTimer found a pre-existing timer.  Not creating another one.");					
					existingTimerMatchFound = true;
					
				} else {
					
					try {
						
					   debugOut("initTimer about to fetch next pre-existing timer.");		
					   existingTimer =  (Timer)existingTimerIterator.next();
					   
					} catch (NoSuchElementException e) {
					   existingTimer = null;	
					}
				}				
			}			
		}

		// shupert - if the persistent timer did not already exist, create it now		
		if (!existingTimerMatchFound){
			
			debugOut("initTimer, no pre-existing, matching, timer was found.  Creating a new timer now.");

			ScheduleExpression se = new ScheduleExpression();
			se.timezone("America/Chicago");
			se.hour("*");
			se.minute("15, 35, 55");
			TimerConfig tc = new TimerConfig();
			tc.setPersistent(true);
			tc.setInfo("ProgrammaticDeliverySessionTimer");
			Timer theTimer = ts.createCalendarTimer(se,tc);
			
			debugOut("EJB persistent, calendar-based, timer created with ScheduledExpression: "
					+ theTimer.getSchedule().toString() + " will first fire at : "
					+ theTimer.getNextTimeout());
		}
	}
	

	private void debugOut(String message) 
	{
		if (System.getProperty("DEBUG_OUT") != null)
		{
			if (System.getProperty("DEBUG_OUT").equals("ON"))
			{
				System.out.println(message);
			}
		}
	}
}
