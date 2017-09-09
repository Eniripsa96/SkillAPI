package com.sucy.skill.listener;

import com.sucy.skill.TestUtils;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.data.PlayerEquips;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * SkillAPI © 2017
 * com.sucy.skill.listener.ItemListenerTest
 */
public class ItemListenerTest {

    @Mock
    private World  world;
    @Mock
    private Entity plainEntity;

    private ItemListener subject;

    private PlayerEquips equips;
    private Player       player;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        subject = new ItemListener();
        equips = setupEquips();
        player = TestUtils.mockPlayer();

        when(TestUtils.getMockSettings().isWorldEnabled(world)).thenReturn(true);

        when(plainEntity.getWorld()).thenReturn(world);
        when(player.getWorld()).thenReturn(world);
    }

    @After
    public void tearDown() {
        TestUtils.tearDown();
    }

    @Test
    public void onDrop() throws Exception {
    }

    @Test
    public void onBreak() throws Exception {
    }

    @Test
    public void onJoin() throws Exception {
    }

    @Test
    public void onPickup() throws Exception {
    }

    @Test
    public void onWorld() throws Exception {
    }

    @Test
    public void onHeld() throws Exception {
    }

    @Test
    public void onClose() throws Exception {
    }

    @Test
    public void onInteract() throws Exception {
    }

    @Test
    public void onAttack_worldDisabled() throws Exception {
        when(TestUtils.getMockSettings().isWorldEnabled(world)).thenReturn(false);
        final EntityDamageByEntityEvent event = mockEvent(plainEntity, null);
        subject.onAttack(event);

        verify(event, never()).setCancelled(anyBoolean());
    }

    @Test
    public void onAttack_neitherPlayers() throws Exception {
        final EntityDamageByEntityEvent event = mockEvent(plainEntity, null);
        subject.onAttack(event);

        verify(event, never()).setCancelled(anyBoolean());
    }

    @Test
    public void onAttack_damagerAble() throws Exception {
        final EntityDamageByEntityEvent event = mockEvent(plainEntity, player);
        when(equips.canHit()).thenReturn(true);
        subject.onAttack(event);

        verify(event, never()).setCancelled(true);
    }

    @Test
    public void onAttack_damagerUnable() throws Exception {
        final EntityDamageByEntityEvent event = mockEvent(plainEntity, player);
        subject.onAttack(event);

        verify(event).setCancelled(anyBoolean());
        verify(event).setCancelled(true);
    }

    @Test
    public void onAttack_entityAble() throws Exception {
        final EntityDamageByEntityEvent event = mockEvent(player, null);
        when(equips.canBlock()).thenReturn(true);
        subject.onAttack(event);

        verify(event, never()).setCancelled(anyBoolean());
    }

    @Test
    public void onAttack_entityUnable() throws Exception {
        final EntityDamageByEntityEvent event = mockEvent(player, null);
        subject.onAttack(event);

        verify(event, never()).setCancelled(anyBoolean());
        verify(event).setDamage(EntityDamageEvent.DamageModifier.BLOCKING, 0);
    }

    @Test
    public void onAttack_entityUnableNotBlocking() throws Exception {
        final EntityDamageByEntityEvent event = mockEvent(player, null);
        when(event.getDamage(EntityDamageEvent.DamageModifier.BLOCKING)).thenReturn(2.0);
        subject.onAttack(event);

        verify(event, never()).setCancelled(anyBoolean());
        verify(event, never()).setDamage(any(EntityDamageEvent.DamageModifier.class), anyDouble());
    }

    @Test
    public void onShoot() throws Exception {
    }

    private EntityDamageByEntityEvent mockEvent(final Entity entity, final Entity damager) {
        final EntityDamageByEntityEvent result = mock(EntityDamageByEntityEvent.class);
        when(result.getEntity()).thenReturn(entity);
        when(result.getDamager()).thenReturn(damager);
        when(result.getDamage(EntityDamageEvent.DamageModifier.BLOCKING)).thenReturn(-1.0);
        return result;
    }

    private PlayerEquips setupEquips() throws Exception {
        final PlayerEquips playerEquips = mock(PlayerEquips.class);

        final PlayerData playerData = TestUtils.mockPlayerData();
        when(playerData.getEquips()).thenReturn(playerEquips);
        return playerEquips;
    }
}