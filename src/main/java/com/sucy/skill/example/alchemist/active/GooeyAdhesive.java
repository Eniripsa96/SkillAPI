package com.sucy.skill.example.alchemist.active;

import com.rit.sucy.player.Protection;
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

public class GooeyAdhesive extends ClassSkill implements SkillShot, Listener {

    public static final String NAME = "Gooey Adhesive";
    private static final String
        DURATION = "Duration";

    public GooeyAdhesive() {
        super(NAME, SkillType.SKILL_SHOT, Material.OBSIDIAN, 5);

        description.add("Launches a bottle of");
        description.add("sludge that slows and");
        description.add("blinds affected enemies");

        setAttribute(SkillAttribute.LEVEL, 1, 1);
        setAttribute(SkillAttribute.COST, 1, 0);
        setAttribute(SkillAttribute.COOLDOWN, 10, -1);
        setAttribute(SkillAttribute.MANA, 16, -2);

        setAttribute(DURATION, 1, 0.5);
    }

    @Override
    public boolean cast(Player player, int level) {

        Potion potion = new Potion(PotionType.REGEN, 1);
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

            PotionEffect slowEffect = new PotionEffect(PotionEffectType.SLOW, duration, 1);
            PotionEffect blindEffect = new PotionEffect(PotionEffectType.BLINDNESS, duration, 0);
            for (LivingEntity entity : event.getAffectedEntities()) {
                if (Protection.canAttack((Player) event.getEntity().getShooter(), entity)) {
                    entity.addPotionEffect(slowEffect);
                    entity.addPotionEffect(blindEffect);
                }
            }

            event.getAffectedEntities().clear();
        }
    }
}
