package Super.Ivle.Boy;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class MyWeek {
	private int year, mth;
	private Calendar c, cal, cal2;
	
	public MyWeek() {
		c=Calendar.getInstance();
		mth=c.get(Calendar.MONTH);
		year=c.get(Calendar.YEAR);
		
		cal=new GregorianCalendar();
		cal.set(year, Calendar.AUGUST, 1);
		
		while(cal.get(Calendar.DAY_OF_WEEK)!=2)
			cal.add(Calendar.DAY_OF_YEAR, 1);

		cal2=new GregorianCalendar();
		cal2.set(year, Calendar.JANUARY, 1);
		
		while(cal2.get(Calendar.DAY_OF_WEEK)!=2)
			cal2.add(Calendar.DAY_OF_YEAR, 1);
	}
	
	public int week_num() {
		int num2=0, diff;
		
		if(mth>=7) {
			diff=c.get(Calendar.DAY_OF_YEAR)-cal.get(Calendar.DAY_OF_YEAR);
		}
		else {
			diff=c.get(Calendar.DAY_OF_YEAR)-cal2.get(Calendar.DAY_OF_YEAR);
		}
		
		num2+=diff/7;
		
		return num2;
	}
	
	public int sem() {
		if (mth>=7)
			return 1;
		else
			return 2;
	}
	
	//Week 0: Orientation Week(Sem 1)
	//Week 7: Recess Week
	//Week 15: Reading Week
	//Week 16 & 17: Examination Week
	//Followed by 4-5 weeks of vacation
	//Active Weeks: 1-6, 8-14
}
