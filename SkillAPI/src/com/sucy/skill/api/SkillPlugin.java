package com.sucy.skill.api;

import com.sucy.skill.SkillAPI;

/**
 * <p>Interface for plugins that define new classes and skills</p>
 * <p>Make sure to only add the appropriate type in each method
 * (e.g. adding classes in the registerClasses method and skills
 * in the registerSkills method). It keeps the API working nicely!</p>
 */
public interface SkillPlugin {

    /**
     * <p>Method to add new skills to the game</p>
     * <p>Use api.addSkills(ClassSkill ... skills) to add them</p>
     * <p>This is called before registerClasses so if you want to keep
     * a reference of the API, you can store the api reference into one
     * of your own fields</p>
     *
     * @param api the api reference
     */
    public void registerSkills(SkillAPI api);

    /**
     * <p>Method to add new classes to the game</p>
     * <p>Use api.addClasses(CustomClass ... classes) to add them</p>
     * <p>This is called after registerSkills</p>
     */
    public void registerClasses(SkillAPI api);
}
