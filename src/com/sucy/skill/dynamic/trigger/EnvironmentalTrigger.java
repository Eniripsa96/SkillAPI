package com.sucy.skill.dynamic.trigger;

import com.sucy.skill.api.Settings;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Map;

/**
 * SkillAPI © 2018
 * com.sucy.skill.dynamic.trigger.BlockBreakTrigger
 */
public class EnvironmentalTrigger implements Trigger<EntityDamageEvent> {

    /** {@inheritDoc} */
    @Override
    public String getKey() {
        return "ENVIRONMENT_DAMAGE";
    }

    /** {@inheritDoc} */
    @Override
    public Class<EntityDamageEvent> getEvent() {
        return EntityDamageEvent.class;
    }

    /** {@inheritDoc} */
    @Override
    public boolean shouldTrigger(final EntityDamageEvent event, final int level, final Settings settings) {
        final String type = settings.getString("type", "any");
        return type.equalsIgnoreCase("ANY") || type.equalsIgnoreCase(event.getEntity().getType().name());
    }

    /** {@inheritDoc} */
    @Override
    public void setValues(final EntityDamageEvent event, final Map<String, Object> data) {
        data.put("api-taken", event.getDamage());
    }

    /** {@inheritDoc} */
    @Override
    public LivingEntity getCaster(final EntityDamageEvent event) {
        if (event.getEntity() instanceof LivingEntity) {
            return (LivingEntity) event.getEntity();
        } else {
            return null;
        }
    }

    /** {@inheritDoc} */
    @Override
    public LivingEntity getTarget(final EntityDamageEvent event, final Settings settings) {
        return getCaster(event);
    }
}
