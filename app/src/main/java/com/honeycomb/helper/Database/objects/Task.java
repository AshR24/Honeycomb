package com.honeycomb.helper.Database.objects;

/**
 * Created by Ash on 30/01/2017.
 */

public class Task
{
    public static final String TABLE_NAME = "Task";

    private String projectID;
    private String taskID;
    private String name;
    private String description;

    public String getProjectID() { return projectID; }
    public void setProjectID(String projectID) { this.projectID = projectID; }

    public String getTaskID() { return taskID; }
    public void setTaskID(String taskID) { this.taskID = taskID; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
