package com.honeycomb.helper.Database.operations;

import android.util.Log;

import com.amazonaws.AmazonClientException;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;

import java.util.List;

/**
 * Created by Ash on 13/01/2017.
 */

public class Delete<T> extends baseAsyncTask<T, Void, Void>
{
    public Delete(Class<T> type)
    {
        super(type);
    }

    @SafeVarargs
    @Override
    protected final Void doInBackground(T... ts)
    {
        try
        {
            List<T> result = mapper.scan(type, new DynamoDBScanExpression());
            mapper.delete(result.get(0));
        }
        catch (AmazonClientException ex)
        {
            Log.e(TAG, "Failed deleting item: " + ex.getMessage(), ex);
        }
        Log.d(TAG, "Successfully deleted an item");
        return null;
    }
}
