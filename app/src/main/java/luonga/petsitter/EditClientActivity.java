package luonga.petsitter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

/**
 * Activity class for edit client screen.
 *
 * @author Linda Zuvich
 * @version 12/21/2018
 */
public class EditClientActivity extends BaseActivity {

    //fields
    private DataManager dm;
    private int clientId;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EventListener<DocumentSnapshot> dataListener;
    private ListenerRegistration reg;
    private EditText txtLastName;
    private EditText txtFirstName;
    private EditText txtCell;
    private EditText txtEmail;

    /**
     * Android onCreate method.
     *
     * @param savedInstanceState the class state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_client);
        //create action bar and back arrow
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Intent intent = getIntent();
        clientId = intent.getIntExtra(Extras.CLIENT_ID, -1);
        setTitle(R.string.titleEditClient);
        if (clientId < 0) {
            setTitle(R.string.titleAddClient);
        }
        txtLastName = findViewById(R.id.txtLastName);
        txtFirstName = findViewById(R.id.txtFirstName);
        txtCell = findViewById(R.id.txtCell);
        txtEmail = findViewById(R.id.txtEmail);
    }

    /**
     * On pause, data listener is stopped.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (reg != null && dataListener != null) {
            reg.remove();
        }
    }

    /**
     * Connects up the data listener once authentication is completed in the BaseActivity.
     */
    @Override
    public void setUpDataListeners() {
        dm = DataManager.getDataManager(getApplicationContext(), userId);
        if (clientId >= 0) {
            final DocumentReference ref = db.collection("users").document(userId).
                    collection("clients").document(String.valueOf(clientId));
            dataListener = new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w("MYLOG", "Client listener failed.", e);
                        return;
                    }
                    if (snapshot != null && snapshot.exists()) {
                        Client thisClient = snapshot.toObject(Client.class);
                        if (thisClient != null) {
                            dm.setClient(thisClient);
                            txtLastName.setText(thisClient.getLastName());
                            txtFirstName.setText(thisClient.getFirstName());
                            txtCell.setText(thisClient.getCell());
                            txtEmail.setText(thisClient.getEmail());
                        }
                    }
                }
            };
            reg = ref.addSnapshotListener(dataListener);
        }
    }

    /**
     * Creates the top menu.
     *
     * @param menu the top menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
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
            case R.id.action_sign_out:
                signOut();
                return true;
            case R.id.action_done:
                saveClient();
                return true;
            case android.R.id.home:
                //back arrow
                finish();
                return true;
            case R.id.action_settings:
                //settings menu option
                Intent intent = new Intent(this, PreferencesActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Save current click data.
     */
    private void saveClient() {
        //make sure important fields are filled
        String entry1 = txtLastName.getText().toString();
        String entry2 = txtFirstName.getText().toString();
        if (TextUtils.isEmpty(entry1) && TextUtils.isEmpty(entry2)) {
            txtLastName.setError("Last name or first name is required.");
            return;
        }
        entry1 = PhoneNumberUtils.stripSeparators(txtCell.getText().toString());
        if (entry1.length() < 7 || !android.util.Patterns.PHONE.matcher(entry1).matches()) {
            txtCell.setError("Valid phone number is required.");
            return;
        }
        //set up client object
        Client c = new Client();
        //set vet fields
        c.setLastName(txtLastName.getText().toString());
        c.setFirstName(txtFirstName.getText().toString());
        c.setCell(txtCell.getText().toString());
        c.setEmail(txtEmail.getText().toString());
        //if new, save to list and return id
        if (clientId < 0) {
            clientId = dm.addClient(c);
            //intent set up for returning to EditPetActivity
            Intent resultIntent = new Intent();
            resultIntent.putExtra(Extras.CLIENT_ID, clientId);
            setResult(RESULT_OK, resultIntent);
        } else {
            c.setClientId(clientId);
            dm.replaceClient(c);
        }
        //close
        finish();
    }

}
