package com.sucy.skill.dynamic.trigger;

import com.google.common.collect.ImmutableList;
import com.sucy.skill.api.Settings;
import com.sucy.skill.dynamic.ComponentType;
import com.sucy.skill.dynamic.custom.CustomTrigger;
import com.sucy.skill.dynamic.custom.EditorOption;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent.Action;

import java.util.List;
import java.util.Map;

/**
 * SkillAPI © 2018
 * com.sucy.skill.dynamic.trigger.BlockBreakTrigger
 */
public class PotionApplyTrigger implements CustomTrigger<EntityPotionEffectEvent> {

	@Override
	public String getDescription() {
		return "Triggers when the player has a specific flag applied.";
	}
	
	@Override
	public Class<EntityPotionEffectEvent> getEvent() {
		return EntityPotionEffectEvent.class;
	}

	@Override
	public List<EditorOption> getOptions() {
        return ImmutableList.of(
        		EditorOption.list("potions", "Potions", "Which potions to trigger with",
        				ImmutableList.of("SPEED", "SLOW", "BLINDNESS", "POISON")));
	}

	@Override
	public String getKey() {
		return "POTION_APPLY";
	}

	@Override
	public ComponentType getType() {
		return ComponentType.TRIGGER;
	}

	@Override
	public LivingEntity getCaster(EntityPotionEffectEvent e) {
		return (LivingEntity) e.getEntity();
	}

	@Override
	public LivingEntity getTarget(EntityPotionEffectEvent e, Settings s) {
		return (LivingEntity) e.getEntity();
	}

	@Override
	public void setValues(EntityPotionEffectEvent arg0, Map<String, Object> arg1) {
		return;
		
	}

	@Override
	public boolean shouldTrigger(EntityPotionEffectEvent e, final int level, Settings s) {
        if (!e.getAction().equals(Action.ADDED) && !e.getAction().equals(Action.CHANGED)) return false;
        final List<String> potions = s.getStringList("potions");
        for (String potion : potions) {
        	if (e.getNewEffect().getType().getName().startsWith(potion)) return true;
        }
        return false;
	}
}
