package com.sucy.skill.tree;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.classes.RPGClass;
import com.sucy.skill.api.exception.SkillTreeException;
import com.sucy.skill.api.skills.Skill;

import java.util.*;

/**
 * <p>Root class for tree implementations based on levels</p>
 * <p>This is still in development to make it work as intended</p>
 *
 */
public abstract class LevelTree extends SkillTree {

    /**
     * Constructor
     *
     * @param api  api reference
     * @param tree class reference
     */
    public LevelTree(SkillAPI api, RPGClass tree) {
        super(api, tree);
    }

    /**
     * Arranges the skill tree
     *
     * @param skills skills to arrange
     * @throws SkillTreeException
     */
    @Override
    protected void arrange(List<Skill> skills) throws SkillTreeException {

        // Get the max level
        int maxLevel = 1;
        for (Skill skill : skills) {
            if (skill.getLevelReq(0) > maxLevel) {
                maxLevel = skill.getLevelReq(0);
            }
        }

        // Break it up into tiers
        int scale = (maxLevel + getTierLimit() - 1) / getTierLimit();
        Collections.sort(skills, levelComparator);
        HashMap<Integer, List<Skill>> tiers = new HashMap<Integer, List<Skill>>();
        int tier = 0;
        while (skills.size() > 0) {
            List<Skill> list = new ArrayList<Skill>();
            tiers.put(tier++, list);
            int max = tier * scale;
            int count = 0;

            while (skills.size() > 0 && count++ < getPerTierLimit() && skills.get(0).getLevelReq(0) <= max) {
                list.add(skills.remove(0));
            }
        }

        // Arrange the tree
        for (int i = 0; i < tier; i++) {
            List<Skill> list = tiers.get(i);
            int maxIndex = 0;

            for (int j = 0; j < getPerTierLimit(); j++) {
                for (int k = 0; k < i; k++) {
                    List<Skill> prevList = tiers.get(k);
                    if (prevList.size() <= j) continue;
                    Skill prevSkill = prevList.get(j);
                    for (int l = 0; l < list.size(); l++) {
                        Skill nextSkill = list.get(l);
                        if (nextSkill.getSkillReq() != null
                                && nextSkill.getSkillReq().equalsIgnoreCase(prevSkill.getName())) {
                            list.remove(l);
                            int index = Math.min(Math.max(maxIndex, j), list.size());
                            maxIndex = Math.max(maxIndex + 1, j);
                            list.add(index, nextSkill);
                        }
                    }
                }
            }
            for (int j = 0; j < list.size(); j++) {
                int index;
                if (getPerTierLimit() == 9) index = j + i * 9;
                else index = j * 9 + i;
                skillSlots.put(index, list.get(j));
                if (index / 9 + 1 > height) height = index / 9 + 1;
            }
        }
        if (height == 0) height = 1;
    }

    /**
     * Maximum number of skills per tier allowed
     *
     * @return number of skills per tier
     */
    protected abstract int getPerTierLimit();

    /**
     * @return maximum number of tiers allowed
     */
    protected int getTierLimit() {
        return 15 - getPerTierLimit();
    }

    /**
     * Comparator for skills for level trees
     */
    private static final Comparator<Skill> levelComparator = new Comparator<Skill>() {

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
        public int compare(Skill skill1, Skill skill2) {
            return skill1.getLevelReq(0) > skill2.getLevelReq(0) ? 1
                    : skill1.getLevelReq(0) < skill2.getLevelReq(0) ? -1
                    : skill1.getCost(0) > skill2.getCost(0) ? 1
                    : skill1.getCost(0) < skill2.getCost(0) ? -1
                    : skill1.getName().compareTo(skill2.getName());
        }
    };
}
