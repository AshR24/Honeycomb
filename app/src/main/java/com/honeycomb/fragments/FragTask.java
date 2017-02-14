package com.honeycomb.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.honeycomb.R;
import com.honeycomb.helper.Database.objects.Milestone;
import com.honeycomb.helper.Database.objects.Task;
import com.honeycomb.helper.adapters.MilestoneAdapter;

import org.joda.time.DateTime;

import java.util.ArrayList;

/**
 * Created by Ash on 20/01/2017.
 */

public class FragTask extends baseEditFragment
{
    private static Task currentTask;

    private EditText txtName;
    private EditText txtDescription;
    private EditText txtDeadline;

    public static FragTask newInstance(@Nullable Task task)
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
        getToolbar().setTitle("Task");

        loadTask();
        loadMilestones();

        ArrayList<FloatingActionButton> fabs = new ArrayList<>();
        FloatingActionButton fab = new FloatingActionButton(getContext());
        fab.setLabelText("Add Milestone");
        fab.setOnClickListener(addMilestone);
        fabs.add(fab);
        if(currentTask.getDeadline() == null)
        {
            FloatingActionButton fabAddDeadline = new FloatingActionButton(getContext());
            fabAddDeadline.setLabelText("Add Deadline");
            fabAddDeadline.setOnClickListener(addDeadline);
            fabs.add(fabAddDeadline);
        }
        setFam(fabs);
    }

    @Override
    protected void setEditMode(boolean isEditMode)
    {
        super.setEditMode(isEditMode);
        if(!isEditMode)
        {
            currentTask.setName(txtName.getText().toString());
            currentTask.setDescription(txtDescription.getText().toString());
            dbRoot.child(Task.TABLE_NAME).child(currentTask.getTaskID()).setValue(currentTask);
            Log.d(TAG, "Saved Task");
        }
    }

    protected void loadIO()
    {
        txtName = initEditText(R.id.txtName);
        txtDescription = initEditText(R.id.txtDescription);
        txtDeadline = initEditText(R.id.txtDeadline);
    }

    private void loadTask()
    {
        dbRoot.child(Task.TABLE_NAME).child(currentTask.getTaskID()).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                currentTask = dataSnapshot.getValue(Task.class);
                txtName.setText(currentTask.getName());
                txtDescription.setText(currentTask.getDescription());

                if(currentTask.getDeadline() == null)
                {
                    txtDeadline.setVisibility(View.INVISIBLE);
                }
                else
                {
                    txtDeadline.setVisibility(View.VISIBLE);
                    txtDeadline.setText(currentTask.getDeadline());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }

    private void loadMilestones()
    {
        final MilestoneAdapter milestoneAdapter = new MilestoneAdapter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        RecyclerView rv = (RecyclerView)getView().findViewById(R.id.rvMilestones);
        rv.setLayoutManager(linearLayoutManager);
        rv.setAdapter(milestoneAdapter);

        milestoneAdapter.getClickSubject()
                .subscribe(this::switchToMilestone);

        Query queryRef = dbRoot.child(Milestone.TABLE_NAME).orderByChild("taskID").equalTo(currentTask.getTaskID()); // TODO, write a DB helper class
        queryRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                ArrayList<Milestone> milestones = new ArrayList<>();
                for(DataSnapshot snap : dataSnapshot.getChildren())
                {
                    Milestone milestone = snap.getValue(Milestone.class);
                    milestones.add(milestone);
                }
                milestoneAdapter.update(milestones);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }

    private void switchToMilestone(Milestone milestone)
    {
        fragmentHelper.switchToFragment(R.id.fragment_container, FragMilestone.newInstance(milestone), null);
    }

    private View.OnClickListener addMilestone = v ->
    {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setTitle("Add Milestone")
                .setView(inflater.inflate(R.layout.dialog_add_task, null))
                .setPositiveButton("Add", (dialog, which) -> {
                    AlertDialog ad = (AlertDialog)dialog;
                    Log.d(TAG, "Adding Milestone");
                    Milestone milestone = new Milestone();
                    milestone.setTaskID(currentTask.getTaskID());
                    milestone.setMilestoneID(dbRoot.child(Milestone.TABLE_NAME).push().getKey());
                    milestone.setName(((EditText)ad.findViewById(R.id.txtName)).getText().toString());
                    milestone.setDescription(((EditText)ad.findViewById(R.id.txtDescription)).getText().toString());
                    milestone.setCompleted(false);
                    dbRoot.child(Milestone.TABLE_NAME).child(milestone.getMilestoneID()).setValue(milestone);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    };

    private View.OnClickListener addDeadline = v ->
    {
        Log.d(TAG, "Adding Milestone");
        currentTask.setDeadline(new DateTime().toString());

        dbRoot.child(Task.TABLE_NAME).child(currentTask.getTaskID()).child("deadline").setValue(currentTask.getDeadline());
    };
}
