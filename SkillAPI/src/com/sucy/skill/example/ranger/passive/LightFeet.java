package com.sucy.skill.example.ranger.passive;

import com.sucy.skill.api.skill.ClassSkill;
import com.sucy.skill.api.skill.PassiveSkill;
import com.sucy.skill.api.skill.SkillAttribute;
import com.sucy.skill.api.skill.SkillType;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Passive that causes arrows to travel faster
 */
public class LightFeet extends ClassSkill implements PassiveSkill {

    public static final String NAME = "LightFeet";
    private static final String
            SPEED = "Speed (%)";

    private static final float NORMAL_SPEED = 0.2f;

    public LightFeet() {
        super(NAME, SkillType.PASSIVE, Material.GOLD_BOOTS, 4);

        description.add("Improves your running");
        description.add("proficiency, increasing");
        description.add("your movement speed.");

        setAttribute(SkillAttribute.LEVEL, 1, 1);
        setAttribute(SkillAttribute.COST, 1, 0);

        setAttribute(SPEED, 110, 5);
    }

    @Override
    public void onUpgrade(Player player, int level) {
        onInitialize(player, level);
    }

    @Override
    public void onInitialize(Player player, int level) {
        player.setWalkSpeed((float) (NORMAL_SPEED * getAttribute(SPEED, level) / 100));
    }

    @Override
    public void stopEffects(Player player, int level) {
        player.setWalkSpeed(NORMAL_SPEED);
    }
}
