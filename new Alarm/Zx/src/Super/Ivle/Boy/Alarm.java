package Super.Ivle.Boy;

import java.util.Calendar;

import Super.Ivle.Boy.Schedule.DAY;
import Super.Ivle.Boy.Schedule.FREQ;
import Super.Ivle.Boy.ScheduleAdapter.MODE;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class Alarm {

	private Calendar c, now, temp;
	private int today, flag, flag2, starttime, endtime;
	private DAY day;
	private AlarmManager alarm;
	private ScheduleAdapter adapter;
	private Context context;
	private ScheduleProtected schedule;
	
	public Alarm(Context x) {
		
		context=x;
		MyWeek wk=new MyWeek();
		int num=wk.week_num();
		flag2=flag=-1;
		adapter=new ScheduleAdapter(context, MODE.ALARM);
		alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		
		while (num!=0 && num!=7 && num<15) {
		
			if(num%2>0) 
				odd_week();
			
			else 
				even_week();
			
			wk=new MyWeek();
			num=wk.week_num();
		}
	}
	
	private void odd_week() {
		now=Calendar.getInstance();
		c=(Calendar) now.clone();
		temp=(Calendar) now.clone();
		
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.MILLISECOND, 0);
		temp.set(Calendar.MINUTE, 0);
		temp.set(Calendar.MILLISECOND, 0);
		
		today=now.get(Calendar.DAY_OF_WEEK);
		
		switch(today) {
		case 1:
			day=DAY.SUNDAY;
			break;
		case 2:
			day=DAY.MONDAY;
			break;
		case 3:
			day=DAY.TUESDAY;
			break;
		case 4:
			day=DAY.WEDNESDAY;
			break;
		case 5:
			day=DAY.THURSDAY;
			break;
		case 6:
			day=DAY.FRIDAY;
			break;
		default:
			day=DAY.SATURDAY;
			break;
		}	
		
		int size=adapter.size(FREQ.ODD_WEEK, day);
		
		for(int i=0;i<size;++i)
		{
			ScheduleProtected tmp = adapter.get(FREQ.ODD_WEEK, day, i);
			endtime=tmp.getEndtime();
			
			c.set(Calendar.HOUR_OF_DAY, endtime/100);
			c.set(Calendar.MINUTE, endtime%100);
			
			if(i==0) {
				temp.set(Calendar.HOUR_OF_DAY, endtime/100);
				temp.set(Calendar.MINUTE, endtime%100);
				schedule=tmp;
			}
			
			if(c.after(now) && c.before(temp)) {
				temp.set(Calendar.HOUR_OF_DAY, endtime/100);
				temp.set(Calendar.MINUTE, endtime%100);
				schedule=tmp;
			}
		}
		
		int size2=adapter.size(FREQ.EVERY_WEEK, day);
		
		for(int i=0;i<size2;++i)
		{
			ScheduleProtected tmp = adapter.get(FREQ.EVERY_WEEK, day, i);
			endtime=tmp.getEndtime();
			
			c.set(Calendar.HOUR_OF_DAY, endtime/100);
			c.set(Calendar.MINUTE, endtime%100);
			
			if(size==0 && i==0) {
				temp.set(Calendar.HOUR_OF_DAY, endtime/100);
				temp.set(Calendar.MINUTE, endtime%100);
				schedule=tmp;
			}
			
			if(c.after(now) && c.before(temp)) {
				temp.set(Calendar.HOUR_OF_DAY, endtime/100);
				temp.set(Calendar.MINUTE, endtime%100);
				schedule=tmp;
			}
		}
	
		starttime=schedule.getStarttime();
		
		if(flag!=starttime || flag2!=today) {
			c.set(Calendar.HOUR_OF_DAY, starttime/100);
			c.set(Calendar.MINUTE, starttime%100);
			Intent intent=new Intent("super.ivle.boy.silent");
			intent.putExtra("Schedule", schedule);
			PendingIntent pi=PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
			alarm.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);
		
			endtime=schedule.getEndtime();
			c.set(Calendar.HOUR_OF_DAY, endtime/100);
			c.set(Calendar.MINUTE, endtime%100);
			intent=new Intent("super.ivle.boy.normal");
			intent.putExtra("Schedule", schedule);
			pi=PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
			alarm.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);
			flag=starttime;
			flag2=today;
		}
	}
	
	private void even_week() {
		now=Calendar.getInstance();
		c=(Calendar) now.clone();
		temp=(Calendar) now.clone();
		
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.MILLISECOND, 0);
		temp.set(Calendar.MINUTE, 0);
		temp.set(Calendar.MILLISECOND, 0);
		
		today=now.get(Calendar.DAY_OF_WEEK);
		
		switch(today) {
		case 1:
			day=DAY.SUNDAY;
			break;
		case 2:
			day=DAY.MONDAY;
			break;
		case 3:
			day=DAY.TUESDAY;
			break;
		case 4:
			day=DAY.WEDNESDAY;
			break;
		case 5:
			day=DAY.THURSDAY;
			break;
		case 6:
			day=DAY.FRIDAY;
			break;
		default:
			day=DAY.SATURDAY;
			break;
		}	
		
		int size=adapter.size(FREQ.EVEN_WEEK, day);
		
		for(int i=0;i<size;++i)
		{
			ScheduleProtected tmp = adapter.get(FREQ.EVEN_WEEK, day, i);
			endtime=tmp.getEndtime();
			
			c.set(Calendar.HOUR_OF_DAY, endtime/100);
			c.set(Calendar.MINUTE, endtime%100);
			
			if(i==0) {
				temp.set(Calendar.HOUR_OF_DAY, endtime/100);
				temp.set(Calendar.MINUTE, endtime%100);
				schedule=tmp;
			}
			
			if(c.after(now) && c.before(temp)) {
				temp.set(Calendar.HOUR_OF_DAY, endtime/100);
				temp.set(Calendar.MINUTE, endtime%100);
				schedule=tmp;
			}
		}
		
		int size2=adapter.size(FREQ.EVERY_WEEK, day);
		
		for(int i=0;i<size2;++i)
		{
			ScheduleProtected tmp = adapter.get(FREQ.EVERY_WEEK, day, i);
			endtime=tmp.getEndtime();
			
			c.set(Calendar.HOUR_OF_DAY, endtime/100);
			c.set(Calendar.MINUTE, endtime%100);
			
			if(size==0 && i==0) {
				temp.set(Calendar.HOUR_OF_DAY, endtime/100);
				temp.set(Calendar.MINUTE, endtime%100);
				schedule=tmp;
			}
			
			if(c.after(now) && c.before(temp)) {
				temp.set(Calendar.HOUR_OF_DAY, endtime/100);
				temp.set(Calendar.MINUTE, endtime%100);
				schedule=tmp;
			}
		}
		
		starttime=schedule.getStarttime();
		
		if(flag!=starttime || flag2!=today) {
			c.set(Calendar.HOUR_OF_DAY, starttime/100);
			c.set(Calendar.MINUTE, starttime%100);
			Intent intent=new Intent("super.ivle.boy.silent");
			intent.putExtra("Schedule", schedule);
			PendingIntent pi=PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
			alarm.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);
			
			endtime=schedule.getEndtime();
			c.set(Calendar.HOUR_OF_DAY, endtime/100);
			c.set(Calendar.MINUTE, endtime%100);
			intent=new Intent("super.ivle.boy.normal");
			intent.putExtra("Schedule", schedule);
			pi=PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
			alarm.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);
			flag=starttime;
			flag2=today;
		}
	}
}
