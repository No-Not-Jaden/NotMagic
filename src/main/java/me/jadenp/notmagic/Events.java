package me.jadenp.notmagic;


import me.jadenp.notmagic.Alchemy.AlchemyBrewEvent;
import me.jadenp.notmagic.RevisedClasses.Items;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.*;


/*  TO ADD NEW SPELL
 *  1. Add spell book item to Items.java
 *  2. Add to interact event: if you click the book, you unlock the spell
 *  3. Go to Spells.java and create how it is going to be cast
 *  4. Create what the spell does, if it is a main spell, switch main spell in file to that spell & add it to main spells to be cast in this class
 *
 */
public class Events implements Listener {


    Items items = new Items();
    private final Plugin plugin;
    private final File playerdata;
    private final int maxPoints;
    private final String prefix;
    private final MainSpells mainSpells;
    private final File manaMines;
    private final File backups;
    private final File config;
    private boolean obtainSpellsNaturally;
    private Map<String, Integer> level;
    private Map<String, Integer> xp;
    private Map<String, Integer> manaNum;
    private Map<String, List<String>> spellsUnlocked;
    private Map<String, Integer> manaCap;
    private Map<String, Integer> manaRegen;
    private Map<String, Integer> interactCooldown;
    private Map<String, Integer> spellCooldown;
    private Map<String, Integer> spellExpire;
    private Map<String, Integer[]> spellColor;
    private Map<String, Boolean> dev;
    private Map<String, String> selectedSpell;
    private Map<String, Location[]> spellPos;
    private Map<String, List<String>> familiars;
    private Map<String, String> selectedFamiliar;
    private Map<String, String> familiarUUID;
    private Map<String, String> customMobs;
    private int dropChance;
    private Map<Integer, Location> manaMineLocs;
    private Map<String, Integer> spellDrain;
    private Map<String, Boolean> shadow;

    private Commands commandClass;

    public Events(Plugin p, Commands commandClass){
        this.commandClass = commandClass;
        this.plugin = p;
        this.backups = new File(plugin.getDataFolder()+File.separator+"backups");
        this.config = new File(plugin.getDataFolder()+File.separator+ "oldconfig.txt");
        this.mainSpells = new MainSpells(p);
        this.playerdata = new File(plugin.getDataFolder()+File.separator+"playerdata");
        this.maxPoints = 5;
        this.prefix = ChatColor.GRAY + "[" + ChatColor.of(new Color(26, 194, 232)) + "Not" + ChatColor.of(new Color(232, 26, 225)) + "Magic" + ChatColor.GRAY + "] " + ChatColor.DARK_GRAY + "» ";
        this.manaMines = new File(plugin.getDataFolder()+File.separator+"mana-mines.yml");
        this.level = new HashMap<>();
        this.xp = new HashMap<>();
        this.manaNum = new HashMap<>();
        this.spellsUnlocked = new HashMap<>();
        this.manaCap = new HashMap<>();
        this.manaRegen = new HashMap<>();
        this.interactCooldown = new HashMap<>();
        this.spellCooldown = new HashMap<>();
        this.spellExpire = new HashMap<>();
        this.spellColor = new HashMap<>();
        this.dev = new HashMap<>();
        this.selectedSpell = new HashMap<>();
        this.spellPos = new HashMap<>();
        this.familiars = new HashMap<>();
        this.selectedFamiliar = new HashMap<>();
        this.customMobs = new HashMap<>();
        this.dropChance = 0;
        this.manaMineLocs = new HashMap<>();
        this.familiarUUID = new HashMap<>();
        this.spellDrain = new HashMap<>();
        this.shadow = new HashMap<>();
        this.obtainSpellsNaturally = plugin.getConfig().getBoolean("obtain-spells-naturally");
        new BukkitRunnable(){
            @Override
            public void run() {

                YamlConfiguration c = YamlConfiguration.loadConfiguration(config);
                List<String> monsters = (List<String>) c.getList("custom-monsters.monsters");
                if (monsters != null)
                    for (String m : monsters) {
                        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                            int rate = c.getInt("custom-monsters." + m + ".spawn-rate");
                            if (rate != 0) {
                                int a = rand.nextInt(rate);
                                if (a + 1 == 1) {

                                    Player p = player;
                                    assert p != null;
                                        Location location = new Location(p.getLocation().getWorld(), p.getLocation().getX() + (rand.nextInt(100) - 50), p.getLocation().getY(), p.getLocation().getZ() + (rand.nextInt(100) - 50));
                                        Location loc = new Location(location.getWorld(), location.getX(), location.getWorld().getHighestBlockYAt(location) + 1, location.getZ());
                                            System.out.println(prefix + ChatColor.RESET + "Spawned Custom Mob at " + loc.toString());
                                            if (player.getInventory().contains(items.data("MonsterNotifier")))
                                            player.sendMessage(prefix + ChatColor.DARK_GREEN + "A Monster King has spawned near you!");
                                            Objects.requireNonNull(loc.getWorld()).spawnParticle(Particle.CLOUD, loc, 20, 3, 3, 3);
                                            LivingEntity entity = (LivingEntity) Objects.requireNonNull(loc.getWorld()).spawnEntity(loc, Objects.requireNonNull(EntityType.fromName(c.getString("custom-monsters." + m + ".mob-type"))));
                                            entity.setCustomName(colorize(c.getString("custom-monsters." + m + ".custom-name.name")));
                                            entity.setCustomNameVisible(c.getBoolean("custom-monsters." + m + ".custom-name.visible"));
                                            entity.setMetadata(m, new FixedMetadataValue(plugin, true));
                                            entity.setPersistent(true);
                                            entity.setRemoveWhenFarAway(false);
                                            customMobs.put(entity.getUniqueId().toString(), m);
                                            double health = c.getDouble("custom-monsters." + m + ".health");
                                            if (health != -1) {
                                                entity.setMaxHealth(health);
                                                entity.setHealth(health);
                                            }
                                            int i = 1;
                                            while (c.getString("custom-monsters." + m + ".potion-effects." + i + ".effect") != null) {
                                                entity.addPotionEffect(new PotionEffect(stringToPotion(c.getString("custom-monsters." + m + ".potion-effects." + i + ".effect")), Integer.MAX_VALUE, c.getInt("custom-monsters." + m + ".potion-effects." + i + ".level") - 1));
                                                i++;
                                            }
                                        }



                            }
                        }
                    }



            }
        }.runTaskTimer(plugin, 0, 36000L);
        //36000
        new BukkitRunnable(){
            @Override
            public void run() {
                for(Player player : Bukkit.getServer().getOnlinePlayers()){
                    for (int i = 0; i < 5; i++) {
                        if (getInteractCooldown(player) > 0) {
                            try {
                                setInteractCooldown(player, getInteractCooldown(player) - 1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if (getSpellExpire(player) > 0) {
                            try {
                                if (getSpellExpire(player) == 1) {
                                    resetSpellPos(player);
                                }
                                setSpellExpire(player, getSpellExpire(player) - 1);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if (getSpellCooldown(player) > 0) {
                            try {
                                setSpellCooldown(player, getSpellCooldown(player) - 1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        reduceSpellDrain(player);
                    }
                    try {
                        replenishMana(player);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (wandLevel(player) > -1){

                        int num = manaNum.get(player.getUniqueId().toString());
                        StringBuilder str = new StringBuilder();
                        if (getSpellCooldown(player) > 0) {
                            str.append(ChatColor.of(new Color(21, 130, 158))).append("▏");
                        } else {
                            str.append(ChatColor.of(new Color(158, 228, 247))).append("▏");
                        }
                        if (num > 0) {
                            for (int i = 0; i < num/20; i++) {
                                str.append(ChatColor.of(new Color(85, 181, 171))).append("█");
                            }
                            int num2 = num%20;
                            if (num2 > 14) {
                                str.append(ChatColor.of(new Color(85, 181, 171))).append("▓");
                            } else if (num2 > 9) {
                                str.append(ChatColor.of(new Color(85, 181, 171))).append("▒");
                            } else if (num2 > 4) {
                                str.append(ChatColor.of(new Color(85, 181, 171))).append("░");
                            } else if (num2 > 0){
                                str.append(ChatColor.of(new Color(170, 226, 240))).append("░");
                            }
                            // leftover mana points

                            // if there is a group

                        }
                        int ln = (str.length()-15)/15;
                        for (int i = 0; i < (manaCap.get(player.getUniqueId().toString())/20)-ln; i++){
                            str.append(ChatColor.of(new Color(171, 193, 199))).append("░");
                        }
                        if (getSpellCooldown(player) > 0) {
                            str.append(ChatColor.of(new Color(21, 130, 158))).append("▏");
                        } else {
                            str.append(ChatColor.of(new Color(158, 228, 247))).append("▏");
                        }
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(String.valueOf(str)));
                    } else {
                        try {
                            resetSpellPos(player);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(plugin, 0, 10L);
        // run a backup every 5 min
        new BukkitRunnable(){
            @Override
            public void run() {
                manaMineLocs.clear();
                int i = 1;
                YamlConfiguration z = YamlConfiguration.loadConfiguration(manaMines);
                while (true) {
                    if (z.getLocation(i + "") != null) {
                        Location l = z.getLocation(i + "");
                        manaMineLocs.put(i, l);
                    } else {
                        break;
                    }
                    i++;
                }
                YamlConfiguration x = YamlConfiguration.loadConfiguration(config);
                dropChance = x.getInt("magic-resources.drop-chance");
                for (Player player : Bukkit.getServer().getOnlinePlayers()){
                    File pFile = new File(playerdata+File.separator+player.getUniqueId().toString()+".yml");
                    if (pFile.exists()) {
                        YamlConfiguration y = YamlConfiguration.loadConfiguration(pFile);
                        Integer[] pSpellColor = new Integer[]{y.getInt("spell.color.r"), y.getInt("spell.color.g"), y.getInt("spell.color.b")};
                        spellColor.replace(player.getUniqueId().toString(), pSpellColor);
                    }
                    int pLevel = level.get(player.getUniqueId().toString());
                    int pXp = xp.get(player.getUniqueId().toString());
                    int pManaNum = manaNum.get(player.getUniqueId().toString());
                    int pManaCap = manaCap.get(player.getUniqueId().toString());
                    int pManaRegen = manaRegen.get(player.getUniqueId().toString());
                    List<String> pSpellsUnlocked = spellsUnlocked.get(player.getUniqueId().toString());
                    List<String> pFamiliars = familiars.get(player.getUniqueId().toString());
                    String pSelectedSpell = selectedSpell.get(player.getUniqueId().toString());
                    Integer[] pSpellColor = spellColor.get(player.getUniqueId().toString());
                    boolean pDev = dev.get(player.getUniqueId().toString());
                    YamlConfiguration c = new YamlConfiguration();
                    c.set("level", pLevel);
                    c.set("xp", pXp);
                    c.set("mana.num", pManaNum);
                    c.set("mana.cap", pManaCap);
                    c.set("mana.regen", pManaRegen);
                    c.set("spells", pSpellsUnlocked);
                    c.set("spell.selected", pSelectedSpell);
                    c.set("familiars", pFamiliars);
                    c.set("dev", pDev);
                    c.set("spell.color.r", pSpellColor[0]);
                    c.set("spell.color.g", pSpellColor[1]);
                    c.set("spell.color.b", pSpellColor[2]);
                    if (pFile.exists()) {
                        try {
                            c.save(pFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println(ChatColor.DARK_RED + "<!> NotMagic tried to save " + player.getName() + " to their file but they don't have one! <!>");
                    }
                }

            }
        }.runTaskTimerAsynchronously(plugin, 1000, 6000L);
        new BukkitRunnable(){
            @Override
            public void run() {
                File[] fileList = playerdata.listFiles();
                if (fileList != null && fileList.length > 0){
                    System.out.println(prefix + ChatColor.GOLD + "Starting backup...");
                    for (File file : fileList){
                        YamlConfiguration c = YamlConfiguration.loadConfiguration(file);
                        File pBackup = new File(backups+File.separator+file.getName());
                        if (!pBackup.exists()) {
                            try {
                                pBackup.createNewFile();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        try {
                            c.save(pBackup);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    System.out.println(prefix + ChatColor.GOLD + "No player data files found!");
                }
                System.out.println(prefix + ChatColor.GOLD + "Backup Complete!");
            }
        }.runTaskTimerAsynchronously(plugin, 2000, 864000L);
    }
    public static String colorize(String s){
        if(s == null) return null;
        return s.replaceAll("&([0-9a-f])", "\u00A7$1");
    }
    public PotionEffectType stringToPotion(String s){
        if (s.equalsIgnoreCase("STRENGTH")){
            return PotionEffectType.INCREASE_DAMAGE;
        } else if (s.equalsIgnoreCase("REGEN")) {
            return PotionEffectType.REGENERATION;
        } else if (s.equalsIgnoreCase("SPEED")) {
            return PotionEffectType.SPEED;
        } else if (s.equalsIgnoreCase("FIRE_RESISTANCE")) {
            return PotionEffectType.FIRE_RESISTANCE;
        } else if (s.equalsIgnoreCase("JUMP")) {
            return PotionEffectType.JUMP;
        } else if (s.equalsIgnoreCase("SLOW_FALL")) {
            return PotionEffectType.SLOW_FALLING;
        }
        return null;

    }
    @EventHandler
    public void onDeath(EntityDeathEvent event){

        if (event.getEntity().getKiller() != null) {
                if (obtainSpellsNaturally){
                    Player p = event.getEntity().getKiller();
                    Entity e = event.getEntity();
                    if (getLevel(p) > 1){
                        if (e instanceof Skeleton && !(e instanceof  WitherSkeleton)){
                            int num = rand.nextInt(100);
                            if (num == 0){
                                e.getWorld().dropItemNaturally(e.getLocation(), items.data("SBZap"));
                            } else if (num == 1){
                                e.getWorld().dropItemNaturally(e.getLocation(), items.data("SBKineticElectrocute"));
                            }
                        } else if (e instanceof Zombie){
                            int num = rand.nextInt(100);
                            if (num == 0){
                                e.getWorld().dropItemNaturally(e.getLocation(), items.data("SBHeal"));
                            } else if (num == 1){
                                e.getWorld().dropItemNaturally(e.getLocation(), items.data("SBStrength"));
                            }
                        } else if (e instanceof Creeper){
                            int num = rand.nextInt(100);
                            if (num == 0){
                                e.getWorld().dropItemNaturally(e.getLocation(), items.data("SBBurst"));
                            } else if (num == 1){
                                e.getWorld().dropItemNaturally(e.getLocation(), items.data("SBThunderCloud"));
                            }
                        } else if (e instanceof MagmaCube){
                            int num = rand.nextInt(100);
                            if (num == 0){
                                e.getWorld().dropItemNaturally(e.getLocation(), items.data("SBLocate"));
                            } else if (num == 1){
                                e.getWorld().dropItemNaturally(e.getLocation(), items.data("SBDarkPoisoning"));
                            }
                        } else if (e instanceof WitherSkeleton){
                            int num = rand.nextInt(100);
                            if (num == 0){
                                e.getWorld().dropItemNaturally(e.getLocation(), items.data("SBSnipe2"));
                            } else if (num == 1){
                                e.getWorld().dropItemNaturally(e.getLocation(), items.data("SBDarkSummoning"));
                            }
                        } else if (e instanceof PiglinBrute){
                            int num = rand.nextInt(200);
                            if (num == 0){
                                e.getWorld().dropItemNaturally(e.getLocation(), items.data("SBBurn2"));
                            } else if (num == 1){
                                e.getWorld().dropItemNaturally(e.getLocation(), items.data("SBShield"));
                            }
                        } else if (e instanceof Ghast){
                            int num = rand.nextInt(200);
                            if (num == 0){
                                e.getWorld().dropItemNaturally(e.getLocation(), items.data("SBTeleport"));
                            } else if (num == 1){
                                e.getWorld().dropItemNaturally(e.getLocation(), items.data("SBDefence"));
                            }
                        } else if (e instanceof IronGolem){
                            int num = rand.nextInt(200);
                            if (num == 0){
                                e.getWorld().dropItemNaturally(e.getLocation(), items.data("SBShadowWandering"));
                            } else if (num == 1){
                                e.getWorld().dropItemNaturally(e.getLocation(), items.data("SBIronWallAttack"));
                            }
                        } else if (e instanceof Wither){
                            int num = rand.nextInt(100);
                            if (num == 0){
                                e.getWorld().dropItemNaturally(e.getLocation(), items.data("SBDarkCurse"));
                            } else if (num == 1){
                                e.getWorld().dropItemNaturally(e.getLocation(), items.data("SBLifeSteal"));
                            }
                        } else if (e instanceof Shulker){
                            int num = rand.nextInt(200);
                            if (num == 0){
                                e.getWorld().dropItemNaturally(e.getLocation(), items.data("SBSmite"));
                            } else if (num == 1){
                                e.getWorld().dropItemNaturally(e.getLocation(), items.data("SBTimeBomb"));
                            }
                        } else if (e instanceof Enderman){
                            int num = rand.nextInt(500);
                            if (num == 0){
                                e.getWorld().dropItemNaturally(e.getLocation(), items.data("SBFireBall"));
                            } else if (num == 1){
                                e.getWorld().dropItemNaturally(e.getLocation(), items.data("SBWither"));
                            }
                        } else if (e instanceof ElderGuardian){
                            int num = rand.nextInt(100);
                            if (num == 0){
                                e.getWorld().dropItemNaturally(e.getLocation(), items.data("SBIceShards"));
                            } else if (num == 1){
                                e.getWorld().dropItemNaturally(e.getLocation(), items.data("SBAbsorb"));
                            }
                        } else if (e instanceof PolarBear){
                            int num = rand.nextInt(500);
                            if (num == 0){
                                e.getWorld().dropItemNaturally(e.getLocation(), items.data("SBFreeze"));
                            } else if (num == 1){
                                e.getWorld().dropItemNaturally(e.getLocation(), items.data("SBDrown"));
                            }
                        }
                    }
                }
            if (customMobs.containsKey(event.getEntity().getUniqueId().toString())) {
                YamlConfiguration c = YamlConfiguration.loadConfiguration(config);
                List<String> drops = (List<String>) c.getList("custom-monsters." + customMobs.get(event.getEntity().getUniqueId().toString()) + ".drops");
                if (drops != null) {
                    for (String s : drops) {
                        String str = StringUtils.substringBetween(s, "[", "]");
                        String strs = s.substring(s.lastIndexOf("] ") + 1);
                        strs = strs.substring(1);
                        if (strs.contains("{player}")) {
                            strs = strs.replace("{player}", event.getEntity().getKiller().getName());
                        }
                        if (str.equals("command")) {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), strs);
                        }
                        if (str.equals("message")) {
                            event.getEntity().getKiller().sendMessage(colorize(strs));
                        }
                        if (str.equals("item")) {
                            String ss = StringUtils.substringBefore(strs, " ");
                            String ss2 = StringUtils.substringAfter(strs, " ");
                            ItemStack item = new ItemStack(Objects.requireNonNull(Material.getMaterial(ss)), Integer.parseInt(ss2));
                            event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), item);
                        }
                    }
                }
                customMobs.remove(event.getEntity().getUniqueId().toString());
            }
        }
    }
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        if (event.getEntity().hasMetadata("cursed")){
            event.getEntity().removeMetadata("cursed", plugin);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) throws IOException {
        Player p = event.getPlayer();
        String uID = p.getUniqueId().toString();
        File pFile = new File(playerdata+File.separator+uID+".yml");
        if (pFile.exists()){
            YamlConfiguration c = YamlConfiguration.loadConfiguration(pFile);
            if (level.containsKey(uID)) {
                level.replace(uID, c.getInt("level"));
                xp.replace(uID, c.getInt("xp"));
                manaNum.replace(uID, c.getInt("mana.num"));
                manaCap.replace(uID, c.getInt("mana.cap"));
                spellsUnlocked.replace(uID, (List<String>) c.getList("spells"));
                manaRegen.replace(uID, c.getInt("mana.regen"));
                selectedSpell.replace(uID, c.getString("spell.selected"));
                dev.replace(uID, c.getBoolean("dev"));
                spellColor.replace(uID, new Integer[]{c.getInt("spell.color.r"), c.getInt("spell.color.g"), c.getInt("spell.color.b")});
                familiars.replace(uID , (List<String>) c.getList("familiars"));

            } else {
                level.put(uID, c.getInt("level"));
                xp.put(uID, c.getInt("xp"));
                manaNum.put(uID, c.getInt("mana.num"));
                manaCap.put(uID, c.getInt("mana.cap"));
                spellsUnlocked.put(uID, (List<String>) c.getList("spells"));
                manaRegen.put(uID, c.getInt("mana.regen"));
                selectedSpell.put(uID, c.getString("spell.selected"));
                dev.put(uID, c.getBoolean("dev"));
                spellColor.put(uID, new Integer[]{c.getInt("spell.color.r"), c.getInt("spell.color.g"), c.getInt("spell.color.b")});
                familiars.put(uID , (List<String>) c.getList("familiars"));
            }

        } else {
            pFile.createNewFile();
            level.put(uID, 1);
            xp.put(uID, 0);
            manaNum.put(uID, 100);
            manaCap.put(uID, 100);
            manaRegen.put(uID, 1);
            List<String> list = new ArrayList<>();
            list.add("snipe1");
            list.add("burn1");
            spellsUnlocked.put(uID, list);
            selectedSpell.put(uID, "snipe1");
            dev.put(uID, false);
            spellColor.put(uID, new Integer[]{90,168,232});
            List<String> list2 = new ArrayList<>();
            list2.add("owo");
            familiars.put(uID, list2);
        }
        if (interactCooldown.containsKey(uID)){
            interactCooldown.replace(uID, 0);
            spellCooldown.replace(uID, 0);
            spellExpire.replace(uID, 0);
            spellPos.replace(uID, new Location[5]);
            selectedFamiliar.replace(uID, "none");
            if (!(familiarUUID.get(uID).equals("none"))){
                if (Bukkit.getEntity(UUID.fromString(familiarUUID.get(uID))) != null)
                Objects.requireNonNull(Bukkit.getEntity(UUID.fromString(familiarUUID.get(uID)))).remove();
            }
            familiarUUID.replace(uID, "none");
        } else {
            interactCooldown.put(uID, 0);
            spellCooldown.put(uID, 0);
            spellExpire.put(uID, 0);
            spellPos.put(uID, new Location[5]);
            selectedFamiliar.put(uID, "none");
            familiarUUID.put(uID, "none");
            spellDrain.put(uID, 0);
        }
        resetSpellPos(p);

        if (p.getInventory().contains(items.data("RegenDust"))){
            p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100,1));
        }

    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) throws IOException, ReflectiveOperationException {
        Player p = event.getPlayer();
        if (p.getInventory().getItemInMainHand().getType() == Material.AIR){
            return;
        }
        if (getInteractCooldown(p) > 0){
            return;
        }
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
        if (p.getInventory().getItemInMainHand().isSimilar(items.data("iItem"))){
            if (p.getTargetBlock(null, 5).getType() == Material.LODESTONE){
                p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount()-1);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute run give " + p.getName() + " item_frame{EntityTag:{Invisible:1}}");
            } else {
                p.sendMessage(prefix + ChatColor.DARK_GREEN + "You cannot place that item!");
            }
            event.setCancelled(true);
            return;
        }
        if (p.getInventory().getItemInMainHand().isSimilar(items.data("ParrotFamiliar")) || p.getInventory().getItemInMainHand().isSimilar(items.data("CatFamiliar")) || p.getInventory().getItemInMainHand().isSimilar(items.data("DogFamiliar")) || p.getInventory().getItemInMainHand().isSimilar(items.data("FoxFamiliar")) || p.getInventory().getItemInMainHand().isSimilar(items.data("BunnyFamiliar"))){
            event.setCancelled(true);
            List<String> list = familiars.get(p.getUniqueId().toString());
            if (p.getInventory().getItemInMainHand().isSimilar(items.data("ParrotFamiliar"))){
                boolean has = false;
                for (String next : list) {
                    if (next.equals("parrot")) {
                        has = true;
                        break;
                    }
                }
                if (has) {
                    p.sendMessage(prefix + ChatColor.RED + "You already have this Familiar befriended!");
                } else {
                    removeItem(p, items.data("ParrotFamiliar"));
                    list.add("parrot");
                    familiars.replace(p.getUniqueId().toString(), list);
                    p.sendMessage(prefix + ChatColor.GREEN + "You've befriended a parrot!");
                }
            } else if (p.getInventory().getItemInMainHand().isSimilar(items.data("CatFamiliar"))){
                boolean has = false;
                for (String next : list) {
                    if (next.equals("cat")) {
                        has = true;
                        break;
                    }
                }
                if (has) {
                    p.sendMessage(prefix + ChatColor.RED + "You already have this Familiar befriended!");
                } else {
                    removeItem(p, items.data("CatFamiliar"));
                    list.add("cat");
                    familiars.replace(p.getUniqueId().toString(), list);
                    p.sendMessage(prefix + ChatColor.GREEN + "You've befriended a cat!");
                }
            } else if (p.getInventory().getItemInMainHand().isSimilar(items.data("DogFamiliar"))){
                boolean has = false;
                for (String next : list) {
                    if (next.equals("dog")) {
                        has = true;
                        break;
                    }
                }
                if (has) {
                    p.sendMessage(prefix + ChatColor.RED + "You already have this Familiar befriended!");
                } else {
                    removeItem(p, items.data("DogFamiliar"));
                    list.add("dog");
                    familiars.replace(p.getUniqueId().toString(), list);
                    p.sendMessage(prefix + ChatColor.GREEN + "You've befriended a dog!");
                }
            } else if (p.getInventory().getItemInMainHand().isSimilar(items.data("FoxFamiliar"))){
                boolean has = false;
                for (String next : list) {
                    if (next.equals("fox")) {
                        has = true;
                        break;
                    }
                }
                if (has) {
                    p.sendMessage(prefix + ChatColor.RED + "You already have this Familiar befriended!");
                } else {
                    removeItem(p, items.data("FoxFamiliar"));
                    list.add("fox");
                    familiars.replace(p.getUniqueId().toString(), list);
                    p.sendMessage(prefix + ChatColor.GREEN + "You've befriended a fox!");
                }
            }  else if (p.getInventory().getItemInMainHand().isSimilar(items.data("BunnyFamiliar"))){
                boolean has = false;
                for (String next : list) {
                    if (next.equals("bunny")) {
                        has = true;
                        break;
                    }
                }
                if (has) {
                    p.sendMessage(prefix + ChatColor.RED + "You already have this Familiar befriended!");
                } else {
                    removeItem(p, items.data("BunnyFamiliar"));
                    list.add("bunny");
                    familiars.replace(p.getUniqueId().toString(), list);
                    p.sendMessage(prefix + ChatColor.GREEN + "You've befriended a bunny!");
                }
            }
            setInteractCooldown(p, 2);
            return;
        }
        if (wandLevel(p) > -1) {
            if (getSpellDrain(p) > 0){
                p.sendMessage(prefix + ChatColor.DARK_PURPLE + "You are to drained to do any magic!");
                setInteractCooldown(p,2);
                return;
            }
            if (wandLevel(p) == 4 && getLevel(p) < 15){
                p.sendMessage(prefix + ChatColor.DARK_PURPLE + "You are not a high enough level to use this! (" + ChatColor.LIGHT_PURPLE + "15" + ChatColor.DARK_PURPLE + ")");
                setInteractCooldown(p, 2);
                return;
            }
            if (wandLevel(p) == 5 && getLevel(p) < 20){
                p.sendMessage(prefix + ChatColor.DARK_PURPLE + "You are not a high enough level to use this! (" + ChatColor.LIGHT_PURPLE + "20" + ChatColor.DARK_PURPLE + ")");
                setInteractCooldown(p, 2);
                return;
            }
            if (wandLevel(p) == 6 && getLevel(p) < 25){
                p.sendMessage(prefix + ChatColor.DARK_PURPLE + "You are not a high enough level to use this! (" + ChatColor.LIGHT_PURPLE + "25" + ChatColor.DARK_PURPLE + ")");
                setInteractCooldown(p, 2);
                return;
            }
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK){
                if (p.getTargetBlock(null, 5).getType() == Material.LODESTONE){
                    p.sendMessage(prefix + ChatColor.BLUE + "Your level is " + ChatColor.GREEN + getLevel(p) + ChatColor.BLUE + " and your XP is " + ChatColor.GREEN + getXP(p) + " / " + (Math.pow(getLevel(p), 2) * 75) + ChatColor.BLUE + ".");
                    List<String> list = spellsUnlocked.get(p.getUniqueId().toString());
                    p.sendMessage(prefix + ChatColor.BLUE + "--------" + ChatColor.AQUA + "Spells Unlocked" + ChatColor.BLUE + "--------");
                    for (String s : list) {
                        p.sendMessage(prefix + ChatColor.LIGHT_PURPLE + s);
                    }
                    setInteractCooldown(p,2);
                    return;
                }
            }
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR ){
            if (getSpellCooldown(p) == 0) {
                if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    Location front = p.getEyeLocation().add(p.getLocation().getDirection().multiply(1.5));
                    Particle.DustOptions dustOptions = new Particle.DustOptions(org.bukkit.Color.fromRGB(spellColor.get(p.getUniqueId().toString())[0], spellColor.get(p.getUniqueId().toString())[1], spellColor.get(p.getUniqueId().toString())[2]), 2);
                    p.getWorld().spawnParticle(Particle.REDSTONE, front, 1, dustOptions);

                    int np = nextPos(p);
                    if (np != 777) {
                        setSpellPos(p, front, np);

                    }
                    if (np == maxPoints) {
                        setSpellCooldown(p, 40);
                        Location pos1 = getSpellPos(p, 1);
                        Location pos2 = getSpellPos(p, 2);
                        Location pos3 = getSpellPos(p, 3);
                        Location pos4 = getSpellPos(p, 4);
                        Location pos5 = getSpellPos(p, 5);
                        Spells spell = new Spells(pos1, pos2, pos3, pos4, pos5, p, plugin, getMana(p), spellsUnlocked.get(p.getUniqueId().toString()), dev.get(p.getUniqueId().toString()));
                        resetSpellPos(p);
                        new BukkitRunnable(){
                            @Override
                            public void run() {
                                if (spell.getUsedMana() > 0) {
                                    useMana(p, spell.getUsedMana());
                                } else if (spell.getUsedMana() == -1){
                                    openFamiliars(p);
                                } else if (spell.getUsedMana() == -2){
                                    spellDrain.replace(p.getUniqueId().toString(), 9000);
                                    useMana(p, -1);
                                } else if (spell.getUsedMana() == -3){
                                    shadowWandering(p);
                                }
                                if (!spell.getSpellSelected().equals("noChange"))
                                selectedSpell.replace(p.getUniqueId().toString(), spell.getSpellSelected());
                            }
                        }.runTask(plugin);
                    }
                    setInteractCooldown(p, 2);
                    return;
                }
            } else {
                p.sendMessage(prefix + ChatColor.DARK_GRAY + "" + ChatColor.ITALIC + "Spell cooldown!");
            }
        }

            event.setCancelled(true);


            if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR){

                String spell = selectedSpell.get(p.getUniqueId().toString());
                assert spell != null;
                switch (spell) {
                    case "snipe1":
                        if (useMana(p, 7))
                        mainSpells.snipe1(p);
                        break;
                    case "burn1":
                        if (useMana(p, 7))
                        mainSpells.burn1(p);
                        break;
                    case "zap":
                        if (wandLevel(p) > 0) {
                            if (useMana(p, 25))
                            mainSpells.zap(p);
                        } else {
                            p.sendMessage(prefix + ChatColor.DARK_GREEN + "Your wand isn't powerful enough to do this!");
                        }
                        break;
                    case "burst":
                        if (wandLevel(p) > 0) {
                            if (useMana(p, 50))
                            mainSpells.burst(p);
                        } else {
                            p.sendMessage(prefix + ChatColor.DARK_GREEN + "Your wand isn't powerful enough to do this!");
                        }
                        break;
                    case "snipe2":
                        if (wandLevel(p) > 1) {
                            if (useMana(p, 9))
                            mainSpells.snipe2(p);
                        } else {
                            p.sendMessage(prefix + ChatColor.DARK_GREEN + "Your wand isn't powerful enough to do this!");
                        }
                        break;
                    case "locate":
                        if (wandLevel(p) > 1) {
                            if (useMana(p, 75))
                            mainSpells.locate(p);
                        } else {
                            p.sendMessage(prefix + ChatColor.DARK_GREEN + "Your wand isn't powerful enough to do this!");
                        }
                        break;
                    case "burn2":
                        if (wandLevel(p) > 2) {
                            if (useMana(p, 15))
                                mainSpells.burn2(p);
                        } else {
                            p.sendMessage(prefix + ChatColor.DARK_GREEN + "Your wand isn't powerful enough to do this!");
                        }
                        break;
                    case "teleport":
                        if (wandLevel(p) > 2) {
                            if (useMana(p, 25))
                                mainSpells.teleport(p);
                        } else {
                            p.sendMessage(prefix + ChatColor.DARK_GREEN + "Your wand isn't powerful enough to do this!");
                        }
                        break;
                    case "ironWallAttack":
                        if (wandLevel(p) > 3) {
                            if (useMana(p, 30))
                                mainSpells.ironWallAttack(p);
                        } else {
                            p.sendMessage(prefix + ChatColor.DARK_GREEN + "Your wand isn't powerful enough to do this!");
                        }
                        break;
                    case "lifeSteal":
                        if (wandLevel(p) > 3){
                            if (useMana(p,75))
                                mainSpells.lifeSteal(p);
                        } else {
                            p.sendMessage(prefix + ChatColor.DARK_GREEN + "Your wand isn't powerful enough to do this!");
                        }
                        break;
                    case "smite":
                        if (wandLevel(p) > 4){
                            if (useMana(p,20))
                                mainSpells.smite(p);
                        } else {
                            p.sendMessage(prefix + ChatColor.DARK_GREEN + "Your wand isn't powerful enough to do this!");
                        }
                        break;
                    case "fireball":
                        if (wandLevel(p) > 4){
                            if (useMana(p,20))
                                mainSpells.fireball(p);
                        } else {
                            p.sendMessage(prefix + ChatColor.DARK_GREEN + "Your wand isn't powerful enough to do this!");
                        }
                        break;
                    case "iceShards":
                        if (wandLevel(p) > 5){
                            if (useMana(p, 30))
                                mainSpells.iceShards(p);
                        } else {
                            p.sendMessage(prefix + ChatColor.DARK_GREEN + "Your wand isn't powerful enough to do this!");
                        }
                }
            }
            setInteractCooldown(p, 2);

        }
        if (Objects.requireNonNull(p.getInventory().getItemInMainHand().getItemMeta()).getDisplayName().equals(Objects.requireNonNull(items.data("SBHeal").getItemMeta()).getDisplayName())) {
            List<String> list =  spellsUnlocked.get(p.getUniqueId().toString());
            assert list != null;
            if (p.getInventory().getItemInMainHand().isSimilar(items.data("SBHeal"))) {
                if (list.contains("heal")) {
                    p.sendMessage(prefix + ChatColor.RED + "You already have this spell unlocked!");
                } else {
                    removeItem(p, items.data("SBHeal"));
                    list.add("heal");
                    spellsUnlocked.replace(p.getUniqueId().toString(), list);
                    p.sendMessage(prefix + ChatColor.GREEN + "You've Unlocked the Heal spell!");
                }
            } else if (p.getInventory().getItemInMainHand().isSimilar(items.data("SBStrength"))) {
                if (list.contains("strength")) {
                    p.sendMessage(prefix + ChatColor.RED + "You already have this spell unlocked!");
                } else {
                    p.getInventory().remove(items.data("SBStrength"));
                    list.add("strength");
                    spellsUnlocked.replace(p.getUniqueId().toString(), list);
                    p.sendMessage(prefix + ChatColor.GREEN + "You've Unlocked the Strength spell!");
                }
            } else if (p.getInventory().getItemInMainHand().isSimilar(items.data("SBZap"))) {
                if (list.contains("zap")) {
                    p.sendMessage(prefix + ChatColor.RED + "You already have this spell unlocked!");
                } else {
                    p.getInventory().remove(items.data("SBZap"));
                    list.add("zap");
                    spellsUnlocked.replace(p.getUniqueId().toString(), list);
                    p.sendMessage(prefix + ChatColor.GREEN + "You've Unlocked the Zap spell!");
                }
            } else if (p.getInventory().getItemInMainHand().isSimilar(items.data("SBBurst"))) {
                if (list.contains("burst")) {
                    p.sendMessage(prefix + ChatColor.RED + "You already have this spell unlocked!");
                } else {
                    p.getInventory().remove(items.data("SBBurst"));
                    list.add("burst");
                    spellsUnlocked.replace(p.getUniqueId().toString(), list);
                    p.sendMessage(prefix + ChatColor.GREEN + "You've Unlocked the Burst spell!");
                }
            } else if (p.getInventory().getItemInMainHand().isSimilar(items.data("SBKineticElectrocute"))) {
                if (list.contains("kineticElectrocute")) {
                    p.sendMessage(prefix + ChatColor.RED + "You already have this spell unlocked!");
                } else {
                    p.getInventory().remove(items.data("SBKineticElectrocute"));
                    list.add("kineticElectrocute");
                    spellsUnlocked.replace(p.getUniqueId().toString(), list);
                    p.sendMessage(prefix + ChatColor.GREEN + "You've Unlocked the Kinetic Electrocute spell!");
                }
            } else if (p.getInventory().getItemInMainHand().isSimilar(items.data("SBThunderCloud"))) {
                if (list.contains("thunderCloud")) {
                    p.sendMessage(prefix + ChatColor.RED + "You already have this spell unlocked!");
                } else {
                    p.getInventory().remove(items.data("SBThunderCloud"));
                    list.add("thunderCloud");
                    spellsUnlocked.replace(p.getUniqueId().toString(), list);
                    p.sendMessage(prefix + ChatColor.GREEN + "You've Unlocked the Thunder Cloud spell!");
                }
            } else if (p.getInventory().getItemInMainHand().isSimilar(items.data("SBDarkSummoning"))) {
                if (list.contains("darkSummoning")) {
                    p.sendMessage(prefix + ChatColor.RED + "You already have this spell unlocked!");
                } else {
                    p.getInventory().remove(items.data("SBDarkSummoning"));
                    list.add("darkSummoning");
                    spellsUnlocked.replace(p.getUniqueId().toString(), list);
                    p.sendMessage(prefix + ChatColor.GREEN + "You've Unlocked the Dark Summoning spell!");
                }
            } else if (p.getInventory().getItemInMainHand().isSimilar(items.data("SBSnipe2"))) {
                if (list.contains("snipe2")) {
                    p.sendMessage(prefix + ChatColor.RED + "You already have this spell unlocked!");
                } else {
                    p.getInventory().remove(items.data("SBSnipe2"));
                    list.add("snipe2");
                    spellsUnlocked.replace(p.getUniqueId().toString(), list);
                    p.sendMessage(prefix + ChatColor.GREEN + "You've Unlocked the Snipe 2 spell!");
                }
            } else if (p.getInventory().getItemInMainHand().isSimilar(items.data("SBDarkPoisoning"))) {
                if (list.contains("darkPoisoning")) {
                    p.sendMessage(prefix + ChatColor.RED + "You already have this spell unlocked!");
                } else {
                    p.getInventory().remove(items.data("SBDarkPoisoning"));
                    list.add("darkPoisoning");
                    spellsUnlocked.replace(p.getUniqueId().toString(), list);
                    p.sendMessage(prefix + ChatColor.GREEN + "You've Unlocked the Dark Poisoning spell!");
                }
            } else if (p.getInventory().getItemInMainHand().isSimilar(items.data("SBLocate"))) {
                if (list.contains("locate")) {
                    p.sendMessage(prefix + ChatColor.RED + "You already have this spell unlocked!");
                } else {
                    p.getInventory().remove(items.data("SBLocate"));
                    list.add("locate");
                    spellsUnlocked.replace(p.getUniqueId().toString(), list);
                    p.sendMessage(prefix + ChatColor.GREEN + "You've Unlocked the Locate spell!");
                }
            } else if (p.getInventory().getItemInMainHand().isSimilar(items.data("SBBurn2"))) {
                if (list.contains("burn2")) {
                    p.sendMessage(prefix + ChatColor.RED + "You already have this spell unlocked!");
                } else {
                    p.getInventory().remove(items.data("SBBurn2"));
                    list.add("burn2");
                    spellsUnlocked.replace(p.getUniqueId().toString(), list);
                    p.sendMessage(prefix + ChatColor.GREEN + "You've Unlocked the Burn 2 spell!");
                }
            } else if (p.getInventory().getItemInMainHand().isSimilar(items.data("SBTeleport"))) {
                if (list.contains("teleport")) {
                    p.sendMessage(prefix + ChatColor.RED + "You already have this spell unlocked!");
                } else {
                    p.getInventory().remove(items.data("SBTeleport"));
                    list.add("teleport");
                    spellsUnlocked.replace(p.getUniqueId().toString(), list);
                    p.sendMessage(prefix + ChatColor.GREEN + "You've Unlocked the Teleport spell!");
                }
            } else if (p.getInventory().getItemInMainHand().isSimilar(items.data("SBShield"))) {
                if (list.contains("shield")) {
                    p.sendMessage(prefix + ChatColor.RED + "You already have this spell unlocked!");
                } else {
                    p.getInventory().remove(items.data("SBShield"));
                    list.add("shield");
                    spellsUnlocked.replace(p.getUniqueId().toString(), list);
                    p.sendMessage(prefix + ChatColor.GREEN + "You've Unlocked the Shield spell!");
                }
            } else if (p.getInventory().getItemInMainHand().isSimilar(items.data("SBDefence"))) {
                if (list.contains("defence")) {
                    p.sendMessage(prefix + ChatColor.RED + "You already have this spell unlocked!");
                } else {
                    p.getInventory().remove(items.data("SBDefence"));
                    list.add("defence");
                    spellsUnlocked.replace(p.getUniqueId().toString(), list);
                    p.sendMessage(prefix + ChatColor.GREEN + "You've Unlocked the Defence spell!");
                }
            } else if (p.getInventory().getItemInMainHand().isSimilar(items.data("SBDarkCurse"))) {
                if (list.contains("darkCurse")) {
                    p.sendMessage(prefix + ChatColor.RED + "You already have this spell unlocked!");
                } else {
                    p.getInventory().remove(items.data("SBDarkCurse"));
                    list.add("darkCurse");
                    spellsUnlocked.replace(p.getUniqueId().toString(), list);
                    p.sendMessage(prefix + ChatColor.GREEN + "You've Unlocked the Dark Curse spell!");
                }
            } else if (p.getInventory().getItemInMainHand().isSimilar(items.data("SBShadowWandering"))) {
                if (list.contains("shadowWandering")) {
                    p.sendMessage(prefix + ChatColor.RED + "You already have this spell unlocked!");
                } else {
                    p.getInventory().remove(items.data("SBShadowWandering"));
                    list.add("shadowWandering");
                    spellsUnlocked.replace(p.getUniqueId().toString(), list);
                    p.sendMessage(prefix + ChatColor.GREEN + "You've Unlocked Shadow Wandering spell!");
                }
            } else if (p.getInventory().getItemInMainHand().isSimilar(items.data("SBAbsorb"))) {
                if (list.contains("absorb")) {
                    p.sendMessage(prefix + ChatColor.RED + "You already have this spell unlocked!");
                } else {
                    p.getInventory().remove(items.data("SBAbsorb"));
                    list.add("absorb");
                    spellsUnlocked.replace(p.getUniqueId().toString(), list);
                    p.sendMessage(prefix + ChatColor.GREEN + "You've Unlocked Absorb spell!");
                }
            } else if (p.getInventory().getItemInMainHand().isSimilar(items.data("SBFreeze"))) {
                if (list.contains("freeze")) {
                    p.sendMessage(prefix + ChatColor.RED + "You already have this spell unlocked!");
                } else {
                    p.getInventory().remove(items.data("SBFreeze"));
                    list.add("freeze");
                    spellsUnlocked.replace(p.getUniqueId().toString(), list);
                    p.sendMessage(prefix + ChatColor.GREEN + "You've Unlocked Freeze spell!");
                }
            } else if (p.getInventory().getItemInMainHand().isSimilar(items.data("SBIronWallAttack"))) {
                if (list.contains("ironWallAttack")) {
                    p.sendMessage(prefix + ChatColor.RED + "You already have this spell unlocked!");
                } else {
                    p.getInventory().remove(items.data("SBIronWallAttack"));
                    list.add("ironWallAttack");
                    spellsUnlocked.replace(p.getUniqueId().toString(), list);
                    p.sendMessage(prefix + ChatColor.GREEN + "You've Unlocked the Iron Wall Attack spell!");
                }
            } else if (p.getInventory().getItemInMainHand().isSimilar(items.data("SBLifeSteal"))) {
                if (list.contains("lifeSteal")) {
                    p.sendMessage(prefix + ChatColor.RED + "You already have this spell unlocked!");
                } else {
                    p.getInventory().remove(items.data("SBLifeSteal"));
                    list.add("lifeSteal");
                    spellsUnlocked.replace(p.getUniqueId().toString(), list);
                    p.sendMessage(prefix + ChatColor.GREEN + "You've Unlocked the Life Steal spell!");
                }
            } else if (p.getInventory().getItemInMainHand().isSimilar(items.data("SBSmite"))) {
                if (list.contains("smite")) {
                    p.sendMessage(prefix + ChatColor.RED + "You already have this spell unlocked!");
                } else {
                    p.getInventory().remove(items.data("SBSmite"));
                    list.add("smite");
                    spellsUnlocked.replace(p.getUniqueId().toString(), list);
                    p.sendMessage(prefix + ChatColor.GREEN + "You've Unlocked the Smite spell!");
                }
            } else if (p.getInventory().getItemInMainHand().isSimilar(items.data("SBFireball"))) {
                if (list.contains("fireball")) {
                    p.sendMessage(prefix + ChatColor.RED + "You already have this spell unlocked!");
                } else {
                    p.getInventory().remove(items.data("SBFireball"));
                    list.add("fireball");
                    spellsUnlocked.replace(p.getUniqueId().toString(), list);
                    p.sendMessage(prefix + ChatColor.GREEN + "You've Unlocked the Fireball spell!");
                }
            } else if (p.getInventory().getItemInMainHand().isSimilar(items.data("SBTimeBomb"))) {
                if (list.contains("timeBomb")) {
                    p.sendMessage(prefix + ChatColor.RED + "You already have this spell unlocked!");
                } else {
                    p.getInventory().remove(items.data("SBTimeBomb"));
                    list.add("timeBomb");
                    spellsUnlocked.replace(p.getUniqueId().toString(), list);
                    p.sendMessage(prefix + ChatColor.GREEN + "You've Unlocked the Time Bomb spell!");
                }
            } else if (p.getInventory().getItemInMainHand().isSimilar(items.data("SBWither"))) {
                if (list.contains("wither")) {
                    p.sendMessage(prefix + ChatColor.RED + "You already have this spell unlocked!");
                } else {
                    p.getInventory().remove(items.data("SBWither"));
                    list.add("wither");
                    spellsUnlocked.replace(p.getUniqueId().toString(), list);
                    p.sendMessage(prefix + ChatColor.GREEN + "You've Unlocked the Wither spell!");
                }
            } else if (p.getInventory().getItemInMainHand().isSimilar(items.data("SBDrown"))) {
                if (list.contains("drown")) {
                    p.sendMessage(prefix + ChatColor.RED + "You already have this spell unlocked!");
                } else {
                    p.getInventory().remove(items.data("SBDrown"));
                    list.add("drown");
                    spellsUnlocked.replace(p.getUniqueId().toString(), list);
                    p.sendMessage(prefix + ChatColor.GREEN + "You've Unlocked the Drown spell!");
                }
            } else if (p.getInventory().getItemInMainHand().isSimilar(items.data("SBIceShards"))) {
                if (list.contains("iceShards")) {
                    p.sendMessage(prefix + ChatColor.RED + "You already have this spell unlocked!");
                } else {
                    p.getInventory().remove(items.data("SBIceShards"));
                    list.add("iceShards");
                    spellsUnlocked.replace(p.getUniqueId().toString(), list);
                    p.sendMessage(prefix + ChatColor.GREEN + "You've Unlocked the Ice Shards spell!");
                }
            }
            setInteractCooldown(p, 2);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void placeBlock(BlockPlaceEvent event){
        if (event.getItemInHand().isSimilar(items.data("MagicBlock")) || event.getItemInHand().isSimilar(items.data("CompressedMagicBlock")) || event.getItemInHand().isSimilar(items.data("iItem")) || event.getItemInHand().isSimilar(items.data("RegenDust")) || event.getItemInHand().isSimilar(items.data("AlchemyBlock"))){
            event.setCancelled(true);
            event.getPlayer().sendMessage(prefix + ChatColor.DARK_GREEN + "You cannot place that!");
        }
        if (event.getBlock().getType() == Material.GOLD_ORE){
            event.getBlock().setMetadata("placed", new FixedMetadataValue(plugin, true));
        }
    }
    @EventHandler public void breakBlock(BlockBreakEvent event) {
        if (event.getBlock().getType() == Material.GOLD_ORE) {
            if (!event.isCancelled()) {
                Iterator hmIterator = manaMineLocs.entrySet().iterator();
                    while (hmIterator.hasNext()) {
                        Map.Entry mapElement = (Map.Entry)hmIterator.next();
                        Location l = (Location) mapElement.getValue();
                        if (event.getBlock().getLocation().equals(l)) {
                            Objects.requireNonNull(l.getWorld()).dropItem(l, items.data("MagicDust"));
                            event.setCancelled(true);
                            event.getBlock().setType(Material.AIR);
                            final Particle.DustOptions dustOptions = new Particle.DustOptions(org.bukkit.Color.fromRGB(219, 182, 31), 2);
                            Location loc = new Location(l.getWorld(), l.getX() + 0.5, l.getY() + 0.5, l.getZ() + 0.5);
                            l.getWorld().spawnParticle(Particle.REDSTONE, loc, 5, 0.3, 0.3, 0.3, dustOptions);
                            return;
                        }
                    }
                if (!(event.getBlock().hasMetadata("placed")))
                    if (dropChance != 0)
                        if (rand.nextInt(100) <= dropChance) {
                            event.setCancelled(true);
                            event.getBlock().setType(Material.AIR);
                            Location ll = event.getBlock().getLocation();
                            Objects.requireNonNull(ll.getWorld()).dropItem(ll, items.data("MagicDust"));
                            final Particle.DustOptions dustOptions = new Particle.DustOptions(org.bukkit.Color.fromRGB(219, 182, 31), 2);
                            Location loc = new Location(ll.getWorld(), ll.getX() + 0.5, ll.getY() + 0.5, ll.getZ() + 0.5);
                            ll.getWorld().spawnParticle(Particle.REDSTONE, loc, 5, 0.3, 0.3, 0.3, dustOptions);
                        }

            }
        }
    }
    @EventHandler
    public void target(EntityTargetEvent event){
        if (event.getEntity() instanceof WitherSkeleton){
            if (event.getEntity().hasMetadata("magic")){
                if (event.getTarget() instanceof Player){
                    if (event.getEntity().hasMetadata(event.getTarget().getUniqueId().toString())){
                        double radius = 10D;
                        List<Entity> near = Objects.requireNonNull(event.getEntity().getWorld()).getEntities();
                        for (Entity b : near) {
                            if (b.getLocation().distance(event.getEntity().getLocation()) <= radius) {
                                if (b instanceof LivingEntity) {
                                    if (b != event.getTarget()) {
                                        if (b != event.getEntity()) {
                                            event.setTarget(b);
                                            return;
                                        }
                                    }
                                }
                            }
                        }
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
    Random rand = new Random();
    @EventHandler
    private void playerPVPEvent (EntityDamageByEntityEvent event){
        if (event.getDamager() instanceof LightningStrike || event.getDamager() instanceof Fireball){
            if (event.getDamager().hasMetadata("magic")){
                Player damager = null;
                for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                    if (event.getDamager().hasMetadata(player.getUniqueId().toString())){
                        damager = player;
                        break;
                    }
                }
                if (damager != null){

                        event.getEntity().getWorld().spawnParticle(Particle.CRIT_MAGIC, event.getEntity().getLocation(), 10);

                } else {
                    event.setCancelled(true);
                }
            }
        }
        if (event.getDamager() instanceof Player) {
            if (event.getEntity() instanceof EnderCrystal){
                if (event.getEntity().hasMetadata("magic")){
                    event.setCancelled(true);
                }
            }
            if (wandLevel((Player) event.getDamager()) > -1){
                if (event.isCancelled()){
                    if (event.getEntity().getFireTicks() > 0){
                        event.getEntity().setFireTicks(0);
                    }
                    PotionEffect effect = ((Player) event.getDamager()).getPotionEffect( PotionEffectType.POISON );
                    if ( effect != null && effect.getAmplifier() == 2 )
                    {
                        ((Player) event.getDamager()).removePotionEffect(PotionEffectType.POISON);
                    }
                }
            }
            if (selectedFamiliar.get(event.getDamager().getUniqueId().toString()).equals("dog")){
                if (rand.nextInt(2) == 1) {
                    event.setDamage(event.getDamage()+(event.getDamage()/4));

                }
            }
        }

    }
    @EventHandler
    public void onPickup(PlayerPickupArrowEvent event){
        if (event.getArrow().hasMetadata("magic")){
            event.setCancelled(true);
        }
    }
    public void replenishMana(Player p) throws IOException {
        String uID = p.getUniqueId().toString();
        if (getSpellDrain(p) > 0){
            return;
        }
        if (manaNum.get(uID) < manaCap.get(uID)){
            int num = manaNum.get(uID);
            manaNum.replace(uID, num+manaRegen.get(uID));
            if (manaNum.get(uID) > manaCap.get(uID)){
                manaNum.replace(uID, manaCap.get(uID));
            }
        }
    }
    public int getLevel(Player p){
        return level.get(p.getUniqueId().toString());
    }
    public int getXP(Player p){
        return xp.get(p.getUniqueId().toString());
    }
    public void setInteractCooldown(Player p, int i) throws IOException {
        interactCooldown.replace(p.getUniqueId().toString(), i);
    }
    public int getInteractCooldown(Player p){
        return interactCooldown.get(p.getUniqueId().toString());
    }
    public void setSpellExpire(Player p, int i) throws IOException {
        spellExpire.replace(p.getUniqueId().toString(), i);
    }
    public int getSpellExpire(Player p){
        return spellExpire.get(p.getUniqueId().toString());
    }
    public void setSpellPos(Player p, Location loc, int i) throws IOException {
        Location[] locations = spellPos.get(p.getUniqueId().toString());
        if (i > 0 && i < 6){
            locations[i-1] = loc;
            spellPos.replace(p.getUniqueId().toString(), locations);
        }
        setSpellExpire(p, 200);
    }
    public void resetSpellPos(Player p) throws IOException {
        spellPos.replace(p.getUniqueId().toString(), new Location[5]);
    }
    public int nextPos(Player p){
        Location[] locations = spellPos.get(p.getUniqueId().toString());
        for (int i = 1; i < maxPoints+1; i++){
            if (locations[i-1] == null){
                return i;
            }
        }
        return 777;

    }
    public Location getSpellPos(Player p, int i){
        Location[] locations = spellPos.get(p.getUniqueId().toString());
        return locations[i-1];
    }
    public int getSpellCooldown(Player p){
        return spellCooldown.get(p.getUniqueId().toString());
    }
    public void setSpellCooldown(Player p, int i) throws IOException {
        spellCooldown.replace(p.getUniqueId().toString(), i);
    }
    public void reduceSpellDrain(Player p){
        if (spellDrain.get(p.getUniqueId().toString()) > 0){
            spellDrain.replace(p.getUniqueId().toString(), spellDrain.get(p.getUniqueId().toString())-1);
        }
    }
    public int getSpellDrain(Player p){
        return spellDrain.get(p.getUniqueId().toString());
    }
    public int wandLevel(Player p){
        if (p.getInventory().getItemInMainHand().isSimilar(items.data("BasicWand"))){
            return 0;
        } else if (p.getInventory().getItemInMainHand().isSimilar(items.data("ProsaicWand"))){
            return 1;
        } else if (p.getInventory().getItemInMainHand().isSimilar(items.data("ShadowWand"))){
            return 2;
        } else if (p.getInventory().getItemInMainHand().isSimilar(items.data("EnhancedWand"))){
            return 3;
        } else if (p.getInventory().getItemInMainHand().isSimilar(items.data("WardenWand"))){
            return 4;
        } else if (p.getInventory().getItemInMainHand().isSimilar(items.data("DestructionWand"))){
            return 5;
        } else if (p.getInventory().getItemInMainHand().isSimilar(items.data("AquaWand"))){
            return 6;
        }
        return -1;
    }
    public void addXP(Player p, int i) {
        xp.replace(p.getUniqueId().toString(), xp.get(p.getUniqueId().toString()) + i);
        if (Math.pow(level.get(p.getUniqueId().toString()), 2) * 75 <= xp.get(p.getUniqueId().toString())){
            xp.replace(p.getUniqueId().toString() , (int) ( xp.get(p.getUniqueId().toString())-(Math.pow(level.get(p.getUniqueId().toString()), 2) * 75)));
            level.replace(p.getUniqueId().toString(), level.get(p.getUniqueId().toString()) + 1);
            levelUp(p, level.get(p.getUniqueId().toString()));
        }
    }
    public void levelUp(Player p, int i) {
        Bukkit.broadcastMessage(prefix + ChatColor.GREEN + p.getName() + ChatColor.YELLOW + " has leveled up to level " + i + "!");

        if (i % 10 == 0){
            manaCap.replace(p.getUniqueId().toString(), manaCap.get(p.getUniqueId().toString()) + 20);
            p.sendMessage(prefix + ChatColor.GREEN + "Your mana capacity was upgraded!");
        } else if (i % 5 == 0){
            manaRegen.replace(p.getUniqueId().toString(), manaRegen.get(p.getUniqueId().toString()) + 1);
            p.sendMessage(prefix + ChatColor.GREEN + "Your mana regeneration was upgraded!");
        }
    }
    public int getMana(Player p){
        return manaNum.get(p.getUniqueId().toString());
    }
    public boolean useMana(Player p, int i){
        if (i == -1){
            addXP(p, manaNum.get(p.getUniqueId().toString()));
            manaNum.replace(p.getUniqueId().toString(), -1);
        }
        if (getMana(p) >= i) {
            manaNum.replace(p.getUniqueId().toString(), manaNum.get(p.getUniqueId().toString()) - i);
            addXP(p, i);
            return true;
        } else {
            p.sendMessage(prefix + org.bukkit.ChatColor.RED + "You don't have enough mana!");
            return false;
        }
    }
    public void runBackup(){
                for (Player player : Bukkit.getServer().getOnlinePlayers()){
                    int pLevel = level.get(player.getUniqueId().toString());
                    int pXp = xp.get(player.getUniqueId().toString());
                    int pManaNum = manaNum.get(player.getUniqueId().toString());
                    int pManaCap = manaCap.get(player.getUniqueId().toString());
                    int pManaRegen = manaRegen.get(player.getUniqueId().toString());
                    List<String> pSpellsUnlocked = spellsUnlocked.get(player.getUniqueId().toString());
                    List<String> pFamiliars = familiars.get(player.getUniqueId().toString());
                    String pSelectedSpell = selectedSpell.get(player.getUniqueId().toString());
                    boolean pDev = dev.get(player.getUniqueId().toString());
                    Integer[] pSpellColor = spellColor.get(player.getUniqueId().toString());
                    YamlConfiguration c = new YamlConfiguration();
                    c.set("level", pLevel);
                    c.set("xp", pXp);
                    c.set("mana.num", pManaNum);
                    c.set("mana.cap", pManaCap);
                    c.set("mana.regen", pManaRegen);
                    c.set("spells", pSpellsUnlocked);
                    c.set("spell.selected", pSelectedSpell);
                    c.set("familiars", pFamiliars);
                    c.set("dev", pDev);
                    c.set("spell.color.r", pSpellColor[0]);
                    c.set("spell.color.g", pSpellColor[1]);
                    c.set("spell.color.b", pSpellColor[2]);
                    File pFile = new File(playerdata+File.separator+player.getUniqueId().toString()+".yml");
                    if (pFile.exists()) {
                        try {
                            c.save(pFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println(ChatColor.DARK_RED + "<!> NotMagic tried to save " + player.getName() + " to their file but they don't have one! <!>");
                    }
                }
    }
    @EventHandler
    public void onDisable(PluginDisableEvent event){
        if (event.getPlugin().equals(plugin)) {
            Iterator hmIterator = familiarUUID.entrySet().iterator();
            while (hmIterator.hasNext()) {
                Map.Entry mapElement = (Map.Entry) hmIterator.next();
                if (!(mapElement.getValue().equals("none"))) {
                    Objects.requireNonNull(Bukkit.getEntity(UUID.fromString((String) mapElement.getValue()))).remove();
                }
            }
            runBackup();
            for (World world : Bukkit.getWorlds()) {
                for (Entity e : world.getEntities()) {
                    if (customMobs.containsKey(e.getUniqueId().toString())) {
                        e.remove();
                    }
                    if (e.hasMetadata("familiar")) {
                        e.remove();
                    }
                    if (e.hasMetadata("rremove")) {
                        e.remove();
                    }
                    if (e instanceof Player) {
                        Player p = (Player) e;
                        if (p.getShoulderEntityLeft() != null)
                            if (Objects.requireNonNull(p.getShoulderEntityLeft()).hasMetadata("familiar")) {
                                p.setShoulderEntityLeft(null);
                            }
                        if (p.getShoulderEntityRight() != null)
                            if (Objects.requireNonNull(p.getShoulderEntityRight()).hasMetadata("familiar")) {
                                p.setShoulderEntityRight(null);
                            }
                    }
                }
            }
        }
    }
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {

        Player player = event.getPlayer();
        if (shadow.containsKey(player.getUniqueId().toString()))
            if (shadow.get(player.getUniqueId().toString())){
                player.setInvisible(false);
                player.setWalkSpeed(0.2f);
                shadow.replace(player.getUniqueId().toString(), false);
            }
        int pLevel = level.get(player.getUniqueId().toString());
        int pXp = xp.get(player.getUniqueId().toString());
        int pManaNum = manaNum.get(player.getUniqueId().toString());
        int pManaCap = manaCap.get(player.getUniqueId().toString());
        int pManaRegen = manaRegen.get(player.getUniqueId().toString());
        List<String> pSpellsUnlocked = spellsUnlocked.get(player.getUniqueId().toString());
        List<String> pFamiliars = familiars.get(player.getUniqueId().toString());
        String pSelectedSpell = selectedSpell.get(player.getUniqueId().toString());
        boolean pDev = dev.get(player.getUniqueId().toString());
        Integer[] pSpellColor = spellColor.get(player.getUniqueId().toString());
        YamlConfiguration c = new YamlConfiguration();
        c.set("level", pLevel);
        c.set("xp", pXp);
        c.set("mana.num", pManaNum);
        c.set("mana.cap", pManaCap);
        c.set("mana.regen", pManaRegen);
        c.set("spells", pSpellsUnlocked);
        c.set("spell.selected", pSelectedSpell);
        c.set("familiars", pFamiliars);
        c.set("dev", pDev);
        c.set("spell.color.r", pSpellColor[0]);
        c.set("spell.color.g", pSpellColor[1]);
        c.set("spell.color.b", pSpellColor[2]);
        File pFile = new File(playerdata + File.separator + player.getUniqueId().toString() + ".yml");
        if (pFile.exists()) {
            try {
                c.save(pFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (event.getPlayer().getShoulderEntityLeft() != null)
        if (Objects.requireNonNull(event.getPlayer().getShoulderEntityLeft()).hasMetadata("familiar")){
            event.getPlayer().setShoulderEntityLeft(null);
        }
        if (event.getPlayer().getShoulderEntityRight() != null)
        if (Objects.requireNonNull(event.getPlayer().getShoulderEntityRight()).hasMetadata("familiar")){
            event.getPlayer().setShoulderEntityRight(null);
        }
    }
    public void openFamiliars(Player p){
        Inventory inventory = Bukkit.createInventory(p, 18, ChatColor.BLUE + p.getName() + "'s Familiars");
        List<String> list = familiars.get(p.getUniqueId().toString());
        ItemStack parrot = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
        ItemMeta Meta = parrot.getItemMeta();
        assert Meta != null;
        Meta.setDisplayName(ChatColor.DARK_GRAY + "Unknown Familiar");
        parrot.setItemMeta(Meta);
        ItemStack cat = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
        Meta = cat.getItemMeta();
        assert Meta != null;
        Meta.setDisplayName(ChatColor.DARK_GRAY + "Unknown Familiar");
        cat.setItemMeta(Meta);
        ItemStack dog = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
        Meta = dog.getItemMeta();
        assert Meta != null;
        Meta.setDisplayName(ChatColor.DARK_GRAY + "Unknown Familiar");
        dog.setItemMeta(Meta);
        ItemStack fox = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
        Meta = fox.getItemMeta();
        assert Meta != null;
        Meta.setDisplayName(ChatColor.DARK_GRAY + "Unknown Familiar");
        fox.setItemMeta(Meta);
        ItemStack bunny = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
        Meta = bunny.getItemMeta();
        assert Meta != null;
        Meta.setDisplayName(ChatColor.DARK_GRAY + "Unknown Familiar");
        bunny.setItemMeta(Meta);
        ItemStack b = new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1);
        Meta = b.getItemMeta();
        assert Meta != null;
        Meta.setDisplayName(ChatColor.of(new Color(170, 180, 181)) + "o.o");
        b.setItemMeta(Meta);
        ItemStack ba = new ItemStack(Material.BARRIER, 1);
        Meta = ba.getItemMeta();
        assert Meta != null;
        Meta.setDisplayName(ChatColor.RED + "Remove Familiar");
        ba.setItemMeta(Meta);

        if (list.contains("parrot"))
            parrot = items.data("ParrotFamiliar");
        if (list.contains("cat"))
            cat = items.data("CatFamiliar");
        if (list.contains("dog"))
            dog = items.data("DogFamiliar");
        if (list.contains("fox"))
            fox = items.data("FoxFamiliar");
        if (list.contains("bunny"))
            bunny = items.data("BunnyFamiliar");

        ItemStack[] contents = new ItemStack[]{parrot,cat,dog,fox,bunny,b,b,b,b,b,b,b,b,b,b,b,b,ba};
        inventory.setContents(contents);
        p.openInventory(inventory);
        p.updateInventory();
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        if (event.getView().getTitle().equals(ChatColor.BLUE + event.getWhoClicked().getName() + "'s Familiars")){
            ItemStack ba = new ItemStack(Material.BARRIER, 1);
            ItemMeta Meta = ba.getItemMeta();
            assert Meta != null;
            Meta.setDisplayName(ChatColor.RED + "Remove Familiar");
            ba.setItemMeta(Meta);
            event.setCancelled(true);

            if (Objects.equals(event.getCurrentItem(), items.data("ParrotFamiliar"))){
                event.getView().close();
                if (!selectedFamiliar.get(event.getWhoClicked().getUniqueId().toString()).equals("parrot")) {
                    event.getWhoClicked().sendMessage(prefix + ChatColor.AQUA + "You have summoned your parrot!");
                    spawnParrot((Player) event.getWhoClicked());
                } else {
                    event.getWhoClicked().sendMessage(prefix + ChatColor.RED + "You already have this familiar selected!");
                }
            } else if (Objects.equals(event.getCurrentItem(), items.data("CatFamiliar"))){
                event.getView().close();
                if (!selectedFamiliar.get(event.getWhoClicked().getUniqueId().toString()).equals("cat")) {
                    event.getWhoClicked().sendMessage(prefix + ChatColor.AQUA + "You have summoned your cat!");
                    spawnCat((Player) event.getWhoClicked());
                } else {
                    event.getWhoClicked().sendMessage(prefix + ChatColor.RED + "You already have this familiar selected!");
                }
            } else if (Objects.equals(event.getCurrentItem(), items.data("DogFamiliar"))){
                event.getView().close();
                if (!selectedFamiliar.get(event.getWhoClicked().getUniqueId().toString()).equals("dog")) {
                    event.getWhoClicked().sendMessage(prefix + ChatColor.AQUA + "You have summoned your dog!");
                    spawnDog((Player) event.getWhoClicked());
                } else {
                    event.getWhoClicked().sendMessage(prefix + ChatColor.RED + "You already have this familiar selected!");
                }
            } else if (Objects.equals(event.getCurrentItem(), items.data("FoxFamiliar"))){
                event.getView().close();
                if (!selectedFamiliar.get(event.getWhoClicked().getUniqueId().toString()).equals("fox")) {
                    event.getWhoClicked().sendMessage(prefix + ChatColor.AQUA + "You have summoned your fox!");
                    spawnFox((Player) event.getWhoClicked());
                } else {
                    event.getWhoClicked().sendMessage(prefix + ChatColor.RED + "You already have this familiar selected!");
                }
            } else if (Objects.equals(event.getCurrentItem(), items.data("BunnyFamiliar"))){
                event.getView().close();
                if (!selectedFamiliar.get(event.getWhoClicked().getUniqueId().toString()).equals("bunny")) {
                    event.getWhoClicked().sendMessage(prefix + ChatColor.AQUA + "You have summoned your bunny!");
                    spawnBunny((Player) event.getWhoClicked());
                } else {
                    event.getWhoClicked().sendMessage(prefix + ChatColor.RED + "You already have this familiar selected!");
                }
            } else if (Objects.equals(event.getCurrentItem(), ba)){
                event.getView().close();
                if (!(selectedFamiliar.get(event.getWhoClicked().getUniqueId().toString()).equals("none"))) {
                    event.getWhoClicked().sendMessage(prefix + ChatColor.AQUA + "You have removed your familiar!");
                    selectedFamiliar.replace(event.getWhoClicked().getUniqueId().toString(), "none");
                    if (!(familiarUUID.get(event.getWhoClicked().getUniqueId().toString()).equals("none"))){
                        Objects.requireNonNull(Bukkit.getEntity(UUID.fromString(familiarUUID.get(event.getWhoClicked().getUniqueId().toString())))).remove();
                    }
                    familiarUUID.replace(event.getWhoClicked().getUniqueId().toString(), "none");
                } else {
                    event.getWhoClicked().sendMessage(prefix + ChatColor.RED + "You don't have a familiar active!");
                }
            }
        }
    }
    public void spawnBunny(Player p){
        if (!(familiarUUID.get(p.getUniqueId().toString()).equals("none"))){
            Objects.requireNonNull(Bukkit.getEntity(UUID.fromString(familiarUUID.get(p.getUniqueId().toString())))).remove();
        }
        selectedFamiliar.replace(p.getUniqueId().toString(), "bunny");
        Rabbit f = p.getWorld().spawn(p.getLocation(), Rabbit.class);
        familiarUUID.replace(p.getUniqueId().toString(), f.getUniqueId().toString());
        f.setInvulnerable(true);
        f.setAgeLock(true);
        f.setCanPickupItems(false);
        f.setRemoveWhenFarAway(false);
        f.setPersistent(true);
        f.setCustomName(ChatColor.GREEN + p.getName() + "'s Bunny");
        f.setCustomNameVisible(true);
        f.setMetadata("familiar", new FixedMetadataValue(plugin, true));
        new BukkitRunnable(){
            @Override
            public void run() {
                if (selectedFamiliar.get(p.getUniqueId().toString()).equals("bunny") && p.isOnline()){
                    f.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 25, 1));
                    if ((f.getWorld().equals(p.getWorld()))) {
                        if (f.getLocation().distance(p.getLocation()) > 15) {
                            f.teleport(p);
                        }
                    } else {
                        f.teleport(p);
                    }
                    if (rand.nextInt(50) == 1){
                        int num = rand.nextInt(14);
                        p.playSound(f.getLocation(), Sound.ENTITY_PLAYER_BURP,1,1);
                        if (num == 1){
                            f.getWorld().dropItemNaturally(f.getLocation(), new ItemStack(Material.BREAD, rand.nextInt(32)));
                        } else if (num == 2){
                            f.getWorld().dropItemNaturally(f.getLocation(), new ItemStack(Material.HONEY_BOTTLE, rand.nextInt(32)));
                        } else if (num == 3){
                            f.getWorld().dropItemNaturally(f.getLocation(), new ItemStack(Material.PUMPKIN_PIE, rand.nextInt(32)));
                        } else if (num == 4){
                            f.getWorld().dropItemNaturally(f.getLocation(), new ItemStack(Material.CAKE, 1));
                        } else if (num == 5){
                            f.getWorld().dropItemNaturally(f.getLocation(), new ItemStack(Material.COOKIE, rand.nextInt(32)));
                        } else if (num == 6){
                            f.getWorld().dropItemNaturally(f.getLocation(), new ItemStack(Material.MELON_SLICE, rand.nextInt(32)));
                        } else if (num == 7){
                            f.getWorld().dropItemNaturally(f.getLocation(), new ItemStack(Material.MUSHROOM_STEW, 1));
                        } else if (num == 8){
                            f.getWorld().dropItemNaturally(f.getLocation(), new ItemStack(Material.BEETROOT_SOUP, 1));
                        } else if (num == 9){
                            f.getWorld().dropItemNaturally(f.getLocation(), new ItemStack(Material.RABBIT_STEW, 1));
                        } else if (num == 10){
                            f.getWorld().dropItemNaturally(f.getLocation(), new ItemStack(Material.DRIED_KELP, rand.nextInt(32)));
                        } else if (num == 11){
                            f.getWorld().dropItemNaturally(f.getLocation(), new ItemStack(Material.CARROT, rand.nextInt(32)));
                        } else if (num == 12){
                            f.getWorld().dropItemNaturally(f.getLocation(), new ItemStack(Material.GOLDEN_CARROT, rand.nextInt(32)));
                        } else if (num == 13){
                            f.getWorld().dropItemNaturally(f.getLocation(), new ItemStack(Material.BAKED_POTATO, rand.nextInt(32)));
                        }
                    }
                } else {
                    this.cancel();
                    f.remove();
                    if (!p.isOnline()){
                        selectedFamiliar.replace(p.getUniqueId().toString(), "none");
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 20L);
    }
    public void spawnFox(Player p){
        if (!(familiarUUID.get(p.getUniqueId().toString()).equals("none"))){
            Objects.requireNonNull(Bukkit.getEntity(UUID.fromString(familiarUUID.get(p.getUniqueId().toString())))).remove();
        }
        selectedFamiliar.replace(p.getUniqueId().toString(), "fox");
        Fox f = p.getWorld().spawn(p.getLocation(), Fox.class);
        familiarUUID.replace(p.getUniqueId().toString(), f.getUniqueId().toString());
        f.setInvulnerable(true);
        f.setFirstTrustedPlayer(p);
        f.setAgeLock(true);
        f.setCanPickupItems(false);
        f.setRemoveWhenFarAway(false);
        f.setPersistent(true);
        f.setCustomName(ChatColor.GREEN + p.getName() + "'s Fox");
        f.setCustomNameVisible(true);
        f.setMetadata("familiar", new FixedMetadataValue(plugin, true));
        new BukkitRunnable(){
            @Override
            public void run() {
                if (selectedFamiliar.get(p.getUniqueId().toString()).equals("fox") && p.isOnline()){
                    if ((Objects.requireNonNull((Entity) f).getWorld().equals(Objects.requireNonNull(p).getWorld()))) {
                        if (f.getLocation().distance(p.getLocation()) > 15) {
                            f.teleport(p);
                        }
                    } else {
                        f.teleport(p);
                    }
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 25, 0));
                } else {
                    this.cancel();
                    f.remove();
                    if (!p.isOnline()){
                        selectedFamiliar.replace(p.getUniqueId().toString(), "none");
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 20L);
    }
    public void spawnDog(Player p){
        if (!(familiarUUID.get(p.getUniqueId().toString()).equals("none"))){
            Objects.requireNonNull(Bukkit.getEntity(UUID.fromString(familiarUUID.get(p.getUniqueId().toString())))).remove();
        }
        selectedFamiliar.replace(p.getUniqueId().toString(), "dog");
        Wolf f = p.getWorld().spawn(p.getLocation(), Wolf.class);
        familiarUUID.replace(p.getUniqueId().toString(), f.getUniqueId().toString());
        f.setInvulnerable(true);
        f.setTamed(true);
        f.setOwner(p);
        f.setCustomName(ChatColor.GREEN + p.getName() + "'s Dog");
        f.setCustomNameVisible(true);
        f.setMetadata("familiar", new FixedMetadataValue(plugin, true));
        f.setAgeLock(true);
        new BukkitRunnable(){
            @Override
            public void run() {
                if (selectedFamiliar.get(p.getUniqueId().toString()).equals("dog") && p.isOnline()){
                } else {
                    this.cancel();
                    f.remove();
                    if (!p.isOnline()){
                        selectedFamiliar.replace(p.getUniqueId().toString(), "none");
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 20L);
    }
    @EventHandler
    public void onDamage(EntityDamageEvent event){

        if (event.getCause() == EntityDamageEvent.DamageCause.FALL && event.getEntity() instanceof Player){
            if (selectedFamiliar.get(event.getEntity().getUniqueId().toString()).equals("cat")){
                event.setDamage(event.getDamage()-(event.getDamage()/5));
            }
        }
        if (event.getEntity() instanceof  LivingEntity){
            if (event.getEntity().hasMetadata("familiar")){
                event.setCancelled(true);
            }
        }
    }
    public void spawnCat(Player p){
        if (!(familiarUUID.get(p.getUniqueId().toString()).equals("none"))){
            Objects.requireNonNull(Bukkit.getEntity(UUID.fromString(familiarUUID.get(p.getUniqueId().toString())))).remove();
        }
        selectedFamiliar.replace(p.getUniqueId().toString(), "cat");
        Cat f = p.getWorld().spawn(p.getLocation(), Cat.class);
        familiarUUID.replace(p.getUniqueId().toString(), f.getUniqueId().toString());
        f.setInvulnerable(true);
        f.setTamed(true);
        f.setOwner(p);
        f.setCustomName(ChatColor.GREEN + p.getName() + "'s Cat");
        f.setCustomNameVisible(true);
        f.setAgeLock(true);
        f.setMetadata("familiar", new FixedMetadataValue(plugin, true));
        new BukkitRunnable(){
            @Override
            public void run() {
                if (selectedFamiliar.get(p.getUniqueId().toString()).equals("cat") && p.isOnline()){
                } else {
                    this.cancel();
                    f.remove();
                    if (!p.isOnline()){
                        selectedFamiliar.replace(p.getUniqueId().toString(), "none");
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 20L);
    }

    public void spawnParrot(Player p){
        if (!(familiarUUID.get(p.getUniqueId().toString()).equals("none"))){
            Objects.requireNonNull(Bukkit.getEntity(UUID.fromString(familiarUUID.get(p.getUniqueId().toString())))).remove();
        }
        selectedFamiliar.replace(p.getUniqueId().toString(), "parrot");
        Parrot f = p.getWorld().spawn(p.getLocation(), Parrot.class);
        familiarUUID.replace(p.getUniqueId().toString(), f.getUniqueId().toString());
        f.setInvulnerable(true);
        f.setTamed(true);
        f.setOwner(p);
        f.setCustomName(ChatColor.GREEN + p.getName() + "'s Parrot");
        f.setMetadata("familiar", new FixedMetadataValue(plugin, true));
        f.setCustomNameVisible(true);
        new BukkitRunnable(){

            @Override
            public void run() {
                if (selectedFamiliar.get(p.getUniqueId().toString()).equals("parrot") && p.isOnline()){
                } else {
                    this.cancel();
                    f.remove();
                    if (!p.isOnline()){
                        selectedFamiliar.replace(p.getUniqueId().toString(), "none");
                    }
                    if (p.getShoulderEntityLeft() != null)
                        if (Objects.requireNonNull(p.getShoulderEntityLeft()).hasMetadata("familiar")){
                            p.setShoulderEntityLeft(null);
                        }
                    if (p.getShoulderEntityRight() != null)
                        if (Objects.requireNonNull(p.getShoulderEntityRight()).hasMetadata("familiar")){
                            p.setShoulderEntityRight(null);
                        }
                }
            }
        }.runTaskTimer(plugin, 0, 20L);
        new BukkitRunnable(){
            boolean found = false;
            @Override
            public void run() {
                if (selectedFamiliar.get(p.getUniqueId().toString()).equals("parrot") && p.isOnline()){
                    int radius = 15;

                    for (int x = -(radius); x <= radius; x++) {
                        for (int y = -(radius); y <= radius; y++) {
                            for (int z = -(radius); z <= radius; z++) {
                                Location la = f.getLocation().getBlock().getRelative(x,y,z).getLocation();
                                Block l = la.getBlock();
                                if (l.getType() == Material.ANCIENT_DEBRIS || l.getType() == Material.DIAMOND_ORE){
                                    found = true;
                                    break;
                                }
                            }
                            if (found)
                                break;
                        }
                        if (found)
                            break;
                    }
                    if (found) {

                        f.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, f.getLocation(), 20, 2, 2, 2);
                        p.playSound(f.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                p.playSound(f.getLocation(), Sound.BLOCK_BELL_USE, 1, 1);
                            }
                        }.runTaskLater(plugin, 10L);
                        found = false;
                    }
                    if ((Objects.requireNonNull(f).getWorld().equals(Objects.requireNonNull(p).getWorld()))) {

                        if (!(p.getWorld().equals(f.getWorld()))) {
                            f.teleport(p);
                        }
                    } else {

                        f.teleport(p);
                    }
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 200L);
    }
    public boolean removeItem(Player p, ItemStack item){
        if (p.getInventory().contains(item)){
            ItemStack[] contents = p.getInventory().getContents();
            for (int i = 0; i < contents.length; i++){
                if (contents[i].isSimilar(item)){
                    if (contents[i].getAmount() == item.getAmount()){
                        contents[i] = null;
                        p.getInventory().setContents(contents);
                        return true;
                    } else if (contents[i].getAmount() > item.getAmount()){
                        ItemStack newItem = contents[i];
                        newItem.setAmount(contents[i].getAmount() - item.getAmount());
                        contents[i] = newItem;
                        p.getInventory().setContents(contents);
                        return true;
                    }
                }
            }
        }
        return false;
    }
    @EventHandler
    public void brew(AlchemyBrewEvent event){
        if (event.getType().equals("basic")){
            addXP(event.getPlayer(), 15);
        } else if (event.getType().equals("advanced")){
            addXP(event.getPlayer(), 25);
        } else if (event.getType().equals("ritual")){
            addXP(event.getPlayer(), 50);
        }
    }
    @EventHandler
    public void statChange(ChangeStatsEvent event){
        if (event.getLevel() > level.get(event.getPlayer().getUniqueId().toString())){
            new BukkitRunnable(){
                @Override
                public void run() {
                    for (int i = 0; i < 10; i++) {
                        if (event.getLevel() > level.get(event.getPlayer().getUniqueId().toString())) {
                            addXP(event.getPlayer(), 1000);
                        } else {
                            this.cancel();
                        }
                    }
                }
            }.runTaskTimer(plugin, 0, 1);
        } else {
            level.replace(event.getPlayer().getUniqueId().toString(), event.getLevel());
        }
    }

    @EventHandler
    public void xpChange(XPChangeEvent event){
        addXP(event.getPlayer(),event.getXP());
    }

    public void shadowWandering(Player p){
        p.setInvisible(true);
        p.setWalkSpeed(0.3f);
        if (shadow.containsKey(p.getUniqueId().toString())){
            shadow.replace(p.getUniqueId().toString(), true);
        } else {
            shadow.put(p.getUniqueId().toString(), true);
        }
        new BukkitRunnable(){
            @Override
            public void run() {
                if (p.isOnline()){
                    if (getMana(p) > 4){
                        manaNum.replace(p.getUniqueId().toString(), manaNum.get(p.getUniqueId().toString())-4);

                        for (int y = p.getLocation().getBlockY(); y > p.getLocation().getBlockY()-15; y--){
                            Location l = new Location(p.getWorld(), p.getLocation().getX(), y, p.getLocation().getZ());
                            if (!(l.getBlock().getType().isAir())){
                                Particle.DustOptions dustOptions = new Particle.DustOptions(org.bukkit.Color.fromRGB(0, 0, 0), 2);
                                for (int i = 0; i < 10; i++)
                                p.spawnParticle(Particle.REDSTONE, new Location(l.getWorld(),l.getX() + (((double)rand.nextInt(8) / 10)-0.4),l.getY() + 0.8,l.getZ() + (((double)rand.nextInt(8) / 10)-0.4)), 10, dustOptions);
                                break;
                            }
                        }
                        if (!(p.isOnline())){
                            this.cancel();
                            p.setInvisible(false);
                            p.setWalkSpeed(0.2f);
                        }
                    } else {
                        this.cancel();
                        p.setInvisible(false);
                        p.setWalkSpeed(0.2f);
                        shadow.replace(p.getUniqueId().toString(), false);
                        p.sendMessage(prefix + ChatColor.GRAY + "Shadow Wandering wore off!");
                    }

                } else {
                    this.cancel();
                    p.setInvisible(false);
                    p.setWalkSpeed(0.2f);
                    shadow.replace(p.getUniqueId().toString(), false);
                }
            }
        }.runTaskTimer(plugin,0L,10);
    }
}
