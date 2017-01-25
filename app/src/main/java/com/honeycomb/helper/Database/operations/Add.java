package com.honeycomb.helper.Database.operations;

import android.util.Log;

import com.amazonaws.AmazonClientException;

/**
 * Created by Ash on 13/01/2017.
 */

public class Add<T> extends baseAsyncTask<T, Void, Void>
{
    public Add(Class<T> type)
    {
        super(type);
    }

    @SafeVarargs
    @Override
    protected final Void doInBackground(T... ts)
    {
        try
        {
            for (T t : ts)
            {
                mapper.save(t);
                Log.d(TAG, "Successfully saved an item");
            }
        }
        catch (AmazonClientException ex)
        {
            Log.e(TAG, "Failed adding item(s): " + ex.getMessage(), ex);
        }
        return null;
    }
}
