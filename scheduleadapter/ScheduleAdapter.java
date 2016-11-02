package Super.Ivle.Boy;

import java.util.*;

import Super.Ivle.Boy.Schedule.*;
import android.content.Context;
import android.database.Cursor;


/** This class acts as the implementation layer between the UI and
 *  the ScheduleDbAdapter storage layer. 
 *  
 *  It ensures that whatever information from the Database will be 
 *  translated into a form suitable for the UI.
 * 
 * @author Damien
 *
 */
public class ScheduleAdapter {
	
	private List<ScheduleProtected>[] odd_modulelist;
	private List<ScheduleProtected>[] even_modulelist;
	private List<ScheduleProtected>[] every_modulelist;
	private List<ScheduleProtected>[] once_modulelist;
	
	private final static int NUM_DAYS_IN_WEEK = 7;

	Context mCtx;
	
	/** In UI and EDIT mode, odd and even modulelists will include modules with EVERY_WEEK frequency 
	 *  In UI mode, the modulelists will contain empty buttons corresponding to empty slots in the list */
	enum MODE   {UI, ALARM, EDIT}
	
	MODE mMode;
	
	/** Reads in all schedules from the SQlite database
	 *  and then populate two lists: odd_modulelist and
	 *  even_modulelist, depending on whether the schedule
	 *  is supposed to appear on the odd or the even weeks 
	 *  of the semester.
	 *  
	 * @param ctx Context 
	 * @param mode  If mode is UI, odd and even modulelists will include EVERY_WEEK freq modules
	 */
	@SuppressWarnings("unchecked")
	ScheduleAdapter(Context ctx, MODE mode)
	{
		//Declaring 2d Lists
		odd_modulelist = (LinkedList<ScheduleProtected>[]) new LinkedList[NUM_DAYS_IN_WEEK];
		even_modulelist = (LinkedList<ScheduleProtected>[]) new LinkedList[NUM_DAYS_IN_WEEK];
		once_modulelist = (LinkedList<ScheduleProtected>[]) new LinkedList[NUM_DAYS_IN_WEEK];
		every_modulelist = (LinkedList<ScheduleProtected>[]) new LinkedList[NUM_DAYS_IN_WEEK];

		mCtx = ctx;
		
		mMode = mode;
		
		//Populate the lists
		for (int i=0; i<7; i++)
		{
			odd_modulelist[i] = new LinkedList<ScheduleProtected>();
			even_modulelist[i] = new LinkedList<ScheduleProtected>();
			once_modulelist[i] = new LinkedList<ScheduleProtected>();
			every_modulelist[i] = new LinkedList<ScheduleProtected>();
		}
		
		ScheduleDbAdapter db = new ScheduleDbAdapter(mCtx);
		

		try	{
			db.open();
			
			if (db.isEmpty())
				throw new IllegalStateException("The sqlite table is empty");
			
			Cursor cursor = db.fetchAllSchedules();

			process_cursor(cursor);
			
			cursor.close();
			
			//Empty buttons for UI
			if (mode == MODE.UI)
			{
				insert_empty_entries(odd_modulelist);
				insert_empty_entries(even_modulelist);
				insert_empty_entries(every_modulelist);
			}
			
		}
		
		finally {
			if (db != null)
				db.close();
		}
	}
	
	/** This function extracts data from the cursor provided
	 *  and places it in the correct modulelists
	 */
	private void process_cursor(Cursor cursor)
	{
		cursor.moveToFirst();
		 
		Schedule currschedule = new Schedule();
		
		//Extract info from cursor into schedule class form
		while (cursor.isAfterLast()==false){
			
			currschedule.day         = DAY.values()[cursor.getInt(cursor.getColumnIndex(ScheduleDbAdapter.KEY_DAY))];
			currschedule.frequency   = FREQ.values()[cursor.getInt(cursor.getColumnIndex(ScheduleDbAdapter.KEY_FREQUENCY))];
			currschedule.label       = cursor.getString(cursor.getColumnIndex(ScheduleDbAdapter.KEY_LABEL));
			currschedule.starttime   = cursor.getInt(cursor.getColumnIndex(ScheduleDbAdapter.KEY_STARTTIME));
			currschedule.endtime     = cursor.getInt(cursor.getColumnIndex(ScheduleDbAdapter.KEY_ENDTIME));
			currschedule.typeOfSched = TYPE_SCHED.values()[cursor.getInt(cursor.getColumnIndex(ScheduleDbAdapter.KEY_TYPE))];
			currschedule.rowId       = cursor.getLong(cursor.getColumnIndex(ScheduleDbAdapter.KEY_ROWID));
			
			//Slight optimization for ALARM and EDIT modes
			if (mMode == MODE.UI)
				currschedule.size = cal_size(currschedule.starttime,currschedule.endtime);
			
			ScheduleProtected newSched = new ScheduleProtected(currschedule);
			
			//Add into correct list according to frequency
			List<ScheduleProtected>[] modulelist = modulelistType(currschedule.frequency);
			modulelist[currschedule.day.ordinal()].add(newSched);
			
			//Special case for UI and EDIT components
			if (mMode != MODE.ALARM && currschedule.frequency == FREQ.EVERY_WEEK)
			{
				//Add in entry for both 
				even_modulelist[currschedule.day.ordinal()].add(newSched);
				odd_modulelist[currschedule.day.ordinal()].add(newSched);
			}

			currschedule.clear();
			
			cursor.moveToNext();
		}
	}
	
	/** Get a reference of schedule object from the modulelist
	 *  depending, on which WEEK_TYPE and DAY
	 *  
	 *  Take note, it's a reference, not a copy. Do not edit if
	 *  you do not want the changes to be reflected in the
	 *  modulelist
	 */
	public ScheduleProtected get(FREQ freq, DAY day, int index)
	{
		return modulelistType(freq)[day.ordinal()].get(index);
	}
	
	/** Returns the number of schedules on a given module frequency and day
	 * 
	 * @param freq  Frequency type of module
	 * @param day   Day of module
	 * @return      Number of schedules on that day
	 */
	public int size(FREQ freq, DAY day)
	{
		return modulelistType(freq)[day.ordinal()].size();
	}
	
	/**
	 * Creates a new schedule
	 * 
	 * @param  newSched New schedule to create
	 * @return True or False
	 * 
	 * @throw IllegalArgumentException  When new schedule timing conflicts with other schedules
	 * @throw IllegalArgumentException  When mode of class is not EDIT, this function will not be available
	 */
	public Boolean create(ScheduleProtected newSched)
	{
		long rowId;
		
		if (mMode != MODE.EDIT)
			throw new IllegalStateException("Only edit mode is allowed to access 'create' function");
		
		ScheduleDbAdapter db = new ScheduleDbAdapter(mCtx);
		
		try
		{
			db.open();

			//Check if it is possible to add in new schedule
			if (checkEmptySpace(newSched) == false)
				return false;
			
			if ((rowId = db.createSchedule(newSched)) == -1)
				return false;
			
			//Now add in the rowId info
			Schedule newSched2 = new Schedule(newSched);
			newSched2.rowId = rowId;
			
			//Replace schedule with updated one containing row information
			insertSchedule(new ScheduleProtected(newSched2));
			
			return true;
			
		}
		finally
		{
			if (db != null)
				db.close();
		}
	}
	
	/**
	 * Updates an existing schedule
	 *
	 * @param newSched     New Schedule to insert
	 * @param oldSched	   Old schedule to delete
	 * 
	 * @throw IllegalArgumentException  When mode of class is not EDIT, this function will not be available
	 * @return True if successful
	 */
	public Boolean update(ScheduleProtected newSched, ScheduleProtected oldSched)
	{
		if (mMode != MODE.EDIT)
			throw new IllegalStateException("Only edit mode is allowed to access 'update' function");

		ScheduleDbAdapter db = new ScheduleDbAdapter(mCtx);
		
		if (newSched.getRowId() != oldSched.getRowId())
		{
			throw new IllegalArgumentException("ScheduleAdapter: update: The rowid of the new schedule and the old" +
					                           "schedule are not the same!");
		}
		
		//If both schedules are the same, nothing left to do
		if (newSched.equals(oldSched))
			return true;
		
		try
		{
			db.open();
			
			if (removeSchedule(oldSched) == false)
				return false;
			
			if (checkEmptySpace(newSched) == false)
			{
				//Add back the old schedule
				insertSchedule(oldSched);
				return false;
			}
			
			insertSchedule(newSched);

			if (db.updateSchedule(newSched) == false)
			{
				//Weird error that shouldn't happen
				//roll back all changes
				removeSchedule(newSched);
				insertSchedule(oldSched);
				return false;
			}
			
			return true;
		}
		finally
		{
			if (db != null)
				db.close();
		}
	}
	
	/**
	 * Delete an existing schedule
	 * 
	 * @throw IllegalArgumentException  When mode of class is not EDIT, this function will not be available
	 * @param sched  The schedule to delete
	 */
	public Boolean delete(ScheduleProtected sched)
	{
		if (mMode != MODE.EDIT)
			throw new IllegalStateException("Only edit mode is allowed to access 'delete' function");

		ScheduleDbAdapter db = new ScheduleDbAdapter(mCtx);
		
		try
		{
			db.open();
			
			if (removeSchedule(sched) == false)
				return false;
			
			if (db.deleteSchedule(sched.getRowId()) == false)
				return false;
			
			return true;
		}
		finally
		{
			if (db != null)
				db.close();
		}
	}
	
	/** Insert a given schedule in modulelist 
	 * 
	 * @param sched Schedule to insert with
	 * @return True or False
	 */
	private void insertSchedule(ScheduleProtected sched)
	{
		int tmpDay = sched.getDay().ordinal();
		
		int index_even,index_odd,index_every,index;
		
		Schedule sched2 = new Schedule(sched);
		
		//Calculate size of schedule
		sched2.size = cal_size(sched2.starttime, sched2.endtime);
		
		ScheduleProtected newSched = new ScheduleProtected(sched2); 
		
		FREQ freq = sched2.frequency;
		
		//Check if empty slot exists, then replaces schedule
		switch (freq)
		{
		case ODD_WEEK:
		case EVEN_WEEK:
			index = calculateIndexInsert(sched, modulelistType(freq));
			modulelistType(freq)[tmpDay].add(index,newSched);
			break;
			
		case EVERY_WEEK:
			index_even = calculateIndexInsert(sched, even_modulelist);
			index_odd = calculateIndexInsert(sched, odd_modulelist);
			index_every = calculateIndexInsert(sched, every_modulelist);
			
			even_modulelist[tmpDay].add(index_even,newSched);
			odd_modulelist[tmpDay].add(index_odd,newSched);
			every_modulelist[tmpDay].add(index_every,newSched);
			break;
		}
	}
	
	
	/** Remove a schedule from the modulelist
	 * 
	 * Take note, it does not repopulate the empty buttons
	 * 
	 * @param sched
	 * @param index
	 */
	private Boolean removeSchedule(ScheduleProtected sched)
	{
		Boolean returnValue[] = new Boolean[3];
		
		FREQ freq = sched.getFrequency();
		
		switch (freq)
		{
		case EVEN_WEEK:
		case ODD_WEEK:
			return removeSchedule(sched,modulelistType(freq));
		
		case EVERY_WEEK:
			returnValue[0] = removeSchedule(sched, even_modulelist);
			returnValue[1] = removeSchedule(sched,odd_modulelist);
			returnValue[2] = removeSchedule(sched,every_modulelist);
			
			for (Boolean value : returnValue)
			{
				if (value == false)
					return false;
			}
			return true;
		}
		
		return true;

	}
	
	/** This function inserts blank schedule slots into the parameter modulelist 
	 * 
	 * The purpose is to simplify UI rendering, so that the modulelist will contain
	 * both the schedules and the empty slots.
	 * 
	 * @param modulelist   The modules to be shown in UI
	 */
	private void insert_empty_entries(List<ScheduleProtected>[] modulelist)
	{
		int index=0;
		for (List<ScheduleProtected> tmpDay : modulelist) //fill in the blanks
		{
			insert_empty_entries(tmpDay,DAY.values()[index]);
			index++;
		}
	}
	
	/** This function updates the modulelist, so that after edits, the
	 *  layout of the UI will be consistent.
	 *  
	 *  This function is only meant to be used by UI mode.
	 * 
	 * 
	 * @param tmpDay	   Day in modulelist to update 
	 * @param freq		   Frequency, for the specific modulelist
	 */
	public void updateModuleList(DAY tmpDay, FREQ freq)
	{
		if (mMode != MODE.UI)
			throw new IllegalStateException("Only UI mode can invode 'update' function.");
		
		int dayNo = tmpDay.ordinal();
		
		ScheduleDbAdapter db = new ScheduleDbAdapter(mCtx);
		Cursor cursor = null;
		
		try
		{
			db.open();
			
			switch (freq)
			{
			case ODD_WEEK:
			case EVEN_WEEK:
				cursor = db.fetchSchedulesByDay(tmpDay,freq);
				modulelistType(freq)[dayNo].clear();
				process_cursor(cursor);
				insert_empty_entries(modulelistType(freq)[dayNo],tmpDay);
				cursor.close();
				break;
			
			case EVERY_WEEK:
				cursor = db.fetchSchedulesByDay(tmpDay);
				odd_modulelist[dayNo].clear();
				even_modulelist[dayNo].clear();
				every_modulelist[dayNo].clear();
				process_cursor(cursor);
				insert_empty_entries(odd_modulelist[dayNo],tmpDay);
				insert_empty_entries(even_modulelist[dayNo],tmpDay);
				insert_empty_entries(every_modulelist[dayNo],tmpDay);
				cursor.close();
				break;
			}
		
		}
		finally
		{
			if (db!=null)
				db.close();
		}
	}
	
	/** This function inserts blank schedule slots into the parameter modulelist 
	 * 
	 * The purpose is to simplify UI rendering, so that the modulelist will contain
	 * both the schedules and the empty slots.
	 * 
	 * @param modulelist   The modules to be shown in UI
	 */
	private void insert_empty_entries(List<ScheduleProtected> modulelist, DAY day)
	{
		int std_starttime = 800;
		
		ListIterator<ScheduleProtected> itr =  modulelist.listIterator();
		
		while (itr.hasNext())
		{
			ScheduleProtected tmpSched = itr.next();
			
			if (std_starttime < tmpSched.getStarttime())
			{
				ScheduleProtected newSched = create_empty_entry(std_starttime,tmpSched.getStarttime(),day);
				
				itr.previous();
				itr.add(newSched);
				itr.next();
			}
			
			std_starttime = tmpSched.getEndtime();
		}
		
		int endtimeofday;
		
		//Check if the schedule list is empty
		if (modulelist.size() == 0)
			endtimeofday = 800;

		else 
			endtimeofday = ((LinkedList<ScheduleProtected>) modulelist).getLast().getEndtime();
		
		if(endtimeofday != 2400)
		{
			ScheduleProtected newSched = create_empty_entry(endtimeofday,2400,day);
			modulelist.add(newSched);
		}
	}
	
	/** Creates an empty button for UI
	 * 
	 * @param starttime Start time of button
	 * @param endtime   End time of button
	 * @return   The schedule to be inserted
	 */
	private ScheduleProtected create_empty_entry(int starttime, int endtime, DAY day)
	{
		Schedule newSched = new Schedule();
		
		newSched.starttime   = starttime;
		newSched.endtime     = endtime;
		newSched.frequency   = FREQ.ONCE;
		newSched.size        = cal_size(starttime, endtime);
		newSched.day         = day;
		newSched.typeOfSched = TYPE_SCHED.EMPTY_BUTTON;
		newSched.label       = "";
		
		return new ScheduleProtected(newSched);
	}
	
	/** Calculates the size of the schedule button according to the
	 *  start time and end time of schedule
	 *  
	 *   
	 * @param currstarttime  Start time of schedule
	 * @param currendtime    End time of schedule
	 * 
	 * @return  value of the size of button
	 */
	private float cal_size(int currstarttime, int currendtime)
	{
		int decistarttime = (currstarttime / 100) * 60 + (currstarttime % 100);
		int deciendtime = (currendtime / 100) * 60 + (currendtime % 100);
		float currsize =  (deciendtime - decistarttime)/60;
		
		return currsize;
	}
	

	/** Gives you the correct modulelist depending on the WEEKTYPE */
	private List<ScheduleProtected>[] modulelistType(FREQ freq)
	{
		switch (freq)
		{
		case EVEN_WEEK:
			return even_modulelist;
		case ODD_WEEK:
			return odd_modulelist;
		case EVERY_WEEK:
			return every_modulelist;
		case ONCE:
			return once_modulelist;
		}
		
		//Should never come here
		return null;
	}
	
	/** Calculates the index of the modulelist for a schedule
	 *  to be inserted into
	 *  
	 * 
	 * @param sched
	 * @return Position in modulelist, or -1 if no position to insert
	 */
	private int calculateIndexInsert(ScheduleProtected newSched, List<ScheduleProtected>[] modulelist)
	{
		//If list is empty, can insert anywhere
		if (modulelist[newSched.getDay().ordinal()].size() == 0)
			return 0;
		
		
		ListIterator<ScheduleProtected> itr = modulelist[newSched.getDay().ordinal()].listIterator();
		int time1=800;
		int time2;
		
		//If first schedule is 8am, just skip
		if ((time2 = itr.next().getStarttime()) == 800)
			;
		else
		{
			if (newSched.getStarttime() >= time1
					&& newSched.getEndtime() <= time2)
			{
				return 0;
			}
		}
		
		itr.previous();
		
		//Find the index
		while (itr.hasNext())
		{
			time1 =	itr.next().getEndtime();
			
			if (time1 == 2400)
				return -1;
			
			if (itr.hasNext())
				time2 = itr.next().getStarttime();
			else
				time2 = 2400;
			
			//Optimization
			if (time1 > newSched.getStarttime())
				return -1;
			
			//Find which location the schedule will fit within
			if (newSched.getStarttime() >= time1
					&& newSched.getEndtime() <= time2)
			{
				if (time2 == 2400)
					return itr.previousIndex() + 1;
				else
					return itr.previousIndex();
			}
			
			itr.previous();
		}
		
		return -1;
	}
	
	/** Calculates the index of an existing schedule in the list.
	 *  For use when deleting schedules
	 * 
	 * @param sched         Schedule to search
	 * @param modulelist    The modulelist to search in
	 * @return
	 */
	private Boolean removeSchedule(ScheduleProtected sched, List<ScheduleProtected>[] modulelist)
	{
		ListIterator<ScheduleProtected> itr = modulelist[sched.getDay().ordinal()].listIterator();
		
		//Find the schedule
		while (itr.hasNext())
		{
			ScheduleProtected oldSched = itr.next();
			
			if (oldSched.getRowId() == sched.getRowId())
			{
				itr.remove();
				return true;
			}
		}
		
		return false;
	}
	
	/** Checks if there is an empty slot in the modulelist
	 *  to fit the schedule
	 *  
	 * @param  sched Schedule to insert
	 * @return True if there is enough space, false otherwise
	 */
	private Boolean checkEmptySpace(ScheduleProtected sched)
	{
		FREQ freq = sched.getFrequency();
		switch (freq)
		{
		case ODD_WEEK:
		case EVEN_WEEK:
			if (calculateIndexInsert(sched, modulelistType(freq)) == -1)
				return false;
		break;
		
		case EVERY_WEEK:
			if (calculateIndexInsert(sched, even_modulelist) == -1)
				return false;
			if (calculateIndexInsert(sched, odd_modulelist) == -1)
				return false;
			if (calculateIndexInsert(sched, every_modulelist) == -1)
				return false;
		break;
		}
		
		return true;
	}
	
}