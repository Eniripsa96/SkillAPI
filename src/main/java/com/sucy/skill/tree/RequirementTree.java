package com.sucy.skill.tree;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.CustomClass;
import com.sucy.skill.api.SkillTreeException;
import com.sucy.skill.api.skill.ClassSkill;

import java.util.*;

/**
 * Tree implementation based on requirement chains
 */
public class RequirementTree extends SkillTree {

    /**
     * Constructor
     *
     * @param api  api reference
     * @param tree class reference
     */
    public RequirementTree(SkillAPI api, CustomClass tree) {
        super(api, tree);
    }

    /**
     * Arranges the skill tree
     *
     * @throws SkillTreeException
     */
    @Override
    public void arrange(List<ClassSkill> skills) throws SkillTreeException {

        // Organize skills into chained and unchained
        List<ClassSkill> chained = new ArrayList<ClassSkill>();
        List<ClassSkill> unchained = new ArrayList<ClassSkill>();
        for (ClassSkill skill : skills) {
            if (isChained(skills, skill)) chained.add(skill);
            else unchained.add(skill);
        }

        // Determine the widths for each group
        int unchainedWidth = (unchained.size() + 5) / 6;
        int chainedWidth = 8 - unchainedWidth;
        if (unchainedWidth == 0) chainedWidth = 8;
        if (unchainedWidth > 0) {
            height = (unchained.size() + unchainedWidth - 1) / unchainedWidth;
        }

        // Fill in the unchained group
        int index = 0;
        Collections.sort(unchained, comparator);
        for (ClassSkill skill : unchained) {
            int x = index % unchainedWidth;
            int y = index / unchainedWidth;
            index++;
            skillSlots.put(x + y * 9, skill);
        }

        // Fill in the chained group
        HashMap<ClassSkill, Integer> tier = new HashMap<ClassSkill, Integer>();
        HashMap<ClassSkill, Integer> prevTier = new HashMap<ClassSkill, Integer>();
        int row = 0;
        index = 0;

        do {
            // Get the next tier of skills
            tier.clear();
            for (ClassSkill skill : chained) {
                boolean hasSkillReq = skill.getSkillReq() != null && api.hasSkill(skill.getSkillReq());
                if ((!hasSkillReq && prevTier.size() == 0)) {
                    tier.put(skill, index++);
                }
                else if (hasSkillReq && prevTier.containsKey(api.getSkill(skill.getSkillReq()))) {
                    tier.put(skill, prevTier.get(api.getSkill(skill.getSkillReq())));
                }
            }

            // Fill in the tier
            int filled = 0;
            for (int i = 0; i < index; i++) {
                for (Map.Entry<ClassSkill, Integer> entry : tier.entrySet()) {
                    if (entry.getValue() == i) {
                        int x = filled % chainedWidth + unchainedWidth + 1;
                        int y = filled / chainedWidth + row;
                        filled++;
                        skillSlots.put(x + y * 9, entry.getKey());
                    }
                }
            }

            // Move the current tier to the previous tier
            prevTier.clear();
            for (Map.Entry<ClassSkill, Integer> entry : tier.entrySet()) {
                prevTier.put(entry.getKey(), entry.getValue());
            }

            // Increment the row
            row += (tier.size() + chainedWidth - 1) / chainedWidth;
        }
        while (tier.size() > 0);

        if (row + 1 > height) height = row + 1;
    }

    /**
     * Checks whether or not the skill is attached to a chain
     *
     * @param skills skill list to check in
     * @param skill  skill to check for
     * @return       true if attached, false otherwise
     */
    private boolean isChained(List<ClassSkill> skills, ClassSkill skill) {
        if (skill.getSkillReq() != null) return true;
        for (ClassSkill s : skills) {
            if (s.getSkillReq() != null && s.getSkillReq().equalsIgnoreCase(skill.getName())) {
                return true;
            }
        }
        return false;
    }
}
