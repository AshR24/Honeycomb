package com.honeycomb.helper.adapters;

import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.honeycomb.R;
import com.honeycomb.helper.Database.objects.Milestone;
import com.honeycomb.helper.Time;

import org.joda.time.DateTime;

import java.util.ArrayList;

import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by Ash on 26/01/2017.
 */

public class MilestoneAdapter extends RecyclerView.Adapter<MilestoneAdapter.ViewHolder>
{
    public static final String TAG = MilestoneAdapter.class.getSimpleName();

    private final ArrayList<Milestone> items;

    private final PublishSubject<Milestone> onClickSubject = PublishSubject.create();

    public Observable<Milestone> getClickSubject()
    {
        return onClickSubject.asObservable();
    }

    DatabaseReference dbRoot = FirebaseDatabase.getInstance().getReference();

    protected class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView name;
        public TextView deadline;
        public CheckBox isCompleted;

        public ViewHolder(View itemView)
        {
            super(itemView);
            name = (TextView)itemView.findViewById(R.id.txtName);
            deadline = (TextView)itemView.findViewById(R.id.txtDeadline);
            isCompleted = (CheckBox)itemView.findViewById(R.id.cbCompleted);
        }

        public void set(Milestone milestone)
        {
            if(milestone != null)
            {
                name.setText(milestone.getName());
                if(milestone.getDeadline() == null)
                {
                    deadline.setVisibility(View.GONE);
                }
                else
                {
                    deadline.setText(Time.untilDeadline(new DateTime(milestone.getDeadline())));
                    deadline.setVisibility(View.VISIBLE);
                }
                isCompleted.setChecked(milestone.isCompleted());
                if(milestone.isCompleted())
                {
                    name.setPaintFlags(name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }
                else
                {
                    name.setPaintFlags(name.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                }
                isCompleted.setOnClickListener(setOnCheckboxClicked(milestone));
            }
        }
    }

    public MilestoneAdapter()
    {
        items = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_milestone, parent, false);
        return new MilestoneAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        Milestone milestone = items.get(position);
        holder.set(milestone);
        holder.itemView.setOnClickListener(v -> onClickSubject.onNext(milestone));
    }

    @Override
    public int getItemCount()
    {
        return items.size();
    }

    protected View.OnClickListener setOnCheckboxClicked(final Milestone milestone)
    {
        return view -> {
            CheckBox cb = (CheckBox)view.findViewById(R.id.cbCompleted);
            milestone.setCompleted(cb.isChecked());
            dbRoot.child(Milestone.TABLE_NAME).child(milestone.getMilestoneID()).child("completed").setValue(milestone.isCompleted()); // TODO, send in single value or whole object?
        };
    }

    public void update(ArrayList<Milestone> milestones)
    {
        items.clear();
        items.addAll(milestones);
        notifyDataSetChanged();
        Log.d(TAG, "Updated list with " + milestones.size() + " item(s)");
    }
}
