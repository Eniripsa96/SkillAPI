package com.sucy.skill.api;

import org.bukkit.Material;
import org.bukkit.entity.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>Base class for classes</p>
 * <p>For a detailed tutorial on how to use this class, visit:
 * <a href="http://dev.bukkit.org/bukkit-plugins/skillapi/pages/class-tutorial/" /></p>
 */
public abstract class CustomClass extends Attributed {

    private final HashMap<Class<? extends Projectile>, Integer> projectileDamage = new HashMap<Class<? extends Projectile>, Integer>();
    private final HashMap<Material, Integer> damage = new HashMap<Material, Integer>();
    private final HashMap<String, Integer> bonusDamage = new HashMap<String, Integer>();

    private final List<String> inheritance = new ArrayList<String>();
    private final List<String> skills = new ArrayList<String>();

    private final String name;
    private final String parent;
    private final String prefix;
    private final int professLevel;
    private final int maxLevel;

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
        return skills;
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
     * Sets the damage for an item type
     *
     * @param mat    item type
     * @param damage damage dealt
     */
    protected void setDamage(Material mat, int damage) {
        this.damage.put(mat, damage);
    }

    /**
     * Gets the damage of an item type
     *
     * @param mat item type
     * @return    item damage
     */
    public int getDamage(Material mat, String player) {

        // Custom damage if applicable
        if (damage.containsKey(mat)) {
            int dmg = damage.get(mat);
            return dmg == 0 ? 0 : dmg + bonusDamage.get(player.toLowerCase());
        }

        // Default damages higher than 1
        else if (defaultDamage.containsKey(mat)) {
            int dmg = defaultDamage.get(mat);
            return dmg == 0 ? 0 : dmg + bonusDamage.get(player.toLowerCase());
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
    public int getDamage(Class<? extends Projectile> type, String player) {

        // Custom damage if applicable
        if (projectileDamage.containsKey(type)) {
            int damage = projectileDamage.get(type);
            return damage == 0 ? 0 : damage + bonusDamage.get(player.toLowerCase());
        }

        // Default damage if applicable
        else if (defaultProjectileDamage.containsKey(type)) {
            int damage = defaultProjectileDamage.get(type);
            return damage == 0 ? 0 : damage + bonusDamage.get(player.toLowerCase());
        }

        // No damage set for the item
        else {
            return 0;
        }
    }

    /**
     * Retrieves the bonus damage of a player
     *
     * @param player player to retrieve for
     * @return       bonus damage
     */
    public int getBonusDamage(String player) {
        return bonusDamage.get(player.toLowerCase());
    }

    /**
     * Adds bonus damage for a player
     *
     * @param player player to add to
     * @param damage damage to add
     */
    public void addBonusDamage(String player, int damage) {
        if (bonusDamage.containsKey(player.toLowerCase())) {
            bonusDamage.put(player.toLowerCase(), bonusDamage.get(player.toLowerCase()) + damage);
        }
        else {
            bonusDamage.put(player.toLowerCase(), damage);
        }
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
