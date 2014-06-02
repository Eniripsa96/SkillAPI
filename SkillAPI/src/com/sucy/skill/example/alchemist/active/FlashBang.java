package com.sucy.skill.example.alchemist.active;

import com.rit.sucy.player.Protection;
import com.sucy.skill.api.Status;
import com.sucy.skill.api.skill.ClassSkill;
import com.sucy.skill.api.skill.SkillAttribute;
import com.sucy.skill.api.skill.SkillShot;
import com.sucy.skill.api.skill.SkillType;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class FlashBang extends ClassSkill implements SkillShot, Listener {

    public static final String NAME = "Flash Bang";
    private static final String
            DURATION = "Duration";

    public FlashBang() {
        super(NAME, SkillType.SKILL_SHOT, Material.FIREWORK_CHARGE, 5);

        description.add("Launches a flash bomb");
        description.add("that stuns affected enemies");
        description.add("for a brief duration.");

        setAttribute(SkillAttribute.LEVEL, 1, 1);
        setAttribute(SkillAttribute.COST, 1, 0);
        setAttribute(SkillAttribute.COOLDOWN, 12, -1);
        setAttribute(SkillAttribute.MANA, 18, -1);

        setAttribute(DURATION, 1, 0.25);
    }

    @Override
    public boolean cast(Player player, int level) {

        Potion potion = new Potion(PotionType.STRENGTH, 1);
        potion.setSplash(true);

        ItemStack item = new ItemStack(Material.POTION);
        potion.apply(item);

        ThrownPotion thrownPotion = player.launchProjectile(ThrownPotion.class);
        thrownPotion.setItem(item);
        thrownPotion.setMetadata(NAME, new FixedMetadataValue(api, level));

        return true;
    }

    @EventHandler
    public void onPotionSplash(PotionSplashEvent event) {
        if (event.getEntity().hasMetadata(NAME)) {
            int level = event.getEntity().getMetadata(NAME).get(0).asInt();
            int duration = (int)(20 * getAttribute(DURATION, level));

            for (LivingEntity entity : event.getAffectedEntities()) {
                if (Protection.canAttack((Player) event.getEntity().getShooter(), entity)) {
                    if (entity instanceof Player) {
                        api.getStatusHolder(entity).addStatus(Status.STUN, duration * 50);
                    }
                    else entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, duration, 100));
                }
            }

            event.getAffectedEntities().clear();
        }
    }
}
