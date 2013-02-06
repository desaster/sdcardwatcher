package com.desaster.sdcardwatcher;

import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.content.Context;
import android.content.ContentValues;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper
{
    static final String mDBName = "sdcardwatcher";

    public DatabaseHelper(Context context) {
        super(context, mDBName, null, 1); 
    }

    public void onCreate(SQLiteDatabase db) {
        MyLog.d("SQLite onCreate");
        db.execSQL("CREATE TABLE basedirs (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "path TEXT)");
        db.execSQL("CREATE TABLE files (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "basedir INTEGER, " +
            "filename TEXT, " +
            "app TEXT, " +
            "timestamp DATE DEFAULT (datetime('now','localtime')))");
        db.execSQL(
            "INSERT INTO basedirs (path) VALUES (\"/sdcard/\")");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
    }

    public List<String> getBasedirs()
    {
        /*
        SQLiteDatabase foo = this.getWritableDatabase();
        foo.execSQL(
            "DELETE FROM basedirs WHERE path = \"/sdcard/boblor/\"");
        foo.execSQL(
            "INSERT INTO basedirs (path) VALUES (\"/sdcard/boblor/\")");
        foo.close();
        */

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(
            "basedirs",
            new String[]{"path"},
            null, null, null, null, null);

        List<String> entries = new ArrayList<String>();
        while (c.moveToNext()) {
            entries.add(c.getString(0));
        }
        db.close();
        return entries;
    }

    public int getBasedirID(String basedir)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(
            "basedirs",
            new String[]{"id"},
            "path = ?",
            new String[]{basedir},
            null, null, null);
        MyLog.d("getBasedirID() getCount(): [%d]", c.getCount());
        if (c.getCount() < 1) {
            db.close();
            return 0;
        }
        c.moveToFirst();
        int retval = c.getInt(0);
        c.close();
        return retval;
    }

    public void deleteFile(String basedir, String path)
    {
        int id = getBasedirID(basedir);
        if (id < 1) {
            MyLog.e("Could not delete file from db because basedir was 0!");
            return;
        }
        deleteFile(id, path);
    }

    public void deleteFile(int basedir_id, String path)
    {
        MyLog.d("deleteFile() [%d][%s]", basedir_id, path);
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("files",
            "basedir=? AND filename=?",
            new String[]{Integer.toString(basedir_id), path});
        db.close();
    }

    public void deleteBasedir(String basedir)
    {
        int id = getBasedirID(basedir);
        if (id < 1) {
            MyLog.e("Could not delete basedir from db because basedir was 0!");
            return;
        }
        deleteBasedir(id);
    }


    public void deleteBasedir(int basedir_id)
    {
        MyLog.d("deleteBasedir() [%d]", basedir_id);
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("files", "basedir=?",
            new String[]{Integer.toString(basedir_id)});
        db.delete("basedirs", "id=?",
            new String[]{Integer.toString(basedir_id)});
        db.close();
    }

    public void addBasedir(String basedir)
    {
        MyLog.d("addBasedir() [%s]", basedir);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("path", basedir);
        db.insert("basedirs", null, values);
        db.close();
    }

    public void addFile(String basedir, String path, String app)
    {
        int id = getBasedirID(basedir);
        if (id < 1) {
            MyLog.e("Could not add file to db because basedir was 0!");
            return;
        }

        deleteFile(basedir, path);

        MyLog.d("addFile() [%d][%s][%s]", id, path, app);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("basedir", id);
        values.put("filename", path);
        values.put("app", app);
        //values.put("package", packageName);
        db.replace("files", null, values);
        db.close();
    }

    public String getApp(int basedir, String filename)
    {
        MyLog.d("getApp() [%s][%s]", basedir, filename);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(
            "files",
            new String[]{"app"},
            "basedir = ? AND filename = ?",
            new String[]{Integer.toString(basedir), filename},
            null, null, null);
        MyLog.d("getApp() getCount(): [%d]", c.getCount());
        if (c.getCount() < 1) {
            db.close();
            return null;
        }
        c.moveToFirst();
        String retval = c.getString(0);
        db.close();
        return retval;
    }

    public String getLastUpdate(int basedir)
    {
        MyLog.d("getLastUpdate() (int) [%d]", basedir);
        SQLiteDatabase db = this.getReadableDatabase();

        // SELECT timestamp FROM files ORDER BY timestamp DESC LIMIT 1
        Cursor c = db.query(
            "files",
            new String[]{"timestamp"},
            "basedir = ?",
            new String[]{Integer.toString(basedir)},
            null, null, "timestamp DESC", "1");
        MyLog.d("getLastUpdate() getCount(): [%d]", c.getCount());
        if (c.getCount() < 1) {
            db.close();
            return null;
        }
        c.moveToFirst();
        String retval = c.getString(0);
        db.close();
        return retval;
    }

    public String getLastUpdate(String basedir)
    {
        MyLog.d("getLastUpdate() (String) [%s]", basedir);
        int id = getBasedirID(basedir);
        if (id < 1) {
            MyLog.e("Could not call getLastUpdate() because basedir was 0!");
            return null;
        }
        return getLastUpdate(id);
    }
}
