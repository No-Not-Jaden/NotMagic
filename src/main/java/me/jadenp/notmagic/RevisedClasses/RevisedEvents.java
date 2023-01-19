package me.jadenp.notmagic.RevisedClasses;

import me.jadenp.notmagic.Commands;
import me.jadenp.notmagic.NotMagic;
import me.jadenp.notmagic.SpellWorkshop.Essence;
import me.jadenp.notmagic.SpellWorkshop.SpellNames;
import me.jadenp.notmagic.SpellWorkshop.WorkshopSpell;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class RevisedEvents implements Listener {

    private Plugin plugin;
    private NotMagic notMagic;
    public Magic magicClass;

    private List<PlayerData> playerData = new ArrayList<>();
    private List<WorkshopSpell> workshopSpells = new ArrayList<>();
    Items items = new Items();


    public RevisedEvents(NotMagic notMagic) throws IOException {
        this.plugin = notMagic;
        this.notMagic = notMagic;
        magicClass = new Magic(notMagic,this);

        // load key from recordKey file to get uuids of every player, then grab their corresponding file and place it in a list
        Bukkit.getLogger().info("Loading Player Data...");
        playerData.clear();
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(notMagic.recordKey);
        List<String> players = configuration.getStringList("uuids");
        for (String uuid : players){
            File pFile = new File(notMagic.playerRecords+File.separator+uuid+".yml");
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
        Bukkit.getLogger().info("Loading Workshop Spells...");
        YamlConfiguration configuration1 = YamlConfiguration.loadConfiguration(notMagic.craftedSpells);
        workshopSpells.clear();
        int i = 0;
        while (configuration1.isSet(i + ".name")){
            workshopSpells.add(new WorkshopSpell(
                    Essence.valueOf(configuration1.getString(i + ".potential").toUpperCase(Locale.ROOT)), configuration1.getInt(i + ".potential-amount"),
                    Essence.valueOf(configuration1.getString(i + ".area-effect").toUpperCase(Locale.ROOT)), configuration1.getInt(i + ".area-effect-amount"),
                    Essence.valueOf(configuration1.getString(i + ".intensity").toUpperCase(Locale.ROOT)), configuration1.getInt(i + ".intensity-amount"),
                    Essence.valueOf(configuration1.getString(i + ".control").toUpperCase(Locale.ROOT)), configuration1.getInt(i + ".control-amount"),
                    configuration1.getInt(i + ".accuracy"),
                    configuration1.getString(i + ".name"),
                    configuration1.getInt(i + ".mana-cost"),
                    configuration1.getBoolean(i + ".main-spell"),
                    configuration1.getInt(i + ".magic-value"),
                    configuration1.getInt(i + ".cast-time"),
                    UUID.fromString(configuration1.getString(i + ".uuid")),
                    configuration1.getInt("cooldown"),
                    configuration1.getInt("required-level"),
                    configuration1.getStringList("cast-pattern"),
                    notMagic));
            i++;
        }

        // auto save every 5 min
        new BukkitRunnable(){
            @Override
            public void run() {
                saveData();
            }
        }.runTaskTimerAsynchronously(plugin,3600, 3600);
    }

    public void saveData(){
        for (PlayerData data : playerData){
            File pFile = new File(notMagic.playerRecords+File.separator+data.getUuid()+".yml");
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
        int i = 0;
        YamlConfiguration c = new YamlConfiguration();
        for (WorkshopSpell workshopSpell : workshopSpells) {
            c.set(i + ".potential", workshopSpell.getPotential().toString());
            c.set(i + ".potential-amount", workshopSpell.getPotentialAmount());
            c.set(i + ".area-effect", workshopSpell.getAreaEffect().toString());
            c.set(i + ".area-effect-amount", workshopSpell.getAreaEffectAmount());
            c.set(i + ".intensity", workshopSpell.getIntensity().toString());
            c.set(i + ".intensity-amount", workshopSpell.getIntensityAmount());
            c.set(i + ".control", workshopSpell.getControl().toString());
            c.set(i + ".control-amount", workshopSpell.getControlAmount());
            c.set(i + ".accuracy", workshopSpell.getAccuracy());
            c.set(i + ".name", workshopSpell.getName());
            c.set(i + ".mana-cost", workshopSpell.getMpCost());
            c.set(i + ".main-spell", workshopSpell.isMainSpell());
            c.set(i + ".magic-value", workshopSpell.getMagicValue());
            c.set(i + ".cast-time", workshopSpell.getCastTime());
            c.set(i + ".uuid", workshopSpell.getUuid().toString());
            c.set(i + ".cooldown", workshopSpell.getCooldown());
            c.set(i + ".required-level", workshopSpell.getRequiredLevel());
            c.set(i + ".cast-pattern", workshopSpell.getSpellPattern());
            i++;
        }
        try {
            c.save(notMagic.craftedSpells);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) throws IOException {
        Player p = event.getPlayer();
        String uID = p.getUniqueId().toString();
        PlayerData data = findPlayer(p.getUniqueId());
        if (data == null){
            File pFile = new File(notMagic.playerRecords+File.separator+uID+".yml");
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
                c.set("spells-unlocked", Collections.singletonList("Burn"));
                c.save(pFile);

            }
        } else {
            data.setPlayerName(p.getName());
            data.relog();
        }

        if (p.getInventory().contains(items.data("RegenDust"))){
            p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100,1));
        }

        for (Player hidden : magicClass.spellIndex.getHiddenPlayers()){
            event.getPlayer().hidePlayer(NotMagic.getInstance(), hidden);
        }
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


    public List<WorkshopSpell> getWorkshopSpells() {
        return workshopSpells;
    }

    public void addWorkshopSpell(){

    }

    public String getUniqueSpellName(WorkshopSpell spell){
        // list of modifier words for how much essence were put into the spell
        // 2 more words for the spell name:
        // 1 word for intensity, potential, area effect
        // 1 word for accuracy & control
        int multiplier = spell.getPotentialAmount() + spell.getAreaEffectAmount() + spell.getIntensityAmount() + spell.getControlAmount();
        String name = SpellNames.getMultiplierName(multiplier) + " " + SpellNames.combinePassiveEssence(spell.getControl(), spell.getAccuracy()) + " " + SpellNames.combineEssence(Arrays.asList(spell.getPotential(), spell.getAreaEffect(), spell.getIntensity(), spell.getControl()));
        if (name.charAt(0) == ' '){
            name = name.substring(1);
        }
        return name;
    }

    public WorkshopSpell getWorkshopSpell(Essence potential, int potentialAmount, Essence areaEffect, int areaEffectAmount, Essence intensity, int intensityAmount, Essence control, int controlAmount, int accuracy){
        for (WorkshopSpell spell : workshopSpells){
            if (spell.getAccuracy() == accuracy &&
                    spell.getPotential().equals(potential) && spell.getPotentialAmount() == potentialAmount &&
                    spell.getAreaEffect().equals(areaEffect) && spell.getAreaEffectAmount() == areaEffectAmount &&
                    spell.getIntensity().equals(intensity) && spell.getIntensityAmount() == intensityAmount &&
                    spell.getControl().equals(control) && spell.getControlAmount() == controlAmount){
                return spell;
            }
        }
        return null;
    }

    @EventHandler
    public void onDamageEntity(EntityDamageByEntityEvent event){
        if (event.getEntity() instanceof EnderCrystal){
            if (event.getEntity().hasMetadata("magic")){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDisable(PluginDisableEvent event){
        if (event.getPlugin().equals(NotMagic.getInstance())){
            for (Entity entity : magicClass.spellIndex.getMagicEntities()){
                entity.remove();
            }
            for (Player player : Bukkit.getOnlinePlayers()){
                for (Player hidden : magicClass.spellIndex.getHiddenPlayers()){
                    player.showPlayer(NotMagic.getInstance(), hidden);
                }
            }
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        if (magicClass.spellIndex.getHiddenPlayers().contains(event.getPlayer())){
            for (Player player : Bukkit.getOnlinePlayers()){
                    player.showPlayer(NotMagic.getInstance(), event.getPlayer());
            }
        }
        for (Player player : magicClass.spellIndex.getHiddenPlayers()){
            player.showPlayer(NotMagic.getInstance(), event.getPlayer());
        }
    }
}
