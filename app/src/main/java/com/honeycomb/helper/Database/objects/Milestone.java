package com.honeycomb.helper.Database.objects;

/**
 * Created by Ash on 30/01/2017.
 */

public class Milestone
{
    public static final String TABLE_NAME = "Milestone";

    private String taskID;
    private String milestoneID;
    private String name;
    private String description;
    private boolean isCompleted;

    public String getTaskID() { return taskID; }
    public void setTaskID(String taskID) { this.taskID = taskID; }

    public String getMilestoneID() { return milestoneID; }
    public void setMilestoneID(String milestoneID) { this.milestoneID = milestoneID; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean isCompleted) { this.isCompleted = isCompleted; }
}
