package com.sucy.skill.manager;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.data.Click;

import java.util.*;

/**
 * Manages click combos with what combos are active and
 * what skills result from various combinations
 */
public class ComboManager
{
    private HashMap<String, Integer> combos = new HashMap<String, Integer>();
    private HashMap<Integer, String> skills = new HashMap<Integer, String>();

    private SkillAPI api;
    private int      comboSize;
    private Click[]  buffer;
    private boolean[] clicks;
    private int[] next;
    private boolean enabled;

    /**
     * Initializes the combo manager, grabbing settings from
     * the configuration to prepare data
     */
    public void initialize()
    {
        comboSize = Math.min(SkillAPI.getSettings().getComboSize(), Click.MAX_COMBO_SIZE);
        clicks = new boolean[] {
                SkillAPI.getSettings().isComboLeft(),
                SkillAPI.getSettings().isComboRight(),
                SkillAPI.getSettings().isComboShift()
        };
        next = new int[comboSize];
        buffer = new Click[comboSize];
        enabled = comboSize > 0 && (clicks[0] || clicks[1] || clicks[2]) && SkillAPI.getSettings().isCombosEnabled();
    }

    /**
     * Sets a specific combo for a skill if the combo is not already taken.
     * If it is already taken or the ID is of a combo that uses a disabled
     * click type, this will return false.
     *
     * @param skill skill to set the combo for
     * @param id    combo ID
     *
     * @return true if successfully set, false otherwise
     */
    public boolean setCombo(Skill skill, int id)
    {
        if (clicks[id])
        if (!skills.containsKey(id))
        {
            skills.put(id, skill.getName());
            combos.put(skill.getName(), id);
            return true;
        }
        return false;
    }

    /**
     * Retrieves the name of a skill from the given clicks
     *
     * @param clicks clicks in the combo
     * @return name of the skill resulted from the combo or null if not a valid combo
     */
    public String getSkillName(Click... clicks)
    {
        if (clicks.length != comboSize)
        {
            throw new IllegalArgumentException("Invalid combo - doesn't match desired combo size");
        }
        return skills.get(convertCombo(clicks));
    }

    /**
     * Retrieves the name of a skill from a list of clicks
     *
     * @param clicks list of clicks in the combo
     * @return name of the skill resulted from the combo or null if not a valid combo
     */
    public String getSkillName(Collection<Click> clicks)
    {
        return getSkillName(clicks.toArray(buffer));
    }

    /**
     * Gets the integer ID of a click combo via skill name
     *
     * @param name skill name
     * @return combo ID attached to the skill
     */
    public int getComboId(String name)
    {
        Integer id = combos.get(name);
        if (id == null)
        {
            throw new IllegalArgumentException(name + " is not a skill!");
        }

        return id;
    }

    /**
     * Gets a click combination from a skill name
     *
     * @param name skill name
     * @return click combination
     */
    public List<Click> getCombo(String name)
    {
        return convertId(getComboId(name));
    }

    /**
     * Gets a text representation of a combo by skill name
     *
     * @param name skill name
     * @return text combo representation
     */
    public String getComboString(String name)
    {
        List<Click> clicks = getCombo(name);
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (Click click : clicks)
        {
            sb.append(click.getName());
            if (i++ < comboSize - 1)
            {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    /**
     * Converts a combo ID to clicks
     *
     * @param id combo ID
     * @return click combination
     */
    public List<Click> convertId(int id)
    {
        List<Click> clicks = new ArrayList<Click>(comboSize);
        for (int i = 0; i < comboSize; i++)
        {
            clicks.add(Click.getById(id & Click.BIT_MASK));
            id >>= Click.BITS;
        }
        Collections.reverse(clicks);
        return clicks;
    }

    /**
     * Converts a click combination to an ID
     *
     * @param clicks clicks to convert
     * @return combo ID
     */
    public int convertCombo(Click[] clicks)
    {
        int id = 0;
        for (Click click : clicks)
        {
            id <<= Click.BITS;
            id |= click.getId();
        }
        return id;
    }

    /**
     * Converts a click combination to an ID
     *
     * @param clicks clicks to convert
     * @return combo ID
     */
    public int convertCombo(Collection<Click> clicks)
    {
        return convertCombo(clicks.toArray(buffer));
    }
}
