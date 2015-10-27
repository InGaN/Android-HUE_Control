package com.example.kevin.hue_control;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class HSBeditorActivity extends AppCompatActivity {
    private SeekBar seekSat;
    private SeekBar seekHue;
    private SeekBar seekBri;
    private View colorBox;
    private Light light;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        //String message = intent.getStringExtra(MainActivity.LIGHT_ID);
        light = (Light) intent.getParcelableExtra("incoming_light");


        setContentView(R.layout.activity_hsbeditor);
        seekHue = (SeekBar) findViewById(R.id.seekbar_hue);
        seekSat = (SeekBar) findViewById(R.id.seekbar_sat);
        seekBri = (SeekBar) findViewById(R.id.seekbar_bri);
        colorBox = findViewById(R.id.view_editor_colorbox);
        TextView lightID = (TextView)findViewById(R.id.lbl_editor_lightid);
        ToggleButton toggleButton = (ToggleButton)findViewById(R.id.toggle_onoff);

        determineLightType();

        //lightID.setText(light.getName());
        toggleButton.setChecked(light.isOn());

        colorBox.setBackgroundColor(light.getColor());

        SeekBar.OnSeekBarChangeListener seekbarListeners = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateColorBlock();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                updateColorBlock();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                updateColorBlock();
            }
        };

        seekHue.setOnSeekBarChangeListener(seekbarListeners);
        seekSat.setOnSeekBarChangeListener(seekbarListeners);
        seekBri.setOnSeekBarChangeListener(seekbarListeners);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_hsbeditor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void determineLightType() {
        if(light.getType().equals("LCT001")) {
            HueLight hueLight = (HueLight) light;
            seekHue.setProgress(hueLight.getHue360());
            seekSat.setProgress((int)(hueLight.getSaturation()/2.55));
            seekBri.setProgress((int)(hueLight.getBrightness()/2.55));
        }
        else if (light.getType().equals("LWB004")) {
            LuxLight luxLight = (LuxLight) light;
            seekHue.setEnabled(false);
            seekSat.setEnabled(false);
            seekBri.setProgress((int) (luxLight.getBrightness()/2.55));
            TextView lightID = (TextView)findViewById(R.id.lbl_editor_lightid);
            lightID.setText(String.valueOf(luxLight.getBrightness()));
        }
    }

    private void updateColorBlock() {
        float hue = seekHue.getProgress();
        float sat = (float)seekSat.getProgress() / 100;
        float bri = (float)seekBri.getProgress() / 100;

        TextView lightID = (TextView) findViewById(R.id.lbl_editor_lightid);

        float[] hsv = {hue, sat, bri};
        lightID.setText("#" + Integer.toHexString(Color.HSVToColor(hsv)));
        colorBox.setBackgroundColor(Color.HSVToColor(hsv));


    }

    public void updateHueLight(View view) {
        if(light.getType().equals("LCT001")) {
            HueLight hueLight = (HueLight) light;
            hueLight.updateHueLight(seekHue.getProgress(), seekSat.getProgress(), seekBri.getProgress());
        }
        else if (light.getType().equals("LWB004")) {
            LuxLight luxLight = (LuxLight) light;
            luxLight.setBrightness(seekBri.getProgress());
        }

        sendMessage();
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.putExtra("returning_light", true);
//        startActivity(intent);
    }

    public void sendMessage() {
        ToggleButton toggle_onoff = (ToggleButton) findViewById(R.id.toggle_onoff);
        boolean onoff = toggle_onoff.isChecked();
        SeekBar seekSat = (SeekBar) findViewById(R.id.seekbar_sat);
        int sat = (int)(seekSat.getProgress() * 2.55);
        SeekBar seekBri = (SeekBar) findViewById(R.id.seekbar_bri);
        int bri = (int)(seekBri.getProgress() * 2.55);
        SeekBar seekHue = (SeekBar) findViewById(R.id.seekbar_hue);
        int hue = seekHue.getProgress() * 182;

        JSONObject json = generateJSON(onoff, sat, bri, hue);
        volleyPUTRequest(MainActivity.HUE_IP, MainActivity.HUE_USERNAME, String.valueOf(light.getId()), json);

    }

    private JSONObject generateJSON(boolean on, int sat, int bri, int hue) {
        final JSONObject json = new JSONObject();
        try {
            json.put("on", on);
            json.put("sat", sat);
            json.put("bri", bri);
            json.put("hue", hue);
        } catch (JSONException e) {
            // exception body
        }
        return json;
    }

    private void volleyPUTRequest(String ip, String username, String light, JSONObject json) {
        //String url = "http://httpbin.org/html";
        String url = "http://"+ ip +"/api/" + username + "/lights/" + light + "/state";

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.PUT, url, json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            VolleyLog.v("Response:%n %s", response.toString(4));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });
        Volley.newRequestQueue(this).add(jsonRequest);
    }

    private void showAlert(String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(HSBeditorActivity.this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
}
