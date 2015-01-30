package com.sucy.skill.dynamic.mechanic;

import com.rit.sucy.player.TargetHelper;
import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;

import java.util.List;
import java.util.Random;

/**
 * Strikes lightning about each target with an offset
 */
public class WarpRandomMechanic extends EffectComponent
{
    private static final Random random = new Random();

    private static final String WALL       = "walls";
    private static final String HORIZONTAL = "horizontal";
    private static final String DISTANCE   = "distance";

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
        boolean throughWalls = settings.getString(WALL, "false").toLowerCase().equals("true");
        boolean horizontal = !settings.getString(HORIZONTAL, "true").toLowerCase().equals("false");
        double distance = settings.getAttr(DISTANCE, level, 3.0);

        for (LivingEntity target : targets)
        {
            Location loc;
            Location temp = target.getLocation();
            do
            {
                loc = temp.clone().add(rand(distance), 0, rand(distance));
                if (!horizontal)
                {
                    loc.add(0, rand(distance), 0);
                }
            }
            while (temp.distanceSquared(loc) > distance * distance);
            loc = TargetHelper.getOpenLocation(target.getLocation().add(0, 1, 0), loc, throughWalls);
            if (!loc.getBlock().getType().isSolid() && loc.getBlock().getRelative(BlockFace.DOWN).getType().isSolid())
            {
                loc.add(0, 1, 0);
            }
            target.teleport(loc.subtract(0, 1, 0));
        }
        return targets.size() > 0;
    }

    private double rand(double distance)
    {
        return random.nextDouble() * distance * 2 - distance;
    }
}
