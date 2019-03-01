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
 * Fragment class for vet list screen.
 *
 * @author Linda Zuvich
 * @version 12/21/2018
 */
public class VetListFragment extends Fragment {

    //fields
    private ListView lstVets;
    private EditText txtFilter;
    private ArrayAdapter<Vet> lstAdapter;
    private List<Vet> vetList = new ArrayList<>();
    private boolean isFiltered;
    private VetListListener listener;
    private DataManager dm;
    private View rootView;

    /**
     * Interface for an activity to register as a VetListFragment listener.
     */
    interface VetListListener {
        void viewVetRequested(Vet vet);
    }

    /**
     * Required default constructor.
     */
    public VetListFragment() {
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
        rootView = inflater.inflate(R.layout.fragment_vet_list, container, false);
        lstVets = rootView.findViewById(R.id.lstVets);
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
        if (context instanceof VetListListener) {
            this.listener = (VetListListener) context;
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
     * Updates the vet list. This method must be called by the controlling activity
     * whenever this fragment is visible and vet data is altered outside of this fragment.
     */
    public void updateData() {
        vetList = dm.getVetList();
        if (isFiltered) {
            filterList();
        } else {
            //set up list view adapter
            lstAdapter = new ArrayAdapter<>(rootView.getContext(),
                    android.R.layout.simple_list_item_1, vetList);
            lstVets.setAdapter(lstAdapter);
            //create list view click event
            AdapterView.OnItemClickListener itemClickListener =
                    new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            Vet thisVet = (Vet) lstVets.getItemAtPosition(position);
                            listener.viewVetRequested(thisVet);
                            clearFilter();
                            isFiltered = false;
                        }
                    };
            lstVets.setOnItemClickListener(itemClickListener);
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
        List<Vet> filteredList = new ArrayList<>();
        for (Vet v : vetList) {
            if ((v.getLastName().toLowerCase().contains(filter))
                    || (v.getFirstName().toLowerCase().contains(filter))
                    || (v.getClinic().toLowerCase().contains(filter))) {
                filteredList.add(v);
            }
        }
        lstAdapter = new ArrayAdapter<>(rootView.getContext(),
                android.R.layout.simple_list_item_1, filteredList);
        lstVets.setAdapter(lstAdapter);
    }

    /**
     * Clears the list filter.
     */
    private void clearFilter() {
        lstAdapter = new ArrayAdapter<>(rootView.getContext(),
                android.R.layout.simple_list_item_1, vetList);
        lstVets.setAdapter(lstAdapter);
        txtFilter.setText("");
    }
}