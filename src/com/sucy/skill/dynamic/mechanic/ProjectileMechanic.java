/**
 * SkillAPI
 * com.sucy.skill.dynamic.mechanic.ProjectileMechanic
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
import com.sucy.skill.cast.*;
import com.sucy.skill.dynamic.EffectComponent;
import com.sucy.skill.dynamic.TempEntity;
import com.sucy.skill.listener.MechanicListener;
import com.sucy.skill.task.RemoveTask;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Heals each target
 */
public class ProjectileMechanic extends EffectComponent
{
    private static final String PROJECTILE = "projectile";
    private static final String SPEED      = "velocity";
    private static final String ANGLE      = "angle";
    private static final String AMOUNT     = "amount";
    private static final String LEVEL      = "skill_level";
    private static final String HEIGHT     = "height";
    private static final String RADIUS     = "rain-radius";
    private static final String SPREAD     = "spread";
    private static final String COST       = "cost";
    private static final String RANGE      = "range";
    private static final String FLAMING    = "flaming";

    /**
     * Creates the list of indicators for the skill
     *
     * @param list   list to store indicators in
     * @param caster caster reference
     * @param target location to base location on
     * @param level  the level of the skill to create for
     */
    @Override
    public void makeIndicators(List<IIndicator> list, Player caster, LivingEntity target, int level)
    {
        // Get common values
        String spread = settings.getString(SPREAD, "cone").toLowerCase();

        // Apply the spread type
        if (spread.equals("rain"))
        {
            double radius = attr(caster, RADIUS, level, 2.0, true);

            if (indicatorType == IndicatorType.DIM_2)
            {
                IIndicator indicator = new CircleIndicator(radius);
                indicator.moveTo(target.getLocation().add(0, 0.1, 0));
                list.add(indicator);
            }
            else
            {
                double height = attr(caster, HEIGHT, level, 8.0, true);
                IIndicator indicator = new CylinderIndicator(radius, height);
                indicator.moveTo(target.getLocation());
                list.add(indicator);
            }
        }
        else
        {
            // Get common values
            int amount = (int) attr(caster, AMOUNT, level, 1.0, true);
            double speed = attr(caster, SPEED, level, 2.0, true);
            String projectile = settings.getString(PROJECTILE, "arrow").toLowerCase();
            Class<? extends Projectile> type = PROJECTILES.get(projectile);
            if (type == null)
            {
                type = Arrow.class;
            }

            double g;
            if (type == Arrow.class)
                g = 0.05;
            else if (type == LargeFireball.class)
                g = 0;
            else
                g = 0.03;

            Vector dir = target.getLocation().getDirection();
            if (spread.equals("horizontal cone"))
            {
                dir.setY(0);
                dir.normalize();
            }
            double angle = attr(caster, ANGLE, level, 30.0, true);
            ArrayList<Vector> dirs = CustomProjectile.calcSpread(dir, angle, amount);
            Location loc = caster.getLocation().add(0, caster.getEyeHeight(), 0);
            for (Vector d : dirs)
            {
                ProjectileIndicator indicator = new ProjectileIndicator(speed, g);
                indicator.setDirection(d);
                indicator.moveTo(loc);
                list.add(indicator);
            }
        }
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
        // Get common values
        int amount = (int) attr(caster, AMOUNT, level, 1.0, true);
        double speed = attr(caster, SPEED, level, 2.0, true);
        double range = attr(caster, RANGE, level, 999, true);
        boolean flaming = settings.getString(FLAMING, "false").equalsIgnoreCase("true");
        String spread = settings.getString(SPREAD, "cone").toLowerCase();
        String projectile = settings.getString(PROJECTILE, "arrow").toLowerCase();
        String cost = settings.getString(COST, "none").toLowerCase();
        Class<? extends Projectile> type = PROJECTILES.get(projectile);
        if (type == null)
        {
            type = Arrow.class;
        }

        // Cost to cast
        if (cost.equals("one") || cost.equals("all"))
        {
            Material mat = MATERIALS.get(settings.getString(PROJECTILE, "arrow").toLowerCase());
            if (mat == null || !(caster instanceof Player)) return false;
            Player player = (Player) caster;
            if (cost.equals("one") && !player.getInventory().contains(mat, 1))
            {
                return false;
            }
            if (cost.equals("all") && !player.getInventory().contains(mat, amount))
            {
                return false;
            }
            if (cost.equals("one"))
            {
                player.getInventory().removeItem(new ItemStack(mat));
            }
            else player.getInventory().removeItem(new ItemStack(mat, amount));
        }

        // Fire from each target
        ArrayList<Entity> projectiles = new ArrayList<Entity>();
        for (LivingEntity target : targets)
        {
            // Apply the spread type
            if (spread.equals("rain"))
            {
                double radius = attr(caster, RADIUS, level, 2.0, true);
                double height = attr(caster, HEIGHT, level, 8.0, true);

                ArrayList<Location> locs = CustomProjectile.calcRain(target.getLocation(), radius, height, amount);
                for (Location loc : locs)
                {
                    Projectile p = caster.launchProjectile(type);
                    p.setVelocity(new Vector(0, speed, 0));
                    p.teleport(loc);
                    SkillAPI.setMeta(p, LEVEL, level);
                    if (flaming) p.setFireTicks(9999);
                    projectiles.add(p);
                }
            }
            else
            {
                Vector dir = target.getLocation().getDirection();
                if (spread.equals("horizontal cone"))
                {
                    dir.setY(0);
                    dir.normalize();
                }
                double angle = attr(caster, ANGLE, level, 30.0, true);
                ArrayList<Vector> dirs = CustomProjectile.calcSpread(dir, angle, amount);
                for (Vector d : dirs)
                {
                    Projectile p = caster.launchProjectile(type);
                    p.setVelocity(d.multiply(speed));
                    SkillAPI.setMeta(p, MechanicListener.P_CALL, this);
                    SkillAPI.setMeta(p, LEVEL, level);
                    if (flaming) p.setFireTicks(9999);
                    projectiles.add(p);
                }
            }
        }
        new RemoveTask(projectiles, (int) Math.ceil(range / Math.abs(speed)));

        return targets.size() > 0;
    }

    /**
     * The callback for the projectiles that applies child components
     *
     * @param projectile projectile calling back for
     * @param hit        the entity hit by the projectile, if any
     */
    public void callback(Projectile projectile, LivingEntity hit)
    {
        if (hit == null)
        {
            hit = new TempEntity(projectile.getLocation());
        }
        ArrayList<LivingEntity> targets = new ArrayList<LivingEntity>();
        targets.add(hit);
        executeChildren((LivingEntity) projectile.getShooter(), SkillAPI.getMetaInt(projectile, LEVEL), targets);
        projectile.remove();
    }

    private static final HashMap<String, Class<? extends Projectile>> PROJECTILES = new HashMap<String, Class<? extends Projectile>>()
    {{
            put("arrow", Arrow.class);
            put("egg", Egg.class);
            put("ghast fireball", LargeFireball.class);
            put("snowball", Snowball.class);
        }};

    private static final HashMap<String, Material> MATERIALS = new HashMap<String, Material>()
    {{
            put("arrow", Material.ARROW);
            put("egg", Material.EGG);
            put("snowball", Material.SNOW_BALL);
        }};
}
