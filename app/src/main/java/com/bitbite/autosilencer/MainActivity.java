package com.bitbite.autosilencer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static ArrayList<Rule> rules = new ArrayList<>();
    public static RuleListAdapter ruleListAdapter;
    public static Rule selectedRule;

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
            RuleManager ruleManager = new RuleManager();
            rules = ruleManager.getSavedRules(this.getApplicationContext());
        }

        ruleListAdapter = new RuleListAdapter(this, android.R.layout.simple_list_item_1, rules);

        final ListView listView = (ListView)findViewById(R.id.rules_list);
        listView.setAdapter(ruleListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = listView.getItemAtPosition(position);
                selectedRule = (Rule)o;
                Intent intent = new Intent(view.getContext(), rule_detail.class);
                startActivity(intent);
            }
        });

        Switch isAppActive = (Switch)findViewById(R.id.disableSwitch);
        isAppActive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(
                        getString(R.string.AS_preferences), getApplicationContext().MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                if (buttonView.isChecked()) {
                    Log.d("Main", "Changing to false");
                    editor.putString(getString(R.string.AS_isActive), "false");
                    editor.apply();
                }else{
                    Log.d("Main", "Changing to true");
                    editor.putString(getString(R.string.AS_isActive), "true");
                    editor.apply();
                }
                String active = sharedPref.getString(getString(R.string.AS_isActive),"true");
                Log.d("Main", "Is active? " + active);
                SilencerService.startActionCheckConnectivity(getApplicationContext(),"BLAH","COOL");
            }});

        startService(new Intent(this, StayAliveService.class));



//        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                final String action = intent.getAction();
//                if (action.equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)) {
//                    Log.d("NETWORKCHANGE", "network changed");
//                    SilencerService.startActionCheckConnectivity(context,"BLAH","COOL");
//                }
//            }
//        };
//
//        IntentFilter intentFilter = new IntentFilter();
//        //intentFilter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
//        intentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
//        registerReceiver(broadcastReceiver, intentFilter);

    }



}
