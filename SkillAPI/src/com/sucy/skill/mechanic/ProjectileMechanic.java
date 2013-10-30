package com.sucy.skill.mechanic;

import com.sucy.skill.api.dynamic.DynamicSkill;
import com.sucy.skill.api.dynamic.EmbedData;
import com.sucy.skill.api.dynamic.IMechanic;
import com.sucy.skill.api.dynamic.Target;
import com.sucy.skill.api.PlayerSkills;
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
     * Gives mana to all targets
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
        list = launchHorizontalCircle(player, projectile, amount, angle, speed);

        for (int id : list) {
            projectiles.put(id, new EmbedData(player, data, skill));
        }

        return worked;
    }

    private List<Integer> launchHorizontalCircle(Player source, Class<? extends Projectile> projectileType, int amount, int angle, int speed) {
        List<Integer> list = new ArrayList<Integer>();

        Vector vel = source.getLocation().getDirection();
        vel.setY(0);
        vel.multiply(speed / vel.length());

        // Fire one straight ahead if odd
        if (amount % 2 == 1) {
            Projectile projectile = source.launchProjectile(projectileType);
            projectile.setVelocity(vel);
            list.add(projectile.getEntityId());
        }

        double a = (double)angle / (amount - 1);
        for (int i = 0; i < (amount - 1) / 2; i++) {
            for (int j = -1; j <= 1; j += 2) {
                double b = angle / 2 * j - a * i * j;
                Projectile projectile = source.launchProjectile(projectileType);
                Vector v = vel.clone();
                double cos = Math.cos(b * Math.PI / 180);
                double sin = Math.sin(b * Math.PI / 180);
                v.setX(v.getX() * cos - v.getZ() * sin);
                v.setZ(v.getX() * sin + v.getZ() * cos);
                projectile.setVelocity(v);
                list.add(projectile.getEntityId());
            }
        }

        return list;
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
            projectiles.get(event.getEntity().getEntityId()).resolveNonTarget(event.getEntity().getLocation());
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
            projectiles.get(event.getDamager().getEntityId()).resolveTarget((LivingEntity)event.getEntity());
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
