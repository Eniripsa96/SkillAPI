package com.sucy.skill.dynamic;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.*;

/**
 * Temporary dummy entity used for targeting a location in the dynamic system
 */
public class TempEntity implements LivingEntity
{

    private Location loc;

    /**
     * Sets up a new dummy entity
     *
     * @param loc location to represent
     */
    public TempEntity(Location loc)
    {
        this.loc = loc;
    }

    @Override
    public double getEyeHeight()
    {
        return 0;
    }

    @Override
    public double getEyeHeight(boolean b)
    {
        return 0;
    }

    @Override
    public Location getEyeLocation()
    {
        return null;
    }

    @Override
    public List<Block> getLineOfSight(HashSet<Byte> hashSet, int i)
    {
        return null;
    }

    @Override
    public Block getTargetBlock(HashSet<Byte> hashSet, int i)
    {
        return null;
    }

    @Override
    public List<Block> getLastTwoTargetBlocks(HashSet<Byte> hashSet, int i)
    {
        return null;
    }

    @Override
    public Egg throwEgg()
    {
        return null;
    }

    @Override
    public Snowball throwSnowball()
    {
        return null;
    }

    @Override
    public Arrow shootArrow()
    {
        return null;
    }

    @Override
    public int getRemainingAir()
    {
        return 0;
    }

    @Override
    public void setRemainingAir(int i)
    {

    }

    @Override
    public int getMaximumAir()
    {
        return 0;
    }

    @Override
    public void setMaximumAir(int i)
    {

    }

    @Override
    public int getMaximumNoDamageTicks()
    {
        return 0;
    }

    @Override
    public void setMaximumNoDamageTicks(int i)
    {

    }

    @Override
    public double getLastDamage()
    {
        return 0;
    }

    @Override
    public int _INVALID_getLastDamage()
    {
        return 0;
    }

    @Override
    public void setLastDamage(double v)
    {

    }

    @Override
    public void _INVALID_setLastDamage(int i)
    {

    }

    @Override
    public int getNoDamageTicks()
    {
        return 0;
    }

    @Override
    public void setNoDamageTicks(int i)
    {

    }

    @Override
    public Player getKiller()
    {
        return null;
    }

    @Override
    public boolean addPotionEffect(PotionEffect potionEffect)
    {
        return false;
    }

    @Override
    public boolean addPotionEffect(PotionEffect potionEffect, boolean b)
    {
        return false;
    }

    @Override
    public boolean addPotionEffects(Collection<PotionEffect> collection)
    {
        return false;
    }

    @Override
    public boolean hasPotionEffect(PotionEffectType potionEffectType)
    {
        return false;
    }

    @Override
    public void removePotionEffect(PotionEffectType potionEffectType)
    {

    }

    @Override
    public Collection<PotionEffect> getActivePotionEffects()
    {
        return null;
    }

    @Override
    public boolean hasLineOfSight(Entity entity)
    {
        return false;
    }

    @Override
    public boolean getRemoveWhenFarAway()
    {
        return false;
    }

    @Override
    public void setRemoveWhenFarAway(boolean b)
    {

    }

    @Override
    public EntityEquipment getEquipment()
    {
        return null;
    }

    @Override
    public void setCanPickupItems(boolean b)
    {

    }

    @Override
    public boolean getCanPickupItems()
    {
        return false;
    }

    @Override
    public void setCustomName(String s)
    {

    }

    @Override
    public String getCustomName()
    {
        return null;
    }

    @Override
    public void setCustomNameVisible(boolean b)
    {

    }

    @Override
    public boolean isCustomNameVisible()
    {
        return false;
    }

    @Override
    public boolean isLeashed()
    {
        return false;
    }

    @Override
    public Entity getLeashHolder() throws IllegalStateException
    {
        return null;
    }

    @Override
    public boolean setLeashHolder(Entity entity)
    {
        return false;
    }

    @Override
    public void damage(double v)
    {

    }

    @Override
    public void _INVALID_damage(int i)
    {

    }

    @Override
    public void damage(double v, Entity entity)
    {

    }

    @Override
    public void _INVALID_damage(int i, Entity entity)
    {

    }

    @Override
    public double getHealth()
    {
        return 1;
    }

    @Override
    public int _INVALID_getHealth()
    {
        return 0;
    }

    @Override
    public void setHealth(double v)
    {

    }

    @Override
    public void _INVALID_setHealth(int i)
    {

    }

    @Override
    public double getMaxHealth()
    {
        return 1;
    }

    @Override
    public int _INVALID_getMaxHealth()
    {
        return 0;
    }

    @Override
    public void setMaxHealth(double v)
    {

    }

    @Override
    public void _INVALID_setMaxHealth(int i)
    {

    }

    @Override
    public void resetMaxHealth()
    {

    }

    @Override
    public Location getLocation()
    {
        return loc;
    }

    @Override
    public Location getLocation(Location location)
    {
        location.setX(loc.getX());
        location.setY(loc.getY());
        location.setZ(loc.getZ());
        location.setWorld(loc.getWorld());
        location.setYaw(loc.getYaw());
        location.setPitch(loc.getPitch());
        return location;
    }

    @Override
    public void setVelocity(Vector vector)
    {

    }

    @Override
    public Vector getVelocity()
    {
        return new Vector(0, 0, 0);
    }

    @Override
    public boolean isOnGround()
    {
        return true;
    }

    @Override
    public World getWorld()
    {
        return loc.getWorld();
    }

    @Override
    public boolean teleport(Location location)
    {
        loc = location;
        return true;
    }

    @Override
    public boolean teleport(Location location, PlayerTeleportEvent.TeleportCause teleportCause)
    {
        loc = location;
        return true;
    }

    @Override
    public boolean teleport(Entity entity)
    {
        loc = entity.getLocation();
        return true;
    }

    @Override
    public boolean teleport(Entity entity, PlayerTeleportEvent.TeleportCause teleportCause)
    {
        loc = entity.getLocation();
        return true;
    }

    @Override
    public List<Entity> getNearbyEntities(double x, double y, double z)
    {
        ArrayList<Entity> list = new ArrayList<Entity>();
        for (Entity entity : loc.getWorld().getEntities())
        {
            if (entity.getLocation().distanceSquared(loc) < x * x)
            {
                list.add(entity);
            }
        }
        return list;
    }

    @Override
    public int getEntityId()
    {
        return 0;
    }

    @Override
    public int getFireTicks()
    {
        return 0;
    }

    @Override
    public int getMaxFireTicks()
    {
        return 0;
    }

    @Override
    public void setFireTicks(int i)
    {

    }

    @Override
    public void remove()
    {

    }

    @Override
    public boolean isDead()
    {
        return false;
    }

    @Override
    public boolean isValid()
    {
        return false;
    }

    @Override
    public Server getServer()
    {
        return Bukkit.getServer();
    }

    @Override
    public Entity getPassenger()
    {
        return null;
    }

    @Override
    public boolean setPassenger(Entity entity)
    {
        return false;
    }

    @Override
    public boolean isEmpty()
    {
        return false;
    }

    @Override
    public boolean eject()
    {
        return false;
    }

    @Override
    public float getFallDistance()
    {
        return 0;
    }

    @Override
    public void setFallDistance(float v)
    {

    }

    @Override
    public void setLastDamageCause(EntityDamageEvent entityDamageEvent)
    {

    }

    @Override
    public EntityDamageEvent getLastDamageCause()
    {
        return null;
    }

    @Override
    public UUID getUniqueId()
    {
        return null;
    }

    @Override
    public int getTicksLived()
    {
        return 0;
    }

    @Override
    public void setTicksLived(int i)
    {

    }

    @Override
    public void playEffect(EntityEffect entityEffect)
    {
    }

    @Override
    public EntityType getType()
    {
        return EntityType.CHICKEN;
    }

    @Override
    public boolean isInsideVehicle()
    {
        return false;
    }

    @Override
    public boolean leaveVehicle()
    {
        return false;
    }

    @Override
    public Entity getVehicle()
    {
        return null;
    }

    @Override
    public void setMetadata(String s, MetadataValue metadataValue)
    {

    }

    @Override
    public List<MetadataValue> getMetadata(String s)
    {
        return null;
    }

    @Override
    public boolean hasMetadata(String s)
    {
        return false;
    }

    @Override
    public void removeMetadata(String s, Plugin plugin)
    {

    }

    @Override
    public <T extends Projectile> T launchProjectile(Class<? extends T> aClass)
    {
        return null;
    }

    @Override
    public <T extends Projectile> T launchProjectile(Class<? extends T> aClass, Vector vector)
    {
        return null;
    }
}
