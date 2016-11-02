package Super.Ivle.Boy;

import java.util.List;

import org.simpleframework.xml.*;

/** For use by Simple XML to store xml timetable info
 * 
 *
 */
@Root(name="response")
public class TimetableStorage {
	
	@ElementList
	public List<Timetable> data;
	
}
