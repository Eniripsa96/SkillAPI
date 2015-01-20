package com.sucy.skill.dynamic;

import com.sucy.skill.api.Settings;
import com.sucy.skill.api.player.PlayerSkill;
import com.sucy.skill.dynamic.condition.*;
import com.sucy.skill.dynamic.mechanic.*;
import com.sucy.skill.dynamic.target.AreaTarget;
import com.sucy.skill.dynamic.target.LinearTarget;
import com.sucy.skill.dynamic.target.SelfTarget;
import com.sucy.skill.dynamic.target.SingleTarget;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A component for dynamic skills which takes care of one effect
 */
public abstract class EffectComponent
{
    private final ArrayList<EffectComponent> children = new ArrayList<EffectComponent>();

    /**
     * The settings for the component
     */
    protected final Settings settings = new Settings();

    /**
     * The skill containing this component
     */
    public PlayerSkill skill;

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
            worked = child.execute(caster, level, targets) || worked;
        }
        return worked;
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
     * Loads component data from the configuration
     *
     * @param config config data to load from
     */
    public void load(ConfigurationSection config)
    {
        if (config == null) return;
        settings.load(config.getConfigurationSection("data"));

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
                    Bukkit.getLogger().warning("Invalid component type for skill \"" + skill.getData().getName() + "\" - " + type);
                    continue;
                }
                if (map.containsKey(key.toLowerCase()))
                {
                    try
                    {
                        EffectComponent child = map.get(key.toLowerCase()).newInstance();
                        child.load(children.getConfigurationSection(key));
                        this.children.add(child);
                        Bukkit.getLogger().info("Loaded component: " + key);
                    }
                    catch (Exception ex)
                    {
                        // Failed to create the component, just don't add it
                    }
                }
            }
        }
    }

    private static final HashMap<String, Class<? extends EffectComponent>> targets = new HashMap<String, Class<? extends EffectComponent>>()
    {{
            put("area", AreaTarget.class);
            put("linear", LinearTarget.class);
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
            put("light", LightCondition.class);
            put("lore", LoreCondition.class);
            put("mana", ManaCondition.class);
            put("name", NameCondition.class);
            put("potion", PotionCondition.class);
            put("skillLevel", SkillLevelCondition.class);
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
            put("delay", DelayMechanic.class);
            put("fire", FireMechanic.class);
            put("flag", FlagMechanic.class);
            put("heal", HealMechanic.class);
            put("launch", LaunchMechanic.class);
            put("lightning", LightningMechanic.class);
            put("mana", ManaMechanic.class);
            put("particle", ParticleMechanic.class);
            //put("particle projectile", ParticleProjectileMechanic.class);
            //put("potion", PotionMechanic.class);
            //put("projectile", ProjectileMechanic.class);
            //put("push", PushMechanic.class);
            //put("sound", SoundMechanic.class);
            //put("status", StatusMechanic.class);
            //put("warp", WarpMechanic.class);
            //put("warp location", WarpLocationMechanic.class);
            //put("warp random", WarpRandomMechanic.class);
            //put("warp target", WarpTargetMechanic.class);
        }};
}
