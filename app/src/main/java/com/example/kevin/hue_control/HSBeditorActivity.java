package com.example.kevin.hue_control;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

public class HSBeditorActivity extends AppCompatActivity {
    private SeekBar seekSat;
    private SeekBar seekHue;
    private SeekBar seekBri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.LIGHT_ID);

        setContentView(R.layout.activity_hsbeditor);
        seekSat = (SeekBar) findViewById(R.id.seekbar_sat);
        seekHue = (SeekBar) findViewById(R.id.seekbar_hue);
        seekBri = (SeekBar) findViewById(R.id.seekbar_bri);

        TextView lightID = (TextView)findViewById(R.id.lbl_editor_lightid);
        lightID.setText(message);

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

        seekSat.setOnSeekBarChangeListener(seekbarListeners);
        seekBri.setOnSeekBarChangeListener(seekbarListeners);
        seekHue.setOnSeekBarChangeListener(seekbarListeners);
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

    private void updateColorBlock() {
        float sat = (float)seekSat.getProgress() / 100;
        float bri = (float)seekBri.getProgress() / 100;
        float hue = seekHue.getProgress();

        TextView lightID = (TextView) findViewById(R.id.lbl_editor_lightid);
        View colorBox = (View) findViewById(R.id.view_editor_colorbox);

        float[] hsv = {hue, sat, bri};


        lightID.setText("#" + Integer.toHexString(Color.HSVToColor(hsv)));
        colorBox.setBackgroundColor(Color.HSVToColor(hsv));
    }
}
