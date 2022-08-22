package me.jadenp.notmagic;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ChangeStatsEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private int level;
    private boolean cancelled;
    private Player p;

    public ChangeStatsEvent(String statType, int newLevel, Player player){
        if (statType.equals("level")){
            this.level = newLevel;
        }

        p = player;
    }

    public int getLevel(){
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
