package com.sucy.skill.mechanic;

import com.sucy.skill.api.PlayerSkills;
import com.sucy.skill.api.dynamic.DynamicSkill;
import com.sucy.skill.api.dynamic.EmbedData;
import com.sucy.skill.api.dynamic.IMechanic;
import com.sucy.skill.api.dynamic.Target;
import com.sucy.skill.api.event.ParticleProjectileHitEvent;
import com.sucy.skill.api.util.effects.ParticleType;
import com.sucy.skill.api.util.effects.ProjectileHelper;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Mechanic for giving mana to targets
 */
public class ParticleProjectileMechanic implements IMechanic, Listener {

    private static final String
            PARTICLE = "Projectile Particle",
            DATA = "Projectile Data",
            DAMAGE = "Projectile Damage",
            SPEED = "Projectile Speed",
            ANGLE = "Spread Angle",
            SPREAD = "Spread Type",
            QUANTITY = "Projectile Quantity";

    private final HashMap<Integer, EmbedData> projectiles = new HashMap<Integer, EmbedData>();

    /**
     * Constructor
     */
    public ParticleProjectileMechanic() {
        Bukkit.getPluginManager().registerEvents(this, Bukkit.getPluginManager().getPlugin("SkillAPI"));
    }

    /**
     * Launches projectiles from a source
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
        int level = data.getSkillLevel(skill.getName());
        double speed = skill.getAttribute(SPEED, target, level);
        double damage = skill.getAttribute(DAMAGE, target, level);
        int amount = (int)skill.getAttribute(QUANTITY, target, level);
        int angle = (int)skill.getAttribute(ANGLE, target, level);
        int spread = skill.getValue(SPREAD);
        int value = skill.getValue(DATA);
        int particleID = skill.getValue(PARTICLE);
        ParticleType type;
        if (PARTICLES.containsKey(particleID)) type = PARTICLES.get(particleID);
        else type = ParticleType.SMOKE;

        EmbedData embed = new EmbedData(player, data, skill);
        if (spread == 0) ProjectileHelper.launchHorizontalCircle(player, type, value, amount, angle, speed, damage, embed);
        else ProjectileHelper.launchCircle(player, type, value, amount, angle, speed, damage, embed);

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
        skill.checkDefault(prefix + SPEED, 3, 1);
        skill.checkDefault(prefix + QUANTITY, 1, 0);
        skill.checkDefault(prefix + ANGLE, 30, 0);
        skill.checkDefault(prefix + DAMAGE, 5, 2);
        if (!skill.isSet(SPREAD)) skill.setValue(SPREAD, 0);
        if (!skill.isSet(PARTICLE)) skill.setValue(PARTICLE, 0);
        if (!skill.isSet(DATA)) skill.setValue(DATA, 0);
    }

    /**
     * @return names of all attributes used by this mechanic
     */
    @Override
    public String[] getAttributeNames() {
        return new String[] { SPEED, QUANTITY, ANGLE, DAMAGE };
    }

    private static final HashMap<Integer, ParticleType> PARTICLES = new HashMap<Integer, ParticleType>() {{
        put(0, ParticleType.SMOKE);
        put(1, ParticleType.ENDER_SIGNAL);
        put(2, ParticleType.MOBSPAWNER_FLAMES);
        put(3, ParticleType.POTION_BREAK);
        put(4, ParticleType.OTHER);
    }};
}
