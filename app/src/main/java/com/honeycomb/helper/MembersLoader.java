package com.honeycomb.helper;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.honeycomb.Subjects;
import com.honeycomb.helper.Database.Database;
import com.honeycomb.helper.Database.objects.User;
import com.honeycomb.helper.adapters.MemberAdapter;
import com.hootsuite.nachos.NachoTextView;
import com.hootsuite.nachos.chip.Chip;
import com.hootsuite.nachos.chip.ChipInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import io.reactivex.subjects.BehaviorSubject;

/**
 * Created by Ash on 24/02/2017.
 */

public class MembersLoader
{
    public static final String TAG = MembersLoader.class.getSimpleName();
    private Database mDb;

    private BehaviorSubject<ArrayList<User>> mSubMembers;

    private NachoTextView mTxtMembers;

    public MembersLoader(Context context, Database db, NachoTextView txtMembers)
    {
        mDb = db;
        mTxtMembers = txtMembers;
        mSubMembers = BehaviorSubject.create();

        db.addSubscriber(mSubMembers.subscribe(users ->
        {
            mTxtMembers.enableEditChipOnTouch(false, true);

            List<ChipInfo> chips = new ArrayList<>();
            for(User u : users)
            {
                ChipInfo ci = new ChipInfo(u.getName(), u);
                chips.add(ci);
            }
            mTxtMembers.setTextWithChips(chips);
            Log.d(TAG, users.size() + " users loaded into members");
        }));


        db.addSubscriber(Subjects.SUBJECT_ALL_USERS.subscribe(AllUsers ->
        {
            ArrayAdapter<User> adapter = new MemberAdapter(context, android.R.layout.simple_dropdown_item_1line, new ArrayList<>(AllUsers));
            mTxtMembers.setAdapter(adapter);

            Log.d(TAG, "Updated adapter with: " + AllUsers.size() + " users");
        }));

    }

    public MembersLoader(Database db)
    {
        mDb = db;
    }

    public void loadMembersOf(DatabaseReference dbRef)
    {
        mDb.addValueEventListener(dbRef, new ValueEventListener()
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
                            mSubMembers.onNext(members);
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

    public ArrayList<String> getMembers()
    {
        HashSet<String> res = new HashSet<>();
        List<Chip> chips = mTxtMembers.getAllChips();
        for(Chip c : chips)
        {
            User user = (User)c.getData();
            res.add(user.getUserID());
        }
        return new ArrayList<>(res);
    }
}
