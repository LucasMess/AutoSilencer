package com.bitbite.autosilencer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

public class StayAliveService extends Service {
    public StayAliveService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartService = new Intent(getApplicationContext(),this.getClass());
        restartService.setPackage(getPackageName());
        PendingIntent restartSevicePI = PendingIntent.getService(getApplicationContext(),
                1 , restartService, PendingIntent.FLAG_ONE_SHOT);

        AlarmManager myAlarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        myAlarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartSevicePI);


        Log.d("STAYALIVE", "Restarted service");
        super.onTaskRemoved(rootIntent);

    }

    @Override
    public void onCreate() {
        super.onCreate();

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();
                if (action.equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)) {
                    Log.d("NETWORKCHANGE", "network changed");
                    SilencerService.startActionCheckConnectivity(context,"BLAH","COOL");
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        //intentFilter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
        intentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);
    }
}
