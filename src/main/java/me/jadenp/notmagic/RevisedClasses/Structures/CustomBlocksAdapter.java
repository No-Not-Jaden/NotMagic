package me.jadenp.notmagic.RevisedClasses.Structures;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.IOException;
import java.util.UUID;

public class CustomBlocksAdapter extends TypeAdapter<CustomBlocks> {
    @Override
    public void write(JsonWriter out, CustomBlocks value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        StringBuilder data = new StringBuilder(value.getLocation().getWorld().getUID() + ":" + value.getLocation().getX() + ":" + value.getLocation().getY() + ":" + value.getLocation().getZ() + "," + value.getType());
        if (value.hasData()){
            data.append(",");
            for (int i : value.getData()){
                data.append(i).append(":");
            }
            data = new StringBuilder(data.substring(0, data.length() - 1));
        }
        out.value(data.toString());
    }

    @Override
    public CustomBlocks read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        String[] data = in.nextString().split(",");
        String[] lData = data[0].split(":");
        Location location = new Location(Bukkit.getWorld(UUID.fromString(lData[0])), Double.parseDouble(lData[1]), Double.parseDouble(lData[2]), Double.parseDouble(lData[3]));
        CustomBlocks customBlocks = new CustomBlocks(location, data[1]);
        if (data.length > 2){
            String[] bData = data[2].split(":");
            int[] iData = new int[bData.length];
            for (int i = 0; i < bData.length; i++) {
                iData[i] = Integer.parseInt(bData[i]);
            }
            customBlocks.setData(iData);
        }
        return customBlocks;
    }
}
