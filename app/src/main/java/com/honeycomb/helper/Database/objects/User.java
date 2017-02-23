package com.honeycomb.helper.Database.objects;

/**
 * Created by Ash on 23/02/2017.
 */

public class User
{
    public static final String TABLE_NAME = "User";

    private String mUserID;
    private String mName;
    private String mEmail;

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
}
