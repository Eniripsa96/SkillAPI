/**
 * SkillAPI
 * com.sucy.skill.api.classes.TreeType
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
package com.sucy.skill.api.classes;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.tree.basic.InventoryTree;

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
    InventoryTree getTree(SkillAPI api, RPGClass parent);
}
