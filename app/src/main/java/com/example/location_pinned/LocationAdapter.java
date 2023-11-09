package com.example.location_pinned;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {

    private List<LocationModel> locationList;
    private LayoutInflater inflater;

    public LocationAdapter(List<LocationModel> locations) {
        this.locationList = locations;
    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item_location, parent, false);
        return new LocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        LocationModel location = locationList.get(position);
        holder.bind(location);
    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }

    public class LocationViewHolder extends RecyclerView.ViewHolder {
        TextView addressTextView;
        TextView latitudeTextView;
        TextView longitudeTextView;

        public LocationViewHolder(View itemView) {
            super(itemView);
            addressTextView = itemView.findViewById(R.id.addressTextView);
            latitudeTextView = itemView.findViewById(R.id.latitudeTextView);
            longitudeTextView = itemView.findViewById(R.id.longitudeTextView);
        }

        public void bind(LocationModel location) {
            if (location != null) {
                addressTextView.setText(location.getAddress());
                latitudeTextView.setText("Latitude: " + location.getLatitude());
                longitudeTextView.setText("Longitude: " + location.getLongitude());
            }
        }
    }
}
