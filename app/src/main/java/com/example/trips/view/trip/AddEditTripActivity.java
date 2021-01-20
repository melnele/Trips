package com.example.trips.view.trip;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
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
import java.util.Date;

import static com.example.trips.view.main.TripsAdapter.TRIP;

public class AddEditTripActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    public static final String TRIP_ACTION = "TRIP_ACTION";
    public static final String ADD_TRIP = "ADD_TRIP";
    public static final String EDIT_TRIP = "EDIT_TRIP";
    private static final int permission_request_code = 961;

    private AutocompleteSupportFragment autocompleteFragment1, autocompleteFragment2;
    private TextView dateText, timeText, tripNameEditText;
    private Address startAddress, endAddress;
    private boolean dateSet = false, timeSet = false;
    private CheckBox roundTripCheckBox;
    private Button btnAddTrip;
    private Calendar date;
    private Trip trip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_trip);
        getPermission();

        date = Calendar.getInstance();

        tripNameEditText = findViewById(R.id.tripNameEditText);
        dateText = findViewById(R.id.showDateTextView);
        timeText = findViewById(R.id.showTimeTextView);
        btnAddTrip = findViewById(R.id.btnAddTrip);
        roundTripCheckBox = findViewById(R.id.roundTripCheckBox);

        initDateTime();
        initPlaces();

        String tripAction = getIntent().getStringExtra(TRIP_ACTION);
        if (tripAction != null && tripAction.equalsIgnoreCase(EDIT_TRIP)) {
            setEditView();
            return;
        }

        btnAddTrip.setOnClickListener(v -> {
            if (tripNameEditText.getText().toString().isEmpty()) {
                Toast.makeText(getApplicationContext(), getString(R.string.name_required), Toast.LENGTH_SHORT).show();
                return;
            } else if (startAddress == null) {
                Toast.makeText(getApplicationContext(), getString(R.string.start_required), Toast.LENGTH_SHORT).show();
                return;
            } else if (endAddress == null) {
                Toast.makeText(getApplicationContext(), getString(R.string.end_required), Toast.LENGTH_SHORT).show();
                return;
            } else if (!dateSet) {
                Toast.makeText(getApplicationContext(), getString(R.string.date_required), Toast.LENGTH_SHORT).show();
                return;
            } else if (!timeSet) {
                Toast.makeText(getApplicationContext(), getString(R.string.time_required), Toast.LENGTH_SHORT).show();
                return;
            }
            FirebaseDatabase database = DBUtil.getDB();
            DatabaseReference myRef = database.getReference()
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("trips").push();
            trip = new Trip(myRef.getKey(), tripNameEditText.getText().toString(), startAddress, endAddress, date.getTime());
            trip.setRoundTrip(roundTripCheckBox.isChecked());
            myRef.keepSynced(true);
            myRef.setValue(trip);
            startAlarm();

            finish();
        });
    }

    private void setEditView() {
        trip = (Trip) getIntent().getSerializableExtra(TRIP);
        if (trip != null) {
            ((TextView) findViewById(R.id.titleTextView)).setText(R.string.edit_trip_details);
            btnAddTrip.setText(R.string.edit);
            Date time = trip.getTime();
            date.setTime(time);
            tripNameEditText.setText(trip.getName());
            roundTripCheckBox.setChecked(trip.getRoundTrip());
            dateText.setText(DateFormat.getDateInstance().format(time));
            timeText.setText(DateFormat.getTimeInstance((DateFormat.SHORT)).format(time));
            autocompleteFragment1.setText(trip.getStartPoint().getName());
            autocompleteFragment2.setText(trip.getEndPoint().getName());
            btnAddTrip.setOnClickListener(v -> {
                FirebaseDatabase database = DBUtil.getDB();
                DatabaseReference myRef = database.getReference()
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("trips").child(trip.getId());
                trip.setName(tripNameEditText.getText().toString());
                if (!tripNameEditText.getText().toString().isEmpty()) {
                    trip.setName(tripNameEditText.getText().toString());
                }
                if (startAddress != null) {
                    trip.setStartPoint(startAddress);
                }
                if (endAddress != null) {
                    trip.setEndPoint(endAddress);
                }
                if (dateSet || timeSet) {
                    trip.setTime(date.getTime());
                }
                trip.setRoundTrip(roundTripCheckBox.isChecked());
                myRef.setValue(trip);
                startAlarm();

                finish();
            });
        }
    }

    private void initDateTime() {
        findViewById(R.id.timeImageView).setOnClickListener(v -> {
            DialogFragment timePicker = new TimePickerFragment();
            timePicker.show(getSupportFragmentManager(), "time picker");
        });
        findViewById(R.id.calendarImageView).setOnClickListener(v -> {
            DialogFragment datePicker = new DatePickerFragment();
            datePicker.show(getSupportFragmentManager(), "date picker");
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        date.set(Calendar.YEAR, year);
        date.set(Calendar.MONTH, month);
        date.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String str = DateFormat.getDateInstance().format(date.getTime());
        dateText.setText(str);
        dateSet = true;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        date.set(Calendar.HOUR_OF_DAY, hourOfDay);
        date.set(Calendar.MINUTE, minute);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        timeText.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(date.getTime()));
        timeSet = true;
    }

    private void initPlaces() {
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.api_key));
        }

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);
        // Initialize the AutocompleteSupportFragment.
        autocompleteFragment1 = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment_start_point);

        autocompleteFragment2 = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment_end_point);

        autocompleteFragment1.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG));
        autocompleteFragment2.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG));

        autocompleteFragment1.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                LatLng latLng = place.getLatLng();
                LatLong latLong = new LatLong(latLng.latitude, latLng.longitude);
                startAddress = new Address(place.getId(), place.getName(), latLong, place.getAddress());
            }

            @Override
            public void onError(@NonNull Status status) {
//                Toast.makeText(getApplicationContext(), status.toString(), Toast.LENGTH_SHORT).show();
                startAddress = null;
            }
        });
        autocompleteFragment2.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                LatLng latLng = place.getLatLng();
                LatLong latLong = new LatLong(latLng.latitude, latLng.longitude);
                endAddress = new Address(place.getId(), place.getName(), latLong, place.getAddress());
            }

            @Override
            public void onError(@NonNull Status status) {
//                Toast.makeText(getApplicationContext(), status.toString(), Toast.LENGTH_SHORT).show();
                endAddress = null;
            }
        });
    }

    //Permission for the over screen
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == permission_request_code) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void getPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            new AlertDialog.Builder(this)
                    .setMessage(R.string.permission_message)
                    .setPositiveButton(R.string.yes, (dialog, which) -> {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, permission_request_code);
                    })
                    .setNegativeButton(R.string.no, (dialog, which) -> Toast.makeText(this, getString(R.string.permission_toast), Toast.LENGTH_SHORT).show()).create().show();
        }
    }

    private void startAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), AlertActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(TRIP, trip);
        intent.putExtra(TRIP, bundle);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), trip.getId().hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (date.after(Calendar.getInstance())) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, date.getTimeInMillis(), pendingIntent);
        }
    }
}