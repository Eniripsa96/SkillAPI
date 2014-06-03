package com.sucy.skill.example.wizard.passive;

import com.sucy.skill.api.event.PlayerCastSkillEvent;
import com.sucy.skill.api.skill.ClassSkill;
import com.sucy.skill.api.skill.PassiveSkill;
import com.sucy.skill.api.skill.SkillAttribute;
import com.sucy.skill.api.skill.SkillType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.UUID;

/**
 * Passive that causes arrows to travel faster
 */
public class QuickCasting extends ClassSkill implements PassiveSkill, Listener {

    public static final String NAME = "Quick Casting";
    private static final String
            CHANCE = "Chance (%)",
            SECONDS = "Seconds";

    private HashMap<UUID, Integer> active = new HashMap<UUID, Integer>();

    public QuickCasting() {
        super(NAME, SkillType.PASSIVE, Material.WATCH, 5);

        description.add("Has a chance on spell");
        description.add("cast to lower your");
        description.add("cooldowns a little.");

        setAttribute(SkillAttribute.LEVEL, 1, 1);
        setAttribute(SkillAttribute.COST, 1, 0);

        setAttribute(CHANCE, 10, 5);
        setAttribute(SECONDS, 2, 0);
    }

    @Override
    public void onUpgrade(Player player, int level) {
        onInitialize(player, level);
    }

    @Override
    public void onInitialize(Player player, int level) {
        active.put(player.getUniqueId(), level);
    }

    @Override
    public void stopEffects(Player player, int level) {
        active.remove(player.getUniqueId());
    }

    @EventHandler
    public void onSpellCast(PlayerCastSkillEvent event) {
        Player p = event.getPlayerData().getPlayer();
        UUID id = p.getUniqueId();
        if (active.containsKey(id)) {
            int level = active.get(id);
            if (Math.random() * 100 < getAttribute(CHANCE, level)) {
                int seconds = (int)getAttribute(SECONDS, level);
                for (String skill : event.getPlayerData().getSkills().keySet()) {
                    api.getSkill(skill).subtractCooldown(event.getPlayerData(), seconds);
                }
                p.sendMessage(ChatColor.GOLD + NAME + ChatColor.DARK_GREEN + " worked! Cooldowns have been lowered.");
            }
        }
    }
}
