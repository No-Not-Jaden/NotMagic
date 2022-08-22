package me.jadenp.notmagic;


import me.jadenp.notmagic.RevisedClasses.Items;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Crops;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.awt.Color;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Spells {
    private final Location pos1;
    private final Player p;
    private final Plugin plugin;
    private final String prefix;
    private int usedMana;
    private String selectedSpell;
    private final int mana;

    public Spells(Location p1, Location p2, Location p3, Location p4, Location p5, Player player, Plugin plug, int manaAmount, List<String> spellList, boolean dev) {
        this.plugin = plug;
        this.prefix = ChatColor.GRAY + "[" + ChatColor.of(new Color(26, 194, 232)) + "Not" + ChatColor.of(new Color(232, 26, 225)) + "Magic" + ChatColor.GRAY + "] " + ChatColor.DARK_GRAY + "» ";
        this.pos1 = p1;
        this.p = player;
        this.usedMana = 0;
        this.selectedSpell = "noChange";
        this.mana = manaAmount;

        Location eL = p.getEyeLocation();
        Vector v1 = eL.toVector().subtract(pos1.toVector()).normalize();
        Vector v2 = eL.toVector().subtract(p2.toVector()).normalize();
        Vector v3 = eL.toVector().subtract(p3.toVector()).normalize();
        Vector v4 = eL.toVector().subtract(p4.toVector()).normalize();
        Vector v5 = eL.toVector().subtract(p5.toVector()).normalize();
        Vector vb1_2 = pos1.toVector().subtract(p2.toVector());
        Vector vb2_3 = p2.toVector().subtract(p3.toVector());
        Vector vb3_4 = p3.toVector().subtract(p4.toVector());
        Vector vb4_5 = p4.toVector().subtract(p5.toVector());
        if (dev) {
            p.sendMessage("Yaw of v1, v2 " + getYawAngle(v1, v2));
            p.sendMessage("Yaw of v2, v3 " + getYawAngle(v2, v3));
            p.sendMessage("Yaw of v3, v4 " + getYawAngle(v3, v4));
            p.sendMessage("Yaw of v4, v5 " + getYawAngle(v4, v5));
            p.sendMessage("Vec between 1&2 " + vb1_2.normalize().getY());
            p.sendMessage("Vec between 2&3 " + vb2_3.normalize().getY());
            p.sendMessage("Vec between 3&4 " + vb3_4.normalize().getY());
            p.sendMessage("Vec between 4&5 " + vb4_5.normalize().getY());
            p.sendMessage(getRelativeVector(v1,v2));
            p.sendMessage(getRelativeVector(v2,v3));
            p.sendMessage(getRelativeVector(v3,v4));
            p.sendMessage(getRelativeVector(v4,v5));




     }


        // vertical: y values are close to 1 or -1
        // horizontal: y values close to 0
        // vb.normalize = how close it is to verticle
        // getYawAngle = distance away from eachother (not y)
        // getRelativeVector = return r if its going right, l if its going left




        if (vb1_2.normalize().getY() < -0.6 && vb2_3.normalize().getY() < -0.6 && vb3_4.normalize().getY() < -0.6 && vb4_5.normalize().getY() < -0.6 && getYawAngle(v1,v2) < 0.09 && getYawAngle(v2,v3) < 0.09 && getYawAngle(v3,v4) < 0.09 && getYawAngle(v4,v5) < 0.09){
            selectedSpell = "snipe1";
            p.sendMessage(prefix+ChatColor.AQUA + "Your main spell is now Snipe 1!");
        } else if (vb1_2.normalize().getY() > 0.6 && vb2_3.normalize().getY() > 0.6 && vb3_4.normalize().getY() > 0.6 && vb4_5.normalize().getY() > 0.6 && getYawAngle(v1,v2) < 0.09 && getYawAngle(v2,v3) < 0.09 && getYawAngle(v3,v4) < 0.09 && getYawAngle(v4,v5) < 0.09){
            selectedSpell = "burn1";
            p.sendMessage(prefix+ChatColor.AQUA + "Your main spell is now Burn 1!");
        } else if (getRelativeVector(v1, v2).equals("l") && getRelativeVector(v2, v3).equals("l") && getRelativeVector(v3, v4).equals("l") && getRelativeVector(v4, v5).equals("l") && vb1_2.normalize().getY() > -0.2 && vb2_3.normalize().getY() > -0.2 && vb3_4.normalize().getY() > -0.2 && vb4_5.normalize().getY() > -0.2 && vb1_2.normalize().getY() < 0.2 && vb2_3.normalize().getY() < 0.2 && vb3_4.normalize().getY() < 0.2 && vb4_5.normalize().getY() < 0.2){
            if (spellList.contains("heal")) {
                    p.sendMessage(prefix + ChatColor.AQUA + "You've cast the spell: " + ChatColor.of(new Color(219, 20, 110)) + "Heal" + ChatColor.AQUA + ".");
                    heal();
                } else {
                    p.sendMessage(prefix+ ChatColor.RED + "You do not have this spell unlocked!");
                }
            } else if (getRelativeVector(v1, v2).equals("r") && getRelativeVector(v2, v3).equals("r") && getRelativeVector(v3, v4).equals("r") && getRelativeVector(v4, v5).equals("r") && vb1_2.normalize().getY() > -0.2 && vb2_3.normalize().getY() > -0.2 && vb3_4.normalize().getY() > -0.2 && vb4_5.normalize().getY() > -0.2 && vb1_2.normalize().getY() < 0.2 && vb2_3.normalize().getY() < 0.2 && vb3_4.normalize().getY() < 0.2 && vb4_5.normalize().getY() < 0.2){
            if (spellList.contains("strength")) {
                    p.sendMessage(prefix + ChatColor.AQUA + "You've cast the spell: " + ChatColor.of(new Color(219, 20, 110)) + "Strength Boost" + ChatColor.AQUA + ".");
                    strength();
                } else {
                    p.sendMessage(prefix+ ChatColor.RED + "You do not have this spell unlocked!");
                }

            } else if (getRelativeVector(v1,v2).equals("r") && getRelativeVector(v2,v3).equals("l") && getRelativeVector(v3,v4).equals("r") && getRelativeVector(v4,v5).equals("l") && getYawAngle(v1,v2) < 1 && getYawAngle(v2,v3) < 1 && getYawAngle(v3,v4) < 1 && getYawAngle(v4,v5) < 1 && vb1_2.normalize().getY() > -0.2 && vb2_3.normalize().getY() > -0.2 && vb3_4.normalize().getY() > -0.2 && vb4_5.normalize().getY() > -0.2 && vb1_2.normalize().getY() < 0.2 && vb2_3.normalize().getY() < 0.2 && vb3_4.normalize().getY() < 0.2 && vb4_5.normalize().getY() < 0.2){
            if (spellList.contains("zap")) {
                    if (wandLevel(p) > 0) {
                        p.sendMessage(prefix + ChatColor.AQUA + "You're main spell is now Zap!");
                        selectedSpell = "zap";
                    } else {
                        p.sendMessage(prefix + ChatColor.DARK_GREEN + "Your wand isn't powerful enough to do this!");
                    }

                } else {
                    p.sendMessage(prefix+ ChatColor.RED + "You do not have this spell unlocked!");
                }
            } else if (getRelativeVector(v1,v2).equals("r") && getRelativeVector(v2,v3).equals("l") && getRelativeVector(v3,v4).equals("r") && getRelativeVector(v4,v5).equals("l") && getYawAngle(v1,v2) > 1 && getYawAngle(v2,v3) > 1 && getYawAngle(v3,v4) > 1 && getYawAngle(v4,v5) > 1 && vb1_2.normalize().getY() > -0.2 && vb2_3.normalize().getY() > -0.2 && vb3_4.normalize().getY() > -0.2 && vb4_5.normalize().getY() > -0.2 && vb1_2.normalize().getY() < 0.2 && vb2_3.normalize().getY() < 0.2 && vb3_4.normalize().getY() < 0.2 && vb4_5.normalize().getY() < 0.2){
            if (spellList.contains("burst")) {
                    if (wandLevel(p) > 0) {
                         p.sendMessage(prefix + ChatColor.AQUA + "You're main spell is now Burst!");
                        selectedSpell = "burst";
                    } else {
                        p.sendMessage(prefix + ChatColor.DARK_GREEN + "Your wand isn't powerful enough to do this!");
                    }
                } else {
                    p.sendMessage(prefix+ ChatColor.RED + "You do not have this spell unlocked!");
                }


        } else if (vb1_2.normalize().getY() < -0.6 && vb2_3.normalize().getY() > -0.2 && vb2_3.normalize().getY() < 0.2 && getYawAngle(v1,v2) > 0.4 && getYawAngle(v2,v3) > 0.4 && vb3_4.normalize().getY() > 0.6 && getYawAngle(v3,v4) > 0.4 && vb4_5.normalize().getY() < -0.3 && getYawAngle(v4,v5) > 0.3 && getRelativeVector(v1,v2).equals("l") && getRelativeVector(v2,v3).equals("r") && getRelativeVector(v3,v4).equals("l") && getRelativeVector(v4,v5).equals("r")){
            if (spellList.contains("kineticElectrocute")) {
                if (wandLevel(p) > 0) {
                    p.sendMessage(prefix + ChatColor.AQUA + "You've cast the spell: " + ChatColor.of(new Color(219, 20, 110)) + "Kinetic Electrocute" + ChatColor.AQUA + ".");
                    kineticElectrocute();
                } else {
                    p.sendMessage(prefix + ChatColor.DARK_GREEN + "Your wand isn't powerful enough to do this!");
                }
            } else {
                p.sendMessage(prefix+ ChatColor.RED + "You do not have this spell unlocked!");
            }
        } else if (vb1_2.normalize().getY() < -0.3 && getYawAngle(v1,v2) > 0.4 && getRelativeVector(v1,v2).equals("l") && vb2_3.normalize().getY() < -0.3 && getYawAngle(v2,v3) > 0.4 && getRelativeVector(v2,v3).equals("r") && vb3_4.normalize().getY() > 0.3 && getYawAngle(v3,v4) > 0.4 && getRelativeVector(v3,v4).equals("r") && vb4_5.normalize().getY() > -0.2 && vb4_5.normalize().getY() < 0.2 && getYawAngle(v4,v5) > 0.1 && getRelativeVector(v4,v5).equals("l")){
            if (spellList.contains("thunderCloud")) {
                if (wandLevel(p) > 0) {
                    p.sendMessage(prefix + ChatColor.AQUA + "You've cast the spell: " + ChatColor.of(new Color(219, 20, 110)) + "Thunder Cloud" + ChatColor.AQUA + ".");
                    thunderCloud();
                } else {
                    p.sendMessage(prefix + ChatColor.DARK_GREEN + "Your wand isn't powerful enough to do this!");
                }
            } else {
                p.sendMessage(prefix+ ChatColor.RED + "You do not have this spell unlocked!");
            }
        } else if (vb1_2.normalize().getY() > 0.6 && getYawAngle(v1,v2) < 0.1 && vb2_3.normalize().getY() < -0.3 && getYawAngle(v2,v3) > 0.3 && getRelativeVector(v2,v3).equals("l") && vb3_4.normalize().getY() < 0.2 && vb3_4.normalize().getY() > -0.2 && getYawAngle(v3,v4) > 0.4 && getRelativeVector(v3,v4).equals("r") && vb4_5.normalize().getY() < 0.2 && vb4_5.normalize().getY() > -0.2 && getYawAngle(v4,v5) > 0.4 && getRelativeVector(v4,v5).equals("r")){
            if (spellList.contains("darkSummoning")) {
                if (wandLevel(p) > 1) {
                    p.sendMessage(prefix + ChatColor.AQUA + "You've cast the spell: " + ChatColor.of(new Color(219, 20, 110)) + "Dark Summoning" + ChatColor.AQUA + ".");
                    darkSummoning();
                } else {
                    p.sendMessage(prefix + ChatColor.DARK_GREEN + "Your wand isn't powerful enough to do this!");
                }
            } else {
                p.sendMessage(prefix+ ChatColor.RED + "You do not have this spell unlocked!");
            }
        } else if (vb1_2.normalize().getY() < -0.6 && getYawAngle(v1,v2) < 0.1 && vb2_3.normalize().getY() < -0.6 && getYawAngle(v2,v3) < 0.1 && vb3_4.normalize().getY() > -0.2 && vb3_4.normalize().getY() < 0.2 && getYawAngle(v3,v4) > 0.2 && getRelativeVector(v3,v4).equals("r") && vb4_5.normalize().getY() > -0.2 && vb4_5.normalize().getY() < 0.2 && getYawAngle(v4,v5) > 0.2 && getRelativeVector(v4,v5).equals("r")){
            if (spellList.contains("snipe2")) {
                if (wandLevel(p) > 1) {
                    p.sendMessage(prefix + ChatColor.AQUA + "You're main spell is now Snipe 2!");
                    selectedSpell = "snipe2";
                } else {
                    p.sendMessage(prefix + ChatColor.DARK_GREEN + "Your wand isn't powerful enough to do this!");
                }
            } else {
                p.sendMessage(prefix+ ChatColor.RED + "You do not have this spell unlocked!");
            }
        } else if (vb1_2.normalize().getY() > -0.2 && vb1_2.normalize().getY() < 0.2 && getYawAngle(v1,v2) > 0.2 && getRelativeVector(v1,v2).equals("l") && vb2_3.normalize().getY() > 0.5 && getYawAngle(v2,v3) > 0.3 && vb3_4.normalize().getY() < -0.6 && getYawAngle(v3,v4) < 0.1 && vb4_5.normalize().getY() > 0.5 && getYawAngle(v4,v5) > 0.3){
            if (spellList.contains("darkPoisoning")) {
                if (wandLevel(p) > 1) {
                    p.sendMessage(prefix + ChatColor.AQUA + "You've cast the spell: " + ChatColor.of(new Color(219, 20, 110)) + "Dark Poisoning" + ChatColor.AQUA + ".");
                    darkPoisoning();
                } else {
                    p.sendMessage(prefix + ChatColor.DARK_GREEN + "Your wand isn't powerful enough to do this!");
                }
            } else {
                p.sendMessage(prefix+ ChatColor.RED + "You do not have this spell unlocked!");
            }
        } else if (vb1_2.normalize().getY() < -0.4 && getYawAngle(v1,v2) > 0.3 && getRelativeVector(v1,v2).equals("r") && vb2_3.normalize().getY() > 0.4 && getYawAngle(v2,v3) > 0.3 && getRelativeVector(v2,v3).equals("r") && vb3_4.normalize().getY() > 0.4 && getYawAngle(v3,v4) > 0.3 && getRelativeVector(v3,v4).equals("l") && vb4_5.normalize().getY() > 0.4 && getYawAngle(v4,v5) < 0.2){
            if (spellList.contains("locate")) {
                if (wandLevel(p) > 1) {
                    p.sendMessage(prefix + ChatColor.AQUA + "You're main spell is now Locate!");
                    selectedSpell = "locate";
                } else {
                    p.sendMessage(prefix + ChatColor.DARK_GREEN + "Your wand isn't powerful enough to do this!");
                }
            } else {
                p.sendMessage(prefix+ ChatColor.RED + "You do not have this spell unlocked!");
            }
        } else if (vb1_2.normalize().getY() > 0.6 && getYawAngle(v1,v2) < 0.2 && vb2_3.normalize().getY() > 0.6 && getYawAngle(v2,v3) < 0.2 && vb3_4.normalize().getY() > -0.2 && vb3_4.normalize().getY() < 0.2 && getYawAngle(v3,v4) > 0.3 && getRelativeVector(v3,v4).equals("l") && vb4_5.normalize().getY() > -0.2 && vb4_5.normalize().getY() < 0.2 && getYawAngle(v4,v5) > 0.3 && getRelativeVector(v4,v5).equals("l")){
            if (spellList.contains("burn2")) {
                if (wandLevel(p) > 2) {
                    p.sendMessage(prefix + ChatColor.AQUA + "You're main spell is now Burn 2!");
                    selectedSpell = "burn2";
                } else {
                    p.sendMessage(prefix + ChatColor.DARK_GREEN + "Your wand isn't powerful enough to do this!");
                }
            } else {
                p.sendMessage(prefix+ ChatColor.RED + "You do not have this spell unlocked!");
            }
        } else if (vb1_2.normalize().getY() > 0.6 && getYawAngle(v1,v2) < 0.2 && vb2_3.normalize().getY() < -0.6 && getYawAngle(v2,v3) < 0.2 && vb3_4.normalize().getY() > 0.6 && getYawAngle(v3,v4) < 0.2 && vb4_5.normalize().getY() < -0.6 && getYawAngle(v4,v5) < 0.2){
            if (spellList.contains("teleport")) {
                if (wandLevel(p) > 2) {
                    p.sendMessage(prefix + ChatColor.AQUA + "You're main spell is now Teleport!");
                    selectedSpell = "teleport";
                } else {
                    p.sendMessage(prefix + ChatColor.DARK_GREEN + "Your wand isn't powerful enough to do this!");
                }
            } else {
                p.sendMessage(prefix+ ChatColor.RED + "You do not have this spell unlocked!");
            }
        } else if (vb1_2.normalize().getY() < -0.6 && getYawAngle(v1,v2) < 0.2 && vb2_3.normalize().getY() > 0.6 && getYawAngle(v2,v3) < 0.2 && vb3_4.normalize().getY() < 0.2 && vb3_4.normalize().getY() > -0.2 && getYawAngle(v3,v4) > 0.3 && getRelativeVector(v3,v4).equals("l") && vb4_5.normalize().getY() < 0.2 && vb4_5.normalize().getY() > -0.2 && getYawAngle(v4,v5) > 0.3 && getRelativeVector(v4,v5).equals("r")){
            if (spellList.contains("shield")) {
                if (wandLevel(p) > 2) {
                    p.sendMessage(prefix + ChatColor.AQUA + "You've cast the spell: " + ChatColor.of(new Color(219, 20, 110)) + "Shield" + ChatColor.AQUA + ".");
                    shield();
                } else {
                    p.sendMessage(prefix + ChatColor.DARK_GREEN + "Your wand isn't powerful enough to do this!");
                }
            } else {
                p.sendMessage(prefix+ ChatColor.RED + "You do not have this spell unlocked!");
            }
        } else if (vb1_2.normalize().getY() < 0.2 && vb1_2.normalize().getY() > -0.2 && getYawAngle(v1,v2) > 0.3 && getRelativeVector(v1,v2).equals("r") && vb2_3.normalize().getY() > 0.6 && getYawAngle(v2,v3) < 0.2 && vb3_4.normalize().getY() > 0.3 && getYawAngle(v3,v4) > 0.3 && getRelativeVector(v3,v4).equals("l") && vb4_5.normalize().getY() < -0.4 && getYawAngle(v4,v5) > 0.3 && getRelativeVector(v4,v5).equals("l")){
            usedMana = -1;
        } else if (vb1_2.normalize().getY() < 0.2 && vb1_2.normalize().getY() > -0.2 && getYawAngle(v1,v2) > 0.3 && getRelativeVector(v1,v2).equals("l") && vb2_3.normalize().getY() > 0.4 && getYawAngle(v2,v3) > 0.3 && getRelativeVector(v2,v3).equals("r") && vb3_4.normalize().getY() < -0.4 && getYawAngle(v3,v4) > 0.3 && getRelativeVector(v3,v4).equals("r") && vb4_5.normalize().getY() > -0.2 && vb4_5.normalize().getY() < 0.2 && getYawAngle(v4,v5)>0.3 && getRelativeVector(v4,v5).equals("l")){
            if (spellList.contains("defence")) {
                if (wandLevel(p) > 2) {
                    p.sendMessage(prefix + ChatColor.AQUA + "You've cast the spell: " + ChatColor.of(new Color(219, 20, 110)) + "Defence" + ChatColor.AQUA + ".");
                    defence();
                } else {
                    p.sendMessage(prefix + ChatColor.DARK_GREEN + "Your wand isn't powerful enough to do this!");
                }
            } else {
                p.sendMessage(prefix+ ChatColor.RED + "You do not have this spell unlocked!");
            }
        } else if (vb1_2.normalize().getY() < -0.3 && getYawAngle(v1,v2) > 0.3 && getRelativeVector(v1,v2).equals("r") && vb2_3.normalize().getY() > 0.3 && getYawAngle(v2,v3) > 0.3 && getRelativeVector(v2,v3).equals("l") && vb3_4.normalize().getY() < -0.3 && getYawAngle(v3,v4) > 0.3 && getRelativeVector(v3,v4).equals("r") && getYawAngle(v4,v5) < 0.01){
            if (spellList.contains("darkCurse")) {
                if (wandLevel(p) > 3) {
                    p.sendMessage(prefix + ChatColor.AQUA + "You've cast the spell: " + ChatColor.of(new Color(219, 20, 110)) + "Dark Curse" + ChatColor.AQUA + ".");
                    darkCurse();
                } else {
                    p.sendMessage(prefix + ChatColor.DARK_GREEN + "Your wand isn't powerful enough to do this!");
                }
            } else {
                p.sendMessage(prefix+ ChatColor.RED + "You do not have this spell unlocked!");
            }
        } else if (vb1_2.normalize().getY() > 0.4 && getYawAngle(v1,v2) < 0.2 && vb2_3.normalize().getY() > -0.2 && vb2_3.normalize().getY() < 0.2 && getYawAngle(v2,v3) > 0.3 && getRelativeVector(v2,v3).equals("l") && vb3_4.normalize().getY() > -0.2 && vb3_4.normalize().getY() < 0.2 && getYawAngle(v3,v4) > 0.3 && getRelativeVector(v3,v4).equals("r") && vb4_5.normalize().getY() > -0.2 && vb4_5.normalize().getY() < 0.2 && getYawAngle(v4,v5) > 0.3 && getRelativeVector(v4,v5).equals("r")){
            if (spellList.contains("shadowWandering")) {
                if (wandLevel(p) > 3) {
                    p.sendMessage(prefix + ChatColor.AQUA + "You've cast the spell: " + ChatColor.of(new Color(219, 20, 110)) + "Shadow Wandering" + ChatColor.AQUA + ".");
                    shadowWandering();
                } else {
                    p.sendMessage(prefix + ChatColor.DARK_GREEN + "Your wand isn't powerful enough to do this!");
                }
            } else {
                p.sendMessage(prefix+ ChatColor.RED + "You do not have this spell unlocked!");
            }
        } else if (vb1_2.normalize().getY() < 0.2 && vb1_2.normalize().getY() > -0.2 && getYawAngle(v1,v2) > 0.3 && getRelativeVector(v1,v2).equals("l") && vb2_3.normalize().getY() < 0.2 && vb2_3.normalize().getY() > -0.2 && getYawAngle(v2,v3) > 0.3 && getRelativeVector(v2,v3).equals("r") && vb3_4.normalize().getY() < 0.2 && vb3_4.normalize().getY() > -0.2 && getYawAngle(v3,v4) > 0.3 && getRelativeVector(v3,v4).equals("r") && vb4_5.normalize().getY() < 0.2 && vb4_5.normalize().getY() > -0.2 && getYawAngle(v4,v5) > 0.3 && getRelativeVector(v4,v5).equals("l")){
            if (spellList.contains("absorb")) {
                if (wandLevel(p) > 3) {
                    p.sendMessage(prefix + ChatColor.AQUA + "You've cast the spell: " + ChatColor.of(new Color(219, 20, 110)) + "Absorb" + ChatColor.AQUA + ".");
                    absorb();
                } else {
                    p.sendMessage(prefix + ChatColor.DARK_GREEN + "Your wand isn't powerful enough to do this!");
                }
            } else {
                p.sendMessage(prefix+ ChatColor.RED + "You do not have this spell unlocked!");
            }
        } else if (vb1_2.normalize().getY() < -0.5 && getYawAngle(v1,v2) < 0.2 && vb2_3.normalize().getY() > 0.5 && getYawAngle(v2,v3) > 0.2 && getRelativeVector(v2,v3).equals("l") && vb3_4.normalize().getY() > -0.2 && vb3_4.normalize().getY() < 0.2 && getYawAngle(v3,v4) > 0.3 && getRelativeVector(v3,v4).equals("r") && vb4_5.normalize().getY() > -0.2 && vb4_5.normalize().getY() < 0.2 && getYawAngle(v4,v5) > 0.3 && getRelativeVector(v4,v5).equals("l")){
            if (spellList.contains("freeze")) {
                if (wandLevel(p) > 5) {
                    p.sendMessage(prefix + ChatColor.AQUA + "You've cast the spell: " + ChatColor.of(new Color(219, 20, 110)) + "Freeze" + ChatColor.AQUA + ".");
                    freeze();
                } else {
                    p.sendMessage(prefix + ChatColor.DARK_GREEN + "Your wand isn't powerful enough to do this!");
                }
            } else {
                p.sendMessage(prefix+ ChatColor.RED + "You do not have this spell unlocked!");
            }
            // 2     3
            //
            // 1/5   4
        } else if (vb1_2.normalize().getY() < -0.5 && getYawAngle(v1,v2) < 0.2 && vb2_3.normalize().getY() < 0.2 && vb2_3.normalize().getY() > -0.2 && getYawAngle(v2,v3) > 0.3 && getRelativeVector(v2,v3).equals("r") && vb3_4.normalize().getY() > 0.5 && getYawAngle(v3,v4) < 0.2 && vb4_5.normalize().getY() > -0.2 && vb4_5.normalize().getY() < 0.2 && getYawAngle(v4,v5) > 0.3 && getRelativeVector(v4,v5).equals("l")){
            if (spellList.contains("ironWallAttack")) {
                if (wandLevel(p) > 3) {
                    p.sendMessage(prefix + ChatColor.AQUA + "You're main spell is now Iron Wall Attack!");
                    selectedSpell = "ironWallAttack";
                } else {
                    p.sendMessage(prefix + ChatColor.DARK_GREEN + "Your wand isn't powerful enough to do this!");
                }
            } else {
                p.sendMessage(prefix+ ChatColor.RED + "You do not have this spell unlocked!");
            }
            //   1
            //  2/5
            // 3   4
        } else if(vb1_2.normalize().getY() > 0.5 && getYawAngle(v1,v2) < 0.2 && vb2_3.normalize().getY() > 0.2 && getYawAngle(v2,v3) > 0.3 && getRelativeVector(v2,v3).equals("l") && vb3_4.normalize().getY() > -0.2 && vb3_4.normalize().getY() < 0.2 && getYawAngle(v3,v4) > 0.3 && getRelativeVector(v3,v4).equals("r") && vb4_5.normalize().getY() < -0.2 && getYawAngle(v4,v5) > 0.3 && getRelativeVector(v4,v5).equals("l")){
            if (spellList.contains("lifeSteal")) {
                if (wandLevel(p) > 3) {
                    p.sendMessage(prefix + ChatColor.AQUA + "You're main spell is now Life Steal!");
                    selectedSpell = "lifeSteal";
                } else {
                    p.sendMessage(prefix + ChatColor.DARK_GREEN + "Your wand isn't powerful enough to do this!");
                }
            } else {
                p.sendMessage(prefix+ ChatColor.RED + "You do not have this spell unlocked!");
            }
            //   1
            // 2
            //   3
            // 4
            //   5
        } else if (vb1_2.normalize().getY() > 0.2 && getYawAngle(v1,v2) > 0.2 && getRelativeVector(v1,v2).equals("l") && vb2_3.normalize().getY() > 0.2 && getYawAngle(v2,v3) > 0.2 && getRelativeVector(v2,v3).equals("r") && vb3_4.normalize().getY() > 0.2 && getYawAngle(v3,v4) > 0.2 && getRelativeVector(v3,v4).equals("l") && vb4_5.normalize().getY() > 0.2 && getYawAngle(v4,v5) > 0.2 && getRelativeVector(v4,v5).equals("r")){
            if (spellList.contains("smite")) {
                if (wandLevel(p) > 4) {
                    p.sendMessage(prefix + ChatColor.AQUA + "You're main spell is now Smite!");
                    selectedSpell = "smite";
                } else {
                    p.sendMessage(prefix + ChatColor.DARK_GREEN + "Your wand isn't powerful enough to do this!");
                }
            } else {
                p.sendMessage(prefix+ ChatColor.RED + "You do not have this spell unlocked!");
            }
            //   5
            // 4
            //   3
            // 2
            //   1
        } else if (vb1_2.normalize().getY() < -0.2 && getYawAngle(v1,v2) > 0.2 && getRelativeVector(v1,v2).equals("l") && vb2_3.normalize().getY() < -0.2 && getYawAngle(v2,v3) > 0.2 && getRelativeVector(v2,v3).equals("r") && vb3_4.normalize().getY() < -0.2 && getYawAngle(v3,v4) > 0.2 && getRelativeVector(v3,v4).equals("l") && vb4_5.normalize().getY() < -0.2 && getYawAngle(v4,v5) > 0.2 && getRelativeVector(v4,v5).equals("r")){
            if (spellList.contains("fireball")) {
                if (wandLevel(p) > 4) {
                    p.sendMessage(prefix + ChatColor.AQUA + "You're main spell is now Fireball!");
                    selectedSpell = "fireball";
                } else {
                    p.sendMessage(prefix + ChatColor.DARK_GREEN + "Your wand isn't powerful enough to do this!");
                }
            } else {
                p.sendMessage(prefix+ ChatColor.RED + "You do not have this spell unlocked!");
            }
            // 1 3 2
            //   4
            //   5
        } else if (vb1_2.normalize().getY() < 0.2 && vb1_2.normalize().getY() > -0.2 && getYawAngle(v1,v2) > 0.3 && getRelativeVector(v1,v2).equals("r") && vb2_3.normalize().getY() < 0.2 && vb2_3.normalize().getY() > -0.2 && getYawAngle(v2,v3) > 0.3 && getRelativeVector(v2,v3).equals("l") && vb3_4.normalize().getY() > 0.5 && getYawAngle(v4,v5) < 0.2 && vb4_5.normalize().getY() > 0.4 && getYawAngle(v4,v5) < 0.2){
            if (spellList.contains("timeBomb")) {
                if (wandLevel(p) > 4) {
                    p.sendMessage(prefix + ChatColor.AQUA + "You've cast the spell: " + ChatColor.of(new Color(219, 20, 110)) + "Time Bomb" + ChatColor.AQUA + ".");
                    timeBomb();
                } else {
                    p.sendMessage(prefix + ChatColor.DARK_GREEN + "Your wand isn't powerful enough to do this!");
                }
            } else {
                p.sendMessage(prefix+ ChatColor.RED + "You do not have this spell unlocked!");
            }
            //     3
            // 1 2   4 5
            //
        } else if (vb1_2.normalize().getY() < 0.2 && vb1_2.normalize().getY() > -0.2 && getYawAngle(v1,v2) > 0.3 && getRelativeVector(v1,v2).equals("r") && vb2_3.normalize().getY() < -0.3 && getYawAngle(v2,v3) > 0.3 && getRelativeVector(v2,v3).equals("r") && vb3_4.normalize().getY() > 0.3 && getYawAngle(v3,v4) > 0.3 && getRelativeVector(v3,v4).equals("r") && vb4_5.normalize().getY() < 0.2 && vb4_5.normalize().getY() > -0.2 && getYawAngle(v4,v5) > 0.3 && getRelativeVector(v4,v5).equals("r")){
            if (spellList.contains("wither")) {
                if (wandLevel(p) > 4) {
                    p.sendMessage(prefix + ChatColor.AQUA + "You've cast the spell: " + ChatColor.of(new Color(219, 20, 110)) + "Wither" + ChatColor.AQUA + ".");
                    wither();
                } else {
                    p.sendMessage(prefix + ChatColor.DARK_GREEN + "Your wand isn't powerful enough to do this!");
                }
            } else {
                p.sendMessage(prefix+ ChatColor.RED + "You do not have this spell unlocked!");
            }
            //    3
            //   2 4
            //  1   5
        } else if(vb1_2.normalize().getY() < -0.3 && getYawAngle(v1,v2) > 0.2 && getRelativeVector(v1,v2).equals("r") && vb2_3.normalize().getY() < -0.3 && getYawAngle(v2,v3) > 0.2 && getRelativeVector(v2,v3).equals("r") && vb3_4.normalize().getY() > 0.3 && getYawAngle(v3,v4) > 0.2 && getRelativeVector(v3,v4).equals("r") && vb4_5.normalize().getY() > 0.3 && getYawAngle(v4,v5) > 0.2 && getRelativeVector(v4,v5).equals("r")){
            if (spellList.contains("drown")) {
                if (wandLevel(p) > 5) {
                    p.sendMessage(prefix + ChatColor.AQUA + "You've cast the spell: " + ChatColor.of(new Color(219, 20, 110)) + "Drown" + ChatColor.AQUA + ".");
                    drown();
                } else {
                    p.sendMessage(prefix + ChatColor.DARK_GREEN + "Your wand isn't powerful enough to do this!");
                }
            } else {
                p.sendMessage(prefix+ ChatColor.RED + "You do not have this spell unlocked!");
            }
            //
            // 2 3 4
            //  1/5
        } else if (vb1_2.normalize().getY() < -0.3 && getYawAngle(v1,v2) > 0.3 && getRelativeVector(v1,v2).equals("l") && vb2_3.normalize().getY() < 0.2 && vb2_3.normalize().getY() > -0.2 && getYawAngle(v2,v3) > 0.3 && getRelativeVector(v2,v3).equals("r") && vb3_4.normalize().getY() > -0.2 && vb3_4.normalize().getY() < 0.2 && getYawAngle(v3,v4) > 0.3 && getRelativeVector(v3,v4).equals("r") && vb4_5.normalize().getY() > 0.3 && getYawAngle(v4,v5) > 0.3 && getRelativeVector(v4,v5).equals("l")){
            if (spellList.contains("iceShards")) {
                if (wandLevel(p) > 5) {
                    p.sendMessage(prefix + ChatColor.AQUA + "You're main spell is now Ice Shards!");
                    selectedSpell = "iceShards";
                } else {
                    p.sendMessage(prefix + ChatColor.DARK_GREEN + "Your wand isn't powerful enough to do this!");
                }
            } else {
                p.sendMessage(prefix+ ChatColor.RED + "You do not have this spell unlocked!");
            }
        }
        else {
            p.sendMessage(prefix + ChatColor.DARK_GREEN + "Unknown Spell!");
        }
    }

    public static float getLookAtYaw(Vector motion) {
        double dx = motion.getX();
        double dz = motion.getZ();
        double yaw = 0;
        // Set yaw
        if (dx != 0) {
            // Set yaw start value based on dx
            if (dx < 0) {
                yaw = 1.5 * Math.PI;
            } else {
                yaw = 0.5 * Math.PI;
            }
            yaw -= Math.atan(dz / dx);
        } else if (dz < 0) {
            yaw = Math.PI;
        }
        return (float) (yaw * 180 / Math.PI);
    }
    // comparing where v2 is relative to v1
    public String getRelativeVector(Vector v1, Vector v2){
        double a1 = getLookAtYaw(v2);
        double a2 = a1 + 360;
        double a3 = a1 - 360;
        double v = getLookAtYaw(v1);
        double d1 = v - a1;
        double d2 = v - a2;
        double d3 = v - a3;
        if (Math.abs(d1) < Math.abs(d2) && Math.abs(d1) < Math.abs(d3)){
            if (d1 > 0){
                return "r";
            } else {
                return "l";
            }
        } else if (Math.abs(d2) < Math.abs(d1) && Math.abs(d2) < Math.abs(d3)){
            if (d2 > 0){
                return "r";
            } else {
                return "l";
            }
        } else if (Math.abs(d3) < Math.abs(d1) && Math.abs(d3) < Math.abs(d2)){
            if (d3 > 0){
                return "r";
            } else {
                return "l";
            }
        } else {
            return "i";
        }
    }
    public double getYawAngle(Vector v1, Vector v2){
        double x = v1.getX();
        double z = v1.getZ();
        double x2 = v2.getX();
        double z2 = v2.getZ();
        return Math.acos((x*x2 + z*z2) / (Math.sqrt(Math.pow(x, 2)+Math.pow(z, 2)) * Math.sqrt(Math.pow(x2, 2)+Math.pow(z2, 2))));
    }
    public int getUsedMana(){
        return usedMana;
    }
    public String getSpellSelected(){
        return  selectedSpell;
    }
    public int wandLevel(Player p){
        if (p.getInventory().getItemInMainHand().isSimilar(items.data("BasicWand"))){
            return 0;
        } else if (p.getInventory().getItemInMainHand().isSimilar(items.data("ProsaicWand"))){
            return 1;
        }  else if (p.getInventory().getItemInMainHand().isSimilar(items.data("ShadowWand"))){
            return 2;
        } else if (p.getInventory().getItemInMainHand().isSimilar(items.data("EnhancedWand"))){
            return 3;
        } else if (p.getInventory().getItemInMainHand().isSimilar(items.data("WardenWand"))){
            return 4;
        } else if (p.getInventory().getItemInMainHand().isSimilar(items.data("DestructionWand"))){
            return 5;
        }  else if (p.getInventory().getItemInMainHand().isSimilar(items.data("AquaWand"))){
            return 6;
        }
        return -1;
    }

    Items items = new Items();
    Random rand = new Random();

    public void heal(){
        if (mana > 24){
            usedMana=25;
            Location loc = p.getLocation();
            int r = 4;
            int x;
            int y = loc.getBlockY();
            int z;
            int w = 0;
            for (double i = 0.0; i < 360.0; i += 0.1) {
                double angle = i * Math.PI / 180;
                x = (int)(loc.getX() + r * Math.cos(angle));
                z = (int)(loc.getZ() + r * Math.sin(angle));
                if (w < 5) {
                    w++;
                } else {
                    w = 0;
                    Objects.requireNonNull(loc.getWorld()).spawnParticle(Particle.VILLAGER_HAPPY, x,y,z, 1, 0.2, 0, 0.2);
                }
            }
            double radius = 4D;
            List<Entity> near = Objects.requireNonNull(loc.getWorld()).getEntities();
            for (Entity e: near) {
                if (e.getLocation().distance(loc) <= radius) {
                    if (e instanceof LivingEntity) {
                        if (!(e instanceof Monster)) {
                            ((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 10, 0));
                            e.getWorld().spawnParticle(Particle.HEART, e.getLocation().getX(), e.getLocation().getY() + 2, e.getLocation().getZ(), 3, 0.2, 0.2, 0.2);
                        }
                    }
                }
            }
        } else {
            p.sendMessage(prefix + org.bukkit.ChatColor.RED + "You don't have enough mana!");
        }
    }

    public void strength(){
        if (mana > 24){
            usedMana = 25;
            Location loc = p.getLocation();
            int r = 4;
            int x;
            int y = loc.getBlockY();
            int z;
            int w = 0;
            for (double i = 0.0; i < 360.0; i += 0.1) {
                double angle = i * Math.PI / 180;
                x = (int)(loc.getX() + r * Math.cos(angle));
                z = (int)(loc.getZ() + r * Math.sin(angle));
                if (w < 5) {
                    w++;
                } else {
                    w = 0;
                    Objects.requireNonNull(loc.getWorld()).spawnParticle(Particle.DRIP_LAVA, x,y,z, 1, 0.2, 0, 0.2);
                }
            }
            double radius = 4D;
            List<Entity> near = Objects.requireNonNull(loc.getWorld()).getEntities();
            for (Entity e: near) {
                if (e.getLocation().distance(loc) <= radius) {
                    if (e instanceof LivingEntity) {
                        if (!(e instanceof Monster)) {
                            ((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 400, 0));
                            e.getWorld().spawnParticle(Particle.CRIT_MAGIC, e.getLocation().getX(), e.getLocation().getY() + 2, e.getLocation().getZ(), 3, 0.2, 0.2, 0.2);
                        }
                    }
                }
            }
        } else {
            p.sendMessage(prefix + org.bukkit.ChatColor.RED + "You don't have enough mana!");
        }
    }

    public void kineticElectrocute(){
        Player player = p;
        if (mana > 74){
            usedMana = 75;
            EnderCrystal crystal = p.getWorld().spawn(new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY() + 3, p.getLocation().getZ()), EnderCrystal.class);
            crystal.setShowingBottom(false);
            crystal.setInvulnerable(true);
            crystal.setGlowing(true);
            crystal.setMetadata("magic", new FixedMetadataValue(plugin, true));
            crystal.setMetadata("rremove", new FixedMetadataValue(plugin, true));
            new BukkitRunnable(){
                int timer = 0;
                @Override
                public void run() {
                    Location loc = crystal.getLocation();
                    int r = 6;
                    int x;
                    double y = loc.getBlockY() - 2.7;
                    int z;
                    int w = 0;
                    for (double i = 0.0; i < 360.0; i += 0.1) {
                        double angle = i * Math.PI / 180;
                        x = (int)(loc.getX() + r * Math.cos(angle));
                        z = (int)(loc.getZ() + r * Math.sin(angle));
                        if (w < 30) {
                            w++;
                        } else {
                            w = 0;
                            Objects.requireNonNull(loc.getWorld()).spawnParticle(Particle.FALLING_OBSIDIAN_TEAR, x,y,z, 1, 0.2, 0, 0.2);
                        }
                    }
                    double radius = 6D;
                    List<Entity> near = Objects.requireNonNull(crystal.getWorld()).getEntities();
                    boolean hit = false;
                    for (Entity e: near) {
                        if (e.getLocation().distance(crystal.getLocation()) <= radius) {
                            if (e instanceof LivingEntity) {
                                if (e != p) {

                                        crystal.setBeamTarget(new Location(e.getWorld(), e.getLocation().getX(), e.getLocation().getY() -1, e.getLocation().getZ()));
                                        hit = true;


                                                ((LivingEntity) e).damage(4, p);
                                                e.getWorld().spawnParticle(Particle.CRIT_MAGIC, e.getLocation(), 10, 1, 1, 1);


                                        break;

                                }
                            }
                        }
                    }
                    if (!hit){
                        crystal.setBeamTarget(null);
                    }
                    if (!player.isOnline()){
                        this.cancel();
                        crystal.remove();
                    }
                    if (timer >= 1200){
                        this.cancel();
                        crystal.remove();
                    }
                    timer+= 10;

                }
            }.runTaskTimer(plugin, 0, 10L);


        } else {
            p.sendMessage(prefix + org.bukkit.ChatColor.RED + "You don't have enough mana!");
        }
    }

    public void thunderCloud(){
        if (mana > 19){
            usedMana = 20;
            double radius = 6D;
            List<Entity> near = Objects.requireNonNull(p.getWorld()).getEntities();
            boolean hit = false;
            Player pHit = null;
            for (Entity e: near) {
                if (e.getLocation().distance(p.getLocation()) <= radius) {
                    if (e instanceof Player) {
                        if (e != p) {
                            hit = true;
                            pHit = (Player) e;
                            break;
                        }
                    }
                }
            }
            if (hit) {
                Player finalPHit = pHit;
                new BukkitRunnable() {
                    int timer = 0;
                    @Override
                    public void run() {
                        Location loc = new Location(finalPHit.getWorld(), finalPHit.getLocation().getX(), finalPHit.getLocation().getY() + 2.5, finalPHit.getLocation().getZ());
                        Objects.requireNonNull(loc.getWorld()).spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, loc, 0, 0, 0, 0);

                        for (int i = 0; i < 3; i++) {
                            Location location = new Location(loc.getWorld(), loc.getX() + (((double)rand.nextInt(11)/10)-0.5), loc.getY(), loc.getZ() + (((double)rand.nextInt(11)/10)-0.5));
                            loc.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, location, 0, 0, 0, 0);
                            loc.getWorld().spawnParticle(Particle.DRIP_WATER, location, 1);
                        }
                        if (timer == 2000){
                            this.cancel();
                        }
                        timer++;
                        if (Bukkit.getPlayer(finalPHit.getUniqueId()) == null){
                            this.cancel();
                        }
                    }
                }.runTaskTimer(plugin, 0, 10L);
            } else {
                p.sendMessage(prefix + ChatColor.DARK_GREEN + "There is nobody to cast the spell on!");
            }
        } else {
            p.sendMessage(prefix + org.bukkit.ChatColor.RED + "You don't have enough mana!");
        }
    }

    public void darkSummoning(){
        if (mana > 74) {
            usedMana = 75;
            WitherSkeleton w = p.getWorld().spawn(p.getLocation(), WitherSkeleton.class);
            w.setMetadata("magic", new FixedMetadataValue(plugin, true));
            w.setMetadata(p.getUniqueId().toString(), new FixedMetadataValue(plugin, true));
            w.setCustomName(ChatColor.DARK_GRAY + "Dark Wither Skeleton");
            w.setCustomNameVisible(true);
            w.setPersistent(true);
            w.setRemoveWhenFarAway(false);
            Objects.requireNonNull(w.getLocation().getWorld()).spawnParticle(Particle.CLOUD, w.getLocation(), 10, 3, 3, 3);

            EntityEquipment ee = w.getEquipment();
            assert ee != null;
            ee.setChestplate(new ItemStack(Material.NETHERITE_CHESTPLATE));
            ee.setHelmet(new ItemStack(Material.NETHERITE_HELMET));
            ee.setLeggings(new ItemStack(Material.NETHERITE_LEGGINGS));
            ee.setBoots(new ItemStack(Material.NETHERITE_BOOTS));
            ee.setItemInMainHand(new ItemStack(Material.NETHERITE_SWORD));
            ee.setBootsDropChance(0);
            ee.setChestplateDropChance(0);
            ee.setHelmetDropChance(0);
            ee.setLeggingsDropChance(0);
            ee.setItemInMainHandDropChance(0);
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                if (w.isValid()) {
                    w.getLocation().getWorld().spawnParticle(Particle.CLOUD, w.getLocation(), 10, 3, 3, 3);
                    w.remove();
                }
            }, 1000L);
        } else {
            p.sendMessage(prefix + org.bukkit.ChatColor.RED + "You don't have enough mana!");
        }
    }

    public void darkPoisoning(){
        Vector v = p.getEyeLocation().toVector().subtract(pos1.toVector()).normalize();
        Vector v1 = new Vector(0-v.getX(), 0-v.getY(), 0-v.getZ());

        if (mana > 19){
            usedMana = 20;
            Location front = p.getEyeLocation().add(v1.multiply(1.3));
            Location loc;
            for (int i = 0; i < 7; i++){
                loc = front.add(v1.multiply(1 + ((double) i / 10)));
                double radius = 4D;
                List<Entity> near = Objects.requireNonNull(p.getWorld()).getEntities();
                for (Entity e: near) {
                    if (e.getLocation().distance(loc) <= radius) {
                        if (e instanceof LivingEntity) {
                            if (e != p) {

                                    e.getWorld().spawn(e.getLocation(), EvokerFangs.class);
                                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> ((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.POISON, 600, 2)), 20L);


                            }
                        }
                    }
                }

            }
        } else {
            p.sendMessage(prefix + org.bukkit.ChatColor.RED + "You don't have enough mana!");
        }
    }

    public void shield(){
        if (mana > 69){
            usedMana = 70;
        Location loc = p.getLocation();
        Vector direction = p.getLocation().getDirection();
        Location front = loc.add(direction);
        ArmorStand stand1 = p.getLocation().getWorld().spawn(front, ArmorStand.class);
        Location b = new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ(), p.getLocation().getYaw() + 30, p.getLocation().getPitch());
        Vector direction2 = b.getDirection();
        Location front2 = b.add(direction2);
        ArmorStand stand2 = b.getWorld().spawn(front2, ArmorStand.class);
        Location a = new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ(), p.getLocation().getYaw() - 30, p.getLocation().getPitch());
        Vector direction3 = a.getDirection();
        Location front3 = a.add(direction3);
        ArmorStand stand3 = a.getWorld().spawn(front3, ArmorStand.class);
        Location c = new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ(), p.getLocation().getYaw() + 60, p.getLocation().getPitch());
        Vector direction4 = c.getDirection();
        Location front4 = c.add(direction4);
        ArmorStand stand4 = c.getWorld().spawn(front4, ArmorStand.class);
        Location d = new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ(), p.getLocation().getYaw() - 60, p.getLocation().getPitch());
        Vector direction5 = d.getDirection();
        Location front5 = d.add(direction5);
        ArmorStand stand5 = d.getWorld().spawn(front5, ArmorStand.class);
        stand1.setGravity(false);
        stand2.setGravity(false);
        stand3.setGravity(false);
        stand4.setGravity(false);
        stand5.setGravity(false);
        stand1.setCollidable(true);
        stand2.setCollidable(true);
        stand3.setCollidable(true);
        stand4.setCollidable(true);
        stand5.setCollidable(true);
        stand1.setVisible(false);
        stand2.setVisible(false);
        stand3.setVisible(false);
        stand4.setVisible(false);
        stand5.setVisible(false);
        stand1.setInvulnerable(true);
            stand2.setInvulnerable(true);
            stand3.setInvulnerable(true);
            stand4.setInvulnerable(true);
            stand5.setInvulnerable(true);

        Objects.requireNonNull(front.getWorld()).spawnParticle(Particle.CLOUD, front, 10);
        Objects.requireNonNull(front2.getWorld()).spawnParticle(Particle.CLOUD, front2, 10);
        Objects.requireNonNull(front3.getWorld()).spawnParticle(Particle.CLOUD, front3, 10);
        Objects.requireNonNull(front4.getWorld()).spawnParticle(Particle.CLOUD, front4, 10);
        Objects.requireNonNull(front5.getWorld()).spawnParticle(Particle.CLOUD, front5, 10);

        new BukkitRunnable() {
            int timer = 0;
            @Override
            public void run(){

                if (timer < 300){
                        Objects.requireNonNull(front.getWorld()).spawnParticle(Particle.BLOCK_MARKER, front.getX(), front.getY() + 1, front.getZ(), 10);
                        Objects.requireNonNull(front.getWorld()).spawnParticle(Particle.BLOCK_MARKER, front.getX(), front.getY() + 2, front.getZ(), 10);
                        Objects.requireNonNull(front.getWorld()).spawnParticle(Particle.BLOCK_MARKER, front2.getX(), front2.getY() + 1, front2.getZ(), 10);
                        Objects.requireNonNull(front.getWorld()).spawnParticle(Particle.BLOCK_MARKER, front2.getX(), front2.getY() + 2, front2.getZ(), 10);
                        Objects.requireNonNull(front.getWorld()).spawnParticle(Particle.BLOCK_MARKER, front3.getX(), front3.getY() + 1, front3.getZ(), 10);
                        Objects.requireNonNull(front.getWorld()).spawnParticle(Particle.BLOCK_MARKER, front3.getX(), front3.getY() + 2, front3.getZ(), 10);
                        Objects.requireNonNull(front.getWorld()).spawnParticle(Particle.BLOCK_MARKER, front4.getX(), front4.getY() + 1, front4.getZ(), 10);
                        Objects.requireNonNull(front.getWorld()).spawnParticle(Particle.BLOCK_MARKER, front4.getX(), front4.getY() + 2, front4.getZ(), 10);
                        Objects.requireNonNull(front.getWorld()).spawnParticle(Particle.BLOCK_MARKER, front5.getX(), front5.getY() + 1, front5.getZ(), 10);
                        Objects.requireNonNull(front.getWorld()).spawnParticle(Particle.BLOCK_MARKER, front5.getX(), front5.getY() + 2, front5.getZ(), 10);

                    timer+= 70;


                } else {
                    this.cancel();
                    Objects.requireNonNull(front.getWorld()).spawnParticle(Particle.CLOUD, front, 10);
                    Objects.requireNonNull(front2.getWorld()).spawnParticle(Particle.CLOUD, front2, 10);
                    Objects.requireNonNull(front3.getWorld()).spawnParticle(Particle.CLOUD, front3, 10);
                    Objects.requireNonNull(front4.getWorld()).spawnParticle(Particle.CLOUD, front4, 10);
                    Objects.requireNonNull(front5.getWorld()).spawnParticle(Particle.CLOUD, front5, 10);
                    stand1.remove();
                    stand2.remove();
                    stand3.remove();
                    stand4.remove();
                    stand5.remove();

                }
            }
        }.runTaskTimer(plugin, 0L, 70L);
        } else {
            p.sendMessage(prefix + org.bukkit.ChatColor.RED + "You don't have enough mana!");
        }
    }

    public void defence(){
        if (mana > 34){
            usedMana = 35;
            Location loc = p.getLocation();
            int r = 4;
            int x;
            int y = loc.getBlockY();
            int z;
            int w = 0;
            for (double i = 0.0; i < 360.0; i += 0.1) {
                double angle = i * Math.PI / 180;
                x = (int)(loc.getX() + r * Math.cos(angle));
                z = (int)(loc.getZ() + r * Math.sin(angle));
                if (w < 5) {
                    w++;
                } else {
                    w = 0;
                    Objects.requireNonNull(loc.getWorld()).spawnParticle(Particle.LANDING_OBSIDIAN_TEAR, x,y,z, 1, 0.2, 0, 0.2);
                }
            }
            double radius = 4D;
            List<Entity> near = Objects.requireNonNull(loc.getWorld()).getEntities();
            for (Entity e: near) {
                if (e.getLocation().distance(loc) <= radius) {
                    if (e instanceof LivingEntity) {
                        if (!(e instanceof Monster)) {
                            ((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 800, 0));
                            e.getWorld().spawnParticle(Particle.CRIT_MAGIC, e.getLocation().getX(), e.getLocation().getY() + 2, e.getLocation().getZ(), 3, 0.2, 0.2, 0.2);
                        }
                    }
                }
            }
        } else {
            p.sendMessage(prefix + org.bukkit.ChatColor.RED + "You don't have enough mana!");
        }
    }

    public void darkCurse(){
        if (mana > 99){
            usedMana = -2;
            Vector v = p.getEyeLocation().toVector().subtract(pos1.toVector()).normalize();
            Vector v1 = new Vector(0-v.getX(), 0-v.getY(), 0-v.getZ());
            Location front = p.getEyeLocation().add(v1.multiply(1.3));
            Location loc;
            for (int i = 0; i < 7; i++){
                loc = front.add(v1.multiply(1 + ((double) i / 10)));
                double radius = 4D;
                List<Entity> near = Objects.requireNonNull(p.getWorld()).getEntities();
                for (Entity e: near) {
                    if (e.getLocation().distance(loc) <= radius) {
                        if (e instanceof LivingEntity) {
                            if (e != p) {

                                    e.getWorld().spawnParticle(Particle.SUSPENDED_DEPTH, e.getLocation(),100,1,1,1);
                                    e.getWorld().playSound(e.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE,1,1);
                                    e.setMetadata("cursed", new FixedMetadataValue(plugin, p.getUniqueId().toString()));
                                    if (e instanceof Player)
                                    e.sendMessage(prefix + ChatColor.DARK_PURPLE + p.getName() + " has cursed you!");
                                    new BukkitRunnable(){
                                        @Override
                                        public void run() {
                                            if (e.hasMetadata("curse")){
                                                e.removeMetadata("curse", plugin);
                                            }
                                        }
                                    }.runTaskLater(plugin, 1000);
                                    break;

                            }
                        }
                    }
                }

            }
        } else {
            p.sendMessage(prefix + org.bukkit.ChatColor.RED + "You don't have enough mana!");
        }
    }

    public void shadowWandering(){
        if (mana > 75){
            usedMana = -3;
        } else {
            p.sendMessage(prefix + org.bukkit.ChatColor.RED + "You don't have enough mana!");
        }
    }

    public void absorb(){
        if (mana > 99){
            Particle.DustOptions dustOptions = new Particle.DustOptions(org.bukkit.Color.fromRGB(0, 0, 0), 10);
            Location location = p.getEyeLocation().add(p.getEyeLocation().getDirection().multiply(3));
            int slot = p.getInventory().getHeldItemSlot();
            usedMana = -2;
            new BukkitRunnable(){
                int timer = 0;
                @Override
                public void run() {
                    p.getInventory().setHeldItemSlot(slot);
                    if (p.isOnline()){
                        for (int i = 0; i < 10; i++)
                        p.spawnParticle(Particle.REDSTONE, new Location(location.getWorld(),location.getX() + (((double)rand.nextInt(8) / 10)-0.4),location.getY() + (((double)rand.nextInt(8) / 10)-0.4),location.getZ() + (((double)rand.nextInt(8) / 10)-0.4)), 1, dustOptions);
                        Location particle = new Location(location.getWorld(),location.getX() + (((double)rand.nextInt(20))-10),location.getY() + (((double)rand.nextInt(20))-10),location.getZ() + (((double)rand.nextInt(20))-10));
                        Vector vector = location.toVector().subtract(particle.toVector()).normalize();
                        p.spawnParticle(Particle.SQUID_INK, particle , 0, vector.getX(),vector.getY(),vector.getZ());
                        p.spawnParticle(Particle.CRIT_MAGIC,p.getLocation(),15,1,1,1);
                        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,15,20));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 15, 250));
                        double radius = 10D;
                        List<Entity> near = Objects.requireNonNull(p.getWorld()).getEntities();
                        for (Entity e: near) {
                            if (e.getLocation().distance(location) <= radius) {
                                if (e instanceof LivingEntity) {
                                    if (e != p) {

                                            if (e.getLocation().distance(location) <= 2) {
                                                e.setVelocity(new Vector(0,0,0));
                                                ((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,15,1));
                                                if (((LivingEntity) e).getHealth() < 30 && !(e instanceof Player) && !(e instanceof Cat) && !(e instanceof Wolf) && !(e instanceof Parrot) && !(e instanceof Horse) && !(e instanceof Fox) && !(e instanceof Llama) && !(e instanceof Donkey) && !(e instanceof Mule)){
                                                    p.playSound(e.getLocation(), Sound.ENTITY_STRIDER_EAT,1,1);
                                                    p.spawnParticle(Particle.SUSPENDED,e.getLocation(),20,1,1,1);
                                                    XPChangeEvent event = new XPChangeEvent((int) ((LivingEntity) e).getMaxHealth() * 3, p);
                                                    Bukkit.getServer().getPluginManager().callEvent(event);
                                                    e.remove();
                                                } else {
                                                    ((LivingEntity) e).damage(30,p);
                                                }

                                            } else {
                                                Vector v = location.toVector().subtract(e.getLocation().toVector()).normalize();
                                                e.setVelocity(e.getVelocity().add(v.multiply((10 - e.getLocation().distance(p.getLocation())) / 5)));
                                            }
                                        }

                                }
                            }
                        }
                        if (timer > 32){
                            this.cancel();
                        }
                        timer++;
                    } else {
                        this.cancel();
                    }

                }
            }.runTaskTimer(plugin,0,10L);
        } else {
            p.sendMessage(prefix + org.bukkit.ChatColor.RED + "You don't have enough mana!");
        }
    }

    public void freeze(){
        if (mana > 29){
            usedMana = 30;
            double radius = 6D;
            List<Entity> near = Objects.requireNonNull(p.getWorld()).getEntities();
            for (Entity e: near) {
                if (e.getLocation().distance(p.getLocation()) <= radius) {
                    if (e instanceof LivingEntity) {
                        if (e != p) {

                                ((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.SLOW,400,30));
                                ((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.JUMP,400,250));
                                e.getWorld().spawnParticle(Particle.SNOWBALL,e.getLocation(),20,1,1,1);
                                if (e instanceof Player)
                                e.sendMessage(prefix + ChatColor.BLUE + p.getName() + ChatColor.AQUA + " has froze you!.");

                        }
                    }
                }
            }
        } else {
            p.sendMessage(prefix + org.bukkit.ChatColor.RED + "You don't have enough mana!");
        }
    }

    public void timeBomb(){
        if (mana > 49){
            usedMana = 50;
            EnderCrystal crystal = p.getWorld().spawn(p.getLocation(),EnderCrystal.class);
            new BukkitRunnable(){
                int timer = 0;
                int speed = 0;
                @Override
                public void run() {
                    if (!(crystal.isValid())){
                        this.cancel();
                        return;
                    }
                    //10 10 | 10 5 | 10 2|
                    Particle.DustOptions dustOptions = null;
                    if (speed == 0){
                        if (timer % 4 == 0){
                            crystal.getWorld().playSound(crystal.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1,1);
                            dustOptions = new Particle.DustOptions(org.bukkit.Color.fromRGB(32, 240, 10), 1);
                            crystal.getWorld().spawnParticle(Particle.REDSTONE, crystal.getLocation().add(0.5, 3, 0.5), 1, dustOptions);
                        }
                    } else if (speed == 1){
                        if (timer % 3 == 0){
                            crystal.getWorld().playSound(crystal.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1,1);
                            dustOptions = new Particle.DustOptions(org.bukkit.Color.fromRGB(217, 240, 10), 1);
                            crystal.getWorld().spawnParticle(Particle.REDSTONE, crystal.getLocation().add(0.5, 3, 0.5), 1, dustOptions);
                        }
                    } else if (speed == 2){
                        if (timer % 2 == 0){
                            crystal.getWorld().playSound(crystal.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1,1);
                            dustOptions = new Particle.DustOptions(org.bukkit.Color.fromRGB(240, 148, 10), 1);
                            crystal.getWorld().spawnParticle(Particle.REDSTONE, crystal.getLocation().add(0.5, 3, 0.5), 1, dustOptions);
                        }
                    } else if (speed == 3){
                        crystal.getWorld().playSound(crystal.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1,1);
                        dustOptions = new Particle.DustOptions(org.bukkit.Color.fromRGB(237, 74, 52), 1);
                        crystal.getWorld().spawnParticle(Particle.REDSTONE, crystal.getLocation().add(0.5, 3, 0.5), 1, dustOptions);
                    }

                    timer++;
                    if (timer == 40){
                        speed++;
                    } else if (timer == 60){
                        speed++;
                    } else if (timer == 72){
                        speed++;
                    } else if (timer == 80){
                        for (int i = 0; i < 20; i++) {
                            TNTPrimed tnt = crystal.getWorld().spawn(crystal.getLocation(), TNTPrimed.class);
                            tnt.setFuseTicks(rand.nextInt(20));
                        }
                        crystal.remove();
                        this.cancel();
                    }
                }
            }.runTaskTimer(plugin, 0, 5);
        }
    }

    public void wither(){
        if (mana > 24) {
            usedMana = 25;
            int repeat = 1;
            int radius = 5;
            for (int x = radius; x > radius * -1; x--) {
                for (int y = radius; y > radius * -1; y--) {
                    for (int z = radius; z > radius * -1; z--) {
                        int finalX = x;
                        int finalY = y;
                        int finalZ = z;
                        final Location location = p.getLocation();
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                Location change = new Location(p.getWorld(), location.getBlockX() + finalX, location.getBlockY() + finalY, location.getBlockZ() + finalZ);
                                Block b = change.getBlock();
                                if (!(b.getType().isAir())) {
                                    if (b.getType() == Material.GRASS_BLOCK) {
                                        b.setType(Material.DIRT);
                                    } else if (b.getType() == Material.GRASS || b.getType() == Material.TALL_GRASS) {
                                        b.setType(Material.AIR);
                                    } else if (b.getBlockData() instanceof org.bukkit.block.data.type.Sapling){
                                        b.getWorld().dropItem(b.getLocation(), new ItemStack(b.getType(), 1));
                                        b.setType(Material.AIR);
                                    } else if (b.getBlockData() instanceof org.bukkit.block.data.type.CoralWallFan){
                                        b.getWorld().dropItem(b.getLocation(), new ItemStack(b.getType(), 1));
                                        b.setType(Material.AIR);
                                    }  else if (b.getBlockData() instanceof  Crops){
                                        b.getWorld().dropItem(b.getLocation(), new ItemStack(b.getType(), 1));
                                        b.setType(Material.AIR);
                                    } else if (b.getType() == Material.DANDELION || b.getType() == Material.POPPY || b.getType() == Material.POPPY || b.getType() == Material.BLUE_ORCHID || b.getType() == Material.ALLIUM || b.getType() == Material.AZURE_BLUET || b.getType() == Material.ORANGE_TULIP || b.getType() == Material.PINK_TULIP || b.getType() == Material.RED_TULIP || b.getType() == Material.WHITE_TULIP || b.getType() == Material.OXEYE_DAISY || b.getType() == Material.CORNFLOWER || b.getType() == Material.LILY_OF_THE_VALLEY){
                                        b.getWorld().dropItem(b.getLocation(), new ItemStack(b.getType(), 1));
                                        b.setType(Material.AIR);
                                    } else if (b.getType() == Material.LILAC || b.getType() == Material.ROSE_BUSH || b.getType() == Material.PEONY || b.getType() == Material.SUNFLOWER){
                                        b.setType(Material.AIR);
                                    }

                                }
                            }
                        }.runTaskLater(plugin, repeat);
                        repeat++;

                    }
                }
            }
        }
    }

    public void drown(){
        if (mana > 74) {
            usedMana = 75;
            LivingEntity target = null;
            Vector v = p.getEyeLocation().toVector().subtract(pos1.toVector()).normalize();
            Vector v1 = new Vector(0 - v.getX(), 0 - v.getY(), 0 - v.getZ());
            Location front = p.getEyeLocation().add(v1.multiply(1.3));
            Location loc;
            for (int i = 0; i < 7; i++) {
                loc = front.add(v1.multiply(1 + ((double) i / 10)));
                double radius = 4D;
                List<Entity> near = Objects.requireNonNull(p.getWorld()).getEntities();
                for (Entity e : near) {
                    if (e.getLocation().distance(loc) <= radius) {
                        if (e instanceof LivingEntity) {
                            if (e != p) {

                                    target = (LivingEntity) e;

                            }
                        }
                    }
                }
            }
            if (target == null){
                p.sendMessage(prefix + ChatColor.DARK_BLUE + "You didn't drown anyone!");
                return;
            }
            final Location start = p.getLocation();
            LivingEntity finalTarget = target;
            if (target instanceof Player){
                target.sendMessage(prefix + ChatColor.AQUA + p.getName() + " is drowning you!");
            }
            new BukkitRunnable(){
                @Override
                public void run() {
                    if (!p.isOnline()){
                        this.cancel();
                    }
                    if (p.getLocation().distance(start) > 1){
                        p.sendMessage(prefix + ChatColor.BLUE + "Spell canceled! You moved.");
                        this.cancel();
                        return;
                    }
                    if (finalTarget.isValid()){
                        finalTarget.damage(1, p);
                        finalTarget.getWorld().playSound(finalTarget.getLocation(), Sound.ENTITY_PLAYER_HURT_DROWN, 1, 1);
                    }
                    if (finalTarget.isDead()){
                        this.cancel();
                    }
                }
            }.runTaskTimer(plugin, 0, 20);
        }
    }

}

