package com.bitbite.autosilencer;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class rule_detail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rule_detail);
        setTitle("Details");

        TextView wifiName = (TextView)findViewById(R.id.wifiNameLabel);
        TextView desc = (TextView)findViewById(R.id.descriptionLabel);

        wifiName.setText(MainActivity.selectedRule.wifiName);

        StringBuilder text = new StringBuilder();
        text.append("Set ringer to " + MainActivity.selectedRule.desiredRingerMode.toLowerCase() + " when ");
        if (MainActivity.selectedRule.desiredTriggerAction.equals("On WiFi Disconnect")){
            text.append("disconnected from this WiFi");
        }else text.append("connected to this WiFi");

        desc.setText(text);


        Button deleteButton = (Button)findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                MainActivity.rules.remove(MainActivity.selectedRule);
                                MainActivity.ruleListAdapter.notifyDataSetChanged();
                                SaveRules();
                                Dialog d = (Dialog)dialog;
                                Intent intent = new Intent(d.getContext() ,MainActivity.class);
                                startActivity(intent);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setMessage("Are you sure you want to delete this rule?")
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

            }
        });
    }

    public void SaveRules(){
        // Convert the current rules into a text file.
        StringBuilder text = new StringBuilder();

        text.append("autosilencer_1\n");

        for (int i = 0; i < MainActivity.rules.size(); i++){
            text.append(MainActivity.rules.get(i).wifiName);
            text.append("\n");
            text.append(MainActivity.rules.get(i).desiredRingerMode);
            text.append("\n");
            text.append(MainActivity.rules.get(i).desiredTriggerAction);
            text.append("\n");
        }

        text.append("===END===");

        // Save file to device.
        try {
            FileOutputStream fos = openFileOutput(AddRuleActivity.FILE_NAME, Context.MODE_PRIVATE);
            fos.write(text.toString().getBytes());
            fos.close();
        }
        catch (FileNotFoundException e){

        }
        catch (IOException e){

        }

    }
}
