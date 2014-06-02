package com.sucy.skill.example.ranger.active;

import com.sucy.skill.api.skill.ClassSkill;
import com.sucy.skill.api.skill.SkillAttribute;
import com.sucy.skill.api.skill.SkillShot;
import com.sucy.skill.api.skill.SkillType;
import com.sucy.skill.example.ClassListener;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * Fires a spread of arrows
 */
public class FireArrow extends ClassSkill implements SkillShot {

    public static final String NAME = "Fire Arrow";
    private static final String
        DAMAGE = "Damage (%)";

    public FireArrow() {
        super(NAME, SkillType.SKILL_SHOT, Material.FIRE, 4);

        description.add("Fires a flaming arrow");
        description.add("that deals additional");
        description.add("damage and sets the");
        description.add("target on fire.");

        setAttribute(SkillAttribute.LEVEL, 1, 1);
        setAttribute(SkillAttribute.COST, 1, 0);
        setAttribute(SkillAttribute.COOLDOWN, 6, 0);
        setAttribute(SkillAttribute.MANA, 20, 0);

        setAttribute(DAMAGE, 120, 20);
    }

    @Override
    public boolean cast(Player player, int level) {

        if (player.getInventory().contains(Material.ARROW, 1)) {

            player.getInventory().removeItem(new ItemStack(Material.ARROW, 1));
            Arrow arrow = player.launchProjectile(Arrow.class);
            arrow.setMetadata(ClassListener.DAMAGE_KEY, new FixedMetadataValue(api, getAttribute(DAMAGE, level)));
            arrow.setFireTicks(200);

            return true;
        }

        else return false;
    }
}
