package com.example.trips.view.main;

import android.content.Intent;
import android.os.Bundle;

import com.example.trips.R;
import com.example.trips.view.addtrip.CreateTripItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;


import android.view.View;

import com.google.android.material.tabs.TabLayoutMediator;

import static com.example.trips.view.main.SectionsStateAdapter.TAB_TITLES;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUI();
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
                Intent intent = new Intent(MainActivity.this, CreateTripItem.class);
                startActivity(intent);
            }
        });
    }
}