/**
 * SkillAPI
 * com.sucy.skill.gui.handlers.SkillHandler
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2016 Steven Sucy
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.sucy.skill.gui.handlers;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.gui.tool.GUIHolder;

import java.util.HashMap;

public class SkillHandler extends GUIHolder<Skill> {
    private HashMap<String, Integer> start = new HashMap<String, Integer>();

    @Override
    public void onSetup() {
        for (String key : data.keySet()) { start.put(key, player.getSkillLevel(key)); }
    }

    @Override
    public void onClick(Skill type, int slot, boolean left, boolean shift) {
        if (left) {
            if (player.upgradeSkill(type)) { setPage(page); }
        } else if ((SkillAPI.getSettings()
                .isAllowDowngrade() || player.getSkillLevel(type.getKey()) > start.get(type.getKey()))
                && player.downgradeSkill(type)) { setPage(page); }
    }

    @Override
    public void onHotBar(Skill type, int from, int to) {
        if (player.getSkillBar().isSetup() && type.canCast() && player.hasSkill(type.getName())) {
            player.getSkillBar().assign(player.getSkill(type.getName()), to);
        }
    }
}
