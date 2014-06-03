package com.sucy.skill.example.warrior.passive;

import com.sucy.skill.api.DamageModifier;
import com.sucy.skill.api.PlayerSkills;
import com.sucy.skill.api.skill.ClassSkill;
import com.sucy.skill.api.skill.PassiveSkill;
import com.sucy.skill.api.skill.SkillAttribute;
import com.sucy.skill.api.skill.SkillType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

/**
 * Damage reduction passive
 */
public class Berserk extends ClassSkill implements PassiveSkill {

    public static final String NAME = "Berserk";
    private static final String
        HEALTH = "Health (%)",
        DAMAGE = "Bonus Damage (%)";

    private static BerserkTask task;

    private HashMap<UUID, Integer> active = new HashMap<UUID, Integer>();

    public Berserk() {
        super(NAME, SkillType.PASSIVE, Material.APPLE, 3);

        description.add("When your health gets");
        description.add("low, you go berserk,");
        description.add("gaining damage and");
        description.add("movement speed.");

        setAttribute(SkillAttribute.LEVEL, 1, 1);
        setAttribute(SkillAttribute.COST, 1, 0);

        setAttribute(HEALTH, 25, 5);
        setAttribute(DAMAGE, 15, 5);

        if (task != null) {
            task.cancel();
        }
        task = new BerserkTask();
        task.runTaskTimer(api, 20, 20);
    }

    @Override
    public void onUpgrade(Player player, int level) {
        onInitialize(player, level);
    }

    @Override
    public void onInitialize(Player player, int level) {
        active.put(player.getUniqueId(), level);
    }

    @Override
    public void stopEffects(Player player, int level) {
        active.remove(player.getUniqueId());
    }

    public class BerserkTask extends BukkitRunnable {

        @Override
        public void run() {
            for (UUID id : active.keySet()) {
                PlayerSkills player = api.getPlayer(id);
                Player p = player.getPlayer();
                int level = active.get(id);
                if (p.getHealth() / p.getMaxHealth() < getAttribute(HEALTH, level) / 100) {
                    player.getStatusData().addDamageModifier(new DamageModifier(1 + getAttribute(DAMAGE, level) / 100, 1000));
                    api.getServer().getPlayer(id).addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 25, 1), true);
                }
            }
        }
    }
}
