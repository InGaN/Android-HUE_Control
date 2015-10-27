package com.example.kevin.hue_control;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kevin on 2015/10/27.
 */
public class LuxLight extends Light implements Parcelable {
    private int bri;

    public LuxLight(int id, String name, String type, boolean on, int brightness) {
        super(id, name, type, on);
        this.bri = brightness;
    }

    public LuxLight(Parcel in){
        super(in.readInt(), in.readString(), in.readString(), (in.readInt() > 0));
        this.bri = in.readInt();
    }

    public void setBrightness(int bri) {
        this.bri = bri;
    }
    public int getBrightness() {
        return bri;
    }

    @Override
    public int getColor() {
        float[] hsv = {0, 0, (float)(bri/2.55)/100};
        return (isOn()) ? Color.HSVToColor(hsv) : Color.BLACK;
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
        dest.writeInt(bri);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public LuxLight createFromParcel(Parcel in) {
            return new LuxLight(in);
        }

        public LuxLight[] newArray(int size) {
            return new LuxLight[size];
        }
    };
}
