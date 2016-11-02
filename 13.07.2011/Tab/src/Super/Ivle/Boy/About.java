package Super.Ivle.Boy;

import android.app.Activity;
import android.os.Bundle;
import android.view.*;

/** Activity that shows when the About button is clicked
 *  on the settings menu
 * 
 * @author Damien Tan
 *
 */
public class About extends Activity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.about);
	}

	//OPTION MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
    	menu.add(0,1,1,R.string.back).setIcon(android.R.drawable.ic_menu_revert);
    	return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()){ 
        case 1: finish();
        		return true; 
        } 
        return false; 
    }
}
