package com.example.trips.view.main;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.example.trips.view.AlertActivity;
import com.example.trips.view.BubbleService;
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
    private boolean visible = false;

    public static TripsListFragment newInstance(int index) {
        TripsListFragment fragment = new TripsListFragment();
        fragment.setRetainInstance(true);
        fragment.setHasOptionsMenu(true);
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
        if (section == 1)
            root.findViewById(R.id.mapFragment).setVisibility(View.VISIBLE);

        swipeRefresh = root.findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(this::getTrips);

        return root;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.sync) {
            swipeRefresh.setRefreshing(true);
            getTrips();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        Trip trip = adapter.getCurrTrip();
        if (!(isVisible() && visible) || trip == null) {
            return super.onContextItemSelected(item);
        }
        int itemId = item.getItemId();
        if (itemId == R.id.ctx_menu_add_notes) {
            Intent intent = new Intent(getContext(), NoteActivity.class);
            intent.putExtra(TRIP, trip);
            startActivity(intent);
        } else if (itemId == R.id.ctx_menu_edit_trip) {
            Intent intent = new Intent(getContext(), AddEditTripActivity.class);
            intent.putExtra(TRIP_ACTION, EDIT_TRIP);
            intent.putExtra(TRIP, trip);
            startActivity(intent);
        } else if (itemId == R.id.ctx_menu_delete_trip) {
            new AlertDialog.Builder(getContext())
                    .setMessage(R.string.delete_trip).setPositiveButton(R.string.yes, (dialog, which) -> {
                FirebaseDatabase database = DBUtil.getDB();
                DatabaseReference myRef = database.getReference()
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("trips").child(trip.getId());
                myRef.removeValue();
                removeAlarm(trip.getId());
                getTrips();
            }).setNegativeButton(R.string.no, (dialog, which) -> {
            }).create().show();
        } else if (itemId == R.id.ctx_menu_start_trip) {
            removeAlarm(trip.getId());
            startTrip(trip);
        } else if (itemId == R.id.ctx_menu_cancel_trip) {
            FirebaseDatabase database = DBUtil.getDB();
            DatabaseReference myRef = database.getReference()
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("trips").child(trip.getId()).child("status");
            myRef.setValue(TripStatus.CANCELLED);
            removeAlarm(trip.getId());
            getTrips();
        } else
            return super.onContextItemSelected(item);
        return true;
    }

    @Override
    public void onResume() {
        visible = true;
        getTrips();
        super.onResume();
    }

    @Override
    public void onPause() {
        visible = false;
        super.onPause();
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
            public void onCancelled(@NotNull DatabaseError error) {
                // Failed to read value
                Toast.makeText(getContext(), "There was an error connecting to the database.", Toast.LENGTH_SHORT).show();
                myRef.removeEventListener(this);
                swipeRefresh.setRefreshing(false);
                recyclerView.scrollToPosition(0);
            }
        });
    }

    private void removeAlarm(String id) {
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), AlertActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), id.hashCode(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }

    private void startTrip(Trip trip) {
        FirebaseDatabase database = DBUtil.getDB();
        DatabaseReference myRef = database.getReference()
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("trips").child(trip.getId()).child("status");
        myRef.setValue(TripStatus.DONE);

        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + trip.getEndPoint().getLatLong().toString());
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);

        if (trip.getNotes() != null) {
            Intent bubbleIntent = new Intent(getContext(), BubbleService.class);
            bubbleIntent.putExtra(TRIP, trip);
            getActivity().startService(bubbleIntent);
        }
    }
}