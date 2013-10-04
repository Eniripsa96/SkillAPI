package com.sucy.skill.skills;

import com.sucy.skill.mccore.CoreChecker;
import com.sucy.skill.mccore.PrefixManager;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.ClassAttribute;
import com.sucy.skill.api.CustomClass;
import com.sucy.skill.api.event.*;
import com.sucy.skill.api.skill.ClassSkill;
import com.sucy.skill.api.skill.PassiveSkill;
import com.sucy.skill.api.skill.SkillAttribute;
import com.sucy.skill.language.OtherNodes;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import com.sucy.skill.config.PlayerValues;

import java.util.*;

/**
 * <p>Player class data</p>
 * <p>You should not instantiate any new player data. You may
 * use the references given to you either through events or
 * via the SkillAPI.getPlayer(String) method however to modify
 * the stats of a player.</p>
 */
public final class PlayerSkills {

    private Hashtable<String, Integer> skills = new Hashtable<String, Integer>();
    private Hashtable<Material, String> binds = new Hashtable<Material, String>();
    private SkillAPI plugin;
    private String player;
    private String tree;
    private int points;
    private int level;
    private int mana;
    private int exp;

    /**
     * Constructor
     *
     * @param plugin API reference
     * @param player player name
     */
    public PlayerSkills(SkillAPI plugin, String player) {
        this.plugin = plugin;
        this.player = player;
    }

    /**
     * Constructor
     * @param plugin API reference
     * @param player player name
     * @param config config section to load from
     */
    public PlayerSkills(SkillAPI plugin, String player, ConfigurationSection config) {
        this.plugin = plugin;
        this.player = player;

        this.level = config.getInt(PlayerValues.LEVEL);
        this.exp = config.getInt(PlayerValues.EXP);
        this.points = config.getInt(PlayerValues.POINTS);
        if (config.contains(PlayerValues.MANA))
            this.mana = config.getInt(PlayerValues.MANA);
        if (config.contains(PlayerValues.CLASS))
            tree = config.getString(PlayerValues.CLASS);

        // Class skill tree
        ConfigurationSection skillConfig = config.getConfigurationSection(PlayerValues.SKILLS);
        if (this.tree != null) {
            SkillTree tree = plugin.getClass(this.tree);
            if (tree == null) {
                setClass(null);
                return;
            }

            if (plugin.getServer().getPlayer(player) != null && CoreChecker.isCoreActive()) {
                PrefixManager.setPrefix(this, tree.prefix, tree.braceColor);
            }
            if (skillConfig != null) {
                for (String skill : skillConfig.getKeys(false)) {
                    if (tree.skillSlots.contains(plugin.getSkill(skill)))
                        skills.put(skill, skillConfig.getInt(skill));
                }
            }

            // Load new skills in the tree if any
            for (Skill skill : tree.skillSlots.values()) {
                if (!skills.containsKey(skill.getName().toLowerCase())) {
                    skills.put(skill.getName().toLowerCase(), 0);
                }
            }
        }

        // Skill bindings
        ConfigurationSection bindConfig = config.getConfigurationSection(PlayerValues.BIND);
        if (bindConfig != null) {
            for (String bind : bindConfig.getKeys(false)) {
                binds.put(Material.getMaterial(bind), bindConfig.getString(bind));
            }
        }
    }

    /**
     * Gets the skills the player has
     *
     * @return skill data
     */
    public Hashtable<String, Integer> getSkills() {
        return skills;
    }

    /**
     * @return player name
     */
    public String getName() {
        return player;
    }

    /**
     * @return class level
     */
    public int getLevel() {
        return level;
    }

    /**
     * @return class experience
     */
    public int getExp() {
        return exp;
    }

    /**
     * @return mana
     */
    public int getMana() {
        return mana;
    }

    /**
     * @return skill points
     */
    public int getPoints() {
        return points;
    }

    /**
     * @return class prefix
     */
    public String getPrefix() {
        if (tree == null) {
            return ChatColor.GRAY + "No Class";
        }
        else {
            return plugin.getClass(tree).prefix;
        }
    }

    /**
     * @param amount amount of mana to use
     */
    public void useMana(int amount) {

        PlayerManaUseEvent event = new PlayerManaUseEvent(this, amount);
        plugin.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        CustomClass c = plugin.getRegisteredClass(tree);
        int maxMana = c == null ? 0 : c.getAttribute(ClassAttribute.MANA, level);
        mana -= event.getMana();
        if (mana < 0) mana = 0;
        if (mana > maxMana) mana = maxMana;
    }

    /**
     * @param amount amount of mana to gain
     */
    public void gainMana(int amount) {

        PlayerManaGainEvent event = new PlayerManaGainEvent(this, amount);
        plugin.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        mana += event.getMana();
        if (mana < 0) mana = 0;
        if (mana > 100) mana = 100;
    }

    /**
     * Upgrades a skill to the next level, consuming skill points in the process
     *
     * @param skill skill to upgrade
     * @return      true if upgraded, false otherwise
     */
    public boolean upgradeSkill(Skill skill) {

        // Skill isn't available
        if (!hasSkill(skill.getName()))
            return false;

        int level = getSkillLevel(skill.getName());
        ClassSkill classSkill = skill.getClassSkill();

        // Skill is already maxed
        if (level >= skill.getMaxLevel())
            return false;

        // Level requirement isn't met
        if (this.level < classSkill.getAttribute(SkillAttribute.LEVEL, level))
            return false;

        // Skill cost isn't met
        if (points < classSkill.getAttribute(SkillAttribute.COST, level))
            return false;

        // Doesn't have prerequisite
        if (classSkill.getSkillReq() != null && getSkillLevel(classSkill.getSkillReq()) < classSkill.getSkillReqLevel())
            return false;

        // Apply passive skill effects
        if (classSkill instanceof PassiveSkill)
            ((PassiveSkill) classSkill).onUpgrade(plugin.getServer().getPlayer(getName()), level + 1);

        // Upgrade the skill
        this.points -= skill.getClassSkill().getAttribute(SkillAttribute.COST, level);
        skills.put(skill.getName().toLowerCase(), level + 1);

        // If first level, call the unlock event
        if (level == 0) {
            plugin.getServer().getPluginManager().callEvent(
                    new PlayerSkillUnlockEvent(this, skill));
        }

        // Call the upgrade event
        plugin.getServer().getPluginManager().callEvent(
                new PlayerSkillUpgradeEvent(this, skill));

        return true;
    }

    /**
     * Changes the player's class
     *
     * @param className name of the target class
     */
    public void setClass(String className) {
        String prevTree = this.tree;
        this.tree = className;

        // Reset stats if applicable
        if (plugin.doProfessionsReset()) {
            level = 1;
            points = 1;
            exp = 0;
            stopPassiveAbilities();
            skills.clear();
            binds.clear();
        }

        // If the player was reverted to no class, clear all data
        if (this.tree == null) {
            level = 1;
            points = 0;
            exp = 0;
            stopPassiveAbilities();
            skills.clear();
            binds.clear();
            if (CoreChecker.isCoreActive())
                PrefixManager.clearPrefix(player);
            updateHealth();

            plugin.getServer().getPluginManager().callEvent(
                    new PlayerClassChangeEvent(this, plugin.getRegisteredClass(prevTree), null));
            return;
        }

        SkillTree tree = plugin.getClass(className);

        // If not resetting, simply remove any skills no longer in the tree
        if (!plugin.doProfessionsReset()) {
            for (String skill : skills.keySet()) {
                if (tree.skillSlots.contains(skill.toLowerCase()))
                    continue;
                skills.remove(skill);
                ArrayList<Material> keys = new ArrayList<Material>();
                for (Map.Entry<Material, String> entry : binds.entrySet())
                    if (entry.getValue().equalsIgnoreCase(skill))
                        keys.add(entry.getKey());
                for (Material mat : keys)
                    binds.remove(mat);
            }
        }

        // Add any new skills from the skill tree
        for (Skill skill : tree.skillSlots.values()) {
            skills.put(skill.getName().toLowerCase(), 0);
        }

        // Set mana if just starting
        if (prevTree == null) {
            mana = plugin.getRegisteredClass(this.tree).getAttribute(ClassAttribute.MANA, level);
        }

        // Set the new prefix for the class
        if (CoreChecker.isCoreActive())
            PrefixManager.setPrefix(this, tree.prefix, tree.braceColor);

        updateHealth();
        plugin.getServer().getPluginManager().callEvent(
                new PlayerClassChangeEvent(this, plugin.getRegisteredClass(prevTree), plugin.getRegisteredClass(className)));
    }

    /**
     * Updates the health of the player to match the class details
     */
    public void updateHealth() {
        if (tree == null) {
            setMaxHealth(20);
        }
        else setMaxHealth(plugin.getRegisteredClass(tree).getAttribute(ClassAttribute.HEALTH, level));
    }

    /**
     * Sets the max health keeping the amount of missing health the same
     *
     * @param amount new max health
     */
    public void setMaxHealth(int amount) {
        Player p = plugin.getServer().getPlayer(player);
        if (p == null) return;

        double prevMax = p.getMaxHealth();
        double prevHealth = p.getHealth();

        p.setMaxHealth(amount);
        p.setHealth(Math.max(1, prevHealth + amount - prevMax));
    }

    /**
     * Stops the effects of all passive abilities
     */
    public void stopPassiveAbilities() {
        for (Map.Entry<String, Integer> entry : getSkills().entrySet()) {
            if (entry.getValue() >= 1) {
                ClassSkill s = plugin.getRegisteredSkill(entry.getKey());
                if (s != null && s instanceof PassiveSkill)
                    ((PassiveSkill) s).stopEffects(plugin.getServer().getPlayer(player), entry.getValue());
            }
        }
    }

    /**
     * @return skill tree name
     */
    public String getClassName() {
        return tree;
    }

    /**
     * @return level the player is able to profess, less than 1 if unable to profess
     */
    public int getProfessionLevel() {
        if (tree == null) return 1;

        SkillTree tree = plugin.getClass(this.tree);
        return tree.level;
    }

    /**
     * Checks if the player has the skill
     *
     * @param name skill name
     * @return     true if included in the class, false otherwise
     */
    public boolean hasSkill(String name) {
        return skills.containsKey(name.toLowerCase());
    }

    /**
     * Checks if the player has invested points in the skill
     *
     * @param name skill name
     * @return     true if upgraded
     */
    public boolean hasSkillUnlocked(String name) {
        return hasSkill(name) && getSkillLevel(name) > 0;
    }

    /**
     * Gets the level of the skill
     *
     * @param name skill name
     * @return     skill level
     * @throws IllegalArgumentException
     */
    public int getSkillLevel(String name) {
        if (!skills.containsKey(name.toLowerCase()))
            throw new IllegalArgumentException("Player does not have skill: " + name);
        return skills.get(name.toLowerCase());
    }

    /**
     * Displays the player's skill tree
     *
     * @return true if successful, false if the player doesn't have skills
     */
    public boolean viewSkills() {
        if (tree == null)
            return false;

        Player p = plugin.getServer().getPlayer(player);
        if (p.getOpenInventory() != null)
            p.closeInventory();
        p.openInventory(plugin.getClass(tree).getInventory(this, skills));
        return true;
    }

    /**
     * Binds a skill to a material
     *
     * @param material material to bind to
     * @param skill    name of skill to be bound
     * @return         previously bound skill if any
     */
    public String bind(Material material, String skill) {
        return binds.put(material, skill);
    }

    /**
     * Gets the skill bound on a material
     *
     * @param material material to check
     * @return         bound skill or null if none
     */
    public String getBound(Material material) {
        return binds.get(material);
    }

    /**
     * Unbinds any skill from a material
     *
     * @param material material to unbind
     */
    public void unbind(Material material) {
        if (binds.containsKey(material))
            binds.remove(material);
    }

    /**
     * Gives the player class experience
     *
     * @param amount amount of exp to gain
     */
    public void giveExp(int amount) {
        if (tree == null) return;

        // Call an event
        PlayerExperienceGainEvent event = new PlayerExperienceGainEvent(this, amount);
        plugin.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        // Add the experience
        exp += event.getExp();

        // Level up if there's enough exp
        int levels = 0;
        while (exp >= getRequiredExp()) {
            exp -= getRequiredExp();
            levels++;
        }

        // Level the player up
        if (levels > 0) levelUp(levels);
    }

    /**
     * Levels the player up the given amount of times
     *
     * @param amount amount of levels to go up
     */
    public void levelUp(int amount) {
        if (tree == null) throw new IllegalArgumentException("Player cannot level up while not having a class");

        SkillTree skillTree = plugin.getClass(tree);
        if (amount + level > skillTree.getMaxLevel()) amount = skillTree.getMaxLevel() - level;
        if (amount <= 0) return;

        // Add to stats
        level += amount;
        points += amount;
        updateHealth();

        // Display a message
        Player p = plugin.getServer().getPlayer(player);
        List<String> messages = plugin.getMessages(OtherNodes.LEVEL_UP, true);
        for (String message : messages) {
            message = message.replace("{level}", level + "")
                    .replace("{points}", points + "")
                    .replace("{class}", tree);

            p.sendMessage(message);
        }

        // Display max level message if applicable
        if (level >= skillTree.getMaxLevel()) {
            messages = plugin.getMessages(OtherNodes.MAX_LEVEL, true);
            for (String message : messages) {
                message = message.replace("{level}", level + "")
                                 .replace("{class}", skillTree.getName());

                p.sendMessage(message);
            }
        }

        // Call the event
        plugin.getServer().getPluginManager().callEvent(
                new PlayerLevelUpEvent(this));
    }

    /**
     * Checks if the player can profess to the class
     *
     * @param target class to profess to
     * @return       true if able, false otherwise
     */
    public boolean canProfess(String target) {
        SkillTree skillTree = plugin.getClass(target);
        if (tree == null) return skillTree.parent == null;
        else if (getProfessionLevel() < 1) return false;
        return skillTree.parent != null && skillTree.parent.equalsIgnoreCase(tree) && plugin.getClass(tree).level <= level;
    }

    /**
     * @return amount of experience required for the next level
     */
    public int getRequiredExp() {
        return getRequiredExp(level);
    }

    /**
     * Calculates the required experience for a level
     *
     * @param level level to calculate for
     * @return      amount of experience needed
     */
    public static int getRequiredExp(int level) {
        return (level + 4) * (level + 4) / 10 * 10;
    }

    /**
     * @return experience to the next level
     */
    public int getExpToNextLevel() {
        return getRequiredExp() - exp;
    }

    /**
     * Gets the API reference
     *
     * @return api reference
     */
    public SkillAPI getAPI() {
        return plugin;
    }

    /**
     * Saves the player to the config file
     *
     * @param config config file
     * @param path   config path
     */
    public void save(ConfigurationSection config, String path) {
        config.set(path + PlayerValues.CLASS, tree);
        config.set(path + PlayerValues.LEVEL, level);
        config.set(path + PlayerValues.EXP, exp);
        config.set(path + PlayerValues.POINTS, points);
        config.set(path + PlayerValues.MANA, mana);
        for (Map.Entry<String, Integer> entry : skills.entrySet()) {
            config.set(path + PlayerValues.SKILLS + "." + entry.getKey(), entry.getValue());
        }
        for (Map.Entry<Material, String> entry : binds.entrySet()) {
            config.set(path + PlayerValues.BIND + "." + entry.getKey().name(), entry.getValue());
        }
    }
}
