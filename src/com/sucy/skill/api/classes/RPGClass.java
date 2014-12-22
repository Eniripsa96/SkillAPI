package com.sucy.skill.api.classes;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.Settings;
import com.sucy.skill.api.enums.ExpSource;
import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.api.util.Data;
import com.sucy.skill.data.GroupSettings;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class RPGClass
{
    private final HashMap<Material, Double> weaponDamage     = new HashMap<Material, Double>();
    private final HashMap<Material, Double> projectileDamage = new HashMap<Material, Double>();
    private final ArrayList<Skill>          skills           = new ArrayList<Skill>();

    private RPGClass  parent;
    private ItemStack icon;
    private String    name;
    private String    group;
    private String    mana;
    private int       maxLevel;
    private int       expSources;
    private double    manaRegen;
    private boolean   needsPermission;

    protected final Settings settings = new Settings();

    ///////////////////////////////////////////////////////
    //                                                   //
    //                   Constructors                    //
    //                                                   //
    ///////////////////////////////////////////////////////

    protected RPGClass(String name, ItemStack icon, int maxLevel)
    {
        this(name, icon, maxLevel, null, null);
    }

    protected RPGClass(String name, ItemStack icon, int maxLevel, String parent)
    {
        this(name, icon, maxLevel, null, parent);
    }

    protected RPGClass(String name, ItemStack icon, int maxLevel, String group, String parent)
    {
        this.parent = SkillAPI.getClass(parent);
        this.icon = icon;
        this.name = name;
        this.group = group == null ? "default" : group.toLowerCase();
        this.mana = "Mana";
        this.maxLevel = maxLevel;
        this.expSources = ExpSource.MOB.getId();

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

    public String getName()
    {
        return name;
    }

    public String getGroup()
    {
        return group;
    }

    public GroupSettings getGroupSettings()
    {
        return SkillAPI.getSettings().getGroupSettings(group);
    }

    public boolean hasParent()
    {
        return parent != null;
    }

    public RPGClass getParent()
    {
        return parent;
    }

    public ItemStack getIcon()
    {
        return icon;
    }

    public String getSerializedIcon()
    {
        return icon.getType().name() + "," + icon.getData().getData();
    }

    public boolean receivesExp(ExpSource source)
    {
        return (expSources & source.getId()) != 0;
    }

    public int getMaxLevel()
    {
        return maxLevel;
    }

    public int getRequiredExp(int level)
    {
        return SkillAPI.getSettings().getRequiredExp(level);
    }

    public double getHealth(int level)
    {
        return settings.get(ClassAttribute.HEALTH, level);
    }

    public double getMana(int level)
    {
        return settings.get(ClassAttribute.MANA, level);
    }

    public String getManaName()
    {
        return mana;
    }

    public ArrayList<Skill> getSkills()
    {
        return skills;
    }

    public boolean hasManaRegen()
    {
        return manaRegen > 0;
    }

    public double getManaRegen()
    {
        return manaRegen;
    }

    public boolean hasWeaponDamage(Material mat)
    {
        return weaponDamage.containsKey(mat);
    }

    public boolean hasProjectileDamage(Material mat)
    {
        return projectileDamage.containsKey(mat);
    }

    public double getWeaponDamage(Material mat)
    {
        return weaponDamage.get(mat);
    }

    public double getProjectileDamage(Material mat)
    {
        return projectileDamage.get(mat);
    }

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

    public void addSkill(String name)
    {
        Skill skill = SkillAPI.getSkill(name);
        if (skill != null)
        {
            skills.add(skill);
        }
        else
        {
            Bukkit.getLogger().severe("Class \"" + this.name + "\" tried to add an invalid skill - \"" + name + "\"");
        }
    }

    public void addSkills(String... names)
    {
        for (String name : names)
        {
            addSkill(name);
        }
    }

    public void setManaName(String name)
    {
        mana = name;
    }

    public void setWeaponDamage(Material mat, double damage)
    {
        weaponDamage.put(mat, damage);
    }

    public void setProjectileDamage(Material mat, double damage)
    {
        projectileDamage.put(mat, damage);
    }

    public void setAllowedExpSources(ExpSource... sources)
    {
        expSources = 0;
        for (ExpSource source : sources)
        {
            allowExpSource(source);
        }
    }

    public void allowExpSource(ExpSource source)
    {
        expSources |= source.getId();
    }

    public void disallowExpSource(ExpSource source)
    {
        expSources |= source.getId();
    }

    public void setManaRegen(double amount)
    {
        this.manaRegen = amount;
    }

    ///////////////////////////////////////////////////////
    //                                                   //
    //                    IO Methods                     //
    //                                                   //
    ///////////////////////////////////////////////////////

    private static final String WEAPON   = "weapons";
    private static final String PROJECTS = "projectiles";
    private static final String SKILLS   = "skills";
    private static final String PARENT   = "parent";
    private static final String ITEM     = "item";
    private static final String NAME     = "name";
    private static final String GROUP    = "group";
    private static final String MANA     = "mana";
    private static final String MAX      = "max";
    private static final String EXP      = "exp-source";
    private static final String REGEN    = "mana-regen";
    private static final String PERM     = "perm";
    private static final String ATTR     = "attributes";

    public void save(ConfigurationSection config)
    {

        ConfigurationSection weapons = config.createSection(WEAPON);
        for (Map.Entry<Material, Double> entry : weaponDamage.entrySet())
        {
            weapons.set(entry.getKey().name(), entry.getValue());
        }

        ConfigurationSection projects = config.createSection(PROJECTS);
        for (Map.Entry<Material, Double> entry : projectileDamage.entrySet())
        {
            projects.set(entry.getKey().name(), entry.getValue());
        }

        ArrayList<String> skillNames = new ArrayList<String>();
        for (Skill skill : skills)
        {
            skillNames.add(skill.getName());
        }
        config.set(SKILLS, skillNames);

        if (parent != null)
        {
            config.set(PARENT, parent.getName());
        }

        config.set(ITEM, getSerializedIcon());
        config.set(NAME, name);
        config.set(GROUP, group);
        config.set(MANA, mana);
        config.set(MAX, maxLevel);
        config.set(EXP, expSources);
        config.set(REGEN, manaRegen);
        config.set(PERM, needsPermission);

        settings.save(config.createSection(ATTR));
    }

    public void softSave(ConfigurationSection config)
    {

        boolean neededOnly = config.getKeys(false).size() > 0;

        if (weaponDamage.size() > 0 && !neededOnly)
        {
            ConfigurationSection weapons = config.createSection(WEAPON);
            for (Map.Entry<Material, Double> entry : weaponDamage.entrySet())
            {
                weapons.set(entry.getKey().name(), entry.getValue());
            }
        }

        if (projectileDamage.size() > 0 && !neededOnly)
        {
            ConfigurationSection projects = config.createSection(PROJECTS);
            for (Map.Entry<Material, Double> entry : projectileDamage.entrySet())
            {
                projects.set(entry.getKey().name(), entry.getValue());
            }
        }

        if (skills.size() > 0 && !neededOnly)
        {
            ArrayList<String> skillNames = new ArrayList<String>();
            for (Skill skill : skills)
            {
                skillNames.add(skill.getName());
            }
            config.set(SKILLS, skillNames);
        }

        if (parent != null && !neededOnly)
        {
            config.set(PARENT, parent.getName());
        }
        if (!config.isSet(ITEM))
        {
            config.set(ITEM, getSerializedIcon());
        }
        if (!config.isSet(NAME))
        {
            config.set(NAME, name);
        }
        if (!config.isSet(group))
        {
            config.set(GROUP, group);
        }
        if (!config.isSet(MANA))
        {
            config.set(MANA, mana);
        }
        if (!config.isSet(MAX))
        {
            config.set(MAX, maxLevel);
        }
        if (!config.isSet(EXP))
        {
            config.set(EXP, expSources);
        }
        if (!config.isSet(REGEN))
        {
            config.set(REGEN, manaRegen);
        }
        if (!config.isSet(PERM))
        {
            config.set(PERM, needsPermission);
        }
    }

    public void load(ConfigurationSection config)
    {

        if (config.isConfigurationSection(WEAPON))
        {
            ConfigurationSection weapons = config.getConfigurationSection(WEAPON);
            for (String key : weapons.getKeys(false))
            {
                weaponDamage.put(Data.parseMat(key), weapons.getDouble(key));
            }
        }

        if (config.isConfigurationSection(PROJECTS))
        {
            ConfigurationSection projects = config.getConfigurationSection(PROJECTS);
            for (String key : projects.getKeys(false))
            {
                projectileDamage.put(Data.parseMat(key), projects.getDouble(key));
            }
        }

        if (config.isList(SKILLS))
        {
            skills.clear();
            for (String name : config.getStringList(SKILLS))
            {
                Skill skill = SkillAPI.getSkill(name);
                if (skill != null)
                {
                    skills.add(skill);
                }
            }
        }

        parent = SkillAPI.getClass(config.getString(PARENT));
        icon = Data.parseIcon(config.getString(ITEM, getSerializedIcon()));
        name = config.getString(NAME, name);
        group = config.getString(GROUP, "default");
        mana = config.getString(MANA, mana);
        maxLevel = config.getInt(MAX, maxLevel);
        expSources = config.getInt(EXP, expSources);
        manaRegen = config.getDouble(REGEN, manaRegen);
        needsPermission = config.getBoolean(PERM);

        settings.load(config.getConfigurationSection(ATTR));
    }
}
