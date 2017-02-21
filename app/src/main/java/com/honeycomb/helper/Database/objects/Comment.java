package com.honeycomb.helper.Database.objects;

/**
 * Created by Ash on 18/02/2017.
 */

public class Comment
{
    public static final String TABLE_NAME = "Comment";

    private String milestoneID;
    private String commentID;
    private String userID;
    private String comment;
    private String datePosted;

    public String getMilestoneID() { return milestoneID; }
    public void setMilestoneID(String milestoneID) { this.milestoneID = milestoneID; }

    public String getCommentID() { return commentID; }
    public void setCommentID(String commentID) { this.commentID = commentID; }

    public String getUserID() { return userID; }
    public void setUserID(String userID) { this.userID = userID; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getDatePosted() { return datePosted; }
    public void setDatePosted(String datePosted) { this.datePosted = datePosted; }
}
