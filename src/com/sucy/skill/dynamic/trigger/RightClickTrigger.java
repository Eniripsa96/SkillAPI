package com.sucy.skill.dynamic.trigger;

import java.util.List;
import java.util.Map;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import com.google.common.collect.ImmutableList;
import com.sucy.skill.api.Settings;
import com.sucy.skill.dynamic.ComponentType;
import com.sucy.skill.dynamic.custom.CustomTrigger;
import com.sucy.skill.dynamic.custom.EditorOption;

public class RightClickTrigger implements CustomTrigger<PlayerInteractEvent> {

	@Override
	public String getDescription() {
		return "Triggers when the player right clicks.";
	}
	
	@Override
	public Class<PlayerInteractEvent> getEvent() {
		return PlayerInteractEvent.class;
	}

	@Override
	public List<EditorOption> getOptions() {
        return ImmutableList.of();
	}

	@Override
	public String getKey() {
		return "RIGHT_CLICK";
	}

	@Override
	public ComponentType getType() {
		return ComponentType.TRIGGER;
	}

	@Override
	public LivingEntity getCaster(PlayerInteractEvent e) {
		return e.getPlayer();
	}

	@Override
	public LivingEntity getTarget(PlayerInteractEvent e, Settings s) {
		return e.getPlayer();
	}

	@Override
	public void setValues(PlayerInteractEvent arg0, Map<String, Object> arg1) {
		return;
		
	}

	@Override
	public boolean shouldTrigger(PlayerInteractEvent e, final int level, Settings s) {
		// TODO Auto-generated method stub
		if (e.getHand() != null && e.getHand().equals(EquipmentSlot.HAND))
			return e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK);
		return false;
	}

}
