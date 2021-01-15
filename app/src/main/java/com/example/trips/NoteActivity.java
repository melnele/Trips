package com.example.trips;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class NoteActivity extends AppCompatActivity implements View.OnClickListener {
    LinearLayout layoutList;
    TextView addItem ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        layoutList=findViewById(R.id.layout_list);
        addItem =findViewById(R.id.plusTextView);
        addItem.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(layoutList.getChildCount()<10)
        {
        addView();}
        else {
            Toast.makeText(this,"You Reached The Maximum of Notes Number.",Toast.LENGTH_LONG).show();
        }

    }

    private void addView() {
      final View createView = getLayoutInflater().inflate(R.layout.note_item,null,false);

      EditText addYourNote = createView.findViewById(R.id.editTextNOte);
      ImageView minusImage = createView.findViewById(R.id.minusImageView);
      minusImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeView(createView);
            }
        });
      layoutList.addView(createView);
      Log.v("Main",""+layoutList.getChildCount());
    }
    private void removeView(View view) {
        layoutList.removeView(view);

    }
}