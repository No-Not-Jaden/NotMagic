package me.jadenp.notmagic;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class XPChangeEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private int level;
    private boolean cancelled;
    private Player p;

    public XPChangeEvent(int mana, Player player){
        this.level = mana;

        p = player;
    }

    public int getXP(){
        return level;
    }

    public Player getPlayer(){
        return p;
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }
}
