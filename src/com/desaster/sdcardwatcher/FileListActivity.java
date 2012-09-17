package com.desaster.sdcardwatcher;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.view.Window;
import java.util.*;
import java.io.File;

public class FileListActivity extends Activity
{
    private DatabaseHelper mDB;
    private ListView mListView;
    private List<FileInformation> mListContents;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.filelist);

        mDB = new DatabaseHelper(this);

        Bundle extras = getIntent().getExtras();
        String basedir = extras.getString("basedir");

        TextView fileListTitle = (TextView) findViewById(R.id.filelisttitle);
        fileListTitle.setText(basedir);

        mListView = (ListView) findViewById(R.id.filelistview);
        mListContents = new ArrayList<FileInformation>();

        File dir;
        try {
            dir = new File(basedir);
        } catch (Exception e) {
            MyLog.d("Error opening basedir: %s", e.toString());
            return;
        }
        if (!dir.exists() ||!dir.exists()) {
            MyLog.d("Directory doesn't exist or is not a directory: %s",
                basedir);
            return;
        }
        for (File file : dir.listFiles()) {
            MyLog.d("File: [%s][%s]", file.getName(), file.getAbsolutePath());

            FileInformation info =
                new FileInformation(basedir, file.getName());
            String app = mDB.getApp(mDB.getBasedirID(basedir), file.getName());
            if (app != null) {
                info.setApp(app);
            }
            mListContents.add(info);
        }


        FileItemAdapter adapter = new FileItemAdapter(
            this, R.layout.basedirlistitem, mListContents);
        mListView.setAdapter(adapter); 
    }
}
