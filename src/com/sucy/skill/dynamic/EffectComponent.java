package com.sucy.skill.dynamic;

import com.sucy.skill.api.Settings;
import com.sucy.skill.api.player.PlayerSkill;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * A component for dynamic skills which takes care of one effect
 */
public abstract class EffectComponent
{
    private final ArrayList<EffectComponent> children = new ArrayList<EffectComponent>();

    /**
     * The settings for the component
     */
    protected final Settings settings = new Settings();

    /**
     * The skill containing this component
     */
    public PlayerSkill skill;

    /**
     * Adds a new component to the effect's children. Some effects
     * do not use these so be aware what you're adding it to.
     *
     * @param child child component to add
     */
    public void add(EffectComponent child)
    {
        children.add(child);
    }

    /**
     * Executes the children of the component using the given targets
     *
     * @param caster  caster of the skill
     * @param level   level of the skill
     * @param targets targets to execute on
     *
     * @return true if executed, false if conditions not met
     */
    protected boolean executeChildren(LivingEntity caster, int level, List<LivingEntity> targets)
    {
        boolean worked = false;
        for (EffectComponent child : children)
        {
            worked = worked || child.execute(caster, level, targets);
        }
        return worked;
    }

    /**
     * Executes the component (to be implemented)
     *
     * @param caster  caster of the skill
     * @param level   level of the skill
     * @param targets targets to execute on
     *
     * @return true if executed, false if conditions not met
     */
    public abstract boolean execute(LivingEntity caster, int level, List<LivingEntity> targets);
}
