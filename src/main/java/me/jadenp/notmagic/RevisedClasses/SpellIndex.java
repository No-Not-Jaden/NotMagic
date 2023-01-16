package me.jadenp.notmagic.RevisedClasses;

import me.jadenp.notmagic.SpellWorkshop.NotCallback;
import me.jadenp.notmagic.SpellWorkshop.WorkshopSpell;
import org.bukkit.*;
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
     * Add a method in this class for the spell that accepts a Player parameter
     * go to Magic.java and add an if statement to check for the spell name if it is a main spell
     */

    private Plugin plugin;
    private Magic magic;
    Random rand = new Random();
    Items items = new Items();
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
        spells.add(new Spell("Burn", 3, 0,15, 1, new ArrayList<>(Arrays.asList("Start", "Up")), items.data("SBBurn"), plugin, false));

        spells.add(new Spell("Zap", 9, 0,100, 1, new ArrayList<>(Arrays.asList("Start", "RightDown", "LeftDown", "RightDown")), items.data("SBZap"), plugin, false));

        spells.add(new Spell("Heal", 15, 2,200, 1, new ArrayList<>(Arrays.asList("Start", "Left", "Right", "Up", "Down", "Right", "Left", "Down", "Up")), items.data("SBHeal"), plugin, false));

        spells.add(new Spell("Strength", 20, 2,300, 1, new ArrayList<>(Arrays.asList("Start", "Up", "Left", "RightDown", "Right")), items.data("SBStrength"), plugin, false));

        spells.add(new Spell("Burst", 15, 2,200, 1, new ArrayList<>(Arrays.asList("Start", "LeftUp", "Right", "LeftDown", "Down")), items.data("SBBurst"), plugin, false));

        spells.add(new Spell("Snipe", 30, 60, 200,1, new ArrayList<>(Arrays.asList("Start", "LeftUp", "RightDown", "RightUp", "LeftDown")), items.data("SBSnipe"), plugin, false));

        spells.add(new Spell("Locate", 50, 15, 300, 1, new ArrayList<>(Arrays.asList("Start", "RightUp", "RightDown", "LeftDown", "Down")), items.data("SBLocate"), plugin, false));
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
                if (querySpell(pattern).equalsIgnoreCase("unknown")) {
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

    public String querySpell(ArrayList<String> pattern){
        if (pattern == null) {
            if (debug){
                Bukkit.getLogger().info("Null spell pattern");
            }
            return "unknown";
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
                    return spell.getName();
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
                    return spell.getName();
                }
            }
        }
        return "unknown";
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
                }
            }
            } else{
                // custom spell
                CustomSpell customSpell = queryCustomSpell(spell);
                if (customSpell != null) {
                    List<String> actions = customSpell.getActions();

                } else {
                    // not a spell
                }
            }

    }

    public void burn(Player p) {
        Spell spell = spells.get(0);
        PlayerData data = findPlayer(p.getUniqueId());
        if (data.onCooldown(spell.getName())){
            // on cooldown
            p.sendMessage("on cooldown");
            return;
        }
        if (data.getLevel() < spell.getRequiredLevel()){
            // level too small
            p.sendMessage("no level");
            return;
        }
        if (data.getMp() < spell.getMpCost()){
            // not enough mana
            p.sendMessage("no mana");
            return;
        }
        // finally, able to cast the spell
        data.useMP(spell.getMpCost());
        data.addCooldown(spell.getName(),spell.getCooldown());
        p.sendMessage("cast burn yay ");


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
            p.sendMessage("on cooldown");
            return;
        }
        if (data.getLevel() < spell.getRequiredLevel()){
            // level too small
            p.sendMessage("no level");
            return;
        }
        if (data.getMp() < spell.getMpCost()){
            // not enough mana
            p.sendMessage("no mana");
            return;
        }
        // finally, able to cast the spell
        data.useMP(spell.getMpCost());
        p.sendMessage("cast zap yay ");

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
        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, Integer.MAX_VALUE));
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
        for (Entity entity : p.getWorld().getNearbyEntities(p.getLocation(), 5,5,5)){
            if (entity instanceof LivingEntity){
                ((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 1,0));
                entity.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, ((LivingEntity) entity).getEyeLocation().add(0,1,0), 5, .25,.5,.25);
            }
        }
    }
    public void strength(Player p){
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
            p.sendMessage(magic.eventClass.prefix + net.md_5.bungee.api.ChatColor.DARK_GREEN + "There are no players in your world!");
        }
    }
}
