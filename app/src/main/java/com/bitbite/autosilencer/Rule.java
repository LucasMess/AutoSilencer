package com.bitbite.autosilencer;

import android.media.AudioManager;

/**
 * Created by Lucas on 10/13/2016.
 */

public class Rule {
    String wifiName;
    RingerMode desiredRingerMode;
    public enum RingerMode{
        Silent, Vibrate, Sound
    }
    TriggerAction desiredTriggerAction;
    public enum TriggerAction{
        OnWifiConnect, OnWifiDisconnect
    }

    public Rule(String wifiName, String desiredRingerMode, String desiredTriggerAction){
        this.wifiName = wifiName;

        switch (desiredRingerMode){
            case "Silent":
                this.desiredRingerMode = RingerMode.Silent;
                break;
            case "Vibrate":
                this.desiredRingerMode = RingerMode.Vibrate;
                break;
            case "Sound":
                this.desiredRingerMode = RingerMode.Sound;
                break;
        }

        switch (desiredTriggerAction){
            case "On WiFi Disconnect":
                this.desiredTriggerAction = TriggerAction.OnWifiConnect;
                break;
            case "On WiFi Connect":
                this.desiredTriggerAction = TriggerAction.OnWifiDisconnect;
                break;
        }
    }

}
