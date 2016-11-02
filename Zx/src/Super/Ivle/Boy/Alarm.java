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

	private Calendar c, temp, c2;
	private int today, flag;
	private DAY day;
	private AlarmManager alarm;
	private ScheduleAdapter adapter;
	private Intent intent1, intent2;
	private Context context;
	
	public Alarm(Context x) {
		context=x;
		MyWeek wk=new MyWeek();
		int num=wk.week_num();
		
		flag=0;
		adapter=new ScheduleAdapter(context, MODE.ALARM);
		
		alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		
		temp=Calendar.getInstance();
		c=(Calendar) temp.clone();
		c2=(Calendar) temp.clone();
			
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		
		c2.set(Calendar.SECOND, 0);
		c2.set(Calendar.MILLISECOND, 0);
		
		today=c.get(Calendar.DAY_OF_WEEK);

		if(num%2>0) 
			odd_week();
		
			
		else 
			even_week();
		
		every_week();
	}
	
	private void odd_week() {
		for(int j=today;j<=7;++j) {
			switch(j) {
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
			
			int size=adapter.size(FREQ.ODD_WEEK, day); // Damien's unimplemented part on number
													   // of lessons per day
			
			for(int i=0;i<size;++i)
			{
				ScheduleProtected tmp = adapter.get(FREQ.ODD_WEEK, day, i);
				int starttime=tmp.getStarttime(), endtime=tmp.getEndtime();
				intent1=new Intent("super.ivle.boy.silent");
				intent2=new Intent("super.ivle.boy.normal");
				
				c.set(Calendar.HOUR_OF_DAY, starttime/100);
				c.set(Calendar.MINUTE, starttime%100);
				
				c2.set(Calendar.HOUR_OF_DAY, endtime/100);
				c2.set(Calendar.MINUTE, endtime%100);
				
				intent1.putExtra("Schedule", tmp);
				intent2.putExtra("Schedule", tmp);
				PendingIntent pi1= PendingIntent.getBroadcast(context, 0, intent1, PendingIntent.FLAG_CANCEL_CURRENT), pi2=PendingIntent.getBroadcast(context, 0, intent2, PendingIntent.FLAG_CANCEL_CURRENT);
				
				if(j==today && c.before(temp) && c2.before(temp)) {
					
					if(i==size-1) {
						int size2=adapter.size(FREQ.EVERY_WEEK, day);
						Calendar c3=(Calendar) c.clone();
						
						if(size2>0) {
							ScheduleProtected tmp2=adapter.get(FREQ.EVERY_WEEK, day, size2-1);
							int endtime2=tmp2.getEndtime();
							c3.set(Calendar.HOUR_OF_DAY, endtime2/100);
							c3.set(Calendar.MINUTE, endtime2%100);
						}
							
						if(size2==0 || c3.before(c2)) {
							alarm.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), pi2);
							++flag;
						}
					}
				continue;
				}
				
				alarm.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), 1209600000, pi1);
				alarm.setRepeating(AlarmManager.RTC_WAKEUP, c2.getTimeInMillis(), 1209600000, pi2);
			}
			c.add(Calendar.DAY_OF_YEAR, 1);
			c2.add(Calendar.DAY_OF_YEAR, 1);
		}
		
		for(int j=1;j<=7;++j) {
			switch(j) {
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
				int starttime=tmp.getStarttime(), endtime=tmp.getEndtime();
				intent1=new Intent("super.ivle.boy.silent");
				intent2=new Intent("super.ivle.boy.normal");
				
				c.set(Calendar.HOUR_OF_DAY, starttime/100);
				c.set(Calendar.MINUTE, starttime%100);
				
				c2.set(Calendar.HOUR_OF_DAY, endtime/100);
				c2.set(Calendar.MINUTE, endtime%100);
				
				intent1.putExtra("Schedule", tmp);
				intent2.putExtra("Schedule", tmp);
				PendingIntent pi1= PendingIntent.getBroadcast(context, 0, intent1, PendingIntent.FLAG_CANCEL_CURRENT), pi2=PendingIntent.getBroadcast(context, 0, intent2, PendingIntent.FLAG_CANCEL_CURRENT);
				alarm.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), 1209600000, pi1);
				alarm.setRepeating(AlarmManager.RTC_WAKEUP, c2.getTimeInMillis(), 1209600000, pi2);
			}
			c.add(Calendar.DAY_OF_YEAR, 1);
			c2.add(Calendar.DAY_OF_YEAR, 1);
		}
	}
	
	private void even_week() {
		for(int j=today;j<=7;++j) {
			switch(j) {
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
			
			int size=adapter.size(FREQ.EVEN_WEEK, day); // Damien's unimplemented part on number
			   // of lessons per day

			for(int i=0;i<size;++i)
			{
				ScheduleProtected tmp = adapter.get(FREQ.EVEN_WEEK, day, i);
				int starttime=tmp.getStarttime(), endtime=tmp.getEndtime();
				intent1=new Intent("super.ivle.boy.silent");
				intent2=new Intent("super.ivle.boy.normal");
				
				c.set(Calendar.HOUR_OF_DAY, starttime/100);
				c.set(Calendar.MINUTE, starttime%100);
				
				c2.set(Calendar.HOUR_OF_DAY, endtime/100);
				c2.set(Calendar.MINUTE, endtime%100);
				
				intent1.putExtra("Schedule", tmp);
				intent2.putExtra("Schedule", tmp);
				PendingIntent pi1= PendingIntent.getBroadcast(context, 0, intent1, PendingIntent.FLAG_CANCEL_CURRENT), pi2=PendingIntent.getBroadcast(context, 0, intent2, PendingIntent.FLAG_CANCEL_CURRENT);
				
				if(j==today && c.before(temp) && c2.before(temp)) {
					
					if(i==size-1) {
						int size2=adapter.size(FREQ.EVERY_WEEK, day);
						Calendar c3=(Calendar) c.clone();
						
						if(size2>0) {
							ScheduleProtected tmp2=adapter.get(FREQ.EVERY_WEEK, day, size2-1);
							int endtime2=tmp2.getEndtime();
							c3.set(Calendar.HOUR_OF_DAY, endtime2/100);
							c3.set(Calendar.MINUTE, endtime2%100);
						}
						
						if(size2==0 || c3.before(c2)) {
							alarm.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), pi2);
							++flag;
						}
					}
					continue;
				}
				
				alarm.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), 1209600000, pi1);
				alarm.setRepeating(AlarmManager.RTC_WAKEUP, c2.getTimeInMillis(), 1209600000, pi2);	
			}
			c.add(Calendar.DAY_OF_YEAR, 1);
			c2.add(Calendar.DAY_OF_YEAR, 1);
		}
		
		for(int j=1;j<=7;++j) {
			switch(j) {
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
			
			int size=adapter.size(FREQ.ODD_WEEK, day); // Damien's unimplemented part on number
													   // of lessons per day
			
			for(int i=0;i<size;++i)
			{
				ScheduleProtected tmp = adapter.get(FREQ.ODD_WEEK, day, i);
				int starttime=tmp.getStarttime(), endtime=tmp.getEndtime();
				intent1=new Intent("super.ivle.boy.silent");
				intent2=new Intent("super.ivle.boy.normal");
				
				c.set(Calendar.HOUR_OF_DAY, starttime/100);
				c.set(Calendar.MINUTE, starttime%100);
				
				c2.set(Calendar.HOUR_OF_DAY, endtime/100);
				c2.set(Calendar.MINUTE, endtime%100);
				
				intent1.putExtra("Schedule", tmp);
				intent2.putExtra("Schedule", tmp);
				PendingIntent pi1= PendingIntent.getBroadcast(context, 0, intent1, PendingIntent.FLAG_CANCEL_CURRENT), pi2=PendingIntent.getBroadcast(context, 0, intent2, PendingIntent.FLAG_CANCEL_CURRENT);						
				alarm.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), 1209600000, pi1);
				alarm.setRepeating(AlarmManager.RTC_WAKEUP, c2.getTimeInMillis(), 1209600000, pi2);
			}
			c.add(Calendar.DAY_OF_YEAR, 1);
			c2.add(Calendar.DAY_OF_YEAR, 1);
		}
	}
	
	private void every_week() {
		c=(Calendar) temp.clone();
		c2=(Calendar) temp.clone();
		
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		
		c2.set(Calendar.SECOND, 0);
		c2.set(Calendar.MILLISECOND, 0);
		
		for(int j=today;j<=7;++j) {
			switch(j) {
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
			
			int size=adapter.size(FREQ.EVERY_WEEK, day); // Damien's unimplemented part on number
			   // of lessons per day

			for(int i=0;i<size;++i)
			{
				ScheduleProtected tmp = adapter.get(FREQ.EVERY_WEEK, day, i);
				int starttime=tmp.getStarttime(), endtime=tmp.getEndtime();
				intent1=new Intent("super.ivle.boy.silent");
				intent2=new Intent("super.ivle.boy.normal");
				
				c.set(Calendar.HOUR_OF_DAY, starttime/100);
				c.set(Calendar.MINUTE, starttime%100);
				
				c2.set(Calendar.HOUR_OF_DAY, endtime/100);
				c2.set(Calendar.MINUTE, endtime%100);
				
				intent1.putExtra("Schedule", tmp);
				intent2.putExtra("Schedule", tmp);
				PendingIntent pi1= PendingIntent.getBroadcast(context, 0, intent1, PendingIntent.FLAG_CANCEL_CURRENT), pi2=PendingIntent.getBroadcast(context, 0, intent2, PendingIntent.FLAG_CANCEL_CURRENT);
				
				if(j==today && c.before(temp) && c2.before(temp)) {
					if(i==size-1 && flag==0)
						alarm.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), pi2);
					continue;
				}
				
				alarm.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), 604800000, pi1);
				alarm.setRepeating(AlarmManager.RTC_WAKEUP, c2.getTimeInMillis(), 604800000, pi2);
			}
			c.add(Calendar.DAY_OF_WEEK, 1);
			c2.add(Calendar.DAY_OF_WEEK, 1);
		}
	}
}
