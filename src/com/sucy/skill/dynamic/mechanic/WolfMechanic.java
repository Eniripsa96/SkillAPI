package com.sucy.skill.dynamic.mechanic;

import com.rit.sucy.text.TextFormatter;
import com.sucy.skill.dynamic.EffectComponent;
import com.sucy.skill.listener.MechanicListener;
import com.sucy.skill.task.RemoveTask;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Applies a flag to each target
 */
public class WolfMechanic extends EffectComponent
{
    private static final ArrayList<RemoveTask> tasks = new ArrayList<RemoveTask>();

    private static final String COLOR   = "color";
    private static final String HEALTH  = "health";
    private static final String SECONDS = "seconds";
    private static final String NAME    = "name";
    private static final String DAMAGE  = "damage";

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
        if (!(caster instanceof Player))
        {
            return false;
        }

        boolean isSelf = targets.size() == 1 && targets.get(0) == caster;
        String color = settings.getString(COLOR);
        double health = attr(caster, HEALTH, level, 10.0, isSelf);
        String name = TextFormatter.colorString(settings.getString(NAME, "").replace("{player}", ((Player) caster).getName()));
        double damage = attr(caster, DAMAGE, level, 3.0, isSelf);

        DyeColor dye = null;
        if (color != null)
        {
            try
            {
                dye = DyeColor.valueOf(color);
            }
            catch (Exception ex)
            { /* Invalid color */ }
        }

        double seconds = attr(caster, SECONDS, level, 10.0, isSelf);
        int ticks = (int) (seconds * 20);
        ArrayList<LivingEntity> wolves = new ArrayList<LivingEntity>();
        for (LivingEntity target : targets)
        {
            Wolf wolf = target.getWorld().spawn(target.getLocation(), Wolf.class);
            wolf.setOwner((Player) caster);
            wolf.setMaxHealth(health);
            wolf.setHealth(health);
            wolf.setMetadata(MechanicListener.SUMMON_DAMAGE, new FixedMetadataValue(Bukkit.getPluginManager().getPlugin("SkillAPI"), damage));

            if (dye != null)
            {
                wolf.setCollarColor(dye);
            }
            if (name.length() > 0)
            {
                wolf.setCustomName(name);
                wolf.setCustomNameVisible(true);
            }

            RemoveTask task = new RemoveTask(wolf, ticks);
            tasks.add(task);
            wolves.add(wolf);
        }

        // Apply children to the wolves
        if (wolves.size() > 0)
        {
            executeChildren(caster, level, wolves);
            return true;
        }
        return false;
    }

    /**
     * Removes all of the currently summoned wolves. This would be used
     * to clean up before the plugin is disabled or the server is shut down.
     */
    public static void removeWolves()
    {
        for (RemoveTask task : tasks)
        {
            task.run();
            task.cancel();
        }
        tasks.clear();
    }

    /**
     * Removes any wolves summoned by the given player
     *
     * @param player player to desummon wolves for
     */
    public static void removeWolves(Player player)
    {
        for (RemoveTask task : tasks)
        {
            if (task.isOwnedBy(player)) {
                task.run();
                task.cancel();
                tasks.remove(task);
            }
        }
    }
}
