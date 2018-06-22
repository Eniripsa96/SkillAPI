/**
 * SkillAPI
 * com.sucy.skill.task.GUITask
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
package com.sucy.skill.task;

import com.rit.sucy.text.TextFormatter;
import com.rit.sucy.version.VersionManager;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.util.ActionBar;
import com.sucy.skill.dynamic.DynamicSkill;
import com.sucy.skill.log.LogType;
import com.sucy.skill.log.Logger;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Task that handles updating GUI elements such as level bar,
 * food bar, and action bar according to the config.yml content.
 */
public class GUITask extends BukkitRunnable
{
    private final boolean levelMana;
    private final boolean levelLevel;

    private final boolean foodMana;
    private final boolean foodExp;

    private final boolean forceScaling;
    private final boolean oldHealth;

    private final boolean useAction;
    private final String  actionText;

    private boolean isRunning = false;

    /**
     * Sets up the task, running if any of the GUI options are enabled
     *
     * @param api API reference
     */
    public GUITask(SkillAPI api)
    {
        String levelBar = SkillAPI.getSettings().getLevelBar().toLowerCase();
        levelMana = levelBar.equals("mana");
        levelLevel = levelBar.equals("level");

        String foodBar = SkillAPI.getSettings().getFoodBar().toLowerCase();
        foodMana = foodBar.equals("mana");
        foodExp = foodBar.equals("exp");

        forceScaling = SkillAPI.getSettings().isForceScaling();
        oldHealth = SkillAPI.getSettings().isOldHealth();

        useAction = SkillAPI.getSettings().isUseActionBar() && ActionBar.isSupported();
        actionText = TextFormatter.colorString(SkillAPI.getSettings().getActionText());

        Logger.log(LogType.GUI, 1, "GUI Settings: " + levelMana + "/" + levelLevel + "/" + foodMana + "/" + foodExp + "/" + useAction + "/" + actionText);

        if (useAction || levelMana || levelLevel || foodMana || foodExp || forceScaling)
        {
            runTaskTimer(api, 5, 5);
            isRunning = true;
        }
    }

    /**
     * Checks whether or not the task is running
     *
     * @return true if running, false otherwise
     */
    public boolean isRunning()
    {
        return isRunning;
    }

    /**
     * Runs the tasks, updating GUI elements for players
     */
    @Override
    public void run()
    {
        Logger.log(LogType.GUI, 1, "Updating GUI (" + VersionManager.getOnlinePlayers().length + " players)...");
        for (Player player : VersionManager.getOnlinePlayers())
        {
            if (!SkillAPI.getSettings().isWorldEnabled(player.getWorld())) continue;

            PlayerData data = SkillAPI.getPlayerData(player);

            // Health scale
            if (forceScaling)
            {
                if (oldHealth)
                    player.setHealthScale(20);
                else
                    player.setHealthScale(player.getMaxHealth());
            }

            // Level bar options
            if (levelMana)
            {
                Logger.log(LogType.GUI, 2, "Updating level bar with mana");
                if (data.getMaxMana() == 0) {
                    player.setLevel(0);
                    player.setExp(0);
                }
                else {
                    player.setLevel((int) data.getMana());
                    player.setExp(Math.min(0.999f, (float) (0.999 * data.getMana() / data.getMaxMana())));
                }
            }
            else if (levelLevel)
            {
                Logger.log(LogType.GUI, 2, "Updating level bar with class level/exp");
                if (!data.hasClass())
                {
                    player.setLevel(0);
                    player.setExp(0);
                }
                else
                {
                    PlayerClass main = data.getMainClass();
                    player.setLevel(main.getLevel());
                    player.setExp((float) main.getExp() / main.getRequiredExp());
                }
            }

            // Food bar options
            if (foodMana)
            {
                Logger.log(LogType.GUI, 2, "Updating food bar with mana");
                player.setSaturation(20);
                if (data.getMaxMana() == 0) {
                    player.setFoodLevel(20);;
                }
                else {
                    player.setFoodLevel((int) Math.ceil(20 * data.getMana() / data.getMaxMana()));
                }
            }
            else if (foodExp)
            {
                Logger.log(LogType.GUI, 2, "Updating food bar with class level/exp");
                player.setSaturation(20);
                if (!data.hasClass())
                {
                    player.setFoodLevel(0);
                }
                else
                {
                    PlayerClass main = data.getMainClass();
                    player.setFoodLevel((int) Math.floor(20 * main.getExp() / main.getRequiredExp()));
                }
            }

            // Action bar options
            if (useAction && data.hasClass())
            {
                Logger.log(LogType.GUI, 2, "Updating action bar (Working=" + ActionBar.isSupported() + ")");
                PlayerClass main = data.getMainClass();
                String filtered = actionText
                    .replace("{combo}", data.getComboData().getCurrentComboString())
                    .replace("{class}", main.getData().getPrefix())
                    .replace("{level}", "" + main.getLevel())
                    .replace("{exp}", "" + (int) main.getExp())
                    .replace("{expReq}", "" + main.getRequiredExp())
                    .replace("{expLeft}", "" + (int) Math.ceil(main.getRequiredExp() - main.getExp()))
                    .replace("{mana}", "" + (int) data.getMana())
                    .replace("{maxMana}", "" + (int) data.getMaxMana())
                    .replace("{name}", player.getName())
                    .replace("{health}", "" + (int) player.getHealth())
                    .replace("{maxHealth}", "" + (int) player.getMaxHealth())
                    .replace("{attr}", "" + data.getAttributePoints())
                    .replace("{sp}", "" + main.getPoints());
                while (filtered.contains("{value:"))
                {
                    int index = filtered.indexOf("{value:");
                    int end = filtered.indexOf('}', index);
                    String key = filtered.substring(index + 7, end);
                    Object value = DynamicSkill.getCastData(player).get(key);
                    filtered = filtered.replace("{value:" + key + "}", (value == null ? "None" : value.toString()));
                }
                ActionBar.show(player, filtered);
            }
        }
    }
}
