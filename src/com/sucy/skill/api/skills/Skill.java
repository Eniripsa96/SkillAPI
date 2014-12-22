package com.sucy.skill.api.skills;

import com.rit.sucy.config.Filter;
import com.rit.sucy.config.FilterType;
import com.rit.sucy.config.LanguageConfig;
import com.rit.sucy.text.TextFormatter;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.Settings;
import com.sucy.skill.api.player.PlayerSkill;
import com.sucy.skill.api.util.Data;
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

    private final ArrayList<String> description = new ArrayList<String>();

    private String    name;
    private String    type;
    private ItemStack indicator;
    private int       maxLevel;
    private String    skillReq;
    private int       skillReqLevel;
    private String    message;
    private String    attrInfo;
    private boolean   needsPermission;

    protected final Settings settings = new Settings();

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
        if (name == null)
        {
            throw new IllegalArgumentException("Skill name cannot be null");
        }

        // Default values
        if (type == null)
        {
            type = "Unknown type";
        }
        if (indicator == null)
        {
            indicator = new ItemStack(Material.APPLE);
        }
        if (maxLevel < 1)
        {
            maxLevel = 1;
        }

        this.type = type;
        this.name = name;
        this.indicator = indicator;
        this.maxLevel = maxLevel;
        this.skillReq = skillReq;
        this.skillReqLevel = skillReqLevel;
        this.attrInfo = "";
        this.needsPermission = false;
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

    public ItemStack getIndicator()
    {
        return indicator;
    }

    public String getSerializedInidcator()
    {
        return indicator.getType().name() + "," + indicator.getData().getData();
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
        return (int) settings.get(SkillAttribute.LEVEL, level + 1);
    }

    public double getManaCost(int level)
    {
        return settings.get(SkillAttribute.MANA, level);
    }

    public double getCooldown(int level)
    {
        return settings.get(SkillAttribute.COOLDOWN, level);
    }

    public double getRange(int level)
    {
        return settings.get(SkillAttribute.RANGE, level);
    }

    public int getCost(int level)
    {
        return (int) settings.get(SkillAttribute.COST, level + 1);
    }

    public boolean canCast()
    {
        return this instanceof SkillShot || this instanceof TargetSkill;
    }

    public ItemStack getIndicator(PlayerSkill skillData)
    {

        LanguageConfig lang = SkillAPI.getLanguage();
        List<String> layout = lang.getMessage(SkillNodes.LAYOUT);
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
                String title = lang.getMessage(SkillNodes.TITLE).get(0);

                title = title.replace("{name}", getName())
                        .replace("{level}", skillData.getLevel() + "")
                        .replace("{max}", getMaxLevel() + "");

                line = line.replace("{title}", title);
            }

            // Type filter
            if (line.contains("{type}"))
            {
                String type = lang.getMessage(SkillNodes.TYPE).get(0);
                type = type.replace("{name}", this.type);
                line = line.replace("{type}", type);
            }

            // Requirement Filter
            if (line.contains("{requirements}"))
            {

                int requiredLevel = getLevelReq(skillData.getLevel());
                line = line.replace("{requirements}",
                        getRequirementString(SkillAPI.getLanguage(), SkillAttribute.LEVEL, requiredLevel,
                                skillData.getPlayerClass().getLevel() >= requiredLevel));

                int requiredPoints = getCost(skillData.getLevel());
                results.add(getRequirementString(SkillAPI.getLanguage(), SkillAttribute.COST, requiredPoints,
                        skillData.getPlayerClass().getPoints() >= requiredPoints));

                String skillReq = getSkillReq();
                if (skillReq != null)
                {
                    results.add(getRequirementString(SkillAPI.getLanguage(), skillReq, skillReqLevel,
                            skillData.getPlayerData().getSkillLevel(skillReq) >= skillReqLevel));
                }
            }

            // Attributes filter
            if (line.contains("{attributes}"))
            {
                line = line.replace("{attributes}", attrInfo);
            }

            // Description filter
            if (line.contains("{description}"))
            {

                // No description
                if (getDescription().size() == 0)
                {
                    line = line.replace("{description}", lang.getMessage(SkillNodes.DESCRIPTION_NONE).get(0));
                }

                // Go through each line
                else
                {

                    // First line
                    String descFirst = lang.getMessage(SkillNodes.DESCRIPTION_FIRST).get(0);
                    descFirst = descFirst.replace("{line}", description.get(0));
                    line = line.replace("{description}", descFirst);

                    // Other lines
                    String descLine = lang.getMessage(SkillNodes.DESCRIPTION_OTHER).get(0);
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
        if (SkillAPI.getSettings().isUseClickCombos() && canCast())
        {
            lore.add("");
            lore.add(ChatColor.GOLD + SkillAPI.getComboManager().getComboString(name));
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
                : SkillNodes.REQUIREMENT_NOT_MET).get(0);

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

    private static final String NAME      = "name";
    private static final String TYPE      = "type";
    private static final String ITEM      = "item";
    private static final String MAX       = "max";
    private static final String REQ       = "req";
    private static final String REQLVL    = "req-lvl";
    private static final String MSG       = "msg";
    private static final String PERM      = "reqperm";
    private static final String DESC      = "desc";
    private static final String ATTR      = "attributes";
    private static final String ATTR_INFO = "attribute-info";

    public void save(ConfigurationSection config)
    {
        config.set(NAME, name);
        config.set(TYPE, type.replace(ChatColor.COLOR_CHAR, '&'));
        config.set(ITEM, getSerializedInidcator());
        config.set(MAX, maxLevel);
        config.set(REQ, skillReq);
        config.set(REQLVL, skillReqLevel);
        config.set(MSG, message.replace(ChatColor.COLOR_CHAR, '&'));
        config.set(PERM, needsPermission);
        config.set(DESC, description);
        settings.save(config.createSection(ATTR));
    }

    public void softSave(ConfigurationSection config)
    {

        boolean neededOnly = config.getKeys(false).size() > 0;

        if (!config.contains(NAME))
        {
            config.set(NAME, name);
        }
        if (!config.isSet(TYPE))
        {
            config.set(TYPE, type.replace(ChatColor.COLOR_CHAR, '&'));
        }
        if (!config.isSet(ITEM))
        {
            config.set(ITEM, getSerializedInidcator());
        }
        if (!config.isSet(MAX))
        {
            config.set(MAX, maxLevel);
        }
        if (skillReq != null && !neededOnly)
        {
            config.set(REQ, skillReq);
            config.set(REQLVL, skillReqLevel);
        }
        if (message != null && !neededOnly)
        {
            config.set(MSG, message.replace(ChatColor.COLOR_CHAR, '&'));
        }
        if (!neededOnly)
        {
            config.set(PERM, needsPermission);
        }
        if (!config.isSet(DESC))
        {
            config.set(DESC, description);
        }
        if (!config.isSet(ATTR_INFO))
        {
            config.set(ATTR_INFO, attrInfo);
        }
    }

    public void load(ConfigurationSection config)
    {
        name = config.getString(NAME, name);
        type = TextFormatter.colorString(config.getString(TYPE, name));
        Data.parseIcon(config.getString(ITEM, getSerializedInidcator()));
        maxLevel = config.getInt(MAX, maxLevel);
        skillReq = config.getString(REQ);
        skillReqLevel = config.getInt(REQLVL, skillReqLevel);
        message = TextFormatter.colorString(config.getString(MSG));
        needsPermission = config.getBoolean(PERM, needsPermission);
        attrInfo = config.getString(ATTR_INFO, attrInfo);

        if (config.isList(DESC))
        {
            description.clear();
            description.addAll(config.getStringList(DESC));
        }

        settings.load(config.getConfigurationSection(ATTR));
    }
}