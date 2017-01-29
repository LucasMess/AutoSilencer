package com.bitbite.autosilencer;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

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
