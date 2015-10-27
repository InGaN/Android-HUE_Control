package com.example.kevin.hue_control;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {
    public final static String LIGHT_ID = "null";
    //public final static String HUE_USERNAME = "236382293a654a17372a0b6d38120b3b";
    public final static String HUE_USERNAME = "1df8f7461a539ea722e09eaa1935f3db";
    public final static String HUE_IP = "192.168.1.179";

    private ArrayList<Light> Lights;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //fillLightsList();
        Lights = new ArrayList<>();

        setContentView(R.layout.lights_seeker); // activity_main

        Intent intent = getIntent();
        if(intent.getExtras() != null) {
            if (intent.getBooleanExtra("returning_light", true)) {
                fillListViewWithLights();
                showAlert("UPDATE", "updating with new value");
            }
        }

        EditText editIP = (EditText)findViewById(R.id.editor_ip);
        editIP.setText(HUE_IP);
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
        Lights.clear();

        getLights(ip);

        //fillListViewWithLights();
        //callEditor(7);
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
                 System.out.println("unable to locate Philips Bridge on this IP");
                 error.printStackTrace();
                 showAlert("ERROR", "unable to locate Philips Bridge on this IP");
             }
         });
         Volley.newRequestQueue(this).add(stringRequest);
     }

    private void parseJSON(String json) {
        try {
            JSONObject jObject = new JSONObject(json);

            Iterator<String> iterator = jObject.keys();
            while(iterator.hasNext()) {
                String key = iterator.next();

                if(jObject.getJSONObject(key).getString("modelid").equals("LCT001")) {
                    HueLight newLight = new HueLight(
                            Integer.parseInt(key),
                            jObject.getJSONObject(key).getString("name").toString(),
                            jObject.getJSONObject(key).getString("modelid").toString(),
                            jObject.getJSONObject(key).getJSONObject("state").getBoolean("on"),
                            jObject.getJSONObject(key).getJSONObject("state").getInt("hue"),
                            jObject.getJSONObject(key).getJSONObject("state").getInt("sat"),
                            jObject.getJSONObject(key).getJSONObject("state").getInt("bri")
                    );
                    Lights.add(newLight);
                }
                else if(jObject.getJSONObject(key).getString("modelid").equals("LWB004")) {
                    LuxLight newLight = new LuxLight(
                        Integer.parseInt(key),
                        jObject.getJSONObject(key).getString("name").toString(),
                        jObject.getJSONObject(key).getString("modelid").toString(),
                        jObject.getJSONObject(key).getJSONObject("state").getBoolean("on"),
                        jObject.getJSONObject(key).getJSONObject("state").getInt("bri")
                    );
                    Lights.add(newLight);
                }
            }
            fillListViewWithLights();
        }
        catch (JSONException jsonEx) {
            System.out.println(jsonEx.toString());
            showAlert("ERROR", "Unable to parse the JSON: " + jsonEx.getMessage());
        }
    }

    private void fillListViewWithLights() {
        //ArrayAdapter<String> idListAdapter = new ArrayAdapter<String>(this, R.layout.hue_listview_item, R.id.list_content, HueKeys);
        CustomListAdapter customListAdapter = new CustomListAdapter(this, Lights);

        ListView idList = (ListView)findViewById(R.id.listviewMain);
        idList.setAdapter(customListAdapter);

        idList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                callEditor(Lights.get(position));
            }
        });
    }

    private void callEditor(Light light) {
        if(light.getType().equals("LCT001")) {
            Intent intent = new Intent(this, HSBeditorActivity.class);
            intent.putExtra("incoming_light", (HueLight)light);
            startActivity(intent);
        }
        else if(light.getType().equals("LWB004")) {
            Intent intent = new Intent(this, HSBeditorActivity.class);
            intent.putExtra("incoming_light", (LuxLight)light);
            startActivity(intent);
        }

    }

    private void fillLightsList() {
        Lights = new ArrayList<>();
        Lights.add(new HueLight(3, "Mario", "hue", true, 1000, 50, 100));
        Lights.add(new HueLight(3, "Luigi", "hue", true, 20000, 100, 50));
        Lights.add(new HueLight(3, "Wario", "hue", true, 10000, 100, 100));
    }
}
