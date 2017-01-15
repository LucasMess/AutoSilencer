package com.bitbite.autosilencer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static ArrayList<Rule> rules = new ArrayList<>();
    public static RuleListAdapter ruleListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        setTitle("Rules");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_rule);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext() ,AddRuleActivity.class);
                startActivity(intent);
            }
        });

        if (rules.isEmpty()){
            GetSavedRules();
        }

        ruleListAdapter = new RuleListAdapter(this, android.R.layout.simple_list_item_1, rules);

        ListView listView = (ListView)findViewById(R.id.rules_list);
        listView.setAdapter(ruleListAdapter);

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

    // Looks for the file with the saved rules on the device's storage.
    public void GetSavedRules(){

        Log.d("SavedRules", "Getting saved rules.");
        // Reads file.
        String text = "";
        try {
            FileInputStream fis = openFileInput(AddRuleActivity.FILE_NAME);
            text = convertStreamToString(fis);
            Log.d("SavedRules", text);
            fis.close();
        }
        catch (FileNotFoundException e){
            Log.d("FILENOTFOUND", ":(");
        }
        catch (IOException e){

        }

        ArrayList<Rule> rules = new ArrayList<>();

        String lines[] = text.split("\\r?\\n");
        if (lines[0].equals("autosilencer_1")){
            int count = 1;
            while (!lines[count].equals("===END===")){
                Log.d("READING LINE", Integer.toString(count));
                String wifiName = lines[count];
                count++;
                String desiredRinger = lines[count];
                count++;
                String desiredAction = lines[count];
                count++;

                rules.add(new Rule(wifiName, desiredRinger, desiredAction));
            }
        }

        MainActivity.rules = rules;
        Log.d("SavedRules", "Assigned new rules.");
    }

    // Converts a stream into a string. Credit: http://stackoverflow.com/questions/10752919/how-can-i-convert-inputstream-data-to-string-in-android-soap-webservices
    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

}
