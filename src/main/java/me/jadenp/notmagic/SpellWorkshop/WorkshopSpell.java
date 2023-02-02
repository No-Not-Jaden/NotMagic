package me.jadenp.notmagic.SpellWorkshop;

import me.jadenp.notmagic.NotMagic;
import me.jadenp.notmagic.RevisedClasses.Spell;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WorkshopSpell extends Spell {

    private final Essence potential;
    private final int potentialAmount;
    private final Essence areaEffect;
    private final int areaEffectAmount;
    private final Essence intensity;
    private final int intensityAmount;
    private final Essence control;
    private final int controlAmount;
    private final int accuracy;
    private final boolean mainSpell;
    private final int magicValue;
    private final UUID uuid;

    public WorkshopSpell(Essence potential, int potentialAmount, Essence areaEffect, int areaEffectAmount, Essence intensity, int intensityAmount, Essence control, int controlAmount, int accuracy, NotMagic notMagic){
        // constants will have to be changed eventually - spell size should be dependent on the magic value
        super(null, 0, 0, 0, 0, null, null, notMagic, false);

        int manaCost;
        manaCost = potential.getPotentialMana(potentialAmount) + areaEffect.getAreaEffectMana(areaEffectAmount) + intensity.getIntensityMana(intensityAmount) + control.getControlMana(controlAmount) + (accuracy * 2);
        if (potential.equals(areaEffect) && potential.equals(control) && potential.equals(intensity)){
            manaCost -= 15;
        }
        super.setMpCost(manaCost);
        // change
        super.setCastTime(3);
        super.setCooldown(3);
        super.setRequiredLevel(1);
        super.setSpellPattern(notMagic.eventClass.magicClass.spellIndex.getUniqueSpellPattern(5));
        //
        this.potential = potential;
        this.potentialAmount = potentialAmount;
        this.areaEffect = areaEffect;
        this.areaEffectAmount = areaEffectAmount;
        this.intensity = intensity;
        this.intensityAmount = intensityAmount;
        this.control = control;
        this.controlAmount = controlAmount;
        this.accuracy = accuracy;


        this.magicValue = potentialAmount * potential.getPotentialPower() + areaEffectAmount * areaEffect.getAreaEffectPower() + intensityAmount * intensity.getIntensityPower() + controlAmount * control.getControlPower() + accuracy * 2;
        uuid = UUID.randomUUID();

        this.mainSpell = manaCost < 25;
        super.setName(notMagic.eventClass.getUniqueSpellName(this));
        super.setSpellBook(getSpellBook());
    }

    public WorkshopSpell(Essence potential, int potentialAmount, Essence areaEffect, int areaEffectAmount, Essence intensity, int intensityAmount, Essence control, int controlAmount, int accuracy, String name, int manaCost, boolean mainSpell, int magicValue, int castTime, UUID uuid, int cooldown, int requiredLevel, List<String> spellPattern, NotMagic notMagic){
        super(name, manaCost, castTime, cooldown, requiredLevel, spellPattern, null, notMagic, false);
        this.potential = potential;
        this.potentialAmount = potentialAmount;
        this.areaEffect = areaEffect;
        this.areaEffectAmount = areaEffectAmount;
        this.intensity = intensity;
        this.intensityAmount = intensityAmount;
        this.control = control;
        this.controlAmount = controlAmount;
        this.accuracy = accuracy;
        this.mainSpell = mainSpell;
        this.magicValue = magicValue;
        this.uuid = uuid;
        super.setSpellBook(getSpellBook());
    }

    public ItemStack getSpellBook(){
        ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.YELLOW + super.getName() + " Spell");
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


    public int getAreaEffectAmount() {
        return areaEffectAmount;
    }

    public int getControlAmount() {
        return controlAmount;
    }

    public int getIntensityAmount() {
        return intensityAmount;
    }


    public int getPotentialAmount() {
        return potentialAmount;
    }

    public boolean isMainSpell() {
        return mainSpell;
    }
}
