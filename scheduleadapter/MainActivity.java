package Super.Ivle.Boy;

import java.io.IOException;
import java.net.UnknownHostException;

import Super.Ivle.Boy.Schedule.DAY;
import Super.Ivle.Boy.Schedule.FREQ;
import Super.Ivle.Boy.Schedule.TYPE_SCHED;
import Super.Ivle.Boy.ScheduleAdapter.MODE;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity{
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    
	    GetTimetable get = new GetTimetable("A0001234K", "2010-2011", 1, this);
	    
	    try {
			get.startDownload();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try
		{
			
	    get.storeInDB();
	    
	    
	    ScheduleAdapter adp = new ScheduleAdapter(this, MODE.EDIT);
	    ScheduleAdapter adp_ui = new ScheduleAdapter(this, MODE.UI);
	    
	    Schedule newSched = new Schedule();
	    
	    newSched.day = DAY.MONDAY;
	    newSched.starttime = 1000;
	    newSched.endtime = 1200;
	    newSched.frequency = FREQ.EVERY_WEEK;
	    newSched.label = "TEST";
	    newSched.typeOfSched = TYPE_SCHED.LECTURE;
	    
	    Log.d("TEST",adp.create(new ScheduleProtected(newSched)).toString());
	    
	    
	    //ScheduleProtected testSched = adp.get(FREQ.EVERY_WEEK, DAY.MONDAY, 1);
	    
	    //Log.d("TEST",testSched.getLabel() + testSched.getSize());
	    
	    //adp.delete(testSched);
	    
	    adp_ui.updateModuleList(DAY.MONDAY, FREQ.EVERY_WEEK);
	    
	    //Log.d("TEST",adp.get(FREQ.ODD_WEEK, DAY.MONDAY, 0).getLabel());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	    
		setContentView(R.layout.main);
	}
}