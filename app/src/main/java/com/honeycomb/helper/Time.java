package com.honeycomb.helper;

/**
 * Created by Ash on 13/01/2017.
 */

public final class Time
{
    public static double getCurrentUnixTimestamp()
    {
        return System.currentTimeMillis() / 1000L;
    }
}
