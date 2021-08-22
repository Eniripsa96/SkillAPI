/**
 * SkillAPI
 * com.sucy.skill.listener.BindListener
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
package com.sucy.skill.listener;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.event.KeyPressEvent;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.player.PlayerSkill;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

/**
 * A listener that handles casting skills through binds. This shouldn't be
 * use by other plugins as it is handled by the API.
 */
public class BindListener extends SkillAPIListener {
    /**
     * Handles interact events to check when a player right clicks with
     * a bound item to cast a skill.
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(KeyPressEvent event) {
        Player player = event.getPlayer();
        if (!SkillAPI.getSettings().isWorldEnabled(player.getWorld())) {
            return;
        }

        PlayerData data = SkillAPI.getPlayerData(player);
        Material heldItem = player.getItemInHand().getType();

        // Must be right clicking with an item
        if (event.getKey() != KeyPressEvent.Key.RIGHT || heldItem == null) {
            return;
        }

        // Must have a valid item
        final PlayerSkill skill = data.getBoundSkill(heldItem);
        if (skill == null || !SkillAPI.isSkillRegistered(skill.getData().getName())) {
            return;
        }

        // Cast the skill
        data.cast(skill);
    }
}
