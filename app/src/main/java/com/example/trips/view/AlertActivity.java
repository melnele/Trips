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

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.example.trips.R;

public class AlertActivity extends AppCompatActivity {
    public static final String channelID = "channelID";
    public static final String channelName = "Channel Name";
    private NotificationManager mManager;
    private int openedBefore = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new AlertDialog.Builder(this)
                .setMessage("Alarm!")
                .setCancelable(false)
                .setPositiveButton("Start", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent((Intent.ACTION_VIEW));
                        intent.setData(Uri.parse("geo:19.076,72.8777"));
                        startActivity(intent);
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
                        PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(),
                                22, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        NotificationCompat.Builder mBuilder;
                        mBuilder = new NotificationCompat.Builder(getApplicationContext(), channelID);
                        mBuilder.setContentText("Trip")
                                .setContentTitle("You are waiting for trip ")
                                .setSmallIcon(R.drawable.ic_launcher_background)
                                .setAutoCancel(false)
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
