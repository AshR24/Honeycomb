package com.honeycomb.helper.Database.objects;

/**
 * Created by Ash on 18/02/2017.
 */

public class Comment
{
    public static final String TABLE_NAME = "Comment";

    private String mMilestoneID;
    private String mCommentID;
    private String mUserID;
    private String mComment;
    private String mDatePosted;

    public Comment() { } // Needed by Firebase
    public Comment(String milestoneID, String commentID, String userID, String comment, String datePosted)
    {
        mMilestoneID = milestoneID;
        mCommentID = commentID;
        mUserID = userID;
        mComment = comment;
        mDatePosted = datePosted;
    }

    public String getMilestoneID() { return mMilestoneID; }
    public void setMilestoneID(String milestoneID) { mMilestoneID = milestoneID; }

    public String getCommentID() { return mCommentID; }
    public void setCommentID(String commentID) { mCommentID = commentID; }

    public String getUserID() { return mUserID; }
    public void setUserID(String userID) { mUserID = userID; }

    public String getComment() { return mComment; }
    public void setComment(String comment) { mComment = comment; }

    public String getDatePosted() { return mDatePosted; }
    public void setDatePosted(String datePosted) { mDatePosted = datePosted; }
}
