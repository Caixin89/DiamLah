package Super.Ivle.Boy;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

/** For use by Simple XML to store xml timetable info
 * 
 *
 */

@Element
public class Timetable {
	
	@Attribute(name="day")
	public String day;

	@Attribute
	public int endtime;
	
	@Attribute
	public String module;
	
	@Attribute
	public int starttime;
	
	@Attribute
	public String type;
	
	@Attribute
	public String weektp;

}