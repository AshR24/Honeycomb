package com.honeycomb.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;
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
import android.widget.RadioButton;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.honeycomb.R;
import com.honeycomb.Subjects;
import com.honeycomb.helper.Database.Database;
import com.honeycomb.helper.Database.managers.MemberManager;
import com.honeycomb.helper.Database.objects.Comment;
import com.honeycomb.helper.Database.objects.Milestone;
import com.honeycomb.helper.Database.objects.Task;
import com.honeycomb.helper.Database.objects.User;
import com.honeycomb.helper.Time;
import com.honeycomb.helper.adapters.CommentAdapter;
import com.hootsuite.nachos.NachoTextView;
import com.hootsuite.nachos.chip.ChipInfo;

import org.joda.time.DateTime;

import java.lang.reflect.Array;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class FragMilestone extends baseEditFragment
{
    private static Milestone currentMilestone;

    private EditText txtName;
    private EditText txtDescription;
    private LinearLayout llDeadline;
    private TextView txtDeadline;
    private LinearLayout llComments;
    private RecyclerView rvComments;
    private NachoTextView mNtxtMembers;
    private RadioButton mRbutWorkingOn;
    private RadioButton mRbutNotWorkingOn;

    private MemberManager mMemberManager;

    public static FragMilestone newInstance(@Nullable Milestone milestone)
    {
        FragMilestone newFragment = new FragMilestone();
        currentMilestone = milestone;
        return newFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_milestone, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        getToolbar().setTitle("Milestone");

        mNtxtMembers.setEnabled(false);

        DatabaseReference dbRef = Database.root.child(Milestone.TABLE_NAME)
                .child(currentMilestone.getMilestoneID())
                .child("members");

        mMemberManager = new MemberManager(db, dbRef);

        db.addSubscriber(Subjects.SUBJECT_CURRENT_MEMBERS.subscribe(users ->
        {
            List<ChipInfo> chips = new ArrayList<>();
            for(User u : users)
            {
                ChipInfo ci = new ChipInfo(u.getName(), u);
                chips.add(ci);
            }
            mNtxtMembers.setTextWithChips(chips);
            Log.d(TAG, users.size() + " users loaded into members");
        }));

        if(currentMilestone.getMembers() != null)
        {
            if(currentMilestone.getMembers().contains(currentUser.getUserID()))
            {
                mRbutWorkingOn.setChecked(true);
            }
            else
            {
                mRbutNotWorkingOn.setChecked(true);
            }
        }

        loadMilestone();
        loadComments();

        ArrayList<FloatingActionButton> fabs = new ArrayList<>();

        FloatingActionButton fabAddDeadline = new FloatingActionButton(getContext());
        if(currentMilestone.getDeadline() == null) { fabAddDeadline.setLabelText("Add Deadline"); }
        else { fabAddDeadline.setLabelText("Change Deadline"); }
        fabAddDeadline.setOnClickListener(addDeadline);
        fabs.add(fabAddDeadline);

        FloatingActionButton fab = new FloatingActionButton(getContext());
        fab.setLabelText("Add Comment");
        fab.setOnClickListener(addComment);
        fabs.add(fab);

        setFam(fabs);
    }

    @Override
    protected void setEditMode(boolean isEditMode)
    {
        super.setEditMode(isEditMode);
        if(!isEditMode)
        {
            currentMilestone.setName(txtName.getText().toString());
            currentMilestone.setDescription(txtDescription.getText().toString());
            Database.root.child(Milestone.TABLE_NAME)
                    .child(currentMilestone.getMilestoneID())
                    .setValue(currentMilestone);
            Log.d(TAG, "Saved Milestone");
        }
    }

    /**
     * Loads input from the view
     */
    protected void loadIO()
    {
        txtName = initEditText(R.id.txtName);
        txtDescription = initEditText(R.id.txtDescription);

        llDeadline = (LinearLayout)getView().findViewById(R.id.llDeadline);
        llDeadline.setVisibility(View.GONE);
        txtDeadline = (TextView)getView().findViewById(R.id.txtDeadline);
        txtDeadline.setOnClickListener(addDeadline);

        llComments = (LinearLayout)getView().findViewById(R.id.llComments);
        llComments.setVisibility(View.GONE);
        rvComments = (RecyclerView)getView().findViewById(R.id.rvComments);

        mNtxtMembers = (NachoTextView)getView().findViewById(R.id.ntxtUsers);
        mRbutWorkingOn = (RadioButton)getView().findViewById(R.id.rbutWorkingOn);
        mRbutNotWorkingOn = (RadioButton)getView().findViewById(R.id.rButNotWorkingOn);

        mRbutWorkingOn.setOnClickListener(v -> updateWorkingOn());
        mRbutNotWorkingOn.setOnClickListener(v -> updateWorkingOn());
    }

    /**
     * Updates the members text box when a user clicks the radio buttons
     */
    private void updateWorkingOn()
    {
        ArrayList<String> currentMembers = currentMilestone.getMembers() != null ? currentMilestone.getMembers() : new ArrayList<>();

        if(mRbutWorkingOn.isChecked())
        {
            if(!currentMembers.contains(currentUser.getUserID()))
            {
                currentMembers.add(currentUser.getUserID());
                currentMilestone.setMembers(currentMembers);
                setEditMode(false);
            }
        }
        else
        {
            if(currentMembers.contains(currentUser.getUserID()))
            {
                currentMembers.remove(currentUser.getUserID());
                currentMilestone.setMembers(currentMembers);
                setEditMode(false);
            }
        }
    }

    /**
     * Loads the current milestone being viewed
     */
    private void loadMilestone()
    {
        DatabaseReference dbRef = Database.root.child(Milestone.TABLE_NAME)
                .child(currentMilestone.getMilestoneID());

        db.addValueEventListener(dbRef, new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                currentMilestone = dataSnapshot.getValue(Milestone.class);
                txtName.setText(currentMilestone.getName());
                txtDescription.setText(currentMilestone.getDescription());

                if(currentMilestone.getDeadline() == null)
                {
                    llDeadline.setVisibility(View.INVISIBLE);
                }
                else
                {
                    txtDeadline.setText(Time.toWordyReadable(new DateTime(currentMilestone.getDeadline())));
                    llDeadline.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    /**
     * Adds a deadline to the milestone using built-in android pickers
     */
    private View.OnClickListener addDeadline = v ->
    {
        final DateTime[] dateTime = {currentMilestone.getDeadline() == null ? new DateTime() : new DateTime(currentMilestone.getDeadline())};

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

    /**
     * Modifies a deadline - pushing updates to the database
     * @param year
     * @param month
     * @param day
     * @param hour
     * @param minute
     * @return
     */
    private DateTime changeDeadline(int year, int month, int day, int hour, int minute)
    {
        DateTime newDT = new DateTime(year, month, day, hour, minute);
        currentMilestone.setDeadline(newDT.toString());
        Database.root.child(Milestone.TABLE_NAME)
                .child(currentMilestone.getMilestoneID())
                .child("deadline")
                .setValue(currentMilestone.getDeadline());
        return newDT;
    }

    /**
     * Loads all related comments for the current milestone
     */
    private void loadComments()
    {
        final CommentAdapter commentAdapter = new CommentAdapter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        RecyclerView rv = (RecyclerView)getView().findViewById(R.id.rvComments);
        rv.setLayoutManager(linearLayoutManager);
        rv.setAdapter(commentAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv.getContext(),
                linearLayoutManager.getOrientation());
        rv.addItemDecoration(dividerItemDecoration);

        Query queryRef = Database.root.child(Comment.TABLE_NAME)
                .orderByChild("milestoneID")
                .equalTo(currentMilestone.getMilestoneID());

        db.addValueEventListener(queryRef, new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                ArrayList<Comment> comments = new ArrayList<>();
                for(DataSnapshot snap : dataSnapshot.getChildren())
                {
                    Comment comment = snap.getValue(Comment.class);
                    comments.add(comment);
                }
                commentAdapter.update(comments);
                if(comments.size() == 0) { llComments.setVisibility(View.GONE); }
                else { llComments.setVisibility(View.VISIBLE); }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    /**
     * Creates a new comment for this milestone
     */
    private View.OnClickListener addComment = v ->
    {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setTitle("Add Comment")
                .setView(inflater.inflate(R.layout.dialog_add_comment, null))
                .setPositiveButton("Add", (dialog, which) -> {
                    AlertDialog ad = (AlertDialog)dialog;
                    Log.d(TAG, "Adding Comment");
                    Comment comment = new Comment();
                    comment.setMilestoneID(currentMilestone.getMilestoneID());
                    comment.setCommentID(Database.root.child(Comment.TABLE_NAME).push().getKey());
                    comment.setComment(((EditText)ad.findViewById(R.id.txtComment)).getText().toString());
                    comment.setDatePosted(new DateTime().toString());
                    comment.setUserName(currentUser.getName());
                    Database.root.child(Comment.TABLE_NAME).child(comment.getCommentID()).setValue(comment);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    };
}
