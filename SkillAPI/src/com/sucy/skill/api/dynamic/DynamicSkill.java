package com.sucy.skill.api.dynamic;

import com.sucy.skill.api.skill.*;
import com.sucy.skill.config.SkillValues;
import com.sucy.skill.api.PlayerSkills;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Details for a skill described through configuration data
 */
public class DynamicSkill extends ClassSkill implements SkillShot, PassiveSkill {

    private static final String
        ATTRIBUTES = "attributes",
        VALUES = "values",
        PASSIVE = "passive",
        ACTIVE = "active",
        ALIASED = "aliased",
        PERIOD = "Period";

    private final HashMap<String, PassiveTask> tasks = new HashMap<String, PassiveTask>();
    private final HashMap<String, Boolean> aliased = new HashMap<String, Boolean>();
    private final List<Mechanic> activeMechanics = new ArrayList<Mechanic>();
    private final List<Mechanic> passiveMechanics = new ArrayList<Mechanic>();

    private boolean passive = false;

    /**
     * Constructor from config data
     *
     * @param name   skill name
     */
    public DynamicSkill(String name) {
        super(name, SkillType.TARGET, Material.GLASS, 1);

        // Default attributes
        validateDefaults();
    }

    /**
     * Updates the skill from configuration data
     *
     * @param config configuration data to update from
     */
    @Override
    public void update(ConfigurationSection config) {

        // Basic values
        type = SkillType.valueOf(config.getString(SkillValues.TYPE, type.name()).toUpperCase());
        indicator = Material.valueOf(config.getString(SkillValues.INDICATOR, indicator.name()).toUpperCase());
        maxLevel = config.getInt(SkillValues.MAX_LEVEL, maxLevel);
        skillReq = config.getString(SkillValues.SKILL_REQ, skillReq);
        skillReqLevel = config.getInt(SkillValues.SKILL_REQ_LEVEL, skillReqLevel);
        description.clear();
        description.addAll(config.getStringList(SkillValues.DESCRIPTION));

        // Load attributes
        loadAttributes(config.getConfigurationSection(ATTRIBUTES));

        // Load values
        loadValues(config.getConfigurationSection(VALUES));

        // Load aliased
        ConfigurationSection aliasSection = config.getConfigurationSection(ALIASED);
        if (aliasSection != null) {
            for (String key : aliasSection.getKeys(false)) {
                aliased.put(key, aliasSection.getBoolean(key));
            }
        }

        // Load passive mechanics
        ConfigurationSection passiveSection = config.getConfigurationSection(PASSIVE);
        if (passiveSection != null) {
            for (String key : passiveSection.getKeys(false)) {
                Mechanic mechanic = new Mechanic(this, passiveSection.getConfigurationSection(key), "Passive ");
                checkConflicts(passiveMechanics, mechanic, "Passive ");
                passiveMechanics.add(mechanic);
                mechanic.getTarget().applyDefaults(this);
            }
        }

        // Load active mechanics
        ConfigurationSection activeSection = config.getConfigurationSection(ACTIVE);
        if (activeSection != null) {
            for (String key : activeSection.getKeys(false)) {
                Mechanic mechanic = new Mechanic(this, activeSection.getConfigurationSection(key), "");
                checkConflicts(activeMechanics, mechanic, "");
                activeMechanics.add(mechanic);
                mechanic.getTarget().applyDefaults(this);
            }
        }

        // If passive abilities, make sure there's a period
        if (passiveMechanics.size() > 0) {
            checkDefault(PERIOD, 3, 1);
        }
    }

    /**
     * GetAttribute override to handle passive prefixes
     *
     * @param key    attribute key
     * @param target target of the attribute
     * @param level  level of the skill
     * @return       attribute value at the level
     */
    public int getAttribute(String key, Target target, int level) {
        String prefix = "";
        if (passive && !key.equals("Range") && !key.equals("Radius")) prefix = "Passive ";
        key = target.getAlias(this, key);
        return super.getAttribute(prefix + key, level);
    }

    /**
     * Adds a new active mechanic to the skill
     *
     * @param mechanic mechanic to add
     * @param target   target of the mechanic
     * @param group    group of the mechanic
     */
    public void addActiveMechanic(IMechanic mechanic, Target target, Group group) {
        Mechanic active = new Mechanic(this, mechanic, target, group, "");
        checkConflicts(activeMechanics, active, "");
        target.applyDefaults(this);
        activeMechanics.add(active);
    }

    /**
     * Adds a new passive mechanic to the skill
     *
     * @param mechanic mechanic to add
     * @param target   target of the mechanic
     * @param group    group of the mechanic
     */
    public void addPassiveMechanic(IMechanic mechanic, Target target, Group group) {
        Mechanic passive = new Mechanic(this, mechanic, target, group, "Passive ");
        checkConflicts(passiveMechanics, passive, "Passive ");
        target.applyDefaults(this);
        passiveMechanics.add(passive);
    }

    /**
     * Checks attribute conflicts for mechanics
     *
     * @param mechanics   list to check against
     * @param newMechanic mechanic being added
     */
    private void checkConflicts(List<? extends Mechanic> mechanics, Mechanic newMechanic, String prefix) {
        for (Mechanic m : mechanics) {
            if (m.conflicts(newMechanic)) throw new IllegalArgumentException("Mechanic is already added to the skill");
            else if (m.needsAlias(newMechanic)) {
                for (String attribute : newMechanic.getEffect().getAttributeNames()) {
                    String fullName = prefix + attribute;
                    aliased.put(fullName, true);

                    // Reassign the old attribute if applicable
                    if (hasAttribute(fullName)) {
                        setAttribute(prefix + m.getTarget().getAlias(this, attribute), getBase(fullName), getScale(fullName));
                        removeAttribute(prefix + attribute);
                    }

                    // Add the new attribute
                    newMechanic.getEffect().applyDefaults(this, prefix);
                    setAttribute(prefix + newMechanic.getTarget().getAlias(this, attribute), getBase(fullName), getScale(fullName));
                    removeAttribute(fullName);
                }

                return;
            }
        }

        newMechanic.getEffect().applyDefaults(this, prefix);
    }

    /**
     * Checks whether or not the attribute is aliased
     *
     * @param attribute attribute name
     * @return          true if aliased, false otherwise
     */
    public boolean isAliased(String attribute) {
        return aliased.containsKey(attribute) && aliased.get(attribute);
    }

    /**
     * Updates passive effects when the skill is upgraded
     *
     * @param player   player unlocking the skill
     * @param newLevel the new level of the skill
     */
    @Override
    public void onUpgrade(Player player, int newLevel) {

        // Cancel any previously running tasks
        stopEffects(player, newLevel);

        // Do nothing if no mechanics present
        if (passiveMechanics.size() == 0) return;

        // Start a new task
        PlayerSkills data = api.getPlayer(player.getName());
        PassiveTask task = new PassiveTask(this, data, player);
        int level = data.getSkillLevel(getName());
        int period = getAttribute(PERIOD, level) * 20;
        task.runTaskTimer(data.getAPI(), period, period);
        tasks.put(player.getName(), task);
    }

    /**
     * Updates passive effects when the skill is unlocked
     *
     * @param player player logging in
     * @param level  skill level
     */
    @Override
    public void onInitialize(Player player, int level) {
        onUpgrade(player, level);
    }

    /**
     * Stops passive effects when the skill is lost or the plugin is disabling
     *
     * @param player player to stop the effects for
     * @param level  active level of the effect
     */
    @Override
    public void stopEffects(Player player, int level) {
        for (PassiveTask task : tasks.values()) {
            task.cancel();
        }
        tasks.clear();
    }

    /**
     * Casts the skill
     *
     * @param player player casting the skill
     * @param level  current level of the skill
     * @return       true if cast successfully, false otherwise
     */
    @Override
    public boolean cast(Player player, int level) {
        PlayerSkills data = api.getPlayer(player.getName());
        boolean successful = false;
        for (Mechanic mechanic : activeMechanics) {
            successful = successful || mechanic.resolve(player, data, this);
        }
        return successful;
    }

    /**
     * Saves the mechanic to the configuration section
     *
     * @param config configuration section to save to
     */
    public void save(ConfigurationSection config) {
        config.set(SkillValues.TYPE, getType().name());
        config.set(SkillValues.INDICATOR, getIndicator().name());
        config.set(SkillValues.MAX_LEVEL, getMaxLevel());
        config.set(SkillValues.SKILL_REQ, getSkillReq());
        config.set(SkillValues.SKILL_REQ_LEVEL, getSkillReqLevel());
        config.set(SkillValues.DESCRIPTION, description);
        saveAttributes(config.createSection(ATTRIBUTES));
        saveValues(config.createSection(VALUES));

        // Aliases
        ConfigurationSection aliasSection = config.createSection(ALIASED);
        for (Map.Entry<String, Boolean> entry : aliased.entrySet()) {
            aliasSection.set(entry.getKey(), entry.getValue());
        }

        // Passives
        ConfigurationSection passiveSection = config.createSection(PASSIVE);
        int index = 0;
        for (Mechanic mechanic : passiveMechanics) {
            mechanic.save(passiveSection.createSection("m" + index++));
        }

        // Actives
        ConfigurationSection activeSection = config.createSection(ACTIVE);
        index = 0;
        for (Mechanic mechanic : activeMechanics) {
            mechanic.save(activeSection.createSection("m" + index++));
        }
    }

    /**
     * Private task for passive mechanics
     */
    private class PassiveTask extends BukkitRunnable {

        private DynamicSkill skill;
        private PlayerSkills data;
        private Player player;

        /**
         * Constructor
         *
         * @param skill  passive skill
         * @param data   data of the player with the passive
         * @param player player with the passive
         */
        private PassiveTask(DynamicSkill skill, PlayerSkills data, Player player) {
            this.skill = skill;
            this.data = data;
            this.player = player;
        }

        /**
         * Performs the passive ability
         */
        @Override
        public void run() {
            skill.passive = true;
            for (Mechanic mechanic : skill.passiveMechanics) {
                mechanic.resolve(player, data, skill);
            }
            skill.passive = false;
        }
    }
}
