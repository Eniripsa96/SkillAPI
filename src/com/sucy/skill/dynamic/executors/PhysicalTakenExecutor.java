/**
 * SkillAPI
 * com.sucy.skill.dynamic.executors.PhysicalTakenExecutor
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Steven Sucy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
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
package com.sucy.skill.dynamic.executors;

import com.sucy.skill.api.event.PhysicalDamageEvent;
import com.sucy.skill.dynamic.DynamicSkill;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;

/**
 * Handles executing events for dynamic skills
 */
public class PhysicalTakenExecutor implements EventExecutor
{
    public static final PhysicalTakenExecutor instance = new PhysicalTakenExecutor();

    /**
     * Executes the event for the skill
     *
     * @param listener the dynamic skill
     * @param event    event being launched
     */
    @Override
    public void execute(Listener listener, Event event)
    {
        ((DynamicSkill)listener).onPhysical((PhysicalDamageEvent) event);
    }
}