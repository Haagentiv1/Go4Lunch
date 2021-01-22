package com.example.go4lunch.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.R;
import com.example.go4lunch.models.NearbySearch.Result;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.ViewHolder> {

    private  List<Result> mPlaces;

    public PlacesAdapter(@NonNull List<Result> places) {
        this.mPlaces = places;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item_restaurant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mName.setText(mPlaces.get(position).getName());
        holder.mAddress.setText(mPlaces.get(position).getVicinity());
        holder.mAddress.setText(mPlaces.get(position).getName());

    }



    @Override
    public int getItemCount() {
        return mPlaces.size();
    }




    public class ViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.text_restaurant_name)
        TextView mName;

        @BindView(R.id.text_restaurant_address)
        TextView mAddress;

        @BindView(R.id.text_restaurant_rating)
        TextView mRating;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);

        }
    }
}