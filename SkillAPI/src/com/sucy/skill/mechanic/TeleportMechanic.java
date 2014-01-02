package com.sucy.skill.mechanic;

import com.sucy.skill.api.PlayerSkills;
import com.sucy.skill.api.dynamic.DynamicSkill;
import com.sucy.skill.api.dynamic.IMechanic;
import com.sucy.skill.api.dynamic.Target;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Mechanic for making targets dash forward
 */
public class TeleportMechanic implements IMechanic {

    private static final String
            V_DISTANCE = "Vertical Distance",
            H_DISTANCE = "Horizontal Distance",
            THROUGH_WALLS = "ThroughWalls";

    /**
     * Forces all targets to dash
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

        // Get attributes
        boolean worked = false;
        int level = data.getSkillLevel(skill.getName());
        int vDistance = (int)skill.getAttribute(V_DISTANCE, target, level);
        int hDistance = (int)skill.getAttribute(H_DISTANCE, target, level);

        // Make all targets teleport

        // Not through walls
        if (skill.getValue(THROUGH_WALLS) == 1) {
            for (LivingEntity t : targets) {
                if (t.getLocation().getBlock().getType().isSolid()) continue;
                Vector vec = t.getLocation().getDirection();
                vec.multiply(hDistance / vec.length());

                double x = Math.abs(vec.getX());
                double z = Math.abs(vec.getZ());
                int steps = 2 * (int)Math.max(Math.max(x, vDistance), z);

                double xScale = vec.getX() / steps;
                double yScale = vDistance / steps;
                double zScale = vec.getZ() / steps;

                Location loc = t.getLocation().add(0, 0.5, 0);
                for (int i = 0; i < steps; i++) {
                    loc.add(xScale, yScale, zScale);
                    if (loc.getBlock().getType().isSolid() && loc.getBlock().getRelative(0, 1, 0).getType() != Material.AIR) {
                        loc.subtract(xScale, yScale, zScale);
                        break;
                    }
                }
                t.teleport(loc.add(0, -0.5, 0));
                worked = true;
            }
        }

        // Through walls
        else {
            for (LivingEntity t : targets) {
                Location loc = t.getLocation().add(0, 0.5, 0);
                if (loc.getBlock().getType().isSolid()) continue;
                Vector vec = t.getLocation().getDirection();

                double x = Math.abs(vec.getX());
                double z = Math.abs(vec.getZ());
                int steps = 2 * (int)Math.max(Math.max(x, vDistance), z);

                double xScale = vec.getX() / steps;
                double yScale = vDistance / steps;
                double zScale = vec.getZ() / steps;

                loc.add(xScale * steps, vDistance, zScale * hDistance);
                int count = 0;
                while (loc.getBlock().getType().isSolid() && count < steps) {
                    if (!loc.getBlock().getRelative(0, 1, 0).getType().isSolid()) {
                        loc = loc.getBlock().getRelative(0, 1, 0).getLocation();
                    }
                    else {
                        loc.add(-xScale, -yScale, -zScale);
                    }
                    count++;
                }
                t.teleport(loc.add(0, -0.5, 0));
                worked = true;
            }
        }

        return worked;
    }

    /**
     * Sets default attributes for the skill
     *
     * @param skill  skill to apply to
     * @param prefix prefix to add to the name
     */
    @Override
    public void applyDefaults(DynamicSkill skill, String prefix) {
        skill.checkDefault(prefix + V_DISTANCE, 0, 0);
        skill.checkDefault(prefix + H_DISTANCE, 5, 1);
        if (!skill.isSet(THROUGH_WALLS) || skill.getValue(THROUGH_WALLS) < 0 || skill.getValue(THROUGH_WALLS) > 1) {
            skill.setValue(THROUGH_WALLS, 0);
        }
    }

    /**
     * @return names of all attributes used by this mechanic
     */
    @Override
    public String[] getAttributeNames() {
        return new String[] { H_DISTANCE, V_DISTANCE };
    }
}
