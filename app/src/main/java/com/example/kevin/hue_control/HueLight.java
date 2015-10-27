package com.example.kevin.hue_control;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kevin on 2015/10/22.
 */
public class HueLight extends Light implements Parcelable {
    private int bri;
    private int hue;
    private int sat;
    private boolean colorLoop;

    public HueLight(int id, String name, String type, boolean on, int hue, int saturation, int brightness, boolean colorLoop) {
        super(id, name, type, on);
        this.hue = hue;
        this.sat = saturation;
        this.bri = brightness;
        this.colorLoop = colorLoop;
    }

    public HueLight(Parcel in){
        super(in.readInt(), in.readString(), in.readString(), (in.readInt() > 0));
        this.hue = in.readInt();
        this.sat = in.readInt();
        this.bri = in.readInt();
        this.colorLoop = (in.readInt() > 0);
    }

    @Override
    public int getColor() {
        float[] hsv = {(float)(hue/182.0), (float)sat/100, (float)bri/100};
        return (isOn()) ? Color.HSVToColor(hsv) : Color.BLACK;
    }

    public boolean isColorLoop() { return colorLoop; }
    public int getBrightness() { return bri; }
    public int getHue() { return hue; }
    public int getHue360() { return hue/182; }
    public int getSaturation() { return sat; }

    public void updateHueLight(int hue, int saturation, int brightness) {
        this.hue = hue;
        this.sat = saturation;
        this.bri = brightness;
    }
    public void setColorLoop(boolean colorLoop) {
        this.colorLoop = colorLoop;
    }
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
        dest.writeInt(colorLoop ? 1 : 0);
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
