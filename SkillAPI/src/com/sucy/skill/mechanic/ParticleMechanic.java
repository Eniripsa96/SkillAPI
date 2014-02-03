package com.sucy.skill.mechanic;

import com.sucy.skill.api.PlayerSkills;
import com.sucy.skill.api.dynamic.DynamicSkill;
import com.sucy.skill.api.dynamic.IMechanic;
import com.sucy.skill.api.dynamic.Target;
import com.sucy.skill.api.util.effects.Direction;
import com.sucy.skill.api.util.effects.ParticleHelper;
import com.sucy.skill.api.util.effects.ParticleType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

/**
 * Mechanic for damaging targets based on missing mana
 */
public class ParticleMechanic implements IMechanic {

    private static final String
            AREA = "Particle Area",
            PARTICLE = "Particle",
            PARTICLE_DATA = "Particle Data",
            AMOUNT_BASE = "Particle Amount Base",
            AMOUNT_BONUS = "Particle Amount Bonus",
            RADIUS_BASE = "Particle Radius Base",
            RADIUS_BONUS = "Particle Radius Bonus",
            HEIGHT = "Particle Height";

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

        // Requires a target
        if (targets.size() == 0) return false;

        // Get values
        int level = data.getSkillLevel(skill.getName());
        int amount = skill.getValue(AMOUNT_BASE) + skill.getValue(AMOUNT_BONUS) * (level - 1);
        int radius = skill.getValue(RADIUS_BASE) + skill.getValue(RADIUS_BONUS) * (level - 1);
        int particle = skill.getValue(PARTICLE);
        int area = skill.getValue(AREA);
        int height = skill.getValue(HEIGHT);
        ParticleType type;
        int value = skill.getValue(PARTICLE_DATA);
        if (PARTICLES.containsKey(particle)) type = PARTICLES.get(particle);
        else type = PARTICLES.get(0);

        // Play the particle effect on the targets
        for (LivingEntity entity : targets) {

            // Sphere
            if (area == 1) {
                ParticleHelper.fillSphere(entity.getLocation().add(0, height, 0), type, value, radius, amount);
            }

            // Hemisphere
            else if (area == 2) {
                ParticleHelper.fillHemisphere(entity.getLocation().add(0, height, 0), type, value, radius, amount);
            }

            // Circle
            else {
                ParticleHelper.fillCircle(entity.getLocation().add(0, height, 0), type, value, radius, amount, Direction.XZ);
            }
        }

        return true;
    }

    /**
     * Sets default attributes for the skill
     *
     * @param skill  skill to apply to
     * @param prefix prefix to add to the name
     */
    @Override
    public void applyDefaults(DynamicSkill skill, String prefix) {
        if (!skill.isSet(AREA)) skill.setValue(AREA, 0);
        if (!skill.isSet(PARTICLE)) skill.setValue(PARTICLE, 0);
        if (!skill.isSet(PARTICLE_DATA)) skill.setValue(PARTICLE_DATA, 0);
        if (!skill.isSet(AMOUNT_BASE)) skill.setValue(AMOUNT_BASE, 20);
        if (!skill.isSet(AMOUNT_BONUS)) skill.setValue(AMOUNT_BONUS, 0);
        if (!skill.isSet(RADIUS_BASE)) skill.setValue(RADIUS_BASE, 8);
        if (!skill.isSet(RADIUS_BONUS)) skill.setValue(RADIUS_BONUS, 0);
        if (!skill.isSet(HEIGHT)) skill.setValue(HEIGHT, 0);
    }

    /**
     * @return names of all attributes used by this mechanic
     */
    @Override
    public String[] getAttributeNames() {
        return new String[0];
    }

    private static final HashMap<Integer, ParticleType> PARTICLES = new HashMap<Integer, ParticleType>() {{
        put(0, ParticleType.SMOKE);
        put(1, ParticleType.ENDER_SIGNAL);
        put(2, ParticleType.MOBSPAWNER_FLAMES);
        put(3, ParticleType.POTION_BREAK);
        put(4, ParticleType.OTHER);
        put(5, ParticleType.ENTITY);
    }};
}
