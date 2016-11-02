package Super.Ivle.Boy;


/** Storage class to store one instance of schedule record
 * 
 * @author Damien Tan, Wei Rong
 *
 */
public class Schedule {
	
	public String label;
	
	/**In 24hrs */
	public int starttime;

	/**In 24hrs */
	public int endtime;

	public DAY day;
	
	/** How often the schedule should repeat i.e. Odd/Even week */
	public FREQ frequency;
	
	/**Used for SQL database */
	public long rowId;
	
	/** The type of lesson i.e. Lecture, Tutorial, Others etc. */
	public TYPE_SCHED typeOfSched;

	/**Used in UI to store the size of buttons in schedule view*/
	public float size;
	
	
    @Override
	public boolean equals(Object o) {
    	super.equals(o);
    	
    	Schedule tmpSched = (Schedule) o;
    	
    	if (       !tmpSched.day.equals(this.day)
    			||  tmpSched.endtime != this.endtime
    			|| !tmpSched.frequency.equals(this.frequency)
    			|| !tmpSched.label.equals(this.label)
    			||  tmpSched.rowId != this.rowId
    			||  tmpSched.size  != this.size
    			||  tmpSched.starttime != this.starttime
    			|| !tmpSched.typeOfSched.equals(this.typeOfSched))
    			
    		return false;
    	
    	
		return true;
	}


	/** Which day the schedule should activate */
    public enum DAY
    {
    	MONDAY,
    	TUESDAY,
    	WEDNESDAY,
    	THURSDAY,
    	FRIDAY,
    	SATURDAY,
    	SUNDAY
    }
    
    /** How often a schedule should repeat*/
    public enum FREQ
    {
    	EVERY_WEEK,
    	ODD_WEEK,
    	EVEN_WEEK,
    	ONCE
    }
    
    /** The type of lesson i.e. Lecture, Tutorial, Others etc. */
	public enum TYPE_SCHED{
		LECTURE,
		TUTORIAL,
		LAB,
		SEMINAR,
		OTHER,
		EMPTY_BUTTON
	}
	
	
	/**Constructor
	 * 
	 */
	Schedule()
	{
		rowId = -1;
		starttime = -1;
		endtime = -1;
		size = -1;
	}
	
	/**Copy Constructor */
	Schedule(Schedule copy)
	{
		label = copy.label;
		starttime = copy.starttime;
		endtime = copy.endtime;
		day = copy.day;
		frequency = copy.frequency;
		rowId = copy.rowId;
		typeOfSched = copy.typeOfSched;
		size = copy.size;
	}
	
	/**Copy Constructor for ScheduleProtected */
	Schedule(ScheduleProtected copy)
	{
		label = copy.getLabel();
		starttime = copy.getStarttime();
		endtime = copy.getEndtime();
		day = copy.getDay();
		frequency = copy.getFrequency();
		rowId = copy.getRowId();
		typeOfSched = copy.getTypeOfSched();
		size = copy.getSize();
	}
	
	
	/**Resets all values in Schedule
	 * 
	 */
	void clear()
	{
		label = null;
		starttime = -1;
		endtime = -1;
		frequency = null;
		day = null;
		rowId = -1;
		size = -1;
		typeOfSched = null;
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
		
		if (frequency == null)
			throw new IllegalStateException("Frequency of schedule cannot be null");
		
		if (day == null)
			throw new IllegalStateException("Day of schedule cannot be null");
		
		if (typeOfSched == null)
			throw new IllegalStateException("Type of schedule cannot be null");
		
		if (label == null)
			throw new IllegalStateException("Label of schedule cannot be null");

		if (endtime == -1 || starttime == -1)
			throw new IllegalStateException("Compulsory value in schedule not initialised");
	}
	
}
