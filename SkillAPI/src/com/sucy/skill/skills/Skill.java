package com.sucy.skill.skills;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.util.AttributeHelper;
import com.sucy.skill.api.skill.ClassSkill;
import com.sucy.skill.language.SkillNodes;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import com.sucy.skill.config.SkillValues;

import java.util.ArrayList;
import java.util.List;

/**
 * Skill data - should not be used by other plugins
 */
public final class Skill implements Comparable<Skill> {

    private SkillAPI plugin;
    private String name;
    private String skillReq;
    private Material indicator;
    private List<String> description;
    private ClassSkill skill;
    private int maxLevel;

    /**
     * Constructor
     *
     * @param name   skill name
     * @param config configuration section to load from
     */
    public Skill(SkillAPI plugin, String name, ConfigurationSection config) {
        this.plugin = plugin;
        this.name = name;
        maxLevel = config.getInt(SkillValues.MAX_LEVEL.getKey());
        indicator = Material.getMaterial(config.getString(SkillValues.INDICATOR.getKey()).toUpperCase());
        description = config.getStringList(SkillValues.DESCRIPTION.getKey());
        if (config.contains(SkillValues.SKILL_REQ.getKey()))
            skillReq = config.getString(SkillValues.SKILL_REQ.getKey());

        skill = plugin.getRegisteredSkill(name);
        if (skill != null) {
            for (String attribute : AttributeHelper.getAllAttributes(skill)) {
                skill.setBase(attribute, config.getInt(attribute + "-base"));
                skill.setScale(attribute, config.getInt(attribute + "-scale"));
            }
        }
        else throw new NullPointerException("Skill not found - " + name);
    }

    /**
     * @return skill name
     */
    public String getName() {
        return name;
    }

    /**
     * @return max level of the skill
     */
    public int getMaxLevel() {
        return maxLevel;
    }

    /**
     * @return the class skill that generated this skill
     */
    public ClassSkill getClassSkill() {
        return skill;
    }

    /**
     * Calculates the skill cost when at the given level
     *
     * @param level current skill level
     * @return      cost to next level
     */
    public int getCost(int level) {
        return skill.getBase(SkillValues.COST.getKey()) + skill.getScale(SkillValues.COST.getKey()) * level;
    }

    /**
     * Calculates the level required to upgrade this skill
     *
     * @param level current skill level
     * @return      class level required for next skill level
     */
    public int getLevelReq(int level) {
        return skill.getBase(SkillValues.LEVEL.getKey()) + skill.getScale(SkillValues.LEVEL.getKey()) * level;
    }

    /**
     * Gets the cost to cast this skill
     *
     * @param level level of the skill
     * @return      mana cost
     */
    public int getManaCost(int level) {
        return AttributeHelper.calculate(skill, SkillValues.MANA.getKey(), level);
    }

    /**
     * Gets the cooldown of the skill
     *
     * @param level level of the skill
     * @return      cooldown
     */
    public double getCooldown(int level) {
        return AttributeHelper.calculate(skill, SkillValues.COOLDOWN.getKey(), level);
    }

    /**
     * @return skill required to get this skill
     */
    public String getSkillReq() {
        return skillReq;
    }

    /**
     * Generates a new indicator item stack for the given skill level
     *
     * @param level current skill level
     * @return      indicator item stack
     */
    public ItemStack getIndicator(PlayerSkills player, int level) {

        ConfigurationSection language = plugin.getLanguageConfig();
        List<String> layout = language.getStringList(SkillNodes.LAYOUT);
        boolean first = true;

        ItemStack item = new ItemStack(indicator);
        ItemMeta meta = item.hasItemMeta() ? item.getItemMeta() : Bukkit.getItemFactory().getItemMeta(indicator);
        ArrayList<String> lore = new ArrayList<String>();

        ClassSkill cs = plugin.getRegisteredSkill(getName());

        // Cycle through each line, parse it, and add it to the display
        for (String line : layout) {
            List<String> results = new ArrayList<String>();

            // Title filter
            if (line.contains("{title}")) {
                String title = language.getString(SkillNodes.TITLE);

                title = title.replace("{name}", name)
                             .replace("{level}", level + "")
                             .replace("{max}", maxLevel + "");

                line = line.replace("{title}", title);
            }

            // Type filter
            if (line.contains("{type}")) {
                String type = language.getString(SkillNodes.TYPE);
                type = type.replace("{name}", plugin.getMessage(cs.getType().getNode(), false));
                line = line.replace("{type}", type);
            }

            // Requirement Filter
            if (line.contains("{requirements}")) {
                int requiredLevel = AttributeHelper.calculate(cs, SkillValues.LEVEL.getKey(), level + 1);
                String levelString = language.getString(
                        requiredLevel > level ?
                            SkillNodes.REQUIREMENT_NOT_MET
                            : SkillNodes.REQUIREMENT_MET);

                levelString = levelString.replace("{name}", SkillValues.LEVEL.getKey())
                                         .replace("{value}", requiredLevel + "");

                line = line.replace("{requirements}", levelString);

                int requiredPoints = AttributeHelper.calculate(cs, SkillValues.COST.getKey(), level + 1);
                String pointString = language.getString(
                        requiredPoints > player.getPoints() ?
                            SkillNodes.REQUIREMENT_NOT_MET
                            : SkillNodes.REQUIREMENT_MET);

                pointString = pointString.replace("{name}", SkillValues.COST.getKey())
                                         .replace("{value}", requiredPoints + "");

                results.add(pointString);
            }

            // Attributes filter
            if (line.contains("{attributes}")) {

                boolean useLine = true;

                // Go through each attribute
                for (String attribute : AttributeHelper.getAllAttributes(cs)) {
                    if (attribute.equals(SkillValues.COST.getKey()) || attribute.equals(SkillValues.LEVEL.getKey()))
                        continue;

                    // Get the values
                    int oldValue = AttributeHelper.calculate(cs, attribute, level);
                    int newValue = AttributeHelper.calculate(cs, attribute, level + 1);

                    // Level 0 doesn't count
                    if (level == 0) oldValue = newValue;

                    String attLine;

                    // Changing attribute
                    if (oldValue != newValue) {

                        attLine = language.getString(SkillNodes.ATTRIBUTE_CHANGING);
                        attLine = attLine.replace("{new}", newValue + "");
                    }

                    // Not changing attribute
                    else attLine = language.getString(SkillNodes.ATTRIBUTE_NOT_CHANGING);

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
                    line = line.replace("{attributes}", language.getString(SkillNodes.ATTRIBUTE_NONE));
                }
            }

            // Description filter
            if (line.contains("{description}")) {

                // No description
                if (description.size() == 0) {
                    line = line.replace("{description}", language.getString(SkillNodes.DESCRIPTION_NONE));
                }

                // Go through each line
                    else {

                    // First line
                    String descFirst = language.getString(SkillNodes.DESCRIPTION_FIRST);
                    descFirst = descFirst.replace("{line}", description.get(0));
                    line = line.replace("{description}", descFirst);

                    // Other lines
                    String descLine = language.getString(SkillNodes.DESCRIPTION_OTHER);
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
     * Compares skills based on their stats for skill tree arrangement
     *  -> Skills with no prerequisite skills come first
     *  -> Then its skills with lower level requirements
     *  -> Then its skills with lower costs
     *  -> Then its skills alphabetically
     *
     * @param skill skill to compare to
     * @return      -1, 0, or 1
     */
    @Override
    public int compareTo(Skill skill) {
        return
            skillReq != null && skill.skillReq == null ? 1
            : skillReq == null && skill.skillReq != null ? -1
            : getLevelReq(0) > skill.getLevelReq(0) ? 1
            : getLevelReq(0) < skill.getLevelReq(0) ? -1
            : getCost(0) > skill.getCost(0) ? 1
            : getCost(0) < skill.getCost(0) ? -1
            : name.compareTo(skill.name);
    }
}