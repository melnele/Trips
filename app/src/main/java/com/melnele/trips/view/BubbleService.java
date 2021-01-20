package com.melnele.trips.view;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.melnele.trips.R;
import com.melnele.trips.model.Trip;

import java.util.Calendar;

import static com.melnele.trips.view.main.TripsAdapter.TRIP;

public class BubbleService extends Service {
    private View mFloatingView;
    private WindowManager windowManager;
    private ImageView imageClose;
    private float height, width;
    private View collapsedView;
    private View expandedView;
    private Boolean expanded;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Trip trip = (Trip) intent.getSerializableExtra(TRIP);
        if (trip == null) {
            stopSelf();
            return START_NOT_STICKY;
        }

        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }
        // inflate the layout
        final ViewGroup nullParent = null;
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.bubble, nullParent);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT
                , WindowManager.LayoutParams.WRAP_CONTENT, LAYOUT_FLAG, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        //initial position
        layoutParams.gravity = Gravity.TOP | Gravity.END;
        layoutParams.x = 0;
        layoutParams.y = 100;

        //layout params for close button
        WindowManager.LayoutParams imageParams = new WindowManager.LayoutParams(140, 140, LAYOUT_FLAG, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        imageParams.gravity = Gravity.BOTTOM | Gravity.CENTER;
        imageParams.y = 100;
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        imageClose = new ImageView(this);
        imageClose.setImageResource(R.drawable.x);
        imageClose.setVisibility(View.INVISIBLE);
        windowManager.addView(imageClose, imageParams);
        windowManager.addView(mFloatingView, layoutParams);
        mFloatingView.setVisibility(View.VISIBLE);

        collapsedView = mFloatingView.findViewById(R.id.icon_container);
        expandedView = mFloatingView.findViewById(R.id.expanded_container);
        LinearLayout noteList = mFloatingView.findViewById(R.id.note_layout);
        if (trip.getNotes() != null) {
            for (String note : trip.getNotes()) {
                CheckBox checkBox = new CheckBox(this);
                checkBox.setText(note);
                noteList.addView(checkBox);
            }
        }

//        height = windowManager.getDefaultDisplay().getHeight();
//        width = windowManager.getDefaultDisplay().getWidth();

//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
//        height = displayMetrics.heightPixels;
//        width = displayMetrics.widthPixels;

        Point size = new Point();
        windowManager.getDefaultDisplay().getSize(size);
        height = size.y;
        width = size.x;

        TextView closeButtonCollapsed = mFloatingView.findViewById(R.id.trip); //to close the table
        closeButtonCollapsed.setOnClickListener(view -> {
            expandedView.setVisibility(View.GONE);
            collapsedView.setVisibility(View.VISIBLE);
        });

        //drag movement for the bubble
        mFloatingView.setOnTouchListener(new View.OnTouchListener() {
            int initialX, initialY;
            float initialTouchX, initialTouchY;
            long startClickTime;
            final int MAX_CLICK_DURATION = 200;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch ((motionEvent.getAction())) {
                    case MotionEvent.ACTION_DOWN:
                        startClickTime = Calendar.getInstance().getTimeInMillis();
                        imageClose.setVisibility(View.VISIBLE);
                        initialX = layoutParams.x;
                        initialY = layoutParams.y;

                        //touch position
                        initialTouchX = motionEvent.getRawX();
                        initialTouchY = motionEvent.getRawY();
                        return true;

                    case MotionEvent.ACTION_UP:
                        view.performClick();
                        long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                        imageClose.setVisibility(View.GONE);

                        layoutParams.x = initialX + (int) (initialTouchX - motionEvent.getRawX());
                        layoutParams.y = initialY + (int) (motionEvent.getRawY() - initialTouchY);

                        if (!(clickDuration < MAX_CLICK_DURATION)) {
                            if (layoutParams.y > (height * 0.6)) {
                                stopSelf();
                            }
                        }
                        int Xdiff = (int) (motionEvent.getRawX() - initialTouchX);
                        int Ydiff = (int) (motionEvent.getRawY() - initialTouchY);
                        if (Xdiff < 10 && Ydiff < 10) {
                            if (isViewCollapsed()) {
                                //When user clicks on the image view of the collapsed layout,
                                //visibility of the collapsed layout will be changed to "View.GONE"
                                //and expanded view will become visible.
                                collapsedView.setVisibility(View.GONE);
                                expandedView.setVisibility(View.VISIBLE);
                                expanded = true;
                            }
                        }
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        //calculate x & y coordinates of view
                        layoutParams.x = initialX + (int) (initialTouchX - motionEvent.getRawX());
                        layoutParams.y = initialY + (int) (motionEvent.getRawY() - initialTouchY);

                        //update layout with new coordinates
                        windowManager.updateViewLayout(mFloatingView, layoutParams);

                        if (layoutParams.y > (height * 0.7)) {
                            imageClose.setImageResource(R.drawable.iconsclose30);
                        } else {
                            imageClose.setImageResource(R.drawable.x);
                        }
                        return true;
                }
                return false;
            }
        });
        return START_STICKY;
    }

    private boolean isViewCollapsed() {
        return mFloatingView == null || mFloatingView.findViewById(R.id.icon_container).getVisibility() == View.VISIBLE;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView != null) {
            windowManager.removeView((mFloatingView));
        }
        if (imageClose != null) {
            windowManager.removeView(imageClose);
        }
    }
}
