package com.sucy.skill.dynamic;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.enums.ManaCost;
import com.sucy.skill.api.event.PhysicalDamageEvent;
import com.sucy.skill.api.event.PlayerLandEvent;
import com.sucy.skill.api.event.SkillDamageEvent;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.player.PlayerSkill;
import com.sucy.skill.dynamic.executors.CrouchExecutor;
import com.sucy.skill.dynamic.executors.DeathExecutor;
import com.sucy.skill.dynamic.executors.EnvironmentExecutor;
import com.sucy.skill.dynamic.executors.KillExecutor;
import com.sucy.skill.dynamic.executors.LandExecutor;
import com.sucy.skill.dynamic.executors.LaunchExecutor;
import com.sucy.skill.dynamic.executors.PhysicalDealtExecutor;
import com.sucy.skill.dynamic.executors.PhysicalTakenExecutor;
import com.sucy.skill.dynamic.executors.SkillDealtExecutor;
import com.sucy.skill.dynamic.executors.SkillTakenExecutor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * SkillAPI © 2017
 * com.sucy.skill.dynamic.TriggerHandler
 */
public class TriggerHandler implements Listener {

    private final HashMap<Integer, Integer> active = new HashMap<Integer, Integer>();

    private final DynamicSkill    skill;
    private final Trigger         trigger;
    private final EffectComponent component;

    private boolean running;

    public TriggerHandler(final DynamicSkill skill, final Trigger trigger, final EffectComponent component) {
        this.skill = skill;
        this.trigger = trigger;
        this.component = component;
    }

    public Trigger getTrigger() {
        return trigger;
    }

    public EffectComponent getComponent() {
        return component;
    }

    public void init(final LivingEntity entity, final int level) {
        active.put(entity.getEntityId(), level);
    }

    public void cleanup(final LivingEntity entity) {
        active.remove(entity.getEntityId());
    }

    /**
     * Registers needed events for the skill, ignoring any unused events for efficiency
     *
     * @param plugin plugin reference
     */
    public void register(final SkillAPI plugin)
    {
        final PluginManager manager = plugin.getServer().getPluginManager();
        final EventPriority p = EventPriority.HIGHEST;

        switch (trigger) {
            case CROUCH:
                manager.registerEvent(PlayerToggleSneakEvent.class, this, p, CrouchExecutor.instance, plugin, true);
                break;
            case DEATH:
                manager.registerEvent(EntityDeathEvent.class, this, p, DeathExecutor.instance, plugin, true);
                break;
            case ENVIRONMENT_DAMAGE:
                manager.registerEvent(EntityDamageEvent.class, this, p, EnvironmentExecutor.instance, plugin, true);
                break;
            case KILL:
                manager.registerEvent(EntityDeathEvent.class, this, p, KillExecutor.instance, plugin, true);
                break;
            case LAND:
                manager.registerEvent(PlayerLandEvent.class, this, p, LandExecutor.instance, plugin, true);
                break;
            case LAUNCH:
                manager.registerEvent(ProjectileLaunchEvent.class, this, p, LaunchExecutor.instance, plugin, true);
                break;
            case PHYSICAL_DAMAGE:
                manager.registerEvent(PhysicalDamageEvent.class, this, p, PhysicalDealtExecutor.instance, plugin, true);
                break;
            case SKILL_DAMAGE:
                manager.registerEvent(SkillDamageEvent.class, this, p, SkillDealtExecutor.instance, plugin, true);
                break;
            case TOOK_PHYSICAL_DAMAGE:
                manager.registerEvent(PhysicalDamageEvent.class, this, p, PhysicalTakenExecutor.instance, plugin, true);
                break;
            case TOOK_SKILL_DAMAGE:
                manager.registerEvent(SkillDamageEvent.class, this, p, SkillTakenExecutor.instance, plugin, true);
                break;
        }
    }

    /**
     * Cancels firing projectiles when the launcher is stunned or disarmed.
     *
     * @param event event details
     */
    public void onLaunch(final ProjectileLaunchEvent event)
    {
        if (running) return;

        if (!(event.getEntity().getShooter() instanceof LivingEntity)) return;

        final LivingEntity shooter = (LivingEntity) event.getEntity().getShooter();
        if (!active.containsKey(shooter.getEntityId()))
            return;

        final String type = component.getSettings().getString("type", "any").toUpperCase().replace(" ", "_");
        final int level = active.get(shooter.getEntityId());
        if (active.containsKey(shooter.getEntityId())
                && (type.equals("ANY") || type.equals(event.getEntity().getType().name()))) {
            DynamicSkill.getCastData(shooter).put("api-velocity", event.getEntity().getVelocity().length());
            trigger(shooter, shooter, level);
            skill.applyCancelled(event);
        }
    }

    /**
     * Applies the death trigger effects
     *
     * @param event event details
     */
    public void onDeath(final EntityDeathEvent event)
    {
        if (running) return;
        if (!active.containsKey(event.getEntity().getEntityId())) return;

        final boolean killer = component.getSettings().getString("killer", "false").equalsIgnoreCase("true");
        if (!killer || event.getEntity().getKiller() != null) {
            trigger(event.getEntity(), killer ? event.getEntity().getKiller() : event.getEntity(), active.get(event.getEntity().getEntityId()));
        }
    }

    /**
     * Applies the kill trigger effects
     *
     * @param event event details
     */
    public void onKill(final EntityDeathEvent event)
    {
        // Kill trigger
        final Player player = event.getEntity().getKiller();
        if (player != null && active.containsKey(player.getEntityId())) {
            trigger(player, player, active.get(player.getEntityId()));
        }
    }

    /**
     * Environmental damage trigger
     *
     * @param event event details
     */
    public void onEnvironmental(final EntityDamageEvent event)
    {
        if (running) return;
        if (!(event.getEntity() instanceof LivingEntity)) return;

        final LivingEntity target = (LivingEntity) event.getEntity();
        if (!active.containsKey(target.getEntityId())) return;

        final String name = component.getSettings().getString("type", "").toUpperCase().replace(' ', '_');
        if (event.getCause().name().equals(name)) {
            DynamicSkill.getCastData(target).put("api-taken", event.getDamage());
            trigger(target, target, active.get(target.getEntityId()));
            skill.applyCancelled(event);
        }
    }

    /**
     * Applies physical damage taken triggers
     *
     * @param event event details
     */
    public void onPhysical(final PhysicalDamageEvent event)
    {
        if (running) return;

        final LivingEntity damager = event.getDamager();
        final LivingEntity target = event.getTarget();
        final boolean projectile = event.isProjectile();

        // Can't be null
        if (damager == null || target == null) return;

        if (!active.containsKey(target.getEntityId())) return;

        final String type = component.settings.getString("type", "both").toLowerCase();
        final boolean caster = !component.settings.getString("target", "true").toLowerCase().equals("false");
        final double min = component.settings.getDouble("dmg-min");
        final double max = component.settings.getDouble("dmg-max");

        if (event.getDamage() >= min && event.getDamage() <= max
                && (type.equals("both") || (type.equals("projectile") == projectile))) {
            DynamicSkill.getCastData(target).put("api-taken", event.getDamage());
            trigger(target, caster ? target : damager, active.get(target.getEntityId()));
            skill.applyCancelled(event);
        }
    }

    /**
     * Applies physical damage dealt trigger effects
     *
     * @param event event details
     */
    public void onDealtPhysical(final PhysicalDamageEvent event)
    {
        final LivingEntity damager = event.getDamager();
        final LivingEntity target = event.getTarget();
        final boolean projectile = event.isProjectile();

        // Can't be null
        if (damager == null || target == null) return;

        if (!active.containsKey(damager.getEntityId())) return;

        final String type = component.settings.getString("type", "both").toLowerCase();
        final boolean caster = !component.settings.getString("target", "true").toLowerCase().equals("false");
        final double min = component.settings.getDouble("dmg-min");
        final double max = component.settings.getDouble("dmg-max");

        if (event.getDamage() >= min && event.getDamage() <= max
                && (type.equals("both") || type.equals("projectile") == projectile)) {
            DynamicSkill.getCastData(damager).put("api-dealt", event.getDamage());
            trigger(damager, caster ? damager : target, active.get(damager.getEntityId()));
            skill.applyCancelled(event);
        }
    }

    /**
     * Applies skill damage taken trigger effects
     *
     * @param event event details
     */
    public void onSkillDamage(final SkillDamageEvent event)
    {
        if (running) return;

        final LivingEntity damager = event.getDamager();
        final LivingEntity target = event.getTarget();

        // Skill received
        if (!active.containsKey(target.getEntityId())) return;

        final boolean caster = !component.settings.getString("target", "true").toLowerCase().equals("false");
        final double min = component.settings.getDouble("dmg-min");
        final double max = component.settings.getDouble("dmg-max");
        final String category = component.settings.getString("category", "");

        if (event.getDamage() >= min && event.getDamage() <= max && (category.length() == 0 || event.getClassification().equals(category))) {
            DynamicSkill.getCastData(target).put("api-taken", event.getDamage());
            trigger(target, caster ? target : damager, active.get(event.getTarget().getEntityId()));
            skill.applyCancelled(event);
        }
    }

    /**
     * Applies skill damage dealt trigger effects
     *
     * @param event event details
     */
    public void onSkillDealt(final SkillDamageEvent event)
    {
        final LivingEntity damager = event.getDamager();
        final LivingEntity target = event.getTarget();

        if (!active.containsKey(damager.getEntityId())) return;

        final boolean caster = !component.settings.getString("target", "true").toLowerCase().equals("false");
        final double min = component.settings.getDouble("dmg-min");
        final double max = component.settings.getDouble("dmg-max");
        final String category = component.settings.getString("category", "");

        if (event.getDamage() >= min && event.getDamage() <= max && (category.length() == 0 || event.getClassification().equals(category))) {
            DynamicSkill.getCastData(damager).put("api-dealt", event.getDamage());
            trigger(damager, caster ? damager : target, active.get(damager.getEntityId()));
        }
    }

    /**
     * Applies crouch triggers
     *
     * @param event event details
     */
    public void onCrouch(final PlayerToggleSneakEvent event)
    {
        if (running) return;
        if (!active.containsKey(event.getPlayer().getEntityId())) return;

        final String type = component.settings.getString("type", "start crouching");
        if (type.equalsIgnoreCase("both") || event.isSneaking() != type.equalsIgnoreCase("stop crouching")) {
            trigger(event.getPlayer(), event.getPlayer(), active.get(event.getPlayer().getEntityId()));
        }
    }

    /**
     * Land trigger
     *
     * @param event event details
     */
    public void onLand(final PlayerLandEvent event)
    {
        if (running) return;
        if (!active.containsKey(event.getPlayer().getEntityId())) return;

        final double minDistance = component.settings.getDouble("min-distance", 0);
        if (event.getDistance() >= minDistance) {
            DynamicSkill.getCastData(event.getPlayer()).put("api-distance", event.getDistance());
            trigger(event.getPlayer(), event.getPlayer(), active.get(event.getPlayer().getEntityId()));
        }
    }

    boolean trigger(final LivingEntity user, final LivingEntity target, final int level)
    {
        if (user == null) return false;

        if (user instanceof Player)
        {
            final PlayerData data = SkillAPI.getPlayerData((Player) user);
            final PlayerSkill skill = data.getSkill(this.skill.getName());
            final boolean cd = component.getSettings().getBool("cooldown", false);
            final boolean mana = component.getSettings().getBool("mana", false);

            if ((cd || mana) && !data.check(skill, cd, mana)) return false;

            if (cd) skill.startCooldown();
            if (mana) data.useMana(skill.getManaCost(), ManaCost.SKILL_CAST);
        }

        final ArrayList<LivingEntity> targets = new ArrayList<LivingEntity>();
        targets.add(target);

        running = true;
        final boolean result = component.execute(user, level, targets);
        running = false;
        return result;
    }
}
