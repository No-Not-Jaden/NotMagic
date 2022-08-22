package me.jadenp.notmagic.RevisedClasses;

import me.jadenp.notmagic.Commands;
import me.jadenp.notmagic.NotMagic;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class RevisedEvents implements Listener {

    private Plugin plugin;
    private NotMagic notMagic;
    private Magic magicClass;

    private List<PlayerData> playerData = new ArrayList<>();
    public List<String> language = new ArrayList<>();
    Items items = new Items();
    public File playerRecords;
    public File backups;
    public File recordKey;
    public String prefix;


    public RevisedEvents(NotMagic notMagic) throws IOException {
        this.plugin = notMagic;
        this.notMagic = notMagic;
        magicClass = new Magic(notMagic,this);
        playerRecords = new File(plugin.getDataFolder()+File.separator+"player-records");
        backups = new File(plugin.getDataFolder()+File.separator+"backups");
        recordKey = new File(playerRecords + File.separator + "record-key.yml");


        // load key from recordKey file to get uuids of every player, then grab their corresponding file and place it in a list
        Bukkit.getLogger().info("Loading Player Data...");
        playerData.clear();
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(recordKey);
        List<String> players = configuration.getStringList("uuids");
        for (String uuid : players){
            File pFile = new File(playerRecords+File.separator+uuid+".yml");
            if (pFile.exists()){
                YamlConfiguration c = YamlConfiguration.loadConfiguration(pFile);
                playerData.add(new PlayerData(UUID.fromString(uuid), c.getString("name"), c.getInt("level"), c.getInt("xp"), c.getInt("mp-max"), c.getDouble("mp-regen"), (ArrayList<String>) c.getStringList("spells-unlocked")));

            } else {
                pFile.createNewFile();
                Bukkit.getLogger().warning("<!> Could not find file for " + uuid + ". Created new one. <!>");
                YamlConfiguration c = new YamlConfiguration();
                c.set("name", "null");
                c.set("level", 1);
                c.set("xp", 0);
                c.set("mp-max", 50);
                c.set("mp-regen", 0.5);
                ArrayList<String> spellsUnlocked = new ArrayList<>();
                spellsUnlocked.add("Burn");
                c.set("spells-unlocked", spellsUnlocked);
                c.save(pFile);
            }
        }
        Bukkit.getLogger().info("Player Data Loaded!");

        // auto save every 5 min
        new BukkitRunnable(){
            @Override
            public void run() {
                for (PlayerData data : playerData){
                    File pFile = new File(playerRecords+File.separator+data.getUuid()+".yml");
                    YamlConfiguration c = new YamlConfiguration();
                    c.set("name", data.getPlayerName());
                    c.set("level", data.getLevel());
                    c.set("xp", data.getXp());
                    c.set("mp-max", data.getMpMax());
                    c.set("mp-regen", data.getMpRegen());
                    c.set("spells-unlocked", data.getSpellsUnlocked());
                    try {
                        c.save(pFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.runTaskTimerAsynchronously(plugin,3600, 3600);
    }
    public void updateConfig(ArrayList<String> language, String prefix, List<CustomSpell> spells, boolean useBuiltInSpells){
        this.language = language;
        this.prefix = prefix;
        magicClass.updateConfig(spells, useBuiltInSpells);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) throws IOException {
        Player p = event.getPlayer();
        String uID = p.getUniqueId().toString();
        PlayerData data = findPlayer(p.getUniqueId());
        if (data == null){
            File pFile = new File(playerRecords+File.separator+uID+".yml");
            if (pFile.exists()){
                YamlConfiguration c = YamlConfiguration.loadConfiguration(pFile);
                playerData.add(new PlayerData(p.getUniqueId(), p.getName(), c.getInt("level"), c.getInt("xp"), c.getInt("mp-max"), c.getDouble("mp-regen"), (ArrayList<String>) c.getStringList("spells-unlocked")));

            } else {
                Bukkit.getLogger().info("Unique player joined! Creating Player Data.");
                pFile.createNewFile();
                YamlConfiguration c = new YamlConfiguration();
                c.set("name", p.getName());
                c.set("level", 1);
                c.set("xp", 0);
                c.set("mp-max", 50);
                c.set("mp-regen", 0.5);
                ArrayList<String> spellsUnlocked = new ArrayList<>();
                spellsUnlocked.add("Burn");
                c.set("spells-unlocked", spellsUnlocked);
                c.save(pFile);

            }
        } else {
            data.setPlayerName(p.getName());
            data.relog();
        }

        if (p.getInventory().contains(items.data("RegenDust"))){
            p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100,1));
        }

    }

    @EventHandler
    public void playerQuitEvent(PlayerQuitEvent event){
        Bukkit.getLogger().info("Saving Player Data...");
        for (PlayerData data : playerData){
            File pFile = new File(playerRecords+File.separator+data.getUuid()+".yml");
            YamlConfiguration c = new YamlConfiguration();
            c.set("name", data.getPlayerName());
            c.set("level", data.getLevel());
            c.set("xp", data.getXp());
            c.set("mp-max", data.getMpMax());
            c.set("mp-regen", data.getMpRegen());
            c.set("spells-unlocked", data.getSpellsUnlocked());
            try {
                c.save(pFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Bukkit.getLogger().info("Player Data Saved.");
    }



    public PlayerData findPlayer(UUID uuid){
        for (PlayerData data : playerData){
            if (data.getUuid().equals(uuid)){
                return data;
            }
        }
        return null;
    }

    public List<PlayerData> getPlayerData() {
        return playerData;
    }

    public Magic getMagicClass() {
        return magicClass;
    }
}
