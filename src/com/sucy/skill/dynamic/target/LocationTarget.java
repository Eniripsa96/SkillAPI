package com.sucy.skill.dynamic.target;

import com.sucy.skill.dynamic.EffectComponent;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Bat;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Applies child components to a location using the caster's faced direction
 */
public class LocationTarget extends EffectComponent
{
    private static final String RANGE  = "range";
    private static final String GROUND = "ground";

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
        double range = settings.get(RANGE, level, 5.0);
        boolean groundOnly = !settings.getString(GROUND, "true").toLowerCase().equals("false");
        for (LivingEntity t : targets)
        {
            Location loc;
            Block b = t.getTargetBlock(null, (int)Math.ceil(range));
            if (b == null && !groundOnly)
            {
                loc = t.getLocation().add(t.getLocation().getDirection().multiply(range));
            }
            else if (b != null)
            {
                loc = b.getLocation();
            }
            else continue;

            ArrayList<LivingEntity> list = new ArrayList<LivingEntity>();
            Bat bat = loc.getWorld().spawn(loc, Bat.class);
            bat.setMaxHealth(9999);
            bat.setHealth(bat.getMaxHealth());
            bat.getLocation().setDirection(caster.getLocation().getDirection());
            list.add(bat);
            worked = executeChildren(caster, level, list) || worked;
            bat.remove();
        }
        return worked;
    }
}
