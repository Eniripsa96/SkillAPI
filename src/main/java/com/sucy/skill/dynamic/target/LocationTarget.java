/**
 * SkillAPI
 * com.sucy.skill.dynamic.target.LocationTarget
 * <p/>
 * The MIT License (MIT)
 * <p/>
 * Copyright (c) 2014 Steven Sucy
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software") to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.sucy.skill.dynamic.target;

import com.sucy.skill.dynamic.EffectComponent;
import com.sucy.skill.dynamic.TempEntity;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Applies child components to a location using the caster's faced direction
 */
public class LocationTarget extends EffectComponent {
    private static final String RANGE = "range";
    private static final String GROUND = "ground";

    private static final HashSet<Byte> NULL_SET = null;

    /**
     * Executes the component
     *
     * @param caster  caster of the skill
     * @param level   level of the skill
     * @param targets targets to apply to
     * @return true if applied to something, false otherwise
     */
    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets) {
        boolean worked = false;
        boolean isSelf = targets.size() == 1 && targets.get(0) == caster;
        double range = attr(caster, RANGE, level, 5.0, isSelf);
        boolean groundOnly = !settings.getString(GROUND, "true").toLowerCase().equals("false");

        ArrayList<LivingEntity> list = new ArrayList<LivingEntity>();
        for (LivingEntity t : targets) {
            Location loc;
            Block b = t.getTargetBlock(NULL_SET, (int) Math.ceil(range));
            if (b == null && !groundOnly) {
                loc = t.getLocation().add(t.getLocation().getDirection().multiply(range));
            } else if (b != null) {
                loc = b.getLocation();
            } else {
                continue;
            }

            list.add(new TempEntity(loc));
        }
        return executeChildren(caster, level, list);
    }
}
