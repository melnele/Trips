package com.example.trips.view.addtrip;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.trips.R;
import com.example.trips.model.Address;
import com.example.trips.model.LatLong;
import com.example.trips.model.Trip;

import com.example.trips.utils.DBUtil;
import com.example.trips.view.AlertActivity;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Calendar;

public class CreateTripItem extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private final int permission_request_code = 100;
    private TextView dateText, timeText, tripNameEditText;
    private Address startAddress, endAddress;
    private Spinner spinnerWay, spinnerRepeat;
    private Button btnAddTrip;
    private Calendar date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_trip_item);
        date = Calendar.getInstance();
        //for spinners
        spinnerWay = findViewById(R.id.spinnerWays);
        spinnerRepeat = findViewById(R.id.spinnerRepeat);
        tripNameEditText = findViewById(R.id.tripNameEditText);

        ArrayAdapter<CharSequence> adapterWay = ArrayAdapter.createFromResource(this, R.array.ways, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapterRepeat = ArrayAdapter.createFromResource(this, R.array.repitition, android.R.layout.simple_spinner_item);

        adapterWay.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterRepeat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerWay.setAdapter(adapterWay);
        spinnerRepeat.setAdapter(adapterRepeat);
        //for date and time
        dateText = findViewById(R.id.showDateTextView);
        timeText = findViewById(R.id.showTimeTextView);
        findViewById(R.id.timeImageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });
        findViewById(R.id.calendarImageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.api_key));
        }
        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);
        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment1 = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment_start_point);

        AutocompleteSupportFragment autocompleteFragment2 = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment_end_point);

        autocompleteFragment1.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG));
        autocompleteFragment2.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG));
        autocompleteFragment1.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                LatLng latLng = place.getLatLng();
                LatLong latLong = new LatLong(latLng.latitude, latLng.latitude);
                startAddress = new Address(place.getId(), place.getName(), latLong, place.getAddress());
            }

            @Override
            public void onError(@NonNull Status status) {
                Toast.makeText(getApplicationContext(), status.toString(), Toast.LENGTH_SHORT).show();
                startAddress = null;
            }
        });
        autocompleteFragment2.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                LatLng latLng = place.getLatLng();
                LatLong latLong = new LatLong(latLng.latitude, latLng.latitude);
                endAddress = new Address(place.getId(), place.getName(), latLong, place.getAddress());
            }

            @Override
            public void onError(@NonNull Status status) {
                Toast.makeText(getApplicationContext(), status.toString(), Toast.LENGTH_SHORT).show();
                endAddress = null;
            }
        });

        btnAddTrip = findViewById(R.id.btnAddTrip);
        btnAddTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                getPermission();
                // TODO Validate input
                FirebaseDatabase database = DBUtil.getDB();
                DatabaseReference myRef = database.getReference()
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("trips").push();
                Trip t = new Trip(myRef.getKey(), tripNameEditText.getText().toString(), startAddress, endAddress, date.getTime());
                myRef.keepSynced(true);
                myRef.setValue(t);
//                startAlarm(date);

                finish();
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        date.set(Calendar.YEAR, year);
        date.set(Calendar.MONTH, month);
        date.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String str = DateFormat.getDateInstance().format(date.getTime());
        dateText.setText(str);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        date.set(Calendar.HOUR_OF_DAY, hourOfDay);
        date.set(Calendar.MINUTE, minute);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        String time = hourOfDay + ":" + minute;
        timeText.setText(time);
    }
}