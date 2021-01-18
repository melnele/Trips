package com.example.trips.view.trip;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trips.R;
import com.example.trips.model.Trip;
import com.example.trips.utils.DBUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static com.example.trips.view.main.TripsAdapter.TRIP;

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
                Toast.makeText(NoteActivity.this, "You Reached The Maximum of Notes Number.", Toast.LENGTH_LONG).show();
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
}