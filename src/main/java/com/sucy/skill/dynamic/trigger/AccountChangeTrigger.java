package com.sucy.skill.dynamic.trigger;

import com.sucy.skill.api.Settings;
import com.sucy.skill.api.event.PlayerAccountChangeEvent;

import org.bukkit.entity.LivingEntity;

import java.util.Map;

/**
 * SkillAPI © 2018
 * com.sucy.skill.dynamic.trigger.BlockBreakTrigger
 */
public class AccountChangeTrigger implements Trigger<PlayerAccountChangeEvent> {

    @Override
    public String getKey() {
        return "ACCOUNT_CHANGE";
    }

    @Override
    public Class<PlayerAccountChangeEvent> getEvent() {
        return PlayerAccountChangeEvent.class;
    }

    /** {@inheritDoc} */
    @Override
    public boolean shouldTrigger(final PlayerAccountChangeEvent event, final int level, final Settings settings) {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public void setValues(final PlayerAccountChangeEvent event, final Map<String, Object> data) { }

    /** {@inheritDoc} */
    @Override
    public LivingEntity getCaster(final PlayerAccountChangeEvent event) {
        return event.getNewAccount().getPlayer();
    }

    /** {@inheritDoc} */
    @Override
    public LivingEntity getTarget(final PlayerAccountChangeEvent event, final Settings settings) {
        return event.getNewAccount().getPlayer();
    }
}
