/**
 * SkillAPI
 * com.sucy.skill.tree.SkillTree
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Steven Sucy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software") to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.sucy.skill.tree;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.classes.RPGClass;
import com.sucy.skill.api.exception.SkillTreeException;
import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.data.Permissions;
import com.sucy.skill.log.Logger;
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
                Logger.invalid("Failed to add skill to tree - " + skill + ": Skill does not exist");
                continue;
            }
            if (SkillAPI.getSettings().isShowingAutoSkills() || skill.canCast() || !skill.canAutoLevel())
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
