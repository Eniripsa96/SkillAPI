package com.sucy.skill.example.alchemist.active;

import com.rit.sucy.player.Protection;
import com.sucy.skill.api.skill.ClassSkill;
import com.sucy.skill.api.skill.SkillAttribute;
import com.sucy.skill.api.skill.SkillShot;
import com.sucy.skill.api.skill.SkillType;
import com.sucy.skill.api.util.effects.DOT;
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

public class PotionOfSickness extends ClassSkill implements SkillShot, Listener {

    public static final String NAME = "Potion Of Sickness";
    private static final String
            DOT_DAMAGE = "DOT Damage",
            DURATION = "Duration";

    public PotionOfSickness() {
        super(NAME, SkillType.SKILL_SHOT, Material.ROTTEN_FLESH, 5);

        description.add("Launches a diseased bottle");
        description.add("that damages affected");
        description.add("enemies over time and");
        description.add("makes them nauseous.");

        setAttribute(SkillAttribute.LEVEL, 1, 1);
        setAttribute(SkillAttribute.COST, 1, 0);
        setAttribute(SkillAttribute.COOLDOWN, 10, -1);
        setAttribute(SkillAttribute.MANA, 18, -1);

        setAttribute(DURATION, 3, 0.5);
        setAttribute(DOT_DAMAGE, 6, 2);
    }

    @Override
    public boolean cast(Player player, int level) {

        Potion potion = new Potion(PotionType.POISON, 1);
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

            double dot = getAttribute(DOT_DAMAGE, level) / (duration / 20);
            PotionEffect nauseaEffect = new PotionEffect(PotionEffectType.CONFUSION, duration, 0);
            for (LivingEntity entity : event.getAffectedEntities()) {
                Player thrower = (Player) event.getEntity().getShooter();
                if (Protection.canAttack(thrower, entity)) {
                    entity.addPotionEffect(nauseaEffect);
                    api.getDOTHelper().getDOTSet(entity).addEffect(NAME, new DOT(this, thrower, duration, dot, 20, true));
                }
            }

            event.getAffectedEntities().clear();
            event.setCancelled(true);
        }
    }
}
