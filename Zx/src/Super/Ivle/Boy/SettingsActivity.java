package Super.Ivle.Boy;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity{

	public static final String Silent_Mode="a";
	public static final String Vibrate_mode="b";
	public static final String fifteen_minutes="1";
	public static final String thirty_minutes="2";
	public static final String fortyfive_minutes="3";
	public static final String one_hour="4";
	public static final String two_hours="5";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		setContentView(R.layout.settings);
	}

}
