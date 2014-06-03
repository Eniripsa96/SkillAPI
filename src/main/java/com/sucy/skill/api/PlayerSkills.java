package com.sucy.skill.api;

import com.rit.sucy.player.Protection;
import com.rit.sucy.player.TargetHelper;
import com.rit.sucy.version.VersionManager;
import com.rit.sucy.version.VersionPlayer;
import com.sucy.skill.PermissionNodes;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.event.*;
import com.sucy.skill.api.skill.*;
import com.sucy.skill.config.PlayerValues;
import com.sucy.skill.language.OtherNodes;
import com.sucy.skill.language.StatusNodes;
import com.sucy.skill.mccore.PrefixManager;
import com.sucy.skill.vault.PermissionManager;
import com.sucy.skill.vault.VaultChecker;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * <p>Player class data</p>
 * <p>You should not instantiate any new player data. You may
 * use the references given to you either through events or
 * via the SkillAPI.getPlayer(String) method however to modify
 * the stats of a player.</p>
 */
public final class PlayerSkills extends Valued {

    public static Stack<ClassSkill> skillsBeingCast = new Stack<ClassSkill>();

    private HashMap<String, Integer> skills = new HashMap<String, Integer>();
    private HashMap<Material, String> binds = new HashMap<Material, String>();
    private SkillAPI plugin;
    private VersionPlayer player;
    private String tree;
    private int bonusHealth;
    private int points;
    private int level;
    private int mana;
    private int exp;

    /**
     * <p>Constructor</p>
     * <p>Do not use this. Instead, get a reference through
     * SkillAPI via the getPlayer(String) method.</p>
     *
     * @param plugin API reference
     * @param name   player name
     *
     * @deprecated   Use PlayerSkills(SkillAPI, UUID) instead
     */
    public PlayerSkills(SkillAPI plugin, String name) {
        this(plugin, new VersionPlayer(name));
    }

    /**
     * <p>Constructor</p>
     * <p>Do not use this. Instead, get a reference through
     * SkillAPI via the getPlayer(String) method.</p>
     *
     * @param plugin API reference
     * @param id     player id
     */
    public PlayerSkills(SkillAPI plugin, UUID id) {
        this(plugin, new VersionPlayer(id));
    }

    /**
     * <p>Constructor</p>
     * <p>Do not use this. Instead, get a reference through
     * SkillAPI via the getPlayer(String) method.</p>
     *
     * @param plugin API reference
     * @param player player reference
     */
    public PlayerSkills(SkillAPI plugin, VersionPlayer player) {
        this.plugin = plugin;
        this.player = player;
        this.level = 1;
        this.exp = 0;
        this.points = plugin.getStartingPoints();
        if (plugin.getDefaultClass() != null) {
            setClass(plugin.getDefaultClass().getName());
        }
    }

    /**
     * <p>Constructor</p>
     * <p>Do not use this. Instead, get a reference through
     * SkillAPI via the getPlayer(String) method.</p>
     *
     * @param plugin API reference
     * @param player player name
     * @param config config section to load from
     * @deprecated use PlayerSkills(plugin, id, config) instead
     */
    public PlayerSkills(SkillAPI plugin, String player, ConfigurationSection config) {
        this(plugin, new VersionPlayer(player), config);
    }

    /**
     * <p>Constructor</p>
     * <p>Do not use this. Instead, get a reference through
     * SkillAPI via the getPlayer(String) method.</p>
     *
     * @param plugin API reference
     * @param id     player UUID
     * @param config config section to load from
     */
    public PlayerSkills(SkillAPI plugin, UUID id, ConfigurationSection config) {
        this(plugin, new VersionPlayer(id), config);
    }

    /**
     * <p>Constructor</p>
     * <p>Do not use this. Instead, get a reference through
     * SkillAPI via the getPlayer(String) method.</p>
     *
     * @param plugin API reference
     * @param player player reference
     * @param config config section to load from
     */
    public PlayerSkills(SkillAPI plugin, VersionPlayer player, ConfigurationSection config) {
        this.plugin = plugin;
        this.player = player;

        this.level = config.getInt(PlayerValues.LEVEL);
        this.exp = config.getInt(PlayerValues.EXP);
        this.points = config.getInt(PlayerValues.POINTS);
        if (config.contains(PlayerValues.MANA))
            this.mana = config.getInt(PlayerValues.MANA);
        if (config.contains(PlayerValues.CLASS))
            tree = config.getString(PlayerValues.CLASS);

        // Default class
        if (!hasClass() && plugin.getDefaultClass() != null) {
            setClass(plugin.getDefaultClass().getName());
            level = 1;
            points = plugin.getStartingPoints();
            exp = 0;
        }

        // Class skill tree
        if (hasClass()) {
            ConfigurationSection skillConfig = config.getConfigurationSection(PlayerValues.SKILLS);
            CustomClass tree = plugin.getClass(this.tree);
            if (tree == null) {
                setClass(null);
                return;
            }

            if (player.getPlayer() != null) {
                PrefixManager.setPrefix(this, tree.getPrefix(), tree.getBraceColor());
            }
            if (skillConfig != null) {
                for (String skill : skillConfig.getKeys(false)) {
                    if (tree.hasSkill(plugin.getSkill(skill)))
                        skills.put(skill, skillConfig.getInt(skill));
                }
            }

            // Load new skills in the tree if any
            for (String skill : tree.getSkills()) {
                if (!skills.containsKey(skill.toLowerCase())) {
                    skills.put(skill.toLowerCase(), 0);
                }
            }

            // Dynamic values
            if (config.contains(PlayerValues.VALUES)) {
                ConfigurationSection values = config.getConfigurationSection(PlayerValues.VALUES);
                for (String key : values.getKeys(false)) {
                    setValue(key, values.getInt(key));
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

        // Level bar
        updateLevelBar();
    }

    /**
     * <p>Retrieves the details of the player's skills</p>
     * <p>The key is the name of the skill</p>
     * <p>The value is the level of the skill</p>
     * <p>Modifying this map will change the player's skill data</p>
     *
     * @return map of the names and levels of the skills the player has
     */
    public HashMap<String, Integer> getSkills() {
        return skills;
    }

    /**
     * <p>Retrieves the details of the player's bindings</p>
     * <p>The key is the material the skill is bound to</p>
     * <p>The value is the skill bound to the item</p>
     * <p>Modifying this map will change the player's skill bindings</p>
     *
     * @return map of the player's skill bindings
     */
    public HashMap<Material, String> getBinds() {
        return binds;
    }

    /**
     * <p>Retrieves the Bukkit player reference</p>
     *
     * @return player reference
     */
    public Player getPlayer() {
        return player.getPlayer();
    }

    /**
     * <p>Retrieves the offline Bukkit player reference</p>
     *
     * @return offline player reference
     */
    public OfflinePlayer getOfflinePlayer() {
        return player.getOfflinePlayer();
    }

    /**
     * <p>Retrieves the name of the player</p>
     *
     * @return player name
     */
    public String getName() {
        return getPlayer().getName();
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
     * @return current mana
     */
    public int getMana() {
        return mana;
    }

    /**
     * Gets the maximum mana for the player
     *
     * @return maximum mana
     */
    public int getMaxMana() {
        CustomClass c = plugin.getClass(tree);
        return c == null ? 0 : (int)c.getAttribute(ClassAttribute.MANA, level);
    }

    /**
     * @return current skill points
     */
    public int getPoints() {
        return points;
    }

    /**
     * @return class prefix
     */
    public String getPrefix() {
        if (plugin.getClass(tree) == null) {
            return ChatColor.GRAY + "No Class";
        }
        else {
            return plugin.getClass(tree).getPrefix();
        }
    }

    /**
     * <p>Subtracts the amount from the player's mana</p>
     * <p>Calls a PlayerManaUseEvent before subtracting so it can
     * be cancelled or modified</p>
     *
     * @param amount amount of mana to use
     */
    public void useMana(int amount) {

        PlayerManaUseEvent event = new PlayerManaUseEvent(this, amount);
        plugin.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        int maxMana = getMaxMana();
        mana -= event.getMana();
        if (mana < 0) mana = 0;
        if (mana > maxMana) mana = maxMana;
    }

    /**
     * <p>Gives mana to the player</p>
     * <p>Calls a PlayerGainManaEvent before giving the mana so it can
     * be cancelled or modified</p>
     *
     * @param amount amount of mana to gain
     */
    public void gainMana(int amount) {
        if (!hasClass()) return;

        PlayerManaGainEvent event = new PlayerManaGainEvent(this, amount);
        plugin.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        int maxMana = getMaxMana();
        mana += event.getMana();
        if (mana < 0) mana = 0;
        if (mana > maxMana) mana = maxMana;
    }

    /**
     * <p>Upgrades a skill for the player, consuming skill points in the process</p>
     * <p>If the player does not have the correct requirements for upgrading the skill
     * (skill points, class level, skill prerequisite), this does nothing</p>
     *
     * @param skill skill to upgrade
     * @return      true if upgraded, false otherwise
     */
    public boolean upgradeSkill(ClassSkill skill) {

        // Skill isn't available
        if (!hasSkill(skill.getName()))
            return false;

        int level = getSkillLevel(skill.getName());

        // Skill is already maxed
        if (level >= skill.getMaxLevel())
            return false;

        // Level requirement isn't met
        if (this.level < (int)skill.getAttribute(SkillAttribute.LEVEL, level + 1))
            return false;

        // Skill cost isn't met
        if (points < (int)skill.getAttribute(SkillAttribute.COST, level))
            return false;

        // Doesn't have prerequisite
        if (skill.getSkillReq() != null && getSkillLevel(skill.getSkillReq()) < skill.getSkillReqLevel())
            return false;

        // Call the upgrade event
        int cost = (int)skill.getAttribute(SkillAttribute.COST, level + 1);
        PlayerSkillUpgradeEvent event = new PlayerSkillUpgradeEvent(this, skill, cost);
        plugin.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) return false;

        // Apply passive skill effects
        if (skill instanceof PassiveSkill) {
            if (level == 0) ((PassiveSkill) skill).onInitialize(getPlayer(), level + 1);
            else ((PassiveSkill) skill).onUpgrade(getPlayer(), level + 1);
        }

        // Upgrade the skill
        this.points -= cost;
        skills.put(skill.getName().toLowerCase(), level + 1);

        // If first level, call the unlock event and give permissions
        if (level == 0) {
            plugin.getServer().getPluginManager().callEvent(
                    new PlayerSkillUnlockEvent(this, skill));
            if (skill.hasPermissions() && VaultChecker.isVaultActive()) {
                for (String perm : skill.getPermissions()) {
                    PermissionManager.add(getPlayer(), perm);
                }
            }
        }

        return true;
    }

    /**
     * <p>Downgrades a skill for the player, refunding skill points in the process</p>
     * <p>If the player does not have points invested in the skill, this does nothing</p>
     *
     * @param skill skill to downgrade
     * @return      true if downgraded, false otherwise
     */
    public boolean downgradeSkill(ClassSkill skill) {

        // Skill isn't available
        if (!hasSkill(skill.getName()))
            return false;

        int level = getSkillLevel(skill.getName());

        // Skill has no points
        if (level == 0)
            return false;

        // Skill is required by another skill
        for (String s : skills.keySet()) {
            ClassSkill classSkill = plugin.getSkill(s);
            if (classSkill == null) continue;
            String req = classSkill.getSkillReq();
            if (req != null && req.equalsIgnoreCase(skill.getName())) {
                if (getSkillLevel(s) > 0 && skills.get(s) >= getSkillLevel(s)) {
                    return false;
                }
            }
        }

        // Remove permissions if now level 0
        if (level == 1 && skill.hasPermissions() && VaultChecker.isVaultActive()) {
            for (String perm : skill.getPermissions()) {
                PermissionManager.remove(getPlayer(), perm);
            }
        }

        // Update passive skill effects
        if (skill instanceof PassiveSkill) {
            ((PassiveSkill) skill).stopEffects(getPlayer(), level);
            if (level > 1) ((PassiveSkill) skill).onInitialize(getPlayer(), level - 1);
        }

        // Downgrade the skill
        this.points += (int)skill.getAttribute(SkillAttribute.COST, level);
        skills.put(skill.getName().toLowerCase(), level - 1);
        if (skills.get(skill.getName().toLowerCase()) == 0) {
            for (Map.Entry<Material, String> bind : binds.entrySet()) {
                if (bind.getValue().equalsIgnoreCase(skill.getName())) {
                    binds.remove(bind.getKey());
                    break;
                }
            }
        }

        // Call the downgrade event
        plugin.getServer().getPluginManager().callEvent(
                new PlayerSkillDowngradeEvent(this, skill));

        return true;
    }

    /**
     * <p>Changes the player's class to the class with the given name</p>
     * <p>The class name is not case-sensitive</p>
     *
     * @param className name of the target class
     */
    public void setClass(String className) {
        String prevTree = this.tree;
        this.tree = className;

        // Reset stats if applicable
        if (plugin.doProfessionsReset()) {
            level = 1;
            points = plugin.getStartingPoints();
            exp = 0;
            stopPassiveAbilities();
            skills.clear();
            binds.clear();
        }

        // Clear permissions on leaving a class
        CustomClass prevClass =  plugin.getClass(prevTree);
        if (prevClass != null && VaultChecker.isVaultActive()) {
            List<String> perms = prevClass.getPermissions();
            for (String perm : perms) {
                PermissionManager.remove(getPlayer(), perm);
            }
        }

        // If the player was reverted to no class, clear all data
        if (!hasClass()) {
            stopPassiveAbilities();
            level = 1;
            points = plugin.getStartingPoints();
            exp = 0;
            stopPassiveAbilities();
            skills.clear();
            binds.clear();
            PrefixManager.clearPrefix(player);
            updateHealth();
            updateLevelBar();

            plugin.getServer().getPluginManager().callEvent(
                    new PlayerClassChangeEvent(this, plugin.getClass(prevTree), null));
            return;
        }

        CustomClass tree = plugin.getClass(className);

        // Set new permission
        if (VaultChecker.isVaultActive()) {
            for (String perm : tree.getPermissions()) {
                PermissionManager.add(getPlayer(), perm);
            }
        }

        // If not resetting, simply remove any skills no longer in the tree
        if (!plugin.doProfessionsReset()) {
            List<String> list = new ArrayList<String>();
            for (String skill : skills.keySet()) {
                if (tree.hasSkill(skill)) continue;
                list.add(skill);
                ClassSkill s = getAPI().getSkill(skill);
                int level = getSkillLevel(skill);
                for (int i = 1; i <= level; i++) {
                    points += (int)s.getAttribute(SkillAttribute.COST, i);
                }
                if (s instanceof PassiveSkill) {
                    ((PassiveSkill)s).stopEffects(player.getPlayer(), level);
                }
                ArrayList<Material> keys = new ArrayList<Material>();
                for (Map.Entry<Material, String> entry : binds.entrySet())
                    if (entry.getValue().equalsIgnoreCase(skill))
                        keys.add(entry.getKey());
                for (Material mat : keys)
                    binds.remove(mat);
            }
            for (String skill : list) {
                skills.remove(skill);
            }
        }

        // Add any new skills from the skill tree
        for (String skill : tree.getSkills()) {
            if (!skills.containsKey(skill.toLowerCase())) {
                skills.put(skill.toLowerCase(), 0);
            }
        }

        // Set mana if just starting
        if (plugin.getClass(prevTree) == null) {
            mana = getMaxMana();
        }

        // Set the new prefix for the class
        PrefixManager.setPrefix(this, tree.getPrefix(), tree.getBraceColor());

        updateHealth();
        updateLevelBar();
        plugin.getServer().getPluginManager().callEvent(
                new PlayerClassChangeEvent(this, plugin.getClass(prevTree), plugin.getClass(className)));
    }

    /**
     * <p>Adds maximum health to the player</p>
     * <p>Bonuses provided this way do not persist through reloads and the player rejoining.
     * Because of this, passive abilities do not need to clean up their health bonuses when stopping their effects</p>
     *
     * @param amount amount of health to give to the player
     */
    public void addMaxHealth(int amount) {
        bonusHealth += amount;
        updateHealth();
    }

    /**
     * <p>Clears all health bonuses the player has</p>
     */
    public void clearHealthBonuses() {
        bonusHealth = 0;
    }

    /**
     * <p>Updates the health of the player, applying the type of health bar the plugin settings indicates</p>
     */
    public void updateHealth() {
        updateHealth(getPlayer());
    }

    /**
     * <p>Updates the health of the player, applying the type of health bar the plugin settings indicates</p>
     * <p>Normally, you can just use updateHealth() but in case you need to do it when getting the player
     * reference cannot be done through the server, you can use this method to get around it (generally
     * for updating during logging in/out)</p>
     *
     * @param player player reference
     */
    public void updateHealth(Player player) {
        if (player == null) return;

        // Apply class health
        if (hasClass()) {
            applyMaxHealth(plugin.getClass(tree).getAttribute(ClassAttribute.HEALTH, level) + bonusHealth);
        }

        // No class just has the default 20hp
        else {
            applyMaxHealth(plugin.getBaseHp() + bonusHealth);
        }

        // Apply health scaling
        if (VersionManager.isVersionAtLeast(VersionManager.MC_1_6_2_MIN)) {
            if (plugin.oldHealthEnabled()) {
                player.setHealthScaled(true);
                player.setHealthScale(20);
            }
            else player.setHealthScaled(false);
        }
    }

    /**
     * <p>Sets the maximum health of the player, adjusting their current health accordingly</p>
     * <p>If the health adjustment would bring their health to or below 0, it leaves them with
     * 1 health instead</p>
     *
     * @param amount new max health
     */
    public void applyMaxHealth(double amount) {
        Player p = getPlayer();
        if (p == null) return;
        VersionManager.setMaxHealth(p, amount);
    }

    /**
     * <p>Stops the effects of all passive abilities for the player.</p>
     */
    public void stopPassiveAbilities() {
        stopPassiveAbilities(getPlayer());
    }

    /**
     * <p>Stops the effects of all passive abilities for the player.</p>
     *
     * @param p player reference
     */
    public void stopPassiveAbilities(Player p) {
        if (p == null) return;
        for (Map.Entry<String, Integer> entry : getSkills().entrySet()) {
            if (entry.getValue() >= 1) {
                ClassSkill s = plugin.getSkill(entry.getKey());
                if (s != null && s instanceof PassiveSkill)
                    ((PassiveSkill) s).stopEffects(p, entry.getValue());
            }
        }
    }

    /**
     * <p>Starts the effects of all passive abilities for the player</p>
     * <p>If the player is currently respawning or offline, this will not work</p>
     */
    public void startPassiveAbilities() {
        startPassiveAbilities(getPlayer());
    }

    /**
     * <p>Starts the effects of all passive abilities for the player</p>
     * <p>This is for use when the player is respawning or offline
     * but a reference is still present</p>
     *
     * @param player player to start the effects for
     */
    public void startPassiveAbilities(Player player) {
        if (player == null) return;
        for (Map.Entry<String, Integer> entry : getSkills().entrySet()) {
            if (entry.getValue() >= 1) {
                ClassSkill s = plugin.getSkill(entry.getKey());
                if (s != null && s instanceof PassiveSkill)
                    ((PassiveSkill) s).onInitialize(player, entry.getValue());
            }
        }
    }

    /**
     * @return name of the player's class or null if has no class
     */
    public String getClassName() {
        return tree;
    }

    /**
     * @return true if the player has a class, false otherwise
     */
    public boolean hasClass() {
        return getAPI().getClass(tree) != null;
    }

    /**
     * @return level the player is able to profess, less than 1 if unable to profess
     */
    public int getProfessionLevel() {
        if (plugin.getClass(tree) == null) return 1;

        CustomClass tree = plugin.getClass(this.tree);
        return tree.getProfessLevel();
    }

    /**
     * <p>Checks if the player has the skill available</p>
     * <p>The skill name is not case-sensitive</p>
     *
     * @param name skill name
     * @return     true if included in the class, false otherwise
     */
    public boolean hasSkill(String name) {
        return skills.containsKey(name.toLowerCase());
    }

    /**
     * <p>Checks if the player has the skill available and has invested at least one point into it</p>
     * <p>The skill name is not case-sensitive</p>
     *
     * @param name skill name
     * @return     true if upgraded
     */
    public boolean hasSkillUnlocked(String name) {
        return hasSkill(name) && getSkillLevel(name) > 0;
    }

    /**
     * <p>Retrieves the level of the skill the player has unlocked</p>
     * <p>The skill name is not case-sensitive</p>
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
     * <p>Displays the player's skill tree</p>
     *
     * @return true if successful, false if the player doesn't have skills
     */
    public boolean viewSkills() {
        if (plugin.getClass(tree) == null)
            return false;

        Player p = player.getPlayer();
        if (p.getOpenInventory() != null)
            p.closeInventory();
        p.openInventory(plugin.getClass(tree).getTree().getInventory(this, skills));
        return true;
    }

    /**
     * <p>Binds a player's skill to the given material</p>
     * <p>The skill name is not case-sensitive</p>
     *
     * @param material material to bind to
     * @param skill    name of skill to be bound
     * @return         previously bound skill if any
     */
    public String bind(Material material, String skill) {
        return binds.put(material, skill);
    }

    /**
     * <p>Retrieves the name of the skill bound to the given item</p>
     * <p>If no skill is bound to the item, this returns null</p>
     *
     * @param material material to check
     * @return         bound skill or null if none
     */
    public String getBound(Material material) {
        return binds.get(material);
    }

    /**
     * <p>Unbinds a skill from the given material</p>
     * <p>If no skill is bound to the material, this does nothing</p>
     *
     * @param material material to unbind
     */
    public void unbind(Material material) {
        if (binds.containsKey(material))
            binds.remove(material);
    }

    /**
     * <p>Gives the player skill points</p>
     * <p>Negative values can be used with this method</p>
     * <p>If this would drop the player below 0, it will be set to 0</p>
     *
     * @param amount amount of skill points to give
     */
    public void givePoints(int amount) {
        points += amount;
        if (points < 0) points = 0;
    }

    /**
     * <p>Awards the player experience</p>
     * <p>A PlayerExperienceGainEvent is called beforehand so it
     * can be cancelled or modified</p>
     *
     * @param amount amount of exp to gain
     */
    public void giveExp(int amount) {
        if (plugin.getClass(tree) == null) return;
        if (getPlayer() == null) return;

        // Call an event
        PlayerExperienceGainEvent event = new PlayerExperienceGainEvent(this, amount);
        plugin.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        // Add the experience
        exp += event.getExp();
        if (plugin.isExpMessageEnabled()) {
            String message = plugin.getMessage(OtherNodes.EXP_GAINED, true);
            message = message.replace("{amount}", event.getExp() + "")
                    .replace("{current}", exp + "")
                    .replace("{left}", getExpToNextLevel() + "")
                    .replace("{required}", getRequiredExp() + "");
            getPlayer().sendMessage(message + ChatColor.RESET);
        }

        // Level up if there's enough exp
        int levels = 0;
        while (exp >= plugin.getRequiredExp(level + levels)) {
            exp -= plugin.getRequiredExp(level + levels);
            levels++;
        }

        // Level the player up
        if (levels > 0) levelUp(levels);
        else updateLevelBar();
    }

    /**
     * <p>Removes a percent of the player's required experience</p>
     * <p>This cannot delevel a player and cannot drop their experience below 0</p>
     *
     * @param percent percentage of experience to lose
     * @return        amount of experience lost
     */
    public int loseExp(double percent) {
        int lost = (int)(getRequiredExp() * percent / 100);
        if (lost > exp) lost = exp;
        exp -= lost;
        return lost;
    }

    /**
     * <p>Levels the player up the amount of times</p>
     * <p>If the player's level would exceed their class's maximum level, their level is set
     * to that instead</p>
     *
     * @param amount amount of levels to go up
     */
    public void levelUp(int amount) {
        if (plugin.getClass(tree) == null) throw new IllegalArgumentException("Player cannot level up while not having a class");

        CustomClass skillTree = plugin.getClass(tree);
        if (amount + level > skillTree.getMaxLevel()) amount = skillTree.getMaxLevel() - level;
        if (amount <= 0) return;

        // Add to stats
        level += amount;
        points += amount * plugin.getPointsPerLevel();
        updateHealth();

        // Update level score
        PrefixManager.updateLevel(this);

        // Display a message
        if (plugin.isLvlMessageEnabled()) {
            Player p = getPlayer();
            List<String> messages = plugin.getMessages(OtherNodes.LEVEL_UP, true);
            double healthScale = (int)skillTree.getScale(ClassAttribute.HEALTH);
            double manaScale = (int)skillTree.getScale(ClassAttribute.MANA);
            for (String message : messages) {
                message = message.replace("{level}", level + "")
                        .replace("{class}", tree)
                        .replace("{points}", points + "")
                        .replace("{health}", (int) p.getMaxHealth() + "")
                        .replace("{mana}", getMaxMana() + "")
                        .replace("{pointsgain}", plugin.getPointsPerLevel() * amount + "")
                        .replace("{healthgain}", (int) (level * healthScale) - (int) ((level - amount) * healthScale) + "")
                        .replace("{managain}", (int) (level * manaScale) - (int) ((level - amount) * manaScale) + "");
    
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
        }

        // Call the event
        plugin.getServer().getPluginManager().callEvent(
                new PlayerLevelUpEvent(this, amount));

        updateLevelBar();
    }

    /**
     * <p>Checks if the player can profess into the class with the given name</p>
     * <p>The name is not case-sensitive</p>
     *
     * @param target class to profess to
     * @return       true if able, false otherwise
     */
    public boolean canProfess(String target) {
        CustomClass skillTree = plugin.getClass(target);

        // Cannot profess as an invalid class
        if (skillTree == null) return false;

        // The player must have permission for the class
        else if (!plugin.hasPermission(player.getPlayer(), skillTree)) return false;
                
        // If it's a starting class, the player must not have a class
        else if (plugin.getClass(tree) == null) return skillTree.getParent() == null;

        // The player's class must allow for professing (profession levels of 0 or below indicate there's no profession)
        else if (getProfessionLevel() < 1) return false;

        // The class must be a profession of the player's current class and the player must be a high enough level
        return skillTree.getParent() != null && skillTree.getParent().equalsIgnoreCase(tree) && plugin.getClass(tree).getProfessLevel() <= level;
    }

    /**
     * <p>Heals the player the given amount, taking into account status effects and maximum health</p>
     *
     * @param amount amount to heal
     */
    public void heal(double amount) {

        // Apply statuses
        if (hasStatus(Status.CURSE)) amount *= -1;
        if (hasStatus(Status.INVINCIBLE) && amount < 0) return;

        Player p = player.getPlayer();
        double health;

        // Call the event
        PlayerHealEvent event = new PlayerHealEvent(p, amount);
        getAPI().getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        // Heal the player
        health = p.getHealth() + amount;
        if (health > p.getMaxHealth()) health = p.getMaxHealth();
        if (health < 0) health = 0;
        p.setHealth(health);
    }

    /**
     * <p>Heals the player the given amount, taking into account status effects and maximum health</p>
     * <p>This method also launches a PlayerSkillHealEvent with the provided details</p>
     *
     * @param healer    player who did the healing
     * @param amount    amount of health restored
     * @param skillName name of the skill used
     */
    public void heal(Player healer, double amount, String skillName) {
        PlayerSkillHealEvent event = new PlayerSkillHealEvent(getPlayer(), healer, skillName, amount);
        getAPI().getServer().getPluginManager().callEvent(event);
        heal(event.getAmount());
    }

    /**
     * <p>Updates the enchanting level bar for the player</p>
     * <p>This sets the enchanting level to the player's class level
     * and the progress to the player's class experience progress to the
     * next level.</p>
     */
    public void updateLevelBar() {
        updateLevelBar(getPlayer());
    }

    /**
     * <p>Updates the enchanting level bar for the player</p>
     * <p>This sets the enchanting level to the player's class level
     * and the progress to the player's class experience progress to the
     * next level.</p>
     * <p>Normally, you can just use updateHealth() but in case you need to do it when getting the player
     * reference cannot be done through the server, you can use this method to get around it (generally
     * for updating during logging in/out)</p>
     *
     * @param player player reference
     */
    public void updateLevelBar(Player player) {

        // Must be online with permission while the plugin is using level bars
        if (player == null || !player.hasPermission(PermissionNodes.BASIC) || !plugin.usingLevelBar()) {
            return;
        }

        // No class leaves the level bar empty
        if (!hasClass()) {
            player.setLevel(0);
            player.setExp(0);
        }

        // A class displays the class level and experience progress
        else {
            player.setLevel(level);
            player.setExp((float)exp / getRequiredExp());
        }
    }

    /**
     * <p>Retrieves the total amount of required experience
     * to the next level for the player. This uses SkillAPI's
     * configurable formula, plugging in the player's current level.</p>
     *
     * @return amount of experience required for the next level
     */
    public int getRequiredExp() {
        return plugin.getRequiredExp(level);
    }

    /**
     * <p>Retrieves the amount of experience left that the
     * player needs to reach the next level</p>
     * <p>This is a simple calculation of (requiredExp - currentExp)</p>
     *
     * @return experience to the next level
     */
    public int getExpToNextLevel() {
        return Math.max(0, getRequiredExp() - exp);
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
     * <p>Applies a status to the player</p>
     * <p>Runs an event beforehand so it can be cancelled or modified</p>
     * <p>This is the same as getting the status holder for the player and
     * applying it there except for the event that is called</p>
     *
     * @param status   status to apply
     * @param duration duration of the status in seconds
     */
    public void applyStatus(IStatus status, double duration) {

        PlayerStatusEvent event = new PlayerStatusEvent(this, status, duration);
        plugin.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        long time = (long)(event.getDuration() * 1000);
        getStatusData().addStatus(status, time);
    }

    /**
     * <p>Retrieves the status data for the player</p>
     * <p>If the player doesn't have data for them,
     * new data is created and returned</p>
     * <p>This is this doing SkillAPI.getStatusHolder(player)</p>
     *
     * @return the status data for the player
     */
    public StatusHolder getStatusData() {
        return getAPI().getStatusHolder(player.getPlayer());
    }

    /**
     * <p>Removes a status from the player</p>
     * <p>If the player does not have the status on them, this does nothing</p>
     * <p>This is the same as doing StatusHolder.removeStatus(status)</p>
     *
     * @param status status to remove
     */
    public void removeStatus(IStatus status) {
        getStatusData().removeStatus(status);
    }

    /**
     * <p>Checks if the player is afflicted with the given status</p>
     * <p>This is the same as doing StatusHolder.hasStatus(status)</p>
     *
     * @param status status to check for
     * @return       true if afflicted, false otherwise
     */
    public boolean hasStatus(IStatus status) {
        return getTimeLeft(status) > 0;
    }

    /**
     * <p>Gets the time left on the status</p>
     * <p>If the status is not applied or is expired, this returns 0</p>
     * <p>This is the same as doing StatusHolder.getTimeLeft(status)</p>
     *
     * @param status status to check
     * @return       time remaining on the status
     */
    public int getTimeLeft(IStatus status) {
        return getStatusData().getTimeLeft(status);
    }

    /**
     * <p>Makes the player cast the skill with the given name</p>
     * <p>The skill name is not case-sensitive</p>
     * <p>If the player is silenced or stunned this does nothing</p>
     * <p>If the skill is not a target skill or a skill shot, this
     * will do nothing</p>
     * <p>If the skill is on cooldown, the cast doesn't work but
     * the player is notified that it is on cooldown</p>
     * <p>If the player doesn't have enough mana to use the skill,
     * the cast doesn't work but the player is notified that they
     * don't have enough mana</p>
     * <p>If the skill was successfully used, mana is deducted from
     * them according to the skill cost and then the cooldown for
     * the skill is started if applicable.</p>
     *
     * @param skillName skill to cast
     * @throws IllegalArgumentException when the skill name is invalid
     */
    public void castSkill(String skillName) {
        ClassSkill skill = plugin.getSkill(skillName);

        // Invalid skill
        if (skill == null) throw new IllegalArgumentException("Invalid skill: " + skillName);

        SkillStatus status = skill.checkStatus(this);
        int level = getSkillLevel(skill.getName());
        skillsBeingCast.push(skill);

        // Silenced
        if (hasStatus(Status.SILENCE) || hasStatus(Status.STUN)) {
            String node;
            int left;
            if (hasStatus(Status.STUN)) {
                node = StatusNodes.STUNNED;
                left = getTimeLeft(Status.STUN);
            }
            else {
                node = StatusNodes.SILENCED;
                left = getTimeLeft(Status.SILENCE);
            }
            plugin.sendStatusMessage(player.getPlayer(), node, left);
        }

        // Skill is on cooldown
        else if (status == SkillStatus.ON_COOLDOWN) {
            List<String> messages = plugin.getMessages(OtherNodes.ON_COOLDOWN, true);
            for (String message : messages) {
                message = message.replace("{cooldown}", skill.getCooldown(this) + "")
                        .replace("{skill}", skill.getName());

                player.getPlayer().sendMessage(message);
            }
        }

        // Skill requires more mana
        else if (status == SkillStatus.MISSING_MANA) {
            List<String> messages = plugin.getMessages(OtherNodes.NO_MANA, true);
            int cost = (int)skill.getAttribute(SkillAttribute.MANA, level);
            for (String message : messages) {
                message = message.replace("{missing}", (cost - getMana()) + "")
                        .replace("{mana}", getMana() + "")
                        .replace("{cost}", cost + "")
                        .replace("{skill}", skill.getName());

                player.getPlayer().sendMessage(message);
            }
        }

        // Check for skill shots
        else if (skill instanceof SkillShot) {

            Player p = getPlayer();
            PlayerCastSkillEvent event = new PlayerCastSkillEvent(this, skill);
            plugin.getServer().getPluginManager().callEvent(event);

            // Don't cast if cancelled
            if (!event.isCancelled()) {

                try {

                    // Try to cast the skill
                    if (((SkillShot) skill).cast(p, getSkillLevel(skill.getName()))) {

                        // Send the message
                        plugin.sendSkillMessage(skill, p);

                        // Start the cooldown
                        skill.startCooldown(this);

                        // Use mana if successful
                        if (plugin.isManaEnabled()) useMana((int)skill.getAttribute(SkillAttribute.MANA, level));
                    }
                }

                // Problem with the skill
                catch (Exception ex) {
                    ex.printStackTrace();
                    getAPI().getLogger().severe("Failed to cast skill - " + skill.getName() + ": Internal skill error");
                }
            }
        }

        // Check for Target Skills
        else if (skill instanceof TargetSkill) {

            // Must have a target
            Player p = getPlayer();
            LivingEntity target = TargetHelper.getLivingTarget(p, skill.getAttribute(SkillAttribute.RANGE, level));
            PlayerCastSkillEvent event = new PlayerCastSkillEvent(this, skill);
            plugin.getServer().getPluginManager().callEvent(event);

            // Don't cast if cancelled
            if (target != null && !event.isCancelled()) {

                try {
                    // Try to cast the skill
                    if (((TargetSkill) skill).cast(p, target, level, Protection.isAlly(p, target))) {

                        // Send the message
                        plugin.sendSkillMessage(skill, p);

                        // Apply the cooldown
                        skill.startCooldown(this);

                        // Use mana if successful
                        if (plugin.isManaEnabled()) useMana((int)skill.getAttribute(SkillAttribute.MANA, level));
                    }
                }

                // Problem with the skill
                catch (Exception ex) {
                    ex.printStackTrace();
                    getAPI().getLogger().severe("Failed to cast skill - " + skill.getName() + ": Internal skill error");
                }
            }
        }

        skillsBeingCast.clear();
    }

    /**
     * <p>Saves the player data to the configuration section</p>
     * <p>Saving is handled automatically by the API so you
     * shouldn't use this unless you plan on managing multiple
     * profiles for each player.</p>
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
        saveValues(config.createSection(path + PlayerValues.VALUES));
        for (Map.Entry<String, Integer> entry : skills.entrySet()) {
            config.set(path + PlayerValues.SKILLS + "." + entry.getKey(), entry.getValue());
        }
        for (Map.Entry<Material, String> entry : binds.entrySet()) {
            if (entry.getKey() == null) continue;
            config.set(path + PlayerValues.BIND + "." + entry.getKey().name(), entry.getValue());
        }
    }
}
