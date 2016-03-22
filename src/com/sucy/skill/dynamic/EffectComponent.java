/**
 * SkillAPI
 * com.sucy.skill.dynamic.EffectComponent
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
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.Settings;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.player.PlayerSkill;
import com.sucy.skill.dynamic.condition.*;
import com.sucy.skill.dynamic.mechanic.*;
import com.sucy.skill.dynamic.target.*;
import com.sucy.skill.log.Logger;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

/**
 * A component for dynamic skills which takes care of one effect
 */
public abstract class EffectComponent
{
    protected static final Pattern NUMBER = Pattern.compile("-?[0-9]+(\\.[0-9]+)?");
    protected static final Pattern RANGE  = Pattern.compile("-?[0-9]+(\\.[0-9]+)?-{1,2}[0-9]+(\\.[0-9]+)?");

    private static final String ICON_KEY   = "icon-key";
    private static final String COUNTS_KEY = "counts";

    public final ArrayList<EffectComponent> children = new ArrayList<EffectComponent>();

    /**
     * The settings for the component
     */
    protected final Settings settings = new Settings();

    /**
     * Parent class of the component
     */
    protected DynamicSkill skill;

    /**
     * Key of the component for the config
     */
    private String key;

    /**
     * Type of the component
     */
    private String type = "trigger";

    /**
     * Retrieves the config key for the component
     *
     * @return config key of the component
     */
    public String getKey()
    {
        return key;
    }

    /**
     * Retrieves the type of the component
     *
     * @return component type
     */
    public String getType()
    {
        return type;
    }

    /**
     * Retrieves the settings of the dynamic component
     *
     * @return settings of the dynamic component
     */
    public Settings getSettings()
    {
        return settings;
    }

    /**
     * Retrieves an attribute value while applying attribute
     * data if enabled and a player is using the skill
     *
     * @param caster   caster of the skill
     * @param key      key of the value to grab
     * @param level    level of the skill
     * @param fallback default value for the attribute
     * @param self     whether or not the skill is targeting the caster
     *
     * @return the value with attribute modifications if applicable
     */
    protected double attr(LivingEntity caster, String key, int level, double fallback, boolean self)
    {
        double value = getNum(caster, key + "-base", fallback) + (level - 1) * getNum(caster, key + "-scale", 0);

        // Apply global modifiers
        if (SkillAPI.getSettings().isAttributesEnabled() && caster instanceof Player)
        {
            PlayerData data = SkillAPI.getPlayerData((Player) caster);
            value = data.scaleDynamic(this, key, value, self);
        }

        return value;
    }

    /**
     * Retrieves a numerical value while using non-numerical values as
     * keys for the cast data. If the value doesn't exist, this will
     * return the default value. If it is a key that doesn't have an
     * attached value, it will return 0. Otherwise, it will return
     * the appropriate value.
     *
     * @param caster   the caster of the skill
     * @param key      key of the value
     * @param fallback fallback value in case the settings don't have it
     *
     * @return the settings value or, if not a number, the cast data value
     */
    protected double getNum(LivingEntity caster, String key, double fallback)
    {
        String val = settings.getString(key);
        if (val == null)
        {
            return fallback;
        }
        else if (NUMBER.matcher(val).matches())
        {
            return Double.parseDouble(val);
        }
        else if (RANGE.matcher(val).matches())
        {
            int mid = val.indexOf('-', 1);
            double min = Double.parseDouble(val.substring(0, mid));
            double max = Double.parseDouble(val.substring(mid + 1));
            return Math.random() * (max - min) + min;
        }
        else
        {
            HashMap<String, Object> map = DynamicSkill.getCastData(caster);
            if (map.containsKey(val))
            {
                String mapVal = map.get(val).toString();
                if (NUMBER.matcher(mapVal).matches())
                {
                    return Double.parseDouble(mapVal);
                }
            }
            return 0;
        }
    }

    /**
     * Executes the children of the component using the given targets
     *
     * @param caster  caster of the skill
     * @param level   level of the skill
     * @param targets targets to execute on
     *
     * @return true if executed, false if conditions not met
     */
    protected boolean executeChildren(LivingEntity caster, int level, List<LivingEntity> targets)
    {
        boolean worked = false;
        for (EffectComponent child : children)
        {
            boolean counts = !child.settings.getString(COUNTS_KEY, "true").toLowerCase().equals("false");
            worked = (child.execute(caster, level, targets) && counts) || worked;
        }
        return worked;
    }

    /**
     * Gets the skill data for the caster
     *
     * @param caster caster of the skill
     *
     * @return skill data for the caster or null if not found
     */
    protected PlayerSkill getSkillData(LivingEntity caster)
    {
        if (caster instanceof Player)
        {
            return SkillAPI.getPlayerData((Player) caster).getSkill(skill.getName());
        }
        else
        {
            return null;
        }
    }

    /**
     * Executes the component (to be implemented)
     *
     * @param caster  caster of the skill
     * @param level   level of the skill
     * @param targets targets to execute on
     *
     * @return true if executed, false if conditions not met
     */
    public abstract boolean execute(LivingEntity caster, int level, List<LivingEntity> targets);

    private static final String TYPE = "type";

    /**
     * Saves the component and its children to the config
     *
     * @param config config to save to
     */
    public void save(DataSection config)
    {
        config.set(TYPE, type);
        settings.save(config.createSection("data"));
        DataSection children = config.createSection("children");
        for (EffectComponent child : this.children)
        {
            child.save(children.createSection(child.key));
        }
    }

    /**
     * Loads component data from the configuration
     *
     * @param skill  owning skill of the component
     * @param config config data to load from
     */
    public void load(DynamicSkill skill, DataSection config)
    {
        this.skill = skill;
        if (config == null)
        {
            return;
        }
        settings.load(config.getSection("data"));
        if (settings.has(ICON_KEY))
        {
            String key = settings.getString(ICON_KEY);
            if (!key.equals(""))
            {
                skill.setAttribKey(key, this);
            }
        }

        DataSection children = config.getSection("children");
        if (children != null)
        {
            for (String key : children.keys())
            {
                String type = children.getSection(key).getString(TYPE, "missing").toLowerCase();
                HashMap<String, Class<? extends EffectComponent>> map;
                if (type.equals("target"))
                {
                    map = targets;
                }
                else if (type.equals("condition"))
                {
                    map = conditions;
                }
                else if (type.equals("mechanic"))
                {
                    map = mechanics;
                }
                else
                {
                    Logger.invalid("Invalid component type - " + type);
                    continue;
                }
                String mkey = key.toLowerCase().replaceAll("-.+", "");
                if (map.containsKey(mkey))
                {
                    try
                    {
                        EffectComponent child = map.get(mkey).newInstance();
                        child.key = key;
                        child.type = type;
                        child.load(skill, children.getSection(key));
                        this.children.add(child);
                    }
                    catch (Exception ex)
                    {
                        // Failed to create the component, just don't add it
                        Logger.bug("Failed to create " + type + " component: " + key);
                    }
                }
                else
                {
                    Logger.invalid("Invalid " + type + " component: " + key);
                }
            }
        }
    }

    private static final HashMap<String, Class<? extends EffectComponent>> targets = new HashMap<String, Class<? extends EffectComponent>>()
    {{
            put("area", AreaTarget.class);
            put("cone", ConeTarget.class);
            put("linear", LinearTarget.class);
            put("location", LocationTarget.class);
            put("nearest", NearestTarget.class);
            put("offset", OffsetTarget.class);
            put("remember", RememberTarget.class);
            put("self", SelfTarget.class);
            put("single", SingleTarget.class);
        }};

    private static final HashMap<String, Class<? extends EffectComponent>> conditions = new HashMap<String, Class<? extends EffectComponent>>()
    {{
            put("attribute", AttributeCondition.class);
            put("biome", BiomeCondition.class);
            put("block", BlockCondition.class);
            put("chance", ChanceCondition.class);
            put("class", ClassCondition.class);
            put("class level", ClassLevelCondition.class);
            put("combat", CombatCondition.class);
            put("direction", DirectionCondition.class);
            put("elevation", ElevationCondition.class);
            put("fire", FireCondition.class);
            put("flag", FlagCondition.class);
            put("health", HealthCondition.class);
            put("inventory", InventoryCondition.class);
            put("item", ItemCondition.class);
            put("light", LightCondition.class);
            put("lore", LoreCondition.class);
            put("mana", ManaCondition.class);
            put("name", NameCondition.class);
            put("potion", PotionCondition.class);
            put("skill level", SkillLevelCondition.class);
            put("status", StatusCondition.class);
            put("time", TimeCondition.class);
            put("tool", ToolCondition.class);
            put("value", ValueCondition.class);
            put("water", WaterCondition.class);
        }};

    private static final HashMap<String, Class<? extends EffectComponent>> mechanics = new HashMap<String, Class<? extends EffectComponent>>()
    {{
            put("attribute", AttributeMechanic.class);
            put("block", BlockMechanic.class);
            put("cancel", CancelMechanic.class);
            put("channel", ChannelMechanic.class);
            put("cleanse", CleanseMechanic.class);
            put("command", CommandMechanic.class);
            put("cooldown", CooldownMechanic.class);
            put("damage", DamageMechanic.class);
            put("damage buff", DamageBuffMechanic.class);
            put("damage lore", DamageLoreMechanic.class);
            put("defense buff", DefenseBuffMechanic.class);
            put("delay", DelayMechanic.class);
            put("explosion", ExplosionMechanic.class);
            put("fire", FireMechanic.class);
            put("flag", FlagMechanic.class);
            put("flag clear", FlagClearMechanic.class);
            put("flag toggle", FlagToggleMechanic.class);
            put("heal", HealMechanic.class);
            put("immunity", ImmunityMechanic.class);
            put("interrupt", InterruptMechanic.class);
            put("item", ItemMechanic.class);
            put("item projectile", ItemProjectileMechanic.class);
            put("item remove", ItemRemoveMechanic.class);
            put("launch", LaunchMechanic.class);
            put("lightning", LightningMechanic.class);
            put("mana", ManaMechanic.class);
            put("message", MessageMechanic.class);
            put("particle", ParticleMechanic.class);
            put("particle animation", ParticleAnimationMechanic.class);
            put("particle projectile", ParticleProjectileMechanic.class);
            put("passive", PassiveMechanic.class);
            put("permission", PermissionMechanic.class);
            put("potion", PotionMechanic.class);
            put("potion projectile", PotionProjectileMechanic.class);
            put("projectile", ProjectileMechanic.class);
            put("purge", PurgeMechanic.class);
            put("push", PushMechanic.class);
            put("remember", RememberTargetsMechanic.class);
            put("repeat", RepeatMechanic.class);
            put("speed", SpeedMechanic.class);
            put("sound", SoundMechanic.class);
            put("status", StatusMechanic.class);
            put("value add", ValueAddMechanic.class);
            put("value attribute", ValueAttributeMechanic.class);
            put("value location", ValueLocationMechanic.class);
            put("value lore", ValueLoreMechanic.class);
            put("value multiply", ValueMultiplyMechanic.class);
            put("value random", ValueRandomMechanic.class);
            put("value set", ValueSetMechanic.class);
            put("warp", WarpMechanic.class);
            put("warp location", WarpLocMechanic.class);
            put("warp random", WarpRandomMechanic.class);
            put("warp swap", WarpSwapMechanic.class);
            put("warp target", WarpTargetMechanic.class);
            put("warp value", WarpValueMechanic.class);
            put("wolf", WolfMechanic.class);
        }};
}
