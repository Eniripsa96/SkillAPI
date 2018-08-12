package com.sucy.skill.dynamic;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.dynamic.condition.ArmorCondition;
import com.sucy.skill.dynamic.condition.AttributeCondition;
import com.sucy.skill.dynamic.condition.BiomeCondition;
import com.sucy.skill.dynamic.condition.BlockCondition;
import com.sucy.skill.dynamic.condition.ChanceCondition;
import com.sucy.skill.dynamic.condition.ClassCondition;
import com.sucy.skill.dynamic.condition.ClassLevelCondition;
import com.sucy.skill.dynamic.condition.CombatCondition;
import com.sucy.skill.dynamic.condition.CrouchCondition;
import com.sucy.skill.dynamic.condition.DirectionCondition;
import com.sucy.skill.dynamic.condition.ElevationCondition;
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
import com.sucy.skill.dynamic.condition.PotionCondition;
import com.sucy.skill.dynamic.condition.SkillLevelCondition;
import com.sucy.skill.dynamic.condition.StatusCondition;
import com.sucy.skill.dynamic.condition.TimeCondition;
import com.sucy.skill.dynamic.condition.ToolCondition;
import com.sucy.skill.dynamic.condition.ValueCondition;
import com.sucy.skill.dynamic.condition.WaterCondition;
import com.sucy.skill.dynamic.custom.CustomComponent;
import com.sucy.skill.dynamic.custom.CustomEffectComponent;
import com.sucy.skill.dynamic.custom.EditorOption;
import com.sucy.skill.dynamic.mechanic.AttributeMechanic;
import com.sucy.skill.dynamic.mechanic.BlockMechanic;
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
import com.sucy.skill.dynamic.mechanic.ExplosionMechanic;
import com.sucy.skill.dynamic.mechanic.FireMechanic;
import com.sucy.skill.dynamic.mechanic.FlagClearMechanic;
import com.sucy.skill.dynamic.mechanic.FlagMechanic;
import com.sucy.skill.dynamic.mechanic.FlagToggleMechanic;
import com.sucy.skill.dynamic.mechanic.HealMechanic;
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
import com.sucy.skill.dynamic.mechanic.ValueAddMechanic;
import com.sucy.skill.dynamic.mechanic.ValueAttributeMechanic;
import com.sucy.skill.dynamic.mechanic.ValueLocationMechanic;
import com.sucy.skill.dynamic.mechanic.ValueLoreMechanic;
import com.sucy.skill.dynamic.mechanic.ValueMultiplyMechanic;
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
import com.sucy.skill.dynamic.trigger.CrouchTrigger;
import com.sucy.skill.dynamic.trigger.DeathTrigger;
import com.sucy.skill.dynamic.trigger.EnvironmentalTrigger;
import com.sucy.skill.dynamic.trigger.KillTrigger;
import com.sucy.skill.dynamic.trigger.LandTrigger;
import com.sucy.skill.dynamic.trigger.LaunchTrigger;
import com.sucy.skill.dynamic.trigger.PhysicalDealtTrigger;
import com.sucy.skill.dynamic.trigger.PhysicalTakenTrigger;
import com.sucy.skill.dynamic.trigger.SkillDealtTrigger;
import com.sucy.skill.dynamic.trigger.SkillTakenTrigger;
import com.sucy.skill.dynamic.trigger.Trigger;
import org.bukkit.event.Event;
import org.bukkit.plugin.EventExecutor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * SkillAPI © 2018
 * com.sucy.skill.dynamic.ComponentRegistry
 */
public class ComponentRegistry {

    private static final Map<ComponentType, Map<String, Class<?>>> COMPONENTS = new EnumMap<>(ComponentType.class);

    private static final Map<String, Trigger<?>>        TRIGGERS  = new HashMap<>();
    private static final Map<Trigger<?>, EventExecutor> EXECUTORS = new HashMap<>();

    public static Trigger<?> getTrigger(final String key) {
        return TRIGGERS.get(key.toUpperCase().replace(' ', '_'));
    }

    static EffectComponent getComponent(final ComponentType type, final String key) {
        final Class<?> componentClass = COMPONENTS.get(type).get(key.toLowerCase());
        if (componentClass == null) {
            throw new IllegalArgumentException("Invalid component key - " + key);
        }
        try {
            return (EffectComponent) componentClass.newInstance();
        } catch (final Exception ex) {
            throw new IllegalArgumentException("Invalid component - does not have a default constructor");
        }
    }

    static EventExecutor getExecutor(final Trigger<?> trigger) {
        return EXECUTORS.get(trigger);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Event> void register(final Trigger<T> trigger) {
        if (getTrigger(trigger.getKey()) != null) {
            throw new IllegalArgumentException("Trigger with key " + trigger.getKey() + " already exists");
        } else if (trigger.getKey().contains("-")) {
            throw new IllegalArgumentException(trigger.getKey() + " is not a valid key: must not contain dashes");
        }

        TRIGGERS.put(trigger.getKey(), trigger);
        EXECUTORS.put(trigger, (listener, event) -> ((TriggerHandler) listener).apply((T) event, trigger));
    }

    public static void register(final CustomEffectComponent component) {
        register((EffectComponent) component);
    }

    public static void save() {
        final StringBuilder builder = new StringBuilder("[");
        TRIGGERS.values().forEach(trigger -> append(trigger, builder));
        COMPONENTS.forEach((type, map) -> map.keySet().forEach(key -> append(getComponent(type, key), builder)));
        if (builder.length() > 2) {
            builder.replace(builder.length() - 1, builder.length(), "]");
        } else {
            builder.append(']');
        }

        final File file = new File(SkillAPI.getPlugin(SkillAPI.class).getDataFolder(), "tool-config.json");
        try (final FileOutputStream out = new FileOutputStream(file)) {
            final BufferedWriter write = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            write.write(builder.toString());
            write.close();
        } catch (Exception var4) {
            var4.printStackTrace();
        }
    }

    private static void append(final Object obj, final StringBuilder builder) {
        if (!(obj instanceof CustomComponent)) { return; }

        final CustomComponent component = (CustomComponent) obj;
        builder.append("{\"type\":\"").append(component.getType().name())
                .append("\",\"key\":\"").append(component.getKey())
                .append("\",\"display\":\"").append(component.getDisplayName())
                .append("\",\"container\":\"").append(component.isContainer())
                .append("\",\"description\":\"").append(component.getDescription())
                .append("\",\"options\":[");

        boolean first = true;
        for (EditorOption option : component.getOptions()) {
            if (!first) { builder.append(','); }
            first = false;

            builder.append("{\"type\":\"").append(option.type)
                    .append("\",\"key\":\"").append(option.key)
                    .append("\",\"display\":\"").append(option.name)
                    .append("\",\"description\":\"").append(option.description)
                    .append("\"");
            option.extra.forEach((key, value) -> builder.append(",\"").append(key).append("\":").append(value));
            builder.append("}");
        }

        builder.append("]},");
    }

    private static void register(final EffectComponent component) {
        COMPONENTS.computeIfAbsent(component.getType(), t -> new HashMap<>())
                .put(component.getKey().toLowerCase(), component.getClass());
    }

    static {

        // Triggers
        register(new CrouchTrigger());
        register(new DeathTrigger());
        register(new EnvironmentalTrigger());
        register(new KillTrigger());
        register(new LandTrigger());
        register(new LaunchTrigger());
        register(new PhysicalDealtTrigger());
        register(new PhysicalTakenTrigger());
        register(new SkillDealtTrigger());
        register(new SkillTakenTrigger());

        // Targets
        register(new AreaTarget());
        register(new ConeTarget());
        register(new LinearTarget());
        register(new LocationTarget());
        register(new NearestTarget());
        register(new OffsetTarget());
        register(new RememberTarget());
        register(new SelfTarget());
        register(new SingleTarget());

        // Conditions
        register(new ArmorCondition());
        register(new AttributeCondition());
        register(new BiomeCondition());
        register(new BlockCondition());
        register(new ChanceCondition());
        register(new ClassCondition());
        register(new ClassLevelCondition());
        register(new CombatCondition());
        register(new CrouchCondition());
        register(new DirectionCondition());
        register(new ElevationCondition());
        register(new FireCondition());
        register(new FlagCondition());
        register(new HealthCondition());
        register(new InventoryCondition());
        register(new ItemCondition());
        register(new LightCondition());
        register(new LoreCondition());
        register(new ManaCondition());
        register(new NameCondition());
        register(new OffhandCondition());
        register(new PotionCondition());
        register(new SkillLevelCondition());
        register(new StatusCondition());
        register(new TimeCondition());
        register(new ToolCondition());
        register(new ValueCondition());
        register(new WaterCondition());

        // Mechanics
        register(new AttributeMechanic());
        register(new BlockMechanic());
        register(new CancelMechanic());
        register(new ChannelMechanic());
        register(new CleanseMechanic());
        register(new CommandMechanic());
        register(new CooldownMechanic());
        register(new DamageMechanic());
        register(new DamageBuffMechanic());
        register(new DamageLoreMechanic());
        register(new DefenseBuffMechanic());
        register(new DelayMechanic());
        register(new DisguiseMechanic());
        register(new ExplosionMechanic());
        register(new FireMechanic());
        register(new FlagMechanic());
        register(new FlagClearMechanic());
        register(new FlagToggleMechanic());
        register(new HealMechanic());
        register(new ImmunityMechanic());
        register(new InterruptMechanic());
        register(new ItemMechanic());
        register(new ItemProjectileMechanic());
        register(new ItemRemoveMechanic());
        register(new LaunchMechanic());
        register(new LightningMechanic());
        register(new ManaMechanic());
        register(new MessageMechanic());
        register(new ParticleMechanic());
        register(new ParticleAnimationMechanic());
        register(new ParticleProjectileMechanic());
        register(new PassiveMechanic());
        register(new PermissionMechanic());
        register(new PotionMechanic());
        register(new PotionProjectileMechanic());
        register(new ProjectileMechanic());
        register(new PurgeMechanic());
        register(new PushMechanic());
        register(new RememberTargetsMechanic());
        register(new RepeatMechanic());
        register(new SpeedMechanic());
        register(new SoundMechanic());
        register(new StatusMechanic());
        register(new TauntMechanic());
        register(new ValueAddMechanic());
        register(new ValueAttributeMechanic());
        register(new ValueLocationMechanic());
        register(new ValueLoreMechanic());
        register(new ValueMultiplyMechanic());
        register(new ValueRandomMechanic());
        register(new ValueSetMechanic());
        register(new WarpMechanic());
        register(new WarpLocMechanic());
        register(new WarpRandomMechanic());
        register(new WarpSwapMechanic());
        register(new WarpTargetMechanic());
        register(new WarpValueMechanic());
        register(new WolfMechanic());
    }
}
