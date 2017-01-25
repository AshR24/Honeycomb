package com.honeycomb.interfaces;

/**
 * Created by Ash on 11/01/2017.
 */

public interface AsyncResponse
{
    <T> void onFinished(T result);
}
