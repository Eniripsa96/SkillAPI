/**
 * SkillAPI
 * com.sucy.skill.listener.KillListener
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Steven Sucy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
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
package com.sucy.skill.listener;

import com.sucy.skill.api.classes.Click;
import com.sucy.skill.api.event.KeyPressEvent.Key;
import com.sucy.skill.api.event.NeoClickComboEvent;
import com.sucy.skill.api.event.NeoClickComboEvent.ClickType;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import java.util.HashMap;
import java.util.UUID;

/**
 * Tracks who kills what entities and awards experience accordingly
 */
public class NeoComboListener extends SkillAPIListener
{
    private HashMap<UUID, Click> lastClick;

    public NeoComboListener()
    {
    	lastClick = new HashMap<UUID, Click>();
    }
    
    @EventHandler
    public void onHitEntity(EntityDamageByEntityEvent e) {
        // Left clicks
    	if (e.getDamager() instanceof Player) {
    		Player p = (Player) e.getDamager();
	    	UUID uuid = p.getUniqueId();
	    	Key key = Key.LEFT;
	    	if (lastClick.containsKey(uuid)) {
	    		Click prev = lastClick.get(uuid);
	    		if (prev.getTime() + 500 > System.currentTimeMillis()) {
	    			if (prev.getKey().equals(Key.RIGHT)) {
	    				Bukkit.getPluginManager().callEvent(new NeoClickComboEvent(p, ClickType.RL));
	    			}
	    		}
	    	}
	    	lastClick.put(uuid, new Click(System.currentTimeMillis(), key));
    	}
    }
    
    @EventHandler(ignoreCancelled=false)
    public void onClick(PlayerInteractEvent e) {
        // Left clicks
    	Key key = null;
        if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
        	key = Key.LEFT;
        }

        // Right clicks
        else if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
        	key = Key.RIGHT;
        }

        if (key != null) {
        	UUID uuid = e.getPlayer().getUniqueId();
        	if (lastClick.containsKey(uuid)) {
        		Click prev = lastClick.get(uuid);
        		if (prev.getTime() + 500 > System.currentTimeMillis()) {
            		if (prev.getKey().equals(Key.RIGHT) && key.equals(Key.LEFT)) {
                        Bukkit.getPluginManager().callEvent(new NeoClickComboEvent(e.getPlayer(), ClickType.RL));
            		}
            		else if (prev.getKey().equals(Key.LEFT) && key.equals(Key.RIGHT)) {
                        Bukkit.getPluginManager().callEvent(new NeoClickComboEvent(e.getPlayer(), ClickType.LR));
            		}
        		}
        	}
        	lastClick.put(uuid, new Click(System.currentTimeMillis(), key));
        }
    }
    
    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
    	lastClick.remove(e.getPlayer().getUniqueId());
    }
    
    @EventHandler
    public void onLeave(PlayerKickEvent e) {
    	lastClick.remove(e.getPlayer().getUniqueId());
    }
    
    @EventHandler
    public void onHotbarSwap(PlayerItemHeldEvent e) {
    	lastClick.remove(e.getPlayer().getUniqueId());
    }
    
    @EventHandler
    public void onOffhandSwap(PlayerSwapHandItemsEvent e) {
    	lastClick.remove(e.getPlayer().getUniqueId());
    }
}
