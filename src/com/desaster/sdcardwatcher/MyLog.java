/*
 * setprop log.tag.SDCardWatcherLogging DEBUG
 */
package com.desaster.sdcardwatcher;

import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyLog
{
    public static final String LOG_TAG = "SDCardWatcherLogging";

    public static String timestamp()
    {
        Date d = new Date();
        SimpleDateFormat timeStampFormatter =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        return timeStampFormatter.format(d);
    }

    public static void d(String msgFormat, Object...args)
    {
        String tmsg = String.format("[%s] %s",
            timestamp(), String.format(msgFormat, args));
        if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
            Log.d(LOG_TAG, tmsg);
        }
    }

    public static void d(String msg)
    {
        String tmsg = String.format("[%s] %s",
            timestamp(), msg);
        if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
            Log.d(LOG_TAG, tmsg);
        }
    }

    public static void i(String msgFormat, Object...args)
    {
        String tmsg = String.format("[%s] %s",
            timestamp(), String.format(msgFormat, args));
        if (Log.isLoggable(LOG_TAG, Log.INFO)) {
            Log.i(LOG_TAG, tmsg);
        }
    }

    public static void i(String msg)
    {
        String tmsg = String.format("[%s] %s",
            timestamp(), msg);
        if (Log.isLoggable(LOG_TAG, Log.INFO)) {
            Log.i(LOG_TAG, tmsg);
        }
    }

    public static void e(String msgFormat, Object...args)
    {
        String tmsg = String.format("[%s] %s",
            timestamp(), String.format(msgFormat, args));
        if (Log.isLoggable(LOG_TAG, Log.ERROR)) {
            Log.e(LOG_TAG, tmsg);
        }
    }

    public static void e(String msg)
    {
        String tmsg = String.format("[%s] %s",
            timestamp(), msg);
        if (Log.isLoggable(LOG_TAG, Log.ERROR)) {
            Log.e(LOG_TAG, tmsg);
        }
    }

    public static void v(String msgFormat, Object...args)
    {
        String tmsg = String.format("[%s] %s",
            timestamp(), String.format(msgFormat, args));
        if (Log.isLoggable(LOG_TAG, Log.VERBOSE)) {
            Log.v(LOG_TAG, tmsg);
        }
    }

    public static void v(String msg)
    {
        String tmsg = String.format("[%s] %s",
            timestamp(), msg);
        if (Log.isLoggable(LOG_TAG, Log.VERBOSE)) {
            Log.v(LOG_TAG, tmsg);
        }
    }

    public static void w(String msgFormat, Object...args)
    {
        String tmsg = String.format("[%s] %s",
            timestamp(), String.format(msgFormat, args));
        if (Log.isLoggable(LOG_TAG, Log.WARN)) {
            Log.w(LOG_TAG, tmsg);
        }
    }

    public static void w(String msg)
    {
        String tmsg = String.format("[%s] %s",
            timestamp(), msg);
        if (Log.isLoggable(LOG_TAG, Log.WARN)) {
            Log.w(LOG_TAG, tmsg);
        }
    }
}
