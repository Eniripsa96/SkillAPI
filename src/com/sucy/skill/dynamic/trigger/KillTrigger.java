package com.sucy.skill.dynamic.trigger;

import com.sucy.skill.api.Settings;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.Map;

/**
 * SkillAPI © 2018
 * com.sucy.skill.dynamic.trigger.BlockBreakTrigger
 */
public class KillTrigger implements Trigger<EntityDeathEvent> {

    /** {@inheritDoc} */
    @Override
    public String getKey() {
        return "KILL";
    }

    /** {@inheritDoc} */
    @Override
    public Class<EntityDeathEvent> getEvent() {
        return EntityDeathEvent.class;
    }

    /** {@inheritDoc} */
    @Override
    public boolean shouldTrigger(final EntityDeathEvent event, final int level, final Settings settings) {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public void setValues(final EntityDeathEvent event, final Map<String, Object> data) { }

    /** {@inheritDoc} */
    @Override
    public LivingEntity getCaster(final EntityDeathEvent event) {
        return event.getEntity().getKiller();
    }

    /** {@inheritDoc} */
    @Override
    public LivingEntity getTarget(final EntityDeathEvent event, final Settings settings) {
        return event.getEntity().getKiller();
    }
}
