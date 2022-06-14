package com.sucy.skill.api;

import java.util.Collections;
import java.util.ArrayList;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ParticleBuilder {
	private Particle particle;
	private Location loc;
	private float dx, dy, dz, extra;
	private int count;
	private Particle.DustOptions dustData;
	private BlockData blockData;
	private ItemStack itemData;
	private ArrayList<Player> receivers = new ArrayList<Player>();
	
	public ParticleBuilder(Particle particle) {
		this.particle = particle;
	}
	
	public ParticleBuilder location(Location loc) {
		this.loc = loc;
		return this;
	}
	
	public ParticleBuilder offset(float dx, float dy, float dz) {
		this.dx = dx;
		this.dy = dy;
		this.dz = dz;
		return this;
	}
	
	public ParticleBuilder count(int count) {
		this.count = count;
		return this;
	}
	
	public ParticleBuilder extra(float extra) {
		this.extra = extra;
		return this;
	}
	
	public ParticleBuilder data(Color color) {
		dustData = new Particle.DustOptions(color, 1.0F);
		return this;
	}
	
	public ParticleBuilder data(BlockData data) {
		this.blockData = data;
		return this;
	}
	
	public ParticleBuilder data(ItemStack data) {
		this.itemData = data;
		return this;
	}
	
	public ParticleBuilder receivers(Player p) {
		receivers.add(p);
		return this;
	}
	
	public ParticleBuilder receivers(int rad) {
		for (Entity e : loc.getWorld().getNearbyEntities(loc, rad, rad, rad)) {
			if (e instanceof Player) {
				receivers.add((Player) e);
			}
		}
		return this;
	}
	
	public void spawn() {
		Object data = null;
		if (dustData != null) {
			data = dustData;
		}
		else if (blockData != null) {
			data = blockData;
		}
		else if (itemData != null) {
			data = itemData;
		}
		for (Player p : receivers) {
			p.spawnParticle(particle, loc, count, dx, dy, dz, extra, data);
		}
	}
}
