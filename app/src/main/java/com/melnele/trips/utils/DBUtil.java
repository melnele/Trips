package com.melnele.trips.utils;

import com.google.firebase.database.FirebaseDatabase;

public class DBUtil {
    private static FirebaseDatabase mDatabase;

    public static FirebaseDatabase getDB() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
        }
        return mDatabase;
    }
}
