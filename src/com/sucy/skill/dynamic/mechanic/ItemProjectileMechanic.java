package com.sucy.skill.dynamic.mechanic;

import com.sucy.skill.api.projectile.CustomProjectile;
import com.sucy.skill.api.projectile.ItemProjectile;
import com.sucy.skill.api.projectile.ProjectileCallback;
import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Bat;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
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
            mat = Material.valueOf(settings.getString(ITEM));
        }
        catch (Exception ex)
        {
            // Invalid or missing item material
        }
        ItemStack item = new ItemStack(mat);
        item.setData(new MaterialData(mat, (byte) settings.getInt(DATA, 0)));

        double speed = settings.get(SPEED, level, 3.0);
        double angle = settings.get(ANGLE, level, 30.0);
        int amount = (int) settings.get(AMOUNT, level, 1.0);

        Vector vel = caster.getLocation().getDirection().multiply(speed);
        ItemProjectile projectile = new ItemProjectile(caster, item, vel);
        projectile.setCallback(this);
        projectile.setMetadata(LEVEL, new FixedMetadataValue(Bukkit.getPluginManager().getPlugin("SkillAPI"), level));

        return true;
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
