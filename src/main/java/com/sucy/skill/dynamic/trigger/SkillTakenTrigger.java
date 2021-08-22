package com.sucy.skill.dynamic.trigger;

import com.sucy.skill.api.Settings;
import com.sucy.skill.api.event.SkillDamageEvent;
import org.bukkit.entity.LivingEntity;

import java.util.Map;

/**
 * SkillAPI © 2018
 * com.sucy.skill.dynamic.trigger.BlockBreakTrigger
 */
public class SkillTakenTrigger extends SkillTrigger {

    /** {@inheritDoc} */
    @Override
    public String getKey() {
        return "TOOK_SKILL_DAMAGE";
    }

    /** {@inheritDoc} */
    @Override
    public LivingEntity getCaster(final SkillDamageEvent event) {
        return event.getTarget();
    }

    /** {@inheritDoc} */
    @Override
    public LivingEntity getTarget(final SkillDamageEvent event, final Settings settings) {
        return isUsingTarget(settings) ? event.getDamager() : event.getTarget();
    }

    /** {@inheritDoc} */
    @Override
    public void setValues(final SkillDamageEvent event, final Map<String, Object> data) {
        data.put("api-taken", event.getDamage());
    }
}
