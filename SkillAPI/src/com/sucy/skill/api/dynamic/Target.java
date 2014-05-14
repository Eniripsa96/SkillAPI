package com.sucy.skill.api.dynamic;

import com.rit.sucy.player.TargetHelper;
import com.rit.sucy.text.TextFormatter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public enum Target {

    /**
     * Targets the caster
     */
    SELF,

    /**
     * Targets what's being looked at
     */
    TARGET (new DefaultAttribute("Range", 8, 0)),

    /**
     * Targets an area around the caster
     */
    AREA (new DefaultAttribute("Radius", 5, 0)),

    /**
     * Targets an area around the target
     */
    TARGET_AREA (new DefaultAttribute("Range", 8, 0), new DefaultAttribute("Radius", 5, 0)),

    /**
     * Targets a line in front of the caster
     */
    LINEAR (new DefaultAttribute("Range", 10, 0)),

    /**
     * Targets in a cone in front of the caster
     */
    CONE (new DefaultAttribute("Arc", 30, 0), new DefaultAttribute("Range", 8, 0)),

    ;

    private static final String
        RANGE = "Range",
        RADIUS = "Radius",
        ARC = "Arc";

    private final DefaultAttribute[] defaults;

    /**
     * Enum constructor
     *
     * @param defaults default attributes for the targeting
     */
    private Target(DefaultAttribute ... defaults) {
        this.defaults = defaults;
    }

    /**
     * Applies default attributes to the skill
     *
     * @param skill skill to apply to
     */
    public void applyDefaults(DynamicSkill skill) {
        for (DefaultAttribute attribute : defaults) {
            attribute.apply(skill);
        }
    }

    /**
     * Gets the targets for the caster
     *
     * @param caster caster of a skill
     * @return       targets for the skill
     */
    public List<LivingEntity> getTargets(DynamicSkill skill, Player caster, int level) {
        if (this == SELF) return self(skill, caster, level);
        if (this == TARGET) return target(skill, caster, level);
        if (this == AREA) return area(skill, caster, level);
        if (this == TARGET_AREA) return targetArea(skill, caster, level);
        if (this == LINEAR) return linear(skill, caster, level);
        if (this == CONE) return cone(skill, caster, level);

        return new ArrayList<LivingEntity>();
    }

    /**
     * Self target
     *
     * @param skill  skill with the effect
     * @param caster caster of the skill
     * @param level  level of the skill
     * @return       caster
     */
    private List<LivingEntity> self(DynamicSkill skill, Player caster, int level) {
        List<LivingEntity> targets = new ArrayList<LivingEntity>();
        targets.add(caster);
        return targets;
    }

    /**
     * Target facing toward
     *
     * @param skill  skill with the effect
     * @param caster caster of the skill
     * @param level  level of the skill
     * @return       target looking at
     */
    private List<LivingEntity> target(DynamicSkill skill, Player caster, int level) {
        List<LivingEntity> targets = new ArrayList<LivingEntity>();
        LivingEntity target = TargetHelper.getLivingTarget(caster, skill.getAttribute(getAlias(skill, RANGE), level));
        if (target != null) targets.add(target);
        return targets;
    }

    /**
     * Targets for area effects
     *
     * @param skill  skill with the effect
     * @param caster player casting the skill
     * @param level  skill level
     * @return       all entities around the player
     */
    private List<LivingEntity> area(DynamicSkill skill, Player caster, int level) {
        List<LivingEntity> targets = new ArrayList<LivingEntity>();
        double radius = skill.getAttribute(getAlias(skill, RADIUS), level);
        targets.add(caster);
        for (Entity entity : caster.getNearbyEntities(radius, radius, radius)) {
            if (entity instanceof LivingEntity) {
                targets.add((LivingEntity)entity);
            }
        }
        return targets;
    }

    /**
     * Targets for target area effects
     *
     * @param skill  skill with the effect
     * @param caster player casting the skill
     * @param level  skill level
     * @return       all entities around the target
     */
    private List<LivingEntity> targetArea(DynamicSkill skill, Player caster, int level) {
        List<LivingEntity> targets = new ArrayList<LivingEntity>();
        LivingEntity target = TargetHelper.getLivingTarget(caster, skill.getAttribute(getAlias(skill, RANGE), level));
        if (target != null) {
            double radius = skill.getAttribute(getAlias(skill, RADIUS), level);
            targets.add(target);
            for (Entity entity : target.getNearbyEntities(radius, radius, radius)) {
                if (entity instanceof LivingEntity) {
                    targets.add((LivingEntity)entity);
                }
            }
        }
        return targets;
    }

    /**
     * Targets for linear effects
     *
     * @param skill  skill with the effect
     * @param caster player casting the skill
     * @param level  skill level
     * @return       all entities in a line
     */
    private List<LivingEntity> linear(DynamicSkill skill, Player caster, int level) {
        List<LivingEntity> targets = TargetHelper.getLivingTargets(caster, skill.getAttribute(getAlias(skill, RANGE), level));
        targets.add(caster);
        return targets;
    }

    /**
     * Targets for cone effects
     *
     * @param skill  skill with the effect
     * @param caster player casting the skill
     * @param level  skill level
     * @return       all entities within the cone
     */
    private List<LivingEntity> cone(DynamicSkill skill, Player caster, int level) {
        return TargetHelper.getConeTargets(caster, skill.getAttribute(getAlias(skill, ARC), level), skill.getAttribute(getAlias(skill, RANGE), level));
    }

    /**
     * Gets the alias for the attribute if needed for the skill
     *
     * @param skill    skill to check
     * @param original attribute name
     * @return         attribute alias
     */
    public String getAlias(DynamicSkill skill, String original) {
        return skill.isAliased(original) ? TextFormatter.format(name()) + " " + original : original;
    }
}
