package com.honeycomb.helper.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.honeycomb.helper.Database.objects.User;

import java.util.ArrayList;

/**
 * Created by Ash on 25/02/2017.
 */

public class MemberAdapter extends ArrayAdapter<User>
{
    private int mLayoutResourceId;
    private ArrayList<User> mAllUsers, mSuggestions;

    private static class ViewHolder
    {
        public TextView txtName;
    }

    public MemberAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<User> objects)
    {
        super(context, resource, objects);
        mLayoutResourceId = resource;
        mAllUsers = new ArrayList<>(objects);
        mSuggestions = new ArrayList<>();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        ViewHolder viewHolder;

        if(convertView == null)
        {
            convertView = LayoutInflater.from(this.getContext()) // TODO, this or parent
                    .inflate(mLayoutResourceId, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.txtName = (TextView)convertView.findViewById(android.R.id.text1);

            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        User user = getItem(position);
        if(user != null)
        {
            viewHolder.txtName.setText(user.getName());
        }

        return convertView;
    }

    @NonNull
    @Override
    public Filter getFilter()
    {
        return nameFilter;
    }

    Filter nameFilter = new Filter()
    {
        @Override
        public CharSequence convertResultToString(Object resultValue)
        {
            return ((User)resultValue).getName();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint)
        {
            if(constraint != null)
            {
                mSuggestions.clear();
                for(User u : mAllUsers)
                {
                    if(u.getName().toLowerCase().contains(constraint.toString().toLowerCase()))
                    {
                        mSuggestions.add(u);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = mSuggestions;
                filterResults.count = mSuggestions.size();
                return filterResults;
            }
            else
            {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results)
        {
            ArrayList<User> filterList = (ArrayList<User>)results.values;
            if(results.count > 0)
            {
                clear();
                for(User u : filterList)
                {
                    add(u);
                    notifyDataSetChanged();
                }
            }
        }
    };
}
