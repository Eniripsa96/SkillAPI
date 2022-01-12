package com.sucy.skill.dynamic;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.enums.ManaCost;
import com.sucy.skill.api.event.PlayerCriticalCheckEvent;
import com.sucy.skill.api.event.PlayerCriticalSuccessEvent;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.player.PlayerSkill;
import com.sucy.skill.dynamic.trigger.Trigger;
import com.sucy.skill.dynamic.trigger.TriggerComponent;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

import static com.sucy.skill.dynamic.ComponentRegistry.getExecutor;

/**
 * SkillAPI © 2017
 * com.sucy.skill.dynamic.TriggerHandler
 */
public class TriggerHandler implements Listener {

    private final HashMap<Integer, Integer> active = new HashMap<>();

    private final DynamicSkill skill;
    private final String key;
    private final Trigger<?> trigger;
    private final TriggerComponent component;
    private static Random gen = new Random();

    public TriggerHandler(
            final DynamicSkill skill,
            final String key,
            final Trigger trigger,
            final TriggerComponent component) {

        Objects.requireNonNull(skill, "Must provide a skill");
        Objects.requireNonNull(key, "Must provide a key");
        Objects.requireNonNull(trigger, "Must provide a trigger");
        Objects.requireNonNull(component, "Must provide a component");

        this.skill = skill;
        this.key = key;
        this.trigger = trigger;
        this.component = component;
    }

    public String getKey() {
        return key;
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
        component.cleanUp(entity);
    }

    /**
     * Registers needed events for the skill, ignoring any unused events for efficiency
     *
     * @param plugin plugin reference
     */
    public void register(final SkillAPI plugin) {
        plugin.getServer().getPluginManager().registerEvent(
                trigger.getEvent(), this, EventPriority.HIGHEST, getExecutor(trigger), plugin, true);
    }

    /**
     * Registers needed events for the skill, intentionally not ignoring unused events
     *
     * @param plugin plugin reference
     */
    public void registerNoIgnore(final SkillAPI plugin) {
        plugin.getServer().getPluginManager().registerEvent(
                trigger.getEvent(), this, EventPriority.HIGHEST, getExecutor(trigger), plugin, false);
    }

    <T extends Event> void apply(final T event, final Trigger<T> trigger) {
        final LivingEntity caster = trigger.getCaster(event);
        if (caster == null || !active.containsKey(caster.getEntityId())) { return; }

        final int level = active.get(caster.getEntityId());
        if (!trigger.shouldTrigger(event, level, component.settings)) { return; }

        final LivingEntity target = trigger.getTarget(event, component.settings);
        trigger.setValues(event, DynamicSkill.getCastData(caster));
        trigger(caster, target, level);

        if (event instanceof Cancellable) { skill.applyCancelled((Cancellable) event); }
        trigger.postProcess(event, skill);
    }

    boolean trigger(final LivingEntity user, final LivingEntity target, final int level) {
        if (user == null || target == null || component.isRunning() || !SkillAPI.getSettings().isValidTarget(target)) {
            return false;
        }

        if (user instanceof Player) {
            final PlayerData data = SkillAPI.getPlayerData((Player) user);
            final PlayerSkill skill = data.getSkill(this.skill.getName());
            final boolean cd = component.getSettings().getBool("cooldown", false);
            final boolean mana = component.getSettings().getBool("mana", false);
            double critChance = component.getSettings().getDouble("crit-chance", 0);
            double augmentCritChance = critChance;
            
            if (critChance > 0) {
                PlayerCriticalCheckEvent e = new PlayerCriticalCheckEvent(data, critChance);
                Bukkit.getPluginManager().callEvent(e);
                augmentCritChance = e.getChance();
            }
            boolean isCrit = augmentCritChance > gen.nextDouble();
            
            critChance = isCrit ? critChance : 0;

            if ((cd || mana) && !data.check(skill, cd, mana)) { return false; }

            if (component.trigger(user, target, level, critChance)) {
                if (cd) { skill.startCooldown(); }
                if (mana) { data.useMana(skill.getManaCost(), ManaCost.SKILL_CAST); }
                if (isCrit) {
                	Bukkit.getPluginManager().callEvent(new PlayerCriticalSuccessEvent(data, critChance));
                	((Player) user).playSound(user.getLocation(), Sound.ITEM_TRIDENT_RIPTIDE_1, 1.0F, 1.0F);
                }

                return true;
            } else {
                return false;
            }
        } else {
            return component.trigger(user, target, level, 0);
        }
    }
}
