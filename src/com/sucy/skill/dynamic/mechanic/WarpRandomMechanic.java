/**
 * SkillAPI
 * com.sucy.skill.dynamic.mechanic.WarpRandomMechanic
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Steven Sucy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software") to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.sucy.skill.dynamic.mechanic;

import com.rit.sucy.player.TargetHelper;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;

import java.util.List;
import java.util.Random;

/**
 * Strikes lightning about each target with an offset
 */
public class WarpRandomMechanic extends MechanicComponent
{
    private static final Random random = new Random();

    private static final String WALL       = "walls";
    private static final String HORIZONTAL = "horizontal";
    private static final String DISTANCE   = "distance";

    @Override
    public String getKey() {
        return "warp random";
    }

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
        boolean horizontal = !settings.getString(HORIZONTAL, "true").toLowerCase().equals("false");
        double distance = parseValues(caster, DISTANCE, level, 3.0);

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
