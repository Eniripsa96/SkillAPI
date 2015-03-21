package com.sucy.skill.dynamic.mechanic;

import com.sucy.skill.api.projectile.CustomProjectile;
import com.sucy.skill.api.projectile.ParticleProjectile;
import com.sucy.skill.api.projectile.ProjectileCallback;
import com.sucy.skill.dynamic.EffectComponent;
import com.sucy.skill.dynamic.TempEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Heals each target
 */
public class ParticleProjectileMechanic extends EffectComponent implements ProjectileCallback
{
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
        boolean isSelf = targets.size() == 1 && targets.get(0) == caster;
        int amount = (int) attr(caster, AMOUNT, level, 1.0, isSelf);
        String spread = settings.getString(SPREAD, "cone").toLowerCase();
        settings.set("level", level);

        // Fire from each target
        for (LivingEntity target : targets)
        {
            Location loc = target.getLocation();

            // Apply the spread type
            ArrayList<ParticleProjectile> list;
            if (spread.equals("rain"))
            {
                double radius = attr(caster, RADIUS, level, 2.0, isSelf);
                double height = attr(caster, HEIGHT, level, 8.0, isSelf);
                list = ParticleProjectile.rain(caster, level, loc, settings, radius, height, amount, this);
            }
            else
            {
                Vector dir = target.getLocation().getDirection();
                if (spread.equals("horizontal cone"))
                {
                    dir.setY(0);
                    dir.normalize();
                }
                double angle = attr(caster, ANGLE, level, 30.0, isSelf);
                list = ParticleProjectile.spread(caster, level, dir, loc.add(0, 1, 0), settings, angle, amount, this);
            }

            // Set metadata for when the callback happens
            for (ParticleProjectile p : list)
            {
                p.setMetadata(LEVEL, new FixedMetadataValue(Bukkit.getPluginManager().getPlugin("SkillAPI"), level));
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
        executeChildren(projectile.getShooter(), projectile.getMetadata(LEVEL).get(0).asInt(), targets);
    }
}
