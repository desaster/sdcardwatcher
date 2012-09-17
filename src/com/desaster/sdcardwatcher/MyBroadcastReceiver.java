package com.desaster.sdcardwatcher;

import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;

public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent startServiceIntent =
            new Intent(context, SDCardWatcherService.class);
        context.startService(startServiceIntent);
    }
}
