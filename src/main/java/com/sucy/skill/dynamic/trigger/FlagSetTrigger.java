package com.sucy.skill.dynamic.trigger;

import com.google.common.collect.ImmutableList;
import com.sucy.skill.api.Settings;
import com.sucy.skill.api.event.FlagApplyEvent;
import com.sucy.skill.dynamic.ComponentType;
import com.sucy.skill.dynamic.custom.CustomTrigger;
import com.sucy.skill.dynamic.custom.EditorOption;

import org.bukkit.entity.LivingEntity;
import java.util.List;
import java.util.Map;

/**
 * SkillAPI © 2018
 * com.sucy.skill.dynamic.trigger.BlockBreakTrigger
 */
public class FlagSetTrigger implements CustomTrigger<FlagApplyEvent> {

	@Override
	public String getDescription() {
		return "Triggers when the player has a specific flag applied.";
	}
	
	@Override
	public Class<FlagApplyEvent> getEvent() {
		return FlagApplyEvent.class;
	}

	@Override
	public List<EditorOption> getOptions() {
        return ImmutableList.of(
        		EditorOption.list("flags", "Flags", "Which flags to trigger with", ImmutableList.of("stun", "root", "silence", "curse")));
	}

	@Override
	public String getKey() {
		return "FLAG_APPLY";
	}

	@Override
	public ComponentType getType() {
		return ComponentType.TRIGGER;
	}

	@Override
	public LivingEntity getCaster(FlagApplyEvent e) {
		return e.getCaster();
	}

	@Override
	public LivingEntity getTarget(FlagApplyEvent e, Settings s) {
		return e.getEntity();
	}

	@Override
	public void setValues(FlagApplyEvent arg0, Map<String, Object> arg1) {
		return;
		
	}

	@Override
	public boolean shouldTrigger(FlagApplyEvent e, final int level, Settings s) {
        final List<String> types = s.getStringList("flags");
        return types.contains(e.getFlag());
	}
}
