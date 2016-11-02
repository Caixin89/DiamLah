package Super.Ivle.Boy;

import java.io.*;
import java.net.*;
import java.util.regex.Pattern;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import Super.Ivle.Boy.Schedule.*;
import android.content.Context;
import android.net.*;
import android.util.Log; 



/**
 * Class that will handle getting all the various information regarding timetable
 * 
 * First initialize the constructor with the user's matric no. and other info,
 * then use the populate function to download all relevant information into the
 * public attributes. 
 * 
 * 
 * @author Damien Tan
 *
 */
public class GetTimetable {
	
	private Context mctx;
	private URL url;
	private TimetableStorage mtimetableStore;

	/** Constructor
	 * 
	 * Takes in all required info for getting the timetable,
	 * and constructs the url from there.
	 * 
	 * @param  matricNo   Contains the matriculation number of the user
	 * @param  acadYear   Contains the current academic year in the form YYYY-YYYY
	 * @param  sem	      Contains the current semester eg. 1/2
	 * 
	 * @throws Exception  When validation checks for parameters fail, or other errors
	 * 
	 */
	GetTimetable(String matricNo, String acadYear, int sem, Context ctx) throws IllegalArgumentException
	{
		mctx = ctx;
		//Check parameters

		if (sem > 4 || sem < 1)
			throw new IllegalArgumentException("Semester can only be 1,2,3 or 4");
		
		if (acadYear.length() != 9)
			throw new IllegalArgumentException("Academic Year is not correctly formatted");
		
		//Make sure acadYear is of form YYYY-YYYY
		if (Pattern.matches("\\d{4}-\\d{4}",acadYear) == false)
			throw new IllegalArgumentException("Academic Year is not correctly formatted");
		
		//Make sure Matric no. is formed correctly
		
		//It must start with A or U, have 6-7 digits in between, then end with a character
		if (Pattern.matches("[AaUu]\\d{6,7}[A-Za-z]", matricNo) == false)
			throw new IllegalArgumentException("Matriculation number is not correctly formatted");
		
		//Build url string
		StringBuilder tmpStr = new StringBuilder("http://mobapps.nus.edu.sg/api/student/");
		tmpStr.append(matricNo);
		tmpStr.append("/");
		tmpStr.append(acadYear);
		tmpStr.append("/");
		tmpStr.append(sem);
		tmpStr.append("/timetable/");
		
		try {
			Log.d("GetTimetable","The URL is " + tmpStr.toString());
			//url = new URL(tmpStr.toString());
			
			//For testing purposes only
			url = new URL("http://www.comp.nus.edu.sg/~damien/timetable.xml");
		
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
	}
	
	/** Use this class to download the data from the web onto
	 * the attributes in the class
	 * 
	 * @return 
	 * @throws IOException               When HTTP connection cannot be opened i.e. Internet not switched on
	 * @throws UnknownHostException      When host mobapps.nus.edu.sg is unreachable (i.e. not on campus)
	 */
	public void startDownload() throws IOException,UnknownHostException {
		BufferedReader in = null;
		HttpURLConnection urlConnection = null;
		
		//Check if there is Internet connection
			ConnectivityManager cm = (ConnectivityManager) mctx.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo ni = cm.getActiveNetworkInfo();

		if (ni == null || ni.isConnected() == false)
		    throw new IOException("Internet Connection is not available");
		

		//Check if http://mobapps.nus.edu.sg/ is reachable
		InetAddress.getByName("mobapps.nus.edu.sg").isReachable(3);

	
		urlConnection=(HttpURLConnection) url.openConnection();
		in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
	
		
		//Workaround for stupid bug in Simple xml
		//Remove all instances of class="" field
		String tmpStr;
		StringBuilder strBuilder = new StringBuilder();
		
		while ((tmpStr = in.readLine()) != null)
		{
			strBuilder.append(tmpStr);
		}
		
		tmpStr = strBuilder.toString();
		
		//Replace class="" using reluctant quantifier
		tmpStr = tmpStr.replaceAll("class=\".{0,5}?\"", "");
		
		StringReader strRead = new StringReader(tmpStr);
			
			
		try {
			//Convert xml into timeStore object
			Serializer serializer=new Persister();
			mtimetableStore = serializer.read(TimetableStorage.class, strRead, false);
		}
		catch (Exception e) {			
			e.printStackTrace();
		}

		finally {
			urlConnection.disconnect();	
		}
	}
	
	
	/**Erases all current information in sqlite Schedule database
	 * and adds in timetable that is obtained from NUS server.
	 * 
	 * Use transactions on sqlite database, so that if any error occurs,
	 * all previous data will be immediately rolled back.
	 * 
	 * @exception IllegalStateException When the class has not obtained timetable information
	 * 									through the startDownload() function
	 */
	public void storeInDB(Context ctx) throws IllegalStateException
	{
		if (mtimetableStore == null)
			throw new IllegalStateException("The timetable has not been downloaded!");
	
		ScheduleDbAdapter db = new ScheduleDbAdapter(ctx);
		
		try
		{
			db.open();
			db.beginTransaction();
			
			db.deleteAllSchedules();
			
			Schedule tmpSchedule = new Schedule();
	
			for (Timetable tmpTimeTable : mtimetableStore.data)
			{
				tmpSchedule.label = tmpTimeTable.module;
							
				
				if (tmpTimeTable.starttime == 0 || tmpTimeTable.endtime == 0)
					throw new IllegalStateException("Starttime or endtime has not been initialised!");
							
				tmpSchedule.starttime = tmpTimeTable.starttime;
				tmpSchedule.endtime = tmpTimeTable.endtime;
				
				
				//Store frequency of event
				if (tmpTimeTable.weektp.equals("EVERY WEEK"))
					tmpSchedule.frequency = FREQ.EVERY_WEEK;
				
				else if (tmpTimeTable.weektp.equals("EVEN WEEK"))
					tmpSchedule.frequency = FREQ.EVEN_WEEK;
				
				else if (tmpTimeTable.weektp.equals("ODD WEEK"))
					tmpSchedule.frequency = FREQ.ODD_WEEK;
				
				else
					throw new RuntimeException("XML timetable from NUS website has malformed weektp field.");
	
				//Store type of schedule
				if (tmpTimeTable.type.equals("LECTURE"))
					tmpSchedule.typeOfSched = TYPE_SCHED.LECTURE;
				
				else if (tmpTimeTable.type.equals("TUTORIAL"))
					tmpSchedule.typeOfSched = TYPE_SCHED.TUTORIAL;
				
				else if (tmpTimeTable.type.equals("LABORATORY"))
					tmpSchedule.typeOfSched = TYPE_SCHED.LAB;
				
				else if (tmpTimeTable.type.equals("SEMINAR-STYLE MODULE CLASS"))
					tmpSchedule.typeOfSched = TYPE_SCHED.SEMINAR;
				
				else
					tmpSchedule.typeOfSched = TYPE_SCHED.OTHER;
				
				//Store day of event
				if (tmpTimeTable.day.equals("MONDAY"))
					tmpSchedule.day = DAY.MONDAY;
				
				else if (tmpTimeTable.day.equals("TUESDAY"))
					tmpSchedule.day = DAY.TUESDAY;
				
				else if (tmpTimeTable.day.equals("WEDNESDAY"))
					tmpSchedule.day = DAY.WEDNESDAY;
				
				else if (tmpTimeTable.day.equals("THURSDAY"))
					tmpSchedule.day = DAY.THURSDAY;
				
				else if (tmpTimeTable.day.equals("FRIDAY"))
					tmpSchedule.day = DAY.FRIDAY;
				
				else if (tmpTimeTable.day.equals("SATURDAY"))
					tmpSchedule.day = DAY.SATURDAY;
				
				else if (tmpTimeTable.day.equals("SUNDAY"))
					tmpSchedule.day = DAY.SUNDAY;
				
				else 
					throw new RuntimeException("XML timetable from NUS website has malformed day field.");
				
				db.createSchedule(tmpSchedule);
				
				tmpSchedule.clear();
			}
			db.setTransactionSuccessful();
		}
		finally {
			
			db.endTransaction();
			db.close();
		}
	}
	
	
}