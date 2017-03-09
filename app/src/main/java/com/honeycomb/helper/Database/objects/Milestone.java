package com.honeycomb.helper.Database.objects;

/**
 * Created by Ash on 30/01/2017.
 */

public class Milestone
{
    public static final String TABLE_NAME = "Milestone";

    private String mTaskID;
    private String mMilestoneID;
    private String mName;
    private String mDescription;
    private String mDeadline;
    private boolean mIsCompleted;

    public Milestone() { } // Needed by Firebase
    public Milestone(String taskID, String milestoneID, String name, String description, String deadline, Boolean isCompleted)
    {
        mTaskID = taskID;
        mMilestoneID = milestoneID;
        mName = name;
        mDescription = description;
        mDeadline = deadline;
        mIsCompleted = isCompleted;
    }

    public String getTaskID() { return mTaskID; }
    public void setTaskID(String taskID) { mTaskID = taskID; }

    public String getMilestoneID() { return mMilestoneID; }
    public void setMilestoneID(String milestoneID) { mMilestoneID = milestoneID; }

    public String getName() { return mName; }
    public void setName(String name) { mName = name; }

    public String getDescription() { return mDescription; }
    public void setDescription(String description) { mDescription = description; }

    public String getDeadline() { return mDeadline; }
    public void setDeadline(String deadline) { mDeadline = deadline; }

    public boolean isCompleted() { return mIsCompleted; }
    public void setCompleted(boolean isCompleted) { mIsCompleted = isCompleted; }

    @Override
    public boolean equals(Object obj)
    {
        return this.mMilestoneID.equals(((Milestone)obj).getMilestoneID());
    }
}
