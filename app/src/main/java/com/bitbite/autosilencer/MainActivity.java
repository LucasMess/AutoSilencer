package com.bitbite.autosilencer;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static ArrayList<Rule> rules = new ArrayList<>();

    public static void SaveRules(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_rule);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext() ,AddRuleActivity.class);
                startActivity(intent);
            }
        });

        String[] testValues = new String[]{"hi", "cool", "testing!"};
        final ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < testValues.length; i++){
            list.add(testValues[i]);
        }
        WifiManager wifiMgr = (WifiManager)this.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        list.add(wifiInfo.getSSID());

        SilencerService.startActionCheckConnectivity(this,"BLAH","COOL");
//        AudioManager audiomanage = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
//        if (list.contains("\"308 Green\"")) {
//            audiomanage.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
//        }
//        else
//        {
//            audiomanage.setRingerMode(AudioManager.RINGER_MODE_SILENT);
//        }
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,list);

        ListView listView = (ListView)findViewById(R.id.rules_list);
        listView.setAdapter(adapter);
    }

}
