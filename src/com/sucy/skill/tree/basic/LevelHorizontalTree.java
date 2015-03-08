package com.sucy.skill.tree.basic;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.classes.RPGClass;

/**
 * A horizontal level tree implementation
 */
public class LevelHorizontalTree extends LevelTree
{

    /**
     * Constructor
     *
     * @param api  api reference
     * @param tree tree reference
     */
    public LevelHorizontalTree(SkillAPI api, RPGClass tree)
    {
        super(api, tree);
    }

    /**
     * @return skills allowed per row
     */
    @Override
    protected int getPerTierLimit()
    {
        return 6;
    }
}
