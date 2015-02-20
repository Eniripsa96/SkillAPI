package com.sucy.skill.dynamic.mechanic;

import com.rit.sucy.player.Protection;
import com.rit.sucy.player.TargetHelper;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.dynamic.EffectComponent;
import com.sucy.skill.dynamic.TempEntity;
import com.sucy.skill.listener.MechanicListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Heals each target
 */
public class PotionProjectileMechanic extends EffectComponent
{
    private static final String POTION = "type";
    private static final String ALLY   = "group";
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
        // Get common values
        String potion = settings.getString(POTION, "slowness").toUpperCase().replace(" ", "_");
        PotionType type;
        try
        {
            type = PotionType.valueOf(potion);
        }
        catch (Exception ex)
        {
            return false;
        }

        Potion p = new Potion(type, 1);
        ItemStack item = new ItemStack(Material.POTION);
        p.apply(item);

        // Fire from each target
        for (LivingEntity target : targets)
        {
            ThrownPotion thrown = caster.launchProjectile(ThrownPotion.class);
            SkillAPI api = (SkillAPI)Bukkit.getPluginManager().getPlugin("SkillAPI");
            thrown.setMetadata(LEVEL, new FixedMetadataValue(api, level));
            thrown.setMetadata(MechanicListener.POTION_PROJECTILE, new FixedMetadataValue(api, this));
            thrown.setItem(item);
        }

        return targets.size() > 0;
    }

    /**
     * The callback for the projectiles that applies child components
     *
     * @param projectile projectile calling back for
     * @param hit        the entity hit by the projectile, if any
     */
    public void callback(ThrownPotion projectile, Collection<LivingEntity> hit)
    {
        ArrayList<LivingEntity> targets = new ArrayList<LivingEntity>(hit);
        String group = settings.getString(ALLY, "enemy").toLowerCase();
        boolean both = group.equals("both");
        boolean ally = group.equals("ally");
        LivingEntity caster = (LivingEntity)projectile.getShooter();
        for (int i = 0; i < targets.size(); i++)
        {
            if (!both && Protection.canAttack(caster, targets.get(i)) == ally)
            {
                targets.remove(i);
                i--;
            }
        }
        if (targets.size() == 0)
        {
            LivingEntity loc = new TempEntity(projectile.getLocation());
            targets.add(loc);
        }
        executeChildren((LivingEntity) projectile.getShooter(), projectile.getMetadata(LEVEL).get(0).asInt(), targets);
    }
}
