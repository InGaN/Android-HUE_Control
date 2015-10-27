package com.example.kevin.hue_control;

/**
 * Created by kevin on 2015/10/27.
 */
public abstract class Light {
    protected int id;
    protected String name;
    protected String type;
    protected boolean on;

    public Light(int id, String name, String type, boolean on) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.on = on;
    }

    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getType() {
        return type;
    }
    public boolean isOn() {
        return on;
    }

    public abstract int getColor();
}
