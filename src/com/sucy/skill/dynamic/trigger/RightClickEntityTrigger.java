package com.sucy.skill.dynamic.trigger;

import com.google.common.collect.ImmutableList;
import com.sucy.skill.api.Settings;
import com.sucy.skill.dynamic.ComponentType;
import com.sucy.skill.dynamic.custom.CustomTrigger;
import com.sucy.skill.dynamic.custom.EditorOption;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.List;
import java.util.Map;

/**
 * SkillAPI © 2018
 * com.sucy.skill.dynamic.trigger.BlockBreakTrigger
 */
public class RightClickEntityTrigger implements CustomTrigger<PlayerInteractEntityEvent> {

	@Override
	public String getDescription() {
		return "Triggers when the player right clicks an entity.";
	}
	
	@Override
	public Class<PlayerInteractEntityEvent> getEvent() {
		return PlayerInteractEntityEvent.class;
	}

	@Override
	public List<EditorOption> getOptions() {
        return ImmutableList.of();
	}

	@Override
	public String getKey() {
		return "RIGHT_CLICK_ENTITY";
	}

	@Override
	public ComponentType getType() {
		return ComponentType.TRIGGER;
	}

	@Override
	public LivingEntity getCaster(PlayerInteractEntityEvent e) {
		return e.getPlayer();
	}

	@Override
	public LivingEntity getTarget(PlayerInteractEntityEvent e, Settings s) {
		return (LivingEntity) e.getRightClicked();
	}

	@Override
	public void setValues(PlayerInteractEntityEvent arg0, Map<String, Object> arg1) {
		return;
		
	}

	@Override
	public boolean shouldTrigger(PlayerInteractEntityEvent e, final int level, Settings s) {
		if (e.getHand() != null && e.getHand().equals(EquipmentSlot.HAND))
			return !(e.getRightClicked() instanceof Player) && e.getRightClicked() instanceof LivingEntity;
		return false;
	}
}
