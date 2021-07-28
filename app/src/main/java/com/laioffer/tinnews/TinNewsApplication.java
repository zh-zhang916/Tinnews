package com.laioffer.tinnews;

import android.app.Application;

import androidx.room.Room;

import com.facebook.stetho.Stetho;
import com.laioffer.tinnews.database.TinNewsDatabase;

public class TinNewsApplication extends Application {
    // static的话也就变成一个singleton，永远放在内存上.
    // 不需要new.
    private static TinNewsDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        // initialize database class
        database = Room.databaseBuilder(this, TinNewsDatabase.class, "tinnews_db").build();
    }

    public static TinNewsDatabase getDatabase() {
        return database;
    }
}
