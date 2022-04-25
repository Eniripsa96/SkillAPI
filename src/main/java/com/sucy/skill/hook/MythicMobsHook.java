package com.sucy.skill.hook;

import org.bukkit.entity.LivingEntity;
import io.lumine.mythic.bukkit.MythicBukkit;

/**
 * SkillAPI © 2017
 * com.sucy.skill.hook.MythicMobsHook
 */
public class MythicMobsHook {

    public static void taunt(final LivingEntity target, final LivingEntity source, final double amount) {
        if (amount > 0) {
            MythicBukkit.inst().getAPIHelper().addThreat(target, source, amount);
        }
        else if (amount < 0) {
        	MythicBukkit.inst().getAPIHelper().reduceThreat(target, source, -amount);
        }
    }

    public static boolean isMonster(final LivingEntity target) {
        return MythicBukkit.inst().getAPIHelper().isMythicMob(target);
    }
}
