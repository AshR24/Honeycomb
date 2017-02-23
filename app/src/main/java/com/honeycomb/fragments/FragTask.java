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
import android.widget.ArrayAdapter;
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
import com.honeycomb.helper.Database.objects.User;
import com.honeycomb.helper.Time;
import com.honeycomb.helper.adapters.MilestoneAdapter;
import com.hootsuite.nachos.NachoTextView;
import com.hootsuite.nachos.chip.ChipInfo;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ash on 20/01/2017.
 */

public class FragTask extends baseEditFragment
{
    private static Task sCurrentTask;

    private EditText mTxtName;
    private EditText mTxtDescription;
    private LinearLayout mLlDeadline;
    private TextView mTxtDeadline;
    private LinearLayout mLlMilestone;
    private RecyclerView mRvMilestones;

    private ArrayList<User> users;

    public static FragTask newInstance(@Nullable Task task)
    {
        FragTask newFragment = new FragTask();
        sCurrentTask = task;
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
        loadMembers();

        ArrayList<FloatingActionButton> fabs = new ArrayList<>();
        FloatingActionButton fabAddMilestone = new FloatingActionButton(getContext());
        fabAddMilestone.setLabelText("Add Milestone");
        fabAddMilestone.setOnClickListener(addMilestone);
        fabs.add(fabAddMilestone);

        FloatingActionButton fabAddDeadline = new FloatingActionButton(getContext());
        if(sCurrentTask.getDeadline() == null) { fabAddDeadline.setLabelText("Add Deadline"); }
        else { fabAddDeadline.setLabelText("Change Deadline"); }
        fabAddDeadline.setOnClickListener(addDeadline);
        fabs.add(fabAddDeadline);

        FloatingActionButton fabAddUser = new FloatingActionButton(getContext());
        fabAddUser.setLabelText("Add User");
        fabAddUser.setOnClickListener(addUser);
        fabs.add(fabAddUser);

        setFam(fabs);
    }

    @Override
    protected void setEditMode(boolean isEditMode)
    {
        super.setEditMode(isEditMode);
        if(!isEditMode)
        {
            sCurrentTask.setName(mTxtName.getText().toString());
            sCurrentTask.setDescription(mTxtDescription.getText().toString());
            Database.root.child(Task.TABLE_NAME)
                    .child(sCurrentTask.getTaskID())
                    .setValue(sCurrentTask);
            Log.d(TAG, "Saved Task");
        }
    }

    protected void loadIO()
    {
        mTxtName = initEditText(R.id.txtName);
        mTxtDescription = initEditText(R.id.txtDescription);

        mLlDeadline = (LinearLayout)getView().findViewById(R.id.llDeadline);
        mLlDeadline.setVisibility(View.GONE);
        mTxtDeadline = (TextView)getView().findViewById(R.id.txtDeadline);

        mLlMilestone = (LinearLayout)getView().findViewById(R.id.llMilestones);
        mLlMilestone.setVisibility(View.GONE);
        mRvMilestones = (RecyclerView)getView().findViewById(R.id.rvMilestones);
    }

    private void loadTask()
    {
        DatabaseReference dbRef = Database.root.child(Task.TABLE_NAME)
                .child(sCurrentTask.getTaskID());
        db.addValueEventListener(TAG, dbRef, new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                sCurrentTask = dataSnapshot.getValue(Task.class);
                mTxtName.setText(sCurrentTask.getName());
                mTxtDescription.setText(sCurrentTask.getDescription());

                if(sCurrentTask.getDeadline() == null)
                {
                    mLlDeadline.setVisibility(View.GONE);
                }
                else
                {
                    mTxtDeadline.setText(Time.toWordyReadable(new DateTime(sCurrentTask.getDeadline())));
                    mLlDeadline.setVisibility(View.VISIBLE);
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

        mRvMilestones.setLayoutManager(linearLayoutManager);
        mRvMilestones.setAdapter(milestoneAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRvMilestones.getContext(),
                linearLayoutManager.getOrientation());
        mRvMilestones.addItemDecoration(dividerItemDecoration);

        milestoneAdapter.getClickSubject()
                .subscribe(this::switchToMilestone);

        Query queryRef = Database.root.child(Milestone.TABLE_NAME)
                .orderByChild("taskID")
                .equalTo(sCurrentTask.getTaskID());

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
                if(milestones.size() == 0) { mLlMilestone.setVisibility(View.GONE); }
                else { mLlMilestone.setVisibility(View.VISIBLE); }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    private void loadMembers()
    {
        users = new ArrayList<>();

        DatabaseReference dbRef = Database.root.child(Task.TABLE_NAME)
                .child(sCurrentTask.getTaskID())
                .child("members");

        db.addValueEventListener(TAG, dbRef, new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for(DataSnapshot snap : dataSnapshot.getChildren())
                {
                    Query queryRef = Database.root.child("User")
                            .orderByKey()
                            .equalTo(snap.getValue(String.class));

                    queryRef.addListenerForSingleValueEvent(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            for(DataSnapshot snap : dataSnapshot.getChildren())
                            {
                                users.add(snap.getValue(User.class));
                            }
                            updateUsers();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) { }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    private void updateUsers()
    {
        NachoTextView ntxtUsers = (NachoTextView)getView().findViewById(R.id.ntxtUsers);
        ntxtUsers.enableEditChipOnTouch(false, true);

        ArrayList<String> userNames = new ArrayList<>();
        List<ChipInfo> chips = new ArrayList<>();
        for(User u : users)
        {
            userNames.add(u.getName());
            ChipInfo ci = new ChipInfo(u.getName(), u);
            chips.add(ci);
        }
        ntxtUsers.setTextWithChips(chips);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, userNames);

        ntxtUsers.setAdapter(adapter);
    }

    private void switchToMilestone(Milestone milestone)
    {
        fragmentHelper.switchToFragment(R.id.fragment_container, FragMilestone.newInstance(milestone), null);
    }

    private View.OnClickListener addDeadline = v ->
    {
        final DateTime[] dateTime = {sCurrentTask.getDeadline() == null ? new DateTime() : new DateTime(sCurrentTask.getDeadline())};

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
                    dateTime[0] = changeDeadline(year, monthOfYear + 1, dayOfMonth, dateTime[0].getHourOfDay(), dateTime[0].getMinuteOfHour());
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
        sCurrentTask.setDeadline(newDT.toString());
        Database.root.child(Task.TABLE_NAME)
                .child(sCurrentTask.getTaskID())
                .child("deadline")
                .setValue(sCurrentTask.getDeadline());
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

                    Milestone m = new Milestone(sCurrentTask.getTaskID(),
                            null,
                            ((EditText)ad.findViewById(R.id.txtName)).getText().toString(),
                            ((EditText)ad.findViewById(R.id.txtDescription)).getText().toString(),
                            null,
                            false);

                    m.setMilestoneID(Database.root.child(Milestone.TABLE_NAME).push().getKey());
                    Database.root.child(Milestone.TABLE_NAME)
                            .child(m.getMilestoneID())
                            .setValue(m);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    };

    private View.OnClickListener addUser = v ->
    {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setTitle("Add Members")
                .setView(inflater.inflate(R.layout.dialog_add_user, null))
                .setPositiveButton("Add", (dialog, which) -> {
                    AlertDialog ad = (AlertDialog)dialog;
                    Log.d(TAG, "Adding User To Task");
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        String[] suggestions = new String[]{"Tortilla Chips", "Melted Cheese", "Salsa", "Guacamole", "Mexico", "Jalapeno"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, suggestions);

        Dialog d = builder.show();
        NachoTextView ntxtUsers = (NachoTextView)d.findViewById(R.id.ntxtUsers);
        ntxtUsers.setAdapter(adapter);
    };
}
