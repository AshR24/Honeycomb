package com.honeycomb.activities;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.honeycomb.R;
import com.honeycomb.fragments.FragMain;
import com.honeycomb.helper.Database.Database;
import com.honeycomb.helper.Database.objects.User;

public class Main extends baseActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        validateUser();
        fragmentHelper.switchToFragment(R.id.fragment_container, FragMain.newInstance());
    }

    private void validateUser()
    {
        Query queryRef = Database.root.child(User.TABLE_NAME)
                .orderByKey()
                .equalTo(currentUser.getUid());

        queryRef.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.getChildrenCount() == 0)
                {
                    Database.root.child(User.TABLE_NAME)
                            .child(currentUser.getUid())
                            .setValue(new User(currentUser.getUid(), currentUser.getDisplayName(), currentUser.getEmail()));
                    Log.d(TAG, "SENT NEW USER TO DATABASE ----------------------------------------");
                }
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
