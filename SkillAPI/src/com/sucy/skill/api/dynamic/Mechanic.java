package com.sucy.skill.api.dynamic;

import com.sucy.skill.api.PlayerSkills;
import com.sucy.skill.mechanic.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

/**
 * Mechanic details for a skill
 */
public class Mechanic {

    private static final String
            EFFECT = "effect",
            TARGET = "target",
            GROUP = "group";

    protected DynamicSkill skill;
    protected IMechanic mechanic;
    protected Target target;
    protected Group group;
    protected String prefix;

    /**
     * Constructor
     *
     * @param mechanic mechanic effect
     * @param target   target of the mechanic
     * @param group    group of the mechanic
     */
    public Mechanic(DynamicSkill skill, IMechanic mechanic, Target target, Group group, String prefix) {
        this.skill = skill;
        this.mechanic = mechanic;
        this.target = target;
        this.group = group;
        this.prefix = prefix;
    }

    /**
     * Constructor from config data
     *
     * @param skill  skill owning the mechanic
     * @param config configuration to load from
     * @param prefix mechanic prefix
     */
    public Mechanic(DynamicSkill skill, ConfigurationSection config, String prefix) {
        this.skill = skill;
        this.prefix = prefix;
        this.target = Target.valueOf(config.getString(TARGET).toUpperCase().replace(" ", "_"));
        this.group = Group.valueOf(config.getString(GROUP).toUpperCase());
        this.mechanic = MECHANICS.get(config.getString(EFFECT).toUpperCase());
    }

    /**
     * @return skill containing this mechanic
     */
    public DynamicSkill getSkill() {
        return skill;
    }

    /**
     * @return effect of the mechanic
     */
    public IMechanic getEffect() {
        return mechanic;
    }

    /**
     * @return target of the mechanic
     */
    public Target getTarget() {
        return target;
    }

    /**
     * @return group of the mechanic
     */
    public Group getGroup() {
        return group;
    }

    /**
     * Checks if this mechanic conflicts with another
     *
     * @param mechanic mechanic to check
     * @return         true if conflicts, false otherwise
     */
    public boolean conflicts(Mechanic mechanic) {
        return mechanic.target == target && mechanic.mechanic == this.mechanic;
    }

    /**
     * Checks if an alias is needed to differentiate the two mechanics
     *
     * @param mechanic mechanic to check
     * @return         true if an alias is needed, false otherwise
     */
    public boolean needsAlias(Mechanic mechanic) {
        return mechanic.mechanic == this.mechanic;
    }

    /**
     * Resolves the mechanic
     *
     * @param player player using the skill
     * @param data   data of the player using the skill
     * @param skill  skill being used
     * @return       true if successfully used, false otherwise
     */
    public boolean resolve(Player player, PlayerSkills data, DynamicSkill skill) {
        int level = data.getSkillLevel(skill.getName());
        List<LivingEntity> targets = target.getTargets(skill, player, level);
        group.filterTargets(player, targets);
        return targets.size() > 0 && mechanic.resolve(player, data, skill, target, targets);
    }

    /**
     * Resolves the mechanic with custom targets
     *
     * @param player  player using the skill
     * @param data    data of the player using the skill
     * @param skill   skill being used
     * @param targets targets of the skill
     * @return        true if successfully used, false otherwise
     */
    public boolean resolve(Player player, PlayerSkills data, DynamicSkill skill, List<LivingEntity> targets) {
        group.filterTargets(player, targets);
        return targets.size() > 0 && mechanic.resolve(player, data, skill, target, targets);
    }

    /**
     * Saves the mechanic details to a configuration section
     *
     * @param config configuration section to save to
     */
    public void save(ConfigurationSection config) {
        config.set(EFFECT, mechanic.getClass().getSimpleName().replace("Mechanic", ""));
        config.set(TARGET, target.name());
        config.set(GROUP, group.name());
    }

    public static final HashMap<String, IMechanic> MECHANICS = new HashMap<String, IMechanic>() {{
        put("ATTACKMODIFIER", new AttackModifierMechanic());
        put("CHANCE", new ChanceMechanic());
        put("CLEANSE", new CleanseMechanic());
        put("COMMAND", new CommandMechanic());
        put("CONDITION", new ConditionMechanic());
        put("COOLDOWN", new CooldownMechanic());
        put("DAMAGE", new DamageMechanic());
        put("DAMAGEBONUS", new DamageBonusMechanic());
        put("DAMAGEPERCENT", new DamagePercentMechanic());
        put("DAMAGEPERCENTREDUCTION", new DamagePercentReductionMechanic());
        put("DAMAGEREDUCTION", new DamageReductionMechanic());
        put("DEFENSEMODIFIER", new DefenseModifierMechanic());
        put("DELAY", new DelayMechanic());
        put("DOT", new DOTMechanic());
        put("FIRE", new FireMechanic());
        put("HEAL", new HealMechanic());
        put("HEALPERCENT", new HealPercentMechanic());
        put("HEALTHDAMAGE", new HealthDamageMechanic());
        put("HOT", new HOTMechanic());
        put("LAUNCH", new LaunchMechanic());
        put("LIGHTNING", new LightningMechanic());
        put("MANA", new ManaMechanic());
        put("MANADAMAGE", new ManaDamageMechanic());
        put("MANAPERCENT", new ManaPercentMechanic());
        put("PARTICLE", new ParticleMechanic());
        put("PARTICLEPROJECTILE", new ParticleProjectileMechanic());
        put("POTION", new PotionMechanic());
        put("PROJECTILE", new ProjectileMechanic());
        put("PULL", new PullMechanic());
        put("PUSH", new PushMechanic());
        put("SOUND", new SoundMechanic());
        put("STATUS", new StatusMechanic());
        put("TAUNT", new TauntMechanic());
        put("TELEPORT", new TeleportMechanic());
        put("TELEPORTLOCATION", new TeleportLocationMechanic());
        put("TELEPORTTARGET", new TeleportTargetMechanic());
        put("VALUECONDITION", new ValueConditionMechanic());
    }};
}
