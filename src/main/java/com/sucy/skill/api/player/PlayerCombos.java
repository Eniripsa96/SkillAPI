/**
 * SkillAPI
 * com.sucy.skill.api.player.PlayerCombos
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
package com.sucy.skill.api.player;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.event.PlayerComboFinishEvent;
import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.data.Click;
import com.sucy.skill.log.Logger;
import com.sucy.skill.manager.ComboManager;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Represents the click combos available for a player to use along
 * with their current click pattern
 */
public class PlayerCombos
{
    private HashMap<Integer, String> skills  = new HashMap<Integer, String>();
    private HashMap<String, Integer> reverse = new HashMap<String, Integer>();

    private PlayerData player;
    private Click[]    clicks;
    private int        clickIndex;
    private long       clickTime;

    /**
     * Initializes a new empty combo set
     *
     * @param data owning player's data
     */
    public PlayerCombos(PlayerData data)
    {
        this.player = data;
        this.clickIndex = 0;
        this.clicks = new Click[SkillAPI.getComboManager().getComboSize()];
        this.clickTime = 0;
    }

    /**
     * Retrieves the number of clicks in the player's active combo. Once
     * this reaches the size of a combo, the combo will activate and
     * try to cast a skill.
     *
     * @return current number of clicks in the active combo
     */
    public int getComboCount()
    {
        return clickIndex;
    }

    /**
     * Retrieves the map of combo IDs to skills.
     *
     * @return map of combo IDs to skills
     */
    public HashMap<Integer, String> getSkillMap()
    {
        return skills;
    }

    /**
     * Retrieves the data of the owning player
     *
     * @return owning player's data
     */
    public PlayerData getPlayerData()
    {
        return player;
    }

    /**
     * Retrieves the name of the skill bound to the combo ID. If
     * no skill is bound to the ID, this will instead return null.
     *
     * @param id combo ID to get the bound skill for
     *
     * @return skill name bound to the ID or null if none
     */
    public String getSkillName(int id)
    {
        return skills.get(id);
    }


    /**
     * Clears the player's current click combo, causing them
     * to not count their recent clicks towards a combo
     */
    public void clearCombo()
    {
        clickIndex = 0;
    }

    /**
     * Applies a click for the player, counting towards their current combo
     * and casts the skill if the combo is completed.
     *
     * @param click click to apply for the player
     */
    public void applyClick(Click click)
    {
        // Don't count disabled clicks
        if (!SkillAPI.getComboManager().isClickEnabled(click.getId())) return;

        checkExpired();

        // Add the click to the current combo
        clicks[clickIndex++] = click;
        clickTime = System.currentTimeMillis();

        // Cast skill when combo is completed
        int id = SkillAPI.getComboManager().convertCombo(clicks, clickIndex);
        if (clickIndex == clicks.length || skills.containsKey(id))
        {
            PlayerComboFinishEvent event = new PlayerComboFinishEvent(player, id, skills.get(id));
            Bukkit.getPluginManager().callEvent(event);

            if (skills.containsKey(id) && !event.isCancelled())
            {
                player.cast(skills.get(id));
            }
        }
    }

    /**
     * Checks for when the combo times out
     */
    private void checkExpired()
    {
        // Reset combo if too much time passed
        if (clickIndex == clicks.length
            || System.currentTimeMillis() - clickTime > SkillAPI.getSettings().getClickTime())
        {
            clearCombo();
        }
    }

    /**
     * Retrieves the current combo string for the player
     *
     * @return current combo string
     */
    public String getCurrentComboString()
    {
        if (clickIndex == 0) return "";
        else if (clickIndex == clicks.length)
        {
            int id = SkillAPI.getComboManager().convertCombo(clicks);
            if (skills.containsKey(id))
            {
                return skills.get(id);
            }
            else return "";
        }

        checkExpired();

        ArrayList<Click> active = new ArrayList<Click>(clickIndex);
        for (int i = 0; i < clickIndex; i++)
        {
            active.add(clicks[i]);
        }
        return SkillAPI.getComboManager().getComboString(active);
    }

    /**
     * Checks whether or not there would be a conflict with the given combo ID
     * if it were to be added
     *
     * @param id combo ID
     *
     * @return true if conflict, false otherwise
     */
    public boolean hasConflict(int id)
    {
        return getConflicts(id).size() > 0;
    }

    /**
     * Checks whether or not there would be a conflict with the given combo ID
     * if it were to be added
     *
     * @param id combo ID
     *
     * @return ID of conflict or -1 if no conflict
     */
    public List<Integer> getConflicts(int id)
    {
        ComboManager cm = SkillAPI.getComboManager();
        List<Integer> conflicts = new ArrayList<Integer>();
        for (int taken : skills.keySet())
        {
            if (cm.conflicts(id, taken))
                conflicts.add(taken);
        }
        return conflicts;
    }

    /**
     * Adds a skill to the available combos. This will not
     * do anything if the skill is already added.
     *
     * @param skill skill to add
     */
    public void addSkill(Skill skill)
    {
        if (skill == null || !skill.canCast()) return;

        // Can't already be added
        if (skill.hasCombo())
        {
            setSkill(skill, skill.getCombo());
            return;
        }

        // Get next available combo
        ComboManager cm = SkillAPI.getComboManager();
        int combo = 1 << (Click.BITS * (cm.getComboSize() - 1));
        int max = (1 << (Click.BITS * cm.getComboSize())) - 1;
        while (combo <= max && (!cm.isValidDefaultCombo(combo) || hasConflict(combo)))
            combo++;

        // Add it if valid
        if (combo <= max)
        {
            skills.put(combo, skill.getName().toLowerCase());
            reverse.put(skill.getName(), combo);
        }
        else Logger.invalid("Failed to assign combo for " + skill.getName() + " - no remaining combos");
    }

    /**
     * Removes a skill from the available combos
     *
     * @param skill skill to remove
     */
    public void removeSkill(Skill skill)
    {
        if (skill == null || !reverse.containsKey(skill.getName())) return;
        skills.remove(reverse.remove(skill.getName()));
    }

    /**
     * Checks if a combo is currently active with any skill
     * for the player.
     *
     * @param id ID of the combo
     *
     * @return true if active, false otherwise
     */
    public boolean isComboUsed(int id)
    {
        return skills.containsKey(id);
    }

    /**
     * Checks if the combo ID is a valid combo
     *
     * @param id ID of the combo
     *
     * @return true if valid, false otherwise
     */
    public boolean isValidCombo(int id)
    {
        return SkillAPI.getComboManager().isValidCombo(id);
    }

    /**
     * Checks whether or not the skill has a combo associated with it
     *
     * @param skill the skill to check
     *
     * @return true if has a combo, false otherwise
     */
    public boolean hasCombo(Skill skill)
    {
        return reverse.containsKey(skill.getName());
    }

    /**
     * Sets the combo for a skill, overriding any previous combo
     * for the skill. If the skill didn't have a combo before, this
     * will add it anyway. If the combo ID is already in use, it will
     * reassign the conflicting skill.
     *
     * @param skill skill to set the combo for
     * @param id    ID of the combo to use
     *
     * @return true if set successfully, false otherwise
     */
    public boolean setSkill(Skill skill, int id)
    {
        if (skill == null || !skill.canCast() || !isValidCombo(id)) return false;

        removeSkill(skill);
        List<Integer> conflicts = getConflicts(id);
        if (conflicts.size() > 0)
        {
            for (int conflict : conflicts)
            {
                if (conflict == id)
                {
                    Skill old = SkillAPI.getSkill(skills.get(conflict));
                    old.clearCombo();
                    addSkill(old);
                }
            }
            skills.put(id, skill.getName().toLowerCase());
            reverse.put(skill.getName(), id);
            for (int conflict : conflicts)
            {
                if (conflict != id)
                {
                    Skill old = SkillAPI.getSkill(skills.get(conflict));
                    old.clearCombo();
                    addSkill(old);
                    reverse.remove(skills.remove(conflict));
                }
            }
        }
        else
        {
            skills.put(id, skill.getName().toLowerCase());
            reverse.put(skill.getName(), id);
        }
        return true;
    }

    /**
     * Retrieves the combo string for the skill
     * according to the player's personal settings.
     *
     * @param skill skill to get the string for
     *
     * @return combo string
     */
    public String getComboString(Skill skill)
    {
        int combo = reverse.get(skill.getName());
        return SkillAPI.getComboManager().getComboString(combo);
    }
}
