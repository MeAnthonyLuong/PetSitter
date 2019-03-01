package luonga.petsitter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

/**
 * Activity class for vet details screen.
 *
 * @author Linda Zuvich
 * @version 12/21/2018
 */
public class VetDetailsActivity extends BaseActivity {

    //fields
    private int vetId;
    private DataManager dm;
    private Vet thisVet;
    private PreferencesManager pm;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EventListener<DocumentSnapshot> dataListener;
    private ListenerRegistration reg;
    private TextView lblLastName;
    private TextView lblFirstName;
    private TextView lblClinic;
    private TextView lblPhone;
    private TextView lblAddress;

    /**
     * Android onCreate method.
     *
     * @param savedInstanceState the class state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pm = PreferencesManager.getInstance(getApplicationContext());
        setContentView(R.layout.activity_vet_details);
        //create action bar and back arrow
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        //get current vet
        Intent intent = getIntent();
        vetId = intent.getIntExtra(Extras.VET_ID, -1);
        if (vetId < 0) {
            finish();
        }
        lblLastName = findViewById(R.id.lblLastName);
        lblFirstName = findViewById(R.id.lblFirstName);
        lblClinic = findViewById(R.id.lblClinic);
        lblPhone = findViewById(R.id.lblPhone);
        lblAddress = findViewById(R.id.lblAddress);
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
                    thisVet = snapshot.toObject(Vet.class);
                    dm.setVet(thisVet);
                    lblLastName.setText(thisVet.getLastName());
                    lblFirstName.setText(thisVet.getFirstName());
                    lblClinic.setText(thisVet.getClinic());
                    lblPhone.setText(thisVet.getPhone());
                    lblAddress.setText(thisVet.getAddress());
                }
            }
        };
        reg = ref.addSnapshotListener(dataListener);
    }

    /**
     * Creates the top menu.
     *
     * @param menu the top menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_delete, menu);
        getMenuInflater().inflate(R.menu.menu_settings, menu);
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
            case R.id.action_edit:
                editVet();
                return true;
            case R.id.action_delete:
                deleteVet();
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
     * Edit the current vet.
     */
    private void editVet() {
        Intent intent = new Intent(this, EditVetActivity.class);
        intent.putExtra(Extras.VET_ID, vetId);
        startActivity(intent);
    }

    /**
     * Delete the current vet.
     */
    private void deleteVet() {
        //delete the current vet
        if (dm.vetHasPets(vetId)) {
            new AlertDialog.Builder(this)
                    .setTitle("Cannot Delete Vet")
                    .setMessage("This vet is still listed for at least one pet.")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        } else if (pm.isWarnBeforeDeletingVet()) {
            new AlertDialog.Builder(this)
                    .setTitle("Confirm")
                    .setMessage("Are you sure you want to delete this vet?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dm.deleteVet(vetId);
                            finish();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
        } else {
            dm.deleteVet(vetId);
        }
    }

    /**
     * Opens a phone dialer when the cell number is tapped.
     *
     * @param view the phone number component
     */
    public void lblPhoneOnClick(View view) {
        TextView lblPhone = (TextView) view;
        String phone = PhoneNumberUtils.stripSeparators(lblPhone.getText().toString());
        Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
        startActivity(callIntent);
    }

    /**
     * Opens a navigator when the address is tapped.
     *
     * @param view the address component
     */
    public void lblAddressOnClick(View view) {
        TextView lblAddress = (TextView) view;
        String address = lblAddress.getText().toString();
        address = address.replaceAll("\n", ", ");
        Intent navIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + address));
        startActivity(navIntent);
    }

}
