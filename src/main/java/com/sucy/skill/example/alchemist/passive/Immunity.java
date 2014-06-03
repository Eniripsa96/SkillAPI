package com.sucy.skill.example.alchemist.passive;

import com.sucy.skill.api.skill.ClassSkill;
import com.sucy.skill.api.skill.PassiveSkill;
import com.sucy.skill.api.skill.SkillAttribute;
import com.sucy.skill.api.skill.SkillType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class Immunity extends ClassSkill implements PassiveSkill, Listener {

    public static final String NAME = "Immunity";

    private HashMap<UUID, Integer> active = new HashMap<UUID, Integer>();

    public Immunity() {
        super(NAME, SkillType.PASSIVE, Material.DIAMOND, 5);

        description.add(ChatColor.DARK_GREEN + "Rank 1: " + ChatColor.GOLD + "Poison");
        description.add(ChatColor.DARK_GREEN + "Rank 2: " + ChatColor.GOLD + "Wither");
        description.add(ChatColor.DARK_GREEN + "Rank 3: " + ChatColor.GOLD + "Slow");
        description.add(ChatColor.DARK_GREEN + "Rank 4: " + ChatColor.GOLD + "Weakness");
        description.add(ChatColor.DARK_GREEN + "Rank 5: " + ChatColor.GOLD + "All Potions");
        description.add("- effects are cumulative -");
        description.add("");
        description.add("Grants immunity to some");
        description.add("potion effects.");

        setAttribute(SkillAttribute.LEVEL, 1, 1);
        setAttribute(SkillAttribute.COST, 1, 0);
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

    public class ImmunityTask extends BukkitRunnable {

        @Override
        public void run() {
            for (UUID id : active.keySet()) {
                Player p = api.getServer().getPlayer(id);
                int level = active.get(id);

                if (level >= 1) {
                    p.removePotionEffect(PotionEffectType.POISON);
                }
                if (level >= 2) {
                    p.removePotionEffect(PotionEffectType.WITHER);
                }
                if (level >= 3) {
                    p.removePotionEffect(PotionEffectType.SLOW);
                }
                if (level >= 4) {
                    p.removePotionEffect(PotionEffectType.WEAKNESS);
                }
                if (level >= 5) {
                    p.removePotionEffect(PotionEffectType.BLINDNESS);
                    p.removePotionEffect(PotionEffectType.SLOW_DIGGING);
                    p.removePotionEffect(PotionEffectType.CONFUSION);
                    p.removePotionEffect(PotionEffectType.HUNGER);
                }
            }
        }
    }
}
