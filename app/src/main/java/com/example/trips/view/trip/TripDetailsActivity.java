package com.example.trips.view.trip;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.trips.R;
import com.example.trips.model.Trip;

import static com.example.trips.view.main.TripsAdapter.TRIP;

public class TripDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);
        Trip t = (Trip) getIntent().getSerializableExtra(TRIP);
        Log.i("TAG", t.toString());
    }
}