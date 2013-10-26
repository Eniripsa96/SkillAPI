package com.sucy.skill.mechanic;

import com.sucy.skill.api.ClassAttribute;
import com.sucy.skill.api.CustomClass;
import com.sucy.skill.api.dynamic.DynamicSkill;
import com.sucy.skill.api.dynamic.IMechanic;
import com.sucy.skill.api.dynamic.Target;
import com.sucy.skill.api.PlayerSkills;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;

/**
 * Mechanic for damaging targets based on missing mana
 */
public class ManaDamageMechanic implements IMechanic {

    private static final String
            MANA = "Mana Percent",
            TYPE = "ManaType";

    /**
     * Damages targets based on missing mana
     *
     * @param player  player using the skill
     * @param data    data of the player using the skill
     * @param skill   skill being used
     * @param target  target type of the skill
     * @param targets targets for the effects
     * @return        true if there were targets, false otherwise
     */
    @Override
    public boolean resolve(Player player, PlayerSkills data, DynamicSkill skill, Target target, List<LivingEntity> targets) {

        // Change mana of all player targets
        boolean worked = false;
        int level = data.getSkillLevel(skill.getName());
        int amount = skill.getAttribute(MANA, target, level);
        int damageType = skill.getValue(TYPE);
        for (LivingEntity t : targets) {
            if (t instanceof Player) {
                PlayerSkills p = skill.getAPI().getPlayer(((Player) t).getName());
                if (!p.hasClass()) continue;
                CustomClass c = skill.getAPI().getClass(p.getClassName());
                int maxMana = c.getAttribute(ClassAttribute.MANA, p.getLevel());
                double damage;

                // Missing Mana
                if (damageType == 1) damage = amount * (maxMana - p.getMana()) / 100.0;

                // Mana
                else damage = amount * p.getMana() / 100.0;

                t.setLastDamageCause(new EntityDamageByEntityEvent(player, t, EntityDamageEvent.DamageCause.CUSTOM, damage));
                double prevHealth = t.getHealth();
                t.damage(damage);
                worked = worked || prevHealth != t.getHealth();
            }
        }

        return worked;
    }

    /**
     * Sets default attributes for the skill
     *
     * @param skill  skill to apply to
     * @param prefix prefix to add to the name
     */
    @Override
    public void applyDefaults(DynamicSkill skill, String prefix) {
        skill.checkDefault(prefix + MANA, 10, 5);
        if (!skill.isSet(TYPE)) skill.setValue(TYPE, 0);
    }

    /**
     * @return names of all attributes used by this mechanic
     */
    @Override
    public String[] getAttributeNames() {
        return new String[] { MANA };
    }
}
