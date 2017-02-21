package com.honeycomb.fragments;

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
import com.honeycomb.helper.Database.objects.Comment;
import com.honeycomb.helper.Database.objects.Milestone;
import com.honeycomb.helper.Time;
import com.honeycomb.helper.adapters.CommentAdapter;

import org.joda.time.DateTime;

import java.util.ArrayList;

public class FragMilestone extends baseEditFragment
{
    private static Milestone currentMilestone;

    private EditText txtName;
    private EditText txtDescription;
    private LinearLayout llDeadline;
    private TextView txtDeadline;
    private LinearLayout llComments;
    private RecyclerView rvComments;

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

        loadMilestone();
        loadComments();

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getContext());
        mBuilder.setSmallIcon(R.drawable.background_splash);
        mBuilder.setContentTitle("Notification Alert, Click Me!");
        mBuilder.setContentText("Hi, This is Android Notification Detail!");

        NotificationManagerCompat manager = NotificationManagerCompat.from(getContext());
        manager.notify(1, mBuilder.build());

        ArrayList<FloatingActionButton> fabs = new ArrayList<>();
        if(currentMilestone.getDeadline() == null)
        {
            FloatingActionButton fab = new FloatingActionButton(getContext());
            fab.setLabelText("Add Deadline");
            fab.setOnClickListener(addDeadline);
            fabs.add(fab);
        }

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

    protected void loadIO()
    {
        txtName = initEditText(R.id.txtName);
        txtDescription = initEditText(R.id.txtDescription);

        llDeadline = (LinearLayout)getView().findViewById(R.id.llDeadline);
        llDeadline.setVisibility(View.GONE);
        txtDeadline = (TextView)getView().findViewById(R.id.txtDeadline);

        llComments = (LinearLayout)getView().findViewById(R.id.llComments);
        llComments.setVisibility(View.GONE);
        rvComments = (RecyclerView)getView().findViewById(R.id.rvComments);
    }

    private void loadMilestone()
    {
        DatabaseReference dbRef = Database.root.child(Milestone.TABLE_NAME)
                .child(currentMilestone.getMilestoneID());

        db.addValueEventListener(TAG, dbRef, new ValueEventListener()
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

        db.addValueEventListener(TAG, queryRef, new ValueEventListener()
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

    private View.OnClickListener addDeadline = v ->
    {
        Log.d(TAG, "Adding Milestone");
        currentMilestone.setDeadline(new DateTime().toString());

        Database.root.child(Milestone.TABLE_NAME).child(currentMilestone.getMilestoneID()).child("deadline").setValue(currentMilestone.getDeadline());
    };

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
                    Database.root.child(Comment.TABLE_NAME).child(comment.getCommentID()).setValue(comment);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    };
}
