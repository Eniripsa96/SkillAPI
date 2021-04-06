package com.sucy.skill.dynamic.trigger;

import java.util.List;
import java.util.Map;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import com.google.common.collect.ImmutableList;
import com.sucy.skill.api.Settings;
import com.sucy.skill.api.event.NeoClickComboEvent;
import com.sucy.skill.api.event.NeoClickComboEvent.ClickType;
import com.sucy.skill.dynamic.ComponentType;
import com.sucy.skill.dynamic.custom.CustomTrigger;
import com.sucy.skill.dynamic.custom.EditorOption;

public class ClickComboTrigger implements CustomTrigger<NeoClickComboEvent> {

	@Override
	public String getDescription() {
		return "Triggers when the player left clicks.";
	}
	
	@Override
	public Class<NeoClickComboEvent> getEvent() {
		return NeoClickComboEvent.class;
	}

	@Override
	public List<EditorOption> getOptions() {
        return ImmutableList.of(
        		EditorOption.dropdown("clicktype", "Click Type", "Which click event to trigger with",
        				ImmutableList.of("LR", "RL")));
	}

	@Override
	public String getKey() {
		return "CLICK_COMBO";
	}

	@Override
	public ComponentType getType() {
		return ComponentType.TRIGGER;
	}

	@Override
	public LivingEntity getCaster(NeoClickComboEvent e) {
		return e.getPlayer();
	}

	@Override
	public LivingEntity getTarget(NeoClickComboEvent e, Settings s) {
		return e.getPlayer();
	}

	@Override
	public void setValues(NeoClickComboEvent arg0, Map<String, Object> arg1) {
		return;
		
	}

	@Override
	public boolean shouldTrigger(NeoClickComboEvent e, final int level, Settings s) {
		if (e.getType().equals(ClickType.LR) && s.getString("clicktype").equals("LR")) return true;
		if (e.getType().equals(ClickType.RL) && s.getString("clicktype").equals("RL")) return true;
		return false;
	}

}
