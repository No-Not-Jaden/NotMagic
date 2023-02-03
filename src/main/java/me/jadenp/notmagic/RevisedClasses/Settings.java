package me.jadenp.notmagic.RevisedClasses;

import me.jadenp.notmagic.NotMagic;

/**
 * To make adding config settings easier
 */
public class Settings {
    public static int magicMobLevelMax;

    public static void loadConfig(){
        NotMagic notMagic = NotMagic.getInstance();
        magicMobLevelMax = notMagic.getConfig().getInt("magic-mob-level-max");
    }

}
