/**
 * SkillAPI
 * com.sucy.skill.data.PlayerEquips
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Steven Sucy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
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
package com.sucy.skill.data;

import com.google.common.base.Objects;
import com.rit.sucy.config.parse.NumberParser;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.classes.RPGClass;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.skills.Skill;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.sucy.skill.listener.ItemListener.ARMOR_TYPES;

/**
 * Handles keeping track of and applying attribute
 * bonuses and requirements for items.
 */
public class PlayerEquips
{
    private static final ItemStack TEMP = new ItemStack(Material.BEDROCK);

    private PlayerData player;

    private EquipData empty  = new EquipData();
    private EquipData weapon = empty;
    private EquipData[] other;

    private int offhand = -1;

    /**
     * @param player player data reference
     */
    public PlayerEquips(PlayerData player)
    {
        this.player = player;
        other = new EquipData[SkillAPI.getSettings().getSlots().length];
        for (int i = 0; i < other.length; i++) {
            other[i] = empty;
            if (SkillAPI.getSettings().getSlots()[i] == 40)
                offhand = i;
        }
    }

    /**
     * @return true if the player can hit something, false otherwise
     */
    public boolean canHit()
    {
        return weapon.isMet();
    }

    public boolean canBlock()
    {
        return offhand >= 0 && other[offhand].isMet();
    }

    /**
     * Updates all available items for the player
     *
     * @param player player to update for
     */
    public void update(Player player)
    {
        PlayerInventory inv = player.getInventory();
        weapon = swap(inv, inv.getHeldItemSlot(), weapon, true);
        for (int i = 0; i < other.length; i++)
            other[i] = swap(inv, SkillAPI.getSettings().getSlots()[i], other[i], i == offhand);
    }

    /**
     * Handles swapping two items, handling any requirements
     *
     * @param inv   inventory to manage
     * @param index related index
     * @param from  old equip data
     *
     * @return the used equip data
     */
    private EquipData swap(PlayerInventory inv, int index, EquipData from, boolean weapon)
    {
        EquipData to = make(inv.getItem(index));
        if (Objects.equal(from.item, to.item))
        {
            return to;
        }

        if (from.isMet() && (!weapon || !from.isArmor)) {
            from.revert();
        }

        if (weapon && to.isArmor) {
            return to;
        }
        else if (!to.isMet())
        {
            if (SkillAPI.getSettings().isDropWeapon() || !weapon) {
                inv.setItem(index, TEMP);
                for (ItemStack item : inv.addItem(to.item).values())
                    inv.getHolder().getWorld().dropItemNaturally(inv.getHolder().getLocation(), item);
                inv.setItem(index, null);
                return empty;
            }
            return to;
        }
        else
        {
            to.apply();
            return to;
        }
    }

    private boolean isArmor(final ItemStack item) {
        return item != null && ARMOR_TYPES.contains(item.getType());
    }

    /**
     * Clears the weapon slot
     */
    public void clearWeapon()
    {
        if (weapon.isMet() && !weapon.isArmor) {
            weapon.revert();
        }
        weapon = empty;
    }

    /**
     * Updates the equipped weapon
     *
     * @param inv inventory data
     */
    public void updateWeapon(PlayerInventory inv)
    {
        weapon = swap(inv, inv.getHeldItemSlot(), weapon, true);
    }

    /**
     * Makes data for the ItemStack if needed
     *
     * @param item item to make for
     *
     * @return item data
     */
    private EquipData make(ItemStack item)
    {
        if (item == null)
            return empty;
        else
            return new EquipData(item);
    }

    /**
     * Represents one available item's data
     */
    private class EquipData
    {
        private HashMap<String, Integer> skillReq;
        private HashMap<String, Integer> attrReq;
        private HashMap<String, Integer> attribs;

        private HashSet<String> classReq;
        private HashSet<String> classExc;

        private ItemStack item;
        private int       levelReq;
        private boolean   isArmor;

        /**
         * Sets up for an empty item slot
         */
        EquipData() { }

        /**
         * Scans an items for bonuses or requirements
         *
         * @param item item to grab data from
         */
        EquipData(ItemStack item)
        {
            this.item = item;
            this.isArmor = PlayerEquips.this.isArmor(item);

            if (!item.hasItemMeta())
                return;

            List<String> lore = item.getItemMeta().getLore();
            if (lore == null)
                return;

            Settings settings = SkillAPI.getSettings();
            String classText = settings.getLoreClassText();
            String excludeText = settings.getLoreExcludeText();
            String levelText = settings.getLoreLevelText();
            boolean skills = settings.isCheckSkillLore();
            boolean attributes = settings.isAttributesEnabled();

            for (String line : lore)
            {
                String lower = ChatColor.stripColor(line).toLowerCase();

                // Level requirements
                if (lower.startsWith(levelText)) {
                    levelReq = NumberParser.parseInt(lower.substring(levelText.length()));
                }

                // Class requirements
                else if (lower.startsWith(classText)) {
                    List<String> required = Arrays.asList(lower.substring(classText.length()).split(", "));
                    if (classReq == null)
                        classReq = new HashSet<>();
                    classReq.addAll(required);
                }

                // Excluded classes
                else if (lower.startsWith(excludeText)) {
                    List<String> excluded = Arrays.asList(lower.substring(excludeText.length()).split(", "));
                    if (classExc == null)
                        classExc = new HashSet<>();
                    classExc.addAll(excluded);
                }
                    
                else
                {
                    boolean done = false;

                    // Skill requirements
                    if (skills)
                    {
                        for (Skill skill : SkillAPI.getSkills().values())
                        {
                            String text = settings.getSkillText(skill.getName());
                            if (lower.startsWith(text))
                            {
                                done = true;
                                if (skillReq == null)
                                    skillReq = new HashMap<>();

                                skillReq.put(skill.getName(), NumberParser.parseInt(lower.substring(text.length())));
                                break;
                            }
                        }
                    }

                    // Attribute requirements
                    if (attributes && !done)
                    {
                        for (String attr : SkillAPI.getAttributeManager().getLookupKeys())
                        {
                            String text = settings.getAttrReqText(attr);
                            if (lower.startsWith(text))
                            {
                                if (attrReq == null)
                                    attrReq = new HashMap<>();

                                String normalized = SkillAPI.getAttributeManager().normalize(attr);
                                attrReq.put(normalized, NumberParser.parseInt(lower.substring(text.length())));
                                break;
                            }

                            text = settings.getAttrGiveText(attr);
                            if (lower.startsWith(text))
                            {
                                if (attribs == null)
                                    attribs = new HashMap<>();

                                String normalized = SkillAPI.getAttributeManager().normalize(attr);
                                int current = attribs.containsKey(attr) ? attribs.get(attr) : 0;
                                int extra = NumberParser.parseInt(lower.substring(text.length()).replace("%", ""));
                                attribs.put(normalized, current + extra);
                                break;
                            }
                        }
                    }
                }
            }
        }

        /**
         * Applies bonuse attributes for the item
         */
        public void apply()
        {
            if (attribs != null)
                for (Map.Entry<String, Integer> entry : attribs.entrySet())
                    player.addBonusAttributes(entry.getKey(), entry.getValue());
        }

        /**
         * Reverts bonus attributes for the item
         */
        void revert()
        {
            if (attribs != null)
                for (Map.Entry<String, Integer> entry : attribs.entrySet())
                    player.addBonusAttributes(entry.getKey(), -entry.getValue());
        }

        public boolean isArmor() {
            return isArmor;
        }

        /**
         * Checks for conditions of an item
         *
         * @return true if conditions are met
         */
        boolean isMet()
        {
            if (item == null) {
                return true;
            }

            PlayerClass main = player.getMainClass();
            String className = main == null ? "null" : main.getData().getName().toLowerCase();
            if ((levelReq > 0 && (main == null || main.getLevel() < levelReq))
                || (classExc != null && main != null && classExc.contains(className))
                || (classReq != null && (main == null || !classReq.contains(className))))
                return false;

            if (classExc != null)
                for (PlayerClass playerClass : player.getClasses())
                    if (matches(classExc, playerClass))
                        return false;

            if (classReq != null) {
                boolean metClassReq = false;
                for (PlayerClass playerClass : player.getClasses())
                    if (matches(classReq, playerClass))
                        metClassReq = true;

                if (!metClassReq)
                    return false;
            }


            for (PlayerClass playerClass : player.getClasses())
                if (!playerClass.getData().canUse(item.getType()))
                    return false;

            if (skillReq != null)
                for (Map.Entry<String, Integer> entry : skillReq.entrySet())
                    if (player.getSkillLevel(entry.getKey()) < entry.getValue())
                        return false;

            if (attrReq != null)
                for (Map.Entry<String, Integer> entry : attrReq.entrySet())
                    if (player.getAttribute(entry.getKey()) < entry.getValue())
                        return false;

            return true;
        }

        private boolean matches(final Set<String> names, final PlayerClass playerClass) {
            if (playerClass == null) return false;

            RPGClass current = playerClass.getData();
            while (current != null) {
                if (names.contains(current.getName().toLowerCase())) {
                    return true;
                }
                current = current.getParent();
            }

            return false;
        }
    }
}
