package me.jadenp.notmagic.SpellWorkshop;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

public class SpellNames {
    public static String combineEssence(List<Essence> essenceList){
        int fire = 0;
        int earth = 0;
        int water = 0;
        int wind = 0;
        int electricity = 0;
        int ice = 0;
        int poison = 0;
        int living = 0;
        int spectral = 0;
        int barrier = 0;
        for (Essence essence : essenceList){
            if (essence.equals(Essence.FIRE)){
                fire++;
            } else if (essence.equals(Essence.EARTH)){
                earth++;
            } else if (essence.equals(Essence.WATER)){
                water++;
            } else if (essence.equals(Essence.WIND)){
                wind++;
            } else if (essence.equals(Essence.ELECTRICITY)){
                electricity++;
            } else if (essence.equals(Essence.ICE)){
                ice++;
            } else if (essence.equals(Essence.POISON)){
                poison++;
            } else if (essence.equals(Essence.LIVING)){
                living++;
            } else if (essence.equals(Essence.SPECTRAL)){
                spectral++;
            } else if (essence.equals(Essence.BARRIER)){
                barrier++;
            }
        }

        StringBuilder name = new StringBuilder();
        if (fire == 1){
            name.append(" Burning");
        } else if (fire == 2){
            name.append(" Flaming");
        } else if (fire == 3){
            name.append(" Blazing");
        } else if (fire == 4){
            name.append(" Inferno");
        }
        if (earth == 1){
            name.append(" Hard");
        } else if (earth == 2){
            name.append(" Rocky");
        } else if (earth == 3){
            name.append(" Cracking");
        } else if (earth == 4){
            name.append(" Avalanche");
        }
        if (water == 1){
            name.append(" Wet");
        } else if (water == 2){
            name.append(" Splashy");
        } else if (water == 3){
            name.append(" Tidal");
        } else if (water == 4){
            name.append(" Tsunami");
        }
        if (wind == 1){
            name.append(" Windy");
        } else if (wind == 2){
            name.append(" Sharp");
        } else if (wind == 3){
            name.append(" Flying");
        } else if (wind == 4){
            name.append(" Hurricane");
        }
        if (electricity == 1){
            name.append(" Electric");
        } else if (electricity == 2){
            name.append(" Jolting");
        } else if (electricity == 3){
            name.append(" Voltaic");
        } else if (electricity == 4){
            name.append(" Blackout");
        }
        if (ice == 1){
            name.append(" Icy");
        } else if (ice == 2){
            name.append(" Frosty");
        } else if (ice == 3){
            name.append(" Freezing");
        } else if (ice == 4){
            name.append(" Blizzard");
        }
        if (poison == 1){
            name.append(" Poisonous");
        } else if (poison == 2){
            name.append(" Toxic");
        } else if (poison == 3){
            name.append(" Noxious");
        } else if (poison == 4){
            name.append(" Death");
        }
        if (living == 1){
            name.append(" Living");
        } else if (living == 2){
            name.append(" Organic");
        } else if (living == 3){
            name.append(" Animatic");
        } else if (living == 4){
            name.append(" Life");
        }
        if (spectral == 1){
            name.append(" Spectral");
        } else if (spectral == 2){
            name.append(" Ghostly");
        } else if (spectral == 3){
            name.append(" Ghastly");
        } else if (spectral == 4){
            name.append(" Phantom");
        }
        if (barrier == 1){
            name.append(" BLocking");
        } else if (barrier == 2){
            name.append(" Halting");
        } else if (barrier == 3){
            name.append(" Immovable");
        } else if (barrier == 4){
            name.append(" Fortress");
        }
        if (name.length() > 0)
            if (name.toString().charAt(0) == ' '){
                return name.substring(1);
            }
        return name.toString();
    }

    public static String combinePassiveEssence(Essence control, int accuracy){
        int realAccuracy = control.getControlPower() + accuracy;
        switch (realAccuracy){
            case 0:
                return "";
            case 1:
                return "Uncontrollable";
            case 2:
                return "Fraudulent";
            case 3:
                return "Faulty";
            case 4:
                return "Loose";
            case 5:
                return "Erroneous";
            case 6:
                return "Imprecise";
            case 7:
                return "Flawed";
            case 8:
                return "Misleading";
            case 9:
                return "Inexact";
            case 10:
                return "Accurate";
            case 11:
                return "Precise";
            case 12:
                return "Errorless";
            case 13:
                return "Valid";
            case 14:
                return "Decisive";
            case 15:
                return "Veracious";
            case 16:
                return "Exact";
            case 17:
                return "Dead-On";
            case 18:
                return "Ultra-Precise";
            case 19:
                return "Sharp-Shop";
            case 20:
                return "Dead-Eye";
        }
        return "Game-Breaking";
    }

    public static String getMultiplierName(int amount){
        if (amount <= 4){
            return "";
        } else if (amount <= 8){
            return "Sturdy";
        } else if (amount <= 16){
            return "Mighty";
        } else if (amount <= 32){
            return "Strong";
        } else if (amount <= 64){
            return "Zealous";
        } else if (amount <= 128){
            return "Superior";
        } else if (amount <= 256){
            return "Mythic";
        }
        // would be higher if essence combined maybe
        return "Godly";
    }
}
