package me.jadenp.notmagic.RevisedClasses;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import me.jadenp.notmagic.NotMagic;
import me.jadenp.notmagic.SpellWorkshop.WorkshopSpell;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Score;
import org.checkerframework.checker.units.qual.C;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


public class Industrial implements Listener {
    /**
     * Conduit magic core
     *
     */

    private List<MultiBlockStructures> multiBlockStructures = new ArrayList<>();

    private final static Map<Integer[], Material> magicStorage = new HashMap<Integer[], Material>(){{
        put(new Integer[]{1,0,1}, Material.WAXED_COPPER_BLOCK);
        put(new Integer[]{-1,0,1}, Material.WAXED_COPPER_BLOCK);
        put(new Integer[]{1,0,-1}, Material.WAXED_COPPER_BLOCK);
        put(new Integer[]{-1,0,-1}, Material.WAXED_COPPER_BLOCK);
        put(new Integer[]{1,1,1}, Material.WAXED_COPPER_BLOCK);
        put(new Integer[]{-1,1,1}, Material.WAXED_COPPER_BLOCK);
        put(new Integer[]{1,1,-1}, Material.WAXED_COPPER_BLOCK);
        put(new Integer[]{-1,1,-1}, Material.WAXED_COPPER_BLOCK);
        put(new Integer[]{1,2,1}, Material.WAXED_COPPER_BLOCK);
        put(new Integer[]{-1,2,1}, Material.WAXED_COPPER_BLOCK);
        put(new Integer[]{1,2,-1}, Material.WAXED_COPPER_BLOCK);
        put(new Integer[]{-1,2,-1}, Material.WAXED_COPPER_BLOCK);
        put(new Integer[]{1,3,1}, Material.WAXED_COPPER_BLOCK);
        put(new Integer[]{-1,3,1}, Material.WAXED_COPPER_BLOCK);
        put(new Integer[]{1,3,-1}, Material.WAXED_COPPER_BLOCK);
        put(new Integer[]{-1,3,-1}, Material.WAXED_COPPER_BLOCK);
        put(new Integer[]{1,4,1}, Material.WAXED_CUT_COPPER_SLAB);
        put(new Integer[]{-1,4,1}, Material.WAXED_CUT_COPPER_SLAB);
        put(new Integer[]{1,4,-1}, Material.WAXED_CUT_COPPER_SLAB);
        put(new Integer[]{-1,4,-1}, Material.WAXED_CUT_COPPER_SLAB);
        put(new Integer[]{0,4,-1}, Material.WAXED_CUT_COPPER_SLAB);
        put(new Integer[]{0,4,1}, Material.WAXED_CUT_COPPER_SLAB);
        put(new Integer[]{-1,4,0}, Material.WAXED_CUT_COPPER_SLAB);
        put(new Integer[]{1,4,0}, Material.WAXED_CUT_COPPER_SLAB);
        put(new Integer[]{1,1,0}, Material.TINTED_GLASS);
        put(new Integer[]{-1,1,0}, Material.TINTED_GLASS);
        put(new Integer[]{0,1,1}, Material.TINTED_GLASS);
        put(new Integer[]{0,1,-1}, Material.TINTED_GLASS);
        put(new Integer[]{1,2,0}, Material.TINTED_GLASS);
        put(new Integer[]{-1,2,0}, Material.TINTED_GLASS);
        put(new Integer[]{0,2,1}, Material.TINTED_GLASS);
        put(new Integer[]{0,2,-1}, Material.TINTED_GLASS);
        put(new Integer[]{1,3,0}, Material.TINTED_GLASS);
        put(new Integer[]{-1,3,0}, Material.TINTED_GLASS);
        put(new Integer[]{0,3,1}, Material.TINTED_GLASS);
        put(new Integer[]{0,3,-1}, Material.TINTED_GLASS);
        put(new Integer[]{0,1,0}, Material.GRAY_CARPET);
    }};

    private Map<Location, CustomBlocks> customBlocks = new HashMap<>();
    private final File specialBlockFile = new File(NotMagic.getInstance().getDataFolder() + File.separator + "special-blocks.json");
    private final Gson gson;
    public Industrial() throws IOException {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        builder.registerTypeAdapter(CustomBlocks.class, new CustomBlocksAdapter());
        gson = builder.create();
        // load placed magic cores
        if (specialBlockFile.exists()){
            Type mapType = new TypeToken<Map<Location, CustomBlocks>>() {}.getType();
            customBlocks = gson.fromJson(new String(Files.readAllBytes(Paths.get(specialBlockFile.getPath()))), mapType);
        }
        RecipeChoice magicDust = new RecipeChoice.ExactChoice(Items.data("MagicDust"));
        NamespacedKey key = new NamespacedKey(NotMagic.getInstance(), "smallCatalystCrystal");
        ShapedRecipe smallCatalystCrystal = new ShapedRecipe(key, Items.data("smallCatalystCrystal"));
        smallCatalystCrystal.shape("*%*", "%&%", "*%*");
        smallCatalystCrystal.setIngredient('*', Material.AIR);
        smallCatalystCrystal.setIngredient('%', magicDust);
        smallCatalystCrystal.setIngredient('&', Material.SMALL_AMETHYST_BUD);
        Bukkit.getServer().addRecipe(smallCatalystCrystal);

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(NotMagic.getInstance(), "mediumCatalystCrystal"), Items.data("mediumCatalystCrystal"));
        recipe.shape("%%%", "%&%", "%%%");
        recipe.setIngredient('%', magicDust);
        recipe.setIngredient('&', Material.MEDIUM_AMETHYST_BUD);
        Bukkit.getServer().addRecipe(recipe);

        RecipeChoice mediumCatalystCrystal = new RecipeChoice.ExactChoice(Items.data("mediumCatalystCrystal"));
        ShapedRecipe recipe1 = new ShapedRecipe(new NamespacedKey(NotMagic.getInstance(), "weakMagicCore"), Items.data("weakMagicCore"));
        recipe1.shape("*%*", "%&%", "*%*");
        recipe1.setIngredient('%', magicDust);
        recipe1.setIngredient('&', Material.WAXED_COPPER_BLOCK);
        recipe1.setIngredient('*', mediumCatalystCrystal);
        Bukkit.getServer().addRecipe(recipe1);

        new BukkitRunnable(){
            @Override
            public void run() {
                final Map<Location, CustomBlocks> blocks = customBlocks;
                Bukkit.getScheduler().runTaskAsynchronously(NotMagic.getInstance(), () -> {
                    try {
                        saveSpecialBlocks(blocks);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

            }
        }.runTaskTimer(NotMagic.getInstance(), 36000, 36000);
    }

    private void saveSpecialBlocks(Map<Location, CustomBlocks> customBlocks) throws IOException {
        if (customBlocks.size() > 0) {
            try {
                specialBlockFile.createNewFile();
                FileWriter writer = new FileWriter(specialBlockFile);
                gson.toJson(customBlocks, writer);
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        ItemStack mainHand = event.getPlayer().getInventory().getItemInMainHand();
        if (Items.isWand(mainHand)){
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK){
                if (event.getClickedBlock() != null)
                    if (event.getClickedBlock().getType() == Material.CONDUIT){
                        if (customBlocks.containsKey(event.getClickedBlock().getLocation())){
                            // check if it can be turned into a structure
                            if (customBlocks.get(event.getClickedBlock().getLocation()).getType().equals("weakMagicCore")){
                                // go through weak core buildings
                                if (validBlockStructure(magicStorage, event.getClickedBlock().getLocation())){
                                    // add to stuctures
                                    multiBlockStructures.add(new MultiBlockStructures("Storage", getAllLocations(magicStorage, event.getClickedBlock().getLocation())));
                                    event.getPlayer().sendMessage("Created Magic Storage");
                                }
                                else {
                                    event.getPlayer().playSound(event.getPlayer(), Sound.ENTITY_VILLAGER_NO,1,1);
                                }
                            }
                        }
                    }
            }
        }
    }
    // make sure to check if the chunk is loaded before using
    public boolean validBlockStructure(Map<Integer[], Material> blockSet, Location core){
        Block coreBlock = core.getBlock();
        for (Map.Entry<Integer[], Material> entry : blockSet.entrySet()){
            Integer[] relative = entry.getKey();
            Material material = entry.getValue();
            if (coreBlock.getRelative(relative[0],relative[1],relative[2]).getType() != material){
                return false;
            }
        }
        return true;
    }
    public List<Location> getAllLocations(Map<Integer[], Material> blockSet, Location core){
        List<Location> locations = new ArrayList<>();
        Block coreBlock = core.getBlock();
        locations.add(core);
        for (Map.Entry<Integer[], Material> entry : blockSet.entrySet()){
            Integer[] relative = entry.getKey();
            locations.add(coreBlock.getRelative(relative[0],relative[1],relative[2]).getLocation());
        }
        return locations;
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event){
        if (!event.isCancelled())
            if (event.getItemInHand().isSimilar(Items.data("smallCatalystCrystal")) || event.getItemInHand().isSimilar(Items.data("mediumCatalystCrystal")) || event.getItemInHand().isSimilar(Items.data("largeCatalystCrystal")) || event.getItemInHand().isSimilar(Items.data("unstableCatalystCrystal"))){
            event.setCancelled(true);
        } else if (event.getItemInHand().isSimilar(Items.data("weakMagicCore"))){
            if (event.getBlockReplacedState().getType() == Material.WATER){
                // don't allow to place in water
                event.setCancelled(true);
                event.getPlayer().playSound(event.getPlayer(), Sound.BLOCK_FIRE_EXTINGUISH, 1, 1);
            } else {
                // add to list of magic cores
                customBlocks.put(event.getBlockPlaced().getLocation(), new CustomBlocks(event.getBlockPlaced().getLocation(), "weakMagicCore"));
                event.getPlayer().playSound(event.getPlayer(), Sound.BLOCK_BEACON_ACTIVATE,1,1);
            }
        }
    }
    // remove from
    @EventHandler
    public void onBreak(BlockBreakEvent event){
        if (!event.isCancelled())
            if (event.getBlock().getType() == Material.CONDUIT){
            if (customBlocks.containsKey(event.getBlock().getLocation())){
                event.setDropItems(false);
                if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
                    Objects.requireNonNull(event.getBlock().getLocation().getWorld()).dropItem(event.getBlock().getLocation(), Objects.requireNonNull(Items.data(customBlocks.get(event.getBlock().getLocation()).getType())));
                customBlocks.remove(event.getBlock().getLocation());
                event.getPlayer().playSound(event.getPlayer(), Sound.BLOCK_BEACON_DEACTIVATE,1,1);
            }
        }
        ListIterator<MultiBlockStructures> multiBlockStructuresListIterator = multiBlockStructures.listIterator();
        while (multiBlockStructuresListIterator.hasNext()){
            MultiBlockStructures structures = multiBlockStructuresListIterator.next();
            if (structures.getBlocks().contains(event.getBlock().getLocation())){
                event.getPlayer().sendMessage("Broken structure");
                multiBlockStructuresListIterator.remove();
            }
        }
    }
    @EventHandler
    public void onExplode(EntityExplodeEvent event){
        if (!event.isCancelled()) {
            ListIterator<Block> blockIterator = event.blockList().listIterator();
            while (blockIterator.hasNext()) {
                Block block = blockIterator.next();
                if (block.getType() == Material.CONDUIT) {
                    if (customBlocks.containsKey(block.getLocation())) {
                        blockIterator.remove();
                        block.setType(Material.AIR);
                        Objects.requireNonNull(block.getLocation().getWorld()).dropItem(block.getLocation(), Objects.requireNonNull(Items.data(customBlocks.get(block.getLocation()).getType())));
                        customBlocks.remove(block.getLocation());
                    }
                }
            }
        }
    }
    @EventHandler
    public void onWaterPlace(PlayerBucketEmptyEvent event){
        if (customBlocks.containsKey(event.getBlock().getLocation())){
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onWaterChange(BlockFromToEvent event){
        if (customBlocks.containsKey(event.getToBlock().getLocation())){
            event.setCancelled(true);
        }

    }
    @EventHandler
    public void onDisable(PluginDisableEvent event) throws IOException {
        if (event.getPlugin().equals(NotMagic.getInstance())){
            saveSpecialBlocks(customBlocks);
        }
    }


}
