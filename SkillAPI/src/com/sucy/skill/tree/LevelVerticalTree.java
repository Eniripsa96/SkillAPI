package com.sucy.skill.tree;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.CustomClass;

/**
 * A horizontal level tree implementation
 */
public class LevelVerticalTree extends LevelTree {

    /**
     * Constructor
     *
     * @param api  api reference
     * @param tree tree reference
     */
    public LevelVerticalTree(SkillAPI api, CustomClass tree) {
        super(api, tree);
    }

    /**
     * @return skills allowed per row
     */
    @Override
    protected int getPerTierLimit() {
        return 9;
    }
}
