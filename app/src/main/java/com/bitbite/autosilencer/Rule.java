package com.bitbite.autosilencer;

import android.media.AudioManager;

/**
 * Created by Lucas on 10/13/2016.
 */

public class Rule {
    String wifiName;
    public String desiredRingerMode;
    public String desiredTriggerAction;

    public Rule(String wifiName, String desiredRingerMode, String desiredTriggerAction){
        this.wifiName = wifiName;
        this.desiredRingerMode = desiredRingerMode;
        this.desiredTriggerAction = desiredTriggerAction;
    }

}
