package com.honeycomb.fragments;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.honeycomb.R;

/**
 * Created by Ash on 29/01/2017.
 */

public abstract class baseEditFragment extends baseFragment
{
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
        actionConfirm = menu.findItem(R.id.action_confirm)
                .setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if(id == R.id.action_confirm)
        {
            getView().clearFocus();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void setEditMode(boolean isEditMode)
    {
        actionConfirm.setVisible(isEditMode);
    }

    protected EditText initEditText(@IdRes int id)
    {
        final EditText result = (EditText)getView().findViewById(id);
        result.setOnFocusChangeListener(focusChanged);

        return result;
    }

    protected View.OnFocusChangeListener focusChanged = (v, hasFocus) -> setEditMode(hasFocus);

    protected abstract void loadIO();
}
