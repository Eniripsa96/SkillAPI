package com.sucy.skill.dynamic.mechanic;

import com.sucy.skill.api.projectile.CustomProjectile;
import com.sucy.skill.api.projectile.ParticleProjectile;
import com.sucy.skill.api.projectile.ProjectileCallback;
import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Bat;
import org.bukkit.entity.LivingEntity;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
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
        int amount = (int) settings.getAttr(AMOUNT, level, 1.0);
        String spread = settings.getString(SPREAD, "cone").toLowerCase();

        // Fire from each target
        for (LivingEntity target : targets)
        {
            // Apply the spread type
            ArrayList<ParticleProjectile> list;
            if (spread.equals("rain"))
            {
                double radius = settings.getAttr(RADIUS, level, 2.0);
                double height = settings.getAttr(HEIGHT, level, 8.0);
                list = ParticleProjectile.rain(caster, target.getLocation(), settings, radius, height, amount, this);
            }
            else
            {
                Vector dir = target.getLocation().getDirection();
                if (spread.equals("horizontal cone"))
                {
                    dir.setY(0);
                    dir.normalize();
                }
                double angle = settings.getAttr(ANGLE, level, 30.0);
                list = ParticleProjectile.spread(caster, dir, target.getLocation().add(0, 1.25, 0), settings, angle, amount, this);
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
        boolean remove = false;
        if (hit == null)
        {
            hit = projectile.getLocation().getWorld().spawn(projectile.getLocation(), Bat.class);
            hit.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 100, 100), false);
            hit.setMaxHealth(10000);
            hit.setHealth(hit.getMaxHealth());
            remove = true;
        }
        ArrayList<LivingEntity> targets = new ArrayList<LivingEntity>();
        targets.add(hit);
        executeChildren(projectile.getShooter(), projectile.getMetadata(LEVEL).get(0).asInt(), targets);
        if (remove)
        {
            hit.remove();
        }
    }
}
