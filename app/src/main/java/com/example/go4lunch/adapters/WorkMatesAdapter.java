package com.example.go4lunch.adapters;

import android.annotation.SuppressLint;
import android.os.Build;
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

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *
 * TODO: Replace the implementation with code for your data type.
 */
public class WorkMatesAdapter extends FirestoreRecyclerAdapter<User,WorkMatesAdapter.ViewHolder> {

    private final RequestManager glide;
    @BindString(R.string.restaurant_chosen)
    public String restaurantChosen;


    public WorkMatesAdapter(@NonNull FirestoreRecyclerOptions<User> options, RequestManager glide) {
        super(options);
        this.glide = glide;
    }
    public Boolean checkUserRestaurant(User user){
       return user.getChosenRestaurant() != null;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item_workmates, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull User model) {
        String restaurantChosen = holder.workmateRestaurantChoice.getContext().getString(R.string.restaurant_chosen);
        String noRestaurant = holder.workmateRestaurantChoice.getContext().getString(R.string.no_restaurant_chosen);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkUserRestaurant(model)){
            holder.workmateRestaurantChoice.setText(model.getUsername() + " " + noRestaurant);
            holder.workmateRestaurantChoice.setTextAppearance(R.style.NoRestaurantChosen);}
            else {
                holder.workmateRestaurantChoice.setText(String.format("%s %s %s", model.getUsername(), restaurantChosen, model.getChosenRestaurantName()));
            }
        }
        glide.load(model.getUrlPicture()).circleCrop().into(holder.workmatesPicture);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_workmates_picture)
        ImageView workmatesPicture;
        @BindView(R.id.item_workmates_restaurant)
        TextView workmateRestaurantChoice;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
        }

    }
}