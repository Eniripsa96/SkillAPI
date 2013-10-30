package com.sucy.skill.api.skill;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.Attributed;
import com.sucy.skill.config.SkillValues;
import com.sucy.skill.language.SkillNodes;
import com.sucy.skill.api.PlayerSkills;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>Base class for skills</p>
 * <p>For a detailed tutorial on how to use this class, visit
 * <a href="http://dev.bukkit.org/bukkit-plugins/skillapi/pages/skill-tutorial/"/></p>
 */
public abstract class ClassSkill extends Attributed {

    private final HashMap<String, Long> timers = new HashMap<String, Long>();
    private final String name;

    protected SkillType type;
    protected Material indicator;
    protected int maxLevel;
    protected String skillReq;
    protected int skillReqLevel;

    protected final ArrayList<String> description = new ArrayList<String>();
    protected final SkillAPI api;

    /**
     * Constructor
     *
     * @param name      skill name
     * @param type      skill type
     * @param indicator skill tree indicator
     * @param maxLevel  maximum skill level
     */
    public ClassSkill(String name, SkillType type, Material indicator, int maxLevel) {
        this(name, type, indicator, maxLevel, null, 0);
    }

    /**
     * Constructor
     *
     * @param name      skill name
     * @param type      skill type
     * @param indicator skill tree indicator
     * @param maxLevel  maximum skill level
     * @param skillReq  skill required before this one
     */
    public ClassSkill(String name, SkillType type, Material indicator, int maxLevel, String skillReq, int skillReqLevel) {
        this.type = type;
        this.name = name;
        this.indicator = indicator;
        this.maxLevel = maxLevel;
        this.skillReq = skillReq;
        this.skillReqLevel = skillReqLevel;
        api = (SkillAPI) Bukkit.getPluginManager().getPlugin("SkillAPI");
    }

    /**
     * @return API reference
     */
    public SkillAPI getAPI() {
        return api;
    }

    /**
     * @return skill name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the maximum level of the skill
     */
    public int getMaxLevel() {
        return maxLevel;
    }

    /**
     * @return the material used to represent the skill in skill trees
     */
    public Material getIndicator() {
        return indicator;
    }

    /**
     * @return the type of skill
     */
    public SkillType getType() {
        return type;
    }

    /**
     * @return skill required to have to get this one
     */
    public String getSkillReq() {
        return skillReq;
    }

    /**
     * @return Level of the required skill that is needed
     */
    public int getSkillReqLevel() {
        return skillReqLevel;
    }

    /**
     * @return the description lore lines for the indicator
     */
    public List<String> getDescription() {
        return description;
    }

    /**
     * Sets the required skill
     *
     * @param skill required skill
     * @param level level of the required skill
     */
    public void setSkillReq(ClassSkill skill, int level) {
        skillReq = skill == null ? null : skill.getName();
        skillReqLevel = level;
    }

    /**
     * Sets the maximum level of the skill
     *
     * @param level maximum level
     */
    public void setMaxLevel(int level) {
        maxLevel = level;
    }

    /**
     * Sets the icon for the skill
     *
     * @param mat material for the icon
     */
    public void setIcon(Material mat) {
        indicator = mat;
    }

    /**
     * Sets the type of the skill
     *
     * @param type new type
     */
    public void setType(SkillType type) {
        this.type = type;
    }

    /**
     * Generates a new indicator item stack for the given skill level
     *
     * @param level current skill level
     * @return      indicator item stack
     */
    public ItemStack getIndicator(PlayerSkills player, int level) {

        List<String> layout = api.getMessages(SkillNodes.LAYOUT, false);
        boolean first = true;

        ItemStack item = new ItemStack(getIndicator());
        ItemMeta meta = item.hasItemMeta() ? item.getItemMeta() : Bukkit.getItemFactory().getItemMeta(indicator);
        ArrayList<String> lore = new ArrayList<String>();

        // Cycle through each line, parse it, and add it to the display
        for (String line : layout) {
            List<String> results = new ArrayList<String>();

            // Title filter
            if (line.contains("{title}")) {
                String title = api.getMessage(SkillNodes.TITLE, false);

                title = title.replace("{name}", getName())
                        .replace("{level}", level + "")
                        .replace("{max}", getMaxLevel() + "");

                line = line.replace("{title}", title);
            }

            // Type filter
            if (line.contains("{type}")) {
                String type = api.getMessage(SkillNodes.TYPE, false);
                type = type.replace("{name}", api.getMessage(getType().getNode(), false));
                line = line.replace("{type}", type);
            }

            // Requirement Filter
            if (line.contains("{requirements}")) {

                int requiredLevel = getAttribute(SkillAttribute.LEVEL, level + 1);
                line = line.replace("{requirements}",
                        getRequirementString(SkillAttribute.LEVEL, requiredLevel, player.getLevel() >= requiredLevel));

                int requiredPoints = getAttribute(SkillAttribute.COST, level + 1);
                results.add(getRequirementString(SkillAttribute.COST, requiredPoints, player.getPoints() >= requiredPoints));

                String skillReq = getSkillReq();
                if (skillReq != null) {
                    results.add(getRequirementString(skillReq, skillReqLevel,
                            player.hasSkill(skillReq) && player.getSkillLevel(skillReq) >= skillReqLevel));
                }
            }

            // Attributes filter
            if (line.contains("{attributes}")) {

                boolean useLine = true;

                // Go through each attribute
                for (String attribute : getAttributeNames()) {
                    if (attribute.equals(SkillAttribute.COST) || attribute.equals(SkillAttribute.LEVEL))
                        continue;

                    // Get the values
                    int oldValue = getAttribute(attribute, level);
                    int newValue = getAttribute(attribute, level + 1);

                    // Level 0 doesn't count
                    if (level == 0) oldValue = newValue;
                    if (level == maxLevel) newValue = oldValue;

                    String attLine;

                    // Changing attribute
                    if (oldValue != newValue) {

                        attLine = api.getMessage(SkillNodes.ATTRIBUTE_CHANGING, false);
                        attLine = attLine.replace("{new}", newValue + "");
                    }

                    // Not changing attribute
                    else attLine = api.getMessage(SkillNodes.ATTRIBUTE_NOT_CHANGING, false);

                    attLine = attLine.replace("{value}", oldValue + "")
                            .replace("{name}", attribute);

                    // Line replace
                    if (useLine) {
                        useLine = false;
                        line = line.replace("{attributes}", attLine);
                    }

                    // Add to results
                    else results.add(attLine);
                }

                // No attributes present
                if (useLine) {
                    line = line.replace("{attributes}", api.getMessage(SkillNodes.ATTRIBUTE_NONE, false));
                }
            }

            // Description filter
            if (line.contains("{description}")) {

                // No description
                if (getDescription().size() == 0) {
                    line = line.replace("{description}", api.getMessage(SkillNodes.DESCRIPTION_NONE, false));
                }

                // Go through each line
                else {

                    // First line
                    String descFirst = api.getMessage(SkillNodes.DESCRIPTION_FIRST, false);
                    descFirst = descFirst.replace("{line}", description.get(0));
                    line = line.replace("{description}", descFirst);

                    // Other lines
                    String descLine = api.getMessage(SkillNodes.DESCRIPTION_OTHER, false);
                    for (int i = 1; i < description.size(); i++) {
                        results.add(descLine.replace("{line}", description.get(i)));
                    }
                }
            }

            results.add(0, line);

            // Add the resulting lines
            for (String result : results) {

                result = result.replaceAll("&([0-9a-fl-orA-FL-OR])", ChatColor.COLOR_CHAR + "$1");

                // First line is assigned to the item's name
                if (first) {
                    first = false;
                    meta.setDisplayName(result);
                }

                // Anything else appends to the lore
                else lore.add(result);
            }
        }

        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Gets the requirement string for the skill icon
     *
     * @param name      requirement name
     * @param value     requirement value
     * @param satisfied whether or not it is satisfied
     * @return          requirement string
     */
    private String getRequirementString(String name, int value, boolean satisfied) {
        String reqString = api.getMessage(satisfied ?
                SkillNodes.REQUIREMENT_MET
                : SkillNodes.REQUIREMENT_NOT_MET, true);

        return reqString.replace("{name}", name)
                .replace("{value}", value + "");
    }

    /**
     * Checks the availability status of the skill for the player
     *
     * @param player      player to check for
     * @param manaEnabled whether or not mana is enabled
     * @return            status of the skill for the player
     */
    public SkillStatus checkStatus(PlayerSkills player, boolean manaEnabled) {

        // Get the skill level
        int level = player.getSkillLevel(name);

        // See if it is on cooldown
        if (getCooldown(player) > 0) return SkillStatus.ON_COOLDOWN;

        // If mana is enabled, check to see if the player has enough
        if (manaEnabled) {
            int manaCost = getAttribute(SkillAttribute.MANA, level);

            if (player.getMana() < manaCost) return SkillStatus.MISSING_MANA;
        }

        // The skill is available when both cooldown and mana were ok
        return SkillStatus.READY;
    }

    /**
     * Starts the cooldown for this skill for the player
     *
     * @param player player to start the cooldown for
     */
    public void startCooldown(PlayerSkills player) {
        timers.put(player.getName(), System.currentTimeMillis());
    }

    /**
     * Refreshes the cooldown for the skill enabling them to cast it again
     *
     * @param player player to refresh the cooldown for
     */
    public void refreshCooldown(PlayerSkills player) {
        timers.put(player.getName(), 0L);
    }

    /**
     * Gets the cooldown remaining on the skill
     *
     * @param player player to check for
     * @return       time left on cooldown (0 if off cooldown)
     */
    public int getCooldown(PlayerSkills player) {

        // Make sure they have a timer
        if (!timers.containsKey(player.getName())) {
            timers.put(player.getName(), 0L);
        }

        int level = player.getSkillLevel(name);
        long passed = System.currentTimeMillis() - timers.get(player.getName());
        long cd = (long)(1000 * getAttribute(SkillAttribute.COOLDOWN, level));
        int left = (int)((cd - passed) / 1000 + 1);
        return left > 0 ? left : 0;
    }

    /**
     * Validates that the default attributes are set and sets them if they aren't there
     */
    public void validateDefaults() {
        checkDefault(SkillAttribute.LEVEL, 1, 0);
        checkDefault(SkillAttribute.COST, 1, 0);
        if (this instanceof SkillShot || this instanceof TargetSkill) {
            checkDefault(SkillAttribute.MANA, 0, 0);
            checkDefault(SkillAttribute.COOLDOWN, 0, 0);
            if (this instanceof TargetSkill) {
                checkDefault(SkillAttribute.RANGE, 6, 0);
            }
        }
    }

    /**
     * Updates the skill from config data
     *
     * @param config configuration data to update from
     */
    public void update(ConfigurationSection config) {

        // Attributes
        for (String attribute : getAttributeNames()) {
            setBase(attribute, config.getInt(attribute + "-base"));
            setScale(attribute, config.getInt(attribute + "-scale"));
        }

        // Max level
        maxLevel = config.getInt(SkillValues.MAX_LEVEL);

        // Description
        List<String> description = config.getStringList(SkillValues.DESCRIPTION);
        if (description != null) {
            this.description.clear();
            this.description.addAll(description);
        }

        // Skill Requirement
        if (config.contains(SkillValues.SKILL_REQ)) {
            skillReq = config.getString(SkillValues.SKILL_REQ);
            skillReqLevel = config.getInt(SkillValues.SKILL_REQ_LEVEL, 1);
        }

        // Icon
        Material icon = Material.valueOf(config.getString(SkillValues.INDICATOR));
        if (icon != null) indicator = icon;
    }
}
