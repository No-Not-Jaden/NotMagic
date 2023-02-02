package me.jadenp.notmagic.RevisedClasses;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

public class PlayerDataAdapter extends TypeAdapter<PlayerData> {
    @Override
    public void write(JsonWriter out, PlayerData value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        StringBuilder data = new StringBuilder(value.getUuid() + "," + value.getPlayerName() + "," + value.getLevel() + "," + value.getXp() + "," + value.getMpMax() + "," + value.getMpRegen() + ",");
        for (String spell : value.getSpellsUnlocked()){
            data.append(spell).append(":");
        }
        data = new StringBuilder(data.substring(0, data.length() - 1));
        out.value(data.toString());
    }

    @Override
    public PlayerData read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        String data = in.nextString();
        String[] split = data.split(",");
        String[] spellsUnlocked = split[6].split(":");
        return new PlayerData(UUID.fromString(split[0]), split[1], Integer.parseInt(split[2]), Integer.parseInt(split[3]), Integer.parseInt(split[4]), Double.parseDouble(split[5]), new ArrayList<>(Arrays.asList(spellsUnlocked)));
    }
}
