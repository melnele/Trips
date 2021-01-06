package com.example.trips;

import android.content.Intent;
import android.os.Bundle;

import com.example.trips.ui.main.SectionsStateAdapter;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;


import android.view.View;

import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.List;

import static com.example.trips.ui.main.SectionsStateAdapter.TAB_TITLES;

public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.EmailBuilder().build(),
                    new AuthUI.IdpConfig.GoogleBuilder().build());

            startActivityForResult(AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build(), RC_SIGN_IN);
        } else {
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
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
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
                        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }
}