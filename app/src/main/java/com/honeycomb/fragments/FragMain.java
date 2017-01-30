package com.honeycomb.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.honeycomb.R;
import com.honeycomb.helper.Database.objects.Task;
import com.honeycomb.helper.adapters.TaskAdapter;

import java.util.ArrayList;

/**
 * Created by Ash on 20/01/2017.
 */

public class FragMain extends baseFragment
{

    public static FragMain newInstance(String thing)
    {
        FragMain newFragment = new FragMain();
        /*Bundle args = new Bundle();
        args.putString("Name", thing);
        newFragment.setArguments(args);*/
        return newFragment;
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
        getToolbar().setTitle("Main");

        Button but = (Button)getView().findViewById(R.id.button);
        but.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                addTestItem(view);
            }
        });

        loadTasks();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void loadTasks()
    {
        final TaskAdapter taskAdapter = new TaskAdapter(getContext());
        ListView lv = (ListView)getView().findViewById(R.id.lvTasks);
        lv.setAdapter(taskAdapter);
        lv.setOnItemClickListener(onClick);

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child(Task.TABLE_NAME).addValueEventListener(new ValueEventListener()
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
                taskAdapter.updateData(tasks);
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
            Task entry = (Task)adapterView.getItemAtPosition(i);
            fragmentHelper.switchToFragment(R.id.fragmentContainer, FragTask.newInstance(entry), null);
        }
    };

    public void addTestItem(View view)
    {
        Task test = new Task();
        test.setProjectID("1");
        test.setTaskID(dbRoot.child(Task.TABLE_NAME).push().getKey());
        test.setName("TestName");
        test.setDescription("TestDescription");

        dbRoot.child(Task.TABLE_NAME).child(test.getTaskID()).setValue(test);
    }
}
