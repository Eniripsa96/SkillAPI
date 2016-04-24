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
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.enums.ManaCost;
import com.sucy.skill.api.event.PhysicalDamageEvent;
import com.sucy.skill.api.event.PlayerLandEvent;
import com.sucy.skill.api.event.SkillDamageEvent;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.player.PlayerSkill;
import com.sucy.skill.api.skills.PassiveSkill;
import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.api.skills.SkillShot;
import com.sucy.skill.cast.IIndicator;
import com.sucy.skill.dynamic.executors.*;
import com.sucy.skill.dynamic.mechanic.PassiveMechanic;
import com.sucy.skill.dynamic.mechanic.RepeatMechanic;
import com.sucy.skill.log.Logger;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A skill implementation for the Dynamic system
 */
public class DynamicSkill extends Skill implements SkillShot, PassiveSkill, Listener
{
    private final HashMap<Trigger, EffectComponent> components = new HashMap<Trigger, EffectComponent>();
    private final HashMap<String, EffectComponent>  attribKeys = new HashMap<String, EffectComponent>();
    private final HashMap<Integer, Integer>         active     = new HashMap<Integer, Integer>();

    private static final HashMap<Integer, HashMap<String, Object>> castData = new HashMap<Integer, HashMap<String, Object>>();

    private boolean cancel  = false;
    private boolean running = false;

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
     * Cancels the event causing a trigger to go off
     */
    public void cancelTrigger()
    {
        cancel = true;
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
     * Registers needed events for the skill, ignoring any unused events for efficiency
     *
     * @param plugin plugin reference
     */
    public void registerEvents(SkillAPI plugin)
    {
        PluginManager manager = plugin.getServer().getPluginManager();
        EventPriority p = EventPriority.HIGHEST;

        if (components.containsKey(Trigger.CROUCH))
            manager.registerEvent(PlayerToggleSneakEvent.class, this, p, CrouchExecutor.instance, plugin, true);
        if (components.containsKey(Trigger.DEATH))
            manager.registerEvent(EntityDeathEvent.class, this, p, CrouchExecutor.instance, plugin, true);
        if (components.containsKey(Trigger.ENVIRONMENT_DAMAGE))
            manager.registerEvent(EntityDamageEvent.class, this, p, EnvironmentExecutor.instance, plugin, true);
        if (components.containsKey(Trigger.KILL))
            manager.registerEvent(EntityDeathEvent.class, this, p, KillExecutor.instance, plugin, true);
        if (components.containsKey(Trigger.LAND))
            manager.registerEvent(PlayerLandEvent.class, this, p, LandExecutor.instance, plugin, true);
        if (components.containsKey(Trigger.LAUNCH))
            manager.registerEvent(ProjectileLaunchEvent.class, this, p, LaunchExecutor.instance, plugin, true);
        if (components.containsKey(Trigger.PHYSICAL_DAMAGE))
            manager.registerEvent(PhysicalDamageEvent.class, this, p, PhysicalDealtExecutor.instance, plugin, true);
        if (components.containsKey(Trigger.TOOK_PHYSICAL_DAMAGE))
            manager.registerEvent(PhysicalDamageEvent.class, this, p, PhysicalTakenExecutor.instance, plugin, true);
        if (components.containsKey(Trigger.SKILL_DAMAGE))
            manager.registerEvent(SkillDamageEvent.class, this, p, SkillDealtExecutor.instance, plugin, true);
        if (components.containsKey(Trigger.TOOK_SKILL_DAMAGE))
            manager.registerEvent(SkillDamageEvent.class, this, p, SkillTakenExecutor.instance, plugin, true);
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
     * Initializes the indicators for a skill.
     *
     * @param list   list to store indicators in
     * @param player player to base location on
     * @param level  the level of the skill to create for
     */
    @Override
    public void createPreview(List<IIndicator> list, Player player, int level)
    {
        list.clear();
        for (EffectComponent component : components.values())
            component.makeIndicators(list, player, player, level);
    }

    /**
     * Updates the positions of indicators for a skill.
     *
     * @param list   list to store indicators in
     * @param player player to base location on
     * @param level  the level of the skill to create for
     */
    @Override
    public void updateIndicators(List<IIndicator> list, Player player, int level)
    {
        createPreview(list, player, level);
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
     * @param caster owner of the skill
     * @param key    attribute key
     * @param level  skill level
     *
     * @return attribute value or 0 if invalid dynamic path
     */
    @Override
    protected Object getAttr(LivingEntity caster, String key, int level)
    {
        // Dynamic attribute paths use periods
        if (key.contains("."))
        {
            String[] path = key.split("\\.");
            String attr = path[1].toLowerCase();
            if (attribKeys.containsKey(path[0]) && attribKeys.get(path[0]).settings.has(attr))
            {
                return format(attribKeys.get(path[0]).attr(caster, attr, level, 0, true));
            }
            else
            {
                return 0;
            }
        }

        // Otherwise get the attribute normally
        else
        {
            return super.getAttr(caster, key, level);
        }
    }

    /**
     * Cancels firing projectiles when the launcher is stunned or disarmed.
     *
     * @param event event details
     */
    public void onLaunch(ProjectileLaunchEvent event)
    {
        if (running) return;

        EffectComponent component = components.get(Trigger.LAUNCH);
        if (component != null && event.getEntity().getShooter() instanceof LivingEntity)
        {
            LivingEntity shooter = (LivingEntity) event.getEntity().getShooter();
            if (!active.containsKey(shooter.getEntityId()))
                return;

            String type = component.getSettings().getString("type", "any").toUpperCase().replace(" ", "_");
            int level = active.get(shooter.getEntityId());
            if (active.containsKey(shooter.getEntityId())
                && (type.equals("ANY") || type.equals(event.getEntity().getType().name())))
            {
                getCastData(shooter).put("api-velocity", event.getEntity().getVelocity().length());
                trigger(shooter, shooter, level, Trigger.LAUNCH);
                if (cancel)
                {
                    event.setCancelled(true);
                    cancel = false;
                }
            }
        }
    }

    /**
     * Applies the death trigger effects
     *
     * @param event event details
     */
    public void onDeath(EntityDeathEvent event)
    {
        if (running) return;

        // Death trigger
        EffectComponent component = components.get(Trigger.DEATH);
        if (active.containsKey(event.getEntity().getEntityId()) && component != null)
        {
            boolean killer = component.getSettings().getString("killer", "false").equalsIgnoreCase("true");
            if (!killer || event.getEntity().getKiller() != null)
            {
                trigger(event.getEntity(), killer ? event.getEntity().getKiller() : event.getEntity(), active.get(event.getEntity().getEntityId()), Trigger.DEATH);
                cancel = false;
            }
        }
    }

    /**
     * Applies the kill trigger effects
     *
     * @param event event details
     */
    public void onKill(EntityDeathEvent event)
    {
        // Kill trigger
        Player player = event.getEntity().getKiller();
        if (player != null && active.containsKey(player.getEntityId()))
        {
            trigger(player, player, active.get(player.getEntityId()), Trigger.KILL);
            cancel = false;
        }
    }

    /**
     * Environmental damage trigger
     *
     * @param event event details
     */
    public void onEnvironmental(EntityDamageEvent event)
    {
        if (running) return;
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
                if (cancel)
                {
                    event.setCancelled(true);
                    cancel = false;
                }
            }
        }

    }

    /**
     * Applies physical damage taken triggers
     *
     * @param event event details
     */
    public void onPhysical(PhysicalDamageEvent event)
    {
        if (running) return;

        LivingEntity damager = event.getDamager();
        LivingEntity target = event.getTarget();
        boolean projectile = event.isProjectile();

        // Can't be null
        if (damager == null || target == null)
        {
            return;
        }

        EffectComponent component = components.get(Trigger.TOOK_PHYSICAL_DAMAGE);
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
                trigger(target, caster ? target : damager, active.get(target.getEntityId()), Trigger.TOOK_PHYSICAL_DAMAGE);
                if (cancel)
                {
                    event.setCancelled(true);
                    cancel = false;
                }
            }
        }
    }

    /**
     * Applies physical damage dealt trigger effects
     *
     * @param event event details
     */
    public void onDealtPhysical(PhysicalDamageEvent event)
    {
        LivingEntity damager = event.getDamager();
        LivingEntity target = event.getTarget();
        boolean projectile = event.isProjectile();

        // Can't be null
        if (damager == null || target == null)
        {
            return;
        }

        EffectComponent component = components.get(Trigger.PHYSICAL_DAMAGE);
        if (active.containsKey(damager.getEntityId()))
        {
            String type = component.settings.getString("type", "both").toLowerCase();
            boolean caster = !component.settings.getString("target", "true").toLowerCase().equals("false");
            double min = component.settings.getDouble("dmg-min");
            double max = component.settings.getDouble("dmg-max");

            if (event.getDamage() >= min && event.getDamage() <= max
                && (type.equals("both") || type.equals("projectile") == projectile))
            {
                getCastData(damager).put("api-dealt", event.getDamage());
                trigger(damager, caster ? damager : target, active.get(damager.getEntityId()), Trigger.PHYSICAL_DAMAGE);
                if (cancel)
                {
                    event.setCancelled(true);
                    cancel = false;
                }
            }
        }
    }

    /**
     * Applies skill damage taken trigger effects
     *
     * @param event event details
     */
    public void onSkillDamage(SkillDamageEvent event)
    {
        if (running) return;

        LivingEntity damager = event.getDamager();
        LivingEntity target = event.getTarget();

        // Skill received
        EffectComponent component = components.get(Trigger.TOOK_SKILL_DAMAGE);
        if (active.containsKey(target.getEntityId()))
        {
            boolean caster = !component.settings.getString("target", "true").toLowerCase().equals("false");
            double min = component.settings.getDouble("dmg-min");
            double max = component.settings.getDouble("dmg-max");

            if (event.getDamage() >= min && event.getDamage() <= max)
            {
                getCastData(target).put("api-taken", event.getDamage());
                trigger(target, caster ? target : damager, active.get(event.getTarget().getEntityId()), Trigger.TOOK_SKILL_DAMAGE);
                if (cancel)
                {
                    event.setCancelled(true);
                    cancel = false;
                }
            }
        }
    }

    /**
     * Applies skill damage dealt trigger effects
     *
     * @param event event details
     */
    public void onSkillDealt(SkillDamageEvent event)
    {
        LivingEntity damager = event.getDamager();
        LivingEntity target = event.getTarget();

        EffectComponent component = components.get(Trigger.SKILL_DAMAGE);
        if (active.containsKey(damager.getEntityId()))
        {
            boolean caster = !component.settings.getString("target", "true").toLowerCase().equals("false");
            double min = component.settings.getDouble("dmg-min");
            double max = component.settings.getDouble("dmg-max");

            if (event.getDamage() >= min && event.getDamage() <= max)
            {
                getCastData(damager).put("api-dealt", event.getDamage());
                trigger(damager, caster ? damager : target, active.get(damager.getEntityId()), Trigger.SKILL_DAMAGE);
                if (cancel)
                {
                    event.setCancelled(true);
                    cancel = false;
                }
            }
        }
    }

    /**
     * Applies crouch triggers
     *
     * @param event event details
     */
    public void onCrouch(PlayerToggleSneakEvent event)
    {
        if (running) return;

        EffectComponent component = components.get(Trigger.CROUCH);
        if (active.containsKey(event.getPlayer().getEntityId()))
        {
            String type = component.settings.getString("type", "start crouching");
            if (type.equalsIgnoreCase("both") || event.isSneaking() != type.equalsIgnoreCase("stop crouching"))
            {
                trigger(event.getPlayer(), event.getPlayer(), active.get(event.getPlayer().getEntityId()), Trigger.CROUCH);
                cancel = false;
            }
        }
    }

    /**
     * Land trigger
     *
     * @param event event details
     */
    public void onLand(PlayerLandEvent event)
    {
        if (running) return;

        EffectComponent component = components.get(Trigger.LAND);
        if (active.containsKey(event.getPlayer().getEntityId()) && component != null)
        {
            double minDistance = component.settings.getDouble("min-distance", 0);
            if (event.getDistance() >= minDistance)
            {
                getCastData(event.getPlayer()).put("api-distance", event.getDistance());
                trigger(event.getPlayer(), event.getPlayer(), active.get(event.getPlayer().getEntityId()), Trigger.LAND);
                cancel = false;
            }
        }
    }

    private boolean trigger(LivingEntity user, LivingEntity target, int level, Trigger trigger)
    {
        if (user != null && components.containsKey(trigger))
        {
            EffectComponent component = components.get(trigger);
            if (user instanceof Player)
            {
                PlayerData data = SkillAPI.getPlayerData((Player) user);
                PlayerSkill skill = data.getSkill(getName());
                boolean cd = component.getSettings().getBool("cooldown", false);
                boolean mana = component.getSettings().getBool("mana", false);
                if ((cd || mana) && !data.check(skill, cd, mana))
                    return false;

                if (cd)
                    skill.startCooldown();
                if (mana)
                    data.useMana(skill.getManaCost(), ManaCost.SKILL_CAST);
            }

            ArrayList<LivingEntity> targets = new ArrayList<LivingEntity>();
            targets.add(target);

            running = true;
            boolean result = component.execute(user, level, targets);
            running = false;
            return result;
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
                    ex.printStackTrace();
                    Logger.invalid("Invalid trigger for the skill \"" + getName() + "\" - \"" + key + "\"");
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
