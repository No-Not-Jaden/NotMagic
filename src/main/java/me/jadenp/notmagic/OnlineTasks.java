package me.jadenp.notmagic;



import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.security.PrivateKey;
import java.util.*;

public class OnlineTasks {
    private Plugin plugin;
    private File playerdata;
    private File manaMines;
    private Map<Integer, Location> ores;
    private Map<Integer, Location> ores2;
    private Map<Integer, Chunk> chunkMap;
    private Map<Integer, Chunk> chunkMap2;
    public OnlineTasks(Plugin p){
        Random rand = new Random();
        this.plugin = p;
        this.playerdata = new File(plugin.getDataFolder()+File.separator+"playerdata");
        this.manaMines = new File(plugin.getDataFolder()+File.separator+"mana-mines.yml");
        this.ores = new HashMap<>();
        this.ores2 = new HashMap<>();
        this.chunkMap = new HashMap<>();
        this.chunkMap2 = new HashMap<>();
        new BukkitRunnable(){
            @Override
            public void run() {
                YamlConfiguration c = YamlConfiguration.loadConfiguration(manaMines);

                Iterator iterator = chunkMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry mapElement = (Map.Entry)iterator.next();
                    if (((Chunk)mapElement.getValue()).getWorld().isChunkLoaded((Chunk)mapElement.getValue())) {
                        Location l = ores.get(mapElement.getKey());
                        Block b = l.getBlock();
                        final Particle.DustOptions dustOptions = new Particle.DustOptions(org.bukkit.Color.fromRGB(219, 182, 31), 2);
                        if (rand.nextInt(3) == 1 && b.getType() == Material.GOLD_ORE) {
                            Location loc = new Location(l.getWorld(), l.getX() + 0.5, l.getY() + 0.5, l.getZ() + 0.5);
                            b.getWorld().spawnParticle(Particle.REDSTONE, loc, 5, 0.3, 0.3, 0.3, dustOptions);

                        }
                    }
                }


            }
        }.runTaskTimer(plugin, 1, 55L);
        new BukkitRunnable(){
            @Override
            public void run() {
                YamlConfiguration c = YamlConfiguration.loadConfiguration(manaMines);
                ores2.clear();
                int i = 1;
                while (true) {
                    if (c.getLocation(i + "") != null) {
                        ores2.put(i,c.getLocation(i + ""));
                        chunkMap2.put(i,c.getLocation(i + "").getChunk());
                    } else {
                        break;
                    }
                    i++;
                }
                ores = ores2;
                chunkMap = chunkMap2;
            }
        }.runTaskTimerAsynchronously(plugin, 0, 18000L);
        new BukkitRunnable(){
            @Override
            public void run() {
                for (World w : Bukkit.getWorlds()){
                    for (Entity e : w.getEntities()){
                        if (e instanceof LivingEntity) {
                            if (e.hasMetadata("cursed")) {
                                    ((LivingEntity) e).damage(15, Bukkit.getPlayer(String.valueOf(e.getMetadata("cursed"))));
                                    ((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 10, 1));
                                    e.getWorld().playSound(e.getLocation(), Sound.BLOCK_CONDUIT_ATTACK_TARGET, 1, 1);
                                    ((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 210, 1));
                                    ((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 210, 1));
                                    e.getWorld().spawnParticle(Particle.DRIPPING_OBSIDIAN_TEAR, e.getLocation(),10,1,1,1);
                            }
                        }
                    }
                }
                for (Map.Entry<Integer, Chunk> integerLocationEntry : chunkMap.entrySet()) {
                    if (integerLocationEntry.getValue().getWorld().isChunkLoaded(integerLocationEntry.getValue())) {
                        Location l = ores.get(integerLocationEntry.getKey());
                        if (rand.nextInt(7) == 1) {
                            if (l.getBlock().getType().isAir()) {
                                l.getBlock().setType(Material.STONE);
                            } else if (l.getBlock().getType() == Material.STONE) {
                                l.getBlock().setType(Material.GOLD_ORE);
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 100, 200L);

    }


}
