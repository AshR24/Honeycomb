package com.honeycomb.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.honeycomb.R;
import com.honeycomb.helper.Database.objects.Milestone;
import com.honeycomb.helper.Database.objects.Task;
import com.honeycomb.helper.adapters.MilestoneAdapter;

import java.util.ArrayList;

/**
 * Created by Ash on 20/01/2017.
 */

public class FragTask extends baseEditFragment
{
    private static Task currentTask;

    private EditText txtName;
    private EditText txtDescription;
    private Button butAddMilestone;

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
    }

    @Override
    protected void setEditMode(boolean isEditMode, boolean needsUpdate)
    {
        super.setEditMode(isEditMode, needsUpdate);
        txtName.setEnabled(isEditMode);
        txtDescription.setEnabled(isEditMode);
        if(needsUpdate)
        {
            currentTask.setName(txtName.getText().toString());
            currentTask.setDescription(txtDescription.getText().toString());
            dbRoot.child(Task.TABLE_NAME).child(currentTask.getTaskID()).setValue(currentTask);
        }
    }

    public void addMilestone(View view)
    {
        Milestone milestone = new Milestone();
        milestone.setTaskID(currentTask.getTaskID());
        milestone.setMilestoneID(dbRoot.child(Milestone.TABLE_NAME).push().getKey());
        milestone.setName("TestName");
        milestone.setDescription("TestDescription");
        milestone.setCompleted(false);

        dbRoot.child(Milestone.TABLE_NAME).child(milestone.getMilestoneID()).setValue(milestone);
    }

    protected void loadIO()
    {
        txtName = (EditText)getView().findViewById(R.id.txtName);
        txtDescription = (EditText)getView().findViewById(R.id.txtDescription);
        butAddMilestone = (Button)getView().findViewById(R.id.butAddMilestone);
        butAddMilestone.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                addMilestone(view);
            }
        });
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }

    private void loadMilestones()
    {
        final MilestoneAdapter milestoneAdapter = new MilestoneAdapter(getContext());
        ListView lv = (ListView)getView().findViewById(R.id.lvMilestones);
        lv.setAdapter(milestoneAdapter);
        lv.setOnItemClickListener(onClick);

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
                milestoneAdapter.updateData(milestones);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }

    private AdapterView.OnItemClickListener onClick = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
        {
            Milestone entry = (Milestone)adapterView.getItemAtPosition(i);
            fragmentHelper.switchToFragment(R.id.fragmentContainer, FragMilestone.newInstance(entry), null); // TODO, fragment container can be generic.... never changes
        }
    };
}
