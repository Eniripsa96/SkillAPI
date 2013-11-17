package com.sucy.skill.api;

import com.sucy.skill.PermissionNodes;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.event.*;
import com.sucy.skill.api.skill.*;
import com.sucy.skill.api.util.Protection;
import com.sucy.skill.api.util.TargetHelper;
import com.sucy.skill.config.PlayerValues;
import com.sucy.skill.language.OtherNodes;
import com.sucy.skill.language.StatusNodes;
import com.sucy.skill.mccore.CoreChecker;
import com.sucy.skill.mccore.PrefixManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Player class data</p>
 * <p>You should not instantiate any new player data. You may
 * use the references given to you either through events or
 * via the SkillAPI.getPlayer(String) method however to modify
 * the stats of a player.</p>
 */
public final class PlayerSkills extends Valued {

    private HashMap<String, Integer> skills = new HashMap<String, Integer>();
    private HashMap<Material, String> binds = new HashMap<Material, String>();
    private SkillAPI plugin;
    private String player;
    private String tree;
    private int bonusHealth;
    private int points;
    private int level;
    private int mana;
    private int exp;

    /**
     * <p>Constructor</p>
     * <p>Do not use this</p>
     *
     * @param plugin API reference
     * @param player player name
     */
    public PlayerSkills(SkillAPI plugin, String player) {
        this.plugin = plugin;
        this.player = player;
        this.level = 1;
        this.exp = 0;
        this.points = plugin.getStartingPoints();
    }

    /**
     * <p>Constructor</p>
     * <p>Do not use this</p>
     *
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
            CustomClass tree = plugin.getClass(this.tree);
            if (tree == null) {
                setClass(null);
                return;
            }

            if (plugin.getServer().getPlayer(player) != null && CoreChecker.isCoreActive()) {
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

        // Level bar
        updateLevelBar();
    }

    /**
     * @return map of the names and levels of the skills the player has
     */
    public HashMap<String, Integer> getSkills() {
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
     * @return current mana
     */
    public int getMana() {
        return mana;
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
        if (tree == null) {
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

        CustomClass c = plugin.getClass(tree);
        int maxMana = c == null ? 0 : c.getAttribute(ClassAttribute.MANA, level);
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

        int maxMana = getAPI().getClass(tree).getAttribute(ClassAttribute.MANA, level);
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
        if (this.level < skill.getAttribute(SkillAttribute.LEVEL, level))
            return false;

        // Skill cost isn't met
        if (points < skill.getAttribute(SkillAttribute.COST, level))
            return false;

        // Doesn't have prerequisite
        if (skill.getSkillReq() != null && getSkillLevel(skill.getSkillReq()) < skill.getSkillReqLevel())
            return false;

        // Apply passive skill effects
        if (skill instanceof PassiveSkill)
            ((PassiveSkill) skill).onUpgrade(plugin.getServer().getPlayer(getName()), level + 1);

        // Upgrade the skill
        this.points -= skill.getAttribute(SkillAttribute.COST, level);
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

        // If the player was reverted to no class, clear all data
        if (this.tree == null) {
            level = 1;
            points = plugin.getStartingPoints();
            exp = 0;
            stopPassiveAbilities();
            skills.clear();
            binds.clear();
            if (CoreChecker.isCoreActive())
                PrefixManager.clearPrefix(player);
            updateHealth();
            updateLevelBar();

            plugin.getServer().getPluginManager().callEvent(
                    new PlayerClassChangeEvent(this, plugin.getClass(prevTree), null));
            return;
        }

        CustomClass tree = plugin.getClass(className);

        // If not resetting, simply remove any skills no longer in the tree
        if (!plugin.doProfessionsReset()) {
            List<String> list = new ArrayList<String>();
            for (String skill : skills.keySet()) {
                if (tree.hasSkill(skill)) continue;
                list.add(skill);
                ClassSkill s = getAPI().getSkill(skill);
                int level = getSkillLevel(skill);
                for (int i = 1; i <= level; i++) {
                    points += s.getAttribute(SkillAttribute.COST, i);
                }
                if (s instanceof PassiveSkill) {
                    ((PassiveSkill)s).stopEffects(plugin.getServer().getPlayer(player), level);
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
            skills.put(skill.toLowerCase(), 0);
        }

        // Set mana if just starting
        if (prevTree == null) {
            mana = plugin.getClass(this.tree).getAttribute(ClassAttribute.MANA, level);
        }

        // Set the new prefix for the class
        if (CoreChecker.isCoreActive())
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
        if (plugin.getServer().getPlayer(player) == null) return;
        if (tree == null || plugin.oldHealthEnabled()) {
            plugin.getServer().getPlayer(player).setHealthScale(20.0);
            applyMaxHealth(20 + bonusHealth);
        }
        else {
            plugin.getServer().getPlayer(player).setHealthScaled(false);
            applyMaxHealth(plugin.getClass(tree).getAttribute(ClassAttribute.HEALTH, level) + bonusHealth);
        }
    }

    /**
     * <p>Sets the maximum health of the player, adjusting their current health accordingly</p>
     * <p>If the health adjustment would bring their health to or below 0, it leaves them with
     * 1 health instead</p>
     *
     * @param amount new max health
     */
    public void applyMaxHealth(int amount) {
        Player p = plugin.getServer().getPlayer(player);
        if (p == null) return;

        double prevMax = p.getMaxHealth();
        double prevHealth = p.getHealth();

        p.setMaxHealth(amount);
        p.setHealth(Math.max(1, prevHealth + amount - prevMax));
    }

    /**
     * <p>Stops the effects of all passive abilities for the player</p>
     */
    public void stopPassiveAbilities() {
        for (Map.Entry<String, Integer> entry : getSkills().entrySet()) {
            if (entry.getValue() >= 1) {
                ClassSkill s = plugin.getSkill(entry.getKey());
                if (s != null && s instanceof PassiveSkill)
                    ((PassiveSkill) s).stopEffects(plugin.getServer().getPlayer(player), entry.getValue());
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
        return tree != null;
    }

    /**
     * @return level the player is able to profess, less than 1 if unable to profess
     */
    public int getProfessionLevel() {
        if (tree == null) return 1;

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
        if (tree == null)
            return false;

        Player p = plugin.getServer().getPlayer(player);
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
     * <p>Awards the player experience</p>
     * <p>A PlayerExperienceGainEvent is called beforehand so it
     * can be cancelled or modified</p>
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
        else updateLevelBar();
    }

    /**
     * <p>Levels the player up the amount of times</p>
     * <p>If the player's level would exceed their class's maximum level, their level is set
     * to that instead</p>
     *
     * @param amount amount of levels to go up
     */
    public void levelUp(int amount) {
        if (tree == null) throw new IllegalArgumentException("Player cannot level up while not having a class");

        CustomClass skillTree = plugin.getClass(tree);
        if (amount + level > skillTree.getMaxLevel()) amount = skillTree.getMaxLevel() - level;
        if (amount <= 0) return;

        // Add to stats
        level += amount;
        points += amount * plugin.getPointsPerLevel();
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
        if (tree == null) return skillTree.getParent() == null;
        else if (getProfessionLevel() < 1) return false;
        else if (!plugin.hasPermission(plugin.getServer().getPlayer(player), skillTree)) return false;
        return skillTree.getParent() != null && skillTree.getParent().equalsIgnoreCase(tree) && plugin.getClass(tree).getProfessLevel() <= level;
    }

    /**
     * <p>Heals the player the given amount, taking into account status effects and maximum health</p>
     *
     * @param amount amount to heal
     */
    public void heal(int amount) {
        if (hasStatus(Status.CURSE)) amount *= -1;
        if (hasStatus(Status.INVINCIBLE) && amount < 0) return;

        Player p = plugin.getServer().getPlayer(player);
        double health;

        // Adjust the health depending on if the old health bar is enabled
        if (plugin.oldHealthEnabled() && tree != null) {
            CustomClass playerClass = plugin.getClass(tree);
            health = p.getHealth() + amount * 20 / playerClass.getAttribute(ClassAttribute.HEALTH, level);
        }
        else health = p.getHealth() + amount;

            if (health > p.getMaxHealth()) health = p.getMaxHealth();
        if (health < 0) health = 0;
        p.setHealth(health);
    }

    /**
     * Updates the level bar for the player
     */
    public void updateLevelBar() {
        Player player = plugin.getServer().getPlayer(this.player);

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
     * @return amount of experience required for the next level
     */
    public int getRequiredExp() {
        return plugin.getRequiredExp(level);
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
     * <p>Applies a status to the player</p>
     * <p>Runs an event beforehand so it can be cancelled or modified</p>
     * <p>This is the same as getting the status holder for the player and
     * applying it there except for the event that is called</p>
     *
     * @param status   status to apply
     * @param duration duration of the status in seconds
     */
    public void applyStatus(Status status, int duration) {

        PlayerStatusEvent event = new PlayerStatusEvent(this, status, duration);
        plugin.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        long time = (long)(event.getDuration() * 1000);
        getStatusData().addStatus(status, time);
    }

    /**
     * @return the status data for the player
     */
    public StatusHolder getStatusData() {
        return getAPI().getStatusHolder(getAPI().getServer().getPlayer(player));
    }

    /**
     * <p>Removes a status from the player</p>
     * <p>This is the same as doing StatusHolder.removeStatus(status)</p>
     *
     * @param status status to remove
     */
    public void removeStatus(Status status) {
        getStatusData().removeStatus(status);
    }

    /**
     * <p>Checks if the player is afflicted with the given status</p>
     * <p>This is the same as doing StatusHolder.hasStatus(status)</p>
     *
     * @param status status to check for
     * @return       true if afflicted, false otherwise
     */
    public boolean hasStatus(Status status) {
        return getTimeLeft(status) > 0;
    }

    /**
     * <p>Gets the time left on the status</p>
     * <p>This is the same as doing StatusHolder.getTimeLeft(status)</p>
     *
     * @param status status to check
     * @return       time remaining on the status
     */
    public int getTimeLeft(Status status) {
        return getStatusData().getTimeLeft(status);
    }

    /**
     * <p>Makes the player cast the skill with the given name</p>
     * <p>If the player is silenced or stunned this does nothing</p>
     * <p>The skill name is not case-sensitive</p>
     *
     * @param skillName skill to cast
     * @throws IllegalArgumentException when the skill name is invalid
     */
    public void castSkill(String skillName) {
        ClassSkill skill = plugin.getSkill(skillName);

        // Invalid skill
        if (skill == null) throw new IllegalArgumentException("Invalid skill: " + skillName);

        SkillStatus status = skill.checkStatus(this, plugin.isManaEnabled());
        int level = getSkillLevel(skill.getName());

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
            plugin.sendStatusMessage(plugin.getServer().getPlayer(player), node, left);
        }

        // Skill is on cooldown
        else if (status == SkillStatus.ON_COOLDOWN) {
            List<String> messages = plugin.getMessages(OtherNodes.ON_COOLDOWN, true);
            for (String message : messages) {
                message = message.replace("{cooldown}", skill.getCooldown(this) + "")
                        .replace("{skill}", skill.getName());

                plugin.getServer().getPlayer(player).sendMessage(message);
            }
        }

        // Skill requires more mana
        else if (status == SkillStatus.MISSING_MANA) {
            List<String> messages = plugin.getMessages(OtherNodes.NO_MANA, true);
            int cost = skill.getAttribute(SkillAttribute.MANA, level);
            for (String message : messages) {
                message = message.replace("{missing}", (cost - getMana()) + "")
                        .replace("{mana}", getMana() + "")
                        .replace("{cost}", cost + "")
                        .replace("{skill}", skill.getName());

                plugin.getServer().getPlayer(player).sendMessage(message);
            }
        }

        // Check for skill shots
        else if (skill instanceof SkillShot) {

            // Try to cast the skill
            if (((SkillShot) skill).cast(plugin.getServer().getPlayer(player), getSkillLevel(skill.getName()))) {

                // Start the cooldown
                skill.startCooldown(this);

                // Use mana if successful
                if (plugin.isManaEnabled()) useMana(skill.getAttribute(SkillAttribute.MANA, level));
            }
        }

        // Check for Target Skills
        else if (skill instanceof TargetSkill) {

            // Must have a target
            Player p = plugin.getServer().getPlayer(player);
            LivingEntity target = TargetHelper.getLivingTarget(p, skill.getAttribute(SkillAttribute.RANGE, level));
            if (target != null) {

                // Try to cast the skill
                if (((TargetSkill) skill).cast(p, target, level, Protection.isAlly(p, target))) {

                    // Apply the cooldown
                    skill.startCooldown(this);

                    // Use mana if successful
                    if (plugin.isManaEnabled()) useMana(plugin.getSkill(skill.getName()).getAttribute(SkillAttribute.MANA, level));
                }
            }
        }
    }

    /**
     * <p>Saves the player data to the configuration section</p>
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
            config.set(path + PlayerValues.BIND + "." + entry.getKey().name(), entry.getValue());
        }
    }
}
