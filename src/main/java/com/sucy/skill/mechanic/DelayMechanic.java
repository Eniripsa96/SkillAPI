package com.sucy.skill.mechanic;

import com.sucy.skill.api.PlayerSkills;
import com.sucy.skill.api.dynamic.DynamicSkill;
import com.sucy.skill.api.dynamic.EmbedData;
import com.sucy.skill.api.dynamic.IMechanic;
import com.sucy.skill.api.dynamic.Target;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Mechanic for applying embedded effects with a chance
 */
public class DelayMechanic implements IMechanic {

    private static final String
            DELAY = "Delay";

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
    public boolean resolve(Player player, PlayerSkills data, DynamicSkill skill, Target target, final List<LivingEntity> targets) {

        // Must have a target
        if (targets.isEmpty()) return false;

        // Get attributes
        final EmbedData embedData = new EmbedData(player, data, skill);
        final int level = data.getSkillLevel(skill.getName());
        final int delay = (int)(skill.getAttribute(DELAY, target, level) * 20);

        // Run the effect later
        data.getAPI().getServer().getScheduler().runTaskLater(data.getAPI(), new Runnable() {
            @Override
            public void run() {
                // Apply the embedded effects
                embedData.getSkill().beginUsage();
                for (LivingEntity t : targets) {
                    embedData.resolveNonTarget(t.getLocation());
                    embedData.resolveTarget(t);
                }
                embedData.getSkill().stopUsage();
            }
        }, delay);

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
        skill.checkDefault(prefix + DELAY, 3, 0);
    }

    /**
     * @return names of the attributes used by the mechanic
     */
    @Override
    public String[] getAttributeNames() {
        return new String[] { DELAY };
    }
}
