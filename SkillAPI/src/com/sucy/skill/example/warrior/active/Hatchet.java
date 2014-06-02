package com.sucy.skill.example.warrior.active;

import com.sucy.skill.api.event.ItemProjectileHitEvent;
import com.sucy.skill.api.skill.ClassSkill;
import com.sucy.skill.api.skill.SkillAttribute;
import com.sucy.skill.api.skill.SkillShot;
import com.sucy.skill.api.skill.SkillType;
import com.sucy.skill.api.util.effects.DOT;
import com.sucy.skill.api.util.effects.DOTSet;
import com.sucy.skill.api.util.effects.ItemProjectile;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

/**
 * Target skill that pulls in the target and roots them
 */
public class Hatchet extends ClassSkill implements SkillShot, Listener {

    public static final String NAME = "Hatchet";
    private static final String
            DAMAGE = "Damage",
            BLEED_DAMAGE = "Bleed Damage",
            META_KEY = "hatchetSkill";

    private static final ItemStack HATCHET = new ItemStack(Material.IRON_AXE);

    public Hatchet() {
        super(NAME, SkillType.SKILL_SHOT, Material.IRON_AXE, 4);

        description.add("Throws a hatchet that deals");
        description.add("damage and causes the target");
        description.add("to bleed for 5 seconds,");
        description.add("additional damage.");

        setAttribute(SkillAttribute.LEVEL, 1, 1);
        setAttribute(SkillAttribute.COST, 1, 0);
        setAttribute(SkillAttribute.COOLDOWN, 6, 0);
        setAttribute(SkillAttribute.MANA, 30, 0);

        setAttribute(DAMAGE, 4, 2);
        setAttribute(BLEED_DAMAGE, 5, 1);
    }

    @Override
    public boolean cast(Player player, int level) {

        Vector vel = player.getLocation().getDirection().multiply(3);
        ItemProjectile projectile = new ItemProjectile(player, HATCHET, vel, getAttribute(DAMAGE, level));
        projectile.setMetadata(META_KEY, new FixedMetadataValue(api, level));

        return true;
    }

    @EventHandler
    public void onHit(ItemProjectileHitEvent event) {
        if (event.getProjectile().hasMetadata(META_KEY)) {
            int level = event.getProjectile().getMetadata(META_KEY).get(0).asInt();
            DOTSet set = api.getDOTHelper().getDOTSet(event.getTarget());
            set.addEffect(NAME, new DOT(100, getAttribute(BLEED_DAMAGE, level) / 5, 20, true));
        }
    }
}
