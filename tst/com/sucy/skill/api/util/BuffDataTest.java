package com.sucy.skill.api.util;

import org.bukkit.entity.LivingEntity;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;

/**
 * SkillAPI © 2017
 * com.sucy.skill.api.util.BuffDataTest
 */
public class BuffDataTest {

    private static final String KEY = "buff";

    private BuffData subject;
    private LivingEntity entity;

    @Before
    public void setUp() {
        entity = mock(LivingEntity.class);
        subject = new BuffData(entity);
    }

    @Test
    public void addBuff() throws Exception {
        subject.addBuff(BuffType.DAMAGE, new Buff(KEY, 1, false), 5);
    }

    @Test
    public void apply() throws Exception {
    }

    @Test
    public void clear() throws Exception {
    }

}