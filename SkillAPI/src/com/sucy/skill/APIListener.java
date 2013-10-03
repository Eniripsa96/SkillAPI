package com.sucy.skill;

import com.sucy.skill.api.*;
import com.sucy.skill.api.event.PlayerOnDamagedEvent;
import com.sucy.skill.api.event.PlayerOnHitEvent;
import com.sucy.skill.api.skill.*;
import com.sucy.skill.api.util.Protection;
import com.sucy.skill.language.OtherNodes;
import com.sucy.skill.skills.*;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * <p>Main listener for the API</p>
 * <p>You should not instantiate or use this class</p>
 */
public class APIListener implements Listener {

    private final Hashtable<String, Hashtable<String, Long>> timers = new Hashtable<String, Hashtable<String, Long>>();
    private final SkillAPI plugin;

    /**
     * Constructor
     *
     * @param plugin plugin reference
     */
    public APIListener(SkillAPI plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Uses bindings for skill shots
     *
     * @param event event details
     */
    @EventHandler (priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        PlayerSkills data = plugin.getPlayer(player.getName());
        Material heldItem = player.getItemInHand().getType();

        // Happens on right click
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.isCancelled()) return;

            // Make sure a skill is bound to the item
            if (data.getBound(heldItem) != null && plugin.isSkillRegistered(data.getBound(heldItem))) {

                // Make sure the skill is a skill shot
                ClassSkill skill = plugin.getRegisteredSkill(data.getBound(heldItem));
                if (skill instanceof SkillShot) {

                    // Check the requirements to cast the skill
                    int level = data.getSkillLevel(skill.getName());
                    if (checkMana(data, skill.getName()) && checkCD(player, skill.getName(), level)) {

                        // Try to cast the skill
                        if (((SkillShot) skill).cast(player, data.getSkillLevel(skill.getName()))) {

                            // Use mana if successful
                            if (plugin.isManaEnabled()) data.useMana(plugin.getSkill(skill.getName()).getClassSkill().getAttribute(SkillAttribute.MANA, level));
                        }

                        // If the skill didn't work, remove the cooldown
                        else timers.get(player.getName()).put(skill.getName(), 0l);
                    }
                }
            }
        }
    }

    /**
     * Uses bindings for target skills
     *
     * @param event event details
     */
    @EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onTarget (PlayerInteractEntityEvent event) {

        // Target must be a living entity
        if (!(event.getRightClicked() instanceof LivingEntity))
            return;

        Player player = event.getPlayer();
        PlayerSkills data = plugin.getPlayer(player.getName());
        Material heldItem = player.getItemInHand().getType();

        // Make sure the item is bound
        if (data.getBound(heldItem) != null && plugin.isSkillRegistered(data.getBound(heldItem))) {

            // Make sure the class is a target skill
            ClassSkill skill = plugin.getRegisteredSkill(data.getBound(heldItem));
            if (skill instanceof TargetSkill) {

                // Check the requirements to cast ths skill
                int level = data.getSkillLevel(skill.getName());
                if (checkMana(data, skill.getName()) && checkCD(player, skill.getName(), level)) {

                    // Try to cast the skill
                    if (((TargetSkill) skill).cast(
                            player,
                            (LivingEntity)event.getRightClicked(),
                            data.getSkillLevel(skill.getName()),
                            !Protection.canAttack(player, (LivingEntity) event.getRightClicked()))) {

                        // Use mana if successful
                        if (plugin.isManaEnabled()) data.useMana(plugin.getSkill(skill.getName()).getClassSkill().getAttribute(SkillAttribute.MANA, level));
                    }

                    // If not successful, remove the cooldown
                    else timers.get(player.getName()).put(skill.getName(), 0l);
                }
            }
        }
    }

    /**
     * Damage modifier for classes
     *
     * @param event event details
     */
    @EventHandler (priority = EventPriority.LOWEST)
    public void onDamage(EntityDamageByEntityEvent event) {

        // Projectile damage
        if (event.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile)event.getDamager();
            LivingEntity shooter = projectile.getShooter();
            if (shooter != null && shooter instanceof Player) {
                PlayerSkills data = plugin.getPlayer(((Player) shooter).getName());
                if (data != null && data.getTree() != null) {
                    CustomClass playerClass = plugin.getRegisteredClass(data.getTree());

                    // When the default damage isn't 0, set the damage relative
                    // to the default damage to account for varied damages
                    if (CustomClass.getDefaultDamage(projectile.getClass()) != 0) {
                        int damage = (int)event.getDamage() * playerClass.getDamage(projectile.getClass(), data.getName()) / CustomClass.getDefaultDamage(projectile.getClass());
                        event.setDamage(damage < 1 ? 1 : damage);
                    }

                    // Otherwise, just set the damage normally
                    else {
                        int damage = playerClass.getDamage(projectile.getClass(), data.getName());
                        int defaultDamage = CustomClass.getDefaultDamage(projectile.getClass());
                        event.setDamage(event.getDamage() + damage - defaultDamage);
                    }
                }
            }
        }

        // Melee damage
        else if (event.getDamager() instanceof Player) {
            PlayerSkills player = plugin.getPlayer(((Player) event.getDamager()).getName());
            if (player != null && player.getTree() != null) {
                CustomClass playerClass = plugin.getRegisteredClass(player.getTree());

                // Set the damage normally
                Material mat = ((Player) event.getDamager()).getItemInHand() == null ?
                        Material.AIR
                        : ((Player) event.getDamager()).getItemInHand().getType();
                int damage = playerClass.getDamage(mat, player.getName());
                int defaultDamage = CustomClass.getDefaultDamage(mat);
                event.setDamage(event.getDamage() + damage - defaultDamage);
            }
        }
    }

    /**
     * Calls events for on-hit effects
     *
     * @param event event details
     */
    @EventHandler (priority =  EventPriority.MONITOR, ignoreCancelled = true)
    public void onHit (EntityDamageByEntityEvent event) {

        // Make sure its the correct causes to avoid infinite loops with the protection checks
        if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK
                || event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {

            // Get the involved entities in terms of living entities considering projectiles
            LivingEntity damaged = event.getEntity() instanceof LivingEntity ? (LivingEntity) event.getEntity()
                    : null;
            LivingEntity damager = event.getDamager() instanceof LivingEntity ? (LivingEntity) event.getDamager()
                    : event.getDamager() instanceof Projectile ? ((Projectile) event.getDamager()).getShooter()
                    : null;

            // Call an event when a player dealt damage
            if (damager != null && damager instanceof Player && damaged != null) {
                PlayerOnHitEvent e = new PlayerOnHitEvent((Player)damager, damaged, (int)event.getDamage());
                plugin.getServer().getPluginManager().callEvent(e);
                event.setDamage(e.getDamage());
            }

            // Call an event when a player received damage
            if (damaged != null && damaged instanceof Player && damager != null) {
                PlayerOnDamagedEvent e = new PlayerOnDamagedEvent((Player)damaged, damager, (int)event.getDamage());
                plugin.getServer().getPluginManager().callEvent(e);
                event.setDamage(e.getDamage());
            }
        }
    }

    /**
     * Loads player data on joining the game
     *
     * @param event event details
     */
    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {

        // If the player data doesn't exist, create a new instance
        if (plugin.getPlayer(event.getPlayer().getName()) == null) {
            plugin.addPlayer(new PlayerSkills(plugin, event.getPlayer().getName()));
        }

        // Otherwise, load the data
        else {
            PlayerSkills skills = plugin.getPlayer(event.getPlayer().getName());

            // Update the player health
            skills.updateHealth();

            // Apply class prefixes
            if (skills.getTree() != null)
                PrefixManager.setPrefix(skills, skills.getPrefix(), plugin.getClass(skills.getTree()).getBraceColor());

            // Apply passive skills
            for (Map.Entry<String, Integer> entry : skills.getSkills().entrySet()) {
                if (entry.getValue() >= 1) {
                    ClassSkill s = plugin.getRegisteredSkill(entry.getKey());
                    if (s != null && s instanceof PassiveSkill)
                        ((PassiveSkill) s).onInitialize(event.getPlayer(), entry.getValue());
                }
            }
        }
    }

    /**
     * Cancels passive abiltiies upon quitting the game
     *
     * @param event event details
     */
    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onQuit(PlayerQuitEvent event) {
        PlayerSkills skills = plugin.getPlayer(event.getPlayer().getName());
        skills.stopPassiveAbilities();
        skills.setMaxHealth(20);
    }

    /**
     * Awards experience for killing a player
     *
     * @param event event details
     */
    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() != null) {
            PlayerSkills player = plugin.getPlayer(event.getEntity().getKiller().getName());
            player.giveExp(plugin.getExp(getName(event.getEntity())));
        }
    }

    /**
     * Handles skill tree interaction
     *
     * @param event event details
     */
    @EventHandler
    public void onClick(InventoryClickEvent event) {

        // Make sure its a skill tree inventory
        SkillTree tree = plugin.getClass(event.getInventory().getTitle());
        if (tree != null && event.getInventory().getHolder() instanceof SkillTree) {

            // Do nothing when clicking outside the inventory
            if (event.getSlot() == -999) return;

            boolean top = event.getRawSlot() < event.getView().getTopInventory().getSize();

            // Always cancel the event when clicking in the top region
            if (top) {
                event.setCancelled(true);
                PlayerSkills player = plugin.getPlayer(event.getWhoClicked().getName());
                Skill skill = tree.getSkill(event.getSlot());

                // If they clicked on a skill, try upgrading it
                if (skill != null) {
                    if (player.upgradeSkill(skill)) {
                        tree.update(event.getInventory(), player);
                    }
                }

                // Allow players to take back their items they glitched into the inventory
                else if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
                    event.setCancelled(false);
                }
            }

            // Do not allow shift clicking items into the inventory
            else if (event.isShiftClick()) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * Checks if a skill is on cooldown
     *
     * @param p     player casting the skill
     * @param skill name of skill being cast
     * @param level current skill level
     * @return      true if available to cast, false otherwise
     */
    private boolean checkCD(Player p, String skill, int level) {
        String player = p.getName();

        // Make sure they have a timer table for them
        if (!timers.containsKey(player)) {
            timers.put(player, new Hashtable<String, Long>());
        }

        // Make sure they have a timer for the skill
        if (!timers.get(player).containsKey(skill)) {
            timers.get(player).put(skill, 0l);
        }

        long passed = System.currentTimeMillis() - timers.get(player).get(skill);
        long cd = (long)(1000 * plugin.getSkill(skill).getClassSkill().getAttribute(SkillAttribute.COOLDOWN, level));

        // If the skill is still on cooldown, provide a message
        if (passed < cd) {
            List<String> messages = plugin.getMessages(OtherNodes.ON_COOLDOWN, true);
            for (String message : messages) {
                message = message.replace("{cooldown}", ((cd - passed) / 1000 + 1) + "")
                                 .replace("{skill}", skill);

                plugin.getServer().getPlayer(player).sendMessage(message);
            }
            return false;
        }

        // Otherwise start a new cooldown
        timers.get(player).put(skill, System.currentTimeMillis());
        return true;
    }

    /**
     * Checks if the player has enough mana to use the skill
     *
     * @param player    player to check
     * @param skillName skill being used
     * @return          true if there's enough mana, false otherwise
     */
    private boolean checkMana(PlayerSkills player, String skillName) {
        int cost = plugin.getSkill(skillName).getClassSkill().getAttribute(SkillAttribute.MANA, player.getSkillLevel(skillName));

        // Make sure mana is enabled
        if (!plugin.isManaEnabled()) return true;

        // If they don't have enough mana, send a message
        else if (player.getMana() < cost) {
            List<String> messages = plugin.getMessages(OtherNodes.NO_MANA, true);
            for (String message : messages) {
                message = message.replace("{missing}", (cost - player.getMana()) + "")
                                 .replace("{mana}", player.getMana() + "")
                                 .replace("{cost}", cost + "")
                                 .replace("{skill}", skillName);

                plugin.getServer().getPlayer(player.getName()).sendMessage(message);
            }
            return false;
        }

        return true;
    }

    /**
     * Translates an entity into a name to use for the config
     *
     * @param entity entity
     * @return       config name
     */
    private String getName(Entity entity) {
        String name = entity.getClass().getSimpleName().toLowerCase().replace("craft", "");
        if (entity instanceof Skeleton) {
            if (((Skeleton)entity).getSkeletonType() == Skeleton.SkeletonType.WITHER) {
                name = "wither" + name;
            }
        }
        return name;
    }
}
