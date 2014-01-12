package com.sucy.skill.mechanic;

import com.sucy.skill.api.PlayerSkills;
import com.sucy.skill.api.Status;
import com.sucy.skill.api.dynamic.DynamicSkill;
import com.sucy.skill.api.dynamic.IMechanic;
import com.sucy.skill.api.dynamic.Target;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.List;

/**
 * Mechanic for applying status effects to all targets
 */
public class StatusMechanic implements IMechanic {

    private static final String
            TYPE = "Status",
            LENGTH = "Length";

    /**
     * Applies a status to all targets
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

        // Get attributes
        int level = data.getSkillLevel(skill.getName());
        Status status = STATUSES.get(skill.getValue(TYPE));
        double duration = skill.getAttribute(LENGTH, target, level);

        // Apply  potion effect to all
        boolean worked = false;
        for (LivingEntity t : targets) {
            if (t instanceof Player) {
                data.getAPI().getPlayer(((Player) t).getName()).applyStatus(status, duration * 1000);
                worked = true;
            }
            else if (status == Status.ROOT || status == Status.STUN) {
                t.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, (int)(duration * 20)));
                worked = true;
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
        skill.checkDefault(prefix + LENGTH, 3, 1);
        if (!skill.isSet(TYPE) || !STATUSES.containsKey(skill.getValue(TYPE))) {
            skill.setValue(TYPE, 0);
        }
    }

    /**
     * @return names of all attributes used by this mechanic
     */
    @Override
    public String[] getAttributeNames() {
        return new String[] { LENGTH };
    }

    private static final HashMap<Integer, Status> STATUSES = new HashMap<Integer, Status>() {{
        put(0, Status.STUN);
        put(1, Status.ROOT);
        put(2, Status.SILENCE);
        put(3, Status.DISARM);
        put(4, Status.CURSE);
        put(5, Status.ABSORB);
        put(6, Status.INVINCIBLE);
    }};
}
