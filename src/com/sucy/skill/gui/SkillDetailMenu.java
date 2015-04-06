package com.sucy.skill.gui;

import com.rit.sucy.gui.*;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerSkill;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public class SkillDetailMenu extends MapMenu
{
    private static HashMap<String, Integer[]> menuData = new HashMap<String, Integer[]>();

    public static void init(Player player)
    {
        menuData.put(player.getName(), new Integer[] { 0, 0 });
    }

    private SkillAPI api;

    public SkillDetailMenu(SkillAPI api)
    {
        this.api = api;
    }

    @Override
    public void onLeft(Player player)
    {
        Integer[] data = menuData.get(player.getName());
        data[0] = Math.max(0, data[0] - 1);
        boolean down = SkillAPI.getSettings().isAllowDowngrade();
        if (data[0] == 2 && !down) data[0]--;
    }

    @Override
    public void onRight(Player player)
    {
        Integer[] data = menuData.get(player.getName());
        data[0] = Math.min(3, data[0] + 1);
        boolean down = SkillAPI.getSettings().isAllowDowngrade();
        if (data[0] == 2 && !down) data[0]++;
    }

    @Override
    public void onUp(Player player)
    {
        SkillAPI.getPlayerData(player).upgradeSkill(SkillListMenu.getSkill(player).getData());
    }

    @Override
    public void onDown(Player player)
    {
        SkillAPI.getPlayerData(player).downgradeSkill(SkillListMenu.getSkill(player).getData());
    }

    @Override
    public void onSelect(Player player)
    {
        Integer[] data = menuData.get(player.getName());

        // Back button
        if (data[0] == 0)
        {
            MapMenuManager.sendBack(player);
        }

        // Upgrade button
        else if (data[0] == 1)
        {
            SkillAPI.getPlayerData(player).upgradeSkill(SkillListMenu.getSkill(player).getData());
        }

        // Downgrade button
        else if (data[0] == 2)
        {
            SkillAPI.getPlayerData(player).downgradeSkill(SkillListMenu.getSkill(player).getData());
        }

        // More info button
        else
        {
            data[1]++;
        }
    }

    @Override
    public void render(MapBuffer mapBuffer, Player player)
    {
        Integer[] data = menuData.get(player.getName());
        int button = data[0];
        int page = data[1];

        MapScheme scheme = MapScheme.get(api, SkillAPI.getPlayerData(player).getScheme());

        // Background
        mapBuffer.drawImg(scheme.getImage(Menu.BACKGROUND), 0, 0);

        // Get text to draw
        PlayerSkill skill = SkillListMenu.getSkill(player);
        ItemStack icon = skill.getData().getIndicator(skill);
        List<String> lore = icon.getItemMeta().getLore();
        lore.add(0, icon.getItemMeta().getDisplayName());

        // Find page details
        MapFont font = scheme.getFont(Menu.DETAIL);
        int linesPerPage = 90 / (font.getFont().getSize() + 3);
        int pages = (lore.size() + linesPerPage - 1) / linesPerPage;
        if (page >= pages)
        {
            data[1] -= pages;
            page -= pages;
        }

        // Draw the text
        int y = font.getFont().getSize() + 5;
        int start = linesPerPage * page;
        for (int i = start; i < start + linesPerPage && i < lore.size(); i++)
        {
            String line = lore.get(i);
            mapBuffer.drawColorString(font, scheme.getColor(Menu.FONT), line, 7, y, '&');
            y += font.getFont().getSize() + 3;
        }

        // Get position for upgrade arrow
        boolean down = SkillAPI.getSettings().isAllowDowngrade();
        int x;
        if (down) x = 42;
        else x = 56;

        // Draw buttons
        mapBuffer.drawImg(button == 0 ? scheme.getImage(Menu.BACK_1) : scheme.getImage(Menu.BACK_0), 6, 95);
        mapBuffer.drawImg(button == 1 ? scheme.getImage(Menu.UP_1) : scheme.getImage(Menu.UP_0), x, 95);
        if (down) mapBuffer.drawImg(button == 2 ? scheme.getImage(Menu.DOWN_1) : scheme.getImage(Menu.DOWN_0), 68, 95);
        mapBuffer.drawImg(button == 3 ? scheme.getImage(Menu.MORE_1) : scheme.getImage(Menu.MORE_0), 90, 95);
    }
}
