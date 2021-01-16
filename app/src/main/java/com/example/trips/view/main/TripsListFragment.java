package com.example.trips.view.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.trips.R;
import com.example.trips.model.Trip;
import com.example.trips.model.TripStatus;
import com.example.trips.utils.DBUtil;
import com.example.trips.view.trip.NoteActivity;
import com.example.trips.view.trip.AddEditTripActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.example.trips.view.main.TripsAdapter.TRIP;
import static com.example.trips.view.trip.AddEditTripActivity.EDIT_TRIP;
import static com.example.trips.view.trip.AddEditTripActivity.TRIP_ACTION;

public class TripsListFragment extends Fragment {

    private int section;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private TripsAdapter adapter;

    public static TripsListFragment newInstance(int index) {
        TripsListFragment fragment = new TripsListFragment();
        fragment.section = index;
        return fragment;
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_trips_list, container, false);
        recyclerView = root.findViewById(R.id.recyclerView);
        adapter = new TripsAdapter(R.layout.list_item);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.scrollToPosition(0);
        registerForContextMenu(recyclerView);

        swipeRefresh = root.findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(this::getTrips);

        return root;
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        Trip trip = adapter.getCurrTrip();
        Intent intent;
        int itemId = item.getItemId();
        if (itemId == R.id.ctx_menu_add_notes) {
            intent = new Intent(getContext(), NoteActivity.class);
            intent.putExtra(TRIP, trip);
            startActivity(intent);
        } else if (itemId == R.id.ctx_menu_edit_trip) {
            intent = new Intent(getContext(), AddEditTripActivity.class);
            intent.putExtra(TRIP_ACTION, EDIT_TRIP);
            intent.putExtra(TRIP, trip);
            startActivity(intent);
        } else if (itemId == R.id.ctx_menu_delete) {
            //TODO confirmation dialog
            //TODO remove alarm
            FirebaseDatabase database = DBUtil.getDB();
            DatabaseReference myRef = database.getReference()
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("trips").child(trip.getId());
            myRef.removeValue();
            getTrips();
        } else if (itemId == R.id.ctx_menu_start_trip) {
            //TODO
//            intent = new Intent(getContext(), AddEditTripActivity.class);
//            intent.putExtra(TRIP_ACTION, EDIT_TRIP);
//            intent.putExtra(TRIP, trip);
//            startActivity(intent);
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        getTrips();
    }

    private void getTrips() {
        FirebaseDatabase database = DBUtil.getDB();
        DatabaseReference myRef = database.getReference()
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("trips");
        myRef.keepSynced(true);

        ArrayList<Trip> trips = new ArrayList<>();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot tripSnapshot : dataSnapshot.getChildren()) {
                    Trip trip = tripSnapshot.getValue(Trip.class);
                    if (trip != null) {
                        if (trip.getStatus().equals(TripStatus.UPCOMING) && section == 0)
                            trips.add(trip);
                        if (!trip.getStatus().equals(TripStatus.UPCOMING) && section != 0)
                            trips.add(trip);
                    }
                }
                adapter.setTrips(trips);
                myRef.removeEventListener(this);
                swipeRefresh.setRefreshing(false);
                recyclerView.scrollToPosition(0);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(getContext(), "There was an error connecting to the database.", Toast.LENGTH_SHORT).show();
                myRef.removeEventListener(this);
                swipeRefresh.setRefreshing(false);
                recyclerView.scrollToPosition(0);
            }
        });
    }
}