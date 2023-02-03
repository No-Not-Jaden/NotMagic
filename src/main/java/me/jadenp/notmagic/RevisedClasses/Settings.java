package me.jadenp.notmagic.RevisedClasses;

import me.jadenp.notmagic.NotMagic;

/**
 * To make adding config settings easier
 */
public class Settings {
    public static int magicMobLevelMax;
    public static boolean debug = false; // not a config option and can only be enabled in-game for admins

    public static void loadConfig(){
        NotMagic notMagic = NotMagic.getInstance();
        magicMobLevelMax = notMagic.getConfig().getInt("magic-mob-level-max");
    }

}
