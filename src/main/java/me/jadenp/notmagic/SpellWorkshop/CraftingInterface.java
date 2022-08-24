package me.jadenp.notmagic.SpellWorkshop;


import me.jadenp.notmagic.NotMagic;
import me.jadenp.notmagic.RevisedClasses.Items;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Lightable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class CraftingInterface implements Listener {
    /**
     *
     * Custom spells
     *  *  Main spell attributes
     *  *  - Potential - how its getting to its activation position - power
     *  *  - Area Effect - area effect - how big it is effected
     *  *  - intensity - what the spell does - destruction
     *  *  - Accuracy - how much control the player has in where the spell goes
     *  *  - Control - where the spell comes from - & how accurate that position will be
     *
     *  Block structure for workshop
     *      using wand, click a block structure:
     *      Candle    |                  | Candle
     *      Bookshelf | Enchanting table | Bookshelf
     *
     *  Combining structure to combine essence:
     *      coming soon...
     *
     *  essence:        Potential                  Area Effect: Immune                                 Intensity                           Control
     *    - Fire        - 8 many arks              - 6 patches: nether mobs                            - High: ignites surfaces           - closest fire/lava source or < 30 blocks from crosshair
     *    - Earth       - 5 bumpy                  - 8 cracks on ground : flying                       - Med: toss blocks                 - < 10 blocks away
     *    - Water       - 7 wavey                  - 5 slower activation: water mobs & turtle helmet   - Low: big push  & water spawn   - closest water source or < 30 blocks from crosshair
     *    - Wind        - 9 many short bursts      - 7 air only: ground mobs                           - Med: med push                    - from sky < 15 blocks from crosshair
     *    - Electricity - 10 fast                  - 3 direct entity attack: lighting rod closer       - High: Lighting strikes           - from Sky - will attract to copper
     *    - Ice         - 4 spread out             - 10 fast-ish: wearing leather armor                - Low: Freeze                      - from floor < 15 blocks from crosshair
     *    - Poison      - 3 even more spread out   - 9 very slow activation: none                      - Med: slow damage tick            - random position < 20 blocks from crosshair
     *    - Living      - 1 only go a few meters   - 3 semi-fast: undead mobs                          - Low: heal                        - From player
     *    - Spectral    - 1 only go a few meters   - 4 semi-fast: unarmed & no armor                   - High: sparkles                   - Same y axis as player & same distance from crosshair, but random location
     *
     *  Accuracy: use ores for the accuracy
     *  - Empty: < 20 degrees from crosshair
     *  - Iron: < 15 degrees from crosshair
     *  - Gold: < 10 degrees from crosshair
     *  - Diamond: < 5 degrees from crosshair
     *  - Netherite Ingot: exactly where crosshair is
     *  - Netherite Block: snaps to nearby entities < 10 degrees from target position
     */

    private Plugin plugin;
    private NotMagic notMagic;
    private final Items items = new Items();
    private final boolean debug = false;
    // might change name later
    private final String workshopName = ChatColor.BLUE + "" + ChatColor.BOLD + "Spell " + ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Workshop";
    private final ItemStack[] workshopContents = new ItemStack[45];
    Map<Player, Block> guiBlock = new HashMap<>();
    public CraftingInterface (NotMagic notMagic){
        this.plugin = notMagic;
        this.notMagic = notMagic;


        // fill workshop
        // 26 1 20 37 13 31 24

        Arrays.fill(workshopContents, items.data("fill"));
        workshopContents[25] = null; // result
        workshopContents[11] = null; // potential
        workshopContents[2] = items.data("potentialPane");
        workshopContents[9] = null; // Area Effect
        workshopContents[0] = items.data("aePane");
        workshopContents[27] = null; // intensity
        workshopContents[36] = items.data("intensityPane");
        workshopContents[29] = null; // accuracy
        workshopContents[38] = items.data("accuracyPane");
        workshopContents[13] = null; // control
        workshopContents[4] = items.data("controlPane");
        workshopContents[31] = null; // magic dust
        workshopContents[40] = items.data("magicDustPane");
        workshopContents[24] = items.data("fillLime");
        workshopContents[33] = items.data("fillLime");
        workshopContents[34] = items.data("fillLime");
        workshopContents[35] = items.data("fillLime");
        workshopContents[26] = items.data("fillLime");
        workshopContents[17] = items.data("fillLime");
        workshopContents[16] = items.data("fillLime");
        workshopContents[15] = items.data("fillLime");


        Bukkit.getPluginManager().registerEvents(this, plugin);


    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        if (event.getClickedBlock() != null) {
            if (event.getClickedBlock().getType() == Material.ENCHANTING_TABLE)
            if (items.isWand(event.getPlayer().getInventory().getItemInMainHand())) {
                // check for blocks
                boolean pass = false;
                if (event.getClickedBlock().getRelative(BlockFace.EAST).getType() == Material.BOOKSHELF && event.getClickedBlock().getRelative(BlockFace.WEST).getType() == Material.BOOKSHELF){
                    Block b1 = event.getClickedBlock().getRelative(BlockFace.EAST).getRelative(BlockFace.UP);
                    Block b2 = event.getClickedBlock().getRelative(BlockFace.WEST).getRelative(BlockFace.UP);
                    if (b1.getType().name().toUpperCase(Locale.ROOT).contains("CANDLE") && b2.getType().name().toUpperCase(Locale.ROOT).contains("CANDLE")){
                        if (((Lightable) b2.getBlockData()).isLit() && ((Lightable) b1.getBlockData()).isLit()){
                            pass = true;
                        }
                    }
                } else if (event.getClickedBlock().getRelative(BlockFace.NORTH).getRelative(BlockFace.UP).getType() == Material.BOOKSHELF && event.getClickedBlock().getRelative(BlockFace.SOUTH).getRelative(BlockFace.UP).getType() == Material.BOOKSHELF) {
                    Block b1 = event.getClickedBlock().getRelative(BlockFace.NORTH).getRelative(BlockFace.UP);
                    Block b2 = event.getClickedBlock().getRelative(BlockFace.SOUTH).getRelative(BlockFace.UP);
                    if (b1.getType().name().toUpperCase(Locale.ROOT).contains("CANDLE") && b2.getType().name().toUpperCase(Locale.ROOT).contains("CANDLE")){
                        if (((Lightable) b2.getBlockData()).isLit() && ((Lightable) b1.getBlockData()).isLit()){
                            pass = true;
                        }
                    }
                }
                if (!pass)
                    return;
                // open workshop
                Inventory workshop = Bukkit.createInventory(null, 45, workshopName);
                workshop.setContents(workshopContents);
                event.getPlayer().openInventory(workshop);
                event.getPlayer().updateInventory();
                event.setCancelled(true);
                guiBlock.put(event.getPlayer(), event.getClickedBlock());
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){

        if (event.getView().getTitle().equals(workshopName)){
            ItemStack[] contents = event.getView().getTopInventory().getContents();
            ItemStack potential = contents[11];
            ItemStack areaEffect = contents[9];
            ItemStack intensity = contents[27];
            ItemStack accuracy = contents[29];
            ItemStack control = contents[13];
            ItemStack dust = contents[31];
            // check if one of the crafting slots will be updated
            if (!event.isCancelled())
            if (event.getRawSlot() < 45) {
                if (event.getCursor() != null) {
                    if (event.getAction() == InventoryAction.PLACE_SOME || event.getAction() == InventoryAction.PLACE_ALL || event.getAction() == InventoryAction.PLACE_ONE || event.getAction() == InventoryAction.SWAP_WITH_CURSOR) {
                        if (event.getRawSlot() == 11) {
                            potential = event.getCursor();
                        }
                        if (event.getRawSlot() == 9) {
                            areaEffect = event.getCursor();
                        }
                        if (event.getRawSlot() == 27) {
                            intensity = event.getCursor();
                        }
                        if (event.getRawSlot() == 29) {
                            accuracy = event.getCursor();
                        }
                        if (event.getRawSlot() == 13) {
                            control = event.getCursor();
                        }
                        if (event.getRawSlot() == 31) {
                            dust = event.getCursor();
                        }
                    } else if (event.getAction() == InventoryAction.PICKUP_ALL || event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                        if (event.getRawSlot() == 11) {
                            potential = null;
                        }
                        if (event.getRawSlot() == 9) {
                            areaEffect = null;
                        }
                        if (event.getRawSlot() == 27) {
                            intensity = null;
                        }
                        if (event.getRawSlot() == 29) {
                            accuracy = null;
                        }
                        if (event.getRawSlot() == 13) {
                            control = null;
                        }
                        if (event.getRawSlot() == 31) {
                            dust = null;
                        }
                    } else if (event.getAction() == InventoryAction.HOTBAR_SWAP){
                        if (event.getRawSlot() == 11) {
                            if (event.getClick() == ClickType.NUMBER_KEY) {
                                potential = event.getView().getBottomInventory().getItem(event.getHotbarButton());
                            } else {
                                potential = ((PlayerInventory) event.getView().getBottomInventory()).getExtraContents()[0];
                            }
                        }
                        if (event.getRawSlot() == 9) {
                            if (event.getClick() == ClickType.NUMBER_KEY) {
                                areaEffect = event.getView().getBottomInventory().getItem(event.getHotbarButton());
                            } else {
                                areaEffect = ((PlayerInventory) event.getView().getBottomInventory()).getExtraContents()[0];
                            }
                        }
                        if (event.getRawSlot() == 27) {
                            if (event.getClick() == ClickType.NUMBER_KEY) {
                                intensity = event.getView().getBottomInventory().getItem(event.getHotbarButton());
                            } else {
                                intensity = ((PlayerInventory) event.getView().getBottomInventory()).getExtraContents()[0];
                            }
                        }
                        if (event.getRawSlot() == 29) {
                            if (event.getClick() == ClickType.NUMBER_KEY) {
                                accuracy = event.getView().getBottomInventory().getItem(event.getHotbarButton());
                            } else {
                                accuracy = ((PlayerInventory) event.getView().getBottomInventory()).getExtraContents()[0];
                            }
                        }
                        if (event.getRawSlot() == 13) {
                            if (event.getClick() == ClickType.NUMBER_KEY) {
                                control = event.getView().getBottomInventory().getItem(event.getHotbarButton());
                            } else {
                                control = ((PlayerInventory) event.getView().getBottomInventory()).getExtraContents()[0];
                            }
                        }
                        if (event.getRawSlot() == 31) {
                            if (event.getClick() == ClickType.NUMBER_KEY) {
                                dust = event.getView().getBottomInventory().getItem(event.getHotbarButton());
                            } else {
                                dust = ((PlayerInventory) event.getView().getBottomInventory()).getExtraContents()[0];
                            }
                        }
                    } else if (event.getAction() == InventoryAction.COLLECT_TO_CURSOR) {
                        //event.getClick() == ClickType.DOUBLE_CLICK
                        if (potential != null)
                            if (potential.isSimilar(event.getCursor())) {
                                potential = null;
                            }
                        if (areaEffect != null)
                            if (areaEffect.isSimilar(event.getCursor())) {
                                areaEffect = null;
                            }
                        if (intensity != null)
                            if (intensity.isSimilar(event.getCursor())) {
                                intensity = null;
                            }
                        if (accuracy != null)
                            if (accuracy.isSimilar(event.getCursor())) {
                                accuracy = null;
                            }
                        if (control != null)
                            if (control.isSimilar(event.getCursor())) {
                                control = null;
                            }
                        if (dust != null)
                            if (dust.isSimilar(event.getCursor())) {
                                dust = null;
                            }
                    }
                }
            }

            // to stop shift clicking into result slot
            if (event.getClick().isShiftClick() && event.getView().getBottomInventory().equals(event.getClickedInventory())){
                ItemStack clickedItem = event.getCurrentItem();
                if (clickedItem != null) {
                    ItemStack[] craftingSlots = new ItemStack[]{areaEffect, potential, control, null, intensity, accuracy, dust};
                    ItemStack[] filledSlots = findSpace(craftingSlots, clickedItem);
                    if (filledSlots[3] != null){
                        event.setCancelled(true);
                        craftingSlots = new ItemStack[]{areaEffect, potential, control, intensity, accuracy, dust};
                        ItemStack[] temp = findSpace(craftingSlots, clickedItem);
                        filledSlots = new ItemStack[]{temp[0], temp[1], temp[2], null, temp[3], temp[4], temp[5], temp[6]};
                        event.getView().setItem(9, temp[0]);
                        event.getView().setItem(11, temp[1]);
                        event.getView().setItem(13, temp[2]);
                        event.getView().setItem(27, temp[3]);
                        event.getView().setItem(29, temp[4]);
                        event.getView().setItem(31, temp[5]);
                        event.getView().setItem(event.getRawSlot(), temp[6]);
                    }
                    areaEffect = filledSlots[0];
                    potential = filledSlots[1];
                    control = filledSlots[2];
                    intensity = filledSlots[4];
                    accuracy = filledSlots[5];
                    dust = filledSlots[6];
                }
            }
            if (event.getView().getTopInventory().equals(event.getClickedInventory())) {
                // cancel event if the slot is in top inv and isnt one of the movable slots
                if (event.getSlot() != 11 && event.getSlot() != 9 && event.getSlot() != 27 && event.getSlot() != 29 && event.getSlot() != 13 && event.getSlot() != 31) {
                    event.setCancelled(true);
                }
                if (event.getSlot() == 25) {
                    if (event.getCurrentItem() != null) {
                        // try making result
                        int magicValue = dust != null ? dust.getAmount() : 0;
                        int accuracyNum = 0;
                        if (accuracy != null) {
                            if (accuracy.getType() == Material.IRON_INGOT) {
                                accuracyNum = 1;
                            } else if (accuracy.getType() == Material.GOLD_INGOT) {
                                accuracyNum = 2;
                            } else if (accuracy.getType() == Material.DIAMOND) {
                                accuracyNum = 3;
                            } else if (accuracy.getType() == Material.NETHERITE_INGOT) {
                                accuracyNum = 4;
                            } else if (accuracy.getType() == Material.NETHERITE_BLOCK) {
                                accuracyNum = 5;
                            }
                        }
                        WorkshopSpell workshopSpell = new WorkshopSpell(
                                Essence.fromItemStack(potential), potential != null ? potential.getAmount() : 0,
                                Essence.fromItemStack(areaEffect), areaEffect != null ? areaEffect.getAmount() : 0,
                                Essence.fromItemStack(intensity), intensity != null ? intensity.getAmount() : 0,
                                Essence.fromItemStack(control), control != null ? control.getAmount() : 0,
                                accuracyNum);
                        // do animation
                        new BukkitRunnable(){
                            final boolean success = workshopSpell.getMagicValue() < magicValue;
                            final Block enchantingTable = guiBlock.get((Player) event.getWhoClicked());
                            final double rotationAngle = 0.5;
                            final Location center = new Location(enchantingTable.getWorld(), enchantingTable.getX() + 0.5, enchantingTable.getY() + 1.5, enchantingTable.getZ() + 0.5);
                            Vector nextSpawn = new Vector(0,0,0.5);
                            final Player player = (Player) event.getWhoClicked();
                            int runs = 0;
                            @Override
                            public void run() {
                                nextSpawn.rotateAroundY(rotationAngle);

                                 new BukkitRunnable(){
                                     final Location location = new Location(center.getWorld(), center.getX() + nextSpawn.getX(), center.getY() + nextSpawn.getY(), center.getZ() + nextSpawn.getZ());
                                     @Override
                                     public void run() {
                                         player.spawnParticle(Particle.END_ROD, location, 0, 0,-0.1,0);
                                     }
                                 }.runTask(plugin);
                                 runs++;
                                 if (runs >= 30){
                                     this.cancel();
                                     assert center.getWorld() != null;
                                     center.getWorld().spawnParticle(Particle.FLASH, center, 1);
                                     center.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, center, 25);
                                     if (success){
                                         Bukkit.broadcastMessage("success");
                                         center.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, center, 10, 0.2, 0.2, 0.2);
                                         center.getWorld().playSound(center, Sound.ITEM_TRIDENT_THUNDER,1,1);
                                     } else {
                                         Bukkit.broadcastMessage("fail");
                                         center.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, center, 0, 0, 0.1, 0);
                                         center.getWorld().playSound(center, Sound.ENTITY_TURTLE_EGG_CRACK,1,1);
                                     }
                                 }
                            }
                        }.runTaskTimerAsynchronously(plugin, 0L, 2L);
                        contents[11] = null;
                        contents[9] = null;
                        contents[27] = null;
                        contents[13] = null;
                        contents[31] = null;
                        if (accuracy != null && accuracy.getAmount() > 1){
                            accuracy.setAmount(accuracy.getAmount() - 1);
                            contents[21] = accuracy;
                        } else {
                            contents[21] = null;
                        }
                        event.getView().getTopInventory().setContents(contents);
                        event.getView().close();
                        return;
                    }
                }
            }
            // check if you can craft item
            if (debug){
                if (intensity != null){
                    Bukkit.getLogger().info("Intensity: " + items.isEssence(intensity));
                }
                if (areaEffect != null){
                    Bukkit.getLogger().info("Area Effect: " + items.isEssence(areaEffect));
                }
                if (potential != null){
                    Bukkit.getLogger().info("Potential: " + items.isEssence(potential));
                }
                if (control != null){
                    Bukkit.getLogger().info("Control: " + items.isEssence(control));
                }
                if (accuracy != null){
                    Bukkit.getLogger().info("Accuracy: " + items.isEssence(accuracy));
                }
                if (dust != null){
                    Bukkit.getLogger().info("Dust: " + dust.isSimilar(items.data("MagicDust")));
                }
            }
            if (intensity != null && areaEffect != null && items.isEssence(intensity) && items.isEssence(areaEffect) && (potential == null || items.isEssence(potential)) && (control == null || items.isEssence(control)) && (accuracy == null || isAccuracyOre(accuracy)) && (dust == null || dust.isSimilar(items.data("MagicDust")) || dust.isSimilar(items.data("MagicBlock")) || dust.isSimilar(items.data("CompressedMagicBlock")))) {
                if (contents[25] == null) {
                    contents[25] = items.data("mysteriousSpell");
                    event.getView().getTopInventory().setContents(contents);
                }
            } else {
                if (contents[25] != null) {
                    contents[25] = null;
                    event.getView().getTopInventory().setContents(contents);
                }
            }
        }
    }

    public boolean isAccuracyOre(ItemStack itemStack){
        Material material = itemStack.getType();
        return material == Material.IRON_INGOT || material == Material.GOLD_INGOT || material == Material.DIAMOND || material == Material.NETHERITE_INGOT || material == Material.NETHERITE_BLOCK;
    }

    public ItemStack[] findSpace(ItemStack[] availableSlots, ItemStack item){
        final ItemStack itemStack = item.clone();
        int amount = itemStack.getAmount();
        Material material = itemStack.getType();
        for (int i = 0; i < availableSlots.length; i++){
            if (availableSlots[i] == null){
                if (amount > material.getMaxStackSize()){
                    itemStack.setAmount(material.getMaxStackSize());
                    availableSlots[i] = itemStack.clone();
                    amount -= material.getMaxStackSize();
                } else {
                    itemStack.setAmount(amount);
                    availableSlots[i] = itemStack.clone();
                    amount = 0;
                    break;
                }
            } else if (availableSlots[i].isSimilar(itemStack)){
                int cAmount = availableSlots[i].getAmount();
                if (amount + cAmount > material.getMaxStackSize()){
                    itemStack.setAmount(material.getMaxStackSize());
                    availableSlots[i] = itemStack.clone();
                    amount -= material.getMaxStackSize() - cAmount;
                } else {
                    itemStack.setAmount(amount + cAmount);
                    availableSlots[i] = itemStack.clone();
                    amount = 0;
                    break;
                }
            }
        }
        ItemStack[] result = new ItemStack[availableSlots.length + 1];
        System.arraycopy(availableSlots, 0, result, 0, availableSlots.length);
        if (amount > 0) {
            itemStack.setAmount(amount);
            result[availableSlots.length] = itemStack;
        } else {
            result[availableSlots.length] = null;
        }
        return result;
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event){
        if (event.getView().getTitle().equals(workshopName)){
            if (!event.isCancelled()){
                if (event.getNewItems().containsKey(25)){
                    event.setCancelled(true);
                } else {
                    ItemStack[] contents = event.getView().getTopInventory().getContents();
                    ItemStack potential = contents[11];
                    ItemStack areaEffect = contents[9];
                    ItemStack intensity = contents[27];
                    ItemStack accuracy = contents[29];
                    ItemStack control = contents[13];
                    ItemStack dust = contents[31];
                    // change items if there is going to be a change in contents
                    for (Map.Entry<Integer, ItemStack> newItems : event.getNewItems().entrySet()){
                        int slot = newItems.getKey();
                        ItemStack item = newItems.getValue();
                        if (item != null && !item.getType().isAir()) {
                            //Bukkit.getLogger().info(slot + ": " + item.getType());
                            if (slot == 11) {
                                potential = item;
                            }
                            if (slot == 9){
                                areaEffect = item;
                            }
                            if (slot == 27){
                                intensity = item;
                            }
                            if (slot == 29){
                                accuracy = item;
                            }
                            if (slot == 13){
                                control = item;
                            }
                            if (slot == 31){
                                dust = item;
                            }
                        }
                    }
                    if (intensity != null && areaEffect != null && items.isEssence(intensity) && items.isEssence(areaEffect) && (potential == null || items.isEssence(potential)) && (control == null || items.isEssence(control)) && (accuracy == null || isAccuracyOre(accuracy)) && (dust == null || dust.isSimilar(items.data("MagicDust")) || dust.isSimilar(items.data("MagicBlock")) || dust.isSimilar(items.data("CompressedMagicBlock")))) {
                        if (contents[25] == null) {
                            contents[25] = items.data("mysteriousSpell");
                            event.getView().getTopInventory().setContents(contents);
                        }
                    } else {
                        if (contents[25] != null) {
                            contents[25] = null;
                            event.getView().getTopInventory().setContents(contents);
                        }
                    }
                }
            }
        }
    }



    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event){
        if (event.getView().getTitle().equals(workshopName)){
            ItemStack[] contents = event.getView().getTopInventory().getContents();
            ItemStack potential = contents[11];
            ItemStack areaEffect = contents[9];
            ItemStack intensity = contents[27];
            ItemStack accuracy = contents[29];
            ItemStack control = contents[13];
            ItemStack dust = contents[31];
            givePlayer((Player) event.getPlayer(), potential);
            givePlayer((Player) event.getPlayer(), areaEffect);
            givePlayer((Player) event.getPlayer(), intensity);
            givePlayer((Player) event.getPlayer(), accuracy);
            givePlayer((Player) event.getPlayer(), control);
            givePlayer((Player) event.getPlayer(), dust);
            guiBlock.remove((Player) event.getPlayer());
        }
    }
    public void givePlayer(Player p, ItemStack itemStack){
        if (itemStack == null)
            return;
        HashMap<Integer, ItemStack> leftOver = new HashMap<Integer, ItemStack>((p.getInventory().addItem(itemStack)));
        if (!leftOver.isEmpty()) {
            Location loc = p.getLocation();
            p.getWorld().dropItem(loc, leftOver.get(0));
        }
    }


    public String getWorkshopName() {
        return workshopName;
    }
}
