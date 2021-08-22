package com.sucy.skill.dynamic.trigger;

import java.util.List;
import java.util.Map;

import org.bukkit.entity.LivingEntity;
import com.google.common.collect.ImmutableList;
import com.sucy.skill.api.Settings;
import com.sucy.skill.api.event.PlayerSkillCastSuccessEvent;
import com.sucy.skill.dynamic.ComponentType;
import com.sucy.skill.dynamic.custom.CustomTrigger;
import com.sucy.skill.dynamic.custom.EditorOption;

public class SkillCastTrigger implements CustomTrigger<PlayerSkillCastSuccessEvent> {

	@Override
	public String getDescription() {
		return "Triggers when the player successfully casts a skill.";
	}
	
	@Override
	public Class<PlayerSkillCastSuccessEvent> getEvent() {
		return PlayerSkillCastSuccessEvent.class;
	}

	@Override
	public List<EditorOption> getOptions() {
        return ImmutableList.of(
                EditorOption.text(
                        "skill_name",
                        "Skill Name",
                        "Skill name",
                        ""));
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
	public LivingEntity getCaster(PlayerSkillCastSuccessEvent e) {
		return e.getPlayer();
	}

	@Override
	public LivingEntity getTarget(PlayerSkillCastSuccessEvent e, Settings s) {
		return e.getPlayer();
	}

	@Override
	public void setValues(PlayerSkillCastSuccessEvent arg0, Map<String, Object> arg1) {
		return;
		
	}

	@Override
	public boolean shouldTrigger(PlayerSkillCastSuccessEvent e, final int level, Settings s) {
		return !e.isCancelled();
	}

}
