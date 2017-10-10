package com.sucy.skill.dynamic.condition;

import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * SkillAPI © 2017
 * com.sucy.skill.dynamic.condition.CeilingCondition
 */
public class CeilingCondition extends EffectComponent
{
    private static final String DISTANCE = "distance";
    private static final String AT_LEAST = "at-least";

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
    public boolean execute(final LivingEntity caster, final int level, final List<LivingEntity> targets)
    {
        final boolean isSelf = targets.size() == 1 && targets.get(0) == caster;
        final boolean atLeast = settings.getBool(AT_LEAST, true);
        final int distance = (int)attr(caster, DISTANCE, level, 5, isSelf);

        // Check the biomes of the targets
        final ArrayList<LivingEntity> list = new ArrayList<LivingEntity>();
        for (final LivingEntity target : targets) {
            final Block block = target.getLocation().getBlock();
            boolean ceiling = false;
            for (int i = 2; i < distance; i++) {
                if (block.getRelative(0, i, 0).getType().isSolid()) {
                    ceiling = true;
                    break;
                }
            }
            if (ceiling != atLeast) {
                list.add(target);
            }
        }
        return executeChildren(caster, level, list);
    }
}
