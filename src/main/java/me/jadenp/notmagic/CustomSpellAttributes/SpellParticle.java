package me.jadenp.notmagic.CustomSpellAttributes;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.checkerframework.checker.units.qual.A;

import org.bukkit.Location;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpellParticle {
    private boolean debug = true;

    private final String action;

    public SpellParticle(String action){
        this.action = action;
    }

    public void spawnParticles(Player player, List<SpellTarget> targets){
        if (action.contains(" spawn:")){
            String spawnString = action.substring(action.indexOf(" spawn:") + 7, action.indexOf(" spawn:") + 7 + action.substring(action.indexOf(" spawn:") + 7).indexOf(' '));
            if (debug)
            Bukkit.broadcastMessage(spawnString);
            String beforeNum = substringBeforeNum(spawnString);
            List<Location> spawnLocations = new ArrayList<>();
            if (beforeNum.equals("player")){
                spawnLocations.add(getLocationChange(spawnString.substring(6), player.getLocation(), true));
            } else if (beforeNum.equals("playerEye")){
                spawnLocations.add(getLocationChange(spawnString.substring(9), player.getEyeLocation(), false));
            } else if (beforeNum.equals("target")){
                for (SpellTarget target : targets){

                }
            } else if (beforeNum.equals("targetEye")){
                for (SpellTarget target : targets){

                }
            } else if (beforeNum.length() == 0){

            }
        } else {
            // cant spawn particle
            if (debug){
                Bukkit.broadcastMessage("no spawn location");
            }
        }
    }

    private String substringBeforeNum(String str){
        List<String> numbers = new ArrayList<>(Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "."));

        int firstNumIndex = str.length();
        for (String num : numbers){
            if (str.contains(num)){
                if (str.indexOf(num) < firstNumIndex){
                    firstNumIndex = str.indexOf(num);
                }
            }
        }

        return str.substring(0,firstNumIndex);
    }

    private String substringAfterNum(String str){
        List<Character> numbers = new ArrayList<>(Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.'));
        boolean foundNum = false;
        for (int i = 0; i < str.length(); i++){
            if (!foundNum){
                if (numbers.contains(str.charAt(i))){
                    foundNum = true;
                }
            } else {
                if (!numbers.contains(str.charAt(i))){
                    return str.substring(i);
                }
            }
        }
        return str;
    }

    private String getFirstNumber(String str){
        List<Character> numbers = new ArrayList<>(Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.'));
        StringBuilder number = new StringBuilder();
        boolean foundNum = false;
        for (int i = 0; i < str.length(); i++){
            if (foundNum){
                if (numbers.contains(str.charAt(i))){
                    number.append(str.charAt(i));
                } else {
                    return number.toString();
                }
            } else {
                if (numbers.contains(str.charAt(i))){
                    foundNum = true;
                    number.append(str.charAt(i));
                }
            }
        }
        return number.toString();
    }

    private Location getLocationChange(String str, Location original, boolean fixY){
        Vector direction = original.getDirection();
        if (fixY){
            direction.setY(0);
        }
        direction.normalize();
        Vector change = new Vector(0,0,0);
        while (str.length() > 0) {
            String after = substringBeforeNum(substringAfterNum(str));
            String num = getFirstNumber(str);
            double power;
            try {
                power = Double.parseDouble(num);
            } catch (NumberFormatException ignored) {
                str = str.substring(num.length() + after.length());
                continue;
            }
            switch (after) {
                case "front":
                    change.add(direction.multiply(power));
                    break;
                case "behind":
                    change.add(direction.rotateAroundY(180.0).multiply(power));
                    break;
                case "left":
                    change.add(direction.rotateAroundY(90.0).multiply(power));
                    break;
                case "right":
                    change.add(direction.rotateAroundY(270.0).multiply(power));
                    break;
                case "up":
                    change.add(new Vector(0, 1, 0).multiply(power));
                    break;
                case "down":
                    change.add(new Vector(0, -1, 0).multiply(power));
                    break;
            }
            str = str.substring(num.length() + after.length());
        }
        if (debug){
            Bukkit.broadcastMessage(change.toString());
        }
        return new Location(original.getWorld(), original.getX() + change.getX(), original.getY() + change.getY(), original.getZ() + change.getZ());
    }
}
