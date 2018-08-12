/**
 * SkillAPI
 * com.sucy.skill.dynamic.TempEntity
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Steven Sucy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software") to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.sucy.skill.dynamic;

import com.google.common.collect.ImmutableList;
import com.sucy.skill.api.util.Nearby;
import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Temporary dummy entity used for targeting a location in the dynamic system
 */
public class TempEntity implements LivingEntity {

    private Location loc;

    /**
     * Sets up a new dummy entity
     *
     * @param loc location to represent
     */
    public TempEntity(Location loc) {
        this.loc = loc;
    }

    public double getEyeHeight() {
        return 0.2;
    }

    public double getEyeHeight(boolean b) {
        return 0.2;
    }

    public Location getEyeLocation() {
        return getLocation().add(0, 1, 0);
    }

    public List<Block> getLineOfSight(HashSet<Byte> hashSet, int i) {
        return null;
    }

    public List<Block> getLineOfSight(Set<Material> set, int i) {
        return null;
    }

    public Block getTargetBlock(HashSet<Byte> hashSet, int i) {
        return null;
    }

    public Block getTargetBlock(Set<Material> set, int i) {
        return null;
    }

    public List<Block> getLastTwoTargetBlocks(HashSet<Byte> hashSet, int i) {
        return null;
    }

    public List<Block> getLastTwoTargetBlocks(Set<Material> set, int i) {
        return null;
    }

    public Egg throwEgg() {
        return null;
    }

    public Snowball throwSnowball() {
        return null;
    }

    public Arrow shootArrow() {
        return null;
    }

    public int getRemainingAir() {
        return 0;
    }

    public void setRemainingAir(int i) {

    }

    public int getMaximumAir() {
        return 0;
    }

    public void setMaximumAir(int i) {

    }

    public int getMaximumNoDamageTicks() {
        return 0;
    }

    public void setMaximumNoDamageTicks(int i) {

    }

    public double getLastDamage() {
        return 0;
    }

    public int _INVALID_getLastDamage() {
        return 0;
    }

    public void setLastDamage(double v) {

    }

    public void _INVALID_setLastDamage(int i) {

    }

    public int getNoDamageTicks() {
        return 0;
    }

    public void setNoDamageTicks(int i) {

    }

    public Player getKiller() {
        return null;
    }

    public boolean addPotionEffect(PotionEffect potionEffect) {
        return false;
    }

    public boolean addPotionEffect(PotionEffect potionEffect, boolean b) {
        return false;
    }

    public boolean addPotionEffects(Collection<PotionEffect> collection) {
        return false;
    }

    public boolean hasPotionEffect(PotionEffectType potionEffectType) {
        return false;
    }

    public PotionEffect getPotionEffect(PotionEffectType potionEffectType) {
        return null;
    }

    public void removePotionEffect(PotionEffectType potionEffectType) {

    }

    public Collection<PotionEffect> getActivePotionEffects() {
        return ImmutableList.of();
    }

    public boolean hasLineOfSight(Entity entity) {
        return false;
    }

    public boolean getRemoveWhenFarAway() {
        return false;
    }

    public void setRemoveWhenFarAway(boolean b) {

    }

    public EntityEquipment getEquipment() {
        return null;
    }

    public void setCanPickupItems(boolean b) {

    }

    public boolean getCanPickupItems() {
        return false;
    }

    public void setCustomName(String s) {

    }

    public String getCustomName() {
        return null;
    }

    public void setCustomNameVisible(boolean b) {

    }

    public boolean isCustomNameVisible() {
        return false;
    }

    public void setGlowing(boolean b) {

    }

    public boolean isGlowing() {
        return false;
    }

    public void setInvulnerable(boolean b) {

    }

    public boolean isInvulnerable() {
        return false;
    }

    public boolean isSilent() {
        return false;
    }

    public void setSilent(boolean b) {

    }

    public boolean hasGravity() {
        return false;
    }

    public void setGravity(boolean b) {

    }

    public int getPortalCooldown() {
        return 0;
    }

    public void setPortalCooldown(int i) {

    }

    public Set<String> getScoreboardTags() {
        return null;
    }

    public boolean addScoreboardTag(String s) {
        return false;
    }

    public boolean removeScoreboardTag(String s) {
        return false;
    }

    public PistonMoveReaction getPistonMoveReaction() {
        return null;
    }

    public boolean isLeashed() {
        return false;
    }

    public Entity getLeashHolder() throws IllegalStateException {
        return null;
    }

    public boolean setLeashHolder(Entity entity) {
        return false;
    }

    public boolean isGliding() {
        return false;
    }

    public void setGliding(boolean b) {

    }

    public boolean isSwimming() { return false; }

    public void setSwimming(final boolean b) { }

    public void setAI(boolean b) { }

    public boolean hasAI() {
        return false;
    }

    public void setCollidable(boolean b) {

    }

    public boolean isCollidable() {
        return false;
    }

    public void damage(double v) {

    }

    public void _INVALID_damage(int i) {

    }

    public void damage(double v, Entity entity) {

    }

    public void _INVALID_damage(int i, Entity entity) {

    }

    public double getHealth() {
        return 1;
    }

    public int _INVALID_getHealth() {
        return 0;
    }

    public void setHealth(double v) {

    }

    public void _INVALID_setHealth(int i) {

    }

    public double getMaxHealth() {
        return 1;
    }

    public int _INVALID_getMaxHealth() {
        return 0;
    }

    public void setMaxHealth(double v) {

    }

    public void _INVALID_setMaxHealth(int i) {

    }

    public void resetMaxHealth() {

    }

    @Override
    public Location getLocation() {
        return loc.clone();
    }

    @Override
    public Location getLocation(Location location) {
        location.setX(loc.getX());
        location.setY(loc.getY());
        location.setZ(loc.getZ());
        location.setWorld(loc.getWorld());
        location.setYaw(loc.getYaw());
        location.setPitch(loc.getPitch());
        return location;
    }

    public void setVelocity(Vector vector) {

    }

    public Vector getVelocity() {
        return new Vector(0, 0, 0);
    }

    public double getHeight() {
        return 0;
    }

    public double getWidth() {
        return 0;
    }

    public boolean isOnGround() {
        return true;
    }

    @Override
    public World getWorld() {
        return loc.getWorld();
    }

    @Override
    public boolean teleport(Location location) {
        loc = location;
        return true;
    }

    @Override
    public boolean teleport(Location location, PlayerTeleportEvent.TeleportCause teleportCause) {
        loc = location;
        return true;
    }

    @Override
    public boolean teleport(Entity entity) {
        loc = entity.getLocation();
        return true;
    }

    @Override
    public boolean teleport(Entity entity, PlayerTeleportEvent.TeleportCause teleportCause) {
        loc = entity.getLocation();
        return true;
    }

    public List<Entity> getNearbyEntities(double x, double y, double z) {
        return Nearby.getNearby(loc, x);
    }

    public int getEntityId() {
        return 0;
    }

    public int getFireTicks() {
        return 0;
    }

    public int getMaxFireTicks() {
        return 0;
    }

    public void setFireTicks(int i) {

    }

    public void remove() {

    }

    public boolean isDead() {
        return false;
    }

    public boolean isValid() {
        return true;
    }

    public void sendMessage(String s) {

    }

    public void sendMessage(String[] strings) {

    }

    public Server getServer() {
        return Bukkit.getServer();
    }

    public String getName() {
        return "Location";
    }

    public Entity getPassenger() {
        return null;
    }

    public boolean setPassenger(Entity entity) {
        return false;
    }

    public List<Entity> getPassengers() {
        return null;
    }

    public boolean addPassenger(final Entity entity) {
        return false;
    }

    public boolean removePassenger(final Entity entity) {
        return false;
    }

    public boolean isEmpty() {
        return false;
    }

    public boolean eject() {
        return false;
    }

    public float getFallDistance() {
        return 0;
    }

    public void setFallDistance(float v) {

    }

    public void setLastDamageCause(EntityDamageEvent entityDamageEvent) {

    }

    public EntityDamageEvent getLastDamageCause() {
        return null;
    }

    public UUID getUniqueId() {
        return null;
    }

    public int getTicksLived() {
        return 0;
    }

    public void setTicksLived(int i) {

    }

    public void playEffect(EntityEffect entityEffect) {
    }

    public EntityType getType() {
        return EntityType.CHICKEN;
    }

    public boolean isInsideVehicle() {
        return false;
    }

    public boolean leaveVehicle() {
        return false;
    }

    public Entity getVehicle() {
        return null;
    }

    public void setMetadata(String s, MetadataValue metadataValue) {

    }

    public List<MetadataValue> getMetadata(String s) {
        return null;
    }

    public boolean hasMetadata(String s) {
        return false;
    }

    public void removeMetadata(String s, Plugin plugin) {

    }

    public <T extends Projectile> T launchProjectile(Class<? extends T> aClass) {
        return null;
    }

    public <T extends Projectile> T launchProjectile(Class<? extends T> aClass, Vector vector) {
        return null;
    }

    public boolean isPermissionSet(String s) {
        return false;
    }

    public boolean isPermissionSet(Permission permission) {
        return false;
    }

    public boolean hasPermission(String s) {
        return false;
    }

    public boolean hasPermission(Permission permission) {
        return false;
    }

    public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b) {
        return null;
    }

    public PermissionAttachment addAttachment(Plugin plugin) {
        return null;
    }

    public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b, int i) {
        return null;
    }

    public PermissionAttachment addAttachment(Plugin plugin, int i) {
        return null;
    }

    public void removeAttachment(PermissionAttachment permissionAttachment) {

    }

    public void recalculatePermissions() {

    }

    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return null;
    }

    public boolean isOp() {
        return false;
    }

    public void setOp(boolean b) {

    }

    public AttributeInstance getAttribute(Attribute attribute) {
        return null;
    }
}
