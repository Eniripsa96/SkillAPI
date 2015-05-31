package com.sucy.skill.task;

import com.rit.sucy.text.TextFormatter;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.util.ActionBar;
import com.sucy.skill.dynamic.DynamicSkill;
import org.bukkit.Bukkit;
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

        useAction = SkillAPI.getSettings().isUseActionBar() && ActionBar.isSupported();
        actionText = TextFormatter.colorString(SkillAPI.getSettings().getActionText());

        if (useAction || levelMana || levelLevel || foodMana || foodExp)
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
        for (Player player : Bukkit.getServer().getOnlinePlayers())
        {
            if (!SkillAPI.getSettings().isWorldEnabled(player.getWorld())) continue;

            PlayerData data = SkillAPI.getPlayerData(player);

            // Level bar options
            if (levelMana)
            {
                player.setLevel((int) data.getMana());
                player.setExp((float) (0.999 * data.getMana() / data.getMaxMana()));
            }
            else if (levelLevel)
            {
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
                player.setSaturation(20);
                player.setFoodLevel((int) Math.ceil(20 * data.getMana() / data.getMaxMana()));
            }
            else if (foodExp)
            {
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
                        .replace("{maxHealth}", "" + (int) player.getMaxHealth());
                while (filtered.contains("{value:")) {
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
