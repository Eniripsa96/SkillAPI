/**
 * SkillAPI
 * com.sucy.skill.hook.NoCheatHook
 * <p/>
 * The MIT License (MIT)
 * <p/>
 * Copyright (c) 2014 Steven Sucy
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.sucy.skill.hook;

import fr.neatmonster.nocheatplus.hooks.NCPExemptionManager;
import org.bukkit.entity.Player;

/**
 * Handles plugin calls to NoCheatPlus to get around it blocking skill effects
 */
public class NoCheatHook {
    /**
     * Exempts the player from NCP functionality
     *
     * @param player player to exempt
     */
    public static void exempt(Player player) {
        NCPExemptionManager.exemptPermanently(player);
    }

    /**
     * Unexempts the player from NCP functionality
     *
     * @param player player to unexempt
     */
    public static void unexempt(Player player) {
        NCPExemptionManager.unexempt(player);
    }
}
