package com.honeycomb.helper.Database.objects;

import java.util.ArrayList;

/**
 * Created by Ash on 23/02/2017.
 */

public class User
{
    public static final String TABLE_NAME = "User";

    private String mUserID;
    private String mName;
    private String mEmail;
    private ArrayList<String> mTasks;
    //private Map

    public User() { } // Needed by Firebase
    public User(String userID, String name, String email)
    {
        mUserID = userID;
        mName = name;
        mEmail = email;
    }

    public String getUserID() { return mUserID; }
    public void setUserID(String userID) { mUserID = userID; }

    public void setEmail(String email) { mEmail = email; }
    public String getName() { return mName; }

    public void setName(String name) { mName = name; }
    public String getEmail() { return mEmail; }

    public void setTasks(ArrayList<String> tasks) { mTasks = tasks; }
    public ArrayList<String> getTasks() { return mTasks; }

    @Override
    public boolean equals(Object obj)
    {
        return this.mUserID.equals(((User)obj).getUserID());
    }
}
