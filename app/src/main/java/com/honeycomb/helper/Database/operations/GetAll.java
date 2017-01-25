package com.honeycomb.helper.Database.operations;

import android.util.Log;

import com.amazonaws.AmazonClientException;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.honeycomb.interfaces.AsyncResponse;

import java.util.List;

/**
 * Created by Ash on 13/01/2017.
 */

public class GetAll<T> extends baseAsyncTask<T, Void, List<T>>
{
    public GetAll(Class<T> type, AsyncResponse delegate)
    {
        super(type, delegate);
    }

    @SafeVarargs
    @Override
    protected final List<T> doInBackground(T... ts)
    {
        List<T> result = null;

        try
        {
            result = mapper.scan(type, new DynamoDBScanExpression());
        }
        catch(AmazonClientException ex) { Log.e(TAG, "Failed getting item(s): " + ex.getMessage(), ex); }
        Log.d(TAG, "Got " + result.size() + " item(s)");
        return result;
    }
}
