package me.jadenp.notmagic.RevisedClasses;

import me.jadenp.notmagic.NotMagic;
import me.jadenp.notmagic.SpellWorkshop.NotCallback;
import me.jadenp.notmagic.SpellWorkshop.WorkshopSpell;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import javax.swing.*;
import java.io.File;
import java.util.*;

public class SpellIndex {

    /**
     * How to add a new spell: (I think)
     * Add a spell object to the spells list in the main SpellIndex class (this)
     * Add to the bookmarked switch statement in this class
     * Add a method in this class for the spell that accepts a Player parameter
     * go to Magic.java and add an if statement to check for the spell name if it is a main spell
     */

    private Plugin plugin;
    private Magic magic;
    Random rand = new Random();
    private final String noLevelMsg = ChatColor.BLUE + "You are not a high enough level to cast this spell! ";
    boolean debug = true;
    //File customSpells = new File(plugin.getDataFolder() + File.separator + "customSpells.yml");

    private List<Spell> spells = new ArrayList<>();
    private List<CustomSpell> customSpells = new ArrayList<>();
    public SpellIndex(Plugin plugin, Magic magic){
        this.plugin = plugin;
        this.magic = magic;

        addSpells();
    }

    public void addSpells(){

        // example spell setup
        // first in the array should always be "Start"
        // next point can be "Up", "Down", "Left", "Right", "LeftUp", "LeftDown", "RightUp", "RightDown" - there cannot be the same direction twice in a row
        // castTime, cooldown - all in ticks
        // save LeftDown to start the custom spells
        spells.add(new Spell("Burn", 3, 0,15, 1, new ArrayList<>(Arrays.asList("Start", "Up")), Items.data("SBBurn"), plugin, true));

        spells.add(new Spell("Zap", 9, 0,100, 1, new ArrayList<>(Arrays.asList("Start", "RightDown", "LeftDown", "RightDown")), Items.data("SBZap"), plugin, false));

        spells.add(new Spell("Heal", 15, 2,1200, 1, new ArrayList<>(Arrays.asList("Start", "Left", "Right", "Up", "Down", "Right", "Left", "Down", "Up")), Items.data("SBHeal"), plugin, false));

        spells.add(new Spell("Strength", 20, 2,1200, 1, new ArrayList<>(Arrays.asList("Start", "Up", "Left", "RightDown", "Right")), Items.data("SBStrength"), plugin, false));

        spells.add(new Spell("Burst", 15, 4,150, 1, new ArrayList<>(Arrays.asList("Start", "LeftUp", "Right", "LeftDown", "Down")), Items.data("SBBurst"), plugin, true));

        spells.add(new Spell("Snipe", 30, 60, 200,1, new ArrayList<>(Arrays.asList("Start", "LeftUp", "RightDown", "RightUp", "LeftDown")), Items.data("SBSnipe"), plugin, true));

        spells.add(new Spell("Locate", 50, 15, 300, 1, new ArrayList<>(Arrays.asList("Start", "RightUp", "RightDown", "LeftDown", "Down")), Items.data("SBLocate"), plugin, true));

        spells.add(new Spell("Teleport", 25, 5, 25, 1, new ArrayList<>(Arrays.asList("Start", "Up", "Down", "Up", "Down", "Up", "Down")), Items.data("SBTeleport"), plugin, true));

        spells.add(new Spell("Iron Wall Attack", 50, 5, 30, 1, new ArrayList<>(Arrays.asList("Start", "Up", "Right", "Down", "Left")), Items.data("SBIronWallAttack"), plugin, true));

        spells.add(new Spell("Life Steal", 75, 120, 600, 1, new ArrayList<>(Arrays.asList("Start", "Down", "LeftDown", "Right", "LeftUp")), Items.data("SBLifeSteal"), plugin, true));

        spells.add(new Spell("Smite", 35, 5, 200, 1, new ArrayList<>(Arrays.asList("Start", "RightDown", "LeftDown", "RightDown", "LeftDown")), Items.data("SBSmite"), plugin, true));

        spells.add(new Spell("Fireball", 30, 3, 100, 1, new ArrayList<>(Arrays.asList("Start", "LeftUp", "RightUp", "RightDown", "LeftDown")), Items.data("SBFireball"), plugin, true));

        spells.add(new Spell("Ice Shards", 45, 2, 20, 1, new ArrayList<>(Arrays.asList("Start", "RightDown", "LeftUp", "Down", "Up", "LeftDown", "RightUp")), Items.data("SBIceShards"), plugin, true));
    }

    public void addWorkshopSpell(WorkshopSpell spell){
        spells.add(spell);
    }

    public List<String> getSpellBookNames(){
        List<String> names = new ArrayList<>();
        for (Spell spell : spells){
            if (spell.getSpellBook() != null)
            if (spell.getSpellBook().getItemMeta() != null)
            names.add(ChatColor.stripColor(spell.getSpellBook().getItemMeta().getDisplayName()));
        }
        return names;
    }

    public List<String> getUniqueSpellPattern(int size){
        ArrayList<String> pattern = new ArrayList<>();
        while (true) {
            for (int i = 0; i < 50; i++) {
                pattern.clear();
                for (int j = 0; j < size; j++) {
                    int r = (int) (Math.random() * 8);
                    switch (r) {
                        case 0:
                            pattern.add("Up");
                            break;
                        case 1:
                            pattern.add("RightUp");
                            break;
                        case 2:
                            pattern.add("Right");
                            break;
                        case 3:
                            pattern.add("RightDown");
                            break;
                        case 4:
                            pattern.add("Down");
                            break;
                        case 5:
                            pattern.add("LeftDown");
                            break;
                        case 6:
                            pattern.add("Left");
                            break;
                        case 7:
                            pattern.add("LeftUp");
                            break;
                        default:
                            break;
                    }
                }
                if (querySpell(pattern) == null) {
                    return pattern;
                }
            }
            size++;
        }
    }


    public PlayerData findPlayer(UUID uuid){
        for (PlayerData data : magic.eventClass.getPlayerData()){
            if (data.getUuid().equals(uuid)){
                return data;
            }
        }
        return null;
    }

    public Spell querySpell(ArrayList<String> pattern){
        if (pattern == null) {
            if (debug){
                Bukkit.getLogger().info("Null spell pattern");
            }
            return null;
        }
        for (Spell spell : spells){
            if (debug)
                Bukkit.getLogger().info("Spell: " + spell.getName());
            if (pattern.size() == spell.getSpellPattern().size()){
                boolean match = true;
                for (int i = 0; i < pattern.size(); i++){
                    if (!pattern.get(i).equals(spell.getSpellPattern().get(i))) {
                        if (debug)
                            Bukkit.getLogger().info(spell.getSpellPattern().get(i) + " : " + pattern.get(i));
                        match = false;
                        break;
                    }
                }
                if (match){
                    if (debug)
                        Bukkit.getLogger().info("Match: " + spell.getName());
                    return spell;
                }
            } else {
                if (debug) {
                    Bukkit.getLogger().info("Different Pattern Size: " + spell.getSpellPattern().size() + " : " + pattern.size());
                    if (spell.getName().equalsIgnoreCase("Zap")){
                        Bukkit.getLogger().info("Zap Pattern:");
                        for (int i = 0; 9 < spell.getSpellPattern().size(); i++){
                            Bukkit.getLogger().info(spell.getSpellPattern().get(i));
                        }
                        Bukkit.getLogger().info("Cast Pattern:");
                        for (int i = 0; 9 < pattern.size(); i++){
                            Bukkit.getLogger().info(pattern.get(i));
                        }
                    }
                }
            }
        }
        /*
        for (CustomSpell spell : customSpells){
            if (debug)
                Bukkit.getLogger().info("Spell: " + spell.getName());
            if (pattern.size() == spell.getSpellPattern().size()){
                boolean match = true;
                for (int i = 0; i < pattern.size(); i++){
                    if (!pattern.get(i).equals(spell.getSpellPattern().get(i))) {
                        if (debug)
                            Bukkit.getLogger().info(spell.getSpellPattern().get(i) + " : " + pattern.get(i));
                        match = false;
                        break;
                    }
                }
                if (match){
                    if (debug)
                        Bukkit.getLogger().info("Match: " + spell.getName());
                    return spell;
                }
            }
        }*/
        return null;
    }

    public Spell querySpell(String name){
        for (Spell spell : spells){
            if (spell.getName().equalsIgnoreCase(name))
                return spell;
        }
        return null;
    }
    public CustomSpell queryCustomSpell(String name){
        for (CustomSpell spell : customSpells){
            if (spell.getName().equalsIgnoreCase(name))
                return spell;
        }
        return null;
    }


    public Spell querySpell(ItemStack spellBook){
        for (Spell spell : spells){
            if (spell.getSpellBook().isSimilar(spellBook)){
                return spell;
            }
        }
        return null;
    }

    public static Vector rotateVectorCC(Vector vec, Vector axis, double theta){
        double x, y, z;
        double u, v, w;
        x=vec.getX();y=vec.getY();z=vec.getZ();
        u=axis.getX();v=axis.getY();w=axis.getZ();
        double v1 = u * x + v * y + w * z;
        double xPrime = u* v1 *(1d - Math.cos(theta))
                + x*Math.cos(theta)
                + (-w*y + v*z)*Math.sin(theta);
        double yPrime = v* v1 *(1d - Math.cos(theta))
                + y*Math.cos(theta)
                + (w*x - u*z)*Math.sin(theta);
        double zPrime = w* v1 *(1d - Math.cos(theta))
                + z*Math.cos(theta)
                + (-v*x + u*y)*Math.sin(theta);
        return new Vector(xPrime, yPrime, zPrime);
    }

    public void performSpell(String spell, Player p){
        // match spells here instead of doing it in magic
        Spell obj = querySpell(spell);
        if (obj != null) {
            if (obj.getCastTime() > 20) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, obj.getCastTime(), 5));
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, obj.getCastTime(), 128));
                p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, obj.getCastTime(), 128));
                new BukkitRunnable(){
                    int clock = 0;

                    @Override
                    public void run() {
                        if (clock < obj.getCastTime()){
                            if (p.isOnline()) {
                                float progress = (float) clock / obj.getCastTime();
                                int coloredBlocks = (int) (progress * 10);
                                StringBuilder builder = new StringBuilder();
                                builder.append(ChatColor.GRAY);
                                for (int i = 0; i < 10 - coloredBlocks; i++) {
                                    builder.append("❙");
                                }
                                builder.append(ChatColor.BLUE);
                                for (int i = 0; i < coloredBlocks * 2; i++) {
                                    builder.append("❙");
                                }
                                builder.append(ChatColor.GRAY);
                                for (int i = 0; i < 10 - coloredBlocks; i++) {
                                    builder.append("❙");
                                }
                                p.sendTitle("", builder.toString(), 0, 10, 0);
                            }
                            clock+= 10;
                        } else {
                            this.cancel();
                        }
                    }
                }.runTaskTimer(NotMagic.getInstance(), 0, 10);
            }


            if (obj instanceof WorkshopSpell) {
                WorkshopSpell ws = (WorkshopSpell) obj;
                // where the player's crosshair landed
                Location target = p.getTargetBlock(null, ws.getPotential().getPotentialPower() * ws.getPotentialAmount()).getLocation();
                // where the spell will spawn from
                Location spawnLocation = ws.getControl().controlResults(target, p.getLocation());
                // vector from spawn location to target
                Vector spawnToTarget = target.toVector().subtract(spawnLocation.toVector());
                // random ass vector
                Vector randomVector = new Vector(Math.random() * 2 - 1, Math.random() * 2 - 1, Math.random() * 2 - 1).normalize();
                // rotating the target vector in a random direction to account for accuracy
                // note: accuracy is coming in max degrees, which we have to use to get a random accuracy and then convert it to radians
                spawnToTarget = rotateVectorCC(spawnToTarget, randomVector, Math.random() * ws.getAccuracy() / 0.0174533);

                // creating the direct path
                List<Location> directPath = new ArrayList<>();
                // length of path
                final double length = spawnToTarget.length();
                // point length will be 2 blocks - larger length = fewer points = less lag
                Vector point = spawnToTarget.normalize().multiply(2);
                final Vector spacing = point.clone();
                // adding spacing and a point to direct path until we get to the target
                while (point.length() < length){
                    directPath.add(p.getLocation().add(point));
                    point.add(spacing);
                }
                // spawn particles with potential
                new BukkitRunnable(){

                    int progress = 0; // how far the particle is in its path
                    // making these chumps final
                    final Location start = spawnLocation;
                    final List<Location> path = directPath;
                    final Location end = spawnLocation.add(point);
                    final Player player = p;
                    @Override
                    public void run() {
                        if (progress < path.size()) {
                            // spawn particles in path
                            ws.getPotential().potentialResults(path.get(progress), start, locations -> {
                                if (locations != null){
                                    // I don't remember why we need these locations, but if we do, we would have to get the callback
                                    return;
                                }
                            });

                            progress++;
                        } else {
                            this.cancel();
                            // do area effect & intensity
                            ws.getAreaEffect().areaEffectResults(end, 1, locations -> {
                                for (Location location : locations){
                                    ws.getIntensity().intensityResults(location, 1, player, end);
                                }
                            });


                        }
                    }
                }.runTaskTimer(plugin, 0, 11 - ws.getPotential().getPotentialPower());

            } else {
                // preset spell
                switch (obj.getName()) {
                    case "Zap":
                        zap(p);
                        break;
                    case "Burn":
                        burn(p);
                        break;
                    case "Snipe":
                        snipe(p);
                        break;
                    case "Heal":
                        heal(p);
                        break;
                    case "Strength":
                        strength(p);
                        break;
                    case "Burst":
                        burst(p);
                        break;
                    case "Teleport":
                        teleport(p);
                        break;
                    case "Iron Wall Attack":
                        ironWallAttack(p);
                        break;
                    case "Life Steal":
                        lifeSteal(p);
                        break;
                    case "Smite":
                        smite(p);
                        break;
                    case "Fireball":
                        fireball(p);
                        break;
                    case "Ice Shards":
                        iceShards(p);
                        break;
                    case "Locate":
                        locate(p);
                        break;
                }
            }
            }/* else{
                // custom spell
                CustomSpell customSpell = queryCustomSpell(spell);
                if (customSpell != null) {
                    List<String> actions = customSpell.getActions();

                } else {
                    // not a spell
                }
            }*/

    }

    public void burn(Player p) {
        Spell spell = spells.get(0);
        PlayerData data = findPlayer(p.getUniqueId());
        if (data.onCooldown(spell.getName())){
            // on cooldown
            p.playSound(p, Sound.ITEM_LODESTONE_COMPASS_LOCK, 1, 1);
            return;
        }
        if (data.getLevel() < spell.getRequiredLevel()){
            // level too small
            p.sendMessage(NotMagic.getInstance().getPrefix() + noLevelMsg + ChatColor.GRAY + "(" + spell.getRequiredLevel() + ")");
            return;
        }
        if (data.getMp() < spell.getMpCost()){
            // not enough mana
            p.playSound(p, Sound.ITEM_DYE_USE, 1, 1);
            return;
        }
        // finally, able to cast the spell
        data.useMP(spell.getMpCost());
        data.addCooldown(spell.getName(),spell.getCooldown());


        Location front = p.getEyeLocation().add(p.getLocation().getDirection().multiply(1.3));
        p.getWorld().spawnParticle(Particle.LAVA, front, 1);
        Vector d = p.getLocation().getDirection();
        new BukkitRunnable() {
            int timer = 0;

            @Override
            public void run() {
                Location loc = front.add(d.multiply(1 + (timer / 20)));
                p.getWorld().spawnParticle(Particle.LAVA, loc, 1);
                timer++;
                if (loc.getBlock().getType() != Material.AIR) {
                    this.cancel();
                    return;
                }
                loc = front.add(d.multiply(1 + (timer / 20)));
                p.getWorld().spawnParticle(Particle.LAVA, loc, 1);
                timer++;
                double radius = 2D;
                List<Entity> near = Objects.requireNonNull(loc.getWorld()).getEntities();
                for (Entity e : near) {
                    if (e.getLocation().distance(loc) <= radius) {
                        if (e instanceof LivingEntity) {
                            if (e != p) {
                                e.setFireTicks(60);
                                ((LivingEntity) e).damage(3, p);

                            }
                        }
                    }
                }
                if (loc.getBlock().getType() != Material.AIR) {
                    this.cancel();
                }
                if (timer == 16) {
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 1L);
    }

    public void zap(Player p){
        Spell spell = spells.get(2);
        PlayerData data = findPlayer(p.getUniqueId());
        if (data.onCooldown(spell.getName())){
            // on cooldown
            p.playSound(p, Sound.ITEM_LODESTONE_COMPASS_LOCK, 1, 1);
            return;
        }
        if (data.getLevel() < spell.getRequiredLevel()){
            // level too small
            p.sendMessage(NotMagic.getInstance().getPrefix() + noLevelMsg + ChatColor.GRAY + "(" + spell.getRequiredLevel() + ")");
            return;
        }
        if (data.getMp() < spell.getMpCost()){
            // not enough mana
            p.playSound(p, Sound.ITEM_DYE_USE, 1, 1);
            return;
        }
        // finally, able to cast the spell
        data.useMP(spell.getMpCost());
        data.addCooldown(spell.getName(), spell.getCooldown());

        Location iF1 = p.getEyeLocation().add(p.getLocation().getDirection().multiply(0.7));
        Location iF = new Location(iF1.getWorld(), iF1.getX(), iF1.getY() + 0.7, iF1.getZ());
        Item item = Objects.requireNonNull(iF.getWorld()).dropItem(iF, new ItemStack(Material.LAPIS_BLOCK, 1));
        item.setTicksLived(6000 - 60);
        item.setPickupDelay(Integer.MAX_VALUE);
        item.setGravity(false);
        item.setVelocity(new Vector(0, 0, 0));
        Location iF2 = new Location(iF1.getWorld(), iF1.getX(), iF1.getY() - 0.7, iF1.getZ());
        Item item2 = Objects.requireNonNull(iF2.getWorld()).dropItem(iF2, new ItemStack(Material.LAPIS_BLOCK, 1));
        item2.setTicksLived(6000 - 60);
        item2.setPickupDelay(Integer.MAX_VALUE);
        item2.setGravity(false);
        item2.setVelocity(new Vector(0, 0, 0));
        Location front = p.getEyeLocation().add(p.getLocation().getDirection().multiply(1.3));
        Vector d = p.getLocation().getDirection();
        Location loc = front;
        for (int i = 0; i < 50; i++) {
            loc = front.add(d.multiply(1 + ((double) i / 10)));
            double radius = 2D;
            List<Entity> near = Objects.requireNonNull(loc.getWorld()).getEntities();
            for (Entity e : near) {
                if (e.getLocation().distance(loc) <= radius) {
                    if (e instanceof LivingEntity) {
                        if (e != p) {

                            final Particle.DustOptions dustOptions = new Particle.DustOptions(org.bukkit.Color.fromRGB(113, 18, 255), 1);

                            new BukkitRunnable() {
                                Location location = p.getEyeLocation().add(p.getLocation().getDirection().multiply(0.3));
                                Vector v = e.getLocation().toVector().subtract(location.toVector()).normalize();
                                int timer = 0;

                                @Override
                                public void run() {

                                    Vector nV = new Vector(v.getX() + (((double) rand.nextInt(20) / 10) - 1), v.getY() + (((double) rand.nextInt(20) / 10) - 1), v.getZ() + (((double) rand.nextInt(20) / 10) - 1)).normalize();

                                    for (int i = 0; i < 10; i++) {
                                        location = location.add(nV.multiply(0.5));
                                        Objects.requireNonNull(location.getWorld()).spawnParticle(Particle.REDSTONE, location, 1, dustOptions);
                                    }
                                    v = e.getLocation().toVector().subtract(location.toVector()).normalize();
                                    double radius = 2D;
                                    List<Entity> near = Objects.requireNonNull(location.getWorld()).getEntities();
                                    for (Entity b : near) {
                                        if (b.getLocation().distance(location) <= radius) {
                                            if (b instanceof LivingEntity) {
                                                if (b != p) {

                                                    ((LivingEntity) b).damage(10, p);
                                                    b.getWorld().spawnParticle(Particle.CRIT_MAGIC, b.getLocation(), 1, 1, 1, 1);

                                                    if (b.equals(e)) {
                                                        this.cancel();
                                                        return;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    if (location.getBlock().equals(e.getLocation().getBlock())) {
                                        this.cancel();
                                        return;
                                    }
                                    if (timer > 50) {
                                        this.cancel();
                                        return;
                                    }
                                    timer++;
                                }
                            }.runTaskTimer(plugin, 0, 1L);
                            return;

                        }
                    }
                }
            }

        }
        final Particle.DustOptions dustOptions = new Particle.DustOptions(org.bukkit.Color.fromRGB(113, 18, 255), 2);
        Location e = loc;
        new BukkitRunnable() {
            Location location = p.getEyeLocation().add(p.getLocation().getDirection().multiply(0.3));
            Vector v = e.toVector().subtract(location.toVector()).normalize();
            int timer = 0;

            @Override
            public void run() {
                Vector nV = new Vector(v.getX() + (((double) rand.nextInt(20) / 10) - 1), v.getY() + (((double) rand.nextInt(20) / 10) - 1), v.getZ() + (((double) rand.nextInt(20) / 10) - 1)).normalize();
                for (int i = 0; i < 10; i++) {
                    location = location.add(nV.multiply(0.5));
                    Objects.requireNonNull(location.getWorld()).spawnParticle(Particle.REDSTONE, location, 1, dustOptions);
                }
                v = e.toVector().subtract(location.toVector()).normalize();
                double radius = 2D;
                List<Entity> near = Objects.requireNonNull(location.getWorld()).getEntities();
                for (Entity b : near) {
                    if (b.getLocation().distance(location) <= radius) {
                        if (b instanceof LivingEntity) {
                            if (b != p) {

                                ((LivingEntity) b).damage(5, p);
                                b.getWorld().spawnParticle(Particle.CRIT_MAGIC, b.getLocation(), 1, 1, 1, 1);

                                if (b.getLocation().getBlock().equals(e.getBlock())) {
                                    this.cancel();
                                    return;
                                }
                            }
                        }
                    }
                }
                if (location.getBlock().equals(e.getBlock())) {
                    this.cancel();
                    return;
                }
                if (timer > 50) {
                    this.cancel();
                    return;
                }
                timer++;
            }
        }.runTaskTimer(plugin, 0, 1L);
    }

    public void snipe(Player p) {
        Spell spell = spells.get(5);
        PlayerData data = findPlayer(p.getUniqueId());
        if (data.onCooldown(spell.getName())){
            // on cooldown
            p.playSound(p, Sound.ITEM_LODESTONE_COMPASS_LOCK, 1, 1);
            return;
        }
        if (data.getLevel() < spell.getRequiredLevel()){
            // level too small
            p.sendMessage(NotMagic.getInstance().getPrefix() + noLevelMsg + ChatColor.GRAY + "(" + spell.getRequiredLevel() + ")");
            return;
        }
        if (data.getMp() < spell.getMpCost()){
            // not enough mana
            p.playSound(p, Sound.ITEM_DYE_USE, 1, 1);
            return;
        }
        // finally, able to cast the spell
        data.useMP(spell.getMpCost());
        data.addCooldown(spell.getName(),spell.getCooldown());

        Location launchLocation = p.getEyeLocation().add(p.getEyeLocation().getDirection());
        Vector launchDirection = p.getEyeLocation().getDirection().normalize();
        assert launchLocation.getWorld() != null;
        // 8 places the particles can come from
        Vector down = new Vector(0,-1,0);
        Vector up = new Vector(0,1,0);
        Vector side1 = launchDirection.getCrossProduct(down); // pretend this is right
        Vector side2 = new Vector(0 - side1.getX(), 0 - side1.getY(), 0 - side1.getZ()); // pretend this is left
        // these are the between vectors (I think of them as quartiles because they are in the middle if you graph the origin as launchLocation)
        Vector q1 = new Vector(side1.getX() + up.getX(), side1.getY() + up.getY(), side1.getZ() + up.getZ()).normalize();
        Vector q2 = new Vector(side2.getX() + up.getX(), side2.getY() + up.getY(), side2.getZ() + up.getZ()).normalize();
        Vector q3 = new Vector(side2.getX() + down.getX(), side2.getY() + down.getY(), side2.getZ() + down.getZ()).normalize();
        Vector q4 = new Vector(side1.getX() + down.getX(), side1.getY() + down.getY(), side1.getZ() + down.getZ()).normalize();
        // adding them to a list, so I can select one of them randomly with a random number gen
        List<Vector> particlePlaces = new ArrayList<>();
        particlePlaces.add(down);
        particlePlaces.add(up);
        particlePlaces.add(side1);
        particlePlaces.add(side2);
        particlePlaces.add(q1);
        particlePlaces.add(q2);
        particlePlaces.add(q3);
        particlePlaces.add(q4);

        // particle distance from launch
        float PDFL = 1.0f;
        float particleSpeed = 1.0f;
        // how many particles will spawn everytime the runnable goes through
        int particlesPerRun = 3; // total particles = particlesPerRun * 6

        // spawn particles
        new BukkitRunnable(){
            int runs = 0;
            @Override
            public void run() {
                if (runs < 60){
                    // some kewl particles
                    for (int i = 0; i < particlesPerRun; i++) {
                        Vector randomOf8 = particlePlaces.get((int) (Math.random() * 8));
                        //                                                                  adding the vector to the launch location to get the starting point of the vector - PDFL so I can adjust how far away it starts                               getting the reverse vector so it can shoot in the opposite direction back into launchLocation, particleSpeed so I can adjust how fast it comes back & so it doesnt overshoot
                        launchLocation.getWorld().spawnParticle(Particle.REVERSE_PORTAL, launchLocation.getX() + (randomOf8.getX() * PDFL), launchLocation.getY() + (randomOf8.getY() * PDFL), launchLocation.getZ() + (randomOf8.getZ() * PDFL), 1, 0 - (randomOf8.getX() * particleSpeed), 0 - (randomOf8.getY() * particleSpeed), 0 - (randomOf8.getZ() * particleSpeed));
                    }
                } else {
                    this.cancel();
                    // launch the arrow
                    Arrow arrow = launchLocation.getWorld().spawnArrow(launchLocation, launchDirection, 2f, 6);
                    arrow.setShooter(p);
                    arrow.setDamage(10 + findPlayer(p.getUniqueId()).getLevel());
                }
                runs++;
            }
        }.runTaskTimer(plugin,0L,1L);


        //arrow.setMetadata("magic", new FixedMetadataValue(plugin, true));
    }

    public void heal(Player p){
        Spell spell = spells.get(2);
        PlayerData data = findPlayer(p.getUniqueId());
        if (data.onCooldown(spell.getName())){
            // on cooldown
            p.playSound(p, Sound.ITEM_LODESTONE_COMPASS_LOCK, 1, 1);
            return;
        }
        if (data.getLevel() < spell.getRequiredLevel()){
            // level too small
            p.sendMessage(NotMagic.getInstance().getPrefix() + noLevelMsg + ChatColor.GRAY + "(" + spell.getRequiredLevel() + ")");
            return;
        }
        if (data.getMp() < spell.getMpCost()){
            // not enough mana
            p.playSound(p, Sound.ITEM_DYE_USE, 1, 1);
            return;
        }
        // finally, able to cast the spell
        data.useMP(spell.getMpCost());
        data.addCooldown(spell.getName(),spell.getCooldown());

        for (Entity entity : p.getWorld().getNearbyEntities(p.getLocation(), 5,5,5)){
            if (entity instanceof LivingEntity){
                ((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 1,0));
                entity.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, ((LivingEntity) entity).getEyeLocation().add(0,1,0), 5, .25,.5,.25);
            }
        }
    }
    public void strength(Player p){
        Spell spell = spells.get(3);
        PlayerData data = findPlayer(p.getUniqueId());
        if (data.onCooldown(spell.getName())){
            // on cooldown
            p.playSound(p, Sound.ITEM_LODESTONE_COMPASS_LOCK, 1, 1);
            return;
        }
        if (data.getLevel() < spell.getRequiredLevel()){
            // level too small
            p.sendMessage(NotMagic.getInstance().getPrefix() + noLevelMsg + ChatColor.GRAY + "(" + spell.getRequiredLevel() + ")");
            return;
        }
        if (data.getMp() < spell.getMpCost()){
            // not enough mana
            p.playSound(p, Sound.ITEM_DYE_USE, 1, 1);
            return;
        }
        // finally, able to cast the spell
        data.useMP(spell.getMpCost());
        data.addCooldown(spell.getName(),spell.getCooldown());

        for (Entity entity : p.getWorld().getNearbyEntities(p.getLocation(), 5,5,5)){
            if (entity instanceof LivingEntity){
                ((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 600,0));
                entity.getWorld().spawnParticle(Particle.VILLAGER_ANGRY, ((LivingEntity) entity).getEyeLocation().add(0,1,0), 5, .25,.5,.25);
            }
        }
    }

    public List<Spell> getSpells() {
        return spells;
    }
    public void burst(Player p) {
        Spell spell = spells.get(4);
        PlayerData data = findPlayer(p.getUniqueId());
        if (data.onCooldown(spell.getName())){
            // on cooldown
            p.playSound(p, Sound.ITEM_LODESTONE_COMPASS_LOCK, 1, 1);
            return;
        }
        if (data.getLevel() < spell.getRequiredLevel()){
            // level too small
            p.sendMessage(NotMagic.getInstance().getPrefix() + noLevelMsg + ChatColor.GRAY + "(" + spell.getRequiredLevel() + ")");
            return;
        }
        if (data.getMp() < spell.getMpCost()){
            // not enough mana
            p.playSound(p, Sound.ITEM_DYE_USE, 1, 1);
            return;
        }
        // finally, able to cast the spell
        data.useMP(spell.getMpCost());
        data.addCooldown(spell.getName(),spell.getCooldown());

        Vector v = new Vector(0 - p.getLocation().getDirection().getX(), 0.7, 0 - p.getLocation().getDirection().getZ());
        Vector v2 = new Vector(p.getLocation().getDirection().getX(), -0.4, p.getLocation().getDirection().getZ());
        Location start = p.getLocation().add(v.multiply(12));
        new BukkitRunnable(){
            int i = 0;
            @Override
            public void run() {
                if (i < 6)
                    for (int y = 0; y < 20; y++) {
                        Location loc = new Location(start.getWorld(), start.getX() + (rand.nextInt(7) - 3), start.getY() + (rand.nextInt(3) - 1), start.getZ() + (rand.nextInt(7) - 3));
                        Objects.requireNonNull(loc.getWorld()).spawnParticle(Particle.CLOUD, loc, 0, v2.getX(), v2.getY(), v2.getZ());
                    }
                double radius = 4D;
                List<Entity> near = Objects.requireNonNull(start.getWorld()).getEntities();
                for (Entity b : near) {
                    if (b.getLocation().distance(start) <= radius) {
                        if (b instanceof LivingEntity) {
                            if (b != p) {

                                b.setVelocity(new Vector(b.getVelocity().getX(), 1, b.getVelocity().getZ()));
                                for (int y = 0; y < 10; y++) {
                                    Location loc = new Location(start.getWorld(), start.getX() + (rand.nextInt(3) - 1), start.getY() + (rand.nextInt(3) - 1), start.getZ() + (rand.nextInt(3) - 1));
                                    Objects.requireNonNull(loc.getWorld()).spawnParticle(Particle.CLOUD, loc, 0, b.getVelocity().getX(), b.getVelocity().getY(), b.getVelocity().getZ());
                                }

                            }
                        }
                    }

                }
                start.add(v2);
                if (i == 20){
                    this.cancel();
                }
                i++;
            }
        }.runTaskTimer(plugin, 0, 1L);

    }
    public void locate(Player p){
        Spell spell = spells.get(6);
        PlayerData data = findPlayer(p.getUniqueId());
        if (data.onCooldown(spell.getName())){
            // on cooldown
            p.playSound(p, Sound.ITEM_LODESTONE_COMPASS_LOCK, 1, 1);
            return;
        }
        if (data.getLevel() < spell.getRequiredLevel()){
            // level too small
            p.sendMessage(NotMagic.getInstance().getPrefix() + noLevelMsg + ChatColor.GRAY + "(" + spell.getRequiredLevel() + ")");
            return;
        }
        if (data.getMp() < spell.getMpCost()){
            // not enough mana
            p.playSound(p, Sound.ITEM_DYE_USE, 1, 1);
            return;
        }
        // finally, able to cast the spell
        data.useMP(spell.getMpCost());
        data.addCooldown(spell.getName(),spell.getCooldown());

        Player closest = null;
        List<Entity> near = Objects.requireNonNull(p.getWorld()).getEntities();
        for (Entity b : near) {
            if (b instanceof Player) {
                if (b != p) {
                    if (closest != null) {
                        if (b.getLocation().distance(p.getLocation()) < closest.getLocation().distance(p.getLocation())) {
                            closest = (Player) b;
                        }
                    } else {
                        closest = (Player) b;
                    }
                }
            }
        }
        if (closest != null) {
            Player finalClosest = closest;
            new BukkitRunnable(){
                int timer = 0;
                @Override
                public void run() {
                    for (int i = 0; i < 5; i++){
                        final Vector v = p.getLocation().toVector().subtract(finalClosest.getLocation().toVector()).normalize();
                        final Vector v1 = new Vector(0 - v.getX(), 0 - v.getY(), 0 - v.getZ()).normalize();
                        final Particle.DustOptions dustOptions = new Particle.DustOptions(org.bukkit.Color.fromRGB(3, 252, 194), 1);
                        final Location location = new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY() + 1, p.getLocation().getZ());
                        Location loc = location.add(v1.multiply(i+1));
                        Objects.requireNonNull(loc.getWorld()).spawnParticle(Particle.REDSTONE, loc, 1, dustOptions);
                    }
                    if (timer == 8){
                        this.cancel();
                    }
                    timer++;
                }
            }.runTaskTimer(plugin, 0, 3L);
        } else {
            p.sendMessage(NotMagic.getInstance().getPrefix() + net.md_5.bungee.api.ChatColor.DARK_GREEN + "There are no players in your world!");
        }
    }
    public void teleport(Player p){
        Spell spell = spells.get(7);
        PlayerData data = findPlayer(p.getUniqueId());
        if (data.onCooldown(spell.getName())){
            // on cooldown
            p.playSound(p, Sound.ITEM_LODESTONE_COMPASS_LOCK, 1, 1);
            return;
        }
        if (data.getLevel() < spell.getRequiredLevel()){
            // level too small
            p.sendMessage(NotMagic.getInstance().getPrefix() + noLevelMsg + ChatColor.GRAY + "(" + spell.getRequiredLevel() + ")");
            return;
        }
        if (data.getMp() < spell.getMpCost()){
            // not enough mana
            p.playSound(p, Sound.ITEM_DYE_USE, 1, 1);
            return;
        }
        // finally, able to cast the spell
        data.useMP(spell.getMpCost());
        data.addCooldown(spell.getName(),spell.getCooldown());

        Block target = p.getTargetBlock(null, 10);
        Location l = new Location(p.getTargetBlock(null, 10).getLocation().getWorld(), target.getLocation().getX(), target.getLocation().getY(), target.getLocation().getZ(), p.getLocation().getYaw(), p.getLocation().getPitch());
        p.getWorld().spawnParticle(Particle.WARPED_SPORE, p.getLocation(), 10);
        Objects.requireNonNull(l.getWorld()).spawnParticle(Particle.WARPED_SPORE, l, 10);
        if (!target.getType().isAir()){
            l.subtract(p.getLocation().getDirection().normalize());
        }
        p.teleport(l);
    }
    public void ironWallAttack(Player p){
        Spell spell = spells.get(8);
        PlayerData data = findPlayer(p.getUniqueId());
        if (data.onCooldown(spell.getName())){
            // on cooldown
            p.playSound(p, Sound.ITEM_LODESTONE_COMPASS_LOCK, 1, 1);
            return;
        }
        if (data.getLevel() < spell.getRequiredLevel()){
            // level too small
            p.sendMessage(NotMagic.getInstance().getPrefix() + noLevelMsg + ChatColor.GRAY + "(" + spell.getRequiredLevel() + ")");
            return;
        }
        if (data.getMp() < spell.getMpCost()){
            // not enough mana
            p.playSound(p, Sound.ITEM_DYE_USE, 1, 1);
            return;
        }
        // finally, able to cast the spell
        data.useMP(spell.getMpCost());
        data.addCooldown(spell.getName(),spell.getCooldown());

        IronGolem golem = p.getWorld().spawn(p.getEyeLocation().add(p.getEyeLocation().getDirection()), IronGolem.class);
        golem.setAware(false);
        golem.setVelocity(p.getLocation().getDirection().multiply(1.5));
        new BukkitRunnable(){
            int timer = 0;
            @Override
            public void run() {
                double radius = 4D;
                List<Entity> near = Objects.requireNonNull(golem.getWorld()).getEntities();
                for (Entity b : near) {
                    if (b.getLocation().distance(golem.getLocation().add(0,1,0)) <= radius) {
                        if (b instanceof LivingEntity) {
                            if (b != p && b != golem){

                                ((LivingEntity) b).damage(15, p);

                                golem.getWorld().spawnParticle(Particle.FLASH, golem.getLocation(), 3);
                                golem.getWorld().playSound(golem.getLocation(), Sound.ENTITY_IRON_GOLEM_REPAIR,1,1);
                                this.cancel();
                                golem.remove();
                            }
                        }
                    }
                }
                if (golem.getVelocity().equals(new Vector(0,0,0))){
                    this.cancel();
                    golem.getWorld().spawnParticle(Particle.FLASH, golem.getLocation(), 3);
                    golem.remove();
                }
                timer++;
                if (timer > 25){
                    this.cancel();
                    golem.getWorld().spawnParticle(Particle.FLASH, golem.getLocation(), 3);
                    golem.remove();
                }
            }
        }.runTaskTimer(plugin, 0L, 3);
    }
    public void lifeSteal(Player p) {
        Spell spell = spells.get(9);
        PlayerData data = findPlayer(p.getUniqueId());
        if (data.onCooldown(spell.getName())){
            // on cooldown
            p.playSound(p, Sound.ITEM_LODESTONE_COMPASS_LOCK, 1, 1);
            return;
        }
        if (data.getLevel() < spell.getRequiredLevel()){
            // level too small
            p.sendMessage(NotMagic.getInstance().getPrefix() + noLevelMsg + ChatColor.GRAY + "(" + spell.getRequiredLevel() + ")");
            return;
        }
        if (data.getMp() < spell.getMpCost()){
            // not enough mana
            p.playSound(p, Sound.ITEM_DYE_USE, 1, 1);
            return;
        }
        // finally, able to cast the spell
        data.useMP(spell.getMpCost());
        data.addCooldown(spell.getName(),spell.getCooldown());

        LivingEntity target = null;
        for (Entity e : p.getWorld().getNearbyEntities(p.getLocation(), 20, 20, 20)) {
            if (e instanceof LivingEntity)
                if (getLookingAt(p, (LivingEntity) e)) {
                    target = (LivingEntity) e;
                    break;
                }
        }
        if (target == null)
            return;


        LivingEntity finalTarget = target;
        new BukkitRunnable() {
            int timer = 0;

            @Override
            public void run() {
                if (!(getLookingAt(p, finalTarget))) {
                    if (timer < 8) {
                        this.cancel();
                        p.getWorld().spawnParticle(Particle.SMOKE_NORMAL, p.getEyeLocation(), 10);
                        finalTarget.getWorld().spawnParticle(Particle.SMOKE_NORMAL, finalTarget.getLocation(), 10);
                        p.playSound(p.getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 1, 1);
                        return;
                    }
                }

                Particle.DustOptions dustOptions = null;
                if (timer < 2) {
                    dustOptions = new Particle.DustOptions(org.bukkit.Color.fromRGB(32, 240, 10), 1);
                } else if (timer < 4) {
                    dustOptions = new Particle.DustOptions(org.bukkit.Color.fromRGB(217, 240, 10), 1);
                } else if (timer < 6) {
                    dustOptions = new Particle.DustOptions(org.bukkit.Color.fromRGB(240, 148, 10), 1);
                } else if (timer < 8) {
                    dustOptions = new Particle.DustOptions(org.bukkit.Color.fromRGB(237, 74, 52), 1);
                } else {
                    dustOptions = new Particle.DustOptions(org.bukkit.Color.fromRGB(219, 15, 124), 1);
                }
                Vector between = new Vector(0, 0, 0).subtract(p.getEyeLocation().toVector().subtract(finalTarget.getEyeLocation().toVector()));
                p.getWorld().spawnParticle(Particle.REDSTONE, p.getEyeLocation().add(between.multiply(0.2)), 1, dustOptions);
                p.getWorld().spawnParticle(Particle.REDSTONE, p.getEyeLocation().add(between.multiply(0.4)), 1, dustOptions);
                p.getWorld().spawnParticle(Particle.REDSTONE, p.getEyeLocation().add(between.multiply(0.6)), 1, dustOptions);
                p.getWorld().spawnParticle(Particle.REDSTONE, p.getEyeLocation().add(between.multiply(0.8)), 1, dustOptions);

                if (timer > 7 && timer < 12) {
                    if (!(finalTarget.isDead())) {
                        finalTarget.damage(2, p);
                        if (p.getHealth() + 2 <= p.getMaxHealth()) {
                            p.setHealth(p.getHealth() + 2);
                        }
                        p.setVelocity(p.getVelocity().add(new Vector(0, 0, 0).subtract(p.getEyeLocation().toVector().subtract(finalTarget.getEyeLocation().toVector())).normalize()));
                    }
                }
                if (timer == 12) {
                    this.cancel();

                }
                timer++;

            }
        }.runTaskTimer(plugin, 0, 10);

    }
    private boolean getLookingAt(Player player, LivingEntity player1)
    {
        Location eye = player.getEyeLocation();
        Vector toEntity = player1.getEyeLocation().toVector().subtract(eye.toVector());
        double dot = toEntity.normalize().dot(eye.getDirection());

        return dot > 0.99D;
    }
    public void smite(Player p){
        Spell spell = spells.get(10);
        PlayerData data = findPlayer(p.getUniqueId());
        if (data.onCooldown(spell.getName())){
            // on cooldown
            p.playSound(p, Sound.ITEM_LODESTONE_COMPASS_LOCK, 1, 1);
            return;
        }
        if (data.getLevel() < spell.getRequiredLevel()){
            // level too small
            p.sendMessage(NotMagic.getInstance().getPrefix() + noLevelMsg + ChatColor.GRAY + "(" + spell.getRequiredLevel() + ")");
            return;
        }
        if (data.getMp() < spell.getMpCost()){
            // not enough mana
            p.playSound(p, Sound.ITEM_DYE_USE, 1, 1);
            return;
        }
        // finally, able to cast the spell
        data.useMP(spell.getMpCost());
        data.addCooldown(spell.getName(),spell.getCooldown());

        Location target = p.getTargetBlock(null, 50).getLocation();
        LightningStrike lightningStrike = p.getWorld().strikeLightning(target);
        lightningStrike.setMetadata("magic", new FixedMetadataValue(plugin, true));
        lightningStrike.setMetadata(p.getUniqueId().toString(), new FixedMetadataValue(plugin, true));
    }
    public void fireball(Player p){
        Spell spell = spells.get(11);
        PlayerData data = findPlayer(p.getUniqueId());
        if (data.onCooldown(spell.getName())){
            // on cooldown
            p.playSound(p, Sound.ITEM_LODESTONE_COMPASS_LOCK, 1, 1);
            return;
        }
        if (data.getLevel() < spell.getRequiredLevel()){
            // level too small
            p.sendMessage(NotMagic.getInstance().getPrefix() + noLevelMsg + ChatColor.GRAY + "(" + spell.getRequiredLevel() + ")");
            return;
        }
        if (data.getMp() < spell.getMpCost()){
            // not enough mana
            p.playSound(p, Sound.ITEM_DYE_USE, 1, 1);
            return;
        }
        // finally, able to cast the spell
        data.useMP(spell.getMpCost());
        data.addCooldown(spell.getName(),spell.getCooldown());

        Fireball fireball = p.launchProjectile(Fireball.class);
        fireball.setMetadata("magic", new FixedMetadataValue(plugin, true));
        fireball.setMetadata(p.getUniqueId().toString(), new FixedMetadataValue(plugin,true));
        fireball.setShooter(p);
    }
    public void iceShards(Player p) {
        Spell spell = spells.get(12);
        PlayerData data = findPlayer(p.getUniqueId());
        if (data.onCooldown(spell.getName())){
            // on cooldown
            p.playSound(p, Sound.ITEM_LODESTONE_COMPASS_LOCK, 1, 1);
            return;
        }
        if (data.getLevel() < spell.getRequiredLevel()){
            // level too small
            p.sendMessage(NotMagic.getInstance().getPrefix() + noLevelMsg + ChatColor.GRAY + "(" + spell.getRequiredLevel() + ")");
            return;
        }
        if (data.getMp() < spell.getMpCost()){
            // not enough mana
            p.playSound(p, Sound.ITEM_DYE_USE, 1, 1);
            return;
        }
        // finally, able to cast the spell
        data.useMP(spell.getMpCost());
        data.addCooldown(spell.getName(),spell.getCooldown());

        Particle.DustOptions dustOptions = new Particle.DustOptions(org.bukkit.Color.fromRGB(10, 50, 200), 1);
        Location front = p.getEyeLocation().add(p.getLocation().getDirection().multiply(1.3));
        p.getWorld().spawnParticle(Particle.REDSTONE, front, 1, dustOptions);
        Vector d = p.getLocation().getDirection();
        new BukkitRunnable() {
            int timer = 0;

            @Override
            public void run() {
                Location loc = front.add(d.multiply(1 + (timer / 10)));
                p.getWorld().spawnParticle(Particle.REDSTONE, loc, 1, dustOptions);
                timer++;
                if (loc.getBlock().getType() != Material.AIR) {
                    this.cancel();
                    return;
                }
                loc = front.add(d.multiply(1 + (timer / 10)));
                p.getWorld().spawnParticle(Particle.REDSTONE, loc, 1, dustOptions);
                timer++;
                double radius = 2D;
                List<Entity> near = Objects.requireNonNull(loc.getWorld()).getEntities();
                for (Entity e : near) {
                    if (e.getLocation().distance(loc) <= radius) {
                        if (e instanceof LivingEntity) {
                            if (e != p) {

                                ((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.SLOW,60,1));
                                ((LivingEntity) e).damage(20, p);

                            }
                        }
                    }
                }
                if (loc.getBlock().getType() != Material.AIR) {
                    this.cancel();
                }
                if (timer == 16) {
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 1L);
    }
}
