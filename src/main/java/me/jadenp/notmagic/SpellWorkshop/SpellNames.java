package me.jadenp.notmagic.SpellWorkshop;

import java.util.List;

public class SpellNames {
    public static String combineEssence(List<Essence> essenceList){
        int fire = 0;
        int earth = 0;
        for (Essence essence : essenceList){
            if (essence.equals(Essence.FIRE)){
                fire++;
            } else if (essence.equals(Essence.EARTH)){
                earth++;
            }
        }

        StringBuilder name = new StringBuilder();
        if (fire == 1){
            name.append(" Fire");
        } else if (fire == 2){
            name.append(" Flame");
        } else if (fire == 3){
            name.append(" Blaze");
        } else if (fire == 4){
            name.append(" Inferno");
        }
        if (earth == 1){
            name.append(" Earth");
        } else if (earth == 2){
            name.append(" Rock");
        } else if (earth == 3){
            name.append(" Boulder");
        } else if (earth == 4){
            name.append(" Avalanche");
        }
        if (name.substring(0, 1).equals(" ")){
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
