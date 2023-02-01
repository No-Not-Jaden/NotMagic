package me.jadenp.notmagic.RevisedClasses;

import org.bukkit.Location;

import java.util.OptionalInt;

public class CustomBlocks {
    private Location location;
    private String type;
    private int[] data;

    public CustomBlocks(Location location, String type, int... data){

        this.location = location;
        this.type = type;
        this.data = data;
    }

    public Location getLocation() {
        return location;
    }

    public String getType() {
        return type;
    }

    public int[] getData(){
        return data;
    }

    public boolean hasData() {
        return data.length > 0;
    }

    public void setData(int[] data) {
        this.data = data;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setType(String type) {
        this.type = type;
    }
}
