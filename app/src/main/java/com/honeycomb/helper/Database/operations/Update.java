package com.honeycomb.helper.Database.operations;

import android.util.Log;

import com.amazonaws.AmazonClientException;
import com.honeycomb.interfaces.AsyncResponse;

/**
 * Created by Ash on 23/01/2017.
 */

public class Update<T> extends baseAsyncTask<T, Void, Void>
{
    public Update(Class<T> type)
    {
        super(type);
    }

    @Override
    protected Void doInBackground(T... ts)
    {
        try
        {
            for(T t : ts)
            {
                mapper.save(t);
                Log.d(TAG, "Successfully updated an item");
            }
        }
        catch (AmazonClientException ex)
        {
            Log.e(TAG, "Failed updating item(s): " + ex.getMessage(), ex);
        }
        return null;
    }
}
