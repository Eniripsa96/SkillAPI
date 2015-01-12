package com.sucy.skill.dynamic.mechanic;

import com.rit.sucy.version.VersionManager;
import com.sucy.skill.api.event.SkillDamageEvent;
import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Executes a command for each target
 */
public class CommandMechanic extends EffectComponent
{
    private static final String COMMAND = "command";
    private static final String TYPE = "type";

    /**
     * Executes the component
     *
     * @param caster  caster of the skill
     * @param level   level of the skill
     * @param targets targets to apply to
     *
     * @return true if applied to something, false otherwise
     */
    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets)
    {
        if (targets.size() == 0 || !settings.has(COMMAND)) return false;

        String command = settings.getString(COMMAND);
        String type = settings.getString(TYPE).toLowerCase();
        boolean worked = false;
        for (LivingEntity t : targets) {
            if (t instanceof Player) {
                Player p = (Player)t;
                worked = true;
                String filtered = command.replace("{player}", p.getName());
                if (type.equals("op")) {
                    boolean op = p.isOp();
                    p.setOp(true);
                    Bukkit.getServer().dispatchCommand(p, filtered);
                    p.setOp(op);
                }
                else Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), filtered);
            }
        }

        return worked;

    }
}
