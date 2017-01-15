package com.bitbite.autosilencer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Lucas on 1/14/2017.
 */

public class RuleListAdapter extends ArrayAdapter<Rule> {

        public RuleListAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        public RuleListAdapter(Context context, int resource, ArrayList<Rule> items) {
            super(context, resource, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;

            if (v == null) {
                LayoutInflater vi;
                vi = LayoutInflater.from(getContext());
                v = vi.inflate(R.layout.item_rule, null);
            }

            Rule p = getItem(position);

            if (p != null) {
                TextView tt1 = (TextView) v.findViewById(R.id.ruleWifiName);
                TextView tt2 = (TextView) v.findViewById(R.id.ruleAction);
//                TextView tt3 = (TextView) v.findViewById(R.id.ruleRinger);

                if (tt1 != null){
                    tt1.setText(p.wifiName);
                }

                if (tt2 != null) {
                    StringBuilder text = new StringBuilder();
                    text.append("Set ringer to " + p.desiredRingerMode.toLowerCase() + " when ");
                    if (p.desiredTriggerAction.equals("On WiFi Disconnect")){
                        text.append("disconnected from this WiFi");
                    }else text.append("connected to this WiFi");

                    tt2.setText(text);
                }

//                if (tt2 != null) {
//                    tt2.setText(p.desiredTriggerAction.toString());
//                }
//
//                if (tt3 != null) {
//                    tt3.setText(p.desiredRingerMode.toString());
//                }
            }

            return v;
        }


}
