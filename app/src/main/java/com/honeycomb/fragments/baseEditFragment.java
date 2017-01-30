package com.honeycomb.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.honeycomb.R;

/**
 * Created by Ash on 29/01/2017.
 */

public abstract class baseEditFragment extends baseFragment
{
    protected boolean isEditMode = false;
    protected MenuItem actionEdit;
    protected MenuItem actionConfirm;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        loadIO();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.edit, menu);
        actionEdit = menu.findItem(R.id.action_edit);
        actionConfirm = menu.findItem(R.id.action_confirm);
        setEditMode(false, false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if(id == R.id.action_edit)
        {
            setEditMode(true, false);
            return true;
        }
        else if(id == R.id.action_confirm)
        {
            setEditMode(false, true);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void setEditMode(boolean isEditMode, boolean needsUpdate)
    {
        this.isEditMode = isEditMode;
        actionEdit.setVisible(!isEditMode);
        actionConfirm.setVisible(isEditMode);
    }

    protected abstract void loadIO();
}
