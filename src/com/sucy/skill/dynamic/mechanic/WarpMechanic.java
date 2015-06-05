package com.sucy.skill.dynamic.mechanic;

import com.rit.sucy.player.TargetHelper;
import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Strikes lightning about each target with an offset
 */
public class WarpMechanic extends EffectComponent
{
    private static final Vector UP = new Vector(0, 1, 0);

    private static final String WALL    = "walls";
    private static final String FORWARD = "forward";
    private static final String UPWARD  = "upward";
    private static final String RIGHT   = "right";

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
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets)
    {
        if (targets.size() == 0)
        {
            return false;
        }

        // Get the world
        boolean isSelf = targets.size() == 1 && targets.get(0) == caster;
        boolean throughWalls = settings.getString(WALL, "false").toLowerCase().equals("true");
        double forward = attr(caster, FORWARD, level, 0.0, isSelf);
        double upward = attr(caster, UPWARD, level, 0.0, isSelf);
        double right = attr(caster, RIGHT, level, 0.0, isSelf);

        for (LivingEntity target : targets)
        {
            Vector dir = target.getLocation().getDirection();
            Vector side = dir.clone().crossProduct(UP).multiply(right);
            Location loc = target.getLocation().add(dir.multiply(forward)).add(side).add(0, upward, 0).add(0, 1, 0);
            loc = TargetHelper.getOpenLocation(target.getLocation().add(0, 1, 0), loc, throughWalls);
            if (!loc.getBlock().getType().isSolid() && loc.getBlock().getRelative(BlockFace.DOWN).getType().isSolid())
            {
                loc.add(0, 1, 0);
            }
            target.teleport(loc.subtract(0, 1, 0));
        }
        return targets.size() > 0;
    }
}
