package com.sucy.skill.api.classes;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.tree.SkillTree;

/**
 * Interface class for extensions of the available skill
 * trees. Simply implement this to provide the SkillTree
 * implementation and then set a class's tree type.
 */
public interface TreeType
{
    /**
     * Retrieves a new instance of a skill tree using
     * the given type.
     *
     * @param api    - SkillAPI reference
     * @param parent - Parent class to organize
     *
     * @return skill tree instance
     */
    public SkillTree getTree(SkillAPI api, RPGClass parent);
}
