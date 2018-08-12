package com.sucy.skill.hook;

import io.lumine.xikage.mythicmobs.MythicMobs;
import org.bukkit.entity.LivingEntity;

/**
 * SkillAPI © 2017
 * com.sucy.skill.hook.MythicMobsHook
 */
public class MythicMobsHook {

    public static void taunt(final LivingEntity target, final LivingEntity source, final double amount) {
        if (amount > 0) {
            MythicMobs.inst().getAPIHelper().addThreat(target, source, amount);
        }
        else if (amount < 0) {
            MythicMobs.inst().getAPIHelper().reduceThreat(target, source, -amount);
        }
    }

    public static boolean isMonster(final LivingEntity target) {
        return MythicMobs.inst().getAPIHelper().isMythicMob(target);
    }
}
