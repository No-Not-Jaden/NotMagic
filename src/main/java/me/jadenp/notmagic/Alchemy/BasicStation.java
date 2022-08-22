package me.jadenp.notmagic.Alchemy;

import me.jadenp.notmagic.RevisedClasses.Items;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


import java.awt.Color;
import java.util.*;
import java.util.List;

public class BasicStation {
    Items items = new Items();
    private Map<java.util.List<ItemStack>, java.util.List<ItemStack>> result;
    private Map<java.util.List<ItemStack>, String> recipes;
    public BasicStation(){
        this.recipes = new HashMap<>();
        this.result = new HashMap<>();
    }
    public void addRecipe(java.util.List<ItemStack> ingredients, String station, java.util.List<ItemStack> output){
        recipes.put(ingredients, station);
        result.put(ingredients, output);
    }
    public java.util.List<ItemStack> works(Map<ItemStack, Integer> amounts, Location block) {

        Iterator hmIterator = recipes.entrySet().iterator();

        while (hmIterator.hasNext()) {
            Map.Entry mapElement = (Map.Entry)hmIterator.next();
            List<ItemStack> inventory = new ArrayList<>();
            for (Map.Entry<ItemStack, Integer> Entry : amounts.entrySet()) {
                ItemStack oneStack;
                if (Entry.getKey().getType() == Material.POTION){
                    oneStack = Entry.getKey();
                } else {
                    oneStack = new ItemStack(Entry.getKey().getType(), 1);
                    oneStack.setItemMeta(Entry.getKey().getItemMeta());
                    oneStack.setData(Entry.getKey().getData());
                    oneStack.addUnsafeEnchantments(Entry.getKey().getEnchantments());
                }
                inventory.add(oneStack);
            }

            boolean bad = false;
                for (int i = 0; i < ((java.util.List<ItemStack>) mapElement.getKey()).size(); i++) {
                    ItemStack oneStack;
                    if (((java.util.List<ItemStack>) mapElement.getKey()).get(i).getType() == Material.POTION){
                        oneStack = ((java.util.List<ItemStack>) mapElement.getKey()).get(i);
                    } else {
                        oneStack = new ItemStack(((java.util.List<ItemStack>) mapElement.getKey()).get(i).getType(), 1);
                        oneStack.setItemMeta(((java.util.List<ItemStack>) mapElement.getKey()).get(i).getItemMeta());
                        oneStack.setData(((java.util.List<ItemStack>) mapElement.getKey()).get(i).getData());
                        oneStack.addUnsafeEnchantments(((java.util.List<ItemStack>) mapElement.getKey()).get(i).getEnchantments());
                    }
                    if (inventory.contains(oneStack)) {
                        if (amounts.get(oneStack) >= ((java.util.List<ItemStack>) mapElement.getKey()).get(i).getAmount()) {
                            inventory.remove(oneStack);
                        } else {
                            bad = true;
                            break;
                        }
                    } else {
                        bad = true;
                        break;
                    }
                }
                if (!bad){
                    if (inventory.size() == 0){
                        Location l = new Location(block.getWorld(), block.getX() + 0.5, block.getY() + 1.2, block.getZ() + 0.5);
                        for (ItemStack out : result.get(mapElement.getKey())){
                            l.getWorld().dropItem(l, out);
                        }
                        return (List<ItemStack>) mapElement.getKey();
                    }
                }


        }
        return null;
    }

    public java.util.List<ItemStack> brew(List<ItemStack> itemStacks, Location block, Player p){
String prefix = ChatColor.GRAY + "[" + ChatColor.of(new Color(26, 194, 232)) + "Not" + ChatColor.of(new Color(232, 26, 225)) + "Magic" + ChatColor.GRAY + "] " + ChatColor.DARK_GRAY + "» ";


                Map<ItemStack, Integer> amounts = new HashMap<>();
                for (ItemStack itemStack : itemStacks) {
                    ItemStack oneStack;
                    if (itemStack != null) {
                        if (itemStack.getType() == Material.POTION){
                            if (amounts.containsKey(itemStack)) {
                                amounts.replace(itemStack, amounts.get(itemStack) + itemStack.getAmount());
                            } else {
                                amounts.put(itemStack, itemStack.getAmount());
                            }
                        } else {
                            oneStack = new ItemStack(itemStack.getType(), 1);
                            oneStack.setItemMeta(itemStack.getItemMeta());
                            oneStack.setData(itemStack.getData());
                            oneStack.addUnsafeEnchantments(itemStack.getEnchantments());
                            if (amounts.containsKey(oneStack)) {
                                amounts.replace(oneStack, amounts.get(oneStack) + itemStack.getAmount());
                            } else {
                                amounts.put(oneStack, itemStack.getAmount());
                            }
                        }

                    }
                }
        java.util.List<ItemStack> output = works(amounts, block);
if (output != null){
    block.getBlock().setType(Material.CAULDRON);
    double red = 255 / 255D;
    double green = 255 / 255D;
    double blue = 255 / 255D;
    for (int i = 0; i < 20; i++)
    block.getWorld().spawnParticle(Particle.SPELL_MOB, new Location(block.getWorld(), block.getX() + 0.5, block.getY() + 1, block.getZ() + 0.5), 0, red, green, blue, 1);
    p.playSound(block, Sound.BLOCK_BREWING_STAND_BREW,1,1);
    return output;
}



                p.sendMessage(prefix + ChatColor.of(new Color(250, 165, 17)) + "Unknown Concoction");
        java.util.List<ItemStack> output2 = new ArrayList<>();
        output2.add(new ItemStack(Material.AIR, 0));
                return output2;
    }
}
