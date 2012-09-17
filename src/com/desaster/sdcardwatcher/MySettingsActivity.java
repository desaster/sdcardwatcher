package com.desaster.sdcardwatcher;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class MySettingsActivity extends PreferenceActivity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setTitle("SDCardWatcher Settings");
        addPreferencesFromResource(R.xml.preferences);
    }
}
