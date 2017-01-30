package com.honeycomb.helper.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by Ash on 30/01/2017.
 */

public abstract class baseAdapter<T> extends ArrayAdapter<T>
{
    public baseAdapter(Context context, int resource)
    {
        super(context, resource);
    }

    public baseAdapter(Context context, int resource, ArrayList<T> items)
    {
        super(context, resource);
    }

    public void updateData(ArrayList<T> items)
    {
        clear();
        if(items != null)
        {
            for(T item : items)
            {
                insert(item, getCount());
            }
        }
        notifyDataSetChanged();
    }
}
