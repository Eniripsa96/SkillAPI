package com.sucy.skill.dynamic.mechanic;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import java.util.List;

/**
 * SkillAPI © 2017
 * com.sucy.skill.dynamic.mechanic.FoodMechanic
 */
public class FoodMechanic extends MechanicComponent {
    private static final String FOOD = "food";
    private static final String SATURATION = "saturation";

    @Override
    public String getKey() {
        return "food";
    }

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
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets, boolean isCrit) {
        double food = parseValues(caster, FOOD, level, 1.0, false);
        double saturation = parseValues(caster, SATURATION, level, 1.0, false);
        for (LivingEntity target : targets) {
            if (target instanceof Player) {
                Player player = (Player) target;
                FoodLevelChangeEvent e = new FoodLevelChangeEvent(player, Math.min(20, Math.max(0, (int) food + player.getFoodLevel())));
                Bukkit.getPluginManager().callEvent(e);
                if (e.isCancelled()) {
                	return targets.size() > 0;
                }
                player.setFoodLevel(Math.min(20, Math.max(0, (int) food + player.getFoodLevel())));
                player.setSaturation(Math.min(
                        player.getFoodLevel(),
                        Math.max(0, player.getSaturation() + (float) saturation)));
            }
        }
        return targets.size() > 0;
    }
}
