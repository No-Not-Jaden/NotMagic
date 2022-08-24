package me.jadenp.notmagic.SpellWorkshop;

import me.jadenp.notmagic.NotMagic;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.UUID;

public class WorkshopSpell {

    private final Essence potential;
    private final int potentialAmount;
    private final Essence areaEffect;
    private final int areaEffectAmount;
    private final Essence intensity;
    private final int intensityAmount;
    private final Essence control;
    private final int controlAmount;
    private final int accuracy;
    private final String name;
    private final int manaCost;
    private final boolean mainSpell;
    private final int magicValue;
    private final int castTime; // in ticks
    private final UUID uuid;

    public WorkshopSpell(Essence potential, int potentialAmount, Essence areaEffect, int areaEffectAmount, Essence intensity, int intensityAmount, Essence control, int controlAmount, int accuracy, NotMagic notMagic){
        int manaCost1;
        this.potential = potential;
        this.potentialAmount = potentialAmount;
        this.areaEffect = areaEffect;
        this.areaEffectAmount = areaEffectAmount;
        this.intensity = intensity;
        this.intensityAmount = intensityAmount;
        this.control = control;
        this.controlAmount = controlAmount;
        this.accuracy = accuracy;
        this.name = notMagic.eventClass.getUniqueSpellName(this);
        manaCost1 = potential.getPotentialMana(potentialAmount) + areaEffect.getAreaEffectMana(areaEffectAmount) + intensity.getIntensityMana(intensityAmount) + control.getControlMana(controlAmount) + (accuracy * 2);
        if (potential.equals(areaEffect) && potential.equals(control) && potential.equals(intensity)){
            manaCost1 -= 15;
        }
        this.manaCost = manaCost1;
        this.mainSpell = manaCost < 25;

        this.magicValue = potentialAmount * potential.getPotentialPower() + areaEffectAmount * areaEffect.getAreaEffectPower() + intensityAmount * intensity.getIntensityPower() + controlAmount * control.getControlPower() + accuracy * 2;
        this.castTime = 3; // to be changed later
        uuid = UUID.randomUUID();
    }

    public WorkshopSpell(Essence potential, int potentialAmount, Essence areaEffect, int areaEffectAmount, Essence intensity, int intensityAmount, Essence control, int controlAmount, int accuracy, String name, int manaCost, boolean mainSpell, int magicValue, int castTime, UUID uuid){
        this.potential = potential;
        this.potentialAmount = potentialAmount;
        this.areaEffect = areaEffect;
        this.areaEffectAmount = areaEffectAmount;
        this.intensity = intensity;
        this.intensityAmount = intensityAmount;
        this.control = control;
        this.controlAmount = controlAmount;
        this.accuracy = accuracy;
        this.name = name;
        this.manaCost = manaCost;
        this.mainSpell = mainSpell;
        this.magicValue = magicValue;
        this.castTime = castTime;
        this.uuid = uuid;
    }

    public ItemStack getSpellBook(){
        ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.YELLOW + name + " Spell");
        ArrayList<String> lore = new ArrayList<>();
        lore.add("");
        if (mainSpell){
            lore.add(ChatColor.GREEN + "" + ChatColor.ITALIC + "Main Spell:");
        } else {
            lore.add(ChatColor.GREEN + "" + ChatColor.ITALIC + "Secondary Spell:");
        }

        lore.add(ChatColor.DARK_GREEN + "" + ChatColor.ITALIC + "AE: " + areaEffect.toString() + " " + areaEffectAmount);
        lore.add(ChatColor.DARK_GREEN + "" + ChatColor.ITALIC + "P: " + potential.toString() + " " + potentialAmount);
        lore.add(ChatColor.DARK_GREEN + "" + ChatColor.ITALIC + "C: " + control.toString() + " " + controlAmount);
        lore.add(ChatColor.DARK_GREEN + "" + ChatColor.ITALIC + "I: " + intensity.toString() + " " + intensityAmount);
        lore.add(ChatColor.DARK_GREEN + "" + ChatColor.ITALIC + "A: " + accuracy);
        lore.add("");
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
        return item;
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getMagicValue(){
        return magicValue;
    }

    public int getCastTime() {
        return castTime;
    }

    public Essence getAreaEffect() {
        return areaEffect;
    }

    public Essence getControl() {
        return control;
    }

    public Essence getIntensity() {
        return intensity;
    }

    public Essence getPotential() {
        return potential;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public String getName() {
        return name;
    }

    public int getAreaEffectAmount() {
        return areaEffectAmount;
    }

    public int getControlAmount() {
        return controlAmount;
    }

    public int getIntensityAmount() {
        return intensityAmount;
    }

    public int getManaCost() {
        return manaCost;
    }

    public int getPotentialAmount() {
        return potentialAmount;
    }

    public boolean isMainSpell() {
        return mainSpell;
    }
}
