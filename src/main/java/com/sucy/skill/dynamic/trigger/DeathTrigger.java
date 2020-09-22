package com.sucy.skill.dynamic.trigger;

import com.sucy.skill.api.Settings;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.Map;

/**
 * SkillAPI © 2018
 * com.sucy.skill.dynamic.trigger.BlockBreakTrigger
 */
public class DeathTrigger implements Trigger<EntityDeathEvent> {

    /** {@inheritDoc} */
    @Override
    public String getKey() {
        return "DEATH";
    }

    /** {@inheritDoc} */
    @Override
    public Class<EntityDeathEvent> getEvent() {
        return EntityDeathEvent.class;
    }

    /** {@inheritDoc} */
    @Override
    public boolean shouldTrigger(final EntityDeathEvent event, final int level, final Settings settings) {
        return !isTargetingKiller(settings) || event.getEntity().getKiller() != null;
    }

    /** {@inheritDoc} */
    @Override
    public void setValues(final EntityDeathEvent event, final Map<String, Object> data) { }

    /** {@inheritDoc} */
    @Override
    public LivingEntity getCaster(final EntityDeathEvent event) {
        return event.getEntity();
    }

    /** {@inheritDoc} */
    @Override
    public LivingEntity getTarget(final EntityDeathEvent event, final Settings settings) {
        return isTargetingKiller(settings) ? event.getEntity().getKiller() : event.getEntity();
    }

    private boolean isTargetingKiller(final Settings settings) {
        return settings.getString("killer", "false").equalsIgnoreCase("true");
    }
}
