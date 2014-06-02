package com.sucy.skill.example.warrior.passive;

import com.sucy.skill.api.PlayerSkills;
import com.sucy.skill.api.skill.ClassSkill;
import com.sucy.skill.api.skill.PassiveSkill;
import com.sucy.skill.api.skill.SkillAttribute;
import com.sucy.skill.api.skill.SkillType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

/**
 * Damage reduction passive
 */
public class Recovery extends ClassSkill implements PassiveSkill {

    public static final String NAME = "Recovery";
    private static final String
        HP5 = "HP5";

    private static RecoveryTask task;

    private HashMap<UUID, Integer> active = new HashMap<UUID, Integer>();

    public Recovery() {
        super(NAME, SkillType.PASSIVE, Material.APPLE, 4);

        description.add("Passively causes you");
        description.add("to regenerate health");
        description.add("over time.");

        setAttribute(SkillAttribute.LEVEL, 1, 1);
        setAttribute(SkillAttribute.COST, 1, 0);

        setAttribute(HP5, 2, 1);

        if (task != null) {
            task.cancel();
        }
        task = new RecoveryTask();
        task.runTaskTimer(api, 100, 100);
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

    public class RecoveryTask extends BukkitRunnable {

        @Override
        public void run() {
            for (UUID id : active.keySet()) {
                PlayerSkills player = api.getPlayer(id);
                if (player.getPlayer().getFoodLevel() > 0) player.heal(active.get(id));
            }
        }
    }
}
