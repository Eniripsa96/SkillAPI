package com.sucy.skill.example.hunter.active;

import com.sucy.skill.api.Status;
import com.sucy.skill.api.skill.ClassSkill;
import com.sucy.skill.api.skill.SkillAttribute;
import com.sucy.skill.api.skill.SkillShot;
import com.sucy.skill.api.skill.SkillType;
import com.sucy.skill.api.util.effects.ItemProjectile;
import com.sucy.skill.api.event.ItemProjectileHitEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Bolas extends ClassSkill implements SkillShot, Listener {

    public static final String NAME = "Bolas";
    private static final String
        DURATION = "Root Duration";

    private static final ItemStack BOLA = new ItemStack(Material.STRING);

    public Bolas() {
        super(NAME, SkillType.SKILL_SHOT, Material.TRIPWIRE, 5);

        description.add("Launches a bola that");
        description.add("roots the first enemy");
        description.add("hit in place for a short");
        description.add("time, stopping movement");

        setAttribute(SkillAttribute.LEVEL, 1, 1);
        setAttribute(SkillAttribute.COST, 1, 0);
        setAttribute(SkillAttribute.COOLDOWN, 9, -1);
        setAttribute(SkillAttribute.MANA, 20, -1);

        setAttribute(DURATION, 1, 0.25);
    }

    @Override
    public boolean cast(Player player, int level) {
        ItemProjectile projectile = new ItemProjectile(player, BOLA, player.getLocation().getDirection().multiply(3), 0);
        projectile.setMetadata(NAME, new FixedMetadataValue(api, level));
        return true;
    }

    @EventHandler
    public void onHit(ItemProjectileHitEvent event) {
        if (event.getProjectile().hasMetadata(NAME)) {
            int duration = (int)(20 * getAttribute(DURATION, event.getProjectile().getMetadata(NAME).get(0).asInt()));
            if (event.getTarget() instanceof Player) {
                api.getStatusHolder(event.getTarget()).addStatus(Status.ROOT, duration * 50);
            }
            else {
                event.getTarget().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, duration, 100), true);
            }
        }
    }
}
