package com.example.go4lunch.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.example.go4lunch.R;
import com.example.go4lunch.models.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *
 * TODO: Replace the implementation with code for your data type.
 */
public class WorkMatesAdapter extends FirestoreRecyclerAdapter<User,WorkMatesAdapter.ViewHolder> {

    private final RequestManager glide;


    public WorkMatesAdapter(@NonNull FirestoreRecyclerOptions<User> options, RequestManager glide) {
        super(options);
        this.glide = glide;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item_workmates, parent, false);
        return new ViewHolder(view);
    }



    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull User model) {

    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_workmates_picture)
        ImageView workmatesPicture;
        @BindView(R.id.item_workmates)
        TextView workmateRestaurantChoice;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
        }

    }
}