/**
 * SkillAPI
 * com.sucy.skill.api.classes.RPGClass
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
package com.sucy.skill.api.classes;

import com.rit.sucy.config.parse.DataSection;
import com.rit.sucy.text.TextFormatter;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.ReadOnlySettings;
import com.sucy.skill.api.Settings;
import com.sucy.skill.api.enums.ExpSource;
import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.api.util.Data;
import com.sucy.skill.data.GroupSettings;
import com.sucy.skill.log.LogType;
import com.sucy.skill.log.Logger;
import com.sucy.skill.tree.SkillTree;
import com.sucy.skill.tree.map.MapTree;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

/**
 * Represents a template for a class used in the RPG system. This is
 * the class to extend when creating your own classes.
 */
public abstract class RPGClass
{
    private final ArrayList<Skill> skills = new ArrayList<Skill>();

    private SkillTree skillTree;
    private String    parent;
    private ItemStack icon;
    private TreeType  tree;
    private String    name;
    private String    prefix;
    private String    group;
    private String    mana;
    private int       maxLevel;
    private int       expSources;
    private double    manaRegen;

    /**
     * Whether or not the class requires permissions
     * in order to be professed into
     */
    protected boolean needsPermission;

    /**
     * The settings for your class. This will include the
     * health and mana scaling for the class.
     */
    protected final Settings         settings         = new Settings();
    private final   ReadOnlySettings readOnlySettings = new ReadOnlySettings(settings);

    ///////////////////////////////////////////////////////
    //                                                   //
    //                   Constructors                    //
    //                                                   //
    ///////////////////////////////////////////////////////

    /**
     * Initializes a class template that does not profess from other
     * classes but is rather a starting class.
     *
     * @param name     name of the class
     * @param icon     icon representing the class in menus
     * @param maxLevel max level the class can reach
     */
    protected RPGClass(String name, ItemStack icon, int maxLevel)
    {
        this(name, icon, maxLevel, null, null);
    }

    /**
     * Initializes a class template that can profess from the parent
     * class when that class reaches its max level.
     *
     * @param name     name of the class
     * @param icon     icon representing the class in menus
     * @param maxLevel max level the class can reach
     * @param parent   parent class to profess from
     */
    protected RPGClass(String name, ItemStack icon, int maxLevel, String parent)
    {
        this(name, icon, maxLevel, null, parent);
    }

    /**
     * Initializes a class template that can profess from the parent
     * class when that class reaches its max level. The group is
     * the category for the class which determines which classes
     * can be professed into simultaneously. Classes in the same
     * group will not be able to both be professed into at the same
     * time while classes in different groups are able to. For example,
     * a class "Warrior" in the "class" group and an "Elf" class in the
     * "race" group can both be professed as by a player at the same
     * time, giving the player the stats and skills from both.
     *
     * @param name     name of the class
     * @param icon     icon representing the class in menus
     * @param maxLevel max level the class can reach
     * @param group    class group
     * @param parent   parent class to profess from
     */
    protected RPGClass(String name, ItemStack icon, int maxLevel, String group, String parent)
    {
        this.parent = parent;
        this.icon = icon;
        this.name = name;
        this.prefix = name;
        this.group = group == null ? "class" : group.toLowerCase();
        this.mana = "Mana";
        this.maxLevel = maxLevel;
        this.tree = DefaultTreeType.REQUIREMENT;

        setAllowedExpSources(ExpSource.MOB, ExpSource.COMMAND, ExpSource.QUEST);

        if (this instanceof Listener)
        {
            Bukkit.getPluginManager().registerEvents((Listener) this, Bukkit.getPluginManager().getPlugin("SkillAPI"));
        }
    }

    ///////////////////////////////////////////////////////
    //                                                   //
    //                 Accessor Methods                  //
    //                                                   //
    ///////////////////////////////////////////////////////

    /**
     * Retrieves the name of the class
     *
     * @return class name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Retrieves the prefix of the class
     *
     * @return class prefix
     */
    public String getPrefix()
    {
        return prefix;
    }

    /**
     * Checks whether or not the class needs permission in
     * order to profess as it
     *
     * @return true if needs permission, false otherwise
     */
    public boolean isNeedsPermission()
    {
        return needsPermission;
    }

    /**
     * Retrieves the color of the class's prefix
     *
     * @return prefix color
     */
    public ChatColor getPrefixColor()
    {
        String colors = ChatColor.getLastColors(prefix);
        if (colors.length() < 2)
        {
            return ChatColor.WHITE;
        }
        return ChatColor.getByChar(colors.charAt(1));
    }

    /**
     * Retrieves the skill tree representing the class skills
     *
     * @return class skill tree
     */
    public SkillTree getSkillTree()
    {
        return skillTree;
    }

    /**
     * Retrieves the group this class falls in
     *
     * @return class group
     */
    public String getGroup()
    {
        return group;
    }

    /**
     * Retrieves the settings for the class's group
     *
     * @return settings for the class's group
     */
    public GroupSettings getGroupSettings()
    {
        return SkillAPI.getSettings().getGroupSettings(group);
    }

    /**
     * Checks whether or not the class professes from another class
     *
     * @return true if professes from another class, false otherwise
     */
    public boolean hasParent()
    {
        return getParent() != null;
    }

    /**
     * Retrieves the parent of this class
     *
     * @return parent of the class or null if none
     */
    public RPGClass getParent()
    {
        return SkillAPI.getClass(parent);
    }

    public RPGClass getRoot() {
        RPGClass root = this;
        while (root.parent != null) root = root.getParent();
        return root;
    }

    /**
     * Retrieves the icon representing this class for menus
     *
     * @return icon representation of the class
     */
    public ItemStack getIcon()
    {
        return icon;
    }

    /**
     * Checks whether or not the class receives experience
     * from the given source
     *
     * @param source source of experience to check
     *
     * @return true if receives experience from the source, false otherwise
     */
    public boolean receivesExp(ExpSource source)
    {
        return (expSources & source.getId()) != 0;
    }

    /**
     * Retrieves the max level in which this class can reach
     *
     * @return max level this class can reach
     */
    public int getMaxLevel()
    {
        return maxLevel;
    }

    /**
     * Retrieves the required amount of experience this class need to level
     *
     * @param level current level of the class
     *
     * @return required amount of experience to reach the next level
     */
    public int getRequiredExp(int level)
    {
        return SkillAPI.getSettings().getRequiredExp(level);
    }

    /**
     * Retrieves the amount of max health this class provides
     *
     * @param level current level of the class
     *
     * @return amount of max health the class provides
     */
    public double getHealth(int level)
    {
        return settings.getAttr(ClassAttribute.HEALTH, level);
    }

    /**
     * Retrieves the base amount of health for the class
     *
     * @return base amount of health for the class
     */
    public double getBaseHealth()
    {
        return settings.getBase(ClassAttribute.HEALTH);
    }

    /**
     * Retrieves the amount of health gained per level for the class
     *
     * @return health gained per level
     */
    public double getHealthScale()
    {
        return settings.getScale(ClassAttribute.HEALTH);
    }

    /**
     * Retrieves the amount of max mana this class provides
     *
     * @param level current level of the class
     *
     * @return amount of max mana the class provides
     */
    public double getMana(int level)
    {
        return settings.getAttr(ClassAttribute.MANA, level);
    }

    /**
     * Retrieves the base amount of mana for the class
     *
     * @return base amount of mana for the class
     */
    public double getBaseMana()
    {
        return settings.getBase(ClassAttribute.MANA);
    }

    /**
     * Retrieves the amount of mana gained per level for the class
     *
     * @return mana gained per level
     */
    public double getManaScale()
    {
        return settings.getScale(ClassAttribute.MANA);
    }

    /**
     * Gets the class attribute amount for the given level
     *
     * @param key   attribute key
     * @param level class level
     *
     * @return attribute amount
     */
    public int getAttribute(String key, int level)
    {
        return (int) settings.getAttr(key, level, 0);
    }

    /**
     * Retrieves the settings for the class in a read-only format
     *
     * @return settings for the class in a read-only format
     */
    public ReadOnlySettings getSettings()
    {
        return readOnlySettings;
    }

    /**
     * Retrieves the alias for mana this class uses
     *
     * @return mana alias for the class
     */
    public String getManaName()
    {
        return mana;
    }

    /**
     * Retrieves the list of skills this class provides a player
     *
     * @return list of skills provided by the class
     */
    public ArrayList<Skill> getSkills()
    {
        ArrayList<Skill> skills = new ArrayList<Skill>();
        skills.addAll(this.skills);
        if (hasParent() && !getGroupSettings().isProfessReset()) skills.addAll(getParent().getSkills());
        return skills;
    }

    /**
     * Checks whether or not this class has mana regeneration
     *
     * @return true if has mana regeneration, false otherwise
     */
    public boolean hasManaRegen()
    {
        return manaRegen > 0;
    }

    /**
     * Retrieves the amount of mana regeneration this class has
     *
     * @return mana regeneration per update or a non-positive number if no regeneration
     */
    public double getManaRegen()
    {
        return manaRegen;
    }

    /**
     * Retrieves the list of child classes that the player has
     * as options to profess into upon reaching max level.
     *
     * @return list of child classes
     */
    public ArrayList<RPGClass> getOptions()
    {
        ArrayList<RPGClass> list = new ArrayList<RPGClass>();
        for (RPGClass c : SkillAPI.getClasses().values())
        {
            if (c.getParent() == this)
            {
                list.add(c);
            }
        }
        return list;
    }

    ///////////////////////////////////////////////////////
    //                                                   //
    //                 Setting Methods                   //
    //                                                   //
    ///////////////////////////////////////////////////////

    /**
     * Adds a skill to the class by name. This will not add it to the
     * skill tree or to players who are already professed as the class.
     *
     * @param name name of the skill
     */
    public void addSkill(String name)
    {
        Skill skill = SkillAPI.getSkill(name);
        if (skill != null)
        {
            skills.add(skill);
        }
        else
        {
            Logger.invalid("Class \"" + this.name + "\" tried to add an invalid skill - \"" + name + "\"");
        }
    }

    /**
     * Adds multiple skills to the class by name. This will not add it to
     * the skill tree or to players who are already professed as the class.
     *
     * @param names names of the skills
     */
    public void addSkills(String... names)
    {
        for (String name : names)
        {
            addSkill(name);
        }
    }

    /**
     * Sets the prefix for the class
     *
     * @param prefix class prefix
     */
    public void setPrefix(String prefix)
    {
        this.prefix = prefix;
    }

    /**
     * Sets the mana alias for the class
     *
     * @param name mana alias
     */
    public void setManaName(String name)
    {
        mana = name;
    }

    /**
     * Sets the experience sources this class can receive experience from.
     *
     * @param sources allowed sources of experience
     */
    public void setAllowedExpSources(ExpSource... sources)
    {
        expSources = 0;
        for (ExpSource source : sources)
        {
            allowExpSource(source);
        }
    }

    /**
     * Adds an experience source to the list of allowed sources for the class.
     *
     * @param source allowed source of experience
     */
    public void allowExpSource(ExpSource source)
    {
        expSources |= source.getId();
    }

    /**
     * Removes an experience source from the list of allowed
     * sources for the class.
     *
     * @param source disallowed source of experience
     */
    public void disallowExpSource(ExpSource source)
    {
        expSources &= (~source.getId());
    }

    /**
     * Sets the amount of mana regen this class has
     *
     * @param amount amount of mana regen
     */
    public void setManaRegen(double amount)
    {
        this.manaRegen = amount;
    }

    ///////////////////////////////////////////////////////
    //                                                   //
    //                    IO Methods                     //
    //                                                   //
    ///////////////////////////////////////////////////////

    private static final String SKILLS = "skills";
    private static final String PARENT = "parent";
    private static final String NAME   = "name";
    private static final String PREFIX = "prefix";
    private static final String GROUP  = "group";
    private static final String MANA   = "mana";
    private static final String MAX    = "max-level";
    private static final String EXP    = "exp-source";
    private static final String REGEN  = "mana-regen";
    private static final String PERM   = "needs-permission";
    private static final String ATTR   = "attributes";
    private static final String TREE   = "tree";

    /**
     * Saves the class template data to the config
     *
     * @param config config to save to
     */
    public void save(DataSection config)
    {
        config.set(NAME, name);
        config.set(PREFIX, prefix.replace(ChatColor.COLOR_CHAR, '&'));
        config.set(GROUP, group);
        config.set(MANA, mana.replace(ChatColor.COLOR_CHAR, '&'));
        config.set(MAX, maxLevel);
        config.set(PARENT, parent);
        config.set(PERM, needsPermission);
        settings.save(config.createSection(ATTR));
        config.set(REGEN, manaRegen);
        config.set(TREE, tree.toString());

        ArrayList<String> skillNames = new ArrayList<String>();
        for (Skill skill : skills)
        {
            skillNames.add(skill.getName());
        }
        config.set(SKILLS, skillNames);

        Data.serializeIcon(icon, config);
        config.set(EXP, expSources);
    }

    /**
     * Saves some of the class template data to the config, avoiding
     * overwriting any existing data.
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
     * Loads class template data from the configuration
     *
     * @param config config to load from
     */
    public void load(DataSection config)
    {
        parent = config.getString(PARENT);
        icon = Data.parseIcon(config);
        name = config.getString(NAME, name);
        prefix = TextFormatter.colorString(config.getString(PREFIX, prefix));
        group = config.getString(GROUP, "class");
        mana = TextFormatter.colorString(config.getString(MANA, mana));
        maxLevel = config.getInt(MAX, maxLevel);
        expSources = config.getInt(EXP, expSources);
        manaRegen = config.getDouble(REGEN, manaRegen);
        needsPermission = config.getString(PERM, needsPermission + "").equalsIgnoreCase("true");
        tree = DefaultTreeType.getByName(config.getString(TREE, "requirement"));

        settings.load(config.getSection(ATTR));

        if (config.isList(SKILLS))
        {
            skills.clear();
            for (String name : config.getList(SKILLS))
            {
                Skill skill = SkillAPI.getSkill(name);
                if (skill != null)
                {
                    skills.add(skill);
                }
                else Logger.invalid("Invalid skill for class " + this.name + " - " + name);
            }
        }

        if (SkillAPI.getSettings().isMapTreeEnabled())
        {
            this.skillTree = new MapTree((SkillAPI) Bukkit.getPluginManager().getPlugin("SkillAPI"), this);
        }
        else
        {
            this.skillTree = this.tree.getTree((SkillAPI) Bukkit.getPluginManager().getPlugin("SkillAPI"), this);
        }
    }

    /**
     * Arranges the skill tree for the class
     */
    public void arrange()
    {
        try
        {
            Logger.log(LogType.REGISTRATION, 2, "Arranging for \"" + name + "\" - " + skills.size() + " skills");
            this.skillTree.arrange();
        }
        catch (Exception ex)
        {
            Logger.invalid("Failed to arrange skill tree for class \"" + name + "\" - " + ex.getMessage());
        }
    }
}
