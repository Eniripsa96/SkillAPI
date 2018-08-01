package com.sucy.skill.hook;

import com.sk89q.worldguard.bukkit.BukkitUtil;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sucy.skill.SkillAPI;
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
        final WorldGuardPlugin plugin = SkillAPI.getPlugin(WorldGuardPlugin.class);
        return plugin.getRegionManager(loc.getWorld()).getApplicableRegionsIDs(BukkitUtil.toVector(loc));
    }
}
