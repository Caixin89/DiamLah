package Super.Ivle.Boy;

import java.io.IOException;
import java.net.UnknownHostException;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

/** This class will run getTimeTable on another thread
 * 
 * 
 * @author Damien
 *
 */
public class getTimetableThreaded extends AsyncTask<String, Integer, Integer> {
	Context mCtx;
	
	//For
	public static final int matricNo = 0;
	public static final int acadYear = 1;
	public static final int sem      = 2;
	
	//Error codes
	public static final int NORMAL = 0;
	public static final int NO_INTERNET = 3;
	public static final int NO_VPN_ACCESS = 4;
	
	ProgressDialog pd = null;
	
	public getTimetableThreaded(Context ctx) {
		super();
		
		mCtx = ctx;
		
		pd = new ProgressDialog(mCtx);

	}
	
	/** Stop any UI notification, and print out any error*/
	@Override
	protected void onPostExecute(Integer result) {
		super.onPostExecute(result);
		
		switch (result)
		{
		case NO_VPN_ACCESS:
			break;
		case NO_INTERNET:
			break;
		default: pd.dismiss();
		}
		
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
	    	ProgressDialog.show(mCtx, "","Please wait for few seconds...", true);	
	}



	@Override
	protected Integer doInBackground(String... arg) {

	    GetTimetable get = new GetTimetable(arg[matricNo], arg[acadYear], Integer.parseInt(arg[sem]), mCtx);
	    
	    try {
			get.startDownload();
		} catch (UnknownHostException e) {
			return NO_VPN_ACCESS;
			
		} catch (IOException e) {
			return NO_INTERNET;
			
		}
		
	    get.storeInDB();
		return null;
	}
}