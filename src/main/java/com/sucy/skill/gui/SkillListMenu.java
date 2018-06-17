/**
 * SkillAPI
 * com.sucy.skill.gui.SkillListMenu
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
package com.sucy.skill.gui;

import com.rit.sucy.gui.*;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.player.PlayerSkill;
import org.bukkit.entity.Player;

import java.util.ArrayList;


public class SkillListMenu extends MapMenu
{
    // Player data keys
    private static final String SELECTION = "sapi_skill";
    private static final String AVAILABLE = "sapi_available";

    // Scene keys
    private static final String PLATE    = "plate";
    private static final String NAME     = "name";
    private static final String SELECTOR = "selector";
    private static final String TITLE    = "title";

    /**
     * Gets the selected skill of a player
     *
     * @param player player to get the selection of
     *
     * @return player's selection or null if hasn't selected one
     */
    public static PlayerSkill getSkill(Player player)
    {
        Object skill = getData(player, SELECTION);
        ArrayList<PlayerSkill> list = getSkills(player);

        if (skill == null || list == null) return null;
        return list.get((Integer) skill);
    }

    private SkillAPI api;

    public SkillListMenu(SkillAPI api)
    {
        this.api = api;
    }

    /**
     * Moves a player's skill selection by making
     * sure they are at the correct screen and that
     * there is room to move the selection in the
     * given direction.
     *
     * @param player player to move the selection for
     * @param i      amount to move the selection by
     */
    private void move(Player player, int i)
    {
        int id = getId(player);
        int scroll = getScroll(player);
        int skills = getSkills(player).size();

        id += i;
        if (id < 0)
        {
            id = 0;
            scroll = 0;
        }
        if (id >= skills)
        {
            id = skills - 1;
            scroll = id - 3;
        }

        while (id - scroll <= 0 && id > 0)
        {
            scroll--;
        }
        while (id - scroll >= 3 && id < skills - 1)
        {
            scroll++;
        }

        setSelection(player, id, scroll);
        setData(player, SELECTION, id);
    }

    private static int getId(Player player)
    {
        return getSelection(player) & 0xff;
    }

    private static int getScroll(Player player)
    {
        return getSelection(player) >> 8;
    }

    private static void setSelection(Player player, int id, int scroll)
    {
        setSelection(player, id | (scroll << 8));
    }

    @SuppressWarnings("unchecked")
    private static ArrayList<PlayerSkill> getSkills(Player player)
    {
        Object list = getData(player, AVAILABLE);
        return list == null ? null : (ArrayList<PlayerSkill>) list;
    }

    /**
     * Moves to the previous skill
     *
     * @param player player to move for
     */
    @Override
    public void onUp(Player player)
    {
        move(player, -1);
    }

    /**
     * Moves to the next skill
     *
     * @param player player to move for
     */
    @Override
    public void onDown(Player player)
    {
        move(player, 1);
    }

    /**
     * Jumping back 4 skills
     *
     * @param player player to jump back for
     */
    @Override
    public void onLeft(Player player)
    {
        move(player, -4);
    }

    /**
     * Jumping forward 4 skills
     *
     * @param player player to jump forward for
     */
    @Override
    public void onRight(Player player)
    {
        move(player, 4);
    }

    /**
     * Selects the current skill for extra details
     *
     * @param player player selecting a skill
     */
    @Override
    public void onSelect(Player player)
    {
        MapMenuManager.sendNext(player, Menu.DETAIL_MENU);
    }

    /**
     * Gets the player ready for the menu
     *
     * @param player player to prepare for the menu
     */
    @Override
    public void setup(Player player)
    {
        MapScheme scheme = MapScheme.get(api, SkillAPI.getPlayerData(player).getScheme());

        MapScene scene = getScene(player);
        PlayerData playerData = SkillAPI.getPlayerData(player);
        ArrayList<PlayerSkill> skills = new ArrayList<PlayerSkill>();
        for (PlayerSkill skill : playerData.getSkills())
        {
            if (skill.getPlayerClass().getData().getSkillTree().canShow(player, skill.getData()))
            {
                int id = skills.size();
                skills.add(skill);

                scene.add(PLATE + id, new MapObject(scheme.getImage(Menu.NAMEPLATE), 0, 0));
                scene.add(NAME + id, new MapObject(new MapString(scheme.getFont(Menu.LIST), scheme.getColor(Menu.FONT), skill.getData().getName()), 0, 0));
            }
        }
        scene.add(SELECTOR, new MapObject(scheme.getImage(Menu.SELECTOR), 0, 0));
        scene.add(TITLE, new MapObject(scheme.getImage(Menu.TITLE), 0, 0));

        setSelection(player, 0);
        setData(player, AVAILABLE, skills);
        setData(player, SELECTION, 0);
    }

    @Override
    public void render(MapBuffer mapBuffer, Player player)
    {
        MapScene scene = getScene(player);

        int id = getId(player);
        int scroll = getScroll(player);
        ArrayList<PlayerSkill> skills = getSkills(player);

        // Draw skill list
        for (int i = Math.max(0, scroll - 1); i < scroll + 6 && i < skills.size(); i++)
        {
            int y = (i - scroll) * 20 + 36;
            scene.get(PLATE + i).moveTo(0, y);
            if (id == i) scene.get(SELECTOR).moveTo(6, y + 5);
            scene.get(NAME + i).moveTo(30, y + 14);
        }

        scene.apply(mapBuffer);
    }
}
