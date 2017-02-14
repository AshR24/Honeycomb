package com.honeycomb.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.honeycomb.R;
import com.honeycomb.helper.Database.objects.Milestone;

import org.joda.time.DateTime;

import java.util.ArrayList;

public class FragMilestone extends baseEditFragment
{
    private static Milestone currentMilestone;

    private EditText txtName;
    private EditText txtDescription;
    private EditText txtDeadline;
    private ListView lvComments;

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

        ArrayList<FloatingActionButton> fabs = new ArrayList<>();
        if(currentMilestone.getDeadline() == null)
        {
            FloatingActionButton fab = new FloatingActionButton(getContext());
            fab.setLabelText("Add Deadline");
            fab.setOnClickListener(addDeadline);
            fabs.add(fab);
        }
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
            dbRoot.child(Milestone.TABLE_NAME).child(currentMilestone.getMilestoneID()).setValue(currentMilestone);
            Log.d(TAG, "Saved Milestone");
        }
    }

    protected void loadIO()
    {
        txtName = initEditText(R.id.txtName);
        txtDescription = initEditText(R.id.txtDescription);
        txtDeadline = initEditText(R.id.txtDeadline);
        //lvComments = (ListView)getView().findViewById(R.id.lvComments);
    }

    private void loadMilestone()
    {
        dbRoot.child(Milestone.TABLE_NAME).child(currentMilestone.getMilestoneID()).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                currentMilestone = dataSnapshot.getValue(Milestone.class);
                txtName.setText(currentMilestone.getName());
                txtDescription.setText(currentMilestone.getDescription());

                if(currentMilestone.getDeadline() == null)
                {
                    txtDeadline.setVisibility(View.INVISIBLE);
                }
                else
                {
                    txtDeadline.setVisibility(View.VISIBLE);
                    txtDeadline.setText(currentMilestone.getDeadline());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }

    private View.OnClickListener addDeadline = v ->
    {
        Log.d(TAG, "Adding Milestone");
        currentMilestone.setDeadline(new DateTime().toString());

        dbRoot.child(Milestone.TABLE_NAME).child(currentMilestone.getMilestoneID()).child("deadline").setValue(currentMilestone.getDeadline().toString());
    };
}
