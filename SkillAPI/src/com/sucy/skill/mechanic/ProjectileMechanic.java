package com.sucy.skill.mechanic;

import com.sucy.skill.api.PlayerSkills;
import com.sucy.skill.api.dynamic.DynamicSkill;
import com.sucy.skill.api.dynamic.EmbedData;
import com.sucy.skill.api.dynamic.IMechanic;
import com.sucy.skill.api.dynamic.Target;
import com.sucy.skill.api.util.effects.ProjectileHelper;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Mechanic for giving mana to targets
 */
public class ProjectileMechanic implements IMechanic, Listener {

    private static final String
            PROJECTILE = "Projectile",
            SPEED = "Speed",
            ANGLE = "Angle",
            SPREAD = "Spread",
            QUANTITY = "Quantity";

    private final HashMap<Integer, EmbedData> projectiles = new HashMap<Integer, EmbedData>();

    /**
     * Constructor
     */
    public ProjectileMechanic() {
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
        boolean worked = false;
        int level = data.getSkillLevel(skill.getName());
        int speed = skill.getAttribute(SPEED, target, level);
        int amount = skill.getAttribute(QUANTITY, target, level);
        int angle = skill.getAttribute(ANGLE, target, level);
        int spread = skill.getValue(SPREAD);
        int projectileId = skill.getValue(PROJECTILE);
        Class<? extends Projectile> projectile;
        if (PROJECTILES.containsKey(projectileId)) projectile = PROJECTILES.get(projectileId);
        else projectile = Arrow.class;

        List<Integer> list;
        if (spread == 0) list = ProjectileHelper.launchHorizontalCircle(player, projectile, amount, angle, speed);
        else list = ProjectileHelper.launchCircle(player, projectile, amount, angle, speed);

        for (int id : list) {
            projectiles.put(id, new EmbedData(player, data, skill));
        }

        return worked;
    }

    /**
     * Non-target embedded effects
     *
     * @param event event details
     */
    @EventHandler
    public void onProjectileHit(final ProjectileHitEvent event) {
        Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("SkillAPI"), new Runnable() {
            @Override
            public void run() {
                projectiles.remove(event.getEntity().getEntityId());
            }
        }, 1);
        if (projectiles.containsKey(event.getEntity().getEntityId())) {
            EmbedData data = projectiles.get(event.getEntity().getEntityId());
            data.getSkill().startEmbeddedEffects();
            data.resolveNonTarget(event.getEntity().getLocation());
            data.getSkill().stopEmbeddedEffects();
        }
    }

    /**
     * Target embedded effects
     *
     * @param event event details
     */
    @EventHandler
    public void onProjectileHit(EntityDamageByEntityEvent event) {
        if (projectiles.containsKey(event.getDamager().getEntityId()) && event.getEntity() instanceof LivingEntity) {
            EmbedData data = projectiles.get(event.getDamager().getEntityId());
            data.getSkill().startEmbeddedEffects();
            data.resolveTarget((LivingEntity)event.getEntity());
            data.getSkill().stopEmbeddedEffects();
        }
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
        if (!skill.isSet(SPREAD)) skill.setValue(SPREAD, 0);
    }

    /**
     * @return names of all attributes used by this mechanic
     */
    @Override
    public String[] getAttributeNames() {
        return new String[] { SPEED, QUANTITY };
    }

    private static final HashMap<Integer, Class<? extends Projectile>> PROJECTILES = new LinkedHashMap<Integer, Class<? extends Projectile>>() {{
        put(0, Arrow.class);
        put(1, Snowball.class);
        put(2, Egg.class);
        put(3, SmallFireball.class);
        put(4, LargeFireball.class);
        put(5, WitherSkull.class);
    }};
}
