package com.sucy.skill.task;

import com.sucy.skill.SkillAPI;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Handles auto saving periodically
 */
public class SaveTask extends BukkitRunnable
{
    /**
     * Sets up the save task. This shouldn't be used by other plugins
     * as it is set up by the API.
     *
     * @param api API reference
     */
    public SaveTask(SkillAPI api)
    {
        runTaskTimer(api, SkillAPI.getSettings().getSaveFreq(), SkillAPI.getSettings().getSaveFreq());
    }

    /**
     * Saves all player data
     */
    @Override
    public void run()
    {
        SkillAPI.saveData();
    }
}
