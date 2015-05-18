package com.sucy.skill.api.player;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.data.Click;
import com.sucy.skill.manager.ComboManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Represents the click combos available for a player to use along
 * with their current click pattern
 */
public class PlayerCombos
{
    private HashMap<String, Integer> combos = new HashMap<String, Integer>();
    private HashMap<Integer, String> skills = new HashMap<Integer, String>();

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
     * Retrieves the data of the owning player
     *
     * @return owning player's data
     */
    public PlayerData getPlayerData()
    {
        return player;
    }

    /**
     * Retrieves the combo data for the player
     *
     * @return player's combo data
     */
    public HashMap<String, Integer> getComboData()
    {
        return combos;
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
        if (clickIndex == clicks.length)
        {
            clearCombo();
            int id = SkillAPI.getComboManager().convertCombo(clicks);
            if (skills.containsKey(id))
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
        long time = System.currentTimeMillis();
        if (time - clickTime > SkillAPI.getSettings().getClickTime())
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

        checkExpired();

        ArrayList<Click> active = new ArrayList<Click>(clickIndex);
        for (int i = 0; i < clickIndex; i++)
        {
            active.add(clicks[i]);
        }
        return getComboString(active);
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
        String key = skill.getName().toLowerCase();

        // Can't already be added
        if (combos.containsKey(key))
        {
            return;
        }

        // Get next available combo
        ComboManager cm = SkillAPI.getComboManager();
        int combo;
        int max = (1 << (Click.BITS * cm.getComboSize())) - 1;
        for (combo = 0; combo <= max && (skills.containsKey(combo) || !cm.isValidCombo(combo)); combo++) ;

        // Add it if valid
        if (combo <= max)
        {
            combos.put(skill.getName().toLowerCase(), combo);
            skills.put(combo, skill.getName().toLowerCase());
        }
    }

    /**
     * Removes a skill from the available combos
     *
     * @param skill skill to remove
     */
    public void removeSkill(Skill skill)
    {
        if (skill == null) return;
        if (combos.containsKey(skill.getName().toLowerCase()))
        {
            skills.remove(combos.remove(skill.getName().toLowerCase()));
        }
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
        if (skills.containsKey(id))
        {
            Skill old = SkillAPI.getSkill(skills.remove(id));
            combos.remove(old.getName().toLowerCase());
            skills.put(id, skill.getName().toLowerCase());
            combos.put(skill.getName().toLowerCase(), id);
            addSkill(old);
        }
        else
        {
            skills.put(id, skill.getName().toLowerCase());
            combos.put(skill.getName().toLowerCase(), id);
        }
        return true;
    }

    /**
     * Retrieves the legible string representing the combo. If
     * the skill is not added to the combo data, this will
     * instead return an empty string
     *
     * @param skill skill to get the string for
     *
     * @return string representation fo the skill's combo
     */
    public String getComboString(Skill skill)
    {
        if (skill == null) return "";
        String key = skill.getName().toLowerCase();
        if (!combos.containsKey(key)) return "";

        List<Click> clicks = SkillAPI.getComboManager().convertId(combos.get(key));
        return getComboString(clicks);
    }

    private String getComboString(List<Click> clicks)
    {
        String result = "";
        for (Click click : clicks)
        {
            if (result.length() > 0) result += ", ";
            result += click.getName();
        }
        return result;
    }
}
