package com.bitbite.autosilencer;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

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

//        wifiManager.startScan();
//        List<ScanResult> networkList = wifiManager.getScanResults();
//        List<String> netNames = new ArrayList<>();
//
//        for (int i = 0; i < networkList.size(); i++)
//        {
//            netNames.add(networkList.get(i).SSID);
//            Log.d("wifi" , networkList.get(i).SSID);
//        }
//        networksInRange = netNames.toArray(new String[netNames.size()]);
//        Spinner networkSpinner = (Spinner)findViewById(R.id.wifi_spinner);
//        ArrayAdapter<String> networkAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, networksInRange);
//        networkSpinner.setAdapter(networkAdapter);
    }

    public String getWifiName(){
        WifiManager wifiManager = (WifiManager)this.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String ssid = wifiInfo.getSSID();
        String withoutQuotes = "";
        for (int i = 1; i < ssid.length() - 1; i++)
        {
            withoutQuotes += ssid.charAt(i);
        }
        return withoutQuotes;
    }

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

    private void InitializeSaveButton(){

        Button saveButton = (Button)findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddNewRule();
                Intent intent = new Intent(view.getContext() ,MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void AddNewRule(){

        Spinner ringerSpinner = (Spinner)findViewById(R.id.ringer_spinner);
        Spinner actionSpinner = (Spinner)findViewById(R.id.action_spinner);
        EditText wifiName = (EditText)findViewById(R.id.wifi_input);

        Rule rule = new Rule(wifiName.getText().toString(), (String)ringerSpinner.getSelectedItem(), (String)actionSpinner.getSelectedItem());

        //TODO: Check for valid rule and existing rules.

        MainActivity.rules.add(rule);
        MainActivity.ruleListAdapter.notifyDataSetChanged();

        RuleManager ruleManager = new RuleManager();
        ruleManager.SaveRules(getApplicationContext(), MainActivity.rules);

    }



}
