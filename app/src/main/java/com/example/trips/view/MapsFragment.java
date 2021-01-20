package com.example.trips.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.trips.R;
import com.example.trips.model.Trip;
import com.example.trips.model.TripStatus;
import com.example.trips.utils.DBUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class MapsFragment extends Fragment {

    private final OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            FirebaseDatabase database = DBUtil.getDB();
            DatabaseReference myRef = database.getReference()
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("trips");
            myRef.keepSynced(true);

            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                    googleMap.clear();
                    Random rnd = new Random();
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    for (DataSnapshot tripSnapshot : dataSnapshot.getChildren()) {
                        Trip trip = tripSnapshot.getValue(Trip.class);
                        if (trip != null) {
                            if (!trip.getStatus().equals(TripStatus.UPCOMING)) {
                                int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                                PolylineOptions options = new PolylineOptions().width(5).color(color).geodesic(true);
                                LatLng s = new LatLng(trip.getStartPoint().getLatLong().latitude, trip.getStartPoint().getLatLong().longitude);
                                LatLng e = new LatLng(trip.getEndPoint().getLatLong().latitude, trip.getEndPoint().getLatLong().longitude);
                                builder.include(s);
                                builder.include(e);
                                options.add(s);
                                options.add(e);
                                googleMap.addPolyline(options);
                            }
                        }
                    }
                    googleMap.setOnMapLoadedCallback(() -> googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 20)));
                }

                @Override
                public void onCancelled(@NotNull DatabaseError error) {
                    // Failed to read value
                    Toast.makeText(getContext(), "There was an error connecting to the database.", Toast.LENGTH_SHORT).show();
                    googleMap.clear();
                }
            });
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }
}