package com.sucy.skill.version;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.regex.Pattern;

/**
 * A player implementation compatible across versions
 */
public class VersionPlayer {

    private static final String ID_REGEX = ".{8}-.{4}-.{4}-.{4}-.{12}";
    private static final Pattern ID_PATTERN = Pattern.compile(ID_REGEX);

    Object id;
    String idString;

    /**
     * Constructor from a player's name
     *
     * @param name player name
     * @deprecated use VersionPlayer(UUID) instead
     */
    public VersionPlayer(String name) {
        this.id = name;
        this.idString = name.toLowerCase();
    }

    /**
     * Constructor from a player's UUID
     *
     * @param id ID of the player
     */
    public VersionPlayer(UUID id) {
        this.id = id;
        this.idString = id.toString().toLowerCase();
    }

    /**
     * Constructor for use when the value could be either the player
     * name or the player's UUID
     *
     * @param id ID or name of the player
     */
    public VersionPlayer(Object id) {
        if (VersionManager.isVersionAtLeast(VersionManager.MC_1_7_2_MIN)) {
            if (ID_PATTERN.matcher(id.toString()).matches()) {
                this.id = UUID.fromString(id.toString());
            }
            else {
                this.id = Bukkit.getOfflinePlayer(id.toString()).getUniqueId();
            }
        }
        else this.id = id;

        this.idString = this.id.toString().toLowerCase();
    }

    /**
     * Constructor for representing an already known player
     *
     * @param player Bukkit player object
     */
    public VersionPlayer(Player player) {
        this((OfflinePlayer)player);
    }

    /**
     * Constructor for representing an already known player
     *
     * @param player Bukkit player object
     */
    public VersionPlayer(OfflinePlayer player) {
        if (VersionManager.isVersionAtLeast(VersionManager.MC_1_7_2_MIN)) {
            this.id = player.getUniqueId();
        }
        else this.id = player.getName();

        this.idString = this.id.toString().toLowerCase();
    }

    /**
     * Constructor for representing an already known player
     *
     * @param player Bukkit player entity object
     */
    public VersionPlayer(HumanEntity player) {
        if (VersionManager.isVersionAtLeast(VersionManager.MC_1_7_2_MIN)) {
            this.id = player.getUniqueId();
        }
        else this.id = player.getName();

        this.idString = this.id.toString().toLowerCase();
    }

    /**
     * Returns the ID for the player (name if pre-1.7)
     *
     * @return player ID or name
     */
    public Object getId() {
        return id;
    }

    /**
     * Gets the ID for the player represented as a string
     *
     * @return string representation of player's ID
     */
    public String getIdString() {
        return idString;
    }

    /**
     * Gets the current name of the player
     *
     * @return name of the player
     */
    public String getName() {
        return getOfflinePlayer().getName();
    }

    /**
     * Gets the Bukkit player object for the represented player
     *
     * @return Bukkit player object
     */
    public Player getPlayer() {
        if (id instanceof String) {
            return Bukkit.getPlayer(id.toString());
        }
        else {
            return Bukkit.getPlayer((UUID)id);
        }
    }

    /**
     * Gets the Bukkit offline player object for the represented player
     *
     * @return Bukkit offline player object
     */
    public OfflinePlayer getOfflinePlayer() {
        if (id instanceof String) {
            return Bukkit.getOfflinePlayer(id.toString());
        }
        else {
            return Bukkit.getOfflinePlayer((UUID) id);
        }
    }
}
