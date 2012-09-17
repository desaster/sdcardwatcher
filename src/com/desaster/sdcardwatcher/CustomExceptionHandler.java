/*
 * CustomExceptionHandler - writes stack traces to files
 *
 * adapted from http://stackoverflow.com/questions/601503/
 * (TODO: some apparently neater solution further down the page)j
 *
 * Just call the following in onCreate:
 *   Thread.setDefaultUncaughtExceptionHandler(
 *     new CustomExceptionHandler("/mnt/sdcard/foobar/"));
 */

package com.desaster.sdcardwatcher;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.lang.Thread.UncaughtExceptionHandler; 

public class CustomExceptionHandler implements UncaughtExceptionHandler
{
    private UncaughtExceptionHandler defaultUEH;
    private String localPath;

    public CustomExceptionHandler(String localPath)
    {
        this.localPath = localPath;
        this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
    }

    public void uncaughtException(Thread t, Throwable e)
    {
        Date d = new Date();
        SimpleDateFormat timeStampFormatter =
            new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String timestamp = timeStampFormatter.format(d);

        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        e.printStackTrace(printWriter);
        String stacktrace = result.toString();
        printWriter.close();
        String filename = timestamp + ".stacktrace";

        writeToFile(stacktrace, filename);
        defaultUEH.uncaughtException(t, e);
    }

    private void writeToFile(String stacktrace, String filename)
    {
        try {
            BufferedWriter bos = new BufferedWriter(new FileWriter(
                localPath + "/" + filename));
            bos.write(stacktrace);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

/* vim: set sw=4 et: */
