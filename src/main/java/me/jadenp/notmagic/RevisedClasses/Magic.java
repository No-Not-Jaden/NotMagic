package me.jadenp.notmagic.RevisedClasses;

import me.jadenp.notmagic.NotMagic;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class Magic implements Listener {
    Items items = new Items();
    boolean debug = true;

    private final Plugin plugin;
    //private NotMagic notMagic;
     RevisedEvents eventClass;
    public final SpellIndex spellIndex;
    private final List<Particle.DustOptions> colors = new ArrayList<>();
    List<SpellTrail> spellTrails = new ArrayList<>();
    Map<Player, Sound> soundQueue = new HashMap<>();
    public Magic(NotMagic plugin, RevisedEvents eventClass){
        this.plugin = plugin;
        //notMagic = plugin;
        this.eventClass = eventClass;
        spellIndex = new SpellIndex(plugin, this);
        // colors of the 9 cast points
        float size = 0.75F;
        colors.add(new Particle.DustOptions(Color.fromRGB(196, 78, 212), size));
        colors.add(new Particle.DustOptions(Color.fromRGB(114, 71, 214), size));
        colors.add(new Particle.DustOptions(Color.fromRGB(67, 70, 224), size));
        colors.add(new Particle.DustOptions(Color.fromRGB(71, 195, 230), size));
        colors.add(new Particle.DustOptions(Color.fromRGB(64, 230, 122), size));
        colors.add(new Particle.DustOptions(Color.fromRGB(200, 240, 43), size));
        colors.add(new Particle.DustOptions(Color.fromRGB(240, 178, 43), size));
        colors.add(new Particle.DustOptions(Color.fromRGB(224, 119, 49), size));
        colors.add(new Particle.DustOptions(Color.fromRGB(224, 49, 49), size));

        new BukkitRunnable(){
            final List<SpellTrail> toDo = spellTrails;
            final Map<Player, Sound> toPlay = soundQueue;
            @Override
            public void run() {
                for (SpellTrail trail : toDo){
                    if (trail.getPlayer().isOnline()){
                        if (Objects.equals(trail.getLocation().getWorld(), trail.getPlayer().getWorld())){
                            if (trail.getLocation().distance(trail.getPlayer().getLocation()) < 10){
                                trail.spawnParticle();
                            }
                        }
                    }
                }
                spellTrails.removeIf(toDo::contains);
                for (Map.Entry<Player, Sound> queue : toPlay.entrySet()){
                    if (queue.getKey().isOnline()){
                        queue.getKey().playSound(queue.getKey(), queue.getValue(), 1, 1);
                    }
                    soundQueue.remove(queue.getKey(), queue.getValue());
                }

            }
        }.runTaskTimer(plugin, 202, 5L);
        // spawning particles for wand casting & checking time since last cast
        new BukkitRunnable(){
            @Override
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()){
                    PlayerData data = findPlayer(p.getUniqueId());
                    final List<Location> castVertexes = data.getCastVertexes();
                    if (castVertexes != null) {
                        if (castVertexes.size() > 0) {

                                    for (int i = 0; i < castVertexes.size(); i++) {
                                        if (i == 0) {
                                            //p.spawnParticle(Particle.REDSTONE, castVertexes.get(i), 1, colors.get(i));
                                            spellTrails.add(new SpellTrail(p, castVertexes.get(i), colors.get(i)));
                                        } else {
                                            //p.spawnParticle(Particle.REDSTONE, castVertexes.get(i), 1, colors.get(i - 1));
                                            spellTrails.add(new SpellTrail(p, castVertexes.get(i), colors.get(i-1)));
                                            // getting location between 2 of the points
                                            Location beginning = castVertexes.get(i-1);
                                            Location end = castVertexes.get(i);
                                            double minDistance = 0.2;
                                            double distance = end.distance(beginning);
                                            if (distance > minDistance) {
                                                double newPoints = distance / minDistance;
                                                double xVal = end.getX();
                                                double yVal = end.getY();
                                                double zVal = end.getZ();
                                                double xChange = (beginning.getX() - end.getX()) / newPoints;
                                                double yChange = (beginning.getY() - end.getY()) / newPoints;
                                                double zChange = (beginning.getZ() - end.getZ()) / newPoints;
                                                for (double d = 0; d < distance - minDistance; d+= minDistance ){
                                                    xVal += xChange;
                                                    yVal += yChange;
                                                    zVal += zChange;
                                                    Location middle = new Location(end.getWorld(), xVal, yVal, zVal);
                                                    spellTrails.add(new SpellTrail(p, middle, colors.get(i-1)));
                                                    //p.spawnParticle(Particle.REDSTONE, middle, 1, colors.get(i - 1));
                                                }

                                            }
                                        }
                                    }
                            if (data.getTimeSinceLastCast() != 0)
                                if (data.getTimeSinceLastCast() + 30000 < System.currentTimeMillis()) {
                                    // been 30 seconds since last cast point
                                    breakSpell(p, data.getCastPoints());
                                    data.resetCast();
                                }
                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(plugin,200,5);
        new BukkitRunnable(){
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (items.isWand(player.getInventory().getItemInMainHand()))  {
                        PlayerData data = findPlayer(player.getUniqueId());
                        data.setMp((int) (data.getMp() + data.getMpRegen() * 2));
                        if (data.getMp() > data.getMpMax())
                            data.setMp(data.getMpMax());

                        int num = data.getMp();
                        int max = data.getMpMax();
                        int barLength = 10;
                        int barsUsed = 0;
                        // somewhere between 0 and 1
                        double cooldownPercent = data.getMainCooldownTicks() / spellIndex.querySpell(data.getMainSpell()).getCooldown();

                        StringBuilder str = new StringBuilder();
                        if (data.onCooldown(data.getMainSpell())) {
                            str.append(net.md_5.bungee.api.ChatColor.of(new java.awt.Color(21, 130, 158))).append("▏");
                        } else {
                            str.append(net.md_5.bungee.api.ChatColor.of(new java.awt.Color(158, 228, 247))).append("▏");
                        }
                        if (num > 0) {
                            for (int i = 0; i < num / (max / barLength); i++) {
                                str.append(net.md_5.bungee.api.ChatColor.of(new java.awt.Color(85, 181, 171)));
                                if (barsUsed == 4 || barsUsed == 5){
                                    if (cooldownPercent > 0)
                                        str.append(ChatColor.UNDERLINE);
                                }
                                if (barsUsed == 3 || barsUsed == 6){
                                    if (cooldownPercent > 0.2)
                                        str.append(ChatColor.UNDERLINE);
                                }
                                if (barsUsed == 2 || barsUsed == 7){
                                    if (cooldownPercent > 0.4)
                                        str.append(ChatColor.UNDERLINE);
                                }
                                if (barsUsed == 1 || barsUsed == 8){
                                    if (cooldownPercent > 0.6)
                                        str.append(ChatColor.UNDERLINE);
                                }
                                if (barsUsed == 0 || barsUsed == 9){
                                    if (cooldownPercent > 0.8)
                                        str.append(ChatColor.UNDERLINE);
                                }
                                str.append("█");
                                barsUsed++;
                            }

                            int num2 = num % (max / barLength);
                            if (num2 > ((double) max / barLength) * 0.75){
                                str.append(net.md_5.bungee.api.ChatColor.of(new java.awt.Color(85, 181, 171)));
                                if (barsUsed == 4 || barsUsed == 5){
                                    if (cooldownPercent > 0)
                                        str.append(ChatColor.UNDERLINE);
                                }
                                if (barsUsed == 3 || barsUsed == 6){
                                    if (cooldownPercent > 0.2)
                                        str.append(ChatColor.UNDERLINE);
                                }
                                if (barsUsed == 2 || barsUsed == 7){
                                    if (cooldownPercent > 0.4)
                                        str.append(ChatColor.UNDERLINE);
                                }
                                if (barsUsed == 1 || barsUsed == 8){
                                    if (cooldownPercent > 0.6)
                                        str.append(ChatColor.UNDERLINE);
                                }
                                if (barsUsed == 0 || barsUsed == 9){
                                    if (cooldownPercent > 0.8)
                                        str.append(ChatColor.UNDERLINE);
                                }
                                str.append("▓");
                                barsUsed++;
                            } else if (num2 > ((double) max / barLength) * 0.50){
                                str.append(net.md_5.bungee.api.ChatColor.of(new java.awt.Color(85, 181, 171)));
                                if (barsUsed == 4 || barsUsed == 5){
                                    if (cooldownPercent > 0)
                                        str.append(ChatColor.UNDERLINE);
                                }
                                if (barsUsed == 3 || barsUsed == 6){
                                    if (cooldownPercent > 0.2)
                                        str.append(ChatColor.UNDERLINE);
                                }
                                if (barsUsed == 2 || barsUsed == 7){
                                    if (cooldownPercent > 0.4)
                                        str.append(ChatColor.UNDERLINE);
                                }
                                if (barsUsed == 1 || barsUsed == 8){
                                    if (cooldownPercent > 0.6)
                                        str.append(ChatColor.UNDERLINE);
                                }
                                if (barsUsed == 0 || barsUsed == 9){
                                    if (cooldownPercent > 0.8)
                                        str.append(ChatColor.UNDERLINE);
                                }
                                str.append("▒");
                                barsUsed++;
                            } else if (num2 > ((double) max / barLength) * 0.25){
                                str.append(net.md_5.bungee.api.ChatColor.of(new java.awt.Color(85, 181, 171)));
                                if (barsUsed == 4 || barsUsed == 5){
                                    if (cooldownPercent > 0)
                                        str.append(ChatColor.UNDERLINE);
                                }
                                if (barsUsed == 3 || barsUsed == 6){
                                    if (cooldownPercent > 0.2)
                                        str.append(ChatColor.UNDERLINE);
                                }
                                if (barsUsed == 2 || barsUsed == 7){
                                    if (cooldownPercent > 0.4)
                                        str.append(ChatColor.UNDERLINE);
                                }
                                if (barsUsed == 1 || barsUsed == 8){
                                    if (cooldownPercent > 0.6)
                                        str.append(ChatColor.UNDERLINE);
                                }
                                if (barsUsed == 0 || barsUsed == 9){
                                    if (cooldownPercent > 0.8)
                                        str.append(ChatColor.UNDERLINE);
                                }
                                str.append("░");
                                barsUsed++;
                            }


                        }
                        for (int i = barsUsed; i < barLength; i++){
                            str.append(net.md_5.bungee.api.ChatColor.of(new java.awt.Color(171, 193, 199)));
                            if (barsUsed == 4 || barsUsed == 5){
                                if (cooldownPercent > 0)
                                    str.append(ChatColor.UNDERLINE);
                            }
                            if (barsUsed == 3 || barsUsed == 6){
                                if (cooldownPercent > 0.2)
                                    str.append(ChatColor.UNDERLINE);
                            }
                            if (barsUsed == 2 || barsUsed == 7){
                                if (cooldownPercent > 0.4)
                                    str.append(ChatColor.UNDERLINE);
                            }
                            if (barsUsed == 1 || barsUsed == 8){
                                if (cooldownPercent > 0.6)
                                    str.append(ChatColor.UNDERLINE);
                            }
                            if (barsUsed == 0 || barsUsed == 9){
                                if (cooldownPercent > 0.8)
                                    str.append(ChatColor.UNDERLINE);
                            }
                            str.append("░");
                            barsUsed++;
                        }

                        if (data.onCooldown(data.getMainSpell())) {
                            str.append(net.md_5.bungee.api.ChatColor.of(new java.awt.Color(21, 130, 158))).append("▏");
                        } else {
                            str.append(net.md_5.bungee.api.ChatColor.of(new java.awt.Color(158, 228, 247))).append("▏");
                        }
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(String.valueOf(str)));
                    }
                }
            }
        }.runTaskTimerAsynchronously(plugin,200,10);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        if (event.getItem() == null)
            return;
        Player player = event.getPlayer();
        if (items.isWand(event.getItem())){
            PlayerData data = findPlayer(event.getPlayer().getUniqueId());
            event.setCancelled(true);
            if (System.currentTimeMillis() - data.getInteractCooldown() < 50)
                return;
            else
                data.setInteractCooldown(System.currentTimeMillis());
            if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK){
                if (!data.isCalculating()) {
                    if (data.getCastVertexes().size() > 0) {
                        // casting a spell
                        ArrayList<String> castPoints = data.getSpellCasting();
                        if (debug) {
                            for (String str : castPoints) {
                                player.sendMessage(ChatColor.BLUE + str);
                            }
                        }
                        Spell spell = spellIndex.querySpell(data.getSpellCasting());

                        if (spell != null) {
                            // <!> add new Main spells here <!>
                            if (debug) {
                                player.sendMessage(spell.getName());
                            }
                            // main spell switch
                            if (spell.isMainSpell()){
                                data.setMainSpell(spell.getName());
                            } else {
                                spellIndex.performSpell(spell.getName(), player);
                            }
                        } else {
                            breakSpell(player, data.getCastPoints());
                        }

                        data.resetCast();
                    } else {
                        // main spell
                        spellIndex.performSpell(data.getMainSpell(), player);
                    }
                }
            } else if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK){

                if (event.getClickedBlock() != null){
                    if (event.getClickedBlock().getType() == Material.ENCHANTING_TABLE){
                        return;
                    }
                }


                data.setCalculating(true);
                new BukkitRunnable() {
                    final Player p = player;
                    @Override
                    public void run() {
                        // reach distance
                        Location pointY = p.getEyeLocation().add(p.getEyeLocation().getDirection().normalize().multiply(1.5));
                        // same xz of reference but cast point y value
                        Location loweredY = new Location(p.getEyeLocation().getWorld(), p.getEyeLocation().getX(), pointY.getY(), p.getEyeLocation().getZ());
                        // add how far away the point is
                        double pointDistance = 1.0;
                        Location point = loweredY.add(pointY.toVector().subtract(loweredY.toVector()).normalize().multiply(pointDistance));
                        // main cast direction changes
                        List<Location> castVertexes = data.getCastVertexes();
                        // secondary spell cast here
                        if (castVertexes.isEmpty()) {
                            // first point to be cast:
                            data.addCastPoint(point);
                            p.spawnParticle(Particle.REDSTONE, point, 1, colors.get(0));
                            ArrayList<String> previousPoints = data.getSpellCasting();
                            previousPoints.add("Start");
                            data.addCastVertex(point, previousPoints);
                            data.setCastReference(p.getEyeLocation());
                            data.setTimeSinceLastCast(System.currentTimeMillis());
                        } else if (castVertexes.size() < 9) {
                            // other points
                            // check if the cast is close enough to other points
                            Location lastVertex = castVertexes.get(castVertexes.size() - 1);
                            if (Objects.equals(point.getWorld(), lastVertex.getWorld())) {
                                // 10 blocks or fewer away
                                if (point.distance(lastVertex) < 10) {
                                    // make sure they don't wait too long
                                    if (System.currentTimeMillis() - data.getTimeSinceLastCast() <= 1000) {
                                        ArrayList<String> previousPoints = data.getSpellCasting();

                                        // checking if the point is far enough away for another point
                                        // calculated in minecraft meters, so 0.25 meters away from last vertex point
                                        // distance x and z is calculated using yaw degrees which makes it a little easier
                                        // to find the degrees to get 0.25 away its just (distance between reference & point) * atan(0.25)
                                        double pDistanceY = 15 * (Math.PI / 180);
                                        double diagonalMultiplier = 0.5;
                                        // because pDistanceXZ is for 2 axes, we can use the getReferenceAngle() method I created to
                                        // check if the new point is to the left or to the right of the last point
                                        double pDistanceXZ = pDistanceY; // pointDistance * Math.atan(pDistanceY);
                                        Vector vertexVector = lastVertex.toVector().subtract(pointY.toVector()); // player to last vertex
                                        Vector vertexPoint = point.toVector().subtract(pointY.toVector()); // player to current point

                                        double yDifference = lastVertex.getY() - point.getY();
                                        double yAngle = Math.asin(yDifference / 2 / pointDistance) * 2;
                                        //p.sendMessage("Y " + yAngle);
                                        Vector hPoint = new Vector(vertexPoint.getX(), 0 , vertexPoint.getZ()).normalize();
                                        Vector hVertex = new Vector(vertexVector.getX(), 0, vertexVector.getZ()).normalize();
                                        double hAngle = Math.acos(hPoint.dot(hVertex) / (hPoint.length() * hVertex.length())); //getYawAngle(vertexVector, vertexPoint);
                                        //p.sendMessage("H " + hAngle);



                                        // first have to check if it is a diagonal point (so multiply by 0.75 to get a diamond shape hit box for next vertex)
                                        // point is below the last vertex
                                        if (yAngle >= pDistanceY * diagonalMultiplier && getYawAngle(vertexVector, vertexPoint) >= pDistanceXZ * diagonalMultiplier) {
                                            // new point is down and diagonal
                                            if (getRelativeVector(vertexVector, vertexPoint).equals("l")) {
                                                // left down diagonal

                                                if (!previousPoints.get(previousPoints.size() - 1).equals("RightDown")) {
                                                    previousPoints.add("RightDown");
                                                    data.addCastVertex(point, previousPoints);
                                                    p.playSound(p, Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
                                                    //soundQueue.put(p, Sound.ENTITY_ARROW_HIT_PLAYER);
                                                } else {
                                                    data.replaceLastVertex(point);
                                                }
                                                if (debug) {
                                                    p.sendMessage("RightDown");
                                                }
                                            } else {
                                                // right down diagonal
                                                if (!previousPoints.get(previousPoints.size() - 1).equals("LeftDown")) {
                                                    previousPoints.add("LeftDown");
                                                    data.addCastVertex(point, previousPoints);
                                                    p.playSound(p, Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
                                                    //soundQueue.put(p, Sound.ENTITY_ARROW_HIT_PLAYER);
                                                } else {
                                                    data.replaceLastVertex(point);
                                                }
                                                if (debug) {
                                                    p.sendMessage("LeftDown");
                                                }
                                            }
                                            // point is higher than the last vertex
                                        } else if (point.getY() - lastVertex.getY() >= pDistanceY * diagonalMultiplier && getYawAngle(vertexVector, vertexPoint) >= pDistanceXZ * diagonalMultiplier) {
                                            // new point is up and diagonal
                                            if (getRelativeVector(vertexVector, vertexPoint).equals("l")) {
                                                // left up diagonal
                                                if (!previousPoints.get(previousPoints.size() - 1).equals("RightUp")) {
                                                    previousPoints.add("RightUp");
                                                    data.addCastVertex(point, previousPoints);
                                                    p.playSound(p, Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
                                                    //soundQueue.put(p, Sound.ENTITY_ARROW_HIT_PLAYER);
                                                } else {
                                                    data.replaceLastVertex(point);
                                                }
                                                if (debug) {
                                                    p.sendMessage("RightUp");
                                                }
                                            } else {
                                                // right up diagonal
                                                if (!previousPoints.get(previousPoints.size() - 1).equals("LeftUp")) {
                                                    previousPoints.add("LeftUp");
                                                    data.addCastVertex(point, previousPoints);
                                                    p.playSound(p, Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
                                                    //soundQueue.put(p, Sound.ENTITY_ARROW_HIT_PLAYER);
                                                } else {
                                                    data.replaceLastVertex(point);
                                                }
                                                if (debug) {
                                                    p.sendMessage("LeftUp");
                                                }
                                            }
                                            // point is below last vertex
                                        } else if (yAngle >= pDistanceY) {
                                            // down
                                            if (!previousPoints.get(previousPoints.size() - 1).equals("Down")) {
                                                previousPoints.add("Down");
                                                data.addCastVertex(point, previousPoints);
                                                p.playSound(p, Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
                                                //soundQueue.put(p, Sound.ENTITY_ARROW_HIT_PLAYER);
                                            } else {
                                                data.replaceLastVertex(point);
                                            }
                                            if (debug) {
                                                p.sendMessage("Down");
                                            }
                                            // point is above last vertex
                                        } else if (point.getY() - lastVertex.getY() >= pDistanceY) {
                                            // up
                                            if (!previousPoints.get(previousPoints.size() - 1).equals("Up")) {
                                                previousPoints.add("Up");
                                                data.addCastVertex(point, previousPoints);
                                                p.playSound(p, Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
                                                //soundQueue.put(p, Sound.ENTITY_ARROW_HIT_PLAYER);
                                            } else {
                                                data.replaceLastVertex(point);
                                            }
                                            if (debug) {
                                                p.sendMessage("Up");
                                            }
                                            // point is left or right
                                        } else if (getYawAngle(vertexVector, vertexPoint) >= pDistanceXZ) {

                                            if (getRelativeVector(vertexVector, vertexPoint).equals("l")) {
                                                // left
                                                if (!previousPoints.get(previousPoints.size() - 1).equals("Right")) {
                                                    previousPoints.add("Right");
                                                    data.addCastVertex(point, previousPoints);
                                                    p.playSound(p, Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
                                                    //soundQueue.put(p, Sound.ENTITY_ARROW_HIT_PLAYER);
                                                } else {
                                                    data.replaceLastVertex(point);
                                                }
                                                if (debug) {
                                                    p.sendMessage("Right");
                                                }
                                            } else {
                                                // right
                                                if (!previousPoints.get(previousPoints.size() - 1).equals("Left")) {
                                                    previousPoints.add("Left");
                                                    data.addCastVertex(point, previousPoints);
                                                    p.playSound(p, Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
                                                    //soundQueue.put(p, Sound.ENTITY_ARROW_HIT_PLAYER);
                                                } else {
                                                    data.replaceLastVertex(point);
                                                }
                                                if (debug) {
                                                    p.sendMessage("Left");
                                                }
                                            }
                                        }

                                        if (castVertexes.size() <= 9) {
                                            //spellTrails.add(new SpellTrail(p, point, colors.get(castVertexes.size() - 1)));
                                            p.spawnParticle(Particle.REDSTONE, point, 1, colors.get(castVertexes.size() - 1));
                                            data.addCastPoint(point);
                                        } else {
                                            soundQueue.put(p, Sound.ENTITY_PARROT_IMITATE_CREEPER);
                                        }

                                    }
                                    data.setTimeSinceLastCast(System.currentTimeMillis());
                                } else {
                                    // break spell- too far away
                                    breakSpell(p, data.getCastPoints());
                                    data.resetCast();
                                }
                            } else {
                                // break spell - in another world - no breakSpell() cuz they wouldn't be there to see it
                                data.resetCast();
                            }
                        } else {
                            // no more points - sound effect??
                            //p.playSound(point, Sound.ENTITY_PARROT_IMITATE_CREEPER, 1, 1);
                            soundQueue.put(p, Sound.ENTITY_PARROT_IMITATE_CREEPER);
                        }
                        data.setCalculating(false);

                    }
                }.runTaskAsynchronously(plugin);
            }
        } else if (player.getInventory().getItemInMainHand().getType().equals(Material.ENCHANTED_BOOK)){
            Spell spell = spellIndex.querySpell(player.getInventory().getItemInMainHand());
            if (spell != null){
                PlayerData data = findPlayer(event.getPlayer().getUniqueId());
                if (!data.getSpellsUnlocked().contains(spell.getName())) {
                    player.sendMessage("You learned the " + spell.getName() + " spell!");
                    data.learnSpell(spell.getName());
                    removeItem(player, spell.getSpellBook());
                } else {
                    player.sendMessage("You already have this spell unlocked!");
                }
            }
        }
    }
    public void removeItem(Player p, ItemStack item){
        ItemStack[] contents = new ItemStack[p.getInventory().getContents().length];
        int amount = item.getAmount();
        for (int i = 0; i < p.getInventory().getContents().length; i++){
            if (amount > 0) {
                ItemStack nextItem = p.getInventory().getContents()[i];
                if (nextItem.isSimilar(item)) {
                    if (nextItem.getAmount() > amount) {
                        item.setAmount(nextItem.getAmount() - amount);
                        contents[i] = item;
                        amount = 0;
                    } else {
                        contents[i] = null;
                        amount -= nextItem.getAmount();
                    }
                } else {
                    contents[i] = nextItem;
                }
            }
        }
        p.getInventory().setContents(contents);
        p.updateInventory();
    }

    public void breakSpell(Player p, List<Location> castPoints){
        p.playSound(castPoints.get(0), Sound.BLOCK_GLASS_BREAK,1,1);
        //soundQueue.put(p, Sound.BLOCK_GLASS_BREAK);
        for (Location location : castPoints){
            ItemStack itemBreakData = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS);
            BlockData data = itemBreakData.getType().createBlockData();
            p.spawnParticle(Particle.BLOCK_DUST, location, 1, data);
        }
    }

    public PlayerData findPlayer(UUID uuid){
        for (PlayerData data : eventClass.getPlayerData()){
            if (data.getUuid().equals(uuid)){
                return data;
            }
        }
        return null;
    }

    // next 3 methods are a bunch of math that get directions and distances from points
    // in order to get relative direction and distances we need to turn the points into vectors beforehand
    // from player eye location to the point
    public String getRelativeVector(Vector v1, Vector v2){
        double a1 = getLookAtYaw(v2);
        double a2 = a1 + 360;
        double a3 = a1 - 360;
        double v = getLookAtYaw(v1);
        double d1 = v - a1;
        double d2 = v - a2;
        double d3 = v - a3;
        if (Math.abs(d1) < Math.abs(d2) && Math.abs(d1) < Math.abs(d3)){
            if (d1 > 0){
                return "r";
            } else {
                return "l";
            }
        } else if (Math.abs(d2) < Math.abs(d1) && Math.abs(d2) < Math.abs(d3)){
            if (d2 > 0){
                return "r";
            } else {
                return "l";
            }
        } else if (Math.abs(d3) < Math.abs(d1) && Math.abs(d3) < Math.abs(d2)){
            if (d3 > 0){
                return "r";
            } else {
                return "l";
            }
        } else {
            return "i";
        }
    }
    public static float getLookAtYaw(Vector motion) {
        double dx = motion.getX();
        double dz = motion.getZ();
        double yaw = 0;
        // Set yaw
        if (dx != 0) {
            // Set yaw start value based on dx
            if (dx < 0) {
                yaw = 1.5 * Math.PI;
            } else {
                yaw = 0.5 * Math.PI;
            }
            yaw -= Math.atan(dz / dx);
        } else if (dz < 0) {
            yaw = Math.PI;
        }
        return (float) (yaw * 180 / Math.PI);
    }
    public double getYawAngle(Vector v1, Vector v2){
        double x = v1.getX();
        double z = v1.getZ();
        double x2 = v2.getX();
        double z2 = v2.getZ();
        return Math.acos((x*x2 + z*z2) / (Math.sqrt(Math.pow(x, 2)+Math.pow(z, 2)) * Math.sqrt(Math.pow(x2, 2)+Math.pow(z2, 2))));
    }


}
