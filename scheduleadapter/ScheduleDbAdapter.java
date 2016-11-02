package Super.Ivle.Boy;

import Super.Ivle.Boy.Schedule.DAY;
import Super.Ivle.Boy.Schedule.FREQ;
import android.content.*;
import android.database.*;
import android.database.sqlite.*;

/**
 * Simple timetable database access helper class. Defines the basic CRUD operations
 * for the timetable example, and gives the ability to list all  as well as
 * retrieve or modify a specific note.
 * 
 * @author Damien 
 * 
 */
public class ScheduleDbAdapter {

    public static final String KEY_LABEL       = "label";
    public static final String KEY_STARTTIME   = "starttime";
    public static final String KEY_ENDTIME     = "endtime";
    public static final String KEY_FREQUENCY   = "frequency";
    public static final String KEY_DAY		   = "day";
    public static final String KEY_TYPE		   = "type";
    public static final String KEY_ROWID 	   = "_id";
    
    private DatabaseHelper mDbHelper = null;
    private SQLiteDatabase mDb = null;;

    /**
     * Database creation sql statement
     */
    private static final String DATABASE_CREATE =
                     				   "create table schedule "
                     		         +"(_id        integer primary key autoincrement, "
                                     + "label       text not null,     "
                            		 + "starttime   integer not null, "
                    				 + "endtime     integer not null,   "
            						 + "frequency   integer not null,"
            						 + "type        integer not null,"
            						 + "day         integer not null); ";
    								 

    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "schedule";
    private static final int DATABASE_VERSION = 1;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
            //        + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS schedule");
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public ScheduleDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the schedule database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public ScheduleDbAdapter open() throws SQLException, IllegalStateException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        
        return this;
    }

    /** Check if tables is empty*/
    public Boolean isEmpty()
    {
    	Cursor cursor = mDb.query(DATABASE_TABLE, new String[] {}, 
                null, null, null, null, null);
       
       return (cursor.getCount() == 0);
    }

    public void close() {
        mDbHelper.close();
    }


    /**
     * Create a new schedule entry using the various fields. If the note is
     * successfully created return the new rowId for that note, otherwise return
     * a -1 to indicate failure.
     * 
     * @param label      the label of the note
     * @param starttime  the starting time of the schedule 
     * @param endtime    the end time of the schedule
     * @param frequency  the frequency of activation
     * @return true or false
     */
    public long createSchedule(ScheduleProtected schedule) {
        
    	ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_LABEL,     schedule.getLabel());
        initialValues.put(KEY_STARTTIME, schedule.getStarttime());
        initialValues.put(KEY_ENDTIME,   schedule.getEndtime());
        initialValues.put(KEY_FREQUENCY, schedule.getFrequency().ordinal());
        initialValues.put(KEY_DAY, schedule.getDay().ordinal());
        initialValues.put(KEY_TYPE, schedule.getTypeOfSched().ordinal());

        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    /**
     * Delete the note with the given rowId
     * 
     * @param rowId id of schedule to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteSchedule(long rowId) {

        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all schedule in the database
     * 
     * Cursor returned is ordered by the time of the schedule, from 0800 to 2400
     * 
     * @return Cursor over all schedule
     */
    public Cursor fetchAllSchedules() {

        return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID,
                KEY_LABEL, KEY_ENDTIME, KEY_DAY, 
                KEY_FREQUENCY, KEY_STARTTIME, KEY_TYPE}, null, null, null, null, KEY_STARTTIME + " ASC ");
    }

    /**
     * Return a Cursor positioned at the note that matches the given rowId
     * 
     * @param rowId id of note to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor fetchSchedule(long rowId) throws SQLException {

        Cursor mCursor =

            mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                    KEY_LABEL, KEY_ENDTIME, KEY_DAY, 
                    KEY_FREQUENCY, KEY_STARTTIME, KEY_TYPE}, KEY_ROWID + "=" + rowId, null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }
    
    /**
     * Return a Cursor positioned at the note that matches the given rowId
     * 
     * @param rowId id of note to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor fetchSchedulesByDay(DAY day) throws SQLException {

        Cursor mCursor =

            mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID,
                    KEY_LABEL, KEY_ENDTIME, KEY_DAY, 
                    KEY_FREQUENCY, KEY_STARTTIME, KEY_TYPE}, KEY_DAY + "=" + Integer.toString(day.ordinal()), null,
                    null, null, KEY_STARTTIME + " ASC ");
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }
    
    /**
     * Return a Cursor positioned at the note that matches the given rowId
     * 
     * @param rowId id of note to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor fetchSchedulesByDay(DAY day,FREQ freq) throws SQLException {

        Cursor mCursor =

            mDb.query( DATABASE_TABLE, new String[] {KEY_ROWID,
                    KEY_LABEL, KEY_ENDTIME, KEY_DAY, 
                    KEY_FREQUENCY, KEY_STARTTIME, KEY_TYPE},
                    KEY_DAY + "=" + Integer.toString(day.ordinal()) + " AND " + KEY_FREQUENCY + "=" + Integer.toString(freq.ordinal())
                    , null,null, null, KEY_STARTTIME + " ASC ");
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    /**
     * Update the note using the details provided. The note to be updated is
     * specified using the rowId, and it is altered to use the label and body
     * values passed in
     * 
     * @param schedule Contains all info 
     * @return true if the schedule was successfully updated, false otherwise
     */
    public boolean updateSchedule(ScheduleProtected schedule) {
    	
    	if (schedule.getRowId() < 0)
    		throw new IllegalArgumentException("rowId of schedule undefined!");
    	    	
        ContentValues args = new ContentValues();
        args.put(KEY_LABEL, schedule.getLabel());
        args.put(KEY_STARTTIME, schedule.getStarttime());
        args.put(KEY_ENDTIME, schedule.getEndtime());
        args.put(KEY_FREQUENCY, schedule.getFrequency().ordinal());
        args.put(KEY_DAY, schedule.getDay().ordinal());
        args.put(KEY_TYPE, schedule.getTypeOfSched().ordinal());
        
        if (mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=?" , new String[] {Long.toString(schedule.getRowId())}) > 0)
        	return true;
        else
        	return false;
    }

    /** For starting a transaction
     * 
     */
    public void beginTransaction()
    {
    	mDb.beginTransaction();
    }
    
    /**Ends a transaction 
     * 
     */
    public void endTransaction()
    {
    	mDb.endTransaction();
    }
    
    /** Marks a transaction as successful
     * 
     * If a transaction ends without being set as successful,
     * all changes will be rolled back.
     */
    public void setTransactionSuccessful()
    {
    	mDb.setTransactionSuccessful();
    }
    
    /** Delete the schedule table and create a new one
     * 
     */
    public void deleteAllSchedules()
    {
    	//Use the function, but do not change database version
    	mDbHelper.onUpgrade(mDb, DATABASE_VERSION, DATABASE_VERSION);
    }
}
