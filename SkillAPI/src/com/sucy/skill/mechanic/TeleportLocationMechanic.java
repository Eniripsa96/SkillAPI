package com.sucy.skill.mechanic;

import com.sucy.skill.api.PlayerSkills;
import com.sucy.skill.api.dynamic.DynamicSkill;
import com.sucy.skill.api.dynamic.IMechanic;
import com.sucy.skill.api.dynamic.Target;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Mechanic for making targets dash forward
 */
public class TeleportLocationMechanic implements IMechanic {

    private static final String
            WORLD = "World",
            X = "X",
            Y = "Y",
            Z = "Z",
            YAW = "Yaw",
            PITCH = "Pitch";

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

        // Needs targets
        if (targets.size() == 0) return false;

        // Get values
        World world = skill.hasString(WORLD) ? skill.getAPI().getServer().getWorld(skill.getString(WORLD)) : null;
        if (world == null) world = player.getWorld();
        int x = skill.getValue(X);
        int y = skill.getValue(Y);
        int z = skill.getValue(Z);
        int yaw = skill.getValue(YAW);
        int pitch = skill.getValue(PITCH);
        Location loc = new Location(world, x, y, z, yaw, pitch);

        // Teleport targets
        for (LivingEntity t : targets) {
            t.teleport(loc);
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
        if (!skill.isSet(X)) skill.setValue(X, 0);
        if (!skill.isSet(Y)) skill.setValue(Y, 70);
        if (!skill.isSet(Z)) skill.setValue(Z, 0);
        if (!skill.isSet(YAW)) skill.setValue(YAW, 0);
        if (!skill.isSet(PITCH)) skill.setValue(PITCH, 0);
    }

    /**
     * @return names of all attributes used by this mechanic
     */
    @Override
    public String[] getAttributeNames() {
        return new String[0];
    }
}
