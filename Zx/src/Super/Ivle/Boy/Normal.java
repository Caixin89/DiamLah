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

public class Normal extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
		Bundle bundle=intent.getExtras();
		ScheduleProtected schedule=bundle.getParcelable("Schedule");
		String msg=schedule.getLabel();
		AudioManager audio=(AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
		
		NotificationManager mNotManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		String MyText = "Silent Mode Deactivated";
	    Notification mNotification = new Notification(R.drawable.icon, MyText, System.currentTimeMillis());
	    
	    String MyNotifyTitle = "Diam Leh";
        String MyNotifiyText  = msg+": Silent Mode Deactivated";
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
		editor.putBoolean("Mode", false);
		editor.putLong("RowID", schedule.getRowId());
		editor.commit();
	}
}
