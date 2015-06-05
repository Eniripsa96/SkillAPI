package com.sucy.skill.dynamic.mechanic;

import com.rit.sucy.text.TextFormatter;
import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Sends a message to each player target
 */
public class MessageMechanic extends EffectComponent
{
    private static final String MESSAGE = "message";

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
        if (targets.size() == 0 || !settings.has(MESSAGE))
        {
            return false;
        }

        String message = TextFormatter.colorString(settings.getString(MESSAGE));
        boolean worked = false;
        for (LivingEntity target : targets)
        {
            if (target instanceof Player)
            {
                ((Player) target).sendMessage(message);
                worked = true;
            }
        }
        return worked;
    }
}
