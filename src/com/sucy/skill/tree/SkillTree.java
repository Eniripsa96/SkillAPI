package com.sucy.skill.tree;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.classes.RPGClass;
import com.sucy.skill.api.exception.SkillTreeException;
import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.data.Permissions;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a skill tree that contains an arrangement of a class's skills
 * for a player to browse and level up or refund skills.
 */
public abstract class SkillTree
{
    protected final SkillAPI api;
    protected final RPGClass tree;

    /**
     * Constructor
     *
     * @param api api reference
     */
    public SkillTree(SkillAPI api, RPGClass tree)
    {
        this.api = api;
        this.tree = tree;
    }

    /**
     * Checks whether or not the player can be shown the skill
     *
     * @param player player to check for
     * @param skill  skill to check for permissions
     *
     * @return true if can be shown, false otherwise
     */
    public boolean canShow(Player player, Skill skill)
    {
        if (skill.canAutoLevel() && !skill.canCast() && !SkillAPI.getSettings().isShowingAutoSkills()) return false;
        return !skill.needsPermission() || player.hasPermission(Permissions.SKILL) || player.hasPermission(Permissions.SKILL + "." + skill.getName().toLowerCase().replaceAll(" ", "-"));
    }

    /**
     * Arranges the skill tree
     *
     * @throws com.sucy.skill.api.exception.SkillTreeException
     */
    public void arrange() throws SkillTreeException
    {

        // Get included skills
        ArrayList<Skill> skills = new ArrayList<Skill>();
        for (Skill skill : tree.getSkills())
        {
            if (!SkillAPI.isSkillRegistered(skill))
            {
                api.getLogger().severe("Failed to add skill to tree - " + skill + ": Skill does not exist");
                continue;
            }
            if (SkillAPI.getSettings().isShowingAutoSkills() || !skill.canAutoLevel())
            {
                skills.add(skill);
            }
        }

        // Arrange the skills
        arrange(skills);
    }

    /**
     * Arranges the skill tree
     *
     * @param skills skills to arrange
     */
    protected abstract void arrange(List<Skill> skills) throws SkillTreeException;

    /**
     * Checks if the class has the skill registered
     *
     * @param skill skill to check
     *
     * @return true if registered, false otherwise
     */
    public abstract boolean hasSkill(Skill skill);
}
