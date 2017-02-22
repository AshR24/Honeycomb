package com.honeycomb.helper.Database;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ash on 18/02/2017.
 */

public class Database
{
    public static final String TAG = Database.class.getSimpleName();

    public static final DatabaseReference root = FirebaseDatabase.getInstance().getReference();
    private final HashMap<Query, ValueEventListener> valueEventListeners;

    public Database()
    {
        valueEventListeners = new HashMap<>();
    }

    public <T extends Query> void addValueEventListener(String whereAreWe, T dbRef, ValueEventListener vel)
    {
        dbRef.addValueEventListener(vel);
        valueEventListeners.put(dbRef, vel);
        Log.d(TAG, whereAreWe + " Added VEL, new size: " + valueEventListeners.size());
    }

    public void clearEventListeners(String whereAreWe)
    {
        Log.d(TAG, whereAreWe + " Clearing event listeners (clearing " + valueEventListeners.size() + ")");

        for(Map.Entry<Query, ValueEventListener> entry : valueEventListeners.entrySet())
        {
            entry.getKey().removeEventListener(entry.getValue());
        }
        valueEventListeners.clear();
    }
}
