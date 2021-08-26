package com.sucy.skill.dynamic.trigger;

import com.sucy.skill.api.Settings;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityShootBowEvent;

import java.util.Map;

/**
 * SkillAPI © 2018
 * com.sucy.skill.dynamic.trigger.BlockBreakTrigger
 */
public class ShootBowTrigger implements Trigger<EntityShootBowEvent> {

    @Override
    public String getKey() {
        return "SHOOTBOW";
    }

    @Override
    public Class<EntityShootBowEvent> getEvent() {
        return EntityShootBowEvent.class;
    }

    /** {@inheritDoc} */
    @Override
    public boolean shouldTrigger(final EntityShootBowEvent event, final int level, final Settings settings) {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public void setValues(final EntityShootBowEvent event, final Map<String, Object> data) { }

    /** {@inheritDoc} */
    @Override
    public LivingEntity getCaster(final EntityShootBowEvent event) {
        return event.getEntity();
    }

    /** {@inheritDoc} */
    @Override
    public LivingEntity getTarget(final EntityShootBowEvent event, final Settings settings) {
        return event.getEntity();
    }
}
