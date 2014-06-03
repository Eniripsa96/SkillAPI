package com.sucy.skill.example.warrior.passive;

import com.sucy.skill.api.skill.ClassSkill;
import com.sucy.skill.api.skill.PassiveSkill;
import com.sucy.skill.api.skill.SkillAttribute;
import com.sucy.skill.api.skill.SkillType;
import org.bukkit.Material;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;
import java.util.UUID;

/**
 * Damage reduction passive
 */
public class Toughness extends ClassSkill implements PassiveSkill, Listener {

    public static final String NAME = "Toughness";
    private static final String
        PVE = "Reduction (%)";

    private HashMap<UUID, Integer> active = new HashMap<UUID, Integer>();

    public Toughness() {
        super(NAME, SkillType.PASSIVE, Material.IRON_CHESTPLATE, 4);

        description.add("Reduces the damage taken");
        description.add("from mobs passively.");

        setAttribute(SkillAttribute.LEVEL, 1, 1);
        setAttribute(SkillAttribute.COST, 1, 0);

        setAttribute(PVE, 20, 5);
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

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (active.containsKey(event.getEntity().getUniqueId())) {
            int level = active.get(event.getEntity().getUniqueId());
            if (event.getDamager() instanceof Monster) {
                event.setDamage(event.getDamage() * (1 - getAttribute(PVE, level) / 100));
            }
        }
    }
}
