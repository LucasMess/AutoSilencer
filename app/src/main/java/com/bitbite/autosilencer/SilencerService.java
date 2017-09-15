package com.bitbite.autosilencer;

import android.app.AlertDialog;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.v4.app.NotificationBuilderWithBuilderAccessor;
import android.support.v4.app.NotificationCompat;
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

    /**
     * From Marshmallow and up, the application cannot change "Do Not Disturb" settings, so the app will crash.
     * To prevent this, check if the permissions have been granted, and if they have not, prompt the user to do it.
     */
    public static boolean hasDoNotDisturbPermission(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && !notificationManager.isNotificationPolicyAccessGranted()) {
            return false;
        }
        return true;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_CHECKCONNECTIVITY.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                changeRinger(param1, param2);
            }
        }
    }

    /**
     * Sends a notification to the user letting them know that the app needs to be granted access to the notification policy
     * in order to run.
     */
    private void notifyInsufficientPermissions() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_notifications_black_24dp);
        builder.setContentTitle("Insufficient Permissions");
        builder.setContentText("Launch the application to grant access.");

        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.putExtra("REQUEST_PERMISSION", true);
        // Because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);

        int notificiationId = 10;
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(notificiationId, builder.build());
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void changeRinger(String param1, String param2) {

        if (!hasDoNotDisturbPermission(getApplicationContext())) {
            notifyInsufficientPermissions();
            return;
        }

        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(
                getString(R.string.AS_preferences), getApplicationContext().MODE_PRIVATE);
        String active = sharedPref.getString(getString(R.string.AS_isActive), "true");
        Log.d("SilencerService", "Is active? " + active);
        if (active.equals("false")) return;

        WifiManager wifiMgr = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        String ssid = wifiInfo.getSSID();
        ssid = RemoveQuotationMarks(ssid);
        Log.d("CONNECT", ssid);
        AudioManager audiomanager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        RuleManager ruleManager = new RuleManager();
        ArrayList<Rule> rules = ruleManager.getSavedRules(getApplicationContext());

        if (rules.size() == 0) {
            Log.d("SilencerService", "No rules found. Setting ringer to silent to prevent disturbances.");
            audiomanager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            return;
        }

        boolean foundRuleOnConnect = false;
        for (int i = 0; i < rules.size(); i++) {
            if (rules.get(i).wifiName.equals(ssid)) {
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

        if (!foundRuleOnConnect) {
            for (int i = 0; i < rules.size(); i++) {
                if (!rules.get(i).wifiName.equals(ssid)) {
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

    private String RemoveQuotationMarks(String string) {
        if (string.length() < 2) return string;
        if (string.charAt(0) == '\"' && string.charAt(string.length() - 1) == '\"') {
            return string.substring(1, string.length() - 1);
        }
        return string;
    }

}
