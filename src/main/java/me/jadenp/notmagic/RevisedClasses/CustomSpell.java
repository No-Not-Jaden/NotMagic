package me.jadenp.notmagic.RevisedClasses;

import org.bukkit.plugin.Plugin;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class CustomSpell {
    private String name;
    private boolean mainSpell;
    private final int mpCost;
    private final int castTime;
    private final int cooldown;
    private final int requiredLevel;
    private final List<String> castPattern;
    private final List<String> actions;
    private final List<String> spellLore;
    private final Plugin plugin;
    private final SpellIndex index;

    public CustomSpell(String name, boolean mainSpell, int mpCost, int castTime, int cooldown, int requiredLevel, List<String> castPattern, List<String> actions, List<String> lore, Plugin plugin, SpellIndex index){
// need to add spell book
        this.name = name;
        this.mainSpell = mainSpell;
        this.mpCost = mpCost;
        this.castTime = castTime;
        this.cooldown = cooldown;
        this.requiredLevel = requiredLevel;
        this.castPattern = castPattern;
        this.actions = actions;
        this.spellLore = lore;
        this.plugin = plugin;
        this.index = index;
    }

    public int getCastTime() {
        return castTime;
    }

    public int getCooldown() {
        return cooldown;
    }

    public int getMpCost() {
        return mpCost;
    }

    public int getRequiredLevel() {
        return requiredLevel;
    }

    public List<String> getActions() {
        return actions;
    }

    public List<String> getSpellPattern() {
        return castPattern;
    }

    public String getName() {
        return name;
    }

    public boolean isMainSpell() {
        return mainSpell;
    }

    public Spell toSpellObject(){
        return new Spell(name, mpCost, castTime, cooldown, requiredLevel, castPattern, getSpellBook(), plugin, true);
    }

    public ItemStack getSpellBook(){
        ItemStack Item = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta Meta = Item.getItemMeta();
        assert Meta != null;
        Meta.setDisplayName(ChatColor.YELLOW + "Spell Book");
        ArrayList<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GREEN + "" + ChatColor.ITALIC + name + ":");
        for (String str : spellLore){
            lore.add(ChatColor.DARK_GREEN + "" + ChatColor.ITALIC + str);
        }
        lore.add("");
        Meta.setLore(lore);
        Meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        Item.setItemMeta(Meta);
        Item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
        return Item;
    }
}
