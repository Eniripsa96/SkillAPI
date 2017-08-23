package com.sucy.skill.dynamic.mechanic;

import com.sucy.skill.api.util.Buff;
import com.sucy.skill.api.util.BuffManager;
import com.sucy.skill.api.util.BuffType;
import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.entity.LivingEntity;

import java.util.List;

/**
 * SkillAPI © 2017
 * com.sucy.skill.dynamic.mechanic.BuffMechanic
 */
public class BuffMechanic extends EffectComponent {

    private static final String MODIFIER = "modifier";
    private static final String TYPE     = "type";
    private static final String VALUE    = "value";
    private static final String SECONDS  = "seconds";

    /**
     * Executes the component
     *
     * @param caster  caster of the skill
     * @param level   level of the skill
     * @param targets targets to apply to
     *
     * @return true if applied to something, false otherwise
     */
    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets) {
        if (targets.size() == 0) return false;

        boolean isSelf = targets.size() == 1 && targets.get(0) == caster;
        BuffType buffType = BuffType.valueOf(settings.getString(TYPE, "DAMAGE"));
        boolean percent = settings.getString(MODIFIER, "flat").toLowerCase().equals("multiplier");
        double value = attr(caster, VALUE, level, 1.0, isSelf);
        double seconds = attr(caster, SECONDS, level, 3.0, isSelf);
        int ticks = (int) (seconds * 20);
        for (LivingEntity target : targets) {
            BuffManager.addBuff(
                    target,
                    buffType,
                    new Buff(this.skill.getName() + "-" + percent, value, percent),
                    ticks);
        }
        return targets.size() > 0;
    }
}
