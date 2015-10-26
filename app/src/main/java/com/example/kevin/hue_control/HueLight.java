package com.example.kevin.hue_control;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kevin on 2015/10/22.
 */
public class HueLight implements Parcelable {
    private int id;
    private String name;
    private String type;
    private boolean on;
    private int bri;
    private int hue;
    private int sat;

    public HueLight(int id, String name, String type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }
    public HueLight(int id, String name, String type, boolean on, int hue, int saturation, int brightness) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.on = on;
        this.hue = hue;
        this.sat = saturation;
        this.bri = brightness;
    }

    public HueLight(Parcel in){
        this.id = in.readInt();
        this.name = in.readString();
        this.type = in.readString();
        this.on = (in.readInt() > 0);
        this.hue = in.readInt();
        this.sat = in.readInt();
        this.bri = in.readInt();
    }

    public int getColor() {
        float[] hsv = {(float)(hue/182.0), (float)sat, (float)bri};
        return (isOn()) ? Color.HSVToColor(hsv) : Color.BLACK;
    }

    public int getId() { return id; }
    public String getName() {
        return name;
    }
    public String getType() { return type; }
    public boolean isOn() {
        return on;
    }
    public int getBrightness() {
        return bri;
    }
    public int getHue() {
        return hue;
    }
    public int getHue360() { return hue/182; }
    public int getSaturation() { return sat; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(type);
        dest.writeInt(on ? 1 : 0);
        dest.writeInt(hue);
        dest.writeInt(sat);
        dest.writeInt(bri);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public HueLight createFromParcel(Parcel in) {
            return new HueLight(in);
        }

        public HueLight[] newArray(int size) {
            return new HueLight[size];
        }
    };
}
