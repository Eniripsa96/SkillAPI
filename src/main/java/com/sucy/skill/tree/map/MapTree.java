/**
 * SkillAPI
 * com.sucy.skill.tree.map.MapTree
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
package com.sucy.skill.tree.map;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.classes.RPGClass;
import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.tree.SkillTree;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Represents a Skill Tree that uses a map for its GUI
 */
public class MapTree extends SkillTree
{
    public static final String IDENTIFIER = "" + ChatColor.DARK_GREEN + ChatColor.BLACK + ChatColor.RESET;

    private ArrayList<Skill> skills;

    /**
     * Constructor
     *
     * @param api api reference
     */
    public MapTree(SkillAPI api, RPGClass tree)
    {
        super(api, tree);
    }

    /**
     * Retrieves the list of skills in the tree
     *
     * @return list of skills
     */
    public ArrayList<Skill> getSkills()
    {
        return skills;
    }

    /**
     * Arranges the skill tree
     *
     * @param skills skills to arrange
     */
    @Override
    public void arrange(List<Skill> skills)
    {
        this.skills = new ArrayList<Skill>();
        this.skills.addAll(skills);
        Collections.sort(skills, MAP_COMPARATOR);
    }

    /**
     * Checks if the class has the skill registered
     *
     * @param skill skill to check
     *
     * @return true if registered, false otherwise
     */
    public boolean hasSkill(Skill skill)
    {
        return skills.contains(skill);
    }

    /**
     * Comparator for skills for level trees
     */
    private static final Comparator<Skill> MAP_COMPARATOR = new Comparator<Skill>()
    {

        /**
         * Compares skills based on their stats for skill tree arrangement
         *  -> Skills with lower level requirements come first
         *  -> Then its skills with lower costs
         *  -> Then its skills alphabetically
         *
         * @param skill1 skill being compared
         * @param skill2 skill to compare to
         * @return      -1, 0, or 1
         */
        @Override
        public int compare(Skill skill1, Skill skill2)
        {
            return skill1.getLevelReq(0) > skill2.getLevelReq(0) ? 1
                : skill1.getLevelReq(0) < skill2.getLevelReq(0) ? -1
                : skill1.getName().compareTo(skill2.getName());
        }
    };
}
