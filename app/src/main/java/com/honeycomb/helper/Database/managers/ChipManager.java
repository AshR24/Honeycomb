package com.honeycomb.helper.Database.managers;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;

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

/**
 * Created by Ash on 04/03/2017.
 */

public class ChipManager
{
    public static final String TAG = ChipManager.class.getSimpleName();

    private Database mDb;

    private NachoTextView mTxtMembers;

    public ChipManager(Context context, Database db, NachoTextView txtMembers)
    {
        mDb = db;
        mTxtMembers = txtMembers;

        db.addSubscriber(Subjects.SUBJECT_ALL_USERS.subscribe(AllUsers ->
        {
            ArrayAdapter<User> adapter = new MemberAdapter(context, android.R.layout.simple_dropdown_item_1line, new ArrayList<>(AllUsers));
            mTxtMembers.setAdapter(adapter);

            Log.d(TAG, "Updated adapter with: " + AllUsers.size() + " users");
        }));

        db.addSubscriber(Subjects.SUBJECT_CURRENT_MEMBERS.subscribe(users ->
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
    }

    public ArrayList<User> getUsersFromChips()
    {
        HashSet<User> result = new HashSet<>();
        List<Chip> chips = mTxtMembers.getAllChips();
        for(Chip c : chips)
        {
            User user = (User)c.getData();
            result.add(user);
        }
        return new ArrayList<>(result);
    }
}
