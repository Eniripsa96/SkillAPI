package com.sucy.skill.tree;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.CustomClass;
import com.sucy.skill.api.SkillTreeException;
import com.sucy.skill.api.skill.ClassSkill;

import java.util.Collections;
import java.util.List;

/**
 * A basic implementation of a horizontally ascending skill tree
 */
public class BasicVerticalTree extends SkillTree {

    // Width of the skill tree
    private int width;

    /**
     * Constructor
     *
     * @param api  api reference
     * @param tree class reference
     */
    public BasicVerticalTree(SkillAPI api, CustomClass tree) {
        super(api, tree);
    }

    /**
     * Arranges the skill tree
     *
     * @throws SkillTreeException
     */
    @Override
    public void arrange(List<ClassSkill> skills) throws SkillTreeException {

        // Arrange the skill tree
        Collections.sort(skills, comparator);
        height = 0;
        int i = -1;
        ClassSkill skill;

        // Cycle through all skills that do not have children, put them
        // at the far left, and branch their children to the right
        while (++i < skills.size() && (skill = skills.get(i)).getSkillReq() == null) {
            skillSlots.put(i, skill);
            width = placeChildren(skills, skill, i + 9, 0);
        }

        // Too large
        if (width >= 9) throw new SkillTreeException("Error generating the skill tree: " + tree.getName() + " - too large of a tree!");
    }

    /**
     * Places the children of a skill to the right of it, branching downward
     *
     * @param skills skills included in the tree
     * @param skill  skill to add the children of
     * @param slot   slot ID for the first child
     * @param depth  current depth of recursion
     * @throws SkillTreeException
     */
    private int placeChildren(List<ClassSkill> skills, ClassSkill skill, int slot, int depth) throws SkillTreeException {

        // Update tree height
        if (depth + 1 > height) height = depth + 1;

        // Add in all children
        int width = 0;
        for (ClassSkill s : skills) {
            if (s.getSkillReq() == null) continue;
            if (s.getSkillReq().equalsIgnoreCase(skill.getName())) {
                skillSlots.put(slot + width, s);
                width += placeChildren(skills, s, slot + width + 9, depth + 1);
            }
        }

        return Math.max(width, 1);
    }
}
