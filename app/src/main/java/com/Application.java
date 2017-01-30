package com;

import android.support.multidex.MultiDexApplication;
import android.util.Log;

public class Application extends MultiDexApplication
{
    private static final String LOG_TAG = Application.class.getSimpleName();

    @Override
    public void onCreate()
    {
        Log.d(LOG_TAG, "Application Started");
        super.onCreate();
    }
}
