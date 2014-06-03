package com.sucy.skill.api.dynamic;

import com.sucy.skill.api.PlayerSkills;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * A mechanic for customized skills
 */
public interface IMechanic {

    /**
     * Resolves the mechanic
     *
     * @param player  player using the skill
     * @param data    data of the player using the skill
     * @param skill   skill being used
     * @param target  target type of the skill
     * @param targets targets for the effects
     * @return        true if able to be cast, false otherwise
     */
    public boolean resolve(Player player, PlayerSkills data, DynamicSkill skill, Target target, List<LivingEntity> targets);

    /**
     * Applies default attributes to the skill
     *
     * @param skill  skill to apply to
     * @param prefix prefix to add to the attribute
     */
    public void applyDefaults(DynamicSkill skill, String prefix);

    /**
     * @return names of attributes used by the mechanic
     */
    public String[] getAttributeNames();
}
