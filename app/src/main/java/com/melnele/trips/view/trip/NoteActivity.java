package com.melnele.trips.view.trip;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.melnele.trips.R;
import com.melnele.trips.model.Trip;
import com.melnele.trips.utils.DBUtil;
import com.melnele.trips.view.AlertActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;

import static com.melnele.trips.view.main.TripsAdapter.TRIP;

public class NoteActivity extends AppCompatActivity {
    private LinearLayout layoutList;
    private Trip trip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        trip = (Trip) getIntent().getSerializableExtra(TRIP);
        if (trip == null) {
            finish();
            return;
        }

        layoutList = findViewById(R.id.layout_list);
        TextView addItem = findViewById(R.id.plusTextView);
        Button saveNotesButton = findViewById(R.id.saveNotesButton);

        addItem.setOnClickListener(v -> {
            if (layoutList.getChildCount() < 10) {
                addView("");
            } else {
                Toast.makeText(NoteActivity.this, getString(R.string.max_notes), Toast.LENGTH_LONG).show();
            }
        });

        saveNotesButton.setOnClickListener(v -> {
            ArrayList<String> notes = new ArrayList<>();
            for (int i = 0; i < layoutList.getChildCount(); i++) {
                String note = ((EditText) layoutList.getChildAt(i).findViewById(R.id.editTextNote)).getText().toString();
                if (note != null && !note.isEmpty())
                    notes.add(note);
            }
            FirebaseDatabase database = DBUtil.getDB();
            DatabaseReference myRef = database.getReference()
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("trips").child(trip.getId()).child("notes");

            myRef.setValue(notes);
            trip.setNotes(notes);
            startAlarm();
            finish();
        });

        if (trip.getNotes() != null) {
            for (String note : trip.getNotes()) {
                addView(note);
            }
        }
    }

    private void addView(String text) {
        final View createView = getLayoutInflater().inflate(R.layout.note_item, layoutList, false);

        EditText editTextNote = createView.findViewById(R.id.editTextNote);
        editTextNote.setText(text);
        ImageView minusImage = createView.findViewById(R.id.minusImageView);
        minusImage.setOnClickListener(v -> removeView(createView));
        layoutList.addView(createView);
    }

    private void removeView(View view) {
        layoutList.removeView(view);
    }

    private void startAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), AlertActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(TRIP, trip);
        intent.putExtra(TRIP, bundle);
        Calendar date = Calendar.getInstance();
        date.setTime(trip.getTime());
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), trip.getId().hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (date.after(Calendar.getInstance())) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, date.getTimeInMillis(), pendingIntent);
        }
    }
}