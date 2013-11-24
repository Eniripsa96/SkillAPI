package com.sucy.skill.api.dynamic;

import com.sucy.skill.api.PlayerSkills;
import com.sucy.skill.api.Valued;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Embedded data for some mechanics
 */
public class EmbedData extends Valued {

    private Player player;
    private PlayerSkills data;
    private DynamicSkill skill;

    /**
     * Constructor
     *
     * @param player        player with the skill
     * @param data          data of player with the skill
     * @param skill         skill being used
     */
    public EmbedData(Player player, PlayerSkills data, DynamicSkill skill) {
        this.player = player;
        this.data = data;
        this.skill = skill;
    }

    /**
     * @return skill with the embedded effects
     */
    public DynamicSkill getSkill() {
        return skill;
    }

    /**
     * Resolves the embedded skill without a target
     *
     * @param loc location the projectile hit
     */
    public void resolveNonTarget(Location loc) {
        if (!player.isValid()) return;
        skill.prefix = "Embed ";
        for (Mechanic mechanic : skill.embedMechanics) {
            if (mechanic.getTarget() == Target.TARGET || mechanic.getTarget() == Target.TARGET_AREA) continue;
            List<LivingEntity> targets;

            // Get the targets
            if (mechanic.getTarget() == Target.AREA) targets = area(loc, skill, data.getSkillLevel(skill.getName()));
            else {
                targets = new ArrayList<LivingEntity>();
                if (mechanic.getTarget() == Target.SELF) targets.add(player);
                else continue;
            }

            // Resolve the effects
            mechanic.resolve(player, data, skill, targets);
        }
        skill.prefix = "";
    }

    /**
     * Resolves the embedded skill on a target
     *
     * @param target target
     */
    public void resolveTarget(LivingEntity target) {
        if (!player.isValid()) return;
        skill.prefix = "Embed ";
        for (Mechanic mechanic : skill.embedMechanics) {
            if (mechanic.getTarget() != Target.TARGET && mechanic.getTarget() != Target.TARGET_AREA) return;

            List<LivingEntity> targets;
            if (mechanic.getTarget() == Target.TARGET_AREA) targets = area(target.getLocation(), skill, data.getSkillLevel(skill.getName()));
            else {
                targets = new ArrayList<LivingEntity>();
                targets.add(target);
            }

            mechanic.resolve(player, data, skill, targets);
        }
        skill.prefix = "";
    }

    /**
     * Targets for area effects
     *
     * @param center the center of the area
     * @param skill  skill with the effect
     * @param level  level of the skill
     * @return       all entities around the player
     */
    private List<LivingEntity> area(Location center, DynamicSkill skill, int level) {
        List<LivingEntity> targets = new ArrayList<LivingEntity>();
        int radius = skill.getAttribute("Radius", level);
        int radiusSq = radius * radius;
        for (Entity entity : center.getWorld().getEntities()) {
            if (entity.getLocation().distanceSquared(center) < radiusSq && entity instanceof LivingEntity) {
                targets.add((LivingEntity)entity);
            }
        }
        return targets;
    }
}
