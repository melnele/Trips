package com.example.trips.view.main;

import android.content.Intent;
import android.os.Bundle;

import com.example.trips.R;
import com.example.trips.view.trip.AddEditTripActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;

import static com.example.trips.view.main.SectionsStateAdapter.TAB_TITLES;
import static com.example.trips.view.trip.AddEditTripActivity.ADD_TRIP;
import static com.example.trips.view.trip.AddEditTripActivity.TRIP_ACTION;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        setUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sync:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.signOut:
                FirebaseAuth.getInstance().signOut();
                finish();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    private void setUI() {
        SectionsStateAdapter sectionsStateAdapter = new SectionsStateAdapter(this);

        ViewPager2 viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsStateAdapter);
        TabLayout tabs = findViewById(R.id.tabs);

        new TabLayoutMediator(tabs, viewPager,
                (tab, position) -> tab.setText(TAB_TITLES[position])
        ).attach();

        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Intent intent = new Intent(MainActivity.this, AddEditTripActivity.class);
                intent.putExtra(TRIP_ACTION, ADD_TRIP);
                startActivity(intent);
            }
        });
    }
}