package me.jadenp.notmagic;

import me.jadenp.notmagic.Alchemy.RevisedAlchemy;
import me.jadenp.notmagic.RevisedClasses.*;
import me.jadenp.notmagic.RevisedClasses.Structures.Industrial;
import me.jadenp.notmagic.SpellWorkshop.CraftingInterface;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * <TODO>
 *     Mana Potions
 *     Admin custom spells
 *     Transfer old spells
 *     Add all the Essences + extra itemstack
 *     change essence new locations to location.getrelative
 *     Workshop spell books work
 *     Can cast workshop spells
 *     change constant variables and pattern size in workshopspell
 *     Make sure all spells work
 *     Add more lower level spells
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
    public RevisedEvents eventClass;
    public Commands commandClass;
    // files - need to make the other files reloadable
    public CraftingInterface craftingInterface;
    public File playerRecords = new File(this.getDataFolder()+File.separator+"player-records");
    public File backups = new File(this.getDataFolder()+File.separator+"backups");

    private static NotMagic instance;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

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


        if (!playerRecords.exists()) {
            playerRecords.mkdir();
        }
        if (!backups.exists()) {
            backups.mkdir();
        }
        // loading configurations

        // starting event listening and command listening
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
        getServer().getPluginManager().registerEvents(new RevisedAlchemy(this), this);
        commandClass.setEventClass(eventClass);
        getServer().getPluginManager().registerEvents(eventClass.magicClass, this);
        craftingInterface = new CraftingInterface(this);
        // creating files if they don't exist
        this.saveDefaultConfig();

        try {
            getServer().getPluginManager().registerEvents(new Industrial(), this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        loadConfig();

        // initializing scheduled tasks to run while server is online
        new OnlineTasks(this);

        // custom recipes
        RecipeChoice magicDust = new RecipeChoice.ExactChoice(Items.data("MagicDust"));
        NamespacedKey key = new NamespacedKey(this, "basicwand");
        ShapedRecipe basicWand = new ShapedRecipe(key, Items.data("BasicWand"));
        basicWand.shape("*%%", "%&%", "%%*");
        basicWand.setIngredient('*', Material.AIR);
        basicWand.setIngredient('%', magicDust);
        basicWand.setIngredient('&', Material.STICK);
        getServer().addRecipe(basicWand);

        NamespacedKey key2 = new NamespacedKey(this, "magicblock");
        ShapedRecipe magicBlock = new ShapedRecipe(key2, Items.data("MagicBlock"));
        magicBlock.shape("***", "***", "***");
        magicBlock.setIngredient('*', magicDust);
        getServer().addRecipe(magicBlock);

        RecipeChoice mBlock = new RecipeChoice.ExactChoice(Items.data("MagicBlock"));
        RecipeChoice bWand = new RecipeChoice.ExactChoice(Items.data("BasicWand"));
        NamespacedKey key3 = new NamespacedKey(this, "prosaicwand");
        ShapedRecipe prosaicWand = new ShapedRecipe(key3, Items.data("ProsaicWand"));
        prosaicWand.shape("*%%", "%&%", "%%*");
        prosaicWand.setIngredient('*', Material.AIR);
        prosaicWand.setIngredient('%', mBlock);
        prosaicWand.setIngredient('&', bWand);
        getServer().addRecipe(prosaicWand);

        RecipeChoice shadowRose = new RecipeChoice.ExactChoice(Items.data("shadowRose"));
        RecipeChoice pWand = new RecipeChoice.ExactChoice(Items.data("ProsaicWand"));
        NamespacedKey key4 = new NamespacedKey(this, "shadowwand");
        ShapedRecipe shadowWand = new ShapedRecipe(key4, Items.data("ShadowWand"));
        shadowWand.shape("*#*", "%&%", "%%%");
        shadowWand.setIngredient('*', Material.AIR);
        shadowWand.setIngredient('%', mBlock);
        shadowWand.setIngredient('&', pWand);
        shadowWand.setIngredient('#', shadowRose);
        getServer().addRecipe(shadowWand);

        RecipeChoice sWand = new RecipeChoice.ExactChoice(Items.data("ShadowWand"));
        NamespacedKey key5 = new NamespacedKey(this, "enhancedwand");
        ShapedRecipe enhancedWand = new ShapedRecipe(key5, Items.data("EnhancedWand"));
        enhancedWand.shape("*%*", "%&%", "*%*");
        enhancedWand.setIngredient('*', Material.EMERALD_ORE);
        enhancedWand.setIngredient('%', Material.EMERALD_BLOCK);
        enhancedWand.setIngredient('&', sWand);
        getServer().addRecipe(enhancedWand);

        NamespacedKey key6 = new NamespacedKey(this, "invizitemframe");
        ShapedRecipe invizItemFrame = new ShapedRecipe(key6, Items.data("iItem"));
        invizItemFrame.shape("*%*", "%&%", "*%*");
        invizItemFrame.setIngredient('*', Material.AIR);
        invizItemFrame.setIngredient('%', magicDust);
        invizItemFrame.setIngredient('&', Material.ITEM_FRAME);
        getServer().addRecipe(invizItemFrame);

        NamespacedKey key7 = new NamespacedKey(this, "alccontroller");
        ShapedRecipe alcController = new ShapedRecipe(key7, Items.data("AlcController"));
        alcController.shape("*%*", "%&%", "*%*");
        alcController.setIngredient('*', Material.AIR);
        alcController.setIngredient('%', magicDust);
        alcController.setIngredient('&', Material.IRON_NUGGET);
        getServer().addRecipe(alcController);

        NamespacedKey key8 = new NamespacedKey(this, "alcdust");
        ShapedRecipe alcDust = new ShapedRecipe(key8, Items.data("AlchemyDust"));
        alcDust.shape("*%*", "%&%", "*%*");
        alcDust.setIngredient('*', Material.AIR);
        alcDust.setIngredient('%', magicDust);
        alcDust.setIngredient('&', Material.BLAZE_POWDER);
        getServer().addRecipe(alcDust);

        RecipeChoice alchemyDust = new RecipeChoice.ExactChoice(Items.data("AlchemyDust"));
        NamespacedKey key9 = new NamespacedKey(this, "alcblock");
        ShapedRecipe alcBlock = new ShapedRecipe(key9, Items.data("AlchemyBlock"));
        alcBlock.shape("***", "***", "***");
        alcBlock.setIngredient('*', alchemyDust);
        getServer().addRecipe(alcBlock);

        RecipeChoice compressedMagicBlock = new RecipeChoice.ExactChoice(Items.data("CompressedMagicBlock"));
        NamespacedKey key10 = new NamespacedKey(this, "zyniumfragment");
        ShapedRecipe zyniumFragment = new ShapedRecipe(key10, Items.data("AlchemyBlock"));
        zyniumFragment.shape("*^*", "^$^", "*^*");
        zyniumFragment.setIngredient('*', mBlock);
        zyniumFragment.setIngredient('^', compressedMagicBlock);
        zyniumFragment.setIngredient('$', Material.NETHERITE_BLOCK);
        getServer().addRecipe(zyniumFragment);

        RecipeChoice wardenRemanents = new RecipeChoice.ExactChoice(Items.data("wardenRemnants"));
        RecipeChoice eWand = new RecipeChoice.ExactChoice(Items.data("EnhancedWand"));
        NamespacedKey key11 = new NamespacedKey(this, "wardenwand");
        ShapedRecipe wardenWand = new ShapedRecipe(key11, Items.data("WardenWand"));
        wardenWand.shape("^*%", "*$*", "^*^");
        wardenWand.setIngredient('*', mBlock);
        wardenWand.setIngredient('^', magicDust);
        wardenWand.setIngredient('%', wardenRemanents);
        wardenWand.setIngredient('$', eWand);
        getServer().addRecipe(wardenWand);

    }

    public static NotMagic getInstance() {
        return instance;
    }


    public void loadConfig(){
        this.reloadConfig();
        Language.setLanguage();
        Settings.loadConfig();
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
