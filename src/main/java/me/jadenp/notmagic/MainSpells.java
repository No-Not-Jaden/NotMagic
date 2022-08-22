package me.jadenp.notmagic;


import org.bukkit.*;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.awt.*;
import java.awt.Color;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Random;


public class MainSpells {

    private final String prefix = net.md_5.bungee.api.ChatColor.GRAY + "[" + net.md_5.bungee.api.ChatColor.of(new Color(26, 194, 232)) + "Not" + net.md_5.bungee.api.ChatColor.of(new Color(232, 26, 225)) + "Magic" + net.md_5.bungee.api.ChatColor.GRAY + "] " + net.md_5.bungee.api.ChatColor.DARK_GRAY + "» ";
    private final Plugin plugin;
    Random rand = new Random();
    public MainSpells(Plugin plug) {
        this.plugin = plug;
    }

    public void snipe1(Player p) {
                Arrow arrow = p.launchProjectile(Arrow.class);
                arrow.setShooter(p);
                arrow.setMetadata("magic", new FixedMetadataValue(plugin, true));
    }

    public void snipe2(Player p) {
                Arrow arrow = p.launchProjectile(Arrow.class);
                arrow.setShooter(p);
                arrow.setVelocity(arrow.getVelocity().multiply(2));
                arrow.setColor(org.bukkit.Color.BLACK);
                arrow.setMetadata("magic", new FixedMetadataValue(plugin, true));
                arrow.setBasePotionData(new PotionData(PotionType.POISON));
                arrow.addCustomEffect(new PotionEffect(PotionEffectType.WITHER, 60, 1), true);
                arrow.setDamage(3);
    }

    public void burn1(Player p) {
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

    public void burn2(Player p){
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
                p.getWorld().spawnParticle(Particle.DRIP_LAVA, loc, 1);
                timer++;
                double radius = 2D;
                List<Entity> near = Objects.requireNonNull(loc.getWorld()).getEntities();
                for (Entity e : near) {
                    if (e.getLocation().distance(loc) <= radius) {
                        if (e instanceof LivingEntity) {
                            if (e != p) {
                                    e.setFireTicks(1000);
                                    ((LivingEntity) e).damage(15, p);

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
                p.sendMessage(prefix + ChatColor.DARK_GREEN + "There are no players in your world!");
            }
    }

    public void teleport(Player p){
        if (p.getTargetBlock(null, 10).getType().isAir()){
            p.getWorld().spawnParticle(Particle.WARPED_SPORE, p.getLocation(), 10);
            Location l = new Location(p.getTargetBlock(null, 10).getLocation().getWorld(), p.getTargetBlock(null, 10).getLocation().getX(), p.getTargetBlock(null, 10).getLocation().getY(), p.getTargetBlock(null, 10).getLocation().getZ(), p.getLocation().getYaw(), p.getLocation().getPitch());
            p.teleport(l);
            Objects.requireNonNull(l.getWorld()).spawnParticle(Particle.WARPED_SPORE, l, 10);
        } else {
            p.sendMessage(prefix + ChatColor.DARK_GREEN + "You cannot teleport through blocks!");
        }
    }

    public void ironWallAttack(Player p){
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
    public void lifeSteal(Player p) throws ReflectiveOperationException {
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
        Location target = p.getTargetBlock(null, 50).getLocation();
        LightningStrike lightningStrike = p.getWorld().strikeLightning(target);
        lightningStrike.setMetadata("magic", new FixedMetadataValue(plugin, true));
        lightningStrike.setMetadata(p.getUniqueId().toString(), new FixedMetadataValue(plugin, true));
    }
    public void fireball(Player p){
        Fireball fireball = p.launchProjectile(Fireball.class);
        fireball.setMetadata("magic", new FixedMetadataValue(plugin, true));
        fireball.setMetadata(p.getUniqueId().toString(), new FixedMetadataValue(plugin,true));
        fireball.setShooter(p);
    }
    public void iceShards(Player p) {
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

