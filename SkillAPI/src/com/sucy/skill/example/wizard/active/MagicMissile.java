package com.sucy.skill.example.wizard.active;

import com.sucy.skill.api.skill.ClassSkill;
import com.sucy.skill.api.skill.SkillAttribute;
import com.sucy.skill.api.skill.SkillShot;
import com.sucy.skill.api.skill.SkillType;
import com.sucy.skill.api.util.effects.ParticleProjectile;
import com.sucy.skill.api.util.effects.ParticleType;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class MagicMissile extends ClassSkill implements SkillShot {

    public static final String NAME = "Magic Missile";
    private static final String
            DAMAGE = "Damage";

    public MagicMissile() {
        super(NAME, SkillType.SKILL_SHOT, Material.SNOW_BALL, 5);

        description.add("Fires a small missle");
        description.add("that deals minor damage");

        setAttribute(SkillAttribute.LEVEL, 1, 1);
        setAttribute(SkillAttribute.COST, 1, 0);
        setAttribute(SkillAttribute.COOLDOWN, 1, -0.1);
        setAttribute(SkillAttribute.MANA, 7, -1);

        setAttribute(DAMAGE, 6, 1);
    }

    @Override
    public boolean cast(Player player, int level) {
        ParticleProjectile.launch(player, 3, ParticleType.OTHER, 34, getAttribute(DAMAGE, level));
        return true;
    }
}
