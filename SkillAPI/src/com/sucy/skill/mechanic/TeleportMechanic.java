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
        int vDistance = skill.getAttribute(V_DISTANCE, target, level);
        int hDistance = skill.getAttribute(H_DISTANCE, target, level);

        // Make all targets teleport

        // Not through walls
        if (skill.getValue(THROUGH_WALLS) == 1) {
            for (LivingEntity t : targets) {
                if (t.getLocation().getBlock().getType() != Material.AIR) continue;
                Vector vec = t.getLocation().getDirection();
                double xRatio = vec.getX() / Math.abs(vec.getX() + vec.getZ());
                double zRatio = vec.getZ() / Math.abs(vec.getX() + vec.getZ());

                double xChange = xRatio * hDistance;
                double zChange = zRatio * hDistance;

                int steps = Math.max(Math.max((int)xChange, vDistance), (int)zChange);

                double xScale = xChange / steps;
                double yScale = (double)vDistance / steps;
                double zScale = zChange / steps;

                Location loc = t.getLocation();
                for (int i = 0; i < steps; i++) {
                    loc.add(xScale, yScale, zScale);
                    if (loc.getBlock().getType() != Material.AIR && loc.getBlock().getRelative(0, 1, 0).getType() != Material.AIR) {
                        loc.subtract(xScale, yScale, zScale);
                        break;
                    }
                }
                t.teleport(loc);
                worked = true;
            }
        }

        // Through walls
        else {
            for (LivingEntity t : targets) {
                if (t.getLocation().getBlock().getType() != Material.AIR) continue;
                Vector vec = t.getLocation().getDirection();
                double xRatio = vec.getX() / (vec.getX() + vec.getZ());
                double zRatio = 1 - xRatio;

                Location loc = t.getLocation().add(xRatio * hDistance, vDistance, zRatio * hDistance);
                while (loc.getBlock().getType() != Material.AIR && loc.distanceSquared(t.getLocation()) > 0.01) {
                    if (loc.getBlock().getRelative(0, 1, 0).getType() != Material.AIR) {
                        loc = loc.getBlock().getRelative(0, 1, 0).getLocation();
                    }
                    else {
                        loc.setX(shrink(t.getLocation().getX(), loc.getX()));
                        loc.setY(shrink(t.getLocation().getY(), loc.getY()));
                        loc.setZ(shrink(t.getLocation().getZ(), loc.getZ()));
                    }
                }
                t.teleport(loc);
                worked = true;
            }
        }

        return worked;
    }

    /**
     * Shrinks a coordinate down towards the original
     *
     * @param original original coordinate
     * @param modified modified coordinate
     * @return         shrunk coordinate
     */
    private double shrink(double original, double modified) {
        if (modified > original) return modified - Math.min(1, modified - original);
        else return modified + Math.min(1, original - modified);
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
