package com.sucy.skill.example.alchemist.active;

import com.sucy.skill.api.skill.ClassSkill;
import com.sucy.skill.api.skill.SkillAttribute;
import com.sucy.skill.api.skill.SkillShot;
import com.sucy.skill.api.skill.SkillType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

public class ExplosionPotion extends ClassSkill implements SkillShot, Listener {

    public static final String NAME = "Explosion Potion";
    private static final String
        DAMAGE = "Mob Damage",
        POWER = "Power";

    public ExplosionPotion() {
        super(NAME, SkillType.SKILL_SHOT, Material.TNT, 5);

        description.add("Launches a potion that");
        description.add("explodes upon impact,");
        description.add("dealing damage and");
        description.add("setting things on fire");

        setAttribute(SkillAttribute.LEVEL, 1, 1);
        setAttribute(SkillAttribute.COST, 1, 0);
        setAttribute(SkillAttribute.COOLDOWN, 10, -1);
        setAttribute(SkillAttribute.MANA, 22, -1);

        setAttribute(DAMAGE, 6, 2);
        setAttribute(POWER, 0.5, 0.125);
    }

    @Override
    public boolean cast(Player player, int level) {

        Potion potion = new Potion(PotionType.FIRE_RESISTANCE, 1);
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
            double damage = getAttribute(DAMAGE, level);
            for (LivingEntity entity : event.getAffectedEntities()) {
                if (entity instanceof Monster) {
                    entity.damage(damage, (Player)event.getEntity().getShooter());
                }
            }

            event.getAffectedEntities().clear();
            event.setCancelled(true);
            double power = getAttribute(POWER, level);
            Location l = event.getEntity().getLocation();
            l.getWorld().createExplosion(l.getX(), l.getY(), l.getZ(), (float)power, true, false);
        }
    }
}
