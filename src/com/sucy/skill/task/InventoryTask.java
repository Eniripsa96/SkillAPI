/**
 * SkillAPI
 * com.sucy.skill.task.InventoryTask
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
package com.sucy.skill.task;

import com.rit.sucy.config.FilterType;
import com.rit.sucy.version.VersionManager;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.language.ErrorNodes;
import com.sucy.skill.manager.AttributeManager;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Repeating task to check for equipment requirements
 */
public class InventoryTask extends BukkitRunnable
{
    private static InventoryTask instance;

    private static Pattern levelRegex;
    private static Pattern classRegex;
    private static Pattern excludeRegex;

    private static SkillAPI plugin;
    private        int      playersPerCheck;
    private int index = -1;

    private static HashMap<UUID, AttribBuffs> attribs     = new HashMap<>();
    private static HashMap<String, Integer>   tempAttribs = new HashMap<>();

    /**
     * Task constructor
     *
     * @param p               API reference
     * @param playersPerCheck how many players to check each tick
     */
    public InventoryTask(SkillAPI p, int playersPerCheck)
    {
        instance = this;

        this.playersPerCheck = playersPerCheck;
        if (plugin != null) return;
        plugin = p;
        runTaskTimer(plugin, 1, 1);

        levelRegex = Pattern.compile(SkillAPI.getSettings().getLoreLevelText() + "[0-9]+");
        classRegex = Pattern.compile(SkillAPI.getSettings().getLoreClassText() + ".+");
        excludeRegex = Pattern.compile(SkillAPI.getSettings().getLoreExcludeText() + ".+");
    }

    /**
     * Clears the plugin reference on cancel
     */
    @Override
    public void cancel()
    {
        super.cancel();
        plugin = null;
    }

    /**
     * Checks player equipment for requirements
     */
    @Override
    public void run()
    {
        Player[] players = VersionManager.getOnlinePlayers();
        for (int i = 0; i < playersPerCheck; i++)
        {
            if (!getNextPlayer(players)) return;
            if (i >= players.length) return;

            // Get the player data
            Player player = players[index];
            _check(player);
        }
    }

    /**
     * Checks a player for item requirements and stat bonuses
     *
     * @param player player to check
     */
    private void _check(Player player)
    {
        if (player.getGameMode() == GameMode.CREATIVE) return;
        PlayerData data = SkillAPI.getPlayerData(player);

        // Check for lore strings
        int index = 0;
        tempAttribs.clear();
        for (ItemStack item : player.getInventory().getArmorContents())
        {
            if (cannotUse(data, item))
                removeArmor(player, index);
            index++;
        }
        if (VersionManager.isVersionAtLeast(VersionManager.V1_9_0)
            && cannotUse(data, player.getInventory().getItemInOffHand()))
        {
            player.getInventory().addItem(player.getInventory().getItemInOffHand());
            player.getInventory().setItemInOffHand(null);
        }

        if (SkillAPI.getSettings().isDropWeapon())
        {
            if (cannotUse(data, player.getItemInHand()))
            {
                player.getWorld().dropItem(player.getLocation(), player.getItemInHand());
                player.setItemInHand(null);
            }
        }
        else if (SkillAPI.getSettings().isCheckAttributes())
            cannotUse(data, player.getItemInHand());

        // Give attributes
        if (SkillAPI.getSettings().isCheckAttributes())
        {
            if (!attribs.containsKey(player.getUniqueId()))
                attribs.put(player.getUniqueId(), new AttribBuffs());
            attribs.get(player.getUniqueId()).apply(data);
        }
    }

    /**
     * Checks a player for item requirements and stat bonuses
     *
     * @param player player to check
     */
    public static void check(Player player)
    {
        if (instance != null)
            instance._check(player);
    }

    /**
     * Removes attribute buff data for a player (should only be called by the API)
     *
     * @param playerId player UUID
     */
    public static void clear(UUID playerId)
    {
        attribs.remove(playerId);
    }

    /**
     * <p>Checks if the player cannot use the item</p>
     * <p>If SkillAPI is not enabled or it's lore requirement setting
     * is disabled, this will always return false</p>
     *
     * @param player player to check for
     * @param item   item to check
     *
     * @return true if cannot use, false otherwise
     */
    public static boolean cannotUse(PlayerData player, ItemStack item)
    {
        if (plugin == null) return false;
        if (item == null) return false;
        boolean hasRequirement = false;
        boolean needsRequirement = false;
        boolean skills = SkillAPI.getSettings().isCheckSkillLore();
        boolean attributes = SkillAPI.getSettings().isAttributesEnabled();
        if (item.hasItemMeta() && item.getItemMeta().hasLore())
        {
            List<String> lore = item.getItemMeta().getLore();
            HashMap<String, Integer> itemAttribs = new HashMap<>();

            // Check each line of the lore
            for (String line : lore)
            {
                String colorless = ChatColor.stripColor(line);
                String lower = colorless.toLowerCase();

                // Level requirements
                if (levelRegex.matcher(colorless).matches())
                {
                    int level = Integer.parseInt(colorless.substring(SkillAPI.getSettings().getLoreLevelText().length()));
                    if (!player.hasClass() || player.getMainClass().getLevel() < level)
                    {
                        return true;
                    }
                }

                // Class requirements
                else if (classRegex.matcher(colorless).matches())
                {
                    needsRequirement = true;
                    String name = colorless.substring(SkillAPI.getSettings().getLoreClassText().length());
                    if (name.contains(", "))
                    {
                        String[] names = name.split(", ");
                        for (String n : names)
                        {
                            if (player.isClass(SkillAPI.getClass(n)))
                            {
                                hasRequirement = true;
                            }
                        }
                    }
                    else
                    {
                        if (player.isClass(SkillAPI.getClass(name)))
                        {
                            hasRequirement = true;
                        }
                    }
                }

                // Class exclusion
                else if (excludeRegex.matcher(colorless).matches())
                {
                    String name = colorless.substring(SkillAPI.getSettings().getLoreExcludeText().length());
                    if (name.contains(", "))
                    {
                        String[] names = name.split(", ");
                        for (String n : names)
                        {
                            if (player.isClass(SkillAPI.getClass(n)))
                            {
                                return true;
                            }
                        }
                    }
                    else
                    {
                        if (player.isClass(SkillAPI.getClass(name)))
                        {
                            return true;
                        }
                    }
                }

                // Skill requirements
                else
                {
                    if (skills)
                    {
                        for (Skill skill : SkillAPI.getSkills().values())
                        {
                            String check = SkillAPI.getSettings().getSkillText().replace("{skill}", skill.getName()).toLowerCase();
                            if (lower.startsWith(check))
                            {
                                if (!player.hasSkill(skill.getName()))
                                    return true;

                                int level = Integer.parseInt(colorless.substring(check.length()));
                                if (player.getSkill(skill.getName()).getLevel() < level)
                                    return true;
                            }
                        }
                    }

                    // Attribute requirements
                    if (attributes)
                    {
                        for (String key : SkillAPI.getAttributeManager().getKeys())
                        {
                            AttributeManager.Attribute attr = SkillAPI.getAttributeManager().getAttribute(key);
                            String name = attr.getName();
                            String check = SkillAPI.getSettings().getAttrReqText().replace("{attr}", name).toLowerCase();
                            if (lower.startsWith(check))
                            {
                                int amount = Integer.parseInt(colorless.substring(check.length()));
                                if (player.getAttribute(attr.getKey()) < amount)
                                {
                                    return true;
                                }
                            }

                            if (SkillAPI.getSettings().isCheckAttributes())
                            {
                                check = SkillAPI.getSettings().getAttrGiveText().replace("{attr}", name).toLowerCase();
                                if (lower.startsWith(check))
                                {
                                    int amount = Integer.parseInt(colorless.substring(check.length()));
                                    if (itemAttribs.containsKey(attr.getKey()))
                                        itemAttribs.put(attr.getKey(), itemAttribs.get(attr.getKey()) + amount);
                                    else
                                        itemAttribs.put(attr.getKey(), amount);
                                }
                            }
                        }
                    }
                }
            }

            // Add attributes to the result afterwards so when the item isn't usable,
            // this part is skipped.
            if (needsRequirement == hasRequirement)
            {
                for (Map.Entry<String, Integer> entry : itemAttribs.entrySet())
                {
                    if (tempAttribs.containsKey(entry.getKey()))
                        tempAttribs.put(entry.getKey(), tempAttribs.get(entry.getKey()) + entry.getValue());
                    else
                        tempAttribs.put(entry.getKey(), entry.getValue());
                }
            }
        }
        return needsRequirement != hasRequirement;
    }

    /**
     * Removes the armor piece at the given index
     *
     * @param player player to remove for
     * @param index  index of the armor piece to remove
     */
    private void removeArmor(Player player, int index)
    {
        ItemStack[] armor = player.getInventory().getArmorContents();
        player.getInventory().addItem(armor[index]);
        armor[index] = null;
        player.getInventory().setArmorContents(armor);
        SkillAPI.getLanguage().sendMessage(ErrorNodes.CANNOT_USE, player, FilterType.COLOR);
    }

    /**
     * Gets the next player to check
     *
     * @return true if found a player, false otherwise
     */
    private boolean getNextPlayer(Player[] players)
    {
        index++;

        // Limit the index
        if (index >= players.length)
        {
            players = VersionManager.getOnlinePlayers();
            index = 0;
        }

        // Make sure its a valid player
        return players.length > 0 && (players[index].isOnline() || getNextPlayer(players));
    }

    private class AttribBuffs
    {
        private HashMap<String, Integer> attribs = new HashMap<>();

        public void apply(PlayerData data)
        {
            boolean dirty = false;
            for (Map.Entry<String, Integer> entry : attribs.entrySet())
            {
                if (!tempAttribs.containsKey(entry.getKey()))
                {
                    data.addBonusAttributes(entry.getKey(), -entry.getValue());
                    dirty = true;
                }
                else
                {
                    int dif = tempAttribs.get(entry.getKey()) - entry.getValue();
                    if (dif != 0)
                    {
                        data.addBonusAttributes(entry.getKey(), dif);
                        dirty = true;
                    }
                }
            }
            for (Map.Entry<String, Integer> entry : tempAttribs.entrySet())
            {
                if (!attribs.containsKey(entry.getKey()))
                {
                    data.addBonusAttributes(entry.getKey(), entry.getValue());
                    dirty = true;
                }
            }
            if (dirty)
                attribs = new HashMap<>(tempAttribs);
        }
    }
}
