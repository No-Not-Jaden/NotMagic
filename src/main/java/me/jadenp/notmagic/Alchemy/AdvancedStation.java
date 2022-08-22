package me.jadenp.notmagic.Alchemy;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.awt.*;
import java.util.*;
import java.util.List;

public class AdvancedStation {
    private Map<List<ItemStack>, List<ItemStack>> recipes = new HashMap<>();
    private final String prefix = ChatColor.GRAY + "[" + ChatColor.of(new Color(26, 194, 232)) + "Not" + ChatColor.of(new Color(232, 26, 225)) + "Magic" + ChatColor.GRAY + "] " + ChatColor.DARK_GRAY + "» " + ChatColor.of(new Color(250, 165, 17)) + "";

    public AdvancedStation(){

    }
    public void addRecipe(List<ItemStack> requirements, List<ItemStack> result){
        recipes.put(requirements,result);
    }
    public boolean brew(List<ItemStack> input, Location controllerBlock, Player p){
        for (Map.Entry<List<ItemStack>, List<ItemStack>> listListEntry : recipes.entrySet()) {
            boolean bad = false;
            List<ItemStack> requirements = listListEntry.getKey();
            if (input.size() == requirements.size()) {


                for (int i = 0; i < input.size(); i++) {
                    if (requirements.get(i) != null) {
                        if (input.get(i).equals(requirements.get(i))) {
                            // make result
                        } else {
                            bad = true;
                        }
                    }
                }
                if (!bad) {
                    List<ItemStack> output = listListEntry.getValue();
                    for (ItemStack out : output)
                        Objects.requireNonNull(controllerBlock.getWorld()).dropItem(new Location(controllerBlock.getWorld(), controllerBlock.getX() + 0.5, controllerBlock.getY() + 1.2, controllerBlock.getZ() + 0.5), out);
                    return true;
                }
            }
        }
        p.sendMessage(prefix + "Unknown Concoction!");
        return false;
    }


}
