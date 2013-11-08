package com.sucy.skill.api;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.skill.ClassSkill;
import com.sucy.skill.api.util.TextFormatter;
import com.sucy.skill.config.ClassValues;
import com.sucy.skill.language.StatNodes;
import com.sucy.skill.tree.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;

import java.util.*;

/**
 * <p>Base class for classes</p>
 * <p>For a detailed tutorial on how to use this class, visit:
 * <a href="http://dev.bukkit.org/bukkit-plugins/skillapi/pages/class-tutorial/" /></p>
 */
public abstract class CustomClass extends Attributed {

    private final HashMap<Class<? extends Projectile>, Integer> projectileDamage = new HashMap<Class<? extends Projectile>, Integer>();
    private final HashMap<Material, Integer> damage = new HashMap<Material, Integer>();
    private final HashMap<Integer, Integer> idDamage = new HashMap<Integer, Integer>();

    private final List<String> inheritance = new ArrayList<String>();
    private final List<String> skills = new ArrayList<String>();

    private final SkillAPI api;
    private final SkillTree tree;
    private final String name;

    private String parent;
    private String prefix;
    private String manaName;
    private boolean gainMana;
    private int professLevel;
    private int maxLevel;

    /**
     * @param name         class name
     * @param parent       parent class
     * @param prefix       class prefix
     * @param professLevel level to profess
     */
    public CustomClass(String name, String parent, String prefix, int professLevel, int maxLevel) {
        this.name = name;
        this.parent = parent;
        this.prefix = prefix;
        this.professLevel = professLevel;
        this.maxLevel = maxLevel;

        api = (SkillAPI)Bukkit.getPluginManager().getPlugin("SkillAPI");
        manaName = api.getMessage(StatNodes.MANA, false);
        gainMana = true;

        if (api.getTreeType().equalsIgnoreCase("BasicHorizontal")) this.tree = new BasicHorizontalTree(api, this);
        else if (api.getTreeType().equalsIgnoreCase("BasicVertical")) this.tree = new BasicVerticalTree(api, this);
        else if (api.getTreeType().equalsIgnoreCase("LevelVertical")) this.tree = new LevelVerticalTree(api, this);
        else if (api.getTreeType().equalsIgnoreCase("LevelHorizontal")) this.tree = new LevelHorizontalTree(api, this);
        else this.tree = new RequirementTree(api, this);
    }

    /**
     * @return SkillAPI reference
     */
    public SkillAPI getAPI() {
        return api;
    }

    /**
     * @return name of the class
     */
    public String getName() {
        return name;
    }

    /**
     * @return parent class or null if none
     */
    public String getParent() {
        return parent;
    }

    /**
     * @return class prefix in case ChatAPI is enabled
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * @return display name for mana
     */
    public String getManaName() {
        return manaName;
    }

    /**
     * @return whether or not this class gains mana passively
     */
    public boolean gainsMana() {
        return gainMana;
    }

    /**
     * Sets the display name for the class's mana
     *
     * @param name display name for the class's mana
     */
    public void setManaName(String name) {
        manaName = name;
    }

    /**
     * Sets whether or not this class gains mana passively
     *
     * @param gainsMana whether or not the class gains mana passively
     */
    public void setGainsMana(boolean gainsMana) {
        this.gainMana = gainsMana;
    }

    /**
     * Checks if the class has the skill registered
     *
     * @param skill skill to check
     * @return      true if registered, false otherwise
     */
    public boolean hasSkill(ClassSkill skill) {
        return tree.hasSkill(skill);
    }

    /**
     * Checks if the class has the skill registered
     *
     * @param name name of the skill to check
     * @return     true if registered, false otherwise
     */
    public boolean hasSkill(String name) {
        return api.hasSkill(name) && tree.hasSkill(api.getSkill(name));
    }

    /**
     * @return skill tree manager for the class
     */
    public SkillTree getTree() {
        return tree;
    }

    /**
     * @return brace color for the class prefix
     */
    public ChatColor getBraceColor() {
        return ChatColor.getByChar(ChatColor.getLastColors(prefix).charAt(1));
    }

    /**
     * @return level required for professing
     */
    public int getProfessLevel() {
        return professLevel;
    }

    /**
     * @return max level of the class
     */
    public int getMaxLevel() {
        return maxLevel;
    }

    /**
     * @return list of classes this takes all skills from or null if it doesn't inherit anything
     */
    public List<String> getInheritance() {
        return inheritance;
    }

    /**
     * @return list of skills included in this tree outside of inheritance
     */
    public List<String> getSkills() {
        if (inheritance.size() == 0) return skills;

        List<String> list = new ArrayList<String>();
        list.addAll(skills);
        for (String parent : inheritance) {
            CustomClass c = api.getClass(parent);
            if (c != null) {
                for (String skill : c.getSkills()) {
                    if (!list.contains(skill)) {
                        list.add(skill);
                    }
                }
            }
        }
        return list;
    }

    /**
     * Sets the max level of the class
     *
     * @param level max level
     */
    public void setMaxLevel(int level) {
        maxLevel = level;
    }

    /**
     * Sets the profession level of the class
     *
     * @param level profession level
     */
    public void setProfessLevel(int level) {
        professLevel = level;
    }

    /**
     * Sets the parent class of the class
     *
     * @param parent parent class
     */
    public void setParent(CustomClass parent) {
        if (parent == null) this.parent = null;
        else this.parent = parent.getName();
    }

    /**
     * Sets the prefix of the class
     *
     * @param prefix class prefix
     */
    public void setPrefix(String prefix) {
        this.prefix = TextFormatter.colorString(prefix);
    }

    /**
     * <p>Adds multiple skills to the class</p>
     *
     * @param skills skills to add
     */
    protected void addSkills(String ... skills) {
        for (String skill : skills) {
            if (!this.skills.contains(skill)) {
                this.skills.add(skill);
            }
        }
    }

    /**
     * Updates th class data from configuration data
     *
     * @param config configuration to update from
     */
    public void update(ConfigurationSection config) {
        setParent(getAPI().getClass(config.getString(ClassValues.PARENT)));
        setPrefix(config.getString(TextFormatter.colorString(ClassValues.PREFIX)));
        setMaxLevel(config.getInt(ClassValues.MAX_LEVEL));
        setProfessLevel(config.getInt(ClassValues.LEVEL));

        // Skills
        skills.clear();
        skills.addAll(config.getStringList(ClassValues.SKILLS));

        // Inheritance
        inheritance.clear();
        inheritance.addAll(config.getStringList(ClassValues.INHERIT));

        // Options
        manaName = TextFormatter.colorString(config.getString(ClassValues.MANA_NAME, "Mana"));
        gainMana = config.getBoolean(ClassValues.PASSIVE_MANA_GAIN);

        // Stats
        if (hasAttribute(ClassAttribute.HEALTH)) {
            if (config.contains(ClassValues.HEALTH_BASE))
                setBase(ClassAttribute.HEALTH, config.getInt(ClassValues.HEALTH_BASE));
            if (config.contains(ClassValues.HEALTH_BONUS))
                setScale(ClassAttribute.HEALTH, config.getInt(ClassValues.HEALTH_BONUS));
        }
        else {
            if (config.contains(ClassValues.HEALTH_BASE)) {
                setAttribute(ClassAttribute.HEALTH, config.getInt(ClassValues.HEALTH_BASE), config.getInt(ClassValues.HEALTH_BONUS));
            }
            if (config.contains(ClassValues.MANA_BASE)) {
                setAttribute(ClassAttribute.MANA, config.getInt(ClassValues.MANA_BASE), config.getInt(ClassValues.MANA_BONUS));
            }
        }
        if (config.contains(ClassValues.MANA_BASE))
            setBase(ClassAttribute.MANA, config.getInt(ClassValues.MANA_BASE));
        if (config.contains(ClassValues.MANA_BONUS))
            setScale(ClassAttribute.MANA, config.getInt(ClassValues.MANA_BONUS));
    }

    /**
     * Sets the damage for an item type
     *
     * @param mat    item type
     * @param damage damage dealt
     */
    protected void setDamage(Material mat, int damage) {
        this.damage.put(mat, damage);
    }

    /**
     * Sets damage for an item ID
     *
     * @param matId  item ID
     * @param damage damage dealt
     */
    @Deprecated
    protected void setDamage(int matId, int damage) {
        this.idDamage.put(matId, damage);
    }

    /**
     * Gets the damage of an item type
     *
     * @param mat item type
     * @return    item damage
     */
    public int getDamage(Material mat) {

        // Custom damage if applicable
        if (damage.containsKey(mat)) {
            return damage.get(mat);
        }

        // Default damages higher than 1
        else if (defaultDamage.containsKey(mat)) {
            return defaultDamage.get(mat);
        }

        // Otherwise just 1 damage
        else return 1;
    }

    /**
     * Retrieves the damage for an item
     *
     * @param matId item ID
     * @return      damage for the item
     */
    public int getDamage(int matId) {

        // Custom damage if applicable
        if (idDamage.containsKey(matId)) {
            return idDamage.get(matId);
        }

        // Otherwise just 1 damage
        else return 1;
    }

    /**
     * Sets the damage for a projectile
     *
     * @param type   projectile type
     * @param damage maximum damage dealt
     */
    protected void setProjectileDamage(Class<? extends Projectile> type, int damage) {
        this.projectileDamage.put(type, damage);
    }

    /**
     * Gets the maximum damage dealt by a projectile
     *
     * @param type projectile type
     * @return     maximum damage dealt
     */
    public int getDamage(Class<? extends Projectile> type) {

        // Custom damage if applicable
        if (projectileDamage.containsKey(type)) {
            return projectileDamage.get(type);
        }

        // Default damage if applicable
        else if (defaultProjectileDamage.containsKey(type)) {
            return defaultProjectileDamage.get(type);
        }

        // No damage set for the item
        else return 0;
    }

    /**
     * Gets the default damage for an item
     *
     * @param mat item type
     * @return    default damage
     */
    public static int getDefaultDamage(Material mat) {

        // Default damages higher than 1
        if (defaultDamage.containsKey(mat)) {
            return defaultDamage.get(mat);
        }

        // Otherwise just 1 damage
        else return 1;
    }

    /**
     * Gets the default damage for the projectile
     *
     * @param type projectile type
     * @return     default damage
     */
    public static int getDefaultDamage(Class<? extends Projectile> type) {

        // Default damage if applicable
        if (defaultProjectileDamage.containsKey(type)) {
            return defaultProjectileDamage.get(type);
        }

        // No damage set
        else {
            return 0;
        }
    }

    /**
     * Saves the class data to the configuration section
     *
     * @param config configuration section to save to
     */
    public void save(ConfigurationSection config) {
        config.set(ClassValues.PARENT, parent);
        config.set(ClassValues.PREFIX, prefix);
        config.set(ClassValues.MAX_LEVEL, maxLevel);
        config.set(ClassValues.LEVEL, professLevel);
        config.set(ClassValues.SKILLS, skills);
        config.set(ClassValues.INHERIT, inheritance);
        config.set(ClassValues.HEALTH_BASE, getBase(ClassAttribute.HEALTH));
        config.set(ClassValues.HEALTH_BONUS, getScale(ClassAttribute.HEALTH));
        config.set(ClassValues.MANA_BASE, getBase(ClassAttribute.MANA));
        config.set(ClassValues.MANA_BONUS, getScale(ClassAttribute.MANA));
    }

    /**
     * Default damages for items
     * Any item not in the table have a damage of 1
     */
    private static final HashMap<Material, Integer> defaultDamage = new HashMap<Material, Integer>() {{

        // Swords
        put(Material.DIAMOND_SWORD, 7);
        put(Material.IRON_SWORD, 6);
        put(Material.STONE_SWORD, 5);
        put(Material.GOLD_SWORD, 4);
        put(Material.WOOD_SWORD, 4);

        // Axes
        put(Material.DIAMOND_AXE, 6);
        put(Material.IRON_AXE, 5);
        put(Material.STONE_AXE, 4);
        put (Material.GOLD_AXE, 3);
        put (Material.WOOD_AXE, 3);

        // Pickaxes
        put (Material.DIAMOND_PICKAXE, 5);
        put (Material.IRON_PICKAXE, 4);
        put (Material.STONE_PICKAXE, 3);
        put (Material.GOLD_PICKAXE, 2);
        put (Material.WOOD_PICKAXE, 2);

        // Shovels
        put (Material.DIAMOND_SPADE, 4);
        put (Material.IRON_SPADE, 3);
        put (Material.STONE_SPADE, 2);
        put (Material.GOLD_SPADE, 1);
        put (Material.WOOD_SPADE, 1);
    }};

    /**
     * Default damage for projectiles
     */
    private static final HashMap<Class<? extends Projectile>, Integer> defaultProjectileDamage = new HashMap<Class<? extends Projectile>, Integer>() {{
        put(Arrow.class, 10);
        put(SmallFireball.class, 5);
        put(LargeFireball.class, 17);
    }};
}
