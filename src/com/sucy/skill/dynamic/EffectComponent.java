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
import com.rit.sucy.mobs.MobManager;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.Settings;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.player.PlayerSkill;
import com.sucy.skill.cast.IIndicator;
import com.sucy.skill.cast.IndicatorType;
import com.sucy.skill.dynamic.condition.ArmorCondition;
import com.sucy.skill.dynamic.condition.AttributeCondition;
import com.sucy.skill.dynamic.condition.BiomeCondition;
import com.sucy.skill.dynamic.condition.BlockCondition;
import com.sucy.skill.dynamic.condition.CastLevelCondition;
import com.sucy.skill.dynamic.condition.CeilingCondition;
import com.sucy.skill.dynamic.condition.ChanceCondition;
import com.sucy.skill.dynamic.condition.ClassCondition;
import com.sucy.skill.dynamic.condition.ClassLevelCondition;
import com.sucy.skill.dynamic.condition.CombatCondition;
import com.sucy.skill.dynamic.condition.CrouchCondition;
import com.sucy.skill.dynamic.condition.DirectionCondition;
import com.sucy.skill.dynamic.condition.ElevationCondition;
import com.sucy.skill.dynamic.condition.ElseCondition;
import com.sucy.skill.dynamic.condition.EntityTypeCondition;
import com.sucy.skill.dynamic.condition.FireCondition;
import com.sucy.skill.dynamic.condition.FlagCondition;
import com.sucy.skill.dynamic.condition.HealthCondition;
import com.sucy.skill.dynamic.condition.InventoryCondition;
import com.sucy.skill.dynamic.condition.ItemCondition;
import com.sucy.skill.dynamic.condition.LightCondition;
import com.sucy.skill.dynamic.condition.LoreCondition;
import com.sucy.skill.dynamic.condition.ManaCondition;
import com.sucy.skill.dynamic.condition.NameCondition;
import com.sucy.skill.dynamic.condition.OffhandCondition;
import com.sucy.skill.dynamic.condition.PermissionCondition;
import com.sucy.skill.dynamic.condition.PotionCondition;
import com.sucy.skill.dynamic.condition.SkillLevelCondition;
import com.sucy.skill.dynamic.condition.SlotCondition;
import com.sucy.skill.dynamic.condition.StatusCondition;
import com.sucy.skill.dynamic.condition.TimeCondition;
import com.sucy.skill.dynamic.condition.ToolCondition;
import com.sucy.skill.dynamic.condition.ValueCondition;
import com.sucy.skill.dynamic.condition.WaterCondition;
import com.sucy.skill.dynamic.mechanic.AttributeMechanic;
import com.sucy.skill.dynamic.mechanic.BlockMechanic;
import com.sucy.skill.dynamic.mechanic.BuffMechanic;
import com.sucy.skill.dynamic.mechanic.CancelMechanic;
import com.sucy.skill.dynamic.mechanic.ChannelMechanic;
import com.sucy.skill.dynamic.mechanic.CleanseMechanic;
import com.sucy.skill.dynamic.mechanic.CommandMechanic;
import com.sucy.skill.dynamic.mechanic.CooldownMechanic;
import com.sucy.skill.dynamic.mechanic.DamageBuffMechanic;
import com.sucy.skill.dynamic.mechanic.DamageLoreMechanic;
import com.sucy.skill.dynamic.mechanic.DamageMechanic;
import com.sucy.skill.dynamic.mechanic.DefenseBuffMechanic;
import com.sucy.skill.dynamic.mechanic.DelayMechanic;
import com.sucy.skill.dynamic.mechanic.DisguiseMechanic;
import com.sucy.skill.dynamic.mechanic.DurabilityMechanic;
import com.sucy.skill.dynamic.mechanic.ExplosionMechanic;
import com.sucy.skill.dynamic.mechanic.FireMechanic;
import com.sucy.skill.dynamic.mechanic.FlagClearMechanic;
import com.sucy.skill.dynamic.mechanic.FlagMechanic;
import com.sucy.skill.dynamic.mechanic.FlagToggleMechanic;
import com.sucy.skill.dynamic.mechanic.FoodMechanic;
import com.sucy.skill.dynamic.mechanic.ForgetTargetsMechanic;
import com.sucy.skill.dynamic.mechanic.HealMechanic;
import com.sucy.skill.dynamic.mechanic.HealthSetMechanic;
import com.sucy.skill.dynamic.mechanic.HeldItemMechanic;
import com.sucy.skill.dynamic.mechanic.ImmunityMechanic;
import com.sucy.skill.dynamic.mechanic.InterruptMechanic;
import com.sucy.skill.dynamic.mechanic.ItemMechanic;
import com.sucy.skill.dynamic.mechanic.ItemProjectileMechanic;
import com.sucy.skill.dynamic.mechanic.ItemRemoveMechanic;
import com.sucy.skill.dynamic.mechanic.LaunchMechanic;
import com.sucy.skill.dynamic.mechanic.LightningMechanic;
import com.sucy.skill.dynamic.mechanic.ManaMechanic;
import com.sucy.skill.dynamic.mechanic.MessageMechanic;
import com.sucy.skill.dynamic.mechanic.ParticleAnimationMechanic;
import com.sucy.skill.dynamic.mechanic.ParticleEffectMechanic;
import com.sucy.skill.dynamic.mechanic.ParticleMechanic;
import com.sucy.skill.dynamic.mechanic.ParticleProjectileMechanic;
import com.sucy.skill.dynamic.mechanic.PassiveMechanic;
import com.sucy.skill.dynamic.mechanic.PermissionMechanic;
import com.sucy.skill.dynamic.mechanic.PotionMechanic;
import com.sucy.skill.dynamic.mechanic.PotionProjectileMechanic;
import com.sucy.skill.dynamic.mechanic.ProjectileMechanic;
import com.sucy.skill.dynamic.mechanic.PurgeMechanic;
import com.sucy.skill.dynamic.mechanic.PushMechanic;
import com.sucy.skill.dynamic.mechanic.RememberTargetsMechanic;
import com.sucy.skill.dynamic.mechanic.RepeatMechanic;
import com.sucy.skill.dynamic.mechanic.SoundMechanic;
import com.sucy.skill.dynamic.mechanic.SpeedMechanic;
import com.sucy.skill.dynamic.mechanic.StatusMechanic;
import com.sucy.skill.dynamic.mechanic.TauntMechanic;
import com.sucy.skill.dynamic.mechanic.TriggerMechanic;
import com.sucy.skill.dynamic.mechanic.ValueAddMechanic;
import com.sucy.skill.dynamic.mechanic.ValueAttributeMechanic;
import com.sucy.skill.dynamic.mechanic.ValueCopyMechanic;
import com.sucy.skill.dynamic.mechanic.ValueDistanceMechanic;
import com.sucy.skill.dynamic.mechanic.ValueHealthMechanic;
import com.sucy.skill.dynamic.mechanic.ValueLocationMechanic;
import com.sucy.skill.dynamic.mechanic.ValueLoreMechanic;
import com.sucy.skill.dynamic.mechanic.ValueLoreSlotMechanic;
import com.sucy.skill.dynamic.mechanic.ValueManaMechanic;
import com.sucy.skill.dynamic.mechanic.ValueMultiplyMechanic;
import com.sucy.skill.dynamic.mechanic.ValuePlaceholderMechanic;
import com.sucy.skill.dynamic.mechanic.ValueRandomMechanic;
import com.sucy.skill.dynamic.mechanic.ValueSetMechanic;
import com.sucy.skill.dynamic.mechanic.WarpLocMechanic;
import com.sucy.skill.dynamic.mechanic.WarpMechanic;
import com.sucy.skill.dynamic.mechanic.WarpRandomMechanic;
import com.sucy.skill.dynamic.mechanic.WarpSwapMechanic;
import com.sucy.skill.dynamic.mechanic.WarpTargetMechanic;
import com.sucy.skill.dynamic.mechanic.WarpValueMechanic;
import com.sucy.skill.dynamic.mechanic.WolfMechanic;
import com.sucy.skill.dynamic.target.AreaTarget;
import com.sucy.skill.dynamic.target.ConeTarget;
import com.sucy.skill.dynamic.target.LinearTarget;
import com.sucy.skill.dynamic.target.LocationTarget;
import com.sucy.skill.dynamic.target.NearestTarget;
import com.sucy.skill.dynamic.target.OffsetTarget;
import com.sucy.skill.dynamic.target.RememberTarget;
import com.sucy.skill.dynamic.target.SelfTarget;
import com.sucy.skill.dynamic.target.SingleTarget;
import com.sucy.skill.log.Logger;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A component for dynamic skills which takes care of one effect
 */
public abstract class EffectComponent
{
    private static final String ICON_KEY   = "icon-key";
    private static final String COUNTS_KEY = "counts";

    private static boolean passed;

    /**
     * Child components
     */
    public final ArrayList<EffectComponent> children = new ArrayList<>();

    /**
     * The settings for the component
     */
    protected final Settings settings = new Settings();

    /**
     * Parent class of the component
     */
    protected DynamicSkill skill;

    /**
     * Type of indicators to show
     */
    protected IndicatorType indicatorType;

    /**
     * Whether or not the component has preview effects
     */
    public boolean hasEffect;

    private String key;
    private String name;
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

    public String getName() {
        return name;
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
     * Checks whether or not the component or its children have an effect
     *
     * @return true if has an effect, false otherwise
     */
    private boolean hasEffect()
    {
        if (indicatorType != IndicatorType.NONE)
            return true;

        for (EffectComponent child : children)
            if (child.hasEffect())
                return true;

        return false;
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
        double base = getNum(caster, key + "-base", fallback);
        double scale = getNum(caster, key + "-scale", 0);
        double value = base + (level - 1) * scale;

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
            return fallback;

        try
        {
            return Double.parseDouble(val);
        }
        catch (Exception ex) { /* Not a number */ }

        HashMap<String, Object> map = DynamicSkill.getCastData(caster);
        if (map.containsKey(val))
        {
            String mapVal = map.get(val).toString();
            try
            {
                return Double.parseDouble(mapVal);
            }
            catch (Exception ex) { /* Not a number */ }
        }

        try
        {
            int mid = val.indexOf('-', 1);
            double min = Double.parseDouble(val.substring(0, mid));
            double max = Double.parseDouble(val.substring(mid + 1));
            return Math.random() * (max - min) + min;
        }
        catch (Exception ex) { /* Not a range */ }

        return 0;
    }

    /**
     * Checks whether or not the last component passed or not
     *
     * @return true if passed, false otherwise
     */
    protected boolean lastPassed()
    {
        return passed;
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
        if (targets.isEmpty()) {
            return false;
        }

        boolean worked = false;
        for (EffectComponent child : children)
        {
            boolean counts = !child.settings.getString(COUNTS_KEY, "true").toLowerCase().equals("false");
            passed = child.execute(caster, level, targets);
            worked = (passed && counts) || worked;
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

    protected String filter(LivingEntity caster, LivingEntity target, String text)
    {
        // Grab values
        int i = text.indexOf('{');
        if (i < 0)
            return text;

        int j = text.indexOf('}', i);
        if (j < 0)
            return text;

        StringBuilder builder = new StringBuilder();
        HashMap<String, Object> data = DynamicSkill.getCastData(caster);

        int k = 0;
        while (i >= 0 && j > i)
        {
            String key = text.substring(i + 1, j);
            if (data.containsKey(key))
            {
                Object obj = data.get(key);
                if (obj instanceof Player)
                    obj = ((Player) obj).getName();
                else if (obj instanceof LivingEntity)
                    obj = MobManager.getName((LivingEntity) obj);
                builder.append(text.substring(k, i));
                builder.append(obj);

                k = j + 1;
            }
            else if (key.equals("player"))
            {
                builder.append(text.substring(k, i));
                builder.append(caster.getName());

                k = j + 1;
            }
            else if (key.equals("target"))
            {
                builder.append(text.substring(k, i));
                builder.append(target.getName());

                k = j + 1;
            }
            i = text.indexOf('{', j);
            j = text.indexOf('}', i);
        }
        builder.append(text.substring(k));
        return builder.toString();
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

    /**
     * Creates the list of indicators for the skill
     *
     * @param list   list to store indicators in
     * @param caster caster reference
     * @param target location to base location on
     * @param level  the level of the skill to create for
     */
    public void makeIndicators(List<IIndicator> list, Player caster, LivingEntity target, int level)
    {
        if (hasEffect)
            for (EffectComponent component : children)
                component.makeIndicators(list, caster, target, level);
    }

    private static final String TYPE      = "type";
    private static final String INDICATOR = "indicator";

    /**
     * Saves the component and its children to the config
     *
     * @param config config to save to
     */
    public void save(DataSection config)
    {
        config.set(TYPE, type);
        config.set(INDICATOR, indicatorType.getKey());
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
        indicatorType = IndicatorType.getByKey(settings.getString(INDICATOR, "2D"));

        DataSection children = config.getSection("children");
        if (children != null)
        {
            for (String key : children.keys())
            {
                String type = children.getSection(key).getString(TYPE, "missing").toLowerCase();
                HashMap<String, Class<? extends EffectComponent>> map;
                switch (type) {
                    case "target":
                        map = targets;
                        break;
                    case "condition":
                        map = conditions;
                        break;
                    case "mechanic":
                        map = mechanics;
                        break;
                    default:
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
                        child.name = mkey;
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
                    Logger.invalid("Invalid " + type + " component: " + mkey);
                }
            }
        }

        hasEffect = hasEffect();
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
        put("armor", ArmorCondition.class);
        put("attribute", AttributeCondition.class);
        put("biome", BiomeCondition.class);
        put("block", BlockCondition.class);
        put("cast level", CastLevelCondition.class);
        put("ceiling", CeilingCondition.class);
        put("chance", ChanceCondition.class);
        put("class", ClassCondition.class);
        put("class level", ClassLevelCondition.class);
        put("combat", CombatCondition.class);
        put("crouch", CrouchCondition.class);
        put("direction", DirectionCondition.class);
        put("elevation", ElevationCondition.class);
        put("else", ElseCondition.class);
        put("entity type", EntityTypeCondition.class);
        put("fire", FireCondition.class);
        put("flag", FlagCondition.class);
        put("health", HealthCondition.class);
        put("inventory", InventoryCondition.class);
        put("item", ItemCondition.class);
        put("light", LightCondition.class);
        put("lore", LoreCondition.class);
        put("mana", ManaCondition.class);
        put("name", NameCondition.class);
        put("offhand", OffhandCondition.class);
        put("permission", PermissionCondition.class);
        put("potion", PotionCondition.class);
        put("skill level", SkillLevelCondition.class);
        put("slot", SlotCondition.class);
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
        put("buff", BuffMechanic.class);
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
        put("disguise", DisguiseMechanic.class);
        put("durability", DurabilityMechanic.class);
        put("explosion", ExplosionMechanic.class);
        put("fire", FireMechanic.class);
        put("flag", FlagMechanic.class);
        put("flag clear", FlagClearMechanic.class);
        put("flag toggle", FlagToggleMechanic.class);
        put("food", FoodMechanic.class);
        put("forget targets", ForgetTargetsMechanic.class);
        put("heal", HealMechanic.class);
        put("health set", HealthSetMechanic.class);
        put("held item", HeldItemMechanic.class);
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
        put("particle effect", ParticleEffectMechanic.class);
        put("particle projectile", ParticleProjectileMechanic.class);
        put("passive", PassiveMechanic.class);
        put("permission", PermissionMechanic.class);
        put("potion", PotionMechanic.class);
        put("potion projectile", PotionProjectileMechanic.class);
        put("projectile", ProjectileMechanic.class);
        put("purge", PurgeMechanic.class);
        put("push", PushMechanic.class);
        put("remember targets", RememberTargetsMechanic.class);
        put("repeat", RepeatMechanic.class);
        put("speed", SpeedMechanic.class);
        put("sound", SoundMechanic.class);
        put("status", StatusMechanic.class);
        put("taunt", TauntMechanic.class);
        put("trigger", TriggerMechanic.class);
        put("value add", ValueAddMechanic.class);
        put("value attribute", ValueAttributeMechanic.class);
        put("value copy", ValueCopyMechanic.class);
        put("value distance", ValueDistanceMechanic.class);
        put("value health", ValueHealthMechanic.class);
        put("value location", ValueLocationMechanic.class);
        put("value lore", ValueLoreMechanic.class);
        put("value lore slot", ValueLoreSlotMechanic.class);
        put("value mana", ValueManaMechanic.class);
        put("value multiply", ValueMultiplyMechanic.class);
        put("value placeholder", ValuePlaceholderMechanic.class);
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
