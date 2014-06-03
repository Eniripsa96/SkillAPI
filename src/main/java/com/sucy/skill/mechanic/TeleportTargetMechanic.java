package com.sucy.skill.mechanic;

import com.sucy.skill.api.PlayerSkills;
import com.sucy.skill.api.dynamic.DynamicSkill;
import com.sucy.skill.api.dynamic.IMechanic;
import com.sucy.skill.api.dynamic.Target;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Random;

/**
 * Teleports to a target or a random one if an AOE effect
 */
public class TeleportTargetMechanic implements IMechanic {

    private static final Random random = new Random();

    /**
     * Teleport to a target
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

        // Cannot teleport to self
        if (targets.contains(player)) targets.remove(player);

        // Must have a target
        if (targets.size() == 0) return false;

        // Teleports to a target
        LivingEntity entity = targets.get(random.nextInt(targets.size()));
        player.teleport(entity);
        return true;
    }

    /**
     * Sets default attributes for the skill
     *
     * @param skill  skill to apply to
     * @param prefix prefix to add to the name
     */
    @Override
    public void applyDefaults(DynamicSkill skill, String prefix) { }

    /**
     * @return names of all attributes used by this mechanic
     */
    @Override
    public String[] getAttributeNames() {
        return new String[0];
    }
}
