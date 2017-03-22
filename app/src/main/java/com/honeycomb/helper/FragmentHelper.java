package com.honeycomb.helper;

import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

/**
 * Created by Ash on 23/01/2017.
 */

public class FragmentHelper
{
    private FragmentActivity activity;

    public FragmentHelper(FragmentActivity activity)
    {
        this.activity = activity;
    }

    /**
     * Replaces current fragment
     * @param containerViewId
     * @param fragment
     */
    public void switchToFragment(@IdRes int containerViewId, Fragment fragment)
    {
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        ft.replace(containerViewId, fragment);
        ft.commit();
    }

    /**
     * Replaces current fragment - uses backstack
     * @param containerViewId
     * @param fragment
     * @param backStackName
     */
    public void switchToFragment(@IdRes int containerViewId, Fragment fragment, @Nullable String backStackName)
    {
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        ft.replace(containerViewId, fragment);
        ft.addToBackStack(backStackName);
        ft.commit();
    }
}
