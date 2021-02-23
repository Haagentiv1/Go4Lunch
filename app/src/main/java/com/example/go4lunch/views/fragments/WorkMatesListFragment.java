package com.example.go4lunch.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.go4lunch.R;
import com.example.go4lunch.adapters.WorkMatesAdapter;
import com.example.go4lunch.api.UserHelper;
import com.example.go4lunch.models.User;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A fragment representing a list of Items.
 */
public class WorkMatesListFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    @BindView(R.id.list_workmates)
    RecyclerView recyclerView;


    private WorkMatesAdapter workMatesAdapter;
    @Nullable private User modelCurrentUser;



    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public WorkMatesListFragment() {
    }


    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static WorkMatesListFragment newInstance(int columnCount) {
        WorkMatesListFragment fragment = new WorkMatesListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list_workmates, container, false);
        ButterKnife.bind(this,view);
        this.configureRecyclerView();
        return view;
    }

    private FirestoreRecyclerOptions<User> generateOptionsForAdapter(Query query){
        return new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .setLifecycleOwner(this)
                .build();
    }

    private void configureRecyclerView(){
        this.workMatesAdapter = new WorkMatesAdapter(generateOptionsForAdapter(UserHelper.getAllUsers()), Glide.with(this));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(workMatesAdapter);

    }
}