package com.honeycomb.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.honeycomb.R;
import com.honeycomb.Subjects;
import com.honeycomb.activities.Splash;
import com.honeycomb.helper.Database.objects.User;

import java.util.ArrayList;

public class NotificationService extends IntentService
{
    private static final String TAG = NotificationService.class.getSimpleName();

    private static User currentUser;

    public NotificationService()
    {
        super(NotificationService.class.getSimpleName());
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId)
    {
        Log.d(TAG, "RECEIVED START ID: " + startId + " : " + intent);
        Toast.makeText(this, "Service starting...", Toast.LENGTH_SHORT).show();
        return START_STICKY;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent)
    {

    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        Log.d(TAG, "onHandleIntent");
        Subjects.SUBJECT_CURRENT_USER.subscribe(this::loadTasks);
    }

    private void loadTasks(User user)
    {
        if(currentUser == null) { currentUser = user; }
        compareTasksToOld(user);
    }

    private void compareTasksToOld(User user)
    {
        ArrayList<String> currentTasks = currentUser.getTasks() != null ? currentUser.getTasks() : new ArrayList<>();
        ArrayList<String> newTasks = user.getTasks() != null ? user.getTasks() : new ArrayList<>();

        if(newTasks.size() > currentTasks.size())
        {
            Log.d(TAG, "Got new task");
            sendNotification();
        }

        currentUser = user;
    }

    private void sendNotification()
    {
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Intent resultIntent = new Intent(this, Splash.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.background_splash)
                .setContentTitle("New Task")
                .setContentText("A new task has been assigned to you, click here to open app")
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationManager.notify(100, builder.build());
    }
}
