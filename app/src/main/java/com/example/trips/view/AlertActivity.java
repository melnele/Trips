package com.example.trips.view;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.example.trips.R;
import com.example.trips.model.Trip;
import com.example.trips.model.TripStatus;
import com.example.trips.utils.DBUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.trips.view.main.TripsAdapter.TRIP;

public class AlertActivity extends AppCompatActivity {
    public static final String channelID = "channelID";
    public static final String channelName = "Channel Name";
    private NotificationManager mManager;
    private Trip trip;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getBundleExtra(TRIP);
        if (bundle != null) {
            trip = (Trip) bundle.getSerializable(TRIP);
        }
        if (trip == null) {
            finish();
            return;
        }

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        r.play();

        new AlertDialog.Builder(this)
                .setMessage(trip.getName())
                .setPositiveButton(R.string.start, (dialog, which) -> {
                    r.stop();
                    startTrip();
                    finish();
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    r.stop();
                    FirebaseDatabase database = DBUtil.getDB();
                    DatabaseReference myRef = database.getReference()
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("trips").child(trip.getId()).child("status");
                    myRef.setValue(TripStatus.CANCELLED);
                    finish();
                })
                .setNeutralButton(R.string.later, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        r.stop();
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

    private void startTrip() {
        FirebaseDatabase database = DBUtil.getDB();
        DatabaseReference myRef = database.getReference()
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("trips").child(trip.getId()).child("status");
        myRef.setValue(TripStatus.DONE);

        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + trip.getEndPoint().getLatLong().toString());
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);

        if (trip.getNotes() != null) {
            Intent bubbleIntent = new Intent(getApplicationContext(), BubbleService.class);
            bubbleIntent.putExtra(TRIP, trip);
            startService(bubbleIntent);
        }
    }
}
