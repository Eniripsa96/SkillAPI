/**
 * SkillAPI
 * com.sucy.skill.dynamic.mechanic.ItemProjectileMechanic
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

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.projectile.CustomProjectile;
import com.sucy.skill.api.projectile.ItemProjectile;
import com.sucy.skill.api.projectile.ProjectileCallback;
import com.sucy.skill.dynamic.EffectComponent;
import com.sucy.skill.dynamic.TempEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Heals each target
 */
public class ItemProjectileMechanic extends EffectComponent implements ProjectileCallback
{
    private static final Vector UP = new Vector(0, 1, 0);

    private static final String ITEM     = "item";
    private static final String DATA     = "item-data";
    private static final String SPEED    = "velocity";
    private static final String ANGLE    = "angle";
    private static final String AMOUNT   = "amount";
    private static final String LEVEL    = "skill_level";
    private static final String HEIGHT   = "height";
    private static final String RADIUS   = "rain-radius";
    private static final String SPREAD   = "spread";
    private static final String ALLY     = "group";
    private static final String RIGHT   = "right";
    private static final String UPWARD  = "upward";
    private static final String FORWARD = "forward";

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
        Material mat = Material.JACK_O_LANTERN;
        try
        {
            mat = Material.valueOf(settings.getString(ITEM).toUpperCase().replace(" ", "_"));
        }
        catch (Exception ex)
        {
            // Invalid or missing item material
        }
        ItemStack item = new ItemStack(mat);
        item.setDurability((short) settings.getInt(DATA, 0));

        // Get other common values
        double speed = attr(caster, SPEED, level, 3.0, true);
        int amount = (int) attr(caster, AMOUNT, level, 1.0, true);
        String spread = settings.getString(SPREAD, "cone").toLowerCase();
        boolean ally = settings.getString(ALLY, "enemy").toLowerCase().equals("ally");

        // Fire from each target
        for (LivingEntity target : targets)
        {
            Location loc = target.getLocation();

            // Apply the spread type
            ArrayList<ItemProjectile> list;
            if (spread.equals("rain"))
            {
                double radius = attr(caster, RADIUS, level, 2.0, true);
                double height = attr(caster, HEIGHT, level, 8.0, true);
                list = ItemProjectile.rain(caster, loc, item, radius, height, speed, amount, this);
            }
            else
            {
                Vector dir = target.getLocation().getDirection();

                double right = attr(caster, RIGHT, level, 0, true);
                double upward = attr(caster, UPWARD, level, 0, true);
                double forward = attr(caster, FORWARD, level, 0, true);

                Vector looking = dir.clone().setY(0).normalize();
                Vector normal = looking.clone().crossProduct(UP);
                looking.multiply(forward).add(normal.multiply(right));

                if (spread.equals("horizontal cone"))
                {
                    dir.setY(0);
                    dir.normalize();
                }
                dir.multiply(speed);
                double angle = attr(caster, ANGLE, level, 30.0, true);
                list = ItemProjectile.spread(
                    caster,
                    dir,
                    loc.add(looking).add(0, 0.5 + upward, 0),
                    item,
                    angle,
                    amount,
                    this
                );
            }

            // Set metadata for when the callback happens
            for (ItemProjectile p : list)
            {
                SkillAPI.setMeta(p, LEVEL, level);
                p.setAllyEnemy(ally, !ally);
            }
        }

        return targets.size() > 0;
    }

    /**
     * The callback for the projectiles that applies child components
     *
     * @param projectile projectile calling back for
     * @param hit        the entity hit by the projectile, if any
     */
    @Override
    public void callback(CustomProjectile projectile, LivingEntity hit)
    {
        if (hit == null)
        {
            hit = new TempEntity(projectile.getLocation());
        }
        ArrayList<LivingEntity> targets = new ArrayList<LivingEntity>();
        targets.add(hit);
        executeChildren(projectile.getShooter(), SkillAPI.getMetaInt(projectile, LEVEL), targets);
    }
}
