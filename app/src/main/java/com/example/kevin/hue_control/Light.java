package com.example.kevin.hue_control;

/**
 * Created by kevin on 2015/10/27.
 */
public abstract class Light {
    protected int id;
    protected String name;
    protected String type;
    protected boolean on;
    protected boolean reachable;

    public Light(int id, String name, String type, boolean on, boolean reachable) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.on = on;
        this.reachable = reachable;
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
    public boolean isReachable() {
        return reachable;
    }
    public abstract int getColor();
}
