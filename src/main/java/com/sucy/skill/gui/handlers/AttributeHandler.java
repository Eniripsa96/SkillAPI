/**
 * SkillAPI
 * com.sucy.skill.gui.handlers.AttributeHandler
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
import com.sucy.skill.gui.tool.GUIHolder;
import com.sucy.skill.manager.AttributeManager;

import java.util.HashMap;

public class AttributeHandler extends GUIHolder<AttributeManager.Attribute> {
    private HashMap<String, Integer> start = new HashMap<String, Integer>();

    @Override
    protected void onSetup() {
        AttributeManager manager = SkillAPI.getAttributeManager();
        for (String key : manager.getKeys()) { start.put(key, player.getAttribute(key)); }
    }

    @Override
    public void onClick(AttributeManager.Attribute type, int slot, boolean left, boolean shift) {
        if (left) {
            if (player.upAttribute(type.getKey())) { setPage(page); }
        } else if (SkillAPI.getSettings().isAttributesDowngrade() || player.getAttribute(type.getKey()) > start.get(type.getKey())) {
            if (player.refundAttribute(type.getKey())) {
                setPage(page);
            }
        }
    }
}
