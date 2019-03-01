package luonga.petsitter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment class for client list screen.
 *
 * @author Linda Zuvich
 * @version 12/21/2018
 */
public class ClientListFragment extends Fragment {

    //fields
    private ListView lstClients;
    private EditText txtFilter;
    private ArrayAdapter<Client> lstAdapter;
    private List<Client> clientList = new ArrayList<>();
    private boolean isFiltered;
    private ClientListListener listener;
    private DataManager dm;
    private View rootView;

    /**
     * Interface for an activity to register as a ClientListFragment listener.
     */
    interface ClientListListener {
        void viewClientRequested(Client client);
    }

    /**
     * Required default constructor.
     */
    public ClientListFragment() {
    }

    /**
     * Creates the fragment.
     *
     * @param inflater           the layout inflater
     * @param container          the container holding the fragment
     * @param savedInstanceState the saved state
     * @return the root view of the fragment
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        dm = DataManager.getDataManager(getContext(), FirebaseAuth.getInstance().getUid());
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_client_list, container, false);
        lstClients = rootView.findViewById(R.id.lstClients);
        txtFilter = rootView.findViewById(R.id.txtFilter);
        //get saved state
        if (savedInstanceState != null) {
            isFiltered = savedInstanceState.getBoolean("isFiltered");
        }
        //set up filter button
        ImageButton btnFilter = rootView.findViewById(R.id.ibtnFilter);
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleFilterClick();
            }
        });
        return rootView;
    }

    /**
     * Registers the controlling activity as a listener when the fragment is attached to it.
     *
     * @param context the controlling activity
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ClientListListener) {
            this.listener = (ClientListListener) context;
        }
    }

    /**
     * Saves the state of the activity.
     *
     * @param outState the class state
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isFiltered", isFiltered);
    }

    /**
     * On resume, set up the list adapter and filter if was filtered.
     */
    @Override
    public void onResume() {
        super.onResume();
        if (lstAdapter != null) {
            lstAdapter.notifyDataSetChanged();
        }
        if (isFiltered) {
            filterList();
        }
    }

    /**
     * Updates the client list. This method must be called by the controlling activity
     * whenever this fragment is visible and client data is altered outside of this fragment.
     */
    public void updateData() {
        clientList = dm.getClientList();
        if (isFiltered) {
            filterList();
        } else {
            //set up client list
            clientList = dm.getClientList();
            clientList = dm.getClientList();
            //set up list view adapter
            lstAdapter = new ArrayAdapter<>(rootView.getContext(),
                    android.R.layout.simple_list_item_1, clientList);
            lstClients.setAdapter(lstAdapter);//set up list view adapter
            //create list view click event
            AdapterView.OnItemClickListener itemClickListener =
                    new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Client thisClient = (Client) lstClients.getItemAtPosition(position);
                            listener.viewClientRequested(thisClient);
                            clearFilter();
                            isFiltered = false;
                        }
                    };
            lstClients.setOnItemClickListener(itemClickListener);
        }
    }

    /**
     * Handles the filter click.
     */
    private void handleFilterClick() {
        //hide keyboard
        if (getActivity() != null) {
            InputMethodManager inputManager = (InputMethodManager)
                    rootView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputManager != null) {
                inputManager.hideSoftInputFromWindow(
                        (null == getActivity().getCurrentFocus()) ? null :
                                getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        //handle filter request
        if (txtFilter.getText().toString().isEmpty()) {
            clearFilter();
            isFiltered = false;
        } else {
            isFiltered = true;
            filterList();
        }
    }

    /**
     * Filters the list based on user selection.
     */
    private void filterList() {
        String filter = txtFilter.getText().toString().toLowerCase();
        //filter list
        List<Client> filteredList = new ArrayList<>();
        for (Client c : clientList) {
            if ((c.getLastName().toLowerCase().contains(filter))
                    || (c.getFirstName().toLowerCase().contains(filter))) {
                filteredList.add(c);
            }
        }
        lstAdapter = new ArrayAdapter<>(rootView.getContext(),
                android.R.layout.simple_list_item_1, filteredList);
        lstClients.setAdapter(lstAdapter);
    }

    /**
     * Clears the list filter.
     */
    private void clearFilter() {
        lstAdapter = new ArrayAdapter<>(rootView.getContext(),
                android.R.layout.simple_list_item_1, clientList);
        lstClients.setAdapter(lstAdapter);
        txtFilter.setText("");
    }

}
