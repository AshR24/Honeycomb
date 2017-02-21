package com.honeycomb.helper.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.honeycomb.R;
import com.honeycomb.helper.Database.objects.Comment;
import com.honeycomb.helper.Time;

import org.joda.time.DateTime;

import java.util.ArrayList;

/**
 * Created by Ash on 18/02/2017.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>
{
    public static final String TAG = CommentAdapter.class.getSimpleName();

    private final ArrayList<Comment> items;

    protected class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView comment;
        public TextView datePosted;

        public ViewHolder(View itemView)
        {
            super(itemView);
            comment = (TextView)itemView.findViewById(R.id.txtComment);
            datePosted = (TextView)itemView.findViewById(R.id.txtDatePosted);
        }

        public void set(Comment comment)
        {
            if(comment != null)
            {
                this.comment.setText(comment.getComment());
                datePosted.setText(Time.toShortWordyReadable(new DateTime(comment.getDatePosted())));
            }
        }
    }

    public CommentAdapter()
    {
        items = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);
        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        Comment comment = items.get(position);
        holder.set(comment);
        //holder.itemView.setOnClickListener(v -> onClickSubject.onNext(milestone));
    }

    @Override
    public int getItemCount()
    {
        return items.size();
    }

    public void update(ArrayList<Comment> comments)
    {
        items.clear();
        items.addAll(comments);
        notifyDataSetChanged();
        Log.d(TAG, "Updated list with " + comments.size() + " item(s)");
    }
}
