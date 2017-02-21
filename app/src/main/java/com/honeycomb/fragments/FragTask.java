package com.honeycomb.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.honeycomb.R;
import com.honeycomb.helper.Database.Database;
import com.honeycomb.helper.Database.objects.Milestone;
import com.honeycomb.helper.Database.objects.Task;
import com.honeycomb.helper.Time;
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
    private LinearLayout llDeadline;
    private TextView txtDeadline;
    private LinearLayout llMilestones;
    private RecyclerView rvMilestones;

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

        FloatingActionButton fabAddDeadline = new FloatingActionButton(getContext());
        if(currentTask.getDeadline() == null) { fabAddDeadline.setLabelText("Add Deadline"); }
        else { fabAddDeadline.setLabelText("Change Deadline"); }
        fabAddDeadline.setOnClickListener(addDeadline);
        fabs.add(fabAddDeadline);

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
            Database.root.child(Task.TABLE_NAME)
                    .child(currentTask.getTaskID())
                    .setValue(currentTask);
            Log.d(TAG, "Saved Task");
        }
    }

    protected void loadIO()
    {
        txtName = initEditText(R.id.txtName);
        txtDescription = initEditText(R.id.txtDescription);

        llDeadline = (LinearLayout)getView().findViewById(R.id.llDeadline);
        llDeadline.setVisibility(View.GONE);
        txtDeadline = (TextView)getView().findViewById(R.id.txtDeadline);

        llMilestones = (LinearLayout)getView().findViewById(R.id.llMilestones);
        llMilestones.setVisibility(View.GONE);
        rvMilestones = (RecyclerView)getView().findViewById(R.id.rvMilestones);
    }

    private void loadTask()
    {
        DatabaseReference dbRef = Database.root.child(Task.TABLE_NAME)
                .child(currentTask.getTaskID());
        db.addValueEventListener(TAG, dbRef, new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                currentTask = dataSnapshot.getValue(Task.class);
                txtName.setText(currentTask.getName());
                txtDescription.setText(currentTask.getDescription());

                if(currentTask.getDeadline() == null)
                {
                    llDeadline.setVisibility(View.GONE);
                }
                else
                {
                    txtDeadline.setText(Time.toWordyReadable(new DateTime(currentTask.getDeadline())));
                    llDeadline.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    private void loadMilestones()
    {
        final MilestoneAdapter milestoneAdapter = new MilestoneAdapter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        rvMilestones.setLayoutManager(linearLayoutManager);
        rvMilestones.setAdapter(milestoneAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvMilestones.getContext(),
                linearLayoutManager.getOrientation());
        rvMilestones.addItemDecoration(dividerItemDecoration);

        milestoneAdapter.getClickSubject()
                .subscribe(this::switchToMilestone);

        Query queryRef = Database.root.child(Milestone.TABLE_NAME)
                .orderByChild("taskID")
                .equalTo(currentTask.getTaskID());

        db.addValueEventListener(TAG, queryRef, new ValueEventListener()
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
                if(milestones.size() == 0) { llMilestones.setVisibility(View.GONE); }
                else { llMilestones.setVisibility(View.VISIBLE); }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    private void switchToMilestone(Milestone milestone)
    {
        fragmentHelper.switchToFragment(R.id.fragment_container, FragMilestone.newInstance(milestone), null);
    }

    private View.OnClickListener addDeadline = v ->
    {
        final DateTime[] dateTime = {currentTask.getDeadline() == null ? new DateTime() : currentTask.getDeadlineAsDateTime()};

        LayoutInflater inflater = getActivity().getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setTitle("Add Deadline")
                .setView(inflater.inflate(R.layout.dialog_add_deadline, null))
                .setNegativeButton("Close", (dialog, which) -> dialog.cancel());
        Dialog d = builder.show();

        Button butDate = (Button)d.findViewById(R.id.butDate);
        butDate.setText("Change Date");
        Button butTime = (Button)d.findViewById(R.id.butTime);
        butTime.setText("Change Time");

        butDate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                new DatePickerDialog(getContext(), (view, year, monthOfYear, dayOfMonth) ->
                {
                    currentTask.setDeadline(Time.dateTimeWithNewDate(currentTask.getDeadlineAsDateTime(), year, monthOfYear + 1, dayOfMonth).toString());
                    Database.root.child(Task.TABLE_NAME)
                            .child(currentTask.getTaskID())
                            .child("deadline")
                            .setValue(currentTask.getDeadline());
                    dateTime[0] = currentTask.getDeadlineAsDateTime();
                }, dateTime[0].getYear(), dateTime[0].getMonthOfYear() - 1, dateTime[0].getDayOfMonth()).show();
            }
        });

        butTime.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                new TimePickerDialog(getContext(), (view, hourOfDay, minute) ->
                {
                    dateTime[0] = changeDeadline(dateTime[0].getYear(), dateTime[0].getMonthOfYear(), dateTime[0].getDayOfMonth(), hourOfDay, minute);
                }, dateTime[0].getHourOfDay(), dateTime[0].getMinuteOfHour(), true).show();
            }
        });
    };

    private DateTime changeDeadline(int year, int month, int day, int hour, int minute)
    {
        DateTime newDT = new DateTime(year, month, day, hour, minute);
        currentTask.setDeadline(newDT.toString());
        Database.root.child(Task.TABLE_NAME)
                .child(currentTask.getTaskID())
                .child("deadline")
                .setValue(currentTask.getDeadline());
        return newDT;
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
                    milestone.setMilestoneID(Database.root.child(Milestone.TABLE_NAME).push().getKey());
                    milestone.setName(((EditText)ad.findViewById(R.id.txtName)).getText().toString());
                    milestone.setDescription(((EditText)ad.findViewById(R.id.txtDescription)).getText().toString());
                    milestone.setCompleted(false);
                    Database.root.child(Milestone.TABLE_NAME).child(milestone.getMilestoneID()).setValue(milestone);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    };
}
