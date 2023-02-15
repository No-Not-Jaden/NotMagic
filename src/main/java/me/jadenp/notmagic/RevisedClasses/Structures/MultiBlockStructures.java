package me.jadenp.notmagic.RevisedClasses.Structures;

import org.bukkit.Location;

import javax.annotation.Nullable;
import java.util.List;

public class MultiBlockStructures {
    private final String name;
    private final List<Location> blocks;
    private final Location activationBlock;

    public MultiBlockStructures(String name, List<Location> blocks){

        this.name = name;
        this.blocks = blocks;
        this.activationBlock = blocks.get(0);

    }

    public String getName() {
        return name;
    }

    public List<Location> getBlocks() {
        return blocks;
    }

    public Location getActivationBlock() {
        return activationBlock;
    }

}
