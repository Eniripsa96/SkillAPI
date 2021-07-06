package com.sucy.skill.dynamic.trigger;

import java.util.List;
import java.util.Map;

import org.bukkit.entity.LivingEntity;
import com.google.common.collect.ImmutableList;
import com.sucy.skill.api.Settings;
import com.sucy.skill.api.event.SkillHealEvent;
import com.sucy.skill.dynamic.ComponentType;
import com.sucy.skill.dynamic.custom.CustomTrigger;
import com.sucy.skill.dynamic.custom.EditorOption;

public class SkillHealTrigger implements CustomTrigger<SkillHealEvent> {

	@Override
	public String getDescription() {
		return "Triggers when the player heals something.";
	}
	
	@Override
	public Class<SkillHealEvent> getEvent() {
		return SkillHealEvent.class;
	}

	@Override
	public List<EditorOption> getOptions() {
        return ImmutableList.of(
                EditorOption.number(
                        "heal-min",
                        "Min Healing",
                        "The minimum healing that needs to be given",
                        0,
                        0),
                EditorOption.number(
                        "dmg-max",
                        "Max Healing",
                        "The maximum healing that can be given",
                        999999,
                        0));
	}

	@Override
	public String getKey() {
		return "SKILL_HEAL";
	}

	@Override
	public ComponentType getType() {
		return ComponentType.TRIGGER;
	}

	@Override
	public LivingEntity getCaster(SkillHealEvent e) {
		return e.getHealer();
	}

	@Override
	public LivingEntity getTarget(SkillHealEvent e, Settings s) {
		return e.getTarget();
	}

	@Override
	public void setValues(SkillHealEvent e, Map<String, Object> data) {
        data.put("api-healed", e.getAmount());
		
	}

	@Override
	public boolean shouldTrigger(SkillHealEvent e, final int level, Settings settings) {
        final double min = settings.getDouble("heal-min", 0);
        final double max = settings.getDouble("heal-max", 999999);
		return !e.isCancelled() && min <= e.getAmount() && max >= e.getAmount();
	}

}
