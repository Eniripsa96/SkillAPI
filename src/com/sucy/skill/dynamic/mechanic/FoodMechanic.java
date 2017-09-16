package com.sucy.skill.dynamic.mechanic;

import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * SkillAPI © 2017
 * com.sucy.skill.dynamic.mechanic.FoodMechanic
 */
public class FoodMechanic extends EffectComponent
{
    private static final String FOOD = "food";
    private static final String SATURATION = "saturation";

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
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets)
    {
        boolean isSelf = targets.size() == 1 && targets.get(0) == caster;
        double food = attr(caster, FOOD, level, 1.0, isSelf);
        double saturation = attr(caster, SATURATION, level, 1.0, isSelf);
        for (LivingEntity target : targets) {
            if (target instanceof Player) {
                Player player = (Player) target;
                player.setFoodLevel(Math.min(20, Math.max(0, (int)food + player.getFoodLevel())));
                player.setSaturation(Math.min(player.getFoodLevel(), Math.max(0, player.getSaturation() + (float)saturation)));
            }
        }
        return targets.size() > 0;
    }
}
