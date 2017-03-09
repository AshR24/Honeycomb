package com.honeycomb.helper.Database.managers;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.honeycomb.Subjects;
import com.honeycomb.helper.Database.Database;
import com.honeycomb.helper.Database.objects.Task;
import com.honeycomb.helper.Database.objects.User;

import java.util.ArrayList;

/**
 * Created by Ash on 04/03/2017.
 */

public class MemberManager
{
    public static final String TAG = MemberManager.class.getSimpleName();

    private Database mDb;
    private final DatabaseReference mDbMembersRef;

    public MemberManager(Database db, DatabaseReference dbRef)
    {
        mDb = db;
        mDbMembersRef = dbRef;
        getMembersOf();
    }

    private void getMembersOf()
    {
        mDb.addValueEventListener(mDbMembersRef, new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                ArrayList<User> members = new ArrayList<>();

                for(DataSnapshot snap : dataSnapshot.getChildren())
                {
                    Query queryRef = Database.root.child("User")
                            .orderByKey()
                            .equalTo(snap.getValue(String.class));

                    queryRef.addListenerForSingleValueEvent(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            for(DataSnapshot snap : dataSnapshot.getChildren())
                            {
                                members.add(snap.getValue(User.class));
                            }
                            Subjects.SUBJECT_CURRENT_MEMBERS.onNext(members);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) { }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    public void updateMembers(ArrayList<User> users)
    {
        ArrayList<String> userIDs = new ArrayList<>();
        for(User user : users)
        {
            userIDs.add(user.getUserID());
        }

        mDbMembersRef.setValue(userIDs);
    }

    public static void updateUserTasks(Task task, User user, boolean isAdd)
    {
        ArrayList<String> tasks = user.getTasks() != null ? user.getTasks() : new ArrayList<>();

        if(isAdd)
        {
            if(!tasks.contains(task.getTaskID()))
            {
                tasks.add(task.getTaskID());
            }
        }
        else
        {
            tasks.remove(task.getTaskID());
        }

        user.setTasks(tasks);
        Database.root.child(User.TABLE_NAME)
                .child(user.getUserID())
                .child("tasks")
                .setValue(tasks);
    }
}
