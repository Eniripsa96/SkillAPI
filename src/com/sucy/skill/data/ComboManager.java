package com.sucy.skill.data;

import com.sucy.skill.SkillAPI;

import java.util.*;

public class ComboManager
{

    private HashMap<String, Integer> combos = new HashMap<String, Integer>();
    private HashMap<Integer, String> skills = new HashMap<Integer, String>();

    private SkillAPI api;
    private int      comboSize;
    private Click[]  buffer;

    public String getSkillName(Click... clicks)
    {
        if (clicks.length != comboSize)
        {
            throw new IllegalArgumentException("Invalid combo - doesn't match desired combo size");
        }
        return skills.get(convertCombo(clicks));
    }

    public String getSkillName(Collection<Click> clicks)
    {
        return getSkillName(clicks.toArray(buffer));
    }

    public int getComboId(String name)
    {
        Integer id = combos.get(name);
        if (id == null)
        {
            throw new IllegalArgumentException(name + " is not a skill!");
        }

        return id;
    }

    public List<Click> getCombo(String name)
    {
        return convertId(getComboId(name));
    }

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

    public int convertCombo(Collection<Click> clicks)
    {
        return convertCombo(clicks.toArray(buffer));
    }
}
