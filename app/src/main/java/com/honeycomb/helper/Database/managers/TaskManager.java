package com.honeycomb.helper.Database.managers;

import com.honeycomb.Subjects;
import com.honeycomb.helper.Database.Database;
import com.honeycomb.helper.Database.objects.Milestone;
import com.honeycomb.helper.Database.objects.Task;
import com.honeycomb.helper.Database.objects.User;

import java.util.ArrayList;

/**
 * Created by Ash on 02/03/2017.
 */

public class TaskManager
{
    public static final String TAG = TaskManager.class.getSimpleName();

    private Task mTask;
    private MemberManager mMemberManager;

    private ArrayList<User> mUsersInDB;

    public TaskManager(Task task, Database db, MemberManager memberManager)
    {
        mTask = task;
        mMemberManager = memberManager;

        db.addSubscriber(Subjects.SUBJECT_CURRENT_MEMBERS.subscribe(users ->
        {
            mUsersInDB = new ArrayList<>(users);
        }));
    }

    /**
     * Finds differences between lists - updates accordingly
     * @param updated
     */
    public void compareMembersToOld(ArrayList<User> updated)
    {
        ArrayList<User> newUsers = new ArrayList<>(mUsersInDB);

        for(User user : newUsers)
        {
            if(!updated.contains(user))
            {
                MemberManager.updateUserTasks(mTask, user, false);
            }
        }

        for(User user : updated)
        {
            if(!newUsers.contains(user))
            {
                MemberManager.updateUserTasks(mTask, user, true);
            }
        }

        mMemberManager.updateMembers(new ArrayList<>(updated));
    }

    /**
     * Adds the redundant data for 2-way referencing
     * @param milestone
     * @param task
     * @param isAdd
     */
    public static void addMilestoneToTasks(Milestone milestone, Task task, boolean isAdd)
    {
        ArrayList<String> milestones = task.getMilestones() != null ? task.getMilestones() : new ArrayList<>();

        if(isAdd)
        {
            if(!milestones.contains(milestone.getMilestoneID()))
            {
                milestones.add(milestone.getMilestoneID());
            }
        }
        else
        {
            milestones.remove(milestone.getMilestoneID());
        }

        task.setMilestones(milestones);
        Database.root.child(Task.TABLE_NAME)
                .child(task.getTaskID())
                .child("milestones")
                .setValue(milestones);
    }
}
