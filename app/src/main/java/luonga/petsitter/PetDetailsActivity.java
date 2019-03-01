package luonga.petsitter;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * Activity class for pet details screen.
 *
 * @author Linda Zuvich
 * @version 12/21/2018
 */
public class PetDetailsActivity extends BaseActivity {

    //fields
    private int petId;
    private DataManager dm;
    private Pet thisPet;
    private TextView lblName;
    private TextView lblBirthYear;
    private TextView lblGender;
    private TextView lblSpeciesBreed;
    private TextView lblCare;
    private TextView lblClient;
    private TextView lblVet;
    private ImageView imgPet;
    private PreferencesManager pm;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EventListener<DocumentSnapshot> petDataListener;
    private EventListener<QuerySnapshot> vetDataListener;
    private EventListener<QuerySnapshot> clientDataListener;
    private ListenerRegistration petReg;
    private ListenerRegistration vetReg;
    private ListenerRegistration clientReg;

    /**
     * Builds the activity on startup.
     *
     * @param savedInstanceState the saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pm = PreferencesManager.getInstance(getApplicationContext());
        setContentView(R.layout.activity_pet_details);
        //create action bar and back arrow
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        //get current pet
        Intent intent = getIntent();
        petId = intent.getIntExtra(Extras.PET_ID, -1);
        if (petId < 0) {
            finish();
        }
        //find UI components
        lblName = findViewById(R.id.lblName);
        lblBirthYear = findViewById(R.id.lblBirthYear);
        lblGender = findViewById(R.id.lblGender);
        lblSpeciesBreed = findViewById(R.id.lblSpeciesBreed);
        lblCare = findViewById(R.id.lblCareInstructions);
        lblClient = findViewById(R.id.lblClient);
        lblVet = findViewById(R.id.lblVet);
        imgPet = findViewById(R.id.imgPet);
    }

    /**
     * On pause, all data listeners are stopped.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (petReg != null && petDataListener != null) {
            petReg.remove();
        }
        if (vetReg != null && vetDataListener != null) {
            vetReg.remove();
        }
        if (clientReg != null && clientDataListener != null) {
            clientReg.remove();
        }
    }

    /**
     * Connects up the data listeners once authentication is completed in the BaseActivity.
     * The pet listener is listening for changes in this particular pet entry.
     */
    @Override
    public void setUpDataListeners() {
        dm = DataManager.getDataManager(getApplicationContext(), userId);
        //pet listener
        final DocumentReference petRef = db.collection("users").document(userId).
                collection("pets").document(String.valueOf(petId));
        petDataListener = new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("MYLOG", "Pet listener failed.", e);
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    thisPet = snapshot.toObject(Pet.class);
                    dm.setPet(thisPet);
                    lblName.setText(thisPet.getName());
                    lblBirthYear.setText(String.valueOf(thisPet.getBirthYear()));
                    String[] genderArray = getResources().getStringArray(R.array.arrGenders);
                    lblGender.setText(genderArray[thisPet.getGender()]);
                    lblSpeciesBreed.setText(thisPet.getType());
                    lblCare.setText(thisPet.getCare());
                    if (thisPet.getClientId() > -1 && dm.getClientList().size() > 0 &&
                            dm.getClient(thisPet.getClientId()) != null) {
                        lblClient.setText(dm.getClient(thisPet.getClientId()).toString());
                    }
                    if (thisPet.getVetId() > -1 && dm.getVetList().size() > 0 &&
                            dm.getVet(thisPet.getVetId()) != null) {
                        lblVet.setText(dm.getVet(thisPet.getVetId()).toString());
                    }
                    String photoStr = thisPet.getPhoto();
                    imgPet = findViewById(R.id.imgPet);
                    if (photoStr != null) {
                        byte[] photo = Base64.decode(photoStr, Base64.DEFAULT);
                        imgPet.setImageBitmap(BitmapFactory.decodeByteArray(photo, 0, photo.length));
                        imgPet.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    } else {
                        imgPet.setImageBitmap(null);
                    }
                }
            }
        };
        petReg = petRef.addSnapshotListener(petDataListener);
        //vet listener
        final CollectionReference vetRef = db.collection("users").document(userId)
                .collection("vets");
        vetDataListener = new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (documentSnapshots != null && !documentSnapshots.isEmpty()) {
                    ArrayList<Vet> list = new ArrayList<>();
                    for (int i = 0; i < documentSnapshots.size(); i++) {
                        DocumentSnapshot snapshot = documentSnapshots.getDocuments().get(i);
                        Vet vet = snapshot.toObject(Vet.class);
                        list.add(vet);
                    }
                    dm.setVetList(list);
                    if (thisPet != null && thisPet.getVetId() >= 0) {
                        lblVet.setText(dm.getVet(thisPet.getVetId()).toString());
                    }
                }
            }
        };
        vetReg = vetRef.addSnapshotListener(vetDataListener);
        //client listener
        final CollectionReference clientRef = db.collection("users").document(userId)
                .collection("clients");
        clientDataListener = new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (documentSnapshots != null && !documentSnapshots.isEmpty()) {
                    ArrayList<Client> list = new ArrayList<>();
                    for (int i = 0; i < documentSnapshots.size(); i++) {
                        DocumentSnapshot snapshot = documentSnapshots.getDocuments().get(i);
                        Client client = snapshot.toObject(Client.class);
                        list.add(client);
                    }
                    dm.setClientList(list);
                    if (thisPet != null && thisPet.getClientId() >= 0) {
                        Client client;
                        client = dm.getClient(thisPet.getClientId());
                        if (client != null) {
                            lblClient.setText(client.toString());
                        }
                    }
                }
            }
        };
        clientReg = clientRef.addSnapshotListener(clientDataListener);
    }

    /**
     * Creates the top menu.
     *
     * @param menu the top menu
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
     * @return true or false
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sign_out:
                signOut();
                return true;
            case R.id.action_edit:
                editPet();
                return true;
            case R.id.action_delete:
                deletePet();
                return true;
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_settings:
                Intent intent = new Intent(this, PreferencesActivity.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Edits the current pet.
     */
    private void editPet() {
        Intent intent = new Intent(this, EditPetActivity.class);
        intent.putExtra(Extras.PET_ID, petId);
        startActivity(intent);
    }

    /**
     * Delete the current pet.
     */
    private void deletePet() {
        if (pm.isWarnBeforeDeletingPet()) {
            new AlertDialog.Builder(this)
                    .setTitle("Confirm")
                    .setMessage("Are you sure you want to delete this pet?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dm.deletePet(petId);
                            finish();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
        } else {
            dm.deletePet(petId);
        }
    }

    /**
     * Handles the click event on the vet name.
     *
     * @param view the vet label
     */
    public void lblVetOnClick(View view) {
        Intent intent = new Intent(this, VetDetailsActivity.class);
        intent.putExtra(Extras.VET_ID, thisPet.getVetId());
        startActivity(intent);
    }

    /**
     * Handles the click event on the client name.
     *
     * @param view the client label
     */
    public void lblClientOnClick(View view) {
        Intent intent = new Intent(this, ClientDetailsActivity.class);
        intent.putExtra(Extras.CLIENT_ID, thisPet.getClientId());
        startActivity(intent);
    }

}
