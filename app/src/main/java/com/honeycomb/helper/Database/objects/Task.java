package com.honeycomb.helper.Database.objects;

import java.util.ArrayList;

/**
 * Created by Ash on 30/01/2017.
 */

public class Task
{
    public static final String TABLE_NAME = "Task";

    private String mProjectID;
    private String mTaskID;
    private String mName;
    private String mDescription;
    private String mDeadline;
    private ArrayList<String> mMembers;

    public Task() { } // Needed by Firebase
    public Task(String projectID, String taskID, String name, String description, String deadline)
    {
        mProjectID = projectID;
        mTaskID = taskID;
        mName = name;
        mDescription = description;
        mDeadline = deadline;
        mMembers = new ArrayList<>();
    }

    public String getProjectID() { return mProjectID; }
    public void setProjectID(String projectID) { mProjectID = projectID; }

    public String getTaskID() { return mTaskID; }
    public void setTaskID(String taskID) { this.mTaskID = taskID; }

    public String getName() { return mName; }
    public void setName(String name) { this.mName = name; }

    public String getDeadline() { return mDeadline; }
    public void setDeadline(String deadline) { this.mDeadline = deadline; }

    public String getDescription() { return mDescription; }
    public void setDescription(String description) { this.mDescription = description; }

    public ArrayList<String> getMembers() { return mMembers; }
    public void setMembers(ArrayList<String> members) { mMembers = members; }
}
