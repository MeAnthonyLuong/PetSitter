package luonga.petsitter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * Activity class for main screen with navigation drawer.
 *
 * @author Linda Zuvich
 * @version 12/21/2018
 */
public class NavActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        PetListFragment.PetListListener, ClientListFragment.ClientListListener,
        VetListFragment.VetListListener {

    //fields
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EventListener<QuerySnapshot> petDataListener;
    private ListenerRegistration petReg;
    private EventListener<QuerySnapshot> clientDataListener;
    private ListenerRegistration clientReg;
    private EventListener<QuerySnapshot> vetDataListener;
    private ListenerRegistration vetReg;
    private Fragment fragment;
    private DataManager dm;
    private ActionBar actionBar;
    private String currentFragment;
    private PreferencesManager pm;

    /**
     * Android onCreate method.
     *
     * @param savedInstanceState the class state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        //set up FAB
        FloatingActionButton fab = findViewById(R.id.listFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fragment instanceof PetListFragment) {
                    Intent intent = new Intent(NavActivity.this, EditPetActivity.class);
                    startActivity(intent);
                } else if (fragment instanceof ClientListFragment) {
                    Intent intent = new Intent(NavActivity.this, EditClientActivity.class);
                    startActivity(intent);
                } else if (fragment instanceof VetListFragment) {
                    Intent intent = new Intent(NavActivity.this, EditVetActivity.class);
                    startActivity(intent);
                }
            }
        });
        //set up navigation drawer
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //set up fragment
        pm = PreferencesManager.getInstance(this);
        currentFragment = pm.getCurrentFragment();
        switch (currentFragment) {
            case "pets":
                fragment = new PetListFragment();
                actionBar.setTitle(R.string.titlePetList);
                navigationView.getMenu().getItem(0).setChecked(true);
                break;
            case "clients":
                fragment = new ClientListFragment();
                actionBar.setTitle(R.string.titleClientList);
                navigationView.getMenu().getItem(1).setChecked(true);
                break;
            case "vets":
                fragment = new VetListFragment();
                actionBar.setTitle(R.string.titleVetList);
                navigationView.getMenu().getItem(2).setChecked(true);
                break;
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frmFragment, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    /**
     * Handles when the back button is clicked. If the navigation drawer is open, just close it.
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * On pause, data listeners are stopped.
     */
    @Override
    protected void onPause() {
        super.onPause();
        stopDataListeners();
    }

    /**
     * Stops all data listeners.
     */
    private void stopDataListeners() {
        if (petReg != null && petDataListener != null) {
            petReg.remove();
        }
        if (clientReg != null && clientDataListener != null) {
            clientReg.remove();
        }
        if (vetReg != null && vetDataListener != null) {
            vetReg.remove();
        }
    }

    /**
     * Creates the top menu. Adds the user's email address to the menu.
     *
     * @param menu the top menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
            case R.id.action_settings:
                Intent intent = new Intent(this, PreferencesActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Handles the navigation drawer item selections.
     *
     * @param item the item selected
     * @return true
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_pets) {
            actionBar.setTitle(R.string.titlePetList);
            fragment = new PetListFragment();
            currentFragment = "pets";
        } else if (id == R.id.nav_clients) {
            actionBar.setTitle(R.string.titleClientList);
            fragment = new ClientListFragment();
            currentFragment = "clients";
        } else if (id == R.id.nav_vets) {
            actionBar.setTitle(R.string.titleVetList);
            fragment = new VetListFragment();
            currentFragment = "vets";
        }
        pm.setCurrentFragment(currentFragment);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frmFragment, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
        setUpDataListeners();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Displays the pet details activity. From PetListFragment.
     *
     * @param pet the pet to view
     */
    @Override
    public void viewPetRequested(Pet pet) {
        Intent intent = new Intent(this, PetDetailsActivity.class);
        intent.putExtra(Extras.PET_ID, pet.getPetId());
        startActivity(intent);
    }

    /**
     * Displays the client details activity. From ClientListFragment.
     *
     * @param client the client to view
     */
    @Override
    public void viewClientRequested(Client client) {
        Intent intent = new Intent(this, ClientDetailsActivity.class);
        intent.putExtra(Extras.CLIENT_ID, client.getClientId());
        startActivity(intent);
    }

    /**
     * Displays the vet details activity. From VetListFragment.
     *
     * @param vet the vet to view
     */
    @Override
    public void viewVetRequested(Vet vet) {
        Intent intent = new Intent(this, VetDetailsActivity.class);
        intent.putExtra(Extras.VET_ID, vet.getVetId());
        startActivity(intent);
    }

    /**
     * Connects up the data listener once authentication is completed in the BaseActivity.
     */
    @Override
    protected void setUpDataListeners() {
        stopDataListeners();
        if (fragment instanceof PetListFragment) {
            //set up pet list
            dm = DataManager.getDataManager(this, userId);
            final CollectionReference ref = db.collection("users").document(userId)
                    .collection("pets");
            petDataListener = new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    if (documentSnapshots != null && !documentSnapshots.isEmpty()) {
                        ArrayList<Pet> petList = new ArrayList<>();
                        for (int i = 0; i < documentSnapshots.size(); i++) {
                            DocumentSnapshot snapshot = documentSnapshots.getDocuments().get(i);
                            Pet pet = snapshot.toObject(Pet.class);
                            petList.add(pet);
                        }
                        dm.setPetList(petList);
                        ((PetListFragment)fragment).updateData();
                    }
                }
            };
            petReg = ref.addSnapshotListener(petDataListener);
        } else if (fragment instanceof ClientListFragment) {
            //set up client list
            dm = DataManager.getDataManager(this, userId);
            final CollectionReference ref = db.collection("users").document(userId)
                    .collection("clients");
            clientDataListener = new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    if (documentSnapshots != null && !documentSnapshots.isEmpty()) {
                        ArrayList<Client> clientList = new ArrayList<>();
                        for (int i = 0; i < documentSnapshots.size(); i++) {
                            DocumentSnapshot snapshot = documentSnapshots.getDocuments().get(i);
                            Client client = snapshot.toObject(Client.class);
                            clientList.add(client);
                        }
                        dm.setClientList(clientList);
                        ((ClientListFragment)fragment).updateData();
                    }
                }
            };
            clientReg = ref.addSnapshotListener(clientDataListener);
        } else if (fragment instanceof VetListFragment) {
            //set up vet list
            dm = DataManager.getDataManager(this, userId);
            final CollectionReference ref = db.collection("users").document(userId)
                    .collection("vets");
            vetDataListener = new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    if (documentSnapshots != null && !documentSnapshots.isEmpty()) {
                        ArrayList<Vet> vetList = new ArrayList<>();
                        for (int i = 0; i < documentSnapshots.size(); i++) {
                            DocumentSnapshot snapshot = documentSnapshots.getDocuments().get(i);
                            Vet vet = snapshot.toObject(Vet.class);
                            vetList.add(vet);
                        }
                        dm.setVetList(vetList);
                        ((VetListFragment)fragment).updateData();
                    }
                }
            };
            vetReg = ref.addSnapshotListener(vetDataListener);
        }
    }

}
