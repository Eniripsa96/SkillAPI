package com.sucy.skill.hook;

import com.google.common.collect.ImmutableList;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sucy.skill.SkillAPI;
import org.bukkit.Location;
import org.bukkit.World;

import java.lang.reflect.Method;
import java.util.List;

/**
 * SkillAPI © 2018
 * com.sucy.skill.hook.WorldGuardHook
 */
public class WorldGuardHook {

    private static Method regionMethod;

    /**
     * Fetches the list of region IDs applicable to a given location
     *
     * @param loc location to get region ids for
     * @return region IDs for the location
     */
    public static List<String> getRegionIds(final Location loc) {
        try {
            return WorldGuard.getInstance()
                    .getPlatform()
                    .getRegionContainer()
                    .get(BukkitAdapter.adapt(loc.getWorld()))
                    .getApplicableRegionsIDs(asVector(loc));
        } catch (NoClassDefFoundError ex) {
            try {
                final WorldGuardPlugin plugin = SkillAPI.getPlugin(WorldGuardPlugin.class);
                return ((RegionManager) getRegionMethod().invoke(plugin, loc.getWorld()))
                        .getApplicableRegionsIDs(asVector(loc));
            } catch (final Exception e) {
                // Cannot handle world guard
                return ImmutableList.of();
            }
        }
    }

    private static Vector asVector(final Location location) {
        return new Vector(location.getX(), location.getY(), location.getZ());
    }

    private static Method getRegionMethod() throws Exception {
        if (regionMethod == null) {
            regionMethod = WorldGuardPlugin.class.getDeclaredMethod("getRegionManager", World.class);
        }
        return regionMethod;
    }
}
