package Super.Ivle.Boy;

import Super.Ivle.Boy.ScheduleDbAdapter.DAY;

/** Storage class to store one instance of schedule record
 * 
 * @author Damien Tan
 *
 */

public class Schedule {
	
	public enum FREQ{
		EVERY_WEEK,
		ODD_WEEK,
		EVEN_WEEK,
		ONCE
	}
	
	public enum TYPE{
		LECTURE,
		TUTORIAL,
		LAB,
		SEMINAR,
		OTHER
	}

	public String label;
	public int starttime;
	public int endtime;
	public DAY day;
	public FREQ freq;
	public long rowId;
	public float size;
	public TYPE type;
	
	/**Constructor
	 * 
	 */
	Schedule()
	{
		rowId = -1;
		starttime = -1;
		endtime = -1;
	}
	
	
	/**Resets all values in Schedule
	 * 
	 */
	void clear()
	{
		label = null;
		starttime = -1;
		endtime = -1;
		freq = null;
		day = null;
		rowId = -1;
		type = null;
	}
	
	
	/**
	 * Checks if all the variables in Schedule have correct values
	 * 
	 * @throws IllegalStateException If any value in schedule is not initialized correctly
	 */
	void validate() throws IllegalStateException
	{
		if (endtime < starttime)
			throw new IllegalStateException("End time cannot be earlier than start time.");
		
		if (freq == null)
			throw new IllegalStateException("Frequency of schedule cannot be null");
		
		if (day == null)
			throw new IllegalStateException("Day of schedule cannot be null");
		
		if (type == null)
			throw new IllegalStateException("Type of schedule cannot be null");

		if (endtime == -1 || starttime == -1 || rowId == -1)
			throw new IllegalStateException("Compulsory value in schedule not initialised");
	}
	
}
