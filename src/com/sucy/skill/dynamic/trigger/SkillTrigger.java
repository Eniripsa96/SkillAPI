package com.sucy.skill.dynamic.trigger;

import com.sucy.skill.api.Settings;
import com.sucy.skill.api.event.SkillDamageEvent;

/**
 * SkillAPI © 2018
 * com.sucy.skill.dynamic.trigger.BlockBreakTrigger
 */
public abstract class SkillTrigger implements Trigger<SkillDamageEvent> {

    /** {@inheritDoc} */
    @Override
    public Class<SkillDamageEvent> getEvent() {
        return SkillDamageEvent.class;
    }

    /** {@inheritDoc} */
    @Override
    public boolean shouldTrigger(final SkillDamageEvent event, final int level, final Settings settings) {
        final double min = settings.getDouble("dmg-min");
        final double max = settings.getDouble("dmg-max");
        return event.getDamage() >= min && event.getDamage() <= max;
    }

    boolean isUsingTarget(final Settings settings) {
        return settings.getString("target", "true").equalsIgnoreCase("false");
    }
}
