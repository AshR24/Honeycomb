package com.honeycomb.helper.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.honeycomb.R;
import com.honeycomb.helper.Database.objects.Milestone;

import java.util.ArrayList;

/**
 * Created by Ash on 26/01/2017.
 */

public class MilestoneAdapter extends baseAdapter<Milestone>
{
    DatabaseReference dbRoot = FirebaseDatabase.getInstance().getReference();

    private static class ViewHolder
    {
        TextView name;
        CheckBox isCompleted;
    }

    public MilestoneAdapter(Context context)
    {
        super(context, R.layout.item_milestone);
    }

    public MilestoneAdapter(Context context, ArrayList<Milestone> milestones)
    {
        super(context, R.layout.item_milestone, milestones);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final ViewHolder viewHolder;
        if(convertView == null)
        {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_milestone, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.name = (TextView)convertView.findViewById(R.id.txtName);
            viewHolder.isCompleted = (CheckBox)convertView.findViewById(R.id.cbCompleted);

            convertView.setTag(viewHolder);
        }
        else { viewHolder = (ViewHolder)convertView.getTag(); }

        Milestone milestone = getItem(position);
        viewHolder.name.setText(milestone.getName());
        viewHolder.isCompleted.setOnClickListener(setOnCheckboxClicked(milestone));
        viewHolder.isCompleted.setChecked(milestone.isCompleted());

        return convertView;
    }

    private View.OnClickListener setOnCheckboxClicked(final Milestone milestone)
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(final View view)
            {
                CheckBox cb = (CheckBox)view.findViewById(R.id.cbCompleted);
                milestone.setCompleted(cb.isChecked());
                dbRoot.child(Milestone.TABLE_NAME).child(milestone.getMilestoneID()).child("completed").setValue(milestone.isCompleted()); // TODO, send in single value or whole object?
            }
        };
    }
}
