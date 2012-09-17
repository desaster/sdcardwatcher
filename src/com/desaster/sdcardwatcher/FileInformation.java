package com.desaster.sdcardwatcher;

public class FileInformation
{
    public static final int TYPE_FILE = 1;
    public static final int TYPE_FOLDER = 2;

    private String mFilename;
    private String mBasedir;
    private String mApp;

    private int type;

    public FileInformation(String basedir, String filename)
    {
        mFilename = filename;
        mBasedir = basedir;
        type = TYPE_FILE;
        mApp = "";
    }

    public String getBasedir()
    {
        return mBasedir;
    }

    public String getFilename()
    {
        return mFilename;
    }

    public String getApp()
    {
        return mApp;
    }

    public void setApp(String app)
    {
        mApp = app;
    }
}
