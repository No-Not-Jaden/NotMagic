package me.jadenp.notmagic.RevisedClasses;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class SpellTrail {
    private final Player player;
    private final Location location;
    private final Particle.DustOptions dustOptions;

    public SpellTrail(Player player, Location location, Particle.DustOptions dustOptions){

        this.player = player;
        this.location = location;
        this.dustOptions = dustOptions;
    }

    public Player getPlayer() {
        return player;
    }

    public Location getLocation() {
        return location;
    }

    public Particle.DustOptions getDustOptions() {
        return dustOptions;
    }

    public void spawnParticle(){
        player.spawnParticle(Particle.REDSTONE, location, 1, dustOptions);
    }
}
