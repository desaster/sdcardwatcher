package com.desaster.sdcardwatcher;

import android.widget.ArrayAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.content.Context;
import android.widget.TextView;
import android.graphics.Color;
import java.util.*;

public class FileItemAdapter extends ArrayAdapter<FileInformation>
{
    private List<FileInformation> files;
    private Context mContext;

    public FileItemAdapter(
        Context context, int textViewResourceId, List<FileInformation> files)
    {
        super(context, textViewResourceId, files);
        mContext = context;
        this.files = files;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) mContext.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.filelistitem, null);
        }

        FileInformation file = files.get(position);
        if (file != null) {
            TextView fileView = (TextView) v.findViewById(R.id.filename);
            TextView infoView = (TextView) v.findViewById(R.id.app);

            fileView.setText(file.getFilename());
            String app = file.getApp();
            infoView.setText(app);
            if (app.length() > 0) {
                MyLog.d("File [%s] has app [%s](%d)",
                    file.getFilename(), app, app.length());
                fileView.setTextColor(Color.parseColor("#ffffff"));
            } else {
                fileView.setTextColor(Color.parseColor("#888888"));
            }
        }

        return v;
    }
}
