package me.jadenp.notmagic.SpellWorkshop;

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

    public WorkshopSpell(Essence potential, int potentialAmount, Essence areaEffect, int areaEffectAmount, Essence intensity, int intensityAmount, Essence control, int controlAmount, int accuracy){
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
        this.name = "?";
        manaCost1 = potential.getPotentialMana(potentialAmount) + areaEffect.getAreaEffectMana(areaEffectAmount) + intensity.getIntensityMana(intensityAmount) + control.getControlMana(controlAmount) + (accuracy * 2);
        if (potential.equals(areaEffect) && potential.equals(control) && potential.equals(intensity)){
            manaCost1 -= 15;
        }
        this.manaCost = manaCost1;
        this.mainSpell = manaCost < 25;

        this.magicValue = potentialAmount * potential.getPotentialPower() + areaEffectAmount * areaEffect.getAreaEffectPower() + intensityAmount * intensity.getIntensityPower() + controlAmount * control.getControlPower() + accuracy * 2;
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
