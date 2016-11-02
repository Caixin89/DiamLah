package Super.Ivle.Boy;

import java.util.Calendar;
import Super.Ivle.Boy.Schedule.*;
import Super.Ivle.Boy.ScheduleAdapter.MODE;
import android.app.*;
import android.content.*;
import android.preference.PreferenceManager;

public class Alarm {

	private Calendar c, time_now, c2;
	private int today, num, count;
	private AlarmManager alarm;
	private ScheduleAdapter adapter;
	private Context context;
	private SharedPreferences pref;
	private int sleep_hour, sleep_min, wake_hour, wake_min;
	
	//Constructor to initialize all global variables.
	public Alarm(Context x) {
		context = x;
		alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		pref=PreferenceManager.getDefaultSharedPreferences(context);
	}
	
	//Activate the alarm
	public void activate() {
		count=pref.getInt("count", 0);
		
		if (count != 0)
			deactivate();
		
		adapter=new ScheduleAdapter(context, MODE.EDIT);
		
		MyWeek wk=new MyWeek();
		num=wk.week_num();
		
		time_now=Calendar.getInstance();
		c=(Calendar) time_now.clone();
		c2=(Calendar) time_now.clone();
			
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		
		c2.set(Calendar.SECOND, 0);
		c2.set(Calendar.MILLISECOND, 0);
		
		today=c.get(Calendar.DAY_OF_WEEK);
		
		
		
		if(num%2>0) 
			week(FREQ.ODD_WEEK, FREQ.EVEN_WEEK);
			
		else 
			week(FREQ.EVEN_WEEK, FREQ.ODD_WEEK);
		
		sleep_mode();
		
		SharedPreferences.Editor editor=pref.edit();
		editor.putInt("count", count);
		editor.commit();
	}
	
	//Initialize intents and pending intents
	private void set_intents(ScheduleProtected tmp, ScheduleProtected tmp2) {
		int starttime=tmp.getStarttime(), endtime=tmp.getEndtime(), starttime2=tmp2.getStarttime();
		
		
		c.set(Calendar.HOUR_OF_DAY, starttime/100);
		c.set(Calendar.MINUTE, starttime%100);
		c2.set(Calendar.HOUR_OF_DAY, endtime/100);
		c2.set(Calendar.MINUTE, endtime%100);
		
		if(!c2.after(time_now))
			return;
		
		//Check for sleep time feature
		//If overlap, do not set the schedule
		
		
		Intent intent1=new Intent("super.ivle.boy.silent");
		intent1.putExtra("Schedule", tmp);
		
		PendingIntent pi1= PendingIntent.getBroadcast(context, count++, intent1, PendingIntent.FLAG_CANCEL_CURRENT);
		
		alarm.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), 1209600000, pi1);
		
		if(starttime2!=endtime) {
			Intent intent2=new Intent("super.ivle.boy.normal");
		
			PendingIntent pi2=PendingIntent.getBroadcast(context, count++, intent2, PendingIntent.FLAG_CANCEL_CURRENT);
			
			alarm.setRepeating(AlarmManager.RTC_WAKEUP, c2.getTimeInMillis(), 1209600000, pi2);
		}
	}
	
	
	private void set_intents2(ScheduleProtected tmp, ScheduleProtected tmp2, int j) {
		int starttime=tmp.getStarttime(), endtime=tmp.getEndtime(), starttime2=tmp2.getStarttime(), compare=time_now.get(Calendar.HOUR_OF_DAY)*100+time_now.get(Calendar.MINUTE);
		
		c.set(Calendar.HOUR_OF_DAY, starttime/100);
		c.set(Calendar.MINUTE, starttime%100);
		c2.set(Calendar.HOUR_OF_DAY, endtime/100);
		c2.set(Calendar.MINUTE, endtime%100);
		
		if(j==today && starttime<compare)
			return;
		
		Intent intent1=new Intent("super.ivle.boy.silent");
		intent1.putExtra("Schedule", tmp);
		
		PendingIntent pi1= PendingIntent.getBroadcast(context, count++, intent1, PendingIntent.FLAG_CANCEL_CURRENT);
		
		alarm.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), 1209600000, pi1);
		
		if(starttime2!=endtime) {
			Intent intent2=new Intent("super.ivle.boy.normal");
		
			PendingIntent pi2=PendingIntent.getBroadcast(context, count++, intent2, PendingIntent.FLAG_CANCEL_CURRENT);
			
			alarm.setRepeating(AlarmManager.RTC_WAKEUP, c2.getTimeInMillis(), 1209600000, pi2);
		}
	}
	
	private DAY getDay(int j) {
		switch(j) {
		case 1:
			return DAY.SUNDAY;
		case 2:
			return DAY.MONDAY;
		case 3:
			return DAY.TUESDAY;
		case 4:
			return DAY.WEDNESDAY;
		case 5:
			return DAY.THURSDAY;
		case 6:
			return DAY.FRIDAY;
		default:
			return DAY.SATURDAY;
		}	
	}
	
	//Used for odd week and even week.
	//First parameter is for the current week and second parameter is for next week.
	private void week(FREQ currweek, FREQ nextweek) {
		//Set current week's schedule
		for(int j=today;j<=7;++j) {
			DAY day=getDay(j);
			
			int size=adapter.size(currweek, day); 						   
			
			for(int i=0;i<size;++i)
			{
				ScheduleProtected tmp = adapter.get(currweek, day, i);
				
				if(i+1<size) {
					ScheduleProtected tmp2=adapter.get(currweek, day, i+1);
					set_intents(tmp, tmp2);
				}
				else
					set_intents(tmp, tmp);
			}
			
			
			c.add(Calendar.DAY_OF_YEAR, 1);
			c2.add(Calendar.DAY_OF_YEAR, 1);
		}
		
		//Set next week's schedule
		for(int j=1;j<=7;++j) {
			DAY day=getDay(j);
			
			int size=adapter.size(nextweek, day);
			
			for(int i=0;i<size;++i)
			{
				ScheduleProtected tmp = adapter.get(nextweek, day, i);
				
				if(i+1<size) {
					ScheduleProtected tmp2=adapter.get(nextweek, day, i+1);
					set_intents(tmp, tmp2);
				}
				else
					set_intents(tmp, tmp);
			}
			c.add(Calendar.DAY_OF_YEAR, 1);
			c2.add(Calendar.DAY_OF_YEAR, 1);
		}
		
		for(int j=1; j<=today; ++j) {
			DAY day=getDay(j);
			
			int size=adapter.size(currweek, day);
			
			for(int i=0;i<size;++i)
			{
				ScheduleProtected tmp = adapter.get(currweek, day, i);
				
				if(i+1<size) {
					ScheduleProtected tmp2=adapter.get(currweek, day, i+1);
					set_intents2(tmp, tmp2, j);
				}
				else
					set_intents2(tmp, tmp, j);
			}
			c.add(Calendar.DAY_OF_YEAR, 1);
			c2.add(Calendar.DAY_OF_YEAR, 1);
		}
	}
	
	//Deactivate the alarm
	public void deactivate() {
		Intent intent1=new Intent("super.ivle.boy.silent");
		Intent intent2=new Intent("super.ivle.boy.normal");
		
		count = pref.getInt("count", 0);
		
		for(int i=0;i<count;i++) {
			PendingIntent pi1= PendingIntent.getBroadcast(context, i, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
			PendingIntent pi2=PendingIntent.getBroadcast(context, i, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
			PendingIntent pi3= PendingIntent.getBroadcast(context, i, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
			PendingIntent pi4= PendingIntent.getBroadcast(context, i, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
			
			pi1.cancel();
			alarm.cancel(pi1);
			pi2.cancel();
			alarm.cancel(pi2);
			pi3.cancel();
			alarm.cancel(pi2);
			pi4.cancel();
			alarm.cancel(pi2);
		}
		
		SharedPreferences.Editor editor=pref.edit();
		editor.putInt("count", 0);
		editor.commit();
		
		count = 0;
	}
	
    private static int getHour(String time) {
        String[] pieces=time.split(":");

        return(Integer.parseInt(pieces[0]));
    }

    private static int getMinute(String time) {
        String[] pieces=time.split(":");

        return(Integer.parseInt(pieces[1]));
    }
    
    static final long dayInMillis = AlarmManager.INTERVAL_HOUR * 24;
	
	private void sleep_mode() {
		if(pref.getBoolean("sleep_mode", false)==true) {
			Intent intent1=new Intent("super.ivle.boy.silent"), intent2=new Intent("super.ivle.boy.normal");
			intent1.putExtra("sleep", true);
			intent2.putExtra("sleep", true);
			PendingIntent pi1=PendingIntent.getBroadcast(context, count++, intent1, PendingIntent.FLAG_CANCEL_CURRENT);
			PendingIntent pi2=PendingIntent.getBroadcast(context, count++, intent2, PendingIntent.FLAG_CANCEL_CURRENT);
			
			String sleeptime=pref.getString("sleep", "00:00"), waketime=pref.getString("wake-up", "00:00");
			sleep_hour=getHour(sleeptime);
			sleep_min=getMinute(sleeptime);
			wake_hour=getHour(waketime);
			wake_min=getMinute(waketime);
			
			c=(Calendar) time_now.clone();
			c2=(Calendar) time_now.clone();
			
			c.set(Calendar.HOUR_OF_DAY, sleep_hour);
			c.set(Calendar.MINUTE, sleep_min);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
			c2.set(Calendar.HOUR_OF_DAY, wake_hour);
			c2.set(Calendar.MINUTE, wake_min);
			c2.set(Calendar.SECOND, 0);
			c2.set(Calendar.MILLISECOND, 0);
			
			if(c2.before(c))
				c2.add(Calendar.DAY_OF_YEAR,1);
		
			
			alarm.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), dayInMillis, pi1);
			alarm.setRepeating(AlarmManager.RTC_WAKEUP, c2.getTimeInMillis(), dayInMillis, pi2);
		}
	}
}
