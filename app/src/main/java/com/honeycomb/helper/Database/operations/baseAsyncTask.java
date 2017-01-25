package com.honeycomb.helper.Database.operations;

import android.os.AsyncTask;

import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.honeycomb.interfaces.AsyncResponse;

/**
 * Created by Ash on 13/01/2017.
 */

public abstract class baseAsyncTask<Params, Progress, Result>
        extends AsyncTask<Params, Progress, Result>
{
    protected final String TAG = this.getClass().getSimpleName();
    protected final DynamoDBMapper mapper;
    protected Class<Params> type;
    protected AsyncResponse delegate;

    public baseAsyncTask(Class<Params> type)
    {
        this(type, null);
    }

    public baseAsyncTask(Class<Params> type, AsyncResponse delegate)
    {
        mapper = AWSMobileClient.defaultMobileClient().getDynamoDBMapper();
        this.type = type;
        this.delegate = delegate;
    }

    @Override
    protected void onPostExecute(Result result)
    {
        if (delegate != null)
        {
            delegate.onFinished(result);
        }
    }
}
