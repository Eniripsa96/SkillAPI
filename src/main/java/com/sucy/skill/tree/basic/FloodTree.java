/**
 * SkillAPI
 * com.sucy.skill.tree.basic.FloodTree
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Steven Sucy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
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
package com.sucy.skill.tree.basic;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.classes.RPGClass;
import com.sucy.skill.api.exception.SkillTreeException;
import com.sucy.skill.api.skills.Skill;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * <p>Root class for tree implementations based on levels</p>
 * <p>This is still in development to make it work as intended</p>
 */
public class FloodTree extends InventoryTree
{
    /**
     * Constructor
     *
     * @param api  api reference
     * @param tree class reference
     */
    public FloodTree(SkillAPI api, RPGClass tree)
    {
        super(api, tree);
    }

    /**
     * Arranges the skill tree
     *
     * @param skills skills to arrange
     *
     * @throws com.sucy.skill.api.exception.SkillTreeException
     */
    @Override
    protected void arrange(List<Skill> skills) throws SkillTreeException
    {
        Collections.sort(skills, levelComparator);
        for (int i = 0; i < skills.size(); i++)
        {
            skillSlots.put(i, skills.get(i));
        }
        height = (skills.size() + 8) / 9;
    }

    /**
     * Comparator for skills for level trees
     */
    private static final Comparator<Skill> levelComparator = new Comparator<Skill>()
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
                : skill1.getCost(0) > skill2.getCost(0) ? 1
                : skill1.getCost(0) < skill2.getCost(0) ? -1
                : skill1.getName().compareTo(skill2.getName());
        }
    };
}
