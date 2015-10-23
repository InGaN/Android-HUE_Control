package com.example.kevin.hue_control;

/**
 * Created by kevin on 2015/10/22.
 */
public class HueLight {
    private int id;
    private String name;
    private boolean on;
    private int bri;
    private int hue;
    private int sat;

    public HueLight(int id, String name) {
        this.id = id;
        this.name = name;
    }
    public HueLight(int id, String name, boolean on, int brightness, int hue, int saturation) {
        this.id = id;
        this.name = name;
        this.on = on;
        this.bri = brightness;
        this.hue = hue;
        this.sat = saturation;
    }

    public int getId() {
        return id;
    }
    public String getString() {
        return name;
    }
    public boolean isOn() {
        return on;
    }
    public int getBrightness() {
        return bri;
    }
    public int getHue() {
        return hue;
    }
    public int getSaturation() {
        return sat;
    }
}
