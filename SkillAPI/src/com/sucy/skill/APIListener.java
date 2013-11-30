package com.sucy.skill;

import com.sucy.skill.api.CustomClass;
import com.sucy.skill.api.PlayerSkills;
import com.sucy.skill.api.Status;
import com.sucy.skill.api.StatusHolder;
import com.sucy.skill.api.event.*;
import com.sucy.skill.api.skill.ClassSkill;
import com.sucy.skill.api.skill.PassiveSkill;
import com.sucy.skill.language.StatusNodes;
import com.sucy.skill.mccore.CoreChecker;
import com.sucy.skill.mccore.PrefixManager;
import com.sucy.skill.tree.SkillTree;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>Main listener for the API</p>
 * <p>You should not instantiate or use this class</p>
 */
public class APIListener implements Listener {

    private static final String P_TYPE = "pType";
    private static final String S_TYPE = "sType";
    private static final int SPAWNER = 0, EGG = 1;

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

        // Must have permission
        if (!player.hasPermission(PermissionNodes.BASIC)) {
            return;
        }

        PlayerSkills data = plugin.getPlayer(player.getName());
        Material heldItem = player.getItemInHand().getType();

        // Must be on right click
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        // Cannot be cancelled if clicking on a block
        if (event.isCancelled() && event.getAction() == Action.RIGHT_CLICK_BLOCK)
            return;

        // Must have a valid item
        if (heldItem == null || data.getBound(heldItem) == null || !plugin.isSkillRegistered(data.getBound(heldItem)))
            return;

        // Cast the skill
        data.castSkill(data.getBound(heldItem));
    }

    /**
     * Damage modifier for classes
     *
     * @param event event details
     */
    @EventHandler (priority = EventPriority.LOW)
    public void onDamage(EntityDamageByEntityEvent event) {

        LivingEntity target = convertEntity(event.getEntity());
        LivingEntity damager = convertEntity(event.getDamager());

        // Statuses
        // If stunned or disarmed, cancel it completely
        if (damager != null) {
            StatusHolder holder = plugin.getStatusHolder(damager);
            if (holder.hasStatus(Status.STUN) || holder.hasStatus(Status.DISARM)) {
                event.setCancelled(true);

                // Send a message if its a player
                if (damager instanceof Player) {
                    Player p = (Player)damager;
                    if (holder.hasStatus(Status.STUN)) plugin.sendStatusMessage(p, StatusNodes.STUNNED, holder.getTimeLeft(Status.STUN));
                    else plugin.sendStatusMessage(p, StatusNodes.DISARMED, holder.getTimeLeft(Status.DISARM));
                }

                return;
            }
        }

        // Player class damage
        if (damager instanceof Player && PlayerSkills.skillsBeingCast.isEmpty()) {

            Player p = (Player)damager;

            // Requires permission
            if (!p.hasPermission(PermissionNodes.BASIC)) {
                return;
            }

            // Projectile damage
            if (event.getDamager() instanceof Projectile) {
                Projectile projectile = (Projectile) event.getDamager();
                PlayerSkills player = plugin.getPlayer(p.getName());
                if (player != null && player.getClassName() != null) {
                    CustomClass playerClass = plugin.getClass(player.getClassName());

                    // Custom items get flat damages
                    if (projectile.hasMetadata(P_TYPE)) {
                        FixedMetadataValue metaValue = (FixedMetadataValue)projectile.getMetadata(P_TYPE).get(0);
                        int id = metaValue.asInt();
                        int damage = playerClass.getCustomDamage(id);
                        if (damage > 0) event.setDamage(damage);
                    }

                    // When the default damage isn't 0, set the damage relative
                    // to the default damage to account for varied damages
                    else if (CustomClass.getDefaultDamage(projectile.getClass()) != 0) {
                        double damage = event.getDamage() * playerClass.getDamage(projectile.getClass()) / CustomClass.getDefaultDamage(projectile.getClass());
                        event.setDamage(damage < 0 ? 0 : damage);
                    }

                    // Otherwise, just set the damage normally
                    else {
                        double damage = playerClass.getDamage(projectile.getClass());
                        int defaultDamage = CustomClass.getDefaultDamage(projectile.getClass());
                        damage = event.getDamage() + damage - defaultDamage;
                        event.setDamage(damage < 0 ? 0 : damage);
                    }
                }
            }

            // Melee damage
            else {
                PlayerSkills player = plugin.getPlayer(p.getName());
                if (player != null && player.getClassName() != null) {
                    CustomClass playerClass = plugin.getClass(player.getClassName());

                    // Set the damage normally
                    Material mat = p.getItemInHand() == null ?
                            Material.AIR : p.getItemInHand().getType();
                    double damage = 1;
                    if (mat.toString().toLowerCase().startsWith("x")) {
                        damage = Math.max(damage, playerClass.getDamage(p.getItemInHand().getTypeId()));
                        if (damage == 0) damage = event.getDamage();
                    }
                    else if (p.getItemInHand() != null) damage = event.getDamage() + playerClass.getDamage(mat) - CustomClass.getDefaultDamage(mat);
                    event.setDamage(damage);
                }
            }
        }

        // Damage modifiers
        if (damager != null) {
            event.setDamage(plugin.getStatusHolder(damager).modifyDamageDealt(event.getDamage()));
        }
        if (target != null) {
            event.setDamage(plugin.getStatusHolder(target).modifyDamageTaken(event.getDamage()));
        }
    }

    /**
     * Sets metadata for custom projectiles
     *
     * @param event event details
     */
    @EventHandler
    public void onLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() instanceof Player) {
            Player player = (Player)event.getEntity().getShooter();
            ItemStack item = player.getItemInHand();
            if (item.getType().toString().toLowerCase().startsWith("x")) {
                event.getEntity().setMetadata(P_TYPE, new FixedMetadataValue(plugin, item.getTypeId()));
            }
        }
    }

    /**
     * Calls events for on-hit effects
     *
     * @param event event details
     */
    @EventHandler (priority =  EventPriority.HIGH, ignoreCancelled = true)
    public void onStatusHit (EntityDamageByEntityEvent event) {

        // Status effects
        LivingEntity damaged = convertEntity(event.getEntity());
        if (damaged != null) {
            StatusHolder holder = plugin.getStatusHolder((LivingEntity)event.getEntity());

            // Absorb
            if (holder.hasStatus(Status.ABSORB)) {
                event.setCancelled(true);
                double health = damaged.getHealth() + event.getDamage();
                if (health > damaged.getMaxHealth()) health = damaged.getMaxHealth();
                damaged.setHealth(health);
            }

            // Invincible
            else if (holder.hasStatus(Status.INVINCIBLE)) {

                // Send a message if applicable
                Player damager = null;
                if (event.getDamager() instanceof Player) damager = (Player)event.getDamager();
                else if (event.getDamager() instanceof Projectile && ((Projectile) event.getDamager()).getShooter() instanceof Player) {
                    damager = (Player)((Projectile) event.getDamager()).getShooter();
                }
                if (damager != null) plugin.sendStatusMessage(damager, StatusNodes.INVINCIBLE, holder.getTimeLeft(Status.INVINCIBLE));

                // Cancel any damage
                event.setCancelled(true);
            }
        }
    }

    /**
     * Launches events when things are hit involving a player
     *
     * @param event event details
     */
    @EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onHit (EntityDamageByEntityEvent event) {

        // Make sure its the correct causes to avoid infinite loops with the protection checks
        if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK
                || event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {

            // Get the involved entities in terms of living entities considering projectiles
            LivingEntity damaged = convertEntity(event.getEntity());
            LivingEntity damager = convertEntity(event.getDamager());

            // Neither can be null
            if (damaged == null || damager == null) return;

            AttackType type;
            if (PlayerSkills.skillsBeingCast.size() > 0) type = AttackType.SKILL;
            else if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) type = AttackType.MELEE;
            else type = AttackType.PROJECTILE;

            // Call the special damage event
            SpecialEntityDamagedByEntityEvent se = new SpecialEntityDamagedByEntityEvent(damaged, damager, type, event.getDamage());
            plugin.getServer().getPluginManager().callEvent(se);
            event.setDamage(se.getDamage());

            // Call an event when a player dealt damage
            if (damager instanceof Player) {

                Player p = (Player)damager;

                // Requires permission
                if (!p.hasPermission(PermissionNodes.BASIC)) {
                    return;
                }

                PlayerOnHitEvent e = new PlayerOnHitEvent(p, damaged, type, event.getDamage());
                plugin.getServer().getPluginManager().callEvent(e);
                event.setDamage(e.getDamage());

                // Call an event when a player's skill dealt damage
                if (type == AttackType.SKILL) {
                    PlayerOnSkillHitEvent she = new PlayerOnSkillHitEvent(p, damaged, PlayerSkills.skillsBeingCast.peek().getName(), event.getDamage());
                    plugin.getServer().getPluginManager().callEvent(she);
                    event.setDamage(she.getDamage());
                }
            }



            // Call an event when a player received damage
            if (damaged instanceof Player) {

                Player p = (Player)damaged;

                // Requires permission
                if (!p.hasPermission(PermissionNodes.BASIC)) {
                    return;
                }

                PlayerOnDamagedEvent e = new PlayerOnDamagedEvent(p, damager, type, event.getDamage());
                plugin.getServer().getPluginManager().callEvent(e);
                event.setDamage(e.getDamage());
            }
        }
    }

    /**
     * Initializes class effects when a player joins the game
     *
     * @param event event details
     */
    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {

        PlayerSkills skills = plugin.getPlayer(event.getPlayer().getName());

        // Level bar
        skills.updateLevelBar();

        // Effects when a player has a class
        if (skills.hasClass()) {

            // Update the player health
            skills.updateHealth();

            // Apply passive skills
            for (Map.Entry<String, Integer> entry : skills.getSkills().entrySet()) {
                if (entry.getValue() >= 1) {
                    ClassSkill s = plugin.getSkill(entry.getKey());
                    if (s != null && s instanceof PassiveSkill)
                        ((PassiveSkill) s).onInitialize(event.getPlayer(), entry.getValue());
                }
            }

            // Apply class prefixes
            if (CoreChecker.isCoreActive()) {
                PrefixManager.setPrefix(skills, skills.getPrefix(), plugin.getClass(skills.getClassName()).getBraceColor());
            }
        }
    }

    /**
     * Converts an entity to a living entity
     *
     * @param entity entity to convert
     * @return       living entity of null if not compatible
     */
    private LivingEntity convertEntity(Entity entity) {
        if (entity == null) return null;
        if (entity instanceof LivingEntity) return (LivingEntity)entity;
        if (entity instanceof Projectile) {
            Projectile projectile = (Projectile)entity;
            return projectile.getShooter();
        }
        return null;
    }

    /**
     * Cancels passive abilities upon quitting the game
     *
     * @param event event details
     */
    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onQuit(PlayerQuitEvent event) {
        PlayerSkills skills = plugin.getPlayer(event.getPlayer().getName());
        skills.stopPassiveAbilities();
        skills.clearHealthBonuses();
        skills.applyMaxHealth(20);
    }

    /**
     * Marks spawned entities with how they spawned to block experience from certain methods
     *
     * @param event event details
     */
    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER) {
            event.getEntity().setMetadata(S_TYPE, new FixedMetadataValue(plugin, SPAWNER));
        }
        else if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER_EGG) {
            event.getEntity().setMetadata(S_TYPE, new FixedMetadataValue(plugin, EGG));
        }
    }

    /**
     * Awards experience for killing a player
     *
     * @param event event details
     */
    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeath(EntityDeathEvent event) {
        if (event.getEntity().hasMetadata(S_TYPE)) {
            int value = event.getEntity().getMetadata(S_TYPE).get(0).asInt();
            if (value == SPAWNER && plugin.blockingSpawnerExp()) return;
            else if (value == EGG && plugin.blockingEggExp()) return;
        }
        if (event.getEntity().getKiller() != null && event.getEntity().getKiller().hasPermission(PermissionNodes.BASIC)) {
            PlayerSkills player = plugin.getPlayer(event.getEntity().getKiller().getName());
            player.giveExp(plugin.getExp(getName(event.getEntity())));
        }
    }

    /**
     * Applies stun/root status effects
     *
     * @param event event details
     */
    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        PlayerSkills player = plugin.getPlayer(event.getPlayer().getName());
        if (player != null && (player.hasStatus(Status.STUN) ||  player.hasStatus(Status.ROOT))) {
            Location from = event.getFrom();
            Location to = event.getTo();
            if (!player.hasStatus(Status.STUN)) {
                if (from.getX() == to.getX() && from.getY() == to.getY() && from.getZ() == to.getZ()) {
                    return;
                }
                from.setPitch(event.getTo().getPitch());
                from.setYaw(event.getTo().getYaw());
            }
            event.getPlayer().teleport(from);

            // Send a message
            if (player.hasStatus(Status.STUN)) {
                plugin.sendStatusMessage(event.getPlayer(), StatusNodes.STUNNED, player.getTimeLeft(Status.STUN));
            }
            else {
                plugin.sendStatusMessage(event.getPlayer(), StatusNodes.ROOTED, player.getTimeLeft(Status.ROOT));
            }
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
        if (event.getInventory().getHolder() instanceof SkillTree) {
            SkillTree tree = (SkillTree)event.getInventory().getHolder();

            // Do nothing when clicking outside the inventory
            if (event.getSlot() == -999) return;

            boolean top = event.getRawSlot() < event.getView().getTopInventory().getSize();

            // Interact with the skill tree when clicking in the top region
            if (top) {
                event.setCancelled(tree.checkClick(event.getSlot()));

                // If they clicked on a skill, try upgrading it
                if (tree.isSkill(event.getSlot())) {
                    PlayerSkills player = plugin.getPlayer(event.getWhoClicked().getName());
                    if (event.isLeftClick()) {
                        if (player.upgradeSkill(tree.getSkill(event.getSlot()))) {
                            tree.update(event.getInventory(), player);
                        }
                    }
                    else if (event.isRightClick()) {
                        if (player.downgradeSkill(tree.getSkill(event.getSlot()))) {
                            tree.update(event.getInventory(), player);
                        }
                    }
                }
            }

            // Do not allow shift clicking items into the inventory
            else if (event.isShiftClick()) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * Updates level bar when the player's enchanting experience changes
     *
     * @param event event details
     */
    @EventHandler
    public void onExpChange(PlayerExpChangeEvent event) {
        if (plugin.usingLevelBar() && event.getPlayer().hasPermission(PermissionNodes.BASIC)) {
            event.setAmount(0);
        }
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
