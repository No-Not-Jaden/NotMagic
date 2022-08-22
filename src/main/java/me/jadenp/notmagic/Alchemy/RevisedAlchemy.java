package me.jadenp.notmagic.Alchemy;

import me.jadenp.notmagic.RevisedClasses.Items;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Levelled;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

/*
Basic Alchemy Station:
doesnt matter which order everything is in, you have 8 slots, need water

Advanced Alchemy Station:
all slots must have exact amounts
first slot: Alchemy Dust, requires 3 per : on bookshelf
Second slot main ingredient, on brewing stand
third, forth, & fith slots: where bottles in the brewing stand go
six, seventh, & 8th above conduit


 */

public class RevisedAlchemy implements Listener {
    Items items = new Items();
    BasicStation basicStationRecipes = new BasicStation();
    AdvancedStation advancedStationRecipes = new AdvancedStation();
    RitualStation ritualStationRecipes = new RitualStation();
    private final Plugin plugin;
    private Map<Location, List<ItemStack>> basicStations = new HashMap<>();
    private Map<Location, List<ItemStack>> advancedStations = new HashMap<>();
    private Map<Location, List<Location>> advancedBlocks = new HashMap<>();
    private Map<Location, List<Item>> stationDisplays = new HashMap<>();
    private Map<String, Integer> interactCooldown = new HashMap<>();
    private Map<Location, List<ItemStack>> ritualStations = new HashMap<>();
    private Map<Location, Chunk> chunks = new HashMap<>();
    private Map<Location, World> worlds = new HashMap<>();
    private final String prefix = ChatColor.GRAY + "[" + ChatColor.of(new Color(26, 194, 232)) + "Not" + ChatColor.of(new Color(232, 26, 225)) + "Magic" + ChatColor.GRAY + "] " + ChatColor.DARK_GRAY + "» " + ChatColor.of(new Color(250, 165, 17)) + "";
    public RevisedAlchemy(Plugin plugin){
        this.plugin = plugin;
        addBasicRecipes();
        addAdvancedRecipes();
        addRitualRecipes();
        File alcStations = new File(plugin.getDataFolder() + File.separator + "alchemy-stations.yml");
        // get saved alchemy station information
        YamlConfiguration alcYml = YamlConfiguration.loadConfiguration(alcStations);
        int fileIterator = 1;
        while (alcYml.getString(fileIterator + ".type") != null){
            Location controllerBlockLocation = alcYml.getLocation(fileIterator + ".location");
            assert controllerBlockLocation != null;
            String type = alcYml.getString(fileIterator + ".type");
            assert type != null;
            List<ItemStack> stationInventory = new ArrayList<>();
            int inventoryIterator = 1;
            while (alcYml.getItemStack(fileIterator + ".inventory." + inventoryIterator) != null){
                stationInventory.add(alcYml.getItemStack(fileIterator + ".inventory." + inventoryIterator));
                inventoryIterator++;
            }
            chunks.put(controllerBlockLocation, controllerBlockLocation.getChunk());
            worlds.put(controllerBlockLocation, controllerBlockLocation.getWorld());
            if (type.equals("basic")){
                basicStations.put(controllerBlockLocation, stationInventory);
                updateDrops(controllerBlockLocation, stationInventory, type);
            } else if (type.equals("advanced")){
                advancedStations.put(controllerBlockLocation, stationInventory);
                int blockIterator = 1;
                List<Location> blockLocations = new ArrayList<>();
                while (alcYml.getLocation(fileIterator + ".blocks." + blockIterator) != null){
                    blockLocations.add(alcYml.getLocation(fileIterator + ".blocks." + blockIterator));
                    blockIterator++;
                }
                advancedBlocks.put(controllerBlockLocation, blockLocations);
                updateDrops(controllerBlockLocation, stationInventory, type);
            } else if (type.equals("ritual")){
                ritualStations.put(controllerBlockLocation, stationInventory);
                updateDrops(controllerBlockLocation, stationInventory, type);
            }
            fileIterator++;
        }

        // checking stations to see if they are built correctly
        new BukkitRunnable(){
            @Override
            public void run() {
                Iterator hmIterator = basicStations.entrySet().iterator();
                while (hmIterator.hasNext()) {
                    Map.Entry locationListEntry = (Map.Entry) hmIterator.next();
                    Location controllerLocation = (Location) locationListEntry.getKey();
                    if (controllerLocation.getWorld().isChunkLoaded(controllerLocation.getChunk())) {
                        if (controllerLocation.getBlock().getType() == Material.CAULDRON && controllerLocation.getBlock().getRelative(BlockFace.DOWN).getType() == Material.SOUL_CAMPFIRE) {
                            // updating dropped items so they don't despawn
                            if (stationDisplays.containsKey(locationListEntry.getKey())) {
                                List<Item> pastDroppedItems = stationDisplays.get(locationListEntry.getKey());
                                for (Item droppedItem : pastDroppedItems) {
                                    droppedItem.setTicksLived(1);
                                }
                            }
                        } else {
                            dropInventory(controllerLocation, "basic");
                            basicStations.remove(controllerLocation);
                        }
                    }

                }
                Iterator advancedIterator = advancedBlocks.entrySet().iterator();
                while (advancedIterator.hasNext()) {
                    Map.Entry locationListEntry = (Map.Entry) advancedIterator.next();
                    World world = worlds.get(locationListEntry);
                    Location controllerLocation = (Location) locationListEntry.getKey();
                    if (controllerLocation.getWorld().isChunkLoaded(controllerLocation.getChunk())) {
                        List<Location> blockLocations = (List<Location>) locationListEntry.getValue();
                        List<Block> blocks = new ArrayList<>();
                        for (Location location : blockLocations){
                            blocks.add(location.getBlock());
                        }
                        if (controllerLocation.getBlock().getType() == Material.BREWING_STAND &&
                                blocks.get(1).getType() == Material.SPRUCE_STAIRS &&
                                blocks.get(2).getType() == Material.BOOKSHELF &&
                                blocks.get(3).getType() == Material.BOOKSHELF &&
                                blocks.get(4).getType() == Material.SPRUCE_TRAPDOOR &&
                                blocks.get(5).getType() == Material.LEVER &&
                                blocks.get(6).getType() == Material.SPRUCE_STAIRS &&
                                blocks.get(7).getType() == Material.CONDUIT &&
                                blocks.get(8).getType() == Material.CAULDRON &&
                                blocks.get(9).getType() == Material.LEVER &&
                                blocks.get(10).getType() == Material.SPRUCE_STAIRS) {
                            if (stationDisplays.containsKey(controllerLocation)){
                                List<Item> pastDroppedItems = stationDisplays.get(controllerLocation);
                                for (Item droppedItem : pastDroppedItems){
                                    droppedItem.setTicksLived(1);
                                }
                            }
                        } else {
                            dropInventory(controllerLocation, "advanced");
                            advancedStations.remove(controllerLocation);
                            advancedBlocks.remove(controllerLocation);
                        }
                    }
                }
                Iterator ritualIterator = ritualStations.entrySet().iterator();
                while (ritualIterator.hasNext()){
                    Map.Entry locationListEntry = (Map.Entry) ritualIterator.next();
                    Location controllerLocation = (Location) locationListEntry.getKey();
                    if (controllerLocation.getWorld().isChunkLoaded(controllerLocation.getChunk())){
                        Location l1 = new Location(controllerLocation.getWorld(), controllerLocation.getX() - 2, controllerLocation.getY(), controllerLocation.getZ() - 2);
                        Location l2 = new Location(controllerLocation.getWorld(), controllerLocation.getX() - 2, controllerLocation.getY(), controllerLocation.getZ() + 2);
                        Location l3 = new Location(controllerLocation.getWorld(), controllerLocation.getX() + 2, controllerLocation.getY(), controllerLocation.getZ() + 2);
                        Location l4 = new Location(controllerLocation.getWorld(), controllerLocation.getX() + 2, controllerLocation.getY(), controllerLocation.getZ() - 2);
                        if (controllerLocation.getBlock().getType() == Material.REDSTONE_WIRE &&
                                controllerLocation.getBlock().getRelative(BlockFace.DOWN).getType() == Material.NETHERITE_BLOCK &&
                                controllerLocation.getBlock().getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).getType() == Material.CAULDRON &&
                                controllerLocation.getBlock().getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).getType() == Material.CAMPFIRE &&
                                controllerLocation.getBlock().getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).getType() == Material.HAY_BLOCK &&
                                controllerLocation.getBlock().getRelative(BlockFace.EAST).getType() == Material.SEA_PICKLE &&
                                controllerLocation.getBlock().getRelative(BlockFace.WEST).getType() == Material.SEA_PICKLE &&
                                controllerLocation.getBlock().getRelative(BlockFace.SOUTH).getType() == Material.SEA_PICKLE &&
                                controllerLocation.getBlock().getRelative(BlockFace.NORTH).getType() == Material.SEA_PICKLE &&
                                controllerLocation.getBlock().getRelative(BlockFace.EAST).getRelative(BlockFace.EAST).getType() == Material.REDSTONE_WIRE &&
                                controllerLocation.getBlock().getRelative(BlockFace.WEST).getRelative(BlockFace.WEST).getType() == Material.REDSTONE_WIRE &&
                                controllerLocation.getBlock().getRelative(BlockFace.SOUTH).getRelative(BlockFace.SOUTH).getType() == Material.REDSTONE_WIRE &&
                                controllerLocation.getBlock().getRelative(BlockFace.NORTH).getRelative(BlockFace.NORTH).getType() == Material.REDSTONE_WIRE &&
                                l1.getBlock().getType() == Material.SOUL_TORCH &&
                                l2.getBlock().getType() == Material.SOUL_TORCH &&
                                l3.getBlock().getType() == Material.SOUL_TORCH &&
                                l4.getBlock().getType() == Material.SOUL_TORCH){
                            Location corner = new Location(controllerLocation.getWorld(), controllerLocation.getX() - 2, controllerLocation.getY() -1, controllerLocation.getZ() - 2);
                            boolean bad = false;
                            for (int x = 0; x < 5; x++){
                                for (int z = 0; z < 5; z++){
                                    Location check = new Location(controllerLocation.getWorld(), corner.getX() + x, corner.getY(), corner.getZ() + z);
                                    if (check.getBlock().getType() != Material.NETHERITE_BLOCK){
                                        bad = true;
                                        break;
                                    }
                                }
                                if (bad){
                                    break;
                                }
                            }
                            if (!bad){
                                if (stationDisplays.containsKey(controllerLocation)){
                                    List<Item> pastDroppedItems = stationDisplays.get(controllerLocation);
                                    for (Item droppedItem : pastDroppedItems){
                                        droppedItem.setTicksLived(1);
                                    }
                                }
                            } else {
                                dropInventory(controllerLocation, "ritual");
                                ritualStations.remove(controllerLocation);
                            }
                        } else {
                            dropInventory(controllerLocation, "ritual");
                            ritualStations.remove(controllerLocation);
                        }
                    }

                }
            }
        }.runTaskTimer(plugin, 3000, 5980L);
        // saving to to file jic
        new BukkitRunnable(){
            @Override
            public void run() {
                YamlConfiguration alcYml2 = new YamlConfiguration();
                Iterator hmIterator = basicStations.entrySet().iterator();
                int i = 1;
                while (hmIterator.hasNext()) {
                    Map.Entry locationListEntry = (Map.Entry) hmIterator.next();
                    Location controllerLocation = (Location) locationListEntry.getKey();
                    List<ItemStack> inventory = (List<ItemStack>) locationListEntry.getValue();
                    alcYml2.set(i + ".type", "basic");
                    alcYml2.set(i + ".location", controllerLocation);
                    for (int y = 1; y < inventory.size()+1; y++){
                        alcYml2.set(i + ".inventory." + y, inventory.get(y-1));
                    }
                    i++;
                }
                Iterator advancedIterator = advancedStations.entrySet().iterator();
                while (advancedIterator.hasNext()){
                    Map.Entry locationListEntry = (Map.Entry) advancedIterator.next();
                    Location controllerLocation = (Location) locationListEntry.getKey();
                    List<ItemStack> inventory = (List<ItemStack>) locationListEntry.getValue();
                    alcYml2.set(i + ".type", "advanced");
                    alcYml2.set(i + ".location", controllerLocation);
                    for (int y = 1; y < inventory.size()+1; y++){
                        alcYml2.set(i + ".inventory." + y, inventory.get(y-1));
                    }
                    List<Location> locations = advancedBlocks.get(controllerLocation);
                    for (int y = 1; y < locations.size()+1; y++){
                        alcYml2.set(i + ".blocks." + y, locations.get(y-1));
                    }
                    i++;
                }
                Iterator ritualIterator = ritualStations.entrySet().iterator();
                while (ritualIterator.hasNext()){
                    Map.Entry locationListEntry = (Map.Entry) ritualIterator.next();
                    Location controllerLocation = (Location) locationListEntry.getKey();
                    List<ItemStack> inventory = (List<ItemStack>) locationListEntry.getValue();
                    alcYml2.set(i + ".type", "ritual");
                    alcYml2.set(i + ".location", controllerLocation);
                    for (int y = 1; y < inventory.size()+1; y++){
                        alcYml2.set(i + ".inventory." + y, inventory.get(y-1));
                    }

                    i++;
                }
                try {
                    alcYml2.save(alcStations);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }.runTaskTimerAsynchronously(plugin, 1000, 36000L);
    }
    // update the displayed ItemStacks that look dropped
    public void updateDrops(Location controllerLocation, List<ItemStack> stationInventory, String type){
        if (type.equals("basic")){
            List<Item> droppedItems = new ArrayList<>();
            for (int i = 0; i < stationInventory.size(); i++){
                Location dropLocation = new Location(controllerLocation.getWorld(), controllerLocation.getX() + 0.5, controllerLocation.getY() + 1 + ((double)i/5), controllerLocation.getZ() + 0.5);
                if (stationInventory.get(i) != null && stationInventory.get(i).getType() != Material.AIR && stationInventory.get(i).getAmount() > 0){
                    Item droppedItem = Objects.requireNonNull(dropLocation.getWorld()).dropItem(dropLocation, stationInventory.get(i));
                    droppedItem.setVelocity(new Vector(0, 0, 0));
                    droppedItem.setPickupDelay(Integer.MAX_VALUE);
                    droppedItem.setTicksLived(1);
                    droppedItem.setGravity(false);
                    droppedItems.add(droppedItem);
                }
            }
            if (stationDisplays.containsKey(controllerLocation)){
                List<Item> pastDroppedItems = stationDisplays.get(controllerLocation);
                for (Item droppedItem : pastDroppedItems){
                    droppedItem.setTicksLived(5999);
                }
                stationDisplays.replace(controllerLocation, droppedItems);
            } else {
                stationDisplays.put(controllerLocation, droppedItems);
            }
        } else if (type.equals("advanced")){
            List<Item> droppedItems = new ArrayList<>();
            if (!(advancedBlocks.get(controllerLocation).isEmpty())) {

                List<Location> blockLocations = advancedBlocks.get(controllerLocation);
                if (stationInventory.size() > 0) {
                    if (stationInventory.get(0).getType() != Material.AIR) {
                        Location location = new Location(controllerLocation.getWorld(), blockLocations.get(4).getX() + 0.5, blockLocations.get(4).getY() + 0.2, blockLocations.get(4).getZ() + 0.5);
                        Item droppedItem = Objects.requireNonNull(controllerLocation.getWorld()).dropItem(location, stationInventory.get(0));
                        droppedItem.setVelocity(new Vector(0, 0, 0));
                        droppedItem.setPickupDelay(Integer.MAX_VALUE);
                        droppedItem.setTicksLived(1);
                        droppedItem.setGravity(false);
                        droppedItems.add(droppedItem);
                    }
                }
                if (stationInventory.size() > 1) {
                    if (stationInventory.get(1).getType() != Material.AIR) {
                        Location location = new Location(controllerLocation.getWorld(), controllerLocation.getX() + 0.5, controllerLocation.getY() + 1.01, controllerLocation.getZ() + 0.5);
                        Item droppedItem = Objects.requireNonNull(controllerLocation.getWorld()).dropItem(location, stationInventory.get(1));
                        droppedItem.setVelocity(new Vector(0, 0, 0));
                        droppedItem.setPickupDelay(Integer.MAX_VALUE);
                        droppedItem.setTicksLived(1);
                        droppedItem.setGravity(false);
                        droppedItems.add(droppedItem);
                    }
                }
                if (stationInventory.size() > 2) {
                    if (stationInventory.get(2).getType() != Material.AIR) {
                        Location location = new Location(controllerLocation.getWorld(), controllerLocation.getX() + 0.3, controllerLocation.getY() + 0.2, controllerLocation.getZ() + 0.3);
                        Item droppedItem = Objects.requireNonNull(controllerLocation.getWorld()).dropItem(location, stationInventory.get(2));
                        droppedItem.setVelocity(new Vector(0, 0, 0));
                        droppedItem.setPickupDelay(Integer.MAX_VALUE);
                        droppedItem.setTicksLived(1);
                        droppedItem.setGravity(false);
                        droppedItems.add(droppedItem);
                    }
                }
                if (stationInventory.size() > 3) {
                    if (stationInventory.get(3).getType() != Material.AIR) {
                        Location location = new Location(controllerLocation.getWorld(), controllerLocation.getX() + 0.7, controllerLocation.getY() + 0.2, controllerLocation.getZ() + 0.5);
                        Item droppedItem = Objects.requireNonNull(controllerLocation.getWorld()).dropItem(location, stationInventory.get(3));
                        droppedItem.setVelocity(new Vector(0, 0, 0));
                        droppedItem.setPickupDelay(Integer.MAX_VALUE);
                        droppedItem.setTicksLived(1);
                        droppedItem.setGravity(false);
                        droppedItems.add(droppedItem);
                    }
                }
                if (stationInventory.size() > 4) {
                    if (stationInventory.get(4).getType() != Material.AIR) {
                        Location location = new Location(controllerLocation.getWorld(), controllerLocation.getX() + 0.3, controllerLocation.getY() + 0.2, controllerLocation.getZ() + 0.7);
                        Item droppedItem = Objects.requireNonNull(controllerLocation.getWorld()).dropItem(location, stationInventory.get(4));
                        droppedItem.setVelocity(new Vector(0, 0, 0));
                        droppedItem.setPickupDelay(Integer.MAX_VALUE);
                        droppedItem.setTicksLived(1);
                        droppedItem.setGravity(false);
                        droppedItems.add(droppedItem);
                    }
                }
                if (stationInventory.size() > 5) {
                    if (stationInventory.get(5).getType() != Material.AIR) {
                        Location location = new Location(controllerLocation.getWorld(), blockLocations.get(7).getX() + 0.5, blockLocations.get(7).getY() + 0.7, blockLocations.get(7).getZ() + 0.5);
                        Item droppedItem = Objects.requireNonNull(controllerLocation.getWorld()).dropItem(location, stationInventory.get(5));
                        droppedItem.setVelocity(new Vector(0, 0, 0));
                        droppedItem.setPickupDelay(Integer.MAX_VALUE);
                        droppedItem.setTicksLived(1);
                        droppedItem.setGravity(false);
                        droppedItems.add(droppedItem);
                    }
                }
                if (stationInventory.size() > 6) {
                    if (stationInventory.get(6).getType() != Material.AIR) {
                        Location location = new Location(controllerLocation.getWorld(), blockLocations.get(7).getX() + 0.5, blockLocations.get(7).getY() + 1.1, blockLocations.get(7).getZ() + 0.5);
                        Item droppedItem = Objects.requireNonNull(controllerLocation.getWorld()).dropItem(location, stationInventory.get(6));
                        droppedItem.setVelocity(new Vector(0, 0, 0));
                        droppedItem.setPickupDelay(Integer.MAX_VALUE);
                        droppedItem.setTicksLived(1);
                        droppedItem.setGravity(false);
                        droppedItems.add(droppedItem);
                    }
                }
                if (stationInventory.size() > 7) {
                    if (stationInventory.get(7).getType() != Material.AIR) {
                        Location location = new Location(controllerLocation.getWorld(), blockLocations.get(7).getX() + 0.5, blockLocations.get(7).getY() + 1.5, blockLocations.get(7).getZ() + 0.5);
                        Item droppedItem = Objects.requireNonNull(controllerLocation.getWorld()).dropItem(location, stationInventory.get(7));
                        droppedItem.setVelocity(new Vector(0, 0, 0));
                        droppedItem.setPickupDelay(Integer.MAX_VALUE);
                        droppedItem.setTicksLived(1);
                        droppedItem.setGravity(false);
                        droppedItems.add(droppedItem);
                    }
                }
                if (stationDisplays.containsKey(controllerLocation)){
                    List<Item> pastDroppedItems = stationDisplays.get(controllerLocation);
                    for (Item droppedItem : pastDroppedItems){
                        droppedItem.setTicksLived(5999);
                    }
                    stationDisplays.replace(controllerLocation, droppedItems);
                } else {
                    stationDisplays.put(controllerLocation, droppedItems);
                }
            }
        } else if (type.equals("ritual")){
            List<Item> droppedItems = new ArrayList<>();
            if (!(ritualStations.get(controllerLocation).isEmpty())){
                if (stationInventory.size() > 0) {
                    if (stationInventory.get(0).getType() != Material.AIR) {
                        Location location = new Location(controllerLocation.getWorld(), controllerLocation.getX() + 0.5, controllerLocation.getY() + 0.2, controllerLocation.getZ() + 0.5);
                        Item droppedItem = Objects.requireNonNull(controllerLocation.getWorld()).dropItem(location, stationInventory.get(0));
                        droppedItem.setVelocity(new Vector(0, 0, 0));
                        droppedItem.setPickupDelay(Integer.MAX_VALUE);
                        droppedItem.setTicksLived(1);
                        droppedItem.setGravity(false);
                        droppedItems.add(droppedItem);
                    }
                }
                if (stationInventory.size() > 1) {
                    if (stationInventory.get(1).getType() != Material.AIR) {
                        Location location = new Location(controllerLocation.getWorld(), controllerLocation.getX() + 0.5 + 1, controllerLocation.getY() + 0.5, controllerLocation.getZ() + 0.5);
                        Item droppedItem = Objects.requireNonNull(controllerLocation.getWorld()).dropItem(location, stationInventory.get(1));
                        droppedItem.setVelocity(new Vector(0, 0, 0));
                        droppedItem.setPickupDelay(Integer.MAX_VALUE);
                        droppedItem.setTicksLived(1);
                        droppedItem.setGravity(false);
                        droppedItems.add(droppedItem);
                    }
                }
                if (stationInventory.size() > 2) {
                    if (stationInventory.get(2).getType() != Material.AIR) {
                        Location location = new Location(controllerLocation.getWorld(), controllerLocation.getX() + 0.5, controllerLocation.getY() + 0.5, controllerLocation.getZ() + 0.5 + 1);
                        Item droppedItem = Objects.requireNonNull(controllerLocation.getWorld()).dropItem(location, stationInventory.get(2));
                        droppedItem.setVelocity(new Vector(0, 0, 0));
                        droppedItem.setPickupDelay(Integer.MAX_VALUE);
                        droppedItem.setTicksLived(1);
                        droppedItem.setGravity(false);
                        droppedItems.add(droppedItem);
                    }
                }
                if (stationInventory.size() > 3) {
                    if (stationInventory.get(3).getType() != Material.AIR) {
                        Location location = new Location(controllerLocation.getWorld(), controllerLocation.getX() + 0.5 - 1, controllerLocation.getY() + 0.5, controllerLocation.getZ() + 0.5);
                        Item droppedItem = Objects.requireNonNull(controllerLocation.getWorld()).dropItem(location, stationInventory.get(3));
                        droppedItem.setVelocity(new Vector(0, 0, 0));
                        droppedItem.setPickupDelay(Integer.MAX_VALUE);
                        droppedItem.setTicksLived(1);
                        droppedItem.setGravity(false);
                        droppedItems.add(droppedItem);
                    }
                }
                if (stationInventory.size() > 4) {
                    if (stationInventory.get(4).getType() != Material.AIR) {
                        Location location = new Location(controllerLocation.getWorld(), controllerLocation.getX() + 0.5, controllerLocation.getY() + 0.5, controllerLocation.getZ() + 0.5 - 1);
                        Item droppedItem = Objects.requireNonNull(controllerLocation.getWorld()).dropItem(location, stationInventory.get(4));
                        droppedItem.setVelocity(new Vector(0, 0, 0));
                        droppedItem.setPickupDelay(Integer.MAX_VALUE);
                        droppedItem.setTicksLived(1);
                        droppedItem.setGravity(false);
                        droppedItems.add(droppedItem);
                    }
                }
                if (stationInventory.size() > 5) {
                    if (stationInventory.get(5).getType() != Material.AIR) {
                        Location location = new Location(controllerLocation.getWorld(), controllerLocation.getX() + 0.5 + 2, controllerLocation.getY() + 0.2, controllerLocation.getZ() + 0.5);
                        Item droppedItem = Objects.requireNonNull(controllerLocation.getWorld()).dropItem(location, stationInventory.get(5));
                        droppedItem.setVelocity(new Vector(0, 0, 0));
                        droppedItem.setPickupDelay(Integer.MAX_VALUE);
                        droppedItem.setTicksLived(1);
                        droppedItem.setGravity(false);
                        droppedItems.add(droppedItem);
                    }
                }
                if (stationInventory.size() > 6) {
                    if (stationInventory.get(6).getType() != Material.AIR) {
                        Location location = new Location(controllerLocation.getWorld(), controllerLocation.getX() + 0.5, controllerLocation.getY() + 0.2, controllerLocation.getZ() + 0.5 + 2);
                        Item droppedItem = Objects.requireNonNull(controllerLocation.getWorld()).dropItem(location, stationInventory.get(6));
                        droppedItem.setVelocity(new Vector(0, 0, 0));
                        droppedItem.setPickupDelay(Integer.MAX_VALUE);
                        droppedItem.setTicksLived(1);
                        droppedItem.setGravity(false);
                        droppedItems.add(droppedItem);
                    }
                }
                if (stationInventory.size() > 7) {
                    if (stationInventory.get(7).getType() != Material.AIR) {
                        Location location = new Location(controllerLocation.getWorld(), controllerLocation.getX() + 0.5 - 2, controllerLocation.getY() + 0.2, controllerLocation.getZ() + 0.5);
                        Item droppedItem = Objects.requireNonNull(controllerLocation.getWorld()).dropItem(location, stationInventory.get(7));
                        droppedItem.setVelocity(new Vector(0, 0, 0));
                        droppedItem.setPickupDelay(Integer.MAX_VALUE);
                        droppedItem.setTicksLived(1);
                        droppedItem.setGravity(false);
                        droppedItems.add(droppedItem);
                    }
                }
                if (stationInventory.size() > 8) {
                    if (stationInventory.get(8).getType() != Material.AIR) {
                        Location location = new Location(controllerLocation.getWorld(), controllerLocation.getX() + 0.5, controllerLocation.getY() + 0.2, controllerLocation.getZ() + 0.5 - 2);
                        Item droppedItem = Objects.requireNonNull(controllerLocation.getWorld()).dropItem(location, stationInventory.get(8));
                        droppedItem.setVelocity(new Vector(0, 0, 0));
                        droppedItem.setPickupDelay(Integer.MAX_VALUE);
                        droppedItem.setTicksLived(1);
                        droppedItem.setGravity(false);
                        droppedItems.add(droppedItem);
                    }
                }
                if (stationInventory.size() > 9) {
                    if (stationInventory.get(9).getType() != Material.AIR) {
                        Location location = new Location(controllerLocation.getWorld(), controllerLocation.getX() + 0.5 + 2, controllerLocation.getY() + 0.8, controllerLocation.getZ() + 0.5 + 2);
                        Item droppedItem = Objects.requireNonNull(controllerLocation.getWorld()).dropItem(location, stationInventory.get(9));
                        droppedItem.setVelocity(new Vector(0, 0, 0));
                        droppedItem.setPickupDelay(Integer.MAX_VALUE);
                        droppedItem.setTicksLived(1);
                        droppedItem.setGravity(false);
                        droppedItems.add(droppedItem);
                    }
                }
                if (stationInventory.size() > 10) {
                    if (stationInventory.get(10).getType() != Material.AIR) {
                        Location location = new Location(controllerLocation.getWorld(), controllerLocation.getX() + 0.5 + 2, controllerLocation.getY() + 0.8, controllerLocation.getZ() + 0.5 - 2);
                        Item droppedItem = Objects.requireNonNull(controllerLocation.getWorld()).dropItem(location, stationInventory.get(10));
                        droppedItem.setVelocity(new Vector(0, 0, 0));
                        droppedItem.setPickupDelay(Integer.MAX_VALUE);
                        droppedItem.setTicksLived(1);
                        droppedItem.setGravity(false);
                        droppedItems.add(droppedItem);
                    }
                }
                if (stationInventory.size() > 11) {
                    if (stationInventory.get(11).getType() != Material.AIR) {
                        Location location = new Location(controllerLocation.getWorld(), controllerLocation.getX() + 0.5 - 2, controllerLocation.getY() + 0.8, controllerLocation.getZ() + 0.5 + 2);
                        Item droppedItem = Objects.requireNonNull(controllerLocation.getWorld()).dropItem(location, stationInventory.get(11));
                        droppedItem.setVelocity(new Vector(0, 0, 0));
                        droppedItem.setPickupDelay(Integer.MAX_VALUE);
                        droppedItem.setTicksLived(1);
                        droppedItem.setGravity(false);
                        droppedItems.add(droppedItem);
                    }
                }
                if (stationInventory.size() > 12) {
                    if (stationInventory.get(12).getType() != Material.AIR) {
                        Location location = new Location(controllerLocation.getWorld(), controllerLocation.getX() + 0.5 - 2, controllerLocation.getY() + 0.8, controllerLocation.getZ() + 0.5 - 2);
                        Item droppedItem = Objects.requireNonNull(controllerLocation.getWorld()).dropItem(location, stationInventory.get(12));
                        droppedItem.setVelocity(new Vector(0, 0, 0));
                        droppedItem.setPickupDelay(Integer.MAX_VALUE);
                        droppedItem.setTicksLived(1);
                        droppedItem.setGravity(false);
                        droppedItems.add(droppedItem);
                    }
                }

            }
            if (stationDisplays.containsKey(controllerLocation)){
                List<Item> pastDroppedItems = stationDisplays.get(controllerLocation);
                for (Item droppedItem : pastDroppedItems){
                    droppedItem.setTicksLived(5999);
                }
                stationDisplays.replace(controllerLocation, droppedItems);
            } else {
                stationDisplays.put(controllerLocation, droppedItems);
            }
        }
        else if (type.equals("remove")) {
            if (stationDisplays.containsKey(controllerLocation)) {
                List<Item> droppedItems = stationDisplays.get(controllerLocation);
                for (Item droppedItem : droppedItems) {
                    droppedItem.setTicksLived(5999);
                }
                stationDisplays.remove(controllerLocation);
            }
        }
    }
    public void dropInventory(Location controllerLocation, String type){
        if (type.equals("basic")){
            Location dropLocation = new Location(controllerLocation.getWorld(), controllerLocation.getX() + 0.5, controllerLocation.getY() + 1.2, controllerLocation.getZ() + 0.5);
            if (basicStations.containsKey(controllerLocation)){
                List<ItemStack> inventory = basicStations.get(controllerLocation);
                for (ItemStack itemStack : inventory){
                    Objects.requireNonNull(dropLocation.getWorld()).dropItem(dropLocation, itemStack);
                }
                basicStations.replace(controllerLocation, new ArrayList<>());
                List<ItemStack> emptyList = new ArrayList<>();
                updateDrops(controllerLocation, emptyList, "basic");
            }
        } else if (type.equals("advanced")){
            Location dropLocation = new Location(controllerLocation.getWorld(), controllerLocation.getX() + 0.5, controllerLocation.getY() + 1.2, controllerLocation.getZ() + 0.5);
            if (advancedStations.containsKey(controllerLocation)){
                List<ItemStack> inventory = advancedStations.get(controllerLocation);
                for (ItemStack itemStack : inventory){
                    Objects.requireNonNull(dropLocation.getWorld()).dropItem(dropLocation, itemStack);
                }
                advancedStations.replace(controllerLocation, new ArrayList<>());
                List<ItemStack> emptyList = new ArrayList<>();
                updateDrops(controllerLocation, emptyList, "advanced");
            }
        } else if (type.equals("ritual")){
            Location dropLocation = new Location(controllerLocation.getWorld(), controllerLocation.getX() + 0.5, controllerLocation.getY() + 1.2, controllerLocation.getZ() + 0.5);
            if (ritualStations.containsKey(controllerLocation)){
                List<ItemStack> inventory = ritualStations.get(controllerLocation);
                for (ItemStack itemStack : inventory){
                    Objects.requireNonNull(dropLocation.getWorld()).dropItem(dropLocation, itemStack);
                }
                ritualStations.replace(controllerLocation, new ArrayList<>());
                List<ItemStack> emptyList = new ArrayList<>();
                updateDrops(controllerLocation, emptyList, "ritual");
            }
        }
    }
    public void addBasicRecipes(){
        File config = new File(plugin.getDataFolder()+File.separator+ "oldconfig.txt");
        YamlConfiguration yy = YamlConfiguration.loadConfiguration(config);
        int o = 1;
        while (yy.getString("alchemy-recipes." + o + ".station-type") != null) {
            if (yy.getString("alchemy-recipes." + o + ".station-type").equals("basic")) {
                List<String> requirements = (List<String>) yy.getList("alchemy-recipes." + o + ".requirements");
                List<ItemStack> itemStackList = new ArrayList<>();
                List<String> result = (List<String>) yy.getList("alchemy-recipes." + o + ".result");
                List<ItemStack> resultList = new ArrayList<>();
                if (requirements != null) {
                    for (String r : requirements) {
                        String str = StringUtils.substringBetween(r, "[", "]");
                        String strs = r.substring(r.lastIndexOf("] ") + 1);
                        strs = strs.substring(1);
                        if (str.equalsIgnoreCase("item")) {
                            String ss = StringUtils.substringBefore(strs, " ");
                            String ss2 = StringUtils.substringAfter(strs, " ");
                            itemStackList.add(new ItemStack(Objects.requireNonNull(Material.getMaterial(ss)), Integer.parseInt(ss2)));
                        } else if (str.equalsIgnoreCase("custom")) {
                            String ss = StringUtils.substringBefore(strs, " ");
                            String ss2 = StringUtils.substringAfter(strs, " ");
                            ItemStack hm = new ItemStack(items.data(ss).getType(), Integer.parseInt(ss2));
                            hm.setItemMeta(items.data(ss).getItemMeta());
                            hm.setData(items.data(ss).getData());
                            hm.addUnsafeEnchantments(items.data(ss).getEnchantments());
                            itemStackList.add(hm);
                        } else if (str.equalsIgnoreCase("potion")) {
                            ItemStack potion = new ItemStack(Material.POTION, 1);
                            PotionMeta meta = (PotionMeta) potion.getItemMeta();
                            assert meta != null;
                            meta.setBasePotionData(new PotionData(PotionType.valueOf(strs)));
                            potion.setItemMeta(meta);
                            itemStackList.add(potion);
                        }
                    }
                }
                int u = 1;
                while (yy.getString("alchemy-recipes." + o + ".required-potions." + u + ".effect") != null) {
                    ItemStack potion = new ItemStack(Material.POTION, yy.getInt("alchemy-recipes." + o + ".required-potions." + u + ".amount"));
                    PotionMeta meta = (PotionMeta) potion.getItemMeta();
                    assert meta != null;
                    meta.setDisplayName(ChatColor.of(new Color(yy.getInt("alchemy-recipes." + o + ".required-potions." + u + ".color.r"), yy.getInt("alchemy-recipes." + o + ".required-potions." + u + ".color.g"), yy.getInt("alchemy-recipes." + o + ".required-potions." + u + ".color.b"))) + yy.getString("alchemy-recipes." + o + ".required-potions." + u + ".name"));
                    meta.setColor(org.bukkit.Color.fromRGB(yy.getInt("alchemy-recipes." + o + ".required-potions." + u + ".color.r"), yy.getInt("alchemy-recipes." + o + ".required-potions." + u + ".color.g"), yy.getInt("alchemy-recipes." + o + ".required-potions." + u + ".color.b")));
                    meta.addCustomEffect(new PotionEffect(PotionEffectType.getByName(yy.getString("alchemy-recipes." + o + ".required-potions." + u + ".effect")), yy.getInt("alchemy-recipes." + o + ".required-potions." + u + ".duration") * 20, yy.getInt("alchemy-recipes." + o + ".required-potions." + u + ".amplifier") - 1), true);
                    potion.setItemMeta(meta);
                    itemStackList.add(potion);
                    u++;
                }
                if (result != null) {
                    for (String r : result) {
                        String str = StringUtils.substringBetween(r, "[", "]");
                        String strs = r.substring(r.lastIndexOf("] ") + 1);
                        strs = strs.substring(1);
                        if (str.equalsIgnoreCase("item")) {
                            String ss = StringUtils.substringBefore(strs, " ");
                            String ss2 = StringUtils.substringAfter(strs, " ");
                            resultList.add(new ItemStack(Objects.requireNonNull(Material.getMaterial(ss)), Integer.parseInt(ss2)));
                        } else if (str.equalsIgnoreCase("custom")) {
                            String ss = StringUtils.substringBefore(strs, " ");
                            String ss2 = StringUtils.substringAfter(strs, " ");
                            ItemStack hm = new ItemStack(items.data(ss).getType(), Integer.parseInt(ss2));
                            hm.setItemMeta(items.data(ss).getItemMeta());
                            hm.setData(items.data(ss).getData());
                            hm.addUnsafeEnchantments(items.data(ss).getEnchantments());
                            resultList.add(hm);
                        } else if (str.equalsIgnoreCase("potion")) {
                            ItemStack potion = new ItemStack(Material.POTION, 1);
                            PotionMeta meta = (PotionMeta) potion.getItemMeta();
                            assert meta != null;
                            meta.setBasePotionData(new PotionData(PotionType.valueOf(strs)));
                            potion.setItemMeta(meta);
                            resultList.add(potion);
                        }
                    }
                }
                int w = 1;
                while (yy.getString("alchemy-recipes." + o + ".result-potions." + w + ".effect") != null) {
                    ItemStack potion = new ItemStack(Material.POTION, yy.getInt("alchemy-recipes." + o + ".result-potions." + w + ".amount"));
                    PotionMeta meta = (PotionMeta) potion.getItemMeta();
                    assert meta != null;
                    meta.setDisplayName(ChatColor.of(new Color(yy.getInt("alchemy-recipes." + o + ".result-potions." + w + ".color.r"), yy.getInt("alchemy-recipes." + o + ".result-potions." + w + ".color.g"), yy.getInt("alchemy-recipes." + o + ".result-potions." + w + ".color.b"))) + yy.getString("alchemy-recipes." + o + ".result-potions." + w + ".name"));
                    meta.setColor(org.bukkit.Color.fromRGB(yy.getInt("alchemy-recipes." + o + ".result-potions." + w + ".color.r"), yy.getInt("alchemy-recipes." + o + ".result-potions." + w + ".color.g"), yy.getInt("alchemy-recipes." + o + ".result-potions." + w + ".color.b")));
                    meta.addCustomEffect(new PotionEffect(PotionEffectType.getByName(yy.getString("alchemy-recipes." + o + ".result-potions." + w + ".effect")), yy.getInt("alchemy-recipes." + o + ".result-potions." + w + ".duration") * 20, yy.getInt("alchemy-recipes." + o + ".result-potions." + w + ".amplifier") - 1), true);
                    potion.setItemMeta(meta);
                    resultList.add(potion);
                    w++;
                }
                basicStationRecipes.addRecipe(itemStackList, yy.getString("alchemy-recipes." + o + ".station-type"), resultList);
            }
            o++;
        }
    }
    public void addAdvancedRecipes(){
        File config = new File(plugin.getDataFolder()+File.separator+ "oldconfig.txt");
        YamlConfiguration yy = YamlConfiguration.loadConfiguration(config);
        int o = 1;
        while (yy.getString("alchemy-recipes." + o + ".station-type") != null){
            if (yy.getString("alchemy-recipes." + o + ".station-type").equals("advanced")) {
                List<String> requirements = (List<String>) yy.getList("alchemy-recipes." + o + ".requirements");
                List<ItemStack> itemStackList = new ArrayList<>();
                List<String> result = (List<String>) yy.getList("alchemy-recipes." + o + ".result");
                List<ItemStack> resultList = new ArrayList<>();
                if (requirements != null) {
                    for (String r : requirements) {
                        String str = StringUtils.substringBetween(r, "[", "]");
                        String strs = r.substring(r.lastIndexOf("] ") + 1);
                        strs = strs.substring(1);
                        if (str.equalsIgnoreCase("item")) {
                            String ss = StringUtils.substringBefore(strs, " ");
                            String ss2 = StringUtils.substringAfter(strs, " ");
                            itemStackList.add(new ItemStack(Objects.requireNonNull(Material.getMaterial(ss)), Integer.parseInt(ss2)));
                        } else if (str.equalsIgnoreCase("custom")) {
                            String ss = StringUtils.substringBefore(strs, " ");
                            String ss2 = StringUtils.substringAfter(strs, " ");
                            ItemStack hm = new ItemStack(items.data(ss).getType(), Integer.parseInt(ss2));
                            hm.setItemMeta(items.data(ss).getItemMeta());
                            hm.setData(items.data(ss).getData());
                            hm.addUnsafeEnchantments(items.data(ss).getEnchantments());
                            itemStackList.add(hm);
                        } else if (str.equalsIgnoreCase("potion")) {
                            ItemStack potion = new ItemStack(Material.POTION, 1);
                            PotionMeta meta = (PotionMeta) potion.getItemMeta();
                            assert meta != null;
                            meta.setBasePotionData(new PotionData(PotionType.valueOf(strs)));
                            potion.setItemMeta(meta);
                            itemStackList.add(potion);
                        }
                    }
                }
                int u = 1;
                while (yy.getString("alchemy-recipes." + o + ".required-potions." + u + ".effect") != null) {
                    ItemStack potion = new ItemStack(Material.POTION, yy.getInt("alchemy-recipes." + o + ".required-potions." + u + ".amount"));
                    PotionMeta meta = (PotionMeta) potion.getItemMeta();
                    assert meta != null;
                    meta.setDisplayName(ChatColor.of(new Color(yy.getInt("alchemy-recipes." + o + ".required-potions." + u + ".color.r"), yy.getInt("alchemy-recipes." + o + ".required-potions." + u + ".color.g"), yy.getInt("alchemy-recipes." + o + ".required-potions." + u + ".color.b"))) + yy.getString("alchemy-recipes." + o + ".required-potions." + u + ".name"));
                    meta.setColor(org.bukkit.Color.fromRGB(yy.getInt("alchemy-recipes." + o + ".required-potions." + u + ".color.r"), yy.getInt("alchemy-recipes." + o + ".required-potions." + u + ".color.g"), yy.getInt("alchemy-recipes." + o + ".required-potions." + u + ".color.b")));
                    meta.addCustomEffect(new PotionEffect(PotionEffectType.getByName(yy.getString("alchemy-recipes." + o + ".required-potions." + u + ".effect")), yy.getInt("alchemy-recipes." + o + ".required-potions." + u + ".duration") * 20, yy.getInt("alchemy-recipes." + o + ".required-potions." + u + ".amplifier") - 1), true);
                    potion.setItemMeta(meta);
                    itemStackList.add(potion);
                    u++;
                }
                if (result != null) {
                    for (String r : result) {
                        String str = StringUtils.substringBetween(r, "[", "]");
                        String strs = r.substring(r.lastIndexOf("] ") + 1);
                        strs = strs.substring(1);
                        if (str.equalsIgnoreCase("item")) {
                            String ss = StringUtils.substringBefore(strs, " ");
                            String ss2 = StringUtils.substringAfter(strs, " ");
                            resultList.add(new ItemStack(Objects.requireNonNull(Material.getMaterial(ss)), Integer.parseInt(ss2)));
                        } else if (str.equalsIgnoreCase("custom")) {
                            String ss = StringUtils.substringBefore(strs, " ");
                            String ss2 = StringUtils.substringAfter(strs, " ");
                            ItemStack hm = new ItemStack(items.data(ss).getType(), Integer.parseInt(ss2));
                            hm.setItemMeta(items.data(ss).getItemMeta());
                            hm.setData(items.data(ss).getData());
                            hm.addUnsafeEnchantments(items.data(ss).getEnchantments());
                            resultList.add(hm);
                        } else if (str.equalsIgnoreCase("potion")) {
                            ItemStack potion = new ItemStack(Material.POTION, 1);
                            PotionMeta meta = (PotionMeta) potion.getItemMeta();
                            assert meta != null;
                            meta.setBasePotionData(new PotionData(PotionType.valueOf(strs)));
                            potion.setItemMeta(meta);
                            resultList.add(potion);
                        }
                    }
                }
                int w = 1;
                while (yy.getString("alchemy-recipes." + o + ".result-potions." + w + ".effect") != null) {
                    ItemStack potion = new ItemStack(Material.POTION, yy.getInt("alchemy-recipes." + o + ".result-potions." + w + ".amount"));
                    PotionMeta meta = (PotionMeta) potion.getItemMeta();
                    assert meta != null;
                    meta.setDisplayName(ChatColor.of(new Color(yy.getInt("alchemy-recipes." + o + ".result-potions." + w + ".color.r"), yy.getInt("alchemy-recipes." + o + ".result-potions." + w + ".color.g"), yy.getInt("alchemy-recipes." + o + ".result-potions." + w + ".color.b"))) + yy.getString("alchemy-recipes." + o + ".result-potions." + w + ".name"));
                    meta.setColor(org.bukkit.Color.fromRGB(yy.getInt("alchemy-recipes." + o + ".result-potions." + w + ".color.r"), yy.getInt("alchemy-recipes." + o + ".result-potions." + w + ".color.g"), yy.getInt("alchemy-recipes." + o + ".result-potions." + w + ".color.b")));
                    meta.addCustomEffect(new PotionEffect(PotionEffectType.getByName(yy.getString("alchemy-recipes." + o + ".result-potions." + w + ".effect")), yy.getInt("alchemy-recipes." + o + ".result-potions." + w + ".duration") * 20, yy.getInt("alchemy-recipes." + o + ".result-potions." + w + ".amplifier") - 1), true);
                    potion.setItemMeta(meta);
                    resultList.add(potion);
                    w++;
                }
                advancedStationRecipes.addRecipe(itemStackList, resultList);
            }
            o++;
        }
    }
    public void addRitualRecipes(){
        File config = new File(plugin.getDataFolder()+File.separator+ "oldconfig.txt");
        YamlConfiguration yy = YamlConfiguration.loadConfiguration(config);
        int o = 1;
        while (yy.getString("alchemy-recipes." + o + ".station-type") != null){
            if (yy.getString("alchemy-recipes." + o + ".station-type").equals("ritual")) {
                List<String> requirements = (List<String>) yy.getList("alchemy-recipes." + o + ".requirements");
                List<ItemStack> itemStackList = new ArrayList<>();
                List<String> result = (List<String>) yy.getList("alchemy-recipes." + o + ".result");
                List<ItemStack> resultList = new ArrayList<>();
                if (requirements != null) {
                    for (String r : requirements) {
                        String str = StringUtils.substringBetween(r, "[", "]");
                        String strs = r.substring(r.lastIndexOf("] ") + 1);
                        strs = strs.substring(1);
                        if (str.equalsIgnoreCase("item")) {
                            String ss = StringUtils.substringBefore(strs, " ");
                            String ss2 = StringUtils.substringAfter(strs, " ");
                            itemStackList.add(new ItemStack(Objects.requireNonNull(Material.getMaterial(ss)), Integer.parseInt(ss2)));
                        } else if (str.equalsIgnoreCase("custom")) {
                            String ss = StringUtils.substringBefore(strs, " ");
                            String ss2 = StringUtils.substringAfter(strs, " ");
                            ItemStack hm = new ItemStack(items.data(ss).getType(), Integer.parseInt(ss2));
                            hm.setItemMeta(items.data(ss).getItemMeta());
                            hm.setData(items.data(ss).getData());
                            hm.addUnsafeEnchantments(items.data(ss).getEnchantments());
                            itemStackList.add(hm);
                        } else if (str.equalsIgnoreCase("potion")) {
                            ItemStack potion = new ItemStack(Material.POTION, 1);
                            PotionMeta meta = (PotionMeta) potion.getItemMeta();
                            assert meta != null;
                            meta.setBasePotionData(new PotionData(PotionType.valueOf(strs)));
                            potion.setItemMeta(meta);
                            itemStackList.add(potion);
                        }
                    }
                }
                int u = 1;
                while (yy.getString("alchemy-recipes." + o + ".required-potions." + u + ".effect") != null) {
                    ItemStack potion = new ItemStack(Material.POTION, yy.getInt("alchemy-recipes." + o + ".required-potions." + u + ".amount"));
                    PotionMeta meta = (PotionMeta) potion.getItemMeta();
                    assert meta != null;
                    meta.setDisplayName(ChatColor.of(new Color(yy.getInt("alchemy-recipes." + o + ".required-potions." + u + ".color.r"), yy.getInt("alchemy-recipes." + o + ".required-potions." + u + ".color.g"), yy.getInt("alchemy-recipes." + o + ".required-potions." + u + ".color.b"))) + yy.getString("alchemy-recipes." + o + ".required-potions." + u + ".name"));
                    meta.setColor(org.bukkit.Color.fromRGB(yy.getInt("alchemy-recipes." + o + ".required-potions." + u + ".color.r"), yy.getInt("alchemy-recipes." + o + ".required-potions." + u + ".color.g"), yy.getInt("alchemy-recipes." + o + ".required-potions." + u + ".color.b")));
                    meta.addCustomEffect(new PotionEffect(PotionEffectType.getByName(yy.getString("alchemy-recipes." + o + ".required-potions." + u + ".effect")), yy.getInt("alchemy-recipes." + o + ".required-potions." + u + ".duration") * 20, yy.getInt("alchemy-recipes." + o + ".required-potions." + u + ".amplifier") - 1), true);
                    potion.setItemMeta(meta);
                    itemStackList.add(potion);
                    u++;
                }
                if (result != null) {
                    for (String r : result) {
                        String str = StringUtils.substringBetween(r, "[", "]");
                        String strs = r.substring(r.lastIndexOf("] ") + 1);
                        strs = strs.substring(1);
                        if (str.equalsIgnoreCase("item")) {
                            String ss = StringUtils.substringBefore(strs, " ");
                            String ss2 = StringUtils.substringAfter(strs, " ");
                            resultList.add(new ItemStack(Objects.requireNonNull(Material.getMaterial(ss)), Integer.parseInt(ss2)));
                        } else if (str.equalsIgnoreCase("custom")) {
                            String ss = StringUtils.substringBefore(strs, " ");
                            String ss2 = StringUtils.substringAfter(strs, " ");
                            ItemStack hm = new ItemStack(items.data(ss).getType(), Integer.parseInt(ss2));
                            hm.setItemMeta(items.data(ss).getItemMeta());
                            hm.setData(items.data(ss).getData());
                            hm.addUnsafeEnchantments(items.data(ss).getEnchantments());
                            resultList.add(hm);
                        } else if (str.equalsIgnoreCase("potion")) {
                            ItemStack potion = new ItemStack(Material.POTION, 1);
                            PotionMeta meta = (PotionMeta) potion.getItemMeta();
                            assert meta != null;
                            meta.setBasePotionData(new PotionData(PotionType.valueOf(strs)));
                            potion.setItemMeta(meta);
                            resultList.add(potion);
                        }
                    }
                }
                int w = 1;
                while (yy.getString("alchemy-recipes." + o + ".result-potions." + w + ".effect") != null) {
                    ItemStack potion = new ItemStack(Material.POTION, yy.getInt("alchemy-recipes." + o + ".result-potions." + w + ".amount"));
                    PotionMeta meta = (PotionMeta) potion.getItemMeta();
                    assert meta != null;
                    meta.setDisplayName(ChatColor.of(new Color(yy.getInt("alchemy-recipes." + o + ".result-potions." + w + ".color.r"), yy.getInt("alchemy-recipes." + o + ".result-potions." + w + ".color.g"), yy.getInt("alchemy-recipes." + o + ".result-potions." + w + ".color.b"))) + yy.getString("alchemy-recipes." + o + ".result-potions." + w + ".name"));
                    meta.setColor(org.bukkit.Color.fromRGB(yy.getInt("alchemy-recipes." + o + ".result-potions." + w + ".color.r"), yy.getInt("alchemy-recipes." + o + ".result-potions." + w + ".color.g"), yy.getInt("alchemy-recipes." + o + ".result-potions." + w + ".color.b")));
                    meta.addCustomEffect(new PotionEffect(PotionEffectType.getByName(yy.getString("alchemy-recipes." + o + ".result-potions." + w + ".effect")), yy.getInt("alchemy-recipes." + o + ".result-potions." + w + ".duration") * 20, yy.getInt("alchemy-recipes." + o + ".result-potions." + w + ".amplifier") - 1), true);
                    potion.setItemMeta(meta);
                    resultList.add(potion);
                    w++;
                }
                ritualStationRecipes.addRecipe(itemStackList, resultList);
            }
            o++;
        }
    }
    public void setInteractCooldown(Player p) {
        interactCooldown.replace(p.getUniqueId().toString(), 1);
        new BukkitRunnable() {
            @Override
            public void run() {
                interactCooldown.replace(p.getUniqueId().toString(), 0);
            }
        }.runTaskLater(plugin, 2);
    }
    public void givePlayer(Player p, ItemStack itemStack){
        HashMap<Integer, ItemStack> leftOver = new HashMap<Integer, ItemStack>((p.getInventory().addItem(itemStack)));
        if (!leftOver.isEmpty()) {
            Location loc = p.getLocation();
            p.getWorld().dropItem(loc, leftOver.get(0));
        }
    }
    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        if (!(interactCooldown.containsKey(event.getPlayer().getUniqueId().toString()))){
            interactCooldown.put(event.getPlayer().getUniqueId().toString(), 0);
        }
            if (interactCooldown.get(event.getPlayer().getUniqueId().toString()) == 0) {
                if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    Block block = event.getClickedBlock();
                    assert block != null;
                    Location bLoc = block.getLocation();
                    Player p = event.getPlayer();
                    setInteractCooldown(event.getPlayer());
                    if (Objects.requireNonNull(block).getType() == Material.CAULDRON) {
                        if (event.getPlayer().getInventory().getItemInMainHand().getType() == Material.WATER_BUCKET){
                            return;
                        }
                        // basic alchemy station
                        if (p.getInventory().getItemInMainHand().isSimilar(items.data("alcController"))) {
                            if (basicStations.containsKey(bLoc)) {
                                Levelled cauldronData = (Levelled) event.getClickedBlock().getBlockData();
                                if (cauldronData.getLevel() == cauldronData.getMaximumLevel()) {
                                    if (!(basicStations.get(bLoc).isEmpty())) {
                                        // attempt to brew something
                                        List<ItemStack> input = basicStations.get(bLoc);
                                        List<ItemStack> output = basicStationRecipes.brew(input, bLoc, p);
                                        if (output.get(0).getType() != Material.AIR) {
                                            AlchemyBrewEvent brewEvent = new AlchemyBrewEvent("basic",p);
                                            Bukkit.getServer().getPluginManager().callEvent(brewEvent);
                                            for (int i = 0; i < input.size(); i++) {
                                                for (ItemStack use : output) {
                                                    if (input.get(i).isSimilar(use)) {
                                                        ItemStack replace = new ItemStack(input.get(i).getType(), input.get(i).getAmount() - use.getAmount());
                                                        replace.setItemMeta(use.getItemMeta());
                                                        replace.setData(use.getData());
                                                        replace.addUnsafeEnchantments(use.getEnchantments());
                                                        input.set(i, replace);
                                                    }
                                                }
                                            }
                                            for (int i = input.size()-1; i > -1; i--) {
                                                if (input.get(i).getType().isAir()){
                                                    input.remove(i);
                                                }
                                                if (input.get(i).getAmount() < 1){
                                                    input.remove(i);
                                                }
                                            }
                                            basicStations.replace(bLoc, input);
                                            updateDrops(bLoc, basicStations.get(bLoc), "basic");
                                        } else {
                                            dropInventory(bLoc, "basic");
                                        }
                                    } else {
                                        p.sendMessage(prefix + "This Alchemy Station is Empty!");
                                    }
                                } else {
                                    p.sendMessage(prefix + "There is not enough water!");
                                }
                            } else {
                                if (block.getRelative(BlockFace.DOWN).getType() == Material.SOUL_CAMPFIRE) {
                                    p.sendMessage(prefix + "You've created a Basic Alchemy Station!");
                                    basicStations.put(bLoc, new ArrayList<>());

                                }
                            }
                        } else if (p.getInventory().getItemInMainHand().getType() == Material.AIR) {
                            // take item out of inventory
                            if (basicStations.containsKey(bLoc)) {
                                event.setCancelled(true);
                                if (!(basicStations.get(bLoc).isEmpty())) {
                                    List<ItemStack> inventory = basicStations.get(bLoc);
                                    givePlayer(p, inventory.get(inventory.size() - 1));
                                    inventory.remove(inventory.size() - 1);
                                    basicStations.replace(bLoc, inventory);
                                    updateDrops(bLoc, inventory, "basic");
                                }
                            }
                        } else {
                            if (basicStations.containsKey(bLoc)) {
                                event.setCancelled(true);
                                List<ItemStack> inventory = basicStations.get(bLoc);
                                if (inventory.size() < 8) {
                                    // add item to inventory
                                    inventory.add(p.getInventory().getItemInMainHand());
                                    p.getInventory().setItemInMainHand(null);
                                    basicStations.replace(bLoc, inventory);
                                    updateDrops(bLoc, inventory, "basic");
                                }
                            }

                        }
                    }
                    // Advanced station
                    if (block.getType() == Material.BREWING_STAND) {
                        if (advancedStations.containsKey(bLoc))
                            event.setCancelled(true);
                        if (p.getInventory().getItemInMainHand().isSimilar(items.data("alcController"))) {
                            event.setCancelled(true);
                            if (advancedStations.containsKey(bLoc)){
                                List<Location> stationBlocks = advancedBlocks.get(bLoc);
                                Block cauldron = stationBlocks.get(8).getBlock();
                                Levelled cauldronData = (Levelled) cauldron.getBlockData();
                                if (cauldronData.getLevel() == cauldronData.getMaximumLevel()) {
                                    if (!(advancedStations.get(bLoc).isEmpty())){
                                           if (advancedStationRecipes.brew(advancedStations.get(bLoc), bLoc,p)){
                                               advancedStations.replace(bLoc, new ArrayList<>());
                                               AlchemyBrewEvent brewEvent = new AlchemyBrewEvent("advanced",p);
                                               Bukkit.getServer().getPluginManager().callEvent(brewEvent);
                                               List<Location> locations = advancedBlocks.get(bLoc);
                                               Location l1 = new Location(bLoc.getWorld(), locations.get(2).getX() + 0.5, locations.get(2).getY() + 1.5, locations.get(2).getZ() + 0.5);
                                               Location l2 = new Location(bLoc.getWorld(), bLoc.getX() + 0.5, bLoc.getY() + 1.5, bLoc.getZ() + 0.5);
                                               Location l3 = new Location(bLoc.getWorld(), locations.get(5).getX() + 0.5, locations.get(5).getY() + 1.5, locations.get(5).getZ() + 0.5);
                                               Location l4 = new Location(bLoc.getWorld(), locations.get(7).getX() + 0.5, locations.get(7).getY() + 1.5, locations.get(7).getZ() + 0.5);
                                               Location l5 = new Location(bLoc.getWorld(), locations.get(9).getX() + 0.5, locations.get(9).getY() + 1.5, locations.get(9).getZ() + 0.5);
                                               for (int i = 0; i < 3; i++) {
                                                   bLoc.getWorld().spawnParticle(Particle.SMOKE_LARGE, l1, 0, 0, 0.1, 0);
                                                   bLoc.getWorld().spawnParticle(Particle.SMOKE_LARGE, l2, 0, 0, 0.1, 0);
                                                   bLoc.getWorld().spawnParticle(Particle.SMOKE_LARGE, l3, 0, 0, 0.1, 0);
                                                   bLoc.getWorld().spawnParticle(Particle.SMOKE_LARGE, l4, 0, 0, 0.1, 0);
                                                   bLoc.getWorld().spawnParticle(Particle.SMOKE_LARGE, l5, 0, 0, 0.1, 0);
                                               }
                                               locations.get(8).getBlock().setType(Material.CAULDRON);
                                               p.playSound(bLoc, Sound.BLOCK_BREWING_STAND_BREW,1,1);
                                           } else {
                                               dropInventory(bLoc, "advanced");
                                           }
                                        updateDrops(bLoc, advancedStations.get(bLoc), "advanced");
                                    } else {
                                        p.sendMessage(prefix + "This Alchemy Station is Empty!");
                                    }
                                } else {
                                    p.sendMessage(prefix + "There is not enough water!");
                                }
                            } else {
                                List<Location> blocks = new ArrayList<>();
                                BlockFace face = null;
                                if (block.getRelative(BlockFace.DOWN).getType() == Material.SPRUCE_STAIRS)
                                if (block.getRelative(BlockFace.NORTH).getType() == Material.BOOKSHELF &&
                                        block.getRelative(BlockFace.NORTH).getRelative(BlockFace.DOWN).getType() == Material.BOOKSHELF &&
                                        block.getRelative(BlockFace.NORTH).getRelative(BlockFace.UP).getType() == Material.SPRUCE_TRAPDOOR){
                                    face = BlockFace.SOUTH;
                                    blocks.add(block.getLocation());
                                    blocks.add(block.getRelative(BlockFace.DOWN).getLocation());
                                    blocks.add(block.getRelative(BlockFace.NORTH).getLocation());
                                    blocks.add(block.getRelative(BlockFace.NORTH).getRelative(BlockFace.DOWN).getLocation());
                                    blocks.add(block.getRelative(BlockFace.NORTH).getRelative(BlockFace.UP).getLocation());
                                } else if (block.getRelative(BlockFace.SOUTH).getType() == Material.BOOKSHELF &&
                                        block.getRelative(BlockFace.SOUTH).getRelative(BlockFace.DOWN).getType() == Material.BOOKSHELF &&
                                        block.getRelative(BlockFace.SOUTH).getRelative(BlockFace.UP).getType() == Material.SPRUCE_TRAPDOOR) {
                                    face = BlockFace.NORTH;
                                    blocks.add(block.getLocation());
                                    blocks.add(block.getRelative(BlockFace.DOWN).getLocation());
                                    blocks.add(block.getRelative(BlockFace.SOUTH).getLocation());
                                    blocks.add(block.getRelative(BlockFace.SOUTH).getRelative(BlockFace.DOWN).getLocation());
                                    blocks.add(block.getRelative(BlockFace.SOUTH).getRelative(BlockFace.UP).getLocation());
                                } else if (block.getRelative(BlockFace.EAST).getType() == Material.BOOKSHELF &&
                                        block.getRelative(BlockFace.EAST).getRelative(BlockFace.DOWN).getType() == Material.BOOKSHELF &&
                                        block.getRelative(BlockFace.EAST).getRelative(BlockFace.UP).getType() == Material.SPRUCE_TRAPDOOR){
                                    face = BlockFace.WEST;
                                    blocks.add(block.getLocation());
                                    blocks.add(block.getRelative(BlockFace.DOWN).getLocation());
                                    blocks.add(block.getRelative(BlockFace.EAST).getLocation());
                                    blocks.add(block.getRelative(BlockFace.EAST).getRelative(BlockFace.DOWN).getLocation());
                                    blocks.add(block.getRelative(BlockFace.EAST).getRelative(BlockFace.UP).getLocation());
                                } else if (block.getRelative(BlockFace.WEST).getType() == Material.BOOKSHELF &&
                                        block.getRelative(BlockFace.WEST).getRelative(BlockFace.DOWN).getType() == Material.BOOKSHELF &&
                                        block.getRelative(BlockFace.WEST).getRelative(BlockFace.UP).getType() == Material.SPRUCE_TRAPDOOR){
                                    face = BlockFace.EAST;
                                    // brewing stand 0
                                    blocks.add(block.getLocation());
                                    // spruce stairs 1
                                    blocks.add(block.getRelative(BlockFace.DOWN).getLocation());
                                    // bookshelf 2
                                    blocks.add(block.getRelative(BlockFace.WEST).getLocation());
                                    // bookshelf 3
                                    blocks.add(block.getRelative(BlockFace.WEST).getRelative(BlockFace.DOWN).getLocation());
                                    // spruce trapdoor 4
                                    blocks.add(block.getRelative(BlockFace.WEST).getRelative(BlockFace.UP).getLocation());
                                }
                                if (face != null){
                                    if (block.getRelative(face).getType() == Material.LEVER &&
                                            block.getRelative(face).getRelative(BlockFace.DOWN).getType() == Material.SPRUCE_STAIRS &&
                                            block.getRelative(face).getRelative(face).getType() == Material.CONDUIT &&
                                            block.getRelative(face).getRelative(face).getRelative(BlockFace.DOWN).getType() == Material.CAULDRON &&
                                            block.getRelative(face).getRelative(face).getRelative(face).getType() == Material.LEVER &&
                                            block.getRelative(face).getRelative(face).getRelative(face).getRelative(BlockFace.DOWN).getType() == Material.SPRUCE_STAIRS){
                                        p.sendMessage(prefix + "You've created an Advanced Alchemist Table!");
                                        advancedStations.put(bLoc, new ArrayList<>());
                                        // lever 5
                                        blocks.add(block.getRelative(face).getLocation());
                                        // spruce stairs 6
                                        blocks.add(block.getRelative(face).getRelative(BlockFace.DOWN).getLocation());
                                        // conduit 7
                                        blocks.add(block.getRelative(face).getRelative(face).getLocation());
                                        //cauldron 8
                                        blocks.add(block.getRelative(face).getRelative(face).getRelative(BlockFace.DOWN).getLocation());
                                        // lever 9
                                        blocks.add(block.getRelative(face).getRelative(face).getRelative(face).getLocation());
                                        // spruce stairs 10
                                        blocks.add(block.getRelative(face).getRelative(face).getRelative(face).getRelative(BlockFace.DOWN).getLocation());
                                        advancedBlocks.put(bLoc, blocks);

                                    }
                                }
                            }
                        } else if (p.getInventory().getItemInMainHand().getType() == Material.AIR){
                            if (advancedStations.containsKey(bLoc)) {
                                event.setCancelled(true);
                                if (!(advancedStations.get(bLoc).isEmpty())) {
                                    List<ItemStack> inventory = advancedStations.get(bLoc);
                                    givePlayer(p, inventory.get(inventory.size() - 1));
                                    inventory.remove(inventory.size() - 1);
                                    advancedStations.replace(bLoc, inventory);
                                    updateDrops(bLoc, inventory, "advanced");
                                }
                            }
                        } else {
                            if (advancedStations.containsKey(bLoc)) {
                                event.setCancelled(true);
                                List<ItemStack> inventory = advancedStations.get(bLoc);
                                if (inventory.size() < 8) {
                                    // add item to inventory
                                    inventory.add(p.getInventory().getItemInMainHand());
                                    p.getInventory().setItemInMainHand(null);
                                    advancedStations.replace(bLoc, inventory);
                                    updateDrops(bLoc, inventory, "advanced");
                                }
                            }
                        }
                    }
                    if (block.getType() == Material.REDSTONE_WIRE){
                        if (ritualStations.containsKey(bLoc))
                            event.setCancelled(true);
                        if (p.getInventory().getItemInMainHand().isSimilar(items.data("alcController"))){
                            if (ritualStations.containsKey(bLoc)){
                                if (!(ritualStations.get(bLoc).isEmpty())){
                                    if (ritualStationRecipes.brew(ritualStations.get(bLoc), bLoc,p)){
                                        ritualStations.replace(bLoc, new ArrayList<>());
                                        AlchemyBrewEvent brewEvent = new AlchemyBrewEvent("ritual",p);
                                        Bukkit.getServer().getPluginManager().callEvent(brewEvent);
                                        bLoc.getWorld().spawnParticle(Particle.CRIMSON_SPORE,new Location(bLoc.getWorld(), bLoc.getX() + 0.5, bLoc.getY() + 2, bLoc.getZ() + 0.5),100,3,3,3);
                                        p.playSound(bLoc, Sound.BLOCK_LAVA_EXTINGUISH,1,1);
                                    } else {
                                        dropInventory(bLoc, "ritual");
                                    }
                                    updateDrops(bLoc, new ArrayList<>(), "ritual");
                                } else {
                                    p.sendMessage(prefix + "This Alchemy Station is Empty!");
                                }
                            } else {
                                event.setCancelled(true);
                                Location l1 = new Location(bLoc.getWorld(), bLoc.getX() - 2, bLoc.getY(), bLoc.getZ() - 2);
                                Location l2 = new Location(bLoc.getWorld(), bLoc.getX() - 2, bLoc.getY(), bLoc.getZ() + 2);
                                Location l3 = new Location(bLoc.getWorld(), bLoc.getX() + 2, bLoc.getY(), bLoc.getZ() + 2);
                                Location l4 = new Location(bLoc.getWorld(), bLoc.getX() + 2, bLoc.getY(), bLoc.getZ() - 2);
                                if (bLoc.getBlock().getType() == Material.REDSTONE_WIRE &&
                                        bLoc.getBlock().getRelative(BlockFace.DOWN).getType() == Material.NETHERITE_BLOCK &&
                                        bLoc.getBlock().getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).getType() == Material.CAULDRON &&
                                        bLoc.getBlock().getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).getType() == Material.CAMPFIRE &&
                                        bLoc.getBlock().getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).getType() == Material.HAY_BLOCK &&
                                        bLoc.getBlock().getRelative(BlockFace.EAST).getType() == Material.SEA_PICKLE &&
                                        bLoc.getBlock().getRelative(BlockFace.WEST).getType() == Material.SEA_PICKLE &&
                                        bLoc.getBlock().getRelative(BlockFace.SOUTH).getType() == Material.SEA_PICKLE &&
                                        bLoc.getBlock().getRelative(BlockFace.NORTH).getType() == Material.SEA_PICKLE &&
                                        bLoc.getBlock().getRelative(BlockFace.EAST).getRelative(BlockFace.EAST).getType() == Material.REDSTONE_WIRE &&
                                        bLoc.getBlock().getRelative(BlockFace.WEST).getRelative(BlockFace.WEST).getType() == Material.REDSTONE_WIRE &&
                                        bLoc.getBlock().getRelative(BlockFace.SOUTH).getRelative(BlockFace.SOUTH).getType() == Material.REDSTONE_WIRE &&
                                        bLoc.getBlock().getRelative(BlockFace.NORTH).getRelative(BlockFace.NORTH).getType() == Material.REDSTONE_WIRE &&
                                        l1.getBlock().getType() == Material.SOUL_TORCH &&
                                        l2.getBlock().getType() == Material.SOUL_TORCH &&
                                        l3.getBlock().getType() == Material.SOUL_TORCH &&
                                        l4.getBlock().getType() == Material.SOUL_TORCH){
                                    Location corner = new Location(bLoc.getWorld(), bLoc.getX() - 2, bLoc.getY() -1, bLoc.getZ() - 2);
                                    boolean bad = false;
                                    for (int x = 0; x < 5; x++){
                                        for (int z = 0; z < 5; z++){
                                            Location check = new Location(bLoc.getWorld(), corner.getX() + x, corner.getY(), corner.getZ() + z);
                                            if (check.getBlock().getType() != Material.NETHERITE_BLOCK){
                                                bad = true;
                                                break;
                                            }
                                        }
                                        if (bad){
                                            break;
                                        }
                                    }
                                    if (!bad){
                                        p.sendMessage(prefix + "You've created a Ritual Alchemist Station");
                                        ritualStations.put(bLoc, new ArrayList<>());
                                    }
                                }
                            }
                        } else if (p.getInventory().getItemInMainHand().getType() == Material.AIR) {
                            if (ritualStations.containsKey(bLoc)){
                                event.setCancelled(true);
                                if (!(ritualStations.get(bLoc).isEmpty())) {
                                    List<ItemStack> inventory = ritualStations.get(bLoc);
                                    givePlayer(p, inventory.get(inventory.size() - 1));
                                    inventory.remove(inventory.size() - 1);
                                    ritualStations.replace(bLoc, inventory);

                                }
                                updateDrops(bLoc, ritualStations.get(bLoc), "ritual");

                            }
                        } else {
                            if (ritualStations.containsKey(bLoc)) {
                                event.setCancelled(true);
                                List<ItemStack> inventory = ritualStations.get(bLoc);
                                if (inventory.size() < 13) {
                                    // add item to inventory
                                    inventory.add(p.getInventory().getItemInMainHand());
                                    p.getInventory().setItemInMainHand(null);
                                    ritualStations.replace(bLoc, inventory);
                                    updateDrops(bLoc, inventory, "ritual");
                                }
                            }
                        }
                    }
                }

            }
    }
    @EventHandler
    public void onBreakBlock(BlockBreakEvent event){
        Block block = event.getBlock();
        Location bLoc = block.getLocation();
        if (block.getType() == Material.CAULDRON){
            if (basicStations.containsKey(bLoc)){
                dropInventory(bLoc, "basic");
                basicStations.remove(bLoc);
            }
        } else if (block.getType() == Material.BREWING_STAND){
            if (advancedStations.containsKey(bLoc)){
                dropInventory(bLoc, "advanced");
                advancedStations.remove(bLoc);
            }
        } else if (block.getType() == Material.REDSTONE_WIRE){
            if (ritualStations.containsKey(bLoc)){
                dropInventory(bLoc, "ritual");
                ritualStations.remove(bLoc);
            }
        }
    }
    @EventHandler
    public void onDisable(PluginDisableEvent event) throws IOException {
        if (event.getPlugin().equals(plugin)) {
            // make sure the stations are built correctly
            YamlConfiguration alcYml = new YamlConfiguration();
            int i = 1;
            Iterator hmIterator = basicStations.entrySet().iterator();
            while (hmIterator.hasNext()) {
                Map.Entry locationListEntry = (Map.Entry) hmIterator.next();
                Location controllerLocation = (Location) locationListEntry.getKey();
                List<ItemStack> inventory = (List<ItemStack>) locationListEntry.getValue();
                boolean bad = true;
                if (controllerLocation.getBlock().getType() == Material.CAULDRON && controllerLocation.getBlock().getRelative(BlockFace.DOWN).getType() == Material.SOUL_CAMPFIRE) {
                    bad = false;
                    // save alchemy information
                    alcYml.set(i + ".type", "basic");
                    alcYml.set(i + ".location", controllerLocation);
                    for (int y = 1; y < inventory.size()+1; y++) {
                        alcYml.set(i + ".inventory." + y, inventory.get(y-1));
                    }
                    updateDrops(controllerLocation, inventory, "remove");
                    i++;
                }
                if (bad) {
                    dropInventory(controllerLocation, "basic");
                }
            }
            Iterator advancedIterator = advancedStations.entrySet().iterator();
            while (advancedIterator.hasNext()){
                Map.Entry locationListEntry = (Map.Entry) advancedIterator.next();
                Location controllerLocation = (Location) locationListEntry.getKey();
                List<ItemStack> inventory = (List<ItemStack>) locationListEntry.getValue();

                    List<Location> blocks = advancedBlocks.get(locationListEntry.getKey());

                    if (controllerLocation.getBlock().getType() == Material.BREWING_STAND &&
                            blocks.get(1).getBlock().getType() == Material.SPRUCE_STAIRS &&
                            blocks.get(2).getBlock().getType() == Material.BOOKSHELF &&
                            blocks.get(3).getBlock().getType() == Material.BOOKSHELF &&
                            blocks.get(4).getBlock().getType() == Material.SPRUCE_TRAPDOOR &&
                            blocks.get(5).getBlock().getType() == Material.LEVER &&
                            blocks.get(6).getBlock().getType() == Material.SPRUCE_STAIRS &&
                            blocks.get(7).getBlock().getType() == Material.CONDUIT &&
                            blocks.get(8).getBlock().getType() == Material.CAULDRON &&
                            blocks.get(9).getBlock().getType() == Material.LEVER &&
                            blocks.get(10).getBlock().getType() == Material.SPRUCE_STAIRS) {
                        if (stationDisplays.containsKey(controllerLocation)){
                            List<Item> pastDroppedItems = stationDisplays.get(controllerLocation);
                            for (Item droppedItem : pastDroppedItems){
                                droppedItem.setTicksLived(5999);
                            }
                        }

                        alcYml.set(i + ".type", "advanced");
                        alcYml.set(i + ".location", controllerLocation);
                        for (int y = 1; y < inventory.size()+1; y++){
                            alcYml.set(i + ".inventory." + y, inventory.get(y-1));
                        }
                        List<Location> locations = advancedBlocks.get(controllerLocation);
                        for (int y = 1; y < locations.size()+1; y++){
                            alcYml.set(i + ".blocks." + y, locations.get(y-1));
                        }
                        i++;
                    } else {
                        dropInventory(controllerLocation, "advanced");
                    }


            }
            Iterator ritualIterator = ritualStations.entrySet().iterator();
            while (ritualIterator.hasNext()){
                Map.Entry locationListEntry = (Map.Entry) ritualIterator.next();
                Location controllerLocation = (Location) locationListEntry.getKey();

                    Location l1 = new Location(controllerLocation.getWorld(), controllerLocation.getX() - 2, controllerLocation.getY(), controllerLocation.getZ() - 2);
                    Location l2 = new Location(controllerLocation.getWorld(), controllerLocation.getX() - 2, controllerLocation.getY(), controllerLocation.getZ() + 2);
                    Location l3 = new Location(controllerLocation.getWorld(), controllerLocation.getX() + 2, controllerLocation.getY(), controllerLocation.getZ() + 2);
                    Location l4 = new Location(controllerLocation.getWorld(), controllerLocation.getX() + 2, controllerLocation.getY(), controllerLocation.getZ() - 2);
                    if (controllerLocation.getBlock().getType() == Material.REDSTONE_WIRE &&
                            controllerLocation.getBlock().getRelative(BlockFace.DOWN).getType() == Material.NETHERITE_BLOCK &&
                            controllerLocation.getBlock().getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).getType() == Material.CAULDRON &&
                            controllerLocation.getBlock().getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).getType() == Material.CAMPFIRE &&
                            controllerLocation.getBlock().getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).getType() == Material.HAY_BLOCK &&
                            controllerLocation.getBlock().getRelative(BlockFace.EAST).getType() == Material.SEA_PICKLE &&
                            controllerLocation.getBlock().getRelative(BlockFace.WEST).getType() == Material.SEA_PICKLE &&
                            controllerLocation.getBlock().getRelative(BlockFace.SOUTH).getType() == Material.SEA_PICKLE &&
                            controllerLocation.getBlock().getRelative(BlockFace.NORTH).getType() == Material.SEA_PICKLE &&
                            controllerLocation.getBlock().getRelative(BlockFace.EAST).getRelative(BlockFace.EAST).getType() == Material.REDSTONE_WIRE &&
                            controllerLocation.getBlock().getRelative(BlockFace.WEST).getRelative(BlockFace.WEST).getType() == Material.REDSTONE_WIRE &&
                            controllerLocation.getBlock().getRelative(BlockFace.SOUTH).getRelative(BlockFace.SOUTH).getType() == Material.REDSTONE_WIRE &&
                            controllerLocation.getBlock().getRelative(BlockFace.NORTH).getRelative(BlockFace.NORTH).getType() == Material.REDSTONE_WIRE &&
                            l1.getBlock().getType() == Material.SOUL_TORCH &&
                            l2.getBlock().getType() == Material.SOUL_TORCH &&
                            l3.getBlock().getType() == Material.SOUL_TORCH &&
                            l4.getBlock().getType() == Material.SOUL_TORCH){
                        Location corner = new Location(controllerLocation.getWorld(), controllerLocation.getX() - 2, controllerLocation.getY() -1, controllerLocation.getZ() - 2);
                        boolean bad = false;
                        for (int x = 0; x < 5; x++){
                            for (int z = 0; z < 5; z++){
                                Location check = new Location(controllerLocation.getWorld(), corner.getX() + x, corner.getY(), corner.getZ() + z);
                                if (check.getBlock().getType() != Material.NETHERITE_BLOCK){
                                    bad = true;
                                    break;
                                }
                            }
                            if (bad){
                                break;
                            }
                        }
                        if (!bad){
                            List<ItemStack> inventory = (List<ItemStack>) locationListEntry.getValue();
                            alcYml.set(i + ".type", "ritual");
                            alcYml.set(i + ".location", controllerLocation);
                            for (int y = 1; y < inventory.size()+1; y++){
                                alcYml.set(i + ".inventory." + y, inventory.get(y-1));
                            }
                            i++;
                        } else {
                            dropInventory(controllerLocation, "ritual");
                        }
                    } else {
                        dropInventory(controllerLocation, "ritual");
                    }
                    if (stationDisplays.containsKey(controllerLocation)){
                        List<Item> pastDroppedItems = stationDisplays.get(controllerLocation);
                        for (Item droppedItem : pastDroppedItems){
                            droppedItem.setTicksLived(5999);
                        }

                }
            }
            File alcStations = new File(plugin.getDataFolder() + File.separator + "alchemy-stations.yml");
            alcYml.save(alcStations);

        }
    }

}
