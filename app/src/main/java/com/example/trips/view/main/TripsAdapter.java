package com.example.trips.view.main;


import android.content.Context;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trips.R;
import com.example.trips.model.Trip;
import com.example.trips.view.trip.TripDetailsActivity;

import java.text.DateFormat;
import java.util.List;

public class TripsAdapter extends RecyclerView.Adapter<TripsAdapter.ViewHolder> {
    public final static String TRIP = "TRIP";
    private List<Trip> trips;
    private final int list_item_id;
    private Trip currTrip;

    public Trip getCurrTrip() {
        return currTrip;
    }

    public void setCurrTrip(Trip currTrip) {
        this.currTrip = currTrip;
    }

    public TripsAdapter(int resource) {
        this.list_item_id = resource;
    }

    public void setTrips(List<Trip> trips) {
        this.trips = trips;
        notifyDataSetChanged();
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
        String date = DateFormat.getDateInstance().format(trips.get(position).getTime());
        String time = DateFormat.getTimeInstance().format(trips.get(position).getTime());

        holder.nameTextView.setText(trips.get(position).getName());
        holder.tripStatusTextView.setText(trips.get(position).getStatus().toString());
        holder.dateTextView.setText(date);
        holder.timeTextView.setText(time);
        holder.startPointTextView.setText(trips.get(position).getStartPoint().getName());
        holder.endPointTextView.setText(trips.get(position).getEndPoint().getName());

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setCurrTrip(trips.get(position));
                return false;
            }
        });
        Context context = holder.itemView.getContext();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TripDetailsActivity.class);
                intent.putExtra(TRIP, trips.get(position));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        holder.itemView.setOnLongClickListener(null);
        holder.itemView.setOnClickListener(null);
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return trips == null ? 0 : trips.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView;
        private final TextView tripStatusTextView;
        private final TextView dateTextView;
        private final TextView timeTextView;
        private final TextView startPointTextView;
        private final TextView endPointTextView;

        public ViewHolder(@NonNull View view) {
            super(view);

            view.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//                    menu.setHeaderTitle("Select The Action");
                    menu.add(Menu.NONE, R.id.ctx_menu_start_trip, Menu.NONE, R.string.start);
                    menu.add(Menu.NONE, R.id.ctx_menu_edit_trip, Menu.NONE, R.string.edit);
                    menu.add(Menu.NONE, R.id.ctx_menu_delete, Menu.NONE, R.string.delete);
                    menu.add(Menu.NONE, R.id.ctx_menu_add_notes, Menu.NONE, R.string.add_notes);
                }
            });

            nameTextView = view.findViewById(R.id.nameTextView);
            tripStatusTextView = view.findViewById(R.id.tripStatusTextView);
            dateTextView = view.findViewById(R.id.dateTextView);
            timeTextView = view.findViewById(R.id.timeTextView);
            startPointTextView = view.findViewById(R.id.startPointTextView);
            endPointTextView = view.findViewById(R.id.endPointTextView);
        }
    }
}
