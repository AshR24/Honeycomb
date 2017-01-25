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

import com.amazonaws.models.nosql.TaskDO;
import com.honeycomb.R;
import com.honeycomb.helper.Database.operations.Add;
import com.honeycomb.helper.Database.operations.GetAll;
import com.honeycomb.helper.Time;
import com.honeycomb.helper.adapters.TaskAdapter;
import com.honeycomb.interfaces.AsyncResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Ash on 20/01/2017.
 */

public class FragMain extends baseFragment implements AsyncResponse
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        getToolbar().setTitle("Main");
        new GetAll<>(TaskDO.class, this).execute();

        Button but = (Button)getView().findViewById(R.id.button);
        but.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                addTestItem(view);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onFinished(Object result)
    {
        TaskAdapter taskAdapter = new TaskAdapter(getContext(), new ArrayList<>((List<TaskDO>)result));

        ListView lv = (ListView)getView().findViewById(R.id.lvTasks);
        lv.setAdapter(taskAdapter);
        lv.setOnItemClickListener(onClick);
    }

    private AdapterView.OnItemClickListener onClick = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
        {
            System.out.println("Hi");

            TaskDO entry = (TaskDO)adapterView.getItemAtPosition(i);

            fragmentHelper.switchToFragment(R.id.fragmentContainer, FragTask.newInstance(entry), null);
        }
    };



    public void addTestItem(View view)
    {
        UUID uuid = UUID.randomUUID();

        TaskDO task = new TaskDO();
        task.setProjectID("1");
        task.setTaskID(Long.toString(uuid.getLeastSignificantBits(), Character.MAX_RADIX));
        task.setName("TestName");
        task.setDescription("TestDescription");

        double currentTime = Time.getCurrentUnixTimestamp();
        //task.setDateCreated(currentTime);
        //task.setDateLastEdited(currentTime);

        new Add<>(TaskDO.class).execute(task);
        getItems(view);
    }

    public void getItems(View view)
    {
        new GetAll<>(TaskDO.class, this).execute();
    }
}
