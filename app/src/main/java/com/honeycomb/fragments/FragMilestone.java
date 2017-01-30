package com.honeycomb.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.honeycomb.R;
import com.honeycomb.helper.Database.objects.Milestone;

public class FragMilestone extends baseEditFragment
{
    private static Milestone currentMilestone;

    private EditText txtName;
    private EditText txtDescription;
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
    }

    @Override
    protected void setEditMode(boolean isEditMode, boolean needsUpdate)
    {
        super.setEditMode(isEditMode, needsUpdate);
        txtName.setEnabled(isEditMode);
        txtDescription.setEnabled(isEditMode);
        if(needsUpdate)
        {
            currentMilestone.setName(txtName.getText().toString());
            currentMilestone.setDescription(txtDescription.getText().toString());
            dbRoot.child(Milestone.TABLE_NAME).child(currentMilestone.getMilestoneID()).setValue(currentMilestone);
        }
    }

    protected void loadIO()
    {
        txtName = (EditText)getView().findViewById(R.id.txtName);
        txtDescription = (EditText)getView().findViewById(R.id.txtDescription);
        lvComments = (ListView)getView().findViewById(R.id.lvComments);
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }
}
