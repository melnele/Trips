package com.example.trips.view.main;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trips.model.Trip;

public class TripsAdapter extends RecyclerView.Adapter<TripsAdapter.ViewHolder> {
    Trip[] trips;
    int list_item_id;

    /**
     * Initialize the dataset of the Adapter.
     */
    public TripsAdapter(int resource, Trip[] trips) {
        this.trips = trips;
        this.list_item_id = resource;
    }

    @NonNull
    @Override
    // Create new views (invoked by the layout manager)
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view.
        View view = LayoutInflater.from(parent.getContext()).inflate(list_item_id, parent, false);
        return new ViewHolder(view);
    }

    @Override
    // Replace the contents of a view (invoked by the layout manager)
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.i("TAG", "Element " + position + " set.");

        // Get element from your dataset at this position and replace the contents of the view
        // with that element
    }

    @Override
    public int getItemCount() {
        return trips.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View view) {
            super(view);
            // Define click listener for the ViewHolder's View.
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return false;
                }
            });
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("TAG", "Element " + getAdapterPosition() + " clicked.");
                }
            });
//            textViewTitle = view.findViewById(R.id.textViewTitle);
//            textViewDescription = view.findViewById(R.id.textViewDescription);
//            imageView = view.findViewById(R.id.imageView);
        }
    }
}
