package com.sucy.skill.api.dynamic;

import com.sucy.skill.api.util.Protection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Target groups for dynamic commands
 */
public enum Group {

    /**
     * Allies of the caster including themselves
     */
    ALLY,

    /**
     * Enemies of the caster
     */
    ENEMY,

    /**
     * All besides the caster
     */
    OTHERS,

    /**
     * All targets
     */
    ALL,
    ;

    /**
     * Filters the targets according to the group
     *
     * @return filtered target list
     */
    public List<LivingEntity> filterTargets(Player caster, List<LivingEntity> targets) {

        // Check each target to see if it fits
        for (int i = 0; i < targets.size(); i++) {
            LivingEntity target = targets.get(i);

            // See if its in the group
            boolean inGroup;
            if (this == ALLY) inGroup = (caster == target || Protection.isAlly(caster, target));
            else if (this == ENEMY) inGroup = !Protection.isAlly(caster, target);
            else if (this == OTHERS) inGroup = (caster != target);
            else if (this == ALL) inGroup = true;
            else inGroup = false;

            // Remove the entry if its in the group
            if (!inGroup) {
                targets.remove(i);
                i--;
            }
        }

        return targets;
    }
}
