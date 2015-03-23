package com.sucy.skill.dynamic.mechanic;

import com.sucy.skill.api.projectile.CustomProjectile;
import com.sucy.skill.api.projectile.ItemProjectile;
import com.sucy.skill.api.projectile.ProjectileCallback;
import com.sucy.skill.dynamic.EffectComponent;
import com.sucy.skill.dynamic.TempEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Heals each target
 */
public class ItemProjectileMechanic extends EffectComponent implements ProjectileCallback
{
    private static final String ITEM   = "item";
    private static final String DATA   = "item-data";
    private static final String SPEED  = "velocity";
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
        double speed = settings.getAttr(SPEED, level, 3.0);
        int amount = (int) settings.getAttr(AMOUNT, level, 1.0);
        String spread = settings.getString(SPREAD, "cone").toLowerCase();

        // Fire from each target
        for (LivingEntity target : targets)
        {
            Location loc = target.getLocation();

            // Apply the spread type
            ArrayList<ItemProjectile> list;
            if (spread.equals("rain"))
            {
                double radius = settings.getAttr(RADIUS, level, 2.0);
                double height = settings.getAttr(HEIGHT, level, 8.0);
                list = ItemProjectile.rain(caster, loc, item, radius, height, speed, amount, this);
            }
            else
            {
                Vector dir = target.getLocation().getDirection();
                if (spread.equals("horizontal cone"))
                {
                    dir.setY(0);
                    dir.normalize();
                }
                dir.multiply(speed);
                double angle = settings.getAttr(ANGLE, level, 30.0);
                list = ItemProjectile.spread(caster, dir, loc.add(0, 0.5, 0), item, angle, amount, this);
            }

            // Set metadata for when the callback happens
            for (ItemProjectile p : list)
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
