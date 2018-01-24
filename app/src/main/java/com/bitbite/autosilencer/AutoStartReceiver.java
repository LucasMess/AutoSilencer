package com.bitbite.autosilencer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Lucas on 1/28/2017.
 */

public class AutoStartReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, StayAliveService.class));
    }
}
