package com.sucy.skill.dynamic;

import com.sucy.skill.api.skills.PassiveSkill;
import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.api.skills.SkillShot;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class DynamicSkill extends Skill implements SkillShot, PassiveSkill, Listener
{
    private HashMap<Trigger, EffectComponent> components = new HashMap<Trigger, EffectComponent>();
    private HashMap<UUID, Integer>            active     = new HashMap<UUID, Integer>();

    public DynamicSkill(String name)
    {
        super(name, "Dynamic", Material.JACK_O_LANTERN, 1);
    }

    @Override
    public void update(LivingEntity user, int prevLevel, int newLevel)
    {
        active.put(user.getUniqueId(), newLevel);
    }

    @Override
    public void initialize(LivingEntity user, int level)
    {
        trigger(user, level, Trigger.SPAWN);
        active.put(user.getUniqueId(), level);
    }

    @Override
    public void stopEffects(LivingEntity user, int level)
    {
        active.remove(user.getUniqueId());
    }

    @Override
    public boolean cast(LivingEntity user, int level)
    {
        return trigger(user, level, Trigger.SPAWN);
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event)
    {
        if (active.containsKey(event.getEntity().getUniqueId()))
        {
            trigger(event.getEntity(), active.get(event.getEntity().getUniqueId()), Trigger.DEATH);
        }
    }

    private boolean trigger(LivingEntity user, int level, Trigger trigger)
    {
        if (user != null && components.containsKey(trigger))
        {
            ArrayList<LivingEntity> self = new ArrayList<LivingEntity>();
            self.add(user);
            return components.get(trigger).execute(user, level, self);
        }
        return false;
    }
}
