package com.example.trips.view;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.example.trips.R;
import com.example.trips.model.Trip;

import static com.example.trips.view.main.TripsAdapter.TRIP;

public class AlertActivity extends AppCompatActivity {
    public static final String channelID = "channelID";
    public static final String channelName = "Channel Name";
    private NotificationManager mManager;
    private int openedBefore = 0;
    private Trip trip;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getBundleExtra(TRIP);
        if (bundle != null) {
            trip = (Trip) bundle.getSerializable(TRIP);
        }
        if (trip == null) {
            Log.i("TAG", trip.toString());

            finish();
            return;
        }
        new AlertDialog.Builder(this)
                .setMessage(trip.getName())
                .setPositiveButton("Start", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + trip.getEndPoint().getLatLong().toString());
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);

                        finish();

//                        Intent bubbleIntent = new Intent(getApplicationContext(), BubbleService.class);
//                        startService(bubbleIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNeutralButton("Later", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            createChannel();
                        }

                        Intent notifyIntent = new Intent(getApplicationContext(), AlertActivity.class);
                        notifyIntent.putExtra(TRIP, bundle);
                        PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(),
                                22, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        NotificationCompat.Builder mBuilder;
                        mBuilder = new NotificationCompat.Builder(getApplicationContext(), channelID);
                        mBuilder.setContentText("Trip")
                                .setContentTitle("You are waiting for trip ")
                                .setSmallIcon(R.drawable.ic_launcher_background)
                                .setAutoCancel(true)
                                .setOngoing(true)
                                .setPriority(NotificationCompat.PRIORITY_MAX)
                                .setContentIntent(pIntent);
                        // NotificationManagerCompat managerCompat = NotificationManagerCompat.from( AlertActivity.this );
                        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        manager.notify(1, mBuilder.build());
                        finish();
                    }

                    @RequiresApi(Build.VERSION_CODES.O)
                    private void createChannel() {
                        NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_DEFAULT);
                        mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        mManager.createNotificationChannel(channel);
                    }
                }).create().show();
    }
}
