package com.sucy.skill.dynamic.trigger;

import com.sucy.skill.api.Settings;
import com.sucy.skill.api.event.PhysicalDamageEvent;
import org.bukkit.entity.LivingEntity;

import java.util.Map;

/**
 * SkillAPI © 2018
 * com.sucy.skill.dynamic.trigger.BlockBreakTrigger
 */
public class PhysicalTakenTrigger extends PhysicalTrigger {

    /** {@inheritDoc} */
    @Override
    public String getKey() {
        return "TOOK_PHYSICAL_DAMAGE";
    }

    /** {@inheritDoc} */
    @Override
    public LivingEntity getCaster(final PhysicalDamageEvent event) {
        return event.getTarget();
    }

    /** {@inheritDoc} */
    @Override
    public LivingEntity getTarget(final PhysicalDamageEvent event, final Settings settings) {
        return isUsingTarget(settings) ? event.getDamager() : event.getTarget();
    }

    /** {@inheritDoc} */
    @Override
    public void setValues(final PhysicalDamageEvent event, final Map<String, Object> data) {
        data.put("api-taken", event.getDamage());
    }
}
