package com.sucy.skill.example.ranger.passive;

import com.sucy.skill.api.skill.ClassSkill;
import com.sucy.skill.api.skill.PassiveSkill;
import com.sucy.skill.api.skill.SkillAttribute;
import com.sucy.skill.api.skill.SkillType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;

import java.util.HashMap;
import java.util.UUID;

/**
 * Passive that causes arrows to travel faster
 */
public class Precision extends ClassSkill implements PassiveSkill, Listener {

    public static final String NAME = "Precision";
    private static final String
        SPEED = "Speed (%)";

    private HashMap<UUID, Integer> active = new HashMap<UUID, Integer>();

    public Precision() {
        super(NAME, SkillType.PASSIVE, Material.BOW, 4);

        description.add("Hones your bow skills,");
        description.add("letting you fire arrows");
        description.add("faster passively.");

        setAttribute(SkillAttribute.LEVEL, 1, 1);
        setAttribute(SkillAttribute.COST, 1, 0);

        setAttribute(SPEED, 120, 10);
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
    public void onFireArrow(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() instanceof Player) {
            Player p = (Player)event.getEntity().getShooter();
            if (active.containsKey(p.getUniqueId())) {
                double m = getAttribute(SPEED, active.get(p.getUniqueId())) / 100;
                event.getEntity().setVelocity(event.getEntity().getVelocity().multiply(m));
            }
        }
    }
}
