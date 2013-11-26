package com.sucy.skill.mechanic;

import com.sucy.skill.api.PlayerSkills;
import com.sucy.skill.api.dynamic.*;
import com.sucy.skill.api.event.AttackType;
import com.sucy.skill.api.event.PlayerOnHitEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Mechanic for applying embedded effects with a chance
 */
public class ChanceMechanic implements IMechanic {

    private static final String
            CHANCE = "Chance";

    private Random random = new Random();

    /**
     * Grants a temporary damage bonus to the targets
     *
     * @param player  player using the skill
     * @param data    data of the player using the skill
     * @param skill   skill being used
     * @param target  target type of the skill
     * @param targets targets for the effects
     * @return        true if was able to use
     */
    @Override
    public boolean resolve(Player player, PlayerSkills data, DynamicSkill skill, Target target, List<LivingEntity> targets) {

        if (targets.isEmpty()) return false;

        // Get attributes
        int level = data.getSkillLevel(skill.getName());
        double chance = skill.getAttribute(CHANCE, target, level);

        // Roll a chance
        if (random.nextDouble() < chance / 100) {

            // Apply the embedded effects
            EmbedData embedData = new EmbedData(player, data, skill);
            skill.startEmbeddedEffects();
            for (LivingEntity t : targets) {
                embedData.resolveNonTarget(t.getLocation());
                embedData.resolveTarget(t);
            }
            skill.stopEmbeddedEffects();
        }

        return true;
    }

    /**
     * Applies default values for the mechanic attributes
     *
     * @param skill  skill to apply to
     * @param prefix prefix to add to the attribute
     */
    @Override
    public void applyDefaults(DynamicSkill skill, String prefix) {
        skill.checkDefault(prefix + CHANCE, 20, 5);
    }

    /**
     * @return names of the attributes used by the mechanic
     */
    @Override
    public String[] getAttributeNames() {
        return new String[] { CHANCE };
    }
}
