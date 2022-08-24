package me.jadenp.notmagic;

import me.jadenp.notmagic.Alchemy.RevisedAlchemy;
import me.jadenp.notmagic.RevisedClasses.CustomSpell;
import me.jadenp.notmagic.RevisedClasses.Items;
import me.jadenp.notmagic.RevisedClasses.RevisedEvents;
import me.jadenp.notmagic.RevisedClasses.Spell;
import me.jadenp.notmagic.SpellWorkshop.CraftingInterface;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.units.qual.A;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.md_5.bungee.api.ChatColor.COLOR_CHAR;

/**
 * <TODO>
 *     Mana Potions
 *     Admin custom spells
 *     Transfer old spells
 *     Add all the Essences + extra itemstack
 *     Workshop spell books work
 *     Can cast workshop spells
 * </TODO>
 * To-Do:
 * PlayerData
 *  - save when leave -
 *  - autosave -
 *
 * Magic
 *  - Spell cast w/ general directions
 *  - copy few spells over
 *  - compare spells
 *
 *
 *

 */


public final class NotMagic extends JavaPlugin {
    Items items = new Items();
    public Plugin plugin;
    public RevisedEvents eventClass;
    public Commands commandClass;
    public ArrayList<String> language = new ArrayList<>();
    private String prefix;
    // files - need to make the other files reloadable
    public File customSpells = new File(this.getDataFolder() + File.separator + "customSpells.yml"); // custom spells file
    public CraftingInterface craftingInterface;
    public File manaMines = new File(this.getDataFolder() + File.separator + "mana-mines.yml");
    public File alcStations = new File(this.getDataFolder() + File.separator + "alchemy-stations.yml");
    public File playerRecords = new File(plugin.getDataFolder()+File.separator+"player-records");
    public File backups = new File(plugin.getDataFolder()+File.separator+"backups");
    public File recordKey = new File(playerRecords + File.separator + "record-key.yml");
    public File craftedSpells = new File(playerRecords + File.separator + "craftedSpells.yml");

    @Override
    public void onEnable() {
        // Plugin startup logic

        // cool NotMagic display on startup
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + "_________________________________________________________");
        Bukkit.getConsoleSender().sendMessage(ChatColor.BLUE + " _   _         _    " + ChatColor.LIGHT_PURPLE + "___  ___               _       ");
        Bukkit.getConsoleSender().sendMessage(ChatColor.BLUE + "| \\ | |       | |  " + ChatColor.LIGHT_PURPLE + " |  \\/  |              (_)      ");
        Bukkit.getConsoleSender().sendMessage(ChatColor.BLUE + "|  \\| |  ___  | |_ " + ChatColor.LIGHT_PURPLE + " | .  . |  __ _   __ _  _   ___ ");
        Bukkit.getConsoleSender().sendMessage(ChatColor.BLUE + "| . ` | / _ \\ | __|" + ChatColor.LIGHT_PURPLE + " | |\\/| | / _` | / _` || | / __|");
        Bukkit.getConsoleSender().sendMessage(ChatColor.BLUE + "| |\\  || (_) || |_ " + ChatColor.LIGHT_PURPLE + " | |  | || (_| || (_| || || (__ ");
        Bukkit.getConsoleSender().sendMessage(ChatColor.BLUE + "\\_| \\_/ \\___/  \\__|" + ChatColor.LIGHT_PURPLE + " \\_|  |_/ \\__,_| \\__, ||_| \\___|");
        Bukkit.getConsoleSender().sendMessage("                                   " + ChatColor.LIGHT_PURPLE + "  __/ |         ");
        Bukkit.getConsoleSender().sendMessage("      " + ChatColor.DARK_GREEN + "Made by: Not_Jaden           " + ChatColor.LIGHT_PURPLE + " |___/          ");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + "_________________________________________________________");
        //                        Made by: Not_Jaden

        // starting event listening and command listening
        plugin = this;
        commandClass = new Commands(this);
        Objects.requireNonNull(this.getCommand("nm")).setExecutor(commandClass);
        Objects.requireNonNull(this.getCommand("nmtop")).setExecutor(commandClass);
        Objects.requireNonNull(this.getCommand("nm")).setTabCompleter(commandClass);
        try {
            eventClass = new RevisedEvents(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        getServer().getPluginManager().registerEvents(eventClass, this);
        getServer().getPluginManager().registerEvents(new RevisedAlchemy(plugin), this);
        commandClass.setEventClass(eventClass);
        getServer().getPluginManager().registerEvents(eventClass.getMagicClass(), this);
        craftingInterface = new CraftingInterface(this);
        // creating files if they don't exist
        File config = new File(this.getDataFolder() + File.separator + "config.yml");
        if (!config.exists()) {
            this.saveDefaultConfig();
        }


        if (!manaMines.exists()) {
            try {
                manaMines.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!alcStations.exists()) {
            try {
                alcStations.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!customSpells.exists()) {
            saveResource("customSpells.yml", false);

            /*YamlConfiguration configuration = new YamlConfiguration();
            // basic spell info
            configuration.set("use-built-in-spells", true);
            configuration.set("spells.1.name", "CustomSpell");
            configuration.set("spells.1.main-spell", false);
            configuration.set("spells.1.mp-cost", 10);
            configuration.set("spells.1.cast-time", 2);
            configuration.set("spells.1.cooldown", 200);
            configuration.set("spells.1.required-level", 9001);
            List<String> spellPattern = new ArrayList<>(Arrays.asList("Start", "Up", "Down", "Left", "Right", "LeftUp", "LeftDown", "RightUp", "RightDown"));
            configuration.set("spells.1.cast-pattern", spellPattern);*/

            // spellCreation - use a set of strings to add actions to the spell
            // Particles
            // particle:<spigotParticle> - selects a particle from spigot to use https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Particle.html
            // can combine the front/behind/etc - need target or player before. ex: spawn:player1front
            // spawn:<player/playerEye/[x]front/[x]behind/[x]left/[x]right/[x]up/[x]down> - spawn particles at players feet, player's eye, x blocks in front/behind/left/right/up/down from player
            // spawn:<[radius]{entity:<SpigotEntity>}/[radius]air/[x]x[y]y[z]z> - spawns particles at nearby entities, air, or in a relative location from the player
            // spawn:<target/targetEye/[x]front/[x]behind/[x]left/[x]right/[x]up/[x]down> - will spawn particles at the target (found later) - does for each target if there are multiple
            // amount:<integer> - amount of particles to spawn - default is 1
            // delay:<ticks> - when the particles will spawn - default is 0
            // vvv These are only for directional particles vvv
            // direction: - same as spawn parameters
            // speed:<float> how fast the particles will go
            // ^^^                                          ^^^
            //
            // Targeting
            // you can replace EntityType with "all" to target all entities
            // you can add a '!' after "Entity:" to exclude a specific entityType from the list ex: target:5{radiusEntity:all}{radiusEntity:!cow}
            // delay does not effect target, but it can effect when the damage occurs
            // target:[x]{radiusEntity:<EntityType>} - targets entities in a radius [x] https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/entity/EntityType.html
            // target:[x]{forwardEntity:<EntityType>} - targets entities in the player's line of sight with a maximum distance of [x]
            // target:[x]block - targets block players are looking at with a maximum distance of [x]
            // blocks cannot be damaged
            // damage:[y]<damageType> - how much damage [y] it should do and what type of damage (optional) https://hub.spigotmc.org/javadocs/spigot/org/bukkit/event/entity/EntityDamageEvent.DamageCause.html
            // damageDuration:[a]every[b]for[c]<damageType> - replaces damage and hurts target for [a] hit points every [b] ticks for [c] time in ticks ex: damageDuration:1every25for900magic - this is what an un-extended poison potion does
            // delay:<ticks> - when the damage will occur
            //
            // Other Effects
            // move:<player/target><[x]front/[x]behind/[x]left/[x]right/[x]up/[x]down> - adds a vector to the player/target's motion - requires player or target
            // teleport:<player/target><[x]front/[x]behind/[x]left/[x]right/[x]up/[x]down> - teleports player or target to a relative location
            // teleport:<player/target><[x]x[y]y[z]z[world]> - teleports player or target to a specific location (world is optional)
            // delay:<ticks> - when the effect will occur
            // effect:<player/target>[ticks]<spigotEffect>[strength]<true/false> - give the player, or targets a potion effect for a desired amount of ticks and strength. true/false is optional - default is false. strength is optional - default is 0

            /*List<String> creation = new ArrayList<>(Arrays.asList("particle:END_ROD spawn:5air amount:100", "particle:CLOUD spawn:playerEye1front direction:targetEye speed:2 amount:5 delay:20", "target:10{radiusEntity:all}{radiusEntity:!player} damage:10 effect:target200LEVITATION delay:20"));
            configuration.set("spells.1.actions", creation);
            try {
                configuration.save(customSpells);
            } catch (IOException e) {
                e.printStackTrace();
            }*/
        }

        if (!playerRecords.exists()) {
            playerRecords.mkdir();
        }
        if (!backups.exists()) {
            backups.mkdir();
        }
        if (!recordKey.exists()){
            try {
                recordKey.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!craftedSpells.exists()){
            try {
                craftedSpells.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // loading configurations
        loadConfig();


        // initializing scheduled tasks to run while server is online
        new OnlineTasks(plugin);

        // custom recipes
        RecipeChoice magicDust = new RecipeChoice.ExactChoice(items.data("MagicDust"));
        NamespacedKey key = new NamespacedKey(plugin, "basicwand");
        ShapedRecipe basicWand = new ShapedRecipe(key, items.data("BasicWand"));
        basicWand.shape("*%%", "%&%", "%%*");
        basicWand.setIngredient('*', Material.AIR);
        basicWand.setIngredient('%', magicDust);
        basicWand.setIngredient('&', Material.STICK);
        getServer().addRecipe(basicWand);

        NamespacedKey key2 = new NamespacedKey(plugin, "magicblock");
        ShapedRecipe magicBlock = new ShapedRecipe(key2, items.data("MagicBlock"));
        magicBlock.shape("***", "***", "***");
        magicBlock.setIngredient('*', magicDust);
        getServer().addRecipe(magicBlock);

        RecipeChoice mBlock = new RecipeChoice.ExactChoice(items.data("MagicBlock"));
        RecipeChoice bWand = new RecipeChoice.ExactChoice(items.data("BasicWand"));
        NamespacedKey key3 = new NamespacedKey(plugin, "prosaicwand");
        ShapedRecipe prosaicWand = new ShapedRecipe(key3, items.data("ProsaicWand"));
        prosaicWand.shape("*%%", "%&%", "%%*");
        prosaicWand.setIngredient('*', Material.AIR);
        prosaicWand.setIngredient('%', mBlock);
        prosaicWand.setIngredient('&', bWand);
        getServer().addRecipe(prosaicWand);

        RecipeChoice shadowRose = new RecipeChoice.ExactChoice(items.data("shadowRose"));
        RecipeChoice pWand = new RecipeChoice.ExactChoice(items.data("ProsaicWand"));
        NamespacedKey key4 = new NamespacedKey(plugin, "shadowwand");
        ShapedRecipe shadowWand = new ShapedRecipe(key4, items.data("ShadowWand"));
        shadowWand.shape("*#*", "%&%", "%%%");
        shadowWand.setIngredient('*', Material.AIR);
        shadowWand.setIngredient('%', mBlock);
        shadowWand.setIngredient('&', pWand);
        shadowWand.setIngredient('#', shadowRose);
        getServer().addRecipe(shadowWand);

        RecipeChoice sWand = new RecipeChoice.ExactChoice(items.data("ShadowWand"));
        NamespacedKey key5 = new NamespacedKey(plugin, "enhancedwand");
        ShapedRecipe enhancedWand = new ShapedRecipe(key5, items.data("EnhancedWand"));
        enhancedWand.shape("*%*", "%&%", "*%*");
        enhancedWand.setIngredient('*', Material.EMERALD_ORE);
        enhancedWand.setIngredient('%', Material.EMERALD_BLOCK);
        enhancedWand.setIngredient('&', sWand);
        getServer().addRecipe(enhancedWand);

        NamespacedKey key6 = new NamespacedKey(plugin, "invizitemframe");
        ShapedRecipe invizItemFrame = new ShapedRecipe(key6, items.data("iItem"));
        invizItemFrame.shape("*%*", "%&%", "*%*");
        invizItemFrame.setIngredient('*', Material.AIR);
        invizItemFrame.setIngredient('%', magicDust);
        invizItemFrame.setIngredient('&', Material.ITEM_FRAME);
        getServer().addRecipe(invizItemFrame);

        NamespacedKey key7 = new NamespacedKey(plugin, "alccontroller");
        ShapedRecipe alcController = new ShapedRecipe(key7, items.data("AlcController"));
        alcController.shape("*%*", "%&%", "*%*");
        alcController.setIngredient('*', Material.AIR);
        alcController.setIngredient('%', magicDust);
        alcController.setIngredient('&', Material.IRON_NUGGET);
        getServer().addRecipe(alcController);

        NamespacedKey key8 = new NamespacedKey(plugin, "alcdust");
        ShapedRecipe alcDust = new ShapedRecipe(key8, items.data("AlchemyDust"));
        alcDust.shape("*%*", "%&%", "*%*");
        alcDust.setIngredient('*', Material.AIR);
        alcDust.setIngredient('%', magicDust);
        alcDust.setIngredient('&', Material.BLAZE_POWDER);
        getServer().addRecipe(alcDust);

        RecipeChoice alchemyDust = new RecipeChoice.ExactChoice(items.data("AlchemyDust"));
        NamespacedKey key9 = new NamespacedKey(plugin, "alcblock");
        ShapedRecipe alcBlock = new ShapedRecipe(key9, items.data("AlchemyBlock"));
        alcBlock.shape("***", "***", "***");
        alcBlock.setIngredient('*', alchemyDust);
        getServer().addRecipe(alcBlock);

        RecipeChoice compressedMagicBlock = new RecipeChoice.ExactChoice(items.data("CompressedMagicBlock"));
        NamespacedKey key10 = new NamespacedKey(plugin, "zyniumfragment");
        ShapedRecipe zyniumFragment = new ShapedRecipe(key10, items.data("AlchemyBlock"));
        zyniumFragment.shape("*^*", "^$^", "*^*");
        zyniumFragment.setIngredient('*', mBlock);
        zyniumFragment.setIngredient('^', compressedMagicBlock);
        zyniumFragment.setIngredient('$', Material.NETHERITE_BLOCK);
        getServer().addRecipe(zyniumFragment);

        RecipeChoice wardenRemanents = new RecipeChoice.ExactChoice(items.data("wardenRemnants"));
        RecipeChoice eWand = new RecipeChoice.ExactChoice(items.data("EnhancedWand"));
        NamespacedKey key11 = new NamespacedKey(plugin, "wardenwand");
        ShapedRecipe wardenWand = new ShapedRecipe(key11, items.data("WardenWand"));
        wardenWand.shape("^*%", "*$*", "^*^");
        wardenWand.setIngredient('*', mBlock);
        wardenWand.setIngredient('^', magicDust);
        wardenWand.setIngredient('%', wardenRemanents);
        wardenWand.setIngredient('$', eWand);
        getServer().addRecipe(wardenWand);

    }



    public void loadConfig(){
        language.clear();
        prefix = color(plugin.getConfig().getString("prefix"));
        // 0 - spell-cooldown
        language.add(color(plugin.getConfig().getString("spell-cooldown")));
        // 1 - spell-insufficient-mana
        language.add(color(plugin.getConfig().getString("spell-insufficient-mana")));
        // 2 - cast-spell
        language.add(color(plugin.getConfig().getString("cast-spell")));
        // 3 - main-spell
        language.add(color(plugin.getConfig().getString("main-spell")));
        // 4 - item-too-powerful
        language.add(color(plugin.getConfig().getString("item-too-powerful")));
        // 5 - spell-too-powerful
        language.add(color(plugin.getConfig().getString("spell-too-powerful")));

        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(customSpells);
        List<CustomSpell> spells = new ArrayList<>();
        int i = 1;
        while (configuration.getString("spells." + i + ".name") != null){
            CustomSpell spell = new CustomSpell(configuration.getString("spells." + i + ".name"), configuration.getBoolean("spells." + i + ".main-spell"), configuration.getInt("spells." + i + ".mp-cost"), configuration.getInt("spells." + i + ".cast-time"), configuration.getInt("spells." + i + ".cooldown"), configuration.getInt("spells." + i + ".required-level"), configuration.getStringList("spells." + i + ".castPattern"), configuration.getStringList("spells." + i + ".actions"), configuration.getStringList("spells." + i + ".lore"), this, eventClass.getMagicClass().getSpellIndex());
            spells.add(spell);
            i++;
        }
        boolean useCustomSpells = configuration.getBoolean("use-built-in-spells");

    }
    public String color(String str){
        str = ChatColor.translateAlternateColorCodes('&', str);
        return translateHexColorCodes("&#","", str);
    }
    public String translateHexColorCodes(String startTag, String endTag, String message)
    {
        final Pattern hexPattern = Pattern.compile(startTag + "([A-Fa-f0-9]{6})" + endTag);
        Matcher matcher = hexPattern.matcher(message);
        StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);
        while (matcher.find())
        {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, COLOR_CHAR + "x"
                    + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1)
                    + COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3)
                    + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5)
            );
        }
        return matcher.appendTail(buffer).toString();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        for (Player player : Bukkit.getOnlinePlayers()){
            if (player.getOpenInventory().getTitle().equals(craftingInterface.getWorkshopName())){
                player.closeInventory();
            }
        }
        eventClass.saveData();
    }
}
