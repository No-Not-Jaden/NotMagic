package me.jadenp.notmagic.Alchemy;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class AlchemyBrewEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private String type;
    private boolean cancelled;
    private Player p;

    public AlchemyBrewEvent(String StationType, Player player){
        type = StationType;
        p = player;
    }

    public String getType(){
        return type;
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
