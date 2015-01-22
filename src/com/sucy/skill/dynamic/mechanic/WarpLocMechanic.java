package com.sucy.skill.dynamic.mechanic;

import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Strikes lightning about each target with an offset
 */
public class WarpLocMechanic extends EffectComponent
{
    private static final String WORLD = "world";
    private static final String X = "x";
    private static final String Y = "y";
    private static final String Z = "z";
    private static final String YAW = "yaw";
    private static final String PITCH = "pitch";

    /**
     * Executes the component
     *
     * @param caster  caster of the skill
     * @param level   level of the skill
     * @param targets targets to apply to
     *
     * @return true if applied to something, false otherwise
     */
    @Override
    public boolean execute(final LivingEntity caster, final int level, final List<LivingEntity> targets)
    {
        if (targets.size() == 0)
        {
            return false;
        }

        // Get the world
        String world = settings.getString(WORLD, "current");
        if (world.equalsIgnoreCase("current"))
        {
            world = caster.getWorld().getName();
        }
        World w = Bukkit.getWorld(world);
        if (w == null)
        {
            return false;
        }

        // Get the other values
        double x = settings.get(X, 0.0);
        double y = settings.get(Y, 0.0);
        double z = settings.get(Z, 0.0);
        float yaw = (float)settings.get(YAW, 0.0);
        float pitch = (float)settings.get(PITCH, 0.0);

        Location loc = new Location(w, x, y, z, yaw, pitch);

        for (LivingEntity target : targets)
        {
            target.teleport(loc);
        }
        return true;
    }
}
