package com.honeycomb.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.honeycomb.helper.FragmentHelper;

/**
 * Created by Ash on 20/01/2017.
 */

public abstract class baseFragment extends Fragment
{
    protected final String TAG = this.getClass().getSimpleName();

    protected FragmentHelper fragmentHelper;
    protected DatabaseReference dbRoot;

    protected ActionBar getToolbar()
    {
        return ((AppCompatActivity)getActivity()).getSupportActionBar();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        fragmentHelper = new FragmentHelper(getActivity());
        dbRoot = FirebaseDatabase.getInstance().getReference();
        setHasOptionsMenu(true);
    }
}
