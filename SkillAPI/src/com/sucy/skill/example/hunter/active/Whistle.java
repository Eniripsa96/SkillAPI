package com.sucy.skill.example.hunter.active;

import com.sucy.skill.api.skill.ClassSkill;
import com.sucy.skill.api.skill.SkillAttribute;
import com.sucy.skill.api.skill.SkillShot;
import com.sucy.skill.api.skill.SkillType;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class Whistle extends ClassSkill implements SkillShot, Listener {

    public static final String NAME = "Whistle";
    private static final String
        DURATION = "Summon Duration",
        DAMAGE = "Wolf Damage",
        HEALTH = "Wolf Health",
        MOB_REDUCTION = "Damage Reduction (%)";

    private ArrayList<WhistleTask> tasks = new ArrayList<WhistleTask>();

    public Whistle() {
        super(NAME, SkillType.SUMMON, Material.BONE, 5);

        description.add("Summons a wolf that fights");
        description.add("by your side temporarily.");
        description.add("Wolves take less damage");
        description.add("from monster attacks.");

        setAttribute(SkillAttribute.LEVEL, 1, 1);
        setAttribute(SkillAttribute.COST, 1, 0);
        setAttribute(SkillAttribute.COOLDOWN, 30, -3);
        setAttribute(SkillAttribute.MANA, 40, -3);

        setAttribute(DURATION, 20, 5);
        setAttribute(DAMAGE, 3, 1);
        setAttribute(HEALTH, 12, 3);
        setAttribute(MOB_REDUCTION, 10, 10);
    }

    public void removeWolves() {
        for (WhistleTask task : tasks) {
            task.cancel();
            task.run();
        }
        tasks.clear();
    }

    @Override
    public boolean cast(Player player, int level) {
        Wolf wolf = (Wolf)player.getWorld().spawnEntity(player.getLocation(), EntityType.WOLF);
        wolf.setOwner(player);
        wolf.setCollarColor(DyeColor.GREEN);
        wolf.setMaxHealth(getAttribute(HEALTH, level));
        wolf.setHealth(wolf.getMaxHealth());
        wolf.setMetadata(NAME, new FixedMetadataValue(api, level));
        wolf.setCustomName(ChatColor.DARK_GREEN + player.getName() + "'s Wolf");
        wolf.setCustomNameVisible(false);
        WhistleTask task = new WhistleTask(wolf);
        task.runTaskLater(api, (int) (20 * getAttribute(DURATION, level)));
        tasks.add(task);
        return true;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager().hasMetadata(NAME)) {
            int level = event.getDamager().getMetadata(NAME).get(0).asInt();
            event.setCancelled(true);
            ((LivingEntity) event.getEntity()).damage(getAttribute(DAMAGE, level), (Player)((Wolf)event.getDamager()).getOwner());
        }
        else if (event.getEntity().hasMetadata(NAME) && !(event.getDamager() instanceof Player)) {
            event.setDamage(event.getDamage() * (1 - getAttribute(MOB_REDUCTION, event.getEntity().getMetadata(NAME).get(0).asInt()) / 100));
        }
    }

    public class WhistleTask extends BukkitRunnable {

        Wolf wolf;

        public WhistleTask(Wolf wolf) {
            this.wolf = wolf;
        }

        @Override
        public void run() {
            tasks.remove(0);
            if (wolf.isValid() && !wolf.isDead()) {
                wolf.remove();
            }
        }
    }
}
