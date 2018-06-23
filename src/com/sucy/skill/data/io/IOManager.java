/**
 * SkillAPI
 * com.sucy.skill.data.io.IOManager
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
package com.sucy.skill.data.io;

import com.rit.sucy.config.parse.DataSection;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.classes.RPGClass;
import com.sucy.skill.api.player.*;
import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.log.Logger;
import com.sucy.skill.manager.ComboManager;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base class for managers that handle saving and loading player data
 */
public abstract class IOManager
{
    private static final String
        LIMIT          = "limit",
        ACTIVE         = "active",
        ACCOUNTS       = "accounts",
        ACCOUNT_PREFIX = "acc",
        HEALTH         = "health",
        MANA           = "mana",
        CLASSES        = "classes",
        SKILLS         = "skills",
        BINDS          = "binds",
        LEVEL          = "level",
        EXP            = "exp",
        POINTS         = "points",
        SKILL_BAR      = "bar",
        HOVER          = "hover",
        EXTRA          = "extra",
        INSTANT        = "instant",
        ENABLED        = "enabled",
        SLOTS          = "slots",
        UNASSIGNED     = "e",
        COMBOS         = "combos",
        ATTRIBS        = "attribs",
        COOLDOWN       = "cd",
        HUNGER         = "hunger",
        ATTRIB_POINTS  = "attrib-points";

    /**
     * API reference
     */
    protected final SkillAPI api;

    /**
     * Initializes a new IO manager
     *
     * @param api SkillAPI reference
     */
    IOManager(SkillAPI api)
    {
        this.api = api;
    }

    /**
     * Loads player data for all online players
     *
     * @return loaded player data
     */
    public abstract HashMap<String, PlayerAccounts> loadAll();

    /**
     * Loads data for the player
     *
     * @param player player to load for
     *
     * @return loaded player data
     */
    public abstract PlayerAccounts loadData(OfflinePlayer player);

    /**
     * Saves the player's data
     *
     * @param data data to save
     */
    public abstract void saveData(PlayerAccounts data);

    /**
     * Saves all player data
     */
    public void saveAll()
    {
        for (PlayerAccounts data : SkillAPI.getPlayerAccountData().values())
        {
            saveData(data);
        }
    }

    /**
     * Loads data from the DataSection for the given player
     *
     * @param player player to load for
     * @param file   DataSection containing the account info
     *
     * @return the loaded player account data
     */
    protected PlayerAccounts load(OfflinePlayer player, DataSection file)
    {
        PlayerAccounts data = new PlayerAccounts(player);
        DataSection accounts = file.getSection(ACCOUNTS);
        if (accounts == null)
        {
            data.getActiveData().endInit();
            return data;
        }
        for (String accountKey : accounts.keys())
        {
            DataSection account = accounts.getSection(accountKey);
            PlayerData acc = data.getData(Integer.parseInt(accountKey.replace(ACCOUNT_PREFIX, "")), player, true);

            // Load classes
            DataSection classes = account.getSection(CLASSES);
            if (classes != null)
            {
                for (String classKey : classes.keys())
                {
                    RPGClass rpgClass = SkillAPI.getClass(classKey);
                    if (rpgClass != null)
                    {
                        PlayerClass c = acc.setClass(rpgClass);
                        DataSection classData = classes.getSection(classKey);
                        int levels = classData.getInt(LEVEL);
                        if (levels > 0)
                            c.setLevel(levels);
                        c.setPoints(classData.getInt(POINTS));
                        if (classData.has("total-exp"))
                            c.setExp(classData.getDouble("total-exp") - c.getTotalExp());
                        else
                            c.setExp(classData.getDouble(EXP));
                    }
                }
            }

            // Load skills
            DataSection skills = account.getSection(SKILLS);
            if (skills != null)
            {
                for (String skillKey : skills.keys())
                {
                    DataSection skill = skills.getSection(skillKey);
                    PlayerSkill skillData = acc.getSkill(skillKey);
                    if (skillData != null)
                    {
                        skillData.setLevel(skill.getInt(LEVEL));
                        skillData.setPoints(skill.getInt(POINTS));
                        skillData.addCooldown(skill.getInt(COOLDOWN, 0));
                    }
                }
            }

            // Load skill bar
            if (SkillAPI.getSettings().isSkillBarEnabled() || SkillAPI.getSettings().isUsingCombat())
            {
                final DataSection skillBar = account.getSection(SKILL_BAR);
                final PlayerSkillBar bar = acc.getSkillBar();
                if (skillBar != null && bar != null)
                {
                    boolean enabled = skillBar.getBoolean(ENABLED, true);
                    for (final String key : skillBar.keys())
                    {
                        final boolean[] locked = SkillAPI.getSettings().getLockedSlots();
                        if (key.equals(SLOTS)) {
                            for (int i = 0; i < 9; i++)
                                if (!bar.isWeaponSlot(i) && !locked[i])
                                    bar.getData().remove(i + 1);

                            final List<String> slots = skillBar.getList(SLOTS);
                            for (final String slot : slots) {
                                int i = Integer.parseInt(slot);
                                if (!locked[i - 1])
                                    bar.getData().put(i, UNASSIGNED);
                            }
                        }
                        else if (SkillAPI.getSkill(key) != null)
                            bar.getData().put(skillBar.getInt(key), key);
                    }
                    
                    bar.applySettings();
                }
            }

            // Load combos
            if (SkillAPI.getSettings().isCustomCombosAllowed())
            {
                DataSection combos = account.getSection(COMBOS);
                PlayerCombos comboData = acc.getComboData();
                ComboManager cm = SkillAPI.getComboManager();
                if (combos != null && comboData != null)
                {
                    for (String key : combos.keys())
                    {
                        Skill skill = SkillAPI.getSkill(key);
                        if (acc.hasSkill(key) && skill != null && skill.canCast())
                        {
                            int combo = cm.parseCombo(combos.getString(key));
                            if (combo == -1) Logger.invalid("Invalid skill combo: " + combos.getString(key));
                            else comboData.setSkill(skill, combo);
                        }
                    }
                }
            }

            // Load attributes
            if (SkillAPI.getSettings().isAttributesEnabled())
            {
                acc.setAttribPoints(account.getInt(ATTRIB_POINTS, 0));
                DataSection attribs = account.getSection(ATTRIBS);
                if (attribs != null)
                {
                    for (String key : attribs.keys())
                    {
                        acc.getAttributeData().put(key, attribs.getInt(key));
                    }
                }
            }

            // Load cast bars
            if (SkillAPI.getSettings().isCastEnabled())
            {
                acc.getCastBars().reset();
                acc.getCastBars().load(account.getSection(HOVER), true);
                acc.getCastBars().load(account.getSection(INSTANT), false);
            }

            acc.setHungerValue(account.getDouble(HUNGER, 1));

            // Extra data
            if (account.has(EXTRA)) {
                acc.getExtraData().applyDefaults(account.getSection(EXTRA));
            }

            acc.endInit();

            // Load binds
            DataSection binds = account.getSection(BINDS);
            if (binds != null)
            {
                for (String bindKey : binds.keys())
                {
                    acc.bind(Material.valueOf(bindKey), acc.getSkill(binds.getString(bindKey)));
                }
            }
        }
        data.setAccount(file.getInt(ACTIVE, data.getActiveId()), false);
        data.getActiveData().setLastHealth(file.getDouble(HEALTH));
        data.getActiveData().setMana(file.getDouble(MANA, data.getActiveData().getMana()));

        return data;
    }

    protected DataSection save(PlayerAccounts data)
    {
        try
        {
            DataSection file = new DataSection();
            file.set(LIMIT, data.getAccountLimit());
            file.set(ACTIVE, data.getActiveId());
            file.set(HEALTH, data.getActiveData().getLastHealth());
            file.set(MANA, data.getActiveData().getMana());
            DataSection accounts = file.createSection(ACCOUNTS);
            for (Map.Entry<Integer, PlayerData> entry : data.getAllData().entrySet())
            {
                DataSection account = accounts.createSection(ACCOUNT_PREFIX + entry.getKey());
                PlayerData acc = entry.getValue();

                // Save classes
                DataSection classes = account.createSection(CLASSES);
                for (PlayerClass c : acc.getClasses())
                {
                    DataSection classSection = classes.createSection(c.getData().getName());
                    classSection.set(LEVEL, c.getLevel());
                    classSection.set(POINTS, c.getPoints());
                    classSection.set(EXP, c.getExp());
                }

                // Save skills
                DataSection skills = account.createSection(SKILLS);
                for (PlayerSkill skill : acc.getSkills())
                {
                    DataSection skillSection = skills.createSection(skill.getData().getName());
                    skillSection.set(LEVEL, skill.getLevel());
                    skillSection.set(POINTS, skill.getPoints());
                    if (skill.isOnCooldown())
                        skillSection.set(COOLDOWN, skill.getCooldown());
                }

                // Save binds
                DataSection binds = account.createSection(BINDS);
                for (Map.Entry<Material, PlayerSkill> bind : acc.getBinds().entrySet())
                {
                    if (bind.getKey() == null || bind.getValue() == null) continue;
                    binds.set(bind.getKey().name(), bind.getValue().getData().getName());
                }

                // Save skill bar
                if ((SkillAPI.getSettings().isSkillBarEnabled() || SkillAPI.getSettings().isUsingCombat())
                        && acc.getSkillBar() != null)
                {
                    DataSection skillBar = account.createSection(SKILL_BAR);
                    PlayerSkillBar bar = acc.getSkillBar();
                    skillBar.set(ENABLED, bar.isEnabled());
                    skillBar.set(SLOTS, new ArrayList<>(bar.getData().keySet()));
                    for (Map.Entry<Integer, String> slotEntry : bar.getData().entrySet())
                    {
                        if (slotEntry.getValue().equals(UNASSIGNED))
                        {
                            continue;
                        }
                        skillBar.set(slotEntry.getValue(), slotEntry.getKey());
                    }
                }

                // Save combos
                if (SkillAPI.getSettings().isCustomCombosAllowed())
                {
                    DataSection combos = account.createSection(COMBOS);
                    PlayerCombos comboData = acc.getComboData();
                    ComboManager cm = SkillAPI.getComboManager();
                    if (combos != null && comboData != null)
                    {
                        HashMap<Integer, String> comboMap = comboData.getSkillMap();
                        for (Map.Entry<Integer, String> combo : comboMap.entrySet())
                        {
                            combos.set(combo.getValue(), cm.getSaveString(combo.getKey()));
                        }
                    }
                }

                // Save attributes
                if (SkillAPI.getSettings().isAttributesEnabled())
                {
                    account.set(ATTRIB_POINTS, acc.getAttributePoints());
                    DataSection attribs = account.createSection(ATTRIBS);
                    for (String key : acc.getAttributeData().keySet())
                    {
                        attribs.set(key, acc.getAttributeData().get(key));
                    }
                }

                // Save cast bars
                if (SkillAPI.getSettings().isCastEnabled())
                {
                    acc.getCastBars().save(account.createSection(HOVER), true);
                    acc.getCastBars().save(account.createSection(INSTANT), false);
                }

                account.set(HUNGER, acc.getHungerValue());

                // Extra data
                if (acc.getExtraData().size() > 0) {
                    account.set(EXTRA, acc.getExtraData());
                }
            }
            return file;
        }
        catch (Exception ex)
        {
            Logger.bug("Failed to save player data for " + data.getPlayer().getName());
            ex.printStackTrace();
            return null;
        }
    }
}
