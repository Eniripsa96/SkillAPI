package com.sucy.skill.example.hunter.passive;

import com.rit.sucy.player.Protection;
import com.sucy.skill.api.skill.ClassSkill;
import com.sucy.skill.api.skill.PassiveSkill;
import com.sucy.skill.api.skill.SkillAttribute;
import com.sucy.skill.api.skill.SkillType;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class WildHunt extends ClassSkill implements PassiveSkill {

    public static final String NAME = "Wild Hunt";
    private static final String
        RADIUS = "Radius";

    private static final PotionEffect SPEED_EFFECT = new PotionEffect(PotionEffectType.SPEED, 25, 1);

    private static WildHuntTask task;

    private HashMap<UUID, Integer> active = new HashMap<UUID, Integer>();

    public WildHunt() {
        super(NAME, SkillType.PASSIVE, Material.GOLD_BOOTS, 5);

        description.add("Passively grants bonus");
        description.add("movement speed to nearby");
        description.add("allies including yourself.");

        setAttribute(SkillAttribute.LEVEL, 1, 1);
        setAttribute(SkillAttribute.COST, 1, 0);

        setAttribute(RADIUS, 4, 2);

        if (task != null) {
            task.cancel();
        }
        task = new WildHuntTask();
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

    public class WildHuntTask extends BukkitRunnable {

        @Override
        public void run() {
            for (UUID id : active.keySet()) {
                Player p = api.getServer().getPlayer(id);

                p.addPotionEffect(SPEED_EFFECT, true);
                double radius = getAttribute(RADIUS, active.get(id));
                for (Entity entity : p.getNearbyEntities(radius, radius, radius)) {
                    if (entity instanceof LivingEntity) {
                        LivingEntity target = (LivingEntity)entity;
                        if (Protection.isAlly(p, target)) {
                            target.addPotionEffect(SPEED_EFFECT, true);
                        }
                    }
                }
            }
        }
    }
}
