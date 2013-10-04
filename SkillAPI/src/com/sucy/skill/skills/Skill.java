package com.sucy.skill.skills;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.skill.ClassSkill;
import com.sucy.skill.api.skill.SkillAttribute;
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
    private int skillReqLevel;

    /**
     * Constructor
     *
     * @param name   skill name
     * @param config configuration section to load from
     */
    public Skill(SkillAPI plugin, String name, ConfigurationSection config) {
        this.plugin = plugin;
        this.name = name;
        maxLevel = config.getInt(SkillValues.MAX_LEVEL);
        indicator = Material.getMaterial(config.getString(SkillValues.INDICATOR).toUpperCase());
        description = config.getStringList(SkillValues.DESCRIPTION);
        if (config.contains(SkillValues.SKILL_REQ)) {
            skillReq = config.getString(SkillValues.SKILL_REQ);
            if (config.contains(SkillValues.SKILL_REQ_LEVEL)) {
                skillReqLevel = config.getInt(SkillValues.SKILL_REQ_LEVEL);
            }
            else skillReqLevel = 1;
        }

        skill = plugin.getRegisteredSkill(name);
        if (skill != null) {
            for (String attribute : skill.getAttributeNames()) {
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
     * @return skill required to get this skill
     */
    public String getSkillReq() {
        return skillReq;
    }

    /**
     * @return level needed for the required skill
     */
    public int getSkillReqLevel() {
        return skillReqLevel;
    }

    /**
     * Generates a new indicator item stack for the given skill level
     *
     * @param level current skill level
     * @return      indicator item stack
     */
    public ItemStack getIndicator(PlayerSkills player, int level) {

        List<String> layout = plugin.getMessages(SkillNodes.LAYOUT, false);
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
                String title = plugin.getMessage(SkillNodes.TITLE, false);

                title = title.replace("{name}", name)
                             .replace("{level}", level + "")
                             .replace("{max}", maxLevel + "");

                line = line.replace("{title}", title);
            }

            // Type filter
            if (line.contains("{type}")) {
                String type = plugin.getMessage(SkillNodes.TYPE, false);
                type = type.replace("{name}", plugin.getMessage(cs.getType().getNode(), false));
                line = line.replace("{type}", type);
            }

            // Requirement Filter
            if (line.contains("{requirements}")) {

                int requiredLevel = cs.getAttribute(SkillAttribute.LEVEL, level + 1);
                line = line.replace("{requirements}",
                        getRequirementString(SkillAttribute.LEVEL, requiredLevel, player.getLevel() >= requiredLevel));

                int requiredPoints = cs.getAttribute(SkillAttribute.COST, level + 1);
                results.add(getRequirementString(SkillAttribute.COST, requiredPoints, player.getPoints() >= requiredPoints));

                String skillReq = cs.getSkillReq();
                if (skillReq != null) {
                    results.add(getRequirementString(skillReq, skillReqLevel,
                            player.hasSkill(skillReq) && player.getSkillLevel(skillReq) >= skillReqLevel));
                }
            }

            // Attributes filter
            if (line.contains("{attributes}")) {

                boolean useLine = true;

                // Go through each attribute
                for (String attribute : cs.getAttributeNames()) {
                    if (attribute.equals(SkillAttribute.COST) || attribute.equals(SkillAttribute.LEVEL))
                        continue;

                    // Get the values
                    int oldValue = cs.getAttribute(attribute, level);
                    int newValue = cs.getAttribute(attribute, level + 1);

                    // Level 0 doesn't count
                    if (level == 0) oldValue = newValue;
                    if (level == maxLevel) newValue = oldValue;

                    String attLine;

                    // Changing attribute
                    if (oldValue != newValue) {

                        attLine = plugin.getMessage(SkillNodes.ATTRIBUTE_CHANGING, false);
                        attLine = attLine.replace("{new}", newValue + "");
                    }

                    // Not changing attribute
                    else attLine = plugin.getMessage(SkillNodes.ATTRIBUTE_NOT_CHANGING, false);

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
                    line = line.replace("{attributes}", plugin.getMessage(SkillNodes.ATTRIBUTE_NONE, false));
                }
            }

            // Description filter
            if (line.contains("{description}")) {

                // No description
                if (description.size() == 0) {
                    line = line.replace("{description}", plugin.getMessage(SkillNodes.DESCRIPTION_NONE, false));
                }

                // Go through each line
                    else {

                    // First line
                    String descFirst = plugin.getMessage(SkillNodes.DESCRIPTION_FIRST, false);
                    descFirst = descFirst.replace("{line}", description.get(0));
                    line = line.replace("{description}", descFirst);

                    // Other lines
                    String descLine = plugin.getMessage(SkillNodes.DESCRIPTION_OTHER, false);
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
        String reqString = plugin.getMessage(satisfied ?
                        SkillNodes.REQUIREMENT_MET
                        : SkillNodes.REQUIREMENT_NOT_MET, true);

        return reqString.replace("{name}", name)
                        .replace("{value}", value + "");
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
        ClassSkill thisSkill = this.skill;
        ClassSkill thatSkill = skill.skill;
        return
            skillReq != null && skill.skillReq == null ? 1
            : skillReq == null && skill.skillReq != null ? -1
            : thisSkill.getBase(SkillAttribute.LEVEL) > thatSkill.getBase(SkillAttribute.LEVEL) ? 1
            : thisSkill.getBase(SkillAttribute.LEVEL) < thatSkill.getBase(SkillAttribute.LEVEL) ? -1
            : thisSkill.getBase(SkillAttribute.COST) > thatSkill.getBase(SkillAttribute.COST) ? 1
            : thisSkill.getBase(SkillAttribute.COST) < thatSkill.getBase(SkillAttribute.COST) ? -1
            : name.compareTo(skill.name);
    }
}