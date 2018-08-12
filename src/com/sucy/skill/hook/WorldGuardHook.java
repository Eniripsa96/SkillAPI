package com.sucy.skill.hook;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import org.bukkit.Location;

import java.util.List;

/**
 * SkillAPI © 2018
 * com.sucy.skill.hook.WorldGuardHook
 */
public class WorldGuardHook {

    /**
     * Fetches the list of region IDs applicable to a given location
     *
     * @param loc location to get region ids for
     * @return region IDs for the location
     */
    public static List<String> getRegionIds(final Location loc) {
        return WorldGuard.getInstance()
                .getPlatform()
                .getRegionContainer()
                .get(BukkitAdapter.adapt(loc.getWorld()))
                .getApplicableRegionsIDs(BukkitAdapter.asVector(loc));
    }
}
