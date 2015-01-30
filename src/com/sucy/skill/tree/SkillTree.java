package com.sucy.skill.tree;

import com.rit.sucy.items.InventoryManager;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.classes.RPGClass;
import com.sucy.skill.api.exception.SkillTreeException;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.data.Permissions;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.*;

/**
 * A skill tree manager for classes
 */
public abstract class SkillTree
{
    public static final String INVENTORY_KEY = "SAPI_ST";

    protected final HashMap<Integer, Skill> skillSlots = new HashMap<Integer, Skill>();
    protected final SkillAPI api;
    protected final RPGClass tree;
    protected       int      height;

    /**
     * Constructor
     *
     * @param api api reference
     */
    public SkillTree(SkillAPI api, RPGClass tree)
    {
        this.api = api;
        this.tree = tree;
    }

    /**
     * Generates a new skill tree inventory
     *
     * @param player player data to show
     *
     * @return skill tree inventory
     */
    public Inventory getInventory(PlayerData player)
    {
        Inventory inv = InventoryManager.createInventory(INVENTORY_KEY, height, tree.getName());
        Player p = player.getPlayer();

        for (Map.Entry<Integer, Skill> entry : skillSlots.entrySet())
        {
            if (!entry.getValue().needsPermission() || p.hasPermission(Permissions.SKILL + "." + entry.getValue().getName().toLowerCase().replaceAll(" ", "-")))
            {
                inv.setItem(entry.getKey(), entry.getValue().getIndicator(player.getSkill(entry.getValue().getName())));
            }
        }

        return inv;
    }

    /**
     * Arranges the skill tree
     *
     * @throws SkillTreeException
     */
    public void arrange() throws SkillTreeException
    {

        // Get included skills
        ArrayList<Skill> skills = new ArrayList<Skill>();
        for (Skill skill : tree.getSkills())
        {
            if (!SkillAPI.isSkillRegistered(skill))
            {
                api.getLogger().severe("Failed to add skill to tree - " + skill + ": Skill does not exist");
                continue;
            }
            if (SkillAPI.getSettings().isShowingAutoSkills() || skill.getCost(0) != 0 || skill.getMaxLevel() > 1)
            {
                skills.add(skill);
            }
        }

        // Arrange the skills
        arrange(skills);

        // Cannot be higher than 6
        if (height > 6)
        {
            throw new SkillTreeException("Error generating the skill tree: " + tree.getName() + " - too large of a tree!");
        }
    }

    /**
     * Arranges the skill tree
     *
     * @param skills skills to arrange
     */
    protected abstract void arrange(List<Skill> skills) throws SkillTreeException;

    /**
     * Updates a skill in the view
     *
     * @param view   inventory view
     * @param player player
     */
    public void update(Inventory view, PlayerData player)
    {
        for (Map.Entry<Integer, Skill> skills : skillSlots.entrySet())
        {
            view.setItem(skills.getKey(), skills.getValue().getIndicator(player.getSkill(skills.getValue().getName())));
        }
    }

    /**
     * Checks a click for actions
     *
     * @param slot slot that was clicked
     *
     * @return whether or not the click should be cancelled (when it was a skill or link)
     */
    public boolean checkClick(int slot)
    {
        return skillSlots.containsKey(slot);
    }

    /**
     * Checks if the slot points to a skill
     *
     * @param slot slot to check
     *
     * @return true if a skill, false otherwise
     */
    public boolean isSkill(HumanEntity player, int slot)
    {
        return skillSlots.containsKey(slot) &&
                !skillSlots.get(slot).needsPermission() ||
                player.hasPermission(Permissions.SKILL) ||
                player.hasPermission(Permissions.SKILL + "." + skillSlots.get(slot).getName().toLowerCase().replace(" ", "-"));
    }

    /**
     * Gets the skill attached to a slot
     *
     * @param slot slot to retrieve for
     *
     * @return skill for the slot
     */
    public Skill getSkill(int slot)
    {
        return skillSlots.get(slot);
    }

    /**
     * Checks if the class has the skill registered
     *
     * @param skill skill to check
     *
     * @return true if registered, false otherwise
     */
    public boolean hasSkill(Skill skill)
    {
        return skillSlots.containsValue(skill);
    }

    /**
     * Comparator for skills for most trees
     */
    protected static final Comparator<Skill> comparator = new Comparator<Skill>()
    {

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
        public int compare(Skill skill1, Skill skill2)
        {

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
