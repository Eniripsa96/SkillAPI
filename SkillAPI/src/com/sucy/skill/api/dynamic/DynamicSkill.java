package com.sucy.skill.api.dynamic;

import com.sucy.skill.api.PlayerSkills;
import com.sucy.skill.api.skill.ClassSkill;
import com.sucy.skill.api.skill.PassiveSkill;
import com.sucy.skill.api.skill.SkillShot;
import com.sucy.skill.api.skill.SkillType;
import com.sucy.skill.config.SkillValues;
import com.sucy.skill.version.VersionPlayer;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import sun.net.www.content.text.plain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Details for a skill described through configuration data</p>
 * <p>You should not use this class. It is for dynamic usage only.</p>
 */
public class DynamicSkill extends ClassSkill implements SkillShot, PassiveSkill {

    private static final String
        ATTRIBUTES = "attributes",
        VALUES = "values",
        PASSIVE = "passive",
        ACTIVE = "active",
        ALIASED = "aliased",
        EMBED = "embed",
        ITEM_REQ = "item-req",
        PERIOD = "Period",
        STRINGS = "strings";

    public final List<Mechanic> activeMechanics = new ArrayList<Mechanic>();
    public final List<Mechanic> passiveMechanics = new ArrayList<Mechanic>();
    public final List<Mechanic> embedMechanics = new ArrayList<Mechanic>();
    public String prefix = "";

    private final HashMap<String, String> strings = new HashMap<String, String>();
    private final HashMap<String, PassiveTask> tasks = new HashMap<String, PassiveTask>();
    private final HashMap<String, Boolean> aliased = new HashMap<String, Boolean>();

    private String itemReq;

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
        type = SkillType.valueOf(config.getString(SkillValues.TYPE, type.name()).toUpperCase().replace(" ", "_"));
        parseIndicator(config.getString(SkillValues.INDICATOR, "GLASS").toUpperCase().replace(" ", "_"));
        maxLevel = config.getInt(SkillValues.MAX_LEVEL, maxLevel);
        skillReq = config.getString(SkillValues.SKILL_REQ, skillReq);
        skillReqLevel = config.getInt(SkillValues.SKILL_REQ_LEVEL, skillReqLevel);
        message = config.getString(SkillValues.MESSAGE);
        description.clear();
        description.addAll(config.getStringList(SkillValues.DESCRIPTION));

        // Required items
        itemReq = config.getString(ITEM_REQ, null);
        if (itemReq != null) itemReq = itemReq.replace(" ", "_").toUpperCase();

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

        // Load strings
        ConfigurationSection stringSection = config.getConfigurationSection(STRINGS);
        if (stringSection != null) {
            for (String key : stringSection.getKeys(false)) {
                strings.put(key, stringSection.getString(key));
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

        // Load embedded mechanics
        ConfigurationSection embedSection = config.getConfigurationSection(EMBED);
        if (embedSection != null) {
            for (String key : embedSection.getKeys(false)) {
                Mechanic mechanic = new Mechanic(this, embedSection.getConfigurationSection(key), "Embed ");
                if (mechanic.getTarget() == Target.LINEAR) continue;
                checkConflicts(embedMechanics, mechanic, "Embed ");
                embedMechanics.add(mechanic);
                mechanic.getTarget().applyDefaults(this);
            }
        }

        // Needed permission
        if (config.contains(SkillValues.NEEDS_PERMISSION)) {
            needsPermission = config.getBoolean(SkillValues.NEEDS_PERMISSION);
        }

        // Load permissions
        if (config.contains(SkillValues.PERMISSIONS)) {
            permissions.clear();
            permissions.addAll(config.getStringList(SkillValues.PERMISSIONS));
        }

        // If passive abilities, make sure there's a period
        if (passiveMechanics.size() > 0) {
            checkDefault(PERIOD, 3, 0);
        }
    }

    /**
     * <p>Checks whether or not the skill has active effects</p>
     *
     * @return true if has active effects, false otherwise
     */
    public boolean hasActiveEffects() {
        return activeMechanics.size() > 0;
    }

    /**
     * <p>Checks whether or not the skill has embedded effects</p>
     *
     * @return true if has embed effects, false otherwise
     */
    public boolean hasEmbedEffects() {
        return embedMechanics.size() > 0;
    }

    /**
     * Retrieves a string by it's key
     *
     * @param key string key
     * @return    string value
     */
    public String getString(String key) {
        return strings.get(key);
    }

    /**
     * Checks if the skill has a value set for the string
     *
     * @param key string key
     * @return    true if set, false otherwise
     */
    public boolean hasString(String key) {
        return strings.containsKey(key);
    }

    /**
     * Sets a string for the skill
     *
     * @param key   string key
     * @param value string value
     */
    public void setString(String key, String value) {
        strings.put(key, value);
    }

    /**
     * getAttribute override to handle passive prefixes
     *
     * @param key    attribute key
     * @param target target of the attribute
     * @param level  level of the skill
     * @return       attribute value at the level
     */
    public double getAttribute(String key, Target target, int level) {
        if (!key.equals("Range") && !key.equals("Radius")) key = this.prefix + target.getAlias(this, key);
        else key = target.getAlias(this, key);
        return getAttribute(key, level);
    }

    /**
     * hasAttribute override to handle passive prefixes
     *
     * @param key    attribute key
     * @param target target of the attribute
     * @return       attribute value at the level
     */
    public boolean hasAttribute(String key, Target target) {
        if (!key.equals("Range") && !key.equals("Radius")) key = this.prefix + target.getAlias(this, key);
        else key = target.getAlias(this, key);
        return hasAttribute(key);
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
     * Adds a new embedded mechanic to the skill
     *
     * @param mechanic mechanic to add
     * @param target   target of the mechanic
     * @param group    group of the mechanic
     */
    public void addEmbedMechanic(IMechanic mechanic, Target target, Group group) {
        if (target == Target.LINEAR) return;
        Mechanic embed = new Mechanic(this, mechanic, target, group, "Embed ");
        checkConflicts(embedMechanics, embed, "Embed ");
        target.applyDefaults(this);
        embedMechanics.add(embed);
    }

    /**
     * Checks attribute conflicts for mechanics
     *
     * @param mechanics   list to check against
     * @param newMechanic mechanic being added
     */
    private void checkConflicts(List<? extends Mechanic> mechanics, Mechanic newMechanic, String prefix) {
        newMechanic.getEffect().applyDefaults(this, prefix);
        for (Mechanic m : mechanics) {
            if (m.conflicts(newMechanic)) throw new IllegalArgumentException("Mechanic is already added to the skill");
            else if (m.needsAlias(newMechanic)) {
                for (String attribute : newMechanic.getEffect().getAttributeNames()) {
                    String fullName = prefix + attribute;
                    aliased.put(fullName, true);

                    // Reassign the old attribute if applicable
                    if (hasAttribute(fullName)) {
                        double base = getBase(fullName);
                        double scale = getScale(fullName);
                        removeAttribute(prefix + attribute);
                        setAttribute(prefix + m.getTarget().getAlias(this, attribute), base, scale);
                    }

                    // Add the new attribute
                    newMechanic.getEffect().applyDefaults(this, prefix);
                    if (hasAttribute(fullName)) {
                        setAttribute(prefix + newMechanic.getTarget().getAlias(this, attribute), getBase(fullName), getScale(fullName));
                        removeAttribute(fullName);
                    }
                }
            }
        }
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
     * @param player player unlocking the skill
     * @param level  the new level of the skill
     */
    @Override
    public void onUpgrade(Player player, int level) {

        // Do nothing if no mechanics present
        if (passiveMechanics.size() == 0) return;

        // Update the effects
        stopEffects(player, level);
        onInitialize(player, level);
    }

    /**
     * Updates passive effects when the skill is unlocked
     *
     * @param player player logging in
     * @param level  skill level
     */
    @Override
    public void onInitialize(Player player, int level) {

        // Do nothing if no mechanics present
        if (passiveMechanics.size() == 0) return;

        PlayerSkills data = api.getPlayer(player);
        PassiveTask task = new PassiveTask(this, data, new VersionPlayer(player));
        int period = (int)(getAttribute(PERIOD, level) * 20);
        task.runTaskTimer(data.getAPI(), period, period);
        tasks.put(player.getName(), task);
    }

    /**
     * Stops passive effects when the skill is lost or the plugin is disabling
     *
     * @param player player to stop the effects for
     * @param level  active level of the effect
     */
    @Override
    public void stopEffects(Player player, int level) {
        if (player == null) return;
        String key = player.getName().toLowerCase();
        if (tasks.containsKey(key)) {
            tasks.get(key).cancel();
            tasks.remove(key);
        }
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
        if (!hasItemReq(player)) return false;

        prefix = "";
        PlayerSkills data = api.getPlayer(player);
        boolean successful = false;
        for (Mechanic mechanic : activeMechanics) {
            successful = mechanic.resolve(player, data, this) || successful;
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
        config.set(SkillValues.INDICATOR, getIndicator().getType().name() + "," + getIndicator().getDurability());
        config.set(SkillValues.MAX_LEVEL, getMaxLevel());
        config.set(SkillValues.SKILL_REQ, getSkillReq());
        config.set(SkillValues.SKILL_REQ_LEVEL, getSkillReqLevel());
        config.set(SkillValues.DESCRIPTION, description);
        config.set(SkillValues.MESSAGE, message);
        config.set(SkillValues.PERMISSIONS, permissions);
        config.set(SkillValues.NEEDS_PERMISSION, needsPermission);
        config.set(ITEM_REQ, itemReq);
        saveAttributes(config.createSection(ATTRIBUTES));
        saveValues(config.createSection(VALUES));

        // Aliases
        ConfigurationSection aliasSection = config.createSection(ALIASED);
        for (Map.Entry<String, Boolean> entry : aliased.entrySet()) {
            aliasSection.set(entry.getKey(), entry.getValue());
        }

        // Strings
        ConfigurationSection stringSection = config.createSection(STRINGS);
        for (Map.Entry<String, String> entry : strings.entrySet()) {
            stringSection.set(entry.getKey(), entry.getValue());
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

        // Embedded
        ConfigurationSection embedSection = config.createSection(EMBED);
        index = 0;
        for (Mechanic mechanic : embedMechanics) {
            mechanic.save(embedSection.createSection("m" + index++));
        }
    }

    /**
     * Checks if a player meets the item requirement
     *
     * @param player player to check
     * @return       true if met, false otherwise
     */
    private boolean hasItemReq(Player player) {
        if (itemReq == null) return true;

        String[] items;
        if (itemReq.contains(",")) items = itemReq.split(",");
        else items = new String[] { itemReq };

        boolean validItem = false;
        for (String item : items) {
            try {
                Material mat = Material.valueOf(item.toUpperCase().replace(" ", "_"));
                validItem = true;
                if (player.getItemInHand().getType() == mat) {
                    return true;
                }
            }
            catch (Exception ex) {
                // Do nothing
            }
        }
        return !validItem;
    }

    /**
     * Private task for passive mechanics
     */
    public class PassiveTask extends BukkitRunnable {

        private DynamicSkill skill;
        private PlayerSkills data;
        private VersionPlayer player;

        /**
         * Constructor
         *
         * @param skill  passive skill
         * @param data   data of the player with the passive
         * @param player reference of the player with the passive
         */
        public PassiveTask(DynamicSkill skill, PlayerSkills data, VersionPlayer player) {
            this.skill = skill;
            this.data = data;
            this.player = player;
        }

        /**
         * Performs the passive ability
         */
        @Override
        public void run() {

            Player player = this.player.getPlayer();

            // Cannot use the effect
            if (player == null || player.isDead() || !skill.hasItemReq(player)) {
                return;
            }

            // Use the effect
            prefix = "Passive ";

            beginUsage();
            for (Mechanic mechanic : skill.passiveMechanics) {
                mechanic.resolve(player, data, skill);
            }
            stopUsage();
        }
    }
}
