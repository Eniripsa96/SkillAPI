package com.sucy.skill.api.dynamic;

import com.sucy.skill.api.PlayerSkills;
import com.sucy.skill.mechanic.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.Arrays;
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
        this.target = Target.values()[config.getInt(TARGET)];
        this.group = Group.values()[config.getInt(GROUP)];
        this.mechanic = MECHANICS.get(config.getString(EFFECT));
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
     * Saves the mechanic details to a configuration section
     *
     * @param config configuration section to save to
     */
    public void save(ConfigurationSection config) {
        config.set(EFFECT, mechanic.getClass().getSimpleName().replace("Mechanic", ""));
        config.set(TARGET, Arrays.asList(Target.values()).indexOf(target));
        config.set(GROUP, Arrays.asList(Group.values()).indexOf(group));
    }

    public static final HashMap<String, IMechanic> MECHANICS = new HashMap<String, IMechanic>() {{
        put("Damage", new DamageMechanic());
        put("Fire", new FireMechanic());
        put("Heal", new HealMechanic());
        put("Launch", new LaunchMechanic());
        put("Potion", new PotionMechanic());
        put("Pull", new PullMechanic());
        put("Push", new PushMechanic());
        put("Status", new StatusMechanic());
        put("Taunt", new TauntMechanic());
    }};
}
