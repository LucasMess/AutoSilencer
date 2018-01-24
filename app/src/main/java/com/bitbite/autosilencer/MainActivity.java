package com.bitbite.autosilencer;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static ArrayList<Rule> rules = new ArrayList<>();
    public static RuleListAdapter ruleListAdapter;
    public static Rule selectedRule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestDoNotDisturbPermission();

        setTitle("Rules");

        // Set the switch to "ON" if the rules are disabled.
        Switch disableSwitch = findViewById(R.id.disableSwitch);
        disableSwitch.setChecked(!IsApplicationEnabled(this));

        SetListeners();
        PopulateRuleList();

        if (IsApplicationEnabled(this)) {
            StartServices(this);
        }

    }

    /**
     * If the user is running API 24 or above, they need to manually approve Do Not Disturb permissions.
     */
    public void requestDoNotDisturbPermission(){

        // Check if app needs to ask for permissions.
        if (!SilencerService.hasDoNotDisturbPermission(getApplicationContext())){
            // Show an alert to the user first, telling them to grant Do Not Disturb permissions.
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setMessage("This app needs permission to change your \"Do Not Disturb\" status in order to silence your phone.\n Don't worry, the app will not override your \"Do Not Disturb\" settings.");
            alert.setTitle("Grant Permission");
            alert.setCancelable(false);
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                    startActivity(intent);
                }

            });
            alert.create().show();
        }
        else{
            // Remove the notification of insufficient permissions if it exists.
            int notificiationId = 10;
            NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancel(notificiationId);
        }

    }

    /**
     * Sets the on click listeners for the views in this activity.
     */
    private void SetListeners(){
        // Floating action button at the bottom right takes the user to the add rule activity.
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_rule);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), AddRuleActivity.class);
                startActivity(intent);
            }
        });

        // Clicking on an item in the rule list shows the details of the rule.
        final ListView listView = findViewById(R.id.rules_list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = listView.getItemAtPosition(position);
                selectedRule = (Rule)o;
                Intent intent = new Intent(view.getContext(), RuleDetailsActivity.class);
                startActivity(intent);
            }
        });

        // If the switch is enabled, then the app should not change ringer settings.
        Switch isAppActive = (Switch)findViewById(R.id.disableSwitch);
        isAppActive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Get the preferences of the app.
                SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("AutoSilencerPreferences", getApplicationContext().MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();

                // Change setting depending on checked status.
                if (buttonView.isChecked()) {
                    editor.putString("rulesDisabled", "true");
                    editor.apply();

                    KillServices(getApplicationContext());
                    Toast.makeText(getApplicationContext(), "Silencer service has been stopped.", Toast.LENGTH_SHORT).show();

                }else{
                    editor.putString("rulesDisabled", "false");
                    editor.apply();

                    StartServices(getApplicationContext());
                    Toast.makeText(getApplicationContext(), "Silencer service has been started.", Toast.LENGTH_SHORT).show();
                }
            }});
    }

    /**
     * Looks for rules in memory and populates the list view with them.
     */
    private void PopulateRuleList(){
        // If rules are empty, try to see if they are saved in memory.
        if (rules.isEmpty()){
            RuleManager ruleManager = new RuleManager();
            rules = ruleManager.getSavedRules(this.getApplicationContext());
        }

        // Binds the adapter to the list view.
        ruleListAdapter = new RuleListAdapter(this, android.R.layout.simple_list_item_1, rules);
        ListView listView = findViewById(R.id.rules_list);
        listView.setAdapter(ruleListAdapter);
    }

    /**
     * Returns true if the user requested the app not to function.
     * @return
     */
    public static boolean IsApplicationEnabled(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences("AutoSilencerPreferences", context.MODE_PRIVATE);
        String active = sharedPref.getString("rulesDisabled", "false");
        return !Boolean.valueOf(active);
    }

    private void StartServices(Context context){
        SilencerService.startActionCheckConnectivity(getApplicationContext());
        startService(new Intent(this, StayAliveService.class));
    }

    private void KillServices(Context context){
        stopService(new Intent(this, StayAliveService.class));
        stopService(new Intent(this, SilencerService.class));
    }



}
