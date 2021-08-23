package com.sucy.skill.dynamic.trigger;

import com.sucy.skill.api.Settings;
import com.sucy.skill.api.event.PlayerLoadCompleteEvent;

import org.bukkit.entity.LivingEntity;

import java.util.Map;

/**
 * SkillAPI © 2018
 * com.sucy.skill.dynamic.trigger.BlockBreakTrigger
 */
public class LoadTrigger implements Trigger<PlayerLoadCompleteEvent> {

    @Override
    public String getKey() {
        return "LOAD";
    }

    @Override
    public Class<PlayerLoadCompleteEvent> getEvent() {
        return PlayerLoadCompleteEvent.class;
    }

    /** {@inheritDoc} */
    @Override
    public boolean shouldTrigger(final PlayerLoadCompleteEvent event, final int level, final Settings settings) {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public void setValues(final PlayerLoadCompleteEvent event, final Map<String, Object> data) { }

    /** {@inheritDoc} */
    @Override
    public LivingEntity getCaster(final PlayerLoadCompleteEvent event) {
        return event.getPlayer();
    }

    /** {@inheritDoc} */
    @Override
    public LivingEntity getTarget(final PlayerLoadCompleteEvent event, final Settings settings) {
        return event.getPlayer();
    }
}
