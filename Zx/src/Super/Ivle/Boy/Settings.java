package Super.Ivle.Boy;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

public class Settings extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_settings_layout);
		
		final SharedPreferences sharedPrefs=PreferenceManager.getDefaultSharedPreferences(this);
		
		StringBuilder builder=new StringBuilder();
		
		builder.append("\n"+sharedPrefs.getString("user_id", "NULL"));
		builder.append("\n"+sharedPrefs.getString("response", "-1"));
		builder.append("\n"+sharedPrefs.getBoolean("show_notifications", true));
		
		TextView settingsTextView=(TextView) findViewById(R.id.settings_text_view);
		settingsTextView.setText(builder.toString());
		ToggleButton btn = (ToggleButton) findViewById(R.id.ToggleButton);
		final SharedPreferences.Editor editor=sharedPrefs.edit();
		editor.putBoolean("activation", true);
		editor.commit();
		
		btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(sharedPrefs.getBoolean("activation", true))
					editor.putBoolean("activation", false);
				else
					editor.putBoolean("activation", true);
				editor.commit();
			}
		});
		
	}
}
