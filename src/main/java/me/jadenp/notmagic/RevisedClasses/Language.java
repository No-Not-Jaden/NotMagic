package me.jadenp.notmagic.RevisedClasses;

import me.jadenp.notmagic.NotMagic;
import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.md_5.bungee.api.ChatColor.COLOR_CHAR;

public class Language {
    public static String prefix;
    public static String insufficientLevelReading;
    public static String learnSpell;
    public static String repeatSpell;
    public static String demonstrateSpell;
    public static String unrecognizedSpell;
    public static String displayWait;
    public static String noPlayerWorld;
    public static String insufficientLevelCasting;
    public static String levelUp;
    public static String noPermission;


    public static void setLanguage(){
        NotMagic notMagic = NotMagic.getInstance();
        addMissing(notMagic);
        prefix = color(notMagic.getConfig().getString("prefix"));
        insufficientLevelReading = color(notMagic.getConfig().getString("insufficient-level-reading"));
        learnSpell = color(notMagic.getConfig().getString("learn-spell"));
        repeatSpell = color(notMagic.getConfig().getString("repeat-spell"));
        demonstrateSpell = color(notMagic.getConfig().getString("demonstrate-spell"));
        unrecognizedSpell = color(notMagic.getConfig().getString("unrecognized-spell"));
        displayWait = color(notMagic.getConfig().getString("display-wait"));
        noPlayerWorld = color(notMagic.getConfig().getString("no-player-world"));
        insufficientLevelCasting = color(notMagic.getConfig().getString("insufficient-level-casting"));
        levelUp = color(notMagic.getConfig().getString("level-up"));
        noPermission = color(notMagic.getConfig().getString("no-permission"));
    }

    private static void addMissing(NotMagic notMagic){
        if (!notMagic.getConfig().isSet("prefix")){
            notMagic.getConfig().set("prefix", "&7[&#1ac2e8Not&#e81ae1Magic&7] &8» ");
        }
        if (!notMagic.getConfig().isSet("insufficient-level-reading")){
            notMagic.getConfig().set("insufficient-level-reading", "&9You are unable to decipher the symbols in this book. &7({level})");
        }
        if (!notMagic.getConfig().isSet("insufficient-level-casting")){
            notMagic.getConfig().set("insufficient-level-casting", "&9Your mind freezes when trying to preform this spell. &7({level})");
        }
        if (!notMagic.getConfig().isSet("learn-spell")){
            notMagic.getConfig().set("learn-spell", "&aYou learned the {spell} spell!");
        }
        if (!notMagic.getConfig().isSet("repeat-spell")){
            notMagic.getConfig().set("repeat-spell", "&9The symbols in this book seem familiar.");
        }
        if (!notMagic.getConfig().isSet("demonstrate-spell")){
            notMagic.getConfig().set("demonstrate-spell", "&eDemonstrating spell: &6{spell}&e.");
        }
        if (!notMagic.getConfig().isSet("no-permission")){
            notMagic.getConfig().set("no-permission", "&cYou do not have permission to use this command!");
        }
        if (!notMagic.getConfig().isSet("unrecognized-spell")){
            notMagic.getConfig().set("unrecognized-spell", "&9You don''t seem to recognize this spell.");
        }
        if (!notMagic.getConfig().isSet("display-wait")){
            notMagic.getConfig().set("display-wait", "&ePlease wait until your first spell is finished demonstrating.");
        }
        if (!notMagic.getConfig().isSet("no-player-world")){
            notMagic.getConfig().set("no-player-world", "&9You do not sense any players in this dimension.");
        }
        if (!notMagic.getConfig().isSet("level-up")){
            notMagic.getConfig().set("level-up", "&dYou have leveled up to level &5&l{level}&d!");
        }
        notMagic.saveConfig();
    }

    public static String color(String str){
        str = ChatColor.translateAlternateColorCodes('&', str);
        return translateHexColorCodes("&#","", str);
    }
    public static String translateHexColorCodes(String startTag, String endTag, String message)
    {
        final Pattern hexPattern = Pattern.compile(startTag + "([A-Fa-f0-9]{6})" + endTag);
        Matcher matcher = hexPattern.matcher(message);
        StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);
        while (matcher.find())
        {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, COLOR_CHAR + "x"
                    + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1)
                    + COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3)
                    + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5)
            );
        }
        return matcher.appendTail(buffer).toString();
    }


}
