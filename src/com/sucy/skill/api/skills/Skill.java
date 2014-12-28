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

    private List<String> iconLore;
    private ItemStack indicator;
    private String    key;
    private String    name;
    private String    type;
    private String    message;
    private String    attrInfo;
    private String    skillReq;
    private int       maxLevel;
    private int       skillReqLevel;
    private boolean   needsPermission;
    private boolean   layoutChanged = false;

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

        this.key = name;
        this.type = type;
        this.name = name;
        this.indicator = indicator;
        this.maxLevel = maxLevel;
        this.skillReq = skillReq;
        this.skillReqLevel = skillReqLevel;
        this.attrInfo = "";
        this.needsPermission = false;

        iconLore = SkillAPI.getLanguage().getMessage(SkillNodes.LAYOUT);
    }

    public String getKey()
    {
        return key;
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

        String lvlReq = SkillAPI.getLanguage().getMessage(getLevelReq(skillData.getLevel()) <= skillData.getPlayerClass().getLevel() ? SkillNodes.REQUIREMENT_MET : SkillNodes.REQUIREMENT_NOT_MET, true, FilterType.COLOR).get(0);
        lvlReq = lvlReq.replace("{name}", "Level").replace("{value}", "" + getLevelReq(skillData.getLevel()));

        String costReq = SkillAPI.getLanguage().getMessage(getCost(skillData.getLevel()) <= skillData.getPlayerClass().getPoints() ? SkillNodes.REQUIREMENT_MET : SkillNodes.REQUIREMENT_NOT_MET, true, FilterType.COLOR).get(0);
        costReq = costReq.replace("{name}", "Cost").replace("{value}", "" + getCost(skillData.getLevel()));

        String attrChanging = SkillAPI.getLanguage().getMessage(SkillNodes.ATTRIBUTE_CHANGING, true, FilterType.COLOR).get(0);
        String attrStatic = SkillAPI.getLanguage().getMessage(SkillNodes.ATTRIBUTE_NOT_CHANGING, true, FilterType.COLOR).get(0);

        for (String line : iconLore)
        {
            try
            {
                // General data
                line = line.replace("{level}", "" + skillData.getLevel())
                        .replace("{req:lvl}", lvlReq)
                        .replace("{req:cost}", costReq)
                        .replace("{max}", "" + maxLevel)
                        .replace("{name}", name)
                        .replace("{type}", type);

                // Attributes
                if (line.contains("{attr:"))
                {
                    int start = line.indexOf("{attr:");
                    int end = line.indexOf("}", start);
                    String attr = line.substring(start + 6, end);
                    Object currValue = getAttrValue(attr, Math.min(1, skillData.getLevel()));
                    Object nextValue = getAttrValue(attr, Math.max(skillData.getLevel() + 1, maxLevel));

                    if (currValue == nextValue)
                    {
                        line = line.replace("{attr:" + attr + "}", attrStatic.replace("{name}", getAttrName(attr)).replace("{value}", currValue.toString()));
                    }
                    else
                    {
                        line = line.replace("{attr:" + attr + "}", attrChanging.replace("{name}", getAttrName(attr)).replace("{value}", currValue.toString()).replace("{value}", nextValue.toString()));
                    }
                }

                // Full description
                else if (line.contains("{desc}"))
                {
                    for (String descLine : description)
                    {
                        lore.add(line.replace("{desc}", descLine));
                    }
                    continue;
                }

                // Description segments
                else if (line.contains("{desc:"))
                {
                    int start = line.indexOf("{desc:");
                    int end = line.indexOf("}", start);
                    String lineInfo = line.substring(start + 6, end);
                    String[] split = lineInfo.contains("-") ? lineInfo.split("-") : new String[] { lineInfo, lineInfo };
                    start = Integer.parseInt(split[0]) - 1;
                    end = (split[1].equals("x") ? description.size() : Integer.parseInt(split[1]));
                    for (int i = start; i < end && i < description.size(); i++)
                    {
                        lore.add(line.replace("{desc:" + lineInfo + "}", description.get(i)));
                    }
                    continue;
                }

                lore.add(line);
            }

            // Lines with invalid filters are ignored
            catch (Exception ex)
            {
                Bukkit.getLogger().warning("Skill icon filter for the skill \"" + name + "\" is invalid (Line = \"" + line + "\"");
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

    protected Object getAttrValue(String key, int level)
    {
        return settings.getObj(key, level);
    }

    protected String getAttrName(String key)
    {
        return TextFormatter.format(key);
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
    private static final String LAYOUT    = "layout";
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
        config.set(LAYOUT, iconLore);
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
        if (!config.isSet(LAYOUT))
        {
            config.set(LAYOUT, iconLore);
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
        if (config.isList(LAYOUT))
        {
            iconLore = config.getStringList(LAYOUT);
        }

        settings.load(config.getConfigurationSection(ATTR));
    }
}
