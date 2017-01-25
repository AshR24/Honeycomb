package com.honeycomb.helper.Database.operations;

import android.util.Log;

import com.amazonaws.AmazonClientException;
import com.honeycomb.interfaces.AsyncResponse;

/**
 * Created by Ash on 13/01/2017.
 */

public class Get<T> extends baseAsyncTask<T, Void, T>
{
    private String hash;
    private String range;

    public Get(Class<T> type, AsyncResponse delegate, String hash, String range)
    {
        super(type, delegate);
        this.hash = hash;
        this.range = range;
    }


    @SafeVarargs
    @Override
    protected final T doInBackground(T... ts)
    {
        T result = null;
        try
        {
            result = mapper.load(type, hash, range);
        }
        catch(AmazonClientException ex) { Log.e(TAG, "Failed getting item: " + ex.getMessage(), ex); }
        Log.d(TAG, "Got item");
        return result;
    }
}
