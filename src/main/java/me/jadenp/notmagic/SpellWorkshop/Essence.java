package me.jadenp.notmagic.SpellWorkshop;

import me.jadenp.notmagic.NotMagic;
import me.jadenp.notmagic.RevisedClasses.Items;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;
import org.checkerframework.checker.units.qual.C;


import javax.annotation.Nonnull;
import java.util.*;

public enum Essence {
    /**
     * don't forget to add more names to SpellNames.java !
     */
    EMPTY(0,0,0, Color.fromRGB(187, 189, 187)),
    FIRE(8, 6, 3, Color.fromRGB(194, 47, 54)),
    EARTH(5, 8, 2, Color.fromRGB(94, 69, 42)),
    WATER(7, 5, 1, Color.fromRGB(7, 106, 186)),
    WIND(9,7,2, Color.fromRGB(199, 255, 231)),
    ELECTRICITY(10,3,3, Color.fromRGB(220, 222, 93)),
    ICE(4,10,1, Color.fromRGB(141, 181, 235)),
    POISON(3,9,2, Color.fromRGB(40, 77, 42)),
    LIVING(1,3,1, Color.fromRGB(202, 119, 217)),
    SPECTRAL(2,4,3, Color.fromRGB(146, 90, 214)),
    BARRIER(0,4,1, Color.fromRGB(82, 2, 2));

    // more info about elements of essence in CraftingInterface.java
    private final int potentialPower;
    private final int areaEffectPower;
    private final int intensityPower;
    private final NotMagic notMagic;
    private final Color color; // what color the essence relates to
    Essence(int potentialPower, int areaEffectPower, int intensityPower, Color color){
        this.potentialPower = potentialPower;
        this.areaEffectPower = areaEffectPower;
        this.intensityPower = intensityPower;
        this.color = color;
        notMagic = NotMagic.getInstance();
    }

    public Color getColor() {
        return color;
    }

    // this callback returns locations that are relative to a predefined path to the target
    public void potentialResults(Location point, Location start, final NotCallback callback) {
        Essence essence = this;
        Bukkit.getScheduler().runTaskAsynchronously(notMagic, new Runnable() {
            @Override
            public void run() {
        List<Location> locations = new ArrayList<>();
        if (essence == Essence.FIRE) {
            // Sin wave to target
            Location location = new Location(point.getWorld(), point.getX() + Math.sin(point.getX() - start.getX()), point.getY() + Math.sin(point.getY() - start.getY()), point.getZ() + Math.sin(point.getZ() - start.getZ()));
            locations.add(location);
            if (location.getWorld() != null && location.getChunk().isLoaded())
                Bukkit.getScheduler().runTask(notMagic, () -> location.getWorld().spawnParticle(Particle.DRIP_LAVA, location,1));
        } else if (essence == Essence.EARTH){
            // rumble - move in a random direction away from the path every once in a while
            if ((int) (Math.random() * 3) == 0){
                Vector direction = point.toVector().subtract(start.toVector()).normalize();
                Vector randomPoint = direction.clone().rotateAroundY(Math.PI / 2);
                randomPoint.rotateAroundAxis(randomPoint, Math.random() * 2 * Math.PI).normalize().multiply(Math.random() * potentialPower);
                Location location = new Location(point.getWorld(), point.getX() + randomPoint.getX(), point.getY() + randomPoint.getY(), point.getZ() + randomPoint.getZ());
                locations.add(location);
                if (location.getWorld() != null && location.getChunk().isLoaded())
                    Bukkit.getScheduler().runTask(notMagic, () -> location.getWorld().spawnParticle(Particle.ASH,location,3));
            } else {
                if (point.getWorld() != null && point.getChunk().isLoaded())
                    Bukkit.getScheduler().runTask(notMagic, () -> point.getWorld().spawnParticle(Particle.ASH, point, 3));
            }
        } else if (essence == Essence.WATER){
            // random splash in front
            Vector direction = point.toVector().subtract(start.toVector()).normalize();
            Location front = point.clone().add(direction);
            front.add(Math.random() * 2 - 1, Math.random() * 2 - 1, Math.random() * 2 - 1);
            locations.add(front);
            if (point.getWorld() != null && point.getChunk().isLoaded()) {
                for (int i = 0; i < 5; i++) {
                    Location changedFront = front.clone().add(Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5);
                    Vector splashDirection = changedFront.toVector().subtract(point.toVector()).normalize();
                    Bukkit.getScheduler().runTask(notMagic, () -> point.getWorld().spawnParticle(Particle.WATER_SPLASH, point, 0, splashDirection.getX(), splashDirection.getY(), splashDirection.getZ()));
                }
            }
        } else if (essence == Essence.WIND){
            // horizontal bursts slightly away from center
            Vector direction = point.toVector().subtract(start.toVector()).normalize();
            Vector up = new Vector(0,1,0);
            Vector horizontal1 = direction.crossProduct(up).normalize();
            Vector horizontal2 = new Vector(0,0,0).subtract(horizontal1);
            Location[] locations1 = new Location[5];
            locations1[0] = point;
            locations1[1] = point.clone().add(horizontal1);
            locations1[2] = point.clone().add(horizontal1.multiply(2));
            locations1[3] = point.clone().add(horizontal2);
            locations1[4] = point.clone().add(horizontal2.multiply(2));

            for (Location location : locations1) {
                Vector vector = rotateVectorCC(direction, up, ((Math.random() * 30) - 15) / 0.0174533);
                if (point.getWorld() != null && point.getChunk().isLoaded()) {
                    Bukkit.getScheduler().runTask(notMagic, () -> point.getWorld().spawnParticle(Particle.CLOUD, location, 0, vector.getX(), vector.getY(), vector.getZ()));
                }
                locations.add(location.add(vector));
            }
        } else if (essence == Essence.ELECTRICITY){
            // get distance from start and use it to get the a and b change perpendicular from direction
            Vector direction = point.toVector().subtract(start.toVector());
            double distance = direction.length();
            int dir = distance % 5 < 2.5 ? -1 : 1;
            double multiplier = distance % 2.5;
            Vector cross = direction.normalize().crossProduct(new Vector(0,1,0));
            point.add(new Vector(0,1,0).multiply(multiplier * dir));
            point.add(cross.multiply(multiplier * dir));

            if (point.getWorld() != null && point.getChunk().isLoaded()) {
                Bukkit.getScheduler().runTask(notMagic, () -> point.getWorld().spawnParticle(Particle.FLASH, point, 1));
            }

            locations.add(point);
        } else if (essence == Essence.ICE){
            // spread out snow flakes
            for (int i = 0; i < 15; i++) {
                Location location = point.clone().add(Math.random() * 4 - 2, Math.random() * 4 - 2, Math.random() * 4 - 2);
                locations.add(location);
            }
            Bukkit.getScheduler().runTask(notMagic, () -> {
                for (Location location :
                        locations) {
                    if (location.getWorld() != null && location.getChunk().isLoaded()) {
                        location.getWorld().spawnParticle(Particle.SNOWFLAKE, location, 1);
                    }
                }
            });

        } else if (essence == Essence.POISON) {

        } else if (essence == Essence.LIVING){

        } else if (essence == Essence.SPECTRAL){

        } else if (essence == Essence.BARRIER){

        }
        else {
            locations.add(point);
        }
        Bukkit.getScheduler().runTask(notMagic, () -> callback.onCalcFinish(locations));
        //return locations;
            }
        });
    }

    public void areaEffectResults(Location center, int amount1, NotCallback callback){
        Essence essence = this;
        Bukkit.getScheduler().runTaskAsynchronously(notMagic, new Runnable() {
            int amount = amount1;
            @Override
            public void run() {
        List<Location> locations = new ArrayList<>();
        while (amount > 0) {
            if (essence == Essence.FIRE) {
                // how many points clustered together? 3-7
                int points = (int) (Math.random() * 5 + 3);
                if (points > amount)
                    points = amount;
                // cluster location
                Location cluster = new Location(center.getWorld(), // more ovular shape
                        center.getX() + Math.random() * 2 * areaEffectPower - areaEffectPower,
                        center.getY() + Math.random() * areaEffectPower - ((double) areaEffectPower / 2),
                        center.getZ() + Math.random() * 2 * areaEffectPower - areaEffectPower);
                locations.add(cluster);
                amount -= 1;
                for (int i = 0; i < points - 1; i++){
                    // location within 1 block from cluster point
                    locations.add(new Location(cluster.getWorld(),
                            cluster.getX() + Math.random() * 2 - 1,
                            cluster.getY() + Math.random() * 2 - 1,
                            cluster.getZ() + Math.random() * 2 - 1));
                    amount -= 1;
                }
            } else if (essence == Essence.EARTH){
                Location crack = new Location(center.getWorld(), center.getX() + Math.random() * 2 * areaEffectPower - areaEffectPower, center.getY(), center.getZ() + Math.random() * 2 * areaEffectPower - areaEffectPower);
                    // how many blocks below it looks for a point
                    Block block = crack.getBlock();
                    for (int i = 0; i < 20; i++){
                        if (block.getRelative(BlockFace.DOWN).getType().isSolid()){
                            locations.add(crack);
                            break;
                        }
                        block = block.getRelative(BlockFace.DOWN);
                    }
                    amount--;
            } else if (essence == Essence.WATER){
                double x, z;
                int radius;
                if (amount <= 8){
                    radius = 1;
                } else if (amount <= 16){
                    radius = 2;
                } else if (amount <= 24){
                    radius = 3;
                } else if (amount <= 32){
                    radius = 4;
                } else {
                    radius = 5;
                }
                for (int i = 0; i < Math.PI * 2; i += Math.PI / 4) {
                    x = radius * Math.sin(i);
                    z = radius * Math.cos(i);
                    locations.add(new Location(center.getWorld(), center.getX() + x, center.getY(), center.getZ() + z));
                }
                break;
            }  else if (essence == Essence.WIND){
                double x;
                double z;
                float radius;
                if (amount <= 8){
                    radius = 2;
                } else if (amount <= 16){
                    radius = 4;
                } else if (amount <= 24){
                    radius = 6;
                } else if (amount <= 32){
                    radius = 8;
                } else {
                    radius = 10;
                }
                for (double i = 0; i < Math.PI * 2; i += Math.PI / 6) {
                    x = Math.cos(i) * radius;
                    z = Math.sin(i) * radius;
                    Vector direction = new Vector(x, 0, z).normalize();
                    locations.add(center.clone().add(direction));
                }
                break;
            } else if (essence == Essence.ELECTRICITY){
                for (int i = 0; i < amount1; i++){
                    locations.add(center);
                }
            } else if (essence == Essence.ICE){
                // big area
                for (int j = 0; j < amount1; j++) {
                    for (int i = 0; i < 10; i++) {
                        locations.add(center.clone().add(new Vector(Math.random() * 20 - 10, Math.random() * 10 - 5, Math.random() * 20 - 10)));
                    }
                }

            } else if (essence == Essence.POISON) {

            } else if (essence == Essence.LIVING){

            } else if (essence == Essence.SPECTRAL){

            } else if (essence == Essence.BARRIER){
                // brute forcing code? I don't know what that means
                locations.add(center.clone().add(new Vector(1,1,1)));
                locations.add(center.clone().add(new Vector(1,1,0)));
                locations.add(center.clone().add(new Vector(0,1,1)));
                locations.add(center.clone().add(new Vector(-1,1,1)));
                locations.add(center.clone().add(new Vector(-1,1,0)));
                locations.add(center.clone().add(new Vector(1,1,-1)));
                locations.add(center.clone().add(new Vector(0,1,-1)));
                locations.add(center.clone().add(new Vector(-1,1,-1)));
                locations.add(center.clone().add(new Vector(1,0,1)));
                locations.add(center.clone().add(new Vector(1,0,-1)));
                locations.add(center.clone().add(new Vector(-1,0,1)));
                locations.add(center.clone().add(new Vector(-1,0,-1)));
                locations.add(center.clone().add(new Vector(1,-1,1)));
                locations.add(center.clone().add(new Vector(1,-1,0)));
                locations.add(center.clone().add(new Vector(0,-1,1)));
                locations.add(center.clone().add(new Vector(-1,-1,1)));
                locations.add(center.clone().add(new Vector(-1,-1,0)));
                locations.add(center.clone().add(new Vector(1,-1,-1)));
                locations.add(center.clone().add(new Vector(0,-1,-1)));
                locations.add(center.clone().add(new Vector(-1,-1,-1)));
            }
            else {
                break;
            }
        }
        Bukkit.getScheduler().runTask(notMagic, () -> callback.onCalcFinish(locations));
        //return locations;
            }
        });
    }

    public void intensityResults(Location point, double damageMultiplier, Player player, Location center){

        if (this == Essence.FIRE){
            // explosion w/ flame particles
            if (point.getWorld() != null && point.getChunk().isLoaded()) {
                point.getWorld().spawnParticle(Particle.FLAME, point, 0, Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5);
                point.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, point, 1, 1, 1, 1);
            }
            Block block = point.getBlock();
            if (block.getType().isAir()){
                block.setType(Material.FIRE);
            } else {
                // try spawning falling block
                if (point.getWorld() != null && point.getChunk().isLoaded()){
                    // hopefully the fire doesn't burn the block
                    if (block.getType().isSolid()){
                        FallingBlock fallingBlock = point.getWorld().spawnFallingBlock(block.getLocation(), block.getBlockData());
                        fallingBlock.setVelocity(new Vector(Math.random() * 2 - 1, Math.random() * 10 - 5, Math.random() * 2 - 1));
                        block.setType(Material.FIRE);
                    }
                }
            }
            // damage ppl in the point block
            for (Entity entity : point.getWorld().getNearbyEntities(point,0.5,0.5,0.5)){
                if (entity instanceof LivingEntity){
                    if (isImmuneToFire(entity)){
                        ((LivingEntity) entity).damage(intensityPower * damageMultiplier / 2, player);
                    } else {
                        ((LivingEntity) entity).damage(intensityPower * damageMultiplier, player);
                    }
                }
            }
        } else if (this == Essence.EARTH){
            if (point.getWorld() != null && point.getChunk().isLoaded()) {
                BlockData dustData;
                Block block = point.getBlock();
                if (block.getType().isSolid()) {
                    dustData = block.getBlockData();
                } else if (block.getRelative(BlockFace.DOWN).getType().isSolid()) {
                    block = block.getRelative(BlockFace.DOWN);
                    dustData = block.getBlockData();
                } else {
                    dustData = Material.STONE.createBlockData();
                }
                point.getWorld().spawnParticle(Particle.BLOCK_DUST, point, 10, dustData);
                if (block.getType().isSolid()){
                    FallingBlock fallingBlock = point.getWorld().spawnFallingBlock(block.getLocation(), block.getBlockData());
                    fallingBlock.setVelocity(new Vector(Math.random() * 4 - 2, Math.random() * 20 - 10, Math.random() * 4 - 2));
                    block.setType(Material.AIR);
                }
            }
            for (Entity entity : point.getWorld().getNearbyEntities(point,1,0.5,1)){
                if (entity instanceof LivingEntity){
                    if (!entity.isOnGround()){
                        ((LivingEntity) entity).damage(intensityPower * damageMultiplier / 2, player);
                    } else {
                        ((LivingEntity) entity).damage(intensityPower * damageMultiplier, player);
                    }
                }
            }
        } else if (this == Essence.WATER){
            if (point.getWorld() != null && point.getChunk().isLoaded()) {
                Block block = point.getBlock();
                if (block.getType().isAir()){
                    block.setType(Material.WATER, true);
                    new BukkitRunnable(){
                        @Override
                        public void run() {
                            if (block.getType() == Material.WATER){
                                block.setType(Material.AIR);
                            }
                        }
                    }.runTaskLater(notMagic, (long) (100 + Math.random() * 50));
                }
            }
            for (Entity entity : point.getWorld().getNearbyEntities(point,0.5,0.5,0.5)){
                if (entity instanceof LivingEntity){
                    if (entity instanceof Player){
                        if (((Player) entity).getInventory().getArmorContents()[3] != null){
                            if (((Player) entity).getInventory().getArmorContents()[3].getType() == Material.TURTLE_HELMET){
                                continue;
                            }
                        }
                    }
                    if (isImmuneToWater(entity)){
                        ((LivingEntity) entity).damage(intensityPower * damageMultiplier / 2, player);
                        continue;
                    }
                    entity.setVelocity(entity.getVelocity().add(point.toVector().subtract(center.toVector()).normalize().multiply(5)));
                    ((LivingEntity) entity).damage(intensityPower * damageMultiplier, player);
                }
            }
        } else if (this == Essence.WIND){
            if (point.getWorld() != null && point.getChunk().isLoaded()) {
                Vector push = point.toVector().subtract(center.toVector()).add(new Vector(0,intensityPower,0)).normalize().multiply(2.5);
                point.getWorld().spawnParticle(Particle.CLOUD, center, 0, push.clone().normalize().getX(), 0, push.clone().normalize().getZ());
                for (Entity entity : point.getWorld().getNearbyEntities(point, 0.5, 0.5, 0.5)) {
                    if (entity instanceof LivingEntity){
                        if (entity instanceof Player){
                            if (((Player) entity).getInventory().getArmorContents()[2] != null){
                                if (((Player) entity).getInventory().getArmorContents()[2].getType() == Material.ELYTRA){
                                    continue;
                                }
                            }
                        }
                        if (((LivingEntity) entity).isGliding() || !entity.isOnGround()){
                            ((LivingEntity) entity).damage(intensityPower * damageMultiplier / 2, player);
                            continue;
                        }
                        entity.setVelocity(entity.getVelocity().add(push));
                        ((LivingEntity) entity).damage(intensityPower * damageMultiplier, player);
                    }
                }
            }
        } else if (this == Essence.ELECTRICITY){
            if (point.getWorld() != null && point.getChunk().isLoaded()) {
                LightningStrike strike = point.getWorld().strikeLightning(point);
                strike.setMetadata("magic", new FixedMetadataValue(NotMagic.getInstance(), intensityPower * damageMultiplier));
                strike.setMetadata("magic", new FixedMetadataValue(NotMagic.getInstance(), player.getUniqueId().toString()));
                // does damage in an event
            }
        } else if (this == Essence.ICE){
            if (point.getWorld() != null && point.getChunk().isLoaded()) {
                point.getWorld().spawnParticle(Particle.SNOWBALL,point,1);
                for (Entity entity : point.getWorld().getNearbyEntities(point, 1, 1, 1)) {
                    if (entity instanceof LivingEntity){
                        if (entity instanceof Player){
                            ItemStack[] armor = ((Player) entity).getInventory().getArmorContents();
                            if (armor[0].getType().toString().toUpperCase(Locale.ROOT).contains("LEATHER")){
                                ((LivingEntity) entity).damage(intensityPower * damageMultiplier / 3);
                                return;
                            }
                        }
                        entity.setFreezeTicks(entity.getFreezeTicks() + intensityPower);
                        ((LivingEntity) entity).damage(intensityPower * damageMultiplier);
                    }
                }
            }
        } else if (this == Essence.POISON) {

        } else if (this == Essence.LIVING){

        } else if (this == Essence.SPECTRAL){

        } else if (this == Essence.BARRIER){

        }
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
    private boolean isImmuneToFire(Entity entity){
        return (entity instanceof Blaze || entity instanceof Ghast || entity instanceof MagmaCube || entity instanceof Strider || entity instanceof WitherSkeleton || entity instanceof Zoglin || entity instanceof PigZombie || entity instanceof Warden || entity instanceof Wither || entity instanceof EnderDragon);
    }

    private boolean isImmuneToWater(Entity entity){
        return (entity instanceof Axolotl || entity instanceof Cod || entity instanceof Dolphin || entity instanceof Guardian || entity instanceof PufferFish || entity instanceof Salmon || entity instanceof Squid || entity instanceof TropicalFish || entity instanceof Turtle);
    }

    public void controlResults(Location crosshair, Location playerLoc, NotCallback callback){
        Essence essence = this;
        Bukkit.getScheduler().runTaskAsynchronously(NotMagic.getInstance(), new Runnable() {
            Location returnLoc = playerLoc;
            @Override
            public void run() {
                if (essence == Essence.FIRE){
                    for (int x = -10; x < 10; x++){
                        for (int y = -5; y < 5; y++){
                            for (int z = -10; z < 10; z++){
                                Block block = crosshair.getBlock().getRelative(x + ((int) Math.signum(x) * 20), y, z + ((int) Math.signum(z) * 20));
                                if (block.getType() == Material.FIRE || block.getType() == Material.LAVA){
                                    returnLoc = block.getLocation();
                                    break;
                                }
                            }
                            if (returnLoc.equals(playerLoc)){
                                break;
                            }
                        }
                        if (returnLoc.equals(playerLoc)){
                            break;
                        }
                    }
                    // try to find an air block 20 < loc < 30
                    for (int i = 0; i < 15; i++){
                        Location location = crosshair.getBlock().getRelative(
                                        (int) ((((int) (Math.random() * 2)) - 1) * ((Math.random() * 10) + 20)),
                                        (int) ((((int) (Math.random() * 2)) - 1) * ((Math.random() * 5))),
                                        (int) ((((int) (Math.random() * 2)) - 1) * ((Math.random() * 10) + 20)))
                                .getLocation();

                        if (!location.getBlock().getType().isSolid()){
                            returnLoc = location;
                            break;
                        }
                    }
                } else if (essence == Essence.EARTH){
                    for (int i = 0; i < 15; i++){
                        Location location = playerLoc.getBlock().getRelative(
                                        (int) (Math.random() * 20 - 10),
                                        (int) (Math.random() * 20 - 10),
                                        (int) (Math.random() * 20 - 10))
                                .getLocation();

                        if (!location.getBlock().getType().isSolid()){
                            returnLoc = location;
                            break;
                        }
                    }
                } else if (essence == Essence.WATER){
                    for (int x = -10; x < 10; x++){
                        for (int y = -5; y < 5; y++){
                            for (int z = -10; z < 10; z++){
                                Block block = crosshair.getBlock().getRelative(x + ((int) Math.signum(x) * 20), y, z + ((int) Math.signum(z) * 20));
                                if (block.getType() == Material.WATER){
                                    returnLoc = block.getLocation();
                                    break;
                                }
                                if (block.getBlockData() instanceof Waterlogged){
                                    if (((Waterlogged) block.getBlockData()).isWaterlogged()){
                                        returnLoc = block.getLocation();
                                        break;
                                    }
                                }
                            }
                            if (returnLoc.equals(playerLoc)){
                                break;
                            }
                        }
                        if (returnLoc.equals(playerLoc)){
                            break;
                        }
                    }
                    // try to find an air block 20 < loc < 30
                    if (!returnLoc.equals(playerLoc)) {
                        for (int i = 0; i < 15; i++) {
                            Location location = crosshair.getBlock().getRelative(
                                            (int) (Math.signum(((Math.random() * 4)) - 2) * ((Math.random() * 10) + 20)),
                                            (int) (Math.signum(((Math.random() * 4)) - 2) * ((Math.random() * 5))),
                                            (int) (Math.signum(((Math.random() * 4)) - 2) * ((Math.random() * 10) + 20)))
                                    .getLocation();

                            if (!location.getBlock().getType().isSolid()) {
                                returnLoc = location;
                                break;
                            }
                            if (i == 14) {
                                returnLoc = location;
                                break;
                            }
                        }
                    }
                } else if (essence == Essence.WIND){
                    for (int i = 0; i < 15; i++){
                        Location location = crosshair.getBlock().getRelative(
                                        (int) (Math.signum(((Math.random() * 4)) - 2) * ((Math.random() * 5) + 10)),
                                        (int) (Math.signum(((Math.random() * 4)) - 2) * ((Math.random() * 5) + 10)),
                                        (int) (Math.signum(((Math.random() * 4)) - 2) * ((Math.random() * 5) + 10)))
                                .getLocation();

                        if (location.getBlock().getType().isAir()){
                            returnLoc = location;
                            break;
                        }
                        if (i == 14){
                            returnLoc = location;
                            break;
                        }
                    }
                } else if (essence == Essence.ELECTRICITY){
                    // sky
                    for (int i = 0; i < 15; i++){
                        Location location = crosshair.getBlock().getRelative(
                                        (int) (Math.signum(((Math.random() * 4)) - 2) * ((Math.random() * 5) + 10)),
                                        (int) ((Math.random() * 10) + 10),
                                        (int) (Math.signum(((Math.random() * 4)) - 2) * ((Math.random() * 5) + 10)))
                                .getLocation();

                        if (location.getBlock().getType().isAir()){
                            returnLoc = location;
                            break;
                        }
                        if (i == 14){
                            returnLoc = location;
                            break;
                        }
                    }
                } else if (essence == Essence.ICE){
                    for (int i = 0; i < 15; i++){
                        Location location = crosshair.getBlock().getRelative(
                                        (int) (Math.signum(((Math.random() * 4)) - 2) * ((Math.random() * 5) + 10)),
                                        (int) (Math.signum(((Math.random() * 4)) - 2) * ((Math.random() * 5) + 10)),
                                        (int) (Math.signum(((Math.random() * 4)) - 2) * ((Math.random() * 5) + 10)))
                                .getLocation();

                        if (!location.getBlock().getType().isAir()){
                            returnLoc = location;
                            break;
                        }
                        if (i == 14){
                            returnLoc = location;
                            break;
                        }
                    }
                } else if (essence == Essence.POISON){

                } else if (essence == Essence.LIVING){

                } else if (essence == Essence.SPECTRAL){

                } else if (essence == Essence.BARRIER){

                }
                Bukkit.getScheduler().runTask(notMagic, () -> callback.onCalcFinish(Collections.singletonList(returnLoc)));
            }
        });

        //return playerLoc;
    }

    public void spawnParticles(@Nonnull Location location){
        if (location.getWorld() != null) {
            if (this == Essence.FIRE) {
                location.getWorld().spawnParticle(Particle.LAVA, location, 10, .5, .5, .5);
            } else if (this == Essence.EARTH) {
                for (int i = 0; i < 10; i++) {
                    BlockData data = Material.COARSE_DIRT.createBlockData();
                    location.getWorld().spawnParticle(Particle.BLOCK_CRACK, new Location(location.getWorld(), location.getX() + (Math.random() - 0.5), location.getY() + (Math.random() - 0.5), location.getZ() + (Math.random() - 0.5)), 1, data);
                }
            } else if (this == Essence.WATER) {
                location.getWorld().spawnParticle(Particle.WATER_SPLASH, location, 10, .5, .5, .5);
            } else if (this == Essence.WIND) {
                for (int i = 0; i < 10; i++) {
                    location.getWorld().spawnParticle(Particle.CLOUD, new Location(location.getWorld(), location.getX() + (Math.random() - 0.5), location.getY() + (Math.random() - 0.5), location.getZ() + (Math.random() - 0.5)), 0, Math.random() * .4 - .2, Math.random() * .4 - .2, Math.random() * .4 - .2);
                }
            } else if (this == Essence.ELECTRICITY) {
                location.getWorld().spawnParticle(Particle.FLASH, location, 10, .5, .5, .5);
            } else if (this == Essence.ICE) {
                location.getWorld().spawnParticle(Particle.SNOWFLAKE, location, 10, .5, .5, .5);
            } else if (this == Essence.POISON) {
                location.getWorld().spawnParticle(Particle.SNOWFLAKE, location, 10, .5, .5, .5);
            } else if (this == Essence.LIVING) {
                location.getWorld().spawnParticle(Particle.SNOWFLAKE, location, 10, .5, .5, .5);
            } else if (this == Essence.SPECTRAL) {
                location.getWorld().spawnParticle(Particle.SNOWFLAKE, location, 10, .5, .5, .5);
            } else if (this == Essence.BARRIER) {
                BlockData data = Material.DEEPSLATE_BRICKS.createBlockData();
                location.getWorld().spawnParticle(Particle.BLOCK_MARKER, location, 1, data);
            }
        }
    }

    public int getPotentialMana(int multiplier){
        return potentialPower * multiplier;
    }

    public int getAreaEffectMana(int multiplier){
        return areaEffectPower * multiplier;
    }

    public int getIntensityMana(int multiplier){
        return intensityPower * multiplier;
    }

    // prob gonna change when you combine essence
    public int getControlMana(int multiplier){
        return 5 * multiplier;
    }

    public int getAreaEffectPower() {
        return areaEffectPower;
    }

    public int getIntensityPower() {
        return intensityPower;
    }

    public int getPotentialPower() {
        return potentialPower;
    }

    public int getControlPower(){
        return 5;
    }


    public static Essence fromItemStack(ItemStack itemStack){
        if (Items.isEssence(itemStack)){
            assert itemStack.getItemMeta() != null;
            String name = itemStack.getItemMeta().getDisplayName();
            String essence = ChatColor.stripColor(name);
            essence = essence.substring(0, essence.lastIndexOf(" "));
            Essence result;
            try{
                result = valueOf(essence.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException ignored){
                return EMPTY;
            }
            return result;
        }
        return EMPTY;
    }
    public ItemStack getItemStack(){
        return Items.data(this + "Essence");
    }
}
