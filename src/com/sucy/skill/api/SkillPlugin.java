/**
 * SkillAPI
 * com.sucy.skill.api.SkillPlugin
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
package com.sucy.skill.api;

import com.google.common.collect.ImmutableList;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.dynamic.custom.CustomEffectComponent;
import com.sucy.skill.dynamic.trigger.Trigger;

import java.util.List;

/**
 * <p>Interface for plugins that define new classes and skills</p>
 * <p>Make sure to only add the appropriate type in each method
 * (e.g. adding classes in the registerClasses method and skills
 * in the registerSkills method). It keeps the API working nicely!</p>
 */
public interface SkillPlugin
{
    /**
     * <p>Method to add new skills to the game</p>
     * <p>Use api.addSkills(Skill ... skills) to add them</p>
     * <p>This is called before registerClasses so if you want to keep
     * a reference of the API, you can store the api reference into one
     * of your own fields</p>
     *
     * @param api the api reference
     */
    void registerSkills(SkillAPI api);

    /**
     * <p>Method to add new classes to the game</p>
     * <p>Use api.addClasses(RPGClass ... classes) to add them</p>
     * <p>This is called after registerSkills</p>
     */
    void registerClasses(SkillAPI api);

    default List<Trigger> getTriggers() {
        return ImmutableList.of();
    }

    default List<CustomEffectComponent> getComponents() {
        return ImmutableList.of();
    }
}
