package me.jadenp.notmagic.SpellWorkshop;

import me.jadenp.notmagic.RevisedClasses.Items;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.*;

public enum Essence {
    /**
     * don't forget to add more names to SpellNames.java !
     */
    EMPTY(0,0,0),
    FIRE(8, 6, 3),
    EARTH(5, 8, 2);

    private final int potentialPower; // speed
    private final int areaEffectPower;
    private final int intensityPower;
    Essence(int potentialPower, int areaEffectPower, int intensityPower){
        this.potentialPower = potentialPower;
        this.areaEffectPower = areaEffectPower;
        this.intensityPower = intensityPower;
    }

    public Location potentialResults(Location point, Location start) {
        if (this.toString().equalsIgnoreCase("Fire")) {
            return new Location(point.getWorld(), point.getX() + Math.sin(point.getX() - start.getX()), point.getY() + Math.sin(point.getY() - start.getY()), point.getZ() + Math.sin(point.getZ() - start.getZ()));
        } else if (this.toString().equalsIgnoreCase("Earth")){
            if ((int) (Math.random() * 3) == 0){
                Vector direction = point.toVector().subtract(start.toVector()).normalize();
                Vector randomPoint = direction.clone().rotateAroundY(Math.PI / 2);
                randomPoint.rotateAroundAxis(randomPoint, Math.random() * 2 * Math.PI).normalize().multiply(Math.random() * potentialPower);
                return new Location(point.getWorld(), point.getX() + randomPoint.getX(), point.getY() + randomPoint.getY(), point.getZ() + randomPoint.getZ());
            }
            return point;
        }
        return point;
    }

    public List<Location> areaEffectResults(Location center, int amount){
        List<Location> locations = new ArrayList<>();
        while (amount > 0) {
            if (this.toString().equalsIgnoreCase("Fire")) {
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
            } else if (this.toString().equalsIgnoreCase("Earth")){
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
            }
        }
        return locations;
    }

    public void intensityResults(Location point, double damageMultiplier, Player player){
        if (this.toString().equalsIgnoreCase("Fire")){
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
        } else if (this.toString().equalsIgnoreCase("Earth")){
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
        }
    }

    private boolean isImmuneToFire(Entity entity){
        return (entity instanceof Blaze || entity instanceof Ghast || entity instanceof MagmaCube || entity instanceof Strider || entity instanceof WitherSkeleton || entity instanceof Zoglin || entity instanceof PigZombie || entity instanceof Warden || entity instanceof Wither || entity instanceof EnderDragon);
    }

    public Location controlResults(Location crosshair, Location playerLoc){
        if (this.toString().equalsIgnoreCase("Fire")){
            for (int x = -10; x < 10; x++){
                for (int y = -5; y < 5; y++){
                    for (int z = -10; z < 10; z++){
                        Block block = crosshair.getBlock().getRelative(x + ((int) Math.signum(x) * 20), y, z + ((int) Math.signum(z) * 20));
                        if (block.getType() == Material.FIRE || block.getType() == Material.LAVA){
                            return block.getLocation();
                        }
                    }
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
                    return location;
                }
            }
        } else if (this.toString().equalsIgnoreCase("Earth")){
            for (int i = 0; i < 15; i++){
                Location location = playerLoc.getBlock().getRelative(
                                (int) (Math.random() * 20 - 10),
                                (int) (Math.random() * 20 - 10),
                                (int) (Math.random() * 20 - 10))
                        .getLocation();

                if (!location.getBlock().getType().isSolid()){
                    return location;
                }
            }
        }
        return playerLoc;
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
        Items items = new Items();
        if (items.isEssence(itemStack)){
            assert itemStack.getItemMeta() != null;
            String name = itemStack.getItemMeta().getDisplayName();
            String essence = name.substring(0, name.lastIndexOf(" "));
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
}
