package Super.Ivle.Boy;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/** This Activity sets up the sms preferences.
 * 
 * 
 * @author Damien Tan
 *
 */
public class SmsActivity extends PreferenceActivity{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.sms);
    }
}