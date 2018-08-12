/**
 * SkillAPI
 * com.sucy.skill.dynamic.mechanic.PotionProjectileMechanic
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

import com.rit.sucy.version.VersionManager;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.dynamic.TempEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LingeringPotion;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.sucy.skill.listener.MechanicListener.POTION_PROJECTILE;
import static com.sucy.skill.listener.MechanicListener.SKILL_CASTER;
import static com.sucy.skill.listener.MechanicListener.SKILL_LEVEL;

/**
 * Heals each target
 */
public class PotionProjectileMechanic extends MechanicComponent
{
    private static final String POTION = "type";
    private static final String ALLY   = "group";
    private static final String LINGER = "linger";

    @Override
    public String getKey() {
        return "potion projectile";
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
        String potion = settings.getString(POTION, "slowness").toUpperCase().replace(" ", "_");
        boolean linger = settings.getString(LINGER, "false").toLowerCase().equals("true") && VersionManager.isVersionAtLeast(
                VersionManager.V1_9_0);
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
        ItemStack item;
        try
        {
            item = new ItemStack(Material.valueOf(linger ? "LINGERING_POTION" : "SPLASH_POTION"));
            Field meta = ItemStack.class.getDeclaredField("meta");
            meta.setAccessible(true);
            PotionMeta potionMeta = (PotionMeta) item.getItemMeta();
            potionMeta.setDisplayName("lol");
            meta.set(item, potionMeta);
        }
        catch (Exception ex)
        {
            item = new ItemStack(Material.POTION);
        }
        p.apply(item);

        // Fire from each target
        for (LivingEntity target : targets)
        {
            ThrownPotion thrown = target.launchProjectile(linger ? LingeringPotion.class : ThrownPotion.class);
            SkillAPI.setMeta(thrown, SKILL_LEVEL, level);
            SkillAPI.setMeta(thrown, SKILL_CASTER, caster);
            SkillAPI.setMeta(thrown, POTION_PROJECTILE, this);
            thrown.setItem(item);
        }

        return targets.size() > 0;
    }

    /**
     * The callback for the projectiles that applies child components
     *
     * @param entity potion effect
     * @param hit    the entity hit by the projectile, if any
     */
    public void callback(Entity entity, Collection<LivingEntity> hit)
    {
        ArrayList<LivingEntity> targets = new ArrayList<LivingEntity>(hit);
        String group = settings.getString(ALLY, "enemy").toLowerCase();
        boolean both = group.equals("both");
        boolean ally = group.equals("ally");
        LivingEntity caster = (LivingEntity) SkillAPI.getMeta(entity, SKILL_CASTER);
        int level = SkillAPI.getMetaInt(entity, SKILL_LEVEL);
        Location loc = entity.getLocation();
        for (int i = 0; i < targets.size(); i++)
        {
            if (!both && SkillAPI.getSettings().canAttack(caster, targets.get(i)) == ally)
            {
                targets.remove(i);
                i--;
            }
        }
        if (targets.size() == 0)
        {
            LivingEntity locTarget = new TempEntity(loc);
            targets.add(locTarget);
        }
        executeChildren(caster, level, targets);
    }
}
