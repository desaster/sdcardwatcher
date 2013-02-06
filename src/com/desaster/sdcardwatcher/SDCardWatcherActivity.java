package com.desaster.sdcardwatcher;


import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import java.io.File;
import java.util.*;

public class SDCardWatcherActivity
    extends Activity
    implements DialogInterface.OnDismissListener
{
    private ImageButton mSettingsButton;
    private ImageButton mNewButton;
    private ListView mListView;
    private List<BasedirInformation> mListContents;
    private DatabaseHelper mDB;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        /* handy way to debug, but not needed for release */
        /*
        Thread.setDefaultUncaughtExceptionHandler(
            new CustomExceptionHandler("/mnt/sdcard/boblor/"));
        */
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //addPreferencesFromResource(R.xml.preferences);

        setTitle("SDCard Watcher");

        setContentView(R.layout.main);

        mDB = new DatabaseHelper(this);

        mNewButton = (ImageButton) findViewById(R.id.newbutton);
        mNewButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                MyLog.d("Should add a new basedir here!");
                AlertDialog.Builder alert =
                    new AlertDialog.Builder(SDCardWatcherActivity.this);
                alert.setMessage(
                    "Enter the full path to a directory you wish to " +
                    "monitor:");
                final EditText input =
                    new EditText(SDCardWatcherActivity.this);
                alert.setView(input);
                alert.setPositiveButton("Add",
                        new DialogInterface.OnClickListener() {
                    public void onClick(
                        DialogInterface dialog, int whichButton)
                    {
                        String basedir = input.getText().toString();
                        addBasedir(basedir);
                    }
                });
                alert.setNegativeButton("Cancel", null);
                /* For some reason, Builder doesn't implement
                 * setOnDismissListener, so we do it like this: */
                //alert.show();
                Dialog d = alert.create();
                d.setOnDismissListener(SDCardWatcherActivity.this);
                d.show();
            }
        });

        mSettingsButton = (ImageButton) findViewById(R.id.settingsbutton);
        mSettingsButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),
                    MySettingsActivity.class));
            }
        });
    }

    public void addBasedir(String basedir)
    {
        MyLog.d("main activity: addBasedir() [%s]", basedir);

        String path = basedir;
        if (!path.endsWith("/")) path = path + "/";

        File file;
        try {
            file = new File(basedir);
        } catch (Exception e) {
            errorDialog("Error opening directory: " + path + "\n" +
                e.toString());
            return;
        }

        if (basedir.length() < 1) {
            errorDialog("Invalid directory");
            return;
        } else if (!file.exists()) {
            errorDialog("Directory doesn't exist:\n" + path);
            return;
        } else if (!file.isDirectory()) {
            errorDialog("File is not a directory:\n" + path);
            return;
        } else if (mDB.getBasedirID(path) > 0) {
            errorDialog("Directory already monitored:\n" + path);
            return;
        }

        mDB.addBasedir(path);
    }

    public void errorDialog(String msg)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage(msg);
        alert.setPositiveButton("Ok", null);
        alert.show();
    }


    @Override
    public void onStart()
    {
        super.onStart();
        MyLog.d("main activity: onStart()");
        startService(new Intent(this, SDCardWatcherService.class));
    }

    @Override
    public void onResume()
    {
        super.onResume();
        MyLog.d("main activity: onResume()");
        refreshBasedirList();
    }

    private void refreshBasedirList()
    {
        mListView = (ListView) findViewById(R.id.basedirlist);
        mListContents = new ArrayList<BasedirInformation>();

        for (String basedirname : mDB.getBasedirs()) {
            BasedirInformation tmp = new BasedirInformation(basedirname);
            mListContents.add(tmp);
        }
        BasedirItemAdapter adapter = new BasedirItemAdapter(
            this, R.layout.basedirlistitem, mListContents);
        /* setItemsCanFocus is part of the kludge required to
           have a button in a listitem:
           http://stackoverflow.com/questions/2322390/ */
        mListView.setItemsCanFocus(true); 
        mListView.setAdapter(adapter); 
        adapter.setItemOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                TextView basedirView = (TextView) v.findViewById(R.id.basedir);
                String basedir = basedirView.getText().toString();
                MyLog.d("basedir onclick [%s]", basedir);
                Intent intent = new Intent(
                    getApplicationContext(), FileListActivity.class);
                intent.putExtra("basedir", basedir);
                startActivity(intent);
            }
        });
        adapter.setDeleteOnClickListener(new ImageButton.OnClickListener()
        {
            public void onClick(View v)
            {
                View parent = (View) v.getParent();
                TextView basedirView =
                    (TextView) parent.findViewById(R.id.basedir);
                String basedir = basedirView.getText().toString();
                MyLog.d("basedir DELETE onclick [%s]", basedir);

                AlertDialog.Builder builder = new
                    AlertDialog.Builder(SDCardWatcherActivity.this);
                builder.setMessage(
                    "Click OK to stop monitoring:\n\n" +
                    basedir + "\n\n" +
                    "Any captured data for this directory will be lost!");
                builder.setNegativeButton("Cancel", null);
                builder.setPositiveButton("OK", new BasedirDeleteListener(
                    SDCardWatcherActivity.this, basedir));

                /* For some reason, Builder doesn't implement
                 * setOnDismissListener, so we do it like this: */
                //builder.show();
                Dialog d = builder.create();
                d.setOnDismissListener(SDCardWatcherActivity.this);
                d.show();
            }
        });
    }

    @Override
    public void onDismiss(DialogInterface dialog)
    {
        refreshBasedirList();
        startService(new Intent(this, SDCardWatcherService.class));
    }

    /* Thanks for this solution. I'd like to add: Instead
     * "com.example.MyService" is more elegant to use
     * MyService.class.getName() - teepee Feb 6 at 13:31 */
    private boolean isMyServiceRunning() {
        ActivityManager manager =
            (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service :
                manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.desaster.sdcardwatcher.SDCardWatcherService".equals(
                    service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
