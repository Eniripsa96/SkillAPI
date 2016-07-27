/**
 * SkillAPI
 * com.sucy.skill.manager.TitleManager
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
package com.sucy.skill.manager;

import be.maximvdw.titlemotd.ui.Title;
import com.rit.sucy.config.CustomFilter;
import com.rit.sucy.config.FilterType;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.data.Settings;
import com.sucy.skill.data.TitleType;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Handles accessing the Title display resource
 */
public class TitleManager {
    private static Title title;

    /**
     * Initializes the title object if not done so already
     */
    private static void init() {
        if (title == null) {
            Settings settings = SkillAPI.getSettings();
            title = new Title(
                    "",
                    "",
                    settings.getTitleFadeIn(),
                    settings.getTitleDuration(),
                    settings.getTitleFadeOut()
            );
            title.setTimingsToTicks();
        }
    }

    /**
     * Shows a message using the Title display
     *
     * @param player  player to send to
     * @param type    type of message
     * @param msgKey  language config key for the message
     * @param filters filters to apply to the message
     */
    public static void show(Player player, TitleType type, String msgKey, CustomFilter... filters) {
        if (SkillAPI.getSettings().useTitle(type) && msgKey != null) {
            List<String> message = SkillAPI.getLanguage().getMessage(msgKey, true, FilterType.COLOR, filters);
            if (message != null && message.size() > 0) {
                init();
                title.setTitle(message.get(0));
                if (message.size() > 1)
                    title.setSubtitle(message.get(1));
                else
                    title.setSubtitle("");

                title.send(player);
            }
        } else if (msgKey != null)
            SkillAPI.getLanguage().sendMessage(msgKey, player, FilterType.COLOR, filters);
    }
}
