package com.honeycomb.helper.Database;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.disposables.Disposable;

/**
 * Created by Ash on 18/02/2017.
 */

public class Database
{
    public static final String TAG = Database.class.getSimpleName();

    public static final DatabaseReference root = FirebaseDatabase.getInstance().getReference();
    private final HashMap<Query, ValueEventListener> valueEventListeners;
    private final ArrayList<Disposable> disposables;

    public Database()
    {
        valueEventListeners = new HashMap<>();
        disposables = new ArrayList<>();
    }

    /**
     * Adds a value listener to the database - for use with garbage collection
     * @param dbRef
     * @param vel
     * @param <T>
     */
    public <T extends Query> void addValueEventListener(T dbRef, ValueEventListener vel)
    {
        dbRef.addValueEventListener(vel);
        valueEventListeners.put(dbRef, vel);
        Log.d(TAG, " Added VEL, new size: " + valueEventListeners.size());
    }

    /**
     * Removes all the value listeners from the database
     */
    public void clearEventListeners()
    {
        Log.d(TAG, "Clearing event listeners (clearing " + valueEventListeners.size() + ")");

        for(Map.Entry<Query, ValueEventListener> entry : valueEventListeners.entrySet())
        {
            entry.getKey().removeEventListener(entry.getValue());
        }
        valueEventListeners.clear();
    }

    /**
     * Adds a subscriber to a given subject - for use with garbage collection
     * @param disposable
     */
    public void addSubscriber(Disposable disposable)
    {
        disposables.add(disposable);
        Log.d(TAG, " Added subscriber, new size: " + disposables.size());
    }

    /**
     * Removes all subscribers from a given subject
     */
    public void clearSubscribers()
    {
        Log.d(TAG, "Disposing subscribers (clearing " + disposables.size() + ")");

        for(Disposable d : disposables)
        {
            d.dispose();
        }
    }
}
