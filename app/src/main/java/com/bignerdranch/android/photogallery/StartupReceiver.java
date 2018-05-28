package com.bignerdranch.android.photogallery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class StartupReceiver extends BroadcastReceiver {
    public static final String TAG = "STARTUP_RECEIVER";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Received broadcast intent: " + intent.getAction());
        boolean isAlarmOn = QueryPreferences.getAlarmOn(context);
        PollService.setServiceAlarm(context, isAlarmOn);
    }
}
