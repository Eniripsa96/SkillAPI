package com.sucy.skill.dynamic.mechanic;

import com.sucy.skill.api.projectile.CustomProjectile;
import com.sucy.skill.api.projectile.ItemProjectile;
import com.sucy.skill.api.projectile.ParticleProjectile;
import com.sucy.skill.api.projectile.ProjectileCallback;
import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.FixedMetadataValue;
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
    private static final String SPEED = "speed";
    private static final String ANGLE  = "angle";
    private static final String AMOUNT = "amount";
    private static final String LEVEL  = "skill_level";
    private static final String HEIGHT = "height";
    private static final String RADIUS = "radius";
    private static final String SPREAD = "spread";

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
        int amount = (int) settings.get(AMOUNT, level, 1.0);
        double speed = settings.get(SPEED, level, 2.0);
        String spread = settings.getString(SPREAD, "cone").toLowerCase();
        String projectile = settings.getString(PROJECTILE, "arrow");
        Class<? extends Projectile> type = PROJECTILES.get(projectile);
        if (type == null) type = Arrow.class;

        // Fire from each target
        for (LivingEntity target : targets)
        {
            // Apply the spread type
            if (spread.equals("rain"))
            {
                double radius = settings.get(RADIUS, level, 2.0);
                double height = settings.get(HEIGHT, level, 8.0);

                ArrayList<Location> locs = CustomProjectile.calcRain(target.getLocation(), radius, height, amount);
                for (Location loc : locs)
                {
                    Projectile p = caster.launchProjectile(type);
                    p.teleport(loc);
                    p.setVelocity(new Vector(0, speed, 0));
                    p.setMetadata(LEVEL, new FixedMetadataValue(Bukkit.getPluginManager().getPlugin("SkillAPI"), level));
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
                double angle = settings.get(ANGLE, level, 30.0);
                ArrayList<Vector> dirs = CustomProjectile.calcSpread(dir, angle, amount);
                for (Vector d : dirs)
                {
                    Projectile p = caster.launchProjectile(type);
                    p.teleport(target.getLocation());
                    p.setVelocity(d.multiply(speed));
                }
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
    public void callback(Projectile projectile, LivingEntity hit)
    {
        boolean remove = false;
        if (hit == null)
        {
            hit = projectile.getLocation().getWorld().spawn(projectile.getLocation(), Bat.class);
            hit.setMaxHealth(10000);
            hit.setHealth(hit.getMaxHealth());
            remove = true;
        }
        ArrayList<LivingEntity> targets = new ArrayList<LivingEntity>();
        targets.add(hit);
        executeChildren((LivingEntity)projectile.getShooter(), projectile.getMetadata(LEVEL).get(0).asInt(), targets);
        if (remove)
        {
            hit.remove();
        }
    }

    private static final HashMap<String, Class<? extends Projectile>> PROJECTILES = new HashMap<String, Class<? extends Projectile>>()
    {{
            put("arrow", Arrow.class);
            put("egg", Egg.class);
            put("ghast fireball", LargeFireball.class);
            put("snowball", Snowball.class);
    }};
}
