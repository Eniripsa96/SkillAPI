/**
 * SkillAPI
 * com.sucy.skill.dynamic.Trigger
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
package com.sucy.skill.dynamic.trigger;

import com.sucy.skill.api.Settings;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;

import java.util.Map;

/**
 * Possible triggers for dynamic skill effects
 */
public interface Trigger<E extends Event> {

    /**
     * @return unique key for the trigger
     */
    String getKey();

    /**
     * @return class of the event related to the trigger
     */
    Class<E> getEvent();

    /**
     * @param event event details
     * @param level the level of the owning skill
     * @param settings skill settings
     * @return true if the skill should activate, false otherwise
     */
    boolean shouldTrigger(final E event, final int level, final Settings settings);

    /**
     * Reads data from the event and provides values to the caster's value data. This can be used within
     * skills for more flexible effects. An example of this in base triggers is the Launch trigger providing
     * the speed a projectile was launched so mechanics can replace it with equally-fast projectiles.
     *
     * @param event event details
     * @param data caster's value data to populate
     */
    void setValues(final E event, final Map<String, Object> data);

    /**
     * Fetches the caster as determined by the triggering event.
     *
     * @param event event details
     * @return the one to apply the trigger for
     */
    LivingEntity getCaster(final E event);

    /**
     * Fetches the target as determined by the triggering event. This can be the same as the caster.
     *
     * @param event event details
     * @param settings skill settings
     * @return the one being affected by the trigger (initial target)
     */
    LivingEntity getTarget(final E event, final Settings settings);
}
