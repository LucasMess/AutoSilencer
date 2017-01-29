package com.bitbite.autosilencer;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Lucas on 1/28/2017.
 */

public class RuleManager {

    final private String FILE_NAME = "saved_rules";

    public void SaveRules(Context context, ArrayList<Rule> rules){
        // Convert the current rules into a text file.
        StringBuilder text = new StringBuilder();

        text.append("autosilencer_1\n");

        for (int i = 0; i < rules.size(); i++){
            text.append(rules.get(i).wifiName);
            text.append("\n");
            text.append(rules.get(i).desiredRingerMode);
            text.append("\n");
            text.append(rules.get(i).desiredTriggerAction);
            text.append("\n");
        }

        text.append("===END===");

        // Save file to device.
        try {
            FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            fos.write(text.toString().getBytes());
            fos.close();
        }
        catch (FileNotFoundException e){

        }
        catch (IOException e){

        }

    }

    // Looks for the file with the saved rules on the device's storage.
    public ArrayList<Rule> getSavedRules(Context context){

        Log.d("SavedRules", "Getting saved rules.");
        // Reads file.
        String text = "";
        try {
            FileInputStream fis = context.openFileInput(FILE_NAME);
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
        Log.d("SavedRules", "Assigned new rules.");
        return rules;
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
