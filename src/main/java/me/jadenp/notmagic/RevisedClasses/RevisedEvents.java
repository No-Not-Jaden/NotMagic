package me.jadenp.notmagic.RevisedClasses;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import me.jadenp.notmagic.NotMagic;
import me.jadenp.notmagic.SpellWorkshop.Essence;
import me.jadenp.notmagic.SpellWorkshop.SpellNames;
import me.jadenp.notmagic.SpellWorkshop.WorkshopSpell;
import me.jadenp.notmagic.SpellWorkshop.WorkshopSpellAdapter;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class RevisedEvents implements Listener {

    private Plugin plugin;
    private NotMagic notMagic;
    public Magic magicClass;
    public Map<UUID, PlayerData> playerData = new HashMap<>();
    private final File workshopSpellsFile;
    private List<WorkshopSpell> workshopSpells = new ArrayList<>();
    private Map<UUID, Integer> magicEntities = new HashMap<>();
    private final File magicEntitiesFile;
    private static RevisedEvents instance;
    Gson gson;


    public RevisedEvents(NotMagic notMagic) throws IOException {
        instance = this;
        this.plugin = notMagic;
        this.notMagic = notMagic;
        magicClass = new Magic(notMagic,this);
        magicEntitiesFile = new File(plugin.getDataFolder() + File.separator + "magic-entities.json");
        workshopSpellsFile = new File(plugin.getDataFolder() + File.separator + "workshopSpells.json");
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        builder.registerTypeAdapter(PlayerData.class, new PlayerDataAdapter());
        builder.registerTypeAdapter(WorkshopSpell.class, new WorkshopSpellAdapter());
        gson = builder.create();

        if (workshopSpellsFile.exists()){
            workshopSpells = Arrays.asList(gson.fromJson(new String(Files.readAllBytes(Paths.get(workshopSpellsFile.getPath()))), WorkshopSpell[].class));
        }



        if (magicEntitiesFile.exists()){
            Type mapType = new TypeToken<Map<UUID, Integer>>() {}.getType();
            magicEntities = gson.fromJson(new String(Files.readAllBytes(Paths.get(magicEntitiesFile.getPath()))), mapType);
        }


        // auto save every 5 min
        new BukkitRunnable(){
            @Override
            public void run() {
                saveData();
            }
        }.runTaskTimerAsynchronously(plugin,3600, 3600);

        // spawn particles at magic entities
        new BukkitRunnable(){
            @Override
            public void run() {
                Iterator<Map.Entry<UUID, Integer>> iterator = magicEntities.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<UUID, Integer> entry = iterator.next();
                    Entity entity = Bukkit.getEntity(entry.getKey());
                    if (entity != null){
                        if (entity.getLocation().getChunk().isEntitiesLoaded()){
                            entityToEssence((LivingEntity) entity).spawnParticles(entity.getLocation().add(0,1,0));
                        }
                    } else {
                        iterator.remove();
                    }
                }

            }
        }.runTaskTimer(plugin, 20, 40);
    }

    public static RevisedEvents getInstance() {
        return instance;
    }

    public void saveData(){
        if (playerData.size() > 0) {
            for (Map.Entry<UUID, PlayerData> entry : playerData.entrySet()) {
                File pFile = new File(notMagic.playerRecords + File.separator + entry.getKey() + ".json");
                try {
                    pFile.createNewFile();
                    PlayerData value = entry.getValue();
                    FileWriter writer = new FileWriter(pFile);
                    gson.toJson(value, writer);
                    writer.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if (workshopSpells.size() > 0) {
            try {
                workshopSpellsFile.createNewFile();
                FileWriter writer = new FileWriter(workshopSpellsFile);
                gson.toJson(workshopSpells.toArray(), writer);
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (magicEntities.size() > 0) {
            try {
                magicEntitiesFile.createNewFile();
                FileWriter writer = new FileWriter(magicEntitiesFile);
                gson.toJson(magicEntities, writer);
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) throws IOException {
        Player p = event.getPlayer();
        String uID = p.getUniqueId().toString();

        if (!playerData.containsKey(p.getUniqueId())){
            File pFile = new File(notMagic.playerRecords+File.separator+uID+".json");
            if (pFile.exists()){
                PlayerData pData = gson.fromJson(new String(Files.readAllBytes(Paths.get(pFile.getPath()))), PlayerData.class);
                playerData.put(p.getUniqueId(), pData);
            } else {
                Bukkit.getLogger().info("Unique player joined! Creating Player Data.");
                playerData.put(p.getUniqueId(), new PlayerData(p.getUniqueId(), p.getName(), 1, 0, 50, 0.5, new ArrayList<>(Collections.singletonList("Burn"))));
            }
        } else {
            PlayerData data = playerData.get(p.getUniqueId());
            data.setPlayerName(p.getName());
            data.relog();
        }

        if (p.getInventory().contains(Items.data("RegenDust"))){
            p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100,1));
        }

        for (Player hidden : magicClass.spellIndex.getHiddenPlayers()){
            event.getPlayer().hidePlayer(NotMagic.getInstance(), hidden);
        }
    }




    public List<WorkshopSpell> getWorkshopSpells() {
        return workshopSpells;
    }

    public void addWorkshopSpell(){

    }

    public String getUniqueSpellName(WorkshopSpell spell){
        // list of modifier words for how much essence were put into the spell
        // 2 more words for the spell name:
        // 1 word for intensity, potential, area effect
        // 1 word for accuracy & control
        int multiplier = spell.getPotentialAmount() + spell.getAreaEffectAmount() + spell.getIntensityAmount() + spell.getControlAmount();
        String name = SpellNames.getMultiplierName(multiplier) + " " + SpellNames.combinePassiveEssence(spell.getControl(), spell.getAccuracy()) + " " + SpellNames.combineEssence(Arrays.asList(spell.getPotential(), spell.getAreaEffect(), spell.getIntensity(), spell.getControl()));
        if (name.charAt(0) == ' '){
            name = name.substring(1);
        }
        return name;
    }

    public WorkshopSpell getWorkshopSpell(Essence potential, int potentialAmount, Essence areaEffect, int areaEffectAmount, Essence intensity, int intensityAmount, Essence control, int controlAmount, int accuracy){
        for (WorkshopSpell spell : workshopSpells){
            if (spell.getAccuracy() == accuracy &&
                    spell.getPotential().equals(potential) && spell.getPotentialAmount() == potentialAmount &&
                    spell.getAreaEffect().equals(areaEffect) && spell.getAreaEffectAmount() == areaEffectAmount &&
                    spell.getIntensity().equals(intensity) && spell.getIntensityAmount() == intensityAmount &&
                    spell.getControl().equals(control) && spell.getControlAmount() == controlAmount){
                return spell;
            }
        }
        return null;
    }
    public void addMagicEntity(Entity entity, int level){
        magicEntities.put(entity.getUniqueId(), level);
    }

    @EventHandler
    public void onDamageEntity(EntityDamageByEntityEvent event){
        if (event.getEntity() instanceof EnderCrystal){
            if (event.getEntity().hasMetadata("magic")){
                event.setCancelled(true);
            }
        }
        if (event.getDamager() instanceof LivingEntity) {
            if (magicEntities.containsKey(event.getDamager().getUniqueId())) {
                int level = magicEntities.get(event.getDamager().getUniqueId());
                Essence type = entityToEssence((LivingEntity) event.getDamager());
                if (type == Essence.FIRE){
                    // burn em
                    event.getEntity().setFireTicks(20 * level);
                } else if (type == Essence.EARTH){
                    // fling em
                    event.getEntity().setVelocity(event.getEntity().getVelocity().add(new Vector(Math.random() * (level * 0.6) - (level * 0.3), Math.random() * (level * 0.6) - (level * 0.3), Math.random() * (level * 0.6) - (level * 0.3))));
                } else if (type == Essence.WATER){
                    // drown em
                    ((LivingEntity) event.getEntity()).setRemainingAir((int) (((LivingEntity) event.getEntity()).getRemainingAir() - Math.random() * 20 * level));
                } else if (type == Essence.WIND){
                    // whip em
                    event.getEntity().setVelocity(event.getEntity().getVelocity().add(new Vector(0, Math.random() * (level * 1.2) - (level * 0.6), 0)));
                } else if (type == Essence.ELECTRICITY){
                    // zap ep
                    event.getEntity().setVelocity(new Vector(0,0,0));
                } else if (type == Essence.ICE){
                    // freeze em
                    event.getEntity().setFreezeTicks(20 * level);
                } else if (type == Essence.POISON){
                    // poison em
                    ((LivingEntity) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20 + (level * 10), level / 2 - 1));
                } else if (type == Essence.LIVING){
                    // heal em
                    ((LivingEntity) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 20 + (level * 10), level / 2 - 1));
                } else if (type == Essence.SPECTRAL){
                    // teleport em
                    event.getEntity().teleport(event.getEntity().getLocation().add(new Vector(Math.random() * (level * 1.2) - (level * 0.6), Math.random() * (level * 1.2) - (level * 0.6), Math.random() * (level * 1.2) - (level * 0.6))));
                } else if  (type == Essence.BARRIER){
                    // fatigue em
                    ((LivingEntity) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 20 + (level * 10), level / 2 - 1));
                }
                type.spawnParticles(event.getEntity().getLocation().add(0,1,0));
                event.setDamage(event.getDamage() * (.5 + ((double) level / 2)));
            }
        }
    }

    @EventHandler
    public void onTarget(EntityTargetLivingEntityEvent event){
        if (magicEntities.containsKey(event.getEntity().getUniqueId())){
            event.getEntity().getWorld().playSound(event.getEntity().getLocation(), Sound.ENTITY_SKELETON_HORSE_AMBIENT_WATER, 1, 1);
            int level = magicEntities.get(event.getEntity().getUniqueId());

            new BukkitRunnable(){
                int i = 0;
                final Essence type = entityToEssence((LivingEntity) event.getEntity());
                final Location location = ((LivingEntity) event.getEntity()).getEyeLocation().add(0,0.8,0);
                @Override
                public void run() {
                    if (i < level){
                        Particle.DustOptions options = new Particle.DustOptions(type.getColor(), 3.0f);
                        location.getWorld().spawnParticle(Particle.REDSTONE, location.add(0,.2,0), 1, options);
                        location.getWorld().playSound(location, Sound.ITEM_FIRECHARGE_USE, 0.5f, 2);
                        i++;
                    } else {
                        this.cancel();
                    }
                }
            }.runTaskTimer(plugin, 5, 5);

        }
    }

    @EventHandler
    public void onDisable(PluginDisableEvent event){
        if (event.getPlugin().equals(NotMagic.getInstance())){
            for (Entity entity : magicClass.spellIndex.getMagicEntities()){
                entity.remove();
            }
            for (Player player : Bukkit.getOnlinePlayers()){
                for (Player hidden : magicClass.spellIndex.getHiddenPlayers()){
                    player.showPlayer(NotMagic.getInstance(), hidden);
                }
            }
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        if (magicClass.spellIndex.getHiddenPlayers().contains(event.getPlayer())){
            for (Player player : Bukkit.getOnlinePlayers()){
                    player.showPlayer(NotMagic.getInstance(), event.getPlayer());
            }
        }
        for (Player player : magicClass.spellIndex.getHiddenPlayers()){
            player.showPlayer(NotMagic.getInstance(), event.getPlayer());
        }
    }

    @EventHandler
    public void onSpawn(EntitySpawnEvent event) {
        if (event.getEntity() instanceof Mob) {
            double magicEntityChance = 0.05;
            if (Math.random() < magicEntityChance) {
                // spawn magic entity if it can be one
                int level = (int) (Settings.magicMobLevelMax - Math.sqrt(Math.random() * Math.pow(Settings.magicMobLevelMax,2)));
                if (entityToEssence((LivingEntity) event.getEntity()) != Essence.EMPTY){
                    AttributeInstance attribute = ((Mob) event.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH);
                    assert attribute != null;
                    double maxHealth = attribute.getValue() * (.5 + ((double) level / 2));
                    attribute.setBaseValue(maxHealth);
                    ((Mob) event.getEntity()).setHealth(maxHealth);
                    magicEntities.put(event.getEntity().getUniqueId(), level);
                }
            }
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event){
        // Drop magic dust for magic entities and possibly a spell book
        if (magicEntities.containsKey(event.getEntity().getUniqueId())){
            int amount = magicEntities.get(event.getEntity().getUniqueId());
            magicEntities.remove(event.getEntity().getUniqueId());
            ItemStack essence = entityToEssence(event.getEntity()).getItemStack();
            // check if a player killed the mob and if they used looting
            Player killer = event.getEntity().getKiller();
            if (killer != null){
                ItemStack handItem = killer.getInventory().getItemInMainHand();
                if (handItem.hasItemMeta()) {
                    ItemMeta meta = handItem.getItemMeta();
                    assert meta != null;
                    if (meta.hasEnchant(Enchantment.LOOT_BONUS_MOBS)) {
                        amount *= (Math.random() * meta.getEnchantLevel(Enchantment.LOOT_BONUS_MOBS)) + 1;
                    }
                }
            }
            essence.setAmount(amount);
            event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), essence);
            // chance that they will drop a spell book
            if (Math.random() <= 0.05) {
                Spell spell = magicClass.spellIndex.getLootListSpell();
                // try to get a book that the player can use
                if (killer != null){
                    PlayerData data = playerData.get(killer.getUniqueId());
                    for (int i = 0; i < 100; i++) {
                        if (data.getLevel() < spell.getRequiredLevel()) {
                            spell = magicClass.spellIndex.getLootListSpell();
                        } else {
                            break;
                        }
                    }
                }
                event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), spell.getSpellBook());
            }
        }
    }


    public Essence entityToEssence(LivingEntity entity){
        if (entity instanceof WitherSkeleton || entity instanceof PigZombie || entity instanceof Piglin || entity instanceof Hoglin || entity instanceof MagmaCube){
            return Essence.FIRE;
        }
        if (entity instanceof CaveSpider || entity instanceof Witch){
            return Essence.POISON;
        }
        if (entity instanceof Vex || entity instanceof Enderman || entity instanceof Shulker || entity instanceof Endermite || entity instanceof Silverfish){
            return Essence.SPECTRAL;
        }
        if (entity instanceof PolarBear || entity instanceof Stray){
            return Essence.ICE;
        }
        if (entity instanceof Pillager || entity instanceof Evoker || entity instanceof Vindicator || entity instanceof Ravager){
            return Essence.LIVING;
        }
        if (entity instanceof Drowned || entity instanceof Guardian){
            return Essence.WATER;
        }
        if (entity instanceof Phantom || entity instanceof Blaze || entity instanceof Ghast){
            return Essence.WIND;
        }
        if (entity instanceof Zombie || entity instanceof Skeleton || entity instanceof Spider || entity instanceof Creeper){
            return Essence.EARTH;
        }
        return Essence.EMPTY;

    }
}
