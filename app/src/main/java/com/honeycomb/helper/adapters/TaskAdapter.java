package com.honeycomb.helper.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.amazonaws.models.nosql.TaskDO;
import com.honeycomb.R;

import java.util.ArrayList;

/**
 * Created by Ash on 23/01/2017.
 */

public class TaskAdapter extends ArrayAdapter<TaskDO>
{
    private static class ViewHolder
    {
        TextView id;
        TextView name;
        TextView description;
    }

    public TaskAdapter(Context context, ArrayList<TaskDO> tasks)
    {
        super(context, R.layout.item_task, tasks);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        TaskDO task = getItem(position);

        final ViewHolder viewHolder;
        if(convertView == null)
        {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_task, parent,false);
            viewHolder = new ViewHolder();

            viewHolder.id = (TextView)convertView.findViewById(R.id.tvID);
            viewHolder.name = (TextView)convertView.findViewById(R.id.tvName);
            viewHolder.description = (TextView)convertView.findViewById(R.id.tvDescription);

            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        viewHolder.id.setText(task.getTaskID());
        viewHolder.name.setText(task.getName() + ":");
        viewHolder.description.setText(task.getDescription());

        return convertView;
    }
}
