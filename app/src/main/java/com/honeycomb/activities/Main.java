package com.honeycomb.activities;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.honeycomb.R;
import com.honeycomb.Subjects;
import com.honeycomb.fragments.FragMain;
import com.honeycomb.helper.Database.Database;
import com.honeycomb.helper.Database.objects.User;

import java.util.ArrayList;

public class Main extends baseActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        validateUser();
        getAllUsers();

        fragmentHelper.switchToFragment(R.id.fragment_container, FragMain.newInstance());
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        db.clearEventListeners();
    }

    private void validateUser()
    {
        Query queryRef = Database.root.child(User.TABLE_NAME)
                .orderByKey()
                .equalTo(currentFirebaseUser.getUid());

        queryRef.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.getChildrenCount() == 0)
                {
                    Database.root.child(User.TABLE_NAME)
                            .child(currentFirebaseUser.getUid())
                            .setValue(new User(currentFirebaseUser.getUid(), currentFirebaseUser.getDisplayName(), currentFirebaseUser.getEmail()));
                    Log.d(TAG, "Pushed new user to DB");
                }
                getUser();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    private void getUser()
    {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        Query queryRef = Database.root.child(User.TABLE_NAME)
                .orderByKey()
                .equalTo(auth.getCurrentUser().getUid());

        queryRef.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for(DataSnapshot snap : dataSnapshot.getChildren())
                {
                    Subjects.SUBJECT_CURRENT_USER.onNext(snap.getValue(User.class));
                    Log.d(TAG, "Published a user to SUBJECT_CURRENT_USER: " + snap.getValue(User.class).getName());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    private void getAllUsers()
    {
        DatabaseReference dbRefAllUsers = Database.root.child(User.TABLE_NAME);
        db.addValueEventListener(dbRefAllUsers, new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                ArrayList<User> allUsers = new ArrayList<>();

                for(DataSnapshot snap : dataSnapshot.getChildren())
                {
                    User user = snap.getValue(User.class);
                    allUsers.add(user);
                }

                Subjects.SUBJECT_ALL_USERS.onNext(allUsers);
                Log.d(TAG, "Loaded: " + allUsers.size() + " users from DB");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }
}
