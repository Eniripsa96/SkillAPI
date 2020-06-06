package com.sucy.skill.dynamic.trigger;

import java.util.List;
import java.util.Map;

import org.bukkit.entity.LivingEntity;
import com.google.common.collect.ImmutableList;
import com.sucy.skill.api.Settings;
import com.sucy.skill.api.event.PlayerCastSkillEvent;
import com.sucy.skill.dynamic.ComponentType;
import com.sucy.skill.dynamic.custom.CustomTrigger;
import com.sucy.skill.dynamic.custom.EditorOption;

public class SkillCastTrigger implements CustomTrigger<PlayerCastSkillEvent> {

	@Override
	public String getDescription() {
		return "Triggers when the player successfully casts a skill.";
	}
	
	@Override
	public Class<PlayerCastSkillEvent> getEvent() {
		return PlayerCastSkillEvent.class;
	}

	@Override
	public List<EditorOption> getOptions() {
        return ImmutableList.of();
	}

	@Override
	public String getKey() {
		return "CAST_SKILL";
	}

	@Override
	public ComponentType getType() {
		return ComponentType.TRIGGER;
	}

	@Override
	public LivingEntity getCaster(PlayerCastSkillEvent e) {
		return e.getPlayer();
	}

	@Override
	public LivingEntity getTarget(PlayerCastSkillEvent e, Settings s) {
		return e.getPlayer();
	}

	@Override
	public void setValues(PlayerCastSkillEvent arg0, Map<String, Object> arg1) {
		return;
		
	}

	@Override
	public boolean shouldTrigger(PlayerCastSkillEvent e, final int level, Settings s) {
		// TODO Auto-generated method stub
		return !e.isCancelled();
	}

}
