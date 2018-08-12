/**
 * SkillAPI
 * com.sucy.skill.api.player.PlayerData
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

import com.rit.sucy.config.Filter;
import com.rit.sucy.config.FilterType;
import com.rit.sucy.items.InventoryManager;
import com.rit.sucy.player.TargetHelper;
import com.rit.sucy.version.VersionManager;
import com.rit.sucy.version.VersionPlayer;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.classes.RPGClass;
import com.sucy.skill.api.enums.ExpSource;
import com.sucy.skill.api.enums.ManaCost;
import com.sucy.skill.api.enums.ManaSource;
import com.sucy.skill.api.enums.PointSource;
import com.sucy.skill.api.enums.SkillStatus;
import com.sucy.skill.api.event.PlayerCastSkillEvent;
import com.sucy.skill.api.event.PlayerClassChangeEvent;
import com.sucy.skill.api.event.PlayerManaGainEvent;
import com.sucy.skill.api.event.PlayerManaLossEvent;
import com.sucy.skill.api.event.PlayerPreClassChangeEvent;
import com.sucy.skill.api.event.PlayerRefundAttributeEvent;
import com.sucy.skill.api.event.PlayerSkillCastFailedEvent;
import com.sucy.skill.api.event.PlayerSkillDowngradeEvent;
import com.sucy.skill.api.event.PlayerSkillUnlockEvent;
import com.sucy.skill.api.event.PlayerSkillUpgradeEvent;
import com.sucy.skill.api.event.PlayerUpAttributeEvent;
import com.sucy.skill.api.skills.PassiveSkill;
import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.api.skills.SkillShot;
import com.sucy.skill.api.skills.TargetSkill;
import com.sucy.skill.data.GroupSettings;
import com.sucy.skill.data.Permissions;
import com.sucy.skill.dynamic.EffectComponent;
import com.sucy.skill.language.ErrorNodes;
import com.sucy.skill.language.GUINodes;
import com.sucy.skill.language.RPGFilter;
import com.sucy.skill.listener.AttributeListener;
import com.sucy.skill.listener.TreeListener;
import com.sucy.skill.log.LogType;
import com.sucy.skill.log.Logger;
import com.sucy.skill.manager.AttributeManager;
import com.sucy.skill.task.InventoryTask;
import com.sucy.skill.task.ScoreboardTask;
import com.sucy.skill.tree.basic.InventoryTree;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import static com.sucy.skill.api.event.PlayerSkillCastFailedEvent.Cause.CANCELED;
import static com.sucy.skill.api.event.PlayerSkillCastFailedEvent.Cause.CASTER_DEAD;
import static com.sucy.skill.api.event.PlayerSkillCastFailedEvent.Cause.EFFECT_FAILED;
import static com.sucy.skill.api.event.PlayerSkillCastFailedEvent.Cause.NOT_UNLOCKED;
import static com.sucy.skill.api.event.PlayerSkillCastFailedEvent.Cause.NO_MANA;
import static com.sucy.skill.api.event.PlayerSkillCastFailedEvent.Cause.NO_TARGET;
import static com.sucy.skill.api.event.PlayerSkillCastFailedEvent.Cause.ON_COOLDOWN;

/**
 * Represents one account for a player which can contain one class from each group
 * and the skills in each of those classes. You should not instantiate this class
 * yourself and instead get it from the SkillAPI static methods.
 */
public final class PlayerData
{
    private final HashMap<String, PlayerClass>   classes     = new HashMap<String, PlayerClass>();
    private final HashMap<String, PlayerSkill>   skills      = new HashMap<String, PlayerSkill>();
    private final HashMap<Material, PlayerSkill> binds       = new HashMap<Material, PlayerSkill>();
    private final HashMap<String, Integer>       attributes  = new HashMap<String, Integer>();
    private final HashMap<String, Integer>       bonusAttrib = new HashMap<String, Integer>();

    private OfflinePlayer  player;
    private PlayerSkillBar skillBar;
    private PlayerCombos   combos;
    private String         scheme;
    private String         menuClass;
    private double         mana;
    private double         maxMana;
    private double         bonusHealth;
    private double         bonusMana;
    private double         lastHealth;
    private boolean        init;
    private boolean        passive;
    private int            attribPoints;

    /**
     * Initializes a new account data representation for a player.
     *
     * @param player player to store the data for
     */
    public PlayerData(OfflinePlayer player, boolean init)
    {
        this.player = player;
        this.skillBar = new PlayerSkillBar(this);
        this.combos = new PlayerCombos(this);
        this.init = SkillAPI.isLoaded() && init;
        this.scheme = "default";
        for (String group : SkillAPI.getGroups())
        {
            GroupSettings settings = SkillAPI.getSettings().getGroupSettings(group);
            RPGClass rpgClass = settings.getDefault();

            if (rpgClass != null && settings.getPermission() == null)
            {
                setClass(rpgClass);
            }
        }
    }

    /**
     * Retrieves the Bukkit player object of the owner
     *
     * @return Bukkit player object of the owner or null if offline
     */
    public Player getPlayer()
    {
        final Player updated = new VersionPlayer(player).getPlayer();
        return updated == null ? player.getPlayer() : updated;
    }

    /**
     * Retrieves the name of the owner
     *
     * @return name of the owner
     */
    public String getPlayerName()
    {
        return player.getName();
    }

    /**
     * Retrieves the skill bar data for the owner
     *
     * @return skill bar data of the owner
     */
    public PlayerSkillBar getSkillBar()
    {
        return skillBar;
    }

    /**
     * Returns the data for the player's combos
     *
     * @return combo data for the player
     */
    public PlayerCombos getComboData()
    {
        return combos;
    }

    /**
     * @return health during last logout
     */
    public double getLastHealth()
    {
        return lastHealth;
    }

    /**
     * Used by the API for restoring health - do not use this.
     *
     * @param health health logged off with
     */
    public void setLastHealth(double health)
    {
        lastHealth = health;
    }

    /**
     * Ends the initialization flag for the data. Used by the
     * API to avoid async issues. Do not use this in other
     * plugins.
     */
    public void endInit()
    {
        init = false;
    }

    /**
     * Retrieves the name of the active map menu scheme for the player
     *
     * @return map menu scheme name
     */
    public String getScheme()
    {
        return scheme;
    }

    /**
     * Sets the active scheme name for the player
     *
     * @param name name of the scheme
     */
    public void setScheme(String name)
    {
        scheme = name;
    }

    ///////////////////////////////////////////////////////
    //                                                   //
    //                    Attributes                     //
    //                                                   //
    ///////////////////////////////////////////////////////

    /**
     * Retrieves a map of all player attribute totals. Modifying
     * the map will not change actual player attributes.
     *
     * @return attribute totals
     */
    public HashMap<String, Integer> getAttributes() {
        HashMap<String, Integer> map = new HashMap<>();
        for (String key : SkillAPI.getAttributeManager().getKeys()) { map.put(key, getAttribute(key)); }
        return map;
    }

    /**
     * Retrieves a map of all attributes the player invested.
     * This doesn't count base attributes from classes or
     * bonus attributes from effects. Modifying the map will
     * not change actual player attributes.
     *
     * @return attribute totals
     */
    public HashMap<String, Integer> getInvestedAttributes() {
        return new HashMap<>(attributes);
    }

    /**
     * Gets the number of attribute points the player has
     * between invested and bonus sources.
     *
     * @param key attribute key
     *
     * @return number of total points
     */
    public int getAttribute(String key) {
        key = key.toLowerCase();
        int total = 0;
        if (attributes.containsKey(key)) { total += attributes.get(key); }
        if (bonusAttrib.containsKey(key)) { total += bonusAttrib.get(key); }
        for (PlayerClass playerClass : classes.values()) {
            total += playerClass.getData().getAttribute(key, playerClass.getLevel());
        }
        return Math.max(0, total);
    }

    /**
     * Gets the number of attribute points invested in the
     * given attribute
     *
     * @param key attribute key
     *
     * @return number of invested points
     */
    public int getInvestedAttribute(String key) {
        return attributes.getOrDefault(key.toLowerCase(), 0);
    }

    /**
     * Checks whether or not the player has any
     * points invested in a given attribute
     *
     * @param key attribute key
     *
     * @return true if any points are invested, false otherwise
     */
    public boolean hasAttribute(String key) {
        return getAttribute(key) > 0;
    }

    /**
     * Invests a point in the attribute if the player
     * has any remaining attribute points. If the player
     * has no remaining points, this will do nothing.
     *
     * @param key attribute key
     *
     * @return whether or not it was successfully upgraded
     */
    public boolean upAttribute(String key) {
        key = key.toLowerCase();
        int current = getInvestedAttribute(key);
        int max = SkillAPI.getAttributeManager().getAttribute(key).getMax();
        if (attribPoints > 0 && current < max) {
            attributes.put(key, current + 1);
            attribPoints--;

            PlayerUpAttributeEvent event = new PlayerUpAttributeEvent(this, key);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                attributes.put(key, current);
                attribPoints++;
            } else { return true; }
        }
        return false;
    }

    /**
     * Gives the player attribute points without costing
     * attribute points.
     *
     * @param key    attribute to give points for
     * @param amount amount to give
     */
    public void giveAttribute(String key, int amount) {
        key = key.toLowerCase();
        int current = getInvestedAttribute(key);
        int max = SkillAPI.getAttributeManager().getAttribute(key).getMax();
        amount = Math.min(amount + current, max);
        if (amount > current) {
            attributes.put(key, amount);
            AttributeListener.updatePlayer(this);
        }
    }

    /**
     * Adds bonus attributes to the player. These do not count towards
     * the max invest amount and cannot be refunded.
     *
     * @param key    attribute key
     * @param amount amount to add
     */
    public void addBonusAttributes(String key, int amount) {
        key = SkillAPI.getAttributeManager().normalize(key);
        amount += bonusAttrib.getOrDefault(key, 0);
        bonusAttrib.put(key, amount);
        AttributeListener.updatePlayer(this);
    }

    /**
     * Refunds an attribute point from the given attribute
     * if there are any points invested in it. If there are
     * none, this will do nothing.
     *
     * @param key attribute key
     */
    public boolean refundAttribute(String key) {
        key = key.toLowerCase();
        int current = getInvestedAttribute(key);
        if (current > 0) {
            PlayerRefundAttributeEvent event = new PlayerRefundAttributeEvent(this, key);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) { return false; }

            attribPoints += 1;
            attributes.put(key, current - 1);
            if (current - 1 <= 0) { attributes.remove(key); }
            AttributeListener.updatePlayer(this);

            return true;
        }
        return false;
    }

    /**
     * Refunds all spent attribute points for a specific attribute
     */
    public void refundAttributes(String key) {
        key = key.toLowerCase();
        attribPoints += getInvestedAttribute(key);
        attributes.remove(key);
        AttributeListener.updatePlayer(this);
    }

    /**
     * Refunds all spent attribute points
     */
    public void refundAttributes() {
        ArrayList<String> keys = new ArrayList<>(attributes.keySet());
        for (String key : keys) {
            refundAttributes(key);
        }
    }

    /**
     * Retrieves the current number of attribute points the player has
     *
     * @return attribute point total
     */
    public int getAttributePoints() {
        return attribPoints;
    }

    /**
     * Gives the player attribute points
     *
     * @param amount amount of attribute points
     */
    public void giveAttribPoints(int amount) {
        attribPoints += amount;
    }

    /**
     * Sets the current amount of attribute points
     *
     * @param amount amount of points to have
     */
    public void setAttribPoints(int amount) {
        attribPoints = amount;
    }

    /**
     * Scales a stat value using the player's attributes
     *
     * @param stat  stat key
     * @param value base value
     *
     * @return modified value
     */
    public double scaleStat(final String stat, final double value) {
        final AttributeManager manager = SkillAPI.getAttributeManager();
        if (manager == null) { return value; }

        final List<AttributeManager.Attribute> matches = manager.forStat(stat);
        if (matches == null) { return value; }

        double modified = value;
        for (final AttributeManager.Attribute attribute : matches) {
            int amount = getAttribute(attribute.getKey());
            if (amount > 0) {
                modified = attribute.modifyStat(stat, modified, amount);
            }
        }
        return modified;
    }

    /**
     * Scales a dynamic skill's value using global modifiers
     *
     * @param component component holding the value
     * @param key       key of the value
     * @param value     unmodified value
     *
     * @return the modified value
     */
    public double scaleDynamic(EffectComponent component, String key, double value) {
        final AttributeManager manager = SkillAPI.getAttributeManager();
        if (manager == null) { return value; }

        final List<AttributeManager.Attribute> matches = manager.forComponent(component, key);
        if (matches == null) { return value; }

        for (final AttributeManager.Attribute attribute : matches) {
            int amount = getAttribute(attribute.getKey());
            if (amount > 0) {
                value = attribute.modify(component, key, value, amount);
            }
        }
        return value;
    }

    /**
     * Opens the attribute menu
     */
    public void openAttributeMenu()
    {
        Player player = getPlayer();
        if (SkillAPI.getSettings().isAttributesEnabled() && player != null)
        {
            AttributeManager manager = SkillAPI.getAttributeManager();
            Inventory inv = InventoryManager.createInventory(
                AttributeListener.MENU_KEY,
                (manager.getKeys().size() + 8) / 9,
                SkillAPI.getLanguage().getMessage(
                    GUINodes.ATTRIB_TITLE,
                    true,
                    FilterType.COLOR,
                    RPGFilter.POINTS.setReplacement(attribPoints + ""),
                    Filter.PLAYER.setReplacement(player.getName())
                ).get(0)
            );
            int i = 0;
            for (String key : manager.getKeys())
            {
                ItemStack icon = manager.getAttribute(key).getIcon().clone();
                ItemMeta meta = icon.getItemMeta();
                meta.setDisplayName(filter(meta.getDisplayName(), key));
                List<String> lore = meta.getLore();
                if (lore == null) lore = new ArrayList<>();
                for (int j = 0; j < lore.size(); j++)
                    lore.set(j, filter(lore.get(j), key));

                icon.setItemMeta(meta);
                inv.setItem(i++, icon);
            }
            player.openInventory(inv);
        }
    }

    private String filter(String text, String key)
    {
        return text
            .replace("{amount}", "" + getInvestedAttribute(key))
            .replace("{total}", "" + getAttribute(key));
    }

    /**
     * Retrieves the player's attribute data.
     * Modifying this will modify the player's
     * actual data.
     *
     * @return the player's attribute data
     */
    public HashMap<String, Integer> getAttributeData() {
        return attributes;
    }

    ///////////////////////////////////////////////////////
    //                                                   //
    //                      Skills                       //
    //                                                   //
    ///////////////////////////////////////////////////////

    /**
     * Checks if the owner has a skill by name. This is not case-sensitive
     * and does not check to see if the skill is unlocked. It only checks if
     * the skill is available to upgrade/use.
     *
     * @param name name of the skill
     *
     * @return true if has the skill, false otherwise
     */
    public boolean hasSkill(String name)
    {
        return name != null && skills.containsKey(name.toLowerCase());
    }

    /**
     * Retrieves a skill of the owner by name. This is not case-sensitive.
     *
     * @param name name of the skill
     *
     * @return data for the skill or null if the player doesn't have the skill
     */
    public PlayerSkill getSkill(String name)
    {
        if (name == null)
            return null;
        return skills.get(name.toLowerCase());
    }

    /**
     * Retrieves all of the skill data the player has. Modifying this
     * collection will not modify the player's owned skills but modifying
     * one of the elements will change that element's data for the player.
     *
     * @return collection of skill data for the owner
     */
    public Collection<PlayerSkill> getSkills()
    {
        return skills.values();
    }

    /**
     * Retrieves the level of a skill for the owner. This is not case-sensitive.
     *
     * @param name name of the skill
     *
     * @return level of the skill or 0 if not found
     */
    public int getSkillLevel(String name)
    {
        PlayerSkill skill = getSkill(name);
        return skill == null ? 0 : skill.getLevel();
    }

    /**
     * Gives the player a skill outside of the normal class skills.
     * This skill will not show up in a skill tree.
     *
     * @param skill skill to give the player
     */
    public void giveSkill(Skill skill)
    {
        giveSkill(skill, null);
    }

    /**
     * Gives the player a skill using the class data as a parent. This
     * skill will not show up in a skill tree.
     *
     * @param skill  skill to give the player
     * @param parent parent class data
     */
    public void giveSkill(Skill skill, PlayerClass parent)
    {
        String key = skill.getKey();
        if (!skills.containsKey(key))
        {
            PlayerSkill data = new PlayerSkill(this, skill, parent);
            combos.addSkill(skill);
            skills.put(key, data);
            autoLevel(skill);
        }
    }

    /**
     * Attempts to auto-level any skills that are able to do so
     */
    public void autoLevel()
    {
        if (init) return;

        for (PlayerSkill skill : skills.values())
        {
            autoLevel(skill.getData());
        }
    }

    private void autoLevel(Skill skill)
    {
        PlayerSkill data = skills.get(skill.getKey());
        if (data == null) return;

        int lastLevel = data.getLevel();
        while (data.getData().canAutoLevel() && !data.isMaxed() && data.getLevelReq() <= data.getPlayerClass().getLevel())
        {
            upgradeSkill(skill);
            if (lastLevel == data.getLevel())
            {
                break;
            }
            lastLevel++;
        }
    }

    /**
     * Upgrades a skill owned by the player. The player must own the skill,
     * have enough skill points, meet the level and skill requirements, and
     * not have maxed out the skill already in order to upgrade the skill.
     * This will consume the skill point cost while upgrading the skill.
     *
     * @param skill skill to upgrade
     *
     * @return true if successfully was upgraded, false otherwise
     */
    public boolean upgradeSkill(Skill skill)
    {
        // Cannot be null
        if (skill == null)
        {
            return false;
        }

        // Must be a valid available skill
        PlayerSkill data = skills.get(skill.getName().toLowerCase());
        if (data == null)
        {
            return false;
        }

        // Must meet any skill requirements
        if (skill.getSkillReq() != null)
        {
            PlayerSkill req = skills.get(skill.getSkillReq().toLowerCase());
            if (req != null && req.getLevel() < skill.getSkillReqLevel())
            {
                return false;
            }
        }

        int level = data.getPlayerClass().getLevel();
        int points = data.getPlayerClass().getPoints();
        int cost = data.getCost();
        if (!data.isMaxed() && level >= data.getLevelReq() && points >= cost)
        {
            // Upgrade event
            PlayerSkillUpgradeEvent event = new PlayerSkillUpgradeEvent(this, data, cost);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled())
            {
                return false;
            }

            // Apply upgrade
            data.getPlayerClass().usePoints(cost);
            data.addLevels(1);

            // Passive calls
            if (passive)
            {
                Player player = getPlayer();
                if (player != null && skill instanceof PassiveSkill)
                {
                    if (data.getLevel() == 1)
                    {
                        ((PassiveSkill) skill).initialize(player, data.getLevel());
                    }
                    else
                    {
                        ((PassiveSkill) skill).update(player, data.getLevel() - 1, data.getLevel());
                    }
                }

                // Unlock event
                if (data.getLevel() == 1)
                {
                    Bukkit.getPluginManager().callEvent(new PlayerSkillUnlockEvent(this, data));
                    this.autoLevel();
                }
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Downgrades a skill owned by the player. The player must own the skill and it must
     * not currently be level 0 for the player to downgrade the skill. This will refund
     * the skill point cost when downgrading the skill.
     *
     * @param skill skill to downgrade
     *
     * @return true if successfully downgraded, false otherwise
     */
    public boolean downgradeSkill(Skill skill)
    {
        // Cannot be null
        if (skill == null)
        {
            return false;
        }

        // Must be a valid available skill
        PlayerSkill data = skills.get(skill.getName().toLowerCase());
        if (data == null)
        {
            return false;
        }

        // Must not be a free skill
        if (data.getCost() == 0)
        {
            return false;
        }

        // Must not be required by another skill
        for (PlayerSkill s : skills.values())
        {
            if (s.getData().getSkillReq() != null
                && s.getData().getSkillReq().equalsIgnoreCase(skill.getName())
                && data.getLevel() <= s.getData().getSkillReqLevel()
                && s.getLevel() > 0)
            {
                return false;
            }
        }

        int cost = skill.getCost(data.getLevel() - 1);
        if (data.getLevel() > 0)
        {
            // Upgrade event
            PlayerSkillDowngradeEvent event = new PlayerSkillDowngradeEvent(this, data, cost);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled())
            {
                return false;
            }

            // Apply upgrade
            data.getPlayerClass().givePoints(cost, PointSource.REFUND);
            data.addLevels(-1);

            // Passive calls
            Player player = getPlayer();
            if (player != null && skill instanceof PassiveSkill)
            {
                if (data.getLevel() == 0)
                {
                    ((PassiveSkill) skill).stopEffects(player, 1);
                }
                else
                {
                    ((PassiveSkill) skill).update(player, data.getLevel() + 1, data.getLevel());
                }
            }

            // Clear bindings
            if (data.getLevel() == 0)
            {
                clearBinds(skill);
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Shows the skill tree for the player. If the player has multiple trees,
     * this will show the list of skill trees they can view.
     */
    public void showSkills()
    {
        showSkills(getPlayer());
    }

    /**
     * Shows the skill tree for the player. If the player has multiple trees,
     * this will show the list of skill trees they can view.
     *
     * @param player player to show the skill tree for
     *
     * @return true if able to show the player, false otherwise
     */
    public boolean showSkills(Player player)
    {
        // Cannot show an invalid player, and cannot show no skills
        if (player == null || classes.size() == 0 || skills.size() == 0)
        {
            return false;
        }

        // Show list of classes that have skill trees
        if (classes.size() > 1)
        {
            Inventory inv = InventoryManager.createInventory(
                TreeListener.CLASS_LIST_KEY,
                (classes.size() + 8) / 9,
                SkillAPI.getLanguage().getMessage(
                    GUINodes.CLASS_LIST,
                    true,
                    FilterType.COLOR,
                    Filter.PLAYER.setReplacement(player.getName())
                ).get(0)
            );
            for (PlayerClass c : classes.values())
            {
                inv.addItem(c.getData().getIcon());
            }
            player.openInventory(inv);
            return true;
        }

        // Show only class's skill tree otherwise
        else return showSkills(player, classes.get(classes.keySet().toArray(new String[1])[0]));
    }

    /**
     * Shows the skill tree to the player for the given class
     *
     * @param player      player to show
     * @param playerClass class to look for
     *
     * @return true if succeeded, false otherwise
     */
    public boolean showSkills(Player player, PlayerClass playerClass)
    {
        // Cannot show an invalid player, and cannot show no skills
        if (player == null || playerClass.getData().getSkills().size() == 0)
        {
            return false;
        }

        // Show skill tree of the class
        this.menuClass = playerClass.getData().getName();
        player.openInventory(((InventoryTree) playerClass.getData().getSkillTree()).getInventory(this));
        return true;
    }

    /**
     * Retrieves the name of the class shown in the skill tree
     *
     * @return class name
     */
    public String getShownClassName()
    {
        return menuClass;
    }

    ///////////////////////////////////////////////////////
    //                                                   //
    //                     Classes                       //
    //                                                   //
    ///////////////////////////////////////////////////////

    /**
     * Checks whether or not the player has as least one class they have professed as.
     *
     * @return true if professed, false otherwise
     */
    public boolean hasClass()
    {
        return classes.size() > 0;
    }

    /**
     * Checks whether or not a player has a class within the given group
     *
     * @param group class group to check
     *
     * @return true if has a class in the group, false otherwise
     */
    public boolean hasClass(String group)
    {
        return classes.containsKey(group);
    }

    /**
     * Retrieves the collection of the data for classes the player has professed as.
     *
     * @return collection of the data for professed classes
     */
    public Collection<PlayerClass> getClasses()
    {
        return classes.values();
    }

    /**
     * Retrieves the data of a class the player professed as by group. This is
     * case-sensitive.
     *
     * @param group group to get the profession for
     *
     * @return professed class data or null if not professed for the group
     */
    public PlayerClass getClass(String group)
    {
        return classes.get(group);
    }

    /**
     * Retrieves the data of the professed class under the main class group. The
     * "main" group is determined by the setting in the config.
     *
     * @return main professed class data or null if not professed for the main group
     */
    public PlayerClass getMainClass()
    {
        String main = SkillAPI.getSettings().getMainGroup();
        if (classes.containsKey(main))
        {
            return classes.get(main);
        }
        else if (classes.size() > 0)
        {
            return classes.values().toArray(new PlayerClass[classes.size()])[0];
        }
        else
        {
            return null;
        }
    }

    /**
     * Sets the professed class for the player for the corresponding group. This
     * will not save any skills, experience, or levels of the previous class if
     * there was any. The new class will start at level 1 with 0 experience.
     *
     * @param rpgClass class to assign to the player
     *
     * @return the player-specific data for the new class
     */
    public PlayerClass setClass(RPGClass rpgClass)
    {
        PlayerClass c = classes.remove(rpgClass.getGroup());
        if (c != null)
        {
            for (Skill skill : c.getData().getSkills())
            {
                skills.remove(skill.getName().toLowerCase());
                combos.removeSkill(skill);
            }
        }
        else attribPoints += rpgClass.getGroupSettings().getStartingAttribs();

        PlayerClass classData = new PlayerClass(this, rpgClass);
        classes.put(rpgClass.getGroup(), classData);

        // Add in missing skills
        for (Skill skill : rpgClass.getSkills())
        {
            giveSkill(skill, classData);
        }

        updateHealthAndMana(getPlayer());
        updateScoreboard();
        return classes.get(rpgClass.getGroup());
    }

    /**
     * Checks whether or not the player is professed as the class
     * without checking child classes.
     *
     * @param rpgClass class to check
     *
     * @return true if professed as the specific class, false otherwise
     */
    public boolean isExactClass(RPGClass rpgClass)
    {
        if (rpgClass == null) return false;
        PlayerClass c = classes.get(rpgClass.getGroup());
        return (c != null) && (c.getData() == rpgClass);
    }

    /**
     * Checks whether or not the player is professed as the class
     * or any of its children.
     *
     * @param rpgClass class to check
     *
     * @return true if professed as the class or one of its children, false otherwise
     */
    public boolean isClass(RPGClass rpgClass)
    {
        if (rpgClass == null)
        {
            return false;
        }

        PlayerClass pc = classes.get(rpgClass.getGroup());
        if (pc == null) return false;

        RPGClass temp = pc.getData();
        while (temp != null)
        {
            if (temp == rpgClass)
            {
                return true;
            }
            temp = temp.getParent();
        }

        return false;
    }

    /**
     * Checks whether or not the player can profess into the given class. This
     * checks to make sure the player is currently professed as the parent of the
     * given class and is high enough of a level to do so.
     *
     * @param rpgClass class to check
     *
     * @return true if can profess, false otherwise
     */
    public boolean canProfess(RPGClass rpgClass)
    {
        if (rpgClass.isNeedsPermission())
        {
            Player p = getPlayer();
            if (p == null || (!p.hasPermission(Permissions.CLASS) && !p.hasPermission(Permissions.CLASS + "." + rpgClass.getName().toLowerCase().replace(" ", "-"))))
            {
                return false;
            }
        }
        if (classes.containsKey(rpgClass.getGroup()))
        {
            PlayerClass current = classes.get(rpgClass.getGroup());
            return rpgClass.getParent() == current.getData() && current.getData().getMaxLevel() <= current.getLevel();
        }
        else
        {
            return !rpgClass.hasParent();
        }
    }

    /**
     * Resets the class data for the owner under the given group. This will remove
     * the profession entirely, leaving no remaining data until the player professes
     * again to a starting class.
     *
     * @param group group to reset
     */
    public void reset(String group)
    {
        GroupSettings settings = SkillAPI.getSettings().getGroupSettings(group);
        if (!settings.canReset())
            return;

        PlayerClass playerClass = classes.remove(group);
        if (playerClass != null)
        {
            // Remove skills
            RPGClass data = playerClass.getData();
            for (Skill skill : data.getSkills())
            {
                PlayerSkill ps = skills.remove(skill.getName().toLowerCase());
                if (ps != null && ps.isUnlocked() && ps.getData() instanceof PassiveSkill)
                    ((PassiveSkill) ps.getData()).stopEffects(getPlayer(), ps.getLevel());
                combos.removeSkill(skill);
            }

            // Update GUI features
            updateScoreboard();

            // Call the event
            Bukkit.getPluginManager().callEvent(new PlayerClassChangeEvent(playerClass, data, null));
        }

        // Restore default class if applicable
        RPGClass rpgClass = settings.getDefault();
        if (rpgClass != null && settings.getPermission() == null)
        {
            setClass(rpgClass);
        }
        binds.clear();
        resetAttribs();
    }

    /**
     * Resets all profession data for the player. This clears all professions the player
     * has, leaving no remaining data until the player professes again to a starting class.
     */
    public void resetAll()
    {
        ArrayList<String> keys = new ArrayList<String>(classes.keySet());
        for (String key : keys)
            reset(key);
    }

    /**
     * Resets attributes for the player
     */
    public void resetAttribs()
    {
        attributes.clear();
        attribPoints = 0;
        for (PlayerClass c : classes.values())
        {
            GroupSettings s = c.getData().getGroupSettings();
            attribPoints += s.getStartingAttribs() + s.getAttribsForLevels(c.getLevel(), 1);
        }
        AttributeListener.updatePlayer(this);
        updateHealthAndMana(player.getPlayer());
    }

    /**
     * Professes the player into the class if they are able to. This will
     * reset the class data if the group options are set to reset upon
     * profession. Otherwise, all skills, experience, and levels of the
     * current class under the group will be retained and carried over into
     * the new profession.
     *
     * @param rpgClass class to profess into
     *
     * @return true if successfully professed, false otherwise
     */
    public boolean profess(RPGClass rpgClass)
    {
        if (rpgClass != null && canProfess(rpgClass))
        {
            final PlayerClass previousData = classes.get(rpgClass.getGroup());
            final RPGClass previous = previousData == null ? null : previousData.getData();

            // Pre-class change event in case someone wants to stop it
            final PlayerPreClassChangeEvent event = new PlayerPreClassChangeEvent(this, previousData, previous, rpgClass);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return false;
            }

            // Reset data if applicable
            final boolean isResetting = SkillAPI.getSettings().getGroupSettings(rpgClass.getGroup()).isProfessReset();
            if (isResetting)
            {
                reset(rpgClass.getGroup());
            }

            // Inherit previous class data if any
            final PlayerClass current;
            if (previousData == null || isResetting)
            {
                current = new PlayerClass(this, rpgClass);
                classes.put(rpgClass.getGroup(), current);
                attribPoints += rpgClass.getGroupSettings().getStartingAttribs();
            }
            else
            {
                current = previousData;
                previousData.setClassData(rpgClass);
            }

            // Add skills
            for (Skill skill : rpgClass.getSkills())
            {
                if (!skills.containsKey(skill.getKey()))
                {
                    skills.put(skill.getKey(), new PlayerSkill(this, skill, current));
                    combos.addSkill(skill);
                }
            }

            Bukkit.getPluginManager().callEvent(new PlayerClassChangeEvent(current, previous, current.getData()));
            resetAttribs();
            updateScoreboard();
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Gives experience to the player from the given source
     *
     * @param amount amount of experience to give
     * @param source source of the experience
     */
    public void giveExp(double amount, ExpSource source)
    {
        for (PlayerClass playerClass : classes.values())
        {
            playerClass.giveExp(amount, source);
        }
    }

    /**
     * Causes the player to lose experience as a penalty (generally for dying)
     */
    public void loseExp()
    {
        for (PlayerClass playerClass : classes.values())
        {
            double penalty = playerClass.getData().getGroupSettings().getDeathPenalty();
            if (penalty > 0)
            {
                playerClass.loseExp(penalty);
            }
        }
    }

    /**
     * Gives levels to the player for all classes matching the experience source
     *
     * @param amount amount of levels to give
     * @param source source of the levels
     */
    public boolean giveLevels(int amount, ExpSource source)
    {
        boolean success = false;
        for (PlayerClass playerClass : classes.values()) {
            RPGClass data = playerClass.getData();
            if (data.receivesExp(source)) {
                success = true;
                playerClass.giveLevels(amount);
            }
        }
        updateHealthAndMana(getPlayer());
        return success;
    }

    /**
     * Gives skill points to the player for all classes matching the experience source
     *
     * @param amount amount of levels to give
     * @param source source of the levels
     */
    public void givePoints(int amount, ExpSource source)
    {
        for (PlayerClass playerClass : classes.values())
        {
            if (playerClass.getData().receivesExp(source))
            {
                playerClass.givePoints(amount);
            }
        }
    }

    ///////////////////////////////////////////////////////
    //                                                   //
    //                  Health and Mana                  //
    //                                                   //
    ///////////////////////////////////////////////////////

    /**
     * Updates the player's max health and mana using class data.
     *
     * @param player player to update the health and mana for
     */
    public void updateHealthAndMana(Player player)
    {
        if (player == null) {
            return;
        }

        // Update maxes
        double health = bonusHealth;
        maxMana = bonusMana;
        for (PlayerClass c : classes.values()) {
            health += c.getHealth();
            maxMana += c.getMana();
        }
        if (health == bonusHealth) {
            health += SkillAPI.getSettings().getDefaultHealth();
        }
        if (health <= 0) {
            health = SkillAPI.getSettings().getDefaultHealth();
        }
        if (SkillAPI.getSettings().isModifyHealth()) { player.setMaxHealth(health); }
        mana = Math.min(mana, maxMana);

        // Health scaling is available starting with 1.6.2
        if (SkillAPI.getSettings().isOldHealth()) {
            player.setHealthScaled(true);
            player.setHealthScale(20);
        } else {
            player.setHealthScaled(false);
        }
    }

    /**
     * Gives max health to the player. This does not carry over to other accounts
     * and will reset when SkillAPI is disabled. This does however carry over through
     * death and professions. This will accept negative values.
     *
     * @param amount amount of bonus health to give
     */
    public void addMaxHealth(double amount)
    {
        bonusHealth += amount;
        final Player player = getPlayer();
        if (player != null) {
            if (VersionManager.isVersionAtLeast(VersionManager.V1_9_0)) {
                final AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                attribute.setBaseValue(attribute.getBaseValue() + amount);
            } else {
                final double newHealth = player.getMaxHealth() + amount;
                player.setMaxHealth(newHealth);
                if (player.getMaxHealth() > newHealth) {
                    player.setMaxHealth(newHealth * 2 - player.getMaxHealth());
                }
            }
        }
    }

    /**
     * Gives max mana to the player. This does not carry over to other accounts
     * and will reset when SkillAPI is disabled. This does however carry over through
     * death and professions. This will accept negative values.
     *
     * @param amount amount of bonus mana to give
     */
    public void addMaxMana(double amount)
    {
        bonusMana += amount;
        maxMana += amount;
        mana += amount;
    }

    /**
     * Retrieves the amount of mana the player currently has.
     *
     * @return current player mana
     */
    public double getMana()
    {
        return mana;
    }

    /**
     * Checks whether or not the player has at least the specified amount of mana
     *
     * @param amount required mana amount
     *
     * @return true if has the amount of mana, false otherwise
     */
    public boolean hasMana(double amount)
    {
        return mana >= amount;
    }

    /**
     * Retrieves the max amount of mana the player can have including bonus mana
     *
     * @return max amount of mana the player can have
     */
    public double getMaxMana()
    {
        return maxMana;
    }

    /**
     * Regenerates mana for the player based on the regen amounts of professed classes
     */
    public void regenMana()
    {
        double amount = 0;
        for (PlayerClass c : classes.values())
        {
            if (c.getData().hasManaRegen())
            {
                amount += c.getData().getManaRegen();
            }
        }
        if (amount > 0)
        {
            giveMana(amount, ManaSource.REGEN);
        }
    }

    /**
     * Sets the player's amount of mana without launching events
     *
     * @param amount current mana
     */
    public void setMana(double amount)
    {
        this.mana = amount;
    }

    /**
     * Gives mana to the player from an unknown source. This will not
     * cause the player's mana to go above their max amount.
     *
     * @param amount amount of mana to give
     */
    public void giveMana(double amount)
    {
        giveMana(amount, ManaSource.SPECIAL);
    }

    /**
     * Gives mana to the player from the given mana source. This will not
     * cause the player's mana to go above the max amount.
     *
     * @param amount amount of mana to give
     * @param source source of the mana
     */
    public void giveMana(double amount, ManaSource source)
    {
        PlayerManaGainEvent event = new PlayerManaGainEvent(this, amount, source);
        Bukkit.getPluginManager().callEvent(event);

        if (!event.isCancelled())
        {
            Logger.log(LogType.MANA, 2, getPlayerName() + " gained " + amount + " mana due to " + event.getSource().name());

            mana += event.getAmount();
            if (mana > maxMana)
            {
                mana = maxMana;
            }
            if (mana < 0)
            {
                mana = 0;
            }
        }
        else Logger.log(LogType.MANA, 2, getPlayerName() + " had their mana gain cancelled");
    }

    /**
     * Takes mana away from the player for an unknown reason. This will not
     * cause the player to fall below 0 mana.
     *
     * @param amount amount of mana to take away
     */
    public void useMana(double amount)
    {
        useMana(amount, ManaCost.SPECIAL);
    }

    /**
     * Takes mana away from the player for the specified reason. This will not
     * cause the player to fall below 0 mana.
     *
     * @param amount amount of mana to take away
     * @param cost   source of the mana cost
     */
    public void useMana(double amount, ManaCost cost)
    {
        PlayerManaLossEvent event = new PlayerManaLossEvent(this, amount, cost);
        Bukkit.getPluginManager().callEvent(event);

        if (!event.isCancelled())
        {
            Logger.log(LogType.MANA, 2, getPlayerName() + " used " + amount + " mana due to " + event.getSource().name());

            mana -= event.getAmount();
            if (mana < 0)
            {
                mana = 0;
            }
        }
    }

    /**
     * Clears bonus health/mana
     */
    public void clearBonuses()
    {
        bonusMana = 0;
        bonusHealth = 0;
        bonusAttrib.clear();
        InventoryTask.clear(player.getUniqueId());
    }

    ///////////////////////////////////////////////////////
    //                                                   //
    //                   Skill Binding                   //
    //                                                   //
    ///////////////////////////////////////////////////////

    /**
     * Retrieves a skill the player has bound by material
     *
     * @param mat material to get the bind for
     *
     * @return skill bound to the material or null if none are bound
     */
    public PlayerSkill getBoundSkill(Material mat)
    {
        return binds.get(mat);
    }

    /**
     * Retrieves the bound data for the player. Modifying this map will
     * modify the bindings the player has.
     *
     * @return the skill binds data for the player
     */
    public HashMap<Material, PlayerSkill> getBinds()
    {
        return binds;
    }

    /**
     * Checks whether or not the material has a skill bound to it
     *
     * @param mat material to check
     *
     * @return true if a skill is bound to it, false otherwise
     */
    public boolean isBound(Material mat)
    {
        return binds.containsKey(mat);
    }

    /**
     * Binds a skill to a material for the player. The bind will not work if the skill
     * was already bound to the material.
     *
     * @param mat   material to bind the skill to
     * @param skill skill to bind to the material
     *
     * @return true if was able to bind the skill, false otherwise
     */
    public boolean bind(Material mat, PlayerSkill skill)
    {
        // Special cases
        if (mat == null || (skill != null && skill.getPlayerData() != this))
        {
            return false;
        }

        PlayerSkill bound = getBoundSkill(mat);
        if (bound != skill)
        {
            // Apply the binding
            if (skill == null)
            {
                binds.remove(mat);
            }
            else
            {
                binds.put(mat, skill);
            }

            // Update the old skill's bind
            if (bound != null)
            {
                bound.setBind(null);
            }

            // Update the new skill's bind
            if (skill != null)
            {
                skill.setBind(mat);
            }

            return true;
        }

        // The skill was already bound
        else
        {
            return false;
        }
    }

    /**
     * Clears a skill binding on the material. If there is no binding on the
     * material, this will do nothing.
     *
     * @param mat material to clear bindings from
     *
     * @return true if a binding was cleared, false otherwise
     */
    public boolean clearBind(Material mat)
    {
        return binds.remove(mat) != null;
    }

    /**
     * Clears the skill binding for the given skill. This will remove the bindings
     * on all materials involving the skill.
     *
     * @param skill skill to unbind
     */
    public void clearBinds(Skill skill)
    {
        ArrayList<Material> keys = new ArrayList<Material>(binds.keySet());
        for (Material key : keys)
        {
            PlayerSkill bound = binds.get(key);
            if (bound.getData() == skill)
            {
                binds.remove(key);
            }
        }
    }

    /**
     * Clears all binds the player currently has
     */
    public void clearAllBinds()
    {
        binds.clear();
    }

    ///////////////////////////////////////////////////////
    //                                                   //
    //                     Functions                     //
    //                                                   //
    ///////////////////////////////////////////////////////

    /**
     * Records any data to save with class data
     *
     * @param player player to record for
     */
    public void record(Player player)
    {
        this.lastHealth = player.getHealth();
    }

    /**
     * Updates the scoreboard with the player's current class.
     * This is already done by the API and doesn't need to be
     * done by other plugins.
     */
    public void updateScoreboard()
    {
        if (SkillAPI.getSettings().isShowScoreboard())
            SkillAPI.schedule(new ScoreboardTask(this), 2);
    }

    /**
     * Starts passive abilities for the player if they are online. This is
     * already called by the API and shouldn't be called by other plugins.
     *
     * @param player player to set the passive skills up for
     */
    public void startPassives(Player player)
    {
        if (player == null)
        {
            return;
        }
        passive = true;
        for (PlayerSkill skill : skills.values())
        {
            if (skill.isUnlocked() && (skill.getData() instanceof PassiveSkill))
            {
                ((PassiveSkill) skill.getData()).initialize(player, skill.getLevel());
            }
        }
    }

    /**
     * Stops passive abilities for the player if they are online. This is already
     * called by the API and shouldn't be called by other plugins.
     *
     * @param player player to stop the passive skills for
     */
    public void stopPassives(Player player)
    {
        if (player == null)
        {
            return;
        }
        passive = false;
        for (PlayerSkill skill : skills.values())
        {
            if (skill.isUnlocked() && (skill.getData() instanceof PassiveSkill))
            {
                ((PassiveSkill) skill.getData()).stopEffects(player, skill.getLevel());
            }
        }
    }

    /**
     * Casts a skill by name for the player. In order to cast the skill,
     * the player must be online, have the skill unlocked, have enough mana,
     * have the skill off cooldown, and have a proper target if applicable.
     *
     * @param skillName name of the skill ot cast
     *
     * @return true if successfully cast the skill, false otherwise
     */
    public boolean cast(String skillName)
    {
        return cast(skills.get(skillName.toLowerCase()));
    }

    /**
     * Casts a skill for the player. In order to cast the skill,
     * the player must be online, have the skill unlocked, have enough mana,
     * have the skill off cooldown, and have a proper target if applicable.
     *
     * @param skill skill to cast
     *
     * @return true if successfully cast the skill, false otherwise
     */
    public boolean cast(PlayerSkill skill)
    {
        // Invalid skill
        if (skill == null)
            throw new IllegalArgumentException("Skill cannot be null");

        int level = skill.getLevel();

        // Not unlocked or on cooldown
        if (!check(skill, true, true))
            return false;

        // Dead players can't cast skills
        Player p = getPlayer();
        if (p.isDead())
            return PlayerSkillCastFailedEvent.invoke(skill, CASTER_DEAD);

        // Skill Shots
        if (skill.getData() instanceof SkillShot)
        {
            PlayerCastSkillEvent event = new PlayerCastSkillEvent(this, skill, p);
            Bukkit.getPluginManager().callEvent(event);

            // Make sure it isn't cancelled
            if (!event.isCancelled())
            {
                try
                {
                    if (((SkillShot) skill.getData()).cast(p, level))
                    {
                        skill.startCooldown();
                        if (SkillAPI.getSettings().isShowSkillMessages())
                        {
                            skill.getData().sendMessage(p, SkillAPI.getSettings().getMessageRadius());
                        }
                        if (SkillAPI.getSettings().isManaEnabled())
                        {
                            useMana(event.getManaCost(), ManaCost.SKILL_CAST);
                        }
                        return true;
                    } else return PlayerSkillCastFailedEvent.invoke(skill, EFFECT_FAILED);
                }
                catch (Exception ex)
                {
                    Logger.bug("Failed to cast skill - " + skill.getData().getName() + ": Internal skill error");
                    ex.printStackTrace();
                    return PlayerSkillCastFailedEvent.invoke(skill, EFFECT_FAILED);
                }
            } else return PlayerSkillCastFailedEvent.invoke(skill, CANCELED);
        }

        // Target Skills
        else if (skill.getData() instanceof TargetSkill)
        {
            LivingEntity target = TargetHelper.getLivingTarget(p, skill.getData().getRange(level));

            // Must have a target
            if (target == null)
                return PlayerSkillCastFailedEvent.invoke(skill, NO_TARGET);

            PlayerCastSkillEvent event = new PlayerCastSkillEvent(this, skill, p);
            Bukkit.getPluginManager().callEvent(event);

            // Make sure it isn't cancelled
            if (!event.isCancelled())
            {
                try
                {
                    if (((TargetSkill) skill.getData()).cast(p, target, level, !SkillAPI.getSettings().canAttack(p, target)))
                    {
                        skill.startCooldown();
                        if (SkillAPI.getSettings().isShowSkillMessages())
                        {
                            skill.getData().sendMessage(p, SkillAPI.getSettings().getMessageRadius());
                        }
                        if (SkillAPI.getSettings().isManaEnabled())
                        {
                            useMana(event.getManaCost(), ManaCost.SKILL_CAST);
                        }
                        return true;
                    } else return PlayerSkillCastFailedEvent.invoke(skill, EFFECT_FAILED);
                }
                catch (Exception ex)
                {
                    Logger.bug("Failed to cast skill - " + skill.getData().getName() + ": Internal skill error");
                    ex.printStackTrace();
                    return PlayerSkillCastFailedEvent.invoke(skill, EFFECT_FAILED);
                }
            } else PlayerSkillCastFailedEvent.invoke(skill, CANCELED);
        }

        return false;
    }

    /**
     * Checks the cooldown and mana requirements for a skill
     *
     * @param skill    skill to check for
     * @param cooldown whether or not to check cooldowns
     * @param mana     whether or not to check mana requirements
     *
     * @return true if can use
     */
    public boolean check(PlayerSkill skill, boolean cooldown, boolean mana)
    {
        if (skill == null)
            return false;

        SkillStatus status = skill.getStatus();
        int level = skill.getLevel();
        double cost = skill.getData().getManaCost(level);

        // Not unlocked
        if (level <= 0) {
            return PlayerSkillCastFailedEvent.invoke(skill, NOT_UNLOCKED);
        }

        // On Cooldown
        if (status == SkillStatus.ON_COOLDOWN && cooldown)
        {
            SkillAPI.getLanguage().sendMessage(
                ErrorNodes.COOLDOWN,
                getPlayer(),
                FilterType.COLOR,
                RPGFilter.COOLDOWN.setReplacement(skill.getCooldown() + ""),
                RPGFilter.SKILL.setReplacement(skill.getData().getName())
            );
            return PlayerSkillCastFailedEvent.invoke(skill, ON_COOLDOWN);
        }

        // Not enough mana
        else if (status == SkillStatus.MISSING_MANA && mana)
        {
            SkillAPI.getLanguage().sendMessage(
                ErrorNodes.MANA,
                getPlayer(),
                FilterType.COLOR,
                RPGFilter.SKILL.setReplacement(skill.getData().getName()),
                RPGFilter.MANA.setReplacement(getMana() + ""),
                RPGFilter.COST.setReplacement((int) Math.ceil(cost) + ""),
                RPGFilter.MISSING.setReplacement((int) Math.ceil(cost - getMana()) + "")
            );
            return PlayerSkillCastFailedEvent.invoke(skill, NO_MANA);
        }

        else return true;
    }

    /**
     * Initializes the application of the data for the player
     *
     * @param player player to set up for
     */
    public void init(Player player)
    {
        if (!SkillAPI.getSettings().isWorldEnabled(player.getWorld()))
            return;

        AttributeListener.updatePlayer(this);
        this.updateHealthAndMana(player);
        InventoryTask.check(player);
        if (this.getLastHealth() > 0 && !player.isDead())
            player.setHealth(Math.min(this.getLastHealth(), player.getMaxHealth()));
        this.startPassives(player);
        this.updateScoreboard();
    }
}
