package com.sucy.skill.example.bard.active;

import com.sucy.skill.api.skill.ClassSkill;
import com.sucy.skill.api.skill.SkillAttribute;
import com.sucy.skill.api.skill.SkillShot;
import com.sucy.skill.api.skill.SkillType;
import com.sucy.skill.api.util.effects.ParticleProjectile;
import com.sucy.skill.api.util.effects.ParticleType;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class Racket extends ClassSkill implements SkillShot {

    public static final String NAME = "Racket";
    private static final String
            DAMAGE = "Damage";

    public Racket() {
        super(NAME, SkillType.SKILL_SHOT, Material.FIREWORK, 5);

        description.add("Plays an awful note that");
        description.add("damages the first enemy");
        description.add("to be hit by the full");
        description.add("force. Poor soul...");

        setAttribute(SkillAttribute.LEVEL, 1, 1);
        setAttribute(SkillAttribute.COST, 1, 0);
        setAttribute(SkillAttribute.COOLDOWN, 5, -0.5);
        setAttribute(SkillAttribute.MANA, 15, 0);

        setAttribute(DAMAGE, 5, 1);
    }

    @Override
    public boolean cast(Player player, int level) {
        ParticleProjectile.launch(player, 3, ParticleType.OTHER, 23, getAttribute(DAMAGE, level));
        return true;
    }
}
