package luonga.petsitter;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.RadioButton;

/**
 * Activity class for preferences screen.
 *
 * @author Linda Zuvich
 * @version 12/21/2018
 */
public class PreferencesActivity extends AppCompatActivity {

    //fields
    private PreferencesManager pm;
    private CheckBox chkBreed;
    private RadioButton rbtnAZ;
    private CheckBox chkWarnDeletePet;
    private CheckBox chkWarnDeleteVet;
    private CheckBox chkWarnDeleteClient;

    /**
     * Android onClick method.
     *
     * @param savedInstanceState the class state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        //create action bar and back arrow
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        //set up preferences screen
        pm = PreferencesManager.getInstance(getApplicationContext());
        chkBreed = findViewById(R.id.chkBreed);
        rbtnAZ = findViewById(R.id.rbtnAZ);
        RadioButton rbtnZA = findViewById(R.id.rbtnZA);
        chkWarnDeletePet = findViewById(R.id.chkWarnDeletePet);
        chkWarnDeleteVet = findViewById(R.id.chkWarnDeleteVet);
        chkWarnDeleteClient = findViewById(R.id.chkWarnDeleteClient);
        chkBreed.setChecked(pm.isListBreed());
        if (pm.isSortAZ()) {
            rbtnAZ.setChecked(true);
        } else {
            rbtnZA.setChecked(true);
        }
        chkWarnDeletePet.setChecked(pm.isWarnBeforeDeletingPet());
        chkWarnDeleteVet.setChecked(pm.isWarnBeforeDeletingVet());
        chkWarnDeleteClient.setChecked(pm.isWarnBeforeDeletingClient());
    }

    /**
     * Creates the top menu.
     *
     * @param menu the top menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return true;
    }

    /**
     * Handles the top menu item selection.
     *
     * @param item the item selected
     * @return true or false
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                savePreferences();
                return true;
            case android.R.id.home:
                //back arrow
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Saves the current preferences.
     */
    private void savePreferences() {
        pm.setListBreed(chkBreed.isChecked());
        pm.setSortAZ(rbtnAZ.isChecked());
        pm.setWarnBeforeDeletingPet(chkWarnDeletePet.isChecked());
        pm.setWarnBeforeDeletingVet(chkWarnDeleteVet.isChecked());
        pm.setWarnBeforeDeletingClient(chkWarnDeleteClient.isChecked());
        finish();
    }

}
