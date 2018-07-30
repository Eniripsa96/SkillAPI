package com.sucy.skill.dynamic;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.enums.ManaCost;
import com.sucy.skill.api.event.PhysicalDamageEvent;
import com.sucy.skill.api.event.PlayerLandEvent;
import com.sucy.skill.api.event.SkillDamageEvent;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.player.PlayerSkill;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.EventExecutor;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collector;

/**
 * SkillAPI © 2017
 * com.sucy.skill.dynamic.TriggerHandler
 */
public class TriggerHandler implements Listener {

    private final HashMap<Integer, Integer> active = new HashMap<>();

    private final DynamicSkill skill;
    private final Trigger trigger;
    private final EffectComponent component;
    private final Set<String> transformed;

    private boolean running;

    public TriggerHandler(final DynamicSkill skill, final Trigger trigger, final EffectComponent component) {
        this.skill = skill;
        this.trigger = trigger;
        this.component = component;

        switch (trigger) {
            case BLOCK_BREAK:
            case BLOCK_PLACE:
                transformed = component.settings.getStringList("material").stream()
                        .map(String::toUpperCase)
                        .collect(toImmutableSet());
                break;
            case SKILL_DAMAGE:
            case TOOK_SKILL_DAMAGE:
                transformed = ImmutableSet.copyOf(component.settings.getStringList("category"));
                break;
            default:
                transformed = ImmutableSet.of();
        }
    }

    private <T> Collector<T, ?, ImmutableSet<T>> toImmutableSet() {
        return Collector.of(
                ImmutableSet.Builder<T>::new,
                ImmutableSet.Builder<T>::add,
                (l, r) -> l.addAll(r.build()),
                ImmutableSet.Builder::build);
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
    public void register(final SkillAPI plugin) {
        if (!EXECUTORS.containsKey(trigger)) { return; }

        plugin.getServer().getPluginManager().registerEvent(
                trigger.getEvent(), this, EventPriority.HIGHEST, EXECUTORS.get(trigger), plugin, true);
    }

    public void onBlockBreak(final BlockBreakEvent event) {
        onBlockEvent(event.getPlayer(), event.getBlock(), event);
    }

    public void onBlockPlace(final BlockPlaceEvent event) {
        onBlockEvent(event.getPlayer(), event.getBlock(), event);
    }

    private void onBlockEvent(final Player player, final Block block, final Cancellable event) {
        if (player == null || !active.containsKey(player.getEntityId())) { return; }

        final int data = component.getSettings().getInt("data", -1);
        final int level = active.get(player.getEntityId());
        if (transformed.isEmpty() || transformed.contains("ANY") || transformed.contains(block.getType().name())
                && (data == -1 || block.getData() == data)) {
            final Map<String, Object> castData = DynamicSkill.getCastData(player);
            castData.put("api-block-type", block.getType().name());
            castData.put("api-block-loc", block.getLocation());

            trigger(player, player, level);
            skill.applyCancelled(event);
        }
    }

    /**
     * Cancels firing projectiles when the launcher is stunned or disarmed.
     *
     * @param event event details
     */
    public void onLaunch(final ProjectileLaunchEvent event) {
        if (!(event.getEntity().getShooter() instanceof LivingEntity)) { return; }

        final LivingEntity shooter = (LivingEntity) event.getEntity().getShooter();
        if (!active.containsKey(shooter.getEntityId())) { return; }

        final String type = component.getSettings().getString("type", "any").toUpperCase().replace(" ", "_");
        final int level = active.get(shooter.getEntityId());
        if (type.equals("ANY") || type.equals(event.getEntity().getType().name())) {
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
    public void onDeath(final EntityDeathEvent event) {
        if (!active.containsKey(event.getEntity().getEntityId())) { return; }

        final boolean killer = component.getSettings().getString("killer", "false").equalsIgnoreCase("true");
        if (!killer || event.getEntity().getKiller() != null) {
            trigger(
                    event.getEntity(),
                    killer ? event.getEntity().getKiller() : event.getEntity(),
                    active.get(event.getEntity().getEntityId()));
        }
    }

    /**
     * Applies the kill trigger effects
     *
     * @param event event details
     */
    public void onKill(final EntityDeathEvent event) {
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
    public void onEnvironmental(final EntityDamageEvent event) {
        if (!(event.getEntity() instanceof LivingEntity)) { return; }

        final LivingEntity target = (LivingEntity) event.getEntity();
        if (!active.containsKey(target.getEntityId())) { return; }

        final String name = component.getSettings().getString("type", "").toUpperCase().replace(' ', '_');
        if (event.getCause().name().equals(name)) {
            DynamicSkill.getCastData(target).put("api-taken", event.getDamage());
            trigger(target, target, active.get(target.getEntityId()));
            skill.applyCancelled(event);
            event.setDamage(skill.applyImmediateBuff(event.getDamage()));
        }
    }

    /**
     * Applies physical damage taken triggers
     *
     * @param event event details
     */
    public void onPhysical(final PhysicalDamageEvent event) {
        final LivingEntity damager = event.getDamager();
        final LivingEntity target = event.getTarget();
        final boolean projectile = event.isProjectile();

        // Can't be null
        if (damager == null || target == null) { return; }

        if (!active.containsKey(target.getEntityId())) { return; }

        final String type = component.settings.getString("type", "both").toLowerCase();
        final boolean caster = !component.settings.getString("target", "true").toLowerCase().equals("false");
        final double min = component.settings.getDouble("dmg-min");
        final double max = component.settings.getDouble("dmg-max");

        if (event.getDamage() >= min && event.getDamage() <= max
                && (type.equals("both") || (type.equals("projectile") == projectile))) {
            DynamicSkill.getCastData(target).put("api-taken", event.getDamage());
            trigger(target, caster ? target : damager, active.get(target.getEntityId()));
            skill.applyCancelled(event);
            event.setDamage(skill.applyImmediateBuff(event.getDamage()));
        }
    }

    /**
     * Applies physical damage dealt trigger effects
     *
     * @param event event details
     */
    public void onDealtPhysical(final PhysicalDamageEvent event) {
        final LivingEntity damager = event.getDamager();
        final LivingEntity target = event.getTarget();
        final boolean projectile = event.isProjectile();

        // Can't be null
        if (damager == null || target == null) { return; }

        if (!active.containsKey(damager.getEntityId())) { return; }

        final String type = component.settings.getString("type", "both").toLowerCase();
        final boolean caster = !component.settings.getString("target", "true").toLowerCase().equals("false");
        final double min = component.settings.getDouble("dmg-min");
        final double max = component.settings.getDouble("dmg-max");

        if (event.getDamage() >= min && event.getDamage() <= max
                && (type.equals("both") || type.equals("projectile") == projectile)) {
            DynamicSkill.getCastData(damager).put("api-dealt", event.getDamage());
            trigger(damager, caster ? damager : target, active.get(damager.getEntityId()));
            skill.applyCancelled(event);
            event.setDamage(skill.applyImmediateBuff(event.getDamage()));
        }
    }

    /**
     * Applies skill damage taken trigger effects
     *
     * @param event event details
     */
    public void onSkillDamage(final SkillDamageEvent event) {
        final LivingEntity damager = event.getDamager();
        final LivingEntity target = event.getTarget();

        // Skill received
        if (!active.containsKey(target.getEntityId())) { return; }

        final boolean caster = !component.settings.getString("target", "true").toLowerCase().equals("false");
        final double min = component.settings.getDouble("dmg-min");
        final double max = component.settings.getDouble("dmg-max");

        if (event.getDamage() >= min && event.getDamage() <= max && (transformed.isEmpty() || transformed.contains(event.getClassification()))) {
            DynamicSkill.getCastData(target).put("api-taken", event.getDamage());
            trigger(target, caster ? target : damager, active.get(event.getTarget().getEntityId()));
            skill.applyCancelled(event);
            event.setDamage(skill.applyImmediateBuff(event.getDamage()));
        }
    }

    /**
     * Applies skill damage dealt trigger effects
     *
     * @param event event details
     */
    public void onSkillDealt(final SkillDamageEvent event) {
        final LivingEntity damager = event.getDamager();
        final LivingEntity target = event.getTarget();

        if (!active.containsKey(damager.getEntityId())) { return; }

        final boolean caster = !component.settings.getString("target", "true").toLowerCase().equals("false");
        final double min = component.settings.getDouble("dmg-min");
        final double max = component.settings.getDouble("dmg-max");

        if (event.getDamage() >= min && event.getDamage() <= max && (transformed.isEmpty() || transformed.contains(event.getClassification()))) {
            DynamicSkill.getCastData(damager).put("api-dealt", event.getDamage());
            trigger(damager, caster ? damager : target, active.get(damager.getEntityId()));
            event.setDamage(skill.applyImmediateBuff(event.getDamage()));
        }
    }

    /**
     * Applies crouch triggers
     *
     * @param event event details
     */
    public void onCrouch(final PlayerToggleSneakEvent event) {
        if (!active.containsKey(event.getPlayer().getEntityId())) { return; }

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
    public void onLand(final PlayerLandEvent event) {
        if (!active.containsKey(event.getPlayer().getEntityId())) { return; }

        final double minDistance = component.settings.getDouble("min-distance", 0);
        if (event.getDistance() >= minDistance) {
            DynamicSkill.getCastData(event.getPlayer()).put("api-distance", event.getDistance());
            trigger(event.getPlayer(), event.getPlayer(), active.get(event.getPlayer().getEntityId()));
        }
    }

    /**
     * Move trigger
     *
     * @param event event details
     */
    public void onMove(final PlayerMoveEvent event) {
        if (!active.containsKey(event.getPlayer().getEntityId())) return;
        if (event.getTo().getWorld() != event.getFrom().getWorld()) return;

        final double distance = event.getTo().getWorld() == event.getFrom().getWorld()
                ? event.getTo().distance(event.getFrom()) : 0;
        DynamicSkill.getCastData(event.getPlayer()).put("api-moved", distance);
        trigger(event.getPlayer(), event.getPlayer(), active.get(event.getPlayer().getEntityId()));
    }

    boolean trigger(final LivingEntity user, final LivingEntity target, final int level) {
        if (user == null || target == null || running || !SkillAPI.getSettings().isValidTarget(target)) {
            return false;
        }

        if (user instanceof Player) {
            final PlayerData data = SkillAPI.getPlayerData((Player) user);
            final PlayerSkill skill = data.getSkill(this.skill.getName());
            final boolean cd = component.getSettings().getBool("cooldown", false);
            final boolean mana = component.getSettings().getBool("mana", false);

            if ((cd || mana) && !data.check(skill, cd, mana)) { return false; }

            if (cd) { skill.startCooldown(); }
            if (mana) { data.useMana(skill.getManaCost(), ManaCost.SKILL_CAST); }
        }

        final ArrayList<LivingEntity> targets = new ArrayList<>();
        targets.add(target);

        try {
            running = true;
            return component.execute(user, level, targets);
        } finally {
            running = false;
        }
    }

    private static final Map<Trigger, EventExecutor> EXECUTORS = new EnumMap<>(
            ImmutableMap.<Trigger, EventExecutor>builder()
                    .put(Trigger.BLOCK_BREAK, create(TriggerHandler::onBlockBreak))
                    .put(Trigger.BLOCK_PLACE, create(TriggerHandler::onBlockPlace))
                    .put(Trigger.CROUCH, create(TriggerHandler::onCrouch))
                    .put(Trigger.DEATH, create(TriggerHandler::onDeath))
                    .put(Trigger.ENVIRONMENT_DAMAGE, create(TriggerHandler::onEnvironmental))
                    .put(Trigger.KILL, create(TriggerHandler::onKill))
                    .put(Trigger.LAND, create(TriggerHandler::onLand))
                    .put(Trigger.LAUNCH, create(TriggerHandler::onLaunch))
                    .put(Trigger.MOVE, create(TriggerHandler::onMove))
                    .put(Trigger.PHYSICAL_DAMAGE, create(TriggerHandler::onDealtPhysical))
                    .put(Trigger.SKILL_DAMAGE, create(TriggerHandler::onSkillDealt))
                    .put(Trigger.TOOK_PHYSICAL_DAMAGE, create(TriggerHandler::onPhysical))
                    .put(Trigger.TOOK_SKILL_DAMAGE, create(TriggerHandler::onSkillDamage))
                    .build());

    private static <T extends Event> EventExecutor create(final BiConsumer<TriggerHandler, T> handler) {
        return (listener, event) -> handler.accept((TriggerHandler) listener, (T) event);
    }
}
