package com.sucy.skill.example.ranger.active;

import com.sucy.skill.api.skill.ClassSkill;
import com.sucy.skill.api.skill.SkillAttribute;
import com.sucy.skill.api.skill.SkillShot;
import com.sucy.skill.api.skill.SkillType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Fires a spread of arrows
 */
public class Fletching extends ClassSkill implements SkillShot {

    public static final String NAME = "Fletching";
    private static final String
            ARROWS = "Arrows";

    public Fletching() {
        super(NAME, SkillType.SELF, Material.FLINT, 5);

        description.add("Creates a few arrows");
        description.add("arrows out of the nearby");
        description.add("materials lying around.");

        setAttribute(SkillAttribute.LEVEL, 1, 1);
        setAttribute(SkillAttribute.COST, 1, 0);
        setAttribute(SkillAttribute.COOLDOWN, 15, 0);
        setAttribute(SkillAttribute.MANA, 20, 0);

        setAttribute(ARROWS, 2, 2);
    }

    @Override
    public boolean cast(Player player, int level) {
        player.getInventory().addItem(new ItemStack(Material.ARROW, (int)getAttribute(ARROWS, level)));
        return true;
    }
}
