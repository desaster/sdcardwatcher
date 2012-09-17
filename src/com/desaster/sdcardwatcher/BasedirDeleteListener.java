package com.desaster.sdcardwatcher;

import android.content.DialogInterface;
import android.content.Context;

public class BasedirDeleteListener implements DialogInterface.OnClickListener
{
    private String mBasedir;
    private Context mContext;

    public BasedirDeleteListener(Context context, String basedir)
    {
        mBasedir = basedir;
        mContext = context;
    }

    @Override
    public void onClick(DialogInterface dialog, int which)
    {
        MyLog.d("YES we can delete! [%s]", mBasedir);
        DatabaseHelper db = new DatabaseHelper(mContext);
        db.deleteBasedir(mBasedir);
        dialog.dismiss();
    }
}
