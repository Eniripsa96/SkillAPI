package com.sucy.skill.tree;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.CustomClass;
import com.sucy.skill.api.PlayerSkills;
import com.sucy.skill.api.SkillTreeException;
import com.sucy.skill.api.skill.ClassSkill;
import com.sucy.skill.api.skill.SkillAttribute;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.*;

/**
 * A skill tree manager for classes
 */
public abstract class SkillTree implements InventoryHolder {

    protected final HashMap<Integer, ClassSkill> skillSlots = new HashMap<Integer, ClassSkill>();
    protected final SkillAPI api;
    protected final CustomClass tree;
    protected int height;

    /**
     * Constructor
     *
     * @param api api reference
     */
    public SkillTree(SkillAPI api, CustomClass tree) {
        this.api = api;
        this.tree = tree;
    }

    /**
     * Generates a new skill tree inventory
     *
     * @param skills skill level data
     * @return       skill tree inventory
     */
    public Inventory getInventory(PlayerSkills player, HashMap<String, Integer> skills) {
        Inventory inv = api.getServer().createInventory(this, height * 9, tree.getPrefix());

        for (Map.Entry<Integer, ClassSkill> entry : skillSlots.entrySet()) {
            inv.setItem(entry.getKey(), entry.getValue().getIndicator(player, player.getSkillLevel(entry.getValue().getName())));
        }

        return inv;
    }

    /**
     * Arranges the skill tree
     *
     * @throws com.sucy.skill.api.SkillTreeException
     */
    public void arrange() throws SkillTreeException {

        // Get included skills
        ArrayList<ClassSkill> skills = new ArrayList<ClassSkill>();
        for (String skill : tree.getSkills()) {
            if (!api.hasSkill(skill)) {
                api.getLogger().severe("Failed to add skill to tree - " + skill + ": Skill does not exist");
                continue;
            }
            skills.add(api.getSkill(skill));
        }

        // Arrange the skills
        arrange(skills);

        // Cannot be higher than 6
        if (height > 6) throw new SkillTreeException("Error generating the skill tree: " + tree.getName() + " - too large of a tree!");
    }

    /**
     * Arranges the skill tree
     *
     * @param skills skills to arrange
     */
    protected abstract void arrange(List<ClassSkill> skills) throws SkillTreeException;

    /**
     * Updates a skill in the view
     *
     * @param view   inventory view
     * @param player player
     */
    public void update(Inventory view, PlayerSkills player) {
        for (Map.Entry<Integer, ClassSkill> skills : skillSlots.entrySet()) {
            view.setItem(skills.getKey(), skills.getValue().getIndicator(player, player.getSkillLevel(skills.getValue().getName())));
        }
    }

    /**
     * Checks a click for actions
     *
     * @param slot slot that was clicked
     * @return     whether or not the click should be cancelled (when it was a skill or link)
     */
    public boolean checkClick(int slot) {
        return skillSlots.containsKey(slot);
    }

    /**
     * Checks if the slot points to a skill
     *
     * @param slot slot to check
     * @return     true if a skill, false otherwise
     */
    public boolean isSkill(int slot) {
        return skillSlots.containsKey(slot);
    }

    /**
     * Gets the skill attached to a slot
     *
     * @param slot slot to retrieve for
     * @return     skill for the slot
     */
    public ClassSkill getSkill(int slot) {
        return skillSlots.get(slot);
    }

    /**
     * Checks if the class has the skill registered
     *
     * @param skill skill to check
     * @return      true if registered, false otherwise
     */
    public boolean hasSkill(ClassSkill skill) {
        return skillSlots.containsValue(skill);
    }

    /**
     * Implemented method just to satisfy the interface
     *
     * @return null
     */
    @Override
    public Inventory getInventory() {
        return null;
    }

    /**
     * Comparator for skills for most trees
     */
    protected static final Comparator<ClassSkill> comparator = new Comparator<ClassSkill>() {

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
        public int compare(ClassSkill skill1, ClassSkill skill2) {

            return skill1.getSkillReq() != null && skill2.getSkillReq() == null ? 1
                    : skill1.getSkillReq() == null && skill2.getSkillReq() != null ? -1
                    : skill1.getBase(SkillAttribute.LEVEL) > skill2.getBase(SkillAttribute.LEVEL) ? 1
                    : skill1.getBase(SkillAttribute.LEVEL) < skill2.getBase(SkillAttribute.LEVEL) ? -1
                    : skill1.getBase(SkillAttribute.COST) > skill2.getBase(SkillAttribute.COST) ? 1
                    : skill1.getBase(SkillAttribute.COST) < skill2.getBase(SkillAttribute.COST) ? -1
                    : skill1.getName().compareTo(skill2.getName());
        }
    };
}
