package me.jadenp.notmagic.RevisedClasses;

import me.jadenp.notmagic.NotMagic;
import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.md_5.bungee.api.ChatColor.COLOR_CHAR;

public class Language {
    private static String prefix;
    private static String insufficientLevelReading;
    private static String learnSpell;
    private static String repeatSpell;
    private static String demonstrateSpell;
    private static String unrecognizedSpell;
    private static String displayWait;
    private static String noPlayerWorld;
    private static String insufficientLevelCasting;
    private static String levelUp;


    public static void setLanguage(){
        NotMagic notMagic = NotMagic.getInstance();
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

    public static String prefix() {
        return prefix;
    }

    public static String insufficientLevelReading() {
        return insufficientLevelReading;
    }

    public static String learnSpell() {
        return learnSpell;
    }

    public static String repeatSpell() {
        return repeatSpell;
    }

    public static String demonstrateSpell() {
        return demonstrateSpell;
    }

    public static String unrecognizedSpell() {
        return unrecognizedSpell;
    }

    public static String displayWait() {
        return displayWait;
    }

    public static String noPlayerWorld() {
        return noPlayerWorld;
    }

    public static String insufficientLevelCasting() {
        return insufficientLevelCasting;
    }

    public static String levelUp() {
        return levelUp;
    }
}
