package com.sucy.skill.example.wizard.active;

import com.sucy.skill.api.skill.ClassSkill;
import com.sucy.skill.api.skill.SkillAttribute;
import com.sucy.skill.api.skill.SkillShot;
import com.sucy.skill.api.skill.SkillType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;

public class ChainLightning extends ClassSkill implements SkillShot, Listener {

    public static final String NAME = "Chain Lightning";
    private final HashMap<UUID, Long> timers = new HashMap<UUID, Long>();

    public ChainLightning() {
        super(NAME, SkillType.LINEAR, Material.TORCH, 5);

        description.add("Strikes all enemies in a line");
        description.add("with lightning, setting them on");
        description.add("fire and dealing damage while");
        description.add("granting yourself lightning");
        description.add("immunity for 3 seconds.");

        setAttribute(SkillAttribute.LEVEL, 1, 1);
        setAttribute(SkillAttribute.COST, 1, 0);
        setAttribute(SkillAttribute.COOLDOWN, 12, -1);
        setAttribute(SkillAttribute.MANA, 25, -2);
        setAttribute(SkillAttribute.RANGE, 4, 1);
    }

    @Override
    public boolean cast(Player player, int level) {
        double range = getAttribute(SkillAttribute.RANGE, level);
        Location loc = player.getLocation();
        Vector change = loc.getDirection();
        change.setY(0);
        change.multiply(1 / change.length());
        loc.add(change);
        loc.add(change);
        for (int i = 1; i < range; i++) {
            loc.add(change);
            loc.getWorld().strikeLightning(loc);
        }
        timers.put(player.getUniqueId(), System.currentTimeMillis() + 3000);

        return true;
    }

    @EventHandler
    public void onDamaged(EntityDamageEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.LIGHTNING
                && timers.containsKey(event.getEntity().getUniqueId())
                && timers.get(event.getEntity().getUniqueId()) > System.currentTimeMillis()) {
            event.setCancelled(true);
        }
    }
}
