package com.sucy.skill.dynamic.trigger;

import com.google.common.collect.ImmutableList;
import com.sucy.skill.api.Settings;
import com.sucy.skill.dynamic.ComponentType;
import com.sucy.skill.dynamic.custom.CustomTrigger;
import com.sucy.skill.dynamic.custom.EditorOption;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import java.util.List;
import java.util.Map;

/**
 * SkillAPI © 2018
 * com.sucy.skill.dynamic.trigger.BlockBreakTrigger
 */
public class DamageDeathTrigger implements CustomTrigger<EntityDamageEvent> {

	@Override
	public String getDescription() {
		return "Triggers if some damage is about to kill the player.";
	}
	
	@Override
	public Class<EntityDamageEvent> getEvent() {
		return EntityDamageEvent.class;
	}

	@Override
	public List<EditorOption> getOptions() {
        return ImmutableList.of();
	}

	@Override
	public String getKey() {
		return "DAMAGE_DEATH";
	}

	@Override
	public ComponentType getType() {
		return ComponentType.TRIGGER;
	}

	@Override
	public LivingEntity getCaster(EntityDamageEvent e) {
		return (LivingEntity) e.getEntity();
	}

	@Override
	public LivingEntity getTarget(EntityDamageEvent e, Settings s) {
		return (LivingEntity) e.getEntity();
	}

	@Override
	public void setValues(EntityDamageEvent arg0, Map<String, Object> arg1) {
		return;
		
	}

	@Override
	public boolean shouldTrigger(EntityDamageEvent e, final int level, Settings s) {
		if (e.getEntity() instanceof LivingEntity) {
			LivingEntity le = (LivingEntity) e.getEntity();
			return !e.isCancelled() && le.getHealth() <= e.getFinalDamage() && e.getCause().equals(DamageCause.CUSTOM);
		}
        return false;
	}
}
