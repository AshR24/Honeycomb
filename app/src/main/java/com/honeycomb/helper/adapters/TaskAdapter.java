package com.honeycomb.helper.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.honeycomb.R;
import com.honeycomb.helper.Database.objects.Task;
import com.honeycomb.helper.Time;

import org.joda.time.DateTime;

import java.util.ArrayList;

import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by Ash on 09/02/2017.
 */

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder>
{
    public static final String TAG = TaskAdapter.class.getSimpleName();

    private final ArrayList<Task> items;

    private final PublishSubject<Task> onClickSubject = PublishSubject.create();

    public Observable<Task> getClickSubject()
    {
        return onClickSubject.asObservable();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView name;
        public TextView description;
        public TextView deadline;

        public ViewHolder(View itemView)
        {
            super(itemView);
            name = (TextView)itemView.findViewById(R.id.txtName);
            description = (TextView)itemView.findViewById(R.id.txtDescription);
            deadline = (TextView)itemView.findViewById(R.id.txtDeadline);
        }

        public void set(Task task)
        {
            if(task != null)
            {
                name.setText(task.getName());
                description.setText(task.getDescription());
                if(task.getDeadline() == null)
                {
                    deadline.setVisibility(View.GONE);
                }
                else
                {
                    deadline.setVisibility(View.VISIBLE);
                    deadline.setText(Time.untilDeadline(new DateTime(task.getDeadline())));
                }
            }
        }
    }

    public TaskAdapter()
    {
        items = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position)
    {
        Task task = items.get(position);
        holder.set(task);
        holder.itemView.setOnClickListener(v -> onClickSubject.onNext(task));
    }

    @Override
    public int getItemCount()
    {
        return items.size();
    }

    public void update(ArrayList<Task> tasks)
    {
        items.clear();
        items.addAll(tasks);
        notifyDataSetChanged();
        Log.d(TAG, "Updated list with " + tasks.size() + " item(s)");
    }
}
