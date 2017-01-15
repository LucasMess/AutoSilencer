package com.bitbite.autosilencer;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.media.AudioManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.SystemClock;
import android.util.Log;

import java.util.ArrayList;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class SilencerService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_CHECKCONNECTIVITY = "com.bitbite.autosilencer.action.checkconnectivity";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.bitbite.autosilencer.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.bitbite.autosilencer.extra.PARAM2";

    public SilencerService() {
        super("SilencerService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionCheckConnectivity(Context context, String param1, String param2) {
        Intent intent = new Intent(context, SilencerService.class);
        intent.setAction(ACTION_CHECKCONNECTIVITY);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_CHECKCONNECTIVITY.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionFoo(param1, param2);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {

            WifiManager wifiMgr = (WifiManager)this.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
            String ssid = wifiInfo.getSSID();
            ssid = RemoveQuotationMarks(ssid);
            Log.d("CONNECT" , ssid);
            AudioManager audiomanager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

            ArrayList<Rule> rules = MainActivity.rules;
            boolean foundRuleOnConnect = false;
            for (int i = 0; i < rules.size(); i++){
                if (rules.get(i).wifiName.equals(ssid)){
                    if (rules.get(i).desiredTriggerAction.equals("On WiFi Connect")) {
                        switch (rules.get(i).desiredRingerMode) {
                            case "Vibrate":
                                audiomanager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                                break;
                            case "Sound":
                                audiomanager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                                break;
                            case "Silent":
                                audiomanager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                                break;
                        }
                        foundRuleOnConnect = true;
                        break;
                    }
                }
            }

            if (!foundRuleOnConnect){
                for (int i = 0; i < rules.size(); i++){
                    if (!rules.get(i).wifiName.equals(ssid)){
                        if (rules.get(i).desiredTriggerAction.equals("On WiFi Disconnect")) {
                            switch (rules.get(i).desiredRingerMode) {
                                case "Vibrate":
                                    audiomanager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                                    break;
                                case "Sound":
                                    audiomanager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                                    break;
                                case "Silent":
                                    audiomanager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                                    break;
                            }
                            break;
                        }
                    }
                }
            }
    }

    private String RemoveQuotationMarks(String string){
        if (string.length() < 2) return string;
        if (string.charAt(0) == '\"' && string.charAt(string.length() - 1) == '\"'){
            return string.substring(1,string.length() - 1);
        }
        return string;
    }

}
