package com.sucy.skill.gui;

import com.rit.sucy.gui.MapBuffer;
import com.rit.sucy.gui.MapMenu;
import com.rit.sucy.gui.MapMenuManager;
import com.rit.sucy.gui.MapScheme;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.player.PlayerSkill;
import com.sucy.skill.api.skills.Skill;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Base class for list menus in SkillAPI
 */
public abstract class ListMenu extends MapMenu
{
    private static HashMap<String, MenuData> menuData = new HashMap<String, MenuData>();

    public static PlayerSkill getSkill(Player player)
    {
        MenuData data = menuData.get(player.getName());
        return data == null ? null : data.skills.get(data.id);
    }

    private SkillAPI api;

    public ListMenu(SkillAPI api)
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
        MenuData data = menuData.get(player.getName());

        data.id += i;
        if (data.id < 0)
        {
            data.id = 0;
            data.scroll = 0;
        }
        if (data.id >= data.skills.size())
        {
            data.id = data.skills.size() - 1;
            data.scroll = data.id - 3;
        }

        while (data.id - data.scroll <= 0 && data.id > 0)
        {
            data.scroll--;
            data.smooth = 20;
        }
        while (data.id - data.scroll >= 3 && data.id < data.skills.size() - 1)
        {
            data.scroll++;
            data.smooth = -20;
        }
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
        menuData.get(player.getName()).smooth = 0;
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
        menuData.get(player.getName()).smooth = 0;
    }

    /**
     * Selects the current skill for extra details
     *
     * @param player player selecting a skill
     */
    @Override
    public void onSelect(Player player)
    {
        SkillDetailMenu.init(player);
        MapMenuManager.sendNext(player, Menu.DETAIL_MENU);
    }

    @Override
    public void render(MapBuffer mapBuffer, Player player)
    {
        if (!menuData.containsKey(player.getName()))
        {
            menuData.put(player.getName(), new MenuData(player, SkillAPI.getPlayerData(player)));
        }
        MenuData data = menuData.get(player.getName());
        MapScheme scheme = MapScheme.get(api, SkillAPI.getPlayerData(player).getScheme());

        // Clear the buffer
        mapBuffer.clear();

        if (data.smooth < 0) data.smooth += 2;
        if (data.smooth > 0) data.smooth -= 2;

        // Draw skill list
        for (int i = Math.max(0, data.scroll - 1); i < data.scroll + 5 && i < data.skills.size(); i++)
        {
            Skill skill = data.skills.get(i).getData();
            int y = (i - data.scroll) * 20 + 36 - data.smooth;
            mapBuffer.drawImg(scheme.getImage(Menu.NAMEPLATE), 0, y);
            if (data.id == i) mapBuffer.drawImg(scheme.getImage(Menu.SELECTOR), 6, y + 5);
            mapBuffer.drawString(scheme.getFont(Menu.LIST), scheme.getColor(Menu.FONT), skill.getName(), 30, y + 14);
        }

        mapBuffer.drawImg(scheme.getImage(Menu.TITLE), 0, 0);
    }

    private class MenuData
    {
        public ArrayList<PlayerSkill> skills = new ArrayList<PlayerSkill>();
        public int scroll, smooth, id;

        public MenuData(Player player, PlayerData data)
        {
            update(player, data);
        }

        public void update(Player player, PlayerData data)
        {
            scroll = smooth = id = 0;
            for (PlayerSkill skill : data.getSkills())
            {
                if (skill.getPlayerClass().getData().getSkillTree().canShow(player, skill.getData()))
                {
                    skills.add(skill);
                }
            }
        }
    }
}
