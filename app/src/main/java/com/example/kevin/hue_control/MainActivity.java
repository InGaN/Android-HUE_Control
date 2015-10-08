package com.example.kevin.hue_control;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Network;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

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

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "com.example.kevin.hue_control.MESSAGE";
    public final static String HUE_USERNAME = "236382293a654a17372a0b6d38120b3b";
    public final static String HUE_IP = "192.168.1.179";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        EditText editIP = (EditText)findViewById(R.id.edit_ip);
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

    public void sendMessage(View view) {
        EditText editIP = (EditText) findViewById(R.id.edit_ip);
        String ip = editIP.getText().toString();
        EditText editLight = (EditText) findViewById(R.id.edit_lightnumber);
        String light = editLight.getText().toString();

        if(ip.length() > 8) { // add a proper IPv4 validation some day...
            //Intent intent = new Intent(this, DisplayMessageActivity.class);
            //intent.putExtra(EXTRA_MESSAGE, message);
            //startActivity(intent);
            volleyPUTRequest(ip, HUE_USERNAME, light);
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

     private void volleyGETRequest() {
         //String url = "http://httpbin.org/html";
         String url = "http://192.168.1.179/api/" + HUE_USERNAME + "/lights";

         StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                 new Response.Listener<String>() {
                     @Override
                     public void onResponse(String response) {
                         System.out.println(response.toString());
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

    private void volleyPUTRequest(String ip, String username, String light) {
        //String url = "http://httpbin.org/html";
        String url = "http://"+ ip +"/api/" + username + "/lights/" + light + "/state";

        final JSONObject json = new JSONObject();
        try {
            json.put("on", true);
        } catch (JSONException e) {
            // exception body
        }

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
}
