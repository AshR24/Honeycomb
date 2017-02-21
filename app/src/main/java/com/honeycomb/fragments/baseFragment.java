package com.honeycomb.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.honeycomb.R;
import com.honeycomb.helper.Database.Database;
import com.honeycomb.helper.FragmentHelper;

import java.util.ArrayList;

/**
 * Created by Ash on 20/01/2017.
 */

public abstract class baseFragment extends Fragment
{
    protected final String TAG = this.getClass().getSimpleName();

    protected FragmentHelper fragmentHelper;
    protected Database db;

    protected FloatingActionMenu fam;
    protected ArrayList<FloatingActionButton> fabs;

    protected ActionBar getToolbar()
    {
        return ((AppCompatActivity)getActivity()).getSupportActionBar();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        fragmentHelper = new FragmentHelper(getActivity());
        db = new Database();
        setHasOptionsMenu(true);
        initFam();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        db.clearEventListeners(this.getClass().getSimpleName());
    }

    private void initFam()
    {
        fam = (FloatingActionMenu)getActivity().findViewById(R.id.fab_Menu);
        fabs = new ArrayList<>();
    }

    public void setFam(ArrayList<FloatingActionButton> fabs)
    {
        fam.removeAllMenuButtons();
        for(FloatingActionButton fab : fabs)
        {
            fam.addMenuButton(fab);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}
