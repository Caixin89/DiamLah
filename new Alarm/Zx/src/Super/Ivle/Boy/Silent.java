package Super.Ivle.Boy;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class Silent extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		SharedPreferences mode=PreferenceManager.getDefaultSharedPreferences(context);
		Bundle bundle=intent.getExtras();
		ScheduleProtected schedule=bundle.getParcelable("Schedule");
		String msg=schedule.getLabel();
		AudioManager audio=(AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		
		if(mode.getString("response", null)=="a")
			audio.setRingerMode(AudioManager.RINGER_MODE_SILENT);
		else
			audio.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
		
		NotificationManager mNotManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		String MyText = "Silent Mode Activated";
	    Notification mNotification = new Notification(R.drawable.icon, MyText, System.currentTimeMillis());
	    
	    String MyNotifyTitle = "Diam Leh";
        String MyNotifiyText  = msg+": Silent Mode Activated";
        Intent MyIntent = new Intent( context.getApplicationContext(), Main.class);
        
        MyIntent.putExtra("extendedTitle", MyNotifyTitle);
        MyIntent.putExtra("extendedText" , MyNotifiyText);
        PendingIntent StartIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, MyIntent, 0);
        
        mNotification.setLatestEventInfo(context.getApplicationContext(), MyNotifyTitle, MyNotifiyText, StartIntent);
        
        mNotification.ledOnMS  = 200;    //Set led blink (Off in ms)
        mNotification.ledOffMS = 200;    //Set led blink (Off in ms)
        mNotification.ledARGB = 0x9400d3;   //Set led color
        
        mNotManager.notify(399940 , mNotification );
        
		SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor=pref.edit();
		editor.putBoolean("Mode", true);
		editor.putLong("RowID", schedule.getRowId());
		editor.commit();
	}
}
