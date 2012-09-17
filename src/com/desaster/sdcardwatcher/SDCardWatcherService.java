package com.desaster.sdcardwatcher;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ApplicationInfo;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.widget.Toast;
import java.lang.Runnable;
import java.util.*;

public class SDCardWatcherService extends Service
{
    private List<MyFileObserver> mFileObservers;
    private DatabaseHelper mDB;

    private final Handler mHandler = new Handler() {
        public void handleMessage(Message msg)
        {
            Bundle bundle = msg.getData();
            switch (msg.what) {
                case MyFileObserver.EVENT_CREATE:
                    fileAdd(bundle.getString("basedir"),
                        bundle.getString("path"));
                    break;
                case MyFileObserver.EVENT_DELETE:
                    fileDelete(bundle.getString("basedir"),
                        bundle.getString("path"));
                    break;
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onCreate()
    {
        MyLog.d("onCreate()");
        mFileObservers = null;
        //Toast.makeText(this,
        //    "SDCardWatcher Service Created", Toast.LENGTH_LONG).show();
    }

    public void refreshObservers()
    {
        if (mFileObservers != null) {
            for (MyFileObserver obs : mFileObservers) {
                MyLog.d("stopWatching() %s", obs.getBasedir());
                obs.stopWatching();
            }
        }
        mFileObservers = new ArrayList<MyFileObserver>();
        mDB = new DatabaseHelper(this);
        for (String basedir : mDB.getBasedirs()) {
            MyLog.d("new MyFileObserver() %s", basedir);
            MyFileObserver obs = 
                new MyFileObserver(this, basedir, mHandler);
            mFileObservers.add(obs);
        }
        for (MyFileObserver obs : mFileObservers) {
            MyLog.d("startWatching() %s", obs.getBasedir());
            obs.startWatching();
        }
    }

    @Override
    public void onDestroy()
    {
        MyLog.d("onDestroy()");
        for (MyFileObserver obs : mFileObservers) {
            MyLog.d("stopWatching() %s", obs.getBasedir());
            obs.stopWatching();
        }
        mFileObservers = null;
        //Toast.makeText(this,
        //    "SDCardWatcher Service Stopped", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStart(Intent intent, int startid)
    {
        MyLog.d("onStart()");
        refreshObservers();
        //Toast.makeText(this,
        //    "SDCardWatcher Service Started", Toast.LENGTH_LONG).show();
        SharedPreferences sharedPrefs =
            PreferenceManager.getDefaultSharedPreferences(this);
        hideNotification();
        if (sharedPrefs.getBoolean("display_notification", false)) {
            MyLog.d("We should display a notification");
            showNotification();
        }
    }

    public void showNotification()
    {
        NotificationManager nm =
            (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Notification note = new Notification(
            R.drawable.ic_stat_notify,
            "Service is running",
            System.currentTimeMillis());
        Intent i = new Intent(this, SDCardWatcherActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
            Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);
        note.setLatestEventInfo(this, "SDCard Watcher",
                "Service is running", pi);
        note.flags |= Notification.FLAG_NO_CLEAR;

        startForeground(1, note);
    }

    public void hideNotification()
    {
        stopForeground(true);
    }

    public void fileAdd(String basedir, String path)
    {
        SharedPreferences sharedPrefs =
            PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPrefs.getBoolean("display_toast", true)) {
            Toast.makeText(this, "New file on SDCard: " + path,
                Toast.LENGTH_LONG).show();
        }
        MyLog.d("fileAdd(): [%s][%s]", basedir, path);

        String app = getForegroundApp();
        mDB.addFile(basedir, path, getApplicationName(getForegroundApp()));
    }

    public void fileDelete(String basedir, String path)
    {
        SharedPreferences sharedPrefs =
            PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPrefs.getBoolean("display_toast", true)) {
            Toast.makeText(this, "File deleted on SDCard: " + path,
                Toast.LENGTH_LONG).show();
        }
        MyLog.d("fileDelete(): [%s][%s]", basedir, path);
        mDB.deleteFile(basedir, path);
    }

    private String getForegroundApp()
    {
        ActivityManager activityManager =
            (ActivityManager) this.getSystemService(
            Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> appProcesses =
            activityManager.getRunningTasks(1);
        return appProcesses.get(0).topActivity.getPackageName();
    }

    private String getApplicationName(String packageName)
    {
        PackageManager pm = getPackageManager();
        ApplicationInfo ai;
        try {
            ai = pm.getApplicationInfo(packageName, 0);
        } catch (final PackageManager.NameNotFoundException e) {
            ai = null;
        }
        //final String applicationName =
        //    (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
        return (String) (ai != null ?
            pm.getApplicationLabel(ai) : "(unknown)");
    }

}
