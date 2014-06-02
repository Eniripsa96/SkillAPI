package com.sucy.skill.example.wizard.active;

import com.sucy.skill.api.skill.ClassSkill;
import com.sucy.skill.api.skill.SkillAttribute;
import com.sucy.skill.api.skill.SkillShot;
import com.sucy.skill.api.skill.SkillType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * Created by Steven on 4/30/14.
 */
public class Blink extends ClassSkill implements SkillShot {

    public static final String NAME = "Blink";
    private static final String
        DISTANCE = "Distance";

    public Blink() {
        super(NAME, SkillType.SELF, Material.ENDER_PEARL, 5);

        description.add("Teleports yourself a");
        description.add("short distance forward");
        description.add("using the power of the");
        description.add("ender pearls.");

        setAttribute(SkillAttribute.LEVEL, 1, 1);
        setAttribute(SkillAttribute.COST, 1, 0);
        setAttribute(SkillAttribute.COOLDOWN, 8, -1);
        setAttribute(SkillAttribute.MANA, 18, -2);

        setAttribute(DISTANCE, 4, 0.5);
    }


    @Override
    public boolean cast(Player player, int level) {
        double distance = getAttribute(DISTANCE, level);

        if (player.getLocation().getBlock().getType().isSolid()) return false;
        Vector vec = player.getLocation().getDirection();
        vec.multiply(distance / vec.length());

        double x = Math.abs(vec.getX());
        double z = Math.abs(vec.getZ());
        int steps = 2 * (int)Math.max(x, z);

        double xScale = vec.getX() / steps;
        double zScale = vec.getZ() / steps;

        Location loc = player.getLocation().add(0, 0.5, 0);
        for (int i = 0; i < steps; i++) {
            loc.add(xScale, 0, zScale);
            if (loc.getBlock().getType().isSolid() && loc.getBlock().getRelative(0, 1, 0).getType().isSolid()) {
                loc.subtract(xScale, 0, zScale);
                break;
            }
        }
        player.teleport(loc.add(0, -0.5, 0));
        return true;
    }
}
