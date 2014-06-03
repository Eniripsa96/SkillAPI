package com.sucy.skill.example.bard.passive;

import com.sucy.skill.api.DamageModifier;
import com.sucy.skill.api.skill.ClassSkill;
import com.sucy.skill.api.skill.PassiveSkill;
import com.sucy.skill.api.skill.SkillAttribute;
import com.sucy.skill.api.skill.SkillType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class Motivation extends ClassSkill implements PassiveSkill {

    public static final String NAME = "Motivation";
    private static final String
        DAMAGE = "Bonus Damage",
        RADIUS = "Radius";

    private static MotivationTask task;

    private static HashMap<UUID, Integer> active = new HashMap<UUID, Integer>();

    public Motivation() {
        super(NAME, SkillType.PASSIVE, Material.DIAMOND, 5);

        description.add(ChatColor.GOLD + "+5 Morale");
        description.add("Nearby allies gain a");
        description.add("small amount of damage");

        setAttribute(SkillAttribute.LEVEL, 1, 1);
        setAttribute(SkillAttribute.COST, 1, 0);

        setAttribute(DAMAGE, 2, 0);
        setAttribute(RADIUS, 2, 2);

        if (task != null) {
            task.cancel();
        }
        task = new MotivationTask();
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

    public class MotivationTask extends BukkitRunnable {

        @Override
        public void run() {
            for (UUID id : active.keySet()) {
                Player player = api.getServer().getPlayer(id);
                int damage = (int)getAttribute(DAMAGE, active.get(id));
                api.getStatusHolder(player).addDamageModifier(new DamageModifier(damage, 20));
                double rSq = getAttribute(RADIUS, active.get(id));
                rSq *= rSq;
                for (Player p : player.getWorld().getPlayers()) {
                    if (p.getLocation().distanceSquared(player.getLocation()) < rSq) {
                        api.getStatusHolder(p).addDamageModifier(new DamageModifier(damage, 20));
                    }
                }
            }
        }
    }
}
