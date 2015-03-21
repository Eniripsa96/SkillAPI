package com.sucy.skill.dynamic;

import com.rit.sucy.text.TextFormatter;
import com.sucy.skill.api.event.PhysicalDamageEvent;
import com.sucy.skill.api.event.PlayerLandEvent;
import com.sucy.skill.api.event.SkillDamageEvent;
import com.sucy.skill.api.skills.PassiveSkill;
import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.api.skills.SkillShot;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
        active.remove(user.getEntityId());
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
     * Applies the death trigger effects
     *
     * @param event event details
     */
    @EventHandler
    public void onDeath(EntityDeathEvent event)
    {
        if (active.containsKey(event.getEntity().getEntityId()))
        {
            trigger(event.getEntity(), event.getEntity(), active.get(event.getEntity().getEntityId()), Trigger.DEATH);
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

        // Physical receieved
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
        if (active.containsKey(event.getPlayer().getEntityId()))
        {
            trigger(event.getPlayer(), event.getPlayer(), active.get(event.getPlayer().getEntityId()), Trigger.LAND);
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
    public void load(ConfigurationSection config)
    {
        ConfigurationSection triggers = config.getConfigurationSection("components");
        if (triggers != null)
        {
            for (String key : triggers.getKeys(false))
            {
                try
                {
                    Trigger trigger = Trigger.valueOf(key.toUpperCase().replace(' ', '_').replaceAll("-.+", ""));
                    EffectComponent component = trigger.getComponent();
                    component.load(this, triggers.getConfigurationSection(key));
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
    public void save(ConfigurationSection config)
    {
        super.save(config);
        ConfigurationSection triggers = config.createSection("components");
        for (Trigger trigger : components.keySet())
        {
            components.get(trigger).save(triggers.createSection(TextFormatter.format(trigger.name())));
        }
    }
}
