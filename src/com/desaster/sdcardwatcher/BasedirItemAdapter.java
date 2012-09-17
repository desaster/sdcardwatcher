package com.desaster.sdcardwatcher;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import java.util.*;

public class BasedirItemAdapter extends ArrayAdapter<BasedirInformation>
{
    private List<BasedirInformation> basedirs;
    private Context mContext;
    private View mView;
    private View.OnClickListener mItemOnClickListener;
    private ImageButton.OnClickListener mDeleteOnClickListener;

    public BasedirItemAdapter(
        Context context, int textViewResourceId,
        List<BasedirInformation> basedirs)
    {
        super(context, textViewResourceId, basedirs);
        mContext = context;
        mView = null;
        mItemOnClickListener = null;
        mDeleteOnClickListener = null;
        this.basedirs = basedirs;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        mView = convertView;
        if (mView == null) {
            LayoutInflater vi = (LayoutInflater) mContext.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
            mView = vi.inflate(R.layout.basedirlistitem, null);
        }

        BasedirInformation basedir = basedirs.get(position);

        if (basedir != null) {
            TextView basedirView = (TextView) mView.findViewById(R.id.basedir);
            TextView infoView = (TextView) mView.findViewById(R.id.info);

            basedirView.setText(basedir.getBasedir());

            DatabaseHelper db = new DatabaseHelper(mContext);
            String lastUpdate = db.getLastUpdate(basedir.getBasedir());
            if (lastUpdate == null) {
                lastUpdate = "never";
            }
            infoView.setText(lastUpdate);
        }

        mView.setClickable(true);
        mView.setFocusable(true);
        mView.setBackgroundResource(android.R.drawable.menuitem_background);

        if (mItemOnClickListener != null) {
            mView.setOnClickListener(mItemOnClickListener);
        }
        if (mDeleteOnClickListener != null) {
            ImageButton deleteView =
                (ImageButton) mView.findViewById(R.id.basedirdeletebutton);
            deleteView.setOnClickListener(mDeleteOnClickListener);
        }

        return mView;
    }

    public void setItemOnClickListener(View.OnClickListener handler)
    {
        mItemOnClickListener = handler;
    }

    public void setDeleteOnClickListener(ImageButton.OnClickListener handler)
    {
        mDeleteOnClickListener = handler;
    }
}
