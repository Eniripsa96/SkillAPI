package com.sucy.skill.api.classes;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.AttributeSet;
import com.sucy.skill.api.enums.ExpSource;
import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.data.GroupSettings;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

public class RPGClass
{

    private final HashMap<Material, Double> weaponDamage     = new HashMap<Material, Double>();
    private final HashMap<Material, Double> projectileDamage = new HashMap<Material, Double>();
    private final ArrayList<Skill>          skills           = new ArrayList<Skill>();
    private final ArrayList<String>         permissions      = new ArrayList<String>();

    private SkillAPI  api;
    private RPGClass  parent;
    private ItemStack icon;
    private String    name;
    private String    group;
    private String    mana;
    private int       maxLevel;
    private int       professLevel;
    private int       expSources;
    private double    manaRegen;

    protected final AttributeSet attributes = new AttributeSet();

    ///////////////////////////////////////////////////////
    //                                                   //
    //                   Constructors                    //
    //                                                   //
    ///////////////////////////////////////////////////////

    protected RPGClass(String name, ItemStack icon, int maxLevel)
    {
        this(name, icon, maxLevel, null, null, 0);
    }

    protected RPGClass(String name, ItemStack icon, int maxLevel, String group)
    {
        this(name, icon, maxLevel, group, null, 0);
    }

    protected RPGClass(String name, ItemStack icon, int maxLevel, String parent, int professLevel)
    {
        this(name, icon, maxLevel, null, parent, professLevel);
    }

    protected RPGClass(String name, ItemStack icon, int maxLevel, String group, String parent, int professLevel)
    {
        this.api = (SkillAPI) Bukkit.getPluginManager().getPlugin("SkillAPI");
        this.parent = api.getClass(parent);
        this.icon = icon;
        this.name = name;
        this.group = group == null ? "default" : group.toLowerCase();
        this.mana = "Mana";
        this.maxLevel = maxLevel;
        this.professLevel = professLevel;
        this.expSources = ExpSource.MOB.getId();
    }

    ///////////////////////////////////////////////////////
    //                                                   //
    //                 Accessor Methods                  //
    //                                                   //
    ///////////////////////////////////////////////////////

    public SkillAPI getAPI()
    {
        return api;
    }

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
        return api.getSettings().getGroupSettings(group);
    }

    public boolean hasParent()
    {
        return parent != null;
    }

    public RPGClass getParent()
    {
        return parent;
    }

    public boolean receivesExp(ExpSource source)
    {
        return (expSources & source.getId()) != 0;
    }

    public int getMaxLevel()
    {
        return maxLevel;
    }

    public int getProfessLevel()
    {
        return professLevel;
    }

    public int getRequiredExp(int level)
    {
        return api.getSettings().getRequiredExp(level);
    }

    public double getHealth(int level)
    {
        return attributes.get(ClassAttribute.HEALTH, level);
    }

    public double getMana(int level)
    {
        return attributes.get(ClassAttribute.MANA, level);
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

    ///////////////////////////////////////////////////////
    //                                                   //
    //                 Setting Methods                   //
    //                                                   //
    ///////////////////////////////////////////////////////

    public void addSkill(String name)
    {
        Skill skill = api.getSkill(name);
        if (skill != null)
        {
            skills.add(skill);
        }
        else
        {
            api.getLogger().severe("class \"" + this.name + "\" tried to add an invalid skill - \"" + name + "\"");
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
}
