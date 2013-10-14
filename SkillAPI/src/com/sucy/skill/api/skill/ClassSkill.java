package com.sucy.skill.api.skill;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.Attributed;
import com.sucy.skill.skills.PlayerSkills;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>Base class for skills</p>
 * <p>For a detailed tutorial on how to use this class, visit
 * <a href="http://dev.bukkit.org/bukkit-plugins/skillapi/pages/skill-tutorial/"/></p>
 */
public abstract class ClassSkill extends Attributed {

    private final HashMap<String, Long> timers = new HashMap<String, Long>();
    private final SkillType type;
    private final String name;
    private Material indicator;
    private String skillReq;
    private int skillReqLevel;
    private int maxLevel;

    protected final ArrayList<String> description = new ArrayList<String>();
    protected final SkillAPI api;

    /**
     * Constructor
     *
     * @param name      skill name
     * @param type      skill type
     * @param indicator skill tree indicator
     * @param maxLevel  maximum skill level
     */
    public ClassSkill(String name, SkillType type, Material indicator, int maxLevel) {
        this(name, type, indicator, maxLevel, null, 0);
    }

    /**
     * Constructor
     *
     * @param name      skill name
     * @param type      skill type
     * @param indicator skill tree indicator
     * @param maxLevel  maximum skill level
     * @param skillReq  skill required before this one
     */
    public ClassSkill(String name, SkillType type, Material indicator, int maxLevel, String skillReq, int skillReqLevel) {
        this.type = type;
        this.name = name;
        this.indicator = indicator;
        this.maxLevel = maxLevel;
        this.skillReq = skillReq;
        this.skillReqLevel = skillReqLevel;
        api = (SkillAPI) Bukkit.getPluginManager().getPlugin("SkillAPI");
    }

    /**
     * @return skill name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the maximum level of the skill
     */
    public int getMaxLevel() {
        return maxLevel;
    }

    /**
     * @return the material used to represent the skill in skill trees
     */
    public Material getIndicator() {
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
     * Checks the availability status of the skill for the player
     *
     * @param player      player to check for
     * @param manaEnabled whether or not mana is enabled
     * @return            status of the skill for the player
     */
    public SkillStatus checkStatus(PlayerSkills player, boolean manaEnabled) {

        // Get the skill level
        int level = player.getSkillLevel(name);

        // See if it is on cooldown
        if (getCooldown(player) > 0) return SkillStatus.ON_COOLDOWN;

        // If mana is enabled, check to see if the player has enough
        if (manaEnabled) {
            int manaCost = getAttribute(SkillAttribute.MANA, level);

            if (player.getMana() < manaCost) return SkillStatus.MISSING_MANA;
        }

        // The skill is available when both cooldown and mana were ok
        return SkillStatus.READY;
    }

    /**
     * Starts the cooldown for this skill for the player
     *
     * @param player player to start the cooldown for
     */
    public void startCooldown(PlayerSkills player) {
        timers.put(player.getName(), System.currentTimeMillis());
    }

    /**
     * Refreshes the cooldown for the skill enabling them to cast it again
     *
     * @param player player to refresh the cooldown for
     */
    public void refreshCooldown(PlayerSkills player) {
        timers.put(player.getName(), 0L);
    }

    /**
     * Gets the cooldown remaining on the skill
     *
     * @param player player to check for
     * @return       time left on cooldown (0 if off cooldown)
     */
    public int getCooldown(PlayerSkills player) {

        // Make sure they have a timer
        if (!timers.containsKey(player.getName())) {
            timers.put(player.getName(), 0L);
        }

        int level = player.getSkillLevel(name);
        long passed = System.currentTimeMillis() - timers.get(player.getName());
        long cd = (long)(1000 * getAttribute(SkillAttribute.COOLDOWN, level));
        int left = (int)((cd - passed) / 1000 + 1);
        return left > 0 ? left : 0;
    }
}
