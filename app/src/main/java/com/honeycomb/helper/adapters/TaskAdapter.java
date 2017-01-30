package com.honeycomb.helper.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.honeycomb.R;
import com.honeycomb.helper.Database.objects.Task;

import java.util.ArrayList;

/**
 * Created by Ash on 23/01/2017.
 */

public class TaskAdapter extends baseAdapter<Task>
{
    private static class ViewHolder
    {
        TextView id;
        TextView name;
        TextView description;
        TextView deadline;
    }

    public TaskAdapter(Context context)
    {
        super(context, R.layout.item_task);
    }

    public TaskAdapter(Context context, ArrayList<Task> tasks)
    {
        super(context, R.layout.item_task, tasks);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final ViewHolder viewHolder;
        if(convertView == null)
        {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_task, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.id = (TextView)convertView.findViewById(R.id.txtID);
            viewHolder.name = (TextView)convertView.findViewById(R.id.txtName);
            viewHolder.description = (TextView)convertView.findViewById(R.id.txtDescription);
            viewHolder.deadline = (TextView)convertView.findViewById(R.id.txtDeadline);

            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        Task task = getItem(position);
        viewHolder.id.setText(task.getTaskID());
        viewHolder.name.setText(task.getName() + ":");
        viewHolder.description.setText(task.getDescription());

        return convertView;
    }
}
