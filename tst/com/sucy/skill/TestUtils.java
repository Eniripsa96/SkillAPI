package com.sucy.skill;

import com.rit.sucy.config.CommentedLanguageConfig;
import com.rit.sucy.version.VersionManager;
import com.sucy.skill.api.player.PlayerAccounts;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.data.Settings;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

/**
 * SkillAPI © 2017
 * com.sucy.skill.TestUtils
 */
public class TestUtils {

    public static final String PLAYER_NAME = "player";
    public static final UUID PLAYER_UUID = UUID.randomUUID();

    private static SkillAPI                mockPlugin;
    private static Settings                mockSettings;
    private static CommentedLanguageConfig language;

    private static void injectInstance() throws Exception {
        if (mockPlugin == null) {
            mockPlugin = mock(SkillAPI.class);
            mockSettings = mock(Settings.class);
            language = mock(CommentedLanguageConfig.class);
            getField(SkillAPI.class, "singleton").set(null, mockPlugin);
            getField(SkillAPI.class, "settings").set(mockPlugin, mockSettings);
            getField(SkillAPI.class, "players").set(mockPlugin, new HashMap<String, PlayerAccounts>());
            getField(SkillAPI.class, "language").set(mockPlugin, language);
            getField(VersionManager.class, "version").set(null, Integer.MAX_VALUE);
        }
    }

    private static <T> Field getField(final Class<T> clazz, final String name) throws Exception {
        final Field field = clazz.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }

    public static void tearDown() {
        reset(mockPlugin);
        reset(mockPlugin);
        reset(language);
    }

    public static Server getMockServer() throws Exception {
        final Server server = mock(Server.class);
        final Field field = Bukkit.class.getDeclaredField("server");
        field.setAccessible(true);
        field.set(null, server);
        return server;
    }

    public static SkillAPI getMockPlugin() throws Exception {
        injectInstance();
        return mockPlugin;
    }

    public static Settings getMockSettings() throws Exception {
        injectInstance();
        return mockSettings;
    }

    public static CommentedLanguageConfig getLanguage() throws Exception {
        injectInstance();
        return language;
    }

    public static PlayerData mockPlayerData() throws Exception {
        injectInstance();

        final PlayerAccounts playerAccounts = mock(PlayerAccounts.class);
        SkillAPI.getPlayerAccountData().put(PLAYER_UUID.toString(), playerAccounts);

        final PlayerData playerData = mock(PlayerData.class);
        when(playerAccounts.getActiveData()).thenReturn(playerData);

        return playerData;
    }

    public static Player mockPlayer() throws Exception {
        final Player player = mock(Player.class);
        when(player.getName()).thenReturn(PLAYER_NAME);
        when(player.getUniqueId()).thenReturn(PLAYER_UUID);
        return player;
    }
}
