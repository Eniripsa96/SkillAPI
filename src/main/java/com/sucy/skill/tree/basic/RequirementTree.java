/**
 * SkillAPI
 * com.sucy.skill.tree.basic.RequirementTree
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

import java.util.*;

/**
 * Tree implementation based on requirement chains
 */
public class RequirementTree extends InventoryTree
{
    /**
     * Constructor
     *
     * @param api  api reference
     * @param tree class reference
     */
    public RequirementTree(SkillAPI api, RPGClass tree)
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

        // Organize skills into chained and unchained
        List<Skill> chained = new ArrayList<Skill>();
        List<Skill> unchained = new ArrayList<Skill>();
        for (Skill skill : skills)
        {
            if (isChained(skills, skill))
            {
                chained.add(skill);
            }
            else
            {
                unchained.add(skill);
            }
        }

        // Determine the widths for each group
        int unchainedWidth = (unchained.size() + 5) / 6;
        int chainedWidth = 8 - unchainedWidth;
        if (unchainedWidth == 0)
        {
            chainedWidth = 8;
        }
        if (unchainedWidth > 0)
        {
            height = (unchained.size() + unchainedWidth - 1) / unchainedWidth;
        }

        // Fill in the unchained group
        int index = 0;
        Collections.sort(unchained, comparator);
        for (Skill skill : unchained)
        {
            int x = index % unchainedWidth;
            int y = index / unchainedWidth;
            index++;
            skillSlots.put(x + y * 9, skill);
        }

        // Fill in the chained group
        HashMap<Skill, Integer> tier = new HashMap<Skill, Integer>();
        HashMap<Skill, Integer> prevTier = new HashMap<Skill, Integer>();
        int row = 0;
        index = 0;

        do
        {
            // Get the next tier of skills
            tier.clear();
            for (Skill skill : chained)
            {
                boolean hasSkillReq = skill.getSkillReq() != null && SkillAPI.isSkillRegistered(skill.getSkillReq());
                if ((!hasSkillReq && prevTier.size() == 0))
                {
                    tier.put(skill, index++);
                }
                else if (hasSkillReq && prevTier.containsKey(SkillAPI.getSkill(skill.getSkillReq())))
                {
                    tier.put(skill, prevTier.get(SkillAPI.getSkill(skill.getSkillReq())));
                }
            }

            // Fill in the tier
            int filled = 0;
            for (int i = 0; i < index; i++)
            {
                for (Map.Entry<Skill, Integer> entry : tier.entrySet())
                {
                    if (entry.getValue() == i)
                    {
                        int x = filled % chainedWidth + unchainedWidth + 1;
                        int y = filled / chainedWidth + row;
                        filled++;
                        skillSlots.put(x + y * 9, entry.getKey());
                    }
                }
            }

            // Move the current tier to the previous tier
            prevTier.clear();
            for (Map.Entry<Skill, Integer> entry : tier.entrySet())
            {
                prevTier.put(entry.getKey(), entry.getValue());
            }

            // Increment the row
            row += (tier.size() + chainedWidth - 1) / chainedWidth;
        }
        while (tier.size() > 0);

        if (row + 1 > height)
        {
            height = row + 1;
        }
    }

    /**
     * Checks whether or not the skill is attached to a chain
     *
     * @param skills skill list to check in
     * @param skill  skill to check for
     *
     * @return true if attached, false otherwise
     */
    private boolean isChained(List<Skill> skills, Skill skill)
    {
        if (SkillAPI.getSkill(skill.getSkillReq()) != null)
        {
            return true;
        }
        for (Skill s : skills)
        {
            if (SkillAPI.getSkill(s.getSkillReq()) == skill)
            {
                return true;
            }
        }
        return false;
    }
}
