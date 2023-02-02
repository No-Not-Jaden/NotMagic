package me.jadenp.notmagic.SpellWorkshop;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import me.jadenp.notmagic.NotMagic;
import me.jadenp.notmagic.SpellWorkshop.Essence;
import me.jadenp.notmagic.SpellWorkshop.WorkshopSpell;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class WorkshopSpellAdapter extends TypeAdapter<WorkshopSpell> {
    @Override
    public void write(JsonWriter out, WorkshopSpell value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        StringBuilder data = new StringBuilder(value.getPotential().toString() + "," + value.getPotentialAmount() + "," + value.getAreaEffect().toString() + "," + value.getAreaEffectAmount() + "," + value.getIntensity().toString() + "," + value.getIntensityAmount() + "," + value.getControl().toString() + "," + value.getControlAmount() + "," + value.getAccuracy() + "," + value.getName() + "," + value.getMpCost() + "," + value.isMainSpell() + "," + value.getMagicValue() + "," + value.getCastTime() + "," + value.getUuid() + "," + value.getCooldown() + "," + value.getRequiredLevel() + ",");
        for (String str : value.getSpellPattern()){
            data.append(data).append(str).append(":");
        }
        data = new StringBuilder(data.substring(0, data.length() - 1));
        out.value(data.toString());
    }

    @Override
    public WorkshopSpell read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        String[] data = in.nextString().split(",");
        String[] pattern = data[17].split(":");
        return new WorkshopSpell(Essence.valueOf(data[0]), Integer.parseInt(data[1]), Essence.valueOf(data[2]), Integer.parseInt(data[3]), Essence.valueOf(data[4]), Integer.parseInt(data[5]), Essence.valueOf(data[6]), Integer.parseInt(data[7]), Integer.parseInt(data[8]), data[9], Integer.parseInt(data[10]), Boolean.parseBoolean(data[11]), Integer.parseInt(data[12]), Integer.parseInt(data[13]), UUID.fromString(data[14]), Integer.parseInt(data[15]), Integer.parseInt(data[16]), new ArrayList<>(Arrays.asList(pattern)), NotMagic.getInstance());
    }
}
