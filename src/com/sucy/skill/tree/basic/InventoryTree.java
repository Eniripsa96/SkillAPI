/**
 * SkillAPI
 * com.sucy.skill.tree.basic.InventoryTree
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
package com.sucy.skill.tree.basic;

import com.rit.sucy.config.Filter;
import com.rit.sucy.config.FilterType;
import com.rit.sucy.items.InventoryManager;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.classes.RPGClass;
import com.sucy.skill.api.exception.SkillTreeException;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.player.PlayerSkill;
import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.data.Permissions;
import com.sucy.skill.language.GUINodes;
import com.sucy.skill.language.RPGFilter;
import com.sucy.skill.tree.SkillTree;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * A skill tree manager for classes
 */
public abstract class InventoryTree extends SkillTree {
    public static final String INVENTORY_KEY = "SAPI_ST";

    protected final HashMap<Integer, Skill> skillSlots = new HashMap<Integer, Skill>();

    protected int height;

    /**
     * Constructor
     *
     * @param api api reference
     */
    public InventoryTree(SkillAPI api, RPGClass tree) {
        super(api, tree);
    }

    /**
     * Shows the player the skill tree so they can
     * view/manage their skills.
     *
     * @param player player to show
     */
    public void show(Player player) {
        player.openInventory(getInventory(SkillAPI.getPlayerData(player)));
    }

    /**
     * Generates a new skill tree inventory
     *
     * @param player player data to show
     *
     * @return skill tree inventory
     */
    public Inventory getInventory(PlayerData player) {
        Inventory inv = InventoryManager.createInventory(
                INVENTORY_KEY,
                height,
                SkillAPI.getLanguage().getMessage(
                        GUINodes.SKILL_TREE,
                        true,
                        FilterType.COLOR,
                        RPGFilter.CLASS.setReplacement(tree.getName()),
                        Filter.PLAYER.setReplacement(player.getPlayerName())
                ).get(0)
        );
        Player p = player.getPlayer();

        for (Map.Entry<Integer, Skill> entry : skillSlots.entrySet()) {
            final PlayerSkill skill = player.getSkill(entry.getValue().getName());
            if (canShow(p, entry.getValue()) && skill != null) {
                inv.setItem(entry.getKey(), entry.getValue().getIndicator(skill));
            }
        }

        return inv;
    }

    /**
     * Checks a click for actions
     *
     * @param slot slot that was clicked
     *
     * @return whether or not the click should be cancelled (when it was a skill or link)
     */
    public boolean checkClick(int slot) {
        return skillSlots.containsKey(slot);
    }

    /**
     * Checks if the slot points to a skill
     *
     * @param slot slot to check
     *
     * @return true if a skill, false otherwise
     */
    public boolean isSkill(HumanEntity player, int slot) {
        return skillSlots.get(slot) != null &&
                player != null &&
                (!skillSlots.get(slot).needsPermission() ||
                        player.hasPermission(Permissions.SKILL) ||
                        player.hasPermission(Permissions.SKILL + "." + skillSlots.get(slot)
                                .getName()
                                .toLowerCase()
                                .replace(" ", "-")));
    }

    /**
     * Gets the skill attached to a slot
     *
     * @param slot slot to retrieve for
     *
     * @return skill for the slot
     */
    public Skill getSkill(int slot) {
        return skillSlots.get(slot);
    }

    /**
     * Retrieves the entire map of occupied skill slots for the skill tree
     *
     * @return map of occupied skill slots
     */
    public HashMap<Integer, Skill> getSkillSlots() {
        return skillSlots;
    }

    /**
     * Arranges the skill tree
     *
     * @throws SkillTreeException
     */
    @Override
    public void arrange() throws SkillTreeException {
        super.arrange();

        // Cannot be higher than 6
        if (height > 6) {
            throw new SkillTreeException("Error generating the skill tree: " + tree.getName() + " - too large of a tree!");
        }
    }

    /**
     * Updates a skill in the view
     *
     * @param player player
     */
    public void update(PlayerData player) {
        InventoryView view = player.getPlayer().getOpenInventory();
        for (Map.Entry<Integer, Skill> skills : skillSlots.entrySet()) {
            view.setItem(skills.getKey(), skills.getValue().getIndicator(player.getSkill(skills.getValue().getName())));
        }
    }

    /**
     * Checks if the class has the skill registered
     *
     * @param skill skill to check
     *
     * @return true if registered, false otherwise
     */
    @Override
    public boolean hasSkill(Skill skill) {
        return skillSlots.containsValue(skill);
    }

    /**
     * Comparator for skills for most trees
     */
    protected static final Comparator<Skill> comparator = new Comparator<Skill>() {

        /**
         * Compares skills based on their stats for skill tree arrangement
         *  -> Skills with no prerequisite skills come first
         *  -> Then its skills with lower level requirements
         *  -> Then its skills with lower costs
         *  -> Then its skills alphabetically
         *
         * @param skill1 skill being compared
         * @param skill2 skill to compare to
         * @return      -1, 0, or 1
         */
        @Override
        public int compare(Skill skill1, Skill skill2) {
            return skill1.getSkillReq() != null && skill2.getSkillReq() == null ? 1
                    : skill1.getSkillReq() == null && skill2.getSkillReq() != null ? -1
                    : skill1.getLevelReq(0) > skill2.getLevelReq(0) ? 1
                    : skill1.getLevelReq(0) < skill2.getLevelReq(0) ? -1
                    : skill1.getCost(0) > skill2.getCost(0) ? 1
                    : skill1.getCost(0) < skill2.getCost(0) ? -1
                    : skill1.getName().compareTo(skill2.getName());
        }
    };
}
