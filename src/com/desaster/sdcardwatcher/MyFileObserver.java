package com.desaster.sdcardwatcher;

import android.os.FileObserver;
import android.os.Handler;
import android.os.Bundle;
import android.content.Context;
import android.os.Message;

public class MyFileObserver extends FileObserver
{
    public static final int EVENT_CREATE = 1;
    public static final int EVENT_DELETE = 2;

    private Context mContext;
    private DatabaseHelper mDB;
    private Handler mHandler;
    private String mAbsolutePath;

    public MyFileObserver(Context context, String path, Handler handler)
    {
        super(path, FileObserver.CREATE | FileObserver.DELETE);
        mContext = context;
        mAbsolutePath = path;
        mHandler = handler;
        mDB = new DatabaseHelper(mContext);
    }

    @Override
    public void onEvent(int event, String path)
    {
        if (path == null) return;
        MyLog.d("onEvent(%d, %s)", event, path);
        if ((FileObserver.CREATE & event) != 0) {
            Message msg = Message.obtain();
            msg.what = EVENT_CREATE;
            Bundle bundle = new Bundle();
            bundle.putString("basedir", mAbsolutePath);
            bundle.putString("path", path);
            msg.setData(bundle);
            mHandler.sendMessage(msg);
        } else if ((FileObserver.DELETE & event) != 0) {
            Message msg = Message.obtain();
            msg.what = EVENT_DELETE;
            Bundle bundle = new Bundle();
            bundle.putString("basedir", mAbsolutePath);
            bundle.putString("path", path);
            msg.setData(bundle);
            mHandler.sendMessage(msg);
        }
    }

    public String getBasedir()
    {
        return mAbsolutePath;
    }
}
