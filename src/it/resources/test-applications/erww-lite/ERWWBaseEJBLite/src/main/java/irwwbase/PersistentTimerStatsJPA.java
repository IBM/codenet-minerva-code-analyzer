package irwwbase;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;

// shupert - Created JPA Entity to interface with database table where the
//           EJB 3.x persistent timer self-checking statistics are stored.

@Entity
@Table(name="ERWW_PERSISTENT_TIMER_STATS_TS")
@IdClass(PersistentTimerStatsPK.class)
public class PersistentTimerStatsJPA implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7624807731830116495L;

	@Id
	@Column(name="NAME")
	private  String name;
	
	@Id
	@Column(name = "BEAN_CREATION_TIMESTAMP")
	private long beanCreationTimestamp;
	
	@Column(name="TIMER_INTERVAL")
	private  long timerInterval;
	
	@Column(name="INITIAL_START_TIME")
	private  long initialStartTime;
	
	@Column(name="ACTUAL_TICK_COUNT")
	private  long actualTickCount;
	
	@Column(name="CALCULATED_TICK_COUNT")
	private  long calculatedTickCount;
		

	public PersistentTimerStatsJPA () {}
	
	public String getName() {
		return name;
	}
	
	public void setName(String inputName) {
		name = inputName;
	}
	
	public long getBeanCreationTimestamp() {
		return beanCreationTimestamp;
	}
	
	public void setBeanCreationTimestamp(long inputBeanCreationTimestamp) {
		beanCreationTimestamp = inputBeanCreationTimestamp;
	}
	
	public long getTimerInterval() {
		return timerInterval;
	}
	
	public void setTimerInterval(long inputTimerInterval) {
		timerInterval = inputTimerInterval;
	}
	
	public long getInitialStartTime() {
		return initialStartTime;
	}
	
	public void setInitialStartTime(long inputInitialStartTime) {
		initialStartTime = inputInitialStartTime;
	}
	
	public long getActualTickCount() {
		return actualTickCount;
	}
	
	public void setActualTickCount(long inputActualTickCount) {
		actualTickCount = inputActualTickCount;
	}
	
	public long getCalculatedTickCount() {
		return calculatedTickCount;
	}
	
	public void setCalculatedTickCount(long inputCalculatedTickCount) {
		calculatedTickCount = inputCalculatedTickCount;
	}

}
