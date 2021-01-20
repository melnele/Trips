package com.melnele.trips.view.main;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.melnele.trips.R;
import com.melnele.trips.model.Trip;
import com.melnele.trips.model.TripStatus;

import java.text.DateFormat;
import java.util.List;

public class TripsAdapter extends RecyclerView.Adapter<TripsAdapter.ViewHolder> {
    public final static String TRIP = "TRIP";
    private final int list_item_id;
    private List<Trip> trips;
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
        String time = DateFormat.getTimeInstance((DateFormat.SHORT)).format(trips.get(position).getTime());

        holder.nameTextView.setText(trips.get(position).getName());
        holder.dateTextView.setText(date);
        holder.timeTextView.setText(time);
        holder.tripStatusTextView.setText(getStatusString(trips.get(position).getStatus()));
        holder.startPointTextView.setText(trips.get(position).getStartPoint().getName());
        holder.endPointTextView.setText(trips.get(position).getEndPoint().getName());

        holder.itemView.setOnCreateContextMenuListener((menu, v, menuInfo) -> {
            if (trips.get(position).getStatus().equals(TripStatus.UPCOMING)) {
                menu.add(Menu.NONE, R.id.ctx_menu_start_trip, Menu.NONE, R.string.start);
                menu.add(Menu.NONE, R.id.ctx_menu_cancel_trip, Menu.NONE, R.string.cancel);
            }
            menu.add(Menu.NONE, R.id.ctx_menu_edit_trip, Menu.NONE, R.string.edit);
            menu.add(Menu.NONE, R.id.ctx_menu_delete_trip, Menu.NONE, R.string.delete);
            menu.add(Menu.NONE, R.id.ctx_menu_add_notes, Menu.NONE, R.string.notes);
        });
        holder.itemView.setOnLongClickListener(v -> {
            setCurrTrip(trips.get(position));
            return false;
        });
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        holder.itemView.setOnLongClickListener(null);
        holder.itemView.setOnCreateContextMenuListener(null);
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return trips == null ? 0 : trips.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView;
        private final TextView tripStatusTextView;
        private final TextView dateTextView;
        private final TextView timeTextView;
        private final TextView startPointTextView;
        private final TextView endPointTextView;

        public ViewHolder(@NonNull View view) {
            super(view);

            nameTextView = view.findViewById(R.id.nameTextView);
            tripStatusTextView = view.findViewById(R.id.tripStatusTextView);
            dateTextView = view.findViewById(R.id.dateTextView);
            timeTextView = view.findViewById(R.id.timeTextView);
            startPointTextView = view.findViewById(R.id.startPointTextView);
            endPointTextView = view.findViewById(R.id.endPointTextView);
        }
    }

    public int getStatusString(TripStatus status) {
        switch (status) {
            case DONE:
                return R.string.done;
            case CANCELLED:
                return R.string.cancelled;
            default:
                return R.string.upcoming;
        }
    }
}
