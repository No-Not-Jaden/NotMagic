package me.jadenp.notmagic.RevisedClasses.Objects;

import me.jadenp.notmagic.RevisedClasses.Language;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.*;

public class PlayerData {
    private UUID uuid;
    private String playerName;

    private int level = 1;
    private int xp = 0;

    private int mp = 50;
    private int mpMax = 50;
    // mp regen / 5 ticks
    private double mpRegen = 0.5;

    private long interactCooldown = 0;
    private ArrayList<String> spellsUnlocked;
    private Map<String, Long> spellsCooldown = new HashMap<>();

    private String mainSpell = "Burn";

    private List<Location> castPoints = new ArrayList<>();
    private List<Location> castVertexes = new ArrayList<>();
    private ArrayList<String> spellCasting = new ArrayList<>();
    private Vector castDirection;
    private Location castReference;
    private long timeSinceLastCast = 0;
    private boolean displayingSpell = false;

    private boolean calculating = false;


    public PlayerData(UUID uuid, String playerName, int level, int xp, int mpMax, double mpRegen, ArrayList<String> spellsUnlocked){
        this.uuid = uuid;
        this.playerName = playerName;
        this.level = level;
        this.xp = xp;
        this.mpMax = mpMax;
        this.mpRegen = mpRegen;
        this.spellsUnlocked = spellsUnlocked;


    }

    public void learnSpell(String spell){
        spellsUnlocked.add(spell);
    }

    public boolean isDisplayingSpell() {
        return displayingSpell;
    }

    public void setDisplayingSpell(boolean displayingSpell) {
        this.displayingSpell = displayingSpell;
    }

    public Map<String, Long> getSpellsCooldown() {
        return spellsCooldown;
    }

    public void setTimeSinceLastCast(long timeSinceLastCast) {
        this.timeSinceLastCast = timeSinceLastCast;
    }

    public ArrayList<String> getSpellsUnlocked() {
        return spellsUnlocked;
    }

    public double getMpRegen() {
        return mpRegen;
    }

    public long getInteractCooldown() {
        return interactCooldown;
    }

    public int getLevel() {
        return level;
    }

    public int getMp() {
        return mp;
    }

    public int getMpMax() {
        return mpMax;
    }

    public int getXp() {
        return xp;
    }

    public List<Location> getCastPoints() {
        return castPoints;
    }

    public String getMainSpell() {
        return mainSpell;
    }

    public String getPlayerName() {
        return playerName;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Vector getCastDirection() {
        return castDirection;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void addCastPoint(Location location){
        castPoints.add(location);
        timeSinceLastCast = System.currentTimeMillis();
    }

    public Location getCastReference() {
        return castReference;
    }

    public List<Location> getCastVertexes() {
        Iterator<Location> iterator = castVertexes.iterator();
        List<Location> copy = new ArrayList<>();
        while (iterator.hasNext()){
            copy.add((Location) iterator.next().clone());
        }
        return copy;
    }

    public void setCastReference(Location castReference) {
        this.castReference = castReference;
    }

    public long getTimeSinceLastCast() {
        return timeSinceLastCast;
    }

    public void addCastVertex(Location location, ArrayList<String> spellCasting){
        this.spellCasting = spellCasting;
        castVertexes.add(location);
    }

    public void replaceLastVertex(Location location){
        if (castVertexes.size() > 0){
            castVertexes.set(castVertexes.size()-1, location);
        }
    }

    public void resetCast(){
        castPoints.clear();
        castVertexes.clear();
        spellCasting.clear();
        castDirection = null;
        castReference = null;
        timeSinceLastCast = 0;
    }
    public void relog(){
        castPoints.clear();
        castVertexes.clear();
        interactCooldown = 0;
        castDirection = null;
        castReference = null;
        timeSinceLastCast = 0;
    }

    public ArrayList<String> getSpellCasting() {
        return spellCasting;
    }

    public void setSpellCasting(ArrayList<String> spellCasting) {
        this.spellCasting = spellCasting;
    }

    public void addCooldown(String spell, int ticks){
        if (spellsCooldown.containsKey(spell)){
            spellsCooldown.replace(spell, System.currentTimeMillis() + (ticks * 50L));
        } else {
            spellsCooldown.put(spell, System.currentTimeMillis() + (ticks * 50L));
        }
    }

    public boolean onCooldown(String spell){
        if (spellsCooldown.containsKey(spell)){
            if (spellsCooldown.get(spell) > System.currentTimeMillis()){
                return true;
            }
            spellsCooldown.remove(spell);
        }
        return false;
    }

    public void useMP(int amount){
        mp -= amount;
        addXP(amount);
    }

    public void addXP(int amount){
        xp += amount;
        while (xp >= Math.pow(level,2) * 200){
            // level up
            xp -= Math.pow(level,2) * 200;
            level++;
            Objects.requireNonNull(Bukkit.getPlayer(uuid)).sendMessage(Language.prefix + Language.levelUp.replace("{level}",  level + ""));
        }
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setMainSpell(String mainSpell) {
        this.mainSpell = mainSpell;
    }

    public void setInteractCooldown(long interactCooldown) {
        this.interactCooldown = interactCooldown;
    }

    public void setMp(int mp) {
        this.mp = mp;
    }

    public double getMainCooldownTicks(){
        if (spellsCooldown.containsKey(mainSpell)){
            if (spellsCooldown.get(mainSpell) > System.currentTimeMillis()){
                return (double) (spellsCooldown.get(mainSpell) - System.currentTimeMillis()) / 50;
            }
            spellsCooldown.remove(mainSpell);
        }
        return 0;
    }

    public boolean isCalculating() {
        return calculating;
    }

    public void setCalculating(boolean calculating) {
        this.calculating = calculating;
    }
}
