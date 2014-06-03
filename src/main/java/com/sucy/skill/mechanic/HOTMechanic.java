package com.sucy.skill.mechanic;

import com.sucy.skill.api.PlayerSkills;
import com.sucy.skill.api.dynamic.DynamicSkill;
import com.sucy.skill.api.dynamic.IMechanic;
import com.sucy.skill.api.dynamic.Target;
import com.sucy.skill.api.event.PlayerSkillHealEvent;
import com.sucy.skill.api.util.effects.DOT;
import com.sucy.skill.api.util.effects.DOTHelper;
import com.sucy.skill.api.util.effects.DOTSet;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Mechanic for dealing damage to each target
 */
public class HOTMechanic implements IMechanic {

    private static final String
            HEALTH = "Health Per Tick",
            DURATION = "Heal Duration",
            FREQUENCY = "Heal Frequency";

    /**
     * Deals damage to all targets
     *
     * @param player  player using the skill
     * @param data    data of the player using the skill
     * @param skill   skill being used
     * @param target  target type of the skill
     * @param targets targets for the effects
     * @return        true if there were targets, false otherwise
     */
    @Override
    public boolean resolve(Player player, PlayerSkills data, DynamicSkill skill, Target target, List<LivingEntity> targets) {

        // Requires a target
        if (targets.size() == 0) {
            return false;
        }

        DOTHelper helper = data.getAPI().getDOTHelper();
        int level = data.getSkillLevel(skill.getName());
        double health = skill.getAttribute(HEALTH, target, level);
        int duration = (int)(skill.getAttribute(DURATION, target, level) * 20);
        int frequency = (int)(skill.getAttribute(FREQUENCY, target, level) * 20);

        // Apply a HOT to all targets
        for (LivingEntity entity : targets) {
            double amount = health;
            if (entity instanceof Player) {
                int ticks = (duration / frequency);
                PlayerSkillHealEvent event = new PlayerSkillHealEvent((Player)entity, player, skill.getName(), ticks * amount);
                skill.getAPI().getServer().getPluginManager().callEvent(event);
                amount = event.getAmount() / ticks;
            }
            DOTSet set = helper.getDOTSet(entity);
            set.addEffect(skill.getName(), new DOT(duration, -amount, frequency, false));
        }

        return true;
    }

    /**
     * Sets default attributes for the skill
     *
     * @param skill  skill to apply to
     * @param prefix prefix to add to the name
     */
    @Override
    public void applyDefaults(DynamicSkill skill, String prefix) {
        skill.checkDefault(prefix + HEALTH, 1, 0);
        skill.checkDefault(prefix + DURATION, 5, 2);
        skill.checkDefault(prefix + FREQUENCY, 1, 0);
    }

    /**
     * @return names of all attributes used by this mechanic
     */
    @Override
    public String[] getAttributeNames() {
        return new String[] {HEALTH, DURATION, FREQUENCY };
    }
}
