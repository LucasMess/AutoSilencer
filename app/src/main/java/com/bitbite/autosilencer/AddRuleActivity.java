package com.bitbite.autosilencer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.preference.DialogPreference;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * This activity is where the user can add another rule to the list.
 */
public class AddRuleActivity extends AppCompatActivity {

    private String[] actions = {"On WiFi Disconnect", "On WiFi Connect"};
    private String[] ringers = {"Silent", "Vibrate", "Sound"};
    private String[] networksInRange;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_rule);

        setTitle("Add a new rule");

        Spinner actionSpinner = (Spinner)findViewById(R.id.action_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, actions);

        actionSpinner.setAdapter(adapter);

        Spinner ringerSpinner = (Spinner)findViewById(R.id.ringer_spinner);
        ArrayAdapter<String> ringerAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, ringers);
        ringerSpinner.setAdapter(ringerAdapter);

        InitializeWifiInputBox();
        InitializeSaveButton();
    }

    /**
     * Fetches the SSID of the connected WiFi.
     * @return WiFi SSID without quotes.
     */
    public String getWifiName(){
        WifiManager wifiManager = (WifiManager)this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String ssid = wifiInfo.getSSID();
        String withoutQuotes = "";
        for (int i = 1; i < ssid.length() - 1; i++)
        {
            withoutQuotes += ssid.charAt(i);
        }
        return withoutQuotes;
    }

    /**
     * Sets the input box of WiFi name the the default value.
     */
    private void InitializeWifiInputBox(){
        TextView wifiInput = (TextView)findViewById(R.id.wifi_input);
        wifiInput.setText(getWifiName());

        wifiInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView warningText = (TextView) findViewById(R.id.warningText);
                String ssid = getWifiName();
                if (s.toString().equals(ssid)) {
                    warningText.setVisibility(View.VISIBLE);
                } else warningText.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * Sets up listeners for button click.
     */
    private void InitializeSaveButton(){

        Button saveButton = (Button)findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AddNewRule()) {
                    Intent intent = new Intent(view.getContext() ,MainActivity.class);
                    startActivity(intent);
                }
        }});
    }

    /**
     * Adds the rule to list and updates everything.
     */
    private boolean AddNewRule(){

        Spinner ringerSpinner = (Spinner)findViewById(R.id.ringer_spinner);
        Spinner actionSpinner = (Spinner)findViewById(R.id.action_spinner);
        EditText wifiName = (EditText)findViewById(R.id.wifi_input);

        // Prevent space at the end of the name.
        String name = wifiName.getText().toString();
        if (name.charAt(name.length() - 1) == ' '){
            name = name.substring(0,name.length() - 1);
        }

        Rule rule = new Rule(name, (String)ringerSpinner.getSelectedItem(), (String)actionSpinner.getSelectedItem());
        //TODO: Check for valid rule and existing rules.
        if (!checkIfValid(rule)){
            AlertDialog alertDialog = new AlertDialog.Builder(AddRuleActivity.this).setTitle("Invalid rule")
                    .setMessage("This rule interferes with an existing rule!")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    }).create();
            alertDialog.show();
            return false;
        }
        else {

            MainActivity.rules.add(rule);
            MainActivity.ruleListAdapter.notifyDataSetChanged();

            RuleManager ruleManager = new RuleManager();
            ruleManager.SaveRules(getApplicationContext(), MainActivity.rules);

            Toast.makeText(getApplicationContext(), "Rule has been saved", Toast.LENGTH_SHORT).show();
            return true;
        }

    }

    /**
     * Checks for logic errors that would be caused by adding a new rule.
     * @param rule The rule that will be added.
     * @return Returns true if the rule is valid and can be added.
     */
    private boolean checkIfValid(Rule rule){

        ArrayList<Rule> rules = MainActivity.rules;
        for (int i = 0; i < rules.size(); i++){
            if (rule.wifiName.equals(rules.get(i).wifiName)){
                if (rule.desiredTriggerAction.equals(rules.get(i).desiredTriggerAction))
                    return false;
            }
        }

        return true;
    }




}
