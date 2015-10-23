package com.example.kevin.hue_control;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Network;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.ToggleButton;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "com.example.kevin.hue_control.MESSAGE";
    public final static String LIGHT_ID = "null";
    //public final static String HUE_USERNAME = "236382293a654a17372a0b6d38120b3b";
    public final static String HUE_USERNAME = "1df8f7461a539ea722e09eaa1935f3db";
    public final static String HUE_IP = "192.168.1.179";

    private ArrayList<String> HueKeys;
    private ArrayList<HueLight> HueLights;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.lights_seeker); // activity_main
        EditText editIP = (EditText)findViewById(R.id.editor_ip);
        editIP.setText(HUE_IP);
        HueKeys = new ArrayList<String>();
        HueLights = new ArrayList<HueLight>();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void seekLights(View view) {
        EditText editIP = (EditText) findViewById(R.id.editor_ip);
        String ip = editIP.getText().toString();

        getLights(ip);
    }

    public void sendMessage(View view) {
        EditText editIP = (EditText) findViewById(R.id.edit_ip);
        String ip = editIP.getText().toString();
        EditText editLight = (EditText) findViewById(R.id.edit_lightnumber);
        String light = editLight.getText().toString();
        ToggleButton toggle_onoff = (ToggleButton) findViewById(R.id.toggle_onoff);
        boolean onoff = toggle_onoff.isChecked();
        SeekBar seekSat = (SeekBar) findViewById(R.id.seekbar_sat);
        int sat = seekSat.getProgress();
        SeekBar seekBri = (SeekBar) findViewById(R.id.seekbar_bri);
        int bri = seekBri.getProgress();
        EditText editHue = (EditText) findViewById(R.id.edit_hue);
        int hue = Integer.parseInt(editHue.getText().toString());

        if(ip.length() > 8) { // add a proper IPv4 validation some day...
            //Intent intent = new Intent(this, DisplayMessageActivity.class);
            //intent.putExtra(EXTRA_MESSAGE, message);
            //startActivity(intent);

            JSONObject json = generateJSON(onoff, sat, bri, hue);
            volleyPUTRequest(ip, HUE_USERNAME, light, json);
        }
        else {
            showAlert(getString(R.string.alert_ip_title), getString(R.string.alert_ip_message));
        }
    }

    private void showAlert(String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
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

     private void getLights(String ip) {
         //String url = "http://httpbin.org/html";
         String url = "http://" + ip + "/api/" + HUE_USERNAME + "/lights";

         StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                 new Response.Listener<String>() {
                     @Override
                     public void onResponse(String response) {
                         System.out.println(response.toString());
                         parseJSON(response.toString());
                     }
                 }, new Response.ErrorListener() {
             @Override
             public void onErrorResponse(VolleyError error) {
                 System.out.println("Something went wrong!");
                 error.printStackTrace();
             }
         });
         Volley.newRequestQueue(this).add(stringRequest);
     }

    private void parseJSON(String json) {
        try {
            JSONObject jObject = new JSONObject(json);
            //jObject.getJSONObject("3").getString("name").toString()

            Iterator<String> iterator = jObject.keys();
            while(iterator.hasNext()) {
                String key = iterator.next();
                HueKeys.add(key);
            }
            fillListViewWithLights();
        }
        catch (JSONException jsonEx) {
            System.out.println(jsonEx.toString());
            showAlert("ERROR", "Unable to parse the JSON");
        }

        HueLight lamp = new HueLight(1, "Mario");
    }

    private void fillListViewWithLights() {
        ArrayAdapter<String> idListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, HueKeys);

        ListView idList = (ListView)findViewById(R.id.listviewMain);
        idList.setAdapter(idListAdapter);

        idList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                callEditor(Integer.parseInt(HueKeys.get(position)));
            }
        });
    }

    private void callEditor(int id) {
        Intent intent = new Intent(this, HSBeditorActivity.class);
        intent.putExtra(LIGHT_ID, "ID: " + id);
        startActivity(intent);
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
}
