package com.honeycomb.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.amazonaws.models.nosql.TaskDO;
import com.honeycomb.R;
import com.honeycomb.helper.Database.operations.Get;
import com.honeycomb.helper.Database.operations.Update;
import com.honeycomb.interfaces.AsyncResponse;

/**
 * Created by Ash on 20/01/2017.
 */

public class FragTask extends baseFragment implements AsyncResponse
{
    private boolean isEditMode = false;

    private MenuItem edit;
    private MenuItem confirm;

    private static TaskDO currentTask;

    public static FragTask newInstance(@Nullable TaskDO task)
    {
        FragTask newFragment = new FragTask();
        currentTask = task;
        return newFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_task, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        getToolbar().setTitle("Tasks");

        new Get<>(TaskDO.class, this, currentTask.getProjectID(), currentTask.getTaskID()).execute();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.task, menu);
        edit = menu.findItem(R.id.action_edit);
        confirm = menu.findItem(R.id.action_confirm);
        setEditMode(isEditMode);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if(id == R.id.action_edit)
        {
            setEditMode(true);
            return true;
        }
        else if(id == R.id.action_confirm)
        {
            setEditMode(false);
            EditText txtDescription = (EditText)getView().findViewById(R.id.txtDescription);
            currentTask.setDescription(txtDescription.getText().toString());
            new Update<>(TaskDO.class).execute(currentTask);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setEditMode(boolean isEditMode)
    {
        this.isEditMode = isEditMode;
        edit.setVisible(!isEditMode);
        confirm.setVisible(isEditMode);
        getView().findViewById(R.id.txtTaskName).setEnabled(isEditMode);
        getView().findViewById(R.id.txtDescription).setEnabled(isEditMode);
    }

    @Override
    public void onFinished(Object result)
    {
        currentTask = (TaskDO)result;

        if(currentTask != null)
        {
            EditText txtName = (EditText)getView().findViewById(R.id.txtTaskName);
            txtName.setText(currentTask.getName());
            EditText txtDescription = (EditText)getView().findViewById(R.id.txtDescription);
            txtDescription.setText(currentTask.getDescription());
        }
        else { Log.d(TAG, "Task is null - Starting new..."); }
    }
}
