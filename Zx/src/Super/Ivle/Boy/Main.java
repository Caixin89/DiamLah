package Super.Ivle.Boy;

import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;

public class Main extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	try {
	        super.onCreate(savedInstanceState);
	        
	        GetTimetable timetable = new GetTimetable("A0001234A", "2010-2011", 2, this);
	        
	        try {
				timetable.startDownload();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        timetable.storeInDB(this);
	        
	        Alarm alarm = new Alarm(this);
        
	        setContentView(R.layout.main);
    	}
    	
    	catch(Exception e) {
    		e.printStackTrace();
    	}
    }
}