package com.sucy.skill.example.ranger.active;

import com.rit.sucy.player.Protection;
import com.sucy.skill.api.Status;
import com.sucy.skill.api.skill.ClassSkill;
import com.sucy.skill.api.skill.SkillAttribute;
import com.sucy.skill.api.skill.SkillShot;
import com.sucy.skill.api.skill.SkillType;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Fires a spread of arrows
 */
public class SlipAway extends ClassSkill implements SkillShot {

    public static final String NAME = "Slip Away";
    private static final String
        RADIUS = "Radius",
        STUN_DURATION = "Stun Duration",
        SPEED_DURATION = "Buff Duration";

    public SlipAway() {
        super(NAME, SkillType.CONE, Material.PUMPKIN, 4);

        description.add("Stuns all nearby enemies");
        description.add("while giving yourself a");
        description.add("small speed buff.");

        setAttribute(SkillAttribute.LEVEL, 1, 1);
        setAttribute(SkillAttribute.COST, 1, 0);
        setAttribute(SkillAttribute.COOLDOWN, 7, 0);
        setAttribute(SkillAttribute.MANA, 15, 0);

        setAttribute(RADIUS, 3, 0.5);
        setAttribute(STUN_DURATION, 1.5, 0);
        setAttribute(SPEED_DURATION, 4, 0);
    }

    @Override
    public boolean cast(Player player, int level) {

        double radius = getAttribute(RADIUS, level);
        int stunDur = (int)(20 * getAttribute(STUN_DURATION, level));
        int speedDur = (int)(20 * getAttribute(SPEED_DURATION, level));
        for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
            if (entity instanceof Monster) {
                ((Monster) entity).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, stunDur, 100), true);
            }
            else if (entity instanceof Player) {
                Player p = (Player)entity;
                if (Protection.canAttack(player, p)) {
                    api.getStatusHolder(p).addStatus(Status.STUN, stunDur * 50);
                }
            }
        }
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, speedDur, 1));

        return true;
    }
}
