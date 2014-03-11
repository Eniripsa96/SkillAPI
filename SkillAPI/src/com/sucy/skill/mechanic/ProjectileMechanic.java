package com.sucy.skill.mechanic;

import com.sucy.skill.api.PlayerSkills;
import com.sucy.skill.api.dynamic.DynamicSkill;
import com.sucy.skill.api.dynamic.EmbedData;
import com.sucy.skill.api.dynamic.IMechanic;
import com.sucy.skill.api.dynamic.Target;
import com.sucy.skill.api.util.effects.ProjectileHelper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Mechanic for giving mana to targets
 */
public class ProjectileMechanic implements IMechanic, Listener {

    private static final String
            META_KEY = "PM_KEY",
            PROJECTILE = "Projectile",
            SPEED = "Speed",
            ANGLE = "Angle",
            SPREAD = "Spread",
            QUANTITY = "Quantity",
            USE_PROJECTILE = "Use Arrow";

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
        int amount = (int)skill.getAttribute(QUANTITY, target, level);
        int angle = (int)skill.getAttribute(ANGLE, target, level);
        int spread = skill.getValue(SPREAD);
        int projectileId = skill.getValue(PROJECTILE);
        Class<? extends Projectile> projectile;
        if (PROJECTILES.containsKey(projectileId)) projectile = PROJECTILES.get(projectileId);
        else projectile = Arrow.class;

        // Using projectiles
        int removed = skill.getValue(USE_PROJECTILE);
        if (removed > 0) {
            Material mat;
            if (!MATERIALS.containsKey(projectileId)) mat = Material.ARROW;
            else mat = MATERIALS.get(projectileId);
            if (player.getInventory().contains(mat, removed)) {
                player.getInventory().removeItem(new ItemStack(mat, removed));
            }
            else return false;
        }

        // Firing the projectiles
        List<Projectile> list;
        if (spread == 0) list = ProjectileHelper.launchHorizontalCircle(player, projectile, amount, angle, speed);
        else list = ProjectileHelper.launchCircle(player, projectile, amount, angle, speed);

        // Applying embed data
        if (skill.hasEmbedEffects()) {
            for (Projectile p : list) {
                p.setMetadata(META_KEY, new FixedMetadataValue(skill.getAPI(), new EmbedData(player, data, skill)));
            }
        }

        return true;
    }

    /**
     * Non-target embedded effects
     *
     * @param event event details
     */
    @EventHandler
    public void onProjectileHit(final ProjectileHitEvent event) {
        if (event.getEntity().hasMetadata(META_KEY)) {
            EmbedData data = (EmbedData)event.getEntity().getMetadata(META_KEY).get(0).value();
            data.getSkill().beginUsage();
            data.resolveNonTarget(event.getEntity().getLocation());
            data.getSkill().stopUsage();
        }
    }

    /**
     * Target embedded effects
     *
     * @param event event details
     */
    @EventHandler
    public void onProjectileHit(EntityDamageByEntityEvent event) {
        if (event.getDamager().hasMetadata(META_KEY) && event.getEntity() instanceof LivingEntity) {
            EmbedData data = (EmbedData)event.getDamager().getMetadata(META_KEY).get(0).value();
            data.getSkill().beginUsage();
            data.resolveTarget((LivingEntity)event.getEntity());
            data.getSkill().stopUsage();
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
        if (!skill.isSet(PROJECTILE)) skill.setValue(PROJECTILE, 0);
        if (!skill.isSet(USE_PROJECTILE)) skill.setValue(USE_PROJECTILE, 0);
    }

    /**
     * @return names of all attributes used by this mechanic
     */
    @Override
    public String[] getAttributeNames() {
        return new String[] { SPEED, QUANTITY, ANGLE };
    }

    private static final HashMap<Integer, Class<? extends Projectile>> PROJECTILES = new LinkedHashMap<Integer, Class<? extends Projectile>>() {{
        put(0, Arrow.class);
        put(1, Snowball.class);
        put(2, Egg.class);
        put(3, SmallFireball.class);
        put(4, LargeFireball.class);
        put(5, WitherSkull.class);
    }};

    private static final HashMap<Integer, Material> MATERIALS = new LinkedHashMap<Integer, Material>() {{
        put(0, Material.ARROW);
        put(1, Material.SNOW_BALL);
        put(2, Material.EGG);
        put(3, Material.FIREBALL);
        put(4, Material.FIREBALL);
        put(5, Material.FIREBALL);
    }};
}
