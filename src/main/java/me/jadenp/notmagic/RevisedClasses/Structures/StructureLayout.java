package me.jadenp.notmagic.RevisedClasses.Structures;

import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

public class StructureLayout {
    public final static Map<Integer[], Material> magicStorage = new HashMap<Integer[], Material>(){{
        put(new Integer[]{1,0,1}, Material.WAXED_COPPER_BLOCK);
        put(new Integer[]{-1,0,1}, Material.WAXED_COPPER_BLOCK);
        put(new Integer[]{1,0,-1}, Material.WAXED_COPPER_BLOCK);
        put(new Integer[]{-1,0,-1}, Material.WAXED_COPPER_BLOCK);
        put(new Integer[]{1,1,1}, Material.WAXED_COPPER_BLOCK);
        put(new Integer[]{-1,1,1}, Material.WAXED_COPPER_BLOCK);
        put(new Integer[]{1,1,-1}, Material.WAXED_COPPER_BLOCK);
        put(new Integer[]{-1,1,-1}, Material.WAXED_COPPER_BLOCK);
        put(new Integer[]{1,2,1}, Material.WAXED_COPPER_BLOCK);
        put(new Integer[]{-1,2,1}, Material.WAXED_COPPER_BLOCK);
        put(new Integer[]{1,2,-1}, Material.WAXED_COPPER_BLOCK);
        put(new Integer[]{-1,2,-1}, Material.WAXED_COPPER_BLOCK);
        put(new Integer[]{1,3,1}, Material.WAXED_COPPER_BLOCK);
        put(new Integer[]{-1,3,1}, Material.WAXED_COPPER_BLOCK);
        put(new Integer[]{1,3,-1}, Material.WAXED_COPPER_BLOCK);
        put(new Integer[]{-1,3,-1}, Material.WAXED_COPPER_BLOCK);
        put(new Integer[]{1,4,1}, Material.WAXED_CUT_COPPER_SLAB);
        put(new Integer[]{-1,4,1}, Material.WAXED_CUT_COPPER_SLAB);
        put(new Integer[]{1,4,-1}, Material.WAXED_CUT_COPPER_SLAB);
        put(new Integer[]{-1,4,-1}, Material.WAXED_CUT_COPPER_SLAB);
        put(new Integer[]{0,4,-1}, Material.WAXED_CUT_COPPER_SLAB);
        put(new Integer[]{0,4,1}, Material.WAXED_CUT_COPPER_SLAB);
        put(new Integer[]{-1,4,0}, Material.WAXED_CUT_COPPER_SLAB);
        put(new Integer[]{1,4,0}, Material.WAXED_CUT_COPPER_SLAB);
        put(new Integer[]{1,1,0}, Material.TINTED_GLASS);
        put(new Integer[]{-1,1,0}, Material.TINTED_GLASS);
        put(new Integer[]{0,1,1}, Material.TINTED_GLASS);
        put(new Integer[]{0,1,-1}, Material.TINTED_GLASS);
        put(new Integer[]{1,2,0}, Material.TINTED_GLASS);
        put(new Integer[]{-1,2,0}, Material.TINTED_GLASS);
        put(new Integer[]{0,2,1}, Material.TINTED_GLASS);
        put(new Integer[]{0,2,-1}, Material.TINTED_GLASS);
        put(new Integer[]{1,3,0}, Material.TINTED_GLASS);
        put(new Integer[]{-1,3,0}, Material.TINTED_GLASS);
        put(new Integer[]{0,3,1}, Material.TINTED_GLASS);
        put(new Integer[]{0,3,-1}, Material.TINTED_GLASS);
        put(new Integer[]{0,1,0}, Material.GRAY_CARPET);
    }};
}
