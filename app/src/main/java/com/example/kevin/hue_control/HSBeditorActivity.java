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
    private View colorBox;
    private HueLight hueLight;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        //String message = intent.getStringExtra(MainActivity.LIGHT_ID);
        hueLight = (HueLight) intent.getParcelableExtra("incoming_light");

        setContentView(R.layout.activity_hsbeditor);
        seekHue = (SeekBar) findViewById(R.id.seekbar_hue);
        seekSat = (SeekBar) findViewById(R.id.seekbar_sat);
        seekBri = (SeekBar) findViewById(R.id.seekbar_bri);
        colorBox = findViewById(R.id.view_editor_colorbox);

        TextView lightID = (TextView)findViewById(R.id.lbl_editor_lightid);
        lightID.setText(hueLight.getName());
        seekHue.setProgress(hueLight.getHue360());
        seekSat.setProgress(hueLight.getSaturation());
        seekBri.setProgress(hueLight.getBrightness());
        colorBox.setBackgroundColor(hueLight.getColor());

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
        hueLight.updateHueLight(seekHue.getProgress(), seekSat.getProgress(), seekBri.getProgress());
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("returning_light", true);
        startActivity(intent);
    }
}
