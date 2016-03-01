package com.sucy.skill.dynamic.target;

import com.sucy.skill.dynamic.EffectComponent;
import com.sucy.skill.dynamic.TempEntity;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Applies child components to a location using the caster's faced direction
 */
public class LocationTarget extends EffectComponent
{
    private static final String RANGE  = "range";
    private static final String GROUND = "ground";

    private static final HashSet<Byte> NULL_SET = null;

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
        boolean worked = false;
        boolean isSelf = targets.size() == 1 && targets.get(0) == caster;
        double range = attr(caster, RANGE, level, 5.0, isSelf);
        boolean groundOnly = !settings.getString(GROUND, "true").toLowerCase().equals("false");
        for (LivingEntity t : targets)
        {
            Location loc;
            Block b = t.getTargetBlock(NULL_SET, (int) Math.ceil(range));
            if (b == null && !groundOnly)
            {
                loc = t.getLocation().add(t.getLocation().getDirection().multiply(range));
            }
            else if (b != null)
            {
                loc = b.getLocation();
            }
            else
            {
                continue;
            }

            ArrayList<LivingEntity> list = new ArrayList<LivingEntity>();
            list.add(new TempEntity(loc));
            worked = executeChildren(caster, level, list) || worked;
        }
        return worked;
    }
}
