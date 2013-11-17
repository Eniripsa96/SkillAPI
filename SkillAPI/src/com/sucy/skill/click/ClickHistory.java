package com.sucy.skill.click;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.CustomClass;
import com.sucy.skill.api.PlayerSkills;
import com.sucy.skill.api.Status;
import com.sucy.skill.api.skill.ClassSkill;
import com.sucy.skill.api.skill.SkillAttribute;
import com.sucy.skill.api.skill.SkillShot;
import com.sucy.skill.api.skill.SkillStatus;
import com.sucy.skill.language.OtherNodes;
import com.sucy.skill.language.StatusNodes;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Click history for a single player
 */
public class ClickHistory {

    private static final long limit = 1000;

    private final ArrayList<MouseClick> entries = new ArrayList<MouseClick>();
    private final SkillAPI api;
    private final String player;

    private long timer;

    /**
     * Constructor
     *
     * @param api api reference
     */
    public ClickHistory(SkillAPI api, Player player) {
        this.api = api;
        this.player = player.getName();
    }

    /**
     * Adds a click to the history
     *
     * @param click click to add
     */
    public void addClick(MouseClick click) {
        entries.add(click);
        updateEntries();
        if (entries.size() == 0) entries.add(click);
    }

    /**
     * Updates the click entries, casting a skill or clearing them as applicable
     */
    private void updateEntries() {

        // Clear the entries if time expired
        if (entries.size() > 0 && System.currentTimeMillis() - timer > limit) {
            entries.clear();
        }

        // Search for a skill after 4 clicks
        else if (entries.size() == 4) {

            PlayerSkills player = api.getPlayer(this.player);
            if (!player.hasClass()) return;

            CustomClass c = api.getClass(player.getClassName());
            ClassSkill skill = c.getSkill(entries.get(0), entries.get(1), entries.get(2), entries.get(3));
            entries.clear();

            // Cast the skill if one is found
            if (skill != null) {

                // Doesn't have the skill
                if (!player.hasSkillUnlocked(skill.getName())) {
                    return;
                }

                player.castSkill(skill.getName());
            }
        }

        // Update the timer
        timer = System.currentTimeMillis();
    }
}
