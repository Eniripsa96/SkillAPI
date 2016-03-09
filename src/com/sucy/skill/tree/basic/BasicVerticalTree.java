/**
 * SkillAPI
 * com.sucy.skill.tree.basic.BasicVerticalTree
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
package com.sucy.skill.tree.basic;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.classes.RPGClass;
import com.sucy.skill.api.exception.SkillTreeException;
import com.sucy.skill.api.skills.Skill;

import java.util.Collections;
import java.util.List;

/**
 * A basic implementation of a horizontally ascending skill tree
 */
public class BasicVerticalTree extends InventoryTree
{
    private int width;

    /**
     * Constructor
     *
     * @param api  api reference
     * @param tree class reference
     */
    public BasicVerticalTree(SkillAPI api, RPGClass tree)
    {
        super(api, tree);
    }

    /**
     * Arranges the skill tree
     *
     * @throws SkillTreeException
     */
    @Override
    public void arrange(List<Skill> skills) throws SkillTreeException
    {

        // Arrange the skill tree
        Collections.sort(skills, comparator);
        height = 0;
        int i = -1;
        Skill skill;

        // Cycle through all skills that do not have children, put them
        // at the far left, and branch their children to the right
        while (++i < skills.size() && (skill = skills.get(i)).getSkillReq() == null)
        {
            skillSlots.put(i, skill);
            width = placeChildren(skills, skill, i + 9, 0);
        }

        // Too large
        if (width >= 9)
        {
            throw new SkillTreeException("Error generating the skill tree: " + tree.getName() + " - too large of a tree!");
        }
    }

    /**
     * Places the children of a skill to the right of it, branching downward
     *
     * @param skills skills included in the tree
     * @param skill  skill to add the children of
     * @param slot   slot ID for the first child
     * @param depth  current depth of recursion
     *
     * @throws SkillTreeException
     */
    private int placeChildren(List<Skill> skills, Skill skill, int slot, int depth) throws SkillTreeException
    {

        // Update tree height
        if (depth + 1 > height)
        {
            height = depth + 1;
        }

        // Add in all children
        int width = 0;
        for (Skill s : skills)
        {
            if (s.getSkillReq() == null)
            {
                continue;
            }
            if (s.getSkillReq().equalsIgnoreCase(skill.getName()))
            {
                skillSlots.put(slot + width, s);
                width += placeChildren(skills, s, slot + width + 9, depth + 1);
            }
        }

        return Math.max(width, 1);
    }
}
