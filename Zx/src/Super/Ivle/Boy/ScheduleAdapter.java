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
	
	/** In UI mode, odd and even modulelists will include modules with EVERY_WEEK frequency */
	enum MODE   {UI, ALARM }
	
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
				
				//Slight optimization for ALARM
				if (mode == MODE.UI)
					currschedule.size = cal_size(currschedule.starttime,currschedule.endtime);
				
				ScheduleProtected newSched = new ScheduleProtected(currschedule);
				
				//Add into correct list according to frequency
				List<ScheduleProtected>[] modulelist = modulelistType(currschedule.frequency);
				modulelist[currschedule.day.ordinal()].add(newSched);
				
				//Special case for UI components
				if (mode == MODE.UI && currschedule.frequency == FREQ.EVERY_WEEK)
				{
					modulelist = modulelistType(FREQ.EVEN_WEEK);
					modulelist[currschedule.day.ordinal()].add(newSched);
					
					modulelist = modulelistType(FREQ.ODD_WEEK);
					modulelist[currschedule.day.ordinal()].add(newSched);
				}

				currschedule.clear();
				
				cursor.moveToNext();
				
			}
			cursor.close();
			
			if (mode == MODE.UI)
				insert_empty_entries();
			
		}
		
		finally {
			if (db != null)
				db.close();
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
	 * @return new rowId, or -1 if failed
	 * 
	 * @throw IllegalArgumentException  When new schedule timing conflicts with other schedules
	 */
	public Boolean create(Schedule newSched)
	{
		long rowId;
		
		if (mMode == MODE.ALARM)
			throw new IllegalStateException("Alarm mode is not allowed to access 'create' function");
		
		ScheduleDbAdapter db = new ScheduleDbAdapter(mCtx);
		db.open();
		
		try
		{
			if ((rowId = db.createSchedule(newSched)) != -1)
			{
				newSched.rowId = rowId;
				replaceSchedule(newSched);
				insert_empty_entries(newSched.day,newSched.frequency);
				
				return true;
				
			}
			return false;
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
	 * @param oldIndex     Index of old element to delete schedule from
	 * 
	 * @return True if successful
	 */
	public Boolean update(Schedule newSched, ScheduleProtected oldSched)
	{
		if (mMode == MODE.ALARM)
			throw new IllegalStateException("Alarm mode is not allowed to access 'update' function");

		ScheduleDbAdapter db = new ScheduleDbAdapter(mCtx);
		
		if (newSched.rowId != oldSched.getRowId())
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
			
			if (db.updateSchedule(newSched) == true)
			{
				if (!removeSchedule(new Schedule(oldSched)))
					return false;
				
				if (!replaceSchedule(newSched))
					return false;
				
				insert_empty_entries(newSched.day,newSched.frequency);
				
				//If they are different days
				if (!newSched.day.equals(oldSched.getDay()))
					insert_empty_entries(oldSched.getDay(),oldSched.getFrequency());
				
				return true;
			}
			else
				return false;
		}
		finally
		{
			if (db != null)
				db.close();
		}
	}
	
	/**
	 * Delete an existing schedule
	 */
	public Boolean delete(Schedule sched)
	{
		if (mMode == MODE.ALARM)
			throw new IllegalStateException("Alarm mode is not allowed to access 'delete' function");

		ScheduleDbAdapter db = new ScheduleDbAdapter(mCtx);
		
		try
		{
			db.open();
			
			if (removeSchedule(sched) == true)
			{
				insert_empty_entries(sched.day,sched.frequency);
				
				if (db.deleteSchedule(sched.rowId) == true)
				{
					return true;
				}
				
				return false;
			}
			return false;
				
		}
		finally
		{
			if (db != null)
				db.close();
		}
	}
	
	/** Replace a given schedule in modulelist with the supplied schedule sched
	 * 
	 * @param sched Schedule to replace with
	 * @return True or False
	 */
	private Boolean replaceSchedule(Schedule sched)
	{
		int tmpDay = sched.day.ordinal();
		ScheduleProtected newSched = new ScheduleProtected(sched);
		
		int index_even=-2,index_odd=-2;
		
		//Calculate size of schedule
		sched.size = cal_size(sched.starttime, sched.endtime);
		
		switch (sched.frequency)
		{
		case EVEN_WEEK:
			index_even = calculateIndex(sched, even_modulelist);
			if (index_even == -1)
				return false; 
			
			removeScheduleByIndex(even_modulelist[tmpDay], index_even);
			even_modulelist[tmpDay].add(index_even,newSched);
			break;
			
		case ODD_WEEK:
			index_odd = calculateIndex(sched, odd_modulelist);
			if (index_odd == -1)
				return false; 
			
			removeScheduleByIndex(odd_modulelist[tmpDay], index_odd);
			odd_modulelist[tmpDay].add(index_even,newSched);
			break;
		
		case EVERY_WEEK:
			index_even = calculateIndex(sched, even_modulelist);
			if (index_even == -1)
				return false; 
			
			index_odd = calculateIndex(sched, odd_modulelist);
			odd_modulelist[tmpDay].add(index_even,newSched);
			if (index_odd == -1)
				return false; 
			
			removeScheduleByIndex(odd_modulelist[tmpDay], index_odd);
			even_modulelist[tmpDay].add(index_even,newSched);
			break;
		}
		return true;
	}
	
	/** Remove a schedule from the modulelist
	 * 
	 * Take note, it does not repopulate the empty buttons
	 * 
	 * @param sched
	 * @param index
	 */
	private Boolean removeSchedule(Schedule sched)
	{
		int index_even=-2, index_odd=-2;
		
		int tmpDay = sched.day.ordinal();
		
		switch (sched.frequency)
		{
		case EVEN_WEEK:
			index_even = calculateIndex(sched, even_modulelist);
			if (index_even == -1)
				return false; 
			
			removeScheduleByIndex(even_modulelist[tmpDay], index_even);
			break;
			
		case ODD_WEEK:
			index_odd = calculateIndex(sched, odd_modulelist);
			if (index_odd == -1)
				return false; 
			
			removeScheduleByIndex(odd_modulelist[tmpDay], index_odd);
			break;
		
		case EVERY_WEEK:
			index_even = calculateIndex(sched, even_modulelist);
			if (index_even == -1)
				return false; 
			
			removeScheduleByIndex(even_modulelist[tmpDay], index_even);
			
			index_odd = calculateIndex(sched, odd_modulelist);
			if (index_odd == -1)
				return false; 
			
			removeScheduleByIndex(odd_modulelist[tmpDay], index_odd);
			break;
		}
		
		return true;

	}
	
	/** Remove a schedule from the modulelist given the index
	 * 
	 * Take note, it does not repopulate the empty buttons
	 * 
	 * @param sched
	 * @param index
	 */
	private void removeScheduleByIndex(List<ScheduleProtected> modulelist, int index)
	{
		if (modulelist.get(index+1).getTypeOfSched() == TYPE_SCHED.EMPTY_BUTTON)
			modulelist.remove(index+1);
		
		modulelist.remove(index);
		
		if (modulelist.get(index-1).getTypeOfSched() == TYPE_SCHED.EMPTY_BUTTON)
			modulelist.remove(index-1);
	}
	
	/** This function inserts blank schedule slots into the 
	 * attributes odd_modulelist and even_modulelist
	 * 
	 * The purpose is to simplify UI rendering, so that the modulelist will contain
	 * both the schedules and the empty slots.
	 * 
	 */
	private void insert_empty_entries() {
		insert_empty_entries(odd_modulelist);
		insert_empty_entries(even_modulelist);
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
	
	/** This function inserts blank schedule slots into the parameter modulelist 
	 * 
	 * The purpose is to simplify UI rendering, so that the modulelist will contain
	 * both the schedules and the empty slots.
	 * 
	 * This version gets the day and frequency, so as to update a specific day in the modulelist
	 * 
	 * @param modulelist   The modules to be shown in UI
	 * @param tmpDay	   Day in modulelist to edit 
	 * @param freq		   Frequency, for the specific modulelist
	 */
	private void insert_empty_entries(DAY tmpDay, FREQ freq)
	{
		
		switch (freq)
		{
		case ODD_WEEK:
			insert_empty_entries(odd_modulelist[tmpDay.ordinal()],tmpDay);
			break;

		case EVEN_WEEK:
			insert_empty_entries(even_modulelist[tmpDay.ordinal()],tmpDay);
			break;
		case EVERY_WEEK:
			insert_empty_entries(odd_modulelist[tmpDay.ordinal()],tmpDay);
			insert_empty_entries(even_modulelist[tmpDay.ordinal()],tmpDay);
			break;
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
		Schedule newSched = new Schedule();
		
		ListIterator<ScheduleProtected> itr =  modulelist.listIterator();
		
		while (itr.hasNext())
		{
			ScheduleProtected tmpSched = itr.next();
			if (std_starttime < tmpSched.getStarttime())
			{
				newSched.starttime	 = std_starttime;
				newSched.endtime 	 = tmpSched.getStarttime();
				newSched.size        = cal_size(newSched.starttime,newSched.endtime);
				newSched.day         = day;
				newSched.frequency   = FREQ.EVERY_WEEK;
				newSched.typeOfSched = TYPE_SCHED.EMPTY_BUTTON;
				newSched.label       = "";
				
				itr.previous();
				itr.add(new ScheduleProtected(newSched));
				itr.next();
			
				newSched.clear();
			}
			std_starttime = tmpSched.getEndtime();
		}
		
		int endtimeofday;
		
		//Check if the schedule list is empty
		if (modulelist.size() == 0)
			endtimeofday = 800;

		else 
			endtimeofday = ((LinkedList<ScheduleProtected>) modulelist).getLast().getEndtime();
		
		if(endtimeofday != 0)
		{
			newSched.starttime   = endtimeofday;
			newSched.endtime     = 2400;
			newSched.frequency   = FREQ.EVERY_WEEK;
			newSched.size        = cal_size(endtimeofday, 2400);
			newSched.day         = day;
			newSched.typeOfSched = TYPE_SCHED.EMPTY_BUTTON;
			newSched.label       = "";
			
			modulelist.add(new ScheduleProtected(newSched));
			newSched.clear();
		}
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
	private List<ScheduleProtected>[] modulelistType(FREQ week)
	{
		switch (week)
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
	 * @param sched
	 * @return Position in modulelist, or -1 if no empty position to insert
	 */
	private int calculateIndex(Schedule newSched, List<ScheduleProtected>[] modulelist)
	{
		ListIterator<ScheduleProtected> itr = modulelist[newSched.day.ordinal()].listIterator();
		
		//Find the index
		while (itr.hasNext())
		{
			ScheduleProtected oldSched = itr.next();
			
			//Find which location the schedule will fit within
			if (newSched.starttime >= oldSched.getStarttime()
					&& newSched.endtime <= oldSched.getEndtime())
			{
				//If it is not an empty button
				if (!oldSched.getTypeOfSched().equals(TYPE_SCHED.EMPTY_BUTTON))
					return -1;
				
				return itr.previousIndex();
			}
			
			//Optimization
			if (newSched.starttime >= oldSched.getStarttime())
				return -1;
			
		}
		
		return -1;
	}
}

