package Super.Ivle.Boy;


import Super.Ivle.Boy.Schedule.DAY;
import Super.Ivle.Boy.Schedule.FREQ;
import Super.Ivle.Boy.Schedule.TYPE_SCHED;
import android.os.Parcel;
import android.os.Parcelable;

/** Immutable form of Schedule object
 * 
 *  Basically, all properties are private in this object,
 *  and can only be accessed through getters.
 *  
 *  After creation, ScheduleProtected cannot be edited.
 * 
 * @author Damien Tan
 *
 */
public class ScheduleProtected implements Parcelable{

	private String label;
	
	/**In 24hrs */
	private int starttime;

	/**In 24hrs */
	private int endtime;

	private DAY day;
	
	/** How often the schedule should repeat i.e. Odd/Even week */
	private FREQ frequency;
	
	/**Used for SQL database */
	private long rowId;
	
	/** The type of lesson i.e. Lecture, Tutorial, Others etc. */
	private TYPE_SCHED typeOfSched;

	/**Used in UI to store the size of buttons in schedule view*/
	private float size;
	
	public String getLabel() {
		return label;
	}

	public int getStarttime() {
		return starttime;
	}

	public int getEndtime() {
		return endtime;
	}

	public DAY getDay() {
		return day;
	}

	public FREQ getFrequency() {
		return frequency;
	}

	public long getRowId() {
		return rowId;
	}

	public TYPE_SCHED getTypeOfSched() {
		return typeOfSched;
	}

	public float getSize() {
		return size;
	}

	/** Constructor
	 * 
	 * Takes in an instance of schedule to construct
	 * 
	 * @param schedule
	 */
	public ScheduleProtected(Schedule schedule) {
		schedule.validate();
		
		label = schedule.label;
		starttime = schedule.starttime;
		endtime = schedule.endtime;
		day = schedule.day;
		frequency = schedule.frequency;
		rowId = schedule.rowId;
		typeOfSched = schedule.typeOfSched;
		size = schedule.size;
	}
	
    @Override
	public boolean equals(Object o) {
    	super.equals(o);
    	
    	ScheduleProtected tmpSched = (ScheduleProtected) o;
    	
    	if (       !tmpSched.getDay().equals(this.day)
    			||  tmpSched.getEndtime() != this.endtime
    			|| !tmpSched.getFrequency().equals(this.frequency)
    			|| !tmpSched.getLabel().equals(this.label)
    			||  tmpSched.getRowId() != this.rowId
    			||  tmpSched.getSize()  != this.size
    			||  tmpSched.getStarttime() != this.starttime
    			|| !tmpSched.getTypeOfSched().equals(this.typeOfSched))
    			
    		return false;
    	
    	
		return true;
	}
    
	
	//Constants for parcelling
	private static final int P_STARTTIME   = 0; 
	private static final int P_ENDTIME     = 1;
	private static final int P_DAY         = 2;
	private static final int P_FREQ        = 3;
	private static final int P_TYPEOFSCHED = 4;
	
	
	// Parcelling part
    public ScheduleProtected(Parcel in){
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

	public static final Parcelable.Creator<ScheduleProtected> CREATOR
	= new Parcelable.Creator<ScheduleProtected>() {
		public ScheduleProtected createFromParcel(Parcel in) {
			return new ScheduleProtected(in);
		}

		public ScheduleProtected[] newArray(int size) {
			return new ScheduleProtected[size];
		}
	};
	
}
