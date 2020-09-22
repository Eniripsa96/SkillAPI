package com.sucy.skill.listener;

import com.rit.sucy.config.CommentedConfig;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.enums.ExpSource;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerData;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * SkillAPI © 2017
 * com.sucy.skill.listener.ExperienceListener
 */
public class ExperienceListener extends SkillAPIListener {

    private static final String CONFIG_KEY = "unnatural";

    boolean track;
    HashSet<String> unnatural = new HashSet<String>();

    public ExperienceListener() {
        track = SkillAPI.getSettings().trackBreaks();
        if (track) {
            CommentedConfig data = SkillAPI.getConfig("data/placed");
            unnatural = new HashSet<String>(data.getConfig().getList(CONFIG_KEY));
        }
    }

    @Override
    public void cleanup() {
        if (track) {
            CommentedConfig config = SkillAPI.getConfig("data/placed");
            config.getConfig().set(CONFIG_KEY, new ArrayList<String>(unnatural));
            config.save();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        if (track && unnatural.contains(format(event.getBlock()))) {
            return;
        }

        PlayerData playerData = SkillAPI.getPlayerData(event.getPlayer());
        for (PlayerClass playerClass : playerData.getClasses()) {
            double yield = SkillAPI.getSettings().getBreakYield(playerClass, event.getBlock().getType());
            if (yield > 0) {
                playerClass.giveExp(yield, ExpSource.BLOCK_BREAK);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        if (track) {
            unnatural.add(format(event.getBlock()));
        }

        PlayerData playerData = SkillAPI.getPlayerData(event.getPlayer());
        for (PlayerClass playerClass : playerData.getClasses()) {
            double yield = SkillAPI.getSettings().getPlaceYield(playerClass, event.getBlock().getType());
            if (yield > 0) {
                playerClass.giveExp(yield, ExpSource.BLOCK_PLACE);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCraft(CraftItemEvent event) {
        PlayerData playerData = SkillAPI.getPlayerData((Player) event.getWhoClicked());
        for (PlayerClass playerClass : playerData.getClasses()) {
            double yield = SkillAPI.getSettings().getCraftYield(playerClass, event.getRecipe().getResult().getType());
            if (yield > 0) {
                playerClass.giveExp(yield, ExpSource.CRAFT);
            }
        }
    }

    private String format(Block block) {
        Location loc = block.getLocation();
        return loc.getWorld().getName() + "|" + loc.getBlockX() + "|" + loc.getBlockY() + "|" + loc.getBlockZ();
    }
}
