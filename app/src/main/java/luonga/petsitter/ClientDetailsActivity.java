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
 * Activity class for client details screen.
 *
 * @author Linda Zuvich
 * @version 12/21/2018
 */
public class ClientDetailsActivity extends BaseActivity {

    //fields
    private int clientId;
    private DataManager dm;
    private Client thisClient;
    private PreferencesManager pm;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EventListener<DocumentSnapshot> dataListener;
    private ListenerRegistration reg;
    private TextView lblLastName;
    private TextView lblFirstName;
    private TextView lblCell;
    private TextView lblEmail;

    /**
     * Android onCreate method.
     *
     * @param savedInstanceState the class state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pm = PreferencesManager.getInstance(getApplicationContext());
        setContentView(R.layout.activity_client_details);
        //create action bar and back arrow
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        //get current vet
        Intent intent = getIntent();
        clientId = intent.getIntExtra(Extras.CLIENT_ID, -1);
        if (clientId < 0) {
            finish();
        }
        //find UI components
        lblLastName = findViewById(R.id.lblLastName);
        lblFirstName = findViewById(R.id.lblFirstName);
        lblCell = findViewById(R.id.lblCell);
        lblEmail = findViewById(R.id.lblEmail);
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
                collection("clients").document(String.valueOf(clientId));
        dataListener = new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("MYLOG", "Client listener failed.", e);
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    thisClient = snapshot.toObject(Client.class);
                    dm.setClient(thisClient);
                    lblLastName.setText(thisClient.getLastName());
                    lblFirstName.setText(thisClient.getFirstName());
                    lblCell.setText(thisClient.getCell());
                    lblEmail.setText(thisClient.getEmail());
                }
            }
        };
        reg = ref.addSnapshotListener(dataListener);
    }

    /**
     * Creates the top menu.
     *
     * @param menu the top menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        getMenuInflater().inflate(R.menu.menu_edit_delete, menu);
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
            case R.id.action_edit:
                editClient();
                return true;
            case R.id.action_delete:
                deleteClient();
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
     * Edit the current client.
     */
    private void editClient() {
        Intent intent = new Intent(this, EditClientActivity.class);
        intent.putExtra(Extras.CLIENT_ID, clientId);
        startActivity(intent);
    }

    /**
     * Delete the current client.
     */
    private void deleteClient() {
        if (dm.clientHasPets(clientId)) {
            new AlertDialog.Builder(this)
                    .setTitle("Cannot Delete Client")
                    .setMessage("This client is still listed for at least one pet.")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        } else if (pm.isWarnBeforeDeletingClient()) {
            new AlertDialog.Builder(this)
                    .setTitle("Confirm")
                    .setMessage("Are you sure you want to delete this client?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dm.deleteClient(clientId);
                            finish();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
        } else {
            dm.deleteClient(clientId);
            finish();
        }
    }

    /**
     * Opens a phone dialer when the cell number is tapped.
     *
     * @param view the cell number component
     */
    public void lblCellOnClick(View view) {
        TextView lblCell = (TextView) view;
        String phone = PhoneNumberUtils.stripSeparators(lblCell.getText().toString());
        Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
        startActivity(callIntent);
    }

    /**
     * Opens an email client when the email address is tapped.
     *
     * @param view the email component
     */
    public void lblEmailOnClick(View view) {
        TextView lblEmail = (TextView) view;
        String address = lblEmail.getText().toString();
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + address));
        startActivity(Intent.createChooser(emailIntent, "E-mail"));
    }

}
