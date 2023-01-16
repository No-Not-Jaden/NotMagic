package me.jadenp.notmagic.RevisedClasses;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Spell {

    private String name;
    private int mpCost;
    // in ticks
    private int castTime;
    private int cooldown;

    private int requiredLevel;

    private List<String> spellPattern;
    private List<Particle.DustOptions> colors = new ArrayList<>();

    private ItemStack spellBook;

    private Plugin plugin;
    private SpellIndex spellIndex;

    private boolean mainSpell;

    public Spell(String name, int mpCost, int castTime, int cooldown, int requiredLevel, List<String> spellPattern, ItemStack spellBook, Plugin plugin, boolean mainSpell){
        this.name = name;
        this.mpCost = mpCost;
        this.castTime = castTime;
        this.cooldown = cooldown;
        this.spellPattern = spellPattern;
        this.spellBook = spellBook;
        this.requiredLevel = requiredLevel;
        this.plugin = plugin;
        this.mainSpell = mainSpell;
        float size = 0.5F;
        colors.add(new Particle.DustOptions(Color.fromRGB(66, 242, 245), size));
        colors.add(new Particle.DustOptions(Color.fromRGB(48, 191, 242), size));
        colors.add(new Particle.DustOptions(Color.fromRGB(48, 116, 242), size));
        colors.add(new Particle.DustOptions(Color.fromRGB(58, 48, 242), size));
        colors.add(new Particle.DustOptions(Color.fromRGB(113, 48, 242), size));
        colors.add(new Particle.DustOptions(Color.fromRGB(181, 48, 242), size));
        colors.add(new Particle.DustOptions(Color.fromRGB(242, 48, 229), size));
        colors.add(new Particle.DustOptions(Color.fromRGB(242, 48, 135), size));
        colors.add(new Particle.DustOptions(Color.fromRGB(242, 48, 51), size));
    }

    protected void setName(String name){
        this.name = name;
    }

    protected void setCastTime(int castTime) {
        this.castTime = castTime;
    }

    protected void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    protected void setMpCost(int mpCost) {
        this.mpCost = mpCost;
    }

    protected void setSpellBook(ItemStack spellBook) {
        this.spellBook = spellBook;
    }

    protected void setSpellPattern(List<String> spellPattern) {
        this.spellPattern = spellPattern;
    }

    protected void setRequiredLevel(int requiredLevel) {
        this.requiredLevel = requiredLevel;
    }

    public boolean isMainSpell() {
        return mainSpell;
    }

    public ItemStack getSpellBook() {
        return spellBook;
    }

    public int getCastTime() {
        return castTime;
    }

    public int getCooldown() {
        return cooldown;
    }

    public int getMpCost() {
        return mpCost;
    }

    public String getName() {
        return name;
    }

    public int getRequiredLevel() {
        return requiredLevel;
    }

    public List<String> getSpellPattern() {
        return spellPattern;
    }

    public void displayRealSpell(Player p){
        UUID uuid = p.getUniqueId();
        double pointDistance = 0.25;
        double distanceAway = 1.0;
        Location startPoint = p.getEyeLocation().add(p.getEyeLocation().getDirection().setY(0).normalize().multiply(distanceAway));
        p.spawnParticle(Particle.REDSTONE, startPoint, 1,colors.get(0));
        final Location reference1 = p.getEyeLocation();
        List<Location> pastPoints = new ArrayList<>();
        pastPoints.add(new Location(startPoint.getWorld(), startPoint.getX(), startPoint.getY(), startPoint.getZ()));
        new BukkitRunnable(){
            // start at 1 to skip start point that was displayed above ^
            float spellPhase = 1;
            Location lastPoint = startPoint;
            Location reference = new Location(reference1.getWorld(), reference1.getX(), reference1.getY(), reference1.getZ());
            // reference to last point
            Vector refToLPoint = new Vector(lastPoint.toVector().getX() - reference.toVector().getX(), lastPoint.toVector().getY() - reference.toVector().getY(), lastPoint.toVector().getZ() - reference.toVector().getZ());

            @Override
            public void run() {
                if (spellPhase < spellPattern.size()){
                    if ((spellPhase * 10) % 10 == 0) {
                        // whole number spell phase
                        String direction = spellPattern.get((int) spellPhase);
                        Location nextPoint = lastPoint;
                        reference = new Location(reference1.getWorld(), reference1.getX(), reference1.getY(), reference1.getZ());
                        refToLPoint = new Vector(lastPoint.toVector().getX() - reference.toVector().getX(), lastPoint.toVector().getY() - reference.toVector().getY(), lastPoint.toVector().getZ() - reference.toVector().getZ());
                        Bukkit.broadcastMessage(direction);
                        switch (direction) {
                            case "Left":
                                //         from reference to an orthogonal vector of the refToLPoint that is directed left and added to the last point
                                nextPoint = reference.add(lastPoint.add(new Vector(refToLPoint.getZ(), 0, refToLPoint.getX() * -1).normalize().multiply(pointDistance)).toVector().subtract(reference.toVector()).normalize().multiply(distanceAway));
                                break;
                            case "Right":
                                // only thing different is that the orthogonal vector has the negative sign switched to face right
                                nextPoint = reference.add(lastPoint.add(new Vector(refToLPoint.getZ() * -1, 0, refToLPoint.getX()).normalize().multiply(pointDistance)).toVector().subtract(reference.toVector()).normalize().multiply(distanceAway));
                                break;
                            case "Up":
                                // if this causes problems im uninstalling java
                                nextPoint = lastPoint.add(0,pointDistance,0);
                                break;
                            case "Down":
                                nextPoint = lastPoint.add(0,-pointDistance,0);
                                break;
                            case "LeftDown":
                                // now just a combo
                                nextPoint = reference.add(lastPoint.add(new Vector(refToLPoint.getZ(), 0, refToLPoint.getX() * -1).normalize().multiply(pointDistance)).toVector().subtract(reference.toVector()).normalize().multiply(distanceAway));
                                nextPoint = nextPoint.add(0,-pointDistance,0);
                                break;
                            case "LeftUp":
                                nextPoint = reference.add(lastPoint.add(new Vector(refToLPoint.getZ(), 0, refToLPoint.getX() * -1).normalize().multiply(pointDistance)).toVector().subtract(reference.toVector()).normalize().multiply(distanceAway));
                                nextPoint = nextPoint.add(0,pointDistance,0);
                                break;
                            case "RightDown":
                                nextPoint = reference.add(lastPoint.add(new Vector(refToLPoint.getZ() * -1, 0, refToLPoint.getX()).normalize().multiply(pointDistance)).toVector().subtract(reference.toVector()).normalize().multiply(distanceAway));
                                nextPoint = nextPoint.add(0,-pointDistance,0);
                                break;
                            case "RightUp":
                                nextPoint = reference.add(lastPoint.add(new Vector(refToLPoint.getZ() * -1, 0, refToLPoint.getX()).normalize().multiply(pointDistance)).toVector().subtract(reference.toVector()).normalize().multiply(distanceAway));
                                nextPoint = nextPoint.add(0,pointDistance,0);
                                break;
                            default:
                                nextPoint = lastPoint;
                                break;
                        }
                        lastPoint = nextPoint;
                        p.spawnParticle(Particle.REDSTONE, nextPoint, 1, colors.get((int) spellPhase));
                        pastPoints.add(new Location(nextPoint.getWorld(), nextPoint.getX(), nextPoint.getY(), nextPoint.getZ()));
                    }
                    // display past points
                    for (int i = 0; i < pastPoints.size(); i++){
                        p.spawnParticle(Particle.REDSTONE, pastPoints.get(i), 1, colors.get(i));
                        if (i > 1){
                            Location between = pastPoints.get(i-1).add(pastPoints.get(i).toVector().subtract(pastPoints.get(i-1).toVector()));
                            p.spawnParticle(Particle.REDSTONE, between, 1, colors.get(i));
                        }
                    }
                    spellPhase += 0.5;
                } else {
                    this.cancel();
                    spellIndex.findPlayer(uuid).setDisplayingSpell(false);
                }
            }
        }.runTaskTimerAsynchronously(plugin,5,5L);
    }

    public void displaySpell(Player p){

        UUID uuid = p.getUniqueId();
        double pointDistance = 0.1;
        double distanceAway = 1.0;
        Vector forward = p.getEyeLocation().getDirection().normalize().setY(0).multiply(2).normalize();
        Location p1 = p.getEyeLocation().add(forward.multiply(distanceAway));
        p.spawnParticle(Particle.REDSTONE, p1, 1,colors.get(0));
        final Location preference = p.getEyeLocation();
        new BukkitRunnable(){
            int spellPhase = 0;
            boolean middle = false;
            boolean displayMore = false;
            Location lastPoint = p1;
            Location reference = preference;
            Vector lastVector = p1.toVector().subtract(reference.toVector());
            Vector referenceVector = forward;
            @Override
            public void run() {
                if (spellPhase < spellPattern.size()) {
                    if (spellPhase > 1){
                        p.spawnParticle(Particle.REDSTONE, lastPoint, 1, colors.get(spellPhase - 1));
                    }
                    reference = preference;
                    referenceVector = lastPoint.toVector().subtract(reference.toVector());
                    String direction = spellPattern.get(spellPhase);
                    Location nextPoint;

                    Vector vb;
                    Location middlePoint;
                    Vector orthogonal;
                    Location oLoc;
                    switch (direction) {
                        case "Left":
                            //orthogonal = referenceVector.rotateAroundY(Math.PI / 2).normalize().multiply(pointDistance);
                            orthogonal = new Vector(referenceVector.getZ(), 0, referenceVector.getX() * -1).normalize();
                            // https://en.wikipedia.org/wiki/Rotation_matrix#Basic_rotations
                            oLoc = new Location(lastPoint.getWorld(), lastPoint.getX() + orthogonal.getX(), lastPoint.getY() + orthogonal.getY(), lastPoint.getZ() + orthogonal.getZ());
                            nextPoint = reference.add(oLoc.toVector().subtract(reference.toVector()).normalize().multiply(distanceAway));
                            break;
                        case "Right":
                            //orthogonal = referenceVector.rotateAroundY(2  * Math.PI / 3).normalize().multiply(pointDistance);
                            //orthogonal = new Vector(referenceVector.getX() * Math.cos((2 * Math.PI) / 3) - referenceVector.getZ() * Math.sin((2 * Math.PI) / 3), 0, referenceVector.getX() * Math.sin((2 * Math.PI) / 3) + referenceVector.getZ() * Math.cos((2 * Math.PI) / 3));
                            orthogonal = new Vector(referenceVector.getZ() * -1, 0, referenceVector.getX()).normalize();
                            oLoc = new Location(lastPoint.getWorld(), lastPoint.getX() + orthogonal.getX(), lastPoint.getY() + orthogonal.getY(), lastPoint.getZ() + orthogonal.getZ());
                            nextPoint = reference.add(oLoc.toVector().subtract(reference.toVector()).normalize().multiply(distanceAway));
                            break;
                        case "Up":
                            nextPoint = new Location(lastPoint.getWorld(), lastPoint.getX(), lastPoint.getY() + pointDistance, lastPoint.getZ());
                            break;
                        case "Down":
                            nextPoint = new Location(lastPoint.getWorld(), lastPoint.getX(), lastPoint.getY() - pointDistance, lastPoint.getZ());
                            break;
                        case "RightUp":
                            orthogonal = referenceVector.rotateAroundY((Math.PI * 2) / 3).normalize().multiply(pointDistance);
                            oLoc = new Location(lastPoint.getWorld(), lastPoint.getX() + orthogonal.getX(), lastPoint.getY() + orthogonal.getY(), lastPoint.getZ() + orthogonal.getZ());
                            nextPoint = reference.add(oLoc.toVector().subtract(reference.toVector()).normalize().multiply(distanceAway));
                            nextPoint = new Location(nextPoint.getWorld(), nextPoint.getX(), nextPoint.getY() + pointDistance, nextPoint.getZ());
                            break;
                        case "LeftUp":
                            orthogonal = referenceVector.rotateAroundY(Math.PI / 2).normalize().multiply(pointDistance);
                            oLoc = new Location(lastPoint.getWorld(), lastPoint.getX() + orthogonal.getX(), lastPoint.getY() + orthogonal.getY(), lastPoint.getZ() + orthogonal.getZ());
                            nextPoint = reference.add(oLoc.toVector().subtract(reference.toVector()).normalize().multiply(distanceAway));
                            nextPoint = new Location(nextPoint.getWorld(), nextPoint.getX(), nextPoint.getY() + pointDistance, nextPoint.getZ());
                            break;
                        case "RightDown":
                            orthogonal = referenceVector.rotateAroundY((Math.PI * 2) / 3).normalize().multiply(pointDistance);
                            oLoc = new Location(lastPoint.getWorld(), lastPoint.getX() + orthogonal.getX(), lastPoint.getY() + orthogonal.getY(), lastPoint.getZ() + orthogonal.getZ());
                            nextPoint = reference.add(oLoc.toVector().subtract(reference.toVector()).normalize().multiply(distanceAway));
                            nextPoint = new Location(nextPoint.getWorld(), nextPoint.getX(), nextPoint.getY() - pointDistance, nextPoint.getZ());
                            break;
                        case "LeftDown":
                            orthogonal = referenceVector.rotateAroundY(Math.PI / 2).normalize().multiply(pointDistance);
                            oLoc = new Location(lastPoint.getWorld(), lastPoint.getX() + orthogonal.getX(), lastPoint.getY() + orthogonal.getY(), lastPoint.getZ() + orthogonal.getZ());
                            nextPoint = reference.add(oLoc.toVector().subtract(reference.toVector()).normalize().multiply(distanceAway));
                            nextPoint = new Location(nextPoint.getWorld(), nextPoint.getX(), nextPoint.getY() - pointDistance, nextPoint.getZ());
                            break;
                        default:
                            nextPoint = lastPoint;
                            break;
                    }
                    vb = nextPoint.toVector().subtract(lastPoint.toVector());
                    middlePoint = lastPoint.add(vb.multiply(0.5));
                    if (middle) {
                        p.spawnParticle(Particle.REDSTONE, middlePoint, 1, colors.get(spellPhase));
                        middle = false;
                    } else {
                        p.spawnParticle(Particle.REDSTONE, nextPoint, 1, colors.get(spellPhase));
                        middle = true;
                        Bukkit.getLogger().info(spellPattern.get(spellPhase));
                        spellPhase++;
                    }
                    lastPoint = nextPoint;
                } else {
                    this.cancel();
                    spellIndex.findPlayer(uuid).setDisplayingSpell(false);
                }
            }
        }.runTaskTimerAsynchronously(plugin,20,5);
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

    /**
     *
     *  YOYOYO CAST USING ARRAYLIST INSTEAD OF INT ARRAY
     *
     * OKOK, basically how a player casts a spell  they can go 8 directions:
     *     1 2 3
     *     4 x 5
     *     6 7 8
     *
     *  x is where the last point was. Idealy, if # is possible points, you could go between two points like:
     *    # # # # # # #
     *    # # # # # # #
     *    # # # # # # #
     *    # # # 1 # # #
     *    # # # # # 2 #
     *    # # # # # # #
     *    # # # # # # #
     *
     *  (This is showing the total matrix of point positions)
     *  If you could go between these points it would be too difficult in game to be that precise,
     *  instead you would have to make a spell like:
     *    # 1 #
     *    # # 2 3
     *
     *  As for detecting where they actually are casting in the game, there are a few rules:
     *    - particles will where your cast points are, and 1 additional particles will be inbetween points - x
     *    - particles are color coded depending on how many cast points you have - x
     *    - The last point will be the reference for the next point - x
     *    - Maximum of 9 cast points - stop inputting particles after - can be changed in the future - x
     *    - moving your wand in the same direction for enough length will add another point - this is measured by yaw angles - (no)
     *    - changing direction of cast will add another point - x
     *    - Left click will attempt to cast the spell - you must left-click, or it will not cast. - x
     *    - It does not matter where you are to left-click, but after 30 seconds, the spell will disappear, and you won't be able to cast it - x
     *
     *  Data for the spell is stored in the PlayerData object class as an array list -
     *  Any spell casts will be reset when a player logs off -
     *
     *  Contrary from a previous version, the inputted spellPattern will be final and nothing will be calculated with it. -
     *  All the cast positions and converting them into a string will be done while the player is casting a spell. -
     *
     *  All spells, or most at least, should have a spell cooldown you should be able to view in a spell menu
     *  Cooldown times will be listed on the book and notify you in chat when a spell's cooldown is up
     *  If you try to cast a spell while in cooldown it will provide you will an error and how long the cooldown has left
     *
     *  To view how to cast a spell, you can toggle in the config how the players can view the spell.
     *  If enabled, clicking a book in your spell menu will close the menu and slowly spawn particles showing you how the spell is cast
     *  Otherwise, players can view a spell by having a command done for them, or having the proper permission to do the command themselves.
     *  /nm spell (spell) [name]
     *  Adding a name is optional if a moderator decides they want to forcefully show someone how to preform a spell
     *
     *  To obtain a spell, certain mobs will drop certain spells
     *  When a spell is dropped - a non-obtainable item spins in the air and explodes w/ particles and noises
     *  Once you learn a spell, the mob will no longer drop that spell
     *  Some mobs require you to be a certain level to obtain certain spells
     *
     *  Wands:
     *  All wands use Magic Dust to craft
     *  To obtain magic dust, there are a few ways
     *   - Mine in Magic Mines (have to be set up my moderator)
     *   - Mine Magic Ore naturally generating (replaces some gold deposits | configurable)
     *   - Kill Fairies (Vex w/ gold swords & gold particles) -
     *     these will naturally spawn in the world in groups of 2-4 and drop 2-4 magic dust each
     *     they have a higher spawn rate near Magic Mines if one has been created (configurable)
     *   - You can also get Magic Dust through commands
     *  Wands can only be crafted if you are a high enough level to use them
     *  Even if someone else crafts one for you, a sufficient level is still required to use
     *  Some wands require special items to craft. If it is an item that isn't in vanilla Minecraft,
     *  then you can fight a mob or boss to get this item.
     *  Bosses and mobs are configurable to spawn in the wild or only spawn with commands
     *  The special items can also be obtained with commands
     *
     *
     *  Other Notes:
     *   - When spell is broken (out of bounds, on cooldown, no mana, been 30 sec), glass breaking noise and glass particles will
     *     be where the cast points were
     *   - Permission node to allow a player to use magic
     *   - Customizable text for each prompt
     *   - placeholderapi for playerdata
     *
     *  Alchemy is separate from magic, It may help obtain magic items but not necessary to progress.
     *  Alchemy will also have it's dedicated items specific to Alchemy
      */



}
