package Super.Ivle.Boy;

import android.os.Parcel;
import android.os.Parcelable;


/** Storage class to store one instance of schedule record
 * 
 * @author Damien Tan, Wei Rong
 *
 */
public class Schedule implements Parcelable{
	
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
		
		if (endtime > 2400 || endtime < 0)
			throw new IllegalStateException("Illegal values for endtime");
		
		if (starttime > 2400 || starttime < 0)
			throw new IllegalStateException("Illegal values for starttime");
		
		if (frequency == null)
			throw new NullPointerException("Frequency of schedule cannot be null");
		
		if (day == null)
			throw new NullPointerException("Day of schedule cannot be null");
		
		if (typeOfSched == null)
			throw new NullPointerException("Type of schedule cannot be null");
		
		if (label == null)
			throw new NullPointerException("Label of schedule cannot be null");
	}
	
	//Constants for parcelling
	private static final int P_STARTTIME   = 0; 
	private static final int P_ENDTIME     = 1;
	private static final int P_DAY         = 2;
	private static final int P_FREQ        = 3;
	private static final int P_TYPEOFSCHED = 4;
	
	
	// Parcelling part
    public Schedule(Parcel in){
    	label = in.readString();
    	
    	int[] data = new int[5];
    	
    	in.readIntArray(data);
    	
    	starttime = data[P_STARTTIME];
    	endtime = data[P_ENDTIME];
    	day = DAY.values()[data[P_DAY]];
    	frequency = FREQ.values()[data[P_FREQ]];
    	typeOfSched = TYPE_SCHED.values()[data[P_TYPEOFSCHED]];
    	
    	size = in.readFloat();
    	
    	rowId = in.readLong();
    }

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(label);
		
		int[] data = new int[5];
		data[P_STARTTIME] = starttime;
		data[P_ENDTIME] = endtime;
		data[P_FREQ] = frequency.ordinal();
		data[P_DAY] = day.ordinal();
		data[P_TYPEOFSCHED] = typeOfSched.ordinal();
		
		dest.writeIntArray(data);
		
		dest.writeFloat(size);
		
		dest.writeLong(rowId);
	}

	public static final Parcelable.Creator<Schedule> CREATOR
	= new Parcelable.Creator<Schedule>() {
		public Schedule createFromParcel(Parcel in) {
			return new Schedule(in);
		}

		public Schedule[] newArray(int size) {
			return new Schedule[size];
		}
	};
	
}
