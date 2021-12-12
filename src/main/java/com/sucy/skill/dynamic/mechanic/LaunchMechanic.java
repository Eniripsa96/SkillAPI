/**
 * SkillAPI
 * com.sucy.skill.dynamic.mechanic.LaunchMechanic
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

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.log.Logger;

import java.util.List;

/**
 * Launches the target in a given direction relative to their forward direction
 */
public class LaunchMechanic extends MechanicComponent {
    private Vector up = new Vector(0, 1, 0);

    private static final String FORWARD = "forward";
    private static final String UPWARD  = "upward";
    private static final String RIGHT   = "right";

    private static final String RELATIVE = "relative";

    @Override
    public String getKey() {
        return "launch";
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
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets, boolean isCrit) {
        if (targets.size() == 0) {
            return false;
        }

        double forward = parseValues(caster, FORWARD, level, 0);
        double upward = parseValues(caster, UPWARD, level, 0);
        double right = parseValues(caster, RIGHT, level, 0);
        String relative = settings.getString(RELATIVE, "target").toLowerCase();
        for (LivingEntity target : targets) {
            final Vector dir;
            if (relative.equals("caster")) {
                dir = caster.getLocation().getDirection().setY(0).normalize();
            } else if (relative.equals("between")) {
                dir = target.getLocation().toVector().subtract(caster.getLocation().toVector()).setY(0).normalize();
            } else {
                dir = target.getLocation().getDirection().setY(0).normalize();
            }

            final Vector nor = dir.clone().crossProduct(up);
            dir.multiply(forward);
            dir.add(nor.multiply(right)).setY(upward);

            try {
            	target.setVelocity(dir);
            }
            catch (IllegalArgumentException e) {
            	Logger.bug("Illegal Argument Exception from player " + caster.getName());
            	Player p = (Player) caster;
            	Logger.bug("Caster class is " + SkillAPI.getPlayerData(p).getClass("class").getData().getName());
            	e.printStackTrace();
            }
        }
        return targets.size() > 0;
    }
}
