package com.sucy.skill.api.skills;

import com.rit.sucy.config.Filter;
import com.rit.sucy.config.FilterType;
import com.rit.sucy.config.LanguageConfig;
import com.rit.sucy.text.TextFormatter;
import com.sucy.skill.api.AttributeSet;
import com.sucy.skill.api.player.PlayerSkill;
import com.sucy.skill.data.io.keys.SkillValues;
import com.sucy.skill.language.OtherNodes;
import com.sucy.skill.language.RPGFilter;
import com.sucy.skill.language.SkillNodes;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public abstract class Skill
{

    private static final DecimalFormat FORMAT = new DecimalFormat("#########0.0#");

    private final ArrayList<String> permissions = new ArrayList<String>();
    private final ArrayList<String> description = new ArrayList<String>();

    private String    name;
    private String    type;
    private ItemStack indicator;
    private int       maxLevel;
    private String    skillReq;
    private int       skillReqLevel;
    private String    message;
    private boolean   needsPermission;

    protected final AttributeSet attributes = new AttributeSet();

    public Skill(String name, String type, Material indicator, int maxLevel)
    {
        this(name, type, new ItemStack(indicator), maxLevel, null, 0);
    }

    public Skill(String name, String type, Material indicator, int maxLevel, String skillReq, int skillReqLevel)
    {
        this(name, type, new ItemStack(indicator), maxLevel, skillReq, skillReqLevel);
    }

    public Skill(String name, String type, ItemStack indicator, int maxLevel)
    {
        this(name, type, indicator, maxLevel, null, 0);
    }

    public Skill(String name, String type, ItemStack indicator, int maxLevel, String skillReq, int skillReqLevel)
    {
        this.type = type;
        this.name = name;
        this.indicator = indicator;
        this.maxLevel = maxLevel;
        this.skillReq = skillReq;
        this.skillReqLevel = skillReqLevel;
        needsPermission = false;
    }

    public String getName()
    {
        return name;
    }

    public int getMaxLevel()
    {
        return maxLevel;
    }

    public boolean hasMessage()
    {
        return message != null;
    }

    public String getMessage()
    {
        return message;
    }

    public boolean needsPermission()
    {
        return needsPermission;
    }

    public boolean hasPermissions()
    {
        return permissions.size() > 0;
    }

    public List<String> getPermissions()
    {
        return permissions;
    }

    public ItemStack getIndicator()
    {
        return indicator;
    }

    public String getType()
    {
        return type;
    }

    public String getSkillReq()
    {
        return skillReq;
    }

    public int getSkillReqLevel()
    {
        return skillReqLevel;
    }

    public List<String> getDescription()
    {
        return description;
    }

    public int getLevelReq(int level)
    {
        return (int) attributes.get(SkillAttribute.LEVEL, level + 1);
    }

    public double getManaCost(int level)
    {
        return attributes.get(SkillAttribute.MANA, level);
    }

    public double getCooldown(int level)
    {
        return attributes.get(SkillAttribute.COOLDOWN, level);
    }

    public double getRange(int level)
    {
        return attributes.get(SkillAttribute.RANGE, level);
    }

    public int getCost(int level)
    {
        return (int) attributes.get(SkillAttribute.COST, level + 1);
    }

    public boolean canCast()
    {
        return this instanceof SkillShot || this instanceof TargetSkill;
    }

    public ItemStack getIndicator(PlayerSkill skillData)
    {

        LanguageConfig lang = skillData.getAPI().getLanguage();
        List<String> layout = lang.getMessage(SkillNodes.LAYOUT, false);
        boolean first = true;

        ItemStack item = indicator.clone();
        item.setAmount(Math.max(1, skillData.getLevel()));
        ItemMeta meta = item.hasItemMeta() ? item.getItemMeta() : Bukkit.getItemFactory().getItemMeta(item.getType());
        ArrayList<String> lore = new ArrayList<String>();

        // Cycle through each line, parse it, and add it to the display
        for (String line : layout)
        {
            List<String> results = new ArrayList<String>();

            // Title filter
            if (line.contains("{title}"))
            {
                String title = lang.getMessage(SkillNodes.TITLE, false).get(0);

                title = title.replace("{name}", getName())
                        .replace("{level}", skillData.getLevel() + "")
                        .replace("{max}", getMaxLevel() + "");

                line = line.replace("{title}", title);
            }

            // Type filter
            if (line.contains("{type}"))
            {
                String type = lang.getMessage(SkillNodes.TYPE, false).get(0);
                type = type.replace("{name}", this.type);
                line = line.replace("{type}", type);
            }

            // Requirement Filter
            if (line.contains("{requirements}"))
            {

                int requiredLevel = getLevelReq(skillData.getLevel());
                line = line.replace("{requirements}",
                        getRequirementString(skillData.getAPI().getLanguage(), SkillAttribute.LEVEL, requiredLevel,
                                skillData.getPlayerClass().getLevel() >= requiredLevel));

                int requiredPoints = getCost(skillData.getLevel());
                results.add(getRequirementString(skillData.getAPI().getLanguage(), SkillAttribute.COST, requiredPoints,
                        skillData.getPlayerClass().getPoints() >= requiredPoints));

                String skillReq = getSkillReq();
                if (skillReq != null)
                {
                    results.add(getRequirementString(skillData.getAPI().getLanguage(), skillReq, skillReqLevel,
                            skillData.getPlayerData().getSkillLevel(skillReq) >= skillReqLevel));
                }
            }

            // Attributes filter
            if (line.contains("{attributes}"))
            {

                boolean useLine = true;

                // Go through each attribute
                for (String attribute : attributes.getNames())
                {
                    if (attribute.equals(SkillAttribute.COST) || attribute.equals(SkillAttribute.LEVEL))
                    {
                        continue;
                    }

                    // Get the values
                    double oldValue = attributes.get(attribute, skillData.getLevel());
                    double newValue = attributes.get(attribute, skillData.getLevel() + 1);

                    // Level 0 doesn't count
                    if (skillData.getLevel() == 0)
                    {
                        oldValue = newValue;
                    }
                    if (skillData.getLevel() == maxLevel)
                    {
                        newValue = oldValue;
                    }

                    String attLine;

                    // Changing attribute
                    if (oldValue != newValue)
                    {

                        attLine = lang.getMessage(SkillNodes.ATTRIBUTE_CHANGING, false).get(0);
                        attLine = attLine.replace("{new}", format(newValue) + "");
                    }

                    // Not changing attribute
                    else
                    {
                        attLine = lang.getMessage(SkillNodes.ATTRIBUTE_NOT_CHANGING, false).get(0);
                    }

                    attLine = attLine.replace("{value}", format(oldValue) + "")
                            .replace("{name}", attribute.replace("Mana", skillData.getPlayerClass().getData().getManaName()));

                    // Line replace
                    if (useLine)
                    {
                        useLine = false;
                        line = line.replace("{attributes}", attLine);
                    }

                    // Add to results
                    else
                    {
                        results.add(attLine);
                    }
                }

                // No attributes present
                if (useLine)
                {
                    line = line.replace("{attributes}", lang.getMessage(SkillNodes.ATTRIBUTE_NONE, false).get(0));
                }
            }

            // Description filter
            if (line.contains("{description}"))
            {

                // No description
                if (getDescription().size() == 0)
                {
                    line = line.replace("{description}", lang.getMessage(SkillNodes.DESCRIPTION_NONE, false).get(0));
                }

                // Go through each line
                else
                {

                    // First line
                    String descFirst = lang.getMessage(SkillNodes.DESCRIPTION_FIRST, false).get(0);
                    descFirst = descFirst.replace("{line}", description.get(0));
                    line = line.replace("{description}", descFirst);

                    // Other lines
                    String descLine = lang.getMessage(SkillNodes.DESCRIPTION_OTHER, false).get(0);
                    for (int i = 1; i < description.size(); i++)
                    {
                        results.add(descLine.replace("{line}", description.get(i)));
                    }
                }
            }

            results.add(0, line);

            // Add the resulting lines
            for (String result : results)
            {

                result = TextFormatter.colorString(result);

                // First line is assigned to the item's name
                if (first)
                {
                    first = false;
                    meta.setDisplayName(result);
                }

                // Anything else appends to the lore
                else
                {
                    lore.add(result);
                }
            }
        }

        // Click string at the bottom
        if (skillData.getAPI().getSettings().isUseClickCombos() && canCast())
        {
            lore.add("");
            lore.add(ChatColor.GOLD + skillData.getAPI().getComboManager().getComboString(name));
        }

        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private String format(double value)
    {
        if ((int) value == value)
        {
            return "" + (int) value;
        }
        return FORMAT.format(value);
    }

    private String getRequirementString(LanguageConfig language, String name, int value, boolean satisfied)
    {
        String reqString = language.getMessage(satisfied ?
                SkillNodes.REQUIREMENT_MET
                : SkillNodes.REQUIREMENT_NOT_MET, true).get(0);

        return reqString.replace("{name}", name)
                .replace("{value}", value + "");
    }

    public void sendMessage(LanguageConfig lang, Player player, double radius)
    {
        lang.sendMessage(hasMessage() ? getMessage() : OtherNodes.SKILL_CAST,
                player.getLocation(), radius, FilterType.COLOR,
                Filter.PLAYER.setReplacement(player.getName()),
                RPGFilter.SKILL.setReplacement(getName()));
    }

    public void validateDefaults()
    {
        attributes.checkDefault(SkillAttribute.LEVEL, 1, 0);
        attributes.checkDefault(SkillAttribute.COST, 1, 0);
        if (this instanceof SkillShot || this instanceof TargetSkill)
        {
            attributes.checkDefault(SkillAttribute.MANA, 0, 0);
            attributes.checkDefault(SkillAttribute.COOLDOWN, 0, 0);
            if (this instanceof TargetSkill)
            {
                attributes.checkDefault(SkillAttribute.RANGE, 6, 0);
            }
        }
    }

    public void update(ConfigurationSection config)
    {

        // Attributes
        attributes.load(config.getConfigurationSection("attributes"));

        // Max level
        maxLevel = config.getInt(SkillValues.MAX_LEVEL);

        // Description
        List<String> description = config.getStringList(SkillValues.DESCRIPTION);
        if (description != null)
        {
            this.description.clear();
            this.description.addAll(description);
        }

        // Skill Requirement
        if (config.contains(SkillValues.SKILL_REQ))
        {
            skillReq = config.getString(SkillValues.SKILL_REQ);
            skillReqLevel = config.getInt(SkillValues.SKILL_REQ_LEVEL, 1);
        }

        // Icon
        parseIndicator(config.getString(SkillValues.INDICATOR));

        // Message
        message = config.getString(SkillValues.MESSAGE);

        // Needed permission
        if (config.contains(SkillValues.NEEDS_PERMISSION))
        {
            needsPermission = config.getBoolean(SkillValues.NEEDS_PERMISSION);
        }

        // Permissions
        if (config.contains(SkillValues.PERMISSIONS))
        {
            permissions.clear();
            permissions.addAll(config.getStringList(SkillValues.PERMISSIONS));
        }
    }

    protected void parseIndicator(String string)
    {
        String[] pieces;
        if (string.contains(","))
        {
            pieces = string.split(",");
        }
        else
        {
            pieces = new String[] { string };
        }
        Material icon = Material.valueOf(pieces[0]);
        if (icon == null)
        {
            return;
        }
        byte data = 0;
        if (pieces.length > 1)
        {
            data = Byte.parseByte(pieces[1]);
        }
        ItemStack item = new ItemStack(icon, 1, (short) 0, data);

        indicator = item;
    }
}
