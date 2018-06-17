/**
 * SkillAPI
 * com.sucy.skill.tree.basic.BasicHorizontalTree
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
public class BasicHorizontalTree extends InventoryTree
{
    /**
     * Constructor
     *
     * @param api  api reference
     * @param tree class reference
     */
    public BasicHorizontalTree(SkillAPI api, RPGClass tree)
    {
        super(api, tree);
    }

    /**
     * Arranges the skill tree
     */
    @Override
    public void arrange(List<Skill> skills) throws SkillTreeException
    {

        // Arrange the skill tree
        Collections.sort(skills, comparator);
        height = 0;
        int i = 0;
        Skill skill;

        // Cycle through all skills that do not have children, put them
        // at the far left, and branch their children to the right
        while (i < skills.size() && (skill = skills.get(i++)).getSkillReq() == null)
        {
            skillSlots.put(9 * height, skill);
            height += placeChildren(skills, skill, height * 9 + 1, 0);
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
     * @return rows needed to fit the skill and all of its children
     *
     * @throws SkillTreeException
     */
    private int placeChildren(List<Skill> skills, Skill skill, int slot, int depth) throws SkillTreeException
    {

        // Prevent going outside the bounds of the inventory
        if (depth == 9)
        {
            throw new SkillTreeException("Error generating the skill tree: " + tree.getName() + " - too large of a tree!");
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
                skillSlots.put(slot + width * 9, s);
                int w = placeChildren(skills, s, slot + width * 9 + 1, depth + 1);
                width += w;
            }
        }

        // Return the rows needed
        return Math.max(width, 1);
    }
}
