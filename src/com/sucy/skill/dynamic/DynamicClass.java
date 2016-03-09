/**
 * SkillAPI
 * com.sucy.skill.dynamic.DynamicClass
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
package com.sucy.skill.dynamic;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.classes.RPGClass;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * <p>A basic implementation of RPGClass that loads from the dynamic
 * folder instead of the normal one. There's not really a difference
 * between normal classes, just this is used repeatedly and is added
 * based off configs versus coded ones which need to be manually
 * registered.</p>
 * <p>You should not use this class as it is meant for dynamic usage
 * and any other usage would only be an extra layer.</p>
 */
public class DynamicClass extends RPGClass
{
    /**
     * Constructs a new dynamic class
     *
     * @param api API reference
     * @param key key for the class
     */
    public DynamicClass(SkillAPI api, String key)
    {
        super(key, new ItemStack(Material.APPLE), 20);
    }
}
