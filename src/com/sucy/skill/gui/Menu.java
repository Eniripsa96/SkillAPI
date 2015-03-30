package com.sucy.skill.gui;

import com.rit.sucy.gui.MapFont;
import com.rit.sucy.gui.MapMenuManager;
import com.rit.sucy.gui.MapScheme;
import com.sucy.skill.SkillAPI;

import java.awt.*;
import java.io.File;

/**
 * Manages schemes for the map menus
 */
public class Menu
{
    // Menu keys
    public static final String SKILL_TREE = "sapiSkills";

    // Menus
    public static SkillListMenu   LIST_MENU;
    public static SkillDetailMenu DETAIL_MENU;

    // Images
    public static final String BACKGROUND = "background";
    public static final String TITLE      = "title";
    public static final String NAMEPLATE  = "nameplate";
    public static final String SELECTOR   = "selector";
    public static final String UP_0       = "up0";
    public static final String UP_1       = "up1";
    public static final String DOWN_0     = "down0";
    public static final String DOWN_1     = "down1";
    public static final String MORE_0     = "more0";
    public static final String MORE_1     = "more1";
    public static final String BACK_0     = "back0";
    public static final String BACK_1     = "back1";

    // Fonts
    public static final String LIST   = "list";
    public static final String DETAIL = "detail";

    // Colors
    public static final String FONT = "font";

    /**
     * Sets up the schemes for SkillAPI
     *
     * @param api SkillAPI reference
     */
    public static void initialize(SkillAPI api)
    {
        LIST_MENU = new SkillListMenu(api);
        DETAIL_MENU = new SkillDetailMenu(api);
        DETAIL_MENU.setParent(LIST_MENU);
        MapMenuManager.registerMenu(SKILL_TREE, LIST_MENU);

        MapScheme scheme = MapScheme.create(api, new File(api.getDataFolder(), "img"));

        // Define images
        scheme.defineImg(BACKGROUND, BACKGROUND);
        scheme.defineImg(TITLE, TITLE);
        scheme.defineImg(NAMEPLATE, NAMEPLATE);
        scheme.defineImg(SELECTOR, SELECTOR);
        scheme.defineImg(UP_0, UP_0);
        scheme.defineImg(UP_1, UP_1);
        scheme.defineImg(DOWN_0, DOWN_0);
        scheme.defineImg(DOWN_1, DOWN_1);
        scheme.defineImg(MORE_0, MORE_0);
        scheme.defineImg(MORE_1, MORE_1);
        scheme.defineImg(BACK_0, BACK_0);
        scheme.defineImg(BACK_1, BACK_1);

        // Define fonts
        scheme.defineFont(LIST, new MapFont(new Font("Tahoma", Font.BOLD, 12), 2));
        scheme.defineFont(DETAIL, new MapFont(new Font("Tahoma", Font.PLAIN, 9), 1));

        // Define colors
        scheme.defineColor(FONT, "FFFFFF");

        scheme.finalize();
    }
}
