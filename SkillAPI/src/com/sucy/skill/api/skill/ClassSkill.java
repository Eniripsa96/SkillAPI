package com.sucy.skill.api.skill;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.Attributed;
import com.sucy.skill.api.CustomClass;
import com.sucy.skill.api.PlayerSkills;
import com.sucy.skill.config.SkillValues;
import com.sucy.skill.language.SkillNodes;
import com.sucy.skill.DamageLoreRemover;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>Base class for skills</p>
 * <p>
 *     You will extend this class for each of your skills. You can also implement
 *     interfaces for additional effects such as one of the interacese provided
 *     (SkillShot, PassiveSkill, or TargetSkill) or Listener for more custom
 *     control over the usage of the skill.
 * </p>
 * <p>
 *     Most of the members in this class are for the API's usage. The main members you will
 *     use for creating skills are:
 *     <li>description - a list of the lines for your skill description</li>
 *     <li>api - a reference to SkillAPI</li>
 *     <li>startCooldown() - method to put the skill on cooldown</li>
 *     <li>refreshCooldown() - method to take a skill off cooldown</li>
 *     <li>addCooldown(int amount) - adds to the cooldown left</li>
 *     <li>subtractCooldown(int amount) - reduces the cooldown left</li>
 *     <br/>
 *     description is used for setting the description for your skill. This
 *     is done in the constructor of your skill and you can add as many
 *     lines as you want. The only limitation to this is you can't set it
 *     to null without facing some problems.
 *     <br/>
 *     The API reference is there just in case you need a plugin reference
 *     during initialization or if you don't want to pass in your own plugin
 *     reference. It's useful during initialization because skills are created
 *     before your plugin will enable when set up correctly (your plugin should
 *     depend or at least softdepend SkillAPI).
 *     <br/>
 *     The cooldown methods are only for skills that influence the cooldown
 *     of themselves or other skills, such as cutting the cooldown in half
 *     if it hits or reducing the cooldown of other skills by some amount.
 * </p>
 * <p>For a detailed tutorial on how to use this class, visit
 * <a href="http://dev.bukkit.org/bukkit-plugins/skillapi/pages/skill-tutorial/"/></p>
 */
public abstract class ClassSkill extends Attributed {

    private static final DecimalFormat FORMAT = new DecimalFormat("#########0.0#");
    private final HashMap<String, Long> timers = new HashMap<String, Long>();
    private final String name;

    /**
     * <p>Type of the skill, mostly just for aesthetics as it is just displayed in the skill tree</p>
     * <p>This is made protected for use by dynamic skills. You should not normally use this field
     * as it is set via the constructor</p>
     */
    protected SkillType type;

    /**
     * <p>Indicator for the skill in a skill tree</p>
     * <p>This is made protected for use by dynamic skills. You should not normally use this field
     * as it is set via the constructor</p>
     */
    protected ItemStack indicator;

    /**
     * <p>Maximum level the skill can reach</p>
     * <p>This is made protected for use by dynamic skills. You should not normally use this field
     * as it is set via the constructor</p>
     */
    protected int maxLevel;

    /**
     * <p>Skill that this skill requires before being able to be upgraded</p>
     * <p>This is made protected for use by dynamic skills. You should not normally use this field
     * as it is set via the constructor</p>
     */
    protected String skillReq;

    /**
     * <p>Level of the required skill that is needed before this can be upgraded</p>
     * <p>This is made protected for use by dynamic skills. You should not normally use this field
     * as it is set via the constructor</p>
     */
    protected int skillReqLevel;

    /**
     * <p>The message that is displayed around the caster when the skill is cast</p>
     * <p>This can be set whenever, but is generally only set in the constructor</p>
     */
    protected String message;

    /**
     * <p>Whether or not the skill requires permission to be used</p>
     * <p>If you want this class to be a restricted skill that is only
     * visible to those who have permission such as a donator or an admin
     * skill or one obtained through a quest, set this to true</p>
     * <p>This should be set in the constructor of your skill</p>
     */
    protected boolean needsPermission;

    /**
     * <p>The permissions that are set when the skill is acquired</p>
     * <p>These are given to the player once they unlock the skill
     * and removed once they no longer have the skill</p>
     * <p>Permissions are only given if Vault is installed as well</p>
     */
    protected final ArrayList<String> permissions = new ArrayList<String>();

    /**
     * <p>Description of the skill</p>
     * <p>You can manipulate this list directly to set the description
     * for your skill. Don't set it to null if you don't want a description though,
     * just clear it.</p>
     */
    protected final ArrayList<String> description = new ArrayList<String>();

    /**
     * <p>SkillAPI reference</p>
     * <p>You can use this reference as the plugin for your skill instead of your own.
     * This can be useful as your skill is initialized before you own plugin is enabled.</p>
     */
    protected final SkillAPI api;

    /**
     * <p>Constructor</p>
     * <p>This is used for basic skills that don't require another skill to be leveled up before this one</p>
     *
     * @param name      skill name
     * @param type      skill type
     * @param indicator skill tree indicator
     * @param maxLevel  maximum skill level
     */
    public ClassSkill(String name, SkillType type, Material indicator, int maxLevel) {
        this(name, type, new ItemStack(indicator), maxLevel, null, 0);
    }

    /**
     * <p>Constructor</p>
     * <p>This is used for higher level skills that require another skill to be leveled up before this one</p>
     *
     * @param name      skill name
     * @param type      skill type
     * @param indicator skill tree indicator
     * @param maxLevel  maximum skill level
     * @param skillReq  skill required before this one
     */
    public ClassSkill(String name, SkillType type, Material indicator, int maxLevel, String skillReq, int skillReqLevel) {
        this(name, type, new ItemStack(indicator), maxLevel, skillReq, skillReqLevel);
    }

    /**
     * <p>Constructor</p>
     * <p>This is used for basic skills that don't require another skill to be leveled up before this one</p>
     * <p>The indicator's display name and lore will not be saved, only the type and durability</p>
     *
     * @param name      skill name
     * @param type      skill type
     * @param indicator skill tree indicator
     * @param maxLevel  maximum skill level
     */
    public ClassSkill(String name, SkillType type, ItemStack indicator, int maxLevel) {
        this(name, type, indicator, maxLevel, null, 0);
    }

    /**
     * <p>Constructor</p>
     * <p>This is used for higher level skills that require another skill to be leveled up before this one</p>
     * <p>The indicator's display name and lore will not be saved, only the type and durability</p>
     *
     * @param name      skill name
     * @param type      skill type
     * @param indicator skill tree indicator
     * @param maxLevel  maximum skill level
     * @param skillReq  skill required before this one
     */
    public ClassSkill(String name, SkillType type, ItemStack indicator, int maxLevel, String skillReq, int skillReqLevel) {
        this.type = type;
        this.name = name;
        this.indicator = indicator;
        this.maxLevel = maxLevel;
        this.skillReq = skillReq;
        this.skillReqLevel = skillReqLevel;
        needsPermission = false;
        api = (SkillAPI) Bukkit.getPluginManager().getPlugin("SkillAPI");
    }

    /**
     * <p>Retrieves the SkillAPI reference</p>
     *
     * @return API reference
     */
    public SkillAPI getAPI() {
        return api;
    }

    /**
     * <p>Retrieves the name of the skill</p>
     *
     * @return skill name
     */
    public String getName() {
        return name;
    }

    /**
     * <p>Retrieves the maximum level this skill can be upgraded to</p>
     *
     * @return the maximum level of the skill
     */
    public int getMaxLevel() {
        return maxLevel;
    }

    /**
     * <p>Checks whether or not the 'message' field has been set to anything</p>
     * <p>This returns true if the field is anything except null</p>
     *
     * @return true if a message has been set, false otherwise
     */
    public boolean hasMessage() {
        return message != null;
    }

    /**
     * <p>Retrieves the custom message of the skill</p>
     * <p>This overrides the global message in the language config</p>
     * <p>If no message was set, this returns null</p>
     *
     * @return custom skill message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return whether or not permission is needed to use this skill
     */
    public boolean needsPermission() {
        return needsPermission;
    }

    /**
     * <p>Checks whether or not the skill grants permissions when unlocked</p>
     * <p>If no permissions were added to the list, this returns false</p>
     *
     * @return true if grants permissions, false otherwise
     */
    public boolean hasPermissions() {
        return permissions.size() > 0;
    }

    /**
     * <p>Retrieves the permissions granted by unlocking this skill</p>
     * <p>If this skill doesn't grant permissions, this returns an empty list</p>
     *
     * @return list of permissions granted by this skill
     */
    public List<String> getPermissions() {
        return permissions;
    }

    /**
     * <p>Retrieves the base item for the skill indicator</p>
     * <p>This does not include the skill data provided when
     * getting the indicator for a player</p>
     *
     * @return the material used to represent the skill in skill trees
     */
    public ItemStack getIndicator() {
        return indicator;
    }

    /**
     * @return the type of skill
     */
    public SkillType getType() {
        return type;
    }

    /**
     * @return skill required to have to get this one
     */
    public String getSkillReq() {
        return skillReq;
    }

    /**
     * @return Level of the required skill that is needed
     */
    public int getSkillReqLevel() {
        return skillReqLevel;
    }

    /**
     * @return the description lore lines for the indicator
     */
    public List<String> getDescription() {
        return description;
    }

    /**
     * <p>Sets the required skill for this skill</p>
     * <p>Generally you will not use this method and provide
     * the data to the constructor instead.</p>
     *
     * @param skill required skill
     * @param level level of the required skill
     */
    public void setSkillReq(ClassSkill skill, int level) {
        skillReq = skill == null ? null : skill.getName();
        skillReqLevel = level;
    }

    /**
     * <p>Sets the maximum level of the skill</p>
     * <p>Generally you will not use this method and provide
     * the data to the constructor instead</p>
     *
     * @param level maximum level
     */
    public void setMaxLevel(int level) {
        maxLevel = level;
    }

    /**
     * <p>Assigns a new indicator for the skill</p>
     * <p>Generally you will not use this method and provide
     * the data to the constructor instead</p>
     *
     * @param mat new indicator
     */
    public void setIcon(Material mat) {
        indicator = new ItemStack(mat);
    }

    /**
     * <p>Assigns a new indicator for the skill</p>
     * <p>Generally you will not use this method and provide
     * the data to the constructor instead</p>
     *
     * @param item new indicator
     */
    public void setIcon(ItemStack item) {
        indicator = item;
    }

    /**
     * <p>Sets the type of the skill</p>
     * <p>Generally you will not use this method and provide
     * the data to the constructor instead</p>
     *
     * @param type new type
     */
    public void setType(SkillType type) {
        this.type = type;
    }

    /**
     * <p>Marks this skill as being used</p>
     * <p>This is handled for normal TargetSkill and SkillShot usages, but for your
     * own passive tasks or listeners that call the skill effects at other times,
     * you should use this to begin your skill and stopUsage() to end your skill.</p>
     * <p>Not using these methods as specified will cause the PlayerOnSkillHitEvent not
     * to detect your skill and the SpecialEntityDamagedByEntityEvent, PlayerOnHitEvent,
     * and PlayerOnDamagedEvents not to know that any damage you deal was caused by a skill.</p>
     * <p>If your skill is triggered by another skill's damage, the events described above
     * will detect that a skill is being used, but the one that triggered yours instead of
     * your skill itself. When this happens, the damage your skill deals will be counted
     * as being dealt by the other one.</p>
     */
    public void beginUsage() {
        PlayerSkills.skillsBeingCast.push(this);
    }

    /**
     * <p>Marks this skill as no longer being used</p>
     * <p>This does nothing if your skill already is not marked as being used</p>
     * <p>This is handled for normal TargetSkill and SkillShot usages, but for your
     * own passive tasks or listeners that call the skill effects at other times,
     * you should use this to begin your skill and stopUsage() to end your skill.</p>
     * <p>Not using these methods as specified will cause the PlayerOnSkillHitEvent not
     * to detect your skill and the SpecialEntityDamagedByEntityEvent, PlayerOnHitEvent,
     * and PlayerOnDamagedEvents not to know that any damage you deal was caused by a skill.</p>
     * <p>If your skill is triggered by another skill's damage, the events described above
     * will detect that a skill is being used, but the one that triggered yours instead of
     * your skill itself. When this happens, the damage your skill deals will be counted
     * as being dealt by the other one.</p>
     */
    public void stopUsage() {
        for (int i = PlayerSkills.skillsBeingCast.indexOf(this); i >= 0 && i < PlayerSkills.skillsBeingCast.size();) {
            PlayerSkills.skillsBeingCast.pop();
        }
    }

    /**
     * <p>Creates a new indicator for this skill using the data of the provided player and skill level</p>
     * <p>If the player does not have a class, this returns null</p>
     * <p>If the player does not have the skill available, this returns null</p>
     *
     * @param player player data
     * @return       indicator item stack
     */
    public ItemStack getIndicator(PlayerSkills player) {
        if (player.hasSkill(getName())) {
            return getIndicator(player, player.getSkillLevel(getName()));
        }
        return null;
    }

    /**
     * <p>Creates a new indicator for this skill using the data of the provided player and skill level</p>
     * <p>If the player does not have a class, this returns null</p>
     * <p>This is primarily for API usage when generating skill trees. Because of this,
     * you will most of the time not use this method.</p>
     *
     * @param player player data
     * @param level  current skill level
     * @return       indicator item stack
     */
    public ItemStack getIndicator(PlayerSkills player, int level) {

        // No indicator for players without a class
        if (!player.hasClass()) return null;

        CustomClass c = getAPI().getClass(player.getClassName());
        List<String> layout = api.getMessages(SkillNodes.LAYOUT, false);
        boolean first = true;

        ItemStack item = indicator.clone();
        item.setAmount(Math.max(1, level));
        ItemMeta meta = item.hasItemMeta() ? item.getItemMeta() : Bukkit.getItemFactory().getItemMeta(item.getType());
        ArrayList<String> lore = new ArrayList<String>();

        // Cycle through each line, parse it, and add it to the display
        for (String line : layout) {
            List<String> results = new ArrayList<String>();

            // Title filter
            if (line.contains("{title}")) {
                String title = api.getMessage(SkillNodes.TITLE, false);

                title = title.replace("{name}", getName())
                        .replace("{level}", level + "")
                        .replace("{max}", getMaxLevel() + "");

                line = line.replace("{title}", title);
            }

            // Type filter
            if (line.contains("{type}")) {
                String type = api.getMessage(SkillNodes.TYPE, false);
                type = type.replace("{name}", api.getMessage(getType().getNode(), false));
                line = line.replace("{type}", type);
            }

            // Requirement Filter
            if (line.contains("{requirements}")) {

                int requiredLevel = (int)getAttribute(SkillAttribute.LEVEL, level + 1);
                line = line.replace("{requirements}",
                        getRequirementString(SkillAttribute.LEVEL, requiredLevel, player.getLevel() >= requiredLevel));

                int requiredPoints = (int)getAttribute(SkillAttribute.COST, level + 1);
                results.add(getRequirementString(SkillAttribute.COST, requiredPoints, player.getPoints() >= requiredPoints));

                String skillReq = getSkillReq();
                if (skillReq != null) {
                    results.add(getRequirementString(skillReq, skillReqLevel,
                            player.hasSkill(skillReq) && player.getSkillLevel(skillReq) >= skillReqLevel));
                }
            }

            // Attributes filter
            if (line.contains("{attributes}")) {

                boolean useLine = true;

                // Go through each attribute
                for (String attribute : getAttributeNames()) {
                    if (attribute.equals(SkillAttribute.COST) || attribute.equals(SkillAttribute.LEVEL))
                        continue;

                    // Get the values
                    double oldValue = getAttribute(attribute, level);
                    double newValue = getAttribute(attribute, level + 1);

                    // Level 0 doesn't count
                    if (level == 0) oldValue = newValue;
                    if (level == maxLevel) newValue = oldValue;

                    String attLine;

                    // Changing attribute
                    if (oldValue != newValue) {

                        attLine = api.getMessage(SkillNodes.ATTRIBUTE_CHANGING, false);
                        attLine = attLine.replace("{new}", format(newValue) + "");
                    }

                    // Not changing attribute
                    else attLine = api.getMessage(SkillNodes.ATTRIBUTE_NOT_CHANGING, false);

                    attLine = attLine.replace("{value}", format(oldValue) + "")
                            .replace("{name}", attribute.replace("Mana", c.getManaName()));

                    // Line replace
                    if (useLine) {
                        useLine = false;
                        line = line.replace("{attributes}", attLine);
                    }

                    // Add to results
                    else results.add(attLine);
                }

                // No attributes present
                if (useLine) {
                    line = line.replace("{attributes}", api.getMessage(SkillNodes.ATTRIBUTE_NONE, false));
                }
            }

            // Description filter
            if (line.contains("{description}")) {

                // No description
                if (getDescription().size() == 0) {
                    line = line.replace("{description}", api.getMessage(SkillNodes.DESCRIPTION_NONE, false));
                }

                // Go through each line
                else {

                    // First line
                    String descFirst = api.getMessage(SkillNodes.DESCRIPTION_FIRST, false);
                    descFirst = descFirst.replace("{line}", description.get(0));
                    line = line.replace("{description}", descFirst);

                    // Other lines
                    String descLine = api.getMessage(SkillNodes.DESCRIPTION_OTHER, false);
                    for (int i = 1; i < description.size(); i++) {
                        results.add(descLine.replace("{line}", description.get(i)));
                    }
                }
            }

            results.add(0, line);

            // Add the resulting lines
            for (String result : results) {

                result = result.replaceAll("&([0-9a-fl-orA-FL-OR])", ChatColor.COLOR_CHAR + "$1");

                // First line is assigned to the item's name
                if (first) {
                    first = false;
                    meta.setDisplayName(result);
                }

                // Anything else appends to the lore
                else lore.add(result);
            }
        }

        // Click string at the bottom
        if (api.usingClickCombos() && (this instanceof SkillShot || this instanceof TargetSkill)) {
            lore.add("");
            lore.add(c.getClickString(this));
        }

        meta.setLore(lore);
        item.setItemMeta(meta);
        return DamageLoreRemover.removeAttackDmg(item);
    }

    /**
     * Formats a double value for display
     *
     * @param value value to format
     * @return      formatted string
     */
    private String format(double value) {
        if ((int)value == value) {
            return "" + (int)value;
        }
        return FORMAT.format(value);
    }

    /**
     * Gets the requirement string for the skill icon
     *
     * @param name      requirement name
     * @param value     requirement value
     * @param satisfied whether or not it is satisfied
     * @return          requirement string
     */
    private String getRequirementString(String name, int value, boolean satisfied) {
        String reqString = api.getMessage(satisfied ?
                SkillNodes.REQUIREMENT_MET
                : SkillNodes.REQUIREMENT_NOT_MET, true);

        return reqString.replace("{name}", name)
                .replace("{value}", value + "");
    }

    /**
     * <p>Checks the availability status of the skill for the player</p>
     * <p>The usage for this is most of the time handled by the API. You
     * normally don't have to use this method</p>
     *
     * @param player      player to check for
     * @return            status of the skill for the player
     */
    public SkillStatus checkStatus(PlayerSkills player) {

        // See if it is on cooldown
        if (getCooldown(player) > 0) return SkillStatus.ON_COOLDOWN;

        // If mana is enabled, check to see if the player has enough
        if (api.isManaEnabled()) {
            int level = player.getSkillLevel(name);
            double manaCost = getAttribute(SkillAttribute.MANA, level);

            if (player.getMana() < manaCost) return SkillStatus.MISSING_MANA;
        }

        // The skill is available when both cooldown and mana were ok
        return SkillStatus.READY;
    }

    /**
     * <p>Starts the cooldown for this skill for the player</p>
     * <p>If the skill was already on cooldown, this just restarts the timer
     * rather than adding onto it</p>
     *
     * @param player player to start the cooldown for
     */
    public void startCooldown(PlayerSkills player) {
        timers.put(player.getName(), System.currentTimeMillis());
    }

    /**
     * <p>Refreshes the cooldown for the skill</p>
     * <p>This allows the player to use the skill again</p>
     *
     * @param player player to refresh the cooldown for
     */
    public void refreshCooldown(PlayerSkills player) {
        timers.put(player.getName(), 0L);
    }

    /**
     * Subtracts from the remaining time left on the cooldown for the player
     *
     * @param player  player to subtract for
     * @param seconds seconds to reduce the cooldown by
     */
    public void subtractCooldown(PlayerSkills player, int seconds) {
        if (!timers.containsKey(player.getName())) return;
        timers.put(player.getName(), timers.get(player.getName()) - seconds * 1000);
    }

    /**
     * Adds to the remaining time left on the cooldown for the player
     *
     * @param player  player to add to
     * @param seconds seconds to increase the cooldown by
     */
    public void addCooldown(PlayerSkills player, double seconds) {
        if (getCooldown(player) == 0) {
            int level = player.getSkillLevel(name);
            timers.put(player.getName(), System.currentTimeMillis() + (int)(1000 * seconds)
                    - (int)(getAttribute(SkillAttribute.COOLDOWN, level) * 1000));
        }
        timers.put(player.getName(), timers.get(player.getName()) + (int)(seconds * 1000));
    }

    /**
     * Gets the cooldown remaining on the skill
     *
     * @param player player to check for
     * @return       time left on cooldown (0 if off cooldown)
     */
    public int getCooldown(PlayerSkills player) {

        // No timer means its not on cooldown
        if (!timers.containsKey(player.getName())) return 0;

        int level = player.getSkillLevel(name);
        long passed = System.currentTimeMillis() - timers.get(player.getName());
        long cd = (long)(1000 * getAttribute(SkillAttribute.COOLDOWN, level));
        return cd > passed ? (int)((cd - passed) / 1000 + 1) : 0;
    }

    /**
     * Validates that the default attributes are set and sets them if they aren't there
     */
    public void validateDefaults() {
        checkDefault(SkillAttribute.LEVEL, 1, 0);
        checkDefault(SkillAttribute.COST, 1, 0);
        if (this instanceof SkillShot || this instanceof TargetSkill) {
            checkDefault(SkillAttribute.MANA, 0, 0);
            checkDefault(SkillAttribute.COOLDOWN, 0, 0);
            if (this instanceof TargetSkill) {
                checkDefault(SkillAttribute.RANGE, 6, 0);
            }
        }
    }

    /**
     * Updates the skill from config data
     *
     * @param config configuration data to update from
     */
    public void update(ConfigurationSection config) {

        // Attributes
        for (String attribute : getAttributeNames()) {
            if (config.contains(attribute + "-base")) {
                setBase(attribute, config.getDouble(attribute + "-base"));
                setScale(attribute, config.getDouble(attribute + "-scale", 0));
            }
        }

        // Max level
        maxLevel = config.getInt(SkillValues.MAX_LEVEL);

        // Description
        List<String> description = config.getStringList(SkillValues.DESCRIPTION);
        if (description != null) {
            this.description.clear();
            this.description.addAll(description);
        }

        // Skill Requirement
        if (config.contains(SkillValues.SKILL_REQ)) {
            skillReq = config.getString(SkillValues.SKILL_REQ);
            skillReqLevel = config.getInt(SkillValues.SKILL_REQ_LEVEL, 1);
        }

        // Icon
        parseIndicator(config.getString(SkillValues.INDICATOR));

        // Message
        message = config.getString(SkillValues.MESSAGE);

        // Needed permission
        if (config.contains(SkillValues.NEEDS_PERMISSION)) {
            needsPermission = config.getBoolean(SkillValues.NEEDS_PERMISSION);
        }

        // Permissions
        if (config.contains(SkillValues.PERMISSIONS)) {
            permissions.addAll(config.getStringList(SkillValues.PERMISSIONS));
        }
    }

    /**
     * <p>Parses the indicator from the config string and
     * updates the indicator to the loaded one</p>
     * <p>This is primarily for the API loading in data.
     * You shouldn't have to use this method.</p>
     *
     * @param string config string
     */
    protected void parseIndicator(String string) {
        String[] pieces;
        if (string.contains(",")) pieces = string.split(",");
        else pieces = new String[] { string };
        Material icon = Material.valueOf(pieces[0]);
        if (icon == null) return;
        ItemStack item = new ItemStack(icon);
        if (pieces.length > 1) item.setDurability(Short.parseShort(pieces[1]));
        indicator = item;
    }
}
