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
 * Activity class for edit vet screen.
 *
 * @author Linda Zuvich
 * @version 12/21/2018
 */
public class EditVetActivity extends BaseActivity {

    //fields
    private DataManager dm;
    private int vetId;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EventListener<DocumentSnapshot> dataListener;
    private ListenerRegistration reg;
    private EditText txtLastName;
    private EditText txtFirstName;
    private EditText txtClinic;
    private EditText txtPhone;
    private EditText txtAddress;

    /**
     * Android onCreate method.
     *
     * @param savedInstanceState the class state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_vet);
        //create action bar and back arrow
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Intent intent = getIntent();
        vetId = intent.getIntExtra(Extras.VET_ID, -1);
        setTitle(R.string.titleEditVet);
        if (vetId < 0) {
            setTitle(R.string.titleAddVet);
        }
        txtLastName = findViewById(R.id.txtLastName);
        txtFirstName = findViewById(R.id.txtFirstName);
        txtClinic = findViewById(R.id.txtClinic);
        txtPhone = findViewById(R.id.txtPhone);
        txtAddress = findViewById(R.id.txtAddress);
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
     * Sets up the data listener for the vet object after authentication is completed.
     */
    @Override
    public void setUpDataListeners() {
        dm = DataManager.getDataManager(getApplicationContext(), userId);
        if (vetId >= 0) {
            final DocumentReference ref = db.collection("users").document(userId).
                    collection("vets").document(String.valueOf(vetId));
            dataListener = new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w("MYLOG", "Vet listener failed.", e);
                        return;
                    }
                    if (snapshot != null && snapshot.exists()) {
                        Vet thisVet = snapshot.toObject(Vet.class);
                        if (thisVet != null) {
                            dm.setVet(thisVet);
                            txtLastName.setText(thisVet.getLastName());
                            txtFirstName.setText(thisVet.getFirstName());
                            txtClinic.setText(thisVet.getClinic());
                            txtPhone.setText(thisVet.getPhone());
                            txtAddress.setText(thisVet.getAddress());
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
        getMenuInflater().inflate(R.menu.menu_done, menu);
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    /**
     * Handles the top menu item selection.
     *
     * @param item the item selected
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sign_out:
                signOut();
                return true;
            case R.id.action_done:
                saveVet();
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
     * Save the current vet data.
     */
    private void saveVet() {
        //make sure important fields are filled
        String entry1 = txtLastName.getText().toString();
        String entry2 = txtClinic.getText().toString();
        if (TextUtils.isEmpty(entry1) && TextUtils.isEmpty(entry2)) {
            txtLastName.setError("Last name or clinic is required.");
            return;
        }
        entry1 = PhoneNumberUtils.stripSeparators(txtPhone.getText().toString());
        if (entry1.length() < 7 || !android.util.Patterns.PHONE.matcher(entry1).matches()) {
            txtPhone.setError("Valid phone number is required.");
            return;
        }
        //set up vet object
        Vet v = new Vet();
        //set vet fields
        v.setLastName(txtLastName.getText().toString());
        v.setFirstName(txtFirstName.getText().toString());
        v.setClinic(txtClinic.getText().toString());
        v.setPhone(txtPhone.getText().toString());
        v.setAddress(txtAddress.getText().toString());
        //if new, save to list and return id
        if (vetId < 0) {
            vetId = dm.addVet(v);
            //intent set up for returning to EditPetActivity
            Intent resultIntent = new Intent();
            resultIntent.putExtra(Extras.VET_ID, vetId);
            setResult(RESULT_OK, resultIntent);
        } else {
            v.setVetId(vetId);
            dm.replaceVet(v);
        }
        //close
        finish();
    }

}
