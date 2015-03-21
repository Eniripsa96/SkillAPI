package com.sucy.skill.listener;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.event.PlayerClassChangeEvent;
import com.sucy.skill.tree.map.TreeRenderer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;

/**
 * Handles controlling the map menu for skill trees
 */
public class MapListener implements Listener
{
    private static final Vector UP   = new Vector(0, 1, 0);
    private static final Vector ZERO = new Vector(0, 0, 0);

    /**
     * Initializes a new map listener. Other plugins should
     * not use this as it is already handled by the API.
     *
     * @param api API reference
     */
    public MapListener(SkillAPI api)
    {
        api.getServer().getPluginManager().registerEvents(this, api);
    }

    /* at */ long last = 0;

    /**
     * Converts movement into controls for the map menu
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMove(PlayerMoveEvent event)
    {
        // If not holding onto a map, ignore it
        if (!TreeRenderer.RENDERER.isHeld(event.getPlayer())) return;

        // If not moving and only turning, ignore it
        Vector moving = event.getTo().subtract(event.getFrom()).toVector();
        moving.setY(0);
        if (moving.lengthSquared() < 1e-8) return;

        // Prevent movement so they don't fall off cliffs or something
        event.getPlayer().teleport(event.getFrom());
        event.getPlayer().setVelocity(ZERO);

        moving.normalize();

        // Ignore repeated updates, only want when they press down
        if (System.currentTimeMillis() - last < 300)
        {
            last = System.currentTimeMillis();
            return;
        }
        last = System.currentTimeMillis();

        // Get the facing direction again ignoring y-direction
        Vector facing = event.getTo().getDirection();
        facing.setY(0);
        facing.normalize();

        // Dot product tells us the direction
        double dot = moving.dot(facing);

        // Forwards is a value of 1, so get close to that for up
        if (dot > 0.5) TreeRenderer.RENDERER.moveUp(event.getPlayer());

            // Backwards is -1, so close to that will be down
        else if (dot < -0.5) TreeRenderer.RENDERER.moveDown(event.getPlayer());

            // Otherwise, check left and right
        else
        {

            // Change the forward to face to the right
            facing.crossProduct(UP);
            dot = moving.dot(facing);

            // Positive would face to the right since that's the new forward
            if (dot > 0) TreeRenderer.RENDERER.moveRight(event.getPlayer());

                // Otherwise it was left
            else TreeRenderer.RENDERER.moveLeft(event.getPlayer());
        }
    }

    /**
     * Handles selecting the current item in the menu
     *
     * @param event event details
     */
    @EventHandler
    public void onInteract(PlayerInteractEvent event)
    {
        // Must be holding the menu
        if (!TreeRenderer.RENDERER.isHeld(event.getPlayer())) return;

        // Left clicking selects
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
        {
            event.setCancelled(true);
            TreeRenderer.RENDERER.select(event.getPlayer());
        }
    }

    /**
     * Clears player menu data on profession to let it reinitialize
     *
     * @param event event details
     */
    @EventHandler
    public void onProfess(PlayerClassChangeEvent event)
    {
        TreeRenderer.RENDERER.clearData(event.getPlayerData().getPlayer());
    }

    /**
     * Clears player menu data on quit
     *
     * @param event event details
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent event)
    {
        TreeRenderer.RENDERER.clearData(event.getPlayer());
    }
}
