package com.sucy.skill.dynamic;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.Settings;
import com.sucy.skill.api.player.PlayerSkill;
import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.dynamic.condition.*;
import com.sucy.skill.dynamic.mechanic.*;
import com.sucy.skill.dynamic.target.*;
import com.sucy.skill.listener.MechanicListener;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
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
    private static final String ICON_KEY = "icon-key";
    private static final String COUNTS_KEY = "counts";

    public final ArrayList<EffectComponent> children = new ArrayList<EffectComponent>();

    /**
     * The settings for the component
     */
    protected final Settings settings = new Settings();

    /**
     * Parent class of the component
     */
    protected Skill skill;

    /**
     * Key of the component for the config
     */
    private String key;

    /**
     * Type of the component
     */
    private String type = "trigger";

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
    public void save(ConfigurationSection config)
    {
        config.set(TYPE, type);
        settings.save(config.createSection("data"));
        ConfigurationSection children = config.createSection("children");
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
    public void load(DynamicSkill skill, ConfigurationSection config)
    {
        this.skill = skill;
        if (config == null)
        {
            return;
        }
        settings.load(config.getConfigurationSection("data"));
        if (settings.has(ICON_KEY))
        {
            String key = settings.getString(ICON_KEY);
            if (!key.equals(""))
            {
                skill.setAttribKey(key, this);
            }
        }

        ConfigurationSection children = config.getConfigurationSection("children");
        if (children != null)
        {
            for (String key : children.getKeys(false))
            {
                String type = children.getConfigurationSection(key).getString(TYPE, "missing").toLowerCase();
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
                    Bukkit.getLogger().warning("Invalid component type - " + type);
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
                        child.load(skill, children.getConfigurationSection(key));
                        this.children.add(child);
                    }
                    catch (Exception ex)
                    {
                        // Failed to create the component, just don't add it
                        Bukkit.getLogger().warning("Failed to create " + type + " component: " + key);
                    }
                }
                else
                {
                    Bukkit.getLogger().warning("Invalid " + type + " component: " + key);
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
            put("self", SelfTarget.class);
            put("single", SingleTarget.class);
        }};

    private static final HashMap<String, Class<? extends EffectComponent>> conditions = new HashMap<String, Class<? extends EffectComponent>>()
    {{
            put("biome", BiomeCondition.class);
            put("chance", ChanceCondition.class);
            put("class level", ClassLevelCondition.class);
            put("direction", DirectionCondition.class);
            put("elevation", ElevationCondition.class);
            put("fire", FireCondition.class);
            put("flag", FlagCondition.class);
            put("health", HealthCondition.class);
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
            put("water", WaterCondition.class);
        }};

    private static final HashMap<String, Class<? extends EffectComponent>> mechanics = new HashMap<String, Class<? extends EffectComponent>>()
    {{
            put("cleanse", CleanseMechanic.class);
            put("command", CommandMechanic.class);
            put("cooldown", CooldownMechanic.class);
            put("damage", DamageMechanic.class);
            put("damage buff", DamageBuffMechanic.class);
            put("damage lore", DamageLoreMechanic.class);
            put("defense buff", DefenseBuffMechanic.class);
            put("delay", DelayMechanic.class);
            put("fire", FireMechanic.class);
            put("flag", FlagMechanic.class);
            put("flag clear", FlagClearMechanic.class);
            put("flag toggle", FlagToggleMechanic.class);
            put("heal", HealMechanic.class);
            put("immunity", ImmunityMechanic.class);
            put("item", ItemMechanic.class);
            put("item projectile", ItemProjectileMechanic.class);
            put("launch", LaunchMechanic.class);
            put("lightning", LightningMechanic.class);
            put("mana", ManaMechanic.class);
            put("message", MessageMechanic.class);
            put("particle", ParticleMechanic.class);
            put("particle projectile", ParticleProjectileMechanic.class);
            put("passive", PassiveMechanic.class);
            put("permission", PermissionMechanic.class);
            put("potion", PotionMechanic.class);
            put("potion projectile", PotionProjectileMechanic.class);
            put("projectile", ProjectileMechanic.class);
            put("purge", PurgeMechanic.class);
            put("push", PushMechanic.class);
            put("repeat", RepeatMechanic.class);
            put("sound", SoundMechanic.class);
            put("status", StatusMechanic.class);
            put("warp", WarpMechanic.class);
            put("warp location", WarpLocMechanic.class);
            put("warp random", WarpRandomMechanic.class);
            put("warp swap", WarpSwapMechanic.class);
            put("warp target", WarpTargetMechanic.class);
            put("wolf", WolfMechanic.class);
        }};
}
