package me.jadenp.notmagic.SpellWorkshop;


import me.jadenp.notmagic.NotMagic;
import me.jadenp.notmagic.RevisedClasses.Items;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Lightable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

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
    // might change name later
    private final String workshopName = ChatColor.BLUE + "" + ChatColor.BOLD + "Spell " + ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Workshop";
    private final ItemStack[] workshopContents = new ItemStack[45];
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
            // to stop shift clicking into result slot
            if (event.getClick().isShiftClick()){
                if (potential != null && areaEffect != null && accuracy != null && dust != null){
                    ItemStack clickedItem = event.getCurrentItem();
                    if (clickedItem != null){
                        if (!clickedItem.isSimilar(potential) && !clickedItem.isSimilar(areaEffect) && !clickedItem.isSimilar(accuracy) && !clickedItem.isSimilar(dust)){
                            event.setCancelled(true);
                            // maybe in the future put the item in the next accurately available slot here
                        }
                    }
                }
            }
            if (event.getView().getTopInventory().equals(event.getClickedInventory())) {
                // cancel event if the slot is in top inv and isnt one of the movable slots
                if (event.getSlot() != 12 && event.getSlot() != 10 && event.getSlot() != 28 && event.getSlot() != 30 && event.getSlot() != 14 && event.getSlot() != 32){
                    event.setCancelled(true);
                }

                if (event.getSlot() == 25){
                    if (event.getCurrentItem() != null){
                        // try making result
                        event.getView().close();

                    }
                } else {
                    // check if you can craft item
                    // need at least intensity & area effect
                    if (intensity != null && areaEffect != null){
                        contents[25] = items.data("mysteriousSpell");
                    } else {
                        contents[25] = null;
                    }
                    if (!Arrays.equals(event.getView().getTopInventory().getContents(), contents)){
                        event.getView().getTopInventory().setContents(contents);
                    }
                }

            }
        }
    }

    /*
    @EventHandler
    public void onDrag(InventoryDragEvent event){
        if (event.getView().getTitle().equals(workshopName)){
            if (event.getRawSlots().contains(26)){
                event.setCancelled(true);
            }
        }
    }*/


}
