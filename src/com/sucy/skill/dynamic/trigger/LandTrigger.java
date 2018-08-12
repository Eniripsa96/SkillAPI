package com.sucy.skill.dynamic.trigger;

import com.sucy.skill.api.Settings;
import com.sucy.skill.api.event.PlayerLandEvent;
import org.bukkit.entity.LivingEntity;

import java.util.Map;

/**
 * SkillAPI © 2018
 * com.sucy.skill.dynamic.trigger.BlockBreakTrigger
 */
public class LandTrigger implements Trigger<PlayerLandEvent> {

    /** {@inheritDoc} */
    @Override
    public String getKey() {
        return "LAND";
    }

    /** {@inheritDoc} */
    @Override
    public Class<PlayerLandEvent> getEvent() {
        return PlayerLandEvent.class;
    }

    /** {@inheritDoc} */
    @Override
    public boolean shouldTrigger(final PlayerLandEvent event, final int level, final Settings settings) {
        final double minDistance = settings.getDouble("min-distance", 0);
        return event.getDistance() >= minDistance;
    }

    /** {@inheritDoc} */
    @Override
    public void setValues(final PlayerLandEvent event, final Map<String, Object> data) {
        data.put("api-distance", event.getDistance());
    }

    /** {@inheritDoc} */
    @Override
    public LivingEntity getCaster(final PlayerLandEvent event) {
        return event.getPlayer();
    }

    /** {@inheritDoc} */
    @Override
    public LivingEntity getTarget(final PlayerLandEvent event, final Settings settings) {
        return event.getPlayer();
    }
}
