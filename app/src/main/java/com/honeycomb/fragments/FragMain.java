package com.honeycomb.fragments;

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
import android.widget.EditText;

import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.honeycomb.R;
import com.honeycomb.helper.Database.Database;
import com.honeycomb.helper.Database.objects.Task;
import com.honeycomb.helper.adapters.TaskAdapter;

import java.util.ArrayList;

/**
 * Created by Ash on 20/01/2017.
 */

public class FragMain extends baseFragment
{
    public static FragMain newInstance()
    {
        return new FragMain();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        getToolbar().setTitle("Tasks");

        loadTasks();

        ArrayList<FloatingActionButton> fabs = new ArrayList<>();
        FloatingActionButton fab = new FloatingActionButton(getContext());
        fab.setLabelText("Add Task");
        fab.setOnClickListener(addTask);
        fabs.add(fab);
        setFam(fabs);
    }

    private void loadTasks()
    {
        final TaskAdapter taskAdapter = new TaskAdapter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        RecyclerView rv = (RecyclerView)getView().findViewById(R.id.rvTasks);
        rv.setLayoutManager(linearLayoutManager);
        rv.setAdapter(taskAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv.getContext(),
                linearLayoutManager.getOrientation());
        rv.addItemDecoration(dividerItemDecoration);

        taskAdapter.getClickSubject()
                .subscribe(this::switchToTask);

        DatabaseReference dbRef = Database.root.child(Task.TABLE_NAME);
        db.addValueEventListener(TAG, dbRef, new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                ArrayList<Task> tasks = new ArrayList<>();
                for(DataSnapshot snap : dataSnapshot.getChildren())
                {
                    Task task = snap.getValue(Task.class);
                    tasks.add(task);
                }
                taskAdapter.update(tasks);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }

    private void switchToTask(Task task)
    {
        fragmentHelper.switchToFragment(R.id.fragment_container, FragTask.newInstance(task), null);
    }

    private View.OnClickListener addTask = v ->
    {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setTitle("Add Task")
                .setView(inflater.inflate(R.layout.dialog_add_task, null))
                .setPositiveButton("Add", (dialog, which) -> {
                    AlertDialog ad = (AlertDialog)dialog;
                    Log.d(TAG, "Adding Task");

                    Task t = new Task("1",
                            null,
                            ((EditText)ad.findViewById(R.id.txtName)).getText().toString(),
                            ((EditText)ad.findViewById(R.id.txtDescription)).getText().toString(),
                            null);
                    ArrayList<String> aList = new ArrayList<>();
                    aList.add(currentUser.getUserID());
                    aList.add(currentUser.getUserID());
                    t.setMembers(aList);

                    t.setTaskID(Database.root.child(Task.TABLE_NAME).push().getKey());
                    Database.root.child(Task.TABLE_NAME)
                            .child(t.getTaskID())
                            .setValue(t);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    };
}
