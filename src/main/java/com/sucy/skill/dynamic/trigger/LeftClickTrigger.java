package com.sucy.skill.dynamic.trigger;

import java.util.List;
import java.util.Map;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import com.google.common.collect.ImmutableList;
import com.sucy.skill.api.Settings;
import com.sucy.skill.dynamic.ComponentType;
import com.sucy.skill.dynamic.custom.CustomTrigger;
import com.sucy.skill.dynamic.custom.EditorOption;

public class LeftClickTrigger implements CustomTrigger<PlayerInteractEvent> {

	@Override
	public String getDescription() {
		return "Triggers when the player left clicks.";
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
		return "LEFT_CLICK";
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
	public void setValues(PlayerInteractEvent e, Map<String, Object> data) {
    	if (data.containsKey("cd-leftclickcooldown")) {
    		// Only change the attack cooldown data value if it's been at least 50 ms (To get through every trigger usually takes 4ms)
    		if ((long) data.get("cd-leftclickcooldown") + 50 < System.currentTimeMillis()) {
                data.put("api-leftclickcooldown", (double) e.getPlayer().getAttackCooldown());
    		}
    	}
    	else {
            data.put("api-leftclickcooldown", (double) e.getPlayer().getAttackCooldown());
    	}
    	data.put("cd-leftclickcooldown", System.currentTimeMillis());
		return;
		
	}

	@Override
	public boolean shouldTrigger(PlayerInteractEvent e, final int level, Settings s) {
		if (e.getHand() != null && e.getHand().equals(EquipmentSlot.HAND))
			return e.getAction().equals(Action.LEFT_CLICK_AIR) || e.getAction().equals(Action.LEFT_CLICK_BLOCK);
		return false;
	}

}
