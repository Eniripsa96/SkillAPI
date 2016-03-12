/**
 * SkillAPI
 * com.sucy.skill.dynamic.DynamicSkill
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
package com.sucy.skill.dynamic;

import com.rit.sucy.config.parse.DataSection;
import com.rit.sucy.text.TextFormatter;
import com.sucy.skill.api.event.PhysicalDamageEvent;
import com.sucy.skill.api.event.PlayerLandEvent;
import com.sucy.skill.api.event.SkillDamageEvent;
import com.sucy.skill.api.skills.PassiveSkill;
import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.api.skills.SkillShot;
import com.sucy.skill.dynamic.mechanic.PassiveMechanic;
import com.sucy.skill.dynamic.mechanic.RepeatMechanic;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A skill implementation for the Dynamic system
 */
public class DynamicSkill extends Skill implements SkillShot, PassiveSkill, Listener
{
    private final HashMap<Trigger, EffectComponent> components = new HashMap<Trigger, EffectComponent>();
    private final HashMap<String, EffectComponent>  attribKeys = new HashMap<String, EffectComponent>();
    private final HashMap<Integer, Integer>         active     = new HashMap<Integer, Integer>();

    private static final HashMap<Integer, HashMap<String, Object>> castData = new HashMap<Integer, HashMap<String, Object>>();

    /**
     * Initializes a new dynamic skill
     *
     * @param name name of the skill
     */
    public DynamicSkill(String name)
    {
        super(name, "Dynamic", Material.JACK_O_LANTERN, 1);
    }

    /**
     * Checks whether or not the dynamic skill can be cast
     *
     * @return true if can cast, false otherwise
     */
    public boolean canCast()
    {
        return components.containsKey(Trigger.CAST);
    }

    /**
     * Checks whether or not the caster's passives are currently active
     *
     * @param caster caster to check for
     *
     * @return true if active, false otherwise
     */
    public boolean isActive(LivingEntity caster)
    {
        return active.containsKey(caster.getEntityId());
    }

    /**
     * Retrieves the active level of the caster for the skill
     *
     * @param caster caster of the skill
     *
     * @return active level of the skill
     */
    public int getActiveLevel(LivingEntity caster)
    {
        return active.get(caster.getEntityId());
    }

    /**
     * Sets an attribute key for obtaining attributes used
     * in the skill indicator.
     *
     * @param key       key string
     * @param component component to grab attributes from
     */
    public void setAttribKey(String key, EffectComponent component)
    {
        attribKeys.put(key, component);
    }

    /**
     * Retrieves the cast data for the caster
     *
     * @param caster caster to get the data for
     *
     * @return cast data for the caster
     */
    public static HashMap<String, Object> getCastData(LivingEntity caster)
    {
        if (caster == null) return null;
        HashMap<String, Object> map = castData.get(caster.getEntityId());
        if (map == null)
        {
            map = new HashMap<String, Object>();
            map.put("caster", caster);
            castData.put(caster.getEntityId(), map);
        }
        return map;
    }

    /**
     * Clears any stored cast data for the entity
     *
     * @param entity entity to clear cast data for
     */
    public static void clearCastData(LivingEntity entity)
    {
        castData.remove(entity.getEntityId());
    }

    /**
     * Updates the skill effects
     *
     * @param user      user to refresh the effect for
     * @param prevLevel previous skill level
     * @param newLevel  new skill level
     */
    @Override
    public void update(LivingEntity user, int prevLevel, int newLevel)
    {
        active.put(user.getEntityId(), newLevel);
    }

    /**
     * Initializes any corresponding effects when the skill is unlocked/enabled
     *
     * @param user  user to initialize the effects for
     * @param level skill level
     */
    @Override
    public void initialize(LivingEntity user, int level)
    {
        trigger(user, user, level, Trigger.INITIALIZE);
        active.put(user.getEntityId(), level);
    }

    /**
     * Removes active users from the map
     *
     * @param user  user to stop the effects for
     * @param level skill level
     */
    @Override
    public void stopEffects(LivingEntity user, int level)
    {
        RepeatMechanic.stopTasks(user, getName());
        PassiveMechanic.stopTasks(user, getName());
        active.remove(user.getEntityId());

        trigger(user, user, 1, Trigger.CLEANUP);
    }

    /**
     * Casts the skill if applicable
     *
     * @param user  user of the skill
     * @param level skill level
     *
     * @return true if casted successfully, false if conditions weren't met or no effects are using the cast trigger
     */
    @Override
    public boolean cast(LivingEntity user, int level)
    {
        return trigger(user, user, level, Trigger.CAST);
    }

    /**
     * Retrieves the formatted name of an attribute which ignores the dynamic
     * path overhead.
     *
     * @param key attribute key
     *
     * @return formatted attribute name
     */
    @Override
    protected String getAttrName(String key)
    {
        if (key.contains("."))
        {
            return TextFormatter.format(key.substring(key.lastIndexOf('.') + 1));
        }
        else
        {
            return super.getAttrName(key);
        }
    }

    /**
     * Retrieves an attribute while supporting dynamic skill attribute paths.
     * Paths are set up by the "icon-key" setting in components. An invalid
     * path will instead return a value of 0. If a path is not provided, this
     * returns a normal attribute on the skill.
     *
     * @param key   attribute key
     * @param level skill level
     *
     * @return attribute value or 0 if invalid dynamic path
     */
    @Override
    protected Object getAttr(String key, int level)
    {
        // Dynamic attribute paths use periods
        if (key.contains("."))
        {
            String[] path = key.split("\\.");
            String attr = path[1].toLowerCase();
            if (attribKeys.containsKey(path[0]) && attribKeys.get(path[0]).settings.has(attr))
            {
                return attribKeys.get(path[0]).settings.getObj(attr, level);
            }
            else
            {
                return 0;
            }
        }

        // Otherwise get the attribute normally
        else
        {
            return super.getAttr(key, level);
        }
    }

    /**
     * Applies the death/kill trigger effects
     *
     * @param event event details
     */
    @EventHandler
    public void onDeath(EntityDeathEvent event)
    {
        // Death trigger
        if (active.containsKey(event.getEntity().getEntityId()))
        {
            getCastData(event.getEntity()).put("api-killer", event.getEntity().getKiller());
            trigger(event.getEntity(), event.getEntity(), active.get(event.getEntity().getEntityId()), Trigger.DEATH);
        }

        // Kill trigger
        Player player = event.getEntity().getKiller();
        if (player != null && active.containsKey(player.getEntityId()))
        {
            trigger(player, player, active.get(player.getEntityId()), Trigger.KILL);
        }
    }

    /**
     * Environmental damage trigger
     *
     * @param event event details
     */
    @EventHandler
    public void onEnvironmental(EntityDamageEvent event)
    {
        if (!(event.getEntity() instanceof LivingEntity)) return;

        EffectComponent component = components.get(Trigger.ENVIRONMENT_DAMAGE);
        LivingEntity target = (LivingEntity) event.getEntity();
        if (component != null && active.containsKey(target.getEntityId()))
        {
            String name = component.getSettings().getString("type", "").toUpperCase().replace(' ', '_');
            if (event.getCause().name().equals(name))
            {
                getCastData(target).put("api-taken", event.getDamage());
                trigger(target, target, active.get(target.getEntityId()), Trigger.ENVIRONMENT_DAMAGE);
            }
        }

    }

    /**
     * Applies physical damage triggers
     *
     * @param event event details
     */
    @EventHandler
    public void onPhysical(PhysicalDamageEvent event)
    {
        LivingEntity damager = event.getDamager();
        LivingEntity target = event.getTarget();
        boolean projectile = event.isProjectile();

        // Can't be null
        if (damager == null || target == null)
        {
            return;
        }

        EffectComponent component;

        // Physical receieved
        component = components.get(Trigger.TOOK_PHYSICAL_DAMAGE);
        if (component != null && active.containsKey(target.getEntityId()))
        {
            String type = component.settings.getString("type", "both").toLowerCase();
            boolean caster = !component.settings.getString("target", "true").toLowerCase().equals("false");
            double min = component.settings.getDouble("dmg-min");
            double max = component.settings.getDouble("dmg-max");

            if (event.getDamage() >= min && event.getDamage() <= max
                && (type.equals("both") || (type.equals("projectile") == projectile)))
            {
                getCastData(target).put("api-taken", event.getDamage());
                if (caster)
                {
                    trigger(target, target, active.get(target.getEntityId()), Trigger.TOOK_PHYSICAL_DAMAGE);
                }
                else
                {
                    trigger(target, damager, active.get(target.getEntityId()), Trigger.TOOK_PHYSICAL_DAMAGE);
                }
            }
        }

        // Physical dealt
        component = components.get(Trigger.PHYSICAL_DAMAGE);
        if (component != null && active.containsKey(damager.getEntityId()))
        {
            String type = component.settings.getString("type", "both").toLowerCase();
            boolean caster = !component.settings.getString("target", "true").toLowerCase().equals("false");
            double min = component.settings.getDouble("dmg-min");
            double max = component.settings.getDouble("dmg-max");

            if (event.getDamage() >= min && event.getDamage() <= max
                && (type.equals("both") || type.equals("projectile") == projectile))
            {
                getCastData(damager).put("api-dealt", event.getDamage());
                if (caster)
                {
                    trigger(damager, damager, active.get(damager.getEntityId()), Trigger.PHYSICAL_DAMAGE);
                }
                else
                {
                    trigger(damager, target, active.get(damager.getEntityId()), Trigger.PHYSICAL_DAMAGE);
                }
            }
        }
    }

    /**
     * Applies skill damage triggers
     *
     * @param event event details
     */
    @EventHandler
    public void onSkillDamage(SkillDamageEvent event)
    {
        LivingEntity damager = event.getDamager();
        LivingEntity target = event.getTarget();

        // Skill received
        EffectComponent component = components.get(Trigger.TOOK_SKILL_DAMAGE);
        if (component != null && active.containsKey(target.getEntityId()))
        {
            boolean caster = !component.settings.getString("target", "true").toLowerCase().equals("false");
            double min = component.settings.getDouble("dmg-min");
            double max = component.settings.getDouble("dmg-max");

            if (event.getDamage() >= min && event.getDamage() <= max)
            {
                getCastData(target).put("api-taken", event.getDamage());
                if (caster)
                {
                    trigger(target, target, active.get(event.getTarget().getEntityId()), Trigger.TOOK_SKILL_DAMAGE);
                }
                else
                {
                    trigger(target, damager, active.get(event.getTarget().getEntityId()), Trigger.TOOK_SKILL_DAMAGE);
                }
            }
        }

        // Skill dealt
        component = components.get(Trigger.SKILL_DAMAGE);
        if (component != null && active.containsKey(damager.getEntityId()))
        {
            boolean caster = !component.settings.getString("target", "true").toLowerCase().equals("false");
            double min = component.settings.getDouble("dmg-min");
            double max = component.settings.getDouble("dmg-max");

            if (event.getDamage() >= min && event.getDamage() <= max)
            {
                getCastData(damager).put("api-dealt", event.getDamage());
                if (caster)
                {
                    trigger(damager, damager, active.get(damager.getEntityId()), Trigger.SKILL_DAMAGE);
                }
                else
                {
                    trigger(damager, target, active.get(damager.getEntityId()), Trigger.SKILL_DAMAGE);
                }
            }
        }
    }

    /**
     * Applies crouch triggers
     *
     * @param event event details
     */
    @EventHandler
    public void onCrouch(PlayerToggleSneakEvent event)
    {
        EffectComponent component = components.get(Trigger.CROUCH);
        if (component != null && active.containsKey(event.getPlayer().getEntityId()))
        {
            if (event.isSneaking() != component.settings.getString("type", "start crouching").toLowerCase().equals("stop crouching"))
            {
                trigger(event.getPlayer(), event.getPlayer(), active.get(event.getPlayer().getEntityId()), Trigger.CROUCH);
            }
        }
    }

    /**
     * Land trigger
     *
     * @param event event details
     */
    @EventHandler
    public void onLand(PlayerLandEvent event)
    {
        EffectComponent component = components.get(Trigger.LAND);
        if (active.containsKey(event.getPlayer().getEntityId()) && component != null)
        {
            double minDistance = component.settings.getDouble("min-distance", 0);
            if (event.getDistance() >= minDistance)
            {
                trigger(event.getPlayer(), event.getPlayer(), active.get(event.getPlayer().getEntityId()), Trigger.LAND);
            }
        }
    }

    private boolean trigger(LivingEntity user, LivingEntity target, int level, Trigger trigger)
    {
        if (user != null && components.containsKey(trigger))
        {
            ArrayList<LivingEntity> targets = new ArrayList<LivingEntity>();
            targets.add(target);

            return components.get(trigger).execute(user, level, targets);
        }
        return false;
    }

    /**
     * Loads dynamic components in addition to the normal values
     *
     * @param config config data to load from
     */
    @Override
    public void load(DataSection config)
    {
        DataSection triggers = config.getSection("components");
        if (triggers != null)
        {
            for (String key : triggers.keys())
            {
                try
                {
                    Trigger trigger = Trigger.valueOf(key.toUpperCase().replace(' ', '_').replaceAll("-.+", ""));
                    EffectComponent component = trigger.getComponent();
                    component.load(this, triggers.getSection(key));
                    components.put(trigger, component);
                }
                catch (Exception ex)
                {
                    // Invalid trigger
                    Bukkit.getLogger().warning("Invalid trigger for the skill \"" + getName() + "\" - \"" + key + "\"");
                }
            }
        }

        super.load(config);
    }

    /**
     * Saves the skill back to the config, appending component data
     * on top of the normal skill data
     *
     * @param config config to save to
     */
    @Override
    public void save(DataSection config)
    {
        super.save(config);
        DataSection triggers = config.createSection("components");
        for (Trigger trigger : components.keySet())
        {
            components.get(trigger).save(triggers.createSection(TextFormatter.format(trigger.name())));
        }
    }
}
