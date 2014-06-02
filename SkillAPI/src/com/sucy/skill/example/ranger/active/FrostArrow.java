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
public class FrostArrow extends ClassSkill implements SkillShot {

    public static final String NAME = "Frost Arrow";
    private static final String
        DAMAGE = "Damage (%)",
        DURATION = "Slow Duration";

    public FrostArrow() {
        super(NAME, SkillType.SKILL_SHOT, Material.ICE, 4);

        description.add("Fires a freezing arrow");
        description.add("that deals moderate");
        description.add("damage and slows the");
        description.add("enemy's movement.");

        setAttribute(SkillAttribute.LEVEL, 1, 1);
        setAttribute(SkillAttribute.COST, 1, 0);
        setAttribute(SkillAttribute.COOLDOWN, 4, 0);
        setAttribute(SkillAttribute.MANA, 15, 0);

        setAttribute(DAMAGE, 80, 20);
        setAttribute(DURATION, 1.5, 0.5);
    }

    @Override
    public boolean cast(Player player, int level) {

        if (player.getInventory().contains(Material.ARROW, 1)) {

            player.getInventory().removeItem(new ItemStack(Material.ARROW, 1));
            Arrow arrow = player.launchProjectile(Arrow.class);
            arrow.setMetadata(ClassListener.DAMAGE_KEY, new FixedMetadataValue(api, getAttribute(DAMAGE, level)));
            arrow.setMetadata(ClassListener.SLOW_KEY, new FixedMetadataValue(api, getAttribute(DURATION, level)));

            return true;
        }
        else return false;
    }
}
