package com.example.kevin.hue_control;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;

import java.util.List;


public class CustomListAdapter extends ArrayAdapter<Light> {
    private Activity activity;
    private List<Light> hueLights;

    public CustomListAdapter(Activity activity, List<Light> hueLights) {
        super(activity, R.layout.hue_listview_item, hueLights);
        this.activity = activity;
        this.hueLights = hueLights;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.hue_listview_item, null, true);

        TextView text1 = (TextView) rowView.findViewById(R.id.list_text1);
        TextView text2 = (TextView) rowView.findViewById(R.id.list_text2);
        ImageView img = (ImageView) rowView.findViewById(R.id.list_image);
        img.setBackgroundColor(hueLights.get(position).getColor());

        text1.setText(hueLights.get(position).getName());
        String lightType = hueLights.get(position).getType();
        text2.setText("ID: " + hueLights.get(position).getId() + " - " + lightType);

        switch (lightType) {
            case "LCT001":
            case "LWB004":
            default:
                img.setImageResource(R.drawable.list_icon_hue);
                break;
        }

        return rowView;
    }
    
}
