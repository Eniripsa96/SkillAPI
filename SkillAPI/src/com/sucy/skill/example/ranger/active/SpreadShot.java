package com.sucy.skill.example.ranger.active;

import com.sucy.skill.api.skill.ClassSkill;
import com.sucy.skill.api.skill.SkillAttribute;
import com.sucy.skill.api.skill.SkillShot;
import com.sucy.skill.api.skill.SkillType;
import com.sucy.skill.api.util.effects.ProjectileHelper;
import com.sucy.skill.example.ClassListener;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.List;

/**
 * Fires a spread of arrows
 */
public class SpreadShot extends ClassSkill implements SkillShot {

    public static final String NAME = "Spread Shot";
    private static final String
        ARROWS = "Arrows",
        ANGLE = "Angle",
        DAMAGE = "Damage (%)";

    public SpreadShot() {
        super(NAME, SkillType.CONE, Material.BOW, 5);

        description.add("Fires multiple arrows in");
        description.add("a cone, dealing moderate");
        description.add("damage to enemies hit.");

        setAttribute(SkillAttribute.LEVEL, 1, 1);
        setAttribute(SkillAttribute.COST, 1, 0);
        setAttribute(SkillAttribute.COOLDOWN, 4, 0);
        setAttribute(SkillAttribute.MANA, 20, 0);

        setAttribute(ARROWS, 7, 0);
        setAttribute(ANGLE, 90, 0);
        setAttribute(DAMAGE, 80, 10);
    }

    @Override
    public boolean cast(Player player, int level) {

        if (player.getInventory().contains(Material.ARROW, 7)) {

            player.getInventory().removeItem(new ItemStack(Material.ARROW, 7));

            List<Projectile> arrows = ProjectileHelper.launchCircle(player, Arrow.class, (int) getAttribute(ARROWS, level), (int) getAttribute(ANGLE, level), 3);
            for (Projectile p : arrows) {
                p.setMetadata(ClassListener.DAMAGE_KEY, new FixedMetadataValue(api, getAttribute(DAMAGE, level)));
            }

            return true;
        }
        else return false;
    }
}
