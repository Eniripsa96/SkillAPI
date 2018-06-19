/**
 * SkillAPI
 * com.sucy.skill.api.skills.Skill
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Steven Sucy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software") to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.sucy.skill.api.skills;

import com.rit.sucy.config.Filter;
import com.rit.sucy.config.FilterType;
import com.rit.sucy.config.parse.DataSection;
import com.rit.sucy.text.TextFormatter;
import com.rit.sucy.version.VersionManager;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.ReadOnlySettings;
import com.sucy.skill.api.Settings;
import com.sucy.skill.api.event.SkillDamageEvent;
import com.sucy.skill.api.event.TrueDamageEvent;
import com.sucy.skill.api.player.PlayerCombos;
import com.sucy.skill.api.player.PlayerSkill;
import com.sucy.skill.api.util.DamageLoreRemover;
import com.sucy.skill.api.util.Data;
import com.sucy.skill.api.util.NumberParser;
import com.sucy.skill.dynamic.TempEntity;
import com.sucy.skill.hook.NoCheatHook;
import com.sucy.skill.hook.PluginChecker;
import com.sucy.skill.language.NotificationNodes;
import com.sucy.skill.language.RPGFilter;
import com.sucy.skill.language.SkillNodes;
import com.sucy.skill.log.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a template for a skill used in the RPG system. This is
 * the class to extend when creating your own custom skills.
 */
public abstract class Skill
{
    private static final DecimalFormat FORMAT = new DecimalFormat("#########0.0#");

    private final ArrayList<String> description = new ArrayList<String>();

    private List<String> iconLore;
    private ItemStack    indicator;
    private String       key;
    private String       name;
    private String       type;
    private String       message;
    private String       skillReq;
    private int          maxLevel;
    private int          skillReqLevel;
    private boolean      needsPermission;
    private int          combo;

    /**
     * The settings for the skill which include configurable stats
     * for your mechanics and the defaults such as mana cost, level
     * requirement, skill point cost, and cooldown.
     */
    protected final Settings         settings         = new Settings();
    private final   ReadOnlySettings readOnlySettings = new ReadOnlySettings(settings);

    /**
     * Initializes a new skill that doesn't require any other skill.
     *
     * @param name      name of the skill
     * @param type      descriptive type of the skill
     * @param indicator indicator to represent the skill
     * @param maxLevel  max level the skill can reach
     */
    public Skill(String name, String type, Material indicator, int maxLevel)
    {
        this(name, type, new ItemStack(indicator), maxLevel, null, 0);
    }

    /**
     * Initializes a skill that requires another skill to be upgraded
     * before it can be upgraded itself.
     *
     * @param name          name of the skill
     * @param type          descriptive type of the skill
     * @param indicator     indicator to represent the skill
     * @param maxLevel      max level the skill can reach
     * @param skillReq      name of the skill required to raise this one
     * @param skillReqLevel level of the required skill needed
     */
    public Skill(String name, String type, Material indicator, int maxLevel, String skillReq, int skillReqLevel)
    {
        this(name, type, new ItemStack(indicator), maxLevel, skillReq, skillReqLevel);
    }

    /**
     * Initializes a new skill that doesn't require any other skill.
     * The indicator's display name and lore will be used as the layout
     * for the skill tree display.
     *
     * @param name      name of the skill
     * @param type      descriptive type of the skill
     * @param indicator indicator to respresent the skill
     * @param maxLevel  max level the skill can reach
     */
    public Skill(String name, String type, ItemStack indicator, int maxLevel)
    {
        this(name, type, indicator, maxLevel, null, 0);
    }

    /**
     * Initializes a skill that requires another skill to be upgraded
     * before it can be upgraded itself. The indicator's display name
     * and lore will be used as the layout for the skill tree display.
     *
     * @param name          name of the skill
     * @param type          descriptive type of the skill
     * @param indicator     indicator to represent the skill
     * @param maxLevel      max level the skill can reach
     * @param skillReq      name of the skill required to raise this one
     * @param skillReqLevel level of the required skill needed
     */
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

        this.key = name.toLowerCase();
        this.type = type;
        this.name = name;
        this.indicator = indicator;
        this.maxLevel = maxLevel;
        this.skillReq = skillReq;
        this.skillReqLevel = skillReqLevel;
        this.needsPermission = false;

        this.message = SkillAPI.getLanguage().getMessage(NotificationNodes.CAST, true, FilterType.COLOR).get(0);
        this.iconLore = SkillAPI.getLanguage().getMessage(SkillNodes.LAYOUT, true, FilterType.COLOR);
    }

    /**
     * Checks whether or not the skill has been assigned
     * a click combination.
     *
     * @return true if has a combo, false otherwise
     */
    public boolean hasCombo()
    {
        return combo >= 0;
    }

    /**
     * Checks whether or not the skill can automatically
     * level up to the next stage.
     *
     * @return true if can level up automatically, false otherwise
     */
    public boolean canAutoLevel()
    {
        return getCost(0) == 0 && getCost(1) == 0;
    }

    /**
     * Retrieves the configuration key for the skill
     *
     * @return configuration key for the skill
     */
    public String getKey()
    {
        return key;
    }

    /**
     * Retrieves the name of the skill
     *
     * @return skill name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Retrieves the max level the skill can reach
     *
     * @return max skill level
     */
    public int getMaxLevel()
    {
        return maxLevel;
    }

    /**
     * Checks whether or not the skill has a message to display when cast.
     *
     * @return true if has a message, false otherwise
     */
    public boolean hasMessage()
    {
        return message != null && message.length() > 0;
    }

    /**
     * Retrieves the ID of the skill's combo
     *
     * @return combo ID
     */
    public int getCombo()
    {
        return combo;
    }

    /**
     * Sets the click combo for the skill
     *
     * @param combo new combo
     */
    public void setCombo(int combo)
    {
        this.combo = combo;
    }

    /**
     * Clears the set combo for the skill.
     * Only the API should call this.
     */
    public void clearCombo()
    {
        combo = -1;
    }

    /**
     * Retrieves the message for the skill to display when cast.
     *
     * @return cast message of the skill
     */
    public String getMessage()
    {
        return message;
    }

    /**
     * Checks whether or not the skill needs a permission for a player to use it.
     *
     * @return true if requires a permission, false otherwise
     */
    public boolean needsPermission()
    {
        return needsPermission;
    }

    /**
     * Retrieves the indicator representing the skill for menus
     *
     * @return indicator for the skill
     */
    public ItemStack getIndicator()
    {
        return indicator;
    }

    /**
     * Retrieves the descriptive type of the skill
     *
     * @return descriptive type of the skill
     */
    public String getType()
    {
        return type;
    }

    /**
     * Checks whether or not the skill requires another before leveling up
     *
     * @return true if requires another skill, false otherwise
     */
    public boolean hasSkillReq()
    {
        return SkillAPI.getSkill(skillReq) != null && skillReqLevel > 0;
    }

    /**
     * Retrieves the skill required to be upgraded before this one
     *
     * @return required skill
     */
    public String getSkillReq()
    {
        return skillReq;
    }

    /**
     * Retrieves the level of the required skill needed to be obtained
     * before this one can be upgraded.
     *
     * @return required skill level
     */
    public int getSkillReqLevel()
    {
        return skillReqLevel;
    }

    /**
     * Retrieves the skill's description
     *
     * @return description of the skill
     */
    public List<String> getDescription()
    {
        return description;
    }

    /**
     * Retrieves the level requirement for the skill to reach the next level
     *
     * @param level current level of the skill
     *
     * @return level requirement for the next level
     */
    public int getLevelReq(int level)
    {
        return (int) settings.getAttr(SkillAttribute.LEVEL, level + 1);
    }

    /**
     * Retrieves the mana cost of the skill
     *
     * @param level current level of the skill
     *
     * @return mana cost
     */
    public double getManaCost(int level)
    {
        return settings.getAttr(SkillAttribute.MANA, level);
    }

    /**
     * Retrieves the cooldown of the skill in seconds
     *
     * @param level current level of the skill
     *
     * @return cooldown
     */
    public double getCooldown(int level)
    {
        return settings.getAttr(SkillAttribute.COOLDOWN, level);
    }

    /**
     * Retrieves the range of the skill in blocks
     *
     * @param level current level of the skill
     *
     * @return target range
     */
    public double getRange(int level)
    {
        return settings.getAttr(SkillAttribute.RANGE, level);
    }

    /**
     * Retrieves the skill point cost of the skill
     *
     * @param level current level of the skill
     *
     * @return skill point cost
     */
    public int getCost(int level)
    {
        return (int) settings.getAttr(SkillAttribute.COST, level + 1);
    }

    /**
     * Retrieves the settings for the skill in a read-only format
     *
     * @return settings for the skill in a read-only format
     */
    public ReadOnlySettings getSettings()
    {
        return readOnlySettings;
    }

    /**
     * Checks whether or not this skill can be cast by players
     *
     * @return true if can be cast, false otherwise
     */
    public boolean canCast()
    {
        return this instanceof SkillShot || this instanceof TargetSkill;
    }

    /**
     * Retrieves the indicator for the skill while applying filters to match
     * the player-specific data.
     *
     * @param skillData player data
     *
     * @return filtered skill indicator
     */
    public ItemStack getIndicator(PlayerSkill skillData)
    {
        Player player = skillData.getPlayerData().getPlayer();

        ItemStack item = indicator.clone();
        item.setAmount(Math.max(1, skillData.getLevel()));
        ItemMeta meta = item.hasItemMeta() ? item.getItemMeta() : Bukkit.getItemFactory().getItemMeta(item.getType());
        ArrayList<String> lore = new ArrayList<String>();

        String lvlReq = SkillAPI.getLanguage().getMessage(skillData.getLevelReq() <= skillData.getPlayerClass().getLevel() ? SkillNodes.REQUIREMENT_MET : SkillNodes.REQUIREMENT_NOT_MET, true, FilterType.COLOR).get(0);
        String costReq = SkillAPI.getLanguage().getMessage(skillData.getCost() <= skillData.getPlayerClass().getPoints() ? SkillNodes.REQUIREMENT_MET : SkillNodes.REQUIREMENT_NOT_MET, true, FilterType.COLOR).get(0);
        lvlReq = lvlReq.substring(0, lvlReq.length() - 2);
        costReq = costReq.substring(0, costReq.length() - 2);

        String attrChanging = SkillAPI.getLanguage().getMessage(SkillNodes.ATTRIBUTE_CHANGING, true, FilterType.COLOR).get(0);
        String attrStatic = SkillAPI.getLanguage().getMessage(SkillNodes.ATTRIBUTE_NOT_CHANGING, true, FilterType.COLOR).get(0);

        for (String line : iconLore)
        {
            try
            {
                // General data
                line = line.replace("{level}", "" + skillData.getLevel())
                    .replace("{req:lvl}", lvlReq)
                    .replace("{req:level}", lvlReq)
                    .replace("{req:cost}", costReq)
                    .replace("{max}", "" + maxLevel)
                    .replace("{name}", name)
                    .replace("{type}", type);

                // Attributes
                while (line.contains("{attr:"))
                {
                    int start = line.indexOf("{attr:");
                    int end = line.indexOf("}", start);
                    String attr = line.substring(start + 6, end);
                    Object currValue = getAttr(player, attr, Math.max(1, skillData.getLevel()));
                    Object nextValue = getAttr(player, attr, Math.min(skillData.getLevel() + 1, maxLevel));
                    if (attr.equals("level") || attr.equals("cost"))
                    {
                        nextValue = (int) Math.floor(NumberParser.parseDouble(nextValue.toString()));
                        currValue = nextValue;
                    }

                    if (currValue.equals(nextValue))
                    {
                        line = line.replace("{attr:" + attr + "}", attrStatic.replace("{name}", getAttrName(attr)).replace("{value}", currValue.toString()));
                    }
                    else
                    {
                        line = line.replace("{attr:" + attr + "}", attrChanging.replace("{name}", getAttrName(attr)).replace("{value}", currValue.toString()).replace("{new}", nextValue.toString()));
                    }
                }

                // Full description
                if (line.contains("{desc}"))
                {
                    for (String descLine : description)
                    {
                        lore.add(line.replace("{desc}", descLine));
                    }
                    continue;
                }

                // Description segments
                if (line.contains("{desc:"))
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
                Logger.invalid("Skill icon filter for the skill \"" + name + "\" is invalid (Line = \"" + line + "\") - " + ex.getMessage());
            }
        }

        // Click string at the bottom
        if (SkillAPI.getSettings().isCombosEnabled() && canCast())
        {
            PlayerCombos combos = skillData.getPlayerData().getComboData();
            if (combos.hasCombo(this))
            {
                lore.add("");
                lore.add(combos.getComboString(this));
            }
        }

        if (lore.size() > 0)
        {
            meta.setDisplayName(lore.remove(0));
        }

        meta.setLore(lore);
        item.setItemMeta(meta);
        return DamageLoreRemover.removeAttackDmg(item);
    }

    /**
     * Formats an attribute name for applying to the indicator
     *
     * @param key attribute key
     *
     * @return formatted attribute name
     */
    protected String getAttrName(String key)
    {
        return TextFormatter.format(key);
    }

    /**
     * Retrieves an attribute value for using in the icon lore
     *
     * @param caster owner of the skill
     * @param key    attribute key
     * @param level  skill level
     *
     * @return attribute value
     */
    protected Object getAttr(LivingEntity caster, String key, int level)
    {
        Object result = settings.getObj(key, level);
        if (result instanceof Double)
        {
            return format((Double) result);
        }
        return result;
    }

    /**
     * Formats a double value to prevent excessive decimals
     *
     * @param value double value to format
     *
     * @return formatted double value
     */
    protected String format(double value)
    {
        String result = FORMAT.format(value);
        if (result.endsWith(".0"))
            return result.substring(0, result.length() - 2);
        else
            return result;
    }

    /**
     * Sends the skill message if one is present from the player to entities
     * within the given radius.
     *
     * @param player player to project the message from
     * @param radius radius to include targets of the message
     */
    public void sendMessage(Player player, double radius)
    {
        if (hasMessage())
        {
            radius *= radius;
            Location l = player.getLocation();
            for (Player p : player.getWorld().getPlayers())
            {
                if (p.getLocation().distanceSquared(l) < radius)
                {
                    p.sendMessage(RPGFilter.SKILL.setReplacement(getName()).apply(Filter.PLAYER.setReplacement(player.getName()).apply(message)));
                }
            }
        }
    }

    /**
     * Applies skill damage to the target, launching the skill damage event
     *
     * @param target target to receive the damage
     * @param damage amount of damage to deal
     * @param source source of the damage (skill caster)
     */
    public void damage(LivingEntity target, double damage, LivingEntity source)
    {
        if (target instanceof TempEntity) return;

        SkillDamageEvent event = new SkillDamageEvent(this, source, target, damage);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled())
        {
            if (source instanceof Player)
            {
                Player player = (Player) source;
                if (PluginChecker.isNoCheatActive()) NoCheatHook.exempt(player);
                skillDamage = true;
                target.setNoDamageTicks(0);
                target.damage(event.getDamage(), source);
                skillDamage = false;
                if (PluginChecker.isNoCheatActive()) NoCheatHook.unexempt(player);
            }
            else
            {
                skillDamage = true;
                VersionManager.damage(target, source, event.getDamage());
                skillDamage = false;
            }
        }
    }

    /**
     * Applies skill damage to the target, launching the skill damage event
     * and keeping the damage version compatible.
     *
     * @param target target to receive the damage
     * @param damage amount of damage to deal
     * @param source source of the damage (skill caster)
     */
    public void trueDamage(LivingEntity target, double damage, LivingEntity source)
    {
        if (target instanceof TempEntity) return;

        TrueDamageEvent event = new TrueDamageEvent(this, source, target, damage);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled() && event.getDamage() != 0)
            target.setHealth(Math.max(Math.min(target.getHealth() - event.getDamage(), target.getMaxHealth()), 0));
    }

    private static boolean skillDamage = false;

    /**
     * Checks whether or not the current damage event is due to
     * skills damaging an entity. This method is used by the API
     * and shouldn't be used by other plugins.
     *
     * @return true if caused by a skill, false otherwise
     */
    public static boolean isSkillDamage()
    {
        return skillDamage;
    }

    private static final String NAME      = "name";
    private static final String TYPE      = "type";
    private static final String LAYOUT    = "icon-lore";
    private static final String MAX       = "max-level";
    private static final String REQ       = "skill-req";
    private static final String REQLVL    = "skill-req-lvl";
    private static final String MSG       = "msg";
    private static final String PERM      = "needs-permission";
    private static final String DESC      = "desc";
    private static final String ATTR      = "attributes";
    private static final String ATTR_INFO = "attribute-info";
    private static final String COMBO     = "combo";

    /**
     * Saves the skill data to the configuration, overwriting all previous data
     *
     * @param config config to save to
     */
    public void save(DataSection config)
    {
        config.set(NAME, name);
        config.set(TYPE, type.replace(ChatColor.COLOR_CHAR, '&'));
        config.set(MAX, maxLevel);
        config.set(REQ, skillReq);
        config.set(REQLVL, skillReqLevel);
        config.set(PERM, needsPermission);
        if (combo >= 0 && canCast())
            config.set(COMBO, SkillAPI.getComboManager().getSaveString(combo));
        settings.save(config.createSection(ATTR));
        if (hasMessage())
        {
            config.set(MSG, message.replace(ChatColor.COLOR_CHAR, '&'));
        }
        Data.serializeIcon(indicator, config);
        config.set(DESC, description);
    }

    /**
     * Saves some of the skill data to the config, avoiding
     * overwriting any pre-existing data
     *
     * @param config config to save to
     */
    public void softSave(DataSection config)
    {

        boolean neededOnly = config.keys().size() > 0;
        if (!neededOnly)
        {
            save(config);
        }
    }

    /**
     * Loads skill data from the configuration
     *
     * @param config config to load from
     */
    public void load(DataSection config)
    {
        name = config.getString(NAME, name);
        type = TextFormatter.colorString(config.getString(TYPE, name));
        indicator = Data.parseIcon(config);
        maxLevel = config.getInt(MAX, maxLevel);
        skillReq = config.getString(REQ);
        if (skillReq == null || skillReq.length() == 0) skillReq = null;
        skillReqLevel = config.getInt(REQLVL, skillReqLevel);
        message = TextFormatter.colorString(config.getString(MSG, message));
        needsPermission = config.getString(PERM, needsPermission + "").equalsIgnoreCase("true");
        combo = SkillAPI.getComboManager().parseCombo(config.getString(COMBO));

        if (config.isList(DESC))
        {
            description.clear();
            description.addAll(config.getList(DESC));
        }
        if (config.isList(LAYOUT))
        {
            iconLore = TextFormatter.colorStringList(config.getList(LAYOUT));
        }

        settings.load(config.getSection(ATTR));
    }
}
